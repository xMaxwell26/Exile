package com.koios.game.model.player;

public final class Privileges {

	public static interface Privilege {
		int getCrownId();

		Priority getPriority();

		default boolean superiorTo(Privilege other) {
			return getPriority().compareTo(other.getPriority()) > 0;
		}

		default boolean inferiorTo(Privilege other) {
			return getPriority().compareTo(other.getPriority()) < 0;
		}
	}

	public enum RegularPrivilege implements Privilege {
		NONE(0, Priority.NONE), HELPER(5, Priority.LOW), MODERATOR(6, Priority.MEDIUM), ADMINISTRATOR(7, Priority.HIGH), DEVELOPER(8, Priority.MAXIMUM), OWNER(9, Priority.MAXIMUM);

		private final int crownId;
		private final Priority priority;

		private RegularPrivilege(int crownId, Priority priority) {
			this.crownId = crownId;
			this.priority = priority;
		}

		@Override
		public int getCrownId() {
			return crownId;
		}

		@Override
		public Priority getPriority() {
			return priority;
		}
	}

	public enum DonatorPrivilege implements Privilege {
		NONE(0, Priority.NONE), REGULAR(1, Priority.NONE), SUPER(2, Priority.LOW), EXTREME(3, Priority.MEDIUM), LEGENDARY(4, Priority.HIGH);

		private final int crownId;
		private final Priority priority;

		private DonatorPrivilege(int crownId, Priority priority) {
			this.crownId = crownId;
			this.priority = priority;
		}

		@Override
		public int getCrownId() {
			return crownId;
		}

		@Override
		public Priority getPriority() {
			return priority;
		}

	}

	private enum Priority {
		NONE, LOW, MEDIUM, HIGH, MAXIMUM
	}

}