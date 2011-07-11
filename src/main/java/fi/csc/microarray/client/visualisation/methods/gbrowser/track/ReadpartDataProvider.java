package fi.csc.microarray.client.visualisation.methods.gbrowser.track;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import fi.csc.microarray.client.visualisation.methods.gbrowser.DataSource;
import fi.csc.microarray.client.visualisation.methods.gbrowser.View;
import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.AreaRequestHandler;
import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.AreaResultListener;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.ColumnType;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.Strand;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.AreaResult;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.Cigar;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.ReadPart;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.RegionContent;

public class ReadpartDataProvider implements AreaResultListener {

	private View view;
	private DataSource readData;
	private Collection<RegionContent> reads = new TreeSet<RegionContent>();
	private LinkedList<ReadPart> readParts = new LinkedList<ReadPart>(); 
	private LinkedList<ReadPart> readPartsF = new LinkedList<ReadPart>(); 
	private LinkedList<ReadPart> readPartsR = new LinkedList<ReadPart>();
	private boolean needsRefresh = false;

	public ReadpartDataProvider(View view, DataSource readData, Class<? extends AreaRequestHandler> readDataHandler) {
		this.view = view;
		this.readData = readData;
		
		// start listening
		view.getQueueManager().createQueue(readData, readDataHandler);
		view.getQueueManager().addResultListener(readData, this);
	}

	@Override
	public void processAreaResult(AreaResult<RegionContent> areaResult) {
		// Check that areaResult has false concised status and correct strand
		if (areaResult.status.file == readData && areaResult.status.concise == false) {
			// Add this to queue of RegionContents to be processed
			this.reads.add(areaResult.content);
			needsRefresh = true;
			view.redraw();
		}
	}

	private void refreshReadparts() {
		readParts.clear();
		readPartsF.clear();
		readPartsR.clear();
		Iterator<RegionContent> iter = reads.iterator();
		while (iter.hasNext()) {

			RegionContent read = iter.next();

			// Remove reads that are not in this view
			if (!read.region.intersects(view.getBpRegion())) {
				iter.remove();
				continue;
			}

			// Split read into continuous blocks (elements) by using the cigar
			List<ReadPart> visibleRegions = Cigar.splitVisibleElements(read);
			
			// Pool and sort read parts by strands
			for (ReadPart visibleRegion : visibleRegions) {
				// Skip read parts that are not in this view
				if (!visibleRegion.intersects(view.getBpRegion())) {
					continue;
				}
				
				readParts.add(visibleRegion); 
				
				if (read.values.get(ColumnType.STRAND) == Strand.FORWARD) {
					readPartsF.add(visibleRegion);
				} else if (read.values.get(ColumnType.STRAND) == Strand.REVERSED) {
					readPartsR.add(visibleRegion);
				}
			}
		}
		
		Collections.sort(readParts);
		Collections.sort(readPartsF);
		Collections.sort(readPartsR);
	}

	public Iterable<ReadPart> getReadparts(Strand strand) {
		
		if (needsRefresh) {
			refreshReadparts();
			needsRefresh = false;
		}
		
		switch (strand) {
			case BOTH:
				return readParts;
			case FORWARD:
				return readPartsF;
			case REVERSED:
				return readPartsR;
		}
		throw new IllegalArgumentException("illegal strand: " + strand);
	}

}
