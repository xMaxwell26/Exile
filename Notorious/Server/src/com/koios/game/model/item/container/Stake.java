package com.koios.game.model.item.container;

import com.koios.GameService;
import com.koios.game.content.minigames.duel.DuelArenaHandler;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingStatus;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendStringPacket;

public class Stake {

	public static boolean removeItem(Player player, int itemId, int amount) {

		if (!player.getDuel().getStakeInventory().contains(itemId) || !player.getDuel().isDueling()) {
			return false;
		}

		if (player.getInterfaceState().getCurrentInterface() != 37888) {
			return false;
		}

		Player playerToDuel = GameService.getWorld().getByUsername(player.getDuel().getDuelingWith()).get();

		if (playerToDuel == null) {
			return false;
		}

		if (player.getDuel().getStatus() != DuelingStatus.WAITING_FIRST_ACCEPT && player.getDuel().getStatus() != DuelingStatus.ACCEPTED_FIRST_INTERFACE
				|| playerToDuel.getDuel().getStatus() != DuelingStatus.WAITING_FIRST_ACCEPT && playerToDuel.getDuel().getStatus() != DuelingStatus.ACCEPTED_FIRST_INTERFACE) {
			DuelArenaHandler.declineDuel(player, true);
			return false;
		}

		if (player.getDuel().getStakeInventory().getItemAmount(itemId) < amount) {
			amount = player.getDuel().getStakeInventory().getItemAmount(itemId);
			if (amount == 0 || player.getDuel().getStakeInventory().getItemAmount(itemId) < amount) {
				return false;
			}
		}

		if (player.getInventory().add(new Item(itemId, amount))) {
			player.getDuel().getStakeInventory().remove(new Item(itemId, amount));
		}

		player.getSession().send(new SendStringPacket("", 6684));
		playerToDuel.getSession().send(new SendStringPacket("", 6684));

		player.getDuel().setStatus(DuelingStatus.WAITING_FIRST_ACCEPT);
		playerToDuel.getDuel().setStatus(DuelingStatus.WAITING_FIRST_ACCEPT);

		DuelArenaHandler.updateStakedItems(player, playerToDuel);

		player.setAttribute("stake_change_delay", System.currentTimeMillis());

		return true;
	}

	public static boolean stakeItem(Player player, int itemId, int amount, int slot) {
		if (!player.getInventory().contains(itemId) || !player.getDuel().isDueling()) {
			return false;
		}

		if (player.getInterfaceState().getCurrentInterface() != 37888) {
			return false;
		}

		if (new Item(itemId).getDefinition().isUntradeable()) {
			player.sendMessage("This item is currently untradeable and cannot be staked.");
			return false;
		}

		Player playerToDuel = GameService.getWorld().getByUsername(player.getDuel().getDuelingWith()).get();

		if (playerToDuel == null) {
			return false;
		}

		if (player.getDuel().getStatus() != DuelingStatus.WAITING_FIRST_ACCEPT && player.getDuel().getStatus() != DuelingStatus.ACCEPTED_FIRST_INTERFACE
				|| playerToDuel.getDuel().getStatus() != DuelingStatus.WAITING_FIRST_ACCEPT && playerToDuel.getDuel().getStatus() != DuelingStatus.ACCEPTED_FIRST_INTERFACE) {
			DuelArenaHandler.declineDuel(player, true);
			return false;
		}

		if (player.getInventory().getItemAmount(itemId) < amount) {
			amount = player.getInventory().getItemAmount(itemId);

			if (amount == 0 || player.getInventory().getItemAmount(itemId) < amount) {
				return false;
			}
		}

		if (player.getDuel().getStakeInventory().add(new Item(itemId, amount))) {
			player.getInventory().remove(new Item(itemId, amount));
		}

		DuelArenaHandler.updateStakedItems(player, playerToDuel);

		player.getSession().send(new SendStringPacket("", 6684));
		playerToDuel.getSession().send(new SendStringPacket("", 6684));

		player.getDuel().setStatus(DuelingStatus.WAITING_FIRST_ACCEPT);
		playerToDuel.getDuel().setStatus(DuelingStatus.WAITING_FIRST_ACCEPT);
		player.setAttribute("stake_change_delay", System.currentTimeMillis());
		return true;
	}

}
