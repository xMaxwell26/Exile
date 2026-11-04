package com.koios.util;

import java.nio.ByteBuffer;

public final class ByteBufferUtil {

	public static boolean getBoolean(ByteBuffer buf) {
		return buf.get() == 1;
	}

	public static void putBoolean(ByteBuffer buf, boolean bool) {
		buf.put((byte) (bool ? 1 : 0));
	}

	private ByteBufferUtil() {

	}

}
