package com.koios.game.model.item.container;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.koios.Constants;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.RequestManager;
import com.koios.game.model.player.RequestManager.RequestType;
import com.koios.network.out.impl.SendInventoryOverlayPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.util.Logger;

/**
 * Handle player's trading
 *
 * @author Gabriel | Wolfs Darker
 */
public final class Trade {

	/**
	 * The player inventory interface.
	 */
	public static final int PLAYER_INVENTORY_INTERFACE = 3322;

	/**
	 * The Size of Trade.
	 */
	public static final int SIZE = 28;

	/**
	 * The shop inventory interface.
	 */
	public static final int TRADE_INTERFACE = 3415;

	/**
	 * Offer a item to the trade
	 */
	public static void addItemToOffer(Player player, int itemId, int slot, int amount) {
        if(player.getInventory().get(slot)!=null && player.getInventory().get(slot).hasRoll()){player.sendMessage("Rolled gear cannot use this legacy transfer yet.");return;}
		if (player.getInventory().get(slot) == null || player.getInventory().get(slot).getId() != itemId) {

			return;
		}

		if (player.getRequestManager().getAcquaintance() == null) {
			exitOutOfTrade(player, false);
			return;
		}

		if (player.getInterfaceState().getCurrentInterface() != 3323 || player.getRequestManager().getAcquaintance().getInterfaceState().getCurrentInterface() != 3323) {
			return;
		}

		if (player.getInventory().get(slot).getDefinition().isUntradeable() && !player.isSuperAdministrator()) {
			player.sendMessage("This item cannot be traded.");
			return;
		}

		Item item = new Item(itemId);
		int transferAmount = player.getInventory().get(slot).getDefinition().isStackable() ? player.getInventory().getCount(itemId, slot) : player.getInventory().getCount(itemId);

		if (amount > transferAmount) {
			amount = transferAmount;
		}

		if (amount == -1) {
			amount = transferAmount;
		}

		if (!item.getDefinition().isStackable() && !item.getDefinition().isNoted()
				&& player.getRequestManager().getAcquaintance().getInventory().freeSlots() < player.getOther().getCapacity() - player.getOther().freeSlots() + amount) {
			player.sendMessage("Not enough inventory space to complete this trade.");
			return;
		}

		if (itemId == 995 && player.getRequestManager().getAcquaintance().getInventory().contains(995)) {
			long l = amount + player.getInventory().getCount(995);

			if (l >= Constants.MAX_COUNT || l < 0) {
				player.sendMessage("The other player can't hold that amount of cash!");
				return;
			}
		}

		if (ItemDefinition.forId(itemId).isStackable()) {
			player.getInventory().remove(slot, new Item(itemId, amount));
		} else {
			player.getInventory().remove(new Item(itemId, amount));
		}

		player.getOther().add(new Item(itemId, amount));
		player.getSession().send(new SendStringPacket("", 3431));
		player.getRequestManager().getAcquaintance().getSession().send(new SendStringPacket("", 3431));
		player.getRequestManager().setRequestStage(1);
		player.getRequestManager().getAcquaintance().getRequestManager().setRequestStage(1);
		sendFreeSlots(player, player.getRequestManager().getAcquaintance());
	}

	/**
	 * Formats a number to string ( decimal format )
	 */
	public static String amountToString(int price) {
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setGroupingSeparator(',');
		df.setDecimalFormatSymbols(dfs);
		return df.format(price);
	}

	/**
	 * Exits out of trading interfaces upon calling.
	 */
	public static void exitOutOfTrade(Player player, boolean complete) {
		if (player == null || player.getRequestManager().getRequestType() != RequestManager.RequestType.TRADE
				|| player.getRequestManager().getState() != RequestManager.RequestState.PARTICIPATING || player.getRequestManager().getAcquaintance() == null) {
			return;
		}

		Player[] traders = { player, player.getRequestManager().getAcquaintance() };
		int count = 0;

		if (complete) {
			Logger.writeLogs(traders[0], traders[1], traders[0].getRequestManager().getAcquaintance().getOther().toArray(),
					traders[1].getRequestManager().getAcquaintance().getOther().toArray(), "tradelogs");

			Logger.writeLogs(traders[1], traders[0], traders[1].getRequestManager().getAcquaintance().getOther().toArray(),
					traders[0].getRequestManager().getAcquaintance().getOther().toArray(), "tradelogs");
		}

		for (Player p : traders) {
			Container items = complete ? p.getRequestManager().getAcquaintance().getOther() : p.getOther();

			for (Item i : items.toArray()) {
				if (i != null) {
					p.getInventory().add(i);
				}
			}

			items.clear();

			if (count == 1) {
				traders[0].getOther().clear();
				traders[1].getOther().clear();
			}

			p.getRequestManager().setAcquaintance(null);
			p.getRequestManager().setRequestStage(-1);
			p.getRequestManager().setRequestType(null);
			p.getRequestManager().setState(null);
			p.getSession().send(new SendRemoveInterfacePacket());
			p.setFaceIndex(-1, false);
			p.setIsTrading(false);

			if (!complete) {
				p.sendMessage(p == player ? "You declined the trade." : "The other person has declined the trade.");
			} else {
				p.sendMessage("Accepted trade.");
			}
		}
	}

	/**
	 * Send the items list into a list ( string )
	 */
	public static String itemString(Item[] items, boolean space) {
		String sendTrade = "Absolutely nothing!";

		if (items == null) {
			return sendTrade;
		}

		String sendAmount;
		int count = 0;

		for (Item item : items) {
			if (item == null || item.getId() < 0) {
				continue;
			}

			if (item.getCount() >= 1000 && item.getCount() < 1000000) {
				sendAmount = "@cya@" + item.getCount() / 1000 + "K@whi@(" + amountToString(item.getCount()) + ")";
			} else if (item.getCount() >= 1000000) {
				sendAmount = "@gre@" + item.getCount() / 1000000 + " million@whi@(" + amountToString(item.getCount()) + ")";
			} else {
				sendAmount = "" + amountToString(item.getCount());
			}

			if (count == 0) {
				sendTrade = item.getDefinition().getName();

				if (item.getDefinition().isStackable()) {
					sendTrade += " x " + sendAmount;
				}
			} else {
				sendTrade += (space ? "\\n" : " ") + item.getDefinition().getName();

				if (item.getDefinition().isStackable()) {
					sendTrade += " x " + sendAmount;
				}
			}

			count++;
		}

		return sendTrade;
	}

	/**
	 * This sends the second interface during trading.
	 */
	public static void openConfirmInterface(Player[] trader) {
		for (Player player : trader) {
			player.getRequestManager().setRequestType(null);

			player.getSession().send(new SendInventoryOverlayPacket(3443, 3213));

			player.getSession().send(new SendStringPacket(player.getRequestManager().getAcquaintance().getName(), 3451));

			player.getRequestManager().setRequestType(RequestType.TRADE);
			player.getSession().send(new SendStringPacket(itemString(player.getOther().toArray(), true), 3557));

			player.getSession().send(new SendStringPacket(itemString(player.getRequestManager().getAcquaintance().getOther().toArray(), true), 3558));

			player.getSession().send(new SendStringPacket("Are you sure you want to make this trade?", 3535));
		}
	}

	/**
	 * Opens the First Trading Interface.
	 */
	public static void openFirstInterface(Player[] trader) {
		for (Player player : trader) {
			player.getRequestManager().setRequestType(RequestManager.RequestType.TRADE);
			player.getRequestManager().setState(RequestManager.RequestState.PARTICIPATING);

			player.getRequestManager().setRequestStage(1);

			player.getSession().send(new SendStringPacket("", 3431));

			player.getInterfaceState().addListener(player.getOther(), new InterfaceContainerListener(player, 3415));
			player.getInterfaceState().addListener(player.getInventory(), new InterfaceContainerListener(player, 3322));
			player.getInterfaceState().addListener(player.getRequestManager().getAcquaintance().getOther(), new InterfaceContainerListener(player, 3416));

			player.getSession().send(new SendStringPacket("Trading With: " + player.getRequestManager().getAcquaintance().getName(), 3417));

			player.getSession()
					.send(new SendStringPacket(
							player.getRequestManager().getAcquaintance().getName() + " has " + player.getRequestManager().getAcquaintance().getInventory().freeSlots()
									+ " \\nfree inventory\\n         " + (player.getRequestManager().getAcquaintance().getInventory().freeSlots() > 1 ? "slots" : "slot"),
							3418));

			player.getSession().send(new SendInventoryOverlayPacket(PLAYER_INVENTORY_INTERFACE + 1, TRADE_INTERFACE - 94));
			player.setIsTrading(true);
		}
	}

	/**
	 * Removes a offer from the trade
	 */
	public static void removeItemFromOffer(Player player, int itemId, int slot, int amount) {

		if (player.getOther().get(slot) == null || player.getOther().get(slot).getId() != itemId) {
			return;
		}

		if (player.getRequestManager().getAcquaintance() == null) {
			exitOutOfTrade(player, false);
			return;
		}

		if (player.getInterfaceState().getCurrentInterface() != 3323 || player.getRequestManager().getAcquaintance().getInterfaceState().getCurrentInterface() != 3323) {
			return;
		}

		int transferAmount = player.getOther().get(slot).getDefinition().isStackable() ? player.getOther().getCount(itemId, slot) : player.getOther().getCount(itemId);

		if (amount > transferAmount) {
			amount = transferAmount;
		}

		if (amount == -1) {
			amount = transferAmount;
		}

		if (ItemDefinition.forId(itemId).isStackable()) {
			player.getOther().remove(slot, new Item(itemId, amount));
		} else {
			player.getOther().remove(new Item(itemId, amount));
		}

		player.getOther().shift();
		player.getInventory().add(new Item(itemId, amount));
		player.getSession().send(new SendStringPacket("", 3431));
		player.getRequestManager().getAcquaintance().getSession().send(new SendStringPacket("", 3431));
		player.getRequestManager().setRequestStage(1);
		player.getRequestManager().getAcquaintance().getRequestManager().setRequestStage(1);
		sendFreeSlots(player, player.getRequestManager().getAcquaintance());
	}

	/**
	 * Send the trader's free slots
	 */
	public static void sendFreeSlots(Player p, Player p2) {
		p2.getSession().send(new SendStringPacket(
				p.getName() + " has " + p.getInventory().freeSlots() + " \\nfree inventory\\n         " + (p.getInventory().freeSlots() > 1 ? "slots" : "slot"), 3418));
		p.getSession().send(new SendStringPacket(
				p2.getName() + " has " + p2.getInventory().freeSlots() + " \\nfree inventory\\n         " + (p2.getInventory().freeSlots() > 1 ? "slots" : "slot"), 3418));
	}

	/**
	 * Sends the ready messages to each player.
	 */
	public static void setReadyMessages(Player player) {
		if (player.getRequestManager().getRequestStage() == 1 && player.getRequestManager().getAcquaintance().getRequestManager().getRequestStage() == 1) {
			player.getRequestManager().setRequestStage(2);
			player.getSession().send(new SendStringPacket("Waiting for other player.", 3431));
			player.getRequestManager().getAcquaintance().getSession().send(new SendStringPacket("Other player has accepted.", 3431));
		} else if (player.getRequestManager().getRequestStage() == 1 && player.getRequestManager().getAcquaintance().getRequestManager().getRequestStage() == 2) {
			player.getRequestManager().setRequestStage(2);
			player.getSession().send(new SendStringPacket("", 3431));
			player.getRequestManager().getAcquaintance().getSession().send(new SendStringPacket("", 3431));
			openConfirmInterface(new Player[] { player, player.getRequestManager().getAcquaintance() });
		} else if (player.getRequestManager().getRequestStage() == 2 && player.getRequestManager().getAcquaintance().getRequestManager().getRequestStage() == 2) {
			if (player.getInventory().freeSlots() < player.getRequestManager().getAcquaintance().getOther().size()) {
				player.sendMessage("You do not have enough inventory space.");
				return;
			}

			player.getRequestManager().setRequestStage(3);
			player.getSession().send(new SendStringPacket("Waiting for other player.", 3535));
			player.getRequestManager().getAcquaintance().getSession().send(new SendStringPacket("Other player has accepted.", 3535));
		} else if (player.getRequestManager().getRequestStage() == 2 && player.getRequestManager().getAcquaintance().getRequestManager().getRequestStage() == 3) {
			if (player.getInventory().freeSlots() < player.getRequestManager().getAcquaintance().getOther().size()) {
				player.sendMessage("You do not have enough inventory space.");
				return;
			}

			player.getRequestManager().setRequestStage(3);
			player.getSession().send(new SendStringPacket("", 3431));
			player.getRequestManager().getAcquaintance().getSession().send(new SendStringPacket("", 3431));
			exitOutOfTrade(player, true);
		}
	}

}
