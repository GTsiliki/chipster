package fi.csc.chipster.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import fi.csc.microarray.analyser.JobCancelledException;
import fi.csc.microarray.analyser.java.JavaAnalysisJobBase;
import fi.csc.microarray.messaging.JobState;

public class TestJavaTool extends JavaAnalysisJobBase {

	@Override
	public String getVVSADL() {
		return 	" ANALYSIS Test/JavaTool (Simple JavaTool test.)" + "\n" + 
				" INPUT GENERIC input.tsv OUTPUT output.tsv, comment.txt" + "\n" +
				" PARAMETER hyvinkö.menee [yes, no] DEFAULT yes (Hyvä parametri)";
 
	}

	@Override
	protected void execute() throws JobCancelledException {
		updateStateToClient(JobState.RUNNING, "Java tool running");
		
		try {
			File inputFile = new File(jobWorkDir, "input.tsv");
			File outputFile = new File(jobWorkDir, "output.tsv");
			FileUtils.copyFile(inputFile, outputFile);
	
			File commentFile = new File(jobWorkDir, "comment.txt");
			FileWriter commentWriter = new FileWriter(commentFile);
			commentWriter.write(inputMessage.getParameters().get(0));
			commentWriter.flush();
			commentWriter.close();
		} catch (IOException ioew) {
			// TODO update state, return
		}
		updateStateToClient(JobState.RUNNING, "Java tool finished");
	}

}