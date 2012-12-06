package storageengine;

import java.io.Serializable;
import java.util.ArrayList;

public class Table implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] column;
	private ArrayList<String[]> rows;

	public Table(String[] column) {
		this.column = column;
		this.rows = new ArrayList<String[]>();
	}

	public ArrayList<String[]> getRows() {
		return rows;
	}
	
	public String[] getColumn() {
		return column;
	}
	
	public void addRecord(String[] row) {
		rows.add(row);
	}

}
