package com.koios.client.utils;

import com.koios.client.sign.Signlink;

public final class UsernameEncoding
{

	private static final char[] CHARTABLE = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9' };

	public static String decode(long encoded)
	{
		try
		{
			if ((encoded <= 0L) || (encoded >= 0x5b5b57f8a98a5dd1L))
			{
				return "invalid_name";
			}

			if ((encoded % 37L) == 0L)
			{
				return "invalid_name";
			}

			int i = 0;
			char ac[] = new char[12];

			while (encoded != 0L)
			{
				long l1 = encoded;
				encoded /= 37L;
				ac[11 - i++] = CHARTABLE[(int) (l1 - (encoded * 37L))];
			}

			return new String(ac, 12 - i, i);
		}
		catch (RuntimeException ex)
		{
			Signlink.reporterror("81570, " + encoded + ", " + (byte) -99 + ", " + ex.toString());
		}

		throw new RuntimeException();
	}

	public static long encode(String username)
	{
		long l = 0L;

		for (int i = 0; (i < username.length()) && (i < 12); i++)
		{
			char c = username.charAt(i);
			l *= 37L;

			if ((c >= 'A') && (c <= 'Z'))
			{
				l += (1 + c) - 65;
			}
			else if ((c >= 'a') && (c <= 'z'))
			{
				l += (1 + c) - 97;
			}
			else if ((c >= '0') && (c <= '9'))
			{
				l += (27 + c) - 48;
			}
		}

		for (; ((l % 37L) == 0L) && (l != 0L); l /= 37L)
		{
		}

		return l;
	}

	public static String formalize(String username)
	{
		if (username.length() > 0)
		{
			char[] ac = username.toCharArray();

			for (int j = 0; j < ac.length; j++)
			{
				if (ac[j] == '_')
				{
					ac[j] = ' ';

					if (((j + 1) < ac.length) && (ac[j + 1] >= 'a') && (ac[j + 1] <= 'z'))
					{
						ac[j + 1] = (char) ((ac[j + 1] + 65) - 97);
					}
				}
			}

			if ((ac[0] >= 'a') && (ac[0] <= 'z'))
			{
				ac[0] = (char) ((ac[0] + 65) - 97);
			}

			return new String(ac);
		}
		else
		{
			return username;
		}
	}

	public static long method585(String s)
	{
		s = s.toUpperCase();
		long l = 0L;

		for (int i = 0; i < s.length(); i++)
		{
			l = ((l * 61L) + s.charAt(i)) - 32L;
			l = (l + (l >> 56)) & 0xffffffffffffffL;
		}

		return l;
	}

	public static String obfuscateString(String s)
	{
		StringBuffer bldr = new StringBuffer();

		for (int i = 0; i < s.length(); i++)
		{
			bldr.append('*');
		}

		return bldr.toString();
	}

	public static String translateIPv4(int ipv4address)
	{
		int addr1 = (ipv4address >> 24) & 0xFF;
		int addr2 = (ipv4address >> 16) & 0xFF;
		int addr3 = (ipv4address >> 8) & 0xFF;
		int addr4 = ipv4address & 0xFF;
		return String.format("%d.%d.%d.%d", addr1, addr2, addr3, addr4);
	}

}
