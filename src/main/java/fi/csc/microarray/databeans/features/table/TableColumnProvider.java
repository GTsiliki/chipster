package fi.csc.microarray.databeans.features.table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import fi.csc.microarray.databeans.Dataset;
import fi.csc.microarray.databeans.features.BasicFeature;
import fi.csc.microarray.databeans.features.Feature;
import fi.csc.microarray.databeans.features.FeatureProvider;
import fi.csc.microarray.databeans.features.FeatureProviderBase;
import fi.csc.microarray.databeans.features.Table;
import fi.csc.microarray.exception.MicroarrayException;
import fi.csc.microarray.util.IOUtils;
import fi.csc.microarray.util.LookaheadLineReader;


/**
 * Exposes tabular data parsing functionality to Feature API. Implementation is in DynamicallyParsedTable.
 * 
 * @see DynamicallyParsedTable
 * @author Aleksi Kallio
 *
 */
public class TableColumnProvider extends FeatureProviderBase {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TableColumnProvider.class);

	private static final Pattern ROW_TOKENISER_REGEX = Pattern.compile("\t");


	public Feature createFeature(String namePostfix, Dataset bean) {
		try {
			return new TableColumn(namePostfix, bean, this);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage() + " (when parsing table data out of " + bean.getName() + ")", e);
		}
	}

	private static class Column {
		public Column(String name) {
			this.name = name;
		}
		String name;
	}

	public static class TableColumnIterable<T> implements Iterable<T> {

		private boolean convertToFloats;
		private String columnName;
		private MatrixParseSettings settings;
		private Dataset dataBean;
		private LinkedList<Integer> columnIndex;

		/**
		 * @param columnIndex list containing the single column index 
		 */
		public TableColumnIterable(Dataset dataBean, MatrixParseSettings settings, LinkedList<Integer> columnIndex, String columnName, boolean convertToFloats) {
			this.dataBean = dataBean;
			this.settings = settings;
			this.columnIndex = columnIndex;
			this.columnName = columnName;
			this.convertToFloats = convertToFloats;
		}

		public Iterator<T> iterator() {
			DynamicallyParsedTable table = new DynamicallyParsedTable(dataBean, settings, columnIndex);
			return new TableColumnIterator<T>(table, columnName, convertToFloats);
		}
	}
	
	public static class TableColumnIterator<T> implements Iterator<T> {

		private DynamicallyParsedTable table;
		private boolean convertToFloats;
		private boolean isValidRow;
		private String columnName;
		
		public TableColumnIterator(DynamicallyParsedTable table, String columnName, boolean convertToFloats) {
			try {
				this.table = table;
				this.columnName = columnName;
				this.convertToFloats = convertToFloats;
				nextRow();
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		}

		public boolean hasNext() {
			return isValidRow;
		}

		@SuppressWarnings(value="unchecked")
		public T next() {
			
			// read "next" (that is, the current)
			T value;
			if (convertToFloats) {
				value = (T)new Float(table.getFloatValue(columnName));
			} else {
				value = (T)table.getStringValue(columnName);
			}
			
			// proceed to "next next"
			nextRow();
			
			return value;
		}

		private void nextRow() {
			this.isValidRow = table.nextRow();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public static class MatrixParseSettings {
		/**
		 * Must include newline if it is part of the header!
		 */
		String headerTerminator = null; 
		String footerStarter = null;
		boolean hasColumnNames = true;
		LinkedHashMap<String, Column> columns = new LinkedHashMap<String, Column>();
	}

	public static class TableColumn extends BasicFeature {

		private MatrixParseSettings settings;
		LinkedList<Integer> indexCollector = new LinkedList<Integer>(); 
		LinkedList<String> nameCollector = new LinkedList<String>();
		
		public TableColumn(String namePostfix, Dataset bean, FeatureProvider factory) throws IOException, MicroarrayException {
			super(bean, factory);

			this.settings = inferSettings(bean);
			
			// iterate over all columns and collect matching ones
			int c = 0;
			for (Column column : settings.columns.values()) {
				boolean match = false;
				if (namePostfix.contains("*")) {
					String[] queryParts = namePostfix.split("\\*");					
					match = queryParts.length == 0 || column.name.startsWith(queryParts[0]); // queryParts.length == 0 => plain asterisk
				} else {
					match = column.name.equals(namePostfix);
				}
				if (match) {					
					indexCollector.add(c);
					nameCollector.add(column.name);
					logger.debug("created column feature for column " + column.name + " (index " + c + ")");
				}
				c++;
			}
		}

		@Override
		public Iterable<Float> asFloats() throws MicroarrayException {
			if (indexCollector.size() != 1) {
				// column name must match exactly one column
				return null;
				
			} else {
				return new TableColumnIterable<Float>(getDataBean(), settings, indexCollector, nameCollector.getFirst(), true);

			}
		}

		@Override
		public Iterable<String> asStrings() throws MicroarrayException {
			if (indexCollector.size() != 1) {
				// column name must match exactly one column
				return null;
				
			} else {
				return new TableColumnIterable<String>(getDataBean(), settings, indexCollector, nameCollector.getFirst(), false);
			}
		}

		@Override
		public Table asTable() throws MicroarrayException {
			if (indexCollector.isEmpty()) {
				return null;
				
			} else {
				return new DynamicallyParsedTable(getDataBean(), settings, indexCollector);
			}
		}

		public MatrixParseSettings inferSettings(Dataset bean) throws IOException, MicroarrayException {
			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(bean.getContentByteStream()));
				LookaheadLineReader source = new LookaheadLineReader(bufferedReader);
				MatrixParseSettings settings = new MatrixParseSettings();

				// check what kind of matrix we are dealing with
				if (source.peekLine() != null && source.peekLine().contains("[CEL]")) {
					logger.debug("parsing cel type");
					settings.headerTerminator = "CellHeader=";
					settings.footerStarter = "\n[MASKS]";
					settings.hasColumnNames = true;

				} else {
					logger.debug("parsing generic type");
					// unknown/generic type, use defaults

					// note: it is safe to call tokeniseRow with null input				
					logger.debug("first line has " + tokeniseRow(source.peekLine(1)).length + " tokens and is " + source.peekLine(1));
					logger.debug("second line has " + tokeniseRow(source.peekLine(2)).length + " tokens and is " + source.peekLine(2));
				}

				// parse away headers, if any
				if (settings.headerTerminator != null) {
					parseAwayHeader(source, settings);
				}

				// parse column names
				String [] columnNames;
				if (settings.hasColumnNames) {
					logger.debug("column name row " + source.peekLine());
					columnNames = tokeniseRow(source.readLine());

				} else {

					logger.debug("no column names, we use numbering");
					columnNames = new String[tokeniseRow(source.peekLine()).length];
					for (int i = 0; i < columnNames.length; i++) {
						columnNames[i] = "column"+i; // generate column names
					}
				}

				// special treatment for extra row name column, if any
				int dataColumnCount = tokeniseRow(source.peekLine(1)).length;
				if (dataColumnCount == (columnNames.length+1)) {
					logger.debug("we have one column for row names");
					// we had an unnamed counter column, add it to index 0
					String[] newColumnNames = new String[columnNames.length + 1];
					System.arraycopy(columnNames, 0, newColumnNames, 1, columnNames.length);
					newColumnNames[0] = " "; // must be space, empty names are not allowed
					columnNames = newColumnNames;

				} else if (dataColumnCount > (columnNames.length+1)) {
					throw new MicroarrayException("table parse error: " + columnNames.length + " column names, but " + dataColumnCount + " values");
				}

				logger.debug("parsed matrix has " + columnNames.length + " columns, column names came with data: " + settings.hasColumnNames);

				// create columns
				for (String columnName : columnNames) {
					logger.debug("added column " + columnName);
					settings.columns.put(columnName, new Column(columnName));
				}

				return settings;

			} finally {
				IOUtils.closeIfPossible(bufferedReader);
			}
		}

		public static void parseAwayHeader(LookaheadLineReader source, MatrixParseSettings settings) throws IOException {
			while (!source.peekLine().contains(settings.headerTerminator)) {
				source.readLine();
			}
			// we must split last line in case header ends in the middle
			String separatingLine = source.peekLine();
			String endOfHeader = separatingLine.substring(separatingLine.indexOf(settings.headerTerminator),
					separatingLine.indexOf(settings.headerTerminator) + settings.headerTerminator.length());
			source.read(endOfHeader.length());
		}

		public static String[] tokeniseRow(String row) {
			if (row == null) {
				return new String[] {};
			} else {				
				String[] result = ROW_TOKENISER_REGEX.split(row);
				
				if (row.endsWith("\t")) {
					// split eats away trailing empty strings, which is bad
					String[] fullResult = new String[result.length + 1];
					System.arraycopy(result, 0, fullResult, 0, result.length);
					fullResult[result.length] = "";
					result = fullResult;
				}
				
				return result;
			}
		}
	}
}
