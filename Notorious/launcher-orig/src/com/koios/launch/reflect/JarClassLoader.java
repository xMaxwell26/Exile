package com.koios.launch.reflect;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;

import org.apache.log4j.Logger;

/**
 * An implementation of a {@link URLClassLoader} as its name implies, URLClassLoader is designed to be used for loading
 * classes and resources that are accessed by searching a set of URLs. The URLs can refer either to directories or to
 * JAR files.
 *
 * In addition to sub-classing URLClassLoader, JarClassLoader also makes use of features in two other new JAR-related
 * APIs, the java.util.jar package and the java.net.JarURLConnection class.
 *
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class JarClassLoader extends URLClassLoader {

	/**
	 * The Logger for this class.
	 */
	private static final Logger logger = Logger.getLogger(JarClassLoader.class);

	/**
	 * The URL used to find the JAR file from which classes are to be loaded.
	 */
	private final URL url;

	/**
	 * Constructs a new {@link JarClassLoader} with the specified URL used to find the JAR file to load classes from,
	 *
	 * @param url The URL used to find the JAR file from which classes are to be loaded.
	 */
	public JarClassLoader(URL url) {
		super(new URL[] { url });
		this.url = url;
	}

	/**
	 * Determines which class in the JAR file is the application's entry point.
	 *
	 * @return The class name of the class within the JAR file that contains the
	 *         {@code public static void main(String[] args)} main method.
	 * @throws IOException If some I/O exception occurs.
	 * @throws MalformedURLException If the URL of the JAR file is malformed.
	 */
	public String getMainClassName() throws IOException, MalformedURLException {
		URL archive = new URL("jar", "", url + "!/");
		JarURLConnection connection = (JarURLConnection) archive.openConnection();
		Attributes attributes = connection.getMainAttributes();
		if (attributes == null) {
			return null;
		}
		return attributes.getValue(Attributes.Name.MAIN_CLASS);
	}

	/**
	 * Invokes the {@code public static void main(String[] args)} main method from the specified class name if and only
	 * if it exists.
	 *
	 * @param name The name of the main class where the main method is located.
	 * @param args The command-line arguments to pass to the main method.
	 * @throws ClassNotFoundException If the specified class name does not exist.
	 * @throws NoSuchMethodException If the main method within the main class does not exist of is malformed.
	 * @throws InvocationTargetException If the main method throws an exception.
	 */
	public void invokeClass(String name, String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		Class<?> clazz = loadClass(name);

		Method main = clazz.getMethod("main", new Class[] { args.getClass() });
		main.setAccessible(true);

		int modifiers = main.getModifiers();
		if (main.getReturnType() != void.class || !Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
			logger.error("Unable to find main method, main must be have the return type void and the static and public modifiers");
			throw new NoSuchMethodError("main(String[])");
		}

		try {
			main.invoke(null, new Object[] { args });
		} catch (IllegalAccessException cause) {
			// Will not happen as we have disabled access checks.
			logger.error("Exception occured while invoking main method", cause);
		}
	}

}