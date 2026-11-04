package com.koios.util;

import com.google.common.base.Strings;

/**
 * A static utility class for player names
 * 
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class StringUtil {

	private static char[] TRANSLATION_TABLE = buildTranslationTable();
	private static final int MAXIMUM_CHARACTERS = 12;

	public static String hashToUsername(long name) {
		if (!checkValid(name))
			return "invalid_name";

		char[] nameCharacters = new char[MAXIMUM_CHARACTERS];
		long base = TRANSLATION_TABLE.length;
		int i = 0;
		while (name != 0L) {
			nameCharacters[MAXIMUM_CHARACTERS - ++i] = TRANSLATION_TABLE[(int) (name % base)];
			name /= base;
		}
		return new String(nameCharacters, MAXIMUM_CHARACTERS - i, i);
	}

	public static long usernameToHash(String name) {
		long base = TRANSLATION_TABLE.length;
		long output = 0L;
		for (int i = 0; i < name.length(); ++i) {
			char character = name.charAt(i);
			output *= base;
			if (character >= 'A' && character <= 'Z') {
				output += 1 + character - 'A';
			} else if (character >= 'a' && character <= 'z') {
				output += 1 + character - 'a';
			} else if (character >= '0' && character <= '9') {
				output += 27 + character - '0';
			}
		}
		while (output % base == 0L && output != 0L) {
			output /= base;
		}
		return output;
	}

	private static char[] buildTranslationTable() {
		char[] table = new char[37];
		table[0] = '_';
		for (char c = 'a'; c <= 'z'; ++c) {
			table[1 + c - 'a'] = c;
		}
		for (int i = 0; i < 10; ++i) {
			table[i + 27] = (char) (i + '0');
		}
		return table;
	}

	private static final char[] CHAR_TABLE = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[', ']', '_', '/', '<', '>' };

	/**
	 * Checks to ensure the specified username hash is valid.
	 * 
	 * @param hash
	 *            The username hash.
	 * @return {@code true} if and only if the specified name is valid otherwise
	 *         {@code false}.
	 */
	public static boolean checkValid(long hash) {
		if (hash <= 0L || hash >= 0x5b5b57f8a98a5dd1L) {
			return false;
		}

		if (hash % 37L == 0L) {
			return false;
		}

		return true;
	}

	public static String decode(byte[] bytes, int size, boolean format) {
		if (size < 0) {
			return new String();
		}
		final char[] chars = new char[size];
		boolean capitalize = true;
		int index = 0;

		for (int i = 0; i < size; i++) {
			if (index >= chars.length) {
				break;
			}
			final int key = bytes[i] & 0xFF;
			if (key >= CHAR_TABLE.length) {
				break;
			}
			chars[index++] = CHAR_TABLE[key];
		}

		if (format) {
			// Formats the first letter a-z and any a-z after '.', '!' or '?'

			for (int i = 0; i < index; i++) {
				char c = chars[i];

				if (capitalize && c >= 'a' && c <= 'z') {
					chars[i] += '\uFFE0';
					capitalize = false;
				}

				if (c == '.' || c == '!' || c == '?') {
					capitalize = true;
				}
			}
		}

		return new String(chars, 0, index);
	}

	public static byte[] encode(String string) {
		if (string.length() > 100) {
			string = string.substring(0, 100).toLowerCase();
		} else {
			string = string.toLowerCase();
		}

		char[] chars = string.toCharArray();
		byte[] buf = new byte[chars.length];

		for (int i = 0; i < chars.length; i++) {
			char ch = string.charAt(i);
			for (int n = 0; n < CHAR_TABLE.length; n++) {
				if (ch != CHAR_TABLE[n]) {
					continue;
				}

				buf[i] = (byte) n;
				break;
			}
		}

		return buf;
	}

	public static String formatUsername(String username) {
		final char[] chars = username.toLowerCase().toCharArray();
		boolean capitalize = true;

		for (int i = 0; i < chars.length; ++i) {
			if (capitalize) {
				if (chars[i] >= 'a' && chars[i] <= 'z') {
					chars[i] += '\uFFE0';
				}

				if (chars[i] != ' ') {
					// check this here so we don't have to re-set below if
					// double-space
					capitalize = false;
				}
			} else if (chars[i] == ' ') {
				capitalize = true;
			}
		}

		return new String(chars);
	}

	public static String capitalize(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
			}

			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
				}
			}
		}

		return s;
	}

	public static boolean isVowel(char ch) {
		ch = Character.toLowerCase(ch);
		return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
	}

	public static String getArticle(String str) {
		// exception is useless and will likely crash the player, "a null"
		// sounds alright :p
		if (Strings.isNullOrEmpty(str))
			return "a";
		return isVowel(str.charAt(0)) ? "an" : "a";
	}

}