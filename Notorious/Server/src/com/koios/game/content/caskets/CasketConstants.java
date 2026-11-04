package com.koios.game.content.caskets;

import com.koios.game.model.item.Item;

public class CasketConstants {

	public static class CasketLoot {

		private final Item item;
		private final int randomAmount;
		private final Rarity rarity;

		public CasketLoot(Item item, Rarity rarity) {
			this.item = item;
			this.rarity = rarity;
			randomAmount = -1;
		}

		public CasketLoot(Item item, Rarity rarity, int randomAmount) {
			this.item = item;
			this.rarity = rarity;
			this.randomAmount = randomAmount;
		}

		public Item getItem() {
			return item;
		}

		public Rarity getRarity() {
			return rarity;
		}

		public int getRandomAmount() {
			return randomAmount;
		}

	}

	public enum Rarity {

		COMMON, UNCOMMON, RARE, VERY_RARE

	}

	static CasketLoot[][] LOOT_TABLE = {
			// Regular casket
			{ new CasketLoot(new Item(995), Rarity.COMMON), new CasketLoot(new Item(1623), Rarity.COMMON), new CasketLoot(new Item(1621), Rarity.UNCOMMON),
					new CasketLoot(new Item(1619), Rarity.UNCOMMON), new CasketLoot(new Item(1617), Rarity.RARE), new CasketLoot(new Item(985), Rarity.RARE),
					new CasketLoot(new Item(987), Rarity.RARE), new CasketLoot(new Item(1454), Rarity.COMMON), new CasketLoot(new Item(1452), Rarity.UNCOMMON),
					new CasketLoot(new Item(1462), Rarity.RARE) },

			// Easy casket
			{ new CasketLoot(new Item(995), Rarity.COMMON, 50000), new CasketLoot(new Item(4561), Rarity.COMMON, 15), new CasketLoot(new Item(555), Rarity.COMMON, 150),
					new CasketLoot(new Item(556), Rarity.COMMON, 150), new CasketLoot(new Item(557), Rarity.COMMON, 150), new CasketLoot(new Item(554), Rarity.COMMON, 150),
					new CasketLoot(new Item(329), Rarity.COMMON), new CasketLoot(new Item(333), Rarity.COMMON), new CasketLoot(new Item(4821), Rarity.COMMON, 100),
					new CasketLoot(new Item(1269), Rarity.COMMON), new CasketLoot(new Item(849), Rarity.COMMON), new CasketLoot(new Item(1077), Rarity.COMMON),
					new CasketLoot(new Item(1089), Rarity.COMMON), new CasketLoot(new Item(1125), Rarity.COMMON), new CasketLoot(new Item(1165), Rarity.COMMON),
					new CasketLoot(new Item(3827), Rarity.UNCOMMON), new CasketLoot(new Item(3828), Rarity.UNCOMMON), new CasketLoot(new Item(3829), Rarity.UNCOMMON),
					new CasketLoot(new Item(3830), Rarity.UNCOMMON), new CasketLoot(new Item(3831), Rarity.UNCOMMON), new CasketLoot(new Item(3832), Rarity.UNCOMMON),
					new CasketLoot(new Item(3833), Rarity.UNCOMMON), new CasketLoot(new Item(3834), Rarity.UNCOMMON), new CasketLoot(new Item(3835), Rarity.UNCOMMON),
					new CasketLoot(new Item(3836), Rarity.UNCOMMON), new CasketLoot(new Item(3837), Rarity.UNCOMMON), new CasketLoot(new Item(3838), Rarity.UNCOMMON),
					new CasketLoot(new Item(1725), Rarity.UNCOMMON), new CasketLoot(new Item(1727), Rarity.UNCOMMON), new CasketLoot(new Item(1729), Rarity.UNCOMMON),
					new CasketLoot(new Item(1731), Rarity.UNCOMMON), new CasketLoot(new Item(1712), Rarity.UNCOMMON), new CasketLoot(new Item(10280), Rarity.RARE),
					new CasketLoot(new Item(10282), Rarity.RARE), // Black santa instead of magic
					// bow ? Facking erik. new
					// CasketLoot(new Item(10284), Rarity.RARE),
					new CasketLoot(new Item(2583), Rarity.RARE), new CasketLoot(new Item(2585), Rarity.RARE), new CasketLoot(new Item(2587), Rarity.RARE),
					new CasketLoot(new Item(2589), Rarity.RARE), new CasketLoot(new Item(2633), Rarity.RARE), new CasketLoot(new Item(2635), Rarity.RARE),
					new CasketLoot(new Item(2637), Rarity.RARE), new CasketLoot(new Item(7388), Rarity.RARE), new CasketLoot(new Item(7392), Rarity.RARE),
					new CasketLoot(new Item(7396), Rarity.RARE), new CasketLoot(new Item(7364), Rarity.RARE), new CasketLoot(new Item(7368), Rarity.RARE),
					new CasketLoot(new Item(10400), Rarity.RARE), new CasketLoot(new Item(10402), Rarity.RARE), new CasketLoot(new Item(10404), Rarity.RARE),
					new CasketLoot(new Item(10406), Rarity.RARE), new CasketLoot(new Item(10408), Rarity.RARE), new CasketLoot(new Item(10410), Rarity.RARE),
					new CasketLoot(new Item(10412), Rarity.RARE), new CasketLoot(new Item(10414), Rarity.RARE), new CasketLoot(new Item(10416), Rarity.RARE),
					new CasketLoot(new Item(10418), Rarity.RARE), new CasketLoot(new Item(10420), Rarity.RARE), new CasketLoot(new Item(10422), Rarity.RARE),
					new CasketLoot(new Item(10424), Rarity.RARE), new CasketLoot(new Item(10426), Rarity.RARE), new CasketLoot(new Item(10428), Rarity.RARE),
					new CasketLoot(new Item(10430), Rarity.RARE), new CasketLoot(new Item(10432), Rarity.RARE), new CasketLoot(new Item(10434), Rarity.RARE),
					new CasketLoot(new Item(10436), Rarity.RARE), new CasketLoot(new Item(10438), Rarity.RARE), new CasketLoot(new Item(10714), Rarity.RARE),
					new CasketLoot(new Item(10715), Rarity.RARE), new CasketLoot(new Item(10716), Rarity.RARE), new CasketLoot(new Item(10717), Rarity.RARE),
					new CasketLoot(new Item(10392), Rarity.RARE), new CasketLoot(new Item(10394), Rarity.RARE), new CasketLoot(new Item(10396), Rarity.RARE),
					new CasketLoot(new Item(10398), Rarity.RARE), new CasketLoot(new Item(10458), Rarity.RARE), new CasketLoot(new Item(10460), Rarity.RARE),
					new CasketLoot(new Item(10462), Rarity.RARE), new CasketLoot(new Item(10464), Rarity.RARE), new CasketLoot(new Item(10466), Rarity.RARE),
					new CasketLoot(new Item(10468), Rarity.RARE), new CasketLoot(new Item(10470), Rarity.RARE), new CasketLoot(new Item(10472), Rarity.RARE),
					new CasketLoot(new Item(10474), Rarity.RARE), new CasketLoot(new Item(13105), Rarity.RARE), new CasketLoot(new Item(2591), Rarity.VERY_RARE),
					new CasketLoot(new Item(2593), Rarity.VERY_RARE), new CasketLoot(new Item(2595), Rarity.VERY_RARE), new CasketLoot(new Item(2597), Rarity.VERY_RARE),
					new CasketLoot(new Item(7386), Rarity.VERY_RARE), new CasketLoot(new Item(7390), Rarity.VERY_RARE), new CasketLoot(new Item(7394), Rarity.VERY_RARE),
					new CasketLoot(new Item(7362), Rarity.VERY_RARE), new CasketLoot(new Item(7366), Rarity.VERY_RARE), new CasketLoot(new Item(10366), Rarity.VERY_RARE),
					new CasketLoot(new Item(13095), Rarity.VERY_RARE) } };

}
