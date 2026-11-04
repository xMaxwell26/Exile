package com.koios.game.content.combat.prayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;

public final class PrayerSet {

	private final Player player;

	private final Map<String, Prayer> prayers = new HashMap<>();

	public PrayerSet(Player player) {
		this.player = player;
	}

	public void effect(Prayer prayer) {
		if (isActive(prayer)) {
			prayer.effect(player);
		}
	}

	public void activate(Prayer prayer) {
		if (!prayer.tryActivate(player)) {
			deactivate(prayer);
			return;
		}

		if (isActive(prayer)) {
			deactivate(prayer);
			return;
		}

		for (Entry<String, Prayer> set : ImmutableSet.copyOf(prayers.entrySet())) {
			Prayer active = set.getValue();
			if (prayer != active && (prayer.getFlags() & active.getFlags()) != 0) {
				deactivate(active);
			}
		}

		player.getSession().send(new SendConfigPacket(prayer.getGlow(), 1));

		if (prayer.getHeadIcon() != -1) {
			player.headIcon = prayer.getHeadIcon();
			player.requestUpdates();
		}

		if (prayer.getActivateAnimation() != -1) {
			player.playAnimation(Animation.create(prayer.getActivateAnimation()));
		}

		if (prayer.getActivateGraphic() != -1) {
			player.playGraphics(Graphic.create(prayer.getActivateGraphic()));
		}

		prayer.onActivate(player);
		prayers.put(prayer.getName(), prayer);
	}

	public void deactivate(Prayer prayer) {
		player.getSession().send(new SendConfigPacket(prayer.getGlow(), 0));

		if (prayer.getHeadIcon() != -1) {
			player.headIcon = -1;
			player.requestUpdates();
		}

		prayer.onDeactivate(player);
		prayers.remove(prayer.getName());
	}

	public boolean isActive(Prayer prayer) {
		// prevent prayer swapping/cheating
		if (prayer.getBook() == PrayerBook.NORMAL && player.altarPrayed != 0) {
			return false;
		}
		if (prayer.getBook() == PrayerBook.CURSED && player.altarPrayed != 1) {
			return false;
		}
		return prayers.get(prayer.getName()) == prayer;
	}

	/**
	 * Tests whether _any_ of the specified Prayers are active
	 * 
	 * @param prayers The Prayers
	 * @return {@code true} if at least one of the specified Prayers is active otherwise {@code false}.
	 */
	public boolean isActive(Prayer... prayers) {
		for (Prayer prayer : prayers)
			if (isActive(prayer))
				return true;

		return false;
	}

	public void deactivate(Prayer... prayers) {
		for (Prayer prayer : prayers)
			deactivate(prayer);
	}

	public void handlePrayerDrain() {
		double pointsToRemove = prayers.values().stream().mapToDouble(p -> p.getDrain()).reduce(0, (value, current) -> value += current / 10);

		if (pointsToRemove > 0) {
			pointsToRemove /= 1 + 0.035 * player.playerBonus[11];
		}

		player.prayerPoint -= pointsToRemove;

		if (player.prayerPoint <= 0) {
			player.prayerPoint = 1.0 + player.prayerPoint;

			if (player.getSkill().getLevel(Skill.PRAYER) >= 1) {
				player.getSkill().decrementLevel(Skill.PRAYER);
			} else {
				player.sendMessage("You have run out of Prayer points!");
				player.getSkill().setLevel(5, 0);
				clear();
			}
		}
	}

	public void clear() {
		for (Prayer prayer : NormalPrayer.PRAYERS)
			deactivate(prayer);
		for (Prayer prayer : CursedPrayer.PRAYERS)
			deactivate(prayer);
	}

	public Optional<Prayer> buttonPressed(int buttonId) {
		for (NormalPrayer prayer : NormalPrayer.PRAYERS) {
			if (prayer.getButtonId() == buttonId) {
				return Optional.of(prayer);
			}
		}

		for (CursedPrayer prayer : CursedPrayer.PRAYERS) {
			if (prayer.getButtonId() == buttonId) {
				return Optional.of(prayer);
			}
		}

		return Optional.empty();
	}

}