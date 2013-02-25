package fi.csc.microarray.client.visualisation.methods.gbrowser.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.ColumnType;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.HeaderTsvParser;
import fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat.TsvParser;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.BpCoord;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.Chromosome;
import fi.csc.microarray.util.Strings;

public class TsvSorter {

	private TsvParser parser;
	private int chrCol;
	private int bpCol;
	private ChromosomeNormaliser chromosomeNormaliser = new ChromosomeNormaliser() {
		@Override
		public String normaliseChromosome(String chromosomeName) {
			// Do default chromosome name normalisation
			return chromosomeName.replace("chr", "").replace(".fa", "");
		}
	};

	public void sort(File in, File out, TsvParser parser) throws Exception {
		this.parser = parser;
		this.chrCol = parser.getFileDefinition().indexOf(ColumnType.CHROMOSOME);
		this.bpCol = parser.getFileDefinition().indexOf(ColumnType.BP_START);
		externalSort(in, out);
	}
	
	public void sort(File in, File out, TsvParser parser, ChromosomeNormaliser chromosomeNormaliser) throws Exception {
		this.chromosomeNormaliser = chromosomeNormaliser;
		sort(in, out, parser);
	}
	
	public void sort(File in, File out,  ChromosomeNormaliser chromosomeNormaliser, int chrColumn, int startColumn) throws Exception {
		this.chromosomeNormaliser = chromosomeNormaliser;
		this.chrCol = chrColumn;
		this.bpCol = startColumn;
		externalSort(in, out);
	}	

	private class Row extends BpCoord {

		public String line;

		public Row(String line) {
			super(null, null);

			this.line = line;
			String[] splitted = line.split("\t");
			String chrStr = splitted.length > chrCol ? splitted[chrCol] : "";
			
			// If chromosome name exists, normalise it
			if (!"".equals(chrStr)) {
				chrStr = chromosomeNormaliser.normaliseChromosome(chrStr);
				splitted[chrCol] = chrStr;
				this.line = Strings.delimit(Arrays.asList(splitted), "\t"); // replace back to raw line
			}
			String bpStr = splitted.length > bpCol ? splitted[bpCol] : "";

			chr = new Chromosome(chrStr);

			if (bpStr.equals("")) {
				bp = -1l;
			} else {
				bp = Long.parseLong(bpStr);
			}
		}
	}

	/**
	 * Based on http://www.codeodor.com/index.cfm/2007/5/14/Re-Sorting-really-BIG-files---the-Java-source-code/1208
	 * 
	 * @param infile
	 * @param outfile
	 * @throws IOException
	 * @throws GBrowserException
	 */
	private void externalSort(File infile, File outfile) throws IOException, GBrowserException {

		// Start reading
		BufferedReader initReader = new BufferedReader(new FileReader(infile));
		
		// Read header row, if exists
		String headerRow = null;
		if (parser != null && parser.getHeaderLength(infile) > 0) {
			headerRow = initReader.readLine(); // we assume header is always a single row!
		}

		// Create and sort chunks
		ArrayList<Row> rowBatch = new ArrayList<Row>(500000);
		boolean quit = false;
		int numFiles = 0;

		while (!quit) {

			// showProgress("Reading...");

			// limit chunks to 200MB
			int size = 0;
			while (size < 200000000) {
				// while (size < 10000000) {
				String line = initReader.readLine();

				if (line == null) {
					quit = true;
					break;
				}

				rowBatch.add(new Row(line));
				size += line.length();
			}

			// showProgress("Sorting...");

			// Use Java's sort.
			Collections.sort(rowBatch);

			// showProgress("Writing...");

			// write to disk
			FileWriter fw = new FileWriter(infile + "_chunk" + numFiles);
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < rowBatch.size(); i++) {
				bw.append(rowBatch.get(i).line + "\n");
			}
			bw.close();
			numFiles++;
			rowBatch.clear();
		}

		// showProgress("Merging...");

		mergeFiles(infile.getAbsolutePath(), outfile, numFiles, headerRow);

		// showProgress("DONE");

		initReader.close();
	}

	private void mergeFiles(String inputFilePath, File outputFilePath, int numChunkFiles, String headerRow) throws IOException, GBrowserException {

		// Initialise
		ArrayList<BufferedReader> mergefbr = new ArrayList<BufferedReader>();
		ArrayList<Row> filerows = new ArrayList<Row>();
		FileWriter fw = new FileWriter(outputFilePath);
		BufferedWriter bw = new BufferedWriter(fw);

		// Write header, if needed
		if (headerRow != null) {
			bw.append(headerRow + "\n");	
		}		

		// Merge chunks
		boolean someFileStillHasRows = false;
		for (int i = 0; i < numChunkFiles; i++) {
			mergefbr.add(new BufferedReader(new FileReader(inputFilePath + "_chunk" + i)));

			// get the first row
			String line = mergefbr.get(i).readLine();
			if (line != null) {
				filerows.add(new Row(line));
				someFileStillHasRows = true;
			} else {
				filerows.add(null);
			}
		}

		Row row;
		while (someFileStillHasRows) {
			Row min;
			int minIndex = 0;

			row = filerows.get(0);
			if (row != null) {
				min = row;
				minIndex = 0;
			} else {
				min = null;
				minIndex = -1;
			}

			// check which one is min
			for (int i = 1; i < filerows.size(); i++) {
				row = filerows.get(i);
				if (min != null) {

					if (row != null && (row.compareTo(min) < 0)) {
						minIndex = i;
						min = filerows.get(i);
					}
				} else {
					if (row != null) {
						min = row;
						minIndex = i;
					}
				}
			}

			if (minIndex < 0) {
				someFileStillHasRows = false;
			} else {
				// write to the sorted file
				bw.append(filerows.get(minIndex).line + "\n");

				// get another row from the file that had the min
				String line = mergefbr.get(minIndex).readLine();
				if (line != null) {
					filerows.set(minIndex, new Row(line));
				} else {
					filerows.set(minIndex, null);
				}
			}
			// check if one still has rows
			for (int i = 0; i < filerows.size(); i++) {

				someFileStillHasRows = false;
				if (filerows.get(i) != null) {
					if (minIndex < 0) {
						throw new GBrowserException("Error in sorting: " + "mindex lt 0 and found row not null" + filerows.get(i));
					}
					someFileStillHasRows = true;
					break;
				}
			}

			// check the actual files one more time
			if (!someFileStillHasRows) {
				// write the last one not covered above
				for (int i = 0; i < filerows.size(); i++) {
					if (filerows.get(i) == null) {
						String line = mergefbr.get(i).readLine();
						if (line != null) {

							someFileStillHasRows = true;
							filerows.set(i, new Row(line));
						}
					}
				}
			}
		}

		// close all the files
		bw.close();
		fw.close();
		for (int i = 0; i < mergefbr.size(); i++)
			mergefbr.get(i).close();

		// Delete all of the chunk files.
		for (int i = 0; i < numChunkFiles; i++) {
			File f = new File(inputFilePath + "_chunk" + i);
			f.delete();
		}
	}

	public static void main(String[] args) throws Exception {

		String filename = "/home/akallio/Desktop/cisREDgroup_contents_for_40193-STAT1_trimmed.tsv";
		String resultFilename = filename + ".sorted";
		new TsvSorter().sort(new File(filename), new File(resultFilename), new HeaderTsvParser());

		// FileDefinition def = new BEDParser().getFileDefinition();
		// new TsvSorter().sort(new File("/home/akallio/Desktop/STAT1/STAT1_peaks.bed"), new
		// File("/home/akallio/Desktop/STAT1/STAT1_peaks_sorted.bed"), def.indexOf(ColumnType.CHROMOSOME),
		// def.indexOf(ColumnType.BP_START));

		// FileDefinition def = new ElandParser().getFileDefinition();
		// new TsvSorter().sort(new File("infile.txt"), new File("infile.txt"), def.indexOf(ColumnType.CHROMOSOME),
		// def.indexOf(ColumnType.BP_START));
	}
}