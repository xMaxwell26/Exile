package com.koios.game.content.area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.koios.game.content.area.impl.BarbarianVillage;
import com.koios.game.content.area.impl.EdgevilleAreaHandler;
import com.koios.game.content.area.impl.GodwarsAreaHandler;
import com.koios.game.content.area.impl.RecipeForDisasterAreaHandler;
import com.koios.game.content.area.impl.SecurityStrongholdFloorFour;
import com.koios.game.content.area.impl.SecurityStrongholdFloorOne;
import com.koios.game.content.area.impl.SecurityStrongholdFloorThree;
import com.koios.game.content.area.impl.SecurityStrongholdFloorTwo;
import com.koios.game.content.area.impl.UnderKaramjaAreaHandler;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;

/**
 * Handles interactable areas
 *
 * @author Timothy Howard <Mobster>
 */
public class AreaManager {

	/**
	 * Stores all of the areas
	 */
	private static List<AreaHandler> areas = new ArrayList<>();

	static {
		submit(new GodwarsAreaHandler());
		submit(new EdgevilleAreaHandler());
		submit(new BarbarianVillage());
		submit(new SecurityStrongholdFloorOne());
		submit(new SecurityStrongholdFloorTwo());
		submit(new SecurityStrongholdFloorThree());
		submit(new SecurityStrongholdFloorFour());
		submit(new UnderKaramjaAreaHandler());
		submit(new RecipeForDisasterAreaHandler());
	}

	/**
	 * Submits a new {@link AreaHandler} to the {@link HashMap}
	 *
	 * @param key The key to the area
	 * @param area
	 */
	private static void submit(AreaHandler area) {
		Objects.requireNonNull(area);
		areas.add(area);
	}

	/**
	 * Handles an interaction with an object or entity in a specific area
	 *
	 * @param player The {@link Player} doing the interaction
	 * @param type The {@link InteractionType} of the interaction
	 * @param values Values passed to handle the interation
	 * @return
	 */
	public static boolean handleInteraction(Player player, InteractionType type, Object... values) {
		Location location = (Location) values[0];
		Mob npc = null;
		int id = -1;
		if (type == InteractionType.FIRST_CLICK_NPC || type == InteractionType.SECOND_CLICK_NPC || type == InteractionType.THIRD_CLICK_NPC) {
			npc = (Mob) values[1];
		} else {
			id = (int) values[1];
		}
		for (AreaHandler area : areas) {
			
			if (area == null) {
				continue;
			}
			
			if (AreaBoundary.inArea(player.getLocation(), area.getArea())) {
				
				if (type == InteractionType.FIRST_CLICK_NPC) {
					area.handleFirstClickNpc(player, npc, location);
				} else if (type == InteractionType.FIRST_CLICK_OBJECT) {
					area.handleFirstClickObject(player, id, location);
				} else if (type == InteractionType.SECOND_CLICK_NPC) {
					area.handleSecondClickNpc(player, npc, location);
				} else if (type == InteractionType.SECOND_CLICK_OBJECT) {
					area.handleSecondClickObject(player, id, location);
				} else if (type == InteractionType.THIRD_CLICK_NPC) {
					area.handleThirdClickNpc(player, npc, location);
				} else if (type == InteractionType.THIRD_CLICK_OBJECT) {
					area.handleThirdClickObject(player, id, location);
				} else {
					System.out.println("Invalid click type.");
	
				}
				break;
			}
		}
		return false;
	}

}
