package fi.csc.microarray.client.selection;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fi.csc.microarray.client.ClientApplication;
import fi.csc.microarray.databeans.Dataset;
import fi.csc.microarray.databeans.DataFolder;
import fi.csc.microarray.databeans.DataItem;

public class DataSelectionManager {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(DataSelectionManager.class);

    private ClientApplication client;
    private LinkedList<DataItem> selectedDatas = new LinkedList<DataItem>();
    private Map<DataItem, RowSelectionManager> rowSelectionManagers = new HashMap<DataItem, RowSelectionManager>();
    
    public DataSelectionManager(ClientApplication client) {
        this.client = client;
        clearAll(true, this);
    }
    
    /**
     * get the selection managers for each dataset. Managers are created lazily
     * only when asked to avoid extra creation with every selection.
     * 
     * @param data
     * @return
     */
    public RowSelectionManager getRowSelectionManager(Dataset data){
    	RowSelectionManager manager;
    	manager = rowSelectionManagers.get(data);
    	
    	if(manager == null){
    		manager = new RowSelectionManager(client,data);
    		rowSelectionManagers.put(data, manager);
    	}
    	
    	return manager;
    }
        
    /**
     * Returns selected DataItem or last selected if in multiple selection mode.
     */
    public DataItem getSelectedItem() {
        return selectedDatas.isEmpty() ? null : selectedDatas.getLast();
    }
    
    /**
     * Returns selected DataBean or last selected if in multiple
     * selection mode. Returns null if something else than DataBean is selected.
     */
    public Dataset getSelectedDataBean() {
    	DataItem selectedItem = getSelectedItem();
        if (selectedItem instanceof DataFolder) {
            return null; // folder is selected, so there is no selected data
        } else {
            return (Dataset) selectedItem;
        }
    }
    
    public Dataset[] getSelectedDatasAsArray() {
        LinkedList<Dataset> beans = new LinkedList<Dataset>();
    	for (Dataset bean : getSelectedDataBeans()) {
    		beans.add(bean);
    	}
    	return beans.toArray(new Dataset[0]);    	
    }
    
    public List<Dataset> getSelectedDataBeans() {
		LinkedList<Dataset> list = new LinkedList<Dataset>();
		for (DataItem item : selectedDatas) {
			if (item instanceof Dataset) {
				list.add((Dataset) item);
			}
		}
    	return list;
    }
    
    public List<DataItem> getSelectedDataItems() {
    	LinkedList<DataItem> newList = new LinkedList<DataItem>();
		newList.addAll(selectedDatas);
		return newList;
    }

	public void selectSingle(DataItem dataItem, Object source) {
		selectedDatas.clear();
		selectMultiple(dataItem, source);		
	}
	
    /**
     * Next method with collection of items should be used in the future. This is 
     * kept for now to avoid big number of change all around program. Later the logic
     * of this method should be unified with the row selection manager to make overall 
     * architecture clearer.
     * 
     * @param selectedItem
     * @param source
     */
    public void selectMultiple(DataItem selectedItem, Object source) {
    	Collection<DataItem> itemCollection = new LinkedList<DataItem>();
    	itemCollection.add(selectedItem);
    	this.selectMultiple(itemCollection, source);
    }
    
    /**
     * Better way to give all selections with one method call and only through event in the end.
     * 
     * @param items
     * @param source
     */
    public void selectMultiple(Iterable<DataItem> items, Object source){
    	boolean realChange = false;
    	for (DataItem item : items){
    		
    		if (!selectedDatas.contains(item)) {
                selectedDatas.add(item);
                realChange = true;
            }
    	}
    	if(realChange){
    		client.dispatchEvent(new DatasetChoiceEvent(source));
    	}
    }
    
    public void deselectMultiple(DataItem selectedItem, Object source) {

        if (selectedDatas.contains(selectedItem)) {
            selectedDatas.remove(selectedItem);
            client.dispatchEvent(new DatasetChoiceEvent(source));     
        }
        
        // print debug
        if (logger.isDebugEnabled()) {
        	String s = "selection contains:";
        	for (DataItem item : selectedDatas) {
				s += " " + item.getName();
			}
        	logger.debug(s);
        }
    }
    
	/**
	 * Clears all selections. Event dispatching can be disabled to avoid multiple events when selecting datasets
	 */
	public void clearAll(boolean dispatchEvent, Object source) {
		this.selectedDatas.clear();
		
        if (dispatchEvent) {
        	client.dispatchEvent(new DatasetChoiceEvent(source));        	
        }
	}
    
    public boolean isSelected(DataItem data) {
    	return this.selectedDatas.contains(data);
    }           
}
