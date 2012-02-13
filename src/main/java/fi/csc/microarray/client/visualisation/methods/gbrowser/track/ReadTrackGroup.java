package fi.csc.microarray.client.visualisation.methods.gbrowser.track;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;

import fi.csc.microarray.client.visualisation.methods.gbrowser.DataSource;
import fi.csc.microarray.client.visualisation.methods.gbrowser.FastaDataSource;
import fi.csc.microarray.client.visualisation.methods.gbrowser.GenomeBrowserConstants;
import fi.csc.microarray.client.visualisation.methods.gbrowser.View;
import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.AreaRequestHandler;
import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.ChunkTreeHandlerThread;
import fi.csc.microarray.client.visualisation.methods.gbrowser.dataFetcher.FastaHandlerThread;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.Strand;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.Chromosome;
import fi.csc.microarray.constants.VisualConstants;

/**
 * Tracks containing information about reads: sequences themselves, gel,
 * profile etc.
 * 
 * @author Rimvydas Naktinis, Vilius Zukauskas
 *
 */
public class ReadTrackGroup extends TrackGroup {

	// Colors
//    private final Color histogramColor = new Color(100, 100, 100, 100); // translucent color
	private final Color histogramColor = new Color(100, 100, 100);
    private final Color fontColor = Color.black;

    // Tracks
    protected TitleTrack titleTrack;
    protected IntensityTrack readOverview;
    protected SeqBlockTrack reads;
    protected SeqTrack seq;
    protected IntensityTrack readOverviewReversed;
    protected SeqBlockTrack readsReversed;
    protected CoverageAndSNPTrack profileTrack;
    protected CoverageAndSNPTrack profileSNPTrack;
//    protected QualityCoverageTrack qualityCoverageTrack;
    protected GelTrack gelTrack;
    protected SeparatorTrack sepTrackReads;
    protected SeparatorTrack sepTrackSeq;
    protected SeparatorTrack sepTrackProfile;
    protected SeparatorTrack sepTrackProfileSNP;
    protected SeparatorTrack sepTrackQualityCoverage;
    protected SeparatorTrack sepTrackGel;
    
    private DataSource seqFile;
	private DataSource userData;
	private ReadpartDataProvider readpartProvider;
	private Class<? extends AreaRequestHandler> userDataHandler;
	private String title;
	private boolean initialised = false;

    public ReadTrackGroup(View view, DataSource userData,
            Class<? extends AreaRequestHandler> userDataHandler,
            DataSource seqFile, String title) {
        super(view);
        
        this.userData = userData;
        this.readpartProvider = new ReadpartDataProvider(view, userData, userDataHandler);
        this.userDataHandler = userDataHandler; 
        this.seqFile = seqFile;
        this.title = title;
    }

    public void initialise() {
        
        // Title
        titleTrack = new TitleTrack(view, title, Color.black);
        tracks.add(titleTrack);
        
        // Overview
        addReadOverviewTrack();

        // Detailed
        reads = new SeqBlockTrack(view, userData, readpartProvider, userDataHandler, 
                fontColor, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT);
        tracks.add(reads);
        sepTrackReads = new SeparatorTrack(view, Color.gray, 1, 0, Long.MAX_VALUE);
        sepTrackReads.setName("Reads");
        tracks.add(sepTrackReads);
        
        // Reference
        if (seqFile != null) {
            // Reference sequence
        	
			try {
				seqFile = new FastaDataSource();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        	((FastaDataSource)seqFile).put(new Chromosome("1"), new File("/home/klemela/chipster/orig-annotations/Homo_sapiens.GRCh37.65.dna.chromosome.1.fa"));
            
            seq = new SeqTrack(view, seqFile,
                    FastaHandlerThread.class, GenomeBrowserConstants.SHOW_REFERENCE_AT);            
            
//            seq = new SeqTrack(view, seqFile,
//                    FastaHandlerThread.class, GenomeBrowserConstants.SHOW_REFERENCE_AT);
                        
            tracks.add(seq);
            
            sepTrackSeq = new SeparatorTrack(view, Color.gray, 1, 0, GenomeBrowserConstants.SHOW_REFERENCE_AT);
            sepTrackSeq.setName("Reads");
            tracks.add(sepTrackSeq);

        }
        
        // Overview - reversed
        addReadOverviewReversedTrack();
        
        // Detailed - reversed
        readsReversed = new SeqBlockTrack(view, userData, readpartProvider, userDataHandler, 
                fontColor, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT);
        readsReversed.setStrand(Strand.REVERSED);
        tracks.add(readsReversed);
    	SeparatorTrack sepTrackReads2 = new SeparatorTrack(view, Color.gray, 1, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT); 
    	sepTrackReads2.setName("Reads");
        tracks.add(sepTrackReads2);
        
        // Profile
        
        Color forwardColor = new Color(0,0,0, 128);
        Color reverseColor = new Color(
        		VisualConstants.COLOR_BLUE.getRed(), 
        		VisualConstants.COLOR_BLUE.getGreen(), 
        		VisualConstants.COLOR_BLUE.getBlue(), 
        		128);
        
//        profileTrack = new CoverageTrack(view, userData, readpartProvider, userDataHandler,
//        		forwardColor, reverseColor, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT);
//        profileTrack.setStrand(Strand.BOTH);
//        tracks.add(profileTrack);
//    	sepTrackProfile = new SeparatorTrack(view, Color.gray, 1, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT); 
//    	sepTrackProfile.setName("ProfileTrack");
//        tracks.add(sepTrackProfile);
        
        profileTrack = new CoverageAndSNPTrack(view, userData, readpartProvider, userDataHandler, 
        		seqFile, ChunkTreeHandlerThread.class, forwardColor, reverseColor, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT);
        profileTrack.setName("ProfileTrack");
        tracks.add(profileTrack);
        sepTrackProfile = new SeparatorTrack(view, Color.gray, 1, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT);
        sepTrackProfile.setName("ProfileTrack");
    	tracks.add(sepTrackProfile);
        
        // SNP profile
        profileSNPTrack = new CoverageAndSNPTrack(view, userData, readpartProvider, userDataHandler, 
        		seqFile, ChunkTreeHandlerThread.class, Color.BLACK, null, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT);
        profileSNPTrack.setName("ProfileSNPTrack");
        tracks.add(profileSNPTrack);
    	sepTrackProfileSNP = new SeparatorTrack(view, Color.gray, 1, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT);
    	sepTrackProfileSNP.setName("ProfileSNPTrack");
    	tracks.add(sepTrackProfileSNP);

    	// Quality coverage
//        qualityCoverageTrack = new QualityCoverageTrack(view, userData, userDataHandler,
//        		Color.ORANGE, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT);
//        tracks.add(qualityCoverageTrack);
//    	sepTrackQualityCoverage = new SeparatorTrack(view, Color.gray, 1, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT);
//    	sepTrackQualityCoverage.setName("QualityCoverageTrack");
//    	tracks.add(sepTrackQualityCoverage);
        
        // Gel
        gelTrack = new GelTrack(view, userData, readpartProvider, userDataHandler,
                Color.WHITE, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT);
        gelTrack.setStrand(Strand.BOTH);
        tracks.add(gelTrack);
    	sepTrackGel = new SeparatorTrack(view, Color.gray, 1, 0, GenomeBrowserConstants.SWITCH_VIEWS_AT); 
    	sepTrackGel.setName("GelTrack");
        tracks.add(sepTrackGel);
        
        this.initialised  = true;
    }


	protected void addReadOverviewReversedTrack() {
		readOverviewReversed = new IntensityTrack(view, userData,
                userDataHandler, histogramColor, GenomeBrowserConstants.SWITCH_VIEWS_AT, false, true);
        readOverviewReversed.setStrand(Strand.REVERSED);
		tracks.add(readOverviewReversed);
	}

	protected void addReadOverviewTrack() {
		readOverview = new IntensityTrack(view, userData,
                userDataHandler, histogramColor, GenomeBrowserConstants.SWITCH_VIEWS_AT, false, true);
		tracks.add(readOverview);
	}
    
    public void setVisibleSNP(boolean b) {
    	check();
    	if (b) {
            reads.enableSNPHighlight(seqFile, ChunkTreeHandlerThread.class);
            readsReversed.enableSNPHighlight(seqFile, ChunkTreeHandlerThread.class);
            profileSNPTrack.enableSNPHighlight();
        } else {
            reads.disableSNPHiglight(seqFile);
            readsReversed.disableSNPHiglight(seqFile);
            profileSNPTrack.disableSNPHighlight();
        }
        view.fireAreaRequests();
        view.redraw();
    }


    @Override
    public String getName() {
    	check();
    	return "Read Track Group";
    }
    
    @Override
    public void showOrHide(String name, boolean state) {
    	check();
    	super.showOrHide(name, state);
    	if (name.equals("highlightSNP")) {
    		setVisibleSNP(state);
    	}
    }

	private void check() {
		if (!initialised) {
    		throw new IllegalStateException("you must call initialise() after creating this object");
    	}
	}
}
