package com.koios.game.content.combat;

import java.util.Optional;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.skill.Skill;
import com.koios.game.def.MobDefinition;
import com.koios.game.map.MapLoading;
import com.koios.game.map.MapRegion;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.Bank;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;
import com.koios.task.Task;
import com.koios.util.NumberUtil;

public class DwarfMultiCannon {

	private static void startCannonballProjectile(Player player, WorldObject cannon, Mob mob) {
		final int oX = cannon.getX();
		final int oY = cannon.getY();
		final int offX = (oX - mob.getX()) * -1;
		final int offY = (oY - mob.getY()) * -1;
		CombatExtras.createPlayersProjectile(oX, oY, player.getLocation().getZ(), offY, offX, 50, 60, 53, 20, 20, mob.getProjetileIndex(), 30, 16);
	}

	public static final int CANNON = 6;
	private static final int CANNON_BARRELS = 9;
	private static final int CANNON_BARRELS_ID = 10;
	private static final int CANNON_BASE = 7;
	private static final int CANNON_BASE_ID = 6;
	private static final int CANNON_FURNACE_ID = 12;
	private static final int CANNON_STAND = 8;
	private static final int CANNON_STAND_ID = 8;
	private static final int CANNONBALL = 2;
	private final Player player;

	public DwarfMultiCannon(Player player) {
		this.player = player;
	}

	public void fireAtMob() {
		Optional<Mob> chosenTarget = getRandomMobWithinDistance(player, 8);

		if (chosenTarget.isPresent()) {
			Mob mob = chosenTarget.get();

			if (mob == null || player.oldCannon == null) {
				return;
			}
			MobDefinition definition = mob.getDefinition();

			if (!definition.isAttackable() || mob.isFamiliar()) {
				return;
			}

			if (mob.getCombat().getAggressor() != null && player.getCombat().getTarget() != null
					&& mob.getCombat().getAggressor().getIndex() != player.getCombat().getTarget().getIndex() && !player.inMulti() && !mob.inMultiCombat()) {
				return;
			}

			if (!player.getSlayer().canFight(mob)) {
				return;
			}

			if (player.inChallenge()) {
				player.sendMessage("You cannot use ranged attacks in the Champion's arena!");
				return;
			}

			if (player.inSW()) {
				player.sendMessage("You cannot use ranged attacks against the Spirit warriors!");
				return;
			}

			if (mob.isDead() || mob.getZ() != player.oldCannon.z) {
				return;
			}

			if (!mob.inMulti()) {
				return;
			}

			startCannonballProjectile(player, player.oldCannon, mob);

			GameService.getTaskScheduler().schedule(new Task(2) {

				int damage = NumberUtil.random(30);

				@Override
				public void execute() {
					if (damage > mob.hitpoints) {
						damage = mob.hitpoints;
					}

					if(damage > 0) mob.getCombat().setLastDamageReceived(System.currentTimeMillis());
					mob.getCombat().setLastHitDealt(System.currentTimeMillis());
					mob.setDamage(new Damage(damage, HitIcons.DWARF_CANNON), player);
					stop();
				}

				@Override
				public void onStop() {
					player.getSkill().addExperience(Skill.RANGED, damage * Constants.RANGE_EXP_RATE - 5000);
					player.cannonBalls--;
					AchievementManager.performed(player, Achievement.FIRE_CANNONBALLS);
					player.lastMobAttacked = mob.getIndex();
					mob.getCombat().setTarget(player);
					mob.getCombat().setAggressor(player);
					mob.getFollowing().setFollow(player, FollowType.COMBAT);
				}

			});
		}
	}

	private boolean hasCannon() {
		return player.getInventory().contains(CANNON_BASE_ID) && player.getInventory().contains(CANNON_STAND_ID) && player.getInventory().contains(CANNON_BARRELS_ID)
				&& player.getInventory().contains(CANNON_FURNACE_ID);
	}

	private boolean inBadArea() {
		return player.inBank() || player.atHome() || player.inWild() || player.isSkilling[Skill.AGILITY] || player.inMiniGame() || player.getMinigame().inMinigame()
				|| player.getCurrentSkill() != null || player.inGodwars() || player.getMinigame().inMinigameLobby();
	}

	public void loadCannon() {
		if (player.oldCannon != null) {
			player.setFacePosition(player.oldCannon.x, player.oldCannon.y);
		}

		if (player.cannonBalls == 30) {
			player.sendMessage("Your cannon cannot hold anymore cannonballs!");
		} else {
			int count = player.getInventory().getCount(CANNONBALL);
			int toAdd = count > 30 ? 30 : count;
			player.cannonBalls = Math.max(player.cannonBalls, 0);

			if (player.cannonBalls > 0) {
				toAdd -= Math.min(toAdd, player.cannonBalls);
			}

			if (toAdd <= 0) {
				player.sendMessage("You don't have any cannonballs to load the cannon.");
			} else {
				player.getInventory().remove(player.getInventory().getSlotById(CANNONBALL), new Item(CANNONBALL, toAdd));
				player.sendMessage(String.format("You load the cannon with %d cannonball%s.", toAdd, toAdd == 1 ? "" : "s"));
				player.cannonBalls += toAdd;
			}
		}
	}

	private void rotateCannon(WorldObject cannon) {
		int r = player.rotation;

		if (r == 0) {
			return;
		}

		int anim = r == 7 ? 514 : r == 8 ? 515 : 515 + r;
		GameService.getObjectManager().sendObjectAnimation(cannon.getX(), cannon.getY(), anim, 10, -1);
		fireAtMob();
	}

	public void setUpCannon() {
		if (player.getSkill().getLevel(Skill.HITPOINTS) <= 0) {
			return;
		}

		if (player.settingUpCannon) {
			player.sendMessage("You are already setting up a cannon");
			return;
		}

		if (player.hasCannon) {
			player.sendMessage("You already have a cannon set up.");
			return;
		}

		if (inBadArea()) {
			player.sendMessage("You cannot setup a cannon here!");
			return;
		}

		if (!hasCannon()) {
			player.sendMessage("You must have all of the cannon parts in your inventory to setup a cannon.");
			return;
		}

		final int xLoc = player.getX();
		final int yLoc = player.getY();
		final int zLoc = player.getZ();

		MapRegion region = MapRegion.getRegion(xLoc, yLoc);
		final int distance = 3;
		for (int x = 0; x < distance; x++) {
			for (int y = 0; y < distance; y++) {
				int toCheckX = xLoc + x;
				int toCheckY = yLoc + y;
				if (region.objectExists(toCheckX, toCheckY, zLoc)) {
					player.sendMessage("You cannot place a cannon here.");
					return;
				}
				if (region.objectExists(CANNON_BASE, toCheckX, toCheckY, zLoc) || region.objectExists(CANNON_STAND, toCheckX, toCheckY, zLoc)
						|| region.objectExists(CANNON_BARRELS, toCheckX, toCheckY, zLoc) || region.objectExists(CANNON, toCheckX, toCheckY, zLoc)) {
					player.sendMessage("This tile is already occupied by somebody's dwarf cannon.");
					return;
				}

			}
		}

		player.settingUpCannon = true;

		GameService.getTaskScheduler().schedule(new Task(2, true) {

			private int time = 4;

			@Override
			public void execute() {
				switch (time) {
				case 4:
					if (!player.getInventory().contains(CANNON_BASE_ID)) {
						player.settingUpCannon = false;
						player.setUpBase = false;
						stop();
						return;
					}

					player.settingUpCannon = true;
					player.hasCannon = true;
					player.playAnimation(new Animation(827));
					player.setUpBase = true;
					final WorldObject base = new WorldObject(CANNON_BASE, xLoc, yLoc, zLoc, 0, 10, 0);
					base.setOwner(player.getUsernameHash());
					player.cannonBaseX = player.getX();
					player.cannonBaseY = player.getY();
					player.cannonBaseH = player.getZ();
					GameService.getObjectHandler().addObject(base);
					GameService.getObjectHandler().placeObject(base);
					MapLoading.addObject(false, CANNON_BASE, xLoc, yLoc, zLoc, 10, 0);
					player.oldCannon = base;
					player.getInventory().remove(new Item(CANNON_BASE_ID, 1));
					base.owner = player.getUsernameHash();
					break;

				case 3:
					if (!player.getInventory().contains(CANNON_STAND_ID)) {
						player.settingUpCannon = false;
						player.setUpStand = false;
						stop();
						return;
					}

					player.playAnimation(new Animation(827));
					player.setUpStand = true;
					final WorldObject stand = new WorldObject(CANNON_STAND, xLoc, yLoc, zLoc, 0, 10, 0);
					stand.setOwner(player.getUsernameHash());
					GameService.getObjectHandler().removeObject(player.oldCannon);
					GameService.getObjectHandler().addObject(stand);
					GameService.getObjectHandler().placeObject(stand);
					MapLoading.addObject(false, CANNON_STAND, xLoc, yLoc, zLoc, 10, 0);
					player.oldCannon = stand;
					player.getInventory().remove(new Item(CANNON_STAND_ID, 1));
					stand.owner = player.getUsernameHash();
					break;

				case 2:
					if (!player.getInventory().contains(CANNON_BARRELS_ID)) {
						player.settingUpCannon = false;
						player.setUpBarrels = false;
						stop();
						return;
					}

					player.playAnimation(new Animation(827));
					player.setUpBarrels = true;
					final WorldObject barrel = new WorldObject(CANNON_BARRELS, xLoc, yLoc, zLoc, 0, 10, 0);
					barrel.setOwner(player.getUsernameHash());
					GameService.getObjectHandler().removeObject(player.oldCannon);
					GameService.getObjectHandler().addObject(barrel);
					GameService.getObjectHandler().placeObject(barrel);
					MapLoading.addObject(false, CANNON_BARRELS, xLoc, yLoc, zLoc, 10, 0);
					player.oldCannon = barrel;
					player.getInventory().remove(new Item(CANNON_BARRELS_ID, 1));
					barrel.owner = player.getUsernameHash();
					break;

				case 1:
					if (!player.getInventory().contains(CANNON_FURNACE_ID)) {
						player.settingUpCannon = false;
						player.setUpFurnace = false;
						stop();
						return;
					}

					player.playAnimation(new Animation(827));
					player.setUpFurnace = true;
					final WorldObject cannon = new WorldObject(CANNON, xLoc, yLoc, zLoc, 0, 10, 0);
					cannon.setOwner(player.getUsernameHash());
					GameService.getObjectHandler().removeObject(player.oldCannon);
					GameService.getObjectHandler().addObject(cannon);
					GameService.getObjectHandler().placeObject(cannon);
					MapLoading.addObject(false, CANNON, xLoc, yLoc, zLoc, 10, 0);
					player.oldCannon = cannon;
					player.getInventory().remove(new Item(CANNON_FURNACE_ID, 1));
					cannon.owner = player.getUsernameHash();
					break;

				case 0:
					player.settingUpCannon = false;
					stop();
					break;
				}

				if (time > 0) {
					time--;
				}
			}

		});
	}

	public void shootCannon(WorldObject cannon) {
		if (cannon == null) {
			return;
		}

		if (cannon.owner != player.getUsernameHash()) {
			player.sendMessage("This is not your cannon.");
			return;
		}

		if (player.cannonIsShooting || player.cannonBalls < 1) {
			loadCannon();
		} else {
			startFiringCannon(cannon);
		}
	}

	private void startFiringCannon(final WorldObject cannon) {
		player.setFacePosition(cannon.x, cannon.y);
		player.cannonIsShooting = true;

		GameService.getTaskScheduler().schedule(new Task(player.inMulti() ? 1 : 2) {

			@Override
			public void execute() {
				if (player.cannonBalls < 1) {
					player.sendMessage("Your cannon is out of ammo!");
					player.cannonIsShooting = false;
					stop();
				} else {
					player.rotation++;
					rotateCannon(cannon);

					if (player.rotation == 8) {
						player.rotation = 0;
					}
				}
			}

		});
	}

	public void takeMultiCannon(WorldObject cannon) {
		if (player.settingUpCannon) {
			player.sendMessage("You cannot pick up your cannon yet!");
			return;
		}

		if (cannon == null) {
			return;
		}
		if (cannon.owner != player.getUsernameHash()) {
			player.sendMessage("This is not your cannon!");
			return;
		}

		player.setFacePosition(cannon.x, cannon.y);
		player.playAnimation(new Animation(827));
		final WorldObject empty = new WorldObject(100, cannon.getX(), cannon.getY(), cannon.getZ(), 0, 10, 0);
		GameService.getObjectHandler().addObject(empty);
		GameService.getObjectHandler().placeObject(empty);
		GameService.getObjectHandler().removeObject(empty);
		MapRegion.getRegion(cannon.getX(), cannon.getY()).removeObject(cannon.getX(), cannon.getY(), cannon.getZ());

		if (player.getInventory().freeSlots() < 4) {
			if (player.setUpBase) {
				Bank.addToBank(player, new Item(CANNON_BASE_ID));
			}

			if (player.setUpStand) {
				Bank.addToBank(player, new Item(CANNON_STAND_ID));
			}

			if (player.setUpBarrels) {
				Bank.addToBank(player, new Item(CANNON_BARRELS_ID));
			}

			if (player.setUpFurnace) {
				Bank.addToBank(player, new Item(CANNON_FURNACE_ID));
			}

			if (player.cannonBalls > 0) {
				Bank.addToBank(player, new Item(CANNONBALL, player.cannonBalls));
			}

			player.sendMessage("You did not have enough inventory space, so the cannon was banked.");
		} else {

			if (player.setUpBase)
				player.getInventory().add(new Item(CANNON_BASE_ID));
			if (player.setUpStand)
				player.getInventory().add(new Item(CANNON_STAND_ID));
			if (player.setUpBarrels)
				player.getInventory().add(new Item(CANNON_BARRELS_ID));
			if (player.setUpFurnace)
				player.getInventory().add(new Item(CANNON_FURNACE_ID));

			if (player.cannonBalls > 0) {
				player.getInventory().add(new Item(CANNONBALL, player.cannonBalls));
			}
		}

		player.setUpBase = false;
		player.setUpStand = false;
		player.setUpBarrels = false;
		player.setUpFurnace = false;
		player.cannonBalls = 0;
		player.settingUpCannon = false;
		player.hasCannon = false;
	}

	public Optional<Mob> getRandomMobWithinDistance(Player player, int distance) {
		final int myX = player.cannonBaseX;
		final int myY = player.cannonBaseY;

		for (Mob mob : MobManager.mobs) {
			if (mob == null || mob.isFamiliar()) {
				continue;
			}

			if (player.isWithinDistance(mob, distance)) {
				if (mob.isDead() || mob.getZ() != player.getLocation().getZ() || !mob.isAttackable()) {
					continue;
				}

				final int theirX = mob.getX();
				final int theirY = mob.getY();

				switch (player.rotation) {
				case 1: // north
					if (theirY > myY && theirX >= myX - 1 && theirX <= myX + 1) {
						return Optional.of(mob);
					}
					break;

				case 2: // north-east
					if (theirX >= myX + 1 && theirY >= myY + 1) {
						return Optional.of(mob);
					}
					break;

				case 3: // east
					if (theirX > myX && theirY >= myY - 1 && theirY <= myY + 1) {
						return Optional.of(mob);
					}
					break;

				case 4: // south-east
					if (theirY <= myY - 1 && theirX >= myX + 1) {
						return Optional.of(mob);
					}
					break;

				case 5: // south
					if (theirY < myY && theirX >= myX - 1 && theirX <= myX + 1) {
						return Optional.of(mob);
					}
					break;

				case 6: // south-west
					if (theirX <= myX - 1 && theirY <= myY - 1) {
						return Optional.of(mob);
					}
					break;

				case 7: // west
					if (theirX < myX && theirY >= myY - 1 && theirY <= myY + 1) {
						return Optional.of(mob);
					}
					break;

				case 8: // north-west
					if (theirX <= myX - 1 && theirY >= myY + 1) {
						return Optional.of(mob);
					}
				}
			}
		}

		return Optional.empty();
	}

}
