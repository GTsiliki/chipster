package fi.csc.microarray.manager.web.ui;

import java.util.LinkedList;

import org.hibernate.exception.GenericJDBCException;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.csc.microarray.manager.web.ChipsterAdminUI;
import fi.csc.microarray.manager.web.data.JobLogContainer;

public class JobLogView extends VerticalLayout implements ClickListener, ValueChangeListener  {
	
	private HorizontalLayout toolbarLayout;

	private Button addSearchButton = new Button();

	private JobLogTable table;
	private JobLogContainer dataSource;

	private ChipsterAdminUI app;

	private LinkedList<JobLogSearch> searches;


	private HorizontalLayout searchLayout;


	public JobLogView(ChipsterAdminUI app) {

		this.app = app;
		init();
	}

	public void init() {
		
		//dataSourceWrapper has to be initialized here because of the transactionListener, so lets init everything else 
		//here as well (and not in the constructor like elsewhere)
		
		try {
			dataSource = new JobLogContainer(this); 
//			dataSource.init();
			
			table = new JobLogTable(this);		
			table.setContainerDataSource(dataSource);
			
		} catch (GenericJDBCException e) {
			//FIXME Show exception message and hide or disable all database based content 
			return;
		}
		

		table.setVisibleColumns(JobLogContainer.NATURAL_COL_ORDER);
		table.setColumnHeaders(JobLogContainer.COL_HEADERS_ENGLISH);
		
		this.addComponent(getToolbar());
		this.addComponent(table);

		setSizeFull();
		this.setExpandRatio(table, 1);
	}


	public HorizontalLayout getToolbar() {

		if (toolbarLayout == null) {
			
			toolbarLayout = new HorizontalLayout();
			
			searchLayout = new HorizontalLayout();
			addSearch();
			toolbarLayout.addComponent(searchLayout);
			addSearchButton.addClickListener((ClickListener)this);
			addSearchButton.setIcon(new ThemeResource("crystal/edit_add.png"));
			addSearchButton.setDescription("Add another search");
			addSearchButton.addStyleName("search-button");
			
			HorizontalLayout buttonBorder = new HorizontalLayout();
			buttonBorder.addStyleName("search-filter");
			buttonBorder.addComponent(addSearchButton);
			toolbarLayout.addComponent(buttonBorder);
			
			Button searchButton = new Button();
			searchButton.setIcon(new ThemeResource("crystal/mail_find.png"));
			searchButton.setDescription("Search");
			//searchButton.addStyleName("search-button");
			toolbarLayout.addComponent(searchButton);
			
			searchButton.addClickListener(new Button.ClickListener() {

				public void buttonClick(ClickEvent event) {

					performSearch();
				}
			});
			
			Label spaceEater = new Label(" ");
			toolbarLayout.addComponent(spaceEater);
			toolbarLayout.setExpandRatio(spaceEater, 1);

			toolbarLayout.addComponent(app.getTitle());	
			
			toolbarLayout.setWidth("100%");
			toolbarLayout.setStyleName("toolbar");
		}

		return toolbarLayout;
	}

	private void addSearch() {
		if (searches == null) {
			searches = new LinkedList<JobLogSearch>();
		}
		JobLogSearch search = new JobLogSearch(this);
		searches.add(search);
		searchLayout.addComponent(search);
	}

	public void buttonClick(ClickEvent event) {
		final Button source = event.getButton();

		if (source == addSearchButton) {
			addSearch();
		}
	}

	public void valueChange(ValueChangeEvent event) {
		Property<?> property = event.getProperty();
		if (property == table) {
			//			Item item = personList.getItem(personList.getValue());
			//			if (item != personForm.getItemDataSource()) {
			//				personForm.setItemDataSource(item);
			//			}
		}
	}

	public void performSearch() {

		updateContainerFilters();

		Notification.show("Found "
						+ table.getContainerDataSource().size() + " item(s)", Notification.Type.TRAY_NOTIFICATION);
	}
	
	private void updateContainerFilters() {
		dataSource.removeAllContainerFilters();

		for (JobLogSearch iteratedSearch : searches) {
			if (iteratedSearch.getContainerFilter() != null) {
				dataSource.addContainerFilter(iteratedSearch.getContainerFilter());
			}
		}
	}

	public void clearSearch(JobLogSearch search) {

		if (searches.size() > 1) {
			searchLayout.removeComponent(search);
			searches.remove(search);
		} else {
			searches.get(0).clear();
		}

		//It's not possible to remove just one filter, so let's do it in the hard way
		updateContainerFilters();
	}

	public void showOutput(Object itemId) {
		
		String output = "";
		Property<?> outputProperty = dataSource.getContainerProperty(itemId, JobLogContainer.OUTPUT_TEXT);
		
		if (outputProperty != null) {
			output = (String) outputProperty.getValue();
		}
		
		showTextWindow("Job output", output);
	}

	public void showErrorOutput(Object itemId) {
		String error = "";
		Property<?> errorProperty = dataSource.getContainerProperty(itemId, JobLogContainer.ERROR_MESSAGE);
		
		if (errorProperty != null) {
			error = (String) errorProperty.getValue();
		}

		showTextWindow("Error message", error);
	}
	
	private void showTextWindow(String caption, String content) {
		
		Label textComponent = new Label(content);
		textComponent.setContentMode(ContentMode.PREFORMATTED);
		
		Window subWindow = new Window(caption);
		subWindow.addComponent(textComponent);
		
		subWindow.setWidth(70, Unit.PIXELS);
		subWindow.setHeight(90, Unit.PIXELS);
		subWindow.center();
		
		this.addComponent(subWindow);
	}
}
