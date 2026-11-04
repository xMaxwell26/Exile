package com.koios.game.content.combat.mob;

import com.koios.game.model.EntityCharacter;
import com.koios.game.model.mob.Mob;

public interface MobAttack {

	public void startAttack(Mob mob, EntityCharacter target);

	public void applyDamage(Mob mob, EntityCharacter target);

}
