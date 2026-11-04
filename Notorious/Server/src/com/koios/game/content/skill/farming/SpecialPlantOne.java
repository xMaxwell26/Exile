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
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.task.Task;
import com.koios.util.ByteBufferUtil;
import com.koios.util.NumberUtil;

public class SpecialPlantOne {

	public enum InspectData {
		SPIRIT_TREE(5375,
				new String[][] { { "The spirit tree sapling has only just been planted. It has not grown yet." },
						{ "The spirit tree has grown slightly, and sprouted a few more leaves." },
						{ "Some dark spots have appeared on the trees trunk, and the leaves have grown longer." },
						{ "The tree has grown larger in all respects, and has grown more leaves." },
						{ "The spirit tree base has widened showing some roots, and the leaves have morphed into a small canopy.",
								"Two small branches have appeared on either side of the trunk." },
				{ "The spirit tree has grown wider in girth, but is still the same z as before.", "The base is larger as well." },
				{ "The spirit tree has grown larger in all respects.", "The trunk is more warped towards the west, and the roots are more visible" },
				{ "The spirit tree is larger in all respects.", "The trunk has grown in a 'S' shape." },
				{ "The spirit tree has grown another knob on the trunk which will eventually become its nose.",
						"The tree is larger in all respects, and its branches are growing out to the sides more." },
				{ "The spirit tree canopy shifts the angle its inclining towards, and its branches are almost parallel to the ground.",
						"he nose is more defined, and the tree is slightly larger." },
				{ "The spirit tree branches are slightly angling towards the ground, and it is slightly larger than before." },
				{ "The spirit tree canopy is smaller, the face is fully defined and the texture of the tree has changed dramatically",
						"The Spirit tree is ready to be checked." } }), CALQUAT_TREE(
								5503,
								new String[][] { { "The calquat sapling has only just been planted." }, { "The calquat tree grows another segment taller." },
										{ "The calquat tree grows another segment taller." },
										{ "The calquat tree grows another segment longer and starts a branch midway up its trunk." }, { "The calquat tree grows some leaves." },
										{ "The calquat tree grows another segment upside down and grows leaves on its mid-branch." },
										{ "The calquat tree grows towards the ground." }, { "The calquat tree is ready to be harvested." }, });

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

		public int getSeedId() {
			return saplingId;
		}
	}

	public enum SpecialPlantData {
		SPIRIT_TREE(5375, -1, 1, 83, 3680, 0.15, 199.5, 0, 0x09, 0x14, 0x2c, 19301.8, 12, 23), CALQUAT_TREE(5503, 5980, 1, 72, 1200, 0.15, 129.5, 48.5, 0x04, 0x12, 0x22, 12096, 14,
				20);

		public static SpecialPlantData forId(int saplingId) {
			return SpecialPlantData.saplings.get(saplingId);
		}

		private int saplingId;
		private int harvestId;
		private int saplingAmount;
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

		private static Map<Integer, SpecialPlantData> saplings = new HashMap<>();

		static {
			for (SpecialPlantData data : SpecialPlantData.values()) {
				SpecialPlantData.saplings.put(data.saplingId, data);
			}
		}

		SpecialPlantData(int saplingId, int harvestId, int saplingAmount, int levelRequired, int growthTime, double diseaseChance, double plantingXp, double harvestXp,
				int startingState, int endingState, int checkHealthState, double checkHealthExperience, int diseaseDiffValue, int deathDiffValue) {
			this.saplingId = saplingId;
			this.harvestId = harvestId;
			this.saplingAmount = saplingAmount;
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

		public int getSapplingId() {
			return saplingId;
		}

		public int getSeedAmount() {
			return saplingAmount;
		}

		public int getStartingState() {
			return startingState;
		}
	}

	public enum SpecialPlantFieldsData {
		BRIMHAVEN(0, new Location[] { new Location(2801, 3202), new Location(2803, 3204) }, 5375), KARAMJA(1, new Location[] { new Location(2795, 3100), new Location(2797, 3102) },
				5503), DRAYNOR(2, new Location[] { new Location(3059, 3257), new Location(3061, 3259) }, 5375), ETCETERIA(3,
						new Location[] { new Location(2612, 3857), new Location(2614, 3859) }, 5375);

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

		public int getSaplingd() {
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
	public int[] specialPlantSaplings = new int[4];

	public int[] specialPlantState = new int[4];

	public long[] specialPlantTimer = new long[4];

	public double[] diseaseChance = { 1, 1, 1, 1 };

	/* This is the enum holding the saplings info */

	public boolean[] hasFullyGrown = { false, false, false, false };

	/* This is the enum data about the different patches */

	public static final int MAIN_SPECIAL_PLANT_CONFIG = 507;

	/* This is the enum that hold the different data for inspecting the plant */

	public SpecialPlantOne(Player player) {
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
					SpecialPlantOne.this.resetSpecialPlants(hopsFieldsData.getSpecialPlantsIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
				}
				player.getSkill().addExperience(Skill.FARMING, SpecialPlantOne.CLEARING_EXPERIENCE * Constants.FARMING_EXPERIENCE);
				specialPlantTimer[hopsFieldsData.getSpecialPlantsIndex()] = player.elapsedMinutes;
				SpecialPlantOne.this.updateSpecialPlants();
				if (specialPlantStages[hopsFieldsData.getSpecialPlantsIndex()] == 3) {
					SpecialPlantOne.this.resetSpecialPlants(hopsFieldsData.getSpecialPlantsIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
					return;
				}
			}
		});
		return true;
	}

	/* update all the patch states */

	public boolean curePlant(int objectX, int objectY, int itemId) {
		SpecialPlantFieldsData specialPlantFieldsData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		if (specialPlantFieldsData == null || itemId != 6036) {
			return false;
		}
		SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSaplings[specialPlantFieldsData.getSpecialPlantsIndex()]);
		if (specialPlantData == null) {
			return false;
		}
		if (specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] != 1) {
			player.sendMessage("This plant doesn't need to be cured.");
			return true;
		}
		player.getInventory().remove(new Item(itemId));
		player.getInventory().add(new Item(229));
		player.playAnimation(new Animation(FarmingConstants.CURING_ANIM));
		player.setStopPacket(true);
		specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] = 0;
		GameService.getTaskScheduler().schedule(new Task(7) {

			@Override
			public void execute() {
				player.sendMessage("You cure the plant with a plant cure.");
				SpecialPlantOne.this.updateSpecialPlants();
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* getting the different config values */

	public void doCalculations() {
		for (int i = 0; i < specialPlantSaplings.length; i++) {
			if (specialPlantStages[i] > 0 && specialPlantStages[i] <= 3 && player.elapsedMinutes - specialPlantTimer[i] >= 5) {
				specialPlantStages[i]--;
				specialPlantTimer[i] = player.elapsedMinutes;
				updateSpecialPlants();
				continue;
			}
			SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSaplings[i]);
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
					+ (specialPlantData == SpecialPlantData.SPIRIT_TREE ? 3 : -2)) {
				if (specialPlantStages[i] == specialPlantData.getEndingState() - specialPlantData.getStartingState()
						+ (specialPlantData == SpecialPlantData.SPIRIT_TREE ? 3 : -2)) {
					specialPlantStages[i] = specialPlantData.getEndingState() - specialPlantData.getStartingState() + 7;
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
			SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSaplings[index]);
			if (specialPlantData == null) {
				return;
			}
			double chance = diseaseChance[index] * specialPlantData.getDiseaseChance();
			int maxChance = (int) chance * 100;
			if (NumberUtil.random(100) < maxChance) {
				specialPlantState[index] = 1;
			}
		}
	}

	/* calculating the disease chance and making the plant grow */

	public int getConfigValue(int specialStage, int saplingId, int plantState, int index) {
		SpecialPlantData specialPlantData = SpecialPlantData.forId(saplingId);
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
		if (SpecialPlantOne.getPlantState(plantState, specialPlantData, specialStage) == 3) {
			return specialPlantData.getCheckHealthState();
		}
		return SpecialPlantOne.getPlantState(plantState, specialPlantData, specialStage);
	}

	private void handleSpiritTree() {
		// player.setClickId(3636);
		// player.getPA().sendDialogue(3636, 1);
	}

	/* calculations about the diseasing chance */

	public boolean harvestOrCheckHealth(int objectX, int objectY) {
		final SpecialPlantFieldsData specialPlantFieldsData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		if (specialPlantFieldsData == null) {
			return false;
		}
		final SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSaplings[specialPlantFieldsData.getSpecialPlantsIndex()]);
		if (specialPlantData == null) {
			return false;
		}
		if (specialPlantData == SpecialPlantData.SPIRIT_TREE && specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] != 3) {
			handleSpiritTree();
			return true;
		}
		if (player.getInventory().freeSlots() <= 0) {
			player.sendMessage("Not enough space in your inventory.");
			return true;
		}
		player.playAnimation(new Animation(832));
		player.setStopPacket(true);
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
					SpecialPlantOne.this.modifyStage(specialPlantFieldsData.getSpecialPlantsIndex());
					player.setStopPacket(false);
					stop();
					return;
				}
				player.sendMessage("You harvest the crop, and pick some " + ItemDefinition.forId(specialPlantData.getHarvestId()).getName().toLowerCase() + '.');
				player.getInventory().add(new Item(specialPlantData.getHarvestId()));
				player.getSkill().addExperience(Skill.FARMING, specialPlantData.getHarvestXp() * Constants.FARMING_EXPERIENCE);
				specialPlantStages[specialPlantFieldsData.getSpecialPlantsIndex()]--;
				SpecialPlantOne.this.updateSpecialPlants();
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* clearing the patch with a rake of a spade */

	public boolean inspect(int objectX, int objectY) {
		final SpecialPlantFieldsData specialPlantFieldsData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		if (specialPlantFieldsData == null) {
			return false;
		}
		final InspectData inspectData = InspectData.forId(specialPlantSaplings[specialPlantFieldsData.getSpecialPlantsIndex()]);
		final SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSaplings[specialPlantFieldsData.getSpecialPlantsIndex()]);
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

	/* planting the saplings */

	public void loadStages(ByteBuffer buffer) {
		for (int i = 0; i < specialPlantStages.length; i++) {
			specialPlantStages[i] = buffer.getInt();
		}
		for (int i = 0; i < specialPlantSaplings.length; i++) {
			specialPlantSaplings[i] = buffer.getInt();
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

	/* harvesting the plant resulted */

	public void lowerStage(int index, int timer) {
		hasFullyGrown[index] = false;
		specialPlantTimer[index] -= timer;
	}

	public void modifyStage(int i) {
		SpecialPlantData specialPlantData = SpecialPlantData.forId(specialPlantSaplings[i]);
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

	public boolean plantSapling(int objectX, int objectY, final int saplingId) {
		final SpecialPlantFieldsData specialPlantFieldData = SpecialPlantFieldsData.forIdPosition(new Location(objectX, objectY));
		final SpecialPlantData specialPlantData = SpecialPlantData.forId(saplingId);
		if (specialPlantFieldData == null || specialPlantData == null || specialPlantFieldData.getSaplingd() != saplingId) {
			return false;
		}
		if ((specialPlantStages[0] > 3 || specialPlantStages[2] > 3 || specialPlantStages[3] > 3) && specialPlantFieldData.getSpecialPlantsIndex() != 1) {
			player.sendMessage("You already have a spirit tree planted somewhere else.");
			return true;
		}
		if (specialPlantStages[specialPlantFieldData.getSpecialPlantsIndex()] != 3) {
			player.sendMessage("You can't plant a sapling here.");
			return true;
		}

		if (specialPlantData.getLevelRequired() > player.getSkill().getLevel(Skill.FARMING)) {
			SimpleDialogues.sendStatement(player, "You need a farming level of " + specialPlantData.getLevelRequired() + " to plant this sapling.");
			return true;
		}
		if (!player.getInventory().contains(FarmingConstants.TROWEL)) {
			SimpleDialogues.sendStatement(player, "You need a trowel to plant the sapling here.");
			return true;
		}
		player.playAnimation(new Animation(FarmingConstants.PLANTING_POT_ANIM));
		specialPlantStages[specialPlantFieldData.getSpecialPlantsIndex()] = 4;
		player.getInventory().remove(new Item(saplingId));
		player.setStopPacket(true);
		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				specialPlantState[specialPlantFieldData.getSpecialPlantsIndex()] = 0;
				specialPlantSaplings[specialPlantFieldData.getSpecialPlantsIndex()] = saplingId;
				specialPlantTimer[specialPlantFieldData.getSpecialPlantsIndex()] = player.elapsedMinutes;
				player.getSkill().addExperience(Skill.FARMING, specialPlantData.getPlantingXp() * Constants.FARMING_EXPERIENCE);
				SpecialPlantOne.this.updateSpecialPlants();
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
				diseaseChance[specialPlantFieldsData.getSpecialPlantsIndex()] *= itemId == 6032 ? SpecialPlantOne.COMPOST_CHANCE : SpecialPlantOne.SUPERCOMPOST_CHANCE;
				specialPlantState[specialPlantFieldsData.getSpecialPlantsIndex()] = 5;
				player.setStopPacket(false);
				stop();
			}
		});
		return true;
	}

	/* inspecting a plant */

	public void resetAll() {
		this.resetSpecialPlants();
	}

	/* opening the corresponding guide about the patch */

	public void resetSpecialPlants() {
		specialPlantStages = new int[4];
		specialPlantSaplings = new int[4];
		specialPlantState = new int[4];
		specialPlantTimer = new long[4];
		hasFullyGrown = new boolean[] { false, false, false, false };
	}

	/* Curing the plant */

	private void resetSpecialPlants(int index) {
		specialPlantSaplings[index] = 0;
		specialPlantState[index] = 0;
		diseaseChance[index] = 1;
		hasFullyGrown[index] = false;
	}

	public void saveStages(ByteBuffer buffer) {
		for (Integer i : specialPlantStages) {
			buffer.putInt(i);
		}
		for (Integer i : specialPlantSaplings) {
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
		// brimhaven - karamja - draynor - Etceteria
		int[] configValues = new int[specialPlantStages.length];
		int configValue;
		for (int i = 0; i < specialPlantStages.length; i++) {
			configValues[i] = getConfigValue(specialPlantStages[i], specialPlantSaplings[i], specialPlantState[i], i);
		}
		configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
		player.getSession().send(new SendConfigPacket(SpecialPlantOne.MAIN_SPECIAL_PLANT_CONFIG, configValue));
	}
}
