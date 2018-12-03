package storageengine.query;

public class Query {

	public static enum QueryType {
		SELECT, INSERT, DROP, UPDATE
	}

	private QueryType queryType;
	private String tableName, rawQuery;

	public Query(QueryType queryType, String tableName, String rawQuery) {
		this.queryType = queryType;
		this.tableName = tableName;
		this.rawQuery = rawQuery;
	}
}
