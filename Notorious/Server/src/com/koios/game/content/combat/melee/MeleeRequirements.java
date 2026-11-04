package com.koios.game.content.combat.melee;

import com.koios.Constants;
import com.koios.game.content.activity.fightpits.FightPits;
import com.koios.game.content.combat.Combat;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.player.Player;

public class MeleeRequirements {

	public static int getCombatDifference(int combat1, int combat2) {
		if (combat1 > combat2) {
			return combat1 - combat2;
		}

		if (combat2 > combat1) {
			return combat2 - combat1;
		}

		return 0;
	}

	public static int getRequiredDistance(Player player) {
		if (player.getFollowing().getInteracting() != null && player.getEF().canWalk(player, false) && !player.isMoving) {
			return 2;
		} else if (player.getFollowing().getInteracting() != null && player.getEF().canWalk(player, false) && player.isMoving) {
			return 3;
		} else {
			return 1;
		}
	}

	public static boolean testRequirements(Player player) {
		if (player.getCombat().getPreviousTarget() == null && player.getCombat().getTarget() == null) {
			return false;
		}

		EntityCharacter target = player.getCombat().getPreviousTarget() == null ? player.getCombat().getTarget() : player.getCombat().getPreviousTarget();

		if (target != null && target.isMob()) {
			return true;
		}

		if (target == null) {
			return false;
		}

		Player opponent = target.isPlayer() ? target.toPlayer() : null;

		if (opponent == null) {
			return false;
		}

		if (target.getIndex() == player.getIndex()) {
			return false;
		}

		if (player.getDuel().isDueling()) {

			if (!player.getDuel().getDuelingWith().equals(target.getName())) {
				player.setFaceIndex(target.getIndex(), target.isPlayer());
				player.sendMessage("This isn't your opponent!");
				return false;
			}
		}

		if (FightPits.getInstance().isParticipant(player)) {
			if (!FightPits.getInstance().isSignalSent()) {
				player.sendMessage("Please wait for the signal before attacking!");
				player.getMovementHandler().reset();
				Combat.resetCombat(player);
				return false;
			}

			if (!FightPits.getInstance().isParticipant(target.toPlayer())) {
				player.getMovementHandler().reset();
				Combat.resetCombat(player);
				return false;
			}

			return true;
		}

		if (!opponent.getMinigame().inMinigame() || !opponent.getMinigame().getCurrentMinigame().getMinigameName().equals("DUEL_ARENA") || player.isSkilling[Skill.AGILITY]) {
			if (!opponent.inWild()) {
				player.sendMessage("Your opponent is not in the wilderness!");
				player.setFacePosition(opponent.getX(), opponent.getY());
				player.getMovementHandler().reset();
				Combat.resetCombat(player);
				return false;
			}
		}

		if (!player.getMinigame().inMinigame() || !player.getMinigame().getCurrentMinigame().getMinigameName().equals("DUEL_ARENA")) {
			if (Constants.COMBAT_LEVEL_DIFFERENCE) {
				if (player.inWild() && !player.inFunPK()) {
					int combatDif1 = getCombatDifference(player.getSkill().getCombatLevel(false), opponent.getSkill().getCombatLevel(false));

					if (combatDif1 > player.wildLevel || combatDif1 > opponent.wildLevel) {
						player.setFacePosition(opponent.getX(), opponent.getY());
						player.sendMessage("Your combat level difference is too great to attack that player here.");
						player.getMovementHandler().reset();
						Combat.resetCombat(player);
						return false;
					}
				} else {
					int myCB = player.getSkill().getCombatLevel(false);
					int pCB = opponent.getSkill().getCombatLevel(false);

					if (myCB > pCB + 12 || myCB < pCB - 12) {
						player.setFacePosition(opponent.getX(), opponent.getY());
						player.sendMessage("You can only fight players in your combat range!");
						player.getMovementHandler().reset();
						Combat.resetCombat(player);
						return false;
					}
				}
			}
		}

		if (!target.inMulti()) {
			if (target.getCombat().getAggressor() != null) {
				// single combat zones
				if (target.getCombat().getAggressor().getIndex() != player.getIndex()) {
					player.setFacePosition(target.getX(), target.getY());
					player.sendMessage("That player is already in combat.");
					player.getMovementHandler().reset();
					Combat.resetCombat(player);
					return false;
				}
			}

			if (player.getCombat().getAggressor() != null) {
				if (target.getIndex() != player.getCombat().getAggressor().getIndex()) {
					player.setFacePosition(target.getX(), target.getY());
					player.sendMessage("You are already in combat.");
					player.getMovementHandler().reset();
					Combat.resetCombat(player);
					return false;
				}
			}
		}

		return true;
	}

}
