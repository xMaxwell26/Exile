package com.koios.network.out.impl;

import com.koios.game.model.player.ActionSender;
import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendSkillGoalPacket implements OutboundPacket {

	private final int skillId;

	public SendSkillGoalPacket(int skillId) {
		this.skillId = skillId;
	}

	@Override
	public void execute(Session session) {
		GamePacketBuilder bldr = new GamePacketBuilder(25);
		bldr.writeByte(skillId);
		bldr.writeSingleInt(session.getPlayer().getSkill().getGoalData(skillId)[0]);
		bldr.writeByte(session.getPlayer().getSkill().getGoalData(skillId)[1]);
		bldr.writeSingleInt(session.getPlayer().getSkill().getGoalData(skillId)[2]);
		session.getPlayer().write(bldr);
		ActionSender.sendSkill(session.getPlayer(), skillId);
	}

}
