package com.koios.task;

public abstract class Task {

	private boolean immediate;
	private int countdown;
	private boolean running = true;
	private int maximumIterations;

	private final int delay;

	public Task(int delay, int maximumIterations) {
		this(delay);
		this.maximumIterations = maximumIterations;
	}

	public Task() {
		this(1);
	}

	public Task(int delay) {
		this(delay, false);
	}

	public Task(int delay, boolean immediate) {
		this.delay = countdown = delay;
		this.immediate = immediate;
	}

	public boolean pulse() {
		if (running && --countdown == 0) {
			execute();
			countdown = delay;

			if (maximumIterations > 0 && --maximumIterations == 0) {
				stop();
			}
		}

		return running;
	}

	public boolean isImmediate() {
		return immediate;
	}

	public boolean isRunning() {
		return running;
	}

	public final void stop() {
		running = false;
		onStop();
	}

	protected void onStop() {
		// Method intended to be overridden
	}

	protected abstract void execute();

}