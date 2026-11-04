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
import com.koios.util.NumberUtil;

public class Herbs {

	public enum HerbData {
		GUAM(5291, 199, 9, 80, 0.25, 11, 12.5, 0x04, 0x08), MARRENTILL(5292, 201, 14, 80, 0.25, 13.5, 15, 0x0b, 0x0f), TARROMIN(5293, 203, 19, 80, 0.25, 16, 18, 0x12,
				0x16), HARRALANDER(5294, 205, 26, 80, 0.25, 21.5, 24, 0x19, 0x1d), GOUT_TUBER(6311, 3261, 29, 80, 0.25, 105, 45, 0xc0, 0xc4), RANARR(5295, 207, 32, 80, 0.20, 27,
						30.5, 0x20, 0x24), TOADFLAX(5296, 3049, 38, 80, 0.20, 34, 38.5, 0x27, 0x2b), IRIT(5297, 209, 44, 80, 0.20, 43, 48.5, 0x2e, 0x32), AVANTOE(5298, 211, 50, 80,
								0.20, 54.5, 61.5, 0x35, 0x39), KUARM(5299, 213, 56, 80, 0.20, 69, 78, 0x44, 0x48), SNAPDRAGON(5300, 3051, 62, 80, 0.15, 87.5, 98.5, 0x4b,
										0x4f), CADANTINE(5301, 215, 67, 80, 0.15, 106.5, 120, 0x52, 0x56), LANTADYME(5302, 2485, 73, 80, 0.15, 134.5, 151.5, 0x59,
												0x5d), DWARF(5303, 217, 79, 80, 0.15, 170.5, 192, 0x60, 0x64), TORSOL(5304, 219, 85, 80, 0.15, 199.5, 224.5, 0x67, 0x6b);

		public static HerbData forId(int seedId) {
			return HerbData.seeds.get(seedId);
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

		private static Map<Integer, HerbData> seeds = new HashMap<>();

		static {
			for (HerbData data : HerbData.values()) {
				HerbData.seeds.put(data.seedId, data);
			}
		}

		HerbData(int seedId, int harvestId, int levelRequired, int growthTime, double diseaseChance, double plantingXp, double harvestXp, int startingState, int endingState) {
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

	public enum HerbFieldsData {
		ARDOUGNE(0, new Location[] { new Location(2670, 3374), new Location(2671, 3375) }), PHASMATYS(1,
				new Location[] { new Location(3605, 3529), new Location(3606, 3530) }), FALADOR(2,
						new Location[] { new Location(3058, 3311), new Location(3059, 3312) }), CATHERBY(3, new Location[] { new Location(2813, 3463), new Location(2814, 3464) });

		public static HerbFieldsData forIdPosition(Location position) {
			for (HerbFieldsData herbFieldsData : HerbFieldsData.values()) {
				if (FarmingConstants.inRangeArea(herbFieldsData.getHerbPosition()[0], herbFieldsData.getHerbPosition()[1], position)) {
					return herbFieldsData;
				}
			}
			return null;
		}

		private int herbIndex;

		private Location[] herbPosition;

		HerbFieldsData(int herbIndex, Location[] herbPosition) {
			this.herbIndex = herbIndex;
			this.herbPosition = herbPosition;
		}

		public int getHerbIndex() {
			return herbIndex;
		}

		public Location[] getHerbPosition() {
			return herbPosition;
		}
	}

	public enum InspectData {
		GUAM(5291,
				new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle z." }, { "The herb is now knee z." }, { "The herb is now mid-thigh z." },
						{ "The herb is fully grown and ready to harvest." } }), MARRENTILL(
								5292,
								new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle z." }, { "The herb is now knee z." },
										{ "The herb is now mid-thigh z." }, { "The herb is fully grown and ready to harvest." } }), TARROMIN(
												5293,
												new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle z." }, { "The herb is now knee z." }, {
														"The herb is now mid-thigh z." },
								{ "The herb is fully grown and ready to harvest." } }), HARRALANDER(
										5294,
										new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle z." }, { "The herb is now knee z." }, {
												"The herb is now mid-thigh z." },
										{ "The herb is fully grown and ready to harvest." } }), GOUT_TUBER(
												6311,
												new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle z." }, { "The herb is now knee z." }, {
														"The herb is now mid-thigh z." },
												{ "The herb is fully grown and ready to harvest." } }), RANARR(
														5295,
														new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle z." }, {
																"The herb is now knee z." }, { "The herb is now mid-thigh z." },
														{ "The herb is fully grown and ready to harvest." } }), TOADFLAX(
																5296,
																new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle z." }, {
																		"The herb is now knee z." }, { "The herb is now mid-thigh z." },
																{ "The herb is fully grown and ready to harvest." } }), IRIT(
																		5297,
																		new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle z." }, {
																				"The herb is now knee z." }, { "The herb is now mid-thigh z." },
																		{ "The herb is fully grown and ready to harvest." } }), AVANTOE(
																				5298,
																				new String[][] { { "The seed has only just been planted." }, { "The herb is now ankle z." }, {
																						"The herb is now knee z." }, { "The herb is now mid-thigh z." },
																				{ "The herb is fully grown and ready to harvest." } }), KUARM(
																						5299,
																						new String[][] { { "The seed has only just been planted." }, {
																								"The herb is now ankle z." }, { "The herb is now knee z." },
																						{ "The herb is now mid-thigh z." },
																						{ "The herb is fully grown and ready to harvest." } }), SNAPDRAGON(
																								5300,
																								new String[][] { { "The seed has only just been planted." },
																										{ "The herb is now ankle z." }, { "The herb is now knee z." },
																										{ "The herb is now mid-thigh z." },
																										{ "The herb is fully grown and ready to harvest." } }), CADANTINE(
																												5301,
																												new String[][] { { "The seed has only just been planted." },
																														{ "The herb is now ankle z." },
																														{ "The herb is now knee z." },
																														{ "The herb is now mid-thigh z." },
																														{ "The herb is fully grown and ready to harvest." } }), LANTADYME(
																																5302,
																																new String[][] {
																																		{ "The seed has only just been planted." },
																																		{ "The herb is now ankle z." },
																																		{ "The herb is now knee z." }, {
																																				"The herb is now mid-thigh z." },
																																{ "The herb is fully grown and ready to harvest." } }), DWARF(
																																		5303,
																																		new String[][] {
																																				{ "The seed has only just been planted." },
																																				{ "The herb is now ankle z." },
																																				{ "The herb is now knee z." },
																																				{ "The herb is now mid-thigh z." },
																																				{ "The herb is fully grown and ready to harvest." } }), TORSOL(
																																						5304,
																																						new String[][] {
																																								{ "The seed has only just been planted." },
																																								{ "The herb is now ankle z." },
																																								{ "The herb is now knee z." },
																																								{ "The herb is now mid-thigh z." },
																																								{ "The herb is fully grown and ready to harvest." } });

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
			return Herbs.GROWING;
		}
		return -1;
	}

	public static boolean guide(int objectX, int objectY) {
		HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(new Location(objectX, objectY));
		if (herbFieldsData == null) {
			return false;
		}
		return true;
	}

	private final Player player;

	// set of global constants for Farming
	private static final int START_HARVEST_AMOUNT = 3;

	private static final int END_HARVEST_AMOUNT = 18;
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;
	// Farming data
	public int[] herbStages = new int[4];
	public int[] herbSeeds = new int[4];

	public int[] herbHarvest = new int[4];

	public int[] herbState = new int[4];

	public long[] herbTimer = new long[4];
	public double[] diseaseChance = { 1, 1, 1, 1, 1 };

	/* This is the enum holding the seeds info */

	/* set of the constants for the patch */
	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;

	/* This is the enum data about the different patches */

	public static final int MAIN_HERB_LOCATION_CONFIG = 515;

	/* This is the enum that hold the different data for inspecting the plant */

	public Herbs(Player player) {
		this.player = player;
	}

	public boolean checkIfRaked(int objectX, int objectY) {
		HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(new Location(objectX, objectY));
		if (herbFieldsData == null) {
			return false;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] == 3) {
			return true;
		}
		return false;
	}

	/* update all the patch states */

	public boolean clearPatch(int objectX, int objectY, int itemId) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(new Location(objectX, objectY));
		int finalAnimation;
		int finalDelay;
		if (herbFieldsData == null || itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE) {
			return false;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] == 3) {
			return true;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] <= 3) {
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
		int times = 3 - herbStages[herbFieldsData.getHerbIndex()];
		GameService.getTaskScheduler().schedule(new Task(finalDelay, times) {

			@Override
			public void execute() {
				player.playAnimation(new Animation(animation));
				if (herbStages[herbFieldsData.getHerbIndex()] <= 2) {
					herbStages[herbFieldsData.getHerbIndex()]++;
					player.getInventory().add(new Item(6055));
				} else {
					herbStages[herbFieldsData.getHerbIndex()] = 3;
					Herbs.this.resetHerbs(herbFieldsData.getHerbIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
				}
				player.getSkill().addExperience(Skill.FARMING, Herbs.CLEARING_EXPERIENCE * Constants.FARMING_EXPERIENCE);
				herbTimer[herbFieldsData.getHerbIndex()] = player.elapsedMinutes;
				Herbs.this.updateHerbsStates();
				if (herbStages[herbFieldsData.getHerbIndex()] == 3) {
					Herbs.this.resetHerbs(herbFieldsData.getHerbIndex());
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
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(new Location(objectX, objectY));
		if (herbFieldsData == null || itemId != 6036) {
			return false;
		}
		HerbData herbData = HerbData.forId(herbSeeds[herbFieldsData.getHerbIndex()]);
		if (herbData == null) {
			return false;
		}
		if (herbState[herbFieldsData.getHerbIndex()] != 1) {
			player.sendMessage("This plant doesn't need to be cured.");
			return true;
		}
		player.getInventory().remove(new Item(itemId));
		player.getInventory().add(new Item(229));
		player.playAnimation(new Animation(FarmingConstants.CURING_ANIM));
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(7) {

			@Override
			public void execute() {
				player.sendMessage("You cure the plant with a plant cure.");
				herbState[herbFieldsData.getHerbIndex()] = 0;
				Herbs.this.updateHerbsStates();
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* getting the plant states */

	public void doCalculations() {
		for (int i = 0; i < herbSeeds.length; i++) {
			if (herbStages[i] > 0 && herbStages[i] <= 3 && player.elapsedMinutes - herbTimer[i] >= 5) {
				herbStages[i]--;
				herbTimer[i] = player.elapsedMinutes;
				updateHerbsStates();
			}
			HerbData herbData = HerbData.forId(herbSeeds[i]);
			if (herbData == null) {
				continue;
			}
			long difference = player.elapsedMinutes - herbTimer[i];
			long growth = herbData.getGrowthTime();
			int nbStates = herbData.getEndingState() - herbData.getStartingState();
			int state = (int) (difference * nbStates / growth);
			if (state > nbStates) {
				state = nbStates;
			}
			if (herbTimer[i] == 0 || herbState[i] == 2) {
				continue;
			}
			if (4 + state != herbStages[i]) {
				herbStages[i] = 4 + state;
				doStateCalculation(i);
				updateHerbsStates();
			}
		}
	}

	/* calculating the disease chance and making the plant grow */

	public void doStateCalculation(int index) {
		if (herbState[index] == 2) {
			return;
		}
		// if the patch is diseased, it dies, if its watched by a farmer, it
		// goes back to normal
		/* if (herbState[index] == 1) { herbState[index] = 2; } */
		if (herbState[index] == 4 && herbStages[index] != 3) {
			herbState[index] = 0;
		}
		if (herbState[index] == 0 && herbStages[index] >= 4 && herbStages[index] <= 7) {
			HerbData herbData = HerbData.forId(herbSeeds[index]);
			if (herbData == null) {
				return;
			}
			double chance = diseaseChance[index] * herbData.getDiseaseChance();
			int maxChance = (int) chance * 100;
			if (NumberUtil.random(100) <= maxChance) {
				herbState[index] = 1;
			}
		}
	}

	/* calculations about the diseasing chance */

	public int getConfigValue(int herbStage, int seedId, int plantState, int index) {
		HerbData herbData = HerbData.forId(seedId);
		switch (herbStage) {
		case 0:// weed
			return (Herbs.GROWING << 6) + 0x00;
		case 1:// weed cleared
			return (Herbs.GROWING << 6) + 0x01;
		case 2:
			return (Herbs.GROWING << 6) + 0x02;
		case 3:
			return (Herbs.GROWING << 6) + 0x03;
		}
		if (herbData == null) {
			return -1;
		}
		if (herbSeeds[index] == 6311) {
			if (plantState == 1) {
				return herbStages[index] + 0xc1;
			} else if (plantState == 2) {
				return herbStages[index] + 0xc3;
			}
		}
		return (plantState == 2 ? herbStages[index] + 0x9e : plantState == 1 ? herbStages[index] + 0x9a : Herbs.getPlantState(plantState) << 6) + herbData.getStartingState()
				+ herbStage - 4;
	}

	/* clearing the patch with a rake of a spade */

	public boolean harvest(int objectX, int objectY) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(new Location(objectX, objectY));
		if (herbFieldsData == null) {
			return false;
		}
		final HerbData herbData = HerbData.forId(herbSeeds[herbFieldsData.getHerbIndex()]);
		if (herbData == null) {
			return false;
		}
		if (herbData.getEndingState() > herbData.getStartingState() + herbStages[herbFieldsData.getHerbIndex()] - 1) {
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
		player.playAnimation(new Animation(FarmingConstants.PICKING_VEGETABLE_ANIM));
		player.stopTask();
		player.setStopPacket(true);
		player.setSkilling(new Task(3) {

			@Override
			public void execute() {
				if (herbHarvest[herbFieldsData.getHerbIndex()] == 0) {
					herbHarvest[herbFieldsData.getHerbIndex()] = (int) (1 + (Herbs.START_HARVEST_AMOUNT + NumberUtil.random(Herbs.END_HARVEST_AMOUNT - Herbs.START_HARVEST_AMOUNT))
							* (player.getInventory().contains(7409) ? 1.10 : 1));
				}
				if (herbHarvest[herbFieldsData.getHerbIndex()] == 1) {
					Herbs.this.resetHerbs(herbFieldsData.getHerbIndex());
					herbStages[herbFieldsData.getHerbIndex()] = 3;
					herbTimer[herbFieldsData.getHerbIndex()] = player.elapsedMinutes;
					Herbs.this.updateHerbsStates();
					player.setStopPacket(false);
					return;
				}
				herbHarvest[herbFieldsData.getHerbIndex()]--;
				player.playAnimation(new Animation(FarmingConstants.PICKING_HERB_ANIM));
				player.sendMessage("You harvest the crop, and get some herbs.");
				player.getInventory().add(new Item(herbData.getHarvestId()));
				player.getSkill().addExperience(Skill.FARMING, herbData.getHarvestXp() * Constants.FARMING_EXPERIENCE);
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* planting the seeds */

	public boolean inspect(int objectX, int objectY) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(new Location(objectX, objectY));
		if (herbFieldsData == null) {
			return false;
		}
		final InspectData inspectData = InspectData.forId(herbSeeds[herbFieldsData.getHerbIndex()]);
		final HerbData herbData = HerbData.forId(herbSeeds[herbFieldsData.getHerbIndex()]);
		if (herbState[herbFieldsData.getHerbIndex()] == 1) {
			SimpleDialogues.sendStatement(player, "This plant is diseased. Use a plant cure on it to cure it, ", "or clear the patch with a spade.");
			return true;
		} else if (herbState[herbFieldsData.getHerbIndex()] == 2) {
			SimpleDialogues.sendStatement(player, "This plant is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
			return true;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] == 0) {
			SimpleDialogues.sendStatement(player, "This is an herb patch. The soil has not been treated.", "The patch needs weeding.");
		} else if (herbStages[herbFieldsData.getHerbIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This is an herb patch. The soil has not been treated.", "The patch is empty and weeded.");
		} else if (inspectData != null && herbData != null) {
			player.sendMessage("You bend down and start to inspect the patch...");
			player.playAnimation(new Animation(1331));
			player.setStopPacket(true);
			GameService.getTaskScheduler().schedule(new Task(5) {

				@Override
				public void execute() {
					if (herbStages[herbFieldsData.getHerbIndex()] - 4 < inspectData.getMessages().length - 2) {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[herbStages[herbFieldsData.getHerbIndex()] - 4]);
					} else if (herbStages[herbFieldsData.getHerbIndex()] < herbData.getEndingState() - herbData.getStartingState() + 2) {
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

	/* harvesting the plant resulted */

	public void loadStages(ByteBuffer buffer) {
		for (int i = 0; i < herbStages.length; i++) {
			herbStages[i] = buffer.getInt();
		}
		for (int i = 0; i < herbSeeds.length; i++) {
			herbSeeds[i] = buffer.getInt();
		}
		for (int i = 0; i < herbHarvest.length; i++) {
			herbHarvest[i] = buffer.getInt();
		}
		for (int i = 0; i < herbState.length; i++) {
			herbState[i] = buffer.getInt();
		}
		for (int i = 0; i < herbTimer.length; i++) {
			herbTimer[i] = buffer.getLong();
		}
		for (int i = 0; i < diseaseChance.length; i++) {
			diseaseChance[i] = buffer.getDouble();
		}
	}

	/* putting compost onto the plant */

	public boolean plantSeed(int objectX, int objectY, final int seedId) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(new Location(objectX, objectY));
		final HerbData herbData = HerbData.forId(seedId);
		if (herbFieldsData == null || herbData == null) {
			return false;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] != 3) {
			player.sendMessage("You can't plant a seed here.");
			return false;
		}
		if (herbData.getLevelRequired() > player.getSkill().getLevel(Skill.FARMING)) {
			SimpleDialogues.sendStatement(player, "You need a farming level of " + herbData.getLevelRequired() + " to plant this seed.");
			return true;
		}
		if (!player.getInventory().contains(FarmingConstants.SEED_DIBBER)) {
			SimpleDialogues.sendStatement(player, "You need a seed dibber to plant seed here.");
			return true;
		}
		player.playAnimation(new Animation(FarmingConstants.SEED_DIBBING));
		player.getInventory().remove(new Item(seedId));
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				herbState[herbFieldsData.getHerbIndex()] = 0;
				herbStages[herbFieldsData.getHerbIndex()] = 4;
				herbSeeds[herbFieldsData.getHerbIndex()] = seedId;
				herbTimer[herbFieldsData.getHerbIndex()] = player.elapsedMinutes;
				player.getSkill().addExperience(Skill.FARMING, herbData.getPlantingXp() * Constants.FARMING_EXPERIENCE);
				Herbs.this.updateHerbsStates();
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
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(new Location(objectX, objectY));
		if (herbFieldsData == null) {
			return false;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] != 3 || herbState[herbFieldsData.getHerbIndex()] == 4) {
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
				diseaseChance[herbFieldsData.getHerbIndex()] *= itemId == 6032 ? Herbs.COMPOST_CHANCE : Herbs.SUPERCOMPOST_CHANCE;
				herbState[herbFieldsData.getHerbIndex()] = 4;
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* opening the corresponding guide about the patch */

	public void resetAll() {
		this.resetHerbs();
	}

	/* Curing the plant */

	private void resetHerbs() {
		herbStages = new int[4];
		herbSeeds = new int[4];
		herbHarvest = new int[4];
		herbState = new int[4];
		herbTimer = new long[4];
	}

	private void resetHerbs(int index) {
		herbSeeds[index] = 0;
		herbState[index] = 0;
		diseaseChance[index] = 1;
		herbHarvest[index] = 0;
	}

	/* reseting the patches */

	public void saveStages(ByteBuffer buffer) {
		for (Integer i : herbStages) {
			buffer.putInt(i);
		}
		for (Integer i : herbSeeds) {
			buffer.putInt(i);
		}
		for (Integer i : herbHarvest) {
			buffer.putInt(i);
		}
		for (Integer i : herbState) {
			buffer.putInt(i);
		}
		for (Long i : herbTimer) {
			buffer.putLong(i);
		}
		for (Double i : diseaseChance) {
			buffer.putDouble(i);
		}
	}

	/* checking if the patch is raked */

	public void updateHerbsStates() {
		// falador catherby ardougne phasmatys
		int[] configValues = new int[herbStages.length];
		int configValue;
		for (int i = 0; i < herbStages.length; i++) {
			configValues[i] = getConfigValue(herbStages[i], herbSeeds[i], herbState[i], i);
		}
		configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
		player.getSession().send(new SendConfigPacket(Herbs.MAIN_HERB_LOCATION_CONFIG, configValue));
	}
}
