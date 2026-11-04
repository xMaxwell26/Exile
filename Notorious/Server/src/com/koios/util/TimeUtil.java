package com.koios.util;

import java.util.Date;

public final class TimeUtil {

	public static int daysBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}

	public static String toString(long duration) {
		StringBuilder bldr = new StringBuilder(32);
		long seconds = duration / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		seconds %= 60;
		minutes %= 60;
		hours %= 24;

		if (days > 0) {
			bldr.append(days);
			bldr.append(days == 1 ? " day " : " days ");
		}

		if (hours > 0) {
			bldr.append(hours);
			bldr.append(hours == 1 ? " hour " : " hours ");
		}

		if (minutes > 0) {
			bldr.append(minutes);
			bldr.append(minutes == 1 ? " minute " : " minutes ");
		}

		bldr.append(seconds);
		bldr.append(seconds == 1 ? " second" : " seconds");

		return bldr.toString();
	}

	// Suppress constructor to prevent instantiation
	private TimeUtil() {
		throw new InstantiationError();
	}

}
