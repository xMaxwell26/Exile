package com.koios;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public final class ServiceConfiguration {

	/**
	 * Internal mapping of name to object.
	 */
	private final Map<String, Object> store;

	private final Path path;

	/**
	 *
	 */
	private final Gson gson;

	public ServiceConfiguration(Path path) {
		this.path = Objects.requireNonNull(path, "file cannot be null");
		gson = new GsonBuilder().setPrettyPrinting().create();
		store = load();
	}

	/**
	 * @return
	 */
	private HashMap<String, Object> load() {
		try (FileReader reader = new FileReader(path.toFile())) {
			Map<String, Object> that = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {
			}.getType());
			HashMap<String, Object> newMap = new HashMap<>();
			newMap.putAll(that);
			return newMap;
		} catch (FileNotFoundException ex) {
			return new HashMap<>();
		} catch (IOException ex) {
			throw new InternalError(ex);
		}
	}

	/**
	 * @throws IOException
	 */
	public void save() throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			gson.toJson(store, writer);
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public Object get(String name) {
		return store.get(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public String getString(String name) {
		return String.valueOf(store.get(name));
	}

	/**
	 * @param name
	 * @param value
	 */
	public void setString(String name, String value) {
		store.put(name, String.valueOf(value));
	}

	/**
	 * @param name
	 * @return
	 */
	public double getInteger(String name) {
		Object that = store.get(name);
		if (that == null) {
			return 0;
		}

		if (that.getClass() == Double.class) {
			return (double) that;
		} else if (that.getClass() == Integer.class) {
			return (int) that;
		} else {
			return (double) that;
		}
	}

	/**
	 * @param name
	 * @param amount
	 * @return
	 */
	public int add(String name, int amount) {
		int current = (int) getInteger(name);
		int next = Math.addExact(current, amount);
		setInteger(name, next);
		return next;
	}

	/**
	 * @param name
	 * @param amount
	 * @return
	 */
	public int subtract(String name, int amount) {
		int current = (int) getInteger(name);
		int next = Math.subtractExact(current, amount);
		setInteger(name, next);
		return next;
	}

	/**
	 * @param name
	 * @return
	 */
	public int decrement(String name) {
		int current = (int) getInteger(name);
		int next = Math.decrementExact(current);
		setInteger(name, next);
		return next;
	}

	/**
	 * @param name
	 * @return
	 */
	public int increment(String name) {
		int current = (int) getInteger(name);
		int next = Math.incrementExact(current);
		setInteger(name, next);
		return next;
	}

	/**
	 * @param name
	 * @param value
	 */
	public int setInteger(String name, int value) {
		int current = (int) getInteger(name);
		store.put(name, value);
		return current;
	}

	/**
	 * Gets the directory of the configuration
	 *
	 * @return
	 */
	public String getDirectory() {
		return path.toString() + File.separator;
	}

}
