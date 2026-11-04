package com.koios.game.model.mob.impl.event.hideandseek;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.koios.GameService;
import com.koios.game.model.Location;
import com.koios.game.model.item.GroundItemManager;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;

public class MobHideAndSeekHelper extends Mob {

	private final long ownerUsernameHash;
	private final List<Item> coffers = new ArrayList<>();
	private String winnerName = null;
	private boolean rewardClaimed = false;
	private boolean claimable = false;
	private int timeout;

	public MobHideAndSeekHelper(Location location, Player owner) {
		this(location, owner.getUsernameHash());
	}

	private MobHideAndSeekHelper(Location location, long ownerUsernameHash) {
		super(8104, location);
		this.ownerUsernameHash = ownerUsernameHash;
	}

	@Override
	public Mob copy() {
		// never respawn
		return null;
	}

	public Player getOwner() {
		return GameService.getWorld().getByUsernameHash(ownerUsernameHash).orElse(null);
	}

	public boolean isOwner(Player player) {
		return player.getUsernameHash() == ownerUsernameHash;
	}

	public void setClaimable(boolean claimable) {
		this.claimable = claimable;
	}

	public boolean addItem(Item item) {
		Objects.requireNonNull(item);

		if (rewardClaimed) {
			return false;
		}

		coffers.add(item);
		return true;
	}

	public void claim(Player player) {
		if (!claimable && player.getUsernameHash() != ownerUsernameHash) {
			player.sendMessage("This hide and seek monument is not ready to be claimed.");
			return;
		}

		if (rewardClaimed) {
			if (winnerName.equals(player.getName())) {
				player.sendMessage("You've already won!");
			} else {
				player.sendMessage("Sorry... " + winnerName + " beat you to it this time!");
			}
		} else {
			speak(player.getName() + " claimed the coffers!");

			for (Item item : coffers) {
				if (!player.getInventory().add(item)) {
					GroundItemManager.spawnNew(player, item.getId(), player.getX(), player.getY(), item.getCount());
				}
			}

			rewardClaimed = true;
			winnerName = player.getName();
			timeout = 100;
			coffers.clear();
		}
	}

	@Override
	public void update() {
		super.update();

		if (rewardClaimed) {
			int n = --timeout;

			if (n == 0) {
				setDead(true);
			}

			if (n % 10 == 4) {
				String[] comments = { "You were too slow!", "Better luck next time.", "The idea is to be first!", "Maybe next time!" };
				speak(winnerName + " won! " + comments[NumberUtil.random(comments.length)]);
			}
		} else if (NumberUtil.random(64) == 0) {
			String[] comments = { "Meh.", "Ka-pow!", "Meep!", "I'm here!", "There you are!", "Wanted: Barrel!", "You have to be first!", "Faster!" };
			speak(comments[NumberUtil.random(comments.length)]);
		}
	}

}
