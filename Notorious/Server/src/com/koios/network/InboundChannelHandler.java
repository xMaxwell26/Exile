package com.koios.network;

import io.netty.channel.ChannelHandler.Sharable;

import com.koios.GameEngine;
import com.koios.game.model.player.Player;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

@Sharable
public final class InboundChannelHandler extends SimpleChannelInboundHandler<GamePacket> {

	private final Session session;

	public InboundChannelHandler(Session session) {
		this.session = session;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		queueLogout(false);
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, GamePacket msg) {
		session.queuePacket(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
		queueLogout(false);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			switch (event.state()) {
			case ALL_IDLE:
			case READER_IDLE:
			case WRITER_IDLE:
				queueLogout(true);
				break;
			}
		}
	}

	private void queueLogout(boolean idle) {
		Player player = session.getPlayer();
		if (player != null) {
			if (idle) {
				System.out.println(player.getName() + " is idle, disconnecting...");
			}

			GameEngine.queueLogout(player);
		}
	}

}