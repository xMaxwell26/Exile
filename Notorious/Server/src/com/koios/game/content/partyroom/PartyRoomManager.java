package com.koios.game.content.partyroom;

import java.util.Optional;

import com.koios.game.model.Animation;
import com.koios.game.model.Area;
import com.koios.game.model.SquareArea;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.Container.Type;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendInventoryOverlayPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;

/**
 * The class that will manage the functions of the Falador party room.
 *
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 */
public final class PartyRoomManager {

	/**
	 * The locations that all items will fall into.
	 */
	public static final Area[] LOCATIONS = { new SquareArea(3041, 3379, 3050, 3383, 0), new SquareArea(3041, 3371, 3050, 3377, 0), new SquareArea(3049, 3372, 3055, 3374, 0),
			new SquareArea(3037, 3371, 3039, 3375, 0) };

	/**
	 * The container that contains all of the chest items.
	 */
	public static final Container CONTAINER = new Container(Type.STANDARD, 80);

	/**
	 * The current activity that the party room is hosting.
	 */
	private static Optional<LeverOption> option = Optional.empty();

	/**
	 * The default constructor.
	 *
	 * @throws InstantiationError if this class is instantiated.
	 */
	private PartyRoomManager() {
		throw new InstantiationError();
	}

	/**
	 * Opens the Falador party room chest for {@code player}.
	 *
	 * @param player the player that will open the party room chest.
	 */
	public static void openChest(Player player) {
		ActionSender.sendInterfaceItems(player, 2273, CONTAINER.toArray());
		ActionSender.sendInterfaceItems(player, 2274, player.chestDeposit.toArray());
		ActionSender.sendInterfaceItems(player, 5064, player.getInventory().toArray());
		player.getSession().send(new SendInventoryOverlayPacket(2156, 5063));
		player.partyChestOpen = true;
	}

	/**
	 * The method that deposits {@code item} into the container of queued chest items.
	 *
	 * @param player the player attempting to select this item for deposition.
	 * @param item the item that is being queued for deposition.
	 * @return {@code true} if the item was successfully queued, {@code false} otherwise.
	 */
	public static boolean queueItem(Player player, Item item) {
		if (item == null || item.getId() < 1 || item.getCount() < 1) {
			return false;
		}

		if (player.chestDeposit.size() == 8) {
			if (!player.chestDeposit.contains(item.getId()) || !item.getDefinition().isStackable()) {
				player.sendMessage("You cannot add any more items to the deposit queue!");
				return false;
			}
		}

		if (!player.getInventory().contains(item.getId())) {
			return false;
		}

		int count = player.getInventory().getCount(item.getId());

		if (count < item.getCount()) {
			item.setCount(count);
		}

		if (!item.getDefinition().isStackable()) {
			int slots = player.chestDeposit.freeSlots();

			if (slots < item.getCount()) {
				item.setCount(slots);
			}
		}

		player.getInventory().remove(item);
		ActionSender.sendInterfaceItems(player, 5064, player.getInventory().toArray());
		player.chestDeposit.add(item);
		ActionSender.sendInterfaceItems(player, 2274, player.chestDeposit.toArray());
		return true;
	}

	/**
	 * The method that withdraws {@code item} from the container of queued chest items.
	 *
	 * @param player the player attempting to withdraw this item.
	 * @param item the item that is being removed from the queue.
	 * @return {@code true} if the item was successfully withdrawn, {@code false} otherwise.
	 */
	public static boolean unqueueItem(Player player, Item item) {
		if (item == null || item.getId() < 1 || item.getCount() < 1) {
			return false;
		}

		if (player.getInventory().freeSlots() == 0) {
			if (!player.getInventory().hasRoomFor(item)) {
				player.sendMessage("You do not have enough space in your inventory.");
				return false;
			}
		}

		if (!player.chestDeposit.contains(item.getId())) {
			return false;
		}

		int count = player.chestDeposit.getCount(item.getId());

		if (count < item.getCount()) {
			item.setCount(count);
		}

		if (!item.getDefinition().isStackable()) {
			int slots = player.getInventory().freeSlots();
			if (slots < item.getCount()) {
				item.setCount(slots);
			}
		}

		player.getInventory().add(item);
		ActionSender.sendInterfaceItems(player, 5064, player.getInventory().toArray());
		player.chestDeposit.remove(item);
		ActionSender.sendInterfaceItems(player, 2274, player.chestDeposit.toArray());
		return true;
	}

	/**
	 * The method that polls all of the queued chest items for {@code player}.
	 *
	 * @param player the player polling the queued items.
	 * @return {@code true} if the item was successfully withdrawn, {@code false} otherwise.
	 */
	public static void pollQueuedItems(Player player) {
		if (player.chestDeposit.size() == 0) {
			player.sendMessage("You do not have any queued items to deposit!");
			return;
		}

		int slot = 0;

		for (Item item : player.chestDeposit.toArray()) {
			if (CONTAINER.hasRoomFor(item)) {
				CONTAINER.add(item);
				player.chestDeposit.set(slot, null);
			}

			slot++;
		}

		ActionSender.sendInterfaceItems(player, 2273, CONTAINER.toArray());
		ActionSender.sendInterfaceItems(player, 2274, player.chestDeposit.toArray());
	}

	/**
	 * The method executed when the Falador party room lever is executed.
	 *
	 * @param player the player that pulled the lever.
	 * @param option the event to fire for this party.
	 */
	public static void pullLever(Player player, LeverOption newOption) {
		if (option.isPresent()) {
			player.getDH().sendPlayerChat1("I should try again when the current event is over.");
			return;
		}

		if (player.chestDeposit.size() == 0 && newOption == LeverOption.DROP_PARTY) {
			player.getDH().sendPlayerChat1("There don't seem to be any items in the chest.");
			return;
		}

		if (!player.getInventory().contains(new Item(995, newOption.getAmount()))) {
			player.getDH().sendPlayerChat1("I don't have enough coins to activate this event.");
			return;
		}

		player.getInventory().remove(new Item(995, newOption.getAmount()));
		player.getSession().send(new SendRemoveInterfacePacket());
		player.playAnimation(new Animation(881));
		option = Optional.of(newOption);
		option.get().getActivity().run();
		player.sendMessage("You pull the lever...");
	}

	/**
	 * The method that cancels the event currently taking place.
	 */
	public static void cancelEvent() {
		option = Optional.empty();
	}

}
