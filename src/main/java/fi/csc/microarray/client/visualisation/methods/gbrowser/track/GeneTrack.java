package fi.csc.microarray.client.visualisation.methods.gbrowser.track;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.AreaRequestHandler;
import fi.csc.microarray.client.visualisation.methods.gbrowser.drawable.Drawable;
import fi.csc.microarray.client.visualisation.methods.gbrowser.drawable.RectDrawable;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.ColumnType;
import fi.csc.microarray.client.visualisation.methods.gbrowser.gui.LayoutTool.LayoutMode;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.AreaResult;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.BpCoord;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.Gene;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.Region;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.RegionContent;

/**
 * Track for genes. Higher zoom level version of {@link TranscriptTrack}.
 *
 */
public class GeneTrack extends Track {

	private Collection<Gene> genes = new TreeSet<Gene>();
	private List<Integer> occupiedSpace = new ArrayList<Integer>();

	private long maxBpLength;
	private long minBpLength;

	private Color color;


	public GeneTrack(Color color, long minBpLength, long maxBpLength) {

		this.color = color;
		this.minBpLength = minBpLength;
		this.maxBpLength = maxBpLength;
		this.layoutMode = this.defaultLayoutMode = LayoutMode.FULL;
	}

	@Override
	public Collection<Drawable> getDrawables() {
		Collection<Drawable> drawables = getEmptyDrawCollection();

		occupiedSpace.clear();
		
		TreeMap<Region, Gene> sortedGenes = new TreeMap<Region, Gene>();
		
		if (genes != null) {

			Iterator<Gene> iter = genes.iterator();
			while (iter.hasNext()) {

				Gene gene = iter.next();

				// FIXME this and all the other incarnations of the same 3 lines should be refactored up to Track or something
				if (!gene.getRegion().intersects(getView().getBpRegion())) {
					iter.remove();
					continue;
				}

				sortedGenes.put(gene.getRegion(), gene);
			}

			for (Gene gene : sortedGenes.values()) {
				
				String name = null;
				
				if (gene.getName() != null) {
					name = gene.getName();
				} else if (gene.getId() != null) {
					name = gene.getId();
				} else {
					name = "n/a";
				}

				createDrawable(gene.getRegion().start, gene.getRegion().end, 10, color, name, drawables);
			}
		}

		return drawables;
	}

	private void createDrawable(BpCoord startBp, BpCoord endBp, int height, Color c, String name, Collection<Drawable> drawables) {
		Rectangle rect = new Rectangle();

		rect.x = getView().bpToTrack(startBp);
		rect.width = getView().bpToTrack(endBp) - rect.x;

		int i = 0;

		while (occupiedSpace.size() > i && occupiedSpace.get(i) > rect.x + 1) {
			i++;
		}

		int end = rect.x + rect.width;

		if (occupiedSpace.size() > i) {
			occupiedSpace.set(i, end);
		} else {
			occupiedSpace.add(end);
		}

		rect.y = (int) ((i + 1) * (height + 2));
		rect.height = height;

		drawables.add(new RectDrawable(rect, c, null));
		if (isNameVisible(rect)) {

			// draw name to leftmost visible part of the gene rectangle
			drawTextAboveRectangle(name, drawables, rect, 10);
		}
	}

	public void processAreaResult(AreaResult areaResult) {

		for (RegionContent content : areaResult.getContents()) {

			Gene gene = (Gene) content.values.get(ColumnType.VALUE);

			if (gene.getRegion().getStrand() == getStrand()) {

				//Genes at edge of edge of screen may contain only visible exons, but moving should
				//reveal also rest of the gene. Remove the old genes (if it exists) to make space for the
				//new ones with better information for the current view location.
				this.genes.remove(gene);

				this.genes.add(gene);
			}
		}
		getView().redraw();
	}
	
    @Override
    public boolean isVisible() {
        // visible region is not suitable
        return (super.isVisible() &&
                getView().getBpRegion().getLength() > minBpLength &&
                getView().getBpRegion().getLength() <= maxBpLength);
    }    

    @Override
    public Map<AreaRequestHandler, Set<ColumnType>> requestedData() {
        HashMap<AreaRequestHandler, Set<ColumnType>> datas = new
        HashMap<AreaRequestHandler, Set<ColumnType>>();
        datas.put(areaRequestHandler, new HashSet<ColumnType>(Arrays.asList(new ColumnType[] {
                ColumnType.VALUE })));
        return datas;
    }
	
	@Override
	public int getMinHeight() {
		return 100;
	}
}
