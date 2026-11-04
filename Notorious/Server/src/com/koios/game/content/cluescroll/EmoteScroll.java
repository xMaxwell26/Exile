package com.koios.game.content.cluescroll;

import java.util.function.Predicate;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.cluescroll.ClueScrollConstants.Difficulty;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.task.Task;

public class EmoteScroll extends ClueScroll {

	private final String lines;

	private final Item[] requiredItems;

	private final int emote;

	public EmoteScroll(int item, Predicate<Location> emoteLocation, Difficulty difficulty, int emote, Item[] requiredItems, String lines) {
		super(item, emoteLocation, difficulty);
		this.lines = lines;
		this.requiredItems = requiredItems;
		this.emote = emote;
	}

	@Override
	public void attemptCompletion(Player player) {
		if (player.getAttribute("KILL_AGENT", false)) {
			return;
		}

		if (player.getEquipment().itemsEqual(requiredItems)) {
			if (getDifficulty().equals(Difficulty.HARD)) {
				player.setAttribute("KILL_AGENT", Boolean.TRUE);
				Location spawn = Constants.getClearAdjacentLocation(player.getLocation(), 1);
				Mob uri = MobManager.spawnMob(player, 5145, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 120, 14, 170, 170, true, true);
				uri.playGraphics(new Graphic(86));
				GameService.getTaskScheduler().schedule(new Task() {

					@Override
					protected void execute() {
						if (player.isDisconnected() || uri.isDead() || player.getLocation().getDistance(uri.getLocation()) > 25) {
							uri.setDead(true);
							stop();
						}
					}

				});
			} else {
				onAgentDeath(player);
			}
		} else {
			player.sendMessage("You must only wear the required items.");
		}
	}

	@Override
	public void open(Player player) {
		player.getSession().send(new SendRemoveInterfacePacket());
		player.getInterfaceState().openInterface(6965);
		String[] lines = this.lines.split("\n");

		for (int i = 6968; i <= 6975; i++) {
			player.getSession().send(new SendStringPacket(lines[i - 6968], i));
		}
	}

	public int getEmote() {
		return emote;
	}

	public void onAgentDeath(Player player) {
		player.setAttribute("KILL_AGENT", true);

		GameService.getTaskScheduler().schedule(new Task(2) {

			@Override
			protected void execute() {
				Location spawn = Constants.getClearAdjacentLocation(player.getLocation(), 1);
				Mob uri = MobManager.spawnMob(player, 5143, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 120, 14, 120, 120, false, false);
				uri.setFaceIndex(player.getIndex(), true);
				uri.playAnimation(new Animation(863));
				uri.playGraphics(new Graphic(86));
				reward(player, "Uri gives you");
				uri.setDead(true);

				GameService.getTaskScheduler().schedule(new Task(4) {

					@Override
					protected void execute() {
						uri.playGraphics(new Graphic(86));
						stop();
					}

				});

				stop();
			}

		});
	}

}
