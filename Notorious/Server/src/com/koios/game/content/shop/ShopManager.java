package com.koios.game.content.shop;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koios.GameService;
import com.koios.game.content.shop.dynamic.SkillcapeShop;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

public final class ShopManager {

	private final Map<Integer, Shop> shops = new HashMap<>();

	public void init() {
		reload();
		regulate();
	}

	public void reload() {
		load();
	}

	private void regulate() {
		GameService.getTaskScheduler().schedule(new Task(17) { // 10 seconds
			@Override
			protected void execute() {
				for (Shop shop : shops.values()) {
					if (!shop.isRegulated()) {
						continue;
					}

					boolean updated = false;

					Item[] stock = shop.getStock();
					Item[] items = shop.toArray();

					for (int slot = 0, length = items.length; slot < length; slot++) {
						Item item = items[slot];

						if (item == null) {
							continue;
						}

						// If the item is part of the stock, gradually modify items back to their
						// original states
						if (stock.length > 0 && slot >= 0 && slot < stock.length) {
							Item stockItem = stock[slot];

							if (item.getCount() < stockItem.getCount()) {
								items[slot] = new Item(item.getId(), item.getCount() + 1);
								updated = true;
							}

							if (item.getCount() > stockItem.getCount()) {
								items[slot] = new Item(item.getId(), item.getCount() - 1);
								updated = true;
							}
						} else {
							// If it isn't part of the stock, remove from the shop to make space for
							// new items.
							shop.remove(slot, new Item(item.getId(), 1));
						}
					}

					// in-case a shop is closed by other means and the current shop is not reset
					shop.getViewers().removeIf(p -> p.currentShop != shop.getId());

					if (updated) {
						shop.setItems(items);
						shop.fireItemsChanged();
					}
				}
			}
		});
	}

	private void load() {
		Path path = Paths.get("data/cfg/shops.json");

		String base = "com.koios.game.content.shop.currency.";
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			JsonParser parser = new JsonParser();
			JsonArray array = (JsonArray) parser.parse(reader);
			Gson gson = new Gson();

			for (int i = 0; i < array.size(); i++) {
				JsonObject obj = (JsonObject) array.get(i);
				int id = obj.get("id").getAsInt();
				String name = obj.get("name").getAsString();
				ShopType type = ShopType.valueOf(obj.get("type").getAsString());
				Currency currency = (Currency) Class.forName(base + obj.get("currency").getAsString()).newInstance();
				Item[] stock = gson.fromJson(obj.get("stock"), Item[].class);
				boolean unlimitedStock = obj.has("unlimited_stock") && obj.get("unlimited_stock").getAsBoolean();
				add(new Shop(id, name, type, currency, stock, unlimitedStock));
			}
		} catch (Exception cause) {
			cause.printStackTrace();
		}

		// Custom shops with dynamic items
		add(new SkillcapeShop());

		System.out.println("Loaded " + shops.size() + " shops!");
	}

	private void add(Shop shop) {
		shops.put(shop.getId(), shop);
	}

	public void buyItem(Player player, int id, int amount, int slot) {
		Shop shop = shops.get(player.currentShop);
		if (shop == null) {
			return;
		}

		shop.buyItem(player, id, amount, slot);
	}

	public void checkBuyPrice(Player player, int id, int slot) {
		Shop shop = shops.get(player.currentShop);
		if (shop == null) {
			return;
		}

		shop.checkBuyPrice(player, id, slot);
	}

	public void sellItem(Player player, int id, int amount, int slot) {
        if(player.getInventory().get(slot)!=null && player.getInventory().get(slot).hasRoll()){player.sendMessage("Prototype rolled items cannot be sold to legacy shops.");return;}
		Shop shop = shops.get(player.currentShop);
		if (shop == null) {
			return;
		}

		shop.sellItem(player, id, amount, slot);
	}

	public void checkSellPrice(Player player, int id, int slot) {
		Shop shop = shops.get(player.currentShop);
		if (shop == null) {
			return;
		}

		shop.checkSellPrice(player, id, slot);
	}

	public void open(Player player, int id) {
		Shop shop = shops.get(id);
		if (shop == null) {
			player.sendMessage("The shop you tried to open does not exist.");
			return;
		}

		shop.open(player);
	}

	public void close(Player player) {
		Shop shop = shops.get(player.currentShop);
		if (shop != null) {
			shop.removeViewer(player);
		}

		player.currentShop = -1;
	}

}