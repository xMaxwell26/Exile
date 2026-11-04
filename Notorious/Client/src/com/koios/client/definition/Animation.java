package com.koios.client.definition;

import com.koios.client.stream.Stream;
import com.koios.client.stream.StreamLoader;

public final class Animation {

	public static void unpackConfig(StreamLoader streamLoader) {
		Stream stream = new Stream(streamLoader.getDataForName("seq.dat"));
		int length = stream.readUnsignedShort();
		if (anims == null)
			anims = new Animation[length + 5000];
		for (int j = 0; j < length; j++) {
			if (anims[j] == null) {
				anims[j] = new Animation();
			}
			anims[j].read602Values(stream);
		}
	}

	private void read602Values(Stream stream) {
		do {
			int i = stream.readUnsignedByte();
			if (i == 0)
				break;
			if (i == 1) {
				anInt352 = stream.readUnsignedShort();
				anIntArray353 = new int[anInt352];
				anIntArray354 = new int[anInt352];
				anIntArray355 = new int[anInt352];
				for (int j = 0; j < anInt352; j++) {
					anIntArray353[j] = stream.readInt();
					anIntArray354[j] = -1;
				}

				for (int j = 0; j < anInt352; j++)
					anIntArray355[j] = stream.readUnsignedByte();
			} else if (i == 2)
				anInt356 = stream.readUnsignedShort();
			else if (i == 3) {
				int k = stream.readUnsignedByte();
				anIntArray357 = new int[k + 1];
				for (int l = 0; l < k; l++)
					anIntArray357[l] = stream.readUnsignedByte();
				anIntArray357[k] = 0x98967f;
			} else if (i == 4)
				aBoolean358 = true;
			else if (i == 5)
				anInt359 = stream.readUnsignedByte();
			else if (i == 6)
				anInt360 = stream.readUnsignedShort();
			else if (i == 7)
				anInt361 = stream.readUnsignedShort();
			else if (i == 8)
				anInt362 = stream.readUnsignedByte();
			else if (i == 9)
				anInt363 = stream.readUnsignedByte();
			else if (i == 10)
				anInt364 = stream.readUnsignedByte();
			else if (i == 11)
				anInt365 = stream.readUnsignedByte();
			else if (i == 12)
				stream.readInt();
			else
				System.out.println("Error unrecognised seq config code: " + i);
		} while (true);
		if (anInt352 == 0) {
			anInt352 = 1;
			anIntArray353 = new int[1];
			anIntArray353[0] = -1;
			anIntArray354 = new int[1];
			anIntArray354[0] = -1;
			anIntArray355 = new int[1];
			anIntArray355[0] = -1;
		}
		if (anInt363 == -1)
			if (anIntArray357 != null)
				anInt363 = 2;
			else
				anInt363 = 0;
		if (anInt364 == -1) {
			if (anIntArray357 != null) {
				anInt364 = 2;
				return;
			}
			anInt364 = 0;
		}
		if (anInt360 == 65535)
			anInt360 = 0;
		if (anInt361 == 65535)
			anInt361 = 0;
	}

	public int method258(int i) {
		int j = anIntArray355[i];
		if (j == 0) {
			FrameReader class36 = FrameReader.get(anIntArray353[i]);
			if (class36 != null)
				j = anIntArray355[i] = class36.anInt636;
		}
		if (j == 0)
			j = 1;
		return j;
	}

	public void read634Values(Stream stream) {
		do {
			int i = stream.readUnsignedByte();
			if (i == 0)
				break;
			if (i == 1) {
				anInt352 = stream.readUnsignedShort();
				anIntArray353 = new int[anInt352];
				anIntArray354 = new int[anInt352];
				anIntArray355 = new int[anInt352];
				for (int i_ = 0; i_ < anInt352; i_++) {
					anIntArray353[i_] = stream.readInt();
					anIntArray354[i_] = -1;
				}
				for (int i_ = 0; i_ < anInt352; i_++)
					anIntArray355[i_] = stream.readUnsignedByte();
			} else if (i == 2)
				anInt356 = stream.readUnsignedShort();
			else if (i == 3) {
				int k = stream.readUnsignedByte();
				anIntArray357 = new int[k + 1];
				for (int l = 0; l < k; l++)
					anIntArray357[l] = stream.readUnsignedByte();
				anIntArray357[k] = 0x98967f;
			} else if (i == 4)
				aBoolean358 = true;
			else if (i == 5)
				anInt359 = stream.readUnsignedByte();
			else if (i == 6)
				anInt360 = stream.readUnsignedShort();
			else if (i == 7)
				anInt361 = stream.readUnsignedShort();
			else if (i == 8)
				anInt362 = stream.readUnsignedByte();
			else if (i == 9)
				anInt363 = stream.readUnsignedByte();
			else if (i == 10)
				anInt364 = stream.readUnsignedByte();
			else if (i == 11)
				anInt365 = stream.readUnsignedByte();
			else
				System.out.println("Unrecognized seq.dat config code: " + i);
		} while (true);
		if (anInt352 == 0) {
			anInt352 = 1;
			anIntArray353 = new int[1];
			anIntArray353[0] = -1;
			anIntArray354 = new int[1];
			anIntArray354[0] = -1;
			anIntArray355 = new int[1];
			anIntArray355[0] = -1;
		}
		if (anInt363 == -1)
			if (anIntArray357 != null)
				anInt363 = 2;
			else
				anInt363 = 0;
		if (anInt364 == -1) {
			if (anIntArray357 != null) {
				anInt364 = 2;
				return;
			}
			anInt364 = 0;
		}
	}

	public void readValues(Stream stream) {
		do {
			int i = stream.readUnsignedByte();
			if (i == 0)
				break;
			if (i == 1) {
				anInt352 = stream.readUnsignedShort();
				anIntArray353 = new int[anInt352];
				anIntArray354 = new int[anInt352];
				anIntArray355 = new int[anInt352];
				for (int j = 0; j < anInt352; j++) {
					anIntArray355[j] = stream.readUnsignedShort();
					anIntArray354[j] = -1;
				}
				for (int j = 0; j < anInt352; j++)
					anIntArray353[j] = stream.readUnsignedShort();
				for (int i1 = 0; i1 < anInt352; i1++) {
					anIntArray353[i1] = (stream.readUnsignedShort() << 16) + anIntArray353[i1];
				}
			} else if (i == 2)
				anInt356 = stream.readUnsignedShort();
			else if (i == 3) {
				int k = stream.readUnsignedByte();
				anIntArray357 = new int[k + 1];
				for (int l = 0; l < k; l++)
					anIntArray357[l] = stream.readUnsignedByte();

				anIntArray357[k] = 0x98967f;
			} else if (i == 4)
				aBoolean358 = true;
			else if (i == 5)
				anInt359 = stream.readUnsignedByte();
			else if (i == 6)
				anInt360 = stream.readUnsignedShort();
			else if (i == 7)
				anInt361 = stream.readUnsignedShort();
			else if (i == 8)
				anInt362 = stream.readUnsignedByte();
			else if (i == 9)
				anInt363 = stream.readUnsignedByte();
			else if (i == 10)
				anInt364 = stream.readUnsignedByte();
			else if (i == 11)
				anInt365 = stream.readUnsignedByte();
			else if (i == 12)
				stream.readInt();
			else
				System.out.println("Error unrecognised seq config code: " + i);
		} while (true);
		if (anInt352 == 0) {
			anInt352 = 1;
			anIntArray353 = new int[1];
			anIntArray353[0] = -1;
			anIntArray354 = new int[1];
			anIntArray354[0] = -1;
			anIntArray355 = new int[1];
			anIntArray355[0] = -1;
		}
		if (anInt363 == -1)
			if (anIntArray357 != null)
				anInt363 = 2;
			else
				anInt363 = 0;
		if (anInt364 == -1) {
			if (anIntArray357 != null) {
				anInt364 = 2;
				return;
			}
			anInt364 = 0;
		}
	}

	private Animation() {
		anInt356 = -1;
		aBoolean358 = false;
		anInt359 = 5;
		anInt360 = -1; // Removes shield
		anInt361 = -1; // Removes weapon
		anInt362 = 99;
		anInt363 = -1; // Stops character from moving
		anInt364 = -1;
		anInt365 = 1;
	}

	public static Animation anims[];
	public int anInt352;
	public int anIntArray353[];
	public int anIntArray354[];
	public int[] anIntArray355;
	public int anInt356;
	public int anIntArray357[];
	public boolean aBoolean358;
	public int anInt359;
	public int anInt360;
	public int anInt361;
	public int anInt362;
	public int anInt363;
	public int anInt364;
	public int anInt365;
	public static int anInt367;
}
