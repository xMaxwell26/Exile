package com.koios.game.content.skill.woodcutting;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Handle the Bird Nests for Woodcutting.
 *
 * @author Gabbe
 */
public class BirdNests {

	public enum NestsData {

		blueEggNest(5072, new int[] { 5077 }), goldRingNest(5074, new int[] { 1635 }), greeEggNest(5071, new int[] { 5078 }), greyEggNest(11966,
				new int[] { 11964, 11965 }), redEggNest(5070, new int[] { 5076 }), seedNest1(5073, new int[] { 5298, 5299, 5296, 5295 }), seedNest2(7413,
						new int[] { 5298, 5299, 5296, 5295 });

		public static NestsData forNest(int id) {
			return values.get(id);
		}

		private static final Map<Integer, NestsData> values = new HashMap<>();

		static {
			for (NestsData n : values()) {
				values.put(n.getNestId(), n);
			}
		}

		public final int itemId;
		public final int[] reward;

		private NestsData(int itemId, int[] reward) {
			this.itemId = itemId;
			this.reward = reward;
		}

		public int getNestId() {
			return itemId;
		}

		public int[] getReward() {
			return reward;
		}

		public int randomReward() {
			return getReward()[(int) (Math.random() * getReward().length)];
		}

	}

	public static void birdNests(Player p) {
		int[] nestsId = { 5070, 5071, 5072, 5073, 5074, 7413, 11966 };
		int random = 100;
		if (p.getEquipment().get(Equipment.SLOT_AMULET) != null && p.getEquipment().get(Equipment.SLOT_AMULET).getId() == 10132) {
			random = 80;
		}
		if (NumberUtil.random(random) < 5) {
			random = NumberUtil.random(nestsId.length - 1);
			GroundItemManager.spawnNew(p, nestsId[random], p.getX(), p.getY(), 1);
			p.sendMessage("A bird's nest falls out of the tree!");
		}
	}

	public static boolean isNest(int id) {
		NestsData n = NestsData.forNest(id);
		return n != null;
	}

	public static void search(Player player, int id) {
		NestsData n = NestsData.forNest(id);
		player.getInventory().remove(new Item(id, 1));
		player.getInventory().add(new Item(n.randomReward(), 1));
		player.getInventory().add(new Item(5075, 1));
	}

}