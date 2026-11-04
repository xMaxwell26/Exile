package com.koios.game.model.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.koios.game.map.MapLoading;

public class CustomObjects {

	private CustomObjects() {
	}

	public static final int BANK_DECORATIVE_ROPE = 4655;
	public static final int BDR_TYPE = 0;

	public static final int BANK_GROUND_D_EDGE = 27018;
	public static final int BANK_GROUND_D_CONNECTOR = 27019;
	public static final int BANK_GROUND_D_TILE = 27020;

	public static final int BLANK_OBJECT_ID = 2376;

	private static final void spawn(int id, int x, int y, int z, int type, int face) {
		MapLoading.addObject(false, id, x, y, z, type, face);
	}

	private static final void delete(int x, int y, int z) {
		MapLoading.removeObject(x, y, z);
	}

	private static final void delete(int id, int x, int y, int z, int type, int rotation) {
		MapLoading.removeObject(x, y, z);
	}

	private static List<SceneryObject> customs = new ArrayList<SceneryObject>();

	public static void load() {
		// ::trade bank booths
		for (int x = 3160; x < 3170; x++) {
			customs.add(new SceneryObject(2213, Location.create(x, 3479, 2), Direction.SOUTH)); // Bank
			// booth
		}

		// ::trade altars
		customs.add(new SceneryObject(409, Location.create(3154, 3485, 2), Direction.EAST)); // Chaos
		// altar
		customs.add(new SceneryObject(61, Location.create(3154, 3488, 2), Direction.EAST)); // Prayer
		// altar
		customs.add(new SceneryObject(6552, Location.create(3154, 3491, 2), Direction.EAST)); // Ancient
		customs.add(new SceneryObject(172, Location.create(3175, 3492, 2), Direction.WEST)); // Crystal
		// chest

		// Edgeville home
		customs.add(new SceneryObject(409, Location.create(3095, 3506), Direction.NORTH)); // Chaos
		// altar
		customs.add(new SceneryObject(61, Location.create(3092, 3506), Direction.NORTH)); // Prayer
		// altar
		customs.add(new SceneryObject(6552, Location.create(3098, 3506), Direction.SOUTH)); // Ancient
		// altar
		customs.add(new SceneryObject(3735, Location.create(3074, 3502), Direction.WEST)); // Revenant
		// cave
		// entrance
		customs.add(new SceneryObject(4875, Location.create(3095, 3500), Direction.NORTH)); // Food
		// stall
		customs.add(new SceneryObject(4876, Location.create(3096, 3500), Direction.NORTH)); // General
		// stall
		customs.add(new SceneryObject(4874, Location.create(3097, 3500), Direction.NORTH)); // Crafting
		// stall
		customs.add(new SceneryObject(4877, Location.create(3098, 3500), Direction.NORTH)); // Magic
		// stall
		customs.add(new SceneryObject(3515, Location.create(3093, 3488), Direction.SOUTH)); // Mysterious
		// cape
		// rack
		customs.add(new SceneryObject(3192, Location.create(3084, 3515), Direction.NORTH)); // Scoreboard
		for (int x = 3087; x < 3090; x++) {
			for (int y = 3508; y < 3514; y++) {
				delete(x, y, 0);
			}
		}
		customs.add(new SceneryObject(10781, Location.create(3088, 3509), Direction.NORTH)); // Donators
		// Teleport
		customs.add(new SceneryObject(29954, Location.create(3090, 3499), Direction.EAST)); // Small
		// obelisk

		// " + Constants.SERVER_NAME + " Donators Playground
		customs.add(new SceneryObject(2213, Location.create(3365, 9642), Direction.EAST)); // Bank
		// booth
		customs.add(new SceneryObject(2213, Location.create(3365, 9638), Direction.EAST)); // Bank
		// booth
		customs.add(new SceneryObject(2213, Location.create(3361, 9642), Direction.EAST)); // Bank
		// booth
		customs.add(new SceneryObject(2213, Location.create(3361, 9638), Direction.EAST)); // Bank
		// booth
		customs.add(new SceneryObject(4008, Location.create(3361, 9635), Direction.SOUTH)); // Restore
		// altar
		customs.add(new SceneryObject(2562, Location.create(3350, 9636), Direction.EAST)); // Gem
		// stall
		customs.add(new SceneryObject(1306, Location.create(3354, 9634), Direction.NORTH)); // Magic
		// tree
		customs.add(new SceneryObject(1306, Location.create(3351, 9631), Direction.NORTH)); // Magic
		// tree
		customs.add(new SceneryObject(1306, Location.create(3354, 9628), Direction.NORTH)); // Magic
		// tree
		customs.add(new SceneryObject(1306, Location.create(3358, 9630), Direction.NORTH)); // Magic
		// tree
		customs.add(new SceneryObject(2107, Location.create(3371, 9647), Direction.NORTH)); // Runite
		// ore
		customs.add(new SceneryObject(2107, Location.create(3374, 9650), Direction.NORTH)); // Runite
		// ore
		customs.add(new SceneryObject(2107, Location.create(3371, 9652), Direction.NORTH)); // Runite
		// ore
		customs.add(new SceneryObject(2107, Location.create(3374, 9646), Direction.NORTH)); // Runite
		// ore
		customs.add(new SceneryObject(2107, Location.create(3376, 9649), Direction.NORTH)); // Runite
		// ore
		customs.add(new SceneryObject(2728, Location.create(3363, 9646), Direction.NORTH)); // Range

		// Dice Zone
		customs.add(new SceneryObject(2213, Location.create(2841, 10209), Direction.WEST)); // Bank
		// booth
		customs.add(new SceneryObject(2213, Location.create(2841, 10210), Direction.WEST)); // Bank
		// booth

		// Skilling zone
		customs.add(new SceneryObject(2213, Location.create(2088, 4428), Direction.EAST)); // Bank
		// booth
		customs.add(new SceneryObject(2213, Location.create(2088, 4429), Direction.EAST)); // Bank
		// booth

		// Falador Mine
		customs.add(new SceneryObject(2213, Location.create(3045, 9777), Direction.SOUTH)); // Bank
		// booth
		customs.add(new SceneryObject(3044, Location.create(3046, 9782), Direction.SOUTH)); // Furnace
		customs.add(new SceneryObject(2783, Location.create(3043, 9785), Direction.NORTH)); // Anvil

		customs.add(new SceneryObject(2273, Location.create(3004, 3933), Direction.NORTH)); // Portal

		for (SceneryObject sceneryObject : customs) {
			spawn(sceneryObject.getId(), sceneryObject.getPosition().getX(), sceneryObject.getPosition().getY(), sceneryObject.getPosition().getZ(), 10,
					sceneryObject.getDirection());
		}

		delete(1276, 3096, 3501, 0, 10, Direction.NORTH.getDirection());

		delete(2213, 2816, 3438, 0, 10, Direction.NORTH.getDirection());

		delete(2213, 3445, 3554, 2, 10, Direction.NORTH.getDirection());

		delete(2213, 3428, 3535, 0, 10, Direction.NORTH.getDirection());
		delete(2213, 3429, 3535, 0, 10, Direction.NORTH.getDirection());

		delete(2213, 3426, 3555, 1, 10, Direction.NORTH.getDirection());
		delete(2213, 3427, 3555, 1, 10, Direction.NORTH.getDirection());

		delete(2213, 2551, 3555, 0, 10, Direction.NORTH.getDirection());
		delete(2213, 2551, 3554, 0, 10, Direction.NORTH.getDirection());

		delete(2213, 3005, 3953, 0, 10, Direction.NORTH.getDirection());
		delete(2213, 3005, 3952, 0, 10, Direction.NORTH.getDirection());

		delete(3096, 3501, 0);

		/* Delete table in edge bank */
		for (int x = 3090; x < 3093; x++) {
			for (int y = 3494; y < 3497; y++) {
				delete(x, y, 0);
			}
		}

		/* Delete other table and chair in edge bank */
		delete(3095, 3498, 0);
		delete(3095, 3499, 0);

		/* Delete 2 bank stalls in edge bank */
		delete(3092, 3488, 0);
		delete(3093, 3488, 0);
	}

	private static class Location {

		private final int x;
		private final int y;
		private final int z;

		public Location(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public static Location create(int x, int y, int z) {
			return new Location(x, y, z);
		}

		public static Location create(int x, int y) {
			return new Location(x, y, 0);
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}
	}

	/**
	 * @author J
	 */
	public enum Direction {

		NORTH(1, 0, 1), NORTH_EAST(2, 1, 1), EAST(4, 1, 0), SOUTH_EAST(7, 1, -1), SOUTH(6, 0, -1), SOUTH_WEST(5, -1, -1), WEST(3, -1, 0), NORTH_WEST(0, -1, 1), NONE(-1, 0, 0);

		public static Direction direction(int diffX, int diffY) {
			if (diffX < 0) {
				if (diffY < 0) {
					return Direction.SOUTH_WEST;
				}

				if (diffY > 0) {
					return Direction.NORTH_WEST;
				}

				return Direction.WEST;
			}

			if (diffX > 0) {
				if (diffY < 0) {
					return Direction.SOUTH_EAST;
				}

				if (diffY > 0) {
					return Direction.NORTH_EAST;
				}

				return Direction.EAST;
			}

			if (diffY < 0) {
				return Direction.SOUTH;
			}

			if (diffY > 0) {
				return Direction.NORTH;
			}

			return Direction.NONE;
		}

		private final String name;
		private final int direction;
		private final int x;
		private final int y;

		private Direction(int direction, int x, int y) {
			name = name().toLowerCase().replace("_", "-");
			this.direction = direction;
			this.x = x;
			this.y = y;
		}

		public int getDirection() {
			return direction;
		}

		public String getName() {
			return name;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

	}

	public static class SceneryObject {

		private final int id;
		private final Location position;
		private final int direction;

		public SceneryObject(int id, Location position, Direction direction) {
			this.id = id;
			this.position = Objects.requireNonNull(position);

			switch (direction) {
			case NORTH:
				this.direction = 0;
				break;

			case SOUTH:
				this.direction = 2;
				break;

			case WEST:
				this.direction = 3;
				break;

			case EAST:
				this.direction = 1;
				break;

			default:
				throw new IllegalArgumentException("illegal direction: " + direction);
			}
		}

		public int getDirection() {
			return direction;
		}

		public int getId() {
			return id;
		}

		public Location getPosition() {
			return position;
		}

	}

}
