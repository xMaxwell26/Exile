package com.koios.network;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import com.koios.Constants;
import com.koios.GameEngine;
import com.koios.game.model.player.Player;
import com.koios.network.out.OutboundPacket;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;

public class Session {

	private Player player;
	private final Queue<GamePacket> packets;
	private final SocketChannel channel;
	private long clientHash;

	private ISAACCipher encodingRandom;
	private ISAACCipher decodingRandom;

	public Session(SocketChannel session) {
		channel = Objects.requireNonNull(session, "null session");
		packets = new LinkedList<>();
	}

	public void seedCiphers(int[] seed) {
		int[] copy = seed.clone();

		decodingRandom = new ISAACCipher(copy);
		Arrays.setAll(copy, index -> copy[index] += 50);
		encodingRandom = new ISAACCipher(copy);
	}

	public int decipherPacketOpcode(int opcode) {
		Objects.requireNonNull(decodingRandom, "The decoding cipher were not set!");
		return opcode - decodingRandom.nextKey() & 0xFF;
	}

	public int encipherPacketOpcode(int opcode) {
		Objects.requireNonNull(decodingRandom, "The encoding cipher were not set!");
		return opcode + encodingRandom.nextKey() & 0xFF;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void processQueuedPackets() {
		for (;;) {
			synchronized (packets) {
				GamePacket packet = packets.poll();
				if (packet == null) {
					break;
				}

				try {
					ReceivedPacketExecutor.processPacket(player, packet);
					player.setAttribute("timeout", 0);
				} catch (Exception cause) {
					GameEngine.queueLogout(player);
					cause.printStackTrace();
				}
			}
		}
	}

	public ChannelFuture write(Object msg) {
		return channel.writeAndFlush(msg);
	}
	
	public void write(GamePacketBuilder bldr) {
		write(bldr.toPacket());
	}

	public void write(GamePacket packet) {
		channel.writeAndFlush(packet);
	}

	public void queuePacket(GamePacket packet) {
		if (packets.size() < Constants.MAX_PACKETS_PROCESSED) {
			synchronized (packets) {
				packets.add(packet);
			}
		}
	}

	public void send(OutboundPacket packet) {
		packet.execute(this);
	}

	public void close() {
		channel.close();
	}

	public String getHostAddress() {
		return channel.remoteAddress().getHostString();
	}

	public String getHostName() {
		return channel.remoteAddress().getHostName();
	}

	public void setClientHash(long clientHash) {
		this.clientHash = clientHash;
	}

	public long getClientHash() {
		return clientHash;
	}

	public SocketChannel getChannel() {
		return channel;
	}

}
