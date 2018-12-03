package storageengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Storage {

	private File f;
	private StorageObject storageObject;

	public Storage(File f) throws Exception {
		this.f = f;
		if (!f.exists() || !f.isFile())
			throw new FileNotFoundException();
		reload();
	}

	public boolean execQuery(String query) {
		
		
		return true;
	}

	public void reload() throws Exception {
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		storageObject = (StorageObject) ois.readObject();
		ois.close();
	}

	public static Storage create(File f) throws Exception {
		StorageObject so = new StorageObject();
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(so);
		oos.close();
		return new Storage(f);
	}
}
