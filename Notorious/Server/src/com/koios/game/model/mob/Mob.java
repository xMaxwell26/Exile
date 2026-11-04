package com.koios.game.model.mob;

import java.util.Optional;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.cluescroll.ClueScroll;
import com.koios.game.content.cluescroll.ClueScrollConstants;
import com.koios.game.content.cluescroll.CoordinateScroll;
import com.koios.game.content.cluescroll.EmoteScroll;
import com.koios.game.content.cluescroll.ClueScrollConstants.Difficulty;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.combat.mob.Godwars;
import com.koios.game.content.following.Following;
import com.koios.game.content.following.MobFollowing;
import com.koios.game.content.following.Following.FollowType;
import com.koios.game.content.minigames.barrows.Barrows;
import com.koios.game.content.minigames.barrows.BarrowsData;
import com.koios.game.content.minigames.misc.TzhaarFightCaveConstants;
import com.koios.game.content.minigames.rfd.GlovesDialogue;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.Prayer.BoneDefinition;
import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.content.skill.hunter.Trap;
import com.koios.game.content.skill.hunter.TrapProcessor;
import com.koios.game.content.skill.hunter.impl.BoxTrap;
import com.koios.game.content.skill.hunter.impl.NetTrap;
import com.koios.game.content.skill.hunter.impl.Snare;
import com.koios.game.def.ItemDefinition;
import com.koios.game.def.MobDefinition;
import com.koios.game.map.MapRegion;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Location;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.movement.MobMovementHandler;
import com.koios.game.model.movement.MovementHandler;
import com.koios.game.model.player.Player;
import com.koios.task.impl.MobDeathTask;
import com.koios.util.NumberUtil;

/**
 * Represents a single mob registered to the world
 *
 * @author Timothy Howard <Arithium>
 */
public class Mob extends EntityCharacter {

	protected static Hunter.Capturable getCapturable(int id) {
		Hunter.Capturable capturable = Hunter.SnareTrapped.getForMob(id);

		if (capturable != null) {
			return capturable;
		}

		capturable = Hunter.BoxTrapped.getForMob(id);

		if (capturable != null) {
			return capturable;
		}

		capturable = Hunter.NetTrapped.getForMob(id);

		if (capturable != null) {
			return capturable;
		}

		return null;
	}

	/**
	 * @param id The data value of the new mob.
	 * @param location The spawn position
	 */
	public static Mob newGenericMob(int id, Location location) {
		Mob mob = new Mob(id, location);
		return mob;
	}

	public int actionTimer, deathStage;
	private long aggressive_timer;
	private int attack;
	public final Hunter.Capturable capturable;
	public boolean deathTask = false;
	public int defence;
	public int direction = -1;
	public int endGfx;
	private final Following following = new MobFollowing(this);
	public int hitpoints;
	public Location homeLocation = new Location(0, 0, 0);
	private int id;
	public int index;
	public boolean inTask = false;
	private int interactionDistance = 1;
	private int maxHit;
	public int maximumHitpoints;
	private final MovementHandler mov = new MobMovementHandler(this);
	public int projectileId;
	public int runDirection = -1;
	private Skill skill;
	public EntityCharacter summoner;
	public int transformId = -1;
	public Trap trap;
	public boolean walkingHome;
	private int walkingType;

	public Mob(int index, int id, Location location) {
		this(id, location);
		setSkill(new Skill(this, getAttack(), getDefence(), hitpoints));
		this.index = index;
	}

	/**
	 * Only use with {@link MobManager#spawn(Mob)} so {@code index} is corrected.
	 */
	protected Mob(int id, Location location) {
		this.id = id;
		MobDefinition definition = MobDefinition.get(id);
		hitpoints = definition.getHitpoints();
		maximumHitpoints = definition.getHitpoints();
		capturable = getCapturable(id);
		getLocation().setAs(location);
		homeLocation = location;
		Walking.setMobOnTile(this, true);
		mov.resetMoveDirections();
	}

	@Override
	public Mob toMob() {
		return this;
	}

	public Mob copy() {
		Mob mob = new Mob(id, homeLocation);
		mob.internalCopy(this);
		return mob;
	}

	/**
	 * Called when the mob died to a player.
	 */
	@Override
	public void death() {
		Player player = null;

		if (summoner != null && summoner.isPlayer()) {
			player = summoner.toPlayer();
		}

		if (player == null) {
			Optional<Player> optionalPlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

			if (optionalPlayer.isPresent()) {
				player = optionalPlayer.get();
			}
		}

		if (player == null) {
			return;
		}

		player.getFollowing().reset();

		getFollowing().reset();
		removeNpcFromTile();

		// Drop loot
		if (!isFamiliar() && !isFromMinigame()) {
			dropItems(player);
			dropCharms(player);
			dropClues(player);
		}

		Barrows.handleBrotherDeath(player, this);
		Godwars.increaseKillCount(player, getId());

		switch (getId()) {
		case 1264:
			if (ClueScrollConstants.playerHasScroll(player)) {
				for (Item item : player.getInventory().toArray()) {

					if (item == null) {
						continue;
					}

					ClueScroll scroll = ClueScrollConstants.getClueScroll(item.getId());

					if (scroll == null || !(scroll instanceof CoordinateScroll)) {
						continue;
					}
					player.getInventory().remove(new Item(scroll.getItem()));
				}
			}
			drop(ClueScrollConstants.CASKET_HARD);
			player.setAttribute("KILL_AGENT", false);
			break;
		case 5143:
			player.setAttribute("KILL_AGENT", false);
			break;
		case 3493:
		case 3494:
		case 3495:
			player.dialogue().start(new GlovesDialogue(player, 4));
			break;
		case 3496:
			player.dialogue().start(new GlovesDialogue(player, 5));
			break;
		case 3491:
			player.dialogue().start(new GlovesDialogue(player, 6));
			break;
		/*
		 * case 3062:// imp MobManager.spawnSecondChallenge(index); break;
		 *
		 * case 3065:// skeleton MobManager.spawnThirdChallenge(index); break;
		 *
		 * case 3066:// zombie MobManager.spawnFourthChallenge(index); break;
		 *
		 * case 3058:// giant MobManager.spawnFifthChallenge(index); break;
		 *
		 * case 8986:// banshee MobManager.spawnSixthChallenge(index); break;
		 *
		 * case 3059:// ghoul MobManager.spawnSeventhChallenge(index); break;
		 *
		 * case 7078:// ogress MobManager.spawnEighthChallenge(index); break;
		 *
		 * case 8987:// abberant MobManager.spawnNineChallenge(index); break;
		 *
		 * case 3057:// earth MobManager.spawnTenthChallenge(index); break;
		 *
		 * case 8989:// mummy MobManager.spawnEleventhChallenge(index); break;
		 *
		 * case 3064:// demon MobManager.spawnFinalChallenge(index); break;
		 *
		 * case 3067:// leon MobManager.wonChallenge(index); break;
		 */

		case 1558:
		case 1951:
		case 1952:
		case 1953:
		case 1954:
		case 1955:
		case 1956:
		case 6052:
			AchievementManager.performed(player, Achievement.KILL_ICE_WOLF);
			break;

		case 1265:
			AchievementManager.performed(player, Achievement.ROCK_CRABS);
			break;

		case 4688:
			AchievementManager.performed(player, Achievement.KILL_MOSS_GIANTS);
			break;

		case 6688:
			AchievementManager.performed(player, Achievement.REVENANT_HELLHOUND);
			break;

		case 50:
			AchievementManager.performed(player, Achievement.KILL_KBD);
			break;

		case 1590:
			AchievementManager.performed(player, Achievement.KILL_BRONZE_DRAGONS);
			break;

		case 1596:
			AchievementManager.performed(player, Achievement.KILL_ICE_WOLF);
			break;

		case 6248:
		case 6250:
		case 6252:
			AchievementManager.performed(player, Achievement.KILL_SARADOMIN_MINIONS);
			break;

		case 6261:
		case 6263:
		case 6265:
			AchievementManager.performed(player, Achievement.KILL_BANDOS_MINIONS);
			break;

		case 6204:
		case 6206:
		case 6208:
			AchievementManager.performed(player, Achievement.KILL_ZAMORAK_MINIONS);
			break;

		case 1615:
			AchievementManager.performed(player, Achievement.SLAY_ABYSSAL);
			break;

		case 6260:
			AchievementManager.performed(player, Achievement.DEFEAT_BANDOS);
			break;

		case 6222:
			AchievementManager.performed(player, Achievement.DEFEAT_ARMADYL);
			break;

		case 6247:
			AchievementManager.performed(player, Achievement.DEFEAT_SARADOMIN);
			break;

		case 6203:
			AchievementManager.performed(player, Achievement.DEFEAT_ZAMORAK);
			break;

		case 2783:
			AchievementManager.performed(player, Achievement.DARK_BEASTS);
			break;

		case 8133:
			AchievementManager.performed(player, Achievement.CORPOREAL_BEAST);
			AchievementManager.performed(player, Achievement.CORPOREAL_BEAST_ELITE);
			break;

		case 8351:
			AchievementManager.performed(player, Achievement.KILL_TORMENTED_DEMONS);
			break;

		case 6225:
		case 6227:
		case 6223:
			AchievementManager.performed(player, Achievement.KILL_ARAMDYL_MINIONS);
			break;

		case 6279:
			AchievementManager.performed(player, Achievement.REVENANT_ORK);
			break;

		case 6691:
			AchievementManager.performed(player, Achievement.REVENANT_DARK_BEAST);
			break;

		case 6689:
			AchievementManager.performed(player, Achievement.REVENANT_DEMON);
			break;

		case 6730:
			AchievementManager.performed(player, Achievement.REVENANT_KNIGHT);
			break;

		case 1459:
			AchievementManager.performed(player, Achievement.KILL_MONKEY_GUARD);
			break;

		case 10773:
			AchievementManager.performed(player, Achievement.KILL_FROST_DRAGONS);
			break;

		case 7133:
			AchievementManager.performed(player, Achievement.DEFEAT_BORK);
			break;

		case 3200:
			AchievementManager.performed(player, Achievement.KILL_CHAOS_ELEMENTAL);
			break;

		case 2881:
			AchievementManager.performed(player, Achievement.KILL_DAG_SUPREME);
			break;

		case 2882:
			AchievementManager.performed(player, Achievement.KILL_DAG_PRIME);
			break;

		case 2883:
			AchievementManager.performed(player, Achievement.KILL_DAG_REX);
			break;

		case 1158:
			AchievementManager.performed(player, Achievement.DEFEAT_KQ);
			break;

		case 5666:
			AchievementManager.performed(player, Achievement.DEFEAT_BARRELCHEST);
			break;

		case 5145:
			if (ClueScrollConstants.playerHasScroll(player)) {
				for (Item item : player.getInventory().toArray()) {
					if (item == null) {
						continue;
					}

					ClueScroll scroll = ClueScrollConstants.getClueScroll(item.getId());

					if (scroll != null && scroll instanceof EmoteScroll) {
						((EmoteScroll) scroll).onAgentDeath(player);
						return;
					}
				}
			}
		}

		player.getSlayer().mobDeathHook(this);
	}

	public void drop(int id) {
		drop(id, 1);
	}

	public void drop(int id, int amount) {
		if (amount < 1) {
			return;
		}

		Optional<Player> maybePlayer = GameService.getWorld().getByUsername(getDamage().getKillerUsername());

		if (!maybePlayer.isPresent()) {
			return;
		}

		Player player = maybePlayer.get();

		// if (!GameService.isOnlineMode() && player.isSuperAdministrator())
		// {
		// return;
		// }

		if (player.getInventory().contains(18337)) {
			BoneDefinition boneDefinition = BoneDefinition.forId(id);

			if (boneDefinition != null) {
				player.getSkill().addExperience(Skill.PRAYER, boneDefinition.getExperience() * Constants.PRAYER_EXPERIENCE);
				return;
			}
		}

		if (ItemDefinition.forId(id).isStackable() || amount == 1) {
			GroundItemManager.spawnNew(player, id, getX(), getY(), amount);
		} else {
			for (; amount >= 1; amount--) {
				GroundItemManager.spawnNew(player, id, getX(), getY(), 1);
			}
		}
	}

	public void dropCharms(Player player) {
		if (player.getRandom().nextInt(100) < 30) {
			int charmAmount = NumberUtil.inclusiveRandom((int) (0.01 * getDefinition().getCombatLevel()));
			int charmId = 12158 + NumberUtil.inclusiveRandom((int) (0.05 * getDefinition().getCombatLevel()));

			if (charmId > 12160) {
				charmId = 12163;
			}

			if (charmAmount < 1) {
				charmAmount = 1;
			}

			if (charmAmount > 10) {
				charmAmount = 10;
			}

			drop(charmId, charmAmount);
		}
	}

	public void dropClues(Player player) {
		MobDefinition def = getDefinition();
		double rand = Math.random();
		double chance = 1 - 1 / 175.0;

		if (rand >= chance) {
			int level = def == null ? 1 : def.getCombatLevel();
			Item item = null;

			if (level < 70) {
				item = ClueScrollConstants.getRandomClue(player, Math.random() > 0.5 ? Difficulty.MEDIUM : Difficulty.EASY);
			} else if (level >= 70 && level < 120) {
				item = ClueScrollConstants.getRandomClue(player, Math.random() > 0.5 ? Difficulty.HARD : Difficulty.MEDIUM);
			} else if (level >= 120) {
				item = ClueScrollConstants.getRandomClue(player, Difficulty.HARD);
			}

			if (item != null) {
				drop(item.getId(), 1);
			}
		}
	}

	public void dropItems(Player player) {
		MobManager.dropItems(this, player);
	}

	@Override
	public int getAttack() {
		return attack;
	}

	@Override
	public int getClientIndex() {
		return getIndex();
	}

	@Override
	public int getDefence() {
		return defence;
	}

	public MobDefinition getDefinition() {
		return MobDefinition.get(id);
	}

	@Override
	public int getDirection1() {
		return direction;
	}

	@Override
	public int getDirection2() {
		return runDirection;
	}

	@Override
	public Following getFollowing() {
		return following;
	}

	@Override
	public int getHitpoints() {
		return hitpoints;
	}

	public final int getId() {
		return id;
	}

	@Override
	public int getIndex() {
		return index;
	}

	/**
	 * Gets this mobs interaction distance.
	 *
	 * @return This mobs interaction distance.
	 */
	public int getInteractionDistance() {
		return interactionDistance;
	}

	@Override
	public int getMagicLevel() {
		return getAttack();
	}

	public int getMaxHit() {
		return maxHit;
	}

	@Override
	public int getMaxHitpoints() {
		return maximumHitpoints;
	}

	@Override
	public MovementHandler getMovementHandler() {
		return mov;
	}

	@Override
	public String getName() {
		return MobDefinition.get(id).getName();
	}

	@Override
	public int getRanged() {
		return getAttack();
	}

	@Override
	public int getSize() {
		return MobDefinition.get(id).getSize();
	}

	@Override
	public Skill getSkill() {
		return skill;
	}

	@Override
	public int getStrength() {
		return getAttack();
	}

	public int getWalkingType() {
		return walkingType;
	}

	@Override
	public void hurt(int damage, EntityCharacter applier) {
		if (damage <= 0) {
			return;
		}

		if (damage > hitpoints) {
			damage = hitpoints;
		}

		hitpoints -= damage;

		if (hitpoints < 1 && !isDead()) {
			setDead(true);
			GameService.getTaskScheduler().schedule(new MobDeathTask(this, applier));
		}
	}

	public boolean inMultiCombat() {
		if (inZombiesGame()) {
			return true;
		}

		return getX() >= 3136 && getX() <= 3327 && getY() >= 3519 && getY() <= 3607 || getX() >= 3190 && getX() <= 3327 && getY() >= 3648 && getY() <= 3839
				|| getX() >= 3200 && getX() <= 3390 && getY() >= 3840 && getY() <= 3967 || getX() >= 2992 && getX() <= 3007 && getY() >= 3912 && getY() <= 3967
				|| getX() >= 2946 && getX() <= 2959 && getY() >= 3816 && getY() <= 3831 || getX() >= 3008 && getX() <= 3199 && getY() >= 3856 && getY() <= 3903
				|| getX() >= 3008 && getX() <= 3071 && getY() >= 3600 && getY() <= 3711 || getX() >= 3072 && getX() <= 3327 && getY() >= 3608 && getY() <= 3647
				|| getX() >= 2624 && getX() <= 2690 && getY() >= 2550 && getY() <= 2619 || getX() >= 2371 && getX() <= 2422 && getY() >= 5062 && getY() <= 5117
				|| getX() >= 2896 && getX() <= 2927 && getY() >= 3595 && getY() <= 3630 || getX() >= 2892 && getX() <= 2932 && getY() >= 4435 && getY() <= 4464
				|| getX() >= 3131 && getX() <= 3273 && getY() >= 9520 && getY() <= 9602 || getX() >= 2850 && getX() <= 2878 && getY() >= 9825 && getY() <= 9860
				|| getX() >= 3460 && getX() <= 3517 && getY() >= 9475 && getY() <= 9532 || getX() >= 2971 && getX() <= 2986 && getY() >= 9512 && getY() <= 9523
				|| getX() >= 2256 && getX() <= 2287 && getY() >= 4680 && getY() <= 4711 || getX() >= 2538 && getX() <= 2590 && getY() >= 4932 && getY() <= 4987
				|| getX() >= 2690 && getX() <= 2750 && getY() >= 9410 && getY() <= 9470 || getX() >= 2501 && getX() <= 2545 && getY() >= 4623 && getY() <= 4666
				|| getX() >= 2820 && getX() <= 2889 && getY() >= 9484 && getY() <= 9549 || getX() >= 2515 && getX() <= 2785 && getY() >= 4770 && getY() <= 4784
				|| getX() >= 2411 && getX() <= 2426 && getY() >= 4673 && getY() <= 4687 || getX() >= 3156 && getX() <= 3165 && getY() >= 2975 && getY() <= 2990
				|| getX() >= 2882 && getX() <= 3000 && getY() >= 4357 && getY() <= 4406 || inBounds(2847, 2863, 9628, 9646) || inBounds(3332, 3358, 3206, 3220)
				|| inBounds(2810, 2960, 5260, 5370) || inBounds(1620, 1707, 5653, 5735);
	}

	/**
	 * Helper method to copy internals to avoid use of Object#clone() because that also clones unique fields.
	 *
	 * @param mob The mob to copy.
	 */
	protected Mob internalCopy(Mob mob) {
		setAttack(mob.getAttack());
		defence = mob.defence;
		setWalkingType(mob.getWalkingType());
		setMaxHit(mob.getMaxHit());
		maximumHitpoints = mob.maximumHitpoints;
		hitpoints = mob.maximumHitpoints;
		skill = new Skill(this, attack, defence, hitpoints);
		return this;
	}

	void internalReset() {
		updateflags.clear();
		getMovementHandler().resetMoveDirections();
		reset();
		runDirection = -1;
		setTeleporting(false);
		direction = -1;
		setFocusAtPointX(-1);
		setFocusAtPointY(-1);
		super.spokenText = null;
	}

	public boolean inZombiesGame() {
		return getX() >= 3396 && getX() <= 3445 && getY() >= 9878 && getY() <= 9911;
	}

	public boolean isAttackable() {
		return maximumHitpoints != 0 && getId() != 944;
	}

	public boolean isFamiliar() {
		return false;
	}

	public boolean isFromMinigame() {
		return false;
	}

	public int getHitpointPercentage() {
		return (int) ((double) ((double) hitpoints / (double) maximumHitpoints) * 100);
	}

	protected boolean isInWilderness() {
		return getX() > 2941 && getX() < 3392 && getY() > 3518 && getY() < 3966 || getX() > 2941 && getX() < 3392 && getY() > 9918 && getY() < 10366;
	}

	@Override
	public boolean isMob() {
		return true;
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	public void remove() {
		MobManager.remove(this);
	}

	public void removeNpcFromTile() {
		int x = getLocation().getX();
		int y = getLocation().getY();
		int z = getLocation().getZ();
		int size = getSize();

		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < Constants.SIZES[i].length; k++) {
				int x2 = x + Constants.SIZES[i][k][0];
				int y2 = y + Constants.SIZES[i][k][1];

				if (MapRegion.getRegion(x2, y2) == null) {
					return;
				}

				MapRegion.getRegion(x2, y2).setMobOnTile(false, x2, y2, z);
			}
		}
	}

	public void requestTransform(int id) {
		transformId = id;
		getUpdateFlags().flag(UpdateFlag.TRANSFORM);
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	@Override
	public int setDirection1(int i) {
		direction = i;
		return direction;
	}

	@Override
	public int setDirection2(int i) {
		runDirection = i;
		return runDirection;
	}

	@Override
	public void setHitpoints(int hp) {
		if (hp < 0) {
			hp = 0;
		}

		if (hp > getMaxHitpoints()) {
			hp = getMaxHitpoints();
		}

		hitpoints = hp;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets this mobs interaction distance.
	 *
	 * @param interactionDistance The distance this npc will be able to interact from.
	 */
	public void setInterationDistance(int interactionDistance) {
		this.interactionDistance = interactionDistance;
	}

	public void setMaxHit(int maxHit) {
		this.maxHit = maxHit;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public void setWalkingType(int walkingType) {
		this.walkingType = walkingType;
	}

	@Override
	public void teleport(Location loc) {
		Walking.setMobOnTile(this, false);
		getMovementHandler().getLastLocation().setAs(new Location(loc.getX(), loc.getY() + 1, loc.getZ()));
		getLocation().setAs(loc);
		Walking.setMobOnTile(this, true);
		getMovementHandler().resetMoveDirections();
		getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		setTeleporting(true);
	}

	@Override
	public void update() {

		if (MobManager.mobs[index] == null) {
			return;
		}

		if (isDead() && !deathTask) {
			GameService.getTaskScheduler().schedule(new MobDeathTask(this, null));
			return;
		}

		getEF().updateEffects();

		if (id < 6142 || id > 6145) {
			getFollowing().process();
		}

		if (getCombat().getHitDelay() > 0) {
			getCombat().setHitDelay(getCombat().getHitDelay() - 1);
		}

		if (getCombat().getHitDelay() == 1) {
			Combat.applyDamage(this, getCombat().getPreviousTarget());
		}

		if (getCombat().getAttackTime() > 0) {
			getCombat().setAttackTimer(getCombat().getAttackTime() - 1);
		}

		if (getCombat().getAggressor() != null && System.currentTimeMillis() - getCombat().getLastHitDealt() >= 5000) {
			getCombat().setAggressor(null);
		}

		if (summoner != null && !isFamiliar()) {
			if (summoner.getZ() != getZ() || summoner.isDead() || summoner.isPlayer() && !GameService.getWorld().isPlayerLoggedIn(summoner.getName())
					|| !summoner.goodDistance(getX(), getY(), summoner.getX(), summoner.getY(), TzhaarFightCaveConstants.isTzhaarNpc(getId()) ? 150 : 25))

			{
				Player p = summoner.isPlayer() ? summoner.toPlayer() : null;

				if (p != null) {
					for (int n = 0; n < p.getSummonedBrothers().length; n++) {
						if (id == BarrowsData.BROTHER_IDS[n]) {
							if (p.getSummonedBrothers()[n]) {
								p.getSummonedBrothers()[n] = false;
							}
						}
					}
				}
				remove();
				return;
			}
		}

		if (System.currentTimeMillis() - aggressive_timer >= 15_000 && !isFromMinigame() && !isFamiliar() && summoner == null && !getCombat().inCombat()
				&& MobManager.isAggressive(index)) {
			aggressive_timer = System.currentTimeMillis();
			Player p = MobManager.getCloseRandomPlayer(index);

			if (p != null) {
				getCombat().setTarget(p);
				getFollowing().setFollow(p, FollowType.COMBAT);
			}
		}

		if ((id < 6142 || id > 6145) && !walkingHome) {
			if (getCombat().getTarget() != null) {
				if (getCombat().getAttackTime() <= 0) {
					getCombat().setAttackTimer(0);
					Combat.startAttack(this, getCombat().getTarget());
				}
			} else if (getCombat().getAggressor() != null && MobManager.retaliates(id)) {
				getCombat().setTarget(getCombat().getAggressor());
				getFollowing().setFollow(getCombat().getAggressor(), FollowType.COMBAT);
			}
		}

		if (!isFamiliar() && summoner == null) {
			if (!Walking.withinMaxDistance(this)) {
				walkingHome = true;
			}

			if (walkingHome && getX() == homeLocation.getX() && getY() == homeLocation.getY()) {
				walkingHome = false;
			}

			if (walkingHome) {
				setFaceIndex(0, false);

				if (getCombat().getTarget() != null) {
					Combat.resetCombat(this);
				}

				getFollowing().reset();
				GameService.getTaskScheduler().schedule(new MobWalkTask(this, homeLocation, true));
			} else if (getCombat().getTarget() == null && !getFollowing().isFollowing() && getEF().canWalk(this, false)) {
				if (getWalkingType() == 2) {
					setFacePosition(getX(), getY() + 1);
				} else if (getWalkingType() == 3) {
					setFacePosition(getX(), getY() - 1);
				} else if (getWalkingType() == 4) {
					setFacePosition(getX() + 1, getY());
				} else if (getWalkingType() == 5) {
					setFacePosition(getX() - 1, getY());
				} else if (getWalkingType() == 1) {
					Walking.randomWalk(this);
				}
			}
		}

		// Hunter

		if (!isDead() && capturable != null) {

			if (trap == null && NumberUtil.nextBoolean()) {
				trap = TrapProcessor.getClosest(getLocation(), capturable);
			}

			if (trap != null) {
				if (trap.isActive() || trap.isBound()) {
					trap.setBound();
					Location trapPosition = Location.create(trap.getPosition().getX(), trap.getPosition().getY(), trap.getPosition().getZ());
					setFacePosition(trapPosition.getX(), trapPosition.getY());

					if (trapPosition.equals(getLocation()) && !inTask) {
						switch (capturable.identifier()) {
						case Hunter.Capturable.BOX:
							GameService.getTaskScheduler().schedule(new BoxTrap(this, trap));
							break;

						case Hunter.Capturable.SNARE:
							GameService.getTaskScheduler().schedule(new Snare(this, trap));
							break;

						case Hunter.Capturable.NET:
							GameService.getTaskScheduler().schedule(new NetTrap(this, trap));
							break;
						}
					} else {
						getUpdateFlags().flag(UpdateFlag.APPEARANCE);
						inTask = true;
						GameService.getTaskScheduler().schedule(new MobWalkTask(this, new Location(trapPosition.getX(), trapPosition.getY(), getLocation().getZ()), true));
					}
				} else {
					trap = null;
				}
			}
		}

	}

	/**
	 * If the entity is within the mobs walking distance
	 *
	 * @param e The entity the mob is following
	 * @return
	 */
	public boolean withinMobWalkDistance(EntityCharacter e) {
		if (following.isIgnoreDistance() || summoner != null) {
			return true;
		}

		return Math.abs(e.getLocation().getX() - homeLocation.getX()) + Math.abs(e.getLocation().getY() - homeLocation.getY()) < getSize() * 2 + 6;
	}

}
