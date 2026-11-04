package com.koios.game.content.effects.impl;

import java.util.Objects;

import com.koios.game.content.effects.Effect;
import com.koios.game.content.effects.EffectConstants;
import com.koios.game.content.minigames.duel.DuelArenaConstants.DuelingRules;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;

/**
 * Handles the vengeance effect
 *
 * @author Gabriel | Wolfs Darker
 */
public class Vengeance implements Effect {

	private final Player player;
	private int timeLeft;
	private boolean message = true;

	public Vengeance(Player player) {
		this.player = Objects.requireNonNull(player, "player");
	}

	public Vengeance(Player player, int time) {
		this(player);
		timeLeft = time;
	}

	@Override
	public String getName() {
		return EffectConstants.VENGEANCE_NAME;
	}

	@Override
	public QueuePolicy getQueueType() {
		return QueuePolicy.NEVER;
	}

	@Override
	public Duration getDuration() {
		return Duration.TEMPORARY;
	}

	@Override
	public int getTimeLeft() {
		return timeLeft;
	}

	@Override
	public Walk getWalkEffect() {
		return Walk.WALKABLE;
	}

	@Override
	public void renew() {
		timeLeft = 0;
		message = false;
	}

	@Override
	public void start() {
		timeLeft = EffectConstants.VENGEANCE_TIME;
		player.getEF().setQueueState(false);
	}

	@Override
	public void tick() {
		if (timeLeft > 0) {
			timeLeft--;
		}

		if (player.isDead()) {
			timeLeft = 0;
			message = false;
		}
	}

	@Override
	public void end() {
		if (message) {
			player.sendMessage("Your vengeance effect fades away.");
		}

		player.getEF().setQueueState(false);
	}

	@Override
	public void load(Player player, String s) {
		int time = Integer.parseInt(s.split(":")[1].replace(":", ""));

		if (time > 0) {
			Effect effect = new Vengeance(player, time);
			player.getEF().queueEffect(effect);
		}
	}

	public static boolean castVengeance(Player player) {
		if (player.getSpellbook() != Spellbook.LUNAR) {
			// vengeance can only be cast with the lunar spellbook.
			System.err.println(String.format("%s attempted to cast vengeance spell with spellbook: %s", player.getName(), player.getSpellbook().getName()));
			return false;
		}

		if (player.getDuel().isRuleActive(DuelingRules.NO_MAGIC)) {
			player.sendMessage("Magic has been disabled for this duel!");
			return false;
		}

		if (player.getSkill().getLevel(Skill.MAGIC) < 94) {
			player.sendMessage("You need a Magic level of 94 to cast this spell.");
			return false;
		}

		if (player.getSkill().getLevel(Skill.DEFENCE) < 40) {
			player.sendMessage("You need a Defence level of 40 to cast this spell.");
			return false;
		}

		if (player.getEF().isEffectListed(EffectConstants.VENGEANCE_NAME)) {
			player.sendMessage("You already have vengeance casted!");
			return false;
		}

		if (System.currentTimeMillis() - player.getLastVengeanceCast() < 30_000) {
			player.sendMessage("You must wait 30 seconds before casting this spell again.");
			return false;
		}

		if (!player.getInventory().hasItem(new Item(557, 10)) || !player.getInventory().hasItem(new Item(9075, 4)) || !player.getInventory().hasItem(new Item(560, 2))) {
			player.sendMessage("You don't have the required runes to cast this spell.");
			return false;
		}

		player.playAnimation(new Animation(4410));
		player.playGraphics(new Graphic(726));
		player.getInventory().remove(new Item(557, 10));
		player.getInventory().remove(new Item(560, 2));
		player.getInventory().remove(new Item(9075, 4));
		player.getEF().queueEffect(new Vengeance(player));
		player.setLastVengeanceCast(System.currentTimeMillis());
		return true;
	}

	@Override
	public String toString() {
		return EffectConstants.VENGEANCE_NAME + ":" + timeLeft;
	}

	@Override
	public String getNonWalkableMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
