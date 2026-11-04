package com.koios.network.in.impl;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.content.randoms.RandomEvent;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.task.Task;

/**
 * Magic on floor items
 */
public class PacketSpellWithGroundItem implements IncomingPacketDecoder {

	private static final int TELEKINETIC_GRAB = 1168;

	@Override
	public void handle(Player player, GamePacket packet) {
		int itemY = packet.readLEShort();
		int itemId = packet.readShort();
		int itemX = packet.readLEShort();
		int spellId = packet.readShortA();
		player.getMovementHandler().reset();

		if (spellId != TELEKINETIC_GRAB) {
			// Note: invalidate bad requests to cast non-telekinetic grab spells.
			return;
		}

		if (!GameService.getGroundItemManager().exists(player, itemId, itemX, itemY)) {
			// Note: invalidate bad requests to use a ground item that does not exist.
			return;
		}

		if (player.getSpellbook() != Spellbook.REGULAR) {
			// Telekinetic grab can only be cast with regular magic spells.
			return;
		}

		if (player.goodDistance(player.getX(), player.getY(), itemX, itemY, 12)) {
			if (player.getSkill().getLevel(Skill.MAGIC) < 33) {
				player.sendMessage("You need a Magic level of 33 to cast telekinetic grab.");
				return;
			}

			int offY = (player.getX() - itemX) * -1;
			int offX = (player.getY() - itemY) * -1;
			player.setFacePosition(itemX, itemY);
			player.playAnimation(new Animation(MagicData.MAGIC_SPELLS[51][2]));
			player.playGraphics(new Graphic(GraphicHeight.HIGH, MagicData.MAGIC_SPELLS[51][3]));
			Graphic.createGlobalGraphic(144, itemX, itemY, 0, 72);
			CombatExtras.createPlayersProjectile(player.getX(), player.getY(), player.getLocation().getZ(), offX, offY, 50, 70, MagicData.MAGIC_SPELLS[51][4], 50, 10, 0, 50, 16);

			GameService.getTaskScheduler().schedule(new Task(2) {

				@Override
				public void execute() {
					if (!player.isDisconnected()) {
						if (GameService.getGroundItemManager().removeGroundItem(player, itemId, itemX, itemY, player.getZ(), true)) {
							player.getSkill().addExperience(Skill.MAGIC, 3 * Constants.MAGIC_EXP_RATE);
						}

						RandomEvent.tryRandom(player);
					}

					stop();
				}

			});
		}
	}

}
