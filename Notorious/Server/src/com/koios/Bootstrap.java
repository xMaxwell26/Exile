package com.koios;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import org.sql2o.Sql2o;

import com.koios.game.content.bountyhunter.BountyHunterTick;
import com.koios.game.content.minigames.prophunt.PropHuntLobby;
import com.koios.game.model.player.io.PlayerSaver;
import com.koios.game.model.player.io.StoreUtils;
import com.koios.network.InboundChannelHandler;
import com.koios.network.Session;
import com.koios.network.codec.LoginDecoder;
import com.koios.task.Task;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * Handles launching the server
 *
 * @author Timothy Howard <Arithium>
 */
public final class Bootstrap {

	/**
	 * The default online mode.
	 */
	private static final boolean DEFAULT_ONLINE_MODE = false;

	/**
	 * The default service port to listen for incomming connections at.
	 */
	private static final int DEFAULT_SERVICE_PORT = 43594;

	/**
	 * Binds the networking to the specified port
	 * 
	 * @param host The host the server will listen on.
	 * @param servicePort The port to bind the server to.
	 */
	private static void bind(Optional<String> host, int servicePort) {
		try {
			final ServerBootstrap bootstrap = new ServerBootstrap();

			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) {
					Session session = new Session(ch);

					ch.pipeline().addLast("decoder", new LoginDecoder(session))
							.addLast("read-timeout", new ReadTimeoutHandler(15)).addLast("handler", new InboundChannelHandler(session));
				}

			});

			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.group(new NioEventLoopGroup());
			if (host.isPresent()) {
				bootstrap.bind(host.get(), servicePort).syncUninterruptibly();
			} else {
				bootstrap.bind(servicePort).syncUninterruptibly();
			}

			System.out.println(String.format("Success! Listening at port %d...", servicePort));
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Initializes the database connection
	 *
	 * @param onlineMode The server is online
	 */
	private static void initializeDatabase(boolean onlineMode) {
		if (onlineMode) {
			ServiceConfiguration conf = GameService.getConfiguration();
			String sqlAddress = conf.getString(Constants.MYSQL_ADDRESS);
			String sqlDatabase = conf.getString(Constants.MYSQL_DATABASE);
			String sqlUsername = conf.getString(Constants.MYSQL_USERNAME);
			String sqlPassword = conf.getString(Constants.MYSQL_PASSWORD);
			Sql2o database = new Sql2o(sqlAddress + "/" + sqlDatabase, sqlUsername, sqlPassword);
			GameService.setDatabase(database);
		}
	}

	/**
	 * Starts up the {@link GameService} which loads the definitions
	 *
	 * @param onlineMode The server is ready to be hosted
	 * @throws IOException
	 */
	private static void initializeDefinitions(boolean onlineMode) throws IOException {
		ServiceConfiguration configuration = new ServiceConfiguration(StoreUtils.getDirectory("global").resolve("global-vars.json"));
		GameService service = new GameService(configuration, new GameEngine(), onlineMode);
		GameService.setGameService(service);
		service.init();
		service.getEngine().start();
	}

	/**
	 * Starts up the global tasks
	 *
	 * @param onlineMode The server is online and ready for hosting
	 */
	private static void initializeTasks(boolean onlineMode) {
		GameService.getTaskScheduler().schedule(PropHuntLobby.getInstance());
		GameService.getTaskScheduler().schedule(new BountyHunterTick());

		GameService.getTaskScheduler().schedule(new Task(20, true) {

			@Override
			protected void execute() {
				GameService.getWorld().execute(PlayerSaver::save);

				try {
					GameService.getConfiguration().save();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

		});

		if (onlineMode) {
			GameService.getTaskScheduler().schedule(new Task(275, true) {

				private final Random random = new Random();

				@Override
				protected void execute() {
					int index = random.nextInt(Constants.ANNOUNCEMENTS.length);
					String message = Constants.ANNOUNCEMENTS[index];
					GameService.getWorld().broadcast(message, p -> !p.isDonator());
				}

			});
		}
	}

	/**
	 * Starts the server up by loading everything
	 *
	 * @param args The runtime arguements
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// Application session settings
		int servicePort = DEFAULT_SERVICE_PORT;
		boolean onlineMode = DEFAULT_ONLINE_MODE;
		Optional<String> host = Optional.empty();

		// Parse command line arguments
		if (args.length > 0) {
			if (args.length % 2 != 0) {
				System.err.println("Malformed argument line.");
				System.exit(0);
				return;
			}

			for (int i = 0; i < args.length; i++) {
				String command = args[i];

				if ("-p".equals(command) || "--port".equals(command)) {
					try {
						servicePort = Integer.parseInt(args[++i]);
					} catch (NumberFormatException ex) {
						System.err.println("Malformed port: " + args[i]);
						System.exit(0);
						return;
					}
				} else if ("-o".equals(command) || "--onlinemode".equals(command)) {
					onlineMode = Boolean.parseBoolean(args[++i]);
				} else if ("-h".equals(command) || "--host".equals(command)) {
					host = Optional.of(args[++i]);
				} else {
					System.err.println("Unrecognized argument: " + command + ".");
					System.exit(0);
					return;
				}
			}
		}

		System.out.println("Starting service... port: " + servicePort + ", online: " + onlineMode);
		initializeDefinitions(onlineMode);
		initializeTasks(onlineMode);
		initializeDatabase(onlineMode);
		bind(host, servicePort);
	}

	private Bootstrap() {
	}

}
