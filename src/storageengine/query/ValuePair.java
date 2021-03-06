package storageengine.query;

import storageengine.exceptions.CompareException;
import storageengine.query.QueryParser.Condition;

public class ValuePair {

	/*
	 * This class store a key and the value with the compare type
	 * the compare type will be always 'EQUAL' if its a SET clause
	 */

	public static enum CompareType {
		EQUAL, NOT_EQUAL, GREATER_THAN, SMALLER_THAN
	}

	private CompareType type;
	private String key, value;

	// the next condition
	private Condition conditionAfter;

	public ValuePair(CompareType type, String key, String value, Condition conditionAfter) {
		this.type = type;
		this.key = key;
		this.value = value;
		this.conditionAfter = conditionAfter;
	}

	public Condition getConditionAfter() {
		return conditionAfter;
	}

	public void setConditionAfter(Condition conditionAfter) {
		this.conditionAfter = conditionAfter;
	}

	public CompareType getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public boolean compare(String checkValue) throws CompareException {
		try {
			switch (type) {
			case EQUAL:
				return checkValue.equalsIgnoreCase(value);
			case NOT_EQUAL:
				return !checkValue.equalsIgnoreCase(value);
			case GREATER_THAN:
				return Integer.valueOf(checkValue) > Integer.valueOf(value);
			case SMALLER_THAN:
				return Integer.valueOf(checkValue) < Integer.valueOf(value);
			}
		} catch (NumberFormatException e) {
			throw new CompareException("cant cast to integer (" + e.getMessage() + ")");
		}
		return false;
	}
}
