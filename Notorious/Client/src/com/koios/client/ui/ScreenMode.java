package com.koios.client.ui;

/**
 * The different screen modes the client can enter
 * 
 * @author Timothy Howard <Arithium>
 *
 */
public enum ScreenMode {

	/**
	 * The client is a fixed size of 765x503
	 */
	FIXED,

	/**
	 * The client can be resizes to any size
	 */
	RESIZABLE, 
	
	/**
	 * The client is the size of the screen resolution
	 */
	FULLSCREEN;

	public boolean isFixed() {
		return this == FIXED;
	}
}