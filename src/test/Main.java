package test;

import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import storageengine.Storage;
import storageengine.Table;
import storageengine.query.Query;
import storageengine.query.QueryParser;
import storageengine.query.ResultSet;

public class Main {

	public static void main(String[] args) {
		try {
			// QueryParser qp = new QueryParser("select test where abc='d',u='a',d='test'");
			// System.out.println(Arrays.toString(qp.getWhereParts()));
			File soFile = new File("C:\\Users\\lenny\\Documents\\workspaceV2\\storage.txt");
			Storage s;
			if (soFile.exists()) {
				s = new Storage(soFile);
			} else {
				s = Storage.create(soFile);
			}
			
			s.debug = true;

			// create table if not exist
			if (!s.getStorageObject().tableExist("users")) {
				Table t = new Table(new String[] { "id", "name", "age" });
				for (int i = 0; i < 100; i++)
					t.addRecord(new String[] { i + "", "test #" + i, i * new Random().nextInt(10) + "" });
				s.getStorageObject().addTable("users", t);
			}

			Scanner console = new Scanner(System.in);
			while (true) {
				try {
					Query query = new QueryParser(console.nextLine()).parse();
					ResultSet rs = query.execute(s.getStorageObject());
					System.out.println("Results: " + rs.getResult().size());
					// print column names
					System.out.println(Arrays.toString(rs.getTable().getColumn()));
					for (String[] sArr : rs.getResult())
						System.out.println(Arrays.toString(sArr));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error: " + e.getMessage());
				}
				Thread.currentThread().sleep(100);
				System.out.println("----------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

}
