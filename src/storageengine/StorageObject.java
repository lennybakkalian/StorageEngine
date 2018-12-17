package storageengine;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import storageengine.exceptions.TableAlreadyExistException;
import storageengine.exceptions.TableDontExistException;

public class StorageObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private HashMap<String, Table> tables;

	public StorageObject() {
		tables = new HashMap<String, Table>();
	}

	public void addTable(String name, Table table) throws TableAlreadyExistException {
		if (tables.get(name) != null)
			throw new TableAlreadyExistException();
		tables.put(name, table);
	}

	public Table getTable(String table) {
		return tables.get(table);
	}

	public void deleteTable(String table) throws TableDontExistException {
		if (tables.get(table) == null)
			throw new TableDontExistException();
		tables.remove(table);
	}

	public boolean tableExist(String table) {
		return getTable(table) != null;
	}

	public String getDataRaw() {
		StringBuilder raw = new StringBuilder();
		for (String s : tables.keySet()) {
			Table t = tables.get(s);
			raw.append(Arrays.toString(t.getColumn()));
			for (String[] c : t.getRows())
				raw.append(Arrays.toString(c));
		}
		return raw.toString();
	}
}
