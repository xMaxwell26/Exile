package com.koios.game.content.command.impl;

import java.text.DecimalFormat;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.combat.CombatVariables.AttackType;
import com.koios.game.content.combat.util.CalculateMaxHit;
import com.koios.game.content.command.Command;
import com.koios.game.content.dialogue.impl.util.SimpleChoice;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.Location;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;
import com.koios.game.model.player.Privileges.Privilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.game.model.player.task.SkullTimerTask;
import com.koios.util.TimeUtil;

public class PlayerCommands implements Command {

	@SuppressWarnings("unused")
	private final int TRADE_X = 3158;
	@SuppressWarnings("unused")
	private final int TRADE_Y = 3486;

	@Override
	public void execute(Player player, String command) {
		String[] args = command.split(" ");

		switch (args[0]) {
        case "prototype":
         com.koios.game.arpg.PrototypePanel.show(player,"guide");break;
        case "passivefree":
         com.koios.game.arpg.PrototypePanel.handle(player,"arpgproto free");break;
        case "base":
         com.koios.game.arpg.MapRuns.home(player);break;
        case "arena":
         if(GameService.isOnlineMode())return;
         if(args.length>1&&args[1].equals("leave"))com.koios.game.arpg.TestArena.leave(player);
         else if(args.length>1&&args[1].equals("pause"))com.koios.game.arpg.TestArena.pause(player);
         else com.koios.game.arpg.TestArena.enter(player);
         break;
        case "arpgarmour":
         if(GameService.isOnlineMode())return;
         if(args.length!=2){player.sendMessage("Use ::arpgarmour armour/evasion/es/hybrid");return;}
         if(!player.getInventory().add(com.koios.game.arpg.PrototypeItems.armour(args[1])))player.sendMessage("Inventory full.");break;
        case "arpgcombat":
         if(GameService.isOnlineMode())return;
         boolean enabled=!player.getAttribute("arpg-combat-log",false);player.setAttribute("arpg-combat-log",enabled);player.sendMessage("ARPG combat log: "+enabled);break;
        case "arpgsword":
         if(GameService.isOnlineMode())return;
         if(!player.getInventory().add(com.koios.game.arpg.PrototypeItems.sword(new java.util.Random())))player.sendMessage("Inventory full.");
         else player.sendMessage("Rolled sword created. Use ::arpgitem <inventory slot 1-28>.");
         break;
        case "arpgitem":
         if(GameService.isOnlineMode())return;
         if(args.length!=2){player.sendMessage("Usage: ::arpgitem <slot 1-28>");return;}
         com.koios.game.model.item.Item inspected=player.getInventory().get(Integer.parseInt(args[1])-1);
         if(inspected==null || !inspected.hasRoll()){player.sendMessage("No rolled item in that slot.");return;}
         player.sendMessage(inspected.getRoll().getRarity()+" ID "+inspected.getRoll().getInstanceId());
         if(inspected.getRoll().isIdentified())for(com.koios.game.arpg.ItemRoll.Affix a:inspected.getRoll().getAffixes())player.sendMessage(a.toString());
         else player.sendMessage("Unidentified");
         break;
        case "arpgstats":
         if(GameService.isOnlineMode())return;
         java.util.Map<com.koios.game.arpg.Stat,Double> snapshot=com.koios.game.arpg.ArpgStats.equipment(player.getEquipment().toArray());
         for(java.util.Map.Entry<com.koios.game.arpg.Stat,Double> e:snapshot.entrySet())if(e.getValue()!=0)player.sendMessage(e.getKey()+": "+String.format(java.util.Locale.ROOT,"%.2f",e.getValue()));
         break;
		case "timeonline":
			long playTime = player.cachedPlaytime + System.currentTimeMillis() - player.loginTimeMillis;
			String asDuration = TimeUtil.toString(playTime);
			player.sendMessage("Total online time: " + asDuration + ".");
			break;

		case "commands":
            player.sendMessage("Exile: ::guide, ::maps, ::craft, ::passives and ::help.");
            com.koios.game.arpg.PrototypePanel.handle(player, "arpgproto guide");
            break;
        case "guide":
        case "guides":
        case "help":
        case "info":
            com.koios.game.arpg.PrototypePanel.handle(player, "arpgproto guide");
            break;
        case "maps":
            com.koios.game.arpg.PrototypePanel.handle(player, "arpgproto maps");
            break;
        case "craft":
            com.koios.game.arpg.PrototypePanel.handle(player, "arpgproto craft");
            break;
        case "passives":
            com.koios.game.arpg.PrototypePanel.handle(player, "arpgproto tree");
            break;
        // Preserve familiar legacy aliases as local help, never browser/SQL calls.
        case "auth":
        case "vote":
        case "register":
        case "advertise":
        case "rules":
        case "forums":
        case "updates":
        case "shop":
        case "donate":
        case "website":
        case "hiscores":
            player.sendMessage("Exile prototype services are available in game.");
            com.koios.game.arpg.PrototypePanel.handle(player, "arpgproto guide");
            break;

		case "maxhit":
			player.sendMessage("Melee max hit: " + CalculateMaxHit.execute(player, AttackType.MELEE));
			player.sendMessage("Ranged max hit: " + CalculateMaxHit.execute(player, AttackType.RANGE));
			player.sendMessage("Magic max hit: " + CalculateMaxHit.execute(player, AttackType.MAGIC));
			break;

		case "train":
			player.castTeleport(
					new Teleport(new Location(2322, 3803, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			break;

		case "dice":
			player.castTeleport(
					new Teleport(new Location(2837, 10209, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			break;

		case "donatorzone":
		case "dzone":
			if (player.isDonator() || player.isExtremeDonator()) {
				// player.getAssistant().spellTeleport(2392, 9903, 0);
				player.castTeleport(
						new Teleport(new Location(3363, 9639, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else {
				player.sendMessage("");
				player.sendMessage("This feature is only available to those who have donated to " + Constants.SERVER_NAME + ".");
				player.sendMessage("Thinking about donating to help " + Constants.SERVER_NAME + " stay online? View the ::shop!");
				player.sendMessage("");
				player.sendMessage("~ Love from the " + Constants.SERVER_NAME + " Development Team ~");
			}
			break;

		case "home":
			player.castTeleport(new Teleport(new Location(3087, 3492, 0), TeleportModes.MODERN_SPELL));
			break;

		case "trade":
			player.castTeleport(new Location(2837, 10209, 0));
			break;

		case "yellkills":
			player.speak("I have killed " + player.killCount + " amount of players.");
			break;
		case "yelldeaths":
			player.speak("I have died " + player.deathCount + " times against players.");
			break;
		case "kdr":
		case "yellkdr":
			if (player.deathCount == 0) {
				player.sendMessage("You must have atleast one death to get kill/death ratio.");
				return;
			}
			player.speak("My kill/death ratio is: " + new DecimalFormat(".#").format(player.killCount / player.deathCount) + ".");
			break;

		case "getkills":
			player.sendMessage("You have killed " + player.killCount + " amount of players.");
			break;
		case "getdeaths":
			player.sendMessage("You have died " + player.deathCount + " times against players.");
			break;
		case "getkdr":
			if (player.deathCount == 0) {
				player.sendMessage("You must have atleast one death to get kill/death ratio.");
				return;
			}

			player.sendMessage("Your kill/death ratio is: " + new DecimalFormat(".#").format(player.killCount / player.deathCount) + ".");
			break;

		case "claim":
			// Overflow prototype rewards are local; the legacy donation backend is retired.
            com.koios.game.arpg.PrototypePanel.handle(player, "arpgproto claim");
			break;

		case "players":
			player.sendMessage("There are currently " + GameService.getWorld().getPlayerCount() + " players playing " + Constants.SERVER_NAME + ".");
			if (player.getPrivilege().equals(RegularPrivilege.DEVELOPER)) {
				for (Player p : GameService.getWorld().getPlayers()) {
					player.sendMessage(p.getName() + ", rights: " + p.getPrivilege() + ", location: " + p.getLocation());
				}
			}
			break;

		case "skull":
			if (player.isSkulled) {
				player.sendMessage("You are already skulled!");
				return;
			}

			player.skullIcon = 0;
			player.skullTimer = Constants.SKULL_TIMER;
			player.getTaskScheduler().schedule(new SkullTimerTask(player));
			player.isSkulled = true;
			player.requestUpdates();
			player.sendMessage("You are now skulled. Skull timer -> " + Constants.SKULL_TIMER / 2 / 60 + " minutes.");
			break;

		case "empty":
			player.dialogue().start(new SimpleChoice(player, "Clear inventory", p -> {
				player.getInventory().clear();
				player.sendMessage("Your inventory has been wiped!");
			} , "No! Don't do that.", p -> p.sendMessage("Cancelled.")));
			break;
		}
	}

	@Override
	public Privilege getMinimumPrivilege() {
		return RegularPrivilege.NONE;
	}

	@Override
	public boolean testPermissions(Player player) {
		return true;
	}

}