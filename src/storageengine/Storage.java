package storageengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileAlreadyExistsException;

import storageengine.exceptions.QueryExecuteException;
import storageengine.exceptions.QueryParseException;
import storageengine.query.Query;
import storageengine.query.QueryParser;
import storageengine.query.ResultSet;

public class Storage {

	private File f;
	private StorageObject storageObject;
	private boolean open = false;
	private UpdateThread updateThread;
	public boolean debug = false;

	public Storage(File f) throws Exception {
		this.f = f;
		if (!f.exists() || !f.isFile())
			throw new FileNotFoundException();
		open = true;
		load();
		updateThread = new UpdateThread();
		updateThread.start();
	}

	public ResultSet execQuery(String query) throws QueryExecuteException, QueryParseException {
		Query q = new QueryParser(query).parse();
		return q.execute(storageObject);
	}

	public StorageObject getStorageObject() {
		return storageObject;
	}

	public void close() {
		open = false;
		updateThread.interrupt();
	}

	public void load() throws Exception {
		debug("load database... " + f.getPath());
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		storageObject = (StorageObject) ois.readObject();
		ois.close();
		debug("loaded!");
	}

	public void save() throws Exception {
		debug("save database... " + f.getPath());
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(storageObject);
		oos.close();
		debug("saved!");
	}

	public static Storage create(File f) throws Exception {
		if (f.exists())
			throw new FileAlreadyExistsException("StorageObject file already exist");
		StorageObject so = new StorageObject();
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(so);
		oos.close();
		return new Storage(f);
	}

	public void debug(String msg) {
		if (debug)
			System.out.println("[DEBUG] " + msg);
	}

	private class UpdateThread extends Thread {
		public void run() {
			String oldData = storageObject.getDataRaw();
			while (!interrupted() && open) {
				synchronized (this) {
					try {
						sleep(5000);
						// save current storageObject file if anything changed
						if (!oldData.equals(storageObject.getDataRaw())) {
							oldData = storageObject.getDataRaw();
							save();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
