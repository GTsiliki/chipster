package fi.csc.microarray.client.visualisation.methods.gbrowser.message;

import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.FileParser;

public class FileResult {

	public FileRequest request;
	public FileParser chunkParser;
	public FsfStatus status;

	/**
	 * @param fileRequest
	 * @param avg
	 * @param requestQueueSize
	 *            only to update user interface
	 */
	public FileResult(FileRequest fileRequest, FileParser inputParser, FsfStatus status) {
		this.request = fileRequest;
		this.chunkParser = inputParser;
		this.status = status;
	}

}