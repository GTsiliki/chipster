package fi.csc.microarray.client.visualisation.methods;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import fi.csc.microarray.client.visualisation.Visualisation;
import fi.csc.microarray.client.visualisation.VisualisationFrame;
import fi.csc.microarray.client.visualisation.methods.gbrowser.tools.SamBamUtils;
import fi.csc.microarray.databeans.DataBean;
import fi.csc.microarray.exception.MicroarrayException;

public class SamBamViewer extends Visualisation {

	private static int MAX_RECORD_LIMIT = 1024;
	
	public void initialise(VisualisationFrame frame) throws Exception {
		super.initialise(frame);
	}

	@Override
	public JComponent getVisualisation(DataBean data) throws Exception {
		String txt = new SamBamUtils().printSamBam(data.getContentByteStream(), MAX_RECORD_LIMIT);
		JTextPane txtPane = TextViewer.makeTxtPane(txt);
		return new JScrollPane(txtPane);
	}

	@Override
	public boolean canVisualise(DataBean bean) throws MicroarrayException {
		return bean.getName().endsWith(".bam") || bean.getName().endsWith(".sam"); 
	}
}
