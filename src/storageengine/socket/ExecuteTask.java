package storageengine.socket;

import storageengine.query.Query;

public class ExecuteTask {

	private Client c;
	private Query query;

	public ExecuteTask(Client c, Query query) {
		this.c = c;
		this.query = query;
	}

	public void process() {

	}
}
