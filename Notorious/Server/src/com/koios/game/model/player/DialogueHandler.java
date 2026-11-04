package com.koios.game.model.player;

import java.util.Calendar;
import java.util.Objects;

import com.koios.Constants;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.def.MobDefinition;
import com.koios.network.out.impl.SendChatInterfacePacket;
import com.koios.network.out.impl.SendHeadToInterfacePacket;
import com.koios.network.out.impl.SendInterfaceAnimationPacket;
import com.koios.network.out.impl.SendMobHeadOnInterfacePacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.network.out.impl.ToggleWidgetLayer;
import com.koios.util.StringUtil;
import com.koios.util.TimeUtil;

public class DialogueHandler {

	private final Player player;

	public DialogueHandler(Player player) {
		this.player = Objects.requireNonNull(player, "player");
	}

	public void sendChoice(String text1, String text2) {
		player.getSession().send(new SendStringPacket("Select an Option", 2460));
		player.getSession().send(new SendStringPacket(text1, 2461));
		player.getSession().send(new SendStringPacket(text2, 2462));
		player.getSession().send(new ToggleWidgetLayer(true, 2465));
		player.getSession().send(new SendChatInterfacePacket(2459));
	}

	public void sendChoice(String text1, String text2, String text3) {
		player.getSession().send(new SendStringPacket("Select an Option", 2470));
		player.getSession().send(new SendStringPacket(text1, 2471));
		player.getSession().send(new SendStringPacket(text2, 2472));
		player.getSession().send(new SendStringPacket(text3, 2473));
		player.getSession().send(new ToggleWidgetLayer(true, 2476));
		player.getSession().send(new SendChatInterfacePacket(2469));
	}

	public void sendChoice(String text1, String text2, String text3, String text4) {
		player.getSession().send(new SendStringPacket("Select an Option", 2481));
		player.getSession().send(new SendStringPacket(text1, 2482));
		player.getSession().send(new SendStringPacket(text2, 2483));
		player.getSession().send(new SendStringPacket(text3, 2484));
		player.getSession().send(new SendStringPacket(text4, 2485));
		player.getSession().send(new ToggleWidgetLayer(true, 2488));
		player.getSession().send(new SendChatInterfacePacket(2480));
	}

	/**
	 * Handles all talking
	 *
	 * @param dialogueId The dialogue you want to use
	 * @param mobId The mob id that the chat will focus on during the chat
	 */
	public void sendDialogues(int dialogueId, int mobId) {
		player.dialogueAction = -1;
		player.dialogueMobId = mobId;

		switch (dialogueId) {
		case 700:
			sendMobChat1("Greetings noble warrior, and what can I do for you?", 747);
			player.nextChat = 701;
			break;
		case 701:
			sendPlayerChat1("Can you make me a dragonfire shield?");
			player.nextChat = 702;
			break;
		case 702:
			sendMobChat1("It'll cost you 5 million coins, is that okay?", mobId);
			player.nextChat = 703;
			break;
		case 703:
			this.sendChoice("Yes, that's fine!", "No thanks!");
			player.nextChat = 0;
			player.dialogueAction = 700;
			break;
		case 0:
			player.getSession().send(new SendRemoveInterfacePacket());
			player.dialogueMobId = -1;
			player.nextChat = 0;
			break;

		case 1:
			sendStatement("You found a hidden tunnel, do you want to enter it?");
			player.nextChat = 2;
			break;

		case 2:
			sendChoice("Yeah I'm fearless!", "No way, that looks scary!");
			player.dialogueAction = 7;
			player.nextChat = 0;
			break;

		case 149:
			sendMobChat2("My store is only for those who have excelled in at least", "one skill within the World of " + Constants.SERVER_NAME + ".", mobId);
			player.nextChat = 0;
			break;

		case 3:
			sendMobChat1("Is there anything I can do to help you?", player.dialogueMobId);
			player.nextChat = 4;
			break;

		case 4:
			sendChoice("I was given a challenge, what now?", "What is this place?", "Nothing, thanks.");
			player.dialogueAction = 1;
			break;

		case 5:
			sendMobChat1("Well pass it here and we will get you started.", 3050);
			player.nextChat = 0;
			break;

		case 6:
			sendMobDialogue("Here you can fight powerful Champions of different races.", "You will be rewarded with Slayer points for each Champion",
					"that you defeat. You will also recieve a special reward", "for defeating all the Champions... If you can that is!", 3050);
			player.nextChat = 0;
			break;

		case 7:
			sendMobDialogue("So you want to accept the challenge huh? Well there ", "are some specific rules for these Champion fights. You",
					"are not allowed to use any magic or ranged attacks.", "The use of prayers is also not allowed.", 3050);
			player.nextChat = 10;
			break;

		case 8:
			sendChoice("Yes, let me at them!", "No thanks, I'll pass.");
			player.dialogueAction = 2;
			break;

		case 9:
			sendMobChat2("Your challengers are ready, please go down through", "the trapdoor when you're ready.", 3050);
			player.nextChat = 0;
			break;

		case 10:
			sendMobChat1("Do you still want to proceed?", player.dialogueMobId);
			player.nextChat = 8;
			break;

		case 25:
			sendChoice("Saradomin Boss", "Armadyl Boss", "Bandos Boss", "Zamorak Boss");
			player.dialogueAction = 13;
			break;

		case 26:
			sendChoice("Pick the flowers", "Leave the flowers");
			player.dialogueAction = 26;
			break;

		case 76:
			sendMobChat1("I can heal you if your injured.", player.dialogueMobId);
			player.nextChat = 0;
			break;

		case 77:
			sendMobDialogue(player.getName() + " you have failed.", "You did participate enough to take down", "the portals. ", "Try Harder next time.", player.dialogueMobId);
			break;

		case 78:
			sendMobDialogue("All is lost!", "You could not take down the portals in time.", "", "Try harder next time.", player.dialogueMobId);
			break;

		case 79:
			sendMobDialogue("Congratulations " + player.getName() + "!", "You took down all the portals whilst keeping", "the void knight alive.", "You been awarded, well done.",
					player.dialogueMobId);
			break;

		case 300:
			sendPlayerChat1("Hello there Zambo, do you have any items for sale?");
			player.nextChat = 311;
			break;

		case 311:
			sendMobDialogue("Indeed I do " + player.getName() + ", I sell a wide range of rare and", "powerful items in exchange for Donator Points. You can get",
					"Donator Points by simply typing ::donate and by following", "the instructions on that page or by talking to William.", player.dialogueMobId);
			player.nextChat = 312;
			break;

		case 312:
			sendPlayerChat1("Okay thanks a lot Zambo, loving the outfit by the way!");
			player.nextChat = 313;
			break;

		case 313:
			sendMobChat1("No problem my friend and thanks!", player.dialogueMobId);
			player.nextChat = 0;
			break;

		case 350:
			sendChoice("Open Donator Points Shop 1", "Open Donator Points Shop 2");
			player.dialogueAction = 100;
			player.nextChat = 0;
			break;

		case 169:
			sendOptions("Bet 5M?", "Bet 10M?", "Bet 25M?", "Bet 50M?", "I don't want to bet");
			player.dialogueAction = 411;
			break;

		case 106:
			sendOptions("One 6-sided die", "Two 6-sided dice", "One 4-sided die", "One 8-sided die", "More...");
			player.dialogueAction = 106;
			player.nextChat = 0;
			break;

		case 107:
			sendOptions("One 10-sided die", "One 12-sided die", "One 20-sided die", "Two 10-sided dice for 1-100", "Back...");
			player.dialogueAction = 107;
			player.nextChat = 0;
			break;

		case 99:
			sendMobDialogue("Welcome to my PvP exchange shop. I add new", "items to my shop depending on how many kills", "you have made. Every kill you make I add",
					"a new item to the shop. Would you like to access it?", player.dialogueMobId);
			player.nextChat = 100;
			break;

		case 100:
			sendChoice("Yes, please.", "No, thanks.");
			player.dialogueAction = 121;
			player.nextChat = 0;
			break;

		case 185:
			sendMobDialogue("Hello, Im Bonzo! Im in charge of ProjectSurge's Lottery.", "Entering cost is 10 million gp but you could win up to",
					"250 million gp! you can enter up to 5 times per draw.", "Would you like to enter the lottery?", player.dialogueMobId);
			player.nextChat = 186;
			break;

		case 186:
			sendChoice("Yes I would like to enter!", "No, Id rather not.");
			player.dialogueAction = 186;
			break;

		case 215:
			sendMobChat2("Hello there! Are you interested in buying one of my special", "team capes?", player.dialogueMobId);
			player.nextChat = 216;
			break;

		case 216:
			sendChoice("Yes please!", "No thanks.");
			player.dialogueAction = 277;
			break;

		case 202:
			sendPlayerChat1("Nurmof, could you give me a guide on Falador Mine?");
			player.nextChat = 203;
			break;

		case 203:
			sendMobChat3("Yes of course I can! You can buy any pickaxe you", "need up to rune, from me for a certain price. All",
					"the ore you can mine can be found around this area.", player.dialogueMobId);
			player.nextChat = 204;
			break;

		case 204:
			sendPlayerChat1("Okay brilliant, thanks a lot Nurmof!");
			player.nextChat = 0;
			break;

		/* Fight pits dialogue. */

		case 12:
			sendMobChat1("Wait for my signal before fighting.", 2618);
			player.nextChat = 0;
			break;

		case 13:
			sendMobChat1("FIGHT!", 2618);
			player.nextChat = 0;
			break;

		case 14:
			sendMobChat2("Congratulations, you are the champion!", "You have been given 5 fight points as a reward.", 2618);
			player.nextChat = 0;
			break;

		case 15:
			if (player.getFightPitPoints() <= 0) {
				sendMobChat2("You do not have any fight points!", "Enter the vent to wait for a game to begin.", 2620);
			} else {
				sendMobChat1("You have " + player.getFightPitPoints() + " available fight points.", 2620);
			}

			player.nextChat = 0;
			break;

		/* Banker Dialogue. */
		case 494:
			sendMobChat1("What can I do for you?", mobId);
			player.nextChat = 495;
			break;

		case 495:
			sendOptions("What would you like to do?", "I'd like to access my bank account, please.", "I'd like to check my PIN settings.", "Nothing.");
			player.dialogueAction = 495;
			break;

		/* Bank pin dialogue. */
		case 496:
			sendStatement("Since you don't know your PIN, it will be deleted @dre@in another " + player.getBankPinHandler().getSettings().getRecoverDelay(),
					"@dre@days@bla@. If you wish to cancel this change you may do so by entering", "your PIN correctly the next time you attempt to use your bank.");
			break;

		case 497:
			sendStatement("@dre@That number was incorrect.", "Please try again.");
			player.getBankPinHandler().getSettings().setAttempts(1, 0);
			break;

		case 498:
			final int[] DISABLE_TIMES = { 10, 15 };
			final int attempts = player.getBankPinHandler().getSettings().getAttempts();
			final int time = DISABLE_TIMES[attempts - 1];
			sendStatement("If you've forgotten your PIN, please click the", "'@dre@I don't know it@bla@' button on the keypad screen.",
					"Meanwhile, please wait @blu@" + time + " seconds@bla@ before trying again.");
			player.getBankPinHandler().getSettings().setAttempts(attempts + 1, time);
			break;

		case 499:
			sendStatement("You have entered the PIN incorrectly several times.", "@dre@Please wait 10 minutes before trying again.");
			player.getBankPinHandler().getSettings().setAttempts(4, 600);
			break;

		case 500:
			final int remaining = player.getBankPinHandler().getSettings().getDisableTime();
			String message = remaining > 60 ? Math.round(remaining / 60) + " minute" : remaining + " second";
			String plural = remaining > 60 ? Math.round(remaining / 60) == 1 ? "" : "s" : remaining == 1 ? "" : "s";
			sendStatement("Please wait @blu@" + message + plural + "@bla@ before trying this again.");
			break;

		case 501:
			sendOptions("A PIN will be set on your bank account in "
					+ TimeUtil.daysBetween(Calendar.getInstance().getTime(), player.getBankPinHandler().getSettings().getActiveDate()) + " days.",
					"Yes, I asked for this. I want this PIN.", "No, I didn't ask for this. Cancel it.");
			player.dialogueAction = 501;
			break;

		case 502:
			sendOptions("Do you really wish to delete your Bank Pin?", "Yes, I don't need a PIN anymore.", "No thanks, I'd rather keep the extra security.");
			player.dialogueAction = 502;
			break;

		case 503:
			sendOptions("Do you really wish to change your Bank Pin?", "Yes, I am ready for a new one.", "No thanks, I'll stick with my present one.");
			player.dialogueAction = 503;
			player.nextChat = 0;
			break;

		case 504:
			sendStatement("The PIN has been cancelled and will NOT be set.", "You still do not have a Bank PIN.");
			player.dialogueAction = 504;
			break;

		/* Slayer master dialogue. */
		case 50:
			sendMobChat1("'Ello, and what are you after then?", mobId);
			player.nextChat = 51;
			break;

		case 51:
			if (player.getSlayer().getPartner() != null) {
				sendOptions("Select an Option", "I need another assignment.", "Do you have anything for point exchange?", "End my co-op session.", "Er...nothing.");
			} else {
				sendOptions("Select an Option", "I need another assignment.", "Do you have anything for point exchange?", "Er...nothing.");
			}

			player.dialogueAction = 51;
			break;

		case 52:
			sendPlayerChat1("I need another assignment.");
			player.nextChat = 54;
			break;

		case 53:
			sendPlayerChat1("Do you have anything for trade?");
			player.nextChat = 56;
			break;

		case 54:
			if (player.getSlayer().assignTask(mobId)) {
				sendMobChat1(
						"Your new task is to kill " + player.getSlayer().getKillsLeft() + " " + StringUtil.capitalize(player.getSlayer().getAssignment().getMobName()) + "(s).",
						mobId);
			}

			player.nextChat = 0;
			break;

		case 55:
			sendMobChat2("You're still hunting " + StringUtil.capitalize(player.getSlayer().getAssignment().getMobName()) + "s,", "would you like to clear your task?", mobId);
			player.nextChat = 60;
			break;

		case 56:
			sendMobChat1("I have a wide selection of Slayer point equipment; take a look!", mobId);
			player.nextChat = 0;
			break;

		case 57:
			sendOptions(player.getSlayer().getRequestedBy().getName() + " would like to be your Slayer partner", "I would like a co-op partner.", "I don't need any help.");
			player.dialogueAction = 57;
			player.nextChat = 0;
			break;

		case 58:
			sendOptions("This player is already your co-op partner. End co-op partnership?", "Yes, please.", "No thank you.");
			player.dialogueAction = 58;
			player.nextChat = 0;
			break;

		case 59:
			sendMobChat2("It seems that your partner does not reach the requirements", "to obtain a task from me.", mobId);
			player.nextChat = 0;
			break;

		case 60:
			sendOptions("Would you like to clear your task for 5 slayer points?", "Yes, please.", "No thank you.");
			player.dialogueAction = 60;
			player.nextChat = 0;
			break;

		/* Fight Kiln dialogue. */
		case 61:
			if (!player.getInventory().contains(6570)) {
				sendMobChat1("You cannot enter without a Firecape to sacrifice.", mobId);
				player.nextChat = 0;
				return;
			}

			sendMobChat1("Would you like to sacrifice your Firecape and enter the kiln?", mobId);
			player.nextChat = 62;
			break;

		case 62:
			sendOptions("Would you like to sacrifice your Firecape?", "Yes", "No");
			player.dialogueAction = 62;
			player.nextChat = 0;
			break;

		default:
			System.err.println(String.format("Invalid dialogue attempted opened ['%s', %d, %d]", player.getName(), dialogueId, mobId));
		}
	}

	private void sendMobChat1(String text, int id) {
		player.getSession().send(new SendInterfaceAnimationPacket(4883, 9847));
		player.getSession().send(new SendStringPacket(MobDefinition.get(id).getName(), 4884));
		player.getSession().send(new SendStringPacket(text, 4885));
		player.getSession().send(new SendMobHeadOnInterfacePacket(id, 4883));
		player.getSession().send(new SendChatInterfacePacket(4882));
	}

	public void sendMobChat2(String text1, String text2, int id) {
		player.getSession().send(new SendInterfaceAnimationPacket(4888, 9847));
		player.getSession().send(new SendStringPacket(MobDefinition.get(id).getName(), 4889));
		player.getSession().send(new SendStringPacket(text1, 4890));
		player.getSession().send(new SendStringPacket(text2, 4891));
		player.getSession().send(new SendMobHeadOnInterfacePacket(id, 4888));
		player.getSession().send(new SendChatInterfacePacket(4887));
	}

	private void sendMobChat3(String text1, String text2, String text3, int id) {
		player.getSession().send(new SendInterfaceAnimationPacket(4894, 9847)); // Was 591
		player.getSession().send(new SendStringPacket(MobDefinition.get(id).getName(), 4895));
		player.getSession().send(new SendStringPacket(text1, 4896));
		player.getSession().send(new SendStringPacket(text2, 4897));
		player.getSession().send(new SendStringPacket(text3, 4898));
		player.getSession().send(new SendMobHeadOnInterfacePacket(id, 4894));
		player.getSession().send(new SendChatInterfacePacket(4893));
	}

	private void sendMobDialogue(String text1, String text2, String text3, String text4, int id) {
		player.getSession().send(new SendInterfaceAnimationPacket(4901, 9847));
		player.getSession().send(new SendStringPacket(MobDefinition.get(id).getName(), 4902));
		player.getSession().send(new SendStringPacket(text1, 4903));
		player.getSession().send(new SendStringPacket(text2, 4904));
		player.getSession().send(new SendStringPacket(text3, 4905));
		player.getSession().send(new SendStringPacket(text4, 4906));
		player.getSession().send(new SendMobHeadOnInterfacePacket(id, 4901));
		player.getSession().send(new SendChatInterfacePacket(4900));
	}

	/**
	 * Sends options for the player to click.
	 *
	 * @param options The options.
	 */
	public void sendOptions(String title, String... options) {
		Objects.requireNonNull(title, "null title");
		Objects.requireNonNull(options, "null options");

		final int[] OPTION_IDS = { 2459, 2459, 2469, 2480, 2492 };
		final int id = OPTION_IDS[options.length - 1];
		player.getSession().send(new SendStringPacket(title, id + 1));

		for (int i = 0; i < options.length; i++) {
			player.getSession().send(new SendStringPacket(options[i], id + 2 + i));
		}

		final int[] ids = { 2465, 2465, 2476, 2488, 2501 };
		player.getSession().send(new ToggleWidgetLayer(title.equalsIgnoreCase("Select an Option"), ids[options.length - 1]));
		player.getSession().send(new SendChatInterfacePacket(id));
	}

	public void sendOptions(String text1, String text2, String text3, String text4, String text5) {
		player.getSession().send(new SendStringPacket("Select an Option", 2493));
		player.getSession().send(new SendStringPacket(text1, 2494));
		player.getSession().send(new SendStringPacket(text2, 2495));
		player.getSession().send(new SendStringPacket(text3, 2496));
		player.getSession().send(new SendStringPacket(text4, 2497));
		player.getSession().send(new SendStringPacket(text5, 2498));
		player.getSession().send(new ToggleWidgetLayer(true, 2501));
		player.getSession().send(new SendChatInterfacePacket(2492));
	}

	public void sendPlayerChat1(String text) {
		player.getSession().send(new SendInterfaceAnimationPacket(969, Expression.CALM_TALK.getEmoteId()));
		player.getSession().send(new SendStringPacket(player.getName(), 970));
		player.getSession().send(new SendStringPacket(text, 971));
		player.getSession().send(new SendHeadToInterfacePacket(969));
		player.getSession().send(new SendChatInterfacePacket(968));
	}

	/**
	 * Sends a statement.
	 *
	 * @param lines The lines of dialogue for the statement.
	 */
	public void sendStatement(String... lines) {
		int[] STATEMENT_IDS = { 356, 359, 363, 368, 374 };
		int id = STATEMENT_IDS[lines.length - 1];

		for (int i = 0; i < lines.length; i++) {
			player.getSession().send(new SendStringPacket(lines[i], id + 1 + i));
		}

		player.getSession().send(new SendChatInterfacePacket(id));
	}

}
