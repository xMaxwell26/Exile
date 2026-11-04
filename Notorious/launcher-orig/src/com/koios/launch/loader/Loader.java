package com.koios.launch.loader;

import java.applet.Applet;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;

import org.apache.log4j.PropertyConfigurator;

import com.koios.launch.loader.applet.LoaderAppletContext;
import com.koios.launch.loader.applet.LoaderAppletStub;
import com.koios.launch.loader.frame.LoaderFrame;
import com.koios.launch.reflect.JarClassLoader;

public final class Loader {

	static {
		try {
			/* The file where our logging properties are located. */
			File properties = new File(".", "log4j.properties");

			/* Configure our logging properties. */
			PropertyConfigurator.configure(properties.getAbsolutePath());

			/* Configure our loaders attributes. */
			Attributes.load(new File(".", "settings.ini"));

			/*
			 * Required system properties for the loader to work properly on MacIntosh systems.
			 */
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", Attributes.getName() + " Client Loader");

			/* Enable anti-aliased text: */
			System.setProperty("awt.useSystemAAFontSettings", "on");
			System.setProperty("swing.aatext", "true");

		} catch (IOException cause) {
			// If something goes wrong during initialization we cannot continue!
			throw new RuntimeException("Something went wrong! Stopping...", cause);
		}
	}

	public static void start() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException, ExecutionException {
		VersionChecker versionChecker = new VersionChecker();
		versionChecker.start();

		File archive = new File(Attributes.getRoot(), Attributes.getArchive());
		JarClassLoader classLoader = new JarClassLoader(archive.toURI().toURL());

		Class<?> appletClass = Class.forName(classLoader.getMainClassName(), true, classLoader);

		final LoaderAppletStub stub = new LoaderAppletStub(new LoaderAppletContext());
		final Applet applet = (Applet) appletClass.newInstance();
		applet.setPreferredSize(new Dimension(765, 503));
		applet.setStub(stub);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				LoaderFrame frame = new LoaderFrame();
				frame.add(applet);
				frame.construct();
				frame.pack();

				applet.init();
				stub.setActive(true);
				if (stub.isActive()) {
					applet.start();
				}
			}
		});

	}

	private Loader() {

	}

}