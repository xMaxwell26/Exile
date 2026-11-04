package com.koios.game.content.minigames.prophunt;

import java.util.HashMap;
import java.util.Map;

import com.koios.GameService;
import com.koios.game.content.combat.CombatExtras;
import com.koios.game.content.minigames.Minigame;
import com.koios.game.content.minigames.MinigameLobby;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.Graphic.GraphicHeight;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.ActionSender;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendOptionPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.task.Task;

/**
 * The prop hunt game is set here. This class cannot be instantiated. For use, refer to this class through {@code PropHunt.getInstance()}
 *
 * @author Michael | Chex
 */
public class PropHunt extends Minigame {

	/**
	 * A map of the players in the prop hunt game.
	 */
	private static Map<Player, String> players = new HashMap<>();

	/**
	 * Creates an instance of {@link PropHunt}.
	 */
	private static final PropHunt instance = new PropHunt();

	/**
	 * Gets the instance of {@linkplain PropHunt}.
	 */
	public static PropHunt getInstance() {
		return instance;
	}

	/**
	 * Constructs the Prop Hunt minigame class.
	 */
	private PropHunt() {
		super(1);
	}

	@Override
	public String getMinigameName() {
		return "PROP_HUNT";
	}

	@Override
	public Map<Player, String> getPlayerList() {
		return players;
	}

	@Override
	public Map<String, Mob> getMobList() {
		return null;
	}

	@Override
	public boolean canTeleport() {
		return false;
	}

	@Override
	public boolean canLogout() {
		return true;
	}

	@Override
	public boolean canAttack() {
		return false;
	}

	@Override
	public boolean handleObjectClick(Player player, WorldObject object) {
		shootTarget(player, Location.create(object.getX(), object.getY()));

		GameService.getTaskScheduler().schedule(new Task(1) {

			@Override
			protected void execute() {
				damageHook(player, null, 5);
				stop();
			}

		});

		ActionSender.sendStillGfx(player, 287, Location.create(object.getX(), object.getY()), GraphicHeight.HIGH, 45);
		return false;
	}

	@Override
	public boolean handleMobClick(Player player, Mob mob) {
		return true;
	}

	@Override
	public boolean handleButtonClick(Player player, int buttonId) {
		return false;
	}

	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public Location getSpawnLocation() {
		int randomX = Math.random() > 0.5 ? -1 : 1;
		int randomY = Math.random() > 0.5 ? -1 : 1;
		return Location.create(2662 + randomX, 3306 + randomY, 8);
	}

	@Override
	public Location getRespawnLocation() {
		return getSpawnLocation();
	}

	@Override
	public Location getExitLocation() {
		return getLobby().getJoinLocation();
	}

	@Override
	public MinigameLobby getLobby() {
		return PropHuntLobby.getInstance();
	}

	@Override
	public void handleLogout(Player player) {
		Player[] toRemove = new Player[getPlayerList().size()];

		for (int index = 0; index < toRemove.length; index++) {
			Player p = (Player) getPlayerList().keySet().toArray()[index];
			toRemove[index] = p;
		}

		for (Player p : toRemove) {
			removePlayer(p);
		}

		broadcastMessage(player.getName() + " has disconnected.");
	}

	@Override
	public void handleLogin(Player player) {
	}

	@Override
	public void handleDeath(Player player) {
	}

	@Override
	public void startMinigame() {
	}

	@Override
	public void joinMinigame(Player player) {
		player.getSession().send(new SendOptionPacket(1, null, false));
		player.getSession().send(new SendOptionPacket(2, null, false));
		player.getSession().send(new SendOptionPacket(3, "Shoot", true));
		player.getSession().send(new SendOptionPacket(4, null, false));
		player.getSession().send(new SendOptionPacket(5, null, false));
		player.setPosition(getSpawnLocation());

		if (PropHuntLobby.getInstance().isHunter()) {
			player.getSkill().setLevel(3, 100);
		}

		getPlayerList().put(player, Boolean.toString(PropHuntLobby.getInstance().isHunter()));
		PropHuntLobby.getInstance().inverseHunter();
	}

	@Override
	public void setLobby(MinigameLobby lobby) {
	}

	@Override
	public void damageHook(EntityCharacter attacker, EntityCharacter target, int damage) {
		attacker.setDamage(new Damage(5), attacker);
		Player player = (Player) attacker;

		if (player.getSkill().getLevel(Skill.HITPOINTS) <= 0) {
			player.sendMessage("You have died!");
			player.getSkill().setLevel(Skill.HITPOINTS, 100);
		} else {
			player.sendMessage("You have incorrectly shot a normal object, losing 5 health!");
		}
	}

	@Override
	public void removePlayer(Player player) {
		getPlayerList().remove(player);
		resetPlayerVariables(player);
		player.setPosition(getExitLocation());
	}

	@Override
	public void nextWave(Object[] objects) {
	}

	@Override
	protected void execute() {
	}

	private void broadcastMessage(String message) {
		players.keySet().stream().forEach(p -> p.sendMessage(message));
	}

	private void resetPlayerVariables(Player player) {
		player.getPrayers().clear();
		player.getSession().send(new SendRemoveInterfacePacket());
		player.specialEnergy = 100;
		player.getItems().updateSpecialBar();
		player.getSkill().restoreAll();
		player.getSession().send(new SendOptionPacket(1, null, false));
		player.getSession().send(new SendOptionPacket(2, null, false));
		player.getSession().send(new SendOptionPacket(3, null, false));
		player.getSession().send(new SendOptionPacket(4, "Follow", false));
		player.getSession().send(new SendOptionPacket(5, "Trade", false));
	}

	public static void shootTarget(Player player, Location target) {
		int pX = player.getX();
		int pY = player.getY();
		int nX = target.getX();
		int nY = target.getY();
		player.setFacePosition(nX, nY);
		player.playAnimation(Animation.create(2326));
		int offX = (pY - nY) * -1;
		int offY = (pX - nX) * -1;
		CombatExtras.createPlayersProjectile(pX, pY, player.getLocation().getZ(), offX, offY, 50, 45, 55, 44, 22, -1, 30, 4750);
		player.getMovementHandler().reset();
		player.requestUpdates();
	}

	public static void shootTarget(Player player, Player target) {
		shootTarget(player, target.getLocation());
		target.playGraphics(new Graphic(659, 45, GraphicHeight.MIDDLE));

		GameService.getTaskScheduler().schedule(new Task(1) {

			@Override
			protected void execute() {
				player.sendMessage("You found " + target.getName() + ", gaining 20 health!");
				player.setDamage(new Damage(-20), player);
				stop();
			}

		});
	}

	/**
	 * This method checks if a certain player is a hunter.
	 *
	 * @param player The player to check.
	 * @return <code>True</code> if the player is a hunter.
	 */
	public boolean isHunter(Player player) {
		if (!getPlayerList().containsKey(player)) {
			return false;
		}

		return Boolean.parseBoolean(getPlayerList().get(player));
	}

	@Override
	public boolean canUsePrayer() {
		return false;
	}

	@Override
	public boolean isOver() {
		return false;
	}

}
