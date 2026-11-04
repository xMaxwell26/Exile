package com.koios.game.content.wilderness;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koios.GameService;
import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.content.questtab.impl.PointsQuestPage;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

/**
 * Handle's PvP killstreak features
 *
 * @author Gabriel | Wolfs Darker
 */
public class Killstreaks {

	private final CopyOnWriteArrayList<String> murderedPlayers = new CopyOnWriteArrayList<>();

	private final Player player;

	public Killstreaks(Player player) {
		this.player = Objects.requireNonNull(player);
	}

	/**
	 * Get the player's murdered list
	 *
	 * @return the list
	 */
	public CopyOnWriteArrayList<String> getMurderedPlayers() {
		return murderedPlayers;
	}

	/**
	 * Increase the player's killstreak
	 */
	public boolean increaseKillstreak(String t) {
		if (!isKillValid(t)) {
			return false;
		}

		murderedPlayers.add(t);

		if (murderedPlayers.size() < 5) {
			return true;
		}

		int bonusPkp = murderedPlayers.size() - 5 + 1;
		Optional<Player> target = GameService.getWorld().getByUsername(t);

		if (target != null && target.get().getKS().getMurderedPlayers().size() > 5) {
			player.pvpPoints += (target.get().getKS().getMurderedPlayers().size() - 5 + 1) / 2;
		}

		player.pvpPoints += bonusPkp;
		
		if (QuestTabHandler.onPage(player, QuestPages.POINTS)) {
			player.getSession().send(new SendStringPacket("@or1@Pk Points: @gre@" + player.pvpPoints, QuestTabHandler.getLineId(PointsQuestPage.PKING_POINTS)));
		}

		for (Player p2 : GameService.getWorld().getPlayers()) {
			if (p2 == null) {
				continue;
			}

			p2.sendMessage("@bla@[ @blu@Wilderness@bla@ ] " + player.getName() + " just achieved a killstreak of @blu@" + murderedPlayers.size() + "@bla@! Kill him for "
					+ (bonusPkp == 1 ? 1 : bonusPkp / 2) + " extra points!");
		}

		return true;
	}

	/**
	 * Checks a player name to see if the murderer didn't kill him before
	 *
	 * @param t target's name
	 * @return if the kill is valid/or not
	 */
	public boolean isKillValid(String t) {
		for (String s : new LinkedList<>(murderedPlayers)) {
			if (t.equals(s)) {
				player.sendMessage("Your killstreak won't increase as you've already killed this player.");
				return false;
			}
		}

		return true;
	}

}
