package com.koios.network.out.impl;

import com.koios.network.GamePacketBuilder;
import com.koios.network.Session;
import com.koios.network.out.OutboundPacket;

public class SendCounterExpPacket implements OutboundPacket {

	private final int skill;
	private final int experience;

	public SendCounterExpPacket(int skill, int experience) {
		this.skill = skill;
		this.experience = experience;
	}

	@Override
	public void execute(Session client) {
		client.getPlayer().totalExperience += experience;
		client.getPlayer().write(new GamePacketBuilder(124).writeByte(skill).writeInt(experience).writeInt(client.getPlayer().totalExperience));
	}

}
