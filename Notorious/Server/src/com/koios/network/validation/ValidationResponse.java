package com.koios.network.validation;

public class ValidationResponse {

	private final boolean success;
	private final boolean banned;
	private final boolean muted;

	public ValidationResponse(boolean success, boolean banned, boolean muted) {
		this.success = success;
		this.banned = banned;
		this.muted = muted;
	}

	public boolean success() {
		return success;
	}

	public boolean isBanned() {
		return banned;
	}

	public boolean isMuted() {
		return muted;
	}

}
