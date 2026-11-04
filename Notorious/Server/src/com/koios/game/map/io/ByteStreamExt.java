package com.koios.game.map.io;

public final class ByteStreamExt {

	public byte[] buffer;
	public int currentOffset;

	public ByteStreamExt(byte[] buffer) {
		this.buffer = buffer;
		currentOffset = 0;
	}

	public int read3Bytes() {
		currentOffset += 3;
		return (buffer[currentOffset - 3] & 0xFF) << 16 | (buffer[currentOffset - 2] & 0xFF) << 8 | buffer[currentOffset - 1] & 0xFF;
	}

	public byte[] readBytes() {
		int i = currentOffset;

		while (buffer[currentOffset++] != 10) {
		}

		byte[] buf = new byte[currentOffset - i - 1];
		System.arraycopy(buffer, i, buf, i - i, currentOffset - 1 - i);
		return buf;
	}

	public void readBytes(int len, int pos, byte[] buf) {
		for (int i = pos; i < pos + len; i++) {
			buf[i] = buffer[currentOffset++];
		}
	}

	public int readDWord() {
		currentOffset += 4;
		return (buffer[currentOffset - 4] & 0xFF) << 24 | (buffer[currentOffset - 3] & 0xFF) << 16 | (buffer[currentOffset - 2] & 0xFF) << 8 | buffer[currentOffset - 1] & 0xFF;
	}

	public String readNewString() {
		int i = currentOffset;

		while (buffer[currentOffset++] != 0) {
		}

		return new String(buffer, i, currentOffset - i - 1);
	}

	public long readQWord() {
		long ms = readDWord() & 0xFFFFFFFFL;
		long ls = readDWord() & 0xFFFFFFFFL;
		return ms << 32 | ls;
	}

	public int readR3Bytes() {
		currentOffset += 3;
		return (buffer[currentOffset - 1] & 0xFF) << 16 | (buffer[currentOffset - 2] & 0xFF) << 8 | buffer[currentOffset - 3] & 0xFF;
	}

	public byte readSignedByte() {
		return buffer[currentOffset++];
	}

	public int readSignedWord() {
		currentOffset += 2;
		int i = ((buffer[currentOffset - 2] & 0xFF) << 8) + (buffer[currentOffset - 1] & 0xFF);

		if (i > 32767) {
			i -= 0x10000;
		}

		return i;
	}

	public String readString() {
		int i = currentOffset;

		while (buffer[currentOffset++] != 10) {
		}

		return new String(buffer, i, currentOffset - i - 1);
	}

	public int readUnsignedByte() {
		return buffer[currentOffset++] & 0xFF;
	}

	public int readUnsignedWord() {
		currentOffset += 2;
		return (buffer[currentOffset - 2] & 0xFF) << 8 | buffer[currentOffset - 1] & 0xFF;
	}

	public void skip(int length) {
		currentOffset += length;
	}

}
