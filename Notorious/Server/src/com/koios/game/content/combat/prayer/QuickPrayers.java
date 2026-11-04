package com.koios.game.content.combat.prayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.koios.game.content.skill.Skill;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendOpenSideTabPacket;
import com.koios.network.out.impl.SendSidebarInterfacePacket;

import java.util.Optional;

public class QuickPrayers {

	private final Player player;

	public QuickPrayers(Player player) {
		this.player = player;
		NormalPrayer.PRAYERS.forEach(p -> normalPrayers.put(p, false));
		CursedPrayer.PRAYERS.forEach(p -> cursedPrayers.put(p, false));
	}

	private final Map<NormalPrayer, Boolean> normalPrayers = new HashMap<>();
	private final Map<CursedPrayer, Boolean> cursedPrayers = new HashMap<>();

	public void flagQuickPrayer(int button) {

		switch (button) {
		case 19136:
			if (player.altarPrayed == 0) {
				for (Entry<NormalPrayer, Boolean> entry : normalPrayers.entrySet()) {
					NormalPrayer key = entry.getKey();
					boolean state = entry.getValue().booleanValue();
					if (state)
						player.getPrayers().activate(key);
				}
			} else {
				for (Entry<CursedPrayer, Boolean> entry : cursedPrayers.entrySet()) {
					CursedPrayer key = entry.getKey();
					boolean state = entry.getValue().booleanValue();
					if (state)
						player.getPrayers().activate(key);
				}
			}
			break;
		case 19137:
			player.getSession().send(new SendSidebarInterfacePacket(5, player.altarPrayed == 1 ? 17234 : 17200));
			player.getSession().send(new SendOpenSideTabPacket(5));
			if (player.altarPrayed == 0) {
				for (Entry<NormalPrayer, Boolean> entry : normalPrayers.entrySet()) {
					NormalPrayer key = entry.getKey();
					boolean state = entry.getValue().booleanValue();
					player.getSession().send(new SendConfigPacket(630 + key.getId(), state ? 1 : 0));
				}
			} else {
				for (Entry<CursedPrayer, Boolean> entry : cursedPrayers.entrySet()) {
					CursedPrayer key = entry.getKey();
					boolean state = entry.getValue().booleanValue();
					player.getSession().send(new SendConfigPacket(630 + key.getId(), state ? 1 : 0));
				}
			}
			break;
		case 67079:
			player.getSession().send(new SendSidebarInterfacePacket(5, player.altarPrayed == 1 ? 22500 : 5608));
			break;
		}
		Optional<NormalPrayer> prayer = NormalPrayer.fromQuickButton(button);
		prayer.ifPresent(this::flag);

		Optional<CursedPrayer> cursedPrayer = CursedPrayer.fromQuickButton(button);
		cursedPrayer.ifPresent(this::flag);
	}

	private void flag(CursedPrayer prayer) {
		if (player.getSkill().getLevelForExperience(Skill.PRAYER) < prayer.getLevelRequired()) {
			player.sendMessage("You need a Prayer level of " + prayer.getLevelRequired() + " to select that quick Prayer.");
			return;
		}

		for (Prayer active : cursedPrayers.keySet()) {
			if (prayer != active && (prayer.getFlags() & active.getFlags()) != 0) {
				player.getSession().send(new SendConfigPacket(630 + active.getId(), 0));
			}
		}

		boolean set = cursedPrayers.get(prayer).booleanValue();
		cursedPrayers.put(prayer, !set);
		player.getSession().send(new SendConfigPacket(630 + prayer.getId(), !set));
	}

	private void flag(NormalPrayer prayer) {
		if (player.getSkill().getLevelForExperience(Skill.PRAYER) < prayer.getLevelRequired()) {
			player.sendMessage("You need a Prayer level of " + prayer.getLevelRequired() + " to select that quick Prayer.");
			return;
		}

		for (Prayer active : normalPrayers.keySet()) {
			if (prayer != active && (prayer.getFlags() & active.getFlags()) != 0) {
				player.getSession().send(new SendConfigPacket(630 + active.getId(), 0));
			}
		}

		boolean set = normalPrayers.get(prayer).booleanValue();
		normalPrayers.put(prayer, !set);
		player.getSession().send(new SendConfigPacket(630 + prayer.ordinal(), !set));
	}

}
