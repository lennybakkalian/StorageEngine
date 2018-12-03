package storageengine.exceptions;

public class QueryParseException extends Exception {
	private static final long serialVersionUID = 1L;

	public QueryParseException(String error) {
		super(error);
	}

	public QueryParseException() {
	}
}
