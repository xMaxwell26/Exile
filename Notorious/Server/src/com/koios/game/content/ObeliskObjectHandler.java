package com.koios.game.content;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.koios.GameService;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.TeleportExecutor;
import com.koios.game.content.teleport.TeleportType;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.object.GameObject;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

public final class ObeliskObjectHandler {

	private enum Obelisk {
		PORT_44(14826, 2978, 3864), PORT_27(14827, 3033, 3730), PORT_35(14828, 3104, 3792), PORT_13(14829, 3154, 3618), PORT_19(14830, 3225, 3665), PORT_50(14831, 3305, 3914);

		private final int[] coordinates;

		private final int id;

		private boolean activated = false;

		private Obelisk(int id, int... coordinates) {
			this.id = id;
			this.coordinates = coordinates;
		}

		public int getId() {
			return id;
		}

		public boolean isActivated() {
			return activated;
		}

		public int[] getCoordinates() {
			return coordinates;
		}

		public void setActivated(boolean activated) {
			this.activated = activated;
		}

		private static final Set<Obelisk> ALL_OBELISKS = ImmutableSet.copyOf(EnumSet.allOf(Obelisk.class));

		protected static Obelisk forId(int id) {
			return ALL_OBELISKS.stream().filter(o -> o.getId() == id).findAny().orElseThrow(() -> new IllegalArgumentException());
		}

		protected static Obelisk getRandom(Obelisk currentPort) {
			Set<Obelisk> obelisks = ALL_OBELISKS.stream().filter(obelisk -> obelisk != currentPort).collect(Collectors.toSet());
			return Iterables.get(obelisks, NumberUtil.random(obelisks.size()));
		}

	}

	public static void startObelisk(int id, int height) {
		Obelisk obelisk = Obelisk.forId(id);

		if (obelisk.isActivated()) {
			return;
		}

		obelisk.setActivated(true);

		for (int pillar = 0; pillar < 4; pillar++) {
			int x = pillar == 1 || pillar == 3 ? obelisk.getCoordinates()[0] + 4 : obelisk.getCoordinates()[0];
			int y = pillar == 2 || pillar == 3 ? obelisk.getCoordinates()[1] + 4 : obelisk.getCoordinates()[1];

			GameService.getObjectManager().addObject(new GameObject(14825, x, y, height, -1, 10, obelisk.getId(), 16, false));
		}

		GameService.getTaskScheduler().schedule(new Task(16) {

			@Override
			protected void execute() {
				teleportObelisk(obelisk);
				stop();
			}

			@Override
			protected void onStop() {
				obelisk.setActivated(false);
			}

		});
	}

	private static void teleportObelisk(Obelisk currentPort) {
		Obelisk randomPort = Obelisk.getRandom(currentPort);

		for (Player player : GameService.getWorld().getPlayers()) {
			if (player == null) {
				continue;
			}

			int xOffset = player.getLocation().getX() - currentPort.getCoordinates()[0];
			int yOffset = player.getLocation().getY() - currentPort.getCoordinates()[1];
			if (player.goodDistance(player.getLocation().getX(), player.getLocation().getY(), currentPort.getCoordinates()[0] + 2, currentPort.getCoordinates()[1] + 2, 1)) {
				TeleportExecutor.startTeleportation(player,
						new Teleport(new Location(randomPort.getCoordinates()[0] + xOffset, randomPort.getCoordinates()[1] + yOffset, player.getLocation().getZ()),
								TeleportModes.MODERN_SPELL_2),
						TeleportType.NO_WILD_CHECK);
			}
		}
	}

}