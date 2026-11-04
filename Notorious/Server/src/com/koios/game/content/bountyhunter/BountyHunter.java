package com.koios.game.content.bountyhunter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koios.GameService;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendEntityHintIconPacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.util.NumberUtil;

/**
 * Handles all the player's bounty hunter data and updates.
 *
 * @author Gabriel | Wolfs Darker
 */
public class BountyHunter {

	/**
	 * The player's hunter information ( current and highest killstreak ).
	 */
	private final int[] hunter_info = new int[2];

	/**
	 * The player's 3 last killed targets.
	 */
	private final List<String> last_targets = new CopyOnWriteArrayList<String>();

	/**
	 * The time the player left wilderness.
	 */
	private long left_wilderness;

	/**
	 * The bounty hunter.
	 */
	private final Player player;

	/**
	 * The player's rogue information ( current and highest killstreak ).
	 */
	private final int[] rogue_info = new int[2];
	/**
	 * The bounty to be hunted.
	 */
	private String target;

	/**
	 * The amount of targets the player avoided.
	 */
	private int targets_lost;

	/**
	 * Constructor of the bounty hunter.
	 *
	 * @param player the bounty hunter
	 */
	public BountyHunter(Player player) {
		this.player = player;
	}

	/**
	 * Adds a target to the killed list
	 *
	 * @param target the player to be added
	 */
	public void addTargetToList(String t) {
		if (t == null) {
			return;
		}

		for (int i = 0; i < last_targets.size(); i++) {
			String name = last_targets.get(i);

			if (name == null) {
				last_targets.remove(i);
				continue;
			}

			if (name.toLowerCase().equals(t.toLowerCase())) {
				return;
			}
		}

		last_targets.add(t);

		if (last_targets.size() > 3) {
			last_targets.remove(0);
		}
	}

	/**
	 * Updates the player and the targets info when the player leaves the wilderness ( for longer than 2 mins ) or logs out.
	 */
	public void avoidTarget() {
		if (target == null) {
			return;
		}

		targets_lost += 1;
		addTargetToList(target);

		if (hasTarget()) {
			getTarget().sendMessage("@red@Your target is no longer avaliable.");
			getTarget().getBountyHunter().reset();
		}

		reset();
	}

	/**
	 * Calculates the target's wealth and displays the interface config sprite.
	 *
	 * @return the wealth type ( low, medium, high, etc )
	 */
	public String calculateTargetWealth() {
		Player t = getTarget();
		player.getSession().send(new SendConfigPacket(876, 0));

		for (int i = 0; i < 6; i++) {
			player.getSession().send(new SendConfigPacket(877 + i, 1));
		}

		if (t == null) {
			player.getSession().send(new SendConfigPacket(876, 1));
			return "---";
		}

		int carried_wealth = 0;

		for (int i = 0; i < t.getInventory().size(); i++) {
			if (t.getInventory().get(i) != null) {
				carried_wealth += t.getInventory().get(i).getDefinition().getValue();
			}
		}

		for (int i = 0; i < t.getEquipment().size(); i++) {
			if (t.getEquipment().get(i) != null) {
				carried_wealth += t.getEquipment().get(i).getDefinition().getValue();
			}
		}

		if (carried_wealth > 10_000_000) {
			player.getSession().send(new SendConfigPacket(881, 0));
			return "Wealth: V. High";
		} else if (carried_wealth >= 1_000_000 && carried_wealth < 10_000_000) {
			player.getSession().send(new SendConfigPacket(880, 0));
			return "Wealth: High";
		} else if (carried_wealth >= 250_000 && carried_wealth < 1_000_000) {
			player.getSession().send(new SendConfigPacket(879, 0));
			return "Wealth: Medium";
		} else if (carried_wealth >= 50_000 && carried_wealth < 250_000) {
			player.getSession().send(new SendConfigPacket(878, 0));
			return "Wealth: Low";
		} else {
			player.getSession().send(new SendConfigPacket(877, 0));
			return "Wealth: V. Low";
		}
	}

	/**
	 * Returns the player hunter information ( current and highest target killstreak )
	 *
	 * @return the information
	 */
	public int[] getHunterInfo() {
		return hunter_info;
	}

	/**
	 * Returns the last 3 targets killed by the player.
	 *
	 * @return the last 3 targets
	 */
	public List<String> getLastTargets() {
		return last_targets;
	}

	/**
	 * Returns the time the player left the wilderness.
	 *
	 * @return the time
	 */
	public long getLeftWildernessTime() {
		return left_wilderness;
	}

	/**
	 * Returns the player rogue information ( current and highest killstreak of players with targets that doesnt match with this player)
	 *
	 * @return the information
	 */
	public int[] getRogueInfo() {
		return rogue_info;
	}

	/**
	 * Returns the target in player instance.
	 *
	 * @return the target
	 */
	public Player getTarget() {
		if (target == null) {
			return null;
		}

		Optional<Player> p = GameService.getWorld().getByUsername(target);
		return p.isPresent() ? p.get() : null;
	}

	/**
	 * Returns the target information ( location and combat level )
	 *
	 * @return the location and combat level
	 */
	public String getTargetInformation() {
		Player t = getTarget();

		if (t == null) {
			return "Level: -----";
		}

		String location = "Safe";
		String color = "@gr2@";

		if (t.inWild()) {
			int level = t.wildLevel;
			location = "Lvl " + (level <= 3 ? "1" : level - 3) + "-" + (level + 3);

			if (t.getSkill().getCombatLevel(false) > player.getSkill().getCombatLevel(false) + t.wildLevel
					|| player.getSkill().getCombatLevel(false) > t.getSkill().getCombatLevel(false) + t.wildLevel) {
				color = "@red@";
			}
		}

		location += ", Cmb " + t.getSkill().getCombatLevel(false);
		return color + location;
	}

	/**
	 * Returns the target name.
	 *
	 * @return the name
	 */
	public String getTargetName() {
		return target;
	}

	/**
	 * Returns the amount of targets the player avoided.
	 *
	 * @return the amount
	 */
	public int getTargetsLost() {
		return targets_lost;
	}

	/**
	 * Checks if the player has a target.
	 *
	 * @return if he as a target
	 */
	public boolean hasTarget() {
		return target != null && GameService.getWorld().getByUsername(target).isPresent();
	}

	/**
	 * Rewards the player after killing a player.
	 *
	 * @param player_name
	 * @param x
	 * @param y
	 * @return
	 */
	@SuppressWarnings("unlikely-arg-type")
	public boolean killTarget(Player t) {
		if (t == null || target == null || t.getName().equals(player.getName())) {
			return false;
		}

		if (!target.equals(t.getName())) {
			if (t.getBountyHunter().hasTarget() && !t.getBountyHunter().getTarget().equals(player.getName())) {
				rogue_info[0] += 1;

				if (rogue_info[0] > rogue_info[1]) {
					rogue_info[1] = rogue_info[0];
				}

				reset();
			}

			return false;
		}

		player.sendMessage("@red@You've killed your target: " + t.getName() + "!");
		GroundItemManager.spawnNew(player, BountyHunterEmblem.getBest(player, true).getItemId(), t.getX(), t.getY(), 1);
		addTargetToList(target);
		hunter_info[0] += 1;

		if (hunter_info[0] > hunter_info[1]) {
			hunter_info[1] = hunter_info[0];
		}

		reset();
		t.getBountyHunter().reset();
		return true;
	}

	/**
	 * Resets the player's data.
	 */
	public void reset() {
		target = null;
		updateInterface();
		left_wilderness = 0;
		player.getSession().send(new SendEntityHintIconPacket(null));
	}

	/**
	 * Searches for a new possible target inside the bounty hunters within his combat level and inside wilderness.
	 *
	 * @param possible_players the possible targets
	 */
	public void searchNextTarget(List<String> possible_players) {
		if (player.isInattackable()) {
			// Super administrators in inattackable mode do not get targets.
			return;
		}

		for (int i = 0; i < possible_players.size(); i++) {
			String name = possible_players.get(i);

			if (name == null) {
				possible_players.remove(i);
				continue;
			}

			Optional<Player> op = GameService.getWorld().getByUsername(name);
			Player p = op.orElse(null);

			if (p == null || p.getIndex() == player.getIndex() || p.getName().equals(player.getName()) || last_targets.contains(name) || p.getBountyHunter().hasTarget()
					|| p.getSkill().getCombatLevel(false) > player.getSkill().getCombatLevel(false) + 6
					|| p.getSkill().getCombatLevel(false) + 6 < player.getSkill().getCombatLevel(false)) {
				possible_players.remove(i);
				continue;
			}
		}

		if (possible_players.isEmpty() || possible_players.size() == 0) {
			player.sendMessage("No targets avaliable.");
			return;
		}

		if (targets_lost > 0 && NumberUtil.inclusiveRandom(targets_lost) > targets_lost / 2) {
			return;
		}

		Optional<Player> op = GameService.getWorld().getByUsername(possible_players.get(NumberUtil.inclusiveRandom(possible_players.size() - 1)));
		Player p = op.orElse(null);

		if (p == null || p.getName().equals(player.getName())) {
			player.sendMessage("No targets avaliable.");
			return;
		}

		if (p.isInattackable()) {
			// Cannot assign super administrator with inattackable mode active.
			return;
		}

		player.getBountyHunter().setTarget(p.getName());
		p.getBountyHunter().setTarget(player.getName());
		player.getSession().send(new SendEntityHintIconPacket(p));
		p.getSession().send(new SendEntityHintIconPacket(player));
	}

	/**
	 * Set the time the player left the wilderness.
	 *
	 * @param l
	 */
	public void setLeftWilderness(long l) {
		left_wilderness = l;
	}

	/**
	 * Sets the player new target.
	 *
	 * @param s the new target
	 */
	private void setTarget(String s) {
		target = s;
		player.sendMessage("@red@You've been assigned a target: " + target);
		updateInterface();
	}

	/**
	 * Sets the amount of targets the player avoided.
	 *
	 * @param i the amount
	 */
	public void setTargetsLost(int i) {
		targets_lost = i;
	}

	/**
	 * Checks if the time the player left wilderness is over.
	 *
	 * @param l time to be checked ( seconds * 1000 )
	 * @return if it is over
	 */
	public boolean timeOver(long l) {
		return left_wilderness - System.currentTimeMillis() <= l;
	}

	/**
	 * Updates the bounty hunter interface.
	 */
	public void updateInterface() {
		player.getSession().send(new SendStringPacket("" + rogue_info[0], 23310));
		player.getSession().send(new SendStringPacket("" + rogue_info[1], 23311));
		player.getSession().send(new SendStringPacket("" + hunter_info[0], 23312));
		player.getSession().send(new SendStringPacket("" + hunter_info[1], 23313));
		player.getSession().send(new SendStringPacket(target == null ? "None" : target, 23307));
		player.getSession().send(new SendStringPacket(calculateTargetWealth(), 23305));
		player.getSession().send(new SendStringPacket(getTargetInformation(), 23308));
	}

}
