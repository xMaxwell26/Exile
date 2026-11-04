package com.koios.game.content.skill.farming;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
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

/**
 * Created by J on 25/06/2014.
 */
public class Allotments {

	public enum AllotmentData {

		POTATO(5318, 1942, 5096, 3, 1, new int[] { 6032, 2 }, 40, 0.30, 8, 9.5, 0x06, 0x0c), ONION(5319, 1957, 5096, 3, 5, new int[] { 5438, 1 }, 40, 0.30, 9.5, 10.5, 0x0d,
				0x13), CABBAGE(5324, 1965, 5097, 3, 7, new int[] { 5458, 1 }, 40, 0.25, 10, 11.5, 0x14, 0x1a), TOMATO(5322, 1982, 5096, 3, 12, new int[] { 5478, 2 }, 40, 0.25,
						12.5, 14, 0x1b, 0x21), SWEETCORN(5320, 5986, 6059, 3, 20, new int[] { 5931, 10 }, 40, 0.20, 17, 19, 0x22, 0x2a), STRAWBERRY(5323, 5504, -1, 3, 31,
								new int[] { 5386, 1 }, 40, 0.20, 26, 29, 0x2b, 0x33), WATERMELON(5321, 5982, 5098, 3, 47, new int[] { 5970, 10 }, 40, 0.20, 48.5, 54.5, 0x34, 0x3e);

		public static AllotmentData forId(int seedId) {
			return AllotmentData.values.get(seedId);
		}

		private int seedId;
		private int harvestId;
		private int flowerProtect;
		private int seedAmount;
		private int levelRequired;
		private int growthTime;
		private double plantingXp;
		private double harvestXp;
		private int startingState;
		private int endingState;

		private static final Map<Integer, AllotmentData> values = new HashMap<>();

		static {
			for (AllotmentData data : AllotmentData.values()) {
				AllotmentData.values.put(data.seedId, data);
			}
		}

		AllotmentData(int seedId, int harvestId, int flowerProtect, int seedAmount, int levelRequired, int[] paymentToWatch, int growthTime, double diseaseChance,
				double plantingXp, double harvestXp, int startingState, int endingState) {
			this.seedId = seedId;
			this.harvestId = harvestId;
			this.flowerProtect = flowerProtect;
			this.seedAmount = seedAmount;
			this.levelRequired = levelRequired;
			this.growthTime = growthTime;
			this.plantingXp = plantingXp;
			this.harvestXp = harvestXp;
			this.startingState = startingState;
			this.endingState = endingState;
		}

		public int getEndingState() {
			return endingState;
		}

		public int getFlowerProtect() {
			return flowerProtect;
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

	public enum AllotmentFieldsData {
		CATHERBY_NORTH(0, new Location[] { new Location(2805, 3466), new Location(2806, 3468), new Location(2805, 3467), new Location(2814, 3468) }, 2324), CATHERBY_SOUTH(1,
				new Location[] { new Location(2805, 3459), new Location(2806, 3461), new Location(2802, 3459), new Location(2814, 3460) }, 2324), FALADOR_NORTH_WEST(2,
						new Location[] { new Location(3050, 3307), new Location(3051, 3312), new Location(3050, 3311), new Location(3054, 3312) }, 2323), FALADOR_SOUTH_EAST(3,
								new Location[] { new Location(3055, 3303), new Location(3059, 3304), new Location(3058, 3303), new Location(3059, 3308) },
								2323), PHASMATYS_NORTH_WEST(4,
										new Location[] { new Location(3597, 3525), new Location(3598, 3530), new Location(3597, 3529), new Location(3601, 3530) },
										2326), PHASMATYS_SOUTH_EAST(5,
												new Location[] { new Location(3602, 3521), new Location(3606, 3522), new Location(3605, 3521), new Location(3606, 3526) },
												2326), ARDOUGNE_NORTH(6,
														new Location[] { new Location(2662, 3377), new Location(2663, 3379), new Location(2662, 3378), new Location(2671, 3379) },
														2325), ARDOUGNE_SOUTH(7, new Location[] { new Location(2662, 3370), new Location(2663, 3372), new Location(2662, 3370),
																new Location(2671, 3371) }, 2325);

		public static AllotmentFieldsData forIdPosition(Location position) {
			for (AllotmentFieldsData allotmentFieldsData : AllotmentFieldsData.values()) {
				if (FarmingConstants.inRangeArea(allotmentFieldsData.getAllotmentPosition()[0], allotmentFieldsData.getAllotmentPosition()[1], position)
						|| FarmingConstants.inRangeArea(allotmentFieldsData.getAllotmentPosition()[2], allotmentFieldsData.getAllotmentPosition()[3], position)) {
					return allotmentFieldsData;
				}
			}

			return null;
		}

		private int allotmentIndex;
		private Location[] allotmentPosition;
		private int farmerBelonging;

		AllotmentFieldsData(int allotmentIndex, Location[] allotmentPosition, int farmerBelonging) {
			this.allotmentIndex = allotmentIndex;
			this.allotmentPosition = allotmentPosition;
			this.farmerBelonging = farmerBelonging;
		}

		public int getAllotmentIndex() {
			return allotmentIndex;
		}

		public Location[] getAllotmentPosition() {
			return allotmentPosition;
		}

		public int getFarmerBelonging() {
			return farmerBelonging;
		}

	}

	public enum InspectData {
		POTATOES(5318,
				new String[][] { { "The potato values have only just been planted." }, { "The potato plants have grown to double their", "previous z." },
						{ "The potato plants now are the same z as the", "surrounding weeds." },
						{ "The potato plants now spread their branches wider,",
								"not growing as much as before." },
				{ "The potato plants are ready to harvest. A white", "flower at the top of each plant opens up." } }), ONIONS(
						5319,
						new String[][] { { "The onion values have only just been planted." }, { "The onions are partially visible and the stems", "have grown." }, {
								"The top of the onion of the onion plant is clear", "above the ground and the onion is white." },
						{ "The onion plant is slightly larger than before and", "the onion is cream coloured." },
						{ "The onion stalks are larger than before and the", "onion is now light and brown coloured." } }), CABBAGES(
								5324,
								new String[][] { { "The cabbage values have only just been planted,", "the cabbages are small and bright green." }, {
										"The cabbages are much larger, with more leaves", "surrounding the head." },
								{ "The cabbages are larger than before, and textures", "of leaves are now easily observable." },
								{ "The cabbage head has swollen larger, and the", "surrounding leaves are more close to the ground." },
								{ "The cabbage plants are ready to harvest. The", "centre of each cabbage head is light green coloured." } }), TOMATOES(
										5322,
										new String[][] { { "The tomato values have only just been planted." }, {
												"The tomato plants grow twice as large as before." },
										{ "The tomato plants grow larger, and small green", "tomatoes are now observable." },
										{ "The tomato plants grow thicker to hold up the", "weight of the tomatoes. The tomatoes are now light",
												"orange and slightly larger on the plant." },
										{ "The tomato plants are ready to harvest. The tomato", "plants leaves are larger and the tomatoes are", "ripe red." } }), SWEETCORNS(
												5320,
												new String[][] { { "The sweetcorn plants have only just been planted." },
														{ "The sweetcorn plants are waist tall now and are", "leafy." },
														{ "The sweetcorn plants are slightly taller than", "before and slightly thicker." },
														{ "The sweetcorn leaves are larger at the base, and", "the plants are slightly taller." },
														{ "Closed corn cobs are now observable on the", "sweetcorn plants." },
														{ "The sweetcorn plants are ready to harvest. The", "corn cobs are loot and visibly yellow." } }), STRAWBERRIES(
																5323,
																new String[][] { { "The strawberry values have only just been planted." },
																		{ "The strawberry plants have more leaves than before." },
																		{ "The strawberry plants have even more leaves and is", "slightly taller than before." },
																		{ "Each strawberry plant has opened one white", "flower each." },
																		{ "The strawberry plants are slightly larger, and", "have small strawberries visible at their bases." },
																		{ "The strawberry plants are slightly larger, opened",
																				"a second flower each, and have more strawberries." },
																		{ "The strawberry plants are ready to harvest. The",
																				"strawberries are almost as large as the flowers." } }), WATERMELONS(
																						5321,
																						new String[][] { { "The strawberry values have only just been planted." },
																								{ "The strawberry plants have more leaves than before." },
																								{ "The strawberry plants have even more leaves and is",
																										"slightly taller than before." },
																								{ "Each strawberry plant has opened one white", "flower each." },
																								{ "The strawberry plants are slightly larger, and",
																										"have small strawberries visible at their bases." },
																								{ "The strawberry plants are slightly larger, opened",
																										"a second flower each, and have more strawberries." },
																								{ "The strawberry plants are ready to harvest. The",
																										"strawberries are almost as large as the flowers." } });

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

	}

	/* getting the plant states */
	public static int getPlantState(int plantState) {
		switch (plantState) {
		case 0:
			return Allotments.GROWING;

		case 1:
			return Allotments.WATERED;

		case 2:
			return Allotments.DISEASED;

		case 3:
			return Allotments.DEAD;
		}

		return -1;
	}

	/* opening the corresponding guide about the patch */
	public static boolean guide(int objectX, int objectY) {
		AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(new Location(objectX, objectY));

		if (allotmentFieldsData == null) {
			return false;
		}

		return true;
	}

	private final Player player;
	private static final int START_HARVEST_AMOUNT = 3;

	private static final int END_HARVEST_AMOUNT = 56;

	private static final double WATERING_CHANCE = 0.5;
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final int CLEARING_EXPERIENCE = 4;
	// Farming data
	public int[] allotmentStages = new int[8];
	public int[] allotmentSeeds = new int[8];
	public int[] allotmentHarvest = new int[8];
	public int[] allotmentState = new int[8];
	public long[] allotmentTimer = new long[8];
	public double[] diseaseChance = { 1, 1, 1, 1, 1, 1, 1, 1 };
	public boolean[] allotmentWatched = { false, false, false, false, false, false, false, false };
	public boolean[] hasFullyGrown = { false, false, false, false, false, false, false, false };
	/* set of the constants for the patch */
	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;
	public static final int WATERED = 0x01;

	public static final int DISEASED = 0x02;

	public static final int DEAD = 0x03;

	public static final int FALADOR_AND_CATHERBY_CONFIG = 504;

	/* This is the enum holding the values info */

	public static final int ARDOUGNE_AND_PHASMATYS_CONFIG = 505;

	/* This is the enum data about the different patches */

	public Allotments(Player player) {
		this.player = player;
	}

	/* This is the enum that hold the different data for inspecting the plant */

	public boolean clearPatch(int objectX, int objectY, int itemId) {
		AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(new Location(objectX, objectY));
		int finalAnimation;
		int finalDelay;

		if (allotmentFieldsData == null || itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE) {
			return false;
		}

		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 3) {
			return true;
		}

		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] <= 3) {
			if (!player.getInventory().hasItem(new Item(FarmingConstants.RAKE))) {
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

		int animation = finalAnimation;
		int times = 3 - allotmentStages[allotmentFieldsData.getAllotmentIndex()];

		if (times < 0) {
			return true;
		}

		player.playAnimation(new Animation(animation));
		player.setStopPacket(true);

		GameService.getTaskScheduler().schedule(new Task(finalDelay, times) {

			@Override
			public void execute() {
				player.playAnimation(new Animation(animation));

				if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] <= 2) {
					allotmentStages[allotmentFieldsData.getAllotmentIndex()]++;
					player.getInventory().add(new Item(6055));
				} else {
					allotmentStages[allotmentFieldsData.getAllotmentIndex()] = 3;
					Allotments.this.resetAllotments(allotmentFieldsData.getAllotmentIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
				}

				player.getSkill().addExperience(Skill.FARMING, Allotments.CLEARING_EXPERIENCE * Constants.FARMING_EXPERIENCE);
				allotmentTimer[allotmentFieldsData.getAllotmentIndex()] = player.elapsedMinutes;
				Allotments.this.updateAllotmentsStates();

				if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 3) {
					Allotments.this.resetAllotments(allotmentFieldsData.getAllotmentIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
				}
			}

		});

		return true;
	}

	/* getting the different config values */

	public boolean curePlant(int objectX, int objectY, int itemId) {
		AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(new Location(objectX, objectY));

		if (allotmentFieldsData == null || itemId != 6036) {
			return false;
		}

		AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);

		if (allotmentData == null) {
			return false;
		}

		if (allotmentState[allotmentFieldsData.getAllotmentIndex()] != 2) {
			player.sendMessage("This plant doesn't need to be cured.");
			return true;
		}

		player.getInventory().remove(new Item(itemId));
		player.getInventory().remove(new Item(229));
		player.playAnimation(new Animation(FarmingConstants.CURING_ANIM));
		player.setStopPacket(true);
		allotmentState[allotmentFieldsData.getAllotmentIndex()] = 0;

		GameService.getTaskScheduler().schedule(new Task(7) {

			@Override
			public void execute() {
				player.sendMessage("You cure the plant with a plant cure.");
				Allotments.this.updateAllotmentsStates();
				player.setStopPacket(false);
				stop();
			}

		});

		return true;
	}

	/* calculating the disease chance and making the plant grow */
	public void doCalculations() {
		for (int i = 0; i < allotmentSeeds.length; i++) {
			if (allotmentStages[i] > 0 && allotmentStages[i] <= 3 && player.elapsedMinutes - allotmentTimer[i] >= 5) {
				allotmentStages[i]--;
				allotmentTimer[i] = player.elapsedMinutes;
				updateAllotmentsStates();
			}

			AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[i]);

			if (allotmentData == null) {
				continue;
			}

			long difference = player.elapsedMinutes - allotmentTimer[i];
			long growth = allotmentData.getGrowthTime();
			int nbStates = allotmentData.getEndingState() - allotmentData.getStartingState();
			int state = (int) (difference * nbStates / growth) + 1;

			if (allotmentTimer[i] == 0 || allotmentState[i] == 3) {
				continue;
			}

			if (state > nbStates) {
				state = nbStates;
			}

			if (4 + state != allotmentStages[i]) {
				allotmentStages[i] = 4 + state;

				if (allotmentStages[i] <= 4 + state) {
					for (int j = allotmentStages[i]; j <= 4 + state; j++) {
						doStateCalculation(i);
					}
				}

				updateAllotmentsStates();
			}
		}
	}

	/* calculations about the diseasing chance */
	public void doStateCalculation(int index) {
		if (allotmentState[index] == 3) {
			return;
		}

		// if the patch is diseased, it dies, if its watched by a farmer, it
		// goes back to normal
		if (allotmentState[index] == 2) {
			if (allotmentWatched[index]) {
				allotmentState[index] = 0;
				AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[index]);

				if (allotmentData == null) {
					return;
				}

				int difference = allotmentData.getEndingState() - allotmentData.getStartingState();
				int growth = allotmentData.getGrowthTime();
				allotmentTimer[index] += growth / difference;
				modifyStage(index);
			} else {
				allotmentState[index] = 3;
			}
		}

		if (allotmentState[index] == 1) {
			diseaseChance[index] *= 2;
			allotmentState[index] = 0;
		}

		if (allotmentState[index] == 5 && allotmentStages[index] != 3) {
			allotmentState[index] = 0;
		}

		if (allotmentState[index] == 0 && allotmentStages[index] >= 5 && !hasFullyGrown[index]) {
			handleFlowerProtection(index);
		}
	}

	private void fullReset() {
		allotmentStages = new int[8];
		allotmentSeeds = new int[8];
		allotmentHarvest = new int[8];
		allotmentState = new int[8];
		allotmentTimer = new long[8];
		diseaseChance = new double[] { 1, 1, 1, 1, 1, 1, 1, 1 };
		allotmentWatched = new boolean[] { false, false, false, false, false, false, false, false };
		hasFullyGrown = new boolean[] { false, false, false, false, false, false, false, false };
	}

	public int getConfigValue(int allotmentStage, int seedId, int plantState, int index) {
		AllotmentData allotmentData = AllotmentData.forId(seedId);

		switch (allotmentStage) {
		case 0:// weed
			return Allotments.GROWING << 6;

		case 1:// weed cleared
			return (Allotments.GROWING << 6) + 0x01;

		case 2:
			return (Allotments.GROWING << 6) + 0x02;

		case 3:
			return (Allotments.GROWING << 6) + 0x03;
		}

		if (allotmentData == null) {
			return -1;
		}

		if (allotmentData.getEndingState() == allotmentData.getStartingState() + allotmentStage - 1) {
			hasFullyGrown[index] = true;
		}

		return (Allotments.getPlantState(plantState) << 6) + allotmentData.getStartingState() + allotmentStage - 4;
	}

	/* watering the patch */

	public void handleFlowerProtection(int index) {
		AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[index]);

		if (allotmentData == null) {
			return;
		}

		// double chance = diseaseChance[index] *
		// allotmentData.getDiseaseChance();
		// int maxChance = (int) chance * 100;
		int indexGiven = 0;

		if (!allotmentWatched[index]) {
			// RandomGenerator.random(100) <= maxChance) {

			switch (index) {
			case 0:
			case 1:
				indexGiven = 3;
				break;

			case 2:
			case 3:
				indexGiven = 2;
				break;

			case 4:
			case 5:
				indexGiven = 1;
				break;

			case 6:
			case 7:
				indexGiven = 0;
				break;
			}

			if (player.getFlowers().flowerSeeds[indexGiven] >= 0x21 && player.getFlowers().flowerSeeds[indexGiven] <= 0x24) {
				if (allotmentData.getFlowerProtect() == Flowers.SCARECROW) {
					return;
				}
			}

			if (player.getFlowers().flowerState[indexGiven] != 3 && player.getFlowers().hasFullyGrown[indexGiven]
					&& player.getFlowers().flowerSeeds[indexGiven] == allotmentData.getFlowerProtect()) {
				player.getFlowers().flowerState[indexGiven] = 3;
				player.getFlowers().updateFlowerStates();
			} else {
				// allotmentState[index] = 2;
			}
		}
	}

	/* clearing the patch with a rake of a spade */

	/* harvesting the plant resulted */
	public boolean harvest(int objectX, int objectY) {
		AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(new Location(objectX, objectY));

		if (allotmentFieldsData == null) {
			return false;
		}

		AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);

		if (allotmentData == null) {
			return false;
		}

		if (allotmentData.getEndingState() > allotmentData.getStartingState() + allotmentStages[allotmentFieldsData.getAllotmentIndex()] - 1) {
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

		player.stopTask();
		player.playAnimation(new Animation(FarmingConstants.SPADE_ANIM));
		player.setStopPacket(true);

		GameService.getTaskScheduler().schedule(new Task(2, true) {

			@Override
			public void execute() {
				if (allotmentHarvest[allotmentFieldsData.getAllotmentIndex()] == 0) {
					allotmentHarvest[allotmentFieldsData.getAllotmentIndex()] = (int) (1
							+ (Allotments.START_HARVEST_AMOUNT + NumberUtil.random(Allotments.END_HARVEST_AMOUNT - Allotments.START_HARVEST_AMOUNT))
									* (player.getInventory().hasItem(new Item(7409)) ? 1.10 : 1));
				}

				if (allotmentHarvest[allotmentFieldsData.getAllotmentIndex()] == 1) {
					Allotments.this.resetAllotments(allotmentFieldsData.getAllotmentIndex());
					allotmentStages[allotmentFieldsData.getAllotmentIndex()] = 3;
					allotmentTimer[allotmentFieldsData.getAllotmentIndex()] = player.elapsedMinutes;
					Allotments.this.updateAllotmentsStates();
					player.setStopPacket(false);
					stop();
					return;
				}

				if (player.getInventory().freeSlots() <= 0) {
					Allotments.this.updateAllotmentsStates();
					player.setStopPacket(false);
					stop();
					return;
				}

				allotmentHarvest[allotmentFieldsData.getAllotmentIndex()]--;
				player.playAnimation(new Animation(FarmingConstants.SPADE_ANIM));
				player.sendMessage("You harvest the crop, and get some vegetables.");
				player.getInventory().add(new Item(allotmentData.getHarvestId()));
				player.getSkill().addExperience(Skill.FARMING, allotmentData.getHarvestXp() * Constants.FARMING_EXPERIENCE);
			}
		});

		return true;
	}

	/* inspecting a plant */
	public boolean inspect(int objectX, int objectY) {
		AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(new Location(objectX, objectY));

		if (allotmentFieldsData == null) {
			return false;
		}

		InspectData inspectData = InspectData.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);
		AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);

		if (allotmentState[allotmentFieldsData.getAllotmentIndex()] == 2) {
			SimpleDialogues.sendStatement(player, "This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
			return true;
		} else if (allotmentState[allotmentFieldsData.getAllotmentIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
			return true;
		}

		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 0) {
			SimpleDialogues.sendStatement(player, "This is an allotment patch. The soil has not been treated.", "The patch needs weeding.");
		} else if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This is an allotment patch. The soil has not been treated.", "The patch is empty and weeded.");
		} else if (inspectData != null && allotmentData != null) {
			player.sendMessage("You bend down and start to inspect the patch...");
			player.playAnimation(new Animation(1331));
			player.setStopPacket(true);

			GameService.getTaskScheduler().schedule(new Task(5) {

				@Override
				public void execute() {
					if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] - 4 < inspectData.getMessages().length - 2) {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[allotmentStages[allotmentFieldsData.getAllotmentIndex()] - 4]);
					} else if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] < allotmentData.getEndingState() - allotmentData.getStartingState() + 2) {
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

	public void loadStages(ByteBuffer buffer) {
		for (int i = 0; i < allotmentStages.length; i++) {
			allotmentStages[i] = buffer.getInt();
		}

		for (int i = 0; i < allotmentSeeds.length; i++) {
			allotmentSeeds[i] = buffer.getInt();
		}

		for (int i = 0; i < allotmentHarvest.length; i++) {
			allotmentHarvest[i] = buffer.getInt();
		}

		for (int i = 0; i < allotmentState.length; i++) {
			allotmentState[i] = buffer.getInt();
		}

		for (int i = 0; i < allotmentTimer.length; i++) {
			allotmentTimer[i] = buffer.getLong();
		}

		for (int i = 0; i < diseaseChance.length; i++) {
			diseaseChance[i] = buffer.getDouble();
		}

		for (int i = 0; i < allotmentWatched.length; i++) {
			allotmentWatched[i] = ByteBufferUtil.getBoolean(buffer);
		}

		for (int i = 0; i < hasFullyGrown.length; i++) {
			hasFullyGrown[i] = ByteBufferUtil.getBoolean(buffer);
		}
	}

	public void modifyStage(int i) {
		AllotmentData bushesData = AllotmentData.forId(allotmentSeeds[i]);

		if (bushesData == null) {
			return;
		}

		long difference = player.elapsedMinutes - allotmentTimer[i];
		long growth = bushesData.getGrowthTime();
		int nbStates = bushesData.getEndingState() - bushesData.getStartingState();
		int state = (int) (difference * nbStates / growth);
		allotmentStages[i] = 4 + state;
		updateAllotmentsStates();
	}

	/* planting the values */
	public boolean plantSeed(int objectX, int objectY, final int seedId) {
		AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(new Location(objectX, objectY));
		AllotmentData allotmentData = AllotmentData.forId(seedId);

		if (allotmentFieldsData == null || allotmentData == null) {
			return false;
		}

		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] != 3) {
			player.sendMessage("You can't plant a seed here.");
			return false;
		}

		if (allotmentData.getLevelRequired() > player.getSkill().getLevel(Skill.FARMING)) {
			SimpleDialogues.sendStatement(player, "You need a farming level of " + allotmentData.getLevelRequired() + " to plant this seed.");
			return true;
		}

		if (!player.getInventory().contains(FarmingConstants.SEED_DIBBER)) {
			SimpleDialogues.sendStatement(player, "You need a seed dibber to plant seed here.");
			return true;
		}

		if (player.getInventory().getCount(allotmentData.getSeedId()) < allotmentData.getSeedAmount()) {
			SimpleDialogues.sendStatement(player, "You need atleast " + allotmentData.getSeedAmount() + " values to plant here.");
			return true;
		}

		player.playAnimation(new Animation(FarmingConstants.SEED_DIBBING));
		allotmentStages[allotmentFieldsData.getAllotmentIndex()] = 4;
		player.getInventory().remove(new Item(seedId, allotmentData.getSeedAmount()));
		player.setStopPacket(true);

		if (allotmentData.getEndingState() == AllotmentData.POTATO.getEndingState()) {
			AchievementManager.performed(player, Achievement.PLANT_POTATO);
		}

		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {

				allotmentState[allotmentFieldsData.getAllotmentIndex()] = 0;
				allotmentSeeds[allotmentFieldsData.getAllotmentIndex()] = seedId;
				allotmentTimer[allotmentFieldsData.getAllotmentIndex()] = player.elapsedMinutes;
				player.getSkill().addExperience(Skill.FARMING, allotmentData.getPlantingXp() * Constants.FARMING_EXPERIENCE);
				Allotments.this.updateAllotmentsStates();
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

		AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(new Location(objectX, objectY));

		if (allotmentFieldsData == null) {
			return false;
		}

		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] != 3 || allotmentState[allotmentFieldsData.getAllotmentIndex()] == 5) {
			player.sendMessage("This patch doesn't need compost.");
			return false;
		}

		player.getInventory().add(new Item(itemId));
		player.getInventory().add(new Item(1925));
		player.sendMessage("You pour some " + (itemId == 6034 ? "super" : "") + "compost on the patch.");
		player.playAnimation(new Animation(FarmingConstants.PUTTING_COMPOST));
		player.getSkill().addExperience(Skill.FARMING, itemId == 6034 ? 100 : 50 * Constants.FARMING_EXPERIENCE);
		player.setStopPacket(true);

		GameService.getTaskScheduler().schedule(new Task(7) {

			@Override
			public void execute() {
				diseaseChance[allotmentFieldsData.getAllotmentIndex()] *= itemId == 6032 ? Allotments.COMPOST_CHANCE : Allotments.SUPERCOMPOST_CHANCE;
				allotmentState[allotmentFieldsData.getAllotmentIndex()] = 5;
				player.setStopPacket(false);
				stop();
			}

		});

		return true;
	}

	/* protects the patch with the flowers */

	public void resetAll() {
		fullReset();
	}

	/* Curing the plant */

	/* reseting the patches */
	private void resetAllotments(int index) {
		allotmentSeeds[index] = 0;
		allotmentState[index] = 0;
		diseaseChance[index] = 1;
		allotmentHarvest[index] = 0;
		allotmentWatched[index] = false;
		hasFullyGrown[index] = false;
	}

	public void saveStages(ByteBuffer buffer) {
		for (Integer i : allotmentStages) {
			buffer.putInt(i);
		}

		for (Integer i : allotmentSeeds) {
			buffer.putInt(i);
		}

		for (Integer i : allotmentHarvest) {
			buffer.putInt(i);
		}

		for (Integer i : allotmentState) {
			buffer.putInt(i);
		}

		for (Long i : allotmentTimer) {
			buffer.putLong(i);
		}

		for (Double i : diseaseChance) {
			buffer.putDouble(i);
		}

		for (Boolean i : allotmentWatched) {
			ByteBufferUtil.putBoolean(buffer, i);
		}

		for (Boolean i : hasFullyGrown) {
			ByteBufferUtil.putBoolean(buffer, i);
		}
	}

	/* update all the patch states */
	public void updateAllotmentsStates() {
		// catherby north - catherby south - falador north west - falador south
		// east - phasmatys north west - phasmatys south east - ardougne north -
		// ardougne south
		int[] configValues = new int[allotmentStages.length];

		int configValue;

		for (int i = 0; i < allotmentStages.length; i++) {
			configValues[i] = getConfigValue(allotmentStages[i], allotmentSeeds[i], allotmentState[i], i);

		}

		configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
		player.getSession().send(new SendConfigPacket(Allotments.FALADOR_AND_CATHERBY_CONFIG, configValue));

		configValue = configValues[4] << 16 | configValues[5] << 8 << 16 | configValues[6] | configValues[7] << 8;
		player.getSession().send(new SendConfigPacket(Allotments.ARDOUGNE_AND_PHASMATYS_CONFIG, configValue));
	}

	public boolean waterPatch(int objectX, int objectY, int itemId) {
		AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData.forIdPosition(new Location(objectX, objectY));

		if (allotmentFieldsData == null) {
			return false;
		}

		AllotmentData allotmentData = AllotmentData.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);

		if (allotmentData == null) {
			return false;
		}

		if (allotmentState[allotmentFieldsData.getAllotmentIndex()] == 1 || allotmentStages[allotmentFieldsData.getAllotmentIndex()] <= 1
				|| allotmentStages[allotmentFieldsData.getAllotmentIndex()] == allotmentData.getEndingState() - allotmentData.getStartingState() + 4) {
			player.sendMessage("This patch doesn't need watering.");
			return true;
		}

		player.getInventory().remove(new Item(itemId, 1));
		player.getInventory().add(new Item(itemId == 5333 ? itemId - 2 : itemId - 1, 1));

		if (!player.getInventory().hasItem(new Item(FarmingConstants.RAKE))) {
			SimpleDialogues.sendStatement(player, "You need a seed dibber to plant seed here.");
			return true;
		}

		player.sendMessage("You water the patch.");
		player.playAnimation(new Animation(FarmingConstants.WATERING_CAN_ANIM));
		player.setStopPacket(true);

		GameService.getTaskScheduler().schedule(new Task(5) {

			@Override
			public void execute() {
				diseaseChance[allotmentFieldsData.getAllotmentIndex()] *= Allotments.WATERING_CHANCE;
				allotmentState[allotmentFieldsData.getAllotmentIndex()] = 1;
				Allotments.this.updateAllotmentsStates();
				player.setStopPacket(false);
				stop();
			}

		});

		return true;
	}

}
