package storageengine.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import storageengine.StorageUser;
import storageengine.query.Query;
import storageengine.query.QueryParser;

public class Client {

	private StorageUser user;
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pw;
	private ReadThread readThread;

	public Client(Socket socket) {
		this.socket = socket;
		this.readThread = new ReadThread();
		this.readThread.start();
	}

	public StorageUser getUser() {
		return user;
	}

	public Socket getSocket() {
		return socket;
	}

	public void disconnect() {
		try {
			if (!socket.isClosed())
				socket.close();
			if (!readThread.isInterrupted())
				readThread.interrupt();
			System.out.println("[DISCONNECTED] " + socket.getInetAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String s) {
		try {
			pw.println(s);
		} catch (Exception e) {
			e.printStackTrace();
			disconnect();
		}
	}

	private class ReadThread extends Thread {
		public void run() {
			try {
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				pw = new PrintWriter(socket.getOutputStream(), true);
				while (!interrupted()) {
					synchronized (this) {
						String ln = br.readLine();
						if (user == null) {
							// login required

						} else {
							// QueryParser qp = new QueryParser(ln);
							// Query q = qp.parse();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				disconnect();
				interrupt();
			}
		}
	}
}
