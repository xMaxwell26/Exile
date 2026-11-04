package com.koios.game.map;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.koios.game.map.io.ByteStreamExt;

public final class ObjectDefinition {

	public static ObjectDefinition[] cache;
	public static int cacheIndex;
	public static ObjectDefinition class46;
	public static boolean lowMem;
	private static ByteStreamExt stream;
	public static int[] streamIndices;

	public static byte[] getBuffer(String s) {
		File f = new File("./data/fs/" + s);

		if (!f.exists()) {
			return null;
		}

		byte[] buffer = new byte[(int) f.length()];

		try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
			dis.readFully(buffer);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return buffer;
	}

	public static ObjectDefinition getById(int id) {
		if (id > streamIndices.length) {
			id = streamIndices.length - 1;
		}

		for (int j = 0; j < 20; j++) {
			if (cache[j].type == id) {
				return cache[j];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		class46 = cache[cacheIndex];

		if (id > streamIndices.length - 1 || id < 0) {
			return null;
		}

		stream.currentOffset = streamIndices[id];
		class46.type = id;
		class46.setDefaults();
		class46.readValues(stream);

		switch (class46.type) {
		case 734:
			System.out.println("Set solid to false");
			class46.walkable = false;
			break;

		case 3515:
			class46.xLength = 1;
			class46.yLength = 1;
			break;
		}

		return class46;
	}

	public static void load() {
		stream = new ByteStreamExt(getBuffer("loc.dat"));
		ByteStreamExt stream = new ByteStreamExt(getBuffer("loc.idx"));
		int totalObjects = stream.readUnsignedWord();
		streamIndices = new int[totalObjects];
		int i = 2;

		for (int j = 0; j < totalObjects; j++) {
			streamIndices[j] = i;
			i += stream.readUnsignedWord();
		}

		cache = new ObjectDefinition[20];

		for (int k = 0; k < 20; k++) {
			cache[k] = new ObjectDefinition();
		}

		System.out.println("Finished loading " + totalObjects + " object definitions.");
	}

	public boolean aBoolean736;
	public boolean aBoolean751;
	public boolean aBoolean757;
	public boolean aBoolean762;
	public boolean aBoolean764;
	public boolean aBoolean766;
	public boolean walkable;
	public boolean aBoolean769;
	public boolean aBoolean779;
	public byte aByte737;
	public byte aByte742;
	public String[] actions;
	public int anInt738;
	public int anInt740;
	public int anInt745;
	public int anInt746;
	public int anInt748;
	public int anInt749;
	public int anInt758;
	public int anInt760;
	public int anInt768;
	public int anInt772;
	public int anInt774;
	public int anInt775;
	public int anInt781;
	public int anInt783;
	public int[] anIntArray773;
	public int[] anIntArray776;
	public int[] childrenIDs;
	public byte[] description;
	public boolean hasActions;
	public int[] modifiedModelColors;
	public String name;
	public int[] originalModelColors;
	public int type;
	public int xLength;
	public int yLength;

	private ObjectDefinition() {
		type = -1;
	}

	public boolean containsAction(String string) {
		if (actions == null) {
			return false;
		}
		return Arrays.asList(actions).contains(string);
	}

	public boolean hasActions() {
		return hasActions || actions != null;
	}

	public boolean hasName() {
		return name != null && name.length() > 1;
	}

	private void readValues(ByteStreamExt stream) {
		int i = -1;

		label0: do {
			int j;

			do {
				j = stream.readUnsignedByte();

				if (j == 0) {
					break label0;
				}

				if (j == 1) {
					int k = stream.readUnsignedByte();

					if (k > 0) {
						if (anIntArray773 == null || lowMem) {
							anIntArray776 = new int[k];
							anIntArray773 = new int[k];

							for (int k1 = 0; k1 < k; k1++) {
								anIntArray773[k1] = stream.readUnsignedWord();
								anIntArray776[k1] = stream.readUnsignedByte();
							}
						} else {
							stream.currentOffset += k * 3;
						}
					}
				} else if (j == 2) {
					name = stream.readString();
				} else if (j == 3) {
					description = stream.readBytes();
				} else if (j == 5) {
					int l = stream.readUnsignedByte();

					if (l > 0) {
						if (anIntArray773 == null || lowMem) {
							anIntArray776 = null;
							anIntArray773 = new int[l];

							for (int l1 = 0; l1 < l; l1++) {
								anIntArray773[l1] = stream.readUnsignedWord();
							}
						} else {
							stream.currentOffset += l * 2;
						}
					}
				} else if (j == 14) {
					xLength = stream.readUnsignedByte();
				} else if (j == 15) {
					yLength = stream.readUnsignedByte();
				} else if (j == 17) {
					walkable = false;
				} else if (j == 18) {
					aBoolean757 = false;
				} else if (j == 19) {
					i = stream.readUnsignedByte();

					if (i == 1) {
						hasActions = true;
					}
				} else if (j == 21) {
					aBoolean762 = true;
				} else if (j == 22) {
					aBoolean769 = false;
				} else if (j == 23) {
					aBoolean764 = true;
				} else if (j == 24) {
					anInt781 = stream.readUnsignedWord();

					if (anInt781 == 65535) {
						anInt781 = -1;
					}
				} else if (j == 28) {
					anInt775 = stream.readUnsignedByte();
				} else if (j == 29) {
					aByte737 = stream.readSignedByte();
				} else if (j == 39) {
					aByte742 = stream.readSignedByte();
				} else if (j >= 30 && j < 39) {
					if (actions == null) {
						actions = new String[10];
					}

					actions[j - 30] = stream.readString();

					if (actions[j - 30].equalsIgnoreCase("hidden")) {
						actions[j - 30] = null;
					}
				} else if (j == 40) {
					int i1 = stream.readUnsignedByte();
					modifiedModelColors = new int[i1];
					originalModelColors = new int[i1];

					for (int i2 = 0; i2 < i1; i2++) {
						modifiedModelColors[i2] = stream.readUnsignedWord();
						originalModelColors[i2] = stream.readUnsignedWord();
					}
				} else if (j == 60) {
					anInt746 = stream.readUnsignedWord();
				} else if (j == 62) {
					aBoolean751 = true;
				} else if (j == 64) {
					aBoolean779 = false;
				} else if (j == 65) {
					anInt748 = stream.readUnsignedWord();
				} else if (j == 66) {
					anInt772 = stream.readUnsignedWord();
				} else if (j == 67) {
					anInt740 = stream.readUnsignedWord();
				} else if (j == 68) {
					anInt758 = stream.readUnsignedWord();
				} else if (j == 69) {
					anInt768 = stream.readUnsignedByte();
				} else if (j == 70) {
					anInt738 = stream.readSignedWord();
				} else if (j == 71) {
					anInt745 = stream.readSignedWord();
				} else if (j == 72) {
					anInt783 = stream.readSignedWord();
				} else if (j == 73) {
					aBoolean736 = true;
				} else if (j == 74) {
					aBoolean766 = true;
				} else if (j == 80) {
					int len = stream.readUnsignedByte();
					short[] modifiedTextureFace = new short[len];
					short[] originalTextureFace = new short[len];

					for (int i2 = 0; i2 < len; i2++) {
						modifiedTextureFace[i2] = (short) stream.readUnsignedWord();
						originalTextureFace[i2] = (short) stream.readUnsignedWord();
					}
				} else {
					if (j != 75) {
						continue;
					}

					anInt760 = stream.readUnsignedByte();
				}
				continue label0;
			} while (j != 77);

			anInt774 = stream.readUnsignedWord();

			if (anInt774 == 65535) {
				anInt774 = -1;
			}

			anInt749 = stream.readUnsignedWord();

			if (anInt749 == 65535) {
				anInt749 = -1;
			}

			int j1 = stream.readUnsignedByte();
			childrenIDs = new int[j1 + 1];

			for (int j2 = 0; j2 <= j1; j2++) {
				childrenIDs[j2] = stream.readUnsignedWord();

				if (childrenIDs[j2] == 65535) {
					childrenIDs[j2] = -1;
				}
			}
		} while (true);// 6774

		if (i == -1) {
			hasActions = anIntArray773 != null && (anIntArray776 == null || anIntArray776[0] == 10);

			if (actions != null) {
				hasActions = true;
			}
		}

		if (aBoolean766) {
			walkable = false;
			aBoolean757 = false;
		}

		if (anInt760 == -1) {
			anInt760 = walkable ? 1 : 0;
		}
	}

	private void setDefaults() {
		anIntArray773 = null;
		anIntArray776 = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		xLength = 1;
		yLength = 1;
		walkable = true;
		aBoolean757 = true;
		hasActions = false;
		aBoolean762 = false;
		aBoolean769 = false;
		aBoolean764 = false;
		anInt781 = -1;
		anInt775 = 16;
		aByte737 = 0;
		aByte742 = 0;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		aBoolean751 = false;
		aBoolean779 = true;
		anInt748 = 128;
		anInt772 = 128;
		anInt740 = 128;
		anInt768 = 0;
		anInt738 = 0;
		anInt745 = 0;
		anInt783 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		anInt774 = -1;
		anInt749 = -1;
		childrenIDs = null;
	}

	public boolean solid() {
		return aBoolean779;
	}

	public int xLength() {
		return xLength;
	}

	public int yLength() {
		return yLength;
	}

}
