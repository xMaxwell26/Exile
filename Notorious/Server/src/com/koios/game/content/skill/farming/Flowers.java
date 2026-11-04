package com.koios.game.content.skill.farming;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.task.Task;
import com.koios.util.ByteBufferUtil;
import com.koios.util.NumberUtil;

/**
 * Created by J on 25/06/2014.
 */

public class Flowers { // todo scarecrow 6059

	public enum FlowerData {
		MARIGOLD(5096, 6010, 2, 20, 0.35, 8.5, 47, 0x08, 0x0c), ROSEMARY(5097, 6014, 11, 20, 0.32, 12, 66.5, 0x0d, 0x11), NASTURTIUM(5098, 6012, 24, 20, 0.30, 19.5, 111, 0x12,
				0x16), WOAD(5099, 1793, 25, 20, 0.27, 20.5, 115.5, 0x17, 0x1b), LIMPWURT(5100, 225, 26, 25, 21.5, 8.5, 120, 0x1c, 0x20),;

		public static FlowerData forId(int seedId) {
			return FlowerData.seeds.get(seedId);
		}

		private int seedId;
		private int harvestId;
		private int levelRequired;
		private int growthTime;
		private double diseaseChance;
		private double plantingXp;
		private double harvestXp;
		private int startingState;
		private int endingState;

		private static Map<Integer, FlowerData> seeds = new HashMap<>();

		static {
			for (FlowerData data : FlowerData.values()) {
				FlowerData.seeds.put(data.seedId, data);
			}
		}

		FlowerData(int seedId, int harvestId, int levelRequired, int growthTime, double diseaseChance, double plantingXp, double harvestXp, int startingState, int endingState) {
			this.seedId = seedId;
			this.harvestId = harvestId;
			this.levelRequired = levelRequired;
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

		public double getPlantingXp() {
			return plantingXp;
		}

		public int getSeedId() {
			return seedId;
		}

		public int getStartingState() {
			return startingState;
		}
	}

	public enum FlowerFieldsData {
		ARDOUGNE(0, new Location[] { new Location(2666, 3374), new Location(2667, 3375) }), PHASMATYS(1,
				new Location[] { new Location(3601, 3525), new Location(3602, 3526) }), FALADOR(2,
						new Location[] { new Location(3054, 3307), new Location(3055, 3308) }), CATHERBY(3, new Location[] { new Location(2809, 3463), new Location(2810, 3464) });

		public static FlowerFieldsData forIdPosition(Location position) {
			for (FlowerFieldsData flowerFieldsData : FlowerFieldsData.values()) {
				if (FarmingConstants.inRangeArea(flowerFieldsData.getFlowerPosition()[0], flowerFieldsData.getFlowerPosition()[1], position)) {
					return flowerFieldsData;
				}
			}
			return null;
		}

		private int flowerIndex;

		private Location[] flowerPosition;

		FlowerFieldsData(int flowerIndex, Location[] flowerPosition) {
			this.flowerIndex = flowerIndex;
			this.flowerPosition = flowerPosition;
		}

		public int getFlowerIndex() {
			return flowerIndex;
		}

		public Location[] getFlowerPosition() {
			return flowerPosition;
		}
	}

	public enum InspectData {
		MARIGOLD(5096,
				new String[][] { { "The seeds have only just been planted." }, { "The marigold plants have developed leaves." },
						{ "The marigold plants have begun to grow their", "flowers. The new flowers are orange and small at", "first." },
						{ "The marigold plants are larger, and more",
								"developed in their petals." },
				{ "The marigold plants are ready to harvest. Their", "flowers are fully matured." } }), ROSEMARY(
						5097,
						new String[][] { { "The seeds have only just been planted." }, { "The rosemary plant is taller than before." }, {
								"The rosemary plant is bushier and taller than", "before." }, { "The rosemary plant is developing a flower bud at", "its top." },
						{ "The plant is ready to harvest. The rosemary", "plant's flower has opened." } }), NASTURTIUM(
								5098,
								new String[][] { { "The nasturtium seed has only just been planted." }, { "The nasturtium plants have started to develop", "leaves." }, {
										"The nasturtium plants have grown more leaves,", "and nine flower buds." }, { "The nasturtium plants loot their flower buds." },
								{ "The plants are ready to harvest. The nasturtium", "plants grow larger than before and the flowers", "fully loot." } }), WOAD(
										5099,
										new String[][] { { "The woad seed has only just been planted." },
												{ "The woad plant produces more stalks, that split", "in tow near the top." },
												{ "The woad plant grows more segments from its", "intitial stalks." },
												{ "The woad plant develops flower buds on the end", "of each of its stalks." },
												{ "The woad plant is ready to harvest. The plant has", "all of its stalks pointing directly up, with",
														"all flowers loot." } }), LIMPWURT(
																5100,
																new String[][] { { "The seed has only just been planted." }, { "The limpwurt plant produces more roots." }, {
																		"The limpwurt plant produces an unopened pink", "flower bud and continues to grow larger." },
																{ "The limpwurt plant grows larger, with more loops", "in its roots. The flower bud is still unopened." },
																{ "The limpwurt plant is ready to harvest. The", "flower finally opens wide, with a spike in the", "middle." } });

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
			return Flowers.GROWING;
		case 1:
			return Flowers.WATERED;
		case 2:
			return Flowers.DISEASED;
		case 3:
			return Flowers.DEAD;
		}
		return -1;
	}

	public static boolean guide(int objectX, int objectY) {
		FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Location(objectX, objectY));
		if (flowerFieldsData == null) {
			return false;
		}
		return true;
	}

	private final Player player;

	// set of global constants for Farming
	private static final double WATERING_CHANCE = 0.5;

	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;
	public static final int SCARECROW = 6059;
	// Farming data toSave
	public int[] flowerStages = new int[4];
	public int[] flowerSeeds = new int[4];

	public int[] flowerState = new int[4];

	public long[] flowerTimer = new long[4];

	public double[] diseaseChance = { 1, 1, 1, 1 };
	public boolean[] hasFullyGrown = { false, false, false, false };
	/* set of the constants for the patch */
	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;
	public static final int WATERED = 0x01;
	public static final int DISEASED = 0x02;

	/* This is the enum holding the seeds info */

	public static final int DEAD = 0x03;

	/* This is the enum data about the different patches */

	public static final int FLOWER_PATCH_CONFIGS = 508;

	/* This is the enum that hold the different data for inspecting the plant */

	public Flowers(Player player) {
		this.player = player;
	}

	/* update all the patch states */

	public boolean checkIfRaked(int objectX, int objectY) {
		FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Location(objectX, objectY));
		if (flowerFieldsData == null) {
			return false;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3) {
			return true;
		}
		return false;
	}

	public boolean clearPatch(int objectX, int objectY, int itemId) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Location(objectX, objectY));
		int finalAnimation;
		int finalDelay;
		if (flowerFieldsData == null || itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE) {
			return false;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3) {
			return true;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] <= 3) {
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
		player.playAnimation(new Animation(animation));
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(1, finalDelay) {

			@Override
			public void execute() {
				player.playAnimation(new Animation(animation));
				if (flowerStages[flowerFieldsData.getFlowerIndex()] <= 2) {
					flowerStages[flowerFieldsData.getFlowerIndex()]++;
					player.getInventory().add(new Item(6055));
				} else {
					flowerStages[flowerFieldsData.getFlowerIndex()] = 3;
					Flowers.this.resetFlowers(flowerFieldsData.getFlowerIndex());
					player.setStopPacket(false);
					stop();
				}
				player.getSkill().addExperience(Skill.FARMING, Flowers.CLEARING_EXPERIENCE * Constants.FARMING_EXPERIENCE);
				flowerTimer[flowerFieldsData.getFlowerIndex()] = player.elapsedMinutes;
				Flowers.this.updateFlowerStates();
				if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3) {
					Flowers.this.resetFlowers(flowerFieldsData.getFlowerIndex());
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
		FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Location(objectX, objectY));
		if (flowerFieldsData == null || itemId != 6036) {
			return false;
		}
		FlowerData flowerData = FlowerData.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
		if (flowerData == null) {
			return false;
		}
		if (flowerState[flowerFieldsData.getFlowerIndex()] != 2) {
			player.sendMessage("This plant doesn't need to be cured.");
			return true;
		}
		player.getInventory().remove(new Item(itemId));
		player.getInventory().add(new Item(229));
		player.playAnimation(new Animation(FarmingConstants.CURING_ANIM));
		flowerState[flowerFieldsData.getFlowerIndex()] = 0;
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(7) {

			@Override
			public void execute() {
				player.sendMessage("You cure the plant with a plant cure.");
				Flowers.this.updateFlowerStates();
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* getting the plant states */

	public void doCalculations() {
		for (int i = 0; i < flowerSeeds.length; i++) {
			if (flowerStages[i] > 0 && flowerStages[i] <= 3 && player.elapsedMinutes - flowerTimer[i] >= 5) {
				flowerStages[i]--;
				flowerTimer[i] = player.elapsedMinutes;
				updateFlowerStates();
			}
			if (player.elapsedMinutes - flowerTimer[i] >= 5 && flowerSeeds[i] > 0x21 && flowerSeeds[i] <= 0x24) {
				flowerSeeds[i]--;
				updateFlowerStates();
				return;
			}
			FlowerData flowerData = FlowerData.forId(flowerSeeds[i]);
			if (flowerData == null) {
				continue;
			}
			long difference = player.elapsedMinutes - flowerTimer[i];
			long growth = flowerData.getGrowthTime();
			int nbStates = flowerData.getEndingState() - flowerData.getStartingState();
			int state = (int) (difference * nbStates / growth);
			if (state > nbStates) {
				state = nbStates;
			}
			if (flowerState[i] == 3 || flowerSeeds[i] == 0x21 || flowerTimer[i] == 0 || state > nbStates) {
				continue;
			}
			if (4 + state != flowerStages[i]) {
				flowerStages[i] = 4 + state;
				doStateCalculation(i);
				updateFlowerStates();
			}
		}
	}

	/* calculating the disease chance and making the plant grow */

	public void doStateCalculation(int index) {
		if (flowerState[index] == 3) {
			return;
		}
		// if the patch is diseased, it dies, if its watched by a farmer, it
		// goes back to normal
		if (flowerState[index] == 2) {
			flowerState[index] = 3;
		}
		if (flowerState[index] == 1 || flowerState[index] == 5 && flowerStages[index] != 3) {
			diseaseChance[index] *= 2;
			flowerState[index] = 0;
		}
		if (flowerState[index] == 0 && flowerStages[index] >= 5 && !hasFullyGrown[index]) {
			FlowerData flowerData = FlowerData.forId(flowerSeeds[index]);
			if (flowerData == null) {
				return;
			}
			double chance = diseaseChance[index] * flowerData.getDiseaseChance();
			int maxChance = (int) (chance * 100);
			if (NumberUtil.random(100) <= maxChance) {
				// flowerState[index] = 2;
			}
		}
	}

	/* calculations about the diseasing chance */

	public int getConfigValue(int flowerStage, int seedId, int plantState, int index) {
		if (flowerSeeds[index] >= 0x21 && flowerSeeds[index] <= 0x24 && flowerStages[index] > 3) {
			return (Flowers.GROWING << 6) + flowerSeeds[index];
		}
		FlowerData flowerData = FlowerData.forId(seedId);
		switch (flowerStage) {
		case 0:// weed
			return (Flowers.GROWING << 6) + 0x00;
		case 1:// weed cleared
			return (Flowers.GROWING << 6) + 0x01;
		case 2:
			return (Flowers.GROWING << 6) + 0x02;
		case 3:
			return (Flowers.GROWING << 6) + 0x03;
		}
		if (flowerData == null) {
			return -1;
		}
		if (flowerData.getEndingState() == flowerData.getStartingState() + flowerStage - 2) {
			hasFullyGrown[index] = true;
		}
		return (Flowers.getPlantState(plantState) << 6) + flowerData.getStartingState() + flowerStage - 4;
	}

	/* watering the patch */

	public boolean harvest(int objectX, int objectY) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Location(objectX, objectY));
		if (flowerFieldsData == null) {
			return false;
		}
		final FlowerData flowerData = FlowerData.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
		if (flowerData == null) {
			return false;
		}
		if (flowerData.getEndingState() > flowerData.getStartingState() + flowerStages[flowerFieldsData.getFlowerIndex()] - 1) {
			return false;
		}
		if (player.getInventory().freeSlots() == 0) {
			player.sendMessage("You do not have enough inventory space to do this.");
			return true;
		}
		if (!player.getInventory().contains(FarmingConstants.SPADE)) {
			SimpleDialogues.sendStatement(player, "You need a spade to harvest here.");
			return true;
		}
		player.playAnimation(new Animation(FarmingConstants.SPADE_ANIM));
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(2) {

			@Override
			public void execute() {
				Flowers.this.resetFlowers(flowerFieldsData.getFlowerIndex());
				flowerStages[flowerFieldsData.getFlowerIndex()] = 3;
				flowerTimer[flowerFieldsData.getFlowerIndex()] = player.elapsedMinutes;
				player.playAnimation(new Animation(FarmingConstants.SPADE_ANIM));
				player.sendMessage("You harvest the crop, and get some vegetables.");
				player.getInventory().add(new Item(flowerData.getHarvestId(), flowerData.getHarvestId() == 5099 || flowerData.getHarvestId() == 5100 ? 3 : 1));
				player.getSkill().addExperience(Skill.FARMING, flowerData.getHarvestXp() * Constants.FARMING_EXPERIENCE);
				Flowers.this.updateFlowerStates();
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* clearing the patch with a rake of a spade */

	public boolean inspect(int objectX, int objectY) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Location(objectX, objectY));
		if (flowerFieldsData == null) {
			return false;
		}
		final InspectData inspectData = InspectData.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
		final FlowerData flowerData = FlowerData.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
		if (flowerState[flowerFieldsData.getFlowerIndex()] == 2) {
			SimpleDialogues.sendStatement(player, "This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
			return true;
		} else if (flowerState[flowerFieldsData.getFlowerIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
			return true;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] == 0) {
			SimpleDialogues.sendStatement(player, "This is an flower patch. The soil has not been treated.", "The patch needs weeding.");
		} else if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This is an flower patch. The soil has not been treated.", "The patch is empty and weeded.");
		} else if (inspectData != null && flowerData != null) {
			player.sendMessage("You bend down and start to inspect the patch...");
			player.playAnimation(new Animation(1331));
			player.setStopPacket(true);
			GameService.getTaskScheduler().schedule(new Task(5) {

				@Override
				public void execute() {
					if (flowerStages[flowerFieldsData.getFlowerIndex()] - 4 < inspectData.getMessages().length - 2) {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[flowerStages[flowerFieldsData.getFlowerIndex()] - 4]);
					} else if (flowerStages[flowerFieldsData.getFlowerIndex()] < flowerData.getEndingState() - flowerData.getStartingState() + 4) {
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

	/* planting the seeds */

	public void loadStages(ByteBuffer buffer) {
		for (int i = 0; i < flowerStages.length; i++) {
			flowerStages[i] = buffer.getInt();
		}
		for (int i = 0; i < flowerSeeds.length; i++) {
			flowerSeeds[i] = buffer.getInt();
		}
		for (int i = 0; i < flowerState.length; i++) {
			flowerState[i] = buffer.getInt();
		}
		for (int i = 0; i < flowerTimer.length; i++) {
			flowerTimer[i] = buffer.getLong();
		}
		for (int i = 0; i < diseaseChance.length; i++) {
			diseaseChance[i] = buffer.getDouble();
		}
		for (int i = 0; i < hasFullyGrown.length; i++) {
			hasFullyGrown[i] = ByteBufferUtil.getBoolean(buffer);
		}
	}

	/* harvesting the plant resulted */

	public boolean plantScareCrow(int objectX, int objectY, int itemId) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Location(objectX, objectY));
		if (flowerFieldsData == null || itemId != Flowers.SCARECROW) {
			return false;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] != 3) {
			player.sendMessage("You need to clear the patch before planting a scarecrow");
			return false;
		}
		player.getInventory().remove(new Item(Flowers.SCARECROW));
		player.playAnimation(new Animation(832));
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(2) {

			@Override
			public void execute() {
				player.sendMessage("You put a scarecrow on the flower patch, and some weeds start to grow around it.");
				flowerSeeds[flowerFieldsData.getFlowerIndex()] = 0x24;
				flowerStages[flowerFieldsData.getFlowerIndex()] = 4;
				flowerTimer[flowerFieldsData.getFlowerIndex()] = player.elapsedMinutes;
				player.setStopPacket(false);
				Flowers.this.updateFlowerStates();
				stop();
			}
		});
		return true;
	}

	/* putting compost onto the plant */

	public boolean plantSeed(int objectX, int objectY, final int seedId) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Location(objectX, objectY));
		final FlowerData flowerData = FlowerData.forId(seedId);
		if (flowerFieldsData == null || flowerData == null) {
			return false;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] != 3) {
			SimpleDialogues.sendStatement(player, "You can't plant a seed here.");
			return false;
		}
		if (flowerData.getLevelRequired() > player.getSkill().getLevel(Skill.FARMING)) {
			SimpleDialogues.sendStatement(player, "You need a farming level of " + flowerData.getLevelRequired() + " to plant this seed.");
			return true;
		}
		if (!player.getInventory().contains(FarmingConstants.SEED_DIBBER)) {
			SimpleDialogues.sendStatement(player, "You need a seed dibber to plant seed here.");
			return true;
		}
		player.playAnimation(new Animation(FarmingConstants.SEED_DIBBING));
		flowerStages[flowerFieldsData.getFlowerIndex()] = 4;
		player.getInventory().remove(new Item(seedId));
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				flowerState[flowerFieldsData.getFlowerIndex()] = 0;
				flowerSeeds[flowerFieldsData.getFlowerIndex()] = seedId;
				flowerTimer[flowerFieldsData.getFlowerIndex()] = player.elapsedMinutes;
				player.getSkill().addExperience(Skill.FARMING, flowerData.getPlantingXp() * Constants.FARMING_EXPERIENCE);
				Flowers.this.updateFlowerStates();
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* inspecting a plant */

	public boolean putCompost(int objectX, int objectY, final int itemId) {
		if (itemId != 6032 && itemId != 6034) {
			return false;
		}
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Location(objectX, objectY));
		if (flowerFieldsData == null) {
			return false;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] != 3 || flowerState[flowerFieldsData.getFlowerIndex()] == 5) {
			player.sendMessage("This patch doesn't need compost.");
			return false;
		}
		player.getInventory().remove(new Item(itemId));
		player.getInventory().add(new Item(1925));
		player.sendMessage("You pour some " + (itemId == 6034 ? "super" : "") + "compost on the patch.");
		player.playAnimation(new Animation(FarmingConstants.PUTTING_COMPOST));
		player.getSkill().addExperience(Skill.FARMING, itemId == 6034 ? 100 : 50 * Constants.FARMING_EXPERIENCE);
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(7) {

			@Override
			public void execute() {
				diseaseChance[flowerFieldsData.getFlowerIndex()] *= itemId == 6032 ? Flowers.COMPOST_CHANCE : Flowers.SUPERCOMPOST_CHANCE;
				flowerState[flowerFieldsData.getFlowerIndex()] = 5;
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* opening the corresponding guide about the patch */

	public void resetAll() {
		this.resetFlowers();
	}

	/* Curing the plant */

	private void resetFlowers() {
		flowerStages = new int[4];
		flowerSeeds = new int[4];
		flowerState = new int[4];
		flowerTimer = new long[4];
		diseaseChance = new double[] { 1, 1, 1, 1 };
		hasFullyGrown = new boolean[] { false, false, false, false };
	}

	/* Planting scarecrow to push off the birds */

	private void resetFlowers(int index) {
		flowerSeeds[index] = 0;
		flowerState[index] = 0;
		flowerTimer[index] = -1;
		diseaseChance[index] = 1;
		hasFullyGrown[index] = false;
	}

	public void saveStages(ByteBuffer buffer) {
		for (Integer i : flowerStages) {
			buffer.putInt(i);
		}
		for (Integer i : flowerSeeds) {
			buffer.putInt(i);
		}
		for (Integer i : flowerState) {
			buffer.putInt(i);
		}
		for (Long i : flowerTimer) {
			buffer.putLong(i);
		}
		for (Double i : diseaseChance) {
			buffer.putDouble(i);
		}
		for (Boolean i : hasFullyGrown) {
			ByteBufferUtil.putBoolean(buffer, i);
		}
	}

	/* reseting the patches */

	public void updateFlowerStates() {
		// ardougne - phasmatys - falador - catherby
		int[] configValues = new int[flowerStages.length];
		int configValue;
		for (int i = 0; i < flowerStages.length; i++) {
			configValues[i] = getConfigValue(flowerStages[i], flowerSeeds[i], flowerState[i], i);
		}
		configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
		player.getSession().send(new SendConfigPacket(Flowers.FLOWER_PATCH_CONFIGS, configValue));
	}

	/* checking if the patch is raked */

	public boolean waterPatch(int objectX, int objectY, int itemId) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData.forIdPosition(new Location(objectX, objectY));
		if (flowerFieldsData == null) {
			return false;
		}
		FlowerData flowerData = FlowerData.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
		if (flowerData == null) {
			return false;
		}
		if (flowerState[flowerFieldsData.getFlowerIndex()] == 1 || flowerStages[flowerFieldsData.getFlowerIndex()] <= 1
				|| flowerStages[flowerFieldsData.getFlowerIndex()] == flowerData.getEndingState() - flowerData.getStartingState() + 4) {
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
				diseaseChance[flowerFieldsData.getFlowerIndex()] *= Flowers.WATERING_CHANCE;
				flowerState[flowerFieldsData.getFlowerIndex()] = 1;
				Flowers.this.updateFlowerStates();
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}
}
