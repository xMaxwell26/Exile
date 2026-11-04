package com.koios.client.utils;

import com.koios.client.stream.Stream;

public final class TextInput {

	private static final Stream buffer = new Stream(new byte[100]);

	private static final char[] CHARACTER_TABLE = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u',
			'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=',
			'\243', '$', '%', '"', '[', ']', '_', '/', '<', '>' };

	private static final char[] chars = new char[100];

	public static String processText(String str)
	{
		buffer.currentOffset = 0;
		method526(str, buffer);
		int i = buffer.currentOffset;
		buffer.currentOffset = 0;
		return method525(i, buffer);
	}

	public static String method525(int len, Stream buffer)
	{
		int length = 0;

		for (int i = 0; i < len; i++)
		{
			int index = buffer.readUnsignedByte();
			chars[length++] = CHARACTER_TABLE[index];
		}

		boolean doUpperCaseNext = true;

		for (int i = 0; i < length; i++)
		{
			char ch = chars[i];

			if (doUpperCaseNext && (ch >= 'a') && (ch <= 'z'))
			{
				chars[i] += '\uFFE0';
				doUpperCaseNext = false;
			}

			if ((ch == '.') || (ch == '!') || (ch == '?'))
			{
				doUpperCaseNext = true;
			}
		}

		return new String(chars, 0, length);
	}

	public static void method526(String string, Stream buffer)
	{
		if (string.length() > 100)
		{
			string = string.substring(0, 100).toLowerCase();
		}
		else
		{
			string = string.toLowerCase();
		}

		for (int i = 0; i < string.length(); i++)
		{
			char ch = string.charAt(i);
			int index = 0;

			for (int n = 0; n < CHARACTER_TABLE.length; n++)
			{
				if (ch != CHARACTER_TABLE[n])
				{
					continue;
				}

				index = n;
				break;
			}

			buffer.writeLEShort(index);// sending to server
		}
	}

}
