package com.koios.launch.loader.frame;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.koios.launch.loader.Attributes;
import com.koios.launch.loader.console.ConsolePanel;
import com.koios.launch.loader.listeners.FileActionListener;
import com.koios.launch.loader.listeners.LinksActionListener;
import com.koios.launch.loader.listeners.ToolsActionListener;

public final class LoaderFrame extends Frame {
	/*
	 * Fall-back to AWT for full support on MacIntosh systems. I would use JavaFX but since the target of this build is
	 * 1.7 I am unable to support it without a large external library addition. :/
	 */

	private static final long serialVersionUID = 1792100606729740404L;

	private final ConsolePanel consolePanel = new ConsolePanel();

	public LoaderFrame() {
		super(Attributes.getName() + " Client Loader");
	}

	public void construct() {
		if (Attributes.showConsole()) {
			consolePanel.construct();
			add(consolePanel, BorderLayout.SOUTH);
		}

		if (Attributes.showTools()) {
			configureComponents();
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		requestFocus();
		toFront();
		setResizable(false);
		setVisible(true);
	}

	private void configureComponents() {
		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu("File");
		Menu toolsMenu = new Menu("Tools");
		Menu linksMenu = new Menu("Links");

		MenuItem[] files = { new MenuItem("Exit") };
		for (MenuItem file : files) {
			fileMenu.add(file);
			file.addActionListener(new FileActionListener());
		}

		MenuItem[] links = { new MenuItem("Forums"), new MenuItem("Rules"), new MenuItem("Donate"),
				new MenuItem("Vote"), new MenuItem("Highscores"), new MenuItem("Report a bug"), new MenuItem("Guides"), };
		for (MenuItem link : links) {
			linksMenu.add(link);
			link.addActionListener(new LinksActionListener());
		}

		MenuItem[] tools = { new MenuItem("Take screenshot"), new MenuItem("Browse screenshots") };
		for (MenuItem tool : tools) {
			toolsMenu.add(tool);
			tool.addActionListener(new ToolsActionListener());
		}

		menuBar.add(fileMenu);
		menuBar.add(toolsMenu);
		menuBar.add(linksMenu);

		setMenuBar(menuBar);
	}

}