package com.koios.game.content.dialogue;

import java.util.Objects;

import com.koios.game.def.MobDefinition;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacketBuilder;
import com.koios.network.out.impl.SendChatInterfacePacket;
import com.koios.network.out.impl.SendHeadToInterfacePacket;
import com.koios.network.out.impl.SendInterfaceAnimationPacket;
import com.koios.network.out.impl.SendItemOnInterfacePacket;
import com.koios.network.out.impl.SendMobHeadOnInterfacePacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.network.out.impl.ToggleWidgetLayer;

public abstract class Dialogue {

	/**
	 * The make single item title interface id.
	 */
	private static final int MAKE_ITEM_TITLE_INTERFACE_ID = 2799;

	/**
	 * The make single item model interface id.
	 */
	private static final int MAKE_ITEM_MODEL_INTERFACE_ID = 1746;

	/**
	 * The make single item interface id.
	 */
	private static final int MAKE_ITEM_INTERFACE_ID = 4429;

	/**
	 * The statement interface ids.
	 */
	private static final int[] STATEMENT_DIALOGUE_IDS = { 356, 359, 363, 368, 374 };

	/**
	 * The option interface ids.
	 */
	private static final int[] OPTION_DIALOGUE_IDS = { 13760, 2461, 2471, 2482, 2494 };

	/**
	 * Formula for the player dialogue lines
	 */
	private static final int getPlayerDialogueFormula(int i) {
		return 968 + i - 1 + i * i + i / 2 - 1 - i / 4 - i / 4;
	}

	/**
	 * Formula for the npc dialogue lines
	 */
	private static final int getNpcDialogueFormula(int i) {
		return 4882 + i - 1 + i * i + i / 2 - 1 - i / 4 - i / 4;
	}

	private int phase;

	protected final Player player;

	protected Dialogue(Player player) {
		this(player, 0);
	}

	protected Dialogue(Player player, int phase) {
		this.player = Objects.requireNonNull(player, "player cannot be null");
		this.phase = phase;
	}

	/**
	 * Handles player chatting
	 *
	 * @param expression The {@link Expression} to display
	 * @param text The text to write
	 */
	protected final void chat(Expression expression, String... text) {
		Objects.requireNonNull(text);
		int length = text.length;

		if (length < 0 || length > 5) {
			return;
		}

		player.getSession().send(new SendStringPacket(player.getName(), getPlayerDialogueFormula(text.length) + 2));
		player.getSession().send(new SendInterfaceAnimationPacket(getPlayerDialogueFormula(text.length) + 1, expression.getEmoteId()));
		player.getSession().send(new SendHeadToInterfacePacket(getPlayerDialogueFormula(text.length) + 1));
		player.getSession().send(new SendChatInterfacePacket(getPlayerDialogueFormula(text.length)));
		for (int i = 0; i < text.length; i++) {
			player.getSession().send(new SendStringPacket(text[i], getPlayerDialogueFormula(text.length) + 3 + i));
		}
	}

	/**
	 * Sends a list of choices
	 *
	 * @param title The title of the choice interface
	 * @param options The options to send on the choice interface
	 */
	protected final void choice(String title, String... options) {
		if (title == null) {
			title = "Select an Option";
		}

		int length = options.length;
		int dialogueId = OPTION_DIALOGUE_IDS[length - 1];

		// If the title is too long, we need to switch the layers
		if (title.length() > "Select an Option".length()) {
			int id = dialogueId + length + 4;
			player.getSession().send(new ToggleWidgetLayer(false, id));
			player.getSession().send(new ToggleWidgetLayer(true, id + 3));
		}

		player.getSession().send(new SendStringPacket(title, dialogueId - 1));

		for (int index = 0; index < length; index++) {
			player.getSession().send(new SendStringPacket(options[index], OPTION_DIALOGUE_IDS[length - 1] + index));
		}

		player.getSession().send(new SendChatInterfacePacket(dialogueId - 2));
	}

	/**
	 * Stops the dialogue
	 */
	protected void finish() {
		stop();
	}

	/**
	 * Accepts the input from the integer input class
	 *
	 * @param value The value read
	 */
	protected void input(int value) {
		if (player.isSuperAdministrator()) {
			player.sendMessage("Unhandled dialogue value input: " + value);
			player.sendMessage("in script: " + getClass().getName());
		}

		stop();
	}

	/**
	 * Accepts the input from the integer input class
	 *
	 * @param value The value read
	 */
	protected void input(String value) {
		if (player.isSuperAdministrator()) {
			player.sendMessage("Unhandled dialogue name input: " + value);
			player.sendMessage("in script: " + getClass().getName());
		}

		stop();
	}

	/**
	 * Sends a mob chatting with the player
	 *
	 * @param id The id of the mob
	 * @param expression The {@link Expression} the mob is making
	 * @param text The lines of text the mob is saying
	 */
	protected final void mobChat(int id, Expression expression, String... text) {
		player.getSession().send(new SendChatInterfacePacket(getNpcDialogueFormula(text.length)));
		player.getSession().send(new SendStringPacket(MobDefinition.get(id).getName(), getNpcDialogueFormula(text.length) + 2));
		player.getSession().send(new SendInterfaceAnimationPacket(getNpcDialogueFormula(text.length) + 1, expression.getEmoteId()));
		player.getSession().send(new SendMobHeadOnInterfacePacket(id, getNpcDialogueFormula(text.length) + 1));
		for (int i = 0; i < text.length; i++) {
			player.getSession().send(new SendStringPacket(text[i], getNpcDialogueFormula(text.length) + 3 + i));
		}

	}

	/**
	 * Continues the dialogue to the next phase
	 */
	protected void next() {
		stop();
	}

	/**
	 * Selects an option from a choice menu
	 *
	 * @param index The index of the option
	 */
	protected void select(int index) {
	}

	/**
	 * Starts the dialogue
	 */
	protected abstract void start();

	/**
	 * Sends a statement.
	 *
	 * @param p The player.
	 * @param lines The lines of dialogue for the statement.
	 */
	public void sendStatement(String... lines) {
		int id = STATEMENT_DIALOGUE_IDS[lines.length - 1];
		for (int i = 0; i < lines.length; i++) {
			player.getSession().send(new SendStringPacket(lines[i], id + 1 + i));
		}
		player.getSession().send(new SendChatInterfacePacket(id));
	}

	/**
	 * Sends a chat with an item on it
	 *
	 * @param header The Title of the dialogue
	 * @param one The first line of text
	 * @param two The second line of text
	 * @param three The third line of text
	 * @param four The fourth line of text
	 * @param item The item to display on the interface
	 * @param zoom The zoom of the item
	 */
	public void sendItemChat(String header, String one, String two, String three, String four, int item, int zoom) {
		player.getSession().send(new SendItemOnInterfacePacket(4901, zoom, item));
		player.getSession().send(new SendStringPacket(header, 4902));
		player.getSession().send(new SendStringPacket(one, 4903));
		player.getSession().send(new SendStringPacket(two, 4904));
		player.getSession().send(new SendStringPacket(three, 4905));
		player.getSession().send(new SendStringPacket(four, 4906));
		player.getSession().send(new SendChatInterfacePacket(4900));
	}

	public void sendMakeItemChat(int id, int zoom, String title) {
		player.getSession().send(new SendItemOnInterfacePacket(MAKE_ITEM_MODEL_INTERFACE_ID, zoom, id));
		player.getSession().send(new SendStringPacket(MAKE_ITEM_TITLE_INTERFACE_ID, title));
		player.getSession().send(new SendChatInterfacePacket(MAKE_ITEM_INTERFACE_ID));
	}

	/**
	 * Stops the dialogue from continuing and closes the interface
	 */
	protected final void stop() {
		player.getSession().send(new SendRemoveInterfacePacket());
	}

	/**
	 * Sends the string integer packet
	 */
	protected final void takeNameInput() {
		player.getInterfaceState().openNameInput(this::input);
	}

	/**
	 * Sends the integer input packet
	 */
	protected final void takeValueInput() {
		player.write(new GamePacketBuilder(27));
	}

	/**
	 * Checks if the phase matches the specific phase
	 *
	 * @param phase The phase to check for
	 * @return If the current phase matches the provided phase
	 */
	public boolean isPhase(int phase) {
		return this.phase == phase;
	}

	/**
	 * Sets the current phase value
	 *
	 * @param phase The phase value to set
	 */
	public void setPhase(int phase) {
		this.phase = phase;
	}

	/**
	 * Gets the current phase
	 *
	 * @return The current phase
	 */
	public int getPhase() {
		return phase;
	}

}
