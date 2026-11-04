package com.koios.launch.loader.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

public final class FileActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		switch (event.getActionCommand()) {
			case "Exit":
				int status = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?");
				if (status == JOptionPane.YES_OPTION) {
					System.exit(status);
				}
				break;
		}
	}

}