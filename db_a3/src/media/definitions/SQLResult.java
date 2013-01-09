/**
 * Kapselt die Daten eines SQL-Ergebnisses
 */
package media.definitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Silvio Paschke, Stefan Endrullis
 */


public class SQLResult implements SQLResultInterface{

	private List<String> header = null;
	private List<String[]> body = null;
	private Iterator<String[]> rowIterator;


	public void setHeader(String[] header) {
		this.header =  Arrays.asList(header);
	}

	public void addColumn(String column) {
		header.add(column);
	}

	public void setBody(List<String[]> body) {
		this.body = body;
	}

	public void addRow(String[] row) {
		if (this.body == null) this.body = new ArrayList<String[]>();
		this.body.add(row);
	}

	public List<String> getHeader() {
		return header;
	}

	public String[] getNextRow() throws MediaDbException {
		if (rowIterator == null)
			rowIterator = body.iterator();

		if (rowIterator.hasNext())
			return rowIterator.next();
		else 
			return null;
	}
}
