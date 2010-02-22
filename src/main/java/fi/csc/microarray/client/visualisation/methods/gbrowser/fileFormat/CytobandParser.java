package fi.csc.microarray.client.visualisation.methods.gbrowser.fileFormat;

import java.util.Arrays;

import fi.csc.microarray.client.visualisation.methods.gbrowser.message.BpCoordRegion;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.Chromosome;
import fi.csc.microarray.client.visualisation.methods.gbrowser.message.RegionContent;

public class CytobandParser extends ConstantRowLengthParser {


	public CytobandParser() {
		super(new FileDefinition(
				Arrays.asList(
						new ColumnDefinition[] {

								new ColumnDefinition(ColumnType.CHROMOSOME, Type.STRING, 16),
								new ColumnDefinition(ColumnType.BP_START, Type.LONG, 16),
								new ColumnDefinition(ColumnType.BP_END, Type.LONG, 16),
								new ColumnDefinition(ColumnType.ID, Type.STRING, 16),
								new ColumnDefinition(ColumnType.VALUE, Type.STRING, 16),
								new ColumnDefinition(ColumnType.SKIP, Type.NEWLINE, 1)
						})));
	}

	@Override
	public int getChunkMaxByteLength() {
		return (int)getRowByteLength() * 256;
	}

	@Override
	public long getFilePosition(long readIndex) {
		return readIndex * getRowByteLength();
	}

	@Override
	public long getRowIndex(long filePosition) {
		return filePosition / getRowByteLength();
	}

	@Override
	public RegionContent[] concise(BpCoordRegion readIndexRegion) {

		//Return empty table, otherwise TreeNode gets stuck in calling this again
		return new RegionContent[0];
	}

	@Override
	public BpCoordRegion getBpRegion(long rowIndex) {

		long startBp = (Long)get(rowIndex, ColumnType.BP_START);
		long endBp = (Long)get(rowIndex, ColumnType.BP_END);
		Chromosome chr = (Chromosome)get(rowIndex, ColumnType.CHROMOSOME);

		return new BpCoordRegion(startBp, endBp, chr);
	}

	@Override
	public FileParser clone() {
		FileParser clone = new CytobandParser();
		
		clone.chunk = this.chunk;
		
		return clone;
	}

	@Override
	public String getName() {
		return "USCS Cytoband";
	}
}