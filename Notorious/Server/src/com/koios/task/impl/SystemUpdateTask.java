package com.koios.task.impl;

import java.io.IOException;

import com.koios.GameService;
import com.koios.game.model.World;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacketBuilder;
import com.koios.task.Task;

/**
 * Manages updating of the World.
 *
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class SystemUpdateTask extends Task {

	private int timeRemaining;

	private boolean cancelled;

	private boolean finished;

	public SystemUpdateTask(int time) {
		super(2);
		timeRemaining = time;
	}

	@Override
	public void execute() {
		GameService.getWorld().execute(p -> sendUpdateTimer(p, timeRemaining--));

		if (timeRemaining <= 0) {
			finished = true;
			stop();
			return;
		}
	}

	private void sendUpdateTimer(Player player, int time) {
		GamePacketBuilder bldr = new GamePacketBuilder(114).writeLEShort(time * 50 / 30);
		player.write(bldr.toPacket());
	}

	public void cancel() {
		cancelled = true;
		GameService.getWorld().execute(p -> sendUpdateTimer(p, 0));
		stop();
	}

	@Override
	public void onStop() {
		if (!cancelled) {
			World world = GameService.getWorld();

			// So all clients are reverted back to the login screen instantly
			world.execute(p -> p.write(new GamePacketBuilder(109)));

			world.execute(world::unregister);

			try {
				GameService.getConfiguration().save();
			} catch (IOException cause) {
				cause.printStackTrace();
			}

			System.out.println("Finished system update, you may now close the server.");
		}
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean isCancelled() {
		return cancelled;
	}

}