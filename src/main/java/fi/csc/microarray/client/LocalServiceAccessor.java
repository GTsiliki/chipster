package fi.csc.microarray.client;

import java.util.Collection;
import java.util.LinkedList;

import javax.jms.JMSException;

import fi.csc.chipster.tools.PreprocessNGSSingle;
import fi.csc.microarray.client.operation.OperationCategory;
import fi.csc.microarray.client.operation.OperationDefinition;
import fi.csc.microarray.client.tasks.LocalTaskExecutor;
import fi.csc.microarray.client.tasks.TaskExecutor;
import fi.csc.microarray.databeans.DataManager;
import fi.csc.microarray.description.SADLDescription;
import fi.csc.microarray.description.SADLDescription.Input;
import fi.csc.microarray.description.SADLDescription.Parameter;
import fi.csc.microarray.filebroker.FileBrokerClient;
import fi.csc.microarray.messaging.SourceMessageListener;
import fi.csc.microarray.messaging.auth.AuthenticationRequestListener;
import fi.csc.microarray.messaging.message.FeedbackMessage;
import fi.csc.microarray.module.Module;
import fi.csc.microarray.module.chipster.ChipsterSADLParser;

public class LocalServiceAccessor implements ServiceAccessor {

	private DataManager manager;

	private Collection<OperationCategory> visibleCategories;
	private Collection<OperationCategory> hiddenCategories;

	
	
	@Override
	public String checkRemoveServices() throws Exception {
		return ServiceAccessor.ALL_SERVICES_OK;
	}

	@Override
	public void close() throws Exception {
		// do nothing
	}

	@Override
	public void fetchDescriptions(Module primaryModule) throws Exception {
		this.visibleCategories = new LinkedList<OperationCategory>();
		this.hiddenCategories = new LinkedList<OperationCategory>();
		
		PreprocessNGSSingle tool = new PreprocessNGSSingle();
		
        SADLDescription sadl = new ChipsterSADLParser().parse(tool.getSADL());
		OperationCategory category = new OperationCategory(sadl.getCategory());
		this.visibleCategories.add(category);
		
        OperationDefinition od = new OperationDefinition(sadl.getName().getID(), 
                                                                    sadl.getName().getDisplayName(), category,
                                                                    sadl.getComment(), true,
                                                                    null);
        for (Input input : sadl.inputs()) {
            if (input.getName().isNameSet()) {
                od.addInput(input.getName().getPrefix(), input.getName().getPostfix(), input.getType());
            } else {
                od.addInput(input.getName(), input.getType());
            }
        }

        od.setOutputCount(sadl.outputs().size());
        for (Parameter parameter : sadl.parameters()) {
            od.addParameter(fi.csc.microarray.client.
                                       operation.parameter.Parameter.createInstance(
                parameter.getName(), parameter.getType(), parameter.getSelectionOptions(),
                parameter.getComment(), parameter.getFrom(), parameter.getTo(),
                parameter.getDefaultValues(), parameter.isOptional()));      
        }
	}

	@Override
	public FileBrokerClient getFileBrokerClient() throws Exception {
		throw new UnsupportedOperationException("not supported in standalone mode");
	}

	@Override
	public TaskExecutor getTaskExecutor() {
		try {
			return new LocalTaskExecutor(manager);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<OperationCategory> getHiddenCategories() {
		if (hiddenCategories == null) {
			throw new IllegalStateException("fetchDescriptions(...) must be called first");
		}
		return hiddenCategories;
	}
	
	@Override
	public Collection<OperationCategory> getVisibleCategories() {
		if (visibleCategories == null) {
			throw new IllegalStateException("fetchDescriptions(...) must be called first");
		}
		return visibleCategories;
	}


	@Override
	public void initialise(DataManager manager, AuthenticationRequestListener authenticationRequestListener) throws Exception {
		this.manager = manager;
		// we are not interested in authenticationRequestListener
	}

	@Override
	public SourceMessageListener retrieveSourceCode(String id) throws Exception {
		throw new UnsupportedOperationException("not supported in standalone mode");
	}

	@Override
	public void sendFeedbackMessage(FeedbackMessage message) throws Exception {
		throw new UnsupportedOperationException("not supported in standalone mode");
	}

}
