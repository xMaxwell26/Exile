package com.koios.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.logging.Logger;

import com.koios.game.def.AmmoDefinition;
import com.koios.game.def.FletchingData;
import com.koios.game.def.WeaponAmmoDefinition;
import com.koios.game.def.WeaponDefinitions;
import com.koios.game.def.FletchingData.LogData;
import com.koios.game.def.FletchingData.StringData;
import com.thoughtworks.xstream.XStream;

/**
 * Class handling all XStream
 *
 * @author Ultimate1
 */
public class XStreamLibrary {

	private static final XStream xStream;
	private static final Logger logger = Logger.getLogger(XStreamLibrary.class.getName());

	static {
		xStream = new XStream();
		xStream.alias("logData", LogData.class);
		xStream.alias("stringData", StringData.class);
		xStream.alias("weapons", WeaponDefinitions.class);
		xStream.alias("ammoData", WeaponAmmoDefinition.class);
		xStream.alias("projectileData", AmmoDefinition.class);
	}

	public static void init() {
		long startupTime = System.currentTimeMillis();

		try {
			FletchingData.loadXml();
			WeaponDefinitions.loadWeapons();
			WeaponAmmoDefinition.init();
			AmmoDefinition.loadProjectiles();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		logger.info("Loaded all XML files in  " + (System.currentTimeMillis() - startupTime) + "ms.");
	}

	public static Object load(String file) throws FileNotFoundException {
		try (InputStream in = new FileInputStream(file)) {
			return xStream.fromXML(in);
		} catch (FileNotFoundException ex) {
			// re-throw FileNotFoundException
			throw ex;
		} catch (IOException ex) {
			// thrown during automatic #close()
			throw new UncheckedIOException("Unable to load definition file: " + file, ex);
		}
	}

	public static void save(String file, Object data) throws FileNotFoundException {
		try (OutputStream out = new FileOutputStream(file)) {
			xStream.toXML(data, out);
		} catch (FileNotFoundException ex) {
			// re-throw FileNotFoundException
			throw ex;
		} catch (IOException ex) {
			// thrown during automatic #close()
			throw new UncheckedIOException("Unable to save definition file: " + file, ex);
		}
	}

}
