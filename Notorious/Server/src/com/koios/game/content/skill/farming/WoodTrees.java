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
import com.koios.util.NumberUtil;

public class WoodTrees {

	public enum InspectData {
		OAK(5370,
				new String[][] { { "The acorn sapling has only just been planted." }, { "The acorn sapling grows larger." }, { "The oak tree produces a small canopy." },
						{ "The oak tree grows larger." }, { "The oak tree is ready to harvest." } }), WILLOW(
								5371,
								new String[][] { { "The willow sapling has only just been planted." }, { "The willow sapling grows a few small branches." },
										{ "The willow tree develops a small canopy." }, { "The willow tree trunk becomes dark brown,", "and the canopy grows." },
										{ "The trunk thickens, and the canopy grows",
												"yet larger." },
						{ "The willow tree is fully grown." } }), MAPLE(
								5372,
								new String[][] { { "The maple sapling has only just been planted." }, { "The mapling sapling grows a few small branches." },
										{ "The maple tree develops a small canopy." }, { "The maple tree trunk straightens and the canopy", "grows larger." },
										{ "The maple tree canopy grows." }, { "The maple tree grows." }, { "The maple tree is ready to be harvested." } }), YEW(
												5373,
												new String[][] { { "The yew sapling has only just been planted." }, { "The yew sapling grows a few small branches." }, {
														"The yew tree develops several small canopies." },
										{ "The yew tree trunk texture becomes smoother and", "the canopies grow larger." }, { "The yew tree grows larger." },
										{ "The yew tree canopies become more angular and", "cone shaped due to texture placement." },
										{ "The yew tree gains a rougher tree bark texture and", "the base becomes darker." },
										{ "The yew tree base becomes light again, and the", "trunk loses its texture." }, { "The yew tree bark gains a stripy texture." },
										{ "The yew tree is ready to be harvested." } }), MAGIC(
												5374,
												new String[][] { { "The magic sapling has only just been planted." }, { "The magic sapling grows a little bit." },
														{ "The magic sapling grows a little bit more." }, { "The magic sapling grows a few small branches." },
														{ "The magic tree grows a small canopy." }, { "The magic tree canopy becomes larger, and", "starts producing sparkles." },
														{ "The magic tree grows and the base becomes lighter." }, { "The magic tree grows and the base becomes darker." },
														{ "The magic tree's bark is more prominent and the", "canopy gains more sparkles." }, { "The magic tree grows taller." },
														{ "The magic tree grows taller." }, { "The magic tree is ready to be harvested." } });

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

	}

	public enum TreeData {
		OAK(5370, 6043, 15, new int[] { 5968, 1 }, 160, 0.20, 14, 467.3, 0x08, 0x0c, 0x0d, 0x0e, 1281), WILLOW(5313, 6045, 30, new int[] { 5386, 1 }, 240, 0.20, 25, 1456.3, 0x0f,
				0x15, 0x16, 0x17, 1308), MAPLE(5314, 6047, 45, new int[] { 5396, 1 }, 320, 0.25, 45, 3403.4, 0x18, 0x20, 0x21, 0x22, 1307), YEW(5315, 6049, 60,
						new int[] { 6016, 10 }, 400, 0.25, 81, 7069.9, 0x23, 0x2d, 0x2e, 0x2f,
						1309), MAGIC(5316, 6051, 75, new int[] { 5976, 25 }, 480, 0.25, 145.5, 13768.3, 0x30, 0x3c, 0x3d, 0x3e, 1292);

		public static TreeData forId(int saplingId) {
			return TreeData.saplings.get(saplingId);
		}

		private final int saplingId;
		private final int levelRequired;
		private final int growthTime;
		private final double diseaseChance;
		private final double plantingXp;
		private final double checkHealthXp;
		private final int startingState;
		private final int endingState;
		private final int chopDownState;
		private final int stumpState;

		private static Map<Integer, TreeData> saplings = new HashMap<>();

		static {
			for (TreeData data : TreeData.values()) {
				TreeData.saplings.put(data.saplingId, data);
			}
		}

		TreeData(int saplingId, int rootsId, int levelRequired, int[] paymentToWatch, int growthTime, double diseaseChance, double plantingXp, double checkHealthXp,
				int startingState, int endingState, int chopDownState, int stumpState, int treeObjectAssociated) {
			this.saplingId = saplingId;
			this.levelRequired = levelRequired;
			this.growthTime = growthTime;
			this.diseaseChance = diseaseChance;
			this.plantingXp = plantingXp;
			this.checkHealthXp = checkHealthXp;
			this.startingState = startingState;
			this.endingState = endingState;
			this.chopDownState = chopDownState;
			this.stumpState = stumpState;
		}

		public double getCheckHealthXp() {
			return checkHealthXp;
		}

		public int getChopDownState() {
			return chopDownState;
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

		public int getLevelRequired() {
			return levelRequired;
		}

		public double getPlantingXp() {
			return plantingXp;
		}

		public int getStartingState() {
			return startingState;
		}

		public int getStumpState() {
			return stumpState;
		}

	}

	public enum TreeFieldsData {

		VARROCK(0, new Location[] { new Location(3228, 3458), new Location(3230, 3460) }, 2341), LUMBRIDGE(1, new Location[] { new Location(3192, 3230), new Location(3194, 3232) },
				2342), TAVERLEY(2, new Location[] { new Location(2935, 3437), new Location(2937, 3439) }, 2339), FALADOR(3,
						new Location[] { new Location(3003, 3372), new Location(3005, 3374) }, 2340);

		public static TreeFieldsData forId(int npcId) {
			return TreeFieldsData.npcsProtecting.get(npcId);
		}

		public static TreeFieldsData forIdPosition(Location position) {
			for (TreeFieldsData treeFieldsData : TreeFieldsData.values()) {
				if (FarmingConstants.inRangeArea(treeFieldsData.getTreePosition()[0], treeFieldsData.getTreePosition()[1], position)) {
					return treeFieldsData;
				}
			}

			return null;
		}

		private final int treeIndex;
		private final Location[] treePosition;
		private final int npcId;

		private static final Map<Integer, TreeFieldsData> npcsProtecting = new HashMap<>();

		static {
			for (TreeFieldsData data : TreeFieldsData.values()) {
				TreeFieldsData.npcsProtecting.put(data.npcId, data);
			}
		}

		TreeFieldsData(int treeIndex, Location[] treePosition, int npcId) {
			this.treeIndex = treeIndex;
			this.treePosition = treePosition;
			this.npcId = npcId;
		}

		public int getNpcId() {
			return npcId;
		}

		public int getTreeIndex() {
			return treeIndex;
		}

		public Location[] getTreePosition() {
			return treePosition;
		}

	}

	public static int getPlantState(int plantState) {
		switch (plantState) {
		case 0:
			return WoodTrees.GROWING;
		case 1:
			return WoodTrees.DISEASED;
		case 2:
			return WoodTrees.DEAD;
		}
		return -1;
	}

	public static boolean guide(int objectX, int objectY) {
		TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(new Location(objectX, objectY));
		if (treeFieldsData == null) {
			return false;
		}
		return true;
	}

	private final Player player;
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;
	public int[] treeStages = new int[4];
	public int[] treeSaplings = new int[4];
	public int[] treeHarvest = new int[4];
	public int[] treeState = new int[4];
	public long[] treeTimer = new long[4];
	public double[] diseaseChance = { 1, 1, 1, 1 };
	public boolean[] hasFullyGrown = { false, false, false, false };
	public boolean[] treeWatched = { false, false, false, false };
	public int[] respawnTimer = new int[4];
	public static final int GROWING = 0x00;
	public static final int DISEASED = 0x01;
	public static final int DEAD = 0x02;
	public static final int MAIN_TREE_CONFIG = 502;

	public WoodTrees(Player player) {
		this.player = player;
	}

	public boolean checkHealth(int objectX, int objectY) {
		TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(new Location(objectX, objectY));

		if (treeFieldsData == null) {
			return false;
		}

		TreeData treeData = TreeData.forId(treeSaplings[treeFieldsData.getTreeIndex()]);

		if (treeData == null) {
			return false;
		}

		if (treeState[treeFieldsData.getTreeIndex()] != 0) {
			return false;
		}

		player.playAnimation(new Animation(832));
		player.stopTask();

		player.setSkilling(new Task(2) {

			@Override
			public void execute() {
				player.sendMessage("You examine the tree for signs of disease and find that it is in perfect health");
				player.getSkill().addExperience(Skill.FARMING, treeData.getCheckHealthXp() * Constants.FARMING_EXPERIENCE);
				treeState[treeFieldsData.getTreeIndex()] = 6;
				WoodTrees.this.updateTreeStates();
				stop();
			}

		});
		return true;
	}

	public boolean clearPatch(int objectX, int objectY, int itemId) {
		final TreeFieldsData hopsFieldsData = TreeFieldsData.forIdPosition(new Location(objectX, objectY));
		int finalAnimation;
		int finalDelay;
		if (hopsFieldsData == null || itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE) {
			return false;
		}
		if (treeStages[hopsFieldsData.getTreeIndex()] == 3) {
			return true;
		}
		if (treeStages[hopsFieldsData.getTreeIndex()] <= 3) {
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
		int times = 3 - treeStages[hopsFieldsData.getTreeIndex()];

		player.sendMessage("Final Delay: " + finalDelay + " TIMES: " + times);
		GameService.getTaskScheduler().schedule(new Task(finalDelay, times) {

			@Override
			public void execute() {
				player.playAnimation(new Animation(animation));
				if (treeStages[hopsFieldsData.getTreeIndex()] <= 2) {
					treeStages[hopsFieldsData.getTreeIndex()]++;
					player.getInventory().add(new Item(6055));
				} else {
					treeStages[hopsFieldsData.getTreeIndex()] = 3;
					WoodTrees.this.resetTrees(hopsFieldsData.getTreeIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
				}
				player.getSkill().addExperience(Skill.FARMING, WoodTrees.CLEARING_EXPERIENCE * Constants.FARMING_EXPERIENCE);
				treeTimer[hopsFieldsData.getTreeIndex()] = player.elapsedMinutes;
				WoodTrees.this.updateTreeStates();
				if (treeStages[hopsFieldsData.getTreeIndex()] == 3) {
					WoodTrees.this.resetTrees(hopsFieldsData.getTreeIndex());
					player.sendMessage("You clear the patch.");
					player.setStopPacket(false);
					stop();
					return;
				}
			}
		});
		return true;
	}

	/* calculating the disease chance and making the plant grow */

	public boolean cut(final int x, final int y) {
		TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(new Location(x, y));

		if (treeFieldsData == null) {
			return false;
		}

		TreeData treeData = TreeData.forId(treeSaplings[treeFieldsData.getTreeIndex()]);

		if (treeData == null) {
			return false;
		}

		if (player.getInventory().freeSlots() <= 0) {
			player.sendMessage("Not enough space in your inventory.");
			return true;
		}
		// check with J
		if (Woodcutting.getPlayerAxe(player) < 0) {
			player.sendMessage("You do not have an axe which you have the woodcutting level to use.");
			return true;
		}

		if (treeState[treeFieldsData.getTreeIndex()] != 6) {
			player.sendMessage("Tree is not fully grown");
			return false;
		}

		/**
		 * TODO done so badly.
		 */
		// int object = WoodTrees.idForName(treeData.name().toUpperCase());
		player.sendMessage("You swing your axe at the tree.");
		int emoteId = HatchetData.forAxe(Woodcutting.getPlayerAxe(player)).getAxeAnimation();
		player.playAnimation(new Animation(emoteId));
		player.stopTask();

		player.setSkilling(new Task(5, true) {

			@Override
			public void execute() {
				if (player.getInventory().freeSlots() <= 0) {
					stop();
					return;
				}

				player.playAnimation(new Animation(emoteId));
				stop();
				treeState[treeFieldsData.getTreeIndex()] = 7;
				WoodTrees.this.updateTreeStates();
			}
		});

		// if (treeState[treeFieldsData.getTreeIndex()] == 7)
		// {
		// player.getTrees().respawnStumpTimer(treeFieldsData.getTreeIndex());
		// }

		return true;
	}

	public void doCalculations() {

		for (int i = 0; i < treeSaplings.length; i++) {

			if (treeStages[i] > 0 && treeStages[i] <= 3 && player.elapsedMinutes - treeTimer[i] >= 5) {
				treeStages[i]--;
				treeTimer[i] = player.elapsedMinutes;
				updateTreeStates();
				continue;
			}
			TreeData treeData = TreeData.forId(treeSaplings[i]);

			if (treeData == null) {
				continue;
			}

			if (treeState[i] == 7) {
				treeState[i] = 6;
			}
			long difference = player.elapsedMinutes - treeTimer[i];
			long growth = treeData.getGrowthTime();
			int nbStates = treeData.getEndingState() - treeData.getStartingState();
			int state = (int) (difference * nbStates / growth);

			if (treeTimer[i] == 0 || treeState[i] == 2) {
				continue;
			}
			if (state > nbStates) {
				state = nbStates;
			}
			if (4 + state != treeStages[i]) {
				treeStages[i] = 4 + state;
				if (treeStages[i] <= 4 + state) {
					for (int j = treeStages[i]; j <= 4 + state; j++) {
						doStateCalculation(i);
					}
				}
				updateTreeStates();
			}
		}
	}

	/* calculations about the diseasing chance */

	public void doStateCalculation(int index) {
		if (treeState[index] == 2) {
			return;
		}
		// if the patch is diseased, it dies, if its watched by a farmer, it
		// goes back to normal
		if (treeState[index] == 1) {
			// if (treeWatched[index]) {
			treeState[index] = 0;
			TreeData treeData = TreeData.forId(treeSaplings[index]);
			if (treeData == null) {
				return;
			}
			int difference = treeData.getEndingState() - treeData.getStartingState();
			int growth = treeData.getGrowthTime();
			treeTimer[index] += growth / difference;
			modifyStage(index);
			/* } else { treeState[index] = 2; } */
		}
		if (treeState[index] == 5 && treeStages[index] != 3) {
			treeState[index] = 0;
		}
		if (treeState[index] == 0 && treeStages[index] >= 5 && !hasFullyGrown[index]) {
			TreeData treeData = TreeData.forId(treeSaplings[index]);
			if (treeData == null) {
				return;
			}
			double chance = diseaseChance[index] * treeData.getDiseaseChance();
			int maxChance = (int) chance * 100;
			if (NumberUtil.random(100) <= maxChance) {
				treeState[index] = 1;
			}
		}
	}

	/* clearing the patch with a rake of a spade */

	public int getConfigValue(int treeStage, int saplingId, int plantState, int index) {
		TreeData treeData = TreeData.forId(saplingId);
		switch (treeStage) {
		case 0:// weed
			return (WoodTrees.GROWING << 6) + 0x00;
		case 1:// weed cleared
			return (WoodTrees.GROWING << 6) + 0x01;
		case 2:
			return (WoodTrees.GROWING << 6) + 0x02;
		case 3:
			return (WoodTrees.GROWING << 6) + 0x03;
		}
		if (treeData == null) {
			return -1;
		}
		if (treeData.getEndingState() == treeData.getStartingState() + treeStage - 1) {
			hasFullyGrown[index] = true;
		}
		if (plantState == 6) {
			return treeData.getChopDownState();
		}
		if (plantState == 7) {
			return treeData.getStumpState();
		}
		return (WoodTrees.getPlantState(plantState) << 6) + treeData.getStartingState() + treeStage - 4;
	}

	/* planting the saplings */

	/* Inspect a plant */
	public boolean inspect(int objectX, int objectY) {
		TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(new Location(objectX, objectY));

		if (treeFieldsData == null) {
			return false;
		}

		InspectData inspectData = InspectData.forId(treeSaplings[treeFieldsData.getTreeIndex()]);
		TreeData treeData = TreeData.forId(treeSaplings[treeFieldsData.getTreeIndex()]);

		if (treeState[treeFieldsData.getTreeIndex()] == 1) {
			SimpleDialogues.sendStatement(player, "This tree is diseased. Use secateurs to prune the area, ", "or clear the patch with a spade.");
			return true;
		} else if (treeState[treeFieldsData.getTreeIndex()] == 2) {
			SimpleDialogues.sendStatement(player, "This tree is dead. You did not cure it while it was diseased.", "Clear the patch with a spade.");
			return true;
		} else if (treeState[treeFieldsData.getTreeIndex()] == 6) {
			SimpleDialogues.sendStatement(player, "This is a tree stump, to remove it, use a spade on it", "to recieve some roots and clear the patch.");
			return true;
		}

		if (treeStages[treeFieldsData.getTreeIndex()] == 0) {
			SimpleDialogues.sendStatement(player, "This is a tree patch. The soil has not been treated.", "The patch needs weeding.");
		} else if (treeStages[treeFieldsData.getTreeIndex()] == 3) {
			SimpleDialogues.sendStatement(player, "This is a tree patch. The soil has not been treated.", "The patch is empty and weeded.");
		} else if (inspectData != null && treeData != null) {
			player.sendMessage("You bend down and start to inspect the patch...");
			player.playAnimation(new Animation(1331));
			player.setStopPacket(true);

			GameService.getTaskScheduler().schedule(new Task(5) {

				@Override
				public void execute() {
					if (treeStages[treeFieldsData.getTreeIndex()] - 4 < inspectData.getMessages().length - 2) {
						SimpleDialogues.sendStatement(player, inspectData.getMessages()[treeStages[treeFieldsData.getTreeIndex()] - 4]);
					} else if (treeStages[treeFieldsData.getTreeIndex()] < treeData.getEndingState() - treeData.getStartingState() + 2) {
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
		for (int i = 0; i < treeStages.length; i++) {
			treeStages[i] = buffer.getInt();
		}
		for (int i = 0; i < treeSaplings.length; i++) {
			treeSaplings[i] = buffer.getInt();
		}
		for (int i = 0; i < treeHarvest.length; i++) {
			treeHarvest[i] = buffer.getInt();
		}
		for (int i = 0; i < treeState.length; i++) {
			treeState[i] = buffer.getInt();
		}
		for (int i = 0; i < treeTimer.length; i++) {
			treeTimer[i] = buffer.getLong();
		}
		for (int i = 0; i < diseaseChance.length; i++) {
			diseaseChance[i] = buffer.getDouble();
		}
		for (int i = 0; i < hasFullyGrown.length; i++) {
			hasFullyGrown[i] = ByteBufferUtil.getBoolean(buffer);
		}
		for (int i = 0; i < treeWatched.length; i++) {
			treeWatched[i] = ByteBufferUtil.getBoolean(buffer);
		}
	}

	public void modifyStage(int i) {
		TreeData bushesData = TreeData.forId(treeSaplings[i]);
		if (bushesData == null) {
			return;
		}
		long difference = player.elapsedMinutes - treeTimer[i];
		long growth = bushesData.getGrowthTime();
		int nbStates = bushesData.getEndingState() - bushesData.getStartingState();
		int state = (int) (difference * nbStates / growth);
		treeStages[i] = 4 + state;
		updateTreeStates();
	}

	public boolean plantSapling(int objectX, int objectY, final int saplingId) {
		final TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(new Location(objectX, objectY));
		final TreeData treeData = TreeData.forId(saplingId);
		if (treeFieldsData == null || treeData == null) {
			return false;
		}
		if (treeStages[treeFieldsData.getTreeIndex()] != 3) {
			player.sendMessage("You can't plant a sapling here.");
			return true;
		}
		if (treeData.getLevelRequired() > player.getSkill().getLevel(Skill.FARMING)) {
			SimpleDialogues.sendStatement(player, "You need a farming level of " + treeData.getLevelRequired() + " to plant this sapling.");
			return true;
		}
		if (!player.getInventory().contains(FarmingConstants.TROWEL)) {
			SimpleDialogues.sendStatement(player, "You need a trowel to plant the sapling here.");
			return true;
		}
		player.playAnimation(new Animation(FarmingConstants.PLANTING_POT_ANIM));
		treeStages[treeFieldsData.getTreeIndex()] = 4;
		player.getInventory().remove(new Item(saplingId));
		player.setStopPacket(true);

		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				treeState[treeFieldsData.getTreeIndex()] = 0;
				treeSaplings[treeFieldsData.getTreeIndex()] = saplingId;
				treeTimer[treeFieldsData.getTreeIndex()] = player.elapsedMinutes;
				player.getSkill().addExperience(Skill.FARMING, treeData.getPlantingXp() * Constants.FARMING_EXPERIENCE);
				WoodTrees.this.updateTreeStates();
				player.setStopPacket(false);
				stop();
			}

		});

		return true;
	}

	public boolean pruneArea(int objectX, int objectY, int itemId) {
		TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(new Location(objectX, objectY));

		if (treeFieldsData == null || itemId != FarmingConstants.SECATEURS && itemId != FarmingConstants.MAGIC_SECATEURS) {
			return false;
		}

		TreeData treeData = TreeData.forId(treeSaplings[treeFieldsData.getTreeIndex()]);

		if (treeData == null) {
			return false;
		}

		if (treeState[treeFieldsData.getTreeIndex()] != 1) {
			player.sendMessage("This area doesn't need to be pruned.");
			return true;
		}

		player.playAnimation(new Animation(FarmingConstants.PRUNING_ANIM));
		player.setStopPacket(true);
		treeState[treeFieldsData.getTreeIndex()] = 0;

		GameService.getTaskScheduler().schedule(new Task(15) {

			@Override
			public void execute() {
				player.sendMessage("You prune the area with your secateurs.");
				WoodTrees.this.updateTreeStates();
				player.setStopPacket(false);
				stop();
			}

		});

		return true;
	}

	/* opening the corresponding guide about the patch */

	/* Put compost on farming patch. */
	public boolean putCompost(int objectX, int objectY, int itemId) {
		if (itemId != 6032 && itemId != 6034) {
			return false;
		}

		TreeFieldsData treeFieldsData = TreeFieldsData.forIdPosition(new Location(objectX, objectY));

		if (treeFieldsData == null) {
			return false;
		}

		if (treeStages[treeFieldsData.getTreeIndex()] != 3 || treeState[treeFieldsData.getTreeIndex()] == 5) {
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
				diseaseChance[treeFieldsData.getTreeIndex()] *= itemId == 6032 ? WoodTrees.COMPOST_CHANCE : WoodTrees.SUPERCOMPOST_CHANCE;
				treeState[treeFieldsData.getTreeIndex()] = 5;
				player.setStopPacket(false);
				stop();
			}

		});

		return true;
	}

	/* Curing the plant */

	public void resetAll() {
		this.resetTrees();
	}

	public void resetTrees() {
		treeStages = new int[4];
		treeSaplings = new int[4];
		treeHarvest = new int[4];
		treeState = new int[4];
		treeTimer = new long[4];
		hasFullyGrown = new boolean[] { false, false, false, false };
		treeWatched = new boolean[] { false, false, false, false };
	}

	public void resetTrees(int index) {
		treeSaplings[index] = 0;
		treeState[index] = 0;
		diseaseChance[index] = 1;
		treeHarvest[index] = 0;
		hasFullyGrown[index] = false;
		treeWatched[index] = false;
	}

	// private static int idForName(String name)
	// {
	// if (name.equals("MAGIC"))
	// {
	// return 8408;
	// }
	// else if (name.equals("WILLOW"))
	// {
	// return 8488;
	// }
	// else if (name.equals("MAPLE"))
	// {
	// return 8444;
	// }
	// else if (name.equals("YEW"))
	// {
	// return 8513;
	// }
	//
	// return -1;
	// }

	public void saveStages(ByteBuffer buffer) {
		for (Integer i : treeStages) {
			buffer.putInt(i);
		}
		for (Integer i : treeSaplings) {
			buffer.putInt(i);
		}
		for (Integer i : treeHarvest) {
			buffer.putInt(i);
		}
		for (Integer i : treeState) {
			buffer.putInt(i);
		}
		for (Long i : treeTimer) {
			buffer.putLong(i);
		}
		for (Double i : diseaseChance) {
			buffer.putDouble(i);
		}
		for (Boolean i : hasFullyGrown) {
			ByteBufferUtil.putBoolean(buffer, i);
		}
		for (Boolean i : treeWatched) {
			ByteBufferUtil.putBoolean(buffer, i);
		}
	}

	public void updateTreeStates() {
		// varrock - lumbridge - taverley - falador
		int[] configValues = new int[treeStages.length];
		int configValue;
		for (int i = 0; i < treeStages.length; i++) {
			configValues[i] = getConfigValue(treeStages[i], treeSaplings[i], treeState[i], i);
		}
		configValue = (configValues[0] << 16) + (configValues[1] << 8 << 16) + configValues[2] + (configValues[3] << 8);
		player.getSession().send(new SendConfigPacket(WoodTrees.MAIN_TREE_CONFIG, configValue));
	}

}
