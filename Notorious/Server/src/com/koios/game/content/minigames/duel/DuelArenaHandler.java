package com.koios.game.content.minigames.duel;

import java.util.Optional;

import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.minigames.Minigame;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingStatus;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Location;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.InterfaceContainerListener;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.io.PlayerSaver;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendInventoryOverlayPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.task.Task;
import com.koios.util.Logger;
import com.koios.util.NumberUtil;

/**
 * Handle the duel arena actions.
 *
 * @author Gabriel | Wolfs Darker
 */
public class DuelArenaHandler {

	/**
	 * Checks if the player can duel.
	 */
	public static boolean canDuel(Player playerToDuel, DuelingStatus statusReq) {
		return playerToDuel.getDuel().getStatus() == statusReq;
	}

	/**
	 * Challenges another player to a duel battle.
	 */
	public static void challengePlayer(Player player, Player playerToDuel) {
		if (player == null || playerToDuel == null) {
			return;
		}

		if (!DuelArenaConstants.inDuelArena(new Location(player.getX(), player.getY(), player.getZ()))) {
			return;
		}

		if (player.getName().equals(playerToDuel.getName())) {
			return;
		}

		if (Location.getDistance(new Location(player.getX(), player.getY(), player.getZ()),
				new Location(playerToDuel.getX(), playerToDuel.getY(), playerToDuel.getZ())) > 2) {
			player.sendMessage("Please get closer to request a duel.");
			return;
		}

		if (!canDuel(playerToDuel, DuelingStatus.NONE) || playerToDuel.getInterfaceState().getCurrentInterface() > 0) {
			player.sendMessage("The other player is currently busy.");
			return;
		}

		if (player.getSummoning().hasFollower()) {
			player.sendMessage("You cannot duel with any follower.");
			return;
		}

		if (playerToDuel.getSummoning().hasFollower()) {
			player.sendMessage("The other player has a follower.");
			return;
		}

		if (DuelArenaConstants.insideDuelArena(new Location(player.getX(), player.getY(), player.getZ()))) {
			player.sendMessage("You can only challenge people outside the arena.");
			return;
		}

		player.getDuel().setDuelingWith(playerToDuel.getName());
		player.getDuel().setDuelRequested(true);

		if (playerToDuel.getDuel().requestedDuel(player.getName())) {
			openDuelInterface(player);
			openDuelInterface(playerToDuel);
		} else {
			player.sendMessage("You have sent a duel request to " + playerToDuel.getName() + ".");
			playerToDuel.sendMessage(player.getName() + ":duelreq:");
		}
	}

	/**
	 * Confirms the duel and loot the second screen.
	 */
	public static void confirmDuel(Player player) {
		if (player.getDuel().getDuelingWith() == null) {
			return;
		}

		Optional<Player> p2 = GameService.getWorld().getByUsername(player.getDuel().getDuelingWith());
		Player playerToDuel = !p2.isPresent() ? null : p2.get();

		if (playerToDuel == null) {
			return;
		}

		String itemId = "";

		for (Item item : player.getDuel().getStakeInventory().toArray()) {
			if (item == null || item.getCount() <= 0 || item.getId() <= 0) {
				continue;
			}

			ItemDefinition def = ItemDefinition.forId(item.getId());

			if (def.isStackable() || def.isNoted()) {
				itemId += def.getName() + " x " + NumberUtil.format(item.getCount()) + "\\n";
			} else {
				itemId += def.getName() + "\\n";
			}
		}

		player.getSession().send(new SendStringPacket("" + itemId, 6516));
		itemId = "";

		for (Item item : playerToDuel.getDuel().getStakeInventory().toArray()) {
			if (item == null || item.getCount() <= 0 || item.getId() <= 0) {
				continue;
			}

			ItemDefinition def = ItemDefinition.forId(item.getId());

			if (def.isStackable() || def.isNoted()) {
				itemId += def.getName() + " x " + NumberUtil.format(item.getCount()) + "\\n";
			} else {
				itemId += def.getName() + "\\n";
			}
		}

		player.getSession().send(new SendStringPacket("" + itemId, 6517));
		player.getSession().send(new SendStringPacket("", 8242));

		for (int i = 8238; i <= 8253; i++) {
			player.getSession().send(new SendStringPacket("", i));
		}

		player.getSession().send(new SendStringPacket("Hitpoints will be restored.", 8250));
		player.getSession().send(new SendStringPacket("Boosted stats will be restored.", 8238));

		if (player.getDuel().isRuleActive(DuelingRules.OBSTACLES)) {
			player.getSession().send(new SendStringPacket("There will be obstacles in the arena.", 8239));
		}

		player.getSession().send(new SendStringPacket("", 8240));
		player.getSession().send(new SendStringPacket("", 8241));

		for (int lineNumber = 8242, i = 0; i < player.getDuel().getRules().length; i++) {
			if (player.getDuel().getRules()[i]) {
				player.getSession().send(new SendStringPacket(DuelingRules.forId(i).toString(), lineNumber++));
			}
		}

		player.getSession().send(new SendStringPacket("", 6571));
		player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, 3322));
		player.getSession().send(new SendInventoryOverlayPacket(6412, 3321));
	}

	/**
	 * Declines the duel.
	 */
	public static void declineDuel(Player player, boolean tellOther) {
		if (player.getDuel().getStatus().ordinal() > 4) {
			return;
		}

		if (tellOther && player.getDuel().getDuelingWith() != null) {
			Optional<Player> p = GameService.getWorld().getByUsername(player.getDuel().getDuelingWith());
			Player playerToDuel = !p.isPresent() ? null : p.get();

			if (playerToDuel == null || playerToDuel.getDuel().getStatus() == DuelingStatus.WON_DUEL) {
				return;
			}

			playerToDuel.sendMessage("The other player declined the duel.");
			DuelArenaHandler.declineDuel(playerToDuel, false);
		}

		for (Item item : player.getDuel().getStakeInventory().toArray()) {
			if (item == null || item.getCount() <= 0) {
				continue;
			}

			if (!player.getInventory().add(item)) {
				Bank.addToBank(player, item);
			}
		}

		player.getDuel().reset();
		player.getSession().send(new SendRemoveInterfacePacket());
	}

	/**
	 * Restores the defeated player.
	 */
	public static void duelDefeat(Player player) {
		player.specialEnergy = 100;
		player.getSkill().restoreAll();
		player.getDuel().getDuelData()[1]++;
		player.getDuel().reset();
		player.getMinigame().resetMinigame();
		player.teleport(new Location(3368 + NumberUtil.random(5), 3267 + NumberUtil.random(3), 0));
		PlayerSaver.save(player);
	}

	/**
	 * Displays the victory painel.
	 */
	public static void duelVictory(Player player) {
		player.getDuel().setStatus(DuelingStatus.WON_DUEL);
		player.sendMessage("You won the duel! Congratulations!");
		player.specialEnergy = 100;
		player.getSkill().restoreAll();
		player.teleport(new Location(3368 + NumberUtil.random(5), 3267 + NumberUtil.random(3), 0));

		if (player.getDuel().getDuelingWith() == null) {
			player.getSession().send(new SendStringPacket("Disconnected", 6840));
			player.getSession().send(new SendStringPacket("3", 6839));
			player.getInterfaceState().openInterface(6733);
			declineDuel(player, false);
			return;
		}

		Optional<Player> p = GameService.getWorld().getByUsername(player.getDuel().getDuelingWith());
		Player duelingWith = !p.isPresent() ? null : p.get();

		if (duelingWith == null) {
			player.getSession().send(new SendStringPacket("Disconnected", 6840));
			player.getSession().send(new SendStringPacket("3", 6839));
			player.getInterfaceState().openInterface(6733);
			declineDuel(player, false);
			return;
		}

		ActionSender.sendInterfaceItems(player, 6822, duelingWith.getDuel().getStakeInventory().toArray());
		player.getSession().send(new SendStringPacket(duelingWith.getName(), 6840));
		player.getSession().send(new SendStringPacket("" + duelingWith.getSkill().getCombatLevel(true), 6839));
		player.getInterfaceState().openInterface(6733);

		Item[] items = duelingWith.getDuel().getStakeInventory().toArray();

		Logger.writeLogs(player, duelingWith, player.getDuel().getStakeInventory().toArray(), items, "duellogs");
		Logger.writeLogs(duelingWith, player, items, player.getDuel().getStakeInventory().toArray(), "duellogs");
		duelingWith.getDuel().getStakeInventory().clear();

		for (int i = 0; i < player.getDuel().getStakeInventory().size(); i++) {
			Item item = player.getDuel().getStakeInventory().get(i);

			if (item == null || item.getCount() <= 0) {
				continue;
			}

			if (!player.getInventory().add(item)) {
				System.out.println(item.getCount());
				GroundItemManager.spawnNew(player, item.getId(), player.getX(), player.getY(), item.getCount());
				player.sendMessage("You got no space on inventory! Pick your spoils from the floor.");
			}
		}

		player.getDuel().getStakeInventory().clear();

		for (Item item2 : items) {
			Item item = item2;

			if (item == null || item.getCount() <= 0) {
				continue;
			}

			if (!player.getInventory().add(item)) {
				System.out.println(item.getCount());
				GroundItemManager.spawnNew(player, item.getId(), player.getX(), player.getY(), item.getCount());
				player.sendMessage("You got no space on inventory! Pick your spoils from the floor");
			}
		}

		player.getDuel().getDuelData()[0]++;
		player.getDuel().reset();
		player.getMinigame().resetMinigame();
		AchievementManager.performed(player, Achievement.WIN_DUEL);
		PlayerSaver.save(player);
	}

	/**
	 * Handle the interface's buttons.
	 */
	public static boolean handleButtons(Player player, int button) {
		if (player.getDuel().getDuelingWith() == null) {
			return false;
		}

		if (player.getDuel().getStatus() == DuelingStatus.DUELING
				|| player.getInterfaceState().getCurrentInterface() <= 0) {
			return false;
		}

		if (DuelingRules.forButtonId(button) != null) {
			selectRule(player, button);
			return true;
		}

		Optional<Player> p = GameService.getWorld().getByUsername(player.getDuel().getDuelingWith());
		Player playerToDuel = !p.isPresent() ? null : p.get();

		if (playerToDuel == null) {
			return false;
		}

		switch (button) {
		case 148022:
			if (System.currentTimeMillis() - player.getAttribute("stake_change_delay", 0L) < 1000) {
				return false;
			}
			if (!player.getDuel().isDueling() && !canDuel(player, DuelingStatus.WAITING_FIRST_ACCEPT)
					&& !canDuel(player, DuelingStatus.ACCEPTED_FIRST_INTERFACE)) {
				return false;
			}

			if (player.getDuel().isRuleActive(DuelingRules.NO_MELEE)
					&& player.getDuel().isRuleActive(DuelingRules.NO_MAGIC)
					&& player.getDuel().isRuleActive(DuelingRules.NO_RANGED)) {
				player.sendMessage("You won't be able to attack the other player with the current rules.");
				break;
			}

			player.getDuel().setStatus(DuelingStatus.ACCEPTED_FIRST_INTERFACE);
			player.getSession().send(new SendStringPacket("Waiting for other player...", 38005));
			playerToDuel
					.getSession()
					.send(new SendStringPacket(
							playerToDuel.getDuel().getStatus() == DuelingStatus.ACCEPTED_FIRST_INTERFACE ? "Waiting for other player..."
									: "Other player has accepted.", 38005));

			if (player.getDuel().getStatus() == DuelingStatus.ACCEPTED_FIRST_INTERFACE
					&& playerToDuel.getDuel().getStatus() == DuelingStatus.ACCEPTED_FIRST_INTERFACE) {
				player.getDuel().setStatus(DuelingStatus.WAITING_SECOND_ACCEPT);
				playerToDuel.getDuel().setStatus(DuelingStatus.WAITING_SECOND_ACCEPT);
				confirmDuel(player);
				confirmDuel(playerToDuel);
			}

			return true;

		case 25120:
			if (!player.getDuel().isDueling() || player.getDuel().getStatus() != DuelingStatus.WAITING_SECOND_ACCEPT
					&& player.getDuel().getStatus() != DuelingStatus.ACCEPTED_SECOND_INTERFACE) {
				return false;
			}

			player.getDuel().setStatus(DuelingStatus.ACCEPTED_SECOND_INTERFACE);

			if (playerToDuel.getDuel().getStatus() == DuelingStatus.ACCEPTED_SECOND_INTERFACE
					&& player.getDuel().getStatus() == DuelingStatus.ACCEPTED_SECOND_INTERFACE) {
				Optional<Player> p2 = GameService.getWorld().getByUsername(player.getDuel().getDuelingWith());
				Player duelingWith = !p2.isPresent() ? null : p2.get();

				if (duelingWith == null) {
					DuelArenaHandler.declineDuel(playerToDuel, true);
					return false;
				}

				Minigame m = new DuelArenaMinigame(player, duelingWith);
				player.getMinigame().enterMinigame(m);
				duelingWith.getMinigame().enterMinigame(m);
				m.startMinigame();
			} else {
				player.getSession().send(new SendStringPacket("Waiting for other player...", 6571));
				playerToDuel.getSession().send(new SendStringPacket("Other player has accepted", 6571));
			}

			return true;
		}

		return false;
	}

	/**
	 * Open the options interface and setup the duel.
	 *
	 * @param player
	 */
	public static void openDuelInterface(Player player) {
		if (player.getDuel().getDuelingWith() == null) {
			return;
		}

		Optional<Player> p2 = GameService.getWorld().getByUsername(player.getDuel().getDuelingWith());
		Player dueling_with = !p2.isPresent() ? null : p2.get();

		if (dueling_with == null) {
			return;
		}

		player.getDuel().setStatus(DuelingStatus.WAITING_FIRST_ACCEPT);
		player.getDuel().clearRules();

		for (int i = 0; i < player.getDuel().getRules().length; i++) {
			player.getSession().send(new SendConfigPacket(DuelingRules.forId(i).getConfigId(), 0));
		}

		ActionSender.sendInterfaceItems(player, 13824, player.getEquipment().toArray());
		player.getSession().send(new SendStringPacket("Opponent: " + dueling_with.getName(), 38003));
		player.getSession().send(
				new SendStringPacket("Opponent's Level: @or3@" + dueling_with.getSkill().getCombatLevel(true), 38004));
		player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, 3322));
		player.getSession().send(new SendInventoryOverlayPacket(37888, 3321));
		updateStakedItems(player, dueling_with);
	}

	/**
	 * Selects a rule by the button.
	 */
	public static void selectRule(final Player player, int button) {
		if (player.getDuel().getDuelingWith() == null) {
			return;
		}

		Optional<Player> p2 = GameService.getWorld().getByUsername(player.getDuel().getDuelingWith());
		Player dueling_with = !p2.isPresent() ? null : p2.get();

		if (dueling_with == null) {
			return;
		}

		DuelingRules rule = DuelingRules.forButtonId(button);

		if (rule == null) {
			return;
		}

		int index = rule.ordinal();
		boolean ruleActive = player.getDuel().applyRule(rule);

		if (index >= 11 && rule.getEquipmentSlot() > -1) {
			if (player.getEquipment().get(rule.getEquipmentSlot()) != null) {
				player.getDuel().increaseSpaceReq(ruleActive ? 1 : -1);
			}

			if (dueling_with.getEquipment().get(rule.getEquipmentSlot()) != null) {
				dueling_with.getDuel().increaseSpaceReq(ruleActive ? 1 : -1);
			}
		}

		if (index == 16) {
			if (player.getEquipment().get(3) != null && player.getEquipment().get(3).getDefinition().isTwoHanded()) {
				player.getDuel().increaseSpaceReq(ruleActive ? 1 : -1);
			}

			if (dueling_with.getEquipment().get(3) != null
					&& dueling_with.getEquipment().get(3).getDefinition().isTwoHanded()) {
				dueling_with.getDuel().increaseSpaceReq(ruleActive ? 1 : -1);
			}
		}

		if (index >= 11) {

			if (player.getInventory().freeSlots() < player.getDuel().getSpaceReq()
					|| dueling_with.getInventory().freeSlots() < dueling_with.getDuel().getSpaceReq()) {
				player.sendMessage(player.getInventory().freeSlots() < player.getDuel().getSpaceReq() ? DuelArenaConstants.PLAYER_NO_SPACE
						: DuelArenaConstants.OPPONENT_NO_SPACE);
				player.getDuel().increaseSpaceReq(-1);
				dueling_with.getDuel().increaseSpaceReq(-1);
				player.getDuel().applyRule(rule);
				player.getSession().send(new SendConfigPacket(DuelingRules.forId(index).getConfigId(), 0));
				return;
			}

		}

		dueling_with.getDuel().setRules(player.getDuel().getRules());

		for (int i = 0; i < player.getDuel().getRules().length; i++) {
			player.getSession().send(
					new SendConfigPacket(DuelingRules.forId(i).getConfigId(), player.getDuel().getRules()[i] ? 1 : 0));
			dueling_with.getSession().send(
					new SendConfigPacket(DuelingRules.forId(i).getConfigId(), player.getDuel().getRules()[i] ? 1 : 0));
		}

		dueling_with.getSession().send(
				new SendStringPacket("@red@" + player.getName().replace("_", " ")
						+ " modified the rules! Duel must be accepted again.", 38005));
		player.getSession().send(new SendStringPacket("The rules were modified. Duel must be accepted again.", 38005));

		dueling_with.getDuel().setStatus(DuelingStatus.WAITING_FIRST_ACCEPT);
		player.getDuel().setStatus(DuelingStatus.WAITING_FIRST_ACCEPT);

		GameService.getTaskScheduler().schedule(new Task(6) {

			@Override
			public void execute() {
				dueling_with.getSession().send(new SendStringPacket("", 38005));
				player.getSession().send(new SendStringPacket("", 38005));
				stop();
			}

		});
	}

	/**
	 * Update staked items for both duelers.
	 */
	public static void updateStakedItems(Player player, Player duelingWith) {
		ActionSender.sendInterfaceItems(player, 38001, duelingWith.getDuel().getStakeInventory().toArray());
		ActionSender.sendInterfaceItems(player, 38000, player.getDuel().getStakeInventory().toArray());
		ActionSender.sendInterfaceItems(duelingWith, 38001, player.getDuel().getStakeInventory().toArray());
		ActionSender.sendInterfaceItems(duelingWith, 38000, duelingWith.getDuel().getStakeInventory().toArray());
	}

}
