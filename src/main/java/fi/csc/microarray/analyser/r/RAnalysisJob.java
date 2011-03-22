/*
 * Created on Jan 27, 2005
 *
 */
package fi.csc.microarray.analyser.r;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import fi.csc.microarray.analyser.AnalysisDescription;
import fi.csc.microarray.analyser.JobCancelledException;
import fi.csc.microarray.analyser.OnDiskAnalysisJobBase;
import fi.csc.microarray.analyser.ProcessPool;
import fi.csc.microarray.analyser.AnalysisDescription.ParameterDescription;
import fi.csc.microarray.config.DirectoryLayout;
import fi.csc.microarray.exception.MicroarrayException;
import fi.csc.microarray.messaging.JobState;
import fi.csc.microarray.messaging.message.JobMessage.ParameterSecurityPolicy;
import fi.csc.microarray.messaging.message.JobMessage.ParameterValidityException;
import fi.csc.microarray.util.IOUtils;

/**
 * Uses R to run actual analysis operations.
 * 
 * @author hupponen, akallio
 */
public class RAnalysisJob extends OnDiskAnalysisJobBase {

	private static final String R_STRING_SEPARATOR = "\"";


	/**
	 * Should closely match the code that is used to output the values in transformVariable(...).
	 * 
	 * @see RAnalysisJob#transformVariable(String, String, boolean)
	 *
	 */
	public static class RParameterSecurityPolicy implements ParameterSecurityPolicy {
		
		private static final int MAX_VALUE_LENGTH = 1000;
		private static String NUMERIC_VALUE_PATTERN = "-?\\d*\\.?\\d*"; // Maybe minus, zero or more digits, maybe point, zero or more digits
		private static String TEXT_VALUE_PATTERN = "[\\w+-_:;\\.]*"; // Only word characters and some special symbols are allowed
		
		/**
		 *  
		 */
		public boolean isValueValid(String value, ParameterDescription parameterDescription) {
			
			// Check parameter size (DOS protection)
			if (value.length() > MAX_VALUE_LENGTH) {
				return false;
			}
			
			// Check parameter content (R injection protection)
			if (parameterDescription.isNumeric()) {
				
				// Numeric value must match the strictly specified pattern
				return value.matches(NUMERIC_VALUE_PATTERN);
				
			} else {
				// Text value must match the strictly specified pattern
				return value.matches(TEXT_VALUE_PATTERN);
			}
			
		}

	}
	
	public static RParameterSecurityPolicy R_PARAMETER_SECURITY_POLICY = new RParameterSecurityPolicy();
	
	static final Logger logger = Logger.getLogger(RAnalysisJob.class);
	
	private static String SCRIPT_SUCCESSFUL_STRING = "script-finished-succesfully";
	private static String SCRIPT_FAILED_STRING = "script-finished-unsuccesfully";
	
	private int rTimeout;
	private CountDownLatch waitRLatch = new CountDownLatch(1);
	
	// injected by handler at right after creation
	private ProcessPool processPool;
	private Process process;
	
	
	private class RProcessMonitor implements Runnable {

		private final String ERROR_MESSAGE_TOKEN = "Error";
		private final String CHIPSTER_NOTE_TOKEN = "CHIPSTER-NOTE:"; 
		
		private ArrayList<String> outputLines;

		public void run() {
			
			logger.debug("R process monitor started.");
			outputLines = new ArrayList<String>();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			boolean readMore = true;
			try {
				for (String line = reader.readLine(); readMore ; line = reader.readLine()) {
					
					// read end of stream --> error
					if (line == null || line.contains(SCRIPT_FAILED_STRING)) {
						updateState(JobState.FAILED, "R script failed");
						readMore = false;
					} 
					
					// read script successful
					// TODO better pattern matching 
					else if (line.contains(SCRIPT_SUCCESSFUL_STRING)) {
						updateState(JobState.COMPLETED, "R script finished successfully");
						readMore = false;
					}
					
					// read normal output
					else {
						outputLines.add(line);
					}
				}
				
				// read the error message and chipster note
				if (getState() == JobState.FAILED) {

					// find the error token
					int errorLineNumber = -1;
					for (int i = outputLines.size(); i > 0 && errorLineNumber == -1; i--) {
						if (outputLines.get(i-1).startsWith(ERROR_MESSAGE_TOKEN)) {
							errorLineNumber = i-1;
						}
					}
				
					// get lines starting from the error token, except for the last "Execution halted"
					if (errorLineNumber != -1) {
						String errorMessage = "";
						errorMessage += outputLines.get(errorLineNumber).substring(ERROR_MESSAGE_TOKEN.length()) + "\n";
						for (int i = errorLineNumber + 1; i < outputLines.size() - 1; i++) {
							errorMessage += outputLines.get(i) + "\n";
						}
						errorMessage = errorMessage.substring(0, errorMessage.lastIndexOf("\n"));
						errorMessage = errorMessage.trim();
						
						// check for chipster note
						if (errorMessage.contains(CHIPSTER_NOTE_TOKEN)) {
							errorMessage = errorMessage.substring(errorMessage.indexOf(CHIPSTER_NOTE_TOKEN) + CHIPSTER_NOTE_TOKEN.length());
							errorMessage = errorMessage.trim();
							updateState(JobState.FAILED_USER_ERROR, "");
						}
						
						outputMessage.setErrorMessage(errorMessage);
					}
				}
				
			} catch (IOException e) {
				// also canceling the job leads here 
				logger.debug("error in monitoring R process.");
				updateState(JobState.ERROR, "reading R output failed.");
			}

			waitRLatch.countDown();
		}

		public String getOutput() {
			StringBuilder output = new StringBuilder();
			for (String line: outputLines) {
				output.append(line + "\n");
			}
			return output.toString();
		}
	
	}
	

	protected RAnalysisJob() {
		this.rTimeout = DirectoryLayout.getInstance().getConfiguration().getInt("comp", "r-timeout");
	}

	
	
	/**
	 * Executes the analysis. 
	 * 
	 * @throws IOException
	 * @throws MicroarrayException 
	 * @throws InterruptedException 
	 */
	protected void execute() throws JobCancelledException {
		cancelCheck();
		updateStateDetailToClient("initialising R");
		
		List<BufferedReader> inputReaders = new ArrayList<BufferedReader>();

		// load handler initialiser
		inputReaders.add(new BufferedReader(new StringReader(analysis.getInitialiser())));
		
		// load work dir initialiser
		logger.debug("job work dir: " + jobWorkDir.getPath());
		inputReaders.add(new BufferedReader(new StringReader("setwd(\"" + jobWorkDir.getName() + "\")\n")));
		
		
		// load input parameters		
		int i = 0; 
		List<String> parameterValues;
		try {
			parameterValues = inputMessage.getParameters(R_PARAMETER_SECURITY_POLICY, analysis);
		} catch (ParameterValidityException e) {
			outputMessage.setErrorMessage("There was an invalid parameter value.");
			outputMessage.setOutputText(e.toString());
			updateState(JobState.FAILED_USER_ERROR, "");
			return;
		}
		for (AnalysisDescription.ParameterDescription param : analysis.getParameters()) {
			String value = new String(parameterValues.get(i));
			String rSnippet = transformVariable(param.getName(), value, param.isNumeric());
			logger.debug("added parameter (" +  rSnippet + ")");
			inputReaders.add(new BufferedReader(new StringReader(rSnippet)));
			i++;
		}

		
		// load input script
		String script = (String)analysis.getImplementation();
		inputReaders.add(new BufferedReader(new StringReader(script)));
		
		// load script finished trigger
		inputReaders.add(new BufferedReader(new StringReader("print(\"" + SCRIPT_SUCCESSFUL_STRING + "\")\n")));
		
		
		// get a process
		cancelCheck();
		logger.debug("getting a process.");;
		try {
			this.process = processPool.getProcess();
		} catch (Exception e) {
			outputMessage.setErrorMessage("Starting R failed.");
			outputMessage.setOutputText(e.toString());
			updateState(JobState.ERROR, "");
			return;
		}
		boolean processAlive = false;
		try {
			process.exitValue();
		} catch (IllegalThreadStateException itse) {
			processAlive = true;
		}
		if (!processAlive) {
			outputMessage.setErrorMessage("Starting R failed.");
			outputMessage.setOutputText("R already finished.");
			updateState(JobState.ERROR, "");
			return;
		}
		
		updateStateDetailToClient("running R");

		
		// launch the process monitor
		cancelCheck();
		logger.debug("about to start the R process monitor.");
		RProcessMonitor processMonitor = new RProcessMonitor();
		new Thread(processMonitor).start();
		
		// write the input to process
		logger.debug("writing the input to R.");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			for (BufferedReader reader : inputReaders) {			
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					writer.write(line);
					writer.newLine();
				}
			}
			writer.flush();
		} catch (IOException ioe) {
			// this happens if R has died before or dies while writing the input
			// process monitor will notice this and set state etc
			logger.debug("writing input failed", ioe);
		} finally {
			IOUtils.closeIfPossible(writer);
		}
		
		// wait for the script to finish
		cancelCheck();
		logger.debug("waiting for the script to finish.");
		try {
			waitRLatch.await(rTimeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			outputMessage.setErrorMessage("Running R was interrupted.");
			outputMessage.setOutputText(e.toString());
			updateState(JobState.ERROR, "");
			return;
		}
		
		// script now finished or timeout
		cancelCheck();
		logger.debug("done waiting for " + analysis.getFullName() + ", state is " + getState());		

		// add output to result message
		String output = processMonitor.getOutput();
		// remove the first line (setwd(...))
		output = output.substring(output.indexOf(("\n")));
		outputMessage.setOutputText(output);
		
		// deal with errors and timeout if needed
		switch (getState()) {
		
		case RUNNING:
			// TODO add execution time this far to the message
			outputMessage.setErrorMessage("R did not finish before timeout.");
			updateState(JobState.TIMEOUT, "");
			return;
		case COMPLETED:
			// set state back to running, notify client
			updateState(JobState.RUNNING, "R script finished successfully");
			updateStateDetailToClient("R script finished successfully");
			return;
		case FAILED:
			// set error message if there is no specific message set already
			if (outputMessage.getErrorMessage() == null || outputMessage.getErrorMessage().equals("")) {
				outputMessage.setErrorMessage("Running R script failed.");
			}
			return;
		case FAILED_USER_ERROR:
			return;
		case ERROR:
			// ProcessMonitor error
			outputMessage.setErrorMessage("Reading R output failed.");
			return;
		default: 
			throw new IllegalStateException("Illegal job state: " + getState());
		}
	}

	@Override
	protected void preExecute() throws JobCancelledException {
		super.preExecute();
	}
	
	protected void postExecute() throws JobCancelledException {
		super.postExecute();
	}
	
	
	protected void cleanUp() {
		try {
			// only try to recycle the process if the script finished succesfully
			// don't recycle at the moment
			if (process != null) {
				//this.resultHandler.getProcessPool().releaseProcess(process, getState().equals(JobState.SUCCESS));
				processPool.releaseProcess(process, false);
			}
		} catch (Exception e) {
			logger.error("error when releasing process. ", e);
		} finally {
			super.cleanUp();
		}

	}
	
	
	
	/**
	 * Converts a name-value -pair into R variable definition.
	 */
	public static String transformVariable(String name, String value, boolean isNumeric) {
		if (!isNumeric) {
			value = R_STRING_SEPARATOR + value + R_STRING_SEPARATOR; // escape strings and such
		}
		name = name.replaceAll(" ", "_"); // remove spaces
		return (name + " <- " + value);
	}



	@Override
	protected void cancelRequested() {
		this.waitRLatch.countDown();
		
	}
	
	public void setProcessPool(ProcessPool processPool) {
		this.processPool = processPool;
	}
}
