package com.koios.task.impl;

import java.util.ArrayList;
import java.util.List;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.KeepOnDeath;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.activity.PestControl;
import com.koios.game.content.activity.fightpits.FightPits;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.dialogue.impl.edgeville.MysteriousCapeDialogue;
import com.koios.game.content.minigames.fightpits.FightPitsLobby;
import com.koios.game.content.questtab.QuestPages;
import com.koios.game.content.questtab.QuestTabHandler;
import com.koios.game.content.questtab.impl.PkingStatsQuestPage;
import com.koios.game.content.questtab.impl.PointsQuestPage;
import com.koios.game.content.wilderness.DeathTauntMessages;
import com.koios.game.model.Animation;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.ItemAssistant;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.ConfigKeys;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.game.model.player.io.PlayerSaver;
import com.koios.network.out.impl.AnimationResetPacket;
import com.koios.network.out.impl.SendClientScriptPacket;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendOpenSideTabPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.network.out.impl.SendSidebarInterfacePacket;
import com.koios.network.out.impl.SendStringPacket;
import com.koios.task.Task;

public class PlayerDeathTask extends Task {

	/**
	 * Add 4th item saving
	 */
	private final Player player;

	/**
	 * This flag checks if a player will keep their items on death.
	 */
	private final boolean keepItems;
    private final boolean arpgRespawn;

	/**
	 * The items this player will keep once they die.
	 */
	private List<Item> itemsKept = new ArrayList<>();

	private List<Item> itemsToDrop = new ArrayList<>();

	private int stage = 0;

	private final String killer;

	private final EntityCharacter aggressor;

	public PlayerDeathTask(Player player, EntityCharacter aggressor, boolean keepItems) {
		super(1);
		this.player = player;
        this.arpgRespawn=com.koios.game.arpg.MapRuns.inside(player)||com.koios.game.arpg.MapRuns.isHome(player)||com.koios.game.arpg.Tutorial.active(player);
        com.koios.game.arpg.MapRuns.death(player);
		killer = player.getDamage().getKillerUsername();
		this.aggressor = aggressor;
		this.keepItems = keepItems || com.koios.game.arpg.TestArena.inside(player) || com.koios.game.arpg.MapRuns.inside(player) || player.getInventory().contains(i -> i.hasRoll()) || player.getEquipment().contains(i -> i.hasRoll());
		player.setRespawnTimer(1);
		stage = 0;
		if (player.inChallenge() || player.getPrivilege().superiorTo(RegularPrivilege.ADMINISTRATOR) && !player.dropItems()) {
			itemsKept = null;
		} else {
			itemsKept = KeepOnDeath.getMostValuedItems(player);
		}
		itemsToDrop = getItems(player);
	}

	public List<Item> getItems(Player player) {

		List<Item> itemsToDrop = new ArrayList<>();

		if (player.inChallenge() || player.getPrivilege().superiorTo(RegularPrivilege.ADMINISTRATOR) && !player.dropItems()) {
			return itemsToDrop;
		}

		itemsToDrop.addAll(player.getInventory().getValidItems());
		itemsToDrop.addAll(player.getEquipment().getValidItems());

		if (itemsKept != null) {
			if (itemsKept.size() > 0) {
				for (Item keptItem : itemsKept) {
					itemsToDrop.remove(keptItem);
				}
			}
		}
		return itemsToDrop;
	}

	public void dropItems() {
        if(keepItems)return;
		
		
		if (player.isDisconnected()) {
			return;
		}
		if (itemsToDrop == null || itemsToDrop.isEmpty()) {
			return;
		}

		if (player.getController().isSafe()) {
			return;
		}

		if (player.inChallenge() || (player.getPrivilege().superiorTo(RegularPrivilege.ADMINISTRATOR) && !player.dropItems())) {
			return;
		}
		
		EntityCharacter t = GameService.getWorld().getByUsername(killer).orElse(player);

		if (t.toPlayer().getPrivilege().superiorTo(RegularPrivilege.ADMINISTRATOR) && !t.toPlayer().dropItems()) {
			t = player;
		}

		final Player opponent = t.toPlayer();
		
		player.sendMessage("Saved by the bell. " + opponent.getName());

		player.getInventory().clear();
		player.getEquipment().clear();

		for (int i = 0; i < itemsToDrop.size(); i++) {
			if (opponent != null) {
				if (!itemsToDrop.get(i).getDefinition().isUntradeable()) {
					GroundItemManager.spawnNew(opponent, itemsToDrop.get(i).getId(), player.getX(), player.getY(), itemsToDrop.get(i).getCount());
				} else {
					if (ItemAssistant.specialCase(itemsToDrop.get(i).getId())) {
						GroundItemManager.spawnNew(opponent, 995, player.getX(), player.getY(), ItemAssistant.getUntradePrice(itemsToDrop.get(i).getId()));
					} else {
						GroundItemManager.spawnNew(player, itemsToDrop.get(i));
					}
				}
			} else {
				GroundItemManager.spawnNew(player, itemsToDrop.get(i));
			}
		}

		GroundItemManager.spawnNew(player, 526, player.getX(), player.getY(), 1);

		if (itemsKept != null && itemsKept.size() > 0) {
			for (Item item : itemsKept) {
				if (item != null && item.getId() > 1) {
					player.getInventory().add(item);
				}
			}
		}
	}

	@Override
	protected void execute() {
		if (player.getHitpoints() > 0 && stage == 0) {
			player.setDead(false);
			stop();
			return;
		}

		if (player.getRespawnTimer() > 0) {
			player.setRespawnTimer(player.getRespawnTimer() - 1);
			return;
		}

		switch (stage) {
		case 0:

			player.playAnimation(new Animation(836));

			if (!player.getMinigame().inMinigame() && aggressor != null && aggressor.getClientIndex() != player.getClientIndex()) {
				Player target = aggressor == null || aggressor.isMob() ? null : aggressor.toPlayer();

				if (!aggressor.isMob() && target != null) {
					DeathTauntMessages.sendTaunt(target, player);
					AchievementManager.performed(target, Achievement.KILL_PLAYER);
					AchievementManager.performed(target, Achievement.KILL_PLAYER_ELITE);

					player.deathCount++;
					
					if (QuestTabHandler.onPage(player, QuestPages.PKING)) 
						player.getSession().send(new SendStringPacket("@or1@Death Count: @gre@" + player.deathCount, QuestTabHandler.getLineId(PkingStatsQuestPage.DEATHS)));
					
					if (QuestTabHandler.onPage(player, QuestPages.PKING)) 
						player.getSession().send(new SendStringPacket("@or1@KDR: @gre@" + player.getKDR(), QuestTabHandler.getLineId(PkingStatsQuestPage.KDR)));
					
					
					target.killCount++;
					
					if (QuestTabHandler.onPage(target, QuestPages.PKING)) 
						target.getSession().send(new SendStringPacket("@or1@Kill Count: " + (target.killCount > target.deathCount ? "@gre@" : target.killCount < target.deathCount ? "@red@" : "@yel@") + "" + target.killCount, QuestTabHandler.getLineId(PkingStatsQuestPage.KILLS)));
					
					if (QuestTabHandler.onPage(target, QuestPages.PKING)) 
						target.getSession().send(new SendStringPacket("@or1@KDR: @gre@" + target.getKDR(), QuestTabHandler.getLineId(PkingStatsQuestPage.KDR)));
					
					target.getBountyHunter().killTarget(player);
					target.getKS().increaseKillstreak(player.getName());

					if (player.inWild() && !player.inFunPK() && !FightPits.getInstance().PARTICIPANTS.contains(player)) {
						if (player.getSession().getHostAddress().equals(target.getSession().getHostAddress())) {
						} else if (target.lastKilledIPs.contains(player.getSession().getHostAddress())) {
							target.sendMessage("You need to kill " + Constants.KILLS_BEFORE_PKP_IP_GAIN + " different players before getting PvP Points again.");
						} else {
							target.addKill(player);
							target.pvpPoints += Constants.DOUBLE_PVP_POINTS ? 2 : 1;
							if (QuestTabHandler.onPage(target, QuestPages.POINTS)) {
								target.getSession().send(new SendStringPacket("@or1@Pk Points: @gre@" + target.pvpPoints, QuestTabHandler.getLineId(PointsQuestPage.PKING_POINTS)));
							}
							target.sendMessage("You have received @red@" + (Constants.DOUBLE_PVP_POINTS ? "2" : "1") + " @bla@ PvP Points!");
							player.getSession().send(new SendConfigPacket(ConfigKeys.PVP_POINTS, player.pvpPoints));

							if (target.killCount == 100) {
								target.sendMessage("@dre@You've just reached one hundred PvP kills!");

								if (MysteriousCapeDialogue.canWearCompletionistCape(target, false)) {
									GameService.getWorld().broadcast(String.format("@red@News: %s has just unlocked the completionist cape!", target.getName()));
								}
							}
						}
					}
				}
			}

			if (FightPits.getInstance().isParticipant(player)) {
				FightPits.getInstance().deathHook(player);
			}

			player.getEF().handleDeath();
			player.setFaceIndex(0, false);
			player.getSession().send(new SendRemoveInterfacePacket());
			player.getMovementHandler().reset();
			player.setLastVengeanceCast(0);
			player.berserkerActive = false;
			player.getCombat().setAttackTimer(10);
			player.setRespawnTimer(5);
			stage = 1;
			return;

		case 1:

			player.getController().onDeath(player);
			
			if (!FightPitsLobby.inside(player)) {
				if (!FightPits.getInstance().isParticipant(player) && !player.inFightCaves() && !player.inFunPK() && !player.inBarbDef() && !PestControl.isInGame(player)
						&& !player.inSW()) {
					if (!keepItems) {
						dropItems();
					}
				}
			}

			if (arpgRespawn) {
                // The private-base respawn runs after death flags and resources reset.
            } else if (player.getMinigame().inMinigame()) {
				player.getMinigame().getCurrentMinigame().handleDeath(player);

				if (player.getMinigame().inMinigame()) {
					player.setPosition(player.getMinigame().getCurrentMinigame().getRespawnLocation());
				}
			} else if (player.inFunPK()) {
				player.setPosition(2571, 3895, 0);
			} else {
				player.setPosition(player.getController().getRespawnLocation(player));
			}

			player.getCombat().setTarget(null);
			player.getCombat().setAggressor(null);
			player.getFollowing().reset();
			player.specialEnergy = 100;
			player.isSkulled = false;
			player.attackedPlayers.clear();
			player.skullIcon = player.skullTimer = -1;

			if (player.getSummoning().getFamiliar() != null) {
				player.getSummoning().getFamiliar().setDead(true);
				player.getSummoning().reset();
				player.getSession().send(new SendSidebarInterfacePacket(16, -1));
				player.getSession().send(new SendOpenSideTabPacket(4));
				player.getSession().send(new SendClientScriptPacket(1, "false-null-false"));
			}

			if (player.getEquipment().isSlotUsed(Equipment.SLOT_WEAPON)) {
				player.getItems().addSpecialBar(player.getEquipment().get(Equipment.SLOT_WEAPON).getId());
			}

			player.getDamage().reset();
			player.setFaceIndex(0, false);

			// Reset your anims
			player.playAnimation(new Animation(65535));
			player.playAnimation(new Animation(player.getStandAnim(player.getEquipment().get(Equipment.SLOT_WEAPON))));
			player.requestUpdates();

			// Reset local anims
			for (Player target : GameService.getWorld().getPlayers()) {
				if (player.distanceToPoint(target.getX(), target.getY()) <= 25) {
					target.getSession().send(new AnimationResetPacket());
					target.requestUpdates();
				}
			}

			// Reset teleblock
			player.teleBlockDuration = 0;
			player.teleBlockTime = 0;

			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			player.setDead(false);
			player.getSkill().restoreAll();
			player.getPrayers().clear();
			Combat.resetCombat(player);
            if(arpgRespawn)com.koios.game.arpg.MapRuns.respawn(player);
			PlayerSaver.save(player);
			player.setRespawnTimer(-1);
			stop();
			stage = 0;
			AchievementManager.performed(player, Achievement.DEATH_COUNT);
			return;
		}
	}

}