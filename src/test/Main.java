package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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

			// create table if not exist
			Table t = new Table(new String[] { "id", "name", "age" });
			t.addRecord(new String[] { "1", "test", "10" });
			s.getStorageObject().addTable("users", t);

			Query q = new QueryParser("select users where name='test',age='10'").parse();
			ResultSet rs = q.execute(s.getStorageObject());
			if (rs != null) {
				System.out.println("results: " + rs.getResult().size());
				ArrayList<String[]> result = rs.getResult();
				for (String[] arr : result)
					System.out.println(Arrays.toString(arr));
			} else
				System.out.println("is null");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

}
