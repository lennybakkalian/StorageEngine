package storageengine.query;

import storageengine.StorageObject;
import storageengine.Table;
import storageengine.Utils;
import storageengine.exceptions.QueryExecuteException;
import storageengine.exceptions.QueryParseException;

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

	public ResultSet execute(StorageObject storageObject) throws QueryExecuteException, QueryParseException {
		// check if table exist
		Table t = storageObject.getTable(tableName);
		if (t == null) {
			throw new QueryExecuteException("table '" + tableName + "' not found");
		}
		ResultSet rs = new ResultSet(t);
		switch (queryType) {
		case SELECT:
			QueryParser qp = new QueryParser(rawQuery);
			if (qp.isWhereClause()) {
				for(String f: qp.getWhereParts()) {
					String columnName= f.split("=")[0];
					String value = Utils.mergeArr(f, "=", 1);
					rs.filter(columnName, value);
				}
			}
			break;
		default:
			throw new QueryParseException("invalid query type");
		}

		return rs;
	}
}
