package storageengine.socket;

import java.net.ServerSocket;
import java.net.Socket;

import storageengine.Storage;

public class ServerThread extends Thread {

	private Storage storage;

	public ServerThread(Storage storage) throws Exception {
		this.storage = storage;
		this.storage.server = new ServerSocket(storage.port);
	}

	public Storage getStorage() {
		return storage;
	}

	public void run() {
		try {
			while (!interrupted() && !storage.server.isClosed()) {
				synchronized (storage) {
					Socket client = storage.server.accept();
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
