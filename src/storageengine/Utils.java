package storageengine;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

public class Utils {

	private static final SecureRandom RANDOM = new SecureRandom();

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

	public static String md5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(str.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
		}
		return null;
	}

	public static String randomString(int count) {
		String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; ++i)
			sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		return sb.toString();
	}
}
