package storageengine.exceptions;

public class QueryExecuteException extends Exception {

	private static final long serialVersionUID = 1L;

	public QueryExecuteException(String error) {
		super(error);
	}

	public QueryExecuteException() {
	}
}
