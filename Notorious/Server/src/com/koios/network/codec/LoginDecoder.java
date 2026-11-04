package com.koios.network.codec;

import java.io.UncheckedIOException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import com.koios.Constants;
import com.koios.GameEngine;
import com.koios.GameService;
import com.koios.game.content.skill.farming.Farming;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.io.PlayerLoadResponse;
import com.koios.game.model.player.io.PlayerLoader;
import com.koios.network.Session;
import com.koios.network.validation.MySQLValidationResponseSupplier;
import com.koios.network.validation.ValidationResponse;
import com.koios.network.validation.ValidationResponseSupplier;
import com.koios.util.Functions;
import com.koios.util.StringUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

public final class LoginDecoder extends ByteToMessageDecoder {

	/**
	 * An enumeration with the different states the {@link LoginDecoder} can be in.
	 *
	 * @author Ryley Kimmel <ryley.kimmel@live.com>
	 */
	public enum State {

		/**
		 * The login handshake state will wait for the username hash to be received. Once it is, a server session key will be sent to the client and the state will be set to the
		 * login header state.
		 */
		LOGIN_HANDSHAKE,

		/**
		 * The login header state will wait for the login type and payload length to be received. These are saved, and then the state will be set to the login payload state.
		 */
		LOGIN_HEADER,

		/**
		 * The login payload state will wait for all login information (such as client version, username and password).
		 */
		LOGIN_PAYLOAD

	}

	private static final Pattern ACCEPTED_USERNAME_PATTERN = Pattern.compile("[A-Za-z0-9 ]+");
	private static final Random RANDOM = new SecureRandom();
	private static final long MAXIMUM_CONNECTIONS = 2;

	private static final ValidationResponseSupplier validationSupplier;

	static {
		if (GameService.isOnlineMode()) {
			validationSupplier = new MySQLValidationResponseSupplier();
		} else {
			// In a development environment we accept all connection requests.
			validationSupplier = client -> new ValidationResponse(true, false, false);
		}
	}

	private State state = State.LOGIN_HANDSHAKE;

	private int loginPacketSize;

	private final Session session;

	public LoginDecoder(Session session) {
		this.session = session;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		switch (state) {
		case LOGIN_HANDSHAKE:
			if (in.isReadable()) {
				int protocol = in.readUnsignedByte();

				if (protocol == 14) {
					long serverSessionKey = RANDOM.nextLong();

					ByteBuf handshakeResponse = Unpooled.buffer(Byte.BYTES + Long.BYTES);
					handshakeResponse.writeByte(0);
					handshakeResponse.writeLong(serverSessionKey);
					ctx.channel().writeAndFlush(handshakeResponse);
				}
				state = State.LOGIN_HEADER;
			}
			break;

		case LOGIN_HEADER:
			if (in.readableBytes() < 2) {
				return;
			}

			int loginType = in.readUnsignedByte();
			if (loginType != 16 && loginType != 18) {
				ctx.close();
				return;
			}

			loginPacketSize = in.readUnsignedByte();

			if (loginPacketSize <= 0) {
				System.out.println("Zero or negative login size.");
				ctx.close();
				return;
			}

			state = State.LOGIN_PAYLOAD;
			break;

		case LOGIN_PAYLOAD:
			if (in.readableBytes() < loginPacketSize) {
				return;
			}
			int magic = in.readByte() & 0xFF;

			int version = in.readShort();

			if (magic != 255) {
				System.out.println("Wrong magic id. Recieved = " + magic);
				ctx.channel().close();
				return;
			}

			in.readUnsignedByte();

			int loginEncryptPacketSize = loginPacketSize - (1 + 1 + 2);
			if (--loginEncryptPacketSize != in.readUnsignedByte()) {
				System.out.println("Encrypted size mismatch.");
				ctx.close();
				return;
			}

			/*
			 * An empty byte array with a capacity of {@code #loginEncryptPacketSize} bytes
			 */
			// byte[] encodedBuffer = new byte[loginEncryptPacketSize];

			/* Gets the bytes from the encoded buffer sent from the client. */
			// in.readBytes(encodedBuffer);

			// BigInteger encodedBigInteger = new BigInteger(encodedBuffer);
			// BigInteger decodedBigInteger =
			// encodedBigInteger.modPow(NetworkConstants.RSA_EXPONENT,
			// NetworkConstants.RSA_MODULUS);

			// ByteBuf encryptedBuffer =
			// Unpooled.wrappedBuffer(decodedBigInteger.toByteArray());

			if (in.readUnsignedByte() != 10) {
				System.out.println("Encrypted id != 10.");
				ctx.close();
				return;
			}

			long clientSessionKey = in.readLong();
			long serverSessionKey = in.readLong();
			long clientHash = in.readLong();

			String name = readString(in);
			String pass = readString(in);
			int[] sessionKey = new int[4];
			sessionKey[0] = (int) (clientSessionKey >> 32);
			sessionKey[1] = (int) clientSessionKey;
			sessionKey[2] = (int) (serverSessionKey >> 32);
			sessionKey[3] = (int) serverSessionKey;

			ctx.channel().pipeline().replace("decoder", "decoder", new PacketDecoder(session));
			load(ctx, clientHash, name, pass, sessionKey, version);
		}
	}

	private void load(final ChannelHandlerContext ctx, long clientHash, String username, String password, int[] sessionKey, int version) {
		byte responseCode = 2;
		username = username.trim();
		username = StringUtil.formatUsername(username);
		password = password.trim().toLowerCase();

		if (!ACCEPTED_USERNAME_PATTERN.matcher(username).matches()) {
			// Invalid username
			responseCode = 4;
		}

		if (username.length() > 12) {
			responseCode = 8;
		}

		Player player = new Player(session, username);
		ValidationResponse validationResponse = null;
		session.setPlayer(player);
		session.seedCiphers(sessionKey);
		session.setClientHash(clientHash);
		player.setActive(true);
		player.saveCharacter = false;
		
		String address = session.getHostAddress();
		ChannelGroup group = GameEngine.getChannelGroup();
		Collection<Player> loginQueue = GameEngine.getLoginQueue();
		
		long count = group.stream().flatMap(Functions.instancesOf(SocketChannel.class)).
				filter(ch -> ch.remoteAddress().getHostString().equals(address)).count();

		boolean success = count < MAXIMUM_CONNECTIONS && group.add(session.getChannel());
		if (!success) {
			responseCode = 9;
		} else if (GameService.getWorld().isSystemUpdating()) {
			responseCode = 14;
		} else if (version != Constants.CLIENT_VERSION) {
			responseCode = 6;
		} else if (password.length() > 20 || password.length() < 1) {
			responseCode = 3;
		} else if (GameService.getWorld().isPlayerLoggedIn(username) || loginQueue.contains(player)) {
			responseCode = 5;
		} else if (GameService.getWorld().getPlayerCount() >= Constants.MAX_PLAYERS || GameService.getWorld().getPlayerCount() + loginQueue.size() >= Constants.MAX_PLAYERS) {
			responseCode = 7;
		} else if (responseCode == 2) {
			PlayerLoadResponse loadResponse = PlayerLoader.load(player, password);

			if (loadResponse != PlayerLoadResponse.INVALID_CREDENTIALS) {
				try {
					validationResponse = validationSupplier.fetch(session);

					if (!validationResponse.success()) {
						responseCode = 13; // Could not complete login
					} else if (validationResponse.isBanned()) {
						responseCode = 4; // Account disabled.
						System.out.println(String.format("Client denied: '%s' (banned; %d, '%s')", player.getName(), clientHash, player.getSession().getHostAddress()));
					}
				} catch (UncheckedIOException ex) {
					responseCode = 26;
					System.out.println("Unable to fetch GeoLocation for: " + player.getName());
				}
			}
			if (responseCode == 2) {
				if (loadResponse == PlayerLoadResponse.NEW_ACCOUNT) {
					player.addStarter = true;
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					player.setJoinDate(dateFormat.format(date));
					if (username.startsWith("Mod ")) {
						// New accounts cannot be prefixed with "Mod " or have
						// excessive whitespace.
						responseCode = 22;
						player.allowSaveRequests = false;
					}
				}

				// Count registered users; this is done outside the 'new user'
				// scope to ensure that
				// users who registered prior to this update will receive an id
				// on first login.
				// Note: '0' is magic id and is not valid. The first valid id is
				// '1'.
				if (player.userId == 0) {
					player.userId = GameService.getConfiguration().increment("registered_user_count");
				}

				if (loadResponse == PlayerLoadResponse.INVALID_CREDENTIALS) {
					responseCode = 3;
					player.allowSaveRequests = false;
				} else {
					try {
						Farming.load(player);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}

				if (validationResponse != null && validationResponse.isMuted()) {
					player.setMuted(true);
					player.sendMessage("<col=800000>You are muted and cannot speak freely to other players.");
				}
			} else {
				player.allowSaveRequests = false;
			}
		}

		ByteBuf buf = Unpooled.buffer(3);
		buf.writeByte(responseCode);

		if (responseCode == 2) {
			player.saveCharacter = true;
			buf.writeByte((byte) player.getChatIconId());
		} else {
			buf.writeByte((byte) 0);
		}

		buf.writeByte((byte) 0);
		ctx.channel().writeAndFlush(buf);

		if (responseCode == 2) {
			player.loginFinished = true;
			ctx.pipeline().addFirst("gameEncoder", new PacketEncoder(session));
			GameService.getWorld().register(player);
		} else {
			ctx.channel().close();
		}
	}

	private static String readString(ByteBuf buffer) {
		buffer.markReaderIndex();
		int len = 0;

		while (buffer.readByte() != '\n') {
			len++;
		}

		buffer.resetReaderIndex();
		byte[] bytes = new byte[len];
		buffer.readBytes(bytes);
		buffer.readerIndex(buffer.readerIndex() + 1);
		return new String(bytes);
	}

}