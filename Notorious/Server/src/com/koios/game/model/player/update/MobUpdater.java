package com.koios.game.model.player.update;

import java.util.Iterator;
import java.util.Set;

import com.koios.game.model.UpdateFlags;
import com.koios.game.model.Damage.HitMasks;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacketBuilder;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.GamePacketBuilder.AccessType;

/**
 * Handles mob updating for a single player
 *
 * @author Timothy Howard <Arithium>
 */
public class MobUpdater extends EntityUpdater {

	/**
	 * The amount of mobs to add each cycle to reduce buffer overloading
	 */
	private final int MOBS_PER_CYCLE = 15;

	@Override
	public void update(Player player) {
		GamePacketBuilder updateBlock = new GamePacketBuilder();
		GamePacketBuilder buffer = new GamePacketBuilder(65, PacketType.SHORT);
		buffer.initializeAccess(AccessType.BIT);

		Set<Mob> mobs = player.getLocalMobs();

		buffer.writeBits(8, mobs.size());

		Iterator<Mob> iterator = mobs.iterator();
		while (iterator.hasNext()) {
			Mob mob = iterator.next();
			if (!mob.isTeleporting() && mob.isVisible() && MobManager.getMobs()[mob.getIndex()] != null && player.isWithinDistance(mob)) {
				updateMobMovement(mob, buffer);
				appendNPCUpdateBlock(mob, updateBlock, false);
			} else {
				iterator.remove();
				buffer.writeBits(1, 1);
				buffer.writeBits(2, 3);
			}
		}

		int added = 0;

		for (Mob mob : MobManager.mobs) {
			if (added == MOBS_PER_CYCLE) {
				break;
			}

			if (mobs.size() >= 255) {
				break;
			}

			if (mob == null) {
				continue;
			}

			if (mobs.contains(mob)) {
				continue;
			}

			if (!player.isWithinDistance(mob) || !mob.isVisible() || mob.isTeleporting()) {
				continue;
			}

			mobs.add(mob);
			addNewMob(player, mob, buffer);
			appendNPCUpdateBlock(mob, updateBlock, true);
			added++;
		}

		if (updateBlock.toPacket().getSize() > 0) {
			buffer.writeBits(14, 16383);
			buffer.initializeAccess(AccessType.BYTE);
			buffer.writeBuffer(updateBlock.toPacket().getBuffer());
		} else {
			buffer.initializeAccess(AccessType.BYTE);
		}

		player.write(buffer.toPacket());

	}

	private void appendAnimUpdate(Mob mob, GamePacketBuilder buffer) {
		buffer.writeLEShort(mob.getCurrentAnimation().getId());
		buffer.writeByte(mob.getCurrentAnimation().getDelay());
	}

	private void appendFaceEntity(Mob mob, GamePacketBuilder buffer) {
		buffer.writeShort(mob.getFaceIndex());
	}

 private static int encodedMaximum(Mob mob){return mob.maximumHitpoints>255?100:Math.max(0,mob.maximumHitpoints);}
 private static int encodedHealth(Mob mob){int maximum=Math.max(0,mob.maximumHitpoints),current=Math.max(0,Math.min(maximum,mob.getHitpoints()));return maximum>255?(current==0?0:Math.max(1,(int)((long)current*100/maximum))):current;}

	private void appendHitUpdate(Mob mob, GamePacketBuilder buffer) {
		buffer.writeInt(mob.getDamageMask1().getDamage());
		buffer.writeByteS(mob.getDamageMask1().getHitMask() == HitMasks.CRITICAL ? 3 : mob.getDamageMask1().getHitMask() == HitMasks.POISON ? 2 : mob.getDamageMask1().getDamage() > 0 ? 1 : 0);
		buffer.writeByteS(encodedHealth(mob));
		buffer.writeByteC(encodedMaximum(mob));
	}

	private void appendHitUpdate2(Mob mob, GamePacketBuilder buffer) {
		buffer.writeInt(mob.getDamageMask2().getDamage());
		buffer.writeByteC(mob.getDamageMask2().getHitMask() == HitMasks.CRITICAL ? 3 : mob.getDamageMask2().getHitMask() == HitMasks.POISON ? 2 : mob.getDamageMask2().getDamage() > 0 ? 1 : 0);
		buffer.writeByteA(encodedHealth(mob));
		buffer.writeByte(encodedMaximum(mob));
	}

	private void appendMask80Update(Mob mob, GamePacketBuilder buffer) {
		buffer.writeShort(mob.getCurrentGraphic().getId());
		buffer.writeLEInt(mob.getCurrentGraphic().getDelay());
	}

	public void appendNPCUpdateBlock(Mob mob, GamePacketBuilder buffer, boolean forceAppearance) {
		if (!mob.getUpdateFlags().isUpdateRequired() && !forceAppearance) {
			return;
		}

		final UpdateFlags flag = mob.getUpdateFlags();

		int flags = 0;

		if (flag.get(UpdateFlag.ANIMATION)) {
			flags |= 0x10;
		}

		if (flag.get(UpdateFlag.HIT_2)) {
			flags |= 8;
		}

		if (flag.get(UpdateFlag.GRAPHICS)) {
			flags |= 0x80;
		}

		if (flag.get(UpdateFlag.FACE_ENTITY)) {
			flags |= 0x20;
		}

		if (flag.get(UpdateFlag.FORCED_CHAT) && mob.spokenText != null) {
			flags |= 1;
		}

		if (flag.get(UpdateFlag.HIT)) {
			flags |= 0x40;
		}

		if (flag.get(UpdateFlag.FACE_COORDINATE) && mob.getFocusAtPointX() != -1) {
			flags |= 4;
		}

		if (flag.get(UpdateFlag.TRANSFORM)) {
			flags |= 2;
		}

		buffer.writeByte(flags);

		if (flag.get(UpdateFlag.ANIMATION)) {
			appendAnimUpdate(mob, buffer);
		}

		if (flag.get(UpdateFlag.HIT_2)) {
			appendHitUpdate2(mob, buffer);
		}

		if (flag.get(UpdateFlag.GRAPHICS)) {
			appendMask80Update(mob, buffer);
		}

		if (flag.get(UpdateFlag.FACE_ENTITY)) {
			appendFaceEntity(mob, buffer);
		}

		if (flag.get(UpdateFlag.TRANSFORM)) {
			appendTransformUpdate(mob, buffer);
		}

		if (flag.get(UpdateFlag.FORCED_CHAT) && mob.spokenText != null) {
			buffer.writeString(mob.spokenText);
		}

		if (flag.get(UpdateFlag.HIT)) {
			appendHitUpdate(mob, buffer);
		}

		if (flag.get(UpdateFlag.FACE_COORDINATE) && mob.getFocusAtPointX() != -1) {
			appendSetFocusDestination(mob, buffer);
		}
	}

	private void appendSetFocusDestination(Mob mob, GamePacketBuilder buffer) {
		buffer.writeLEShort(mob.getFocusAtPointX());
		buffer.writeLEShort(mob.getFocusAtPointY());
	}

	public void appendTransformUpdate(Mob mob, GamePacketBuilder buffer) {
		buffer.writeLEShortA(mob.transformId);
	}

	public void updateMobMovement(Mob mob, GamePacketBuilder buffer) {
		if (mob.direction == -1 || mob.direction > 7) {
			// no movement
			if (mob.getUpdateFlags().isUpdateRequired()) {
				buffer.writeBits(1, 1);
				buffer.writeBits(2, 0);
			} else {
				buffer.writeBits(1, 0);
			}
		} else if (mob.runDirection == -1) {
			// walk
			buffer.writeBits(1, 1);
			buffer.writeBits(2, 1);
			buffer.writeBits(3, mob.direction);
			buffer.writeBits(1, mob.getUpdateFlags().isUpdateRequired() ? 1 : 0);

		} else {
			// run
			buffer.writeBits(1, 1);
			buffer.writeBits(2, 2);
			buffer.writeBits(3, mob.direction);
			buffer.writeBits(3, mob.runDirection);
			buffer.writeBits(1, mob.getUpdateFlags().isUpdateRequired() ? 1 : 0);
		}
	}

	public void addNewMob(Player player, Mob mob, GamePacketBuilder buffer) {
		buffer.writeBits(14, mob.getIndex());
		int localY = mob.getY() - player.getY();

		if (localY < 0) {
			localY += 32;
		}

		buffer.writeBits(5, localY);
		int localX = mob.getX() - player.getX();

		if (localX < 0) {
			localX += 32;
		}

		buffer.writeBits(5, localX);
		buffer.writeBits(1, 0);
		buffer.writeBits(14, mob.getId());
		buffer.writeBits(1, 1);
	}

}
