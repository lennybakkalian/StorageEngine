package storageengine.exceptions;

public class TableAlreadyExistException extends Exception {
	private static final long serialVersionUID = 1L;

	public TableAlreadyExistException() {
		super("Table already exist");
	}

}
