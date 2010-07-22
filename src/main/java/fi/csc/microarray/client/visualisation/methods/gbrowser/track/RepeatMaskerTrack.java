package fi.csc.microarray.client.visualisation.methods.gbrowser.track;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import fi.csc.microarray.client.visualisation.methods.gbrowser.DataSource;
import fi.csc.microarray.client.visualisation.methods.gbrowser.View;
import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.AreaRequestHandler;
import fi.csc.microarray.client.visualisation.methods.gbrowser.drawable.Drawable;
import fi.csc.microarray.client.visualisation.methods.gbrowser.drawable.RectDrawable;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.ColumnType;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.AreaResult;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.BpCoord;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.Chromosome;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.RegionContent;

public class RepeatMaskerTrack extends Track {
	
	private long minBpLength;
	private long maxBpLength;
	private LinkedList<Long> collector = new LinkedList<Long>();
	private final Color UPPER_COLOR = Color.BLUE;
	private final Color LOWER_COLOR = Color.GRAY;
    
	private Collection<RegionContent> reads = new TreeSet<RegionContent>();
	
	public RepeatMaskerTrack(View view, DataSource file, Class<? extends AreaRequestHandler> handler, 
			long minBpLength, long maxBpLength){
		
		super(view,file,handler);
		this.minBpLength = minBpLength;
		this.maxBpLength = maxBpLength;
	}

	@Override
	public Collection<Drawable> getDrawables() {
		
		Collection<Drawable> drawables = getEmptyDrawCollection();	
		collector.clear();
		
		// case of the first character in the first read
		Boolean firstCase = null;
		                  	
		if (reads != null) {

			Iterator<RegionContent> iter = reads.iterator();
			Chromosome lastChromosome = null;
			RegionContent read = null;
			
			while (iter.hasNext()) {

				read = iter.next();

				if (!read.region.intercepts(getView().getBpRegion())) {
					iter.remove();
					continue;
				}

				BpCoord startBp = read.region.start;
				BpCoord endBp = read.region.end;
				lastChromosome = read.region.start.chr;

				String seq = ((String) read.values.get(ColumnType.SEQUENCE));
				if (seq != null) {
					seq = seq.trim();
				} else {
					seq = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".substring(0, (int) (endBp.minus(startBp) + 1));
				}
                
                int seqLength = (int) (endBp.minus(startBp)-0);
                
                // assign true for uppercase letters, false for lowercase
                boolean lastCase = Character.isUpperCase(seq.charAt(0));
                
                if (firstCase == null) {
                	firstCase = lastCase;
                }
                
                // add positions where letter case changes
                int j = 0;
                for (Long i = read.region.start.bp + 1; i <= (read.region.start.bp + seqLength); i++) {
                	if (Character.isUpperCase(seq.charAt(j)) != lastCase) {
                		lastCase = Character.isUpperCase(seq.charAt(j));
                		collector.add(i - 1);
                	}
                	j += 1;
				}
			}
			
			if (read != null) {
				collector.add(read.region.end.bp);
			}
			
			Iterator<Long> bpLocations = collector.iterator();
			
            if (bpLocations.hasNext() && reads.iterator().hasNext()) {
                Long lastBpLocation = reads.iterator().next().region.start.bp;
                Color c = firstCase ? UPPER_COLOR : LOWER_COLOR;
                
                while (bpLocations.hasNext()) {
                    // take coordinates from bp
                    Long currentBpLocation = bpLocations.next();
                    long startX = getView().bpToTrack(new BpCoord(lastBpLocation, lastChromosome));
                    long endX = getView().bpToTrack(new BpCoord(currentBpLocation, lastChromosome));
                    
                    // only draw rectangles when letter case changes
            		drawables.add(new RectDrawable(new Rectangle((int)startX, 0,
                            (int)(endX-startX), this.getHeight()), c, c));
                    
                    lastBpLocation = currentBpLocation;
                	c = (c == UPPER_COLOR) ? LOWER_COLOR : UPPER_COLOR;
                }
            }
		}
		
		return drawables;
	}
	
	@Override
	public Collection<ColumnType> getDefaultContents() {
		return Arrays.asList(new ColumnType[] { ColumnType.SEQUENCE });
	}
	
	@Override
	public void processAreaResult(AreaResult<RegionContent> areaResult) {
		this.reads.add(areaResult.content);
        getView().redraw();
	}

	@Override
	public boolean isConcised() {
		
		return false;
	}

	@Override
	public boolean isStretchable() {
		
		return false;
	}

	@Override
    public Integer getHeight() {
        if (isVisible()) {
            return 31;
        } else {
            return 0;
        }
    }
	
	@Override
    public boolean isVisible() {
        // visible region is not suitable
        return (super.isVisible() &&
                getView().getBpRegion().getLength() > minBpLength &&
                getView().getBpRegion().getLength() <= maxBpLength);
    }
	
}
