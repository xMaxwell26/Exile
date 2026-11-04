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
import com.koios.game.content.skill.woodcutting.Woodcutting;
import com.koios.game.content.skill.woodcutting.WoodcuttingData.HatchetData;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.task.Task;
import com.koios.util.ByteBufferUtil;

public class FruitTree {

	public enum FruitTreeData {
		APPLE(5283, 1955, 1, 27, new int[] { 5986, 9 }, 120, 0.20, 22, 8.5, 0x08, 0x14, 0x0e, 0x21, 0x22, 1199.5, 12, 18), BANANA(5284, 1963, 1, 33, new int[] { 5386, 4 }, 120,
				0.20, 28, 10.5, 0x23, 0x2f, 0x29, 0x3c, 0x3d, 1750.5, 12, 18), ORANGE(5285, 2108, 1, 39, new int[] { 5406, 3 }, 120, 0.20, 35.5, 13.5, 0x48, 0x54, 0x4e, 0x61, 0x62,
						2470.2, 12, 18), CURRY(5286, 5970, 1, 42, new int[] { 5416, 5 }, 120, 0.25, 40, 15, 0x63, 0x6f, 0x69, 0x7c, 0x7d, 2906.9, 12, 18), PINEAPPLE(5287, 2114, 1,
								51, new int[] { 5982, 10 }, 120, 0.25, 57, 21.5, 0x88, 0x94, 0x8e, 0xa1, 0xa2, 4605.7, 12, 18), PAPAYA(5288, 5972, 1, 57, new int[] { 2114, 10 },
										120, 0.25, 72, 27, 0xa3, 0xaf, 0xa9, 0xbc, 0xbd, 6146.4, 12,
										18), PALM(5289, 5974, 1, 68, new int[] { 5972, 15 }, 120, 0.25, 170.5, 41.5, 0xc8, 0xd4, 0xce, 0xe1, 0xe2, 10150.1, 12, 18);

		public static FruitTreeData forId(int saplingId) {
			return FruitTreeData.saplings.get(saplingId);
		}

		private int saplingId;
		private int harvestId;
		private int saplingAmount;
		private int levelRequired;
		private int[] paymentToWatch;
		private int growthTime;
		private double diseaseChance;
		private double plantingXp;
		private double harvestXp;
		private int startingState;
		private int endingState;
		private int limitState;
		private int stumpState;
		private int checkHealthState;
		private double checkHealthExperience;
		private int diseaseDiffValue;
		private int deathDiffValue;

		private static Map<Integer, FruitTreeData> saplings = new HashMap<>();

		static {
			for (FruitTreeData data : FruitTreeData.values()) {
				FruitTreeData.saplings.put(data.saplingId, data);
			}
		}

		FruitTreeData(int saplingId, int harvestId, int saplingAmount, int levelRequired, int[] paymentToWatch, int growthTime, double diseaseChance, double plantingXp,
				double harvestXp, int startingState, int endingState, int limitState, int stumpState, int checkHealthState, double checkHealthExperience, int diseaseDiffValue,
				int deathDiffValue) {
			this.saplingId = saplingId;
			this.harvestId = harvestId;
			this.saplingAmount = saplingAmount;
			this.levelRequired = levelRequired;
			this.paymentToWatch = paymentToWatch;
			this.growthTime = growthTime;
			this.diseaseChance = diseaseChance;
			this.plantingXp = plantingXp;
			this.harvestXp = harvestXp;
			this.startingState = startingState;
			this.endingState = endingState;
			this.limitState = limitState;
			this.stumpState = stumpState;
			this.checkHealthState = checkHealthState;
			this.checkHealthExperience = checkHealthExperience;
			this.diseaseDiffValue = diseaseDiffValue;
			this.deathDiffValue = deathDiffValue;
		}

		public int getCheckHealthState() {
			return checkHealthState;
		}

		public double getCheckHealthXp() {
			return checkHealthExperience;
		}

		public int getDeathDiffValue() {
			return deathDiffValue;
		}

		public double getDiseaseChance() {
			return diseaseChance;
		}

		public int getDiseaseDiffValue() {
			return diseaseDiffValue;
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

		public int getLimitState() {
			return limitState;
		}

		public int[] getPaymentToWatch() {
			return paymentToWatch;
		}

		public double getPlantingXp() {
			return plantingXp;
		}

		public int getSapplingAmount() {
			return saplingAmount;
		}

		public int getSapplingId() {
			return saplingId;
		}

		public int getStartingState() {
			return startingState;
		}

		public int getStumpState() {
			return stumpState;
		}
	}

	public enum FruitTreeFieldsData {
		BRIMHAVEN(0, new Location[] { new Location(2764, 3212), new Location(2765, 3213) }, 2330), CATHERBY(1,
				new Location[] { new Location(2860, 3433), new Location(2861, 3434) }, 2331), TREE_STRONGHOLD(2,
						new Location[] { new Location(2475, 3445), new Location(2476, 3446) },
						2343), TREE_VILLAGE(3, new Location[] { new Location(2489, 3179), new Location(2890, 3180) }, 2344);

		public static FruitTreeFieldsData forId(int npcId) {
			return FruitTreeFieldsData.npcsProtecting.get(npcId);
		}

		public static FruitTreeFieldsData forIdPosition(Location position) {
			for (FruitTreeFieldsData fruitTreeFieldsData : FruitTreeFieldsData.values()) {
				if (FarmingConstants.inRangeArea(fruitTreeFieldsData.getFruitTreePosition()[0], fruitTreeFieldsData.getFruitTreePosition()[1], position)) {
					return fruitTreeFieldsData;
				}
			}
			return null;
		}

		private int fruitTreeIndex;
		private Location[] fruitTreePosition;

		private int npcId;

		private static Map<Integer, FruitTreeFieldsData> npcsProtecting = new HashMap<>();

		static {
			for (FruitTreeFieldsData data : FruitTreeFieldsData.values()) {
				FruitTreeFieldsData.npcsProtecting.put(data.npcId, data);
			}
		}

		FruitTreeFieldsData(int fruitTreeIndex, Location[] fruitTreePosition, int npcId) {
			this.fruitTreeIndex = fruitTreeIndex;
			this.fruitTreePosition = fruitTreePosition;
			this.npcId = npcId;
		}

		public int getFruitTreeIndex() {
			return fruitTreeIndex;
		}

		public Location[] getFruitTreePosition() {
			return fruitTreePosition;
		}

		public int getNpcId() {
			return npcId;
		}
	}

	public enum InspectData {
		APPLE(5496, new String[][] { { "The apple sapling has only just been planted." }, { "The apple sapling grows into a small stump." },
				{ "The apple stump grows a little larger." }, { "The apple tree grows a small canopy." }, { "The apple tree grows a second small canopy." },
				{ "The apple tree grows larger." }, { "The apple tree is ready to be harvested." }, }), BANANA(
						5497,
						new String[][] { { "The banana sapling has only just been planted." }, { "The banana sapling grows 3 segments high, with 2 leaves." },
								{ "The banana tree grows 2 more leaves." }, { "The banana tree grows 5 segments high, and has some small bananas." },
								{ "The banana tree grows a bit larger." }, { "The banana tree grows a bit larger." }, { "The banana tree is ready to be harvested." }, }), ORANGE(
										5498,
										new String[][] { { "The orange sapling has only just been planted." }, { "The orange sapling grows slightly taller." },
												{ "The orange sapling grows even taller." }, { "The orange tree grows a small canopy." }, { "The orange tree grows taller." },
												{ "The orange tree grows wider and taller." }, { "The oranges on the tree are ready to be harvested." } }), CURRY(
														5499,
														new String[][] { { "The curry sapling has only just been planted." }, {
																"The curry trunk grows towards the north." }, { "The curry trunk grows towards the north." },
														{ "The curry tree grows upwards." }, { "The curry trunk grows towards the south." },
														{ "The curry trunk grows towards the south." }, { "The curry tree is ready to be harvested." } }), PINEAPPLE(
																5500,
																new String[][] { { "The pineapple sapling has only just been planted." }, { "The pineapple plant grows larger." }, {
																		"The pineapple plant base turns brown." }, { "The pineapple plant grows larger." },
														{ "The pineapple plant grows larger." }, { "The pineapple plant grows larger." },
														{ "The pineapple plant is ready to be harvested." } }), PAPAYA(
																5501,
																new String[][] { { "The papaya sapling has only just been planted." },
																		{ "The papaya sapling grows a little larger." }, { "The papaya tree grows a little larger." },
																		{ "The papaya tree grows a bit larger." }, { "The papaya tree grows some small yellow fruit." },
																		{ "The papaya tree grows larger." }, { "The papaya tree is ready to be harvested." } }), PALM(
																				5502,
																				new String[][] { { "The palm sapling has only just been planted." },
																						{ "The palm sapling grows a little larger." }, { "The palm stump grows a little larger." },
																						{ "The palm tree grows a small canopy." }, { "The palm tree grows taller." },
																						{ "The palm tree grows more leaves." }, { "The palm tree is ready to be harvested." } });

		public static InspectData forId(int saplingId) {
			return InspectData.saplings.get(saplingId);
		}

		private int saplingId;
		private String[][] messages;

		private static Map<Integer, InspectData> saplings = new HashMap<>();

		static {
			for (InspectData data : InspectData.values()) {
				InspectData.saplings.put(data.saplingId, data);
			}
		}

		InspectData(int saplingId, String[][] messages) {
			this.saplingId = saplingId;
			this.messages = messages;
		}

		public String[][] getMessages() {
			return messages;
		}

		public int getSapplingId() {
			return saplingId;
		}
	}

	/* getting the plant states */
	public static int getPlantState(int plantState, FruitTreeData fruitTreeData, int fruitTreeStage) {
		// int value = fruitTreeData.getStartingState() + fruitTreeStage - 4;
		int value = fruitTreeData.getStartingState() + fruitTreeStage - 4;

		switch (plantState) {
		case 0:
			return value;

		case 1:
			return value + fruitTreeData.getDiseaseDiffValue();

		case 2:
			return value + fruitTreeData.getDeathDiffValue();

		case 3:
			return fruitTreeData.getCheckHealthState();
		}

		return -1;
	}

	public static boolean guide(int x, int y) {
		FruitTreeFieldsData fruitTreeFieldsData = FruitTreeFieldsData.forIdPosition(new Location(x, y));
		return fruitTreeFieldsData != null;
	}

	private final Player player;
	// set of global constants for Farming
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;
	// Farming data
	public int[] fruitTreeStages = new int[4];
	public int[] fruitTreeSaplings = new int[4];
	public int[] fruitTreeState = new int[4];

	public long[] fruitTreeTimer = new long[4];

	public double[] diseaseChance = { 1, 1, 1, 1 };

	public boolean[] hasFullyGrown = { false, false, false, false };

	/* This is the enum holding the seeds info */

	public boolean[] fruitTreeWatched = { false, false, false, false, false, false, false, false };

	/* This is the enum data about the different patches */

	public static final int MAIN_FRUIT_TREE_CONFIG = 503;

	/* This is the enum that hold the different data for inspecting the plant */

	public FruitTree(Player player) {
		this.player = player;
	}

	/* checking if the patch is raked */
	public boolean checkIfRaked(int objectX, int objectY) {
		FruitTreeFieldsData fruitTreeFieldsData = FruitTreeFieldsData.forIdPosition(new Location(objectX, objectY));
		if (fruitTreeFieldsData == null) {
			return false;
		}

		if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] == 3) {
			return true;
		}

		return false;
	}

	/* update all the patch states */

	public boolean clearPatch(int objectX, int objectY, int itemId) {
		final FruitTreeFieldsData fruitTreeFieldsData = FruitTreeFieldsData.forIdPosition(new Location(objectX, objectY));
		int finalAnimation;
		int finalDelay;

		if (fruitTreeFieldsData == null || itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE) {
			return false;
		}

		if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] == 3) {
			return true;
		}

		if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] <= 3) {
			if (!player.getInventory().contains(FarmingConstants.RAKE)) {
				SimpleDialogues.sendStatement(player, "You need a rake to clear this path.");
				return true;
			} else {
				finalAnimation = FarmingConstants.RAKING_ANIM;
				finalDelay = 5;
			}
		} else if (!player.getInventory().contains(FarmingConstants.SPADE)) {
			SimpleDialogues.sendStatement(player, "You need a spade to clear this path.");
			return true;
		} else {
			finalAnimation = FarmingConstants.SPADE_ANIM;
			finalDelay = 3;
		}

		final int animation = finalAnimation;
		player.setStopPacket(true);
		player.playAnimation(new Animation(animation));
		int times = 3 - fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()];

		GameService.getTaskScheduler().schedule(new Task(finalDelay, times) {

			@Override
			public void execute() {
				player.playAnimation(new Animation(animation));

				if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] <= 2) {
					fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()]++;
					player.getInventory().add(new Item(6055));
				} else {
					fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] = 3;
					FruitTree.this.resetFruitTrees(fruitTreeFieldsData.getFruitTreeIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
				}

				player.getSkill().addExperience(Skill.FARMING, FruitTree.CLEARING_EXPERIENCE * Constants.FARMING_EXPERIENCE);
				fruitTreeTimer[fruitTreeFieldsData.getFruitTreeIndex()] = player.elapsedMinutes;
				FruitTree.this.updateFruitTreeStates();

				if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] == 3) {
					FruitTree.this.resetFruitTrees(fruitTreeFieldsData.getFruitTreeIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
				}
			}

		});

		return true;
	}

	/**
	 * Woodcutting action
	 *
	 * @param x tree x location
	 * @param y tree y location
	 */
	public boolean cut(int x, int y) {
		final FruitTreeFieldsData fruitTreeFieldsData = FruitTreeFieldsData.forIdPosition(new Location(x, y));

		if (fruitTreeFieldsData == null) {
			return false;
		}

		FruitTreeData fruitTreeData = FruitTreeData.forId(fruitTreeSaplings[fruitTreeFieldsData.getFruitTreeIndex()]);

		if (fruitTreeData == null) {
			return false;
		}

		if (player.getInventory().freeSlots() <= 0) {
			player.sendMessage("Not enough space in your inventory.");
			return true;
		}

		// TODO check with J
		/* if (WoodCutting.hasAxe(player) == -1) { return true; } */

		if (Woodcutting.getPlayerAxe(player) <= 0) {
			player.sendMessage("You do not have an axe which you have the woodcutting level to use.");
			return true;
		}

		player.sendMessage("You swing your axe at the tree.");
		int emoteId = HatchetData.forAxe(Woodcutting.getPlayerAxe(player)).getAxeAnimation();
		player.playAnimation(new Animation(emoteId));
		player.stopTask();

		player.setSkilling(new Task(5) {

			@Override
			public void execute() {
				player.sendMessage("You cut down the tree.");
				fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] = 6;
				FruitTree.this.updateFruitTreeStates();
				player.playAnimation(new Animation(-1));
				stop();
			}

		});

		return true;
	}

	public void doCalculations() {
		for (int i = 0; i < fruitTreeSaplings.length; i++) {
			if (fruitTreeStages[i] > 0 && fruitTreeStages[i] <= 3 && player.elapsedMinutes - fruitTreeTimer[i] >= 5) {
				fruitTreeStages[i]--;
				fruitTreeTimer[i] = player.elapsedMinutes;
				updateFruitTreeStates();
				continue;
			}

			FruitTreeData fruitTreeData = FruitTreeData.forId(fruitTreeSaplings[i]);

			if (fruitTreeData == null) {
				continue;
			}

			long difference = player.elapsedMinutes - fruitTreeTimer[i];
			long growth = fruitTreeData.getGrowthTime();
			int nbStates = fruitTreeData.getEndingState() - fruitTreeData.getStartingState();
			int state = (int) (difference * nbStates / growth);

			if (fruitTreeTimer[i] == 0 || fruitTreeState[i] == 2 || fruitTreeState[i] == 3) {
				continue;
			}

			if (state > nbStates) {
				state = nbStates;
			}

			if (4 + state != fruitTreeStages[i]) {
				if (fruitTreeStages[i] + fruitTreeData.getStartingState() == fruitTreeData.getLimitState() + 3) {
					fruitTreeStages[i] = fruitTreeData.getEndingState() - fruitTreeData.getStartingState() + 7;
					fruitTreeState[i] = 3;
					updateFruitTreeStates();
					continue;
				}

				fruitTreeStages[i] = 4 + state;

				if (fruitTreeStages[i] <= 4 + state) {
					for (int j = fruitTreeStages[i]; j <= 4 + state; j++) {
						doStateCalculation(i);
					}
				}

				updateFruitTreeStates();
			}
		}
	}

	public void doStateCalculation(int index) {
		if (fruitTreeState[index] == 2) {
			return;
		}

		// if the patch is diseased, it dies, if its watched by a farmer, it
		// goes back to normal
		if (fruitTreeState[index] == 1) {
			fruitTreeState[index] = 0;
			FruitTreeData bushesData = FruitTreeData.forId(fruitTreeSaplings[index]);

			if (bushesData == null) {
				return;
			}

			int difference = bushesData.getEndingState() - bushesData.getStartingState();
			int growth = bushesData.getGrowthTime();
			fruitTreeTimer[index] += growth / difference;
			modifyStage(index);
		}

		if (fruitTreeState[index] == 5 && fruitTreeStages[index] != 2) {
			fruitTreeState[index] = 0;
		}

		if (fruitTreeState[index] == 0 && fruitTreeStages[index] >= 5 && !hasFullyGrown[index]) {
			FruitTreeData fruitTreeData = FruitTreeData.forId(fruitTreeSaplings[index]);

			if (fruitTreeData == null) {
				return;
			}

			// double chance = diseaseChance[index] *
			// fruitTreeData.getDiseaseChance();
			// int maxChance = (int) chance * 100;
			/* if (RandomGenerator.random(100) <= maxChance) { fruitTreeState[index] = 1; } */
		}
	}

	/* calculating the disease chance and making the plant grow */

	/* getting the different config values */
	public int getConfigValue(int fruitTreeStage, int saplingId, int plantState, int index) {
		FruitTreeData fruitTreeData = FruitTreeData.forId(saplingId);

		switch (fruitTreeStage) {
		case 0:// weed
			return 0x00;

		case 1:// weed cleared
			return 0x01;

		case 2:
			return 0x02;

		case 3:
			return 0x03;
		}

		if (fruitTreeData == null) {
			return -1;
		}

		if (fruitTreeStage > fruitTreeData.getEndingState() - fruitTreeData.getStartingState() - 1) {
			hasFullyGrown[index] = true;
		}

		if (plantState == 6) {
			return fruitTreeData.getStumpState();
		}

		if (FruitTree.getPlantState(plantState, fruitTreeData, fruitTreeStage) == 3) {
			return fruitTreeData.getCheckHealthState();
		}

		return FruitTree.getPlantState(plantState, fruitTreeData, fruitTreeStage);
	}

	/* harvesting the plant resulted */
	public boolean harvestOrCheckHealth(int objectX, int objectY) {
		FruitTreeFieldsData fruitTreeFieldsData = FruitTreeFieldsData.forIdPosition(new Location(objectX, objectY));

		if (fruitTreeFieldsData == null) {
			return false;
		}

		final FruitTreeData fruitTreeData = FruitTreeData.forId(fruitTreeSaplings[fruitTreeFieldsData.getFruitTreeIndex()]);

		if (fruitTreeData == null) {
			return false;
		}

		if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] + fruitTreeData.getStartingState() == fruitTreeData.getLimitState() + 4) {
			cut(objectX, objectY);
			return true;
		}

		if (player.getInventory().freeSlots() <= 0) {
			player.sendMessage("Not enough space in your inventory.");
			return true;
		}

		player.playAnimation(new Animation(832));
		player.stopTask();
		player.setStopPacket(true);

		player.setSkilling(new Task(2) {

			@Override
			public void execute() {
				if (fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] == 3) {
					player.sendMessage("You examine the tree for signs of disease and find that it's in perfect health.");
					player.getSkill().addExperience(Skill.FARMING, fruitTreeData.getCheckHealthXp() * Constants.FARMING_EXPERIENCE);
					fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] = 0;
					hasFullyGrown[fruitTreeFieldsData.getFruitTreeIndex()] = false;
					fruitTreeTimer[fruitTreeFieldsData.getFruitTreeIndex()] = player.elapsedMinutes - fruitTreeData.getGrowthTime();
					FruitTree.this.modifyStage(fruitTreeFieldsData.getFruitTreeIndex());
					player.setStopPacket(false);
					stop();
					return;
				}

				player.sendMessage("You harvest the crop, and pick a fruit.");
				player.getInventory().add(new Item(fruitTreeData.getHarvestId()));
				player.getSkill().addExperience(Skill.FARMING, fruitTreeData.getHarvestXp() * Constants.FARMING_EXPERIENCE);
				fruitTreeTimer[fruitTreeFieldsData.getFruitTreeIndex()] = player.elapsedMinutes;

				int difference = fruitTreeData.getEndingState() - fruitTreeData.getStartingState();
				int growth = fruitTreeData.getGrowthTime();

				FruitTree.this.lowerStage(fruitTreeFieldsData.getFruitTreeIndex(),
						growth - growth / difference * (difference + 5 - fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()]));
				FruitTree.this.modifyStage(fruitTreeFieldsData.getFruitTreeIndex());
				player.setStopPacket(false);
				stop();
			}

		});

		return true;
	}

	public boolean inspect(int objectX, int objectY) {
		FruitTreeFieldsData fruitTreeFieldsData = FruitTreeFieldsData.forIdPosition(new Location(objectX, objectY));

		if (fruitTreeFieldsData == null) {
			return false;
		}

		InspectData inspectData = InspectData.forId(fruitTreeSaplings[fruitTreeFieldsData.getFruitTreeIndex()]);
		FruitTreeData fruitTreeData = FruitTreeData.forId(fruitTreeSaplings[fruitTreeFieldsData.getFruitTreeIndex()]);

		if (fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] == 1) {
			SimpleDialogues.sendStatement(player, "This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
			return true;
		} else if (fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] == 2) {
			SimpleDialogues.sendStatement(player, "This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
			return true;
		} else if (fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This plant has fully grown. You can check it's health", "to gain some farming experiences.");
			return true;
		} else if (fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] == 6) {
			SimpleDialogues.sendStatement(player, "This is a fruit tree stump, to remove it, use a ", "spade on it to clear the patch");
			return true;
		}
		if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] == 0) {
			SimpleDialogues.sendStatement(player, "This is a fruit tree patch. The soil has not been treated.", "The patch needs weeding.");
		} else if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This is a fruit tree patch. The soil has not been treated.", "The patch is empty and weeded.");
		} else if (inspectData != null && fruitTreeData != null) {
			player.sendMessage("You bend down and start to inspect the patch...");
			player.playAnimation(new Animation(1331));
			player.setStopPacket(true);

			GameService.getTaskScheduler().schedule(new Task(5) {

				@Override
				public void execute() {
					if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] - 4 < inspectData.getMessages().length - 2) {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] - 4]);
					} else if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] < fruitTreeData.getEndingState() - fruitTreeData.getStartingState() + 2) {
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

	/* calculations about the diseasing chance */

	public void loadStages(ByteBuffer buffer) {
		for (int i = 0; i < fruitTreeStages.length; i++) {
			fruitTreeStages[i] = buffer.getInt();
		}
		for (int i = 0; i < fruitTreeSaplings.length; i++) {
			fruitTreeSaplings[i] = buffer.getInt();
		}
		for (int i = 0; i < fruitTreeState.length; i++) {
			fruitTreeState[i] = buffer.getInt();
		}
		for (int i = 0; i < fruitTreeTimer.length; i++) {
			fruitTreeTimer[i] = buffer.getLong();
		}
		for (int i = 0; i < diseaseChance.length; i++) {
			diseaseChance[i] = buffer.getDouble();
		}
		for (int i = 0; i < hasFullyGrown.length; i++) {
			hasFullyGrown[i] = ByteBufferUtil.getBoolean(buffer);
		}
		for (int i = 0; i < fruitTreeWatched.length; i++) {
			fruitTreeWatched[i] = ByteBufferUtil.getBoolean(buffer);
		}
	}

	/* clearing the patch with a rake of a spade */

	public void lowerStage(int index, int timer) {
		hasFullyGrown[index] = false;
		fruitTreeTimer[index] -= timer;
	}

	/* planting the saplings */

	public void modifyStage(int i) {
		FruitTreeData fruitTreeData = FruitTreeData.forId(fruitTreeSaplings[i]);

		if (fruitTreeData == null) {
			return;
		}

		long difference = player.elapsedMinutes - fruitTreeTimer[i];
		long growth = fruitTreeData.getGrowthTime();
		int nbStates = fruitTreeData.getEndingState() - fruitTreeData.getStartingState();
		int state = (int) (difference * nbStates / growth);
		fruitTreeStages[i] = 4 + state;
		updateFruitTreeStates();
	}

	public boolean plantSapling(int objectX, int objectY, final int saplingId) {
		FruitTreeFieldsData fruitTreeFieldsData = FruitTreeFieldsData.forIdPosition(new Location(objectX, objectY));
		FruitTreeData fruitTreeData = FruitTreeData.forId(saplingId);

		if (fruitTreeFieldsData == null || fruitTreeData == null) {
			return false;
		}

		if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] != 3) {
			player.sendMessage("You can't plant a sapling here.");
			return true;
		}

		if (fruitTreeData.getLevelRequired() > player.getSkill().getLevel(Skill.FARMING)) {
			SimpleDialogues.sendStatement(player, "You need a farming level of " + fruitTreeData.getLevelRequired() + " to plant this sapling.");
			return true;
		}

		if (!player.getInventory().contains(FarmingConstants.TROWEL)) {
			SimpleDialogues.sendStatement(player, "You need a trowel to plant the sapling here.");
			return true;
		}

		player.playAnimation(new Animation(FarmingConstants.PLANTING_POT_ANIM));
		fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] = 4;
		player.getInventory().remove(new Item(saplingId));
		player.setStopPacket(true);

		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] = 0;
				fruitTreeSaplings[fruitTreeFieldsData.getFruitTreeIndex()] = saplingId;
				fruitTreeTimer[fruitTreeFieldsData.getFruitTreeIndex()] = player.elapsedMinutes;
				player.getSkill().addExperience(Skill.FARMING, fruitTreeData.getPlantingXp() * Constants.FARMING_EXPERIENCE);
				FruitTree.this.updateFruitTreeStates();
				player.setStopPacket(false);
			}

		});

		return true;
	}

	/* lowering the stage */

	public boolean pruneArea(int objectX, int objectY, int itemId) {
		FruitTreeFieldsData fruitTreeFieldsData = FruitTreeFieldsData.forIdPosition(new Location(objectX, objectY));

		if (fruitTreeFieldsData == null || itemId != FarmingConstants.SECATEURS && itemId != FarmingConstants.MAGIC_SECATEURS) {
			return false;
		}

		FruitTreeData fruitTreeData = FruitTreeData.forId(fruitTreeSaplings[fruitTreeFieldsData.getFruitTreeIndex()]);

		if (fruitTreeData == null) {
			return false;
		}

		if (fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] != 1) {
			player.sendMessage("This area doesn't need to be pruned.");
			return true;
		}

		player.playAnimation(new Animation(FarmingConstants.PRUNING_ANIM));
		player.setStopPacket(true);
		fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] = 0;

		GameService.getTaskScheduler().schedule(new Task(15) {

			@Override
			public void execute() {
				player.sendMessage("You prune the area with your secateurs.");
				FruitTree.this.updateFruitTreeStates();
				player.setStopPacket(false);
			}

		});

		return true;
	}

	/* putting compost onto the plant */

	public boolean putCompost(int objectX, int objectY, final int itemId) {
		if (itemId != 6032 && itemId != 6034) {
			return false;
		}

		FruitTreeFieldsData fruitTreeFieldsData = FruitTreeFieldsData.forIdPosition(new Location(objectX, objectY));

		if (fruitTreeFieldsData == null) {
			return false;
		}

		if (fruitTreeStages[fruitTreeFieldsData.getFruitTreeIndex()] != 3 || fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] == 5) {
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
				diseaseChance[fruitTreeFieldsData.getFruitTreeIndex()] *= itemId == 6032 ? FruitTree.COMPOST_CHANCE : FruitTree.SUPERCOMPOST_CHANCE;
				fruitTreeState[fruitTreeFieldsData.getFruitTreeIndex()] = 5;
				player.setStopPacket(false);
			}

		});

		return true;
	}

	/* inspecting a plant */

	public void resetAll() {
		this.resetFruitTrees();
	}

	/* opening the corresponding guide about the patch */

	private void resetFruitTrees() {
		fruitTreeStages = new int[4];
		fruitTreeSaplings = new int[4];
		fruitTreeState = new int[4];
		fruitTreeTimer = new long[4];
		hasFullyGrown = new boolean[] { false, false, false, false };
		fruitTreeWatched = new boolean[] { false, false, false, false, false, false, false, false };
	}

	/* Curing the plant */

	private void resetFruitTrees(int index) {
		fruitTreeSaplings[index] = 0;
		fruitTreeState[index] = 0;
		diseaseChance[index] = 1;
		hasFullyGrown[index] = false;
		fruitTreeWatched[index] = false;
	}

	public void saveStages(ByteBuffer buffer) {
		for (Integer i : fruitTreeStages) {
			buffer.putInt(i);
		}
		for (Integer i : fruitTreeSaplings) {
			buffer.putInt(i);
		}
		for (Integer i : fruitTreeState) {
			buffer.putInt(i);
		}
		for (Long i : fruitTreeTimer) {
			buffer.putLong(i);
		}
		for (Double i : diseaseChance) {
			buffer.putDouble(i);
		}
		for (Boolean i : hasFullyGrown) {
			ByteBufferUtil.putBoolean(buffer, i);
		}
		for (Boolean i : fruitTreeWatched) {
			ByteBufferUtil.putBoolean(buffer, i);
		}
	}

	public void updateFruitTreeStates() {
		// brimhaven - catherby - tree stronghold - tree village
		int[] configValues = new int[fruitTreeStages.length];
		int configValue;

		for (int i = 0; i < fruitTreeStages.length; i++) {
			configValues[i] = getConfigValue(fruitTreeStages[i], fruitTreeSaplings[i], fruitTreeState[i], i);
		}

		configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
		player.getSession().send(new SendConfigPacket(FruitTree.MAIN_FRUIT_TREE_CONFIG, configValue));
	}

}