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

public class SpecialPlantTwo {

	public enum InspectData {
		BELLADONNA(5281, new String[][] { { "The belladonna seed has only just been planted." }, { "The belladonna plant grows a little taller." },
				{ "The belladonna plant grows taller and leafier." }, { "The belladonna plant grows some flower buds." },
				{ "The belladonna plant is ready to harvest." } }), CACTUS(
						5280,
						new String[][] { { "The cactus seed has only just been planted." }, { "The cactus grows taller." }, { "The cactus grows two small stumps." },
								{ "The cactus grows its stumps longer." }, { "The cactus grows larger." }, { "The cactus curves its arms upwards and grows another stump." },
								{ "The cactus grows all three of its arms upwards." }, { "The cactus is ready to be harvested." } }), BITTERCAP(
										5282,
										new String[][] { { "The mushroom spore has only just been planted." }, { "The mushrooms grow a little taller." },
												{ "The mushrooms grow a little taller." }, { "The mushrooms grow a little larger." }, { "The mushrooms grow a little larger." },
												{ "The mushrooms tops grow a little wider." }, { "The mushrooms are ready to harvest." } });

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

	public enum SpecialPlantData {
		BELLADONNA(5281, 2398, 1, 63, 280, 0.15, 91, 512, 0x04, 0x08, -1, 0, 5, 8), CACTUS(5280, 6016, 1, 55, 550, 0.15, 66.5, 25, 0x08, 0x12, 0x1f, 374, 11, 17), BITTERCAP(5282,
				6004, 1, 53, 220, 0.15, 61.5, 57.7, 0x04, 0x0f, -1, 0, 12, 17);

		public static SpecialPlantData forId(int seedId) {
			return SpecialPlantData.seeds.get(seedId);
		}

		private int seedId;
		private int harvestId;
		private int seedAmount;
		private int levelRequired;
		private int growthTime;
		private double diseaseChance;
		private double plantingXp;
		private double harvestXp;
		private int startingState;
		private int endingState;
		private int checkHealthState;
		private double checkHealthExperience;
		private int diseaseDiffValue;
		private int deathDiffValue;

		private static Map<Integer, SpecialPlantData> seeds = new HashMap<>();

		static {
			for (SpecialPlantData data : SpecialPlantData.values()) {
				SpecialPlantData.seeds.put(data.seedId, data);
			}
		}

		SpecialPlantData(int seedId, int harvestId, int seedAmount, int levelRequired, int growthTime, double diseaseChance, double plantingXp, double harvestXp, int startingState,
				int endingState, int checkHealthState, double checkHealthExperience, int diseaseDiffValue, int deathDiffValue) {
			this.seedId = seedId;
			this.harvestId = harvestId;
			this.seedAmount = seedAmount;
			this.levelRequired = levelRequired;
			this.growthTime = growthTime;
			this.diseaseChance = diseaseChance;
			this.plantingXp = plantingXp;
			this.harvestXp = harvestXp;
			this.startingState = startingState;
			this.endingState = endingState;
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

	public enum SpecialPlantFieldsData {
		DRAYNOR_MANOR(0, new Location[] { new Location(3086, 3354), new Location(3087, 3355) }, 5281), AL_KARID(2,
				new Location[] { new Location(3315, 3202), new Location(3316, 3203) },
				5280), CANIFIS(3, new Location[] { new Location(3451, 3472), new Location(3452, 3473) }, 5282);

		public static SpecialPlantFieldsData forIdPosition(Location position) {
			for (SpecialPlantFieldsData specialPlantFieldsData : SpecialPlantFieldsData.values()) {
				if (FarmingConstants.inRangeArea(specialPlantFieldsData.getSpecialPlantPosition()[0], specialPlantFieldsData.getSpecialPlantPosition()[1], position)) {
					return specialPlantFieldsData;
				}
			}
			return null;
		}

		private int specialPlantsIndex;
		private Location[] specialPlantPosition;

		private int seedId;

		SpecialPlantFieldsData(int specialPlantsIndex, Location[] specialPlantPosition, int seedId) {
			this.specialPlantsIndex = specialPlantsIndex;
			this.specialPlantPosition = specialPlantPosition;
			this.seedId = seedId;
		}

		public int getSeedId() {
			return seedId;
		}

		public Location[] getSpecialPlantPosition() {
			return specialPlantPosition;
		}

		public int getSpecialPlantsIndex() {
			return specialPlantsIndex;
		}
	}

	public static int getPlantState(int plantState, SpecialPlantData specialPlantData, int specialStage) {
		int value = specialPlantData.getStartingState() + specialStage - 4;
		switch (plantState) {
		case 0:
			return value;
		case 1:
			return value + specialPlantData.getDiseaseDiffValue();
		case 2:
			return value + specialPlantData.getDeathDiffValue();
		case 3:
			return specialPlantData.getCheckHealthState();
		}
		return -1;
	}

	public static boolean guide(int objectX, int objectY) {
		SpecialPlantFieldsData specialPlantFieldsData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		if (specialPlantFieldsData == null) {
			return false;
		}
		return true;
	}

	private final Player player;
	// set of global constants for Farming
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;
	// Farming data
	public int[] specialPlantStages = new int[4];
	public int[] specialPlantSeeds = new int[4];

	public int[] specialPlantState = new int[4];

	public long[] specialPlantTimer = new long[4];

	public double[] diseaseChance = { 1, 1, 1, 1 };

	/* This is the enum holding the seeds info */

	public boolean[] hasFullyGrown = { false, false, false, false };

	/* This is the enum data about the different patches */

	public static final int MAIN_SPECIAL_PLANT_CONFIG = 512;

	/* This is the enum that hold the different data for inspecting the plant */

	public SpecialPlantTwo(Player player) {
		this.player = player;
	}

	public boolean checkIfRaked(int objectX, int objectY) {
		SpecialPlantFieldsData specialPlantFieldData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		if (specialPlantFieldData == null) {
			return false;
		}
		if (specialPlantStages[specialPlantFieldData.getSpecialPlantsIndex()] == 3) {
			return true;
		}
		return false;
	}

	public boolean clearPatch(int objectX, int objectY, int itemId) {
		final SpecialPlantFieldsData hopsFieldsData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		int finalAnimation;
		int finalDelay;
		if (hopsFieldsData == null || itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE) {
			return false;
		}
		if (specialPlantStages[hopsFieldsData.getSpecialPlantsIndex()] == 3) {
			return true;
		}
		if (specialPlantStages[hopsFieldsData.getSpecialPlantsIndex()] <= 3) {
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
		int times = 3 - specialPlantStages[hopsFieldsData.getSpecialPlantsIndex()];
		GameService.getTaskScheduler().schedule(new Task(finalDelay, times) {

			@Override
			public void execute() {
				player.playAnimation(new Animation(animation));
				if (specialPlantStages[hopsFieldsData.getSpecialPlantsIndex()] <= 2) {
					specialPlantStages[hopsFieldsData.getSpecialPlantsIndex()]++;
					player.getInventory().add(new Item(6055));
				} else {
					specialPlantStages[hopsFieldsData.getSpecialPlantsIndex()] = 3;
					SpecialPlantTwo.this.resetSpecialPlants(hopsFieldsData.getSpecialPlantsIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
				}
				player.getSkill().addExperience(Skill.FARMING, SpecialPlantTwo.CLEARING_EXPERIENCE * Constants.FARMING_EXPERIENCE);
				specialPlantTimer[hopsFieldsData.getSpecialPlantsIndex()] = player.elapsedMinutes;
				SpecialPlantTwo.this.updateSpecialPlants();
				if (specialPlantStages[hopsFieldsData.getSpecialPlantsIndex()] == 3) {
					SpecialPlantTwo.this.resetSpecialPlants(hopsFieldsData.getSpecialPlantsIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
					return;
				}
			}

			@Override
			public void onStop() {
				SpecialPlantTwo.this.resetSpecialPlants(hopsFieldsData.getSpecialPlantsIndex());
				player.sendMessage("You clear the patch.");
				player.setStopPacket(false);
			}
		});
		return true;
	}

	public boolean curePlant(int objectX, int objectY, int itemId) {
		SpecialPlantFieldsData specialPlantFieldsData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		if (specialPlantFieldsData == null || itemId != 6036) {
			return false;
		}
		SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSeeds[specialPlantFieldsData.getSpecialPlantsIndex()]);
		if (specialPlantData == null) {
			return false;
		}
		if (specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] != 1) {
			player.sendMessage("This plant doesn't need to be cured.");
			return true;
		}
		player.getInventory().add(new Item(itemId));
		player.getInventory().remove(new Item(229));
		player.playAnimation(new Animation(FarmingConstants.CURING_ANIM));
		player.setStopPacket(true);
		specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] = 0;
		GameService.getTaskScheduler().schedule(new Task(7) {

			@Override
			public void execute() {
				player.sendMessage("You cure the plant with a plant cure.");
				SpecialPlantTwo.this.updateSpecialPlants();
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* getting the different config values */

	public void doCalculations() {
		for (int i = 0; i < specialPlantSeeds.length; i++) {
			if (specialPlantStages[i] > 0 && specialPlantStages[i] <= 3 && player.elapsedMinutes - specialPlantTimer[i] >= 5) {
				specialPlantStages[i]--;
				specialPlantTimer[i] = player.elapsedMinutes;
				updateSpecialPlants();
				continue;
			}
			SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSeeds[i]);
			if (specialPlantData == null) {
				continue;
			}
			long difference = player.elapsedMinutes - specialPlantTimer[i];
			long growth = specialPlantData.getGrowthTime();
			int nbStates = specialPlantData.getEndingState() - specialPlantData.getStartingState();
			int state = (int) (difference * nbStates / growth);
			if (specialPlantTimer[i] == 0 || specialPlantState[i] == 2 || specialPlantState[i] == 3) {
				continue;
			}
			if (state > nbStates) {
				state = nbStates;
			}
			if (4 + state != specialPlantStages[i] && specialPlantStages[i] <= specialPlantData.getEndingState() - specialPlantData.getStartingState()
					+ (specialPlantData == SpecialPlantData.BELLADONNA ? 5 : -2)) {
				if (specialPlantStages[i] == specialPlantData.getEndingState() - specialPlantData.getStartingState() - 2 && specialPlantData.getCheckHealthState() != -1) {
					specialPlantStages[i] = specialPlantData.getEndingState() - specialPlantData.getStartingState() + 4;
					specialPlantState[i] = 3;
					updateSpecialPlants();
					return;
				}
				specialPlantStages[i] = 4 + state;
				doStateCalculation(i);
				updateSpecialPlants();
			}
		}
	}

	/* getting the plant states */

	public void doStateCalculation(int index) {
		if (specialPlantState[index] == 2) {
			return;
		}
		// if the patch is diseased, it dies, if its watched by a farmer, it
		// goes back to normal
		/* if (specialPlantState[index] == 1) { specialPlantState[index] = 2; } */
		if (specialPlantState[index] == 5 && specialPlantStages[index] != 2) {
			specialPlantState[index] = 0;
		}
		if (specialPlantState[index] == 0 && specialPlantStages[index] >= 5 && !hasFullyGrown[index]) {
			SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSeeds[index]);
			if (specialPlantData == null) {
				return;
			}
			double chance = diseaseChance[index] * specialPlantData.getDiseaseChance();
			int maxChance = (int) chance * 100;
			if (NumberUtil.random(100) <= maxChance) {
				specialPlantState[index] = 1;
			}
		}
	}

	/* calculating the disease chance and making the plant grow */

	public int getConfigValue(int specialStage, int seedId, int plantState, int index) {
		SpecialPlantData specialPlantData = SpecialPlantData.forId(seedId);
		switch (specialStage) {
		case 0:// weed
			return 0x00;
		case 1:// weed cleared
			return 0x01;
		case 2:
			return 0x02;
		case 3:
			return 0x03;
		}
		if (specialPlantData == null) {
			return -1;
		}
		if (specialStage > specialPlantData.getEndingState() - specialPlantData.getStartingState() - 1) {
			hasFullyGrown[index] = true;
		}
		if (SpecialPlantTwo.getPlantState(plantState, specialPlantData, specialStage) == 3) {
			return specialPlantData.getCheckHealthState();
		}
		return SpecialPlantTwo.getPlantState(plantState, specialPlantData, specialStage);
	}

	public boolean harvestOrCheckHealth(int objectX, int objectY) {
		final SpecialPlantFieldsData specialPlantFieldsData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		if (specialPlantFieldsData == null) {
			return false;
		}
		final SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSeeds[specialPlantFieldsData.getSpecialPlantsIndex()]);
		if (specialPlantData == null) {
			return false;
		}
		if (player.getInventory().freeSlots() <= 0) {
			player.sendMessage("Not enough space in your inventory.");
			return true;
		}
		player.setStopPacket(true);
		player.playAnimation(new Animation(832));
		player.stopTask();
		player.setSkilling(new Task(2) {

			@Override
			public void execute() {
				if (player.getInventory().freeSlots() <= 0) {
					player.setStopPacket(false);
					stop();
					return;
				}
				if (specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] == 3) {
					player.sendMessage("You examine the plant for signs of disease and find that it's in perfect health.");
					player.getSkill().addExperience(Skill.FARMING, specialPlantData.getCheckHealthXp() * Constants.FARMING_EXPERIENCE);
					specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] = 0;
					hasFullyGrown[specialPlantFieldsData.getSpecialPlantsIndex()] = false;
					specialPlantTimer[specialPlantFieldsData.getSpecialPlantsIndex()] = player.elapsedMinutes - specialPlantData.getGrowthTime();
					SpecialPlantTwo.this.modifyStage(specialPlantFieldsData.getSpecialPlantsIndex());
					player.setStopPacket(false);
					stop();
					return;
				}
				player.sendMessage("You harvest the crop, and pick some " + specialPlantData.getHarvestId() + '.');
				player.getInventory().add(new Item(specialPlantData.getHarvestId()));
				player.getSkill().addExperience(Skill.FARMING, specialPlantData.getHarvestXp() * Constants.FARMING_EXPERIENCE);
				switch (specialPlantData) {
				case BELLADONNA:
					SpecialPlantTwo.this.resetSpecialPlants(specialPlantFieldsData.getSpecialPlantsIndex());
					specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()] = 3;
					specialPlantTimer[specialPlantFieldsData.getSpecialPlantsIndex()] = player.elapsedMinutes;
					break;
				case CACTUS:
					specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()]--;
					break;
				case BITTERCAP:
					specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()]++;
					if (specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()] == 16) {
						SpecialPlantTwo.this.resetSpecialPlants(specialPlantFieldsData.getSpecialPlantsIndex());
						specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()] = 3;
						specialPlantTimer[specialPlantFieldsData.getSpecialPlantsIndex()] = player.elapsedMinutes;
					}
					break;
				}
				SpecialPlantTwo.this.updateSpecialPlants();
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* calculations about the diseasing chance */

	public boolean inspect(int objectX, int objectY) {
		final SpecialPlantFieldsData specialPlantFieldsData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		if (specialPlantFieldsData == null) {
			return false;
		}
		final InspectData inspectData = InspectData.forId(specialPlantSeeds[specialPlantFieldsData.getSpecialPlantsIndex()]);
		final SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSeeds[specialPlantFieldsData.getSpecialPlantsIndex()]);
		if (specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] == 1) {
			SimpleDialogues.sendStatement(player, "This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
			return true;
		} else if (specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] == 2) {
			SimpleDialogues.sendStatement(player, "This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
			return true;
		} else if (specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This plant has fully grown. You can check it's health", "to gain some farming experiences.");
			return true;
		}
		if (specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()] == 0) {
			SimpleDialogues.sendStatement(player, "This is one of the special patches. The soil has not been treated.", "The patch needs weeding.");
		} else if (specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This is one of the special patches. The soil has not been treated.", "The patch is empty and weeded.");
		} else if (inspectData != null && specialPlantData != null) {
			player.sendMessage("You bend down and start to inspect the patch...");
			player.playAnimation(new Animation(1331));
			player.setStopPacket(true);
			GameService.getTaskScheduler().schedule(new Task(5) {

				@Override
				public void execute() {
					if (specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()] - 4 < inspectData.getMessages().length - 2) {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()] - 4]);
					} else if (specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()] < specialPlantData.getEndingState() - specialPlantData.getStartingState() + 2) {
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
		for (int i = 0; i < specialPlantStages.length; i++) {
			specialPlantStages[i] = buffer.getInt();
		}
		for (int i = 0; i < specialPlantSeeds.length; i++) {
			specialPlantSeeds[i] = buffer.getInt();
		}
		for (int i = 0; i < specialPlantState.length; i++) {
			specialPlantState[i] = buffer.getInt();
		}
		for (int i = 0; i < specialPlantTimer.length; i++) {
			specialPlantTimer[i] = buffer.getLong();
		}
		for (int i = 0; i < diseaseChance.length; i++) {
			diseaseChance[i] = buffer.getDouble();
		}
		for (int i = 0; i < hasFullyGrown.length; i++) {
			hasFullyGrown[i] = ByteBufferUtil.getBoolean(buffer);
		}
	}

	/* planting the seeds */

	public void lowerStage(int index, int timer) {
		hasFullyGrown[index] = false;
		specialPlantTimer[index] -= timer;
	}

	/* harvesting the plant resulted */

	public void modifyStage(int i) {
		SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSeeds[i]);
		if (specialPlantData == null) {
			return;
		}
		long difference = player.elapsedMinutes - specialPlantTimer[i];
		long growth = specialPlantData.getGrowthTime();
		int nbStates = specialPlantData.getEndingState() - specialPlantData.getStartingState();
		int state = (int) (difference * nbStates / growth);
		specialPlantStages[i] = 4 + state;
		updateSpecialPlants();
	}

	/* lowering the stage */

	public boolean plantSeeds(int objectX, int objectY, final int seedId) {
		final SpecialPlantFieldsData specialPlantFieldsData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		final SpecialPlantData specialPlantData = SpecialPlantData.forId(seedId);
		if (specialPlantFieldsData == null || specialPlantData == null || specialPlantFieldsData.getSeedId() != seedId) {
			return false;
		}
		if (specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()] != 3) {
			player.sendMessage("You can't plant a seed here.");
			return false;
		}
		if (specialPlantData.getLevelRequired() > player.getSkill().getLevel(Skill.FARMING)) {
			SimpleDialogues.sendStatement(player, "You need a farming level of " + specialPlantData.getLevelRequired() + " to plant this seed.");
			return true;
		}
		if (!player.getInventory().contains(FarmingConstants.SEED_DIBBER)) {
			SimpleDialogues.sendStatement(player, "You need a seed dibber to plant seed here.");
			return true;
		}
		if (player.getInventory().getCount(specialPlantData.getSeedId()) < specialPlantData.getSeedAmount()) {
			SimpleDialogues.sendStatement(player, "You need atleast " + specialPlantData.getSeedAmount() + " seeds to plant here.");
			return true;
		}
		player.playAnimation(new Animation(FarmingConstants.SEED_DIBBING));
		specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()] = 4;
		player.getInventory().remove(new Item(seedId, specialPlantData.getSeedAmount()));
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] = 0;
				specialPlantSeeds[specialPlantFieldsData.getSpecialPlantsIndex()] = seedId;
				specialPlantTimer[specialPlantFieldsData.getSpecialPlantsIndex()] = player.elapsedMinutes;
				player.getSkill().addExperience(Skill.FARMING, specialPlantData.getPlantingXp() * Constants.FARMING_EXPERIENCE);
				SpecialPlantTwo.this.updateSpecialPlants();
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
		final SpecialPlantFieldsData specialPlantFieldsData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		if (specialPlantFieldsData == null) {
			return false;
		}
		if (specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()] != 3 || specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] == 5) {
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
				diseaseChance[specialPlantFieldsData.getSpecialPlantsIndex()] *= itemId == 6032 ? SpecialPlantTwo.COMPOST_CHANCE : SpecialPlantTwo.SUPERCOMPOST_CHANCE;
				specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] = 5;
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* inspecting a plant */

	/* update all the patch states */
	public void resetAll() {
		this.resetSpecialPlants();
	}

	/* opening the corresponding guide about the patch */

	public void resetSpecialPlants() {
		specialPlantStages = new int[4];
		specialPlantSeeds = new int[4];
		specialPlantState = new int[4];
		specialPlantTimer = new long[4];
		hasFullyGrown = new boolean[] { false, false, false, false };
	}

	/* Curing the plant */

	private void resetSpecialPlants(int index) {
		specialPlantSeeds[index] = 0;
		specialPlantState[index] = 0;
		diseaseChance[index] = 1;
		hasFullyGrown[index] = false;
	}

	public void saveStages(ByteBuffer buffer) {
		for (Integer i : specialPlantStages) {
			buffer.putInt(i);
		}
		for (Integer i : specialPlantSeeds) {
			buffer.putInt(i);
		}
		for (Integer i : specialPlantState) {
			buffer.putInt(i);
		}
		for (Long i : specialPlantTimer) {
			buffer.putLong(i);
		}
		for (Double i : diseaseChance) {
			buffer.putDouble(i);
		}
		for (Boolean i : hasFullyGrown) {
			ByteBufferUtil.putBoolean(buffer, i);
		}
	}

	/* checking if the patch is raked */

	public void updateSpecialPlants() {
		// draynor manor - none - al karid - canifis
		int[] configValues = new int[specialPlantStages.length];
		int configValue;
		for (int i = 0; i < specialPlantStages.length; i++) {
			configValues[i] = getConfigValue(specialPlantStages[i], specialPlantSeeds[i], specialPlantState[i], i);
		}
		configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
		player.getSession().send(new SendConfigPacket(SpecialPlantTwo.MAIN_SPECIAL_PLANT_CONFIG, configValue));
	}
}
