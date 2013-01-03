package fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher;

import java.util.Queue;

import fi.csc.microarray.client.visualisation.methods.gbrowser.dataSource.DataSource;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.AreaRequest;


public class BedTabixHandlerThread extends TabixHandlerThread {
    

	private BedTabixFileFetcherThread fileFetcher;

	
	public BedTabixHandlerThread(DataSource file,
			Queue<AreaRequest> areaRequestQueue,
			AreaResultListener areaResultListener) {
		super(file, areaRequestQueue, areaResultListener);
	}

	@Override
	public synchronized void run() {

		// Start file processing layer thread
		fileFetcher = new BedTabixFileFetcherThread(fileRequestQueue, fileResultQueue, this, dataSource);
		fileFetcher.start();
		
		// Start this thread
		super.run();
	}
}
