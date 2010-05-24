package fi.csc.microarray.client.visualisation.methods.gbrowser;

import java.awt.Cursor;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;


public class GenomeBrowserStarter {

	private static final File ELAND_DATA_FILE = new File("/home/akallio/Desktop/STAT1/STAT1_treatment_aggregated_filtered_sorted_chr1.txt");
	private static final File MACS_DATA_FILE = new File("/home/akallio/Desktop/STAT1/STAT1_peaks_sorted.bed");
	private static final File URL_ROOT;

	static {
		URL_ROOT = new File("/home/akallio/eclipse-workspace/chipster-csc-src");
	}

	public static void main(String[] args) throws IOException {
		GenomePlot plot = new GenomePlot(true);
		TrackFactory.addCytobandTracks(plot, new DataSource(URL_ROOT, "Homo_sapiens.GRCh37.57_karyotype.tsv"));
//		TrackFactory.addGeneTracks(plot, new DataSource(URL_ROOT, "Homo_sapiens.NCBI36.54_genes.tsv"));
//		TrackFactory.addMirnaTracks(plot, new DataSource(URL_ROOT, "Homo_sapiens.NCBI36.54_miRNA.tsv"));
//		TrackFactory.addTranscriptTracks(plot, new DataSource(URL_ROOT, "Homo_sapiens.NCBI36.54_transcripts.tsv"));
		
//		TrackFactory.addPeakTracks(plot, new DataSource(MACS_DATA_FILE));
		
		TrackFactory.addReadTracks(plot, new DataSource(ELAND_DATA_FILE), 
				new DataSource(URL_ROOT, "Homo_sapiens.NCBI36.54_seq.tsv"));
		
		TrackFactory.addRulerTrack(plot);
		plot.start("1");
		
		ChartPanel panel = new ChartPanel(new JFreeChart(plot));
		panel.setPreferredSize(new Dimension(800, 600));
		plot.chartPanel = panel;
		panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		for (View view : plot.getViews()){
			panel.addMouseListener(view);
			panel.addMouseMotionListener(view);
			panel.addMouseWheelListener(view);
		}

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}