package com.koios.game.model;

import java.util.LinkedHashSet;
import java.util.Set;

import com.koios.Constants;
import com.koios.game.content.combat.CombatVariables;
import com.koios.game.content.effects.EffectManager;
import com.koios.game.content.following.Following;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.teleport.Teleport;
import com.koios.game.content.teleport.TeleportExecutor;
import com.koios.game.content.teleport.Teleport.TeleportModes;
import com.koios.game.model.UpdateFlags.UpdateFlag;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.movement.MovementHandler;
import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;
import com.koios.task.TaskScheduler;

/**
 * A creature is an entity that 'lives' and requires dynamic updates.
 */
public abstract class EntityCharacter extends Entity {

	public static final int PLAYER_INDEX_MOD = 32768;

	/**
	 * A busy flag that tells us if an entity can procceed with a determined action.
	 */
	private boolean busy = false;

	/**
	 * The current animation.
	 */
	private Animation currentAnimation;

	/**
	 * The visibility status of an entity, default as visible
	 */
	private boolean visible = true;

	/**
	 * The current graphic.
	 */
	private Graphic currentGraphic;

	/**
	 * The entity is casting a teleport
	 */
	private boolean teleportActive;

	/**
	 * A Set of local players around this EntityCharacter
	 */
	private final Set<Player> localPlayers = new LinkedHashSet<>();

	/**
	 * A Set of local mobs around this EntityCharacter
	 */
	private final Set<Mob> localMobs = new LinkedHashSet<>();

	/**
	 * This EntityCharacters TaskScheduler
	 */
	protected final TaskScheduler<EntityTask<?>> taskScheduler = new TaskScheduler<>();

	/**
	 * Entity's damage masks.
	 */
	private Damage damageMask1 = new Damage(0), damageMask2 = new Damage(0);

	private final DamageReceiver damageReceiver = new DamageReceiver();

	private final EffectManager effectManager = new EffectManager();

	private int face_index = 0;

	/**
	 *
	 */
	private int focusAtPointX = -1;

	/**
	 *
	 */
	private int focusAtPointY = -1;

	/**
	 * The direction of the force movement.
	 */
	public int forceMoveDirection = 0;

	/**
	 * The speed of the force movement from (getLocation().getX(), getLocation().getY()) to (x1, y1).
	 */
	public int forceMoveSpeed = 0;

	/**
	 * The second speed of the force movement from (x1, y1) to (x2, y2).
	 */
	public int forceMoveSpeed2 = 0;

	/**
	 * Current position on the X-axis for force movement.
	 */
	public int forceX = 0;

	/**
	 * Current position on the Y-axis for force movement.
	 */
	public int forceY = 0;

	private boolean isDead = false;

	/**
	 * The message displayed above the character in-game.
	 */
	public String spokenText = null;

	/**
	 * The teleporting flag.
	 */
	private boolean teleporting = false;

	/**
	 * Entity's update flags
	 */
	protected UpdateFlags updateflags = new UpdateFlags();

	private final CombatVariables variables = new CombatVariables();

	public boolean castTeleport(Teleport teleport) {
		return TeleportExecutor.startTeleportation(this, teleport);
	}

	/**
	 * Casts a teleport using the players spell book to determine which teleport type to use
	 *
	 * @param location The {@link Location} to place the player after teleporting
	 * @return If the teleport was successful
	 */
	public boolean castTeleport(Location location) {
		Teleport teleport = new Teleport(location, getTeleportMode());
		return TeleportExecutor.startTeleportation(this, teleport);
	}

	private TeleportModes getTeleportMode() {
		Player player = toPlayer();
		if (player != null) {
			switch (player.getSpellbook()) {
			case ANCIENT_MAGICKS:
				return TeleportModes.ANCIENT_SPELL;
			case LUNAR:
				return TeleportModes.LUNAR_SPELL;
			case REGULAR:
				return TeleportModes.MODERN_SPELL;
			default:
				return TeleportModes.MODERN_SPELL;
			}
		}
		return TeleportModes.MODERN_SPELL;
	}

	/**
	 *
	 */
	public abstract void death();

	public abstract int getAttack();

	public CombatVariables getCombat() {
		return variables;
	}

	/**
	 * Gets the current animation.
	 *
	 * @return The current animation;
	 */
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	/**
	 * Gets the current graphic.
	 *
	 * @return The current graphic.
	 */
	public Graphic getCurrentGraphic() {
		return currentGraphic;
	}

	public DamageReceiver getDamage() {
		return damageReceiver;
	}

	public Damage getDamageMask1() {
		return damageMask1;
	}

	public Damage getDamageMask2() {
		return damageMask2;
	}

	public abstract int getDefence();

	public abstract int getDirection1();

	public abstract int getDirection2();

	public EffectManager getEF() {
		return effectManager;
	}

	public int getFaceIndex() {
		return face_index;
	}

	public abstract Following getFollowing();

	public abstract int getHitpoints();

	public abstract int getMagicLevel();

	public abstract int getMaxHitpoints();

	public abstract MovementHandler getMovementHandler();

	public int getProjetileIndex() {
		return isPlayer() ? -getIndex() - 1 : getIndex() + 1;
	}

	public abstract int getRanged();

	public abstract int getSize();

	public abstract Skill getSkill();

	public abstract int getStrength();

	public UpdateFlags getUpdateFlags() {
		return updateflags;
	}

	public boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return objectX - playerX <= distance && objectX - playerX >= -distance && objectY - playerY <= distance && objectY - playerY >= -distance;
	}

	/**
	 * Invoked to deal damage to this creature. If the damage is higher than the current hitpoints this creature has, the damage will be equal to the hitpoints remainder.
	 *
	 * @param damage The damage in hitpoints.
	 */
	public abstract void hurt(int damage, EntityCharacter aggressor);

	public boolean inBounds(int x1, int x2, int y1, int y2) {
		return getLocation().getX() >= x1 && getLocation().getX() <= x2 && getLocation().getY() >= y1 && getLocation().getY() <= y2;
	}

	public boolean inDistance(EntityCharacter target, int distance) {

		if (target == null) {
			return false;
		}

		int x = target.getLocation().getX();
		int y = target.getLocation().getY();
		int x2 = getLocation().getX();
		int y2 = getLocation().getY();

		int x3;
		int y3;
		int x4;
		int y4;

		for (int i = 0; i < target.getSize() + 1; i++) {
			for (int k = 0; k < Constants.SIZES[i].length; k++) {
				x3 = x + Constants.SIZES[i][k][0];
				y3 = y + Constants.SIZES[i][k][1];

				for (int i2 = 0; i2 < getSize() + 1; i2++) {
					for (int k2 = 0; k2 < Constants.SIZES[i2].length; k2++) {
						x4 = x2 + Constants.SIZES[i2][k2][0];
						y4 = y2 + Constants.SIZES[i2][k2][1];

						if (Location.getManhattanDistance(x4, y4, x3, y3) <= distance) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean inGodwars() {
		return inBounds(2810, 2960, 5260, 5370);
	}

	public boolean inMulti() {
        if(com.koios.game.arpg.TestArena.inside(this)||isPlayer()&&com.koios.game.arpg.MapRuns.inside(toPlayer())||this instanceof com.koios.game.arpg.MapRuns.MapMob)return true;
		return getLocation().getX() >= 3136 && getLocation().getX() <= 3327 && getLocation().getY() >= 3519 && getLocation().getY() <= 3607
				|| getLocation().getX() >= 3190 && getLocation().getX() <= 3327 && getLocation().getY() >= 3648 && getLocation().getY() <= 3839
				|| getLocation().getX() >= 3200 && getLocation().getX() <= 3390 && getLocation().getY() >= 3840 && getLocation().getY() <= 3967
				|| getLocation().getX() >= 2992 && getLocation().getX() <= 3007 && getLocation().getY() >= 3912 && getLocation().getY() <= 3967
				|| getLocation().getX() >= 2946 && getLocation().getX() <= 2959 && getLocation().getY() >= 3816 && getLocation().getY() <= 3831
				|| getLocation().getX() >= 3008 && getLocation().getX() <= 3199 && getLocation().getY() >= 3856 && getLocation().getY() <= 3903
				|| getLocation().getX() >= 3008 && getLocation().getX() <= 3071 && getLocation().getY() >= 3600 && getLocation().getY() <= 3711
				|| getLocation().getX() >= 3072 && getLocation().getX() <= 3327 && getLocation().getY() >= 3608 && getLocation().getY() <= 3647
				|| getLocation().getX() >= 2624 && getLocation().getX() <= 2690 && getLocation().getY() >= 2550 && getLocation().getY() <= 2619
				|| getLocation().getX() >= 2371 && getLocation().getX() <= 2422 && getLocation().getY() >= 5062 && getLocation().getY() <= 5117
				|| getLocation().getX() >= 2896 && getLocation().getX() <= 2927 && getLocation().getY() >= 3595 && getLocation().getY() <= 3630
				|| getLocation().getX() >= 2892 && getLocation().getX() <= 2932 && getLocation().getY() >= 4435 && getLocation().getY() <= 4464
				|| getLocation().getX() >= 3131 && getLocation().getX() <= 3273 && getLocation().getY() >= 9520 && getLocation().getY() <= 9602
				|| getLocation().getX() >= 2850 && getLocation().getX() <= 2878 && getLocation().getY() >= 9825 && getLocation().getY() <= 9860
				|| getLocation().getX() >= 3460 && getLocation().getX() <= 3517 && getLocation().getY() >= 9475 && getLocation().getY() <= 9532
				|| getLocation().getX() >= 2971 && getLocation().getX() <= 2986 && getLocation().getY() >= 9512 && getLocation().getY() <= 9523
				|| getLocation().getX() >= 2256 && getLocation().getX() <= 2287 && getLocation().getY() >= 4680 && getLocation().getY() <= 4711
				|| getLocation().getX() >= 2538 && getLocation().getX() <= 2590 && getLocation().getY() >= 4932 && getLocation().getY() <= 4987
				|| getLocation().getX() >= 2690 && getLocation().getX() <= 2750 && getLocation().getY() >= 9410 && getLocation().getY() <= 9470
				|| getLocation().getX() >= 2501 && getLocation().getX() <= 2545 && getLocation().getY() >= 4623 && getLocation().getY() <= 4666
				|| getLocation().getX() >= 2820 && getLocation().getX() <= 2889 && getLocation().getY() >= 9484 && getLocation().getY() <= 9549
				|| getLocation().getX() >= 2515 && getLocation().getX() <= 2785 && getLocation().getY() >= 4770 && getLocation().getY() <= 4784
				|| getLocation().getX() >= 2411 && getLocation().getX() <= 2426 && getLocation().getY() >= 4673 && getLocation().getY() <= 4687
				|| getLocation().getX() >= 3156 && getLocation().getX() <= 3165 && getLocation().getY() >= 2975 && getLocation().getY() <= 2990
				|| getLocation().getX() >= 2882 && getLocation().getX() <= 3000 && getLocation().getY() >= 4357 && getLocation().getY() <= 4406 || inBounds(2847, 2863, 9628, 9646)
				|| inBounds(3332, 3358, 3206, 3220) || inBounds(2810, 2960, 5260, 5370) || inBounds(1620, 1707, 5653, 5735) || inBounds(2820, 2846, 3800, 3825)
				|| inBounds(2723, 2755, 5060, 5100);

	}

	public boolean inSafePK() {
		return getX() >= 2754 && getY() >= 5504 && getX() <= 2880 && getY() <= 5583;
	}

	public boolean isBusy() {
		return busy;
	}

	public boolean isDead() {
		return isDead;
	}

	public abstract boolean isMob();

	public boolean isMoving() {
		return false;
	}

	public boolean isOverSafePKBoundary(int y) {
		return inSafePK() && y >= 5575;
	}

	public abstract boolean isPlayer();

	/**
	 * Checks if this player is teleporting.
	 *
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isTeleporting() {
		return teleporting;
	}

	/**
	 * Animates the entity.
	 *
	 * @param animation The animation.
	 */
	public void playAnimation(Animation animation) {
		currentAnimation = animation;
		getUpdateFlags().flag(UpdateFlag.ANIMATION);
	}

	/**
	 * Plays graphics.
	 *
	 * @param graphic The graphics.
	 */
	public void playGraphics(Graphic graphic) {
		currentGraphic = graphic;
		getUpdateFlags().flag(UpdateFlag.GRAPHICS);
	}

	/**
	 * Resets attributes after an update cycle.
	 */
	public void reset() {
		currentAnimation = null;
		currentGraphic = null;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	/**
	 * Set a damage to the avaliable update mask.
	 */
	public void setDamage(Damage damage, EntityCharacter aggressor) {
		if (damage.getDamage() < 0) {
			damage.setDamage(0);
		}

		Integer arpgLifeLoss=isPlayer()?com.koios.game.arpg.Defences.receive(toPlayer(),damage,aggressor):null;

        if (arpgLifeLoss==null && damage.getDamage() > getHitpoints()) {
			damage.setDamage(getHitpoints());
		}

		if (!updateflags.get(UpdateFlag.HIT)) {
			damageMask1 = damage;
			updateflags.flag(UpdateFlag.HIT);
		} else {
			damageMask2 = damage;
			updateflags.flag(UpdateFlag.HIT_2);
		}

		if (aggressor!=null && aggressor.isPlayer()) {
			if (getIndex() != aggressor.getIndex()) {
				damageReceiver.addDamage(aggressor.getName(), damage.getDamage());
			}
		}

		if(damage.getDamage()>0)getCombat().setLastDamageReceived(System.currentTimeMillis());
        hurt(arpgLifeLoss==null?damage.getDamage():arpgLifeLoss, aggressor);
	}

	public void setDead(boolean b) {
		isDead = b;
	}

	public abstract int setDirection1(int i);

	public abstract int setDirection2(int i);

	public void setFaceIndex(int index, boolean isPlayer) {
		face_index = index + (isPlayer ? 32768 : 0);

		getUpdateFlags().flag(UpdateFlag.FACE_ENTITY);
	}

	public void setFacePosition(int x, int y) {
		setFocusAtPointX((x << 1) + 1);
		setFocusAtPointY((y << 1) + 1);
		getUpdateFlags().flag(UpdateFlag.FACE_COORDINATE);
	}

	public void setFacePosition(Location pos) {
		setFacePosition(pos.getX(), pos.getY());
	}

	public abstract void setHitpoints(int hp);

	/**
	 * Sets the teleporting flag.
	 *
	 * @param teleporting The teleporting flag.
	 */
	public void setTeleporting(boolean teleporting) {
		this.teleporting = teleporting;
	}

	public void speak(String message) {
		spokenText = message;
		getUpdateFlags().flag(UpdateFlag.FORCED_CHAT);
	}

	public void schedule(EntityTask<?> task) {
		taskScheduler.schedule(task);
	}

	public abstract void teleport(Location location);

	/**
	 * Returns the entity into a mob
	 *
	 * @return the mob
	 */
	public Mob toMob() {
		throw new UnsupportedOperationException(getClass().getName() + " attempted converted to Mob");
	}

	/**
	 * Returns the entity into a player
	 *
	 * @return the player
	 */
	public Player toPlayer() {
		throw new UnsupportedOperationException(getClass().getName() + " attempted converted to Player");
	}

	public abstract int getClientIndex();

	public int getFocusAtPointX() {
		return focusAtPointX;
	}

	public void setFocusAtPointX(int focusAtPointX) {
		this.focusAtPointX = focusAtPointX;
	}

	public int getFocusAtPointY() {
		return focusAtPointY;
	}

	public void setFocusAtPointY(int focusAtPointY) {
		this.focusAtPointY = focusAtPointY;
	}

	public void setTeleportActive(boolean active) {
		teleportActive = active;
	}

	public boolean isTeleportActive() {
		return teleportActive;
	}

	public Set<Player> getLocalPlayers() {
		return localPlayers;
	}

	public Set<Mob> getLocalMobs() {
		return localMobs;
	}

	public TaskScheduler<EntityTask<?>> getTaskScheduler() {
		return taskScheduler;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
