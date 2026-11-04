package com.koios.network.codec;

import com.koios.network.GamePacket;
import com.koios.network.Session;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class PacketEncoder extends MessageToByteEncoder<GamePacket> {

	private final Session session;

	public PacketEncoder(Session session) {
		this.session = session;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, GamePacket packet, ByteBuf buffer) {
		buffer.writeByte(session.encipherPacketOpcode(packet.getOpcode()));

		switch (packet.getType()) {
		case BYTE:
			buffer.writeByte(packet.getSize());
			break;
		case SHORT:
			buffer.writeShort(packet.getSize());
			break;

		default: // only variable byte and short packets need their lengths written, ignore others.
			break;
		}

		buffer.writeBytes(packet.getBuffer());
	}

}