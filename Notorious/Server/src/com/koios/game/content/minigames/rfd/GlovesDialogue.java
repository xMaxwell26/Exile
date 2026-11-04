package com.koios.game.content.minigames.rfd;

import com.koios.game.content.area.AreaBoundary;
import com.koios.game.content.area.impl.RecipeForDisasterAreaHandler;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.Expression;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

/**
 * Handles the recipe for disaster minigame dialogue
 * 
 * @author Arithium
 *
 */
public final class GlovesDialogue extends Dialogue {

	/**
	 * The id of the gypsy
	 */
	private final int GYPSY_ID = 882;

	/**
	 * The id of the unnattackable culinaromancer
	 */
	private static final int CULI_ID = 3400;

	/**
	 * The id of the agrith na-na npc
	 */
	private static final int AGRITH_NA_NA = 3493;

	/**
	 * The id of the flambeed npc
	 */
	private static final int FLAMBEED = 3494;

	/**
	 * The id of the karamel npc
	 */
	private static final int KARAMEL = 3495;

	/**
	 * The id of the dessourt npc
	 */
	private static final int DESSOURT = 3496;

	/**
	 * The {@link Location} for the center point of the map
	 */
	private static final Location CENTER_POINT = new Location(1899, 5355, -1);

	/**
	 * The {@link Location} to return the player too after finishing
	 */
	public static final Location RETURN_LOCATION = new Location(3097, 3486, 0);

	/**
	 * The ids of the npcs in order for the wave the player is on
	 */
	private static final int[] WAVES = { AGRITH_NA_NA, FLAMBEED, KARAMEL, DESSOURT };

	/**
	 * A random set of coordinates to spawn the first npc
	 */
	private static int[][] coordinates = { { 1900, 5354 }, { 1900, 5354 }, { 1900, 5354 }, { 1900, 5354 }, { 1900, 5354 } };

	/**
	 * Constructs a new {@link Dialogue} for the recipe for disaster minigame
	 * 
	 * @param player
	 *            The {@link Player} the player receiving the dialogue
	 * @param state
	 *            The state to start the dialogue
	 */
	public GlovesDialogue(Player player, int state) {
		super(player, state);
	}
	
	/**
	 * Spawns a new {@link Mob} for the next wave
	 * 
	 * @param id
	 *            The id of the {@link Mob} to spawn
	 * @param close
	 *            Spawn the Mob close to the player
	 */
	private void spawn(int id, boolean close) {
		int random = NumberUtil.random(coordinates.length);
		Location location = null;
		if (close) {
			location = player.getLocation().copy();
		} else {
			location = new Location(coordinates[random][0], coordinates[random][1], getHeight());
		}
		MobManager.spawnMob(player, id, location, 0, 0, 0, 0, true, true, "");
	}

	/**
	 * Gets the height level to start the minigame on
	 * 
	 * @return The height level to start the minigame on
	 */
	public int getHeight() {
		return (player.getIndex() * 4) + 2;
	}

	@Override
	protected void start() {
		if (isPhase(0)) {
			mobChat(GYPSY_ID, Expression.WORRIED, player.getName() + ", I must warn you, inside that dimension", "you will have no assistance from the gods, and your",
					"prayers will not work at all!");
		} else if (isPhase(3)) {
			if (player.distanceToPoint(3097, 3487) < 5) {
				player.setPosition(new Location(1900, 5366, getHeight()));
			}
			next();
			player.setAttribute("spawned_rfd", false);
		} else if (isPhase(4)) {
			mobChat(CULI_ID, Expression.ANGRY, "NOOOooo...");
			player.rfdGloveCount++;
			setPhase(2);
		} else if (isPhase(5)) {
			player.rfdGloveCount = 6;
			mobChat(CULI_ID, Expression.ANGRY, "You have caused me enough grief!", "I guess I'll have to finish you off myself!");
			setPhase(2);
		} else if (isPhase(6)) {
			player.rfdGloveCount = 7;
			mobChat(GYPSY_ID, Expression.CALM_TALK, "Good work " + player.getName() + "!", "I think that will be the last we'll see of him!", "You'd better come back now.");
			setPhase(7);
		}
	}

	@Override
	public void finish() {

		if (AreaBoundary.inArea(player.getLocation(), RecipeForDisasterAreaHandler.AREA)) {
			if (player.rfdGloveCount < 7) {
				if (player.rfdGloveCount == 6) {
					Mob mob = MobManager.mobs[player.getAttribute("culi", 0)];

					if (mob != null && mob.getIndex() > 0) {
						mob.setVisible(false);
						MobManager.remove(mob);
					}
					MobManager.spawnMob(player, 3491, new Location(CENTER_POINT.getX(), CENTER_POINT.getY(), getHeight()), 0, 0, 0, 0, true, true, "");
					return;
				}

				spawn(WAVES[player.rfdGloveCount - 1], player.getAttribute("spawned_rfd", false));
				player.setAttribute("spawned_rfd", true);
			}
		}
	}

	@Override
	protected void next() {
		if (isPhase(0)) {
			mobChat(GYPSY_ID, Expression.CALM_TALK, "Also be aware that he will use his powers to draw upon", "the might of foes he has control of, and that",
					"dimensional instability means that your lost items will be", "irretreievable should you die...");
			setPhase(1);
		} else if (isPhase(1)) {
			mobChat(GYPSY_ID, Expression.CALM_TALK, "Take care, I suspect the culinaromancer will not give", "up so easily!");
			player.rfdGloveCount = 1;
			setPhase(2);
		} else if (isPhase(2)) {
			stop();
		} else if (isPhase(3)) {
			Mob culi = MobManager.spawnMob(CULI_ID, new Location(CENTER_POINT.getX(), CENTER_POINT.getY(), getHeight()), 0, 0, 0, 0, 0);
			culi.summoner = player;
			culi.speak("Say hello to my little friend.");
			player.setAttribute("culi", culi.getIndex());
			switch (player.rfdGloveCount) {
			case 1:
				mobChat(CULI_ID, Expression.ANGRY, "You DARE come HERE?!?!");
				break;
			case 2:
				mobChat(CULI_ID, Expression.ANGRY, "You will regret following me!");
				break;
			case 3:
				mobChat(CULI_ID, Expression.ANGRY, "You should not meddle in affairs that do not concern", "you!");
				break;
			case 4:
				mobChat(CULI_ID, Expression.ANGRY, "You will regret following me!");
				break;
			case 5:
				mobChat(CULI_ID, Expression.ANGRY, "This will be your death!");
				break;
			case 6:
				setPhase(4);
				next();
				return;
			}

			setPhase(2);
		} else if (isPhase(7)) {
			player.teleport(RETURN_LOCATION);
			player.setAttribute("culi", 0);
			stop();
		}
	}

}