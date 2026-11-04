package com.koios.network.in.impl;

import com.koios.game.content.skill.Skill;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.network.out.impl.SendSkillGoalPacket;

public class PacketSkillGoal implements IncomingPacketDecoder {

	@Override
	public void handle(Player player, GamePacket packet) {
		int skill = packet.readShort();
		int goal = -1;
		int type = 0;
		int initialExp = 0;
		
		if (packet.getOpcode() == 63) {
			type = packet.readByte();
			long val = packet.readLong();
			
			if (skill < 0 || skill >= Skill.SKILL_COUNT) {
				return;
			}

			goal = (int) (val > 200_000_000 || val < 0 ? type == 1 ? 200_000_000 : 1 : val);
			initialExp = player.getSkill().getExperience(skill);

			if (type == 0) {
				if (goal <= player.getSkill().getLevel(skill)) {
					return;
				}
			} else if (type == 1) {
				if (goal <= player.getSkill().getExperience(skill)) {
					return;
				}
			}
		}
		
		if (skill < 0 || skill >= Skill.SKILL_COUNT) {
			return;
		}

		player.getSkill().setGoalData(skill, goal, type, initialExp);
		player.getSession().send(new SendSkillGoalPacket(skill));
	}

}
