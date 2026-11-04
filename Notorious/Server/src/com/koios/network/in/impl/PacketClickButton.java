package com.koios.network.in.impl;

import java.util.Optional;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.KeepOnDeath;
import com.koios.game.content.SkillcapeAnimation;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.bankpins.BankPinHandler.Action;
import com.koios.game.content.cluescroll.ClueScroll;
import com.koios.game.content.cluescroll.ClueScrollConstants;
import com.koios.game.content.cluescroll.EmoteScroll;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.combat.CombatVariables.AttackStyle;
import com.koios.game.content.combat.magic.MagicData;
import com.koios.game.content.combat.magic.MagicRequirements;
import com.koios.game.content.combat.prayer.Prayer;
import com.koios.game.content.combat.special.SpecialHandler;
import com.koios.game.content.combat.special.SpecialHandler.SpecialRestoreTask;
import com.koios.game.content.dialogue.impl.misc.SummoningDismiss;
import com.koios.game.content.dialogue.impl.teleportation.BossesTeleportDialogue;
import com.koios.game.content.dialogue.impl.teleportation.CityTeleportDialogue;
import com.koios.game.content.dialogue.impl.teleportation.MinigameTeleportDialogue;
import com.koios.game.content.dialogue.impl.teleportation.PvPTeleportDialogue;
import com.koios.game.content.dialogue.impl.teleportation.SkillTeleportDialogue;
import com.koios.game.content.dialogue.impl.teleportation.TrainingTeleportDialogue;
import com.koios.game.content.effects.impl.Vengeance;
import com.koios.game.content.friendschat.FriendschatCommons;
import com.koios.game.content.minigames.duel.DuelArenaHandler;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.music.MusicTab;
import com.koios.game.content.partyroom.LeverOption;
import com.koios.game.content.partyroom.PartyRoomManager;
import com.koios.game.content.questtab.QuestPage;
import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.content.skill.InterfaceButtons;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.summoning.SummoningData;
import com.koios.game.content.skill.summoning.actions.CallFamiliar;
import com.koios.game.content.skill.summoning.actions.CreatePouch;
import com.koios.game.content.skill.summoning.actions.CreateScroll;
import com.koios.game.content.skill.summoning.actions.RenewFamiliar;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.def.ItemDefinition;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.Trade;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.ConfigKeys;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;
import com.koios.game.model.player.task.StaffOfLightTask;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendOpenSideTabPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendSetWidgetColor;
import com.koios.network.out.impl.SendSidebarInterfacePacket;
import com.koios.util.NumberUtil;
import com.koios.util.Utils;

/**
 * Clicking most buttons
 */
public class PacketClickButton implements IncomingPacketDecoder {

	private static final int[] SPELL_ID = { 4128, 4130, 4132, 4134, 4136, 4139, 4142, 4145, 4148, 4151, 4153, 4157, 4159, 4161, 4164, 4165, 4129, 4133, 4137, 6006, 6007, 6026,
			6036, 6046, 6056, 4147, 6003, 47005, 4166, 4167, 4168, 48157, 50193, 50187, 50101, 50061, 50163, 50211, 50119, 50081, 50151, 50199, 50111, 50071, 50175, 50223, 50129,
			50091 };

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void handle(Player player, GamePacket packet) {
		int buttonId = Utils.hexToInt(packet.getBuffer().array(), 0, packet.getSize()); // wrong
		int componentId = packet.readUnsignedShort();

		if (!GameService.isOnlineMode()) {
			System.out.println(String.format("%s - clicked widget [id: %d, component: %d]", player.getName(), buttonId, componentId));
		}

		if (player.isDead() || !player.hasFocus()) {
			return;
		}

		if (!player.getController().canClick(player)) {
			return;
		}

		Optional<Prayer> prayer = player.getPrayers().buttonPressed(buttonId);
		if (prayer.isPresent()) {
			player.getPrayers().activate(prayer.get());
			return;
		}

		MusicTab.handleClick(player, buttonId);
		
		QuestPage page = QuestTabHandler.get(player.getAttribute(QuestTabHandler.QUEST_TAB, QuestPages.HOME));
		
		if (page != null) {
			page.buttonClick(player, buttonId);
		}

		if (buttonId >= 3162 && buttonId <= 3166) {
			int value = buttonId == 3162 ? 4 : buttonId == 3163 ? 3 : buttonId == 3164 ? 2 : buttonId == 3165 ? 1 : 0;
			player.setAttribute("music_volume", value);
		}

		if (buttonId >= 3173 && buttonId <= 3177) {
			int value = buttonId == 3173 ? 4 : buttonId == 3174 ? 3 : buttonId == 3175 ? 2 : buttonId == 3176 ? 1 : 0;
			player.setAttribute("sound_volume", value);
		}

		if (buttonId == 155) {
			if (player.getSummoning().hasFollower()) {
				if (player.getSummoning().getPet() != null) {
					player.getSession().send(new SendOpenSideTabPacket(16));
				} else if (player.getSummoning().getFamiliar() != null) {
					player.getSummoning().sendTab(true);
				}
			} else {
				player.sendMessage("You do not have a familiar active.");
			}
		}

		int anim = 0;

		switch (buttonId) {
		case 168:
			anim = 855;
			break;

		case 169:
			anim = 856;
			break;

		case 162:
			anim = 857;
			break;

		case 164:
			anim = 858;
			break;

		case 165:
			anim = 859;
			break;

		case 161:
			anim = 860;
			break;

		case 170:
			anim = 861;
			break;

		case 171:
			anim = 862;
			break;

		case 163:
			anim = 863;
			break;

		case 167:
			anim = 864;
			break;

		case 172:
			anim = 865;
			break;

		case 166:
			anim = 866;
			break;

		case 52050:
			anim = 2105;
			break;

		case 52051:
			anim = 2106;
			break;

		case 52052:
			anim = 2107;
			break;

		case 52053:
			anim = 2108;
			break;

		case 52054:
			anim = 2109;
			break;

		case 52055:
			anim = 2110;
			break;

		case 52056:
			anim = 2111;
			break;

		case 52057:
			anim = 2112;
			break;

		case 52058:
			anim = 2113;
			break;

		case 43092:
			anim = 1374;
			break;

		case 2155:
			anim = 0x46B;
			break;

		case 25103:
			anim = 0x46A;
			break;

		case 25106:
			anim = 0x469;
			break;

		case 2154:
			anim = 0x468;
			break;

		case 52071:
			anim = 0x84F;
			break;

		case 52072:
			anim = 0x850;
			break;

		case 59062:
			anim = 2836;
			break;

		case 72032:
			anim = 3544;
			break;

		case 72033:
			anim = 3543;
			break;

		case 88060:
			anim = 4276;
			break;

		case 88061:
			anim = 4278;
			break;

		case 88062:
			anim = 4280;
			break;

		case 88063:
			anim = 4275;
			break;

		case 72254:
			anim = 6111;
			break;

		case 88065:
			anim = 7272;
			break;
		}

		if (CreatePouch.processInterfaceFunctions(player, buttonId)) {
			return;
		}

		if (CreateScroll.processInterfaceFunctions(player, buttonId)) {
			return;
		}

		if (ClueScrollConstants.playerHasScroll(player)) {
			for (Item item : player.getInventory().toArray()) {

				if (item == null) {
					continue;
				}

				ClueScroll scroll = ClueScrollConstants.getClueScroll(item.getId());

				if (scroll == null || !(scroll instanceof EmoteScroll)) {
					continue;
				}

				if (scroll.inEndLocation(player) && ((EmoteScroll) scroll).getEmote() == anim) {

					if (player.getAttribute("KILL_AGENT", false)) {
						return;
					}
					scroll.attemptCompletion(player);
					break;
				}
			}
		}

		if (player.getMinigame().inMinigame() && player.getMinigame().getCurrentMinigame().handleButtonClick(player, buttonId)) {
			return;
		}

		if (DuelArenaHandler.handleButtons(player, buttonId)) {
			return;
		}

		if (Bank.bankButton(player, buttonId)) {
			return;
		}

		if (FriendschatCommons.buttonPressed(player, componentId)) {
			return;
		}

		if (player.getBankPinHandler().handleButtons(buttonId)) {
			return;
		}

		for (int i = 0; i < SPELL_ID.length; i++) {
			if (buttonId == SPELL_ID[i]) {
				player.autocasting = true;
				player.autocastId = i;
				break;
			}
		}

		player.getQuickPrayers().flagQuickPrayer(buttonId);

		switch (componentId) {
		case 2799:
			player.dialogue().select(1);
			break;

		case 2798:
			player.dialogue().select(2);
			break;

		case 1748:
			player.dialogue().select(3);
			break;

		case 1747:
			player.dialogue().select(4);
			break;
		}

		if (InterfaceButtons.handleButtons(player, buttonId)) {
			return;
		}

		/* if (LoyaltyShop.handleButtons(player, buttonId)) { return; } */

		if (player.getAttribute("tutorial", false) && buttonId != 9157 && buttonId != 9158 && buttonId != 9154) {
			return;
		}

		AchievementManager.onButtonClick(player, buttonId);

		switch (buttonId) {
		case 8198:
			PartyRoomManager.pollQueuedItems(player);
			break;
		case 168:
			player.packetAnimation(855);
			break;

		case 201245:
			player.getInterfaceState().setCurrentInterface(52700);
			player.getInterfaceState().openInterface(52700);
			break;

		case 205226:
			player.getInterfaceState().setCurrentInterface(51700);
			player.getInterfaceState().openInterface(51700);
			break;

		case 169:
			player.packetAnimation(856);
			break;

		case 70117:
			player.getSession().send(new SendOpenSideTabPacket(8));
			break;

		case 70116:
			if (player.getSummoning().getFamiliar() != null) {
				player.getSummoning().handleAction("Interaction", null);
				return;
			}
			break;

		case 162:
			player.packetAnimation(857);
			break;

		case 164:
			player.packetAnimation(858);
			break;

		case 165:
			player.packetAnimation(859);
			break;

		case 161:
			player.packetAnimation(860);
			break;

		case 170:
			player.packetAnimation(861);
			break;

		case 171:
			player.packetAnimation(862);
			break;

		case 163:
			player.packetAnimation(863);
			break;

		case 167:
			player.packetAnimation(864);
			break;

		case 172:
			player.packetAnimation(865, 3);
			break;

		case 166:
			player.packetAnimation(866, 5);
			break;

		case 52050:
			player.packetAnimation(2105);
			break;

		case 52051:
			player.packetAnimation(2106, 4);
			break;

		case 52052:
			player.packetAnimation(2107);
			break;

		case 52053:
			player.packetAnimation(2108, 3);
			break;

		case 52054:
			player.packetAnimation(2109);
			break;

		case 52055:
			player.packetAnimation(2110);
			break;

		case 52056:
			player.packetAnimation(2111, 3);
			break;

		case 52057:
			player.packetAnimation(2112);
			break;

		case 52058:
			player.packetAnimation(2113);
			break;

		case 43092:
			player.packetAnimation(1374, 2, () -> player.playGraphics(new Graphic(1702)));
			break;

		case 2155:
			player.packetAnimation(0x46B);
			break;

		case 25103:
			player.packetAnimation(0x46A, 3);
			break;

		case 25106:
			player.packetAnimation(0x469, 3);
			break;

		case 2154:
			player.packetAnimation(0x468, 4);
			break;

		case 52071:
			player.packetAnimation(0x84F);
			break;

		case 52072:
			player.packetAnimation(0x850);
			break;

		case 59062:
			player.packetAnimation(2836, 4);
			break;

		case 72032:
			player.packetAnimation(3544, 7);
			break;

		case 72033:
			player.packetAnimation(3543, 4);
			break;

		case 88060:
			player.packetAnimation(4276, 2, () -> player.playGraphics(new Graphic(712)));
			break;

		case 88061:
			player.packetAnimation(4278);
			break;

		case 88062:
			player.packetAnimation(4280);
			break;

		case 88063:
			player.packetAnimation(4275);
			break;

		case 72254:
			player.packetAnimation(6111, 4);
			break;

		case 88065:
			player.packetAnimation(7272, 2, () -> player.playGraphics(new Graphic(1244)));
			break;

		case 74108:
			SkillcapeAnimation.perform(player);
			break;

		case 70215:
			player.getInterfaceState().setNameInputConsumer(name -> {
				if (player.friendschatName == null) {
					FriendschatCommons.attemptJoinFriendschat(player, name, true);
				} else {
					player.sendMessage("You are already in a friends chat!");
				}
			});
			break;

		case 70103:
			if (player.getSummoning().getFamiliar() == null) {
				player.sendMessage("You don't have a familiar to use this.");
				return;
			}

			player.getSummoning().handleAction(SummoningData.CAST_SCROLL, null);
			break;

		case 70115:
		case 74078:
			if (player.getSummoning().getFamiliar() == null && player.getSummoning().getPet() == null) {
				player.sendMessage("You don't have a familiar to use this.");
				return;
			}

			CallFamiliar.execute(player);
			break;

		case 70118:
		case 74081:
			if (player.getSummoning().getFamiliar() == null && player.getSummoning().getPet() == null) {
				player.sendMessage("You don't have a familiar to use this.");
				return;
			}

			player.dialogue().start(new SummoningDismiss(player));
			break;

		case 70112:
			if (player.getSummoning().getFamiliar() == null) {
				player.sendMessage("You don't have a familiar to use this.");
				return;
			}

			RenewFamiliar.execute(player);
			break;

		case 70109:
			if (player.getSummoning().getFamiliar() == null) {
				player.sendMessage("You don't have a familiar to use this.");
				return;
			}

			player.getSummoning().handleAction(SummoningData.WITHDRAW_ALL, new Object[] { false });
			break;

		case 13092:
		case 13218:
			Trade.setReadyMessages(player);
			break;

		case 13094:
			player.getSession().send(new SendRemoveInterfacePacket());
			break;

		case 9118:
			player.getSession().send(new SendRemoveInterfacePacket());
			break;

		case 10252: // XP lamp interface - Attack option
			player.getSession().send(new SendConfigPacket(261, 1));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.ATTACK));
			break;

		case 10253: // XP lamp interface - Strength option
			player.getSession().send(new SendConfigPacket(261, 2));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.STRENGTH));
			break;

		case 10254: // XP lamp interface - Ranged option
			player.getSession().send(new SendConfigPacket(261, 3));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.RANGED));
			break;

		case 10255: // XP lamp interface - Magic option
			player.getSession().send(new SendConfigPacket(261, 4));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.MAGIC));
			break;

		case 11000: // XP lamp interface - Defence option
			player.getSession().send(new SendConfigPacket(261, 5));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.DEFENCE));
			break;

		case 11001: // XP lamp interface - Hitpoints option
			player.getSession().send(new SendConfigPacket(261, 6));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.HITPOINTS));
			break;

		case 11002: // XP lamp interface - Prayer option
			player.getSession().send(new SendConfigPacket(261, 7));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.PRAYER));
			break;

		case 11003: // XP lamp interface - Agility option
			player.getSession().send(new SendConfigPacket(261, 8));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.AGILITY));
			break;

		case 11004: // XP lamp interface - Herblore option
			player.getSession().send(new SendConfigPacket(261, 9));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.HERBLORE));
			break;

		case 11005: // XP lamp interface - Thieving option
			player.getSession().send(new SendConfigPacket(261, 10));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.THIEVING));
			break;

		case 11006: // XP lamp interface - Crafting option
			player.getSession().send(new SendConfigPacket(261, 11));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.CRAFTING));
			break;

		case 11007: // XP lamp interface - Runecrafting option
			player.getSession().send(new SendConfigPacket(261, 12));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.RUNECRAFTING));
			break;

		case 47002: // XP lamp interface - Slayer option
			player.getSession().send(new SendConfigPacket(261, 20));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.SLAYER));
			break;

		case 54090: // XP lamp interface - Farming option
			player.getSession().send(new SendConfigPacket(261, 21));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.FARMING));
			break;

		case 11008: // XP lamp interface - Mining option
			player.getSession().send(new SendConfigPacket(261, 13));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.MINING));
			break;

		case 11009: // XP lamp interface - Smithing option
			player.getSession().send(new SendConfigPacket(261, 14));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.SMITHING));
			break;

		case 11010: // XP lamp interface - Fishing option
			player.getSession().send(new SendConfigPacket(261, 15));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.FISHING));
			break;

		case 11011: // XP lamp interface - Cooking option
			player.getSession().send(new SendConfigPacket(261, 16));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.COOKING));
			break;

		case 11012: // XP lamp interface - Firemaking option
			player.getSession().send(new SendConfigPacket(261, 17));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.FIREMAKING));
			break;

		case 11013: // XP lamp interface - Woodcutting option
			player.getSession().send(new SendConfigPacket(261, 18));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.WOODCUTTING));
			break;

		case 11014: // XP lamp interface - Fletching option
			player.getSession().send(new SendConfigPacket(261, 19));
			player.setAttribute("xp_lamp_choice", Integer.valueOf(Skill.FLETCHING));
			break;

		case 11015:
			int reward = player.getAttributeValue("xp_lamp_reward");
			int choice = player.getAttributeValue("xp_lamp_choice");
			int objtype = player.getAttributeValue("xp_lamp_type");
			int levelRequirement = player.getAttributeValue("xp_lamp_lvlreq");

			if (choice == -1) {
				player.sendMessage("Please select a skill first!");
			} else if (player.enabledXPLock) {
				// Avoid waste of bonus experience because of a bad lock
				// setting.
				player.sendMessage("You must toggle the XP lock before doing this!");
			} else if (objtype != -1 && !player.getInventory().contains(objtype)) {
				// This bonus XP originated from an inventory item that we
				// no longer have.
				player.sendMessage("Unable to continue without item: " + ItemDefinition.forId(objtype).getName() + ".");
			} else if (levelRequirement != -1 && levelRequirement > player.getSkill().getLevelForExperience(choice)) {
				player.sendMessage("You need to have at least level " + levelRequirement + " in the skill you choose.");
			} else {
				player.getSkill().addExperience(choice, reward);
				player.sendMessage("You've choosen " + NumberUtil.format(reward) + " XP in " + Skill.SKILL_NAME[choice] + "... but at what cost?");
				player.getSession().send(new SendRemoveInterfacePacket());

				if (objtype != -1) {
					player.getInventory().remove(new Item(objtype, 1));
					player.removeAttribute("xp_lamp_type");
				}

				player.removeAttribute("xp_lamp_reward");
				player.removeAttribute("xp_lamp_choice");
				player.removeAttribute("xp_lamp_lvlreq");
			}
			break;

		case 135007:
		case 135008:
			player.getRunEnergy().toggleResting();
			break;

		case 3245: // Reset XP counter
			// The client resets internal properties by itself. No
			// indication required.
			player.totalExperience = 0;
			player.sendMessage("Reset experience counter.");
			break;

		case 28173: // Toggle Global Chat
			player.disabledGlobalChat = !player.disabledGlobalChat;
			player.getSession().send(new SendSetWidgetColor(7341, player.disabledGlobalChat ? 0xFF0000 : 0xFF00));
			player.sendMessage("Global Chat: <col=8A0808>" + (player.disabledGlobalChat ? "disabled" : "enabled"));
			break;

		case 28178: // Toggle Newsfeed
			player.setNewsfeedEnabled(!player.isNewsfeedEnabled());
			player.getSession().send(new SendSetWidgetColor(7346, player.isNewsfeedEnabled() ? 0xFF00 : 0xFF0000));
			player.sendMessage("Newsfeed: <col=8A0808>" + (player.isNewsfeedEnabled() ? "enabled" : "disabled"));
			break;

		case 28174: // Toggle XP Lock
		case 59103:
			player.enabledXPLock = !player.enabledXPLock;
			player.getSession().send(new SendSetWidgetColor(7342, player.enabledXPLock ? 0xFF00 : 0xFF0000));
			player.sendMessage("XP Lock: <col=8A0808>" + (player.enabledXPLock ? "enabled" : "disabled"));
			break;

		case 55095:
			if (player.awaitResponseDestroyItemId > 0 && player.awaitResponseDestroyItemSlot >= 0) {
				int id = player.awaitResponseDestroyItemId;
				int slot = player.awaitResponseDestroyItemSlot;
				player.getInventory().remove(slot, new Item(id, player.getInventory().get(slot).getCount()));
				player.sendMessage("Destroyed item: <col=800000>" + ItemDefinition.forId(id).getName() + "</col>.");
			}

			player.getSession().send(new SendRemoveInterfacePacket());
			break;

		case 55096:
			player.getSession().send(new SendRemoveInterfacePacket());
			break;

		case 62148:
			// player.getSession().send(new SendOpenSideTabPacket(1));
			player.getSession().send(new SendSidebarInterfacePacket(2, 29001));
			break;

		case 113077:
			// player.getSession().send(new SendOpenSideTabPacket(3));
			player.getSession().send(new SendSidebarInterfacePacket(2, 638));
			break;

		case 32195:// 1
		case 32196:
			if (player.getInventory().hasItem(new Item(2996))) {
				player.getInventory().remove(new Item(2996));
				player.getSkill().addExperience(Skill.AGILITY, 2_400);
				player.sendMessage("You've bought 2,400 Agility XP.");
			} else {
				player.sendMessage("You do not have any agility arena tickets.");
			}
			break;

		case 32203:// 10
		case 32197:
			if (player.getInventory().hasItem(new Item(2996, 10))) {
				player.getInventory().remove(new Item(2996, 10));
				player.getSkill().addExperience(Skill.AGILITY, 24_800);
				player.sendMessage("You've bought 24,800 Agility XP.");
			} else {
				player.sendMessage("You do not have enough agility arena tickets.");
			}
			break;

		case 32204:// 25
		case 32198:
			if (player.getInventory().hasItem(new Item(2996, 25))) {
				player.getInventory().remove(new Item(2996, 25));
				player.getSkill().addExperience(Skill.AGILITY, 65_000);
				player.sendMessage("You've bought 65,000 Agility XP.");
			} else {
				player.sendMessage("You do not have enough agility arena tickets.");
			}
			break;

		case 32199:// 100
		case 32205:
			if (player.getInventory().hasItem(new Item(2996, 100))) {
				player.getInventory().remove(new Item(2996, 100));
				player.getSkill().addExperience(Skill.AGILITY, 280_000);
				player.sendMessage("You've bought 280,000 Agility XP.");
			} else {
				player.sendMessage("You do not have enough agility arena tickets.");
			}
			break;

		case 32200:// 1000
		case 32206:
			if (player.getInventory().hasItem(new Item(2996, 1_000))) {
				player.getInventory().remove(new Item(2996, 1_000));
				player.getSkill().addExperience(Skill.AGILITY, 3_200_000);
				player.sendMessage("You've bought 3,200,000 Agility XP.");
			} else {
				player.sendMessage("You do not have enough agility arena tickets.");
			}
			break;

		case 32192:// toadflax
		case 32190:
		case 32202:// snapdragon
		case 32201:
			player.sendMessage("This feature is currently not available.");
			break;

		case 32193:// piratehook
		case 32189:
			if (player.getInventory().hasItem(new Item(2996, 800))) {
				player.getInventory().remove(new Item(2996, 800));
				player.getInventory().add(new Item(2997, 1));
			} else {
				player.sendMessage("You do not have enough agility arena tickets.");
			}
			break;

		case 108005:
			player.getInterfaceState().openInterface(19451);
			break;

		case 83051: // Close equipment interface
		case 184163:
			player.getSession().send(new SendRemoveInterfacePacket());
			break;

		case 59100: // items kept on death
			player.getInterfaceState().openInterface(17100);
			KeepOnDeath.sendItems(player);
			break;

		case 118098:
		case 118082:
			Vengeance.castVengeance(player);
			break;

		case 234098: // attack
			player.getSI().attackComplex(1);
			player.getSI().selectedSkillId = 0;
			break;

		case 234101: // strength
			player.getSI().strengthComplex(1);
			player.getSI().selectedSkillId = 1;
			break;

		case 234104: // Defence
			player.getSI().defenceComplex(1);
			player.getSI().selectedSkillId = 2;
			break;

		case 234107: // range
			player.getSI().rangedComplex(1);
			player.getSI().selectedSkillId = 3;
			break;

		case 234110: // prayer
			player.getSI().prayerComplex(1);
			player.getSI().selectedSkillId = 4;
			break;

		case 234113: // mage
			player.getSI().magicComplex(1);
			player.getSI().selectedSkillId = 5;
			break;

		case 234116: // runecrafting
			player.getSI().runecraftingComplex(1);
			player.getSI().selectedSkillId = 6;
			break;

		case 234099: // hp
			player.getSI().hitpointsComplex(1);
			player.getSI().selectedSkillId = 7;
			break;

		case 234102: // agility
			player.getSI().agilityComplex(1);
			player.getSI().selectedSkillId = 8;
			break;

		case 234105: // herblore
			player.getSI().herbloreComplex(1);
			player.getSI().selectedSkillId = 9;
			break;

		case 234108: // theiving
			player.getSI().thievingComplex(1);
			player.getSI().selectedSkillId = 10;
			break;

		case 234111: // crafting
			player.getSI().craftingComplex(1);
			player.getSI().selectedSkillId = 11;
			break;

		case 234114: // fletching
			player.getSI().fletchingComplex(1);
			player.getSI().selectedSkillId = 12;
			break;

		case 234117:// slayer
			player.getSI().slayerComplex(1);
			player.getSI().selectedSkillId = 13;
			break;

		case 234100:// mining
			player.getSI().miningComplex(1);
			player.getSI().selectedSkillId = 14;
			break;

		case 234106: // fishing
			player.getSI().fishingComplex(1);
			player.getSI().selectedSkillId = 16;
			break;

		case 234109: // cooking
			player.getSI().cookingComplex(1);
			player.getSI().selectedSkillId = 17;
			break;

		case 234103:// smithing
			player.getSI().smithingComplex(1);
			player.getSI().selectedSkillId = 15;
			break;

		case 234112: // firemaking
			player.getSI().firemakingComplex(1);
			player.getSI().selectedSkillId = 18;
			break;

		case 234115: // woodcut
			player.getSI().woodcuttingComplex(1);
			player.getSI().selectedSkillId = 19;
			break;

		case 234118: // farming
			player.getSI().farmingComplex(1);
			player.getSI().selectedSkillId = 20;
			break;

		case 235050: // Construction
			player.getSI().constructionComplex(1);
			player.getSI().selectedSkillId = 21;
			break;

		case 235060: // Hunter
			player.getSI().hunterComplex(1);
			player.getSI().selectedSkillId = 22;
			break;

		case 34142: // tab 1
			player.getSI().menuCompilation(1);
			break;

		case 34119: // tab 2
			player.getSI().menuCompilation(2);
			break;

		case 34120: // tab 3
			player.getSI().menuCompilation(3);
			break;

		case 34123: // tab 4
			player.getSI().menuCompilation(4);
			break;

		case 34133: // tab 5
			player.getSI().menuCompilation(5);
			break;

		case 34136: // tab 6
			player.getSI().menuCompilation(6);
			break;

		case 34139: // tab 7
			player.getSI().menuCompilation(7);
			break;

		case 34155: // tab 8
			player.getSI().menuCompilation(8);
			break;

		case 34158: // tab 9
			player.getSI().menuCompilation(9);
			break;

		case 34161: // tab 10
			player.getSI().menuCompilation(10);
			break;

		case 59199: // tab 11
			player.getSI().menuCompilation(11);
			break;

		case 59202: // tab 12
			player.getSI().menuCompilation(12);
			break;
		case 59203: // tab 13
			player.getSI().menuCompilation(13);
			break;

		case 150:
		case 89061:
		case 93202:
		case 93233:
			player.isAutoRetaliateEnabled = player.isAutoRetaliateEnabled != 0 ? 0 : 1;
			player.getSession().send(new SendConfigPacket(172, player.isAutoRetaliateEnabled == 0 ? 0 : 1));
			break;

		case 9190: // 1st tele option
			if (player.dialogue().select(1)) {
				break;
			}

			if (player.dialogueAction == 106) {
				if (player.getInventory().hasItem(new Item(player.diceId))) {
					player.getInventory().remove(player.getInventory().getSlotById(player.diceId), new Item(player.diceId));
					player.getInventory().add(new Item(15086, 1));
					player.sendMessage("You get a six-sided die out of the dice bag.");
				}

				player.getSession().send(new SendRemoveInterfacePacket());
			} else if (player.dialogueAction == 107) {
				if (player.getInventory().hasItem(new Item(player.diceId, 1))) {
					player.getInventory().remove(player.getInventory().getSlotById(player.diceId), new Item(player.diceId));
					player.getInventory().add(new Item(15092, 1));
					player.sendMessage("You get a ten-sided die out of the dice bag.");
				}

				player.getSession().send(new SendRemoveInterfacePacket());
			}
			break;

		case 9191: // 2nd tele option
			if (player.dialogue().select(2)) {
				break;
			}

			if (player.dialogueAction == 106) {
				if (player.getInventory().hasItem(new Item(player.diceId))) {
					player.getInventory().remove(player.getInventory().getSlotById(player.diceId), new Item(player.diceId));
					player.getInventory().add(new Item(15088));
					player.sendMessage("You get two six-sided dice out of the dice bag.");
				}

				player.getSession().send(new SendRemoveInterfacePacket());
			} else if (player.dialogueAction == 107) {
				if (player.getInventory().hasItem(new Item(player.diceId))) {
					player.getInventory().remove(player.getInventory().getSlotById(player.diceId), new Item(player.diceId));
					player.getInventory().add(new Item(15094));
					player.sendMessage("You get a twelve-sided die out of the dice bag.");
				}

				player.getSession().send(new SendRemoveInterfacePacket());
			}
			break;

		case 9192: // 3rd tele option
			if (player.dialogue().select(3)) {
				break;
			}

			if (player.dialogueAction == 106) {
				if (player.getInventory().hasItem(new Item(player.diceId))) {
					player.getInventory().remove(player.getInventory().getSlotById(player.diceId), new Item(player.diceId));
					player.getInventory().add(new Item(15100, 1));
					player.sendMessage("You get a four-sided die out of the dice bag.");
				}

				player.getSession().send(new SendRemoveInterfacePacket());
			} else if (player.dialogueAction == 107) {
				if (player.getInventory().hasItem(new Item(player.diceId))) {
					player.getInventory().remove(player.getInventory().getSlotById(player.diceId), new Item(player.diceId));
					player.getInventory().add(new Item(15096, 1));
					player.sendMessage("You get a twenty-sided die out of the dice bag.");
				}

				player.getSession().send(new SendRemoveInterfacePacket());
			}
			break;

		// 4th tele option
		case 9193:
			if (player.dialogue().select(4)) {
				break;
			}

			if (player.dialogueAction == 106) {
				if (player.getInventory().hasItem(new Item(player.diceId))) {
					player.getInventory().remove(player.getInventory().getSlotById(player.diceId), new Item(player.diceId));
					player.getInventory().add(new Item(15090, 1));
					player.sendMessage("You get an eight-sided die out of the dice bag.");
				}

				player.getSession().send(new SendRemoveInterfacePacket());
			} else if (player.dialogueAction == 107) {
				if (player.getInventory().hasItem(new Item(player.diceId))) {
					player.getInventory().remove(player.getInventory().getSlotById(player.diceId), new Item(player.diceId));
					player.getInventory().add(new Item(15098, 1));
					player.sendMessage("You get the percentile dice out of the dice bag.");
				}

				player.getSession().send(new SendRemoveInterfacePacket());
			}
			break;

		// 5th tele option
		case 9194:
			if (player.dialogue().select(5)) {
				break;
			}
			if (player.dialogueAction == 106) {
				player.getDH().sendDialogues(107, 0);
			} else if (player.dialogueAction == 107) {
				player.getDH().sendDialogues(106, 0);
			} else if (player.dialogueAction == 411) {
				player.sendMessage("I hope you don't wimp out next time");
				player.getSession().send(new SendRemoveInterfacePacket());
			} else if (player.dialogueAction == 10 || player.dialogueAction == 11) {
				player.dialogueId++;
				player.getDH().sendDialogues(player.dialogueId, 0);
			}
			break;

		case 59004:
			player.getSession().send(new SendRemoveInterfacePacket());
			break;

		case 1093:
		case 1094:
		case 1097:
			if (player.autocastId > 0) {
				player.resetAutocast();
			} else if (player.getSpellbook() == Spellbook.ANCIENT_MAGICKS) {
				if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 4675 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15050
						|| player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15040) {
					player.getSession().send(new SendSidebarInterfacePacket(0, 1689));
				} else {
					player.sendMessage("You can't autocast ancients without an ancient staff.");
				}
			} else if (player.getSpellbook() == Spellbook.REGULAR) {
				if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 4170 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15050
						|| player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15040) {
					player.getSession().send(new SendSidebarInterfacePacket(0, 12050));
				} else {
					player.getSession().send(new SendSidebarInterfacePacket(0, 1829));
				}
			}
			break;

		case 26010:
			player.resetAutocast();
			break;

		case 9178:
			if (player.dialogue().select(1)) {
				break;
			}

			if (player.dialogueAction == 2) {
				player.castTeleport(new Teleport(new Location(3428, 3538, 0), TeleportModes.MODERN_SPELL));
			}

			if (player.dialogueAction == 3) {
				player.castTeleport(new Teleport(new Location(Constants.EDGEVILLE_X, Constants.EDGEVILLE_Y, 0), TeleportModes.MODERN_SPELL));
			}

			switch (player.dialogueAction) {

			case 51:
				if (player.getSlayer().getAssignment().equals("none")) {
					player.getDH().sendDialogues(54, player.dialogueMobId);
					return;
				}

				player.getDH().sendDialogues(55, player.dialogueMobId);
				break;

			case 13: // sara
				player.castTeleport(
						new Teleport(new Location(2900, 3617, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				break;
			}

			player.dialogueAction = -1;
			break;

		case 9179:
			if (player.dialogue().select(2)) {
				break;
			}

			switch (player.dialogueAction) {

			case 51:
				GameService.getShopManager().open(player, 57);
				break;

			case 13:
				player.castTeleport(
						new Teleport(new Location(2900, 3617, 4), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			}

			player.dialogueAction = -1;
			break;

		case 9180:
			if (player.dialogue().select(3)) {
				break;
			}

			switch (player.dialogueAction) {

			case 51:
				player.getDH().sendDialogues(58, -1);
				break;

			case 13:
				// Bandos
				player.castTeleport(
						new Teleport(new Location(2900, 3617, 8), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				break;
			}

			if (player.dialogueAction != 58) {
				player.dialogueAction = -1;
			}
			break;

		case 9181:
			if (player.dialogue().select(4)) {
				break;
			}

			switch (player.dialogueAction) {
			case 13:
				// Zamorak
				player.castTeleport(
						new Teleport(new Location(2900, 3617, 12), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				break;

			case 215:
			case 51:
				player.getSession().send(new SendRemoveInterfacePacket());
				break;
			}

			player.dialogueAction = -1;
			break;

		case 9157: // first option dialogue
			if (player.dialogue().select(1)) {
				break;
			}

			switch (player.dialogueAction) {
			case 700:
				if (!player.getInventory().contains(11286, 1540) || !player.getInventory().contains(new Item(995, 5_000_000))) {
					player.getDH().sendMobChat2("Come back when you have an anti-dragon shield, a", "draconic visage, and 5 million coins!", 747);
					player.nextChat = 0;
					return;
				}
				Container inv = player.getInventory();
				inv.remove(new Item(995, 5_000_000));
				inv.remove(new Item(11286));
				inv.remove(new Item(1540));
				inv.add(new Item(11283));
				player.sendMessage("Oziach turns your anti-dragon shield and draconic visage into a dragonfire shield!");
				player.getSession().send(new SendRemoveInterfacePacket());
				player.nextChat = 0;
				break;
			case 57:
				player.getSlayer().acceptPartnership(player.getSlayer().getRequestedBy());
				player.getSession().send(new SendRemoveInterfacePacket());
				break;

			case 58:
				player.getSlayer().getPartner().sendMessage("@red@" + player.getName() + " has ended the co-op session.");
				player.sendMessage("You ended the co-op session.");
				player.getSlayer().getPartner().getSlayer().setPartner(null);
				player.getSlayer().setPartner(null);
				player.getSession().send(new SendRemoveInterfacePacket());
				break;

			case 60:
				if (player.getSkill().getLevel(18) < 20) {
					player.getSlayer().setAssignment(null);
					player.getSlayer().setKillsLeft(0);
					player.sendMessage("Your assignment has reset at the cost of nothing, since you are new to slayer!");
				}

				else if (player.slayerPoints >= 5) {
					player.slayerPoints -= 5;
					player.getSlayer().setAssignment(null);
					player.getSlayer().setKillsLeft(0);
					player.getSession().send(new SendConfigPacket(ConfigKeys.SLAYER_POINTS, player.slayerPoints));
					player.sendMessage("Your assignment has been cancelled.");
				} else {
					player.sendMessage("You need 5 slayer points to cancel your slayer task.");
					player.sendMessage("@blu@Reminder: @bla@You can vote for 15 slayer points as a reward!");
				}

				player.getSession().send(new SendRemoveInterfacePacket());

				break;

			case 501:
				player.getBankField().setBanking(true);
				player.getBankPinHandler().getSettings().setEntered(true);
				Bank.open(player);
				break;

			case 502:
				player.getBankPinHandler().deletePin();
				player.getBankPinHandler().openSettings();
				break;

			case 503:
				player.getBankPinHandler().openChangePin(Action.CHANGE);
				break;

			case 277:
				GameService.getShopManager().open(player, 64);
				break;

			case 100:
				GameService.getShopManager().open(player, 61);
				break;

			case 2:
				if (player.getInventory().contains(2842)) {
					player.getDH().sendDialogues(9, 0);
					player.getInventory().remove(new Item(2842, 1));
					player.enterChallenge = true;
				} else {
					player.getSession().send(new SendRemoveInterfacePacket());
				}

				player.sendMessage("@red@You need a challenge scroll in your inventory to accept the challenge!");
				return;

			case 4:
				player.getDH().sendDialogues(22, 0);
				return;

			case 8:
				player.getDH().sendDialogues(23, 0);
				return;

			case 186:
				if (player.dialogueAction == 186) {
					player.getSession().send(new SendRemoveInterfacePacket());
					return;
				}
			}

			player.dialogueAction = -1;
			break;

		case 9167: // first dialogue click
			if (player.dialogue().select(1)) {
				break;
			}

			switch (player.dialogueAction) {
			case 100:
				PartyRoomManager.pullLever(player, LeverOption.DANCING_WOMEN);
				break;

			case 495:
				player.getBankField().setBanking(true);
				Bank.open(player);
				break;

			case 51:
				if (player.getSlayer().getAssignment() == null) {
					player.getDH().sendDialogues(54, player.dialogueMobId);
					return;
				}

				player.getDH().sendDialogues(55, player.dialogueMobId);
				break;

			case 8727:
				player.getDH().sendDialogues(8728, 8725);
				break;

			case 64:
				player.castTeleport(
						new Teleport(new Location(2399, 5178, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
				break;

			case 1:
				player.getDH().sendDialogues(5, 3050);
				player.sendMessage("Use a challenge scroll on Larxus to get started..");
			}

			player.dialogueAction = -1;
			break;

		case 9168: // second dialogue click
			if (player.dialogue().select(2)) {
				break;
			}

			switch (player.dialogueAction) {
			case 100:
				PartyRoomManager.pullLever(player, LeverOption.DANCING_DRUNKS);
				break;

			case 495:
				player.getBankPinHandler().openEnterPin(Action.ENTER_SETTINGS);
				break;

			case 51:
				GameService.getShopManager().open(player, 57);
				break;

			case 64:
				player.castTeleport(new Teleport(new Location(2837, 10209, 0), TeleportModes.MODERN_SPELL_2));
				player.getSession().send(new SendRemoveInterfacePacket());
				player.sendMessage("Welcome to the dicing zone! Please read the rules before dicing (::rules).. ");
				break;

			case 8727:
				GameService.getShopManager().open(player, 1);
				break;

			case 1:
				player.getDH().sendDialogues(6, 3050);
				break;

			case 2:
				player.getSession().send(new SendRemoveInterfacePacket());
				break;

			}

			player.dialogueAction = -1;
			break;

		case 9169: // third dialogue click
			if (player.dialogue().select(3)) {
				break;
			}

			switch (player.dialogueAction) {
			case 100:
				PartyRoomManager.pullLever(player, LeverOption.DROP_PARTY);
				break;

			case 495:
			case 51:
				player.getSession().send(new SendRemoveInterfacePacket());
				break;

			case 8727:
				player.getDH().sendDialogues(8729, 8725);
				break;

			case 1:
			case 64:
				player.getSession().send(new SendRemoveInterfacePacket());
			}

			player.dialogueAction = -1;
			break;

		case 9158: // fourth dialogue click
			if (player.dialogue().select(2)) {
				break;
			}

			switch (player.dialogueAction) {
			case 57:
				final Player requestedBy = player.getSlayer().getRequestedBy();
				requestedBy.sendMessage(player.getName() + " has declined your co-op request.");
				requestedBy.getSlayer().setAwaitingPartner(null);
				player.getSlayer().setRequestedBy(null);
				player.getSession().send(new SendRemoveInterfacePacket());
				break;

			case 58:
			case 60:
			case 62:
			case 700:
				player.getSession().send(new SendRemoveInterfacePacket());
				break;

			case 501:
				player.getBankField().setBanking(true);
				player.getBankPinHandler().getSettings().setEntered(true);
				Bank.open(player);
				break;

			case 502:
				player.getBankPinHandler().getSettings().reset();
				player.getDH().sendDialogues(504, 494);
				break;

			case 503:
				player.getBankPinHandler().openSettings();
				break;

			case 100:
				GameService.getShopManager().open(player, 62);
				break;

			case 277:
				player.getSession().send(new SendRemoveInterfacePacket());
				break;

			case 8:
				player.getDH().sendDialogues(24, 0);
				player.dialogueAction = 10;
				break;

			default:
				player.getSession().send(new SendRemoveInterfacePacket());
			}

			player.dialogueAction = -1;
			break;

		/** Specials **/
		case 29188:
		case 29199:
			if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
				player.sendMessage("You may not use special attacks in this duel.");
				return;
			}

			player.specBarId = 7636;
			player.usingSpecial = !player.usingSpecial;
			player.getItems().updateSpecialBar();
			break;

		case 48034:
			if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
				player.sendMessage("You may not use special attacks in this duel.");
				return;
			}

			player.specBarId = 12335;
			player.usingSpecial = !player.usingSpecial;
			player.getItems().updateSpecialBar();
			break;

		case 29163:
			if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
				player.sendMessage("You may not use special attacks in this duel.");
				return;
			}

			player.specBarId = 7611;
			player.usingSpecial = !player.usingSpecial;
			player.getItems().updateSpecialBar();
			break;

		case 29124:
			if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
				player.sendMessage("You may not use special attacks in this duel.");
				return;
			}

			player.specBarId = 7561;
			player.usingSpecial = !player.usingSpecial;
			player.getItems().updateSpecialBar();
			break;

		case 33033:
			if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
				player.sendMessage("You may not use special attacks in this duel.");
				return;
			}

			player.specBarId = 8505;
			player.usingSpecial = !player.usingSpecial;
			player.getItems().updateSpecialBar();
			break;

		case 29038:
		case 29049:
			if (player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)) {
				if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
					player.sendMessage("You may not use special attacks in this duel.");
					return;
				}

				if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 13902 || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 13904) {
					player.specBarId = 7486;
					player.usingSpecial = !player.usingSpecial;
					player.getItems().updateSpecialBar();
				} else if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 4153) {
					player.specBarId = 7486;
					player.getItems().updateSpecialBar();
					CombatExtras.handleGmaul(player);
				}
			}
			break;

		case 29063:
			if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
				player.sendMessage("You may not use special attacks in this duel.");
				return;
			}

			if (SpecialHandler.checkSpecial(player, player.getEquipment().get(Equipment.SLOT_WEAPON).getId())) {
				player.playGraphics(new Graphic(246));
				player.speak("Raarrrrrgggggghhhhhhh!");
				player.playAnimation(new Animation(1056));
				player.getSkill().setLevel(Skill.STRENGTH,
						(int) (player.getSkill().getLevelForExperience(Skill.STRENGTH) + player.getSkill().getLevelForExperience(Skill.STRENGTH) * 0.15));
				player.getItems().updateSpecialBar();
			} else {
				player.sendMessage("You don't have the required special energy to use this attack.");
			}
			break;

		case 48023:
			if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
				player.sendMessage("You may not use special attacks in this duel.");
				return;
			}

			player.specBarId = 12335;
			player.usingSpecial = !player.usingSpecial;
			player.getItems().updateSpecialBar();
			break;

		case 29138:
			if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
				player.sendMessage("You may not use special attacks in this duel.");
				return;
			}

			if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15486) {
				if (SpecialHandler.checkSpecial(player, player.getEquipment().get(Equipment.SLOT_WEAPON).getId())) {
					player.playGraphics(new Graphic(2320));
					boolean active = player.staffOfLightProtection > 0;
					player.staffOfLightProtection = 100;
					if (!active) {
						player.getTaskScheduler().schedule(new StaffOfLightTask(player));
					}
					player.playAnimation(new Animation(10518));
					SpecialHandler.reset(player);
					if (!player.specialEvent) {
						player.specialEvent = true;
						GameService.getTaskScheduler().schedule(new SpecialRestoreTask(player));
					}
					return;
				}
			}

			player.specBarId = 7586;
			player.usingSpecial = !player.usingSpecial;
			if (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 15486) {
				SpecialHandler.reset(player);
			} else {
				player.getItems().updateSpecialBar();
			}
			break;

		case 29113:
			if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
				player.sendMessage("You may not use special attacks in this duel.");
				return;
			}

			player.specBarId = 7561;
			player.usingSpecial = !player.usingSpecial;
			player.getItems().updateSpecialBar();
			break;

		case 29238:
			if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
				player.sendMessage("You may not use special attacks in this duel.");
				return;
			}

			player.specBarId = 7686;
			player.usingSpecial = !player.usingSpecial;
			player.getItems().updateSpecialBar();
			break;

		case 30108:
			if (player.getDuel().isRuleActive(DuelingRules.NO_SPECIAL_ATTACKS)) {
				player.sendMessage("You may not use special attacks in this duel.");
				return;
			}

			player.specBarId = 7812;
			player.usingSpecial = !player.usingSpecial;
			player.getItems().updateSpecialBar();
			break;

		case 4169: // god spell charge
			player.spellCasting = true;

			if (!MagicRequirements.testSpellRequirements(player, 48)) {
				break;
			}

			if (System.currentTimeMillis() - player.godSpellDelay < Constants.GOD_SPELL_CHARGE) {
				player.sendMessage("You still feel the charge in your body!");
				break;
			}

			player.godSpellDelay = System.currentTimeMillis();
			player.sendMessage("You feel charged with a magical power!");
			player.playGraphics(new Graphic(GraphicHeight.HIGH, MagicData.MAGIC_SPELLS[48][3]));
			player.playAnimation(new Animation(MagicData.MAGIC_SPELLS[48][2]));
			player.spellCasting = false;
			break;

		case 9154:
			if (player.isSkilling[Skill.AGILITY]) {
				player.sendMessage("You cannot logout while training agility.");
			} else {
				player.logout();
			}
			break;

		case 195090:
			player.getBankPinHandler().openEnterPin(Action.ENTER_SETTINGS);
			break;

		case 160048: // Toggle withdraw as note
			player.takeAsNote = !player.takeAsNote;
			break;

		case 21010:
			player.takeAsNote = true;
			break;

		case 21011:
			player.takeAsNote = false;
			break;

		// home teleports
		case 4171:
		case 50056:
		case 117048:
		case 75010:
		case 84237:
			player.castTeleport(new Location(3087, 3501, 0));
			break;

		case 50245:
		case 117210:
		case 117123:
		case 4143: // City teleports
			player.dialogue().start(new MinigameTeleportDialogue(player));
			break;

		// PvP teleport:
		case 50235: // Ancient teleportation
		case 4140: // Normal teleportation
		case 117112: // Lunar teleportation
			player.dialogue().start(new TrainingTeleportDialogue(player));
			break;

		case 50253:
		case 117131:
		case 4146: // Training teleports
			player.dialogue().start(new BossesTeleportDialogue(player));
			break;

		case 51005:
		case 4150: // Bosses
			player.dialogue().start(new SkillTeleportDialogue(player));
			break;

		case 51013:
		case 117154:
		case 6004: // pvp teleports
			player.dialogue().start(new PvPTeleportDialogue(player));
			break;

		case 51023:
		case 117162:
		case 6005: // Minigames teleports
			player.dialogue().start(new CityTeleportDialogue(player));
			break;

		case 51031:
		case 29031: // Slayer teleports
			// player.dialogue().start(new SlayerTeleportDialogue(player));
			break;

		case 72038:
		case 51039:
		case 117218:
			player.castTeleport(
					new Teleport(new Location(2787, 2786, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			break;

		case 9125: // Accurate
		case 22228: // punch (unarmed)
		case 48010:// flick (whip)
		case 14218:// pound (mace)
		case 33020:// jab (halberd)
		case 21200: // spike (pickaxe)
		case 6168: // chop (axe)
		case 8234: // stab (dagger)
		case 17102: // accurate (darts)
		case 6236: // accurate (long bow)
		case 1080: // bash (staff)
		case 6221: // range accurate
		case 30088: // claws (chop)
		case 1177: // hammer (pound)
			player.getCombat().setAttackStyle(AttackStyle.ACCURATE);

			if (player.autocasting) {
				player.resetAutocast();
			}
			break;

		// Defence
		case 22229: // block (unarmed)
		case 9126: // Defensive
		case 48008: // deflect (whip)
		case 1175: // block (hammer)
		case 21201: // block (pickaxe)
		case 14219: // block (mace)
		case 1078: // focus - block (staff)
		case 33018: // fend (hally)
		case 6169: // block (axe)
		case 8235: // block (dagger)
		case 18078: // block (spear)
		case 30089: // block (claws)
			player.getCombat().setAttackStyle(AttackStyle.DEFENSIVE);

			if (player.autocasting) {
				player.resetAutocast();
			}

			break;

		// All
		case 48009:
		case 9127: // Controlled
		case 14220: // Spike (mace)
		case 6234: // longrange (long bow)
		case 6219: // longrange
		case 18077: // lunge (spear)
		case 18080: // swipe (spear)
		case 18079: // pound (spear)
		case 17100: // longrange (darts)
			player.getCombat().setAttackStyle(AttackStyle.CONTROLLED);

			if (player.autocasting) {
				player.resetAutocast();
			}
			break;

		// Strength
		case 22230:// kick (unarmed)
		case 9128: // Aggressive
		case 14221: // Pummel(mace)
		case 33019: // Swipe(Halberd)
		case 21203: // impale (pickaxe)
		case 21202: // smash (pickaxe)
		case 6171: // hack (axe)
		case 6170: // smash (axe)
		case 6220: // range rapid
		case 8236: // slash (dagger)
		case 8237: // lunge (dagger)
		case 30090: // claws (lunge)
		case 30091: // claws (Slash)
		case 1176: // stat hammer
		case 1079: // pound (staff)
		case 6235: // rapid (long bow)
		case 17101: // repid (darts)
			player.getCombat().setAttackStyle(AttackStyle.AGGRESSIVE);

			if (player.autocasting) {
				player.resetAutocast();
			}
			break;

		case 3145:
			player.mouseButton = !player.mouseButton;
			player.getSession().send(new SendConfigPacket(500, player.mouseButton));
			player.getSession().send(new SendConfigPacket(170, player.mouseButton));
			break;

		case 3147:
			player.profanityFilter = !player.profanityFilter;
			player.getSession().send(new SendConfigPacket(915, player.profanityFilter));
			player.getSession().send(new SendConfigPacket(171, player.profanityFilter));
			break;

		case 3189:
			player.isChatSplit = !player.isChatSplit;
			player.getSession().send(new SendConfigPacket(502, player.isChatSplit));
			player.getSession().send(new SendConfigPacket(287, player.isChatSplit));
			break;

		case 48176:
			player.acceptAid = !player.acceptAid;
			player.getSession().send(new SendConfigPacket(503, player.acceptAid));
			player.getSession().send(new SendConfigPacket(427, player.acceptAid));
			break;

		case 152:
		case 153:
		case 74214:
			player.getRunEnergy().setRunning(!player.getRunEnergy().isRunning());
			player.getSession().send(new SendConfigPacket(429, player.getRunEnergy().isRunning()));
			break;

		case 3138:
			player.brightnessLevel = 1;
			break;

		case 3140:
			player.brightnessLevel = 2;
			break;

		case 3142:
			player.brightnessLevel = 3;
			break;

		case 3144:
			player.brightnessLevel = 4;
			break;

		case 74206:// area1
			player.getSession().send(new SendConfigPacket(509, true));
			player.getSession().send(new SendConfigPacket(510, false));
			player.getSession().send(new SendConfigPacket(511, false));
			player.getSession().send(new SendConfigPacket(512, false));
			break;

		case 74207:// area2
			player.getSession().send(new SendConfigPacket(509, false));
			player.getSession().send(new SendConfigPacket(510, true));
			player.getSession().send(new SendConfigPacket(511, false));
			player.getSession().send(new SendConfigPacket(512, false));
			break;

		case 74208:// area3
			player.getSession().send(new SendConfigPacket(509, false));
			player.getSession().send(new SendConfigPacket(510, false));
			player.getSession().send(new SendConfigPacket(511, true));
			player.getSession().send(new SendConfigPacket(512, false));
			break;

		case 74209:// area4
			player.getSession().send(new SendConfigPacket(509, false));
			player.getSession().send(new SendConfigPacket(510, false));
			player.getSession().send(new SendConfigPacket(511, false));
			player.getSession().send(new SendConfigPacket(512, true));
			break;

		case 24017:
			player.resetAutocast();
			player.getItems().updateCombatStylesTab(player.getEquipment().get(Equipment.SLOT_WEAPON) != null ? player.getEquipment().get(Equipment.SLOT_WEAPON).getId() : -1);
			break;
		}

		if (player.isAutoButton(buttonId)) {
			player.assignAutocast(buttonId);
		}
	}

}