package com.koios.client;

public class ClanMember {

	public enum Rank {

		NOT_RANKED,
		FRIEND,
		RECRUIT,
		CORPORAL,
		SERGEANT,
		LIEUTENANT,
		CAPTAIN,
		GENERAL,
		OWNER,
		BANNED,
		KICKED;

		public static Rank forId(int id) {
			for (Rank rank : Rank.values()) {
				if (rank.ordinal() == id) {
					return rank;
				}
			}

			return null;
		}

		public String toStr() {
			return Client.instance.capitalize(toString().toLowerCase().replaceAll("_", " "));
		}

	}

	private String playerName;
	private Rank rank;

	public ClanMember(String playerName, int rankId) {
		this.playerName = playerName;
		rank = Rank.forId(rankId);
	}

	public String getPlayerName() {
		return playerName;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(int rankId) {
		rank = Rank.forId(rankId);
	}

}