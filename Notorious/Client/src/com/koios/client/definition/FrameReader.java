package com.koios.client.definition;

import com.koios.client.Client;
import com.koios.client.stream.Stream;

public final class FrameReader {

    public static java.util.Hashtable<Integer, FrameReader> frameList = new java.util.Hashtable<Integer, FrameReader>();

    public static void load(byte abyte0[], int fileId) {
	Stream stream = new Stream(abyte0);
	stream.currentOffset = abyte0.length - 12;
	int i = stream.readInt();
	int j = stream.readInt();
	int k = stream.readInt();
	int i1 = 0;
	Stream stream_1 = new Stream(abyte0);
	stream_1.currentOffset = i1;
	i1 += i + 4;
	Stream stream_2 = new Stream(abyte0);
	stream_2.currentOffset = i1;
	i1 += j;
	Stream stream_3 = new Stream(abyte0);
	stream_3.currentOffset = i1;
	i1 += k;
	Stream stream_4 = new Stream(abyte0);
	stream_4.currentOffset = i1;
	SkinList class18 = new SkinList(stream_4);
	int k1 = stream_1.readInt();
	int ai[] = new int[500];
	int ai1[] = new int[500];
	int ai2[] = new int[500];
	int ai3[] = new int[500];
	for (int l1 = 0; l1 < k1; l1++) {
	    int i2 = stream_1.readInt();
	    FrameReader class36 = new FrameReader();
	    frameList.put(new Integer((fileId << 16) + i2), class36);
	    class36.aClass18_637 = class18;
	    int j2 = stream_1.readUnsignedByte();
	    int k2 = -1;
	    int l2 = 0;
	    for (int i3 = 0; i3 < j2; i3++) {
		int j3 = stream_2.readUnsignedByte();
		if (j3 > 0) {
		    if (class18.anIntArray342[i3] != 0) {
			for (int l3 = i3 - 1; l3 > k2; l3--) {
			    if (class18.anIntArray342[l3] != 0)
				continue;
			    ai[l2] = l3;
			    ai1[l2] = 0;
			    ai2[l2] = 0;
			    ai3[l2] = 0;
			    l2++;
			    break;
			}

		    }
		    ai[l2] = i3;
		    char c = '\0';
		    if (class18.anIntArray342[i3] == 3)
			c = '\200';
		    if ((j3 & 1) != 0)
			ai1[l2] = stream_3.method421();
		    else
			ai1[l2] = c;
		    if ((j3 & 2) != 0)
			ai2[l2] = stream_3.method421();
		    else
			ai2[l2] = c;
		    if ((j3 & 4) != 0)
			ai3[l2] = stream_3.method421();
		    else
			ai3[l2] = c;
		    k2 = i3;
		    l2++;
		}
	    }

	    class36.anInt638 = l2;
	    class36.anIntArray639 = new int[l2];
	    class36.anIntArray640 = new int[l2];
	    class36.anIntArray641 = new int[l2];
	    class36.anIntArray642 = new int[l2];
	    for (int k3 = 0; k3 < l2; k3++) {
		class36.anIntArray639[k3] = ai[k3];
		class36.anIntArray640[k3] = ai1[k3];
		class36.anIntArray641[k3] = ai2[k3];
		class36.anIntArray642[k3] = ai3[k3];
	    }

	}

    }

    public static void load634(int file, byte[] fileData) {
	try {
	    Stream buffer = new Stream(fileData);
	    SkinList class18 = new SkinList(buffer);
	    int k1 = buffer.readUnsignedShort();
	    int ai[] = new int[500];
	    int ai1[] = new int[500];
	    int ai2[] = new int[500];
	    int ai3[] = new int[500];

	    for (int l1 = 0; l1 < k1; l1++) {
		int i2 = buffer.readUnsignedShort();
		FrameReader class36 = new FrameReader();
		frameList.put((file << 16) + i2, class36);
		class36.aClass18_637 = class18;
		int j2 = buffer.readUnsignedByte();
		int l2 = 0;
		int k2 = -1;

		for (int i3 = 0; i3 < j2; i3++) {
		    int j3 = buffer.readUnsignedByte();

		    if (j3 > 0) {
			if (class18.anIntArray342[i3] != 0) {
			    for (int l3 = i3 - 1; l3 > k2; l3--) {
				if (class18.anIntArray342[l3] != 0) {
				    continue;
				}

				ai[l2] = l3;
				ai1[l2] = 0;
				ai2[l2] = 0;
				ai3[l2] = 0;
				l2++;
				break;
			    }
			}

			ai[l2] = i3;
			short c = 0;

			if (class18.anIntArray342[i3] == 3) {
			    c = (short) 128;
			}

			if ((j3 & 1) != 0) {
			    ai1[l2] = (short) buffer.readShort2();
			} else {
			    ai1[l2] = c;
			}

			if ((j3 & 2) != 0) {
			    ai2[l2] = buffer.readShort2();
			} else {
			    ai2[l2] = c;
			}

			if ((j3 & 4) != 0) {
			    ai3[l2] = buffer.readShort2();
			} else {
			    ai3[l2] = c;
			}

			k2 = i3;
			l2++;
		    }
		}

		class36.anInt638 = l2;
		class36.anIntArray639 = new int[l2];
		class36.anIntArray640 = new int[l2];
		class36.anIntArray641 = new int[l2];
		class36.anIntArray642 = new int[l2];
		for (int k3 = 0; k3 < l2; k3++) {
		    class36.anIntArray639[k3] = ai[k3];
		    class36.anIntArray640[k3] = ai1[k3];
		    class36.anIntArray641[k3] = ai2[k3];
		    class36.anIntArray642[k3] = ai3[k3];
		}
	    }
	} catch (Exception exception) {
	}
    }

    public static void nullLoader() {
	frameList = null;
    }

    public static FrameReader get(int j) {
	try {
	    int fileId = j >> 16;
	    FrameReader class36 = (FrameReader) frameList.get(j);
	    if (class36 == null) {
		Client.instance.onDemandFetcher.method558(1, fileId);
		return null;
	    } else
		return class36;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static boolean notNulled(int i) {
	return i == -1;
    }

    public FrameReader() {
    }

    public int anInt636;
    public SkinList aClass18_637;
    public int anInt638;
    public int anIntArray639[];
    public int anIntArray640[];
    public int anIntArray641[];
    public int anIntArray642[];

}
