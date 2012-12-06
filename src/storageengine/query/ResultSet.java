package storageengine.query;

import java.util.ArrayList;

import storageengine.Table;
import storageengine.exceptions.QueryExecuteException;

public class ResultSet {

	private Table table;
	private ArrayList<String[]> result;

	public ResultSet(Table table) {
		this.table = table;
		this.result = new ArrayList<String[]>();
		for (String[] r : table.getRows())
			this.result.add(r);
	}

	public Table getTable() {
		return table;
	}
	
	public ArrayList<String[]> getResult() {
		return result;
	}

	public void filter(String columnName, String value) throws QueryExecuteException {
		int columnIndex = getColumnIndex(columnName);
		for (int i = 0; i < result.size(); i++) {
			String[] r = result.get(i);
			if (!r[columnIndex].equalsIgnoreCase(value))
				result.remove(i);
		}
	}

	public int getColumnIndex(String columnName) throws QueryExecuteException {
		for (int i = 0; i < table.getColumn().length; i++)
			if (table.getColumn()[i].equalsIgnoreCase(columnName))
				return i;
		throw new QueryExecuteException("column '" + columnName + "' doesnt exist");
	}
}
