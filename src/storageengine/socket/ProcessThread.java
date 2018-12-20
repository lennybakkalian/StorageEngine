package storageengine.socket;

import java.util.Stack;

public class ProcessThread extends Thread {

	private Stack<ExecuteTask> executeTasks;

	public ProcessThread() {
		executeTasks = new Stack<ExecuteTask>();
	}

	public void addQuery(ExecuteTask executeTask) {
		executeTasks.add(executeTask);
	}

	public void run() {
		while (!interrupted()) {
			synchronized (this) {
				if (executeTasks.size() > 0) {
					// process
					executeTasks.pop().process();
				}
			}
		}
	}
}
