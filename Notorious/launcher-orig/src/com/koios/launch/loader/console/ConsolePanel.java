package com.koios.launch.loader.console;

import java.awt.Dimension;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import com.koios.launch.loader.Attributes;

public final class ConsolePanel extends JPanel {

	private static final long serialVersionUID = -1110049356232538238L;

	public void construct() {
		JTextArea consoleTextArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(consoleTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(Attributes.getName() + " Console"), BorderFactory.createEmptyBorder()));
		scroll.setPreferredSize(new Dimension(743, 120));
		consoleTextArea.setEditable(false);
		add(scroll);

		configureOutputs(consoleTextArea);
	}

	private void configureOutputs(JTextArea consoleTextArea) {
		PrintStream outputStream = new PrintStream(new ConsoleOutputStream(consoleTextArea));
		System.setOut(outputStream);
		System.setErr(outputStream);
	}

}