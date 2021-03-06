package fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import fi.csc.microarray.client.visualisation.methods.gbrowser.dataSource.CytobandDataSource;
import fi.csc.microarray.client.visualisation.methods.gbrowser.dataSource.DataSource;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.AreaRequest;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.AreaResult;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.ParsedFileResult;

/**
 * Processing layer of cytoband data. There is no need for processing with this data type, so just passing 
 * original data through.
 * 
 * @author Petri Klemelä
 *
 */
public class CytobandHandlerThread extends AreaRequestHandler {

	private CytobandDataSource dataSource;
	
	private CytobandFileFetcherThread fileFetcher;
	private BlockingQueue<BpCoordFileRequest> fileRequestQueue = new LinkedBlockingQueue<BpCoordFileRequest>();
	private ConcurrentLinkedQueue<ParsedFileResult> fileResultQueue = new ConcurrentLinkedQueue<ParsedFileResult>();

    public CytobandHandlerThread(DataSource file, Queue<AreaRequest> areaRequestQueue,
            AreaResultListener areaResultListener) {
        
        super(areaRequestQueue, areaResultListener);
        
        dataSource = (CytobandDataSource) file;
    }

	public CytobandHandlerThread(DataSource cytobandDataSource) {
		this(cytobandDataSource, null, null);
	}

	@Override
	public void runThread() {

		// Start file processing layer thread
		fileFetcher = new CytobandFileFetcherThread(fileRequestQueue, fileResultQueue, this,
				dataSource);
		
		fileFetcher.start();
		
		// Start this thread
		super.runThread();
	}

	protected boolean checkOtherQueues() {
		
		ParsedFileResult fileResult = null;
		if ((fileResult = fileResultQueue.poll()) != null) {
			processFileResult(fileResult);
		}
		return fileResult != null;
	}

    private void processFileResult(ParsedFileResult fileResult) {
    	
    		createAreaResult(new AreaResult(fileResult.getStatus(), fileResult.getContents()));
	}

    @Override
    protected void processAreaRequest(AreaRequest areaRequest) {
    	
		super.processAreaRequest(areaRequest);
		
		if (areaRequest.getStatus().poison) {
			
			BpCoordFileRequest fileRequest = new BpCoordFileRequest(areaRequest, null, null, areaRequest.getStatus());
			fileRequestQueue.add(fileRequest);
			return;
		}

			
		fileRequestQueue.add(new BpCoordFileRequest(areaRequest, areaRequest.start, areaRequest.end, areaRequest.getStatus()));		
    }
}
