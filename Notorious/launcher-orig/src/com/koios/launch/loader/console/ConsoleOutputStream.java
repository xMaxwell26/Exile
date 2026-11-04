package com.koios.launch.loader.console;

import java.io.OutputStream;

import javax.swing.JTextArea;

public final class ConsoleOutputStream extends OutputStream {

	private final JTextArea consoleArea;

	protected ConsoleOutputStream(JTextArea consoleArea) {
		this.consoleArea = consoleArea;
	}

	@Override
	public void write(int data) {
		consoleArea.append(String.valueOf((char) data));
	}

}