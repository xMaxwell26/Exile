package com.koios.game.content.skill.hunter.impl;

import com.koios.Constants;
import com.koios.GameService;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.hunter.Hunter;
import com.koios.game.content.skill.hunter.Trap;
import com.koios.game.content.skill.hunter.TrapProcessor;
import com.koios.game.content.skill.hunter.Hunter.TrapType;
import com.koios.game.map.MapRegion;
import com.koios.game.map.RSObject;
import com.koios.game.model.Animation;
import com.koios.game.model.item.Item;
import com.koios.game.model.object.WorldObject;
import com.koios.game.model.player.Player;
import com.koios.task.EntityTask;

@SuppressWarnings("rawtypes")
public final class DismantleTrap extends EntityTask {

	public static enum State {

		START, FINISH

	}

	private final Trap trap;
	private final Player player;
	private State state = State.START;

	@SuppressWarnings("unchecked")
	public DismantleTrap(Player player, WorldObject object) {
		super(1, player, false);
		trap = TrapProcessor.getTrap(player, object);
		this.player = player;
	}

	@Override
	protected void execute() {
		int code = executeState();

		if (code == 0) {
			stop();
		}
	}

	protected int executeState() {
		switch (state) {
		case START: {
			if (trap == null) {
				player.sendMessage("This isn't your trap.");
				stop();
				return 0;
			}

			if (trap.isCapturing() || trap.isRemoved()) {
				return 0;
			}

			trap.setDismantling();
			int inventory = trap.getRequiredSpace(player) - player.getInventory().freeSlots();

			if (inventory > 0) {
				player.sendMessage("You do not have enough inventory space. You need " + inventory + " more free slot.");
				return 0;
			}

			player.playAnimation(new Animation(trap.getTrapType().getDismantleAnimation()));
			setState(State.FINISH);
			return 3;
		}

		case FINISH: {
			if (!trap.isDismantling()) {
				return 0;
			}

			trap.setRemoved();
			trap.removeObject();
			player.sendMessage("You dismantle the trap.");

			if (trap.getTrapType() == TrapType.NET) {
				RSObject object = MapRegion.getObject(trap.getPosition().getX(), trap.getPosition().getY(), trap.getPosition().getZ());

				if (object != null) {
					GameService.getObjectManager().showRealObject(object);
				}
			}

			player.getInventory().add(trap.getTrapType().getItem());
			Hunter.Capturable captured = trap.getCaptured();

			if (captured != null) {
				player.getSkill().addExperience(Skill.HUNTER, captured.getExperience() * Constants.HUNTER_EXPERIENCE);

				for (Item item : captured.getItems()) {
					player.getInventory().add(item.copy());
				}

				player.sendMessage("You caught a " + captured.getName().toLowerCase() + '.');
			}

			return 0;
		}
		}

		throw new AssertionError();
	}

	public void setState(State state) {
		this.state = state;
	}

}
