package com.koios.client.definition;

import com.koios.client.cache.node.MRUNodes;
import com.koios.client.stream.Stream;
import com.koios.client.stream.StreamLoader;
import com.koios.client.world.Model;

public final class SpotAnim {

    public static void unpackConfig(StreamLoader streamLoader) {
	Stream stream = new Stream(streamLoader.getDataForName("spotanim.dat"));
	int length = stream.readUnsignedShort();
	if (cache == null)
	    cache = new SpotAnim[length];
	for (int j = 0; j < length; j++) {
	    if (cache[j] == null)
		cache[j] = new SpotAnim();
	    cache[j].graphicId = j;
	    cache[j].read602Values(stream);
	}

    }

    private void read602Values(Stream stream) {
	anInt406 = stream.readUnsignedShort();
	anInt405 = stream.readUnsignedShort();

	if ((Animation.anims != null) && (anInt406 != 65535) && (anInt406 != -1)) {
	    aAnimation_407 = Animation.anims[anInt406];
	}

	anInt410 = stream.readUnsignedByte();
	anInt411 = stream.readUnsignedByte();
	int j = stream.readUnsignedShort();

	if (j != 65535) {
	    for (int k = 0; k < j; k++) {
		anIntArray409[k] = stream.readUnsignedShort();
	    }

	    for (int k = 0; k < j; k++) {
		anIntArray408[k] = stream.readUnsignedShort();
	    }
	}
    }

    @SuppressWarnings("unused")
	private void read634Values(Stream stream) {
	do {
	    int i = stream.readUnsignedByte();
	    if (i == 0)
		return;
	    if (i == 1)
		anInt405 = stream.readUnsignedShort();
	    else if (i == 2) {
		anInt406 = stream.readUnsignedShort();
		if (Animation.anims != null)
		    aAnimation_407 = Animation.anims[anInt406];
	    } else if (i == 4)
		anInt410 = stream.readUnsignedShort();
	    else if (i == 5)
		anInt411 = stream.readUnsignedShort();
	    else if (i == 6)
		anInt412 = stream.readUnsignedShort();
	    else if (i == 7)
		anInt413 = stream.readUnsignedByte();
	    else if (i == 8)
		anInt414 = stream.readUnsignedByte();
	    else if (i == 40) {
		int j = stream.readUnsignedByte();
		for (int k = 0; k < j; k++) {
		    anIntArray408[k] = stream.readUnsignedShort();
		    anIntArray409[k] = stream.readUnsignedShort();
		}
	    } else
		System.out.println("Error unrecognised spotanim config code: " + i);
	} while (true);
    }

    public void readValues(Stream stream) {
	do {
	    int i = stream.readUnsignedByte();
	    if (i == 0)
		return;
	    if (i == 1)
		anInt405 = stream.readUnsignedShort();
	    else if (i == 2) {
		anInt406 = stream.readUnsignedShort();
		if (Animation.anims != null)
		    aAnimation_407 = Animation.anims[anInt406];
	    } else if (i == 4)
		anInt410 = stream.readUnsignedShort();
	    else if (i == 5)
		anInt411 = stream.readUnsignedShort();
	    else if (i == 6)
		anInt412 = stream.readUnsignedShort();
	    else if (i == 7)
		anInt413 = stream.readUnsignedByte();
	    else if (i == 8)
		anInt414 = stream.readUnsignedByte();
	    else if (i == 40) {
		int j = stream.readUnsignedByte();
		for (int k = 0; k < j; k++) {
		    anIntArray408[k] = stream.readUnsignedShort();
		    anIntArray409[k] = stream.readUnsignedShort();
		}
	    } else
		System.out.println("Error unrecognised spotanim config code: " + i);
	} while (true);
    }

    public Model getModel() {
	Model model = (Model) aMRUNodes_415.insertFromCache(graphicId);
	if (model != null)
	    return model;
	model = Model.method462(anInt405);
	if (model == null)
	    return null;
	for (int i = 0; i < 6; i++)
	    if (anIntArray408[0] != 0)
		model.method476(anIntArray408[i], anIntArray409[i]);

	aMRUNodes_415.removeFromCache(model, graphicId);
	return model;
    }

    private SpotAnim() {
	anInt406 = -1;
	anIntArray408 = new int[6];
	anIntArray409 = new int[6];
	anInt410 = 128;
	anInt411 = 128;
    }

    public static SpotAnim cache[];
    private int graphicId;
    private int anInt405;
    private int anInt406;
    public Animation aAnimation_407;
    private final int[] anIntArray408;
    private final int[] anIntArray409;
    public int anInt410;
    public int anInt411;
    public int anInt412;
    public int anInt413;
    public int anInt414;
    public static MRUNodes aMRUNodes_415 = new MRUNodes(30);

}
