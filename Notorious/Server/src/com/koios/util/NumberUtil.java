package com.koios.util;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public final class NumberUtil {

	private static final NumberFormat FORMAT = NumberFormat.getInstance(Locale.US);

	public static String format(long num) {
		return FORMAT.format(num);
	}

	public static String format(double num) {
		return FORMAT.format(num);
	}

	public static int inclusiveRandom(int range) {
		return (int) (ThreadLocalRandom.current().nextDouble() * (range + 1));
	}

	public static boolean nextBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}

	public static int random(int range) {
		return (int) (Math.random() * range);
	}

	public static int random(int[] array) {
		return array[random(array.length)];
	}

	public static int random(int minInclusive, int maxInclusive) {
		return random(maxInclusive - minInclusive + 1) + minInclusive;
	}

	private NumberUtil() {

	}

}