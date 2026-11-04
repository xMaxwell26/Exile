package com.koios.client.world.entity;

import com.koios.client.Client;
import com.koios.client.cache.node.MRUNodes;
import com.koios.client.definition.Animation;
import com.koios.client.definition.FrameReader;
import com.koios.client.definition.IdentitiKit;
import com.koios.client.definition.ItemDefinition;
import com.koios.client.definition.NPCDefinition;
import com.koios.client.definition.SpotAnim;
import com.koios.client.stream.Stream;
import com.koios.client.utils.UsernameEncoding;
import com.koios.client.world.Model;

public final class Player extends Entity {

	public Model getRotatedModel() {
		if (!visible)
			return null;
		Model model = method452();
		if (model == null)
			return null;
		super.height = model.modelHeight;
		model.aBoolean1659 = true;
		if (aBoolean1699)
			return model;
		if (super.anInt1520 != -1 && super.anInt1521 != -1) {
			SpotAnim spotAnim = SpotAnim.cache[super.anInt1520];
			Model model_2 = spotAnim.getModel();

			if (model_2 != null) {
				Model model_3 = new Model(true, FrameReader.notNulled(super.anInt1521), false, model_2);
				int nextFrame = spotAnim.aAnimation_407.anIntArray353[super.nextGraphicsAnimationFrame];
				int cycle1 = spotAnim.aAnimation_407.anIntArray355[super.anInt1521];
				int cycle2 = super.anInt1522;
				model_3.method475(0, -super.anInt1524, 0);
				model_3.method469();
				model_3.method470(spotAnim.aAnimation_407.anIntArray353[super.anInt1521], nextFrame, cycle1, cycle2);
				model_3.anIntArrayArray1658 = null;
				model_3.anIntArrayArray1657 = null;
				if (spotAnim.anInt410 != 128 || spotAnim.anInt411 != 128)
					model_3.method478(spotAnim.anInt410, spotAnim.anInt410, spotAnim.anInt411);
				model_3.method479(64 + spotAnim.anInt413, 850 + spotAnim.anInt414, -30, -50, -30, true);
				Model aclass30_sub2_sub4_sub6_1s[] = { model, model_3 };
				model = new Model(aclass30_sub2_sub4_sub6_1s);
			} else {
				return null;
			}
		}
		if (aModel_1714 != null) {
			if (Client.loopCycle >= anInt1708)
				aModel_1714 = null;
			if (Client.loopCycle >= anInt1707 && Client.loopCycle < anInt1708) {
				Model model_1 = aModel_1714;
				model_1.method475(anInt1711 - super.x, anInt1712 - anInt1709, anInt1713 - super.y);
				if (super.turnDirection == 512) {
					model_1.method473();
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1024) {
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1536)
					model_1.method473();
				Model aclass30_sub2_sub4_sub6s[] = { model, model_1 };
				model = new Model(aclass30_sub2_sub4_sub6s);
				if (super.turnDirection == 512)
					model_1.method473();
				else if (super.turnDirection == 1024) {
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1536) {
					model_1.method473();
					model_1.method473();
					model_1.method473();
				}
				model_1.method475(super.x - anInt1711, anInt1709 - anInt1712, super.y - anInt1713);
			}
		}
		model.aBoolean1659 = true;
		return model;
	}

	public void updatePlayer(Stream stream) {
		stream.currentOffset = 0;
		gender = stream.readUnsignedByte();
		headIcon = stream.readUnsignedByte();
		skullIcon = stream.readUnsignedByte();

		desc = null;
		team = 0;
		for (int j = 0; j < 12; j++) {
			int k = stream.readUnsignedByte();
			if (k == 0) {
				equipment[j] = 0;
				continue;
			}
			int i1 = stream.readUnsignedByte();
			equipment[j] = (k << 8) + i1;
			if (j == 0 && equipment[0] == 65535) {
				desc = NPCDefinition.forID(stream.readUnsignedShort());
				break;
			}
			if (equipment[j] >= 512 && equipment[j] - 512 < ItemDefinition.totalItems) {
				int l1 = ItemDefinition.forID(equipment[j] - 512).team;
				if (l1 != 0)
					team = l1;
			}
		}

		for (int color = 0; color < 5; color++) {
			int colorId = stream.readUnsignedByte();
			if (colorId < 0 || colorId >= Client.anIntArrayArray1003[color].length)
				colorId = 0;
			anIntArray1700[color] = colorId;
		}

		super.standAnimation = stream.readUnsignedShort();
		if (super.standAnimation == 65535)
			super.standAnimation = -1;
		super.turnAnimation = stream.readUnsignedShort();
		if (super.turnAnimation == 65535)
			super.turnAnimation = -1;
		super.walkAnimation = stream.readUnsignedShort();
		if (super.walkAnimation == 65535)
			super.walkAnimation = -1;
		super.turn180Animation = stream.readUnsignedShort();
		if (super.turn180Animation == 65535)
			super.turn180Animation = -1;
		super.turn90CW = stream.readUnsignedShort();
		if (super.turn90CW == 65535)
			super.turn90CW = -1;
		super.turn90CCW = stream.readUnsignedShort();
		if (super.turn90CCW == 65535)
			super.turn90CCW = -1;
		super.runAnimation = stream.readUnsignedShort();
		if (super.runAnimation == 65535)
			super.runAnimation = -1;
		name = UsernameEncoding.formalize(UsernameEncoding.decode(stream.readLong()));

		combatLevel = stream.readUnsignedByte();
		// stream.readUnsignedShort();
		skill = 0;// stream.readUnsignedWord();

		visible = true;
		aLong1718 = 0L;
		for (int k1 = 0; k1 < 12; k1++) {
			aLong1718 <<= 4;
			if (equipment[k1] >= 256)
				aLong1718 += equipment[k1] - 256;
		}

		if (equipment[0] >= 256)
			aLong1718 += equipment[0] - 256 >> 4;
		if (equipment[1] >= 256)
			aLong1718 += equipment[1] - 256 >> 8;
		for (int i2 = 0; i2 < 5; i2++) {
			aLong1718 <<= 3;
			aLong1718 += anIntArray1700[i2];
		}

		aLong1718 <<= 1;
		aLong1718 += gender;
	}

	public Model method452() {
		if (desc != null) {
			int currentFrame = -1;
			int nextFrame = -1;
			int cycle1 = 0;
			int cycle2 = 0;
			if (super.anim >= 0 && super.anInt1529 == 0) {
				Animation animation = Animation.anims[super.anim];
				currentFrame = animation.anIntArray353[super.anInt1527];
				nextFrame = animation.anIntArray353[super.nextAnimationFrame];
				cycle1 = animation.anIntArray355[super.anInt1527];
				cycle2 = super.anInt1528;
			} else if (super.anInt1517 >= 0) {
				Animation animation = Animation.anims[super.anInt1517];
				currentFrame = animation.anIntArray353[super.anInt1518];
				nextFrame = animation.anIntArray353[super.nextIdleAnimationFrame];
				cycle1 = animation.anIntArray355[super.anInt1518];
				cycle2 = super.anInt1519;
			}
			Model model = desc.method164(-1, currentFrame, null, nextFrame, cycle1, cycle2);
			return model;
		}
		long l = aLong1718;
		int currentFrame = -1;
		int nextFrame = -1;
		int cycle1 = 0;
		int cycle2 = 0;
		int i1 = -1;
		int j1 = -1;
		int k1 = -1;
		if (super.anim >= 0 && super.anInt1529 == 0) {
			Animation animation = Animation.anims[super.anim];
			currentFrame = animation.anIntArray353[super.anInt1527];
			nextFrame = animation.anIntArray353[super.nextAnimationFrame];
			cycle1 = animation.anIntArray355[super.anInt1527];
			cycle2 = super.anInt1528;
			if (super.anInt1517 >= 0 && super.anInt1517 != super.standAnimation)
				i1 = Animation.anims[super.anInt1517].anIntArray353[super.anInt1518];
			if (animation.anInt360 >= 0) {
				j1 = animation.anInt360;
				l += j1 - equipment[5] << 40;
			}
			if (animation.anInt361 >= 0) {
				k1 = animation.anInt361;
				l += k1 - equipment[3] << 48;
			}
		} else if (super.anInt1517 >= 0) {
			Animation animation = Animation.anims[super.anInt1517];
			currentFrame = animation.anIntArray353[super.anInt1518];
			nextFrame = animation.anIntArray353[super.nextIdleAnimationFrame];
			cycle1 = animation.anIntArray355[super.anInt1518];
			cycle2 = super.anInt1519;
		}
		Model model_1 = (Model) mruNodes.insertFromCache(l);
		if (model_1 == null) {
			boolean flag = false;
			for (int i2 = 0; i2 < 12; i2++) {
				int k2 = equipment[i2];
				if (k1 >= 0 && i2 == 3)
					k2 = k1;
				if (j1 >= 0 && i2 == 5)
					k2 = j1;
				if (k2 >= 256 && k2 < 512 && !IdentitiKit.cache[k2 - 256].method537())
					flag = true;
				if (k2 >= 512 && !ItemDefinition.forID(k2 - 512).method195(gender))
					flag = true;
			}

			if (flag) {
				if (aLong1697 != -1L)
					model_1 = (Model) mruNodes.insertFromCache(aLong1697);
				if (model_1 == null)
					return null;
			}
		}
		if (model_1 == null) {
			Model aclass30_sub2_sub4_sub6s[] = new Model[14];
			int j2 = 0;
			for (int l2 = 0; l2 < 12; l2++) {
				int i3 = equipment[l2];
				if (k1 >= 0 && l2 == 3)
					i3 = k1;
				if (j1 >= 0 && l2 == 5)
					i3 = j1;
				if (i3 >= 256 && i3 < 512) {
					Model model_3 = IdentitiKit.cache[i3 - 256].method538();
					if (model_3 != null)
						aclass30_sub2_sub4_sub6s[j2++] = model_3;
				}
				if (i3 >= 512) {
					Model model_4 = ItemDefinition.forID(i3 - 512).method196(gender);
					if (model_4 != null)
						aclass30_sub2_sub4_sub6s[j2++] = model_4;
				}
			}
			// aclass30_sub2_sub4_sub6s[j2++] = Model.method462(9996);
			model_1 = new Model(j2, aclass30_sub2_sub4_sub6s);
			for (int j3 = 0; j3 < 5; j3++)
				if (anIntArray1700[j3] != 0) {
					model_1.method476(Client.anIntArrayArray1003[j3][0], Client.anIntArrayArray1003[j3][anIntArray1700[j3]]);
					if (j3 == 1)
						model_1.method476(Client.anIntArray1204[0], Client.anIntArray1204[anIntArray1700[j3]]);
				}

			model_1.method469();
			model_1.method478(132, 132, 132);
			model_1.method479(72, 1300, -30, -50, -30, true);
			mruNodes.removeFromCache(model_1, l);
			aLong1697 = l;
		}
		if (aBoolean1699)
			return model_1;
		Model model_2 = Model.aModel_1621;
		model_2.method464(model_1, FrameReader.notNulled(currentFrame) & FrameReader.notNulled(i1));
		if (currentFrame != -1 && i1 != -1)
			model_2.method471(Animation.anims[super.anim].anIntArray357, i1, currentFrame);
		else if (currentFrame != -1 && nextFrame != -1)
			model_2.method470(currentFrame, nextFrame, cycle1, cycle2);
		else
			model_2.method470(currentFrame);
		model_2.method466();
		model_2.anIntArrayArray1658 = null;
		model_2.anIntArrayArray1657 = null;
		return model_2;
	}

	public boolean isVisible() {
		return visible;
	}

	public int privelage;

	public Model method453() {
		if (!visible)
			return null;
		if (desc != null)
			return desc.method160();
		boolean flag = false;
		for (int i = 0; i < 12; i++) {
			int j = equipment[i];
			if (j >= 256 && j < 512 && !IdentitiKit.cache[j - 256].method539())
				flag = true;
			if (j >= 512 && !ItemDefinition.forID(j - 512).method192(gender))
				flag = true;
		}

		if (flag)
			return null;
		Model aclass30_sub2_sub4_sub6s[] = new Model[12];
		int k = 0;
		for (int l = 0; l < 12; l++) {
			int i1 = equipment[l];
			if (i1 >= 256 && i1 < 512) {
				Model model_1 = IdentitiKit.cache[i1 - 256].method540();
				if (model_1 != null)
					aclass30_sub2_sub4_sub6s[k++] = model_1;
			}
			if (i1 >= 512) {
				Model model_2 = ItemDefinition.forID(i1 - 512).method194(gender);
				if (model_2 != null)
					aclass30_sub2_sub4_sub6s[k++] = model_2;
			}
		}

		Model model = new Model(k, aclass30_sub2_sub4_sub6s);
		for (int j1 = 0; j1 < 5; j1++)
			if (anIntArray1700[j1] != 0) {
				model.method476(Client.anIntArrayArray1003[j1][0], Client.anIntArrayArray1003[j1][anIntArray1700[j1]]);
				if (j1 == 1)
					model.method476(Client.anIntArray1204[0], Client.anIntArray1204[anIntArray1700[j1]]);
			}

		return model;
	}

	public Player() {
		aLong1697 = -1L;
		aBoolean1699 = false;
		anIntArray1700 = new int[5];
		visible = false;
		equipment = new int[12];
	}

	private long aLong1697;
	public NPCDefinition desc;
	public boolean aBoolean1699;
	public final int[] anIntArray1700;
	public int team;
	private int gender;
	public String name;
	public static MRUNodes mruNodes = new MRUNodes(260);
	public int combatLevel;
	public int headIcon;
	public int skullIcon;
	public int hintIcon;
	public int anInt1707;
	public int anInt1708;
	public int anInt1709;
	public boolean visible;
	public int anInt1711;
	public int anInt1712;
	public int anInt1713;
	public Model aModel_1714;
	public final int[] equipment;
	private long aLong1718;
	public int anInt1719;
	public int anInt1720;
	public int anInt1721;
	public int anInt1722;
	public int skill;
	public int realPlane;

}
