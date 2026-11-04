package com.koios.network.codec;

import java.util.List;
import java.util.Objects;

import com.koios.network.GamePacket;
import com.koios.network.Session;
import com.koios.network.GamePacket.PacketType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public final class PacketDecoder extends ByteToMessageDecoder {

	public static final int[] PACKET_SIZE = { 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, // 0
			0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
			0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
			0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
			2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
			0, 0, 0, 12, 0, 0, 0, 8, 8, 12, // 50
			8, 8, 2, 11, 0, 0, 0, 0, 0, 0, // 60
			6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
			0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
			0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
			0, 13, 0, -1, 0, 0, 0, 0, 0, 0, // 100
			0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
			1, 0, 6, 0, 0, 0, -1, 0, 2, 6, // 120
			0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
			2, 6, 0, 0, 0, 6, 0, 0, 0, 0, // 140
			0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
			0, 0, 0, 0, -1, -1, 0, 0, 0, 0, // 160
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
			0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
			0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
			-1, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
			4, 0, 0, 0, 8, 8, 0, 0, 10, 0, // 210
			0, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
			1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
			0, 4, 0, 0, 0, 0, -1, 0, -1, 4, // 240
			0, 0, 6, 6, 0, 0, 0 // 250
	};

	private static final int EMPTY_FRAME_LENGTH = 0;
	private static final int VARIABLE_BYTE_LENGTH = -1;
	private static final int VARIABLE_SHORT_LENGTH = -2;

	private enum State {
		DECODE_OPCODE, DECODE_LENGTH, DECODE_PAYLOAD
	}

	private final Session session;

	private int opcode;
	private int length;
	private PacketType type;
	private State state = State.DECODE_OPCODE;

	public PacketDecoder(Session session) {
		this.session = session;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
		if (buffer.isReadable() && state == State.DECODE_OPCODE) {
			int encryptedOpcode = buffer.readUnsignedByte();
			opcode = session.decipherPacketOpcode(encryptedOpcode);

			if (opcode < 0 || opcode >= PACKET_SIZE.length) {
				return;
			}

			length = PACKET_SIZE[opcode];
			type = PacketType.forLength(length);

			switch (length) {
			case EMPTY_FRAME_LENGTH:
				out.add(new GamePacket(opcode, PacketType.FIXED, Unpooled.EMPTY_BUFFER));
				break;

			case VARIABLE_BYTE_LENGTH:
			case VARIABLE_SHORT_LENGTH:
				checkpoint(State.DECODE_LENGTH);
				break;

			default:
				checkpoint(State.DECODE_PAYLOAD);
				break;
			}
		}

		if (state == State.DECODE_LENGTH) {
			int bytes = length == VARIABLE_BYTE_LENGTH ? Byte.BYTES : Short.BYTES;
			if (!buffer.isReadable(bytes)) {
				// The opcode and its cipher key were already consumed. Keep waiting
				// for this frame's length rather than decoding it as a new opcode.
				return;
			}

			length = 0;
			for (int i = 0; i < bytes; i++) {
				length |= buffer.readUnsignedByte() << 8 * (bytes - 1 - i);
			}

			checkpoint(State.DECODE_PAYLOAD);
		}

		if (state == State.DECODE_PAYLOAD) {
			if (!buffer.isReadable(length)) {
				return;
			}

			ByteBuf frameBuffer = buffer.readBytes(length);
			out.add(new GamePacket(opcode, type, frameBuffer));

			checkpoint(State.DECODE_OPCODE);
		}
	}

	private void checkpoint(State state) {
		this.state = Objects.requireNonNull(state, "state");
	}

}
