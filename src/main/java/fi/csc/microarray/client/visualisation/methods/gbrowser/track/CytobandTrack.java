package fi.csc.microarray.client.visualisation.methods.gbrowser.track;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import fi.csc.microarray.client.visualisation.methods.gbrowser.DataSource;
import fi.csc.microarray.client.visualisation.methods.gbrowser.View;
import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.AreaRequestHandler;
import fi.csc.microarray.client.visualisation.methods.gbrowser.drawable.Drawable;
import fi.csc.microarray.client.visualisation.methods.gbrowser.drawable.LineDrawable;
import fi.csc.microarray.client.visualisation.methods.gbrowser.drawable.RectDrawable;
import fi.csc.microarray.client.visualisation.methods.gbrowser.drawable.TextDrawable;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.ColumnType;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.CytobandParser;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.FileParser;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.AreaResult;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.BpCoord;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.Chromosome;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.RegionContent;

public class CytobandTrack extends Track {

	private static final int THICKNESS = 10;

	private static final int MARGIN = 2;

	private BpCoord maxBp;

	private Collection<RegionContent> bands = new TreeSet<RegionContent>();

	private boolean showText;

	enum Band {

		WHITE("gneg", Color.white), 
		LIGHT_GRAY("gpos25", Color.lightGray), 
		MID_GRAY("gpos50", Color.gray), 
		DARK_GRAY("gpos75", Color.darkGray), 
		BLACK("gpos100", Color.black), 
		GAP("acen", null), 
		OTHER("gvar", Color.white),
		STALK("stalk", null);

		private String id;
		private Color color;

		Band(String id, Color color) {
			this.id = id;
			this.color = color;
		}

		public String getId() {
			return id;
		}

		public Color getColor() {
			return color;
		}
	}

	public Band getBand(String id) {
		for (Band band : Band.values()) {
			if (band.getId().equals(id)) {
				return band;
			}
		}
		return null;
	}

	public CytobandTrack(View view, DataSource file, Class<? extends AreaRequestHandler> handler, FileParser inputParser, boolean showText) {
		super(view, file, handler, inputParser);

		this.showText = showText;
	}

	@Override
	public Collection<Drawable> getDrawables() {

		Collection<Drawable> drawables = getEmptyDrawCollection();

		if (bands != null) {

			boolean firstGap = true;

			for (RegionContent bandRegion : bands) {

				Band band = getBand((String) bandRegion.values.get(ColumnType.VALUE));
				String text = (String) bandRegion.values.get(ColumnType.ID);

				if (text == null) {
					text = "";
				}

				if (band != null && band.getColor() != null) {
					RectDrawable box = createDrawable(bandRegion.region.start, bandRegion.region.end, band.color);
					drawables.add(box);

					if (showText) {

						final int CHAR_WIDTH = 7;

						int textSpace = 0;
						if (box.x >= 0) {
							textSpace = box.width;
						} else {
							textSpace = box.width + box.x;
						}

						if (textSpace > text.length() * CHAR_WIDTH) {
							int textX = Math.max(0, box.x);
							drawables.add(new TextDrawable(textX, box.y - 2, text, Color.black));
						}
					}
					firstGap = true;

				} else if (band == Band.GAP) {

					int y = (int) getHeight() - (THICKNESS + MARGIN);

					int sideX = getView().bpToTrack(bandRegion.region.end);
					int cornerX = getView().bpToTrack(bandRegion.region.start);

					if (firstGap) {
						int tmp = sideX;
						sideX = cornerX;
						cornerX = tmp;
						firstGap = false;
					}

					drawables.add(new LineDrawable(sideX, y, cornerX, y + THICKNESS / 2, Color.black));

					drawables.add(new LineDrawable(sideX, y + THICKNESS, cornerX, y + THICKNESS / 2, Color.black));

				} else if (band == Band.STALK) {
					
					Rectangle rect = new Rectangle();

					rect.x = getView().bpToTrack(bandRegion.region.start);
					rect.width = getView().bpToTrack(bandRegion.region.end) - rect.x;

					rect.y = (int) (getHeight() - (THICKNESS + MARGIN)) + THICKNESS / 4;
					rect.height = THICKNESS - THICKNESS / 2;

					drawables.add(new RectDrawable(rect, Color.gray, Color.gray));
				}
			}
		}
		return drawables;
	}

	private RectDrawable createDrawable(BpCoord startBp, BpCoord endBp, Color c) {

		Rectangle rect = new Rectangle();

		rect.x = getView().bpToTrack(startBp);
		rect.width = getView().bpToTrack(endBp) - rect.x;

		rect.y = (int) (getHeight() - (THICKNESS + MARGIN));
		rect.height = THICKNESS;

		return new RectDrawable(rect, c, Color.black);
	}

	public void processAreaResult(AreaResult<RegionContent> areaResult) {

		if (areaResult.content.values.containsKey(ColumnType.METADATA) &&
				((Map<?, ?>)(areaResult.content.values.get(ColumnType.METADATA))).containsKey(CytobandParser.LAST_ROW_OF_CHROMOSOME) && 
				getView().getBpRegion().start.chr.equals(areaResult.content.region.start.chr)) {					

			if (maxBp == null || maxBp.compareTo(areaResult.content.region.end) < 0) {
				maxBp = new BpCoord(areaResult.content.region.end);
			}
		}				

		if (getView().getBpRegion().intercepts(areaResult.content.region)) {

			this.bands.add(areaResult.content);
			getView().redraw();		
		}

		//		 this.reads.addAll(result.collection);
	}

	@Override
	public void updateData() {
		// bands.clear();
		super.updateData();
	}

	@Override
	public Integer getHeight() {
		return showText ? 40 : 20;
	}
	
    @Override
    public boolean isStretchable() {
        return false;
    }

	@Override
	public Collection<ColumnType> getDefaultContents() {
		return Arrays.asList(new ColumnType[] {ColumnType.ID, ColumnType.VALUE});
	}

	@Override
	public boolean isConcised() {
		return false;
	}

	@Override
	public BpCoord getMaxBp(Chromosome chr) {

		return maxBp;
	}
}
