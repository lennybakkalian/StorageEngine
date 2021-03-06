package storageengine.query;

import java.util.ArrayList;

import storageengine.Table;
import storageengine.exceptions.QueryExecuteException;
import storageengine.exceptions.QueryParseException;

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
	
	public void setResult(ArrayList<String[]> result) {
		this.result = result;
	}

	public ArrayList<String[]> filter(ValuePair cp) throws QueryExecuteException, QueryParseException {
		return filterArray(table, result, cp);
	}

	// STATIC STUFF

	public static ArrayList<String[]> filterArray(Table table, ArrayList<String[]> result, ValuePair cp)
			throws QueryExecuteException, QueryParseException {
		ArrayList<String[]> removedElements = new ArrayList<String[]>();
		int columnIndex = table.getColumnIndex(cp.getKey());
		for (int i = result.size() - 1; i >= 0; i--)
			if (!cp.compare(result.get(i)[columnIndex])) {
				removedElements.add(result.get(i));
				result.remove(i);
			}
		return removedElements;
	}
}
