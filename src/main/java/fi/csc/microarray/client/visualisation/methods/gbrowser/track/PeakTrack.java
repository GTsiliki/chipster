package fi.csc.microarray.client.visualisation.methods.gbrowser.track;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import fi.csc.microarray.client.visualisation.methods.gbrowser.View;
import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.AreaRequestHandler;
import fi.csc.microarray.client.visualisation.methods.gbrowser.drawable.Drawable;
import fi.csc.microarray.client.visualisation.methods.gbrowser.drawable.RectDrawable;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.ColumnType;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.FileParser;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.AreaResult;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.BpCoord;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.RegionContent;

public class PeakTrack extends Track {

	private Collection<RegionContent> peaks = new TreeSet<RegionContent>();

	private long maxBpLength;
	private long minBpLength;

	private boolean wasLastConcised = true;
	private Color color;


	public PeakTrack(View view, File file, Class<? extends AreaRequestHandler> handler, FileParser inputParser, Color color, long minBpLength, long maxBpLength) {
		super(view, file, handler, inputParser);
		this.color = color;
		this.minBpLength = minBpLength;
		this.maxBpLength = maxBpLength;
	}

	@Override
	public Collection<Drawable> getDrawables() {
		Collection<Drawable> drawables = getEmptyDrawCollection();

		if (peaks != null) {

			Iterator<RegionContent> iter = peaks.iterator();
			while (iter.hasNext()) {

				RegionContent peak = iter.next();

				if (!peak.region.intercepts(getView().getBpRegion())) {
					iter.remove();
					continue;
				}

				createDrawable(peak.region.start, peak.region.end, 10, color, drawables);
			}
		}

		return drawables;
	}

	private void createDrawable(BpCoord startBp, BpCoord endBp, int height, Color c, Collection<Drawable> drawables) {
		Rectangle rect = new Rectangle();

		rect.x = getView().bpToTrack(startBp);
		rect.width = getView().bpToTrack(endBp) - rect.x;

		rect.y = (int) (getView().getTrackHeight() - height + 2);
		rect.height = height;

		drawables.add(new RectDrawable(rect, c, c.darker()));
	}

	public void processAreaResult(AreaResult<RegionContent> areaResult) {

		if (areaResult.status.concise == this.isConcised()) {
			this.peaks.add(areaResult.content);
			getView().redraw();
		}
	}


	@Override
	public void updateData() {
		if (wasLastConcised != isConcised()) {
			peaks.clear();
			wasLastConcised = isConcised();
		}
		super.updateData();
	}

	@Override
	public int getMaxHeight() {
		if (getView().getBpRegion().getLength() > minBpLength && getView().getBpRegion().getLength() <= maxBpLength) {
			return super.getMaxHeight();
			
		} else {
			return 0;
		}
	}

	@Override
	public Collection<ColumnType> getDefaultContents() {
		return Arrays.asList(new ColumnType[] { });
	}

	@Override
	public boolean isConcised() {
		return false;
	}
}
