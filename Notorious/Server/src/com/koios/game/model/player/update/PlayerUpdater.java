package com.koios.game.model.player.update;

import java.util.Iterator;
import java.util.Set;

import com.koios.GameService;
import com.koios.game.def.MobDefinition;
import com.koios.game.model.UpdateFlags;
import com.koios.game.model.Damage.HitMasks;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.ItemAssistant;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacketBuilder;
import com.koios.network.GamePacket.PacketType;
import com.koios.network.GamePacketBuilder.AccessType;

public final class PlayerUpdater extends EntityUpdater {

	/**
	 * The amount of players to add each cycle to reduce buffer overloading
	 */
	private final int PLAYERS_PER_CYCLE = 15;

	@Override
	public void update(Player player) {

		GamePacketBuilder buffer = new GamePacketBuilder(81, PacketType.SHORT);
		GamePacketBuilder updateBlock = new GamePacketBuilder();

		buffer.initializeAccess(AccessType.BIT);

		updateThisPlayerMovement(player, buffer);
		appendPlayerUpdateBlock(updateBlock, player, true, false, true);

		Set<Player> players = player.getLocalPlayers();

		buffer.writeBits(8, players.size());

		Iterator<Player> iterator = players.iterator();
		while (iterator.hasNext()) {
			Player target = iterator.next();
			if (target.isVisible() && GameService.getWorld().getPlayer(target.getIndex()) != null && !player.isTeleporting() && !target.isTeleporting()
					&& player.isWithinDistance(target)) {
				updatePlayerMovement(target, buffer);
				appendPlayerUpdateBlock(updateBlock, target, false, false, false);
			} else {
				iterator.remove();
				buffer.writeBits(1, 1);
				buffer.writeBits(2, 3);
			}
		}

		int amount = 0;

		for (Player target : GameService.getWorld().getPlayers()) {
			if (amount == PLAYERS_PER_CYCLE) {
				break;
			}

			if (players.size() >= 255) {
				break;
			}

			if (target.getUsernameHash() == player.getUsernameHash()) {
				continue;
			}

			if (!target.isVisible()) {
				return;
			}

			if (players.contains(target)) {
				continue;
			}

			if (!player.isWithinDistance(target)) {
				continue;
			}

			players.add(target);
			addNewPlayer(player, target, buffer, updateBlock);
			appendPlayerUpdateBlock(updateBlock, target, false, true, false);
			amount++;
		}

		if (updateBlock.toPacket().getSize() > 0) {
			buffer.writeBits(11, 2047);
			buffer.initializeAccess(AccessType.BYTE);
			buffer.writeBuffer(updateBlock.toPacket().getBuffer());
		} else {
			buffer.initializeAccess(AccessType.BYTE);
		}

		player.write(buffer.toPacket());
	}

	void updatePlayerMovement(Player player, GamePacketBuilder buffer) {
		boolean updated = player.getUpdateFlags().isUpdateRequired();

		if (player.getWalkingDirection() == -1) {
			buffer.writeBits(updated);
			if (updated) {
				buffer.writeBits(2, 0);
			}
		} else if (player.getRunningDirection() == -1) {
			buffer.writeBits(true);
			buffer.writeBits(2, 1);
			buffer.writeBits(3, player.getWalkingDirection());
			buffer.writeBits(updated);
		} else {
			buffer.writeBits(true);
			buffer.writeBits(2, 2);
			buffer.writeBits(3, player.getWalkingDirection());
			buffer.writeBits(3, player.getRunningDirection());
			buffer.writeBits(updated);
		}
	}

	void updateThisPlayerMovement(Player player, GamePacketBuilder buffer) {
		boolean updated = player.getUpdateFlags().isUpdateRequired();

		if (player.isPlacementNeeded()) {
			buffer.writeBits(true);
			buffer.writeBits(2, 3);
			buffer.writeBits(2, player.getLocation().getZ());
			buffer.writeBits(player.isTeleporting());
			buffer.writeBits(updated);
			buffer.writeBits(7, player.getLocation().getLocalY(player.getCurrentRegion()));
			buffer.writeBits(7, player.getLocation().getLocalX(player.getCurrentRegion()));
			player.setPlacementNeeded(false);
		} else {
			if (player.getWalkingDirection() == -1) {

				player.isMoving = false;

				buffer.writeBits(updated);
				if (player.getUpdateFlags().isUpdateRequired()) {
					buffer.writeBits(2, 0);
				}

			} else {
				buffer.writeBits(true);
				if (player.getRunningDirection() == -1) {
					player.isMoving = true;
					buffer.writeBits(2, 1);
					buffer.writeBits(3, player.getWalkingDirection());
					buffer.writeBits(updated);
				} else {
					player.isMoving = true;
					buffer.writeBits(2, 2);
					buffer.writeBits(3, player.getWalkingDirection());
					buffer.writeBits(3, player.getRunningDirection());
					buffer.writeBits(updated);
				}
			}
		}
	}

	void addNewPlayer(Player player, Player target, GamePacketBuilder buffer, GamePacketBuilder updateBlock) {
		buffer.writeBits(11, target.getIndex());
		buffer.writeBits(1, 1);
		buffer.writeBits(1, 1);

		int yPos = target.getY() - player.getY();
		int xPos = target.getX() - player.getX();

		if (yPos < 0) {
			yPos += 32;
		}

		if (xPos < 0) {
			xPos += 32;
		}

		// write the x and y offsets.
		buffer.writeBits(5, yPos);
		buffer.writeBits(5, xPos);
		buffer.writeBits(2, target.getZ());
	}

	private void appendAnimationRequest(Player player, GamePacketBuilder buffer) {
		buffer.writeLEShort(player.getCurrentAnimation().getId() <= 0 ? 65535 : player.getCurrentAnimation().getId());
		buffer.writeByteC(player.getCurrentAnimation().getDelay());
	}

	private void appendFaceUpdate(Player player, GamePacketBuilder buffer) {
		buffer.writeLEShort(player.getFaceIndex());
	}

	private void appendForcedChat(Player player, GamePacketBuilder buffer) {
		buffer.writeString(player.spokenText);
	}

	public void appendForceMovement(Player player, GamePacketBuilder buffer) {
		buffer.writeByteS(player.getLocation().getLocalX());
		buffer.writeByteS(player.getLocation().getLocalY());
		buffer.writeByteS(player.getForcePosition().getLocalX());
		buffer.writeByteS(player.getForcePosition().getLocalY());
		buffer.writeLEShortA(player.forceMoveSpeed);
		buffer.writeShortA(player.forceMoveSpeed2);
		buffer.writeByteS(player.forceMoveDirection);
	}

	private void appendHitUpdate(Player player, GamePacketBuilder buffer) {
		int damage = player.getDamageMask1().getDamage();
		damage = damage < 0 ? 0 : damage;
		buffer.writeInt(damage); // What the person got 'hit' for
		buffer.writeByteA(player.getDamageMask1().getHitMask() == HitMasks.POISON ? 2 : player.getDamageMask1().getDamage() > 0 ? 1 : 0);
		buffer.writeInt(player.getHitpoints()); // Their current hp, for HP
													// bar
		buffer.writeInt(player.getMaxHitpoints()); // Their max hp, for HP bar
	}

	private void appendHitUpdate2(Player player, GamePacketBuilder buffer) {
		int damage = player.getDamageMask2().getDamage();
		damage = damage < 0 ? -damage : damage;
		buffer.writeInt(damage);
		buffer.writeByteS(player.getDamageMask2().getHitMask() == HitMasks.POISON ? 2 : player.getDamageMask2().getDamage() > 0 ? 1 : 0);

		buffer.writeInt(player.getHitpoints()); // Current health
		buffer.writeInt(player.getMaxHitpoints()); // Maximum health
	}

	private void appendMask100Update(Player player, GamePacketBuilder buffer) {
		buffer.writeLEShort(player.getCurrentGraphic().getId());
		buffer.writeLEInt(player.getCurrentGraphic().getDelay());
	}

	private void appendPlayerAppearance(Player player, GamePacketBuilder buffer) {

		GamePacketBuilder bldr = new GamePacketBuilder();
		bldr.writeByte(player.getAppearance().getGender());
		bldr.writeByte(player.headIcon);
		bldr.writeByte(player.skullIcon);
		boolean isHunter = player.getMinigame().inMinigame() && player.getMinigame().getCurrentMinigame().getMinigameName().equals("PROP_HUNT");

		if (player.getMobId() != -1) {
			bldr.writeShort(65535);
			bldr.writeShort(player.getMobId());
		} else {
			if (isHunter || player.getEquipment().get(Equipment.SLOT_HEAD) != null) {
				bldr.writeShort(0x200 + (isHunter ? 7321 : player.getEquipment().get(Equipment.SLOT_HEAD).getId()));
			} else {
				bldr.writeByte(0);
			}

			// Player cape
			if (isHunter || player.getEquipment().get(Equipment.SLOT_CAPE) != null) {
				bldr.writeShort(0x200 + (isHunter ? 14081 : player.getEquipment().get(Equipment.SLOT_CAPE).getId()));
			} else {
				bldr.writeByte(0);
			}

			// Player amulet
			if (player.getEquipment().get(Equipment.SLOT_AMULET) != null) {
				bldr.writeShort(0x200 + (isHunter ? 0 : player.getEquipment().get(Equipment.SLOT_AMULET).getId()));
			} else {
				bldr.writeByte(0);
			}

			// Player weapon
			if (isHunter || player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
				bldr.writeShort(0x200 + (isHunter ? 6082 : player.getEquipment().get(Equipment.SLOT_WEAPON).getId()));
			} else {
				bldr.writeByte(0);
			}

			// Player chest
			if (isHunter || player.getEquipment().get(Equipment.SLOT_CHEST) != null) {
				bldr.writeShort(0x200 + (isHunter ? 14076 : player.getEquipment().get(Equipment.SLOT_CHEST).getId()));
			} else {
				bldr.writeShort(0x100 + player.getAppearance().getChest());
			}

			// Player shield
			if (player.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
				bldr.writeShort(0x200 + (isHunter ? 0 : player.getEquipment().get(Equipment.SLOT_SHIELD).getId()));
			} else {
				bldr.writeByte(0);
			}

			// Player arms
			if (!isHunter && !ItemAssistant.isFullBody(player.getEquipment().get(Equipment.SLOT_CHEST))) {
				bldr.writeShort(0x100 + player.getAppearance().getArms());
			} else {
				bldr.writeByte(0);
			}

			// Player legs
			if (isHunter || player.getEquipment().get(Equipment.SLOT_LEGS) != null) {
				bldr.writeShort(0x200 + (isHunter ? 14077 : player.getEquipment().get(Equipment.SLOT_LEGS).getId()));
			} else {
				bldr.writeShort(0x100 + player.getAppearance().getLegs());
			}

			// Player hair
			if (!isHunter && ItemAssistant.cantHaveHair(player.getEquipment().get(Equipment.SLOT_HEAD))) {
				bldr.writeByte(0);
			} else {
				bldr.writeShort(0x100 + player.getAppearance().getHead());
			}

			// Player gloves
			if (isHunter || player.getEquipment().get(Equipment.SLOT_GLOVES) != null) {
				bldr.writeShort(0x200 + (isHunter ? 13660 : player.getEquipment().get(Equipment.SLOT_GLOVES).getId()));
			} else {
				bldr.writeShort(0x100 + player.getAppearance().getHands());
			}

			// Player boots
			if (isHunter || player.getEquipment().get(Equipment.SLOT_BOOTS) != null) {
				bldr.writeShort(0x200 + (isHunter ? 21483 : player.getEquipment().get(Equipment.SLOT_BOOTS).getId()));
			} else {
				bldr.writeShort(0x100 + player.getAppearance().getFeet());
			}

			// Player beard
			if (!isHunter && ItemAssistant.cantHaveBeard(player.getEquipment().get(Equipment.SLOT_HEAD)) || player.getAppearance().getGender() == 1) {
				bldr.writeByte(0);
			} else {
				bldr.writeShort(0x100 + player.getAppearance().getBeard());
			}
		}

		// Player model colors
		bldr.writeByte(player.getAppearance().getHairColour());
		bldr.writeByte(player.getAppearance().getTorsoColour());
		bldr.writeByte(player.getAppearance().getLegColour());
		bldr.writeByte(player.getAppearance().getFeetColour());
		bldr.writeByte(player.getAppearance().getSkinColour());

		if (player.getMobId() != -1) {
			MobDefinition definition = MobDefinition.get(player.getMobId());
			bldr.writeShort(definition.getStandAnimation()); // standAnimIndex
			bldr.writeShort(0x337); // standTurnAnimIndex
			bldr.writeShort(definition.getWalkAnimation()); // walkAnimIndex
			bldr.writeShort(definition.getTurn180Animation()); // turn180AnimIndex
			bldr.writeShort(definition.getTurn90Animation(false)); // turn90CWAnimIndex
			bldr.writeShort(definition.getTurn90Animation(true)); // turn90CCWAnimIndex
			bldr.writeShort(definition.getWalkAnimation()); // runAnimIndex
		} else {
			// Weapon constructor
			Item weapon = isHunter ? new Item(6082) : player.getEquipment().get(Equipment.SLOT_WEAPON);

			// Stand animation
			bldr.writeShort(player.getStandAnim(weapon));

			// Turn animation
			bldr.writeShort(player.getTurnAnim(weapon));

			// Walk animation
			bldr.writeShort(player.getWalkAnim(weapon));

			// Turn 180 animation
			bldr.writeShort(player.get180Turn(weapon));

			// Turn 90 clockwise animation
			bldr.writeShort(player.get90CWTurn(weapon));

			// Turn 90 counter-clockwise animation
			bldr.writeShort(player.get90CCWTurn(weapon));

			// Run animation
			bldr.writeShort(player.getRunAnim(weapon));
		}

		bldr.writeLong(player.getUsernameHash());
		bldr.writeByte(player.getSkill().getCombatLevel(true)); // combat level
		// bldr.writeShort(player.getPlayerObjectId());

		// bldr.writeByte((int) player.getSkill().getSummoningCombat());
		// bldr.writeByte(player.inWild() ? 1 : 0);

		buffer.writeByteC(bldr.toPacket().getSize());
		buffer.writeBuffer(bldr.toPacket().getBuffer());

	}

	private void appendPlayerChatText(Player player, GamePacketBuilder buffer) {
		buffer.writeLEShort(((player.getChatTextColor() & 0xFF) << 8) + (player.getChatTextEffects() & 0xFF));
		buffer.writeByte(player.getChatIconId());
		buffer.writeByteC(player.getChatTextSize());
		buffer.writeReversedBytes(player.getChatText(), 0, player.getChatTextSize());
	}

	void appendPlayerUpdateBlock(GamePacketBuilder buffer, Player player, boolean samePlayer, boolean forceAppearance, boolean noChat) {
		if (!player.getUpdateFlags().isUpdateRequired() && !forceAppearance) {
			return;
		}

		if (player.getCachedBlock() != null && !samePlayer && !forceAppearance) {
			// buffer.writeBuffer(player.getCachedBlock().toPacket().getBuffer());
			// return;
		}

		GamePacketBuilder cacheStream = new GamePacketBuilder();

		UpdateFlags flag = player.getUpdateFlags();
		int flags = 0;

		if (flag.get(UpdateFlag.GRAPHICS)) {
			flags |= 0x100;
		}

		if (flag.get(UpdateFlag.ANIMATION)) {
			flags |= 8;
		}

		if (flag.get(UpdateFlag.FORCED_CHAT) && player.spokenText != null) {
			flags |= 4;
		}

		if (flag.get(UpdateFlag.CHAT) && !noChat) {
			flags |= 0x80;
		}

		if (flag.get(UpdateFlag.APPEARANCE) || forceAppearance) {
			flags |= 0x10;
		}

		if (flag.get(UpdateFlag.FACE_ENTITY)) {
			flags |= 1;
		}

		if (flag.get(UpdateFlag.FACE_COORDINATE) && player.getFocusAtPointX() != -1) {
			flags |= 2;
		}

		if (flag.get(UpdateFlag.HIT)) {
			flags |= 0x20;
		}

		if (flag.get(UpdateFlag.HIT_2)) {
			flags |= 0x200;
		}

		if (flag.get(UpdateFlag.FORCE_MOVEMENT)) {
			flags |= 0x400;
		}

		if (flags >= 0x100) {
			flags |= 0x40;
			cacheStream.writeByte(flags & 0xFF);
			cacheStream.writeByte(flags >> 8);
		} else {
			cacheStream.writeByte(flags);
		}

		if (flag.get(UpdateFlag.FORCE_MOVEMENT)) {
			appendForceMovement(player, cacheStream);
		}

		if (flag.get(UpdateFlag.GRAPHICS)) {
			appendMask100Update(player, cacheStream);
		}

		if (flag.get(UpdateFlag.ANIMATION)) {
			appendAnimationRequest(player, cacheStream);
		}

		if (flag.get(UpdateFlag.FORCED_CHAT) && player.spokenText != null) {
			appendForcedChat(player, cacheStream);
		}

		if (flag.get(UpdateFlag.CHAT) && !noChat) {
			appendPlayerChatText(player, cacheStream);
		}

		if (flag.get(UpdateFlag.FACE_ENTITY)) {
			appendFaceUpdate(player, cacheStream);
		}

		if (flag.get(UpdateFlag.APPEARANCE) || forceAppearance) {
			appendPlayerAppearance(player, cacheStream);
		}

		if (flag.get(UpdateFlag.FACE_COORDINATE) && player.getFocusAtPointX() != -1) {
			appendSetFocusDestination(player, cacheStream);
		}

		if (flag.get(UpdateFlag.HIT)) {
			appendHitUpdate(player, cacheStream);
		}

		if (flag.get(UpdateFlag.HIT_2)) {
			appendHitUpdate2(player, cacheStream);
		}

		if (!samePlayer && !forceAppearance) {
			player.setCachedBlock(cacheStream);
		}

		/** Add the cached block to the update block. */
		buffer.writeBuffer(cacheStream.toPacket().getBuffer());
	}

	private void appendSetFocusDestination(Player player, GamePacketBuilder buffer) {
		buffer.writeLEShortA(player.getFocusAtPointX());
		buffer.writeLEShort(player.getFocusAtPointY());
	}

}