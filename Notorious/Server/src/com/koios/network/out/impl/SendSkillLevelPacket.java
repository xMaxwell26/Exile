package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendSkillLevelPacket implements OutboundPacket {

	private final int skill;

	public SendSkillLevelPacket(int skill) {
		this.skill = skill;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().write(new GamePacketBuilder(134).writeByte(skill).writeSingleInt(client.getPlayer().getSkill().getExperience(skill))
				.writeByte(client.getPlayer().getSkill().getLevel(skill)));
		client.send(new SendStringPacket(3984, "Total Lvl: " + client.getPlayer().getSkill().getTotalLevel()));
	}

}
