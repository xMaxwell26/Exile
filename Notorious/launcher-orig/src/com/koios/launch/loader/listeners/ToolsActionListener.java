package com.koios.launch.loader.listeners;

import java.awt.AWTException;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.koios.launch.common.DesktopUtil;
import com.koios.launch.loader.Attributes;

public final class ToolsActionListener implements ActionListener {

	private static final Logger logger = Logger.getLogger(ToolsActionListener.class);
	private static final Random random = new SecureRandom();
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	@Override
	public void actionPerformed(ActionEvent event) {
		switch (event.getActionCommand()) {
			case "Browse screenshots":
				Path directory = Paths.get(Attributes.getScreenshotFolder());

				try {
					if (!Files.exists(directory)) {
						Files.createDirectories(directory);
					}
					DesktopUtil.openFile(directory.toFile());
				} catch (Exception cause) {
					logger.error("Error occured while creating directories", cause);
				}

				break;
			case "Take screenshot":
				Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
				Point point = window.getLocationOnScreen();

				int x = (int) point.getX();
				int y = (int) point.getY();
				int w = window.getWidth();
				int h = window.getHeight();

				String currentDate = dateFormat.format(new Date());
				String fileName = currentDate + random.nextInt();

				Path file = Paths.get(Attributes.getScreenshotFolder(), fileName + ".png");

				try {
					if (!Files.exists(file)) {
						Files.createDirectories(file);
					}

					Robot robot = new Robot(window.getGraphicsConfiguration().getDevice());
					Rectangle captureSize = new Rectangle(x, y, w, h);
					BufferedImage bufferedimage = robot.createScreenCapture(captureSize);
					ImageIO.write(bufferedimage, "png", file.toFile());
				} catch (AWTException | IOException cause) {
					logger.error("Error occured while taking screenshot", cause);
				}
				break;
		}
	}

}