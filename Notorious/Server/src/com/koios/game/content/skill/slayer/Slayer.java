package com.koios.game.content.skill.slayer;

import com.koios.Constants;
import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.content.dialogue.Dialogue;
import com.koios.game.content.dialogue.impl.util.SimpleChoice;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.slayer.assignments.Assignment;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.ConfigKeys;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendConfigPacket;
import com.koios.network.out.impl.SendRemoveInterfacePacket;
import com.koios.util.NumberUtil;
import com.koios.util.StringUtil;

/**
 * Handles the Slayer skill.
 *
 * @author Ben Stulpin
 */
public class Slayer {

	/**
	 * The player.
	 */
	private final Player player;

	/**
	 * The player's co-op partner.
	 */
	private Player partner;

	/**
	 * The player's awaiting co-op partner.
	 */
	private Player awaitingPartner;

	/**
	 * The player's co-op requested by.
	 */
	private Player requestedBy;

	/**
	 * The assignment.
	 */
	private Assignment assignment = null;

	/**
	 * The assignment kills left.
	 */
	private int killsLeft;

	/**
	 * The master who gave the assignment.
	 */
	private Master master;

	/**
	 * Creates a new slayer instance for the player.
	 *
	 * @param player the player.
	 */
	public Slayer(Player player) {
		this.player = player;
	}

	/**
	 * We accept the partnership request.
	 *
	 * @param other the partner.
	 */
	public void acceptPartnership(Player other) {
		/**
		 * We check if the partner exists.
		 */
		if (other == null || !other.isActive() || other.isDisconnected()) {
			player.sendMessage("The co-op request timed out.");
			return;
		}

		/**
		 * We get the partner's slayer instance.
		 */
		Slayer slayer = other.getSlayer();

		/**
		 * We check if the player has found a new awaiting partner.
		 */
		if (slayer.getAwaitingPartner() != player) {
			player.sendMessage("This person has requested somebody else.");
			player.sendMessage("The request has been cancelled.");
			return;
		}

		/**
		 * We check if the player has found another partner.
		 */
		if (slayer.getPartner() != null && slayer.getPartner() != player) {
			player.sendMessage("This person has found a different partner.");
			player.sendMessage("The request has been cancelled.");
			return;
		}

		/**
		 * We get the kill count.
		 */
		int kills = Math.max(killsLeft, slayer.killsLeft);

		/**
		 * We set the partners.
		 */
		setPartner(other);
		slayer.setPartner(player);

		/**
		 * We set the kills left.
		 */
		setKillsLeft(kills);
		slayer.setKillsLeft(kills);

		/**
		 * We clear the awaiting and requested.
		 */
		setRequestedBy(null);
		slayer.setAwaitingPartner(null);

		/**
		 * We send the accept messages.
		 */
		other.sendMessage(player.getName() + " has accepted your co-op request.");
		player.sendMessage("You have accepted " + other.getName() + "'s co-op request.");
	}

	/**
	 * Assigns a task.
	 */
	public boolean assignTask(int masterId) {
		Master master = Master.forId(masterId);
		int index = NumberUtil.inclusiveRandom(master.getTasks().length - 1);
		int amount = NumberUtil.inclusiveRandom(master.getMaximumAmount());
		int increase = partner == null ? 10 : NumberUtil.inclusiveRandom(master.getMaximumAmount()) / 2 + 10;

		/**
		 * Check if slayer level is lower than slayer level required
		 */
		while (!canPartyFight(Assignment.of(master.getTasks()[index]).getLevelRequired())) {
			index = 0;
		}

		Assignment assignment = Assignment.of(master.getTasks()[index]);

		/**
		 * We check if the player has a partner.
		 */
		if (partner != null) {
			/**
			 * We check if the partner reaches the requirements to obtain a task from this master.
			 */
			if (partner.getSkill().getCombatLevel(true) < master.getLevel()) {
				SimpleDialogues.sendMobStatement(player, masterId, "It appears that your partner does not meet the", "requirements to obtain a task from me.");
				return false;
			}

			/**
			 * The partner's slayer instance.
			 */
			Slayer slayer = partner.getSlayer();

			/**
			 * We set the assignment.
			 */
			slayer.setAssignment(assignment);

			/**
			 * We set the amount to kill.
			 */
			slayer.setKillsLeft(increase + amount);

			/**
			 * We set the master who assigned the task.
			 */
			slayer.setMaster(master);

			/**
			 * We send the message of the assigned task to the partner.
			 */
			partner.sendMessage("@dre@Your partner has received a new slayer task.");
			partner.sendMessage("@dre@Your new task is to kill " + slayer.getKillsLeft() + " " + StringUtil.capitalize(slayer.getAssignment().getMobName()) + "(s).");
		}

		/**
		 * We set the assignment.
		 */
		setAssignment(assignment);

		/**
		 * We set the amount to kill.
		 */
		setKillsLeft(increase + amount);

		/**
		 * We set the master who assigned the task.
		 */
		setMaster(master);
		return true;
	}

	/**
	 * Test if the represented player can fight the mob.
	 *
	 * @param mob The mob to be attacked.
	 * @return {@code true} if the {@code mob} can be attacked by the represented {@link Player}.
	 */
	public boolean canFight(Mob mob) {
		int slayerLevel = player.getSkill().getLevel(Skill.SLAYER);
		return slayerLevel >= mob.getDefinition().getSlayerRequirement();
	}

	/**
	 * Used to check if the slayer party can fight a determined monster
	 */
	private boolean canPartyFight(int levelRequired) {
		if (partner == null) {
			if (player.getSkill().getLevelForExperience(Skill.SLAYER) >= levelRequired) {
				return true;
			}
		} else {
			if (player.getSkill().getLevelForExperience(Skill.SLAYER) >= levelRequired && partner.getSkill().getLevelForExperience(Skill.SLAYER) >= levelRequired) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the assignment.
	 *
	 * @return the assignment.
	 */
	public Assignment getAssignment() {
		return assignment;
	}

	/**
	 * Gets the awaiting partner.
	 *
	 * @return the awaiting partner.
	 */
	public Player getAwaitingPartner() {
		return awaitingPartner;
	}

	/**
	 * Gets the kills left.
	 *
	 * @return the kills left.
	 */
	public int getKillsLeft() {
		return killsLeft;
	}

	/**
	 * Gets the master.
	 *
	 * @return the master.
	 */
	public Master getMaster() {
		return master;
	}

	/**
	 * Gets the partner.
	 *
	 * @return the partner.
	 */
	public Player getPartner() {
		return partner;
	}

	/**
	 * Gets the requested by.
	 *
	 * @return the requested by.
	 */
	public Player getRequestedBy() {
		return requestedBy;
	}

	/**
	 * Handles the Master's talk option.
	 *
	 * @param id the npc id.
	 */
	public boolean handleMaster(int id) {
		Master master = Master.forId(id);

		/**
		 * We check if the npc is a master.
		 */
		if (master == null) {
			return false;
		}

		/**
		 * We check if the player reaches the requirements to talk to the master.
		 */
		if (player.getSkill().getCombatLevel(false) < master.getLevel()) {
			player.sendMessage("This slayer master doesn't seem to want to speak with you right now...");
			return false;
		}

		/**
		 * We send the master dialogue.
		 */
		player.getDH().sendDialogues(50, id);
		return true;
	}

	/**
	 * Awards the player slayer experience.
	 *
	 * @param mob the mob.
	 */
	public void mobDeathHook(Mob mob) {
		if (assignment == null) {
			return;
		}

		/**
		 * The lowercase-formatted mob name.
		 */
		String slayerKey = mob.getDefinition().getSlayerKey();

		/**
		 * We check if the master does not exist but the assignment does.
		 */
		if (master == null && slayerKey.equalsIgnoreCase(assignment.getSlayerKey())) {
			player.sendMessage("Your slayer assignment has been clear due to a glitch, please receive a new one.");
			setAssignment(null);
			setKillsLeft(0);
			return;
		}

		/**
		 * We check if the npc is the player's assigned task.
		 */
		if (!slayerKey.equalsIgnoreCase(assignment.getSlayerKey())) {
			return;
		}

		/**
		 * The points to give.
		 */
		int points = partner == null ? master.getPoints() : master.getPoints() / 2;

		/**
		 * We check if the player has a co-op partner.
		 */
		if (partner != null) {
			/**
			 * We get the partner's slayer instance.
			 */
			Slayer slayer = partner.getSlayer();

			/**
			 * We remove a kill from the partner's kill left count.
			 */
			slayer.setKillsLeft(killsLeft - 1);

			/**
			 * We check if the player finished the assignment.
			 */
			if (slayer.getKillsLeft() == 0) {

				/**
				 * We reward the partner points.
				 */
				partner.slayerPoints += points;

				/**
				 * We set the assignment to null.
				 */
				slayer.setAssignment(null);

				/**
				 * We send the finished message.
				 */
				partner.sendMessage("Your partner has completed the slayer task! Speak to a slayer master to get a new one.");

				/**
				 * We send the points received message.
				 */
				partner.sendMessage("@red@You received " + points + " slayer points for completing this task.");

				/**
				 * We update the slayer points line.
				 */
				player.getSession().send(new SendConfigPacket(ConfigKeys.SLAYER_POINTS, player.slayerPoints));
			}
		}

		/**
		 * Awards the player EXP for killing an assigned monster.
		 */
		player.getSkill().addExperience(Skill.SLAYER, (int) (0.1 * mob.getDefinition().getHitpoints() * Constants.SLAYER_EXPERIENCE));

		/**
		 * We check if the player finished the assignment.
		 */
		if (--killsLeft < 1) {
			/**
			 * We set the assignment to null.
			 */
			setAssignment(null);

			/**
			 * We reward the player points.
			 */
			player.slayerPoints += points;

			/**
			 * We send the finished message.
			 */
			player.sendMessage("You have completed your slayer task! Speak to a slayer master to get a new one.");
			AchievementManager.performed(player, Achievement.COMPLETE_SLAYER);

			/**
			 * We send the points received message.
			 */
			player.sendMessage("@red@You received " + points + " slayer points for completing this task.");

			/**
			 * We update the slayer points line.
			 */
			player.getSession().send(new SendConfigPacket(ConfigKeys.SLAYER_POINTS, player.slayerPoints));
		}
	}

	public void sendKillsLeft() {
		if (assignment == null) {
			player.sendMessage("You do not have a slayer assignment. You can talk to a slayer master to obtain one.");
		} else {
			String target = assignment.getMobName().toLowerCase();
			player.sendMessage(String.format("Your current assignment is: %ss; only %d more to go.", target, killsLeft));
		}
	}

	/**
	 * We send the partnership request.
	 *
	 * @param awaiting the awaiting partner.
	 */
	public boolean sendRequest(Player awaiting) {
		/**
		 * We check if the partner exists.
		 */
		if (awaiting == null || !awaiting.isActive() || awaiting.isDisconnected()) {
			return false;
		}

		/**
		 * We get the awaiting partner's slayer instance.
		 */
		Slayer slayer = awaiting.getSlayer();

		/**
		 * We check if the player or if the awaiting partner is in the wild.
		 */
		if (player.inWild() || awaiting.inWild()) {
			player.sendMessage("You cannot send a slayer co-op request in the wilderness!");
			return false;
		}

		/**
		 * We check if the awaiting partner is busy.
		 */
		if (awaiting.getBankField().isBanking() || awaiting.isTrading() || awaiting.currentShop > 0) {
			player.sendMessage("Other player is busy at the moment.");
			return false;
		}

		/**
		 * We check if the awaiting is already being requested.
		 */
		if (slayer.getRequestedBy() != null) {
			player.sendMessage("This player is already awaiting a partner request!");
			return false;
		}

		/**
		 * We check if the awaiting partner already has a partner.
		 */
		if (slayer.getPartner() != null && slayer.getPartner() != player) {
			player.sendMessage("This player already has a co-op partner!");
			return false;
		}

		/**
		 * We check if the awaiting partner is already the player's partner.
		 */
		if (slayer.getPartner() != null && partner == awaiting) {
			player.dialogue().start(new Dialogue(player) {
				@Override
				protected void start() {
					sendStatement("This player is already your co-op partner.", "Do you want to end your co-op partnership?");
				}

				@Override
				protected void next() {
					stop();

					player.dialogue().start(new SimpleChoice(player, "Yes, end partnership.", p -> {
						player.getSlayer().getPartner().sendMessage("@red@" + player.getName() + " has ended the co-op session.");
						player.sendMessage("You ended the co-op session.");
						player.getSlayer().getPartner().getSlayer().setPartner(null);
						player.getSlayer().setPartner(null);
						player.getSession().send(new SendRemoveInterfacePacket());
					} , "No thanks.", p -> stop()));
				}
			});
			return false;
		}

		/**
		 * We check if the players have the same assignment.
		 */
		if (slayer.getAssignment() != null && !slayer.getAssignment().equals(assignment) || !(slayer.getAssignment() == null && player.getSlayer().getAssignment() == null)) {
			player.sendMessage("You both must have the same assignment or have no assignment to partner up!");
			return false;
		}

		/**
		 * We set the awaiting partner.
		 */
		setAwaitingPartner(awaiting);

		/**
		 * We set the requested by.
		 */
		slayer.setRequestedBy(player);

		/**
		 * We loot the accept/decline dialogue.
		 */
		awaiting.getDH().sendDialogues(57, -1);

		/**
		 * We send the request message.
		 */
		player.sendMessage("Sending slayer co-op request...");
		return true;
	}

	/**
	 * Sets the assignment.
	 *
	 * @param assignment the assignment.
	 */
	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	/**
	 * Sets the awaiting partner.
	 *
	 * @param awaitingPartner the awaiting partner.
	 */
	public void setAwaitingPartner(Player awaitingPartner) {
		this.awaitingPartner = awaitingPartner;
	}

	/**
	 * Sets the kills left.
	 *
	 * @param killsLeft the kills left.
	 */
	public void setKillsLeft(int killsLeft) {
		this.killsLeft = killsLeft;
	}

	/**
	 * Sets the master.
	 *
	 * @param master the master.
	 */
	public void setMaster(Master master) {
		this.master = master;
	}

	/**
	 * Sets the partner.
	 *
	 * @param partner the partner.
	 */
	public void setPartner(Player partner) {
		this.partner = partner;
	}

	/**
	 * We set the requested by.
	 *
	 * @param requestedBy the requested by.
	 */
	public void setRequestedBy(Player requestedBy) {
		this.requestedBy = requestedBy;
	}

}
