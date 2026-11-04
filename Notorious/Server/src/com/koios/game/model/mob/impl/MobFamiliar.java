package com.koios.game.model.mob.impl;

import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.summoning.SummoningData.FamiliarAbilitys;
import com.koios.game.content.skill.summoning.SummoningData.SummonPouch;
import com.koios.game.model.Graphic;
import com.koios.game.model.Location;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.mob.MobManager;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendClientScriptPacket;
import com.koios.network.out.impl.SendSidebarInterfacePacket;

public class MobFamiliar extends Mob {

	private SummonPouch pouchDefinition = null;
	private FamiliarAbilitys ability = null;
	private int abilityTime = 0;

	public MobFamiliar(int id, Location loc) {
		super(id, loc);
		pouchDefinition = SummonPouch.getByFamiliar(id);
		ability = FamiliarAbilitys.get(id);
	}

	@Override
	public void death() {
		Player mobSummoner = summoner == null ? null : summoner.toPlayer();

		if (mobSummoner != null) {
			if (mobSummoner.getSummoning().getFamiliarInventory() != null) {
				for (Item item : mobSummoner.getSummoning().getFamiliarInventory().toArray()) {
					if (item == null || item.getCount() <= 0) {
						continue;
					}

					GroundItemManager.spawnNew(mobSummoner, item.getId(), getX(), getY(), item.getCount());
				}

				mobSummoner.sendMessage("Your familiar has dropped its inventory items.");
				mobSummoner.getSummoning().getFamiliarInventory().clear();
			}

			mobSummoner.getSummoning().reset();
			mobSummoner.getSession().send(new SendSidebarInterfacePacket(16, -1));
			mobSummoner.getSession().send(new SendClientScriptPacket(1, "false-null-false"));
		}

		super.death();
	}

	public final SummonPouch getPouchDefinitions() {
		return pouchDefinition;
	}

	public final FamiliarAbilitys getAbility() {
		return ability;
	}

	@Override
	public boolean isFamiliar() {
		return true;
	}

	@Override
	public void update() {
		super.update();

		if (MobManager.mobs[index] == null) {
			return;
		}

		if (ability != null && ability.getLoopDuration() > 0) {
			if (abilityTime > 0) {
				abilityTime--;
			}

			if (abilityTime == 0) {
				Player mobSummoner = summoner == null ? null : summoner.toPlayer();

				if (mobSummoner != null) {
					int skillId = ability.getSkillToBoost();
					int boostAmount = ability.getBoostAmount();

					if (getId() == 6813) {
						boostAmount = (int) (mobSummoner.getSkill().getLevelForExperience(Skill.HITPOINTS) * 0.02);
					}

					mobSummoner.getSkill().incrementLevel(skillId, boostAmount);

					if (mobSummoner.getSkill().getLevel(skillId) > (skillId == 3 ? mobSummoner.getMaxHitpoints() : mobSummoner.getSkill().getLevelForExperience(skillId))) {
						mobSummoner.getSkill().setLevel(skillId, skillId == 3 ? mobSummoner.getMaxHitpoints() : mobSummoner.getSkill().getLevelForExperience(skillId));
					}

					if (getId() == 6813) {
						mobSummoner.playGraphics(new Graphic(1507));
					}

					abilityTime = ability.getLoopDuration();
				}
			}
		}

		if (isInWilderness()) {
			if (getId() == pouchDefinition.getFamiliarId()) {
				requestTransform(pouchDefinition.getFamiliarId() + 1);
				setId(pouchDefinition.getFamiliarId() + 1);
			}
		} else if (getId() == pouchDefinition.getFamiliarId() + 1) {
			requestTransform(pouchDefinition.getFamiliarId());
			setId(pouchDefinition.getFamiliarId());
		}
	}

}