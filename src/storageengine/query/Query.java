package storageengine.query;

import java.util.ArrayList;
import java.util.Arrays;

import storageengine.StorageObject;
import storageengine.Table;
import storageengine.Utils;
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

	public QueryType getQueryType() {
		return queryType;
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
			if (qp.containsClause()) {
				ArrayList<String[]> lastRemovedElements = null;
				Condition lastCondition = null;
				ArrayList<String[]> addTo = null;
				// TODO: RECODE OR CLAUSE
				for (ValuePair cp : qp.getWhereParts()) {
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
		case UPDATE:
			// select all affected rows
			rs = new Query(QueryType.SELECT, tableName, rawQuery).execute(storageObject);
			QueryParser updateParser = new QueryParser(rawQuery);
			// update all rows
			for (String[] currentRow : rs.getResult()) {
				// update all columns in the currentRow
				for (ValuePair vp : updateParser.getSetParts()) {
					int columnIndex = t.getColumnIndex(vp.getKey());
					currentRow[columnIndex] = vp.getValue();
				}
			}
			break;
		case DROP:
			// select all affected rows
			rs = new Query(QueryType.SELECT, tableName, rawQuery).execute(storageObject);
			// remove selected rows
			for (String[] r : rs.getResult())
				t.removeRecord(r);
			break;
		case INSERT:
			// get current table
			rs = new Query(QueryType.SELECT, tableName, rawQuery).execute(storageObject);
			QueryParser insertParser = new QueryParser(rawQuery);
			String[] newRow = Utils.createEmptyStringArr(rs.getTable().getColumn().length);
			for (ValuePair vp : insertParser.getSetParts()) {
				int columnIndex = t.getColumnIndex(vp.getKey());
				newRow[columnIndex] = vp.getValue();
			}
			rs.getTable().addRecord(newRow);
			break;
		default:
			throw new QueryParseException("invalid query type");
		}
		return rs;
	}

	private ArrayList<String[]> addToResult(ArrayList<String[]> lastRemovedElements, Condition condition, ValuePair cp,
			Table t) throws QueryParseException, QueryExecuteException {
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

	// TODO: TEST
	public static String escape(String value) {
		value.replaceAll("\\", "/");
		value.replaceAll("\'", "\\\'");
		value.replaceAll("\"", "\\\"");
		return value;
	}
}
