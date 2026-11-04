package com.koios.game.content.skill.summoning;

import com.koios.game.content.PetManager;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.combat.melee.MeleeRequirements;
import com.koios.game.content.dialogue.impl.misc.FamiliarInteraction;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.summoning.SummoningData.ScrollsData;
import com.koios.game.content.skill.summoning.SummoningData.SummonPouch;
import com.koios.game.content.skill.summoning.actions.SummonFamiliar;
import com.koios.game.content.skill.summoning.specials.ScrollManager;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.Container;
import com.koios.game.model.item.container.FamiliarInventory;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.impl.MobFamiliar;
import com.koios.game.model.mob.impl.MobPet;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendMobHeadOnInterfacePacket;
import com.koios.network.out.impl.SendOpenSideTabPacket;
import com.koios.network.out.impl.SendSidebarInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;

public class Summoning {

	private final Player player;
	private MobFamiliar familiar;
	private MobPet pet;
	private int remaining_ticks = 0;
	private int special_points = 0;
	private long special_delay;
	private int familiar_item = 0;
	private Container familiar_inventory = null;

	public Summoning(Player player) {
		this.player = player;
	}

	public MobFamiliar getFamiliar() {
		return familiar;
	}

	public MobPet getPet() {
		return pet;
	}

	public int getRemainingTicks() {
		return remaining_ticks;
	}

	public int getSpecialPoints() {
		return special_points;
	}

	public long getSpecialDelay() {
		return special_delay;
	}

	public int getFamiliarItem() {
		return familiar_item;
	}

	public boolean hasFollower() {
		return familiar != null || pet != null;
	}

	public Container getFamiliarInventory() {
		return familiar_inventory;
	}

	public void setFamiliarInventory(Container container) {
		familiar_inventory = container;
	}

	public void setFamiliar(MobFamiliar familiar) {
		this.familiar = familiar;
	}

	public void setPet(MobPet pet) {
		this.pet = pet;
	}

	public void setRemainingTicks(int i) {
		if (i < 0) {
			i = 0;
		}

		remaining_ticks = i;
	}

	public void setSpecialPoints(int i) {
		if (i < 0) {
			i = 0;
		}

		if (i > 100) {
			i = 100;
		}

		special_points = i;
	}

	public void setSpecialDelay(long l) {
		special_delay = l;
	}

	public void setFamiliarItem(int i) {
		familiar_item = i;
	}

	public boolean handleAction(String action, Object[] object) {
		switch (action) {
		case "Summon Familiar":
			if (familiar != null || pet != null) {
				player.sendMessage("You already have a familiar.");
				return false;
			}

			SummonPouch pouch = SummonPouch.get((int) object[0]);

			if (pouch == null) {
				return false;
			}

			if (player.getSkill().getLevelForExperience(Skill.SUMMONING) < pouch.getLevelRequired()) {
				player.sendMessage("You need a summoning level of " + pouch.getLevelRequired() + " to summon this.");
				return false;
			}

			if (player.getSkill().getLevel(Skill.SUMMONING) < pouch.getPointsReq()) {
				player.sendMessage("You don't have enough summoning points to summon this.");
				return false;
			}

			SummonFamiliar.execute(player, (int) object[0], false);
			return true;

		case "Duration Over":
			player.sendMessage((boolean) object[0] ? "Your familiar duration is over and he vanishes..." : "You run out of summoning points and your familiar vanishes...");

			player.getSummoning().getFamiliar().setDead(true);
			reset();
			player.getSession().send(new SendSidebarInterfacePacket(16, -1));
			player.getSession().send(new SendOpenSideTabPacket(4));
			return true;

		case "Cast Scroll":
			ScrollsData scroll = ScrollsData.get(familiar.getPouchDefinitions().getScrollId());

			if (scroll == null) {
				player.sendMessage("This familiar's special move is currently unavaliable.");
				return false;
			}

			if (player.getSkill().getLevelForExperience(Skill.SUMMONING) < scroll.getLevelReq()) {
				player.sendMessage("You need a summoning level of " + scroll.getLevelReq() + " to activate this.");
				return false;
			}

			if (!player.getInventory().contains(scroll.getScrollId())) {
				player.sendMessage("You need a " + new Item(familiar.getPouchDefinitions().getScrollId()).getDefinition().getName() + " scroll to activate this.");
				return false;
			}

			if (System.currentTimeMillis() - player.getSummoning().getSpecialDelay() < scroll.getSpecialDelay() * 1000) {
				player.sendMessage("You must wait a few seconds before casting this scroll again.");
				return false;
			}

			if (special_points < scroll.getSpecRequired()) {
				player.sendMessage("You don't have enought special points to cast this scroll.");
				return false;
			}

			player.getSummoning().setSpecialDelay(System.currentTimeMillis());
			ScrollManager.castScroll(player, scroll, object);
			return true;

		case "Attack":
			EntityCharacter target = (EntityCharacter) object[0];

			if (target == null || familiar == null) {
				return false;
			}

			if (target.getIndex() == familiar.getIndex()) {
				return false;
			}

			if (!target.inMulti() || !familiar.inMulti() || !player.inMulti()) {
				player.sendMessage("You must be in a multi area to do this!");
				return false;
			}

			if (target.isPlayer()) {
				Player t = target.toPlayer();

				int combatDif1 = MeleeRequirements.getCombatDifference(player.getSkill().getCombatLevel(false), t.getSkill().getCombatLevel(false));

				if (combatDif1 > player.wildLevel || combatDif1 > t.wildLevel) {
					player.setFaceIndex(target.getIndex(), target.isPlayer());
					player.sendMessage("Your combat level difference is too great to attack that player here.");
					player.getMovementHandler().reset();
					Combat.resetCombat(player);
					return false;
				}
			}

			familiar.getCombat().setTarget(target);
			familiar.getFollowing().setFollow(target, FollowType.COMBAT);
			return true;

		case "Withdraw all":
			if (getFamiliarInventory() != null) {
				FamiliarInventory.withdrawAll(player);
			} else {
				player.sendMessage("Your familiar can't store any items.");
			}

			return true;

		case "Interaction":
			if (familiar == null) {
				return false;
			}

			player.dialogue().start(new FamiliarInteraction(player));
			return true;

		default:
			return false;
		}
	}

	public void sendTab(boolean b) {
		if (familiar != null) {
			player.getSession().send(new SendStringPacket(familiar.getDefinition().getName(), 18028));
			player.getSession().send(new SendStringPacket(getScrollCount() + "", 18024));
			player.getSession().send(new SendStringPacket(player.getSkill().getLevel(Skill.SUMMONING) + "/" + player.getSkill().getLevelForExperience(Skill.SUMMONING), 18045));
			player.getSession().send(new SendMobHeadOnInterfacePacket(familiar.getId(), 18021));

			if (b) {
				player.getSession().send(new SendSidebarInterfacePacket(16, 18017));
				player.getSession().send(new SendOpenSideTabPacket(16));
			}

			String time = "" + (double) remaining_ticks / 2;
			time = time.replace(".5", ":30");
			time = time.replace(".0", ":00");

			if (time.length() < 5) {
				time = "0" + time;
			}

			player.getSession().send(new SendStringPacket(time, 18043));
		} else if (pet != null) {
			player.getSession().send(new SendStringPacket("" + pet.getStage() + "%", 19030)); // Stage
			player.getSession().send(new SendStringPacket("" + pet.getHunger() + "%", 19032)); // Hunger
		}
	}

	public int getScrollCount() {
		if (familiar == null) {
			return 0;
		}

		return player.getInventory().getCount(familiar.getPouchDefinitions().getScrollId());
	}

	public void reset() {
		remaining_ticks = 0;
		familiar = null;
		pet = null;
		familiar_item = 0;
		special_points = 0;
		special_delay = 0;

		player.getSession().send(new SendStringPacket("", 19021));
		player.getSession().send(new SendStringPacket("0", 18024));
		player.getSession().send(new SendStringPacket("00:00", 18043));
		player.getSession().send(new SendStringPacket("", 18028));
		player.getSession().send(new SendStringPacket(player.getSkill().getLevel(Skill.SUMMONING) + "/" + player.getSkill().getLevelForExperience(Skill.SUMMONING), 18045));
	}

	public void handleLogout() {
		Mob mob = familiar == null ? pet : familiar;

		if (mob == null) {
			return;
		}

		mob.remove();
	}

	public void handleLogin() {
		if (!PetManager.spawnPet(player, player.getSummoning().getFamiliarItem(), -1, true)) {
			SummonFamiliar.execute(player, player.getSummoning().getFamiliarItem(), true);
		}
	}

}