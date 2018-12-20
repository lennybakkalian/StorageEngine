package storageengine;

import java.util.ArrayList;

public class Utils {
	public static String[] dynArrToFixedStrArr(ArrayList<String> arr) {
		String[] fixedArr = new String[arr.size()];
		for (int i = 0; i < arr.size(); i++)
			fixedArr[i] = arr.get(i);
		return fixedArr;
	}

	public static String mergeArr(String splitStr, String splitBy, int start) {
		String[] arr = splitStr.split(splitBy);
		if (arr.length < start) {
			System.err.println("[Utils] at mergeArr() start is bigger than length");
			return null;
		}
		StringBuilder sb = new StringBuilder(arr[start]);
		for (int i = start + 1; i < arr.length; i++)
			sb.append(splitBy + arr[i]);
		return sb.toString();
	}

	public static String[] createEmptyStringArr(int size) {
		String[] arr = new String[size];
		for (int i = 0; i < size; i++)
			arr[i] = "";
		return arr;
	}
}
