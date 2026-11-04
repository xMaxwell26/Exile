package com.koios.client.graphic;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.zip.GZIPInputStream;

import com.koios.client.sign.Signlink;
import com.koios.client.stream.Stream;

public class SpriteLoader {

	public static SpriteLoader[] cache;
	public static Sprite[] sprites = null;

	private static final boolean DUMP_SPRITES = false;

	/**
	 * Creates a sprite out of the spriteData.
	 *
	 * @param sprite
	 */
	private static void createSprite(SpriteLoader sprite) {
		if (DUMP_SPRITES)
			dumpSprite(sprite.id, sprite.spriteData);
		sprites[sprite.id] = new Sprite(sprite.spriteData);
		sprites[sprite.id].drawOffsetX = sprite.drawOffsetX;
		sprites[sprite.id].drawOffsetY = sprite.drawOffsetY;

	}

	private static void dumpSprite(int id, byte[] spriteData) {
		File directory = new File(Signlink.findcachedir() + File.separator + "sprites" + File.separator);
		if (!directory.exists()) {
			directory.mkdir();
		}
		DataOutputStream output = null;
		try {
			System.out.println("Dumping Sprite: " + id);
			output = new DataOutputStream(new FileOutputStream(directory + File.separator + id + ".png"));
			output.write(spriteData);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Loads the sprite data and index files from the cache location. This can
	 * be edited to use an archive such as config or media to load from the
	 * cache.
	 *
	 * @param streamLoader_2
	 */

	public static void loadSprites() {
		int totalSprites = 0;
		try {
			Stream index = new Stream(readFile(Signlink.findcachedir() + "sprites.idx"));
			Stream data = new Stream(readFile(Signlink.findcachedir() + "sprites.dat"));
			DataInputStream indexFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(index.buffer)));
			DataInputStream dataFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data.buffer)));
			totalSprites = indexFile.readInt();
			
			if (cache == null) {
				cache = new SpriteLoader[totalSprites + 1];
				sprites = new Sprite[totalSprites + 1];
			}
			
			for (int i = 0; i < totalSprites; i++) {
				int id = indexFile.readInt() + 1;
				
				if (cache[id] == null) {
					cache[id] = new SpriteLoader();
				}
				
				cache[id].readValues(indexFile, dataFile);
				createSprite(cache[id]);
			}

			indexFile.close();
			dataFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Loaded a total of " + totalSprites + " sprites.");
	}

	private static byte[] readFile(String name) {
		try {
			RandomAccessFile raf = new RandomAccessFile(name, "r");
			java.nio.ByteBuffer buf = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());

			try {
				if (buf.hasArray()) {
					return buf.array();
				} else {
					byte[] array = new byte[buf.remaining()];
					buf.get(array);
					return array;
				}
			} finally {
				raf.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private int id;
	private int drawOffsetX;
	private int drawOffsetY;
	public byte[] spriteData;

	/**
	 * Sets the default values.
	 */
	public SpriteLoader() {
		id = -1;
		drawOffsetX = 0;
		drawOffsetY = 0;
		spriteData = null;
	}

	/**
	 * Reads the information from the index and data files.
	 *
	 * @param index
	 *            holds the sprite indices
	 * @param data
	 *            holds the sprite data per index
	 * @throws IOException
	 */
	private void readValues(DataInputStream index, DataInputStream data) throws IOException {
		do {
			int opcode = data.readByte();

			if (opcode == 0) {
				break;
			}
			if (opcode == 1) {
				id = data.readShort();
			} else if (opcode == 2) {
				data.readUTF();
			} else if (opcode == 3) {
				drawOffsetX = data.readShort();
			} else if (opcode == 4) {
				drawOffsetY = data.readShort();
			} else if (opcode == 5) {
				int indexLength = index.readInt();
				byte[] dataread = new byte[indexLength];
				data.readFully(dataread);
				spriteData = dataread;
			}
		} while (true);
	}

}