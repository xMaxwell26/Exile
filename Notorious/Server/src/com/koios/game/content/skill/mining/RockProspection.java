package com.koios.game.content.skill.mining;

import java.util.HashMap;
import java.util.Map;

import com.koios.GameService;
import com.koios.game.model.player.Player;
import com.koios.task.Task;

/**
 * Handles prospecting ores
 *
 * @author Timothy Howard <Mobster>
 */
public class RockProspection {

	public enum ProspectData {
		COPPER(2090, 2091, 9708, 31080, 31081, 31082), TIN(2095, 31077, 31078, 31079), IRON(2092, 2093, 9714, 31073, 31071, 31072), COAL(2096, 2097, 14850, 31068, 31070,
				31069), GOLD(2098, 9720, 31065, 31066), MITHRIL(2102, 2103, 14853), ADAMANT(2104, 2105, 14862, 31083, 31085), RUNITE(2106, 2107, 14859);

		private final int[] rockId;
		private static final Map<Integer, ProspectData> values = new HashMap<>();

		static {
			for (ProspectData p : values()) {
				for (Integer i : p.rockId) {
					values.put(i, p);
				}
			}
		}

		private ProspectData(int... rockId) {
			this.rockId = rockId;
		}

		public String getRockName() {
			return name().toLowerCase() + " ore";
		}

		public static ProspectData forId(int id) {
			return values.get(id);
		}

	}

	/**
	 * Prospects the rock.
	 *
	 * @param player The player class.
	 * @param itemName The name of the item within the object.
	 */
	public static boolean prospectRock(final Player player, int id) {
		ProspectData data = ProspectData.forId(id);

		if (data == null) {
			return false;
		}

		player.sendMessage("You examine the rock for ores...");

		GameService.getTaskScheduler().schedule(new Task(3) {

			@Override
			public void execute() {
				player.sendMessage("This rock contains " + data.getRockName() + ".");
				stop();
			}

		});
		return true;
	}

}
