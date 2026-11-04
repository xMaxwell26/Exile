package com.koios.client.definition;

import com.koios.client.Client;
import com.koios.client.cache.node.MRUNodes;
import com.koios.client.stream.Stream;
import com.koios.client.stream.StreamLoader;
import com.koios.client.world.Model;

public final class NPCDefinition {

	public static NPCDefinition forID(int i) {
		for (int j = 0; j < 20; j++)
			if (cache[j].interfaceType == (long) i)
				return cache[j];

		anInt56 = (anInt56 + 1) % 20;
		NPCDefinition definition = cache[anInt56] = new NPCDefinition();
		stream.currentOffset = streamIndices[com.koios.client.arpg.CustomNpcs.template(i)];
		definition.interfaceType = i;
		definition.read602Values(stream);
		
		switch (i) {
			case 5996:
				definition.combatLevel = 622;
				definition.adjustVertextPointsXOrY = 100;
				definition.adjustVertextPointZ = 100;
				definition.npcSizeInSquares = 3;
				break;
			case 3390:
				definition.name = "Prince Black Dragon";
				definition.adjustVertextPointZ = 30;
				definition.adjustVertextPointsXOrY = 30;
				definition.npcSizeInSquares = 1;
				definition.actions = new String[5];
				definition.actions[0] = "Talk-to";
				definition.actions[2] = "Pick-up";
				definition.npcModels = new int[4];
				definition.npcModels[0] = 17414;
				definition.npcModels[1] = 17415;
				definition.npcModels[2] = 17429;
				definition.npcModels[3] = 17422;
				definition.standAnimation = 90;
				definition.walkAnimation = 4635;
				definition.combatLevel = 0;
				definition.description = "A miniature King Black Dragon!".getBytes();
				definition.varBitChild = -1;
				break;
			case 3391:// stop before 5902
				definition.varBitChild = -1;
				definition.name = "Chaos Elemental Jr.";
				definition.adjustVertextPointZ = 30;
				definition.adjustVertextPointsXOrY = 30;
				definition.npcSizeInSquares = 1;
				definition.actions = new String[5];
				definition.actions[0] = "Talk-to";
				definition.actions[2] = "Pick-up";
				definition.npcModels = new int[1];
				definition.npcModels[0] = 11216;
				definition.standAnimation = 3144;
				definition.walkAnimation = 3145;
				definition.combatLevel = 0;
				definition.description = "A miniature Chaos Elemental!".getBytes();
				break;
			case 3392:// stop before 5902
				definition.varBitChild = -1;
				definition.name = "Baby Mole";
				definition.adjustVertextPointZ = 30;
				definition.adjustVertextPointsXOrY = 30;
				definition.npcSizeInSquares = 1;
				definition.actions = new String[5];
				definition.actions[0] = "Talk-to";
				definition.actions[2] = "Pick-up";
				definition.npcModels = new int[4];
				definition.npcModels[0] = 12076;
				definition.npcModels[1] = 12075;
				definition.npcModels[2] = 12074;
				definition.npcModels[3] = 12077;
				definition.standAnimation = 3309;
				definition.walkAnimation = 3313;
				definition.combatLevel = 0;
				definition.description = "A miniature Giant Mole!".getBytes();
				break;
			case 3393:// stop before 5902
				definition.varBitChild = -1;
				definition.name = "Baby Dagannoth Supreme";
				definition.adjustVertextPointZ = 40;
				definition.adjustVertextPointsXOrY = 40;
				definition.npcSizeInSquares = 1;
				definition.actions = new String[5];
				definition.actions[0] = "Talk-to";
				definition.actions[2] = "Pick-up";
				definition.npcModels = new int[2];
				definition.npcModels[0] = 9941;
				definition.npcModels[1] = 9943;
				definition.standAnimation = 2850;
				definition.walkAnimation = 2849;
				definition.combatLevel = 0;
				definition.description = "A miniature Dagannoth Supreme!".getBytes();
				break;
			case 3394:// stop before 5902
				definition.varBitChild = -1;
				definition.name = "Dagannoth Prime Jr.";
				definition.adjustVertextPointZ = 40;
				definition.adjustVertextPointsXOrY = 40;
				definition.npcSizeInSquares = 1;
				definition.actions = new String[5];
				definition.actions[0] = "Talk-to";
				definition.actions[2] = "Pick-up";
				definition.npcModels = new int[3];
				definition.npcModels[0] = 9940;
				definition.npcModels[1] = 9943;
				definition.npcModels[2] = 9942;
				definition.standAnimation = 2850;
				definition.walkAnimation = 2849;
				definition.combatLevel = 0;
				definition.description = "A miniature Dagannoth Prime!".getBytes();
				break;
			case 3395:// stop before 5902
				definition.varBitChild = -1;
				definition.name = "Baby Dagannoth Rex";
				definition.adjustVertextPointZ = 40;
				definition.adjustVertextPointsXOrY = 40;
				definition.npcSizeInSquares = 1;
				definition.actions = new String[5];
				definition.actions[0] = "Talk-to";
				definition.actions[2] = "Pick-up";
				definition.npcModels = new int[1];
				definition.npcModels[0] = 9941;
				definition.standAnimation = 2850;
				definition.walkAnimation = 2849;
				definition.combatLevel = 0;
				definition.description = "A miniature Dagannoth Rex!".getBytes();
				break;
			case 3396:
				definition.varBitChild = -1;
				definition.actions = new String[5];
				definition.actions[0] = "Talk-to";
				definition.actions[2] = "Pick-up";
				definition.npcModels = new int[2];
				definition.npcModels[0] = 28003;
				definition.npcModels[1] = 28004;
				definition.adjustVertextPointZ = 25;
				definition.adjustVertextPointsXOrY = 25;
				definition.standAnimation = 6972;
				definition.walkAnimation = 6973;
				definition.name = "Kree'arra Jr.";
				definition.combatLevel = 0;
				definition.description = "A mini Kree'arra!".getBytes();
				definition.npcSizeInSquares = 1;
				break;
			case 3397:// stop before 5902
				definition.varBitChild = -1;
				definition.name = "General Graardor Jr.";
				definition.adjustVertextPointZ = 30;
				definition.adjustVertextPointsXOrY = 30;
				definition.npcSizeInSquares = 1;
				definition.actions = new String[5];
				definition.actions[0] = "Talk-to";
				definition.actions[2] = "Pick-up";
				definition.npcModels = new int[2];
				definition.npcModels[0] = 27785;
				definition.npcModels[1] = 27789;
				definition.standAnimation = 7059;
				definition.walkAnimation = 7058;
				definition.combatLevel = 0;
				definition.description = "A miniature General Graardor!".getBytes();
				break;
			case 3398:// stop before 5902
				definition.varBitChild = -1;
				definition.name = "Penance Pet";
				definition.adjustVertextPointZ = 30;
				definition.adjustVertextPointsXOrY = 30;
				definition.npcSizeInSquares = 1;
				definition.actions = new String[5];
				definition.actions[0] = "Talk-to";
				definition.actions[2] = "Pick-up";
				definition.npcModels = new int[8];
				definition.npcModels[0] = 20717;
				definition.npcModels[1] = 20715;
				definition.npcModels[2] = 20714;
				definition.npcModels[3] = 20709;
				definition.npcModels[4] = 20713;
				definition.npcModels[5] = 20712;
				definition.npcModels[6] = 20711;
				definition.npcModels[7] = 20710;
				definition.standAnimation = 5410;
				definition.walkAnimation = 5409;
				definition.combatLevel = 0;
				definition.description = "A miniature Penance Queen!".getBytes();
				break;
			case 3401:// stop before 5902
				definition.varBitChild = -1;
				definition.name = "Zilyana Jr.";
				definition.adjustVertextPointZ = 40;
				definition.adjustVertextPointsXOrY = 40;
				definition.npcSizeInSquares = 1;
				definition.actions = new String[5];
				definition.actions[0] = "Talk-to";
				definition.actions[2] = "Pick-up";
				definition.npcModels = new int[4];
				definition.npcModels[0] = 28057;
				definition.npcModels[1] = 28071;
				definition.npcModels[2] = 28078;
				definition.npcModels[3] = 28056;
				definition.standAnimation = 6963;
				definition.walkAnimation = 6962;
				definition.combatLevel = 0;
				definition.description = "A miniature Commander Zilyana!".getBytes();
				break;
			case 6750:
				definition.name = "Pet Insurance Agent";
				definition.actions = new String[5];
				definition.actions[0] = "Talk-to";
				break;
			case 1374:
				definition.name = "Ganodermic Runt";
				definition.npcModels = new int[] { 13895 };
				definition.standAnimation = 15475;
				definition.walkAnimation = 15476;
				definition.actions = new String[5];
				definition.actions[1] = "Attack";
				definition.combatLevel = 140;
				break;
			case 1381:
				definition.name = "Ganodermic Beast";
				definition.description = "Your adventure end here.".getBytes();
				definition.npcModels = new int[] { 13888 };
				definition.npcSizeInSquares = 3;
				definition.standAnimation = 15464;
				definition.walkAnimation = 15465;
				definition.actions = new String[5];
				definition.actions[1] = "Attack";
				definition.combatLevel = 280;
				break;
		}
		com.koios.client.arpg.CustomNpcs.configure(definition,i);
		return definition;
	}

	public Model method160() {
		if (childrenIDs != null) {
			NPCDefinition entityDef = method161();
			if (entityDef == null)
				return null;
			else
				return entityDef.method160();
		}
		if (anIntArray73 == null)
			return null;
		boolean flag1 = false;
		for (int i = 0; i < anIntArray73.length; i++)
			if (!Model.method463(anIntArray73[i]))
				flag1 = true;

		if (flag1)
			return null;
		Model aclass30_sub2_sub4_sub6s[] = new Model[anIntArray73.length];
		for (int j = 0; j < anIntArray73.length; j++)
			aclass30_sub2_sub4_sub6s[j] = Model.method462(anIntArray73[j]);

		Model model;
		if (aclass30_sub2_sub4_sub6s.length == 1)
			model = aclass30_sub2_sub4_sub6s[0];
		else
			model = new Model(aclass30_sub2_sub4_sub6s.length,
					aclass30_sub2_sub4_sub6s);
		if (anIntArray76 != null) {
			for (int k = 0; k < anIntArray76.length; k++)
				model.method476(anIntArray76[k], anIntArray70[k]);

		}
		return model;
	}

	public NPCDefinition method161() {
		int j = -1;
		if (varBitChild != -1 && varBitChild <= 2113) {
			VarBit varBit = VarBit.cache[varBitChild];
			int k = varBit.anInt648;
			int l = varBit.anInt649;
			int i1 = varBit.anInt650;
			int j1 = Client.anIntArray1232[i1 - l];
			j = clientInstance.variousSettings[k] >> l & j1;
		} else if (configChild != -1)
			j = clientInstance.variousSettings[configChild];
		if (j < 0 || j >= childrenIDs.length || childrenIDs[j] == -1)
			return null;
		else
			return forID(childrenIDs[j]);
	}

	public static void unpackConfig(StreamLoader streamLoader) {
		stream = new Stream(streamLoader.getDataForName("npc.dat"));
		Stream stream2 = new Stream(streamLoader.getDataForName("npc.idx"));
		int totalNPCs = stream2.readUnsignedShort();
		streamIndices = new int[totalNPCs + 5000];
		int i = 2;
		for (int j = 0; j < totalNPCs; j++) {
			streamIndices[j] = i;
			i += stream2.readUnsignedShort();
		}

		cache = new NPCDefinition[20];
		for (int k = 0; k < 20; k++)
			cache[k] = new NPCDefinition();
		for (int index = 0; index < totalNPCs; index++) {
			NPCDefinition ed = forID(index);
			if (ed == null)
				continue;
			if (ed.name == null)
				continue;
		}
	}

	public static void nullLoader() {
		mruNodes = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}
	
	public Model method164(int j, int frame, int ai[], int nextFrame, int idk, int idk2) {
		if (childrenIDs != null) {
			NPCDefinition entityDef = method161();
			if (entityDef == null)
				return null;
			else
				return entityDef.method164(j, frame, ai, nextFrame, idk, idk2);
		}
		Model model = (Model) mruNodes.insertFromCache(interfaceType);
		if (model == null) {
			boolean flag = false;
			for (int i1 = 0; i1 < npcModels.length; i1++)
				if (!Model.method463(npcModels[i1]))
					flag = true;

			if (flag)
				return null;
			Model aclass30_sub2_sub4_sub6s[] = new Model[npcModels.length];
			for (int j1 = 0; j1 < npcModels.length; j1++)
				aclass30_sub2_sub4_sub6s[j1] = Model
				.method462(npcModels[j1]);

			if (aclass30_sub2_sub4_sub6s.length == 1)
				model = aclass30_sub2_sub4_sub6s[0];
			else
				model = new Model(aclass30_sub2_sub4_sub6s.length,
						aclass30_sub2_sub4_sub6s);
			if (anIntArray76 != null) {
				for (int k1 = 0; k1 < anIntArray76.length; k1++)
					model.method476(anIntArray76[k1], anIntArray70[k1]);

			}
			model=com.koios.client.arpg.CustomNpcModels.create(model,(int)interfaceType);
            if(!com.koios.client.arpg.CustomNpcModels.owns((int)interfaceType))com.koios.client.arpg.CustomNpcs.style(model,(int)interfaceType);
			model.method469();
			model.method478(132, 132, 132);
			model.method479(84 + anInt85, 1000 + anInt92, -90, -580, -90, true);
			mruNodes.removeFromCache(model, interfaceType);
		}
		Model model_1 = Model.aModel_1621;
		model_1.method464(model, FrameReader.notNulled(frame) & FrameReader.notNulled(j) & FrameReader.notNulled(nextFrame));
		if (frame != -1 && j != -1)
			model_1.method471(ai, j, frame);
		else if (frame != -1 && nextFrame != -1)
			model_1.method470(frame);
		else if (frame != -1)
			model_1.method470(frame);
		if (adjustVertextPointsXOrY != 128 || adjustVertextPointZ != 128)
			model_1.method478(adjustVertextPointsXOrY, adjustVertextPointsXOrY, adjustVertextPointZ);
		model_1.method466();
		model_1.anIntArrayArray1658 = null;
		model_1.anIntArrayArray1657 = null;
		if (npcSizeInSquares == 1)
			model_1.aBoolean1659 = true;
		return model_1;
	}

	public Model method164(int j, int k, int ai[]) {
		if (childrenIDs != null) {
			NPCDefinition entityDef = method161();
			if (entityDef == null)
				return null;
			else
				return entityDef.method164(j, k, ai);
		}
		Model model = (Model) mruNodes.insertFromCache(interfaceType);
		if (model == null) {
			boolean flag = false;
			for (int i1 = 0; i1 < npcModels.length; i1++)
				if (!Model.method463(npcModels[i1]))
					flag = true;

			if (flag)
				return null;
			Model aclass30_sub2_sub4_sub6s[] = new Model[npcModels.length];
			for (int j1 = 0; j1 < npcModels.length; j1++)
				aclass30_sub2_sub4_sub6s[j1] = Model
				.method462(npcModels[j1]);

			if (aclass30_sub2_sub4_sub6s.length == 1)
				model = aclass30_sub2_sub4_sub6s[0];
			else
				model = new Model(aclass30_sub2_sub4_sub6s.length,
						aclass30_sub2_sub4_sub6s);
			if (anIntArray76 != null) {
				for (int k1 = 0; k1 < anIntArray76.length; k1++)
					model.method476(anIntArray76[k1], anIntArray70[k1]);

			}
			model=com.koios.client.arpg.CustomNpcModels.create(model,(int)interfaceType);
            if(!com.koios.client.arpg.CustomNpcModels.owns((int)interfaceType))com.koios.client.arpg.CustomNpcs.style(model,(int)interfaceType);
			model.method469();
			model.method479(64 + anInt85, 850 + anInt92, -30, -50, -30, true);
			mruNodes.removeFromCache(model, interfaceType);
		}
		Model model_1 = Model.aModel_1621;
		model_1.method464(model, FrameReader.notNulled(k) & FrameReader.notNulled(j));
		if (k != -1 && j != -1)
			model_1.method471(ai, j, k);
		else if (k != -1)
			model_1.method470(k);
		if (adjustVertextPointsXOrY != 128 || adjustVertextPointZ != 128)
			model_1.method478(adjustVertextPointsXOrY, adjustVertextPointsXOrY, adjustVertextPointZ);
		model_1.method466();
		model_1.anIntArrayArray1658 = null;
		model_1.anIntArrayArray1657 = null;
		if (npcSizeInSquares == 1)
			model_1.aBoolean1659 = true;
		return model_1;
	}
	
	private void read602Values(Stream stream)
	{
		do
		{
			int i = stream.readUnsignedByte();
			if(i == 0)
				return;
			if(i == 1)
			{
				int j = stream.readUnsignedByte();
				npcModels = new int[j];
				for(int j1 = 0; j1 < j; j1++)
					npcModels[j1] = stream.readUnsignedShort();

			} else
			if(i == 2)
				name = stream.readString();
			else
			if(i == 3)
				description = stream.readBytes();
			else
			if(i == 12)
				npcSizeInSquares = stream.readSignedByte();
			else
			if(i == 13)
				standAnimation = stream.readUnsignedShort();
			else
			if(i == 14)
				walkAnimation = stream.readUnsignedShort();
			else
			if(i == 17)
			{
				walkAnimation = stream.readUnsignedShort();
				walkingBackwardsAnimation = stream.readUnsignedShort();
				walkLeftAnimation = stream.readUnsignedShort();
				walkRightAnimation = stream.readUnsignedShort();
			} else
			if(i >= 30 && i < 40)
			{
				if(actions == null)
					actions = new String[5];
				actions[i - 30] = stream.readString();
				if(actions[i - 30].equalsIgnoreCase("hidden"))
					actions[i - 30] = null;
			} else
			if(i == 40)
			{
				int k = stream.readUnsignedByte();
				anIntArray76 = new int[k];
				anIntArray70 = new int[k];
				for(int k1 = 0; k1 < k; k1++)
				{
					anIntArray76[k1] = stream.readUnsignedShort();
					anIntArray70[k1] = stream.readUnsignedShort();
				}

			} else
			if(i == 60)
			{
				int l = stream.readUnsignedByte();
				anIntArray73 = new int[l];
				for(int l1 = 0; l1 < l; l1++)
					anIntArray73[l1] = stream.readUnsignedShort();

			} else
			if(i == 90)
				stream.readUnsignedShort();
			else
			if(i == 91)
				stream.readUnsignedShort();
			else
			if(i == 92)
				stream.readUnsignedShort();
			else
			if(i == 93)
				aBoolean87 = false;
			else
			if(i == 95)
				combatLevel = stream.readUnsignedShort();
			else
			if(i == 97)
				adjustVertextPointsXOrY = stream.readUnsignedShort();
			else
			if(i == 98)
				adjustVertextPointZ = stream.readUnsignedShort();
			else
			if(i == 99)
				aBoolean93 = true;
			else
			if(i == 100)
				anInt85 = stream.readSignedByte();
			else
			if(i == 101)
				anInt92 = stream.readSignedByte() * 5;
			else
			if(i == 102)
				anInt75 = stream.readUnsignedShort();
			else
			if(i == 103)
				anInt79 = stream.readUnsignedShort();
			else
			if(i == 106)
			{
				varBitChild = stream.readUnsignedShort();
				if(varBitChild == 65535)
					varBitChild = -1;
				configChild = stream.readUnsignedShort();
				if(configChild == 65535)
					configChild = -1;
				int length = stream.readUnsignedByte();
				childrenIDs = new int[length + 1];
				for(int i2 = 0; i2 <= length; i2++)
				{
					childrenIDs[i2] = stream.readUnsignedShort();
					if(childrenIDs[i2] == 65535)
						childrenIDs[i2] = -1;
				}

			} else
			if(i == 107)
				aBoolean84 = false;
		} while(true);
	}
	
	@SuppressWarnings("unused")
	private void read634Values(Stream stream)
	{
		do
		{
			int i = stream.readUnsignedByte();
			if(i == 0)
				return;
			if(i == 1)
			{
				int j = stream.readUnsignedByte();
				npcModels = new int[j];
				for(int j1 = 0; j1 < j; j1++)
					npcModels[j1] = stream.readUnsignedShort();

			} else
			if(i == 2)
				name = stream.readNewString();
			else
			if(i == 3)
				description = stream.readNewString().getBytes();
			else
			if(i == 12)
				npcSizeInSquares = stream.readSignedByte();
			else
			if(i == 13)
				standAnimation = stream.readUnsignedShort();
			else
			if(i == 14)
				walkAnimation = stream.readUnsignedShort();
			else
			if(i == 17)
			{
				walkAnimation = stream.readUnsignedShort();
				walkingBackwardsAnimation = stream.readUnsignedShort();
				walkLeftAnimation = stream.readUnsignedShort();
				walkRightAnimation = stream.readUnsignedShort();
			} else
			if(i >= 30 && i < 40)
			{
				if(actions == null)
					actions = new String[5];
				actions[i - 30] = stream.readNewString();
				if(actions[i - 30].equalsIgnoreCase("hidden"))
					actions[i - 30] = null;
			} else
			if(i == 40)
			{
				int k = stream.readUnsignedByte();
				anIntArray76 = new int[k];
				anIntArray70 = new int[k];
				for(int k1 = 0; k1 < k; k1++)
				{
					anIntArray76[k1] = stream.readUnsignedShort();
					anIntArray70[k1] = stream.readUnsignedShort();
				}

			} else
			if(i == 60)
			{
				int l = stream.readUnsignedByte();
				anIntArray73 = new int[l];
				for(int l1 = 0; l1 < l; l1++)
					anIntArray73[l1] = stream.readUnsignedShort();

			} else
			if(i == 90)
				stream.readUnsignedShort();
			else
			if(i == 91)
				stream.readUnsignedShort();
			else
			if(i == 92)
				stream.readUnsignedShort();
			else
			if(i == 93)
				aBoolean87 = false;
			else
			if(i == 95)
				combatLevel = stream.readUnsignedShort();
			else
			if(i == 97)
				adjustVertextPointsXOrY = stream.readUnsignedShort();
			else
			if(i == 98)
				adjustVertextPointZ = stream.readUnsignedShort();
			else
			if(i == 99)
				aBoolean93 = true;
			else
			if(i == 100)
				anInt85 = stream.readSignedByte();
			else
			if(i == 101)
				anInt92 = stream.readSignedByte() * 5;
			else
			if(i == 102)
				anInt75 = stream.readUnsignedShort();
			else
			if(i == 103)
				anInt79 = stream.readUnsignedShort();
			else
			if(i == 106)
			{
				varBitChild = stream.readUnsignedShort();
				if(varBitChild == 65535)
					varBitChild = -1;
				configChild = stream.readUnsignedShort();
				if(configChild == 65535)
					configChild = -1;
				int i1 = stream.readUnsignedByte();
				childrenIDs = new int[i1 + 1];
				for(int i2 = 0; i2 <= i1; i2++)
				{
					childrenIDs[i2] = stream.readUnsignedShort();
					if(childrenIDs[i2] == 65535)
						childrenIDs[i2] = -1;
				}

			} else
			if(i == 107)
				aBoolean84 = false;
		} while(true);
	}

	public void readValues(Stream stream) {
		do {
			int i = stream.readUnsignedByte();
			if (i == 0)
				return;
			if (i == 1) {
				int j = stream.readUnsignedByte();
				npcModels = new int[j];
				for (int j1 = 0; j1 < j; j1++)
					npcModels[j1] = stream.readUnsignedShort();

			} else if (i == 2)
				name = stream.readNewString();
			else if (i == 3)
				description = stream.readBytes();
			else if (i == 12)
				npcSizeInSquares = stream.readSignedByte();
			else if (i == 13)
				standAnimation = stream.readUnsignedShort();
			else if (i == 14)
				walkAnimation = stream.readUnsignedShort();
			else if (i == 17) {
				walkAnimation = stream.readUnsignedShort();
				walkingBackwardsAnimation = stream.readUnsignedShort();
				walkLeftAnimation = stream.readUnsignedShort();
				walkRightAnimation = stream.readUnsignedShort();
			} else if (i >= 30 && i < 40) {
				if (actions == null)
					actions = new String[5];
				actions[i - 30] = stream.readNewString();
				if (actions[i - 30].equalsIgnoreCase("hidden"))
					actions[i - 30] = null;
			} else if (i == 40) {
				int k = stream.readUnsignedByte();
				anIntArray76 = new int[k];
				anIntArray70 = new int[k];
				for (int k1 = 0; k1 < k; k1++) {
					anIntArray76[k1] = stream.readUnsignedShort();
					anIntArray70[k1] = stream.readUnsignedShort();
				}

			} else if (i == 60) {
				int l = stream.readUnsignedByte();
				anIntArray73 = new int[l];
				for (int l1 = 0; l1 < l; l1++)
					anIntArray73[l1] = stream.readUnsignedShort();

			} else if (i == 90)
				stream.readUnsignedShort();
			else if (i == 91)
				stream.readUnsignedShort();
			else if (i == 92)
				stream.readUnsignedShort();
			else if (i == 93)
				aBoolean87 = false;
			else if (i == 95)
				combatLevel = stream.readUnsignedShort();
			else if (i == 97)
				adjustVertextPointsXOrY = stream.readUnsignedShort();
			else if (i == 98)
				adjustVertextPointZ = stream.readUnsignedShort();
			else if (i == 99)
				aBoolean93 = true;
			else if (i == 100)
				anInt85 = stream.readSignedByte();
			else if (i == 101)
				anInt92 = stream.readSignedByte() * 5;
			else if (i == 102)
				anInt75 = stream.readUnsignedShort();
			else if (i == 103)
				anInt79 = stream.readUnsignedShort();
			else if (i == 106) {
				varBitChild = stream.readUnsignedShort();
				if (varBitChild == 65535)
					varBitChild = -1;
				configChild = stream.readUnsignedShort();
				if (configChild == 65535)
					configChild = -1;
				int i1 = stream.readUnsignedByte();
				childrenIDs = new int[i1 + 1];
				for (int i2 = 0; i2 <= i1; i2++) {
					childrenIDs[i2] = stream.readUnsignedShort();
					if (childrenIDs[i2] == 65535)
						childrenIDs[i2] = -1;
				}

			} else if (i == 107)
				aBoolean84 = false;
		} while (true);
	}

	public NPCDefinition() {
		walkRightAnimation = -1;
		varBitChild = -1;
		walkingBackwardsAnimation = -1;
		configChild = -1;
		combatLevel = -1;
		anInt64 = 1834;
		walkAnimation = -1;
		npcSizeInSquares = 1;
		anInt75 = -1;
		standAnimation = -1;
		interfaceType = -1L;
		anInt79 = 32;
		walkLeftAnimation = -1;
		aBoolean84 = true;
		adjustVertextPointZ = 128;
		aBoolean87 = true;
		adjustVertextPointsXOrY = 128;
		aBoolean93 = false;
	}

	public int walkRightAnimation;
	public static int anInt56;
	public int varBitChild;
	public int walkingBackwardsAnimation;
	public int configChild;
	public static Stream stream;
	public int combatLevel;
	public final int anInt64;
	public String name;
	public String actions[];
	public int walkAnimation;
	public byte npcSizeInSquares;
	public int[] anIntArray70;
	public static int[] streamIndices;
	public int[] anIntArray73;
	public int anInt75;
	public int[] anIntArray76;
	public int standAnimation;
	public long interfaceType;
	public int anInt79;
	public static NPCDefinition[] cache;
	public static Client clientInstance;
	public int walkLeftAnimation;
	public boolean aBoolean84;
	public int anInt85;
	public int adjustVertextPointZ;
	public boolean aBoolean87;
	public int childrenIDs[];
	public byte description[];
	public int adjustVertextPointsXOrY;
	public int anInt92;
	public boolean aBoolean93;
	public int[] npcModels;
	public static MRUNodes mruNodes = new MRUNodes(30);

}
