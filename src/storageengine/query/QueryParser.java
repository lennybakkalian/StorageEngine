package storageengine.query;

import java.util.ArrayList;
import java.util.Arrays;

import storageengine.Utils;
import storageengine.exceptions.QueryParseException;
import storageengine.query.Query.QueryType;
import storageengine.query.ValuePair.CompareType;

public class QueryParser {

	private String queryStr;
	private ArrayList<ValuePair> whereParts;
	private ArrayList<ValuePair> setParts;

	public static enum Clause {
		WHERE, SET, ORDER
	}

	public static enum Condition {
		AND, OR
	}

	public QueryParser(String queryStr) throws QueryParseException {
		this.queryStr = queryStr;
		this.whereParts = new ArrayList<ValuePair>();
		this.setParts = new ArrayList<ValuePair>();
		if (containsClause())
			processQuery();
	}

	public boolean containsClause() {
		String[] qParts = queryStr.split(" ");
		if (qParts.length > 2) {
			switch (qParts[2].toLowerCase()) {
			case "where":
			case "set":
			case "order":
				return true;
			}
		}
		return false;
	}

	public ArrayList<ValuePair> getWhereParts() {
		return whereParts;
	}

	public ArrayList<ValuePair> getSetParts() {
		return setParts;
	}

	private void processQuery() throws QueryParseException {
		whereParts.clear();
		setParts.clear();
		// read char for char to check if commas are in a string
		String fullString = Utils.mergeArr(queryStr, " ", 3);
		String[] chars = fullString.split("");
		boolean stringMode = false;
		String currentKey = null, currentValue = null;
		String splitStr = "";
		CompareType currentCompareType = null;
		Condition currentCondition = null;
		// TODO: check if the currentClause really starts with WHERE
		String[] qParts = queryStr.split(" ");
		Clause currentClause = null;
		if (qParts.length > 2) {
			switch (qParts[2].toLowerCase()) {
			case "where":
				currentClause = Clause.WHERE;
				break;
			case "set":
				currentClause = Clause.SET;
				break;
			case "order":
				currentClause = Clause.ORDER;
				break;
			default:
				throw new QueryParseException("invalid clause: " + qParts[2]);
			}
		}
		Query q = parse();
		for (int i = 0; i < chars.length; i++) {
			String c = chars[i];
			// check if currentClause is valid
			if (currentClause == Clause.SET
					&& (q.getQueryType() == QueryType.SELECT || q.getQueryType() == QueryType.DROP)) {
				throw new QueryParseException("cant use 'set' in '" + q.getQueryType() + "' statement");
			}
			if (stringMode) {
				// read string until splitStr without backslash
				if (c.equals(splitStr)) {
					// check if previous character was backslash
					// VUL: if user input contains a backslash in the last character, they could
					// escape the query, because the quote gets ignored
					if (chars[i - 1].equals("\\")) {
						// remove backslash and add the splitStr
						currentValue = currentValue.substring(0, currentValue.length() - 1) + splitStr;
					} else {
						// end string mode
						stringMode = false;
						// save where part
						ValuePair cp = new ValuePair(currentCompareType, currentKey, currentValue, null);
						switch (currentClause) {
						case WHERE:
							whereParts.add(cp);
							break;
						case SET:
							setParts.add(cp);
							break;
						case ORDER:
							break;
						}
						currentKey = null;
						currentValue = null;
						// check if there is a next condition
						// if not: ignore rest
						if (chars.length <= i + 1)
							break;
						switch (chars[i + 1]) {
						case "&":
							currentCondition = Condition.AND;
							break;
						case "/":
							currentCondition = Condition.OR;
							break;
						case " ":
							// exit where or set clause and check for next one
							String[] nextTokens = fullString.substring(i, fullString.length()).split(" ");
							switch (nextTokens[1].toLowerCase()) {
							case "set":
								currentClause = Clause.SET;
								break;
							case "where":
								currentClause = Clause.WHERE;
								break;
							case "order":
								currentClause = Clause.ORDER;
								break;
							default:
								throw new QueryParseException(
										"invalid clause at " + fullString.substring(i, fullString.length())
												+ " - please use one of these: 'set/where/order'");
							}
							// skip chars, so we dont have to read the clause again
							// and add '1' because the whitespace
							i += nextTokens[1].length() + 1;
							break;
						default:
							// invalid char, throw parse exception
							throw new QueryParseException(
									"invalid condition after compare '" + chars[i + 1] + "' at: " + queryStr);
						}
						// update condition
						if (currentClause == Clause.WHERE)
							cp.setConditionAfter(currentCondition);
						i++;
					}
				} else {
					currentValue += c;
				}
			} else {
				if (c.equals("=") || c.equals("!") || c.equals("<") || c.equals(">")) {
					switch (c) {
					case "=":
						currentCompareType = CompareType.EQUAL;
						break;
					case "!":
						currentCompareType = CompareType.NOT_EQUAL;
						break;
					case "<":
						currentCompareType = CompareType.SMALLER_THAN;
						break;
					case ">":
						currentCompareType = CompareType.GREATER_THAN;
						break;
					}
					// check if currentKey isnt null
					if (currentKey == null)
						throw new QueryParseException("key isnt defined at: " + queryStr);
					// check if next char is " or '
					if (chars.length <= i + 1)
						throw new QueryParseException("empty value after '=' at: " + currentKey);
					String nextChar = chars[i + 1];
					if (nextChar.equals("\"") || nextChar.equals("'")) {
						currentValue = "";
						// set ' or " as delimiter for the current string
						splitStr = nextChar;
						// read next characters as string
						stringMode = true;
						// skip ' or " character
						i++;
					} else {
						throw new QueryParseException("expexted ' or \" after compare char at: " + queryStr);
					}
				} else {
					// add current char to currentKey
					currentKey = currentKey == null ? c : currentKey + c;
				}
			}
		}
		// check if last string was closed with splitStr
		if (currentKey != null)
			if (currentValue == null)
				throw new QueryParseException("no equal character after key at: " + currentKey);
			else
				throw new QueryParseException("expected " + splitStr + " to end the string from the key:" + currentKey
						+ " value:" + currentValue);
	}

	public Query parse() throws QueryParseException {
		try {
			String[] whitespaceArgs = queryStr.split(" ");
			if (whitespaceArgs.length < 2)
				throw new QueryParseException("not enough arguments (" + whitespaceArgs.length + ")");
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
				throw new QueryParseException("invalid type: " + whitespaceArgs[0] + " - please specify the type: select, insert, update or drop");
			}

			tableName = whitespaceArgs[1];
			return new Query(queryType, tableName, queryStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new QueryParseException();
	}

	// STATIC METHODS
	public static String[] parseWhereClause(String part) throws QueryParseException {
		// part must be column_name='filter'
		String[] whereParts = part.split("=");
		if (whereParts.length == 1)
			throw new QueryParseException("missing equal char at: " + part);
		String value = whereParts[1];
		if ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\""))) {
			value = value.substring(1, value.length() - 1);
			return new String[] { whereParts[0], value };
		} else {
			throw new QueryParseException("missing \" or ' char at: " + part);
		}
	}
}
