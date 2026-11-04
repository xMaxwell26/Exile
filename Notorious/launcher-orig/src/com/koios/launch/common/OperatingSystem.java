package com.koios.launch.common;

import java.util.Locale;

public enum OperatingSystem {

	LINUX, SOLARIS, WINDOWS, MAC, UNKNOWN;

	public static OperatingSystem get() {
		String name = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
		if (name.contains("win")) {
			return OperatingSystem.WINDOWS;
		}
		if (name.contains("mac")) {
			return OperatingSystem.MAC;
		}
		if (name.contains("solaris")) {
			return OperatingSystem.SOLARIS;
		}
		if (name.contains("sunos")) {
			return OperatingSystem.SOLARIS;
		}
		if (name.contains("linux")) {
			return OperatingSystem.LINUX;
		}
		if (name.contains("unix")) {
			return OperatingSystem.LINUX;
		}
		return OperatingSystem.UNKNOWN;
	}

}