package com.koios.game.model.player;

/**
 * The request manager manages
 *
 * @author Graham Edgecombe
 */
public class RequestManager {

	/**
	 * Holds the different states the manager can be in.
	 *
	 * @author Graham Edgecombe
	 */
	public enum RequestState {

		/**
		 * Nobody has offered a request.
		 */
		NORMAL,

		/**
		 * The player is participating in an existing request of this type, so cannot accept new requests at all.
		 */
		PARTICIPATING,

		/**
		 * Somebody has offered some kind of request.
		 */
		REQUESTED

	}

	/**
	 * Represents the different types of request.
	 *
	 * @author Graham Edgecombe
	 */
	public enum RequestType {

		/**
		 * A duel request.
		 */
		DUEL("duelreq"),

		/**
		 * A trade request.
		 */
		TRADE("tradereq");

		/**
		 * The client-side name of the request.
		 */
		private final String clientName;

		/**
		 * Creates a type of request.
		 *
		 * @param clientName The name of the request client-side.
		 */
		private RequestType(String clientName) {
			this.clientName = clientName;
		}

		/**
		 * Gets the client name.
		 *
		 * @return The client name.
		 */
		public String getClientName() {
			return clientName;
		}

	}

	/**
	 * The current 'acquaintance'.
	 */
	private Player acquaintance;

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * The current 'requestStage'.
	 */
	private int requestStage;

	/**
	 * The current request type.
	 */
	private RequestType requestType;

	/**
	 * The current state.
	 */
	private RequestState state = RequestState.NORMAL;

	/**
	 * Creates the request manager.
	 *
	 * @param player The player whose requests the manager is managing.
	 */
	public RequestManager(Player player) {
		this.player = player;
	}

	/**
	 * @return the acquaintance
	 */
	public Player getAcquaintance() {
		return acquaintance;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the requestStage
	 */
	public int getRequestStage() {
		return requestStage;
	}

	/**
	 * @return the requestType
	 */
	public RequestType getRequestType() {
		return requestType;
	}

	/**
	 * @return the state
	 */
	public RequestState getState() {
		return state;
	}

	/**
	 * @param acquaintance the acquaintance to set
	 */
	public void setAcquaintance(Player acquaintance) {
		this.acquaintance = acquaintance;
	}

	/**
	 * @param requestStage the requestStage to set
	 */
	public void setRequestStage(int requestStage) {
		this.requestStage = requestStage;
	}

	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(RequestState state) {
		this.state = state;
	}

}
