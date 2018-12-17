package storageengine.query;

import java.util.ArrayList;
import java.util.Arrays;

import storageengine.StorageObject;
import storageengine.Table;
import storageengine.exceptions.QueryExecuteException;
import storageengine.exceptions.QueryParseException;
import storageengine.query.QueryParser.Condition;

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
		if (!storageObject.tableExist(tableName)) {
			throw new QueryExecuteException("table '" + tableName + "' not found");
		}
		Table t = storageObject.getTable(tableName);
		ResultSet rs = new ResultSet(t);
		switch (queryType) {
		case SELECT:
			QueryParser qp = new QueryParser(rawQuery);
			if (qp.isWhereClause()) {
				ArrayList<String[]> lastRemovedElements = null;
				Condition lastCondition = null;
				ArrayList<String[]> addTo = null;
				for (ComparePair cp : qp.getWhereParts()) {
					// all rows which are added because the 'OR' condition will saved in the addTo
					// array and will be added after the next condition
					addTo = addToResult(lastRemovedElements, lastCondition, cp, t);
					lastRemovedElements = rs.filter(cp);
					lastCondition = cp.getConditionAfter();
					if (addTo != null)
						rs.getResult().addAll(addTo);
				}
			}
			break;
		default:
			throw new QueryParseException("invalid query type");
		}
		return rs;
	}

	private ArrayList<String[]> addToResult(ArrayList<String[]> lastRemovedElements, Condition condition,
			ComparePair cp, Table t) throws QueryParseException, QueryExecuteException {
		if (lastRemovedElements != null && lastRemovedElements.size() > 0 && condition != null
				&& condition == Condition.OR) {
			// check if we can add removed rows again, when the lastCondition was 'OR'
			ResultSet reAddRs = new ResultSet(t);
			reAddRs.setResult(lastRemovedElements);
			reAddRs.filter(cp);
			return reAddRs.getResult();
		}
		return null;
	}
}
