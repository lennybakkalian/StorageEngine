package storageengine.query;

import java.util.ArrayList;

import storageengine.exceptions.QueryParseException;
import storageengine.query.ComparePair.CompareType;
import storageengine.query.Query.QueryType;

public class QueryParser {

	private String queryStr;

	public static enum Condition {
		AND, OR
	}

	public QueryParser(String queryStr) {
		this.queryStr = queryStr;
	}

	public boolean isWhereClause() {
		String[] qParts = queryStr.split(" ");
		return qParts.length > 2 && qParts[2].equalsIgnoreCase("where");
	}

	public ArrayList<ComparePair> getWhereParts() throws QueryParseException {
		if (!isWhereClause())
			throw new QueryParseException("cant parse 'where' parts, because query isnt a 'where' statement");
		// read char for char to check if commas are in a string
		ArrayList<ComparePair> whereParts = new ArrayList<ComparePair>();
		String[] chars = queryStr.split(" ")[3].split("");
		boolean stringMode = false;
		String currentKey = null, currentValue = null;
		String splitStr = "";
		CompareType currentCompareType = null;
		Condition currentCondition = null;
		for (int i = 0; i < chars.length; i++) {
			String c = chars[i];
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
						ComparePair cp = new ComparePair(currentCompareType, currentKey, currentValue, null);
						whereParts.add(cp);
						// TODO: rem me
						//System.out.println(currentCompareType + " : " + currentKey + " : " + currentValue);
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
						default:
							// invalid char, throw parse exception
							throw new QueryParseException(
									"invalid condition after compare '" + chars[i + 1] + "' at: " + queryStr);
						}
						// update condition
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
		return whereParts;
	}

	public Query parse() throws QueryParseException {
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
