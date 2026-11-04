package com.koios.game.model.player;

import java.util.concurrent.TimeUnit;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.Doors;
import com.koios.game.content.HealingContent;
import com.koios.game.content.ObeliskObjectHandler;
import com.koios.game.content.PetManager;
import com.koios.game.content.WebSlash;
import com.koios.game.content.WildernessDitch;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.activity.CollectBananas;
import com.koios.game.content.activity.PestControl;
import com.koios.game.content.area.AreaManager;
import com.koios.game.content.area.InteractionType;
import com.koios.game.content.dialogue.impl.donators.GenieInPlaygroundDialogue;
import com.koios.game.content.dialogue.impl.edgeville.AliTheSmithDialogue;
import com.koios.game.content.dialogue.impl.edgeville.BobBarterDialogue;
import com.koios.game.content.dialogue.impl.edgeville.MakeoverMageDialogue;
import com.koios.game.content.dialogue.impl.edgeville.MandrithDialogue;
import com.koios.game.content.dialogue.impl.edgeville.MorrisDialogue;
import com.koios.game.content.dialogue.impl.edgeville.MysteriousCapeDialogue;
import com.koios.game.content.dialogue.impl.edgeville.NotGuideBuyCapeRejection;
import com.koios.game.content.dialogue.impl.edgeville.OracleDialogue;
import com.koios.game.content.dialogue.impl.edgeville.SigmundMerchantDialogue;
import com.koios.game.content.dialogue.impl.edgeville.SinSeerDialogue;
import com.koios.game.content.dialogue.impl.edgeville.SirAmikVarzeDialogue;
import com.koios.game.content.dialogue.impl.edgeville.TutorialDialogue;
import com.koios.game.content.dialogue.impl.edgeville.WiseOldManDialogue;
import com.koios.game.content.dialogue.impl.edgeville.XuanTitleShopDialogue;
import com.koios.game.content.dialogue.impl.misc.AncientAltarDialogue;
import com.koios.game.content.dialogue.impl.misc.BountyHunterDialogue;
import com.koios.game.content.dialogue.impl.misc.ChaosAltarDialogue;
import com.koios.game.content.dialogue.impl.misc.KilnDialogue;
import com.koios.game.content.dialogue.impl.misc.LotteryDialogue;
import com.koios.game.content.dialogue.impl.misc.PikkupstixDialogue;
import com.koios.game.content.dialogue.impl.misc.YellTagDialogue;
import com.koios.game.content.dialogue.impl.modhideout.OverseerDialogue;
import com.koios.game.content.dialogue.impl.teleportation.RunecraftTeleportDialogue;
import com.koios.game.content.dialogue.impl.util.SimpleChoice;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.content.highscores.Highscores;
import com.koios.game.content.minigames.barrows.Barrows;
import com.koios.game.content.minigames.misc.ChallengeScroll;
import com.koios.game.content.minigames.misc.TzhaarFightCave;
import com.koios.game.content.obj.CrystalChestContents;
import com.koios.game.content.partyroom.BalloonObject;
import com.koios.game.content.partyroom.PartyRoomManager;
import com.koios.game.content.randoms.RandomEvent;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.agility.Course;
import com.koios.game.content.skill.agility.Shortcut;
import com.koios.game.content.skill.agility.Shortcut.ShortcutType;
import com.koios.game.content.skill.agility.course.BarbarianCourse;
import com.koios.game.content.skill.agility.course.GnomeCourse;
import com.koios.game.content.skill.agility.course.WildernessCourse;
import com.koios.game.content.skill.agility.obstacle.Obstacle;
import com.koios.game.content.skill.crafting.TanningInterface;
import com.koios.game.content.skill.farming.Farming;
import com.koios.game.content.skill.fishing.Fishing;
import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.content.skill.hunter.impl.CatchButterfly;
import com.koios.game.content.skill.hunter.impl.CatchImpling;
import com.koios.game.content.skill.mining.Mining;
import com.koios.game.content.skill.mining.RockProspection;
import com.koios.game.content.skill.runecrafting.Runecrafting;
import com.koios.game.content.skill.smithing.SmeltingAction;
import com.koios.game.content.skill.summoning.actions.ObeliskRecharge;
import com.koios.game.content.skill.thieving.ThievingManager;
import com.koios.game.content.skill.woodcutting.Woodcutting;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.TeleportExecutor;
import com.koios.game.content.teleport.TeleportType;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.def.MobDefinition;
import com.koios.game.map.MapRegion;
import com.koios.game.model.Animation;
import com.koios.game.model.Location;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.FamiliarInventory;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.mob.impl.event.hideandseek.HNSEventActionHandler;
import com.koios.game.model.object.GameObject;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.task.PlaygroundAltarTask;
import com.koios.network.out.impl.SendObjectPacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.util.ItemIdentifiers;
import com.koios.util.NumberUtil;
import com.koios.util.TimeUtil;

public class EntityInteraction {

	public static void firstClickMob(Player player, int id) {
		Mob mob = MobManager.mobs[player.mobClickIndex];
		player.clickMobType = 0;
		player.mobClickIndex = 0;
		player.setFaceIndex(id, false);

		if (mob == null) {
			return;
		}

		if(com.koios.game.arpg.HubServices.interact(player,mob))return;

		if (player.isSuperAdministrator()) {
			System.out.println("First Click Mob: [id=" + id + ", location: " + mob.getLocation() + "]");
		}

		if (player.getMinigame().inMinigame() && player.getMinigame().getCurrentMinigame().handleMobClick(player, mob)) {
			return;
		}

		if (PetManager.pickUpPet(player, mob)) {
			return;
		}

		if (player.getSummoning().getFamiliar() != null && mob.getIndex() == player.getSummoning().getFamiliar().getIndex()) {
			player.getSummoning().handleAction("Interaction", null);
			return;
		}

		if (Fishing.isAbleToFish(player, mob.getId(), true)) {
			new Fishing(player, mob.getId(), true).start();
			return;
		}

		if (player.getSlayer().handleMaster(id)) {
			return;
		}

		for (Hunter.Impling impling : Hunter.Impling.values()) {
			if (id != impling.getMobId()) {
				continue;
			}

			player.setStopPacket(true);
			GameService.getTaskScheduler().schedule(new CatchImpling(player, mob, impling));
			return;
		}

		for (Hunter.Butterfly butterfly : Hunter.Butterfly.values()) {
			if (id != butterfly.getMobId()) {
				continue;
			}

			player.setStopPacket(true);
			GameService.getTaskScheduler().schedule(new CatchButterfly(player, mob, butterfly));
			return;
		}

		if (AreaManager.handleInteraction(player, InteractionType.FIRST_CLICK_NPC, mob.getLocation(), mob)) {
			return;
		}

		switch (id) {
		case 227: // Morris
			player.dialogue().start(new MorrisDialogue(player));
			break;

		case 3351: // Genie in Playground
			if (player.isDonator()) {
				player.dialogue().start(new GenieInPlaygroundDialogue(player));
			} else {
				player.sendMessage("He is not interested in talking to you.");
			}
			break;

		case 8104: // Hide and seek - barrel
			HNSEventActionHandler.click(player, mob);
			break;

		case 8904: // Overseer
			player.dialogue().start(new OverseerDialogue(player));
			break;

		case 2998: // Gambler
			player.getDH().sendDialogues(110, 2998);
			break;

		case 731:
			GameService.getShopManager().open(player, 49);
			break;

		case 6970:
			player.dialogue().start(new PikkupstixDialogue(player));
			break;

		case 975:
			player.dialogue().start(new BountyHunterDialogue(player));
			break;

		case 945:
			player.dialogue().start(new TutorialDialogue(player));
			break;

		case 746:
			player.dialogue().start(new OracleDialogue(player));
			break;

		case 13727:
			player.dialogue().start(new XuanTitleShopDialogue(player));
			break;

		case 804:
			TanningInterface.sendTanningInterface(player);
			break;

		case 637: // Blue partyhat
			/*
			 * The fake blue partyhat will disappear when attempted worn. How cruel!
			 *
			 * - Erik
			 */
			if (player.getInventory().contains(ItemIdentifiers.BLUE_PARTYHAT_FAKE) || player.getBank().contains(ItemIdentifiers.BLUE_PARTYHAT_FAKE)) {
				player.sendMessage("You cannot pick up any more blue partyhats.");
			} else {
				player.getInventory().add(new Item(ItemIdentifiers.BLUE_PARTYHAT_FAKE));
				mob.getLocation().setAs(new Location(0, 0));
				mob.setDead(true);
			}
			break;

		case 6524: // bob barter decant herbs
			player.dialogue().start(new BobBarterDialogue(player));
			break;

		case 494:
		case 499:
		case 8948:
		case 4375:
			player.getDH().sendDialogues(494, id);
			break;

		case 747:
			player.getDH().sendDialogues(700, id);
			break;

		case 5112:
			GameService.getShopManager().open(player, 79);
			break;

		case 904: // God Staff shop
			GameService.getShopManager().open(player, 75);
			break;

		case 608: // achievement shop
			player.dialogue().start(new SirAmikVarzeDialogue(player));
			break;

		case 2620: /* Fight Pits Point Exchange */
			player.getDH().sendDialogues(15, 2620);
			break;

		case 2258:
			player.dialogue().start(new RunecraftTeleportDialogue(player));
			break;

		case 594: // Nurmof
			player.getDH().sendDialogues(202, id);
			break;

		case 568: // Zambo
			player.getDH().sendDialogues(300, id);
			break;

		case 520:
			GameService.getShopManager().open(player, 4);
			break;

		case 554:
			GameService.getShopManager().open(player, 72);
			break;

		case 3001: // Gambler
			player.getDH().sendDialogues(169, 0);
			break;

		case 225:
			player.getDH().sendDialogues(185, 0);
			break;

		case 5447: // Agility instructor
			player.getInterfaceState().openInterface(8292);
			player.getSession().send(new SendStringPacket("Agility Ticket Exchange", 8383));
			break;

		case 1282: // Sigmund the Merchant
			player.dialogue().start(new SigmundMerchantDialogue(player));
			break;

		case 5571:
			player.dialogue().start(new SinSeerDialogue(player));
			break;

		case 2820:
			player.dialogue().start(new AliTheSmithDialogue(player));
			break;

		case 1699:
			GameService.getShopManager().open(player, 6);
			break;

		case 587:
			GameService.getShopManager().open(player, 5);
			break;

		case 2305: // Farming shop
			GameService.getShopManager().open(player, 2);
			break;

		case 8725: // Pk points shop
			player.dialogue().start(new MandrithDialogue(player));
			break;

		case 5782:
			player.dialogue().start(new YellTagDialogue(player));
			break;

		case 2169:
			player.dialogue().start(new LotteryDialogue(player));
			break;

		case 541: // Supplies shop
			GameService.getShopManager().open(player, 44);
			break;

		case 550: // Lowe range shop
			GameService.getShopManager().open(player, 46);
			break;

		case 461: // Magic shopkeeper
			GameService.getShopManager().open(player, 47);
			break;

		case 2538: // Giles pure shop
			GameService.getShopManager().open(player, 42);
			break;

		case 549: // Horvik armour shop
			player.dialogue().start(new SimpleChoice(player, "Open Melee Gear Shop", p -> GameService.getShopManager().open(player, 76), "Open Weapon Shop",
					p -> GameService.getShopManager().open(player, 43)));
			break;

		case 561: // Skilling shop
			GameService.getShopManager().open(player, 45);
			break;

		case 522: // General store
			GameService.getShopManager().open(player, 48);
			break;

		case 3789: // Pest control store
			GameService.getShopManager().open(player, 60);
			break;

		case 2566: // Skillcape shop
			player.dialogue().start(new WiseOldManDialogue(player));
			break;

		case 3050:
			player.getDH().sendDialogues(3, id);
			break;

		case 1783:
			player.getDH().sendDialogues(215, id);
			break;

		case 962:
			player.getDH().sendDialogues(76, id);
			break;

		case 364:
			player.getDH().sendDialogues(99, id);
			break;

		case 599:
			player.dialogue().start(new MakeoverMageDialogue(player));
			break;

		case 5143:
			mob.setDead(true);
			break;

		default:
			if (!GameService.isOnlineMode() && player.isSuperAdministrator()) {
				MobDefinition definition = mob.getDefinition();
				String option = definition.getAction(0).orElse("null");
				String name = definition.getName() + "(" + id + ")";
				player.sendMessage(String.format("Unknown option : '%s' at '%s' at (%d, %d)", option, name, mob.getX(), mob.getY()));
			}

			mob.setFaceIndex(-1, false);
			return;
		}

		mob.setFaceIndex(-1, false);
		RandomEvent.tryRandom(player);
	}

	public static void firstClickObject(Player player, int id, final int x, final int y) {
		player.clickObjectType = 0;
		player.setFacePosition(x, y);

		if (!GameService.isOnlineMode() && player.isSuperAdministrator()) {
			player.sendMessage("Object select option: [id: " + id + ", x: " + x + ", y: " + y + "]");
		}

		if (!MapRegion.getRegion(x, y).objectExists(id, x, y, player.getLocation().getZ()) && id != 8143 && id != 451 && id != 9320 && id != 9321 && id != 6 && id != 7
				&& id != 1766 && id != 1816 && id != 2314 && id != 10556 && id != 2878 && id != 10527 && id != 10529 && id != 2479 && id != 2482 && id != 11546 && x == 2584
				&& y == 4837) {
			if (!GameService.isOnlineMode()) {
				System.out.println("Stopped event: " + id + ", " + x + ", " + y);
			}

			// return;
		}

		if (player.getMinigame().inMinigame()
				&& player.getMinigame().getCurrentMinigame().handleObjectClick(player, new WorldObject(id, x, y, player.getLocation().getZ(), 0, 10, 0))) {
			return;
		}

		if (Farming.harvest(player, player.objectX, player.objectY)) {
			return;
		}

		if (Woodcutting.canWoodcut(player, id)) {
			new Woodcutting(player, new WorldObject(id, x, y, player.getLocation().getZ(), 0, 10, 0)).start();
			return;
		}

		if (Mining.canMine(player, id)) {
			new Mining(player, new WorldObject(id, x, y, player.getLocation().getZ(), 0, 10, 0)).start();
			return;
		}

		if (WebSlash.canSlash(player, id, Location.create(x, y, player.getLocation().getZ()))) {
			return;
		}

		if (Runecrafting.canCraftRunes(player, id)) {
			new Runecrafting(player, id).start();
			return;
		}

		Obstacle obstacle = GnomeCourse.getInstace().forData(id, player.getLocation());
		Course courseInstance = GnomeCourse.getInstace();

		if (obstacle == null) {
			courseInstance = BarbarianCourse.getInstace();
			obstacle = courseInstance.forData(id, player.getLocation());
		}

		if (obstacle == null) {
			courseInstance = WildernessCourse.getInstace();
			obstacle = courseInstance.forData(id, player.getLocation());
		}

		if (obstacle != null) {
			courseInstance.interact(player, obstacle);
			return;
		}

		if (AreaManager.handleInteraction(player, InteractionType.FIRST_CLICK_OBJECT, new Location(x, y), id)) {
			return;
		}

		Location pos = player.getLocation();

		if (id == 23271) {
			pos = Location.create(-1, pos.getY(), pos.getZ());
		}

		ShortcutType shortcutType = Shortcut.getInstace().forData(id, pos);

		if (shortcutType != null) {
			Shortcut.getInstace().interact(player, shortcutType);
			return;
		}

		if (CollectBananas.collect(player, id, Location.create(x, y, player.getLocation().getZ()))) {
			return;
		}

		if (id >= 23260 && id <= 23270) {
			WildernessDitch.startMovement(player, Location.create(x, y, player.getLocation().getZ()));
		}

		Barrows.handleObjectClick(player, id, Location.create(x, y, player.getLocation().getZ()));

		switch (id) {
		case 5107:
			player.setPosition(player.getX() > x ? 2694 : 2695, 9482, 0);
			break;
		case 5106:
			player.setPosition(player.getX() > x ? 2674 : 2676, 9479, 0);
			break;
		case 13999:
			player.setPosition(3089, 3492, 0);
			player.sendMessage("You climb out of the hole...");
			break;
		case 1765:
			player.setPosition(3069, 10257, 0);
			break;
		case 1766:
			player.setPosition(3017, 3848, 0);
			break;
		case 4879:
			if (System.currentTimeMillis() - player.lastAction < 1500) {
				return;
			}
			// prevent controlled forced teleport
			if (player.distanceToPoint(2806, 2785) < 5) {
				player.setPosition(2775, 9195, 0);
				player.lastAction = System.currentTimeMillis();
			}
			break;
		case 26193:
			PartyRoomManager.openChest(player);
			break;
		case 11742:
		case 11739:
			player.setPosition(new Location(player.getX(), player.getY(), player.getZ() == 0 ? 1 : 0));
			break;
		case 37929:
		case 38811:
			player.setPosition(player.getX() < x ? 2974 : 2970, player.getY(), player.getZ());
			break;

		case 115:
		case 116:
		case 117:
		case 118:
		case 119:
		case 120:
			WorldObject w = GameService.getObjectHandler().get(x, y, player.getLocation().getZ());

			if (w instanceof BalloonObject) {
				BalloonObject balloon = (BalloonObject) w;

				if (balloon.getItem() != null) {
					GroundItemManager.spawnNew(player, balloon.getItem().getId(), x, y, balloon.getItem().getCount());
				}

				player.playAnimation(new Animation(794));
				player.sendMessage("You step on the balloon and pop it!");
				GameService.getObjectHandler().removeObject(w);
			}
			break;

		case 26194:
			player.getDH().sendChoice("Dancing Women (500K)", "Dancing Drunks (250K)", "Drop Party (1M)");
			player.dialogueAction = 100;
			break;

		case 4039:
			player.teleport(new Location(2209, 5348));
			break;

		case 28714:
			player.teleport(new Location(2926, 3444));
			break;

		case 28716:
			player.getInterfaceState().openInterface(52700);
			break;

		case 29946:
		case 29947:
		case 29954:
		case 29953:
			ObeliskRecharge.execute(player);
			break;

		case 26439:
			player.setPosition(2885, player.getLocation().getY() < 5340 ? 5345 : 5332, 2);
			break;

		case 2273:
			player.castTeleport(new Teleport(new Location(3087, 3501, 0), TeleportModes.MODERN_SPELL_2));
			break;

		case 36778:
			player.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ() - 1);
			break;

		case 36777:
			player.dialogue()
					.start(new SimpleChoice(player, "Climb up the stairs",
							p -> p.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ() + 1), "Climb down the stairs",
							p -> p.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ() - 1)));
			break;

		case 36776:
			player.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ() + 1);
			break;

		case 2781:
		case 2966:
		case 3044:
		case 3294:
		case 3994:
		case 4304:
		case 6189:
		case 11009:
		case 11010:
		case 11546:
		case 11666:
		case 12100:
		case 12809:
		case 14921:
		case 15976:
		case 18497:
		case 18525:
		case 18526:
		case 26814:
			SmeltingAction.sendSmeltingInterface(player);
			break;

		case 172:
			if (!player.getInventory().contains(ItemIdentifiers.CRYSTAL_KEY)) {
				int unlockCount = player.getVariable("count_crystal_chest", 0);

				if (unlockCount > 0) {
					player.sendMessage("You need a crystal key to unlock this chest. You've unlocked it <col=800000>" + NumberUtil.format(unlockCount) + "</col> times.");
				} else {
					player.sendMessage("You need a crystal key to unlock this chest.");
				}
			} else if (player.getInventory().freeSlots() < 2) {
				player.sendMessage("You do not have enough inventory space to unlock this chest.");
			} else {
				player.getInventory().remove(ItemIdentifiers.CRYSTAL_KEY, 1);
				CrystalChestContents.newInstance(player).loot();
			}
			break;

		case 10781: // Donators Teleport
			if (player.isDonator()) {
				player.castTeleport(
						new Teleport(new Location(3363, 9639, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			} else {
				SimpleDialogues.sendStatement(player, "This feature is only available to donators.");
				player.nextChat = 0;
			}
			break;

		case 10803: // Nomad Chute
			SimpleDialogues.sendStatement(player, "This chute leads to the donators-only boss Nomad.");
			player.nextChat = 0;
			break;

		case 10782: // Exit Teleport
			player.castTeleport(new Teleport(new Location(Constants.EDGEVILLE_X, Constants.EDGEVILLE_Y, 0),
					player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			break;

		case 8929: // Dag lair entrance
			player.setPosition(2442, 10147, 0);
			break;
		case 10230:
			player.setPosition(2900, 4449, 0);
			break;
		case 10229:
			player.setPosition(1910, 4367, 0);
			break;
		case 153:
			if (player.isDonator()) {
				HealingContent.produceHealing(player);
			} else {
				player.sendMessage("You need to be a donator to use this ability.");
			}
			break;

		case 415:
			if (player.isDonator()) {
				HealingContent.produceHealing(player);
			} else {
				player.sendMessage("You need to be a donator to use this ability.");
			}
			break;

		case 8966: // Steps out of lair
			player.setPosition(2523, 3739, 0);
			break;

		case 9295: /* Edgeville dungeon */
			if (player.objectX == 3150 && player.objectY == 9906) {
				player.setPosition(3155, 9906, 0);
			} else if (player.objectX == 3153 && player.objectY == 9906) {
				player.setPosition(3149, 9906, 0);
			}
			break;

		case 26933:
			player.setPosition(3096, 9867, 0);
			break;

		case 32105: // Monkey bars
			if (player.getLocation().getY() == 9963) {
				player.setPosition(3120, 9970, 0);
			} else if (player.getLocation().getY() == 9970) {
				player.setPosition(3120, 9963, 0);
			}
			break;
		/* End of Edgeville Dungeon */

		case 10560:
			if (x == 3190 && y == 9758) {
				player.sendMessage("You climb up the ladder.");
				player.setPosition(3077, 3505, 0);
			}
			break;

		case 1757:
			if (x == 3578 && y == 9927) {
				player.sendMessage("This ladder does not lead anywhere...");
			}
			break;

		case 7:
		case 8:
		case 9:
			player.getDwarfMultiCannon().takeMultiCannon(GameService.getObjectHandler().get(x, y, player.getLocation().getZ()));
			break;

		case 6:
			player.getDwarfMultiCannon().shootCannon(GameService.getObjectHandler().get(x, y, player.getLocation().getZ()));
			break;

		case 3192:
			Highscores.dispatch(player, Highscores.PK_HIGHSCORES);
			break;

		case 3515:
			player.dialogue().start(new MysteriousCapeDialogue(player));
			break;

		case 4008:
			if (!player.isDonator()) {
				player.sendMessage("Cabbage-Pow!");
				player.sendMessage("You must be a donator to use the full power of this altar.");
			} else if (player.playgroundAltarCooldown == 0) {
				player.playAnimation(new Animation(645));
				player.getSkill().restoreLevel(Skill.PRAYER);
				player.getSkill().setLevel(Skill.HITPOINTS, player.getMaxHitpoints());
				player.specialEnergy = 100;

				if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
					player.getItems().addSpecialBar(player.getEquipment().get(Equipment.SLOT_WEAPON).getId());
					player.getItems().updateSpecialBar();
				}

				player.sendMessage("You have restored you special attack energy, health and prayer points.");
				player.playgroundAltarCooldown = 300;
				player.getTaskScheduler().schedule(new PlaygroundAltarTask(player));
			} else if (player.getSkill().getLevel(Skill.PRAYER) < player.getSkill().getLevelForExperience(Skill.PRAYER)) {
				player.playAnimation(new Animation(645));
				player.getSkill().restoreLevel(Skill.PRAYER);
				player.sendMessage("You restore your prayer points.");
			} else {
				player.sendMessage("You have to wait approximately " + TimeUtil.toString((1 + player.playgroundAltarCooldown) * 600L) + " before using this altar again!");
			}
			break;

		case 3444:
			if (player.getLocation().getY() == y) {
				player.setPosition(x, y - 1, player.getLocation().getZ());
			} else {
				player.sendMessage("Theres no turning back now...");
			}
			break;

		case 10556:
			if (player.enterChallenge) {
				player.enterChallenge = false;

				if (player.getEF().isEffectListed(EffectConstants.VENGEANCE_NAME)) {
					player.getEF().renewEffect(EffectConstants.VENGEANCE_NAME);
				}

				player.getPrayers().clear();
				player.getMinigame().enterMinigame(new ChallengeScroll(player, false));
			} else {
				player.sendMessage("You need to arrange a challenge with Larxus before entering the arena.");
			}
			break;

		case 3735:
			player.getDH().sendChoice("Enter Revenant cave! @red@(Dangerous)", "Do not enter cave!");
			// player.getSession().myTeleportationId = 300;
			break;

		case 4031:
			if (player.getLocation().getY() > player.objectY) {
				player.setPosition(3304, 3115, 0);
			} else if (player.getLocation().getY() < player.objectY) {
				player.setPosition(3304, 3117, 0);
			}
			break;

		case 4499: // cave entrance
			player.setPosition(2808, 10002, 0);
			break;

		case 2492:
			player.castTeleport(new Teleport(new Location(3254, 3402, 0), TeleportModes.MODERN_SPELL));
			break;

		case 8689:
			player.getDH().sendChoice("Milk cow?", "Leave?");
			player.dialogueAction = 105;
			break;

		case 2557:
			if (player.getInventory().hasItem(new Item(1523)) && player.getLocation().getX() == 3190 && player.getLocation().getY() == 3957) {
				player.setPosition(3190, 3958, 0);
			} else if (player.getInventory().hasItem(new Item(1523, 1)) && player.getLocation().getX() == 3190 && player.getLocation().getY() == 3958) {
				player.setPosition(3190, 3957, 0);
			}
			break;

		case 2971:
			if (player.getEquipment().freeSlots() > 8) {
				player.sendMessage("You must be wearing at least 6 items to enter this portal.");
				return;
			} else {
				player.setPosition(2420, 4690, 16);

			}
			break;

		case 2995:
			player.castTeleport(new Teleport(new Location(2717, 9801, 0), TeleportModes.MODERN_SPELL));
			player.sendMessage("Welcome to the dragon's cavern. Beware of fire breath!");
			break;

		case 1814:
			// ardy lever
			player.castTeleport(new Teleport(new Location(3153, 3923, 0), TeleportModes.MODERN_SPELL));
			break;

		case 2882:
		case 2883:
			if (player.objectX == 3268) {
				if (player.getLocation().getX() < player.objectX) {
					player.getMovementHandler().walkTo(1, 0);
				} else {
					player.getMovementHandler().walkTo(-1, 0);
				}
			}
			break;

		case 3489:
		case 3490:
			if (player.getLocation().getX() < player.objectX) {
				player.sendMessage("I should knock first before entering.");
			}
			break;

		case 3432:
			player.setPosition(3405, 3505, 0);
			break;

		case 272:
			player.setPosition(player.getLocation().getX(), player.getLocation().getY(), 1);
			break;

		case 273:
			player.setPosition(player.getLocation().getX(), player.getLocation().getY(), 0);
			break;

		case 245:
			player.setPosition(player.getLocation().getX(), player.getLocation().getY() + 2, 2);
			break;

		case 246:
			player.setPosition(player.getLocation().getX(), player.getLocation().getY() - 2, 1);
			break;

		case 6552:
			player.dialogue().start(new AncientAltarDialogue(player));
			break;

		case 1734:
			player.setPosition(player.getLocation().getX(), player.getLocation().getY() - 6396, 0);
			break;

		case 8959:
			if (player.getLocation().getX() == 2490 && (player.getLocation().getY() == 10146 || player.getLocation().getY() == 10148)) {
				new GameObject(6951, player.objectX, player.objectY, player.getLocation().getZ(), 1, 10, 8959, 15, false);
			}
			break;

		case 2213:
		case 14367:
		case 11758:
		case 10517:
		case 3193:
		case 5276:
		case 2693:
		case 6084:
		case 4483:
		case 26972:
		case 11402:
		case 24914:
		case 28089:
		case 25808:
		case 27663:
		case 36786:
		case 29636:
			player.getBankField().setBanking(true);
			Bank.open(player);
			break;

		case 10177:
			player.setPosition(1890, 4407, 0);
			break;

		case 2623:
			if (player.getLocation().getX() >= player.objectX) {
				player.getMovementHandler().walkTo(-1, 0);
			} else {
				player.getMovementHandler().walkTo(1, 0);
			}
			break;

		case 1599:
		case 1598:
			if (player.getLocation().getX() < player.objectX) {
				player.getMovementHandler().walkTo(1, 0);
			} else {
				player.getMovementHandler().walkTo(-1, 0);
			}
			break;

		case 14315:
			if (player.getLocation().getX() == 2657 && player.getLocation().getY() == 2639) {
				if (PestControl.addToWaitRoom(player)) {
					player.sendMessage("You board the lander.");
					player.setPosition(2661, 2639, 0);
				}
			}
			break;

		case 14314:
			if (player.inPestControlBoat()) {
				if (PestControl.leaveWaitingBoat(player)) {
					player.setPosition(2657, 2639, 0);
				}
			}
			break;

		case 14235:
		case 14233:
			if (player.objectX == 2670) {
				if (player.getLocation().getX() <= 2670) {
					player.setX(2671);
				} else {
					player.setX(2670);
				}
			} else if (player.objectX == 2643) {
				if (player.getLocation().getX() >= 2643) {
					player.setX(2642);
				} else {
					player.setX(2643);
				}
			}

			if (player.getLocation().getX() <= 2585) {
				player.getLocation().setAs(new Location(player.getLocation().getX(), player.getLocation().getY() + 1, player.getLocation().getZ()));
			} else {
				player.getLocation().setAs(new Location(player.getLocation().getX(), player.getLocation().getY() - 1, player.getLocation().getZ()));
			}

			player.setPosition(player.getLocation().getX(), player.getLocation().getY(), 0);
			break;

		case 14829:
		case 14830:
		case 14827:
		case 14828:
		case 14826:
		case 14831:
			ObeliskObjectHandler.startObelisk(id, player.getLocation().getZ());
			break;

		case 6749:
			if (x == 3562 && y == 9678) {
				player.getSession().send(new SendObjectPacket(3562, 9678, 6749, player.getZ(), -3, 0));
				player.getSession().send(new SendObjectPacket(3562, 9677, 6730, player.getZ(), -1, 0));
			} else if (x == 3558 && y == 9677) {
				player.getSession().send(new SendObjectPacket(3558, 9677, 6749, player.getZ(), -1, 0));
				player.getSession().send(new SendObjectPacket(3558, 9678, 6730, player.getZ(), -3, 0));
			}
			break;

		case 6730:
			if (x == 3558 && y == 9677) {
				player.getSession().send(new SendObjectPacket(3562, 9678, 6749, player.getZ(), -3, 0));
				player.getSession().send(new SendObjectPacket(3562, 9677, 6730, player.getZ(), -1, 0));
			} else if (x == 3558 && y == 9678) {
				player.getSession().send(new SendObjectPacket(3558, 9677, 6749, player.getZ(), -1, 0));
				player.getSession().send(new SendObjectPacket(3558, 9678, 6730, player.getZ(), -3, 0));
			}
			break;

		case 6727:
			if (x == 3551 && y == 9684) {
				player.sendMessage("You can't loot this door..");
			}
			break;

		case 6746:
			if (x == 3552 && y == 9684) {
				player.sendMessage("You can't loot this door..");
			}
			break;

		case 6748:
			if (x == 3545 && y == 9678) {
				player.getSession().send(new SendObjectPacket(3545, 9678, 6748, player.getZ(), -3, 0));
				player.getSession().send(new SendObjectPacket(3545, 9677, 6729, player.getZ(), -1, 0));
			} else if (x == 3541 && y == 9677) {
				player.getSession().send(new SendObjectPacket(3541, 9677, 6748, player.getZ(), -1, 0));
				player.getSession().send(new SendObjectPacket(3541, 9678, 6729, player.getZ(), -3, 0));
			}
			break;

		case 6729:
			if (x == 3545 && y == 9677) {
				player.getSession().send(new SendObjectPacket(3545, 9678, 6748, player.getZ(), -3, 0));
				player.getSession().send(new SendObjectPacket(3545, 9677, 6729, player.getZ(), -1, 0));
			} else if (x == 3541 && y == 9678) {
				player.getSession().send(new SendObjectPacket(3541, 9677, 6748, player.getZ(), -1, 0));
				player.getSession().send(new SendObjectPacket(3541, 9678, 6729, player.getZ(), -3, 0));
			}
			break;

		case 6726:
			if (x == 3534 && y == 9684) {
				player.getSession().send(new SendObjectPacket(3534, 9684, 6726, player.getZ(), -4, 0));
				player.getSession().send(new SendObjectPacket(3535, 9684, 6745, player.getZ(), -2, 0));
			} else if (x == 3535 && y == 9688) {
				player.getSession().send(new SendObjectPacket(3535, 9688, 6726, player.getZ(), -2, 0));
				player.getSession().send(new SendObjectPacket(3534, 9688, 6745, player.getZ(), -4, 0));
			}
			break;

		case 6745:
			if (x == 3535 && y == 9684) {
				player.getSession().send(new SendObjectPacket(3534, 9684, 6726, player.getZ(), -4, 0));
				player.getSession().send(new SendObjectPacket(3535, 9684, 6745, player.getZ(), -2, 0));
			} else if (x == 3534 && y == 9688) {
				player.getSession().send(new SendObjectPacket(3535, 9688, 6726, player.getZ(), -2, 0));
				player.getSession().send(new SendObjectPacket(3534, 9688, 6745, player.getZ(), -4, 0));
			}
			break;

		case 6743:
			if (x == 3545 && y == 9695) {
				player.getSession().send(new SendObjectPacket(3545, 9694, 6724, player.getZ(), -1, 0));
				player.getSession().send(new SendObjectPacket(3545, 9695, 6743, player.getZ(), -3, 0));
			} else if (x == 3541 && y == 9694) {
				player.getSession().send(new SendObjectPacket(3541, 9694, 6724, player.getZ(), -1, 0));
				player.getSession().send(new SendObjectPacket(3541, 9695, 6743, player.getZ(), -3, 0));
			}
			break;

		case 6724:
			if (x == 3545 && y == 9694) {
				player.getSession().send(new SendObjectPacket(3545, 9694, 6724, player.getZ(), -1, 0));
				player.getSession().send(new SendObjectPacket(3545, 9695, 6743, player.getZ(), -3, 0));
			} else if (x == 3541 && y == 9695) {
				player.getSession().send(new SendObjectPacket(3541, 9694, 6724, player.getZ(), -1, 0));
				player.getSession().send(new SendObjectPacket(3541, 9695, 6743, player.getZ(), -3, 0));
			}
			break;

		case 6707: // verac
			player.setPosition(3556, 3298, 0);
			break;

		case 9319:
			if (player.getLocation().getZ() == 0) {
				player.setPosition(player.getLocation().getX(), player.getLocation().getY(), 1);
				player.sendMessage("If the floor is invisible then set your client detail to high detail.");
			} else if (player.getLocation().getZ() == 1) {
				player.setPosition(player.getLocation().getX(), player.getLocation().getY(), 2);
			}
			break;

		case 9320:
			if (player.getLocation().getZ() == 1) {
				player.setPosition(player.getLocation().getX(), player.getLocation().getY(), 0);
			} else if (player.getLocation().getZ() == 2) {
				player.setPosition(player.getLocation().getX(), player.getLocation().getY(), 1);
			}
			break;

		case 4496:
		case 4494:
			if (player.getLocation().getZ() == 2) {
				player.setPosition(player.getLocation().getX() - 5, player.getLocation().getY(), 1);
			} else if (player.getLocation().getZ() == 1) {
				player.setPosition(player.getLocation().getX() + 5, player.getLocation().getY(), 0);
			}
			break;

		case 4493:
			if (player.getLocation().getZ() == 0) {
				player.setPosition(player.getLocation().getX() - 5, player.getLocation().getY(), 1);
			} else if (player.getLocation().getZ() == 1) {
				player.setPosition(player.getLocation().getX() + 5, player.getLocation().getY(), 2);
			}
			break;

		case 4495:
			if (player.getLocation().getZ() == 1) {
				player.setPosition(player.getLocation().getX() + 5, player.getLocation().getY(), 2);
			}
			break;

		case 5126:
			if (player.getLocation().getY() == 3554) {
				player.getMovementHandler().walkTo(0, 1);
			} else {
				player.getMovementHandler().walkTo(0, -1);
			}
			break;

		case 1759:
			if (player.objectX == 2884 && player.objectY == 3397) {
				player.setPosition(player.getLocation().getX(), player.getLocation().getY() + 6400, 0);
			}
			break;

		case 1558:
			if (player.getLocation().getX() == 3041 && player.getLocation().getY() == 10308) {
				player.setPosition(3040, 10308, 0);
			} else if (player.getLocation().getX() == 3040 && player.getLocation().getY() == 10308) {
				player.setPosition(3041, 10308, 0);
			} else if (player.getLocation().getX() == 3040 && player.getLocation().getY() == 10307) {
				player.setPosition(3041, 10307, 0);
			} else if (player.getLocation().getX() == 3041 && player.getLocation().getY() == 10307) {
				player.setPosition(3040, 10307, 0);
			} else if (player.getLocation().getX() == 3044 && player.getLocation().getY() == 10341) {
				player.setPosition(3045, 10341, 0);
			} else if (player.getLocation().getX() == 3045 && player.getLocation().getY() == 10341) {
				player.setPosition(3044, 10341, 0);
			} else if (player.getLocation().getX() == 3044 && player.getLocation().getY() == 10342) {
				player.setPosition(3045, 10342, 0);
			} else if (player.getLocation().getX() == 3045 && player.getLocation().getY() == 10342) {
				player.setPosition(3044, 10343, 0);
			} else if (player.getLocation().getY() == 9944 && (player.getLocation().getX() == 3106 || player.getLocation().getX() == 3105)) {
				player.setPosition(3106, 9945, 0);
			} else if (player.getLocation().getY() == 9945 && (player.getLocation().getX() == 3106 || player.getLocation().getX() == 3105)) {
				player.setPosition(3106, 9944, 0);
			}
			break;

		case 1557:
			if (player.getLocation().getX() == 3023 && player.getLocation().getY() == 10312) {
				player.setPosition(3022, 10312, 0);
			} else if (player.getLocation().getX() == 3022 && player.getLocation().getY() == 10312) {
				player.setPosition(3023, 10312, 0);
			} else if (player.getLocation().getX() == 3023 && player.getLocation().getY() == 10311) {
				player.setPosition(3022, 10311, 0);
			} else if (player.getLocation().getX() == 3022 && player.getLocation().getY() == 10311) {
				player.setPosition(3023, 10311, 0);
			} else if (player.getLocation().getY() == 9944 && (player.getLocation().getX() == 3106 || player.getLocation().getX() == 3105)) {
				player.setPosition(3106, 9945, 0);
			} else if (player.getLocation().getY() == 9945 && (player.getLocation().getX() == 3106 || player.getLocation().getX() == 3105)) {
				player.setPosition(3106, 9944, 0);
			}
			break;

		case 4859:
		case 411:
		case 409:
		case 29631:
			if (player.getSkill().getLevel(Skill.PRAYER) < player.getSkill().getLevelForExperience(Skill.PRAYER)) {
				player.getSkill().restoreLevel(Skill.PRAYER);
				player.playAnimation(new Animation(645));
				player.sendMessage("You recharge your prayer points.");
			} else {
				player.sendMessage("You already have full prayer points.");
			}
			break;

		case 61:
			player.dialogue().start(new ChaosAltarDialogue(player));
			break;

		case 2873:
			if (!hasGodCape(player)) {
				player.playAnimation(new Animation(645));
				player.sendMessage("Saradomin blesses you with a cape.");
				player.getInventory().add(new Item(ItemIdentifiers.SARADOMIN_CAPE));
				AchievementManager.performed(player, Achievement.OBTAIN_SARADOMIN_CAPE);
			} else {
				player.sendMessage("You already have a god cape!");
			}
			break;

		case 2875:
			if (!hasGodCape(player)) {
				player.playAnimation(new Animation(645));
				player.sendMessage("Guthix blesses you with a cape.");
				player.getInventory().add(new Item(ItemIdentifiers.GUTHIX_CAPE));
				AchievementManager.performed(player, Achievement.OBTAIN_GUTHIX_CAPE);
			} else {
				player.sendMessage("You already have a god cape!");
			}
			break;

		case 2874:
			if (!hasGodCape(player)) {
				player.playAnimation(new Animation(645));
				player.sendMessage("Zamorak blesses you with a cape.");
				player.getInventory().add(new Item(ItemIdentifiers.ZAMORAK_CAPE));
				AchievementManager.performed(player, Achievement.OBTAIN_ZAMORAK_CAPE);
			} else {
				player.sendMessage("You already have a god cape!");
			}
			break;

		case 2879:
			player.setPosition(2538, 4716, 0);
			break;

		case 2878:
			player.setPosition(2509, 4689, 0);
			break;

		case 1815:
			if (System.currentTimeMillis() - player.teleBlockTime < player.teleBlockDuration) {
				player.sendMessage("You are teleblocked and cannot teleport.");
			} else {
				player.castTeleport(new Teleport(new Location(Constants.EDGEVILLE_X, Constants.EDGEVILLE_Y, 0), TeleportModes.MODERN_SPELL_2));
			}
			break;

		case 9706:
			player.castTeleport(new Teleport(new Location(3105, 3951, 0), TeleportModes.MODERN_SPELL));
			break;

		case 9707:
			player.castTeleport(new Teleport(new Location(3105, 3956, 0), TeleportModes.MODERN_SPELL));
			break;

		case 5960: // inside mage bank lever
			player.castTeleport(new Teleport(new Location(3090, 3956, 0), TeleportModes.MODERN_SPELL_2));
			break;

		case 5959: // outside mage bank lever
			TeleportExecutor.startTeleportation(player, new Teleport(new Location(2539, 4712, 0), TeleportModes.MODERN_SPELL_2), TeleportType.NO_WILD_CHECK);
			break;

		case 2558:
			player.sendMessage("This door is locked.");
			break;

		case 9294:
			if (player.getLocation().getX() < player.objectX) {
				player.setPosition(player.objectX + 1, player.getLocation().getY(), 0);
			} else if (player.getLocation().getX() > player.objectX) {
				player.setPosition(player.objectX - 1, player.getLocation().getY(), 0);
			}
			break;

		case 9293:
			if (player.getLocation().getX() < player.objectX) {
				player.setPosition(2892, 9799, 0);
			} else {
				player.setPosition(2886, 9799, 0);
			}

			player.sendMessage("@red@Warning: Wear an anti-dragon shield to protect from the dragon's fire.");
			break;

		case 10596: // mid level dungeon
			player.setPosition(3056, 9555, 0);
			break;

		case 10595: // mid level dungeon
			player.setPosition(3056, 9562, 0);
			break;

		case 2467: // single
			if (player.isTeleporting()) {
				return;
			}

			if (System.currentTimeMillis() - player.portalDelay >= 1500) {
				player.setPosition(3087 + NumberUtil.inclusiveRandom(2), 3516 + NumberUtil.inclusiveRandom(2), 0);
				player.requestUpdates();
			}
			break;

		case 2469: // back home
			if (player.isTeleporting()) {
				return;
			}

			if (System.currentTimeMillis() - player.portalDelay >= 1500) {
				player.setPosition(3666 + NumberUtil.inclusiveRandom(1), 3486 + NumberUtil.inclusiveRandom(1), 0);
				player.requestUpdates();
			}
			break;

		case 3831: // kalphite rope down
			player.setPosition(3508, 9493, 0);
			break;

		case 3832: // kalphite rope back up
			player.setPosition(3509, 9496, 2);
			break;

		case 2158:
			if (player.objectX == 2712 && player.objectY == 4592) {
				player.setPosition(2007, 4431, 0);
			}
			break;

		case 10707:
			if (player.objectX == 2009 && player.objectY == 4431) {
				player.setPosition(2008, 4428, 1);
			}
			break;

		case 6282: // Fun PK
			if (player.isTeleporting()) {
				return;
			}

			if (System.currentTimeMillis() - player.portalDelay >= 1500) {
				if (player.objectX == 2570 && player.objectY == 3898) {
					// duel
					player.setPosition(2578, 3908, 0);
				}
			}
			break;

		case 1817:
			player.castTeleport(
					new Teleport(new Location(3067, 10253, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			break;

		case 1816:
			player.castTeleport(
					new Teleport(new Location(2273, 4680, 0), player.getSpellbook() == Spellbook.ANCIENT_MAGICKS ? TeleportModes.ANCIENT_SPELL : TeleportModes.MODERN_SPELL));
			break;

		case 9356:
			player.getMinigame().enterMinigame(new TzhaarFightCave(player, false, false));
			break;

		/**
		 * Godwars objects - will handle this in gwd class soon
		 */
		case 26384: // Bandos
			if (player.getLocation().getX() >= player.objectX) {
				player.setPosition(2850, 5333, 2);
			} else {
				player.setPosition(2851, 5333, 2);
			}
			break;

		case 26303: // Armadyl
			if (player.getLocation().getY() >= 5279) {
				player.setPosition(2871, 5269, 2);
			} else {
				player.setPosition(2871, 5279, 2);
			}
			break;

		case 26300:
			player.setPosition(2920, 5274, 0);
			break;

		case 26444: // Saradomin
			// player.setPosition(2914, 5300, 1);
			player.setPosition(2920, 5274, 0);
			break;

		case 26428: // Zamorak
			player.setPosition(2925, 5331, 2);
			break;
		/**
		 * End of gwd objects
		 */

		case 2466: // home portals
			if (player.isTeleporting()) {
				return;
			}

			if (System.currentTimeMillis() - player.portalDelay >= 1500) {
				if (player.objectX == 3450 && player.objectY == 3576) {
					// duel
					player.setPosition(3367 + NumberUtil.inclusiveRandom(2), 3267 + NumberUtil.inclusiveRandom(2), 0);
				} else if (player.objectX == 3656 && player.objectY == 3503) {
					// range/mage
					player.setPosition(2869, 9836, 0);
				} else if (player.objectX == 3666 && player.objectY == 3501) {
					// main bank
					player.setPosition(3688, 3468, 0);
				} else if (player.objectX == 3657 && player.objectY == 3485) {
					// skilling area
					player.setPosition(2831, 2973, 0);
				} else if (player.objectX == 2864 && player.objectY == 9936) {
					// town theiving
					player.setPosition(3674, 3471, 0);
				} else if (player.objectX == 2864 && player.objectY == 9937) {
					// town minigames
					player.setPosition(3676, 3477, 0);
				} else if (player.objectX == 2864 && player.objectY == 9938) {
					// town shops
					player.setPosition(3659, 3472, 0);
				} else if (player.objectX == 2864 && player.objectY == 9939) {
					// town square
					player.setPosition(3664, 3489, 0);
				} else if (player.objectX == 2864 && player.objectY == 9940) {
					// town alter
					player.setPosition(3674, 3492, 0);
				} else if (player.objectX == 3664 && player.objectY == 3506) {
					// slayer tower
					player.setPosition(3438, 3538, 0);
				} else if (player.objectX == 2864 && player.objectY == 9941) {
					// town pvm
					player.setPosition(3659, 3519, 0);
				} else if (player.objectX == 3444 && player.objectY == 3555) {
					// fish
					player.setPosition(2599, 3421, 0);
				} else if (player.objectX == 3656 && player.objectY == 3504) {
					// lowlev
					player.setPosition(2583, 9449, 0);
				} else if (player.objectX == 3656 && player.objectY == 3505) {
					// midlev
					player.setPosition(3004, 9548, 0);
				} else if (player.objectX == 3656 && player.objectY == 3506) {
					// highlev
					player.setPosition(2336, 9798, 0);
				} else if (player.objectX == 3414 && player.objectY == 3575) {
					// rev boss
					player.setPosition(3158, 9573, 0);
				} else if (player.objectX == 3086 && player.objectY == 3507) {
					// fun pk
					player.setPosition(2571, 3895, 0);
				} else if (player.objectX == 3444 && player.objectY == 3576) {
					// single pvp
					player.setPosition(3087, 3514, 0);
				} else if (player.objectX == 3448 && player.objectY == 3576) {
					// fun pk2
					player.setPosition(2571, 3895, 0);
				} else if (player.objectX == 3446 && player.objectY == 3576) {
					// multi pk
					player.setPosition(2717, 9803, 0);
				} else if (player.objectX == 3086 && player.objectY == 3509) {
					// magebank pk
					player.setPosition(2538, 4717, 0);
				} else if (player.objectX == 3446 && player.objectY == 3555) {
					// WC
					player.setPosition(2608, 4775, 0);
				} else if (player.objectX == 3440 && player.objectY == 3555) {
					// agility
					player.setPosition(2470, 3436, 0);
				} else if (player.objectX == 3442 && player.objectY == 3555) {
					// runecrafting
					player.dialogue().start(new RunecraftTeleportDialogue(player));
				} else if (player.objectX == 3448 && player.objectY == 3555) {
					// farming
					player.setPosition(2817, 3460, 0);
				} else if (player.objectX == 3412 && player.objectY == 3575) {
					// kalphite
					player.setPosition(3502, 9492, 0);
				} else if (player.objectX == 3451 && player.objectY == 3574) {
					// champions
					player.setPosition(3189, 9758, 0);
				} else if (player.objectX == 3424 && player.objectY == 3569) {
					// slayer dungeon DIS
					player.setPosition(3207, 9379, 0);
					player.sendMessage("Type ::slayerdungeon for a map and monster locations within the dungeon");
				} else if (player.objectX == 3416 && player.objectY == 3575) {
					// barrelchest
					player.setPosition(2974, 9515, 1);
				} else if (player.objectX == 2531 && player.objectY == 3031) {
					// barrows
					player.setPosition(3565, 3308, 0);
				} else if (player.objectX == 3449 && player.objectY == 3570) {
					// pc
					player.setPosition(2658, 2649, 0);
				} else if (player.objectX == 3418 && player.objectY == 3575) {
					// godwars
					player.getDH().sendDialogues(25, 0);
				} else if (player.objectX == 3671 && player.objectY == 3483) {
					// sw
					player.setPosition(2559, 4945, player.getIndex() * 4);
				} else if (player.objectX == 3413 && player.objectY == 3570) {
					// bork
					player.setPosition(3228, 3945, 0);
				} else if (player.objectX == 3415 && player.objectY == 3570) {
					// corp
					player.setPosition(2515, 4633, 0);
				} else if (player.objectX == 3417 && player.objectY == 3567) {
					// avatars, // demons
					player.setPosition(3257, 2785, 0);
				} else if (player.objectX == 3412 && player.objectY == 3571) {
					// frost dragons
					player.setPosition(2979, 3959, 0);
				} else if (player.objectX == 3658 && player.objectY == 3515) {
					// nomad
					player.setPosition(3033, 5019, 1);
				} else if (player.objectX == 3656 && player.objectY == 3515) {
					// demons
					player.setPosition(2272, 4680, 0);
				} else if (player.objectX == 3412 && player.objectY == 3573) {
					// nex
					player.setPosition(2421, 4695, 0);
				} else if (player.objectX == 3014 && player.objectY == 5199) {
					// home @ ext
					player.setPosition(3662, 3497, 0);
				} else if (player.objectX == 3014 && player.objectY == 5198) {
					// dz @ ext
					player.setPosition(2866, 9944, 0);
				} else if (player.objectX == 3034 && player.objectY == 5201) {
					// barrows
					player.setPosition(2480, 9678, 0);
				} else if (player.objectX == 3034 && player.objectY == 5202) {
					// ez crystal key
					player.setPosition(2763, 2960, 1);
				} else if (player.objectX == 3034 && player.objectY == 5200) {
					// bal'lak monsters
					player.setPosition(2590, 3159, 0);
				} else if (player.objectX == 2577 && player.objectY == 3910) {
					// bal'lak monsters
					player.setPosition(2571, 3895, 0);
				}
			}

			player.portalDelay = System.currentTimeMillis();
			break;

		default:
			Doors.clickDoor(x, y, player.getLocation().getZ());
			break;
		}
	}

	public static int getObjectDistanceRequired(int id) {
		switch (id) {

		case 26303: // Armadyl objects
		case 26300: // Sara gwd obj
			return 10;

		case 28716: // Summoning obelisk
		case 23271:
			return 2;

		case 2295:
			return 1;
		case 3735: // Revenant cave entrance
		case 2805: // Nomad cave entrance
		case 8929: // Dag lair entrance
			return 4;

		case 8966: // Stairs in Dag lair
		case 36778: // Lumbridge upper stairs
		case 36777: // Lumbridge middle stairs
		case 36776: // Lumbridge lower stairs
		case 2478:
		case 2479:
		case 2480:
		case 2481:
		case 2482:
		case 2483:
		case 2484:
		case 2485:
		case 2486:
		case 2487:
		case 2488:
		case 2489:
		case 1816:
			// Runecrafting altars
			return 3;

		default:
			return 1;
		}
	}

	public static boolean hasGodCape(Player p) {
		if (p.getInventory().contains(2412) || p.getInventory().contains(2413) || p.getInventory().contains(2414)) {
			return true;
		}

		if (p.getEquipment().contains(2412) || p.getEquipment().contains(2413) || p.getEquipment().contains(2414)) {
			return true;
		}

		if (p.getBank().contains(2412) || p.getBank().contains(2413) || p.getBank().contains(2414)) {
			return true;
		}

		return false;
	}

	public static void secondClickMob(Player player, int mobType) {
		Mob mob = MobManager.mobs[player.mobClickIndex];

		if (player.getSummoning().getFamiliar() != null && mob.getIndex() == player.getSummoning().getFamiliar().getIndex()) {
			FamiliarInventory.openBoBInventory(player);
		}

		player.clickMobType = 0;
		player.mobClickIndex = 0;

		if (player.getMinigame().inMinigame() && player.getMinigame().getCurrentMinigame().handleMobClick(player, mob)) {
			return;
		}

		RandomEvent.tryRandom(player);

		if (Fishing.isAbleToFish(player, mobType, false)) {
			new Fishing(player, mobType, false).start();
			return;
		}

		if (AreaManager.handleInteraction(player, InteractionType.SECOND_CLICK_NPC, mob.getLocation(), mob)) {
			return;
		}

		switch (mobType) {
		case 6970:
			player.dialogue().start(new PikkupstixDialogue(player));
			break;

		case 13727:
			player.getInterfaceState().openInterface(30350);
			break;
		case 4294: // Lilly
			GameService.getShopManager().open(player, 80);
			break;

		case 747: // Oziach
			GameService.getShopManager().open(player, 83);
			break;

		case 5112:
			GameService.getShopManager().open(player, 79);
			break;

		case 608:
			GameService.getShopManager().open(player, 78);
			break;

		case 945:
			if (player.cachedPlaytime < TimeUnit.DAYS.toMillis(5)) {
				player.dialogue().start(new NotGuideBuyCapeRejection(player));
			} else {
				GameService.getShopManager().open(player, 77);
			}
			break;

		case 6524:
			player.sendMessage("You can decant potions here and buy herbs from Jatix.");
			break;

		case 904: // God Staff shop
			GameService.getShopManager().open(player, 75);
			break;

		case 8725: // Pk points shop
			GameService.getShopManager().open(player, 1);
			break;

		case 2620: /* Fight Pit Points Exchange */
			GameService.getShopManager().open(player, 74);
			break;

		case 594:
			GameService.getShopManager().open(player, 73);
			break;

		case 554:
			GameService.getShopManager().open(player, 72);
			break;

		case 1783:
			GameService.getShopManager().open(player, 64);
			break;

		case 8274:
		case 1597:
		case 1598:
		case 8275:
		case 9085:
			player.getDH().sendDialogues(player.getSlayer().getAssignment() == null ? 54 : 55, mobType);
			break;

		case 568: // Zambo
			// player.getDH().sendDialogues(350, player.mobType);
			GameService.getShopManager().open(player, 61);
			break;

		case 599:
			player.getInterfaceState().openInterface(3559);
			player.canChangeAppearance = true;
			break;

		case 1699:
			GameService.getShopManager().open(player, 6);
			break;

		case 2258:
			GameService.getShopManager().open(player, 63);
			break;

		case 587:
			GameService.getShopManager().open(player, 5);
			break;

		case 2305: // Farming shop
			GameService.getShopManager().open(player, 2);
			break;

		case 541: // Supplies shop
			GameService.getShopManager().open(player, 44);
			break;

		case 550: // Lowe range shop
			GameService.getShopManager().open(player, 46);
			break;

		case 461: // Magic shopkeeper
			GameService.getShopManager().open(player, 47);
			break;

		case 2538: // Giles pure shop
			GameService.getShopManager().open(player, 42);
			break;

		case 549: // Horvik armour shop
			player.dialogue().start(new SimpleChoice(player, "Open Melee Gear Shop", p -> GameService.getShopManager().open(player, 76), "Open Weapon Shop",
					p -> GameService.getShopManager().open(player, 43)));
			break;

		case 561: // Skilling shop
			GameService.getShopManager().open(player, 45);
			break;

		case 522: // General store
			GameService.getShopManager().open(player, 48);
			break;

		case 3789: // Pest control store
			GameService.getShopManager().open(player, 60);
			break;

		case 494:
		case 499:
		case 8948:
			player.getBankField().setBanking(true);
			Bank.open(player);
			break;

		case 1:
		case 2:
		case 3:
		case 7:
		case 4:
		case 9:
		case 1714:
		case 1715:
		case 18:
		case 23:
		case 32:
		case 26:
		case 20:
		case 2234:
		case 21:
		case 34:
		case 1307:
		case 1305:
		case 1306:
		case 1311:
		case 1310:
		case 1308:
		case 1314:
			ThievingManager.initMobData(player, ThievingManager.forMobData(mobType));
		}
	}

	public static void secondClickObject(Player player, int id, int x, int y) {
		player.clickObjectType = 0;
		player.setFacePosition(x, y);

		if (player.getMinigame().inMinigame()
				&& player.getMinigame().getCurrentMinigame().handleObjectClick(player, new WorldObject(id, x, y, player.getLocation().getZ(), 0, 10, 0))) {
			return;
		}

		RandomEvent.tryRandom(player);

		/*
		 * if (!MapRegion.getRegion(x, y).objectExists(id, x, y, player.getLocation().getZ()) && (id != 36777) & (id != 36786)) { return; }
		 */

		if (Farming.inspectObject(player, player.objectX, player.objectY)) {
			return;
		}

		if (RockProspection.prospectRock(player, id)) {
			return;
		}

		if (AreaManager.handleInteraction(player, InteractionType.SECOND_CLICK_OBJECT, new Location(x, y), id)) {
			return;
		}

		switch (id) {
		case 29954: // Small obelisk - teleport to summoning training
			player.castTeleport(new Teleport(new Location(2918, 3440, 0), TeleportModes.MODERN_SPELL_2));
			break;

		case 36777:
			player.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ() + 1);
			break;

		case 10803: // Nomad chute
			player.getDH().sendChoice("Face Nomad in combat", "Stay where you are");
			// player.getSession().myTeleportationId = 305;
			break;

		case 9356:
			player.dialogue().start(new KilnDialogue(player));
			break;

		case 6:
			player.getDwarfMultiCannon().takeMultiCannon(GameService.getObjectHandler().get(x, y, player.getLocation().getZ()));
			break;

		case 61:
			player.sendMessage("The inscription is unclear.");
			break;

		case 2781:
		case 2966:
		case 3044:
		case 3294:
		case 3994:
		case 4304:
		case 6189:
		case 11009:
		case 11010:
		case 11546:
		case 11666:
		case 12100:
		case 12809:
		case 14921:
		case 15976:
		case 18497:
		case 18525:
		case 18526:
		case 26814:
			SmeltingAction.sendSmeltingInterface(player);
			break;

		case 2562: // Donator stall
			if (player.isDonator()) {
				player.getThieving().stealFromStall(995, 19_000 + NumberUtil.inclusiveRandom(2500), 120, 1);
			} else {
				player.sendMessage("@red@You must be a Donator to steal from this stall!");
				player.sendMessage("@blu@Type ::shop for more information.");
			}
			break;

		case 4875: // First stall
			player.getThieving().stealFromStall(995, 7_500 + NumberUtil.inclusiveRandom(1000), 20, 1);
			break;

		case 4876: // Second stall
			player.getThieving().stealFromStall(995, 9_500 + NumberUtil.inclusiveRandom(1000), 45, 40);
			break;

		case 4874: // Third stall
			player.getThieving().stealFromStall(995, 12_000 + NumberUtil.inclusiveRandom(1000), 75, 65);
			break;

		case 4877: // Last stall
			player.getThieving().stealFromStall(995, 15_000 + NumberUtil.inclusiveRandom(1000), 100, 80);
			break;

		case 4972:
			if (player.isTeleporting()) {
				return;
			}

			if (System.currentTimeMillis() - player.portalDelay >= 1500) {
				if (player.objectX == 2709 && player.objectY == 4591) {
					// ::waffles
					player.setPosition(2008, 4431, 0);
				}
			}
			break;

		case 10707: // ladder
			if (player.isTeleporting()) {
				return;
			}

			if (System.currentTimeMillis() - player.portalDelay >= 1500) {
				if (player.objectX == 2006 && player.objectY == 4431) {
					// xmas
					player.setPosition(2008, 4428, 1);
				} else if (player.objectX == 2009 && player.objectY == 4431) {
					// xmas 2
					player.setPosition(2008, 4428, 1);
				}
			}
			break;

		case 4031:
			player.sendMessage("Nothing interesting happens... You just look at it!");
			break;

		case 2213:
		case 14367:
		case 36786:
		case 5276:
		case 11758:
		case 10517:
		case 6084:
		case 27663:
			player.getBankField().setBanking(true);
			Bank.open(player);
			break;

		case 2558:
			if (System.currentTimeMillis() - player.lastLockPick < 3000 || !player.getEF().canWalk(player, false)) {
				break;
			}

			if (player.getInventory().hasItem(new Item(1523, 1))) {
				player.lastLockPick = System.currentTimeMillis();

				if (NumberUtil.inclusiveRandom(10) <= 3) {
					player.sendMessage("You fail to pick the lock.");
					break;
				}

				if (player.objectX == 3044 && player.objectY == 3956) {
					if (player.getLocation().getX() == 3045) {
						player.getMovementHandler().walkTo(-1, 0);
					} else if (player.getLocation().getX() == 3044) {
						player.getMovementHandler().walkTo(1, 0);
					}
				} else if (player.objectX == 3038 && player.objectY == 3956) {
					if (player.getLocation().getX() == 3037) {
						player.getMovementHandler().walkTo(1, 0);
					} else if (player.getLocation().getX() == 3038) {
						player.getMovementHandler().walkTo(-1, 0);
					}
				} else if (player.objectX == 3041 && player.objectY == 3959) {
					if (player.getLocation().getY() == 3960) {
						player.getMovementHandler().walkTo(0, -1);
					} else if (player.getLocation().getY() == 3959) {
						player.getMovementHandler().walkTo(0, 1);
					}
				}
			} else {
				player.sendMessage("I need a lockpick to pick this lock.");
			}
			break;
		}
	}

	public static void thirdClickMob(Player player, int id) {
		Mob mob = MobManager.mobs[player.mobClickIndex];

		player.clickMobType = 0;
		player.mobClickIndex = 0;

		if (player.getMinigame().inMinigame() && player.getMinigame().getCurrentMinigame().handleMobClick(player, mob)) {
			return;
		}

		RandomEvent.tryRandom(player);

		if (AreaManager.handleInteraction(player, InteractionType.THIRD_CLICK_NPC, mob.getLocation(), mob)) {
			return;
		}

		switch (id) {
		case 6970:
			player.dialogue().start(new PikkupstixDialogue(player));
			break;
		case 13727: // Xuan - clear title
			if (null == player.getTitle()) {
				player.sendMessage("You have no title to clear.");
			} else {
				player.dialogue().start(new SimpleChoice(player, "Please clear my current title!", p -> {
					player.setTitle(null);
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
					player.sendMessage("Title cleared.");
				} , "I want to keep this title.", p -> {
				}));
			}
			break;

		case 6524:
			break;

		case 8274:
		case 1597:
		case 1598:
		case 8275:
		case 9085:
			GameService.getShopManager().open(player, 57);
		}
	}

	public static void thirdClickObject(Player player, int id, int x, int y) {
		player.clickObjectType = 0;

		/*
		 * if (!MapRegion.getRegion(x, y).objectExists(id, x, y, player.getLocation().getZ()) && (id != 36778)) { return; }
		 */

		if (player.getMinigame().inMinigame()
				&& player.getMinigame().getCurrentMinigame().handleObjectClick(player, new WorldObject(id, x, y, player.getLocation().getZ(), 0, 10, 0))) {
			return;
		}

		RandomEvent.tryRandom(player);

		if (AreaManager.handleInteraction(player, InteractionType.THIRD_CLICK_OBJECT, new Location(x, y), id)) {
			return;
		}

		switch (id) {
		case 36777:
			player.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ() - 1);
			break;
		}
	}

}
