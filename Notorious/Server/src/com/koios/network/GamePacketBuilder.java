package com.koios.network;

import com.koios.network.GamePacket.PacketType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Manages writing packet information that will be sent later on to the netty's channel.
 *
 * @author relex lawl
 */

public final class GamePacketBuilder {

	/**
	 * The array containing bit mask values for writing bits.
	 */
	private static final int[] BIT_MASK = new int[32];

	/**
	 * Populates the bit mask values in the array.
	 */
	static {
		for (int i = 0; i < BIT_MASK.length; i++) {
			BIT_MASK[i] = (1 << i) - 1;
		}
	}

	/**
	 * The PacketBuilder constructor.
	 *
	 * @param opcode The packet id to write information for.
	 * @param packetType The packetType of packet being written.
	 */
	public GamePacketBuilder(int opcode, PacketType packetType) {
		this.opcode = opcode;
		this.packetType = packetType;
	}

	/**
	 * The PacketBuilder constructor.
	 *
	 * @param opcode The packet id to write information for.
	 */
	public GamePacketBuilder(int opcode) {
		this(opcode, PacketType.FIXED);
	}

	/**
	 * The PacketBuilder constructor.
	 */
	public GamePacketBuilder() {
		this(-1);
	}

	/**
	 * The packet id.
	 */
	private final int opcode;

	/**
	 * The packet packetType.
	 */
	private final PacketType packetType;

	/**
	 * The packet's current bit position.
	 */
	private int bitPosition;

	/**
	 * The buffer used to write the packet information.
	 */
	private final ByteBuf buffer = Unpooled.buffer();

	/**
	 * Writes a byte value on the packet.
	 *
	 * @param value The byte value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeByte(int value) {
		buffer.writeByte((byte) value);
		return this;
	}

	/**
	 * Writes a packetType-A byte value on the packet.
	 *
	 * @param value The byte value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeByteA(int value) {
		buffer.writeByte(value + 128);
		return this;
	}

	/**
	 * Writes a reversed byte value on the packet.
	 *
	 * @param value The byte value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeByteC(int value) {
		buffer.writeByte(-value);
		return this;
	}

	/**
	 * Writes a packetType-S byte value on the packet.
	 *
	 * @param value The byte value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeByteS(int value) {
		buffer.writeByte(128 - value);
		return this;
	}

	/**
	 * Writes a byte array value on the packet.
	 *
	 * @param value The byte array value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeByteArray(byte[] bytes) {
		buffer.writeBytes(bytes);
		return this;
	}

	/**
	 * Writes a byte array value on the packet.
	 *
	 * @param value The byte array value to write on the packet.
	 * @param offset The byte array offset.
	 * @param length The byte array length.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeByteArray(byte[] bytes, int offset, int length) {
		buffer.writeBytes(bytes, offset, length);
		return this;
	}

	/**
	 * Writes a certain amount of byte values on the packet.
	 *
	 * @param value The value of the byte to write.
	 * @param amount The amount of the bytes to write.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeBytes(int value, int amount) {
		for (; amount > 0; amount--) {
			buffer.writeByte(value);
		}
		return this;
	}

	/**
	 * Writes a reversed byte array onto the packet.
	 *
	 * @param bytes The byte array.
	 * @param offset The offset to write the byte array in.
	 * @param length The amount of bytes.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeReversedBytes(byte[] bytes, int offset, int length) {
		for (int i = offset + length - 1; i >= offset; i--) {
			buffer.writeByte(bytes[i]);
		}
		return this;
	}

	/**
	 * Writes a reversed packetType-A byte array onto the packet.
	 *
	 * @param bytes The byte array.
	 * @param offset The offset to write the byte array in.
	 * @param length The amount of bytes.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeReverseA(byte[] bytes, int offset, int length) {
		for (int i = offset + length - 1; i >= offset; i--) {
			writeByteA(bytes[i]);
		}
		return this;
	}

	/**
	 * Writes a short value onto the packet.
	 *
	 * @param value The value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeShort(int value) {
		buffer.writeShort((short) value);
		return this;
	}

	/**
	 * Writes an int value onto the packet.
	 *
	 * @param value The value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeInt(int value) {
		buffer.writeInt(value);
		return this;
	}

	/**
	 * Write a single int value onto the packet.
	 *
	 * @param value The value to write as bytes to form a single int.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeSingleInt(int value) {
		writeByte(value >> 8).writeByte(value).writeByte(value >> 24).writeByte(value >> 16);
		return this;
	}

	/**
	 * Write a double int value onto the packet.
	 *
	 * @param value The value to write as bytes to form a double int.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeDoubleInt(int value) {
		writeByte(value >> 16).writeByte(value >> 24).writeByte(value).writeByte(value >> 8);
		return this;
	}

	/**
	 * Write a triple int value onto the packet.
	 *
	 * @param value The value to write as bytes to form a triple int.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeTripleInt(int value) {
		writeByte(value >> 16).writeByte(value >> 8).writeByte(value);
		return this;
	}

	/**
	 * Writes a little-endian int onto the packet.
	 *
	 * @param value The value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeLEInt(int value) {
		writeByte(value).writeByte(value >> 8).writeByte(value >> 16).writeByte(value >> 24);
		return this;
	}

	/**
	 * Writes a long value onto the packet.
	 *
	 * @param value The value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeLong(long value) {
		buffer.writeLong(value);
		return this;
	}

	/**
	 * Writes a string value onto the packet.
	 *
	 * @param string The string to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeString(String string) {
		buffer.writeBytes(string.getBytes());
		buffer.writeByte(10);
		return this;
	}

	/**
	 * Writes a short packetType-A value onto the packet.
	 *
	 * @param value The value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeShortA(int value) {
		writeByte(value >> 8).writeByte(value + 128);
		return this;
	}

	/**
	 * Writes a little-endian short value onto the packet.
	 *
	 * @param value The value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeLEShort(int value) {
		writeByte(value).writeByte(value >> 8);
		return this;
	}

	/**
	 * Writes a little-endian packetType-A short value onto the packet.
	 *
	 * @param value The value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeLEShortA(int value) {
		writeByte(value + 128).writeByte(value >> 8);
		return this;
	}

	/**
	 * Writes a smart value onto the packet.
	 *
	 * @param value The value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeSmart(int value) {
		if (value < 128) {
			buffer.writeByte(value);
		} else {
			buffer.writeShort(value + 32768);
		}
		return this;
	}

	/**
	 * Writes a signed smart value onto the packet.
	 *
	 * @param value The value to write on the packet.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeSignedSmart(int value) {
		if (value < 128) {
			buffer.writeByte(value + 64);
		} else {
			buffer.writeShort(value + 49152);
		}
		return this;
	}

	/**
	 * Writes {@code buffer}'s bytes onto this PacketBuilder's buffer.
	 *
	 * @param buffer The buffer to take values from.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeBuffer(ByteBuf buffer) {
		this.buffer.writeBytes(buffer);
		return this;
	}

	/**
	 * Starts bit access, whether it's a Bit or Byte.
	 *
	 * @param packetType The packet's access packetType.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder initializeAccess(AccessType type) {
		switch (type) {
		case BIT:
			bitPosition = buffer.writerIndex() * 8;
			break;
		case BYTE:
			buffer.writerIndex((bitPosition + 7) / 8);
			break;
		}
		return this;
	}

	public GamePacketBuilder writeBits(boolean flag) {
		return writeBits(1, flag ? 1 : 0);
	}

	/**
	 * Writes a value to a certain number of bits being sent from the client.
	 *
	 * @param numBits The number of bits being send.
	 * @param value The value of the bits to send.
	 * @return The PacketBuilder instance.
	 */
	public GamePacketBuilder writeBits(int numBits, final int value) {
		if (!buffer.hasArray()) {
			throw new UnsupportedOperationException("The ChannelBuffer implementation must support array() for bit usage.");
		}

		int bytes = (int) Math.ceil(numBits / 8D) + 1;
		buffer.ensureWritable((bitPosition + 7) / 8 + bytes);

		final byte[] buffer = this.buffer.array();

		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;

		for (; numBits > bitOffset; bitOffset = 8) {
			buffer[bytePos] &= ~BIT_MASK[bitOffset];
			buffer[bytePos++] |= value >> numBits - bitOffset & BIT_MASK[bitOffset];
			numBits -= bitOffset;
		}
		if (numBits == bitOffset) {
			buffer[bytePos] &= ~BIT_MASK[bitOffset];
			buffer[bytePos] |= value & BIT_MASK[bitOffset];
		} else {
			buffer[bytePos] &= ~(BIT_MASK[numBits] << bitOffset - numBits);
			buffer[bytePos] |= (value & BIT_MASK[numBits]) << bitOffset - numBits;
		}
		return this;
	}

	public GamePacketBuilder writeBit(boolean flag) {
		writeBits(1, flag ? 1 : 0);
		return this;
	}

	/**
	 * Transforms the PacketBuilder into an actual Packet.
	 *
	 * @return The Packet instance of this PacketBuilder.
	 */
	public GamePacket toPacket() {
		return new GamePacket(opcode, packetType, buffer);
	}

	/**
	 * Represents an access packetType the packet can have.
	 *
	 * @author relex lawl
	 */
	public enum AccessType {
		BIT, BYTE,
	}
}
