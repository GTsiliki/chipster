package fi.csc.microarray.client.visualisation.methods.genomeBrowser.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class TsvToConstant {

	public static void main(String[] args) {


		//chr1	hg18_refGene	exon	4225	4692	0.000000	-	.	gene_id "NR_024540"; transcript_id "NR_024540"; 
		//int[] fieldLengths = new int[] { 16, 16, 16, 16, 16, 16, 2, 2, 64};
		
		
		// Pipeline CASAVA output
		//int[] fieldLengths = new int[] { 16, 8, 8, 8, 16, 16, 2, 2, 64, 64, 32, 16, 16, 2, 16, 16, 2, 2, 2, 2, 2, 2, 2, 2};
		
		//BED
		//int[] fieldLengths = new int[] { 16, 16, 16, 64, 16, 16};
		
		//Eland export
		int[] fieldLengths = new int[] { 32, 64, 8, 8, 8, 8, 16, 16, 2 };
		
		System.out.println("File converting started");
		Long startTime = System.currentTimeMillis();

		File in = new File("sorted.txt");
		File out = new File("constant.fsf");			

		BufferedWriter writer;
		BufferedReader reader;
		try {
			writer = new BufferedWriter(new FileWriter(out));
			reader = new BufferedReader(new FileReader(in));

			int i = 0;
			String line;
			while((line = reader.readLine()) != null){

				String[] cols = line.split("\t");

				StringBuilder constLine = new StringBuilder();

				for (int j = 0; j < fieldLengths.length; j++){
					//System.out.println(j);
					constLine.append(fillWithSpaces(cols[j], fieldLengths[j]));
				}
				constLine.append("\n");

				writer.write(constLine.toString());

				if( i % 10000 == 0){
					System.out.println("" + (int)((line.length() * i) / (float)in.length() * 100) + " % ");
				}												

				i++;
			}

			writer.flush();
			writer.close();
			reader.close();
			System.out.println("DONE in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static String fillWithSpaces(String orig, int length){
		String spaces = "                                                                                 ";
		return orig + spaces.substring(0, length - orig.length());
	}
}