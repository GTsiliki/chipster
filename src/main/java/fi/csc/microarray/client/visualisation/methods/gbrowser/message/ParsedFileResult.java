package fi.csc.microarray.client.visualisation.methods.gbrowser.message;

import java.util.List;

import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.BpCoordFileRequest;
import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.SAMFileFetcherThread;
import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.SAMHandlerThread;

/**
 * Result generated from {@link SAMHandlerThread} by {@link SAMFileFetcherThread}.
 * 
 * @author Aleksi Kallio
 *
 */
public class ParsedFileResult {

	private List<RegionContent> content;
	private BpCoordFileRequest fileRequest;
	public AreaRequest areaRequest;
	private DataRetrievalStatus status;

	public ParsedFileResult(List<RegionContent> content, BpCoordFileRequest fileRequest, AreaRequest areaRequest, DataRetrievalStatus status) {
		this.content = content;
		this.fileRequest = fileRequest;
		this.areaRequest = areaRequest;
		this.status = status;
	}

	public BpCoordFileRequest getFileRequest() {
		return fileRequest;
	}

	public AreaRequest getAreaRequest() {
		return areaRequest;
	}

	public List<RegionContent> getContents() {
		return content;
	}

	public DataRetrievalStatus getStatus() {
		return status;
	}

}
