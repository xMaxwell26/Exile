package com.koios.game.content.skill.farming;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.io.StoreUtils;

public class Farming {

	public static boolean guide(Player player, int x, int y) {
		// allotments
		if (Allotments.guide(x, y)) {
			return true;
		}

		// flowers
		if (Flowers.guide(x, y)) {
			return true;
		}

		// herbs
		if (Herbs.guide(x, y)) {
			return true;
		}

		// hops
		if (Hops.guide(x, y)) {
			return true;
		}

		// bushes
		if (Bushes.guide(x, y)) {
			return true;
		}

		// trees
		if (WoodTrees.guide(x, y)) {
			return true;
		}

		// fruit trees
		if (FruitTree.guide(x, y)) {
			return true;
		}

		// special plant one
		if (SpecialPlantOne.guide(x, y)) {
			return true;
		}

		// special plant two
		if (SpecialPlantTwo.guide(x, y)) {
			return true;
		}

		return false;
	}

	public static boolean harvest(Player player, int x, int y) {
		// allotments
		if (player.getAllotments().harvest(x, y)) {
			return true;
		}

		// flowers
		if (player.getFlowers().harvest(x, y)) {
			return true;
		}

		// herbs
		if (player.getHerbs().harvest(x, y)) {
			return true;
		}

		// hops
		if (player.getHops().harvest(x, y)) {
			return true;
		}

		// bushes
		if (player.getBushes().harvestOrCheckHealth(x, y)) {
			return true;
		}

		// trees
		if (player.getTrees().checkHealth(x, y)) {
			return true;
		}

		if (player.getTrees().cut(x, y)) {
			return true;
		}

		// fruit trees
		if (player.getFruitTree().harvestOrCheckHealth(x, y)) {
			return true;
		}

		// special plant one
		if (player.getSpecialPlantOne().harvestOrCheckHealth(x, y)) {
			return true;
		}

		// special plant two
		if (player.getSpecialPlantTwo().harvestOrCheckHealth(x, y)) {
			return true;
		}

		return false;
	}

	public static boolean inspectObject(Player player, int x, int y) {
		// allotments
		if (player.getAllotments().inspect(x, y)) {
			return true;
		}

		// flowers
		if (player.getFlowers().inspect(x, y)) {
			return true;
		}

		// herbs
		if (player.getHerbs().inspect(x, y)) {
			return true;
		}

		// hops
		if (player.getHops().inspect(x, y)) {
			return true;
		}

		// bushes
		if (player.getBushes().inspect(x, y)) {
			return true;
		}

		// trees
		if (player.getTrees().inspect(x, y)) {
			return true;
		}

		// fruit trees
		if (player.getFruitTree().inspect(x, y)) {
			return true;
		}

		// special plant one
		if (player.getSpecialPlantOne().inspect(x, y)) {
			return true;
		}

		// special plant two
		if (player.getSpecialPlantTwo().inspect(x, y)) {
			return true;
		}

		return false;
	}

	public static boolean prepareCrop(Player player, int item, int id, int x, int y) {
		// allotments
		if (player.getAllotments().curePlant(x, y, item)) {
			return true;
		}

		if (player.getAllotments().putCompost(x, y, item)) {
			return true;
		}

		if (player.getAllotments().clearPatch(x, y, item)) {
			return true;
		}

		if (item >= 3422 && item <= 3428 && id == 4090) {
			player.getInventory().remove(new Item(item));
			player.getInventory().add(new Item(item + 8));
			player.playAnimation(new Animation(832));
			player.sendMessage("You put the olive oil on the fire, and turn it into sacred oil.");
			return true;
		}

		if (item <= 5340 && item > 5332) {
			if (player.getAllotments().waterPatch(x, y, item)) {
				return true;
			}
		}

		if (player.getAllotments().plantSeed(x, y, item)) {
			return true;
		}

		// flowers
		if (player.getFlowers().plantScareCrow(x, y, item)) {
			return true;
		}

		if (player.getFlowers().curePlant(x, y, item)) {
			return true;
		}

		if (player.getFlowers().putCompost(x, y, item)) {
			return true;
		}

		if (player.getFlowers().clearPatch(x, y, item)) {
			return true;
		}

		if (item <= 5340 && item > 5332) {
			if (player.getFlowers().waterPatch(x, y, item)) {
				return true;
			}
		}

		if (player.getFlowers().plantSeed(x, y, item)) {
			return true;
		}

		if (player.getCompost().handleItemOnObject(item, id, x, y)) {
			return true;
		}

		// herbs
		if (player.getHerbs().curePlant(x, y, item)) {
			return true;
		}

		if (player.getHerbs().putCompost(x, y, item)) {
			return true;
		}

		if (player.getHerbs().clearPatch(x, y, item)) {
			return true;
		}

		if (player.getHerbs().plantSeed(x, y, item)) {
			return true;
		}

		// hops
		if (player.getHops().curePlant(x, y, item)) {
			return true;
		}

		if (player.getHops().putCompost(x, y, item)) {
			return true;
		}

		if (player.getHops().clearPatch(x, y, item)) {
			return true;
		}

		if (item <= 5340 && item > 5332) {
			if (player.getHops().waterPatch(x, y, item)) {
				return true;
			}
		}

		if (player.getHops().plantSeed(x, y, item)) {
			return true;
		}

		// bushes
		if (player.getBushes().curePlant(x, y, item)) {
			return true;
		}

		if (player.getBushes().putCompost(x, y, item)) {
			return true;
		}

		if (player.getBushes().clearPatch(x, y, item)) {
			return true;
		}

		if (player.getBushes().plantSeed(x, y, item)) {
			return true;
		}

		// trees
		if (player.getTrees().pruneArea(x, y, item)) {
			return true;
		}

		if (player.getTrees().putCompost(x, y, item)) {
			return true;
		}

		if (player.getTrees().plantSapling(x, y, item)) {
			return true;
		}

		if (player.getTrees().clearPatch(x, y, item)) {
			return true;
		}

		// fruit trees
		if (player.getFruitTree().pruneArea(x, y, item)) {
			return true;
		}

		if (player.getFruitTree().putCompost(x, y, item)) {
			return true;
		}

		if (player.getFruitTree().clearPatch(x, y, item)) {
			return true;
		}

		if (player.getFruitTree().plantSapling(x, y, item)) {
			return true;
		}

		// special plant one
		if (player.getSpecialPlantOne().curePlant(x, y, item)) {
			return true;
		}

		if (player.getSpecialPlantOne().putCompost(x, y, item)) {
			return true;
		}

		if (player.getSpecialPlantOne().clearPatch(x, y, item)) {
			return true;
		}

		if (player.getSpecialPlantOne().plantSapling(x, y, item)) {
			return true;
		}

		// Special plant two
		if (player.getSpecialPlantTwo().curePlant(x, y, item)) {
			return true;
		}

		if (player.getSpecialPlantTwo().putCompost(x, y, item)) {
			return true;
		}

		if (player.getSpecialPlantTwo().clearPatch(x, y, item)) {
			return true;
		}

		if (player.getSpecialPlantTwo().plantSeeds(x, y, item)) {
			return true;
		}

		return false;
	}

	public static void resetFarming(Player player) {
		player.getAllotments().resetAll();
		player.getFlowers().resetAll();
		player.getHerbs().resetAll();
		player.getHops().resetAll();
		player.getBushes().resetAll();
		player.getAllotments().resetAll();
		player.getTrees().resetAll();
		player.getFruitTree().resetAll();
		player.getSpecialPlantOne().resetAll();
		player.getSpecialPlantTwo().resetAll();
		player.getCompost().resetAll();
	}

	public static void save(Player player) {
		ByteBuffer buffer = ByteBuffer.allocate(4096);

		buffer.putLong(player.elapsedMinutes);
		player.getFlowers().saveStages(buffer);
		player.getAllotments().saveStages(buffer);
		player.getCompost().saveStages(buffer);
		player.getFruitTree().saveStages(buffer);
		player.getHerbs().saveStages(buffer);
		player.getHops().saveStages(buffer);
		player.getSpecialPlantOne().saveStages(buffer);
		player.getSpecialPlantTwo().saveStages(buffer);
		player.getBushes().saveStages(buffer);
		player.getTrees().saveStages(buffer);
		buffer.putLong(player.lastLogout);
		buffer.rewind();

		try {
			Path path = StoreUtils.getDirectory("farming-saves").resolve(player.getName() + ".dat");
			try (FileOutputStream os = new FileOutputStream(path.toFile(), false); FileChannel wChannel = os.getChannel()) {
				wChannel.write(buffer);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void updateFarmingConfigs(Player player) {
		/**
		 * Farming
		 */
		player.getAllotments().updateAllotmentsStates();
		player.getFlowers().updateFlowerStates();
		player.getHerbs().updateHerbsStates();
		player.getHops().updateHopsStates();
		player.getBushes().updateBushesStates();
		player.getAllotments().updateAllotmentsStates();
		player.getTrees().updateTreeStates();
		player.getFruitTree().updateFruitTreeStates();
		player.getSpecialPlantOne().updateSpecialPlants();
		player.getSpecialPlantTwo().updateSpecialPlants();
		player.getCompost().onLogin();
	}

	public static void load(Player player) {
		try {
			Path path = StoreUtils.getDirectory("farming-saves").resolve(player.getName() + ".dat");
			ByteBuffer buffer = ByteBuffer.wrap(Files.readAllBytes(path));

			player.elapsedMinutes = buffer.getLong();
			player.getFlowers().loadStages(buffer);
			player.getAllotments().loadStages(buffer);
			player.getCompost().loadStages(buffer);
			player.getFruitTree().loadStages(buffer);
			player.getHerbs().loadStages(buffer);
			player.getHops().loadStages(buffer);
			player.getSpecialPlantOne().loadStages(buffer);
			player.getSpecialPlantTwo().loadStages(buffer);
			player.getBushes().saveStages(buffer);
			player.getTrees().loadStages(buffer);
			player.lastLogout = buffer.getLong();
		} catch (IOException ex) {
		}
	}

	public static void onLogin(Player player) {
		player.getAllotments().doCalculations();
		player.getFlowers().doCalculations();
		player.getHerbs().doCalculations();
		player.getHops().doCalculations();
		player.getBushes().doCalculations();
		player.getAllotments().doCalculations();
		player.getTrees().doCalculations();
		player.getFruitTree().doCalculations();
		player.getSpecialPlantOne().doCalculations();
		player.getSpecialPlantTwo().doCalculations();
		player.lastFarmingUpdate = System.currentTimeMillis();
		player.getCompost().onLogin();
		Farming.updateFarmingConfigs(player);
	}

}
