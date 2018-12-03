package storageengine.query;

import storageengine.exceptions.QueryParseException;
import storageengine.query.Query.QueryType;

public class QueryParser {
	public Query parse(String queryStr) throws QueryParseException {
		try {
			String[] whitespaceArgs = queryStr.split(" ");
			if (whitespaceArgs.length < 2)
				throw new QueryParseException("not enough arguments");
			QueryType queryType;
			String tableName;
			switch (whitespaceArgs[0].toLowerCase()) {
			case "select":
				queryType = QueryType.SELECT;
				break;
			case "insert":
				queryType = QueryType.INSERT;
				break;
			case "drop":
				queryType = QueryType.DROP;
				break;
			case "update":
				queryType = QueryType.UPDATE;
				break;
			default:
				throw new QueryParseException("please specify the type: select, insert or drop");
			}

			tableName = whitespaceArgs[1];

		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new QueryParseException();
	}
}
