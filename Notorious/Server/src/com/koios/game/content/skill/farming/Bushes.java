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
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.task.Task;
import com.koios.util.ByteBufferUtil;
import com.koios.util.NumberUtil;

public class Bushes {

	public enum BushesData {

		REDBERRY(5101, 1951, 1, 10, new int[] { 5478, 4 }, 100, 0.20, 11.5, 4.5, 0x05, 0x0e, 0x09, 0x3a, 64), CADAVABERRY(5102, 753, 1, 22, new int[] { 5968, 3 }, 140, 0.20, 18, 7,
				0x0f, 0x19, 0x14, 0x3b, 102.5), DWELLBERRY(5103, 2126, 1, 36, new int[] { 5406, 3 }, 140, 0.20, 31.5, 12, 0x1a, 0x25, 0x20, 0x3c, 177.5), JANGERBERRY(5104, 247, 1,
						48, new int[] { 5982, 6 }, 160, 0.20, 50.5, 19, 0x26, 0x32, 0x2d, 0x3d, 284.5), WHITEBERRY(5105, 239, 1, 59, new int[] { 6004, 8 }, 160, 0.20, 78, 29, 0x33,
								0x3f, 0x3a, 0x3e, 437.5), POISONIVYBERRY(5106, 6018, 1, 70, null, 160, 0.20, 120, 45, 0xc5, 0xd1, 0xcc, 0x3f, 674);

		public static BushesData forId(int seedId) {
			return BushesData.seeds.get(seedId);
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

		private static Map<Integer, BushesData> seeds = new HashMap<>();

		static {
			for (BushesData data : BushesData.values()) {
				BushesData.seeds.put(data.seedId, data);
			}
		}

		BushesData(int seedId, int harvestId, int seedAmount, int levelRequired, int[] paymentToWatch, int growthTime, double diseaseChance, double plantingXp, double harvestXp,
				int startingState, int endingState, int limitState, int checkHealthState, double checkHealthExperience) {
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
		}

		public int getCheckHealthState() {
			return checkHealthState;
		}

		public double getCheckHealthXp() {
			return checkHealthExperience;
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

	/* This is the enum data about the different patches */
	public enum BushesFieldsData {

		ETCETERIA(0, new Location[] { new Location(2591, 3863), new Location(2592, 3864) }, 2337), SOUTH_ARDOUGNE(1,
				new Location[] { new Location(2617, 3225), new Location(2618, 3226) }, 2338), CHAMPION_GUILD(2,
						new Location[] { new Location(3181, 3357), new Location(3182, 3358) },
						2335), RIMMINGTON(3, new Location[] { new Location(2940, 3221), new Location(2941, 3222) }, 2336);

		public static BushesFieldsData forId(int npcId) {
			return BushesFieldsData.npcsProtecting.get(npcId);
		}

		public static BushesFieldsData forIdPosition(Location position) {
			for (BushesFieldsData bushesFieldsData : BushesFieldsData.values()) {
				if (FarmingConstants.inRangeArea(bushesFieldsData.getBushesPosition()[0], bushesFieldsData.getBushesPosition()[1], position)) {
					return bushesFieldsData;
				}
			}

			return null;
		}

		private int bushesIndex;
		private Location[] bushesPosition;
		private int npcId;
		private static Map<Integer, BushesFieldsData> npcsProtecting = new HashMap<>();

		static {
			for (BushesFieldsData data : BushesFieldsData.values()) {
				BushesFieldsData.npcsProtecting.put(data.npcId, data);
			}
		}

		BushesFieldsData(int bushesIndex, Location[] bushesPosition, int npcId) {
			this.bushesIndex = bushesIndex;
			this.bushesPosition = bushesPosition;
			this.npcId = npcId;
		}

		public int getBushesIndex() {
			return bushesIndex;
		}

		public Location[] getBushesPosition() {
			return bushesPosition;
		}

		public int getNpcId() {
			return npcId;
		}

	}

	public enum InspectData {
		REDBERRY(5101,
				new String[][] { { "The Redberry seeds have only just been planted." }, { "The Redberry bush grows larger." }, { "The Redberry bush grows larger." },
						{ "The Redberry bush grows small, unripe,", "green berries." }, { "The berries grow larger, and pink." },
						{ "The Redberry bush is ready to harvest.", "The berries on the bush are red." }, }), CADAVABERRY(
								5102,
								new String[][] { { "The Cadavaberry seeds have only just been planted." }, { "The Cadavaberry bush grows larger." },
										{ "The Cadavaberry bush grows larger." }, { "The Cadavaberry bush grows larger." },
										{ "The Cadavaberry bush grows small, unripe,", "green berries." }, { "The berries grow larger, and pink." },
										{ "The Cadavaberry bush is ready to harvest.", "The berries on the bush are purple." } }), DWELLBERRY(
												5103,
												new String[][] { { "The Dwellbery seeds have only just been planted." }, { "The Dwellbery bush grows larger." }, {
														"The Dwellbery bush grows larger." }, { "The Dwellbery bush grows larger." }, { "The Dwellbery bush grows larger." },
										{ "The Dwellbery bush grows small, unripe,", "green berries." }, { "The berries grow larger, and light blue." },
										{ "The Dwellbery bush is ready to harvest.", "The berries on the bush are blue." }, }), JANGERBERRY(
												5104,
												new String[][] { { "The Jangerberry seeds have only just been planted." }, { "The Jangerberry bush grows larger." },
														{ "The Jangerberry bush grows larger." }, { "The Jangerberry bush grows larger." },
														{ "The Jangerberry bush grows larger." }, { "The Jangerberry bush grows small, unripe,", "green berries." },
														{ "The berries grow larger." }, { "The berries grow larger, and light green." },
														{ "The Jangerberry bush is ready to harvest.", "The berries on the bush are green." } }), WHITEBERRY(
																5105,
																new String[][] { { "The Whiteberry seeds have only just been planted." }, { "The Whiteberry bush grows larger." }, {
																		"The Whiteberry bush grows larger." }, { "The Whiteberry bush grows larger." },
														{ "The Whiteberry bush grows larger." }, { "The Whiteberry bush grows larger." },
														{ "The Whiteberry bush grows small, unripe,", "green berries." }, { "The berries grow larger." },
														{ "The Whiteberry bush is ready to harvest.", "The berries on the bush are white." }, }), POISONIVYBERRY(
																5106,
																new String[][] { { "The Poison ivy seeds have only just been planted." }, { "The Poison ivy bush grows larger." },
																		{ "The Poison ivy bush grows larger." }, { "The Poison ivy bush grows larger." },
																		{ "The Poison ivy bush grows larger." }, { "The Poison ivy bush grows small, unripe,", "green berries." },
																		{ "The berries grow larger." }, { "The berries grow larger, and light green." },
																		{ "The Poison ivy bush is ready to harvest.", "The berries on the bush are pale yellow." } });

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
			return Bushes.GROWING;

		case 1:
			return Bushes.DISEASED;

		case 2:
			return Bushes.DEAD;

		case 3:
			return Bushes.CHECK;
		}

		return -1;
	}

	/* opening the corresponding guide about the patch */
	public static boolean guide(int objectX, int objectY) {
		BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(new Location(objectX, objectY));

		if (bushesFieldsData == null) {
			return false;
		}

		return true;
	}

	/**
	 * Death works
	 */
	private final Player player;
	// set of global constants for Farming
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;
	// Farming data
	public int[] bushesStages = new int[4];
	public int[] bushesSeeds = new int[4];
	public int[] bushesState = new int[4];

	public long[] bushesTimer = new long[4];

	public double[] diseaseChance = { 1, 1, 1, 1 };

	public boolean[] hasFullyGrown = { false, false, false, false };
	public boolean[] bushesWatched = { false, false, false, false };
	/* set of the constants for the patch */
	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;
	public static final int DISEASED = 0x01;
	public static final int DEAD = 0x02;

	public static final int CHECK = 0x03;

	public static final int MAIN_BUSHES_CONFIG = 509;

	/* This is the enum holding the seeds info */

	public Bushes(Player player) {
		this.player = player;
	}

	/* clearing the patch with a rake of a spade */
	public boolean clearPatch(int objectX, int objectY, int itemId) {
		final BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(new Location(objectX, objectY));
		int finalAnimation;
		int finalDelay;

		if (bushesFieldsData == null || itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE) {
			return false;
		}

		if (bushesStages[bushesFieldsData.getBushesIndex()] == 3) {
			return true;
		}

		if (bushesStages[bushesFieldsData.getBushesIndex()] <= 3) {
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
		int times = 3 - bushesStages[bushesFieldsData.getBushesIndex()];

		GameService.getTaskScheduler().schedule(new Task(finalDelay, times) {

			@Override
			public void execute() {
				player.playAnimation(new Animation(animation));

				if (bushesStages[bushesFieldsData.getBushesIndex()] <= 2) {
					bushesStages[bushesFieldsData.getBushesIndex()]++;
					player.getInventory().add(new Item(6055));
				} else {
					bushesStages[bushesFieldsData.getBushesIndex()] = 3;
					Bushes.this.resetBushes(bushesFieldsData.getBushesIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
				}

				player.getSkill().addExperience(Skill.FARMING, Bushes.CLEARING_EXPERIENCE * Constants.FARMING_EXPERIENCE);
				bushesTimer[bushesFieldsData.getBushesIndex()] = player.elapsedMinutes;
				Bushes.this.updateBushesStates();

				if (bushesStages[bushesFieldsData.getBushesIndex()] == 3) {
					Bushes.this.resetBushes(bushesFieldsData.getBushesIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
				}
			}

		});

		return true;
	}

	/* Curing the plant */
	public boolean curePlant(int objectX, int objectY, int itemId) {
		BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(new Location(objectX, objectY));

		if (bushesFieldsData == null || itemId != 6036) {
			return false;
		}

		BushesData bushesData = BushesData.forId(bushesSeeds[bushesFieldsData.getBushesIndex()]);

		if (bushesData == null) {
			return false;
		}

		if (bushesState[bushesFieldsData.getBushesIndex()] != 1) {
			player.sendMessage("This plant doesn't need to be cured.");
			return true;
		}

		player.getInventory().remove(new Item(itemId));
		player.getInventory().add(new Item(229));
		player.playAnimation(new Animation(FarmingConstants.CURING_ANIM));
		player.setStopPacket(true);
		bushesState[bushesFieldsData.getBushesIndex()] = 0;

		GameService.getTaskScheduler().schedule(new Task(7) {

			@Override
			public void execute() {
				player.sendMessage("You cure the plant with a plant cure.");
				Bushes.this.updateBushesStates();
				player.setStopPacket(false);
				stop();
			}

		});

		return true;
	}

	/* calculating the disease chance and making the plant grow */
	public void doCalculations() {
		for (int i = 0; i < bushesSeeds.length; i++) {
			if (bushesStages[i] > 0 && bushesStages[i] <= 3 && player.elapsedMinutes - bushesTimer[i] >= 5) {
				bushesStages[i]--;
				bushesTimer[i] = player.elapsedMinutes;
				updateBushesStates();
				continue;
			}

			BushesData bushesData = BushesData.forId(bushesSeeds[i]);

			if (bushesData == null) {
				continue;
			}

			long difference = player.elapsedMinutes - bushesTimer[i];
			long growth = bushesData.getGrowthTime();
			int nbStates = bushesData.getEndingState() - bushesData.getStartingState();
			int state = (int) (difference * nbStates / growth);

			if (bushesTimer[i] == 0 || bushesState[i] == 2 || bushesState[i] == 3) {
				continue;
			}

			if (state > nbStates) {
				state = nbStates;
			}

			if (4 + state != bushesStages[i]) {
				if (bushesStages[i] == bushesData.getEndingState() - bushesData.getStartingState() - 1) {
					bushesStages[i] = bushesData.getEndingState() - bushesData.getStartingState() + 4;
					bushesState[i] = 3;
					updateBushesStates();
					return;
				}

				bushesStages[i] = 4 + state;

				if (bushesStages[i] <= 4 + state) {
					for (int j = bushesStages[i]; j <= 4 + state; j++) {
						doStateCalculation(i);
					}
				}

				updateBushesStates();
			}
		}
	}

	/* calculations about the diseasing chance */
	public void doStateCalculation(int index) {
		if (bushesState[index] == 2) {
			return;
		}

		// if the patch is diseased, it dies, if its watched by a farmer, it
		// goes back to normal

		if (bushesState[index] == 1) {
			bushesState[index] = 0;
			BushesData bushesData = BushesData.forId(bushesSeeds[index]);

			if (bushesData == null) {
				return;
			}

			int difference = bushesData.getEndingState() - bushesData.getStartingState();
			int growth = bushesData.getGrowthTime();
			bushesTimer[index] += growth / difference;
			modifyStage(index);
		}

		if (bushesState[index] == 5 && bushesStages[index] != 2) {
			bushesState[index] = 0;
		}

		if (bushesState[index] == 0 && bushesStages[index] >= 5 && !hasFullyGrown[index]) {
			BushesData bushesData = BushesData.forId(bushesSeeds[index]);

			if (bushesData == null) {
				return;
			}

			double chance = diseaseChance[index] * bushesData.getDiseaseChance();
			int maxChance = (int) chance * 100;

			if (NumberUtil.random(100) <= maxChance) {
				bushesState[index] = 1;
			}
		}
	}

	/* getting the different config values */
	public int getConfigValue(int bushesStage, int seedId, int plantState, int index) {
		BushesData bushesData = BushesData.forId(seedId);

		switch (bushesStage) {
		case 0:// weed
			return Bushes.GROWING << 6;

		case 1:// weed cleared
			return (Bushes.GROWING << 6) + 0x01;

		case 2:
			return (Bushes.GROWING << 6) + 0x02;

		case 3:
			return (Bushes.GROWING << 6) + 0x03;
		}

		if (bushesData == null) {
			return -1;
		}

		if (bushesStage > bushesData.getEndingState() - bushesData.getStartingState() - 1) {
			hasFullyGrown[index] = true;
		}

		if (Bushes.getPlantState(plantState) == 3) {
			return (Bushes.getPlantState(plantState) << 6) + bushesData.getCheckHealthState();
		}

		if (seedId == 5106) {
			if (Bushes.getPlantState(plantState) == 1) {
				return bushesData.getStartingState() + bushesStage - 4 + 12;
			} else if (Bushes.getPlantState(plantState) == 2) {
				return bushesData.getStartingState() + bushesStage - 4 + 20;
			}
		}

		return (Bushes.getPlantState(plantState) << 6) + bushesData.getStartingState() + bushesStage - 4 + (Bushes.getPlantState(plantState) == 2 ? -1 : 0);
	}

	/* harvesting the plant resulted */
	public boolean harvestOrCheckHealth(int objectX, int objectY) {
		final BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(new Location(objectX, objectY));

		if (bushesFieldsData == null) {
			return false;
		}

		final BushesData bushesData = BushesData.forId(bushesSeeds[bushesFieldsData.getBushesIndex()]);

		if (bushesData == null) {
			return false;
		}

		if (player.getInventory().freeSlots() <= 0) {
			player.sendMessage("Not enough space in your inventory.");
			return true;
		}

		player.playAnimation(new Animation(832));
		player.stopTask();
		player.setStopPacket(true);

		GameService.getTaskScheduler().schedule(new Task(2, true) {

			@Override
			public void execute() {
				if (player.getInventory().freeSlots() <= 0) {
					player.setStopPacket(false);
					stop();
					return;
				}

				if (bushesState[bushesFieldsData.getBushesIndex()] == 3) {
					player.sendMessage("You examine the bush for signs of disease and find that it's in perfect health.");
					player.getSkill().addExperience(Skill.FARMING, bushesData.getCheckHealthXp() * Constants.FARMING_EXPERIENCE);
					bushesState[bushesFieldsData.getBushesIndex()] = 0;
					hasFullyGrown[bushesFieldsData.getBushesIndex()] = false;
					bushesTimer[bushesFieldsData.getBushesIndex()] = player.elapsedMinutes - bushesData.getGrowthTime();
					// bushesStages[bushesFieldsData.getBushesIndex()] -= 2;
					Bushes.this.modifyStage(bushesFieldsData.getBushesIndex());
					player.setStopPacket(false);
					stop();
					return;
				}

				player.sendMessage("You harvest the crop, and pick some berries.");
				player.getInventory().add(new Item(bushesData.getHarvestId()));
				player.getSkill().addExperience(Skill.FARMING, bushesData.getHarvestXp() * Constants.FARMING_EXPERIENCE);
				bushesTimer[bushesFieldsData.getBushesIndex()] = player.elapsedMinutes;
				int difference = bushesData.getEndingState() - bushesData.getStartingState();
				int growth = bushesData.getGrowthTime();
				Bushes.this.lowerStage(bushesFieldsData.getBushesIndex(), growth - growth / difference * (difference + 5 - bushesStages[bushesFieldsData.getBushesIndex()]));
				Bushes.this.modifyStage(bushesFieldsData.getBushesIndex());
				player.setStopPacket(false);
				stop();
			}

		});

		return true;
	}

	/* inspecting a plant */
	public boolean inspect(int objectX, int objectY) {
		final BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(new Location(objectX, objectY));

		if (bushesFieldsData == null) {
			return false;
		}

		final InspectData inspectData = InspectData.forId(bushesSeeds[bushesFieldsData.getBushesIndex()]);
		final BushesData bushesData = BushesData.forId(bushesSeeds[bushesFieldsData.getBushesIndex()]);

		if (bushesState[bushesFieldsData.getBushesIndex()] == 1) {
			SimpleDialogues.sendStatement(player, "This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
			return true;
		} else if (bushesState[bushesFieldsData.getBushesIndex()] == 2) {
			SimpleDialogues.sendStatement(player, "This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
			return true;
		} else if (bushesState[bushesFieldsData.getBushesIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This plant has fully grown. You can check it's health", "to gain some farming experiences.");
			return true;
		}
		if (bushesStages[bushesFieldsData.getBushesIndex()] == 0) {
			SimpleDialogues.sendStatement(player, "This is a bush patch. The soil has not been treated.", "The patch needs weeding.");
		} else if (bushesStages[bushesFieldsData.getBushesIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This is a bush patch. The soil has not been treated.", "The patch is empty and weeded.");
		} else if (inspectData != null && bushesData != null) {
			player.sendMessage("You bend down and start to inspect the patch...");
			player.playAnimation(new Animation(1331));
			player.setStopPacket(true);

			GameService.getTaskScheduler().schedule(new Task(5) {

				@Override
				public void execute() {
					if (bushesStages[bushesFieldsData.getBushesIndex()] - 4 < inspectData.getMessages().length - 2) {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[bushesStages[bushesFieldsData.getBushesIndex()] - 4]);
					} else if (bushesStages[bushesFieldsData.getBushesIndex()] < bushesData.getEndingState() - bushesData.getStartingState() + 2) {
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
		for (int i = 0; i < bushesStages.length; i++) {
			bushesStages[i] = buffer.getInt();
		}

		for (int i = 0; i < bushesSeeds.length; i++) {
			bushesSeeds[i] = buffer.getInt();
		}

		for (int i = 0; i < bushesState.length; i++) {
			bushesState[i] = buffer.getInt();
		}

		for (int i = 0; i < bushesTimer.length; i++) {
			bushesTimer[i] = buffer.getLong();
		}

		for (int i = 0; i < diseaseChance.length; i++) {
			diseaseChance[i] = buffer.getDouble();
		}

		for (int i = 0; i < hasFullyGrown.length; i++) {
			hasFullyGrown[i] = ByteBufferUtil.getBoolean(buffer);
		}

		for (int i = 0; i < bushesWatched.length; i++) {
			bushesWatched[i] = ByteBufferUtil.getBoolean(buffer);
		}
	}

	public void lowerStage(int index, int timer) {
		hasFullyGrown[index] = false;
		bushesTimer[index] -= timer;
	}

	/* putting compost onto the plant */

	public void modifyStage(int i) {
		BushesData bushesData = BushesData.forId(bushesSeeds[i]);

		if (bushesData == null) {
			return;
		}

		long difference = player.elapsedMinutes - bushesTimer[i];
		long growth = bushesData.getGrowthTime();
		int nbStates = bushesData.getEndingState() - bushesData.getStartingState();
		int state = (int) (difference * nbStates / growth);
		bushesStages[i] = 4 + state;
		updateBushesStates();
	}

	/* planting the seeds */
	public boolean plantSeed(int objectX, int objectY, final int seedId) {
		final BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(new Location(objectX, objectY));
		final BushesData bushesData = BushesData.forId(seedId);

		if (bushesFieldsData == null || bushesData == null) {
			return false;
		}

		if (bushesStages[bushesFieldsData.getBushesIndex()] != 3) {
			player.sendMessage("You can't plant a seed here.");
			return false;
		}

		if (bushesData.getLevelRequired() > player.getSkill().getLevel(Skill.FARMING)) {
			SimpleDialogues.sendStatement(player, "You need a farming level of " + bushesData.getLevelRequired() + " to plant this seed.");
			return true;
		}

		if (!player.getInventory().contains(FarmingConstants.SEED_DIBBER)) {
			SimpleDialogues.sendStatement(player, "You need a seed dibber to plant seed here.");
			return true;
		}

		if (player.getInventory().getCount(bushesData.getSeedId()) < bushesData.getSeedAmount()) {
			SimpleDialogues.sendStatement(player, "You need atleast " + bushesData.getSeedAmount() + " seeds to plant here.");
			return true;
		}

		player.playAnimation(new Animation(FarmingConstants.SEED_DIBBING));
		bushesStages[bushesFieldsData.getBushesIndex()] = 4;
		player.getInventory().remove(new Item(seedId, bushesData.getSeedAmount()));
		player.setStopPacket(true);

		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				bushesState[bushesFieldsData.getBushesIndex()] = 0;
				bushesSeeds[bushesFieldsData.getBushesIndex()] = seedId;
				bushesTimer[bushesFieldsData.getBushesIndex()] = player.elapsedMinutes;
				player.getSkill().addExperience(Skill.FARMING, bushesData.getPlantingXp() * Constants.FARMING_EXPERIENCE);
				Bushes.this.updateBushesStates();
				player.setStopPacket(false);
				stop();
			}

		});

		return true;
	}

	public boolean putCompost(int objectX, int objectY, final int itemId) {
		if (itemId != 6032 && itemId != 6034) {
			return false;
		}

		final BushesFieldsData bushesFieldsData = BushesFieldsData.forIdPosition(new Location(objectX, objectY));

		if (bushesFieldsData == null) {
			return false;
		}

		if (bushesStages[bushesFieldsData.getBushesIndex()] != 3 || bushesState[bushesFieldsData.getBushesIndex()] == 5) {
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
				diseaseChance[bushesFieldsData.getBushesIndex()] *= itemId == 6032 ? Bushes.COMPOST_CHANCE : Bushes.SUPERCOMPOST_CHANCE;
				bushesState[bushesFieldsData.getBushesIndex()] = 5;
				player.setStopPacket(false);
				stop();
			}

		});

		return true;
	}

	/* This is the enum that hold the different data for inspecting the plant */
	public void resetAll() {
		this.resetBushes();
	}

	private void resetBushes() {
		bushesStages = new int[4];
		bushesSeeds = new int[4];
		bushesState = new int[4];
		bushesTimer = new long[4];
		hasFullyGrown = new boolean[] { false, false, false, false };
		bushesWatched = new boolean[] { false, false, false, false };
	}

	private void resetBushes(int index) {
		bushesSeeds[index] = 0;
		bushesState[index] = 0;
		diseaseChance[index] = 1;
		hasFullyGrown[index] = false;
		bushesWatched[index] = false;
	}

	public void saveStages(ByteBuffer buffer) {
		for (Integer i : bushesStages) {
			buffer.putInt(i);
		}

		for (Integer i : bushesSeeds) {
			buffer.putInt(i);
		}

		for (Integer i : bushesState) {
			buffer.putInt(i);
		}

		for (Long i : bushesTimer) {
			buffer.putLong(i);
		}

		for (Double i : diseaseChance) {
			buffer.putDouble(i);
		}

		for (Boolean i : hasFullyGrown) {
			ByteBufferUtil.putBoolean(buffer, i);
		}

		for (Boolean i : bushesWatched) {
			ByteBufferUtil.putBoolean(buffer, i);
		}
	}

	/* update all the patch states */
	public void updateBushesStates() {
		// etceteria - south ardougne - champion guild - rimmington
		int[] configValues = new int[bushesStages.length];
		int configValue;

		for (int i = 0; i < bushesStages.length; i++) {
			configValues[i] = getConfigValue(bushesStages[i], bushesSeeds[i], bushesState[i], i);
		}

		configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
		player.getSession().send(new SendConfigPacket(Bushes.MAIN_BUSHES_CONFIG, configValue));
	}

}
