package com.koios.game.content.skill.farming;

/**
 * Created by J on 25/06/2014.
 */

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.task.Task;
import com.koios.util.ByteBufferUtil;
import com.koios.util.NumberUtil;

public class Hops {

	public enum HopsData {
		BARLEY(5305, 6006, 4, 3, new int[] { 6032, 3 }, 40, 0.35, 8.5, 9.5, 0x31, 0x35), HAMMERSTONE(5307, 5994, 4, 4, new int[] { 6010, 1 }, 40, 0.35, 9, 10, 0x04,
				0x08), ASGARNIAN(5308, 5996, 4, 8, new int[] { 5458, 1 }, 40, 0.30, 10.5, 12, 0x0b, 0x10), JUTE(5306, 5931, 3, 13, new int[] { 6008, 6 }, 40, 0.30, 13, 14.5, 0x38,
						0x3d), YANILLIAN(5309, 5998, 4, 16, new int[] { 5968, 1 }, 40, 0.25, 14.5, 16, 0x13, 0x19), KRANDORIAN(5310, 6000, 4, 21, new int[] { 5478 }, 40, 0.25,
								17.5, 19.5, 0x1c, 0x23), WILDBLOOD(5311, 6002, 4, 28, new int[] { 6012, 1 }, 40, 0.20, 23, 26, 0x26, 0x2e),;

		public static HopsData forId(int seedId) {
			return HopsData.seeds.get(seedId);
		}

		private int seedId;
		private int harvestId;
		private int seedAmount;
		private int levelRequired;
		private int[] paymentToWatch;
		private int growthTime;
		private double diseaseChance;
		private double plantingXp;
		private double harvestXp;
		private int startingState;
		private int endingState;

		private static Map<Integer, HopsData> seeds = new HashMap<>();

		static {
			for (HopsData data : HopsData.values()) {
				HopsData.seeds.put(data.seedId, data);
			}
		}

		HopsData(int seedId, int harvestId, int seedAmount, int levelRequired, int[] paymentToWatch, int growthTime, double diseaseChance, double plantingXp, double harvestXp,
				int startingState, int endingState) {
			this.seedId = seedId;
			this.harvestId = harvestId;
			this.seedAmount = seedAmount;
			this.levelRequired = levelRequired;
			this.paymentToWatch = paymentToWatch;
			this.growthTime = growthTime;
			this.diseaseChance = diseaseChance;
			this.plantingXp = plantingXp;
			this.harvestXp = harvestXp;
			this.startingState = startingState;
			this.endingState = endingState;
		}

		public double getDiseaseChance() {
			return diseaseChance;
		}

		public int getEndingState() {
			return endingState;
		}

		public int getGrowthTime() {
			return growthTime;
		}

		public int getHarvestId() {
			return harvestId;
		}

		public double getHarvestXp() {
			return harvestXp;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int[] getPaymentToWatch() {
			return paymentToWatch;
		}

		public double getPlantingXp() {
			return plantingXp;
		}

		public int getSeedAmount() {
			return seedAmount;
		}

		public int getSeedId() {
			return seedId;
		}

		public int getStartingState() {
			return startingState;
		}
	}

	public enum HopsFieldsData {
		LUMBRIDGE(0, new Location[] { new Location(3227, 3313, 0), new Location(3231, 3317, 0) }, 2333), MCGRUBOR(1,
				new Location[] { new Location(2664, 3523, 0), new Location(2669, 3528, 0) }, 2334), YANILLE(2,
						new Location[] { new Location(2574, 3103, 0), new Location(2577, 3106, 0) },
						2332), ENTRANA(3, new Location[] { new Location(2809, 3335, 0), new Location(2812, 3338, 0) }, 2327);

		public static HopsFieldsData forId(int npcId) {
			return HopsFieldsData.npcsProtecting.get(npcId);
		}

		public static HopsFieldsData forIdPosition(Location position) {
			for (HopsFieldsData hopsFieldsData : HopsFieldsData.values()) {
				if (FarmingConstants.inRangeArea(hopsFieldsData.getHopsPosition()[0], hopsFieldsData.getHopsPosition()[1], position)) {
					return hopsFieldsData;
				}
			}
			return null;
		}

		private int hopsIndex;
		private Location[] hopsPosition;

		private int npcId;

		private static Map<Integer, HopsFieldsData> npcsProtecting = new HashMap<>();

		static {
			for (HopsFieldsData data : HopsFieldsData.values()) {
				HopsFieldsData.npcsProtecting.put(data.npcId, data);
			}
		}

		HopsFieldsData(int hopsIndex, Location[] hopsPosition, int npcId) {
			this.hopsIndex = hopsIndex;
			this.hopsPosition = hopsPosition;
			this.npcId = npcId;
		}

		public int getHopsIndex() {
			return hopsIndex;
		}

		public Location[] getHopsPosition() {
			return hopsPosition;
		}

		public int getNpcId() {
			return npcId;
		}
	}

	public enum InspectData {
		BARLEY(5305,
				new String[][] { { "The barley seeds have only just been planted." }, { "Grain heads develop at the upper part of the stalks,", "as the barley grows taller." },
						{ "The barley grows taller, the heads weighing", "slightly on the stalks." }, {
								"The barley grows taller." },
				{ "The barley is ready to harvest. The heads of grain", "are weighing down heavily on the stalks!" } }), HAMMERSTONE(
						5307,
						new String[][] { { "The Hammerstone seeds have only just been planted." }, { "The Hammerstone hops plant grows a little bit taller." }, {
								"The Hammerstone hops plant grows a bit taller." }, { "The Hammerstone hops plant grows a bit taller." },
						{ "The Hammerstone hops plant is ready to harvest." } }), ASGARNIAN(
								5308,
								new String[][] { { "The Asgarnian seeds have only just been planted." }, { "The Asgarnian hops plant grows a bit taller." }, {
										"The Asgarnian hops plant grows a bit taller." }, { "The Asgarnian hops plant grows a bit taller." },
								{ "The upper new leaves appear dark green to the", "rest of the plant." }, { "The Asgarnian hops plant is ready to harvest." } }), JUTE(
										5306,
										new String[][] { { "The Jute seeds have only just been planted." }, { "The jute plants grow taller." }, {
												"The jute plants grow taller." }, { "The jute plants grow taller." },
										{ "The jute plant grows taller. They are as high", "as the player." }, { "The jute plants are ready to harvest." } }), YANILLIAN(
												5309,
												new String[][] { { "The Yanillian seeds have only just been planted." }, { "The Yanillian hops plant grows a bit taller." }, {
														"The Yanillian hops plant grows a bit taller." }, { "The Yanillian hops plant grows a bit taller." },
												{ "The new leaves on the top of the Yanillian hops", "plant are dark green." },
												{ "The new leaves on the top of the Yanillian hops", "plant are dark green." },
												{ "The Yanillian hops plant is ready to harvest." } }), KRANDORIAN(
														5310,
														new String[][] { { "The Krandorian seeds have only just been planted." }, { "The Krandorian plant grows a bit taller." }, {
																"The Krandorian plant grows a bit taller." }, { "The Krandorian plant grows a bit taller." },
														{ "The new leaves on top of the Krandorian plant are", "dark green." }, { "The Krandorian plant grows a bit taller." },
														{ "The new leaves on top of the Krandorian plant", "are dark green." },
														{ "The Krandorian plant is ready for harvesting." } }), WILDBLOOD(
																5311,
																new String[][] { { "The wildblood seeds have only just been planted." },
																		{ "The wildblood hops plant grows a bit taller." }, { "The wildblood hops plant grows a bit taller." },
																		{ "The wildblood hops plant grows a bit taller." }, { "The wildblood hops plant grows a bit taller." },
																		{ "The wildblood hops plant grows a bit taller." }, { "The wildblood hops plant grows a bit taller." },
																		{ "The new leaves at the top of the wildblood hops plant", "are dark green." },
																		{ "The wildblood hops plant is ready to harvest." } });

		public static InspectData forId(int seedId) {
			return InspectData.seeds.get(seedId);
		}

		private int seedId;
		private String[][] messages;

		private static Map<Integer, InspectData> seeds = new HashMap<>();

		static {
			for (InspectData data : InspectData.values()) {
				InspectData.seeds.put(data.seedId, data);
			}
		}

		InspectData(int seedId, String[][] messages) {
			this.seedId = seedId;
			this.messages = messages;
		}

		public String[][] getMessages() {
			return messages;
		}

		public int getSeedId() {
			return seedId;
		}
	}

	public static int getPlantState(int plantState) {
		switch (plantState) {
		case 0:
			return Hops.GROWING;
		case 1:
			return Hops.WATERED;
		case 2:
			return Hops.DISEASED;
		case 3:
			return Hops.DEAD;
		}
		return -1;
	}

	public static boolean guide(int objectX, int objectY) {
		HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(new Location(objectX, objectY));
		if (hopsFieldsData == null) {
			return false;
		}
		return true;
	}

	private final Player player;
	// set of global constants for Farming
	private static final int START_HARVEST_AMOUNT = 3;

	private static final int END_HARVEST_AMOUNT = 41;

	private static final double WATERING_CHANCE = 0.5;
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;
	// Farming data
	public int[] hopsStages = new int[4];
	public int[] hopsSeeds = new int[4];
	public int[] hopsHarvest = new int[4];
	public int[] hopsState = new int[4];

	public long[] hopsTimer = new long[4];

	public double[] diseaseChance = { 1, 1, 1, 1 };

	public boolean[] hasFullyGrown = { false, false, false, false };

	public boolean[] hopsWatched = { false, false, false, false };
	/* set of the constants for the patch */
	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;
	public static final int WATERED = 0x01;
	public static final int DISEASED = 0x02;
	public static final int DEAD = 0x03;

	/* This is the enum holding the seeds info */

	public static final int MAIN_HOPS_CONFIG = 506;

	/* This is the enum data about the different patches */

	public Hops(Player player) {
		this.player = player;
	}

	/* This is the enum that hold the different data for inspecting the plant */

	public boolean checkIfRaked(int objectX, int objectY) {
		HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(new Location(objectX, objectY));
		if (hopsFieldsData == null) {
			return false;
		}
		if (hopsStages[hopsFieldsData.getHopsIndex()] == 3) {
			return true;
		}
		return false;
	}

	/* update all the patch states */

	public boolean clearPatch(int objectX, int objectY, int itemId) {
		final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(new Location(objectX, objectY));
		int finalAnimation;
		int finalDelay;
		if (hopsFieldsData == null || itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE) {
			return false;
		}
		if (hopsStages[hopsFieldsData.getHopsIndex()] == 3) {
			return true;
		}
		if (hopsStages[hopsFieldsData.getHopsIndex()] <= 3) {
			if (!player.getInventory().contains(FarmingConstants.RAKE)) {
				SimpleDialogues.sendStatement(player, "You need a rake to clear this path.");
				return true;
			} else {
				finalAnimation = FarmingConstants.RAKING_ANIM;
				finalDelay = 5;
			}
		} else {
			if (!player.getInventory().contains(FarmingConstants.SPADE)) {
				SimpleDialogues.sendStatement(player, "You need a spade to clear this path.");
				return true;
			} else {
				finalAnimation = FarmingConstants.SPADE_ANIM;
				finalDelay = 3;
			}
		}
		final int animation = finalAnimation;
		player.setStopPacket(true);
		player.playAnimation(new Animation(animation));
		int times = 3 - hopsStages[hopsFieldsData.getHopsIndex()];
		GameService.getTaskScheduler().schedule(new Task(finalDelay, times) {

			@Override
			public void execute() {
				player.playAnimation(new Animation(animation));
				if (hopsStages[hopsFieldsData.getHopsIndex()] <= 2) {
					hopsStages[hopsFieldsData.getHopsIndex()]++;
					player.getInventory().add(new Item(6055));
				} else {
					hopsStages[hopsFieldsData.getHopsIndex()] = 3;
					Hops.this.resetHops(hopsFieldsData.getHopsIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
				}
				player.getSkill().addExperience(Skill.FARMING, Hops.CLEARING_EXPERIENCE * Constants.FARMING_EXPERIENCE);
				hopsTimer[hopsFieldsData.getHopsIndex()] = player.elapsedMinutes;
				Hops.this.updateHopsStates();
				if (hopsStages[hopsFieldsData.getHopsIndex()] == 3) {
					Hops.this.resetHops(hopsFieldsData.getHopsIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
					return;
				}
			}
		});
		return true;
	}

	/* getting the different config values */

	public boolean curePlant(int objectX, int objectY, int itemId) {
		HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(new Location(objectX, objectY));
		if (hopsFieldsData == null || itemId != 6036) {
			return false;
		}
		HopsData hopsData = HopsData.forId(hopsSeeds[hopsFieldsData.getHopsIndex()]);
		if (hopsData == null) {
			return false;
		}
		if (hopsState[hopsFieldsData.getHopsIndex()] != 2) {
			player.sendMessage("This plant doesn't need to be cured.");
			return true;
		}
		player.getInventory().remove(new Item(itemId));
		player.getInventory().add(new Item(229));
		player.playAnimation(new Animation(FarmingConstants.CURING_ANIM));
		player.setStopPacket(true);
		hopsState[hopsFieldsData.getHopsIndex()] = 0;
		GameService.getTaskScheduler().schedule(new Task(7) {

			@Override
			public void execute() {
				player.sendMessage("You cure the plant with a plant cure.");
				Hops.this.updateHopsStates();
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* getting the plant states */

	public void doCalculations() {
		for (int i = 0; i < hopsSeeds.length; i++) {
			if (hopsStages[i] > 0 && hopsStages[i] <= 3 && player.elapsedMinutes - hopsTimer[i] >= 5) {
				hopsStages[i]--;
				hopsTimer[i] = player.elapsedMinutes;
				updateHopsStates();
				continue;
			}
			HopsData hopsData = HopsData.forId(hopsSeeds[i]);
			if (hopsData == null) {
				continue;
			}
			long difference = player.elapsedMinutes - hopsTimer[i];
			long growth = hopsData.getGrowthTime();
			int nbStates = hopsData.getEndingState() - hopsData.getStartingState();
			int state = (int) (difference * nbStates / growth);
			if (hopsTimer[i] == 0 || hopsState[i] == 3) {
				continue;
			}
			if (state > nbStates) {
				state = nbStates;
			}
			if (4 + state != hopsStages[i]) {
				hopsStages[i] = 4 + state;
				if (hopsStages[i] <= 4 + state) {
					for (int j = hopsStages[i]; j <= 4 + state; j++) {
						doStateCalculation(i);
					}
				}
				updateHopsStates();
			}
		}
	}

	/* calculating the disease chance and making the plant grow */

	public void doStateCalculation(int index) {
		if (hopsState[index] == 3) {
			return;
		}
		// if the patch is diseased, it dies, if its watched by a farmer, it
		// goes back to normal
		if (hopsState[index] == 2) {
			if (hopsWatched[index]) {
				hopsState[index] = 0;
				HopsData hopsData = HopsData.forId(hopsSeeds[index]);
				if (hopsData == null) {
					return;
				}
				int difference = hopsData.getEndingState() - hopsData.getStartingState();
				int growth = hopsData.getGrowthTime();
				hopsTimer[index] += growth / difference;
				modifyStage(index);
			} else {
				hopsState[index] = 3;
			}
		}
		if (hopsState[index] == 1) {
			diseaseChance[index] *= 2;
			hopsState[index] = 0;
		}
		if (hopsState[index] == 5 && hopsStages[index] != 3) {
			hopsState[index] = 0;
		}
		if (hopsState[index] == 0 && hopsStages[index] >= 5 && !hasFullyGrown[index]) {
			HopsData hopsData = HopsData.forId(hopsSeeds[index]);
			if (hopsData == null) {
				return;
			}
			// double chance = diseaseChance[index] *
			// hopsData.getDiseaseChance();
			// int maxChance = (int) chance * 100;
			/* if (RandomGenerator.random(100) <= maxChance) { hopsState[index] = 2; } */
		}
	}

	public int getConfigValue(int hopsStage, int seedId, int plantState, int index) {
		HopsData hopsData = HopsData.forId(seedId);
		switch (hopsStage) {
		case 0:// weed
			return (Hops.GROWING << 6) + 0x00;
		case 1:// weed cleared
			return (Hops.GROWING << 6) + 0x01;
		case 2:
			return (Hops.GROWING << 6) + 0x02;
		case 3:
			return (Hops.GROWING << 6) + 0x03;
		}
		if (hopsData == null) {
			return -1;
		}
		if (hopsData.getEndingState() == hopsData.getStartingState() + hopsStage - 1) {
			hasFullyGrown[index] = true;
		}
		return (Hops.getPlantState(plantState) << 6) + hopsData.getStartingState() + hopsStage - 4;
	}

	/* calculations about the diseasing chance */

	public boolean harvest(int objectX, int objectY) {
		final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(new Location(objectX, objectY));
		if (hopsFieldsData == null) {
			return false;
		}
		final HopsData hopsData = HopsData.forId(hopsSeeds[hopsFieldsData.getHopsIndex()]);
		if (hopsData == null) {
			return false;
		}
		if (hopsData.getEndingState() > hopsData.getStartingState() + hopsStages[hopsFieldsData.getHopsIndex()] - 1) {
			return false;
		}
		if (!player.getInventory().contains(FarmingConstants.SPADE)) {
			SimpleDialogues.sendStatement(player, "You need a spade to harvest here.");
			return true;
		}
		if (player.getInventory().freeSlots() == 0) {
			player.sendMessage("You do not have enough inventory space to do this.");
			return true;
		}
		player.playAnimation(new Animation(FarmingConstants.SPADE_ANIM));
		player.stopTask();
		player.setStopPacket(true);
		player.setSkilling(new Task(2) {

			@Override
			public void execute() {
				if (hopsHarvest[hopsFieldsData.getHopsIndex()] == 0) {
					hopsHarvest[hopsFieldsData.getHopsIndex()] = (int) (1 + (Hops.START_HARVEST_AMOUNT + NumberUtil.random(Hops.END_HARVEST_AMOUNT - Hops.START_HARVEST_AMOUNT))
							* (player.getInventory().contains(7409) ? 1.10 : 1));
				}
				if (hopsHarvest[hopsFieldsData.getHopsIndex()] == 1) {
					Hops.this.resetHops(hopsFieldsData.getHopsIndex());
					hopsStages[hopsFieldsData.getHopsIndex()] = 3;
					hopsTimer[hopsFieldsData.getHopsIndex()] = player.elapsedMinutes;
					Hops.this.updateHopsStates();
					player.setStopPacket(false);
					stop();
					return;
				}
				if (player.getInventory().freeSlots() <= 0) {
					Hops.this.updateHopsStates();
					player.setStopPacket(false);
					stop();
					return;
				}
				hopsHarvest[hopsFieldsData.getHopsIndex()]--;
				player.playAnimation(new Animation(FarmingConstants.SPADE_ANIM));
				player.sendMessage("You harvest the crop, and get some vegetables.");
				player.getInventory().add(new Item(hopsData.getHarvestId()));
				player.getSkill().addExperience(Skill.FARMING, hopsData.getHarvestXp() * Constants.FARMING_EXPERIENCE);
			}
		});
		return true;
	}

	/* watering the patch */

	public boolean inspect(int objectX, int objectY) {
		final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(new Location(objectX, objectY));
		if (hopsFieldsData == null) {
			return false;
		}
		final InspectData inspectData = InspectData.forId(hopsSeeds[hopsFieldsData.getHopsIndex()]);
		final HopsData hopsData = HopsData.forId(hopsSeeds[hopsFieldsData.getHopsIndex()]);
		if (hopsState[hopsFieldsData.getHopsIndex()] == 2) {
			SimpleDialogues.sendStatement(player, "This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
			return true;
		} else if (hopsState[hopsFieldsData.getHopsIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
			return true;
		}
		if (hopsStages[hopsFieldsData.getHopsIndex()] == 0) {
			SimpleDialogues.sendStatement(player, "This is a hops patch. The soil has not been treated.", "The patch needs weeding.");
		} else if (hopsStages[hopsFieldsData.getHopsIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This is a hops patch. The soil has not been treated.", "The patch is empty and weeded.");
		} else if (inspectData != null && hopsData != null) {
			player.sendMessage("You bend down and start to inspect the patch...");
			player.playAnimation(new Animation(1331));
			player.setStopPacket(true);
			GameService.getTaskScheduler().schedule(new Task(5) {

				@Override
				public void execute() {
					if (hopsStages[hopsFieldsData.getHopsIndex()] - 4 < inspectData.getMessages().length - 2) {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[hopsStages[hopsFieldsData.getHopsIndex()] - 4]);
					} else if (hopsStages[hopsFieldsData.getHopsIndex()] < hopsData.getEndingState() - hopsData.getStartingState() + 2) {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[inspectData.getMessages().length - 2]);
					} else {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[inspectData.getMessages().length - 1]);
					}
					player.playAnimation(new Animation(1332));
					player.setStopPacket(false);
					stop();
				}
			});
		}
		return true;
	}

	/* clearing the patch with a rake of a spade */

	public void loadStages(ByteBuffer buffer) {
		for (int i = 0; i < hopsStages.length; i++) {
			hopsStages[i] = buffer.getInt();
		}
		for (int i = 0; i < hopsSeeds.length; i++) {
			hopsSeeds[i] = buffer.getInt();
		}
		for (int i = 0; i < hopsHarvest.length; i++) {
			hopsHarvest[i] = buffer.getInt();
		}
		for (int i = 0; i < hopsState.length; i++) {
			hopsState[i] = buffer.getInt();
		}
		for (int i = 0; i < hopsTimer.length; i++) {
			hopsTimer[i] = buffer.getLong();
		}
		for (int i = 0; i < diseaseChance.length; i++) {
			diseaseChance[i] = buffer.getDouble();
		}
		for (int i = 0; i < hasFullyGrown.length; i++) {
			hasFullyGrown[i] = ByteBufferUtil.getBoolean(buffer);
		}
		for (int i = 0; i < hopsWatched.length; i++) {
			hopsWatched[i] = ByteBufferUtil.getBoolean(buffer);
		}
	}

	/* planting the seeds */

	public void modifyStage(int i) {
		HopsData hopsData = HopsData.forId(hopsSeeds[i]);
		if (hopsData == null) {
			return;
		}
		long difference = player.elapsedMinutes - hopsTimer[i];
		long growth = hopsData.getGrowthTime();
		int nbStates = hopsData.getEndingState() - hopsData.getStartingState();
		int state = (int) (difference * nbStates / growth);
		hopsStages[i] = 4 + state;
		updateHopsStates();
	}

	/* harvesting the plant resulted */

	public boolean plantSeed(int objectX, int objectY, final int seedId) {
		final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(new Location(objectX, objectY));
		final HopsData hopsData = HopsData.forId(seedId);
		if (hopsFieldsData == null || hopsData == null) {
			return false;
		}
		if (hopsStages[hopsFieldsData.getHopsIndex()] != 3) {
			player.sendMessage("You can't plant a seed here.");
			return false;
		}
		if (hopsData.getLevelRequired() > player.getSkill().getLevel(Skill.FARMING)) {
			SimpleDialogues.sendStatement(player, "You need a farming level of " + hopsData.getLevelRequired() + " to plant this seed.");
			return true;
		}
		if (!player.getInventory().contains(FarmingConstants.SEED_DIBBER)) {
			SimpleDialogues.sendStatement(player, "You need a seed dibber to plant seed here.");
			return true;
		}
		if (player.getInventory().getCount(hopsData.getSeedId()) < hopsData.getSeedAmount()) {
			SimpleDialogues.sendStatement(player, "You need atleast " + hopsData.getSeedAmount() + " seeds to plant here.");
			return true;
		}
		player.playAnimation(new Animation(FarmingConstants.SEED_DIBBING));
		hopsStages[hopsFieldsData.getHopsIndex()] = 4;
		player.getInventory().remove(new Item(seedId, hopsData.getSeedAmount()));
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				hopsState[hopsFieldsData.getHopsIndex()] = 0;
				hopsSeeds[hopsFieldsData.getHopsIndex()] = seedId;
				hopsTimer[hopsFieldsData.getHopsIndex()] = player.elapsedMinutes;
				player.getSkill().addExperience(Skill.FARMING, hopsData.getPlantingXp() * Constants.FARMING_EXPERIENCE);
				Hops.this.updateHopsStates();
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* putting compost onto the plant */

	public boolean putCompost(int objectX, int objectY, final int itemId) {
		if (itemId != 6032 && itemId != 6034) {
			return false;
		}
		final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(new Location(objectX, objectY));
		if (hopsFieldsData == null) {
			return false;
		}
		if (hopsStages[hopsFieldsData.getHopsIndex()] != 3 || hopsState[hopsFieldsData.getHopsIndex()] == 5) {
			player.sendMessage("This patch doesn't need compost.");
			return false;
		}
		player.getInventory().remove(new Item(itemId));
		player.getInventory().add(new Item(1925));
		player.sendMessage("You pour some " + (itemId == 6034 ? "super" : "") + "compost on the patch.");
		player.playAnimation(new Animation(FarmingConstants.PUTTING_COMPOST));
		player.getSkill().addExperience(Skill.FARMING, itemId == 6034 ? Compost.SUPER_COMPOST_EXP_USE : Compost.COMPOST_EXP_USE * Constants.FARMING_EXPERIENCE);
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(7) {

			@Override
			public void execute() {
				diseaseChance[hopsFieldsData.getHopsIndex()] *= itemId == 6032 ? Hops.COMPOST_CHANCE : Hops.SUPERCOMPOST_CHANCE;
				hopsState[hopsFieldsData.getHopsIndex()] = 5;
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* inspecting a plant */

	public void resetAll() {
		this.resetHops();
	}

	/* opening the corresponding guide about the patch */

	private void resetHops() {
		hopsStages = new int[4];
		hopsSeeds = new int[4];
		hopsHarvest = new int[4];
		hopsState = new int[4];
		hopsTimer = new long[4];
		hasFullyGrown = new boolean[] { false, false, false, false };
		hopsWatched = new boolean[] { false, false, false, false };
	}

	/* Curing the plant */

	private void resetHops(int index) {
		hopsSeeds[index] = 0;
		hopsState[index] = 0;
		diseaseChance[index] = 1;
		hopsHarvest[index] = 0;
		hasFullyGrown[index] = false;
		hopsWatched[index] = false;
	}

	public void saveStages(ByteBuffer buffer) {
		for (Integer i : hopsStages) {
			buffer.putInt(i);
		}
		for (Integer i : hopsSeeds) {
			buffer.putInt(i);
		}
		for (Integer i : hopsHarvest) {
			buffer.putInt(i);
		}
		for (Integer i : hopsState) {
			buffer.putInt(i);
		}
		for (Long i : hopsTimer) {
			buffer.putLong(i);
		}
		for (Double i : diseaseChance) {
			buffer.putDouble(i);
		}
		for (Boolean i : hasFullyGrown) {
			ByteBufferUtil.putBoolean(buffer, i);
		}
		for (Boolean i : hopsWatched) {
			ByteBufferUtil.putBoolean(buffer, i);
		}
	}

	public void updateHopsStates() {
		// lumbridge - mc grubor - yanille - entrana
		int[] configValues = new int[hopsStages.length];
		int configValue;
		for (int i = 0; i < hopsStages.length; i++) {
			configValues[i] = getConfigValue(hopsStages[i], hopsSeeds[i], hopsState[i], i);
		}
		configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
		player.getSession().send(new SendConfigPacket(Hops.MAIN_HOPS_CONFIG, configValue));
	}

	/* checking if the patch is raked */

	public boolean waterPatch(int objectX, int objectY, int itemId) {
		final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(new Location(objectX, objectY));
		if (hopsFieldsData == null) {
			return false;
		}
		HopsData hopsData = HopsData.forId(hopsSeeds[hopsFieldsData.getHopsIndex()]);
		if (hopsData == null) {
			return false;
		}
		if (hopsState[hopsFieldsData.getHopsIndex()] == 1 || hopsStages[hopsFieldsData.getHopsIndex()] <= 1
				|| hopsStages[hopsFieldsData.getHopsIndex()] == hopsData.getEndingState() - hopsData.getStartingState() + 4) {
			player.sendMessage("This patch doesn't need watering.");
			return true;
		}
		player.getInventory().remove(new Item(itemId));
		player.getInventory().add(new Item(itemId == 5333 ? itemId - 2 : itemId - 1));
		if (!player.getInventory().contains(FarmingConstants.RAKE)) {
			SimpleDialogues.sendStatement(player, "You need a seed dibber to plant seed here.");
			return true;
		}
		player.sendMessage("You water the patch.");
		player.playAnimation(new Animation(FarmingConstants.WATERING_CAN_ANIM));
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(5) {

			@Override
			public void execute() {
				diseaseChance[hopsFieldsData.getHopsIndex()] *= Hops.WATERING_CHANCE;
				hopsState[hopsFieldsData.getHopsIndex()] = 1;
				Hops.this.updateHopsStates();
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}
}
