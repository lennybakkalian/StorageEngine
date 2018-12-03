package storageengine;

import java.io.Serializable;
import java.util.HashMap;

import storageengine.exceptions.TableAlreadyExistException;

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

	public boolean deleteTable(String table) {
		if (tables.get(table) == null)
			return false;
		tables.remove(table);
		return true;
	}
}
