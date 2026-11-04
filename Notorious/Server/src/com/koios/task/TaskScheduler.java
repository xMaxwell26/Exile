package com.koios.task;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TaskScheduler<T extends Task> {

	private static final ExecutorService service = Executors.newCachedThreadPool();

	private final List<T> tasks = new ArrayList<>();

	private final Queue<T> newTasks = new ArrayDeque<>();

	public void pulse() {
		
		synchronized (newTasks) {
			T task;

			while ((task = newTasks.poll()) != null) {
				tasks.add(task);
			}
		}

		// TODO: Catch potentially uncaught exception?
		
		//tasks.removeIf(task -> !task.pulse());
		
		for (Iterator<T> t = tasks.iterator(); t.hasNext();) {
			Task task = t.next();
			boolean remove = false;
			try {
				remove = !task.pulse();
			} catch (Exception e) {
				remove = true;
				e.printStackTrace();
			}
			
			if (remove) {
				t.remove();
			}
		}
	}

	public void schedule(T task) {
		if (task.isImmediate()) {
			service.execute(task::execute);
		}

		synchronized (newTasks) {
			newTasks.add(task);
		}
	}

	public void stopAll() {
		newTasks.clear(); // clear any pending tasks
		tasks.forEach(Task::stop); // stop all active tasks
	}

	public List<T> getTasks() {
		return tasks;
	}

}