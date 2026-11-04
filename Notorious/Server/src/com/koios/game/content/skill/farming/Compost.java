package com.koios.game.content.skill.farming;

/**
 * Created by J on 25/06/2014.
 */

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendObjectPacket;
import com.koios.task.Task;

public class Compost {

	public enum CompostBinLocations {

		NORTH_ARDOUGNE(0, new Location(2661, 3375, 0), Compost.FIRST_TYPE_COMPOST_BIN, 3), PHASMATYS(1, new Location(3610, 3522, 0), Compost.SECOND_TYPE_COMPOST_BIN, 1), FALADOR(2,
				new Location(3056, 3312, 0), Compost.FIRST_TYPE_COMPOST_BIN, 4), CATHERBY(3, new Location(2804, 3464, 0), Compost.FIRST_TYPE_COMPOST_BIN, 3);

		public static CompostBinLocations forId(int index) {
			return CompostBinLocations.bins.get(index);
		}

		public static CompostBinLocations forPosition(Location position) {
			for (CompostBinLocations compostBinLocations : CompostBinLocations.values()) {
				if (compostBinLocations.binPosition.equals(position)) {
					return compostBinLocations;
				}
			}

			return null;
		}

		private int compostIndex;
		private Location binPosition;
		private int binObjectId;
		private int objectFace;

		private static Map<Integer, CompostBinLocations> bins = new HashMap<>();

		static {
			for (CompostBinLocations data : CompostBinLocations.values()) {
				CompostBinLocations.bins.put(data.compostIndex, data);
			}
		}

		CompostBinLocations(int compostIndex, Location binPosition, int binObjectId, int objectFace) {
			this.compostIndex = compostIndex;
			this.binPosition = binPosition;
			this.binObjectId = binObjectId;
			this.objectFace = objectFace;
		}

		public int getBinObjectId() {
			return binObjectId;
		}

		public Location getBinPosition() {
			return binPosition;
		}

		public int getCompostIndex() {
			return compostIndex;
		}

		public int getObjectFace() {
			return objectFace;
		}

	}

	public enum CompostBinStages {
		FIRST_TYPE(7808, 7813, 7809, 7810, 7811, 7812, 7814, 7815, 7816, 7817, 7828, 7829, 7830, 7831), SECOND_TYPE(7818, 7823, 7819, 7820, 7821, 7822, 7824, 7825, 7826, 7827,
				7832, 7833, 7834, 7835);

		public static CompostBinStages forId(int binId) {
			return CompostBinStages.bins.get(binId);
		}

		private int binEmpty;
		private int closedBin;
		private int binWithCompostable;
		private int binFullOfCompostable;
		private int binWithSuperCompostable;
		private int binFullOFSuperCompostable;
		private int binWithCompost;
		private int binFullOfCompost;
		private int binWithSuperCompost;
		private int binFullOfSuperCompost;
		private int binWithTomatoes;
		private int binFullOfTomatoes;
		private int binWithRottenTomatoes;
		private int binFullOfRottenTomatoes;

		private static Map<Integer, CompostBinStages> bins = new HashMap<>();

		static {
			for (CompostBinStages data : CompostBinStages.values()) {
				CompostBinStages.bins.put(data.binEmpty, data);
			}
		}

		CompostBinStages(int binEmpty, int closedBin, int binWithCompostable, int binFullOfCompostable, int binWithSuperCompostable, int binFullOFSuperCompostable,
				int binWithCompost, int binFullOfCompost, int binWithSuperCompost, int binFullOfSuperCompost, int binWithTomatoes, int binFullOfTomatoes, int binWithRottenTomatoes,
				int binFullOfRottenTomatoes) {
			this.binEmpty = binEmpty;
			this.closedBin = closedBin;
			this.binWithCompostable = binWithCompostable;
			this.binFullOfCompostable = binFullOfCompostable;
			this.binWithSuperCompostable = binWithSuperCompostable;
			this.binFullOFSuperCompostable = binFullOFSuperCompostable;
			this.binWithCompost = binWithCompost;
			this.binFullOfCompost = binFullOfCompost;
			this.binWithSuperCompost = binWithSuperCompost;
			this.binFullOfSuperCompost = binFullOfSuperCompost;
			this.binWithTomatoes = binWithTomatoes;
			this.binFullOfTomatoes = binFullOfTomatoes;
			this.binWithRottenTomatoes = binWithRottenTomatoes;
			this.binFullOfRottenTomatoes = binFullOfRottenTomatoes;
		}

		public int getBinEmpty() {
			return binEmpty;
		}

		public int getBinFullOfCompost() {
			return binFullOfCompost;
		}

		public int getBinFullOfCompostable() {
			return binFullOfCompostable;
		}

		public int getBinFullOfRottenTomatoes() {
			return binFullOfRottenTomatoes;
		}

		public int getBinFullOfSuperCompost() {
			return binFullOfSuperCompost;
		}

		public int getBinFullOFSuperCompostable() {
			return binFullOFSuperCompostable;
		}

		public int getBinFullOfTomatoes() {
			return binFullOfTomatoes;
		}

		public int getBinWithCompost() {
			return binWithCompost;
		}

		public int getBinWithCompostable() {
			return binWithCompostable;
		}

		public int getBinWithRottenTomatoes() {
			return binWithRottenTomatoes;
		}

		public int getBinWithSuperCompost() {
			return binWithSuperCompost;
		}

		public int getBinWithSuperCompostable() {
			return binWithSuperCompostable;
		}

		public int getBinWithTomatoes() {
			return binWithTomatoes;
		}

		public int getClosedBin() {
			return closedBin;
		}
	}

	private final Player player;
	public int[] compostBins = new int[4];
	public long[] compostBinsTimer = new long[4];
	public int[] organicItemAdded = new int[4];

	public int tempCompostState;

	public static final double COMPOST_EXP_RETRIEVE = 4.5;

	public static final double SUPER_COMPOST_EXP_RETRIEVE = 8.5;

	/* setting up the experiences constants */

	public static final double COMPOST_EXP_USE = 18;

	public static final double SUPER_COMPOST_EXP_USE = 26;
	public static final double ROTTEN_TOMATOES_EXP_RETRIEVE = 8.5;
	/* these are the constants related to compost making */
	public static final int COMPOST = 6032;
	public static final int SUPER_COMPOST = 6034;
	public static final int ROTTE_TOMATO = 2518;
	public static final int TOMATO = 1982;
	public static final int FIRST_TYPE_COMPOST_BIN = 7808;
	public static final int SECOND_TYPE_COMPOST_BIN = 7818;
	public static final int[] COMPOST_ORGANIC = { 6055, 1942, 1957, 1965, 5986, 5504, 5982, 249, 251, 253, 255, 257, 2998, 259, 261, 263, 3000, 265, 2481, 267, 269, 1951, 753,
			2126, 247, 239, 6018 };
	public static final int[] SUPER_COMPOST_ORGANIC = { 2114, 5978, 5980, 5982, 6004, 247, 6469 };

	public Compost(Player player) {
		this.player = player;
	}

	/* handle what happens when the player close the compost bin */
	public void closeCompostBin(final int index) {
		compostBins[index] = tempCompostState * 100;
		compostBinsTimer[index] = player.elapsedMinutes;
		player.playAnimation(new Animation(835));

		GameService.getTaskScheduler().schedule(new Task(2, false) {

			@Override
			public void execute() {
				player.sendMessage("You close the compost bin, and its content start to rot.");
				Compost.this.updateCompostBin(index);
				stop();
			}

		});
	}

	/* handle compost bin filling */
	public void fillCompostBin(Location binPosition, final int organicItemUsed) {
		CompostBinLocations compostBinLocations = CompostBinLocations.forPosition(binPosition);

		if (compostBinLocations == null) {
			return;
		}

		final int index = compostBinLocations.getCompostIndex();
		int incrementFactor = 0;

		// setting up the different increments.
		for (int normalCompost : Compost.COMPOST_ORGANIC) {
			if (organicItemUsed == normalCompost) {
				incrementFactor = 2;
			}
		}

		for (int superCompost : Compost.SUPER_COMPOST_ORGANIC) {
			if (organicItemUsed == superCompost) {
				incrementFactor = 17;
			}
		}

		if (organicItemUsed == Compost.TOMATO) {
			if (compostBins[index] % 77 == 0) {
				incrementFactor = 77;
			} else {
				incrementFactor = 2;
			}
		}

		// checking if the item used was an organic item.
		if (incrementFactor == 0) {
			player.sendMessage("You need to put organic items into the compost bin in order to make compost.");
			return;
		}

		final int factor = incrementFactor;
		// launching the main event for filling the compost bin.
		player.playAnimation(new Animation(832));

		if (player.getSkilling() != null) {
			player.getSkilling().stop();
		}

		player.setSkilling(new Task(2) {

			@Override
			public void execute() {
				organicItemAdded[index]++;
				player.getInventory().remove(new Item(organicItemUsed));
				compostBins[index] += factor;
				Compost.this.updateCompostBin(index);
				stop();
			}

		});
	}

	/* this is the enum that stores the different locations of the compost bins */

	public boolean handleItemOnObject(int itemUsed, int objectId, int objectX, int objectY) {
		switch (objectId) {
		case 7814:
		case 7815:
		case 7816:
		case 7817:
		case 7824:
		case 7825:
		case 7826:
		case 7827:
			if (itemUsed == 1925) {
				retrieveCompost(CompostBinLocations.forPosition(new Location(objectX, objectY)).getCompostIndex());
			} else {
				player.sendMessage("You might need some buckets to gather the compost.");
			}
			return true;

		case 7839:
		case 7838:
		case 7837:
		case 7836:
		case 7808:
		case 7809:
		case 7811:
		case 7819:
		case 7821:
		case 7828:
		case 7832:
			fillCompostBin(new Location(objectX, objectY), itemUsed);
			return true;
		}

		return false;
	}

	/* this is the enum that stores the different compost bins stages */

	public boolean handleObjectClick(int objectId, int objectX, int objectY) {
		switch (objectId) {
		case 7810:
		case 7812:
		case 7820:
		case 7822:
		case 7829:
		case 7833:
			closeCompostBin(CompostBinLocations.forPosition(new Location(objectX, objectY)).getCompostIndex());
			return true;

		case 7813:
		case 7823:
			openCompostBin(CompostBinLocations.forPosition(new Location(objectX, objectY)).getCompostIndex());
			return true;

		case 7830:
		case 7831:
		case 7834:
		case 7835:
			retrieveCompost(CompostBinLocations.forPosition(new Location(objectX, objectY)).getCompostIndex());
			return true;
		}

		return false;
	}

	public void loadStages(ByteBuffer buffer) {
		for (int i = 0; i < compostBins.length; i++) {
			compostBins[i] = buffer.getInt();
		}

		for (int i = 0; i < compostBinsTimer.length; i++) {
			compostBinsTimer[i] = buffer.getLong();
		}

		for (int i = 0; i < organicItemAdded.length; i++) {
			organicItemAdded[i] = buffer.getInt();
		}
	}

	public void onLogin() {
		for (int i = 0; i < compostBins.length; i++) {
			updateCompostBin(i);
		}
	}

	/* handle what happens when the player opens the compost bin */
	public void openCompostBin(final int index) {
		// check if the time elapsed is enough to rot the compost
		int timerRequired;
		timerRequired = compostBins[index] == 200 ? 90 : 45;

		if (player.elapsedMinutes - compostBinsTimer[index] >= timerRequired) {
			compostBins[index] += 50;
			player.playAnimation(new Animation(834));

			GameService.getTaskScheduler().schedule(new Task(2, false) {

				@Override
				public void execute() {
					Compost.this.updateCompostBin(index);
					stop();
				}

			});
		} else {
			player.sendMessage("The compost bin is still rotting. I should wait until it is complete.");
		}
	}

	public void resetAll() {
		for (int i = 0; i < compostBins.length; i++) {
			resetVariables(i);
		}
	}

	public void resetVariables(int index) {
		compostBins[index] = 0;
		compostBinsTimer[index] = 0;
		organicItemAdded[index] = 0;
	}

	/* handling the item on object method */

	// handle what happens when the player retrieve the compost
	public void retrieveCompost(final int index) {
		final int finalItem = compostBins[index] == 150 ? Compost.COMPOST : compostBins[index] == 250 ? Compost.SUPER_COMPOST : Compost.ROTTE_TOMATO;
		player.playAnimation(new Animation(832));
		player.stopTask();

		player.setSkilling(new Task(2) {

			@Override
			public void execute() {
				player.getSkill().addExperience(Skill.FARMING, finalItem == Compost.COMPOST ? Compost.COMPOST_EXP_RETRIEVE
						: finalItem == Compost.SUPER_COMPOST ? Compost.SUPER_COMPOST_EXP_RETRIEVE : Compost.ROTTEN_TOMATOES_EXP_RETRIEVE * Constants.FARMING_EXPERIENCE);
				if (index == -1) {
					stop();
					return;
				}

				if (compostBins[index] != 350) {
					player.getInventory().remove(new Item(1925));
				}

				player.getInventory().add(new Item(finalItem));
				organicItemAdded[index]--;

				if (organicItemAdded[index] == 0) {
					Compost.this.resetVariables(index);
				}

				Compost.this.updateCompostBin(index);
				stop();
			}

		});
	}

	/* handling the object click method */

	public void saveStages(ByteBuffer buffer) {
		for (Integer i : compostBins) {
			buffer.putInt(i);
		}

		for (Long i : compostBinsTimer) {
			buffer.putLong(i);
		}

		for (Integer i : organicItemAdded) {
			buffer.putInt(i);
		}
	}

	/* reseting the compost variables */

	/* handle compost bin updating */
	private void updateCompostBin(int index) {
		CompostBinStages compostBinStages = CompostBinStages.forId(CompostBinLocations.forId(index).getBinObjectId());

		if (compostBinStages == null) {
			return;
		}

		int x = CompostBinLocations.forId(index).getBinPosition().getX();
		int y = CompostBinLocations.forId(index).getBinPosition().getY();
		int finalObject;

		// handling the different ways to fill a compost bin
		if (compostBins[index] > 0) {
			if (compostBins[index] % 17 == 0) {
				finalObject = compostBinStages.getBinWithSuperCompostable();
			} else if (compostBins[index] % 77 == 0) {
				finalObject = compostBinStages.getBinWithTomatoes();
			} else {
				finalObject = compostBinStages.getBinWithCompostable();
			}
		} else {
			finalObject = compostBinStages.getBinEmpty();
		}

		// handling the different ways to complete a compost bin
		if (compostBins[index] == 255) {
			finalObject = compostBinStages.getBinFullOFSuperCompostable();
			tempCompostState = 2;
		} else if (compostBins[index] == 1155) {
			finalObject = compostBinStages.getBinFullOfTomatoes();
			tempCompostState = 3;
		} else if (organicItemAdded[index] == 15) {
			finalObject = compostBinStages.getBinFullOfCompostable();
			tempCompostState = 1;
		}

		// handling the closed state of the compost bin
		switch (compostBins[index]) {
		case 100:
		case 200:
		case 300:
			finalObject = compostBinStages.getClosedBin();
			break;

		// handling the rotted state of the compost in the bin
		case 150:
			finalObject = compostBinStages.getBinFullOfCompost();
			break;

		case 250:
			finalObject = compostBinStages.getBinFullOfSuperCompost();
			break;

		case 350:
			finalObject = compostBinStages.getBinFullOfRottenTomatoes();
			break;
		}

		// handle the compost bin state when the player retrieve the compost
		if (compostBins[index] == 150 && organicItemAdded[index] < 15) {
			finalObject = compostBinStages.getBinWithCompost();
		} else if (compostBins[index] == 250 && organicItemAdded[index] < 15) {
			finalObject = compostBinStages.getBinWithSuperCompost();
		}

		if (compostBins[index] == 350 && organicItemAdded[index] < 15) {
			finalObject = compostBinStages.getBinWithRottenTomatoes();
		}

		Location compostPos = Location.create(x, y, 0);
		Location playerPos = Location.create(player.getX(), player.getY(), 0);

		if (playerPos.getDistance(compostPos) < 30) {
			player.getSession().send(new SendObjectPacket(finalObject, x, y, player.getZ(), CompostBinLocations.forId(index).getObjectFace(), 10));
		}
	}

}
