package com.koios.game.model.item.container.bank;

import java.util.Arrays;
import java.util.Objects;

import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.ContainerListener;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.network.in.impl.PacketBankSearch;

/**
 * @author Michael | Chex
 */
public class BankContainerListener implements ContainerListener {

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * Creates the container listener.
	 *
	 * @param player The player.
	 */
	public BankContainerListener(Player player) {
		this.player = Objects.requireNonNull(player);
	}

	@Override
	public void onSingleItemChange(Container container, int slot) {
		if (!player.getBankField().isSearching()) {
			int tab = player.getBankField().getTabForSlot(slot);

			if (slot > Byte.MAX_VALUE) {
				onFullItemChange(container);
			} else {
				ActionSender.sendUpdateItem34(player, Bank.BANK_INVENTORY_INTERFACE + tab, slot - player.getBankField().getOffset(tab), container.get(slot));
			}
		} else {
			PacketBankSearch.searchFor(player, player.getBankField().getSearchText());
		}
	}

	@Override
	public void onItemGroupChange(Container container, int[] slots) {
	}

	@Override
	public void onFullItemChange(Container container) {
		if (!player.getBankField().isSearching()) {
			for (int tab = 0; tab < 9; tab++) {
				int from = player.getBankField().getOffset(tab);
				int to = from + player.getBankField().getTabAmounts()[tab];
				Item[] items = Arrays.copyOf(Arrays.copyOfRange(player.getBank().toArray(), from, to), Bank.SIZE);
				ActionSender.sendInterfaceItems(player, Bank.BANK_INVENTORY_INTERFACE + tab, items);
			}
		} else {
			PacketBankSearch.searchFor(player, player.getBankField().getSearchText());
		}
	}

}
