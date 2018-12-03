package test;

import java.io.File;

import storageengine.Storage;

public class Main {

	public static void main(String[] args) {
		try {
			Storage s = Storage.create(new File("C:\\Users\\lenny.bakkalian\\Desktop\\dbfile.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
