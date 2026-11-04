package com.koios.game.content.skill;

import com.koios.game.content.skill.crafting.CraftLeather;
import com.koios.game.content.skill.crafting.GlassBlowing;
import com.koios.game.content.skill.crafting.TanningInterface;
import com.koios.game.content.skill.crafting.CraftingData.LeatherDialogueData;
import com.koios.game.content.skill.fletching.CraftArrowShaftAction;
import com.koios.game.content.skill.fletching.CraftCrossbowAction;
import com.koios.game.content.skill.fletching.CraftLongBowAction;
import com.koios.game.content.skill.fletching.CraftShortBowAction;
import com.koios.game.content.skill.smithing.SmeltingAction;
import com.koios.game.model.item.Item;
import com.koios.game.model.player.Player;

public class InterfaceButtons {

	public static boolean handleButtons(Player player, int button) {
		switch (button) {
		/**
		 * 5 items interface buttons
		 */
		case 34245: // first item
		case 34244:
		case 34243:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(6322) });
						player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
					} else {
						player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
					}
				} else {
					player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				}

				player.getCurrentSkill().start();
			}
			return true;

		case 34242:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(6322) });
					} else {
						player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
					}
				}

				player.getInterfaceState().openValueInput();
			}
			return true;

		case 34249: // second item
		case 34248:
		case 34247:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(6324) });
						player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
					} else {
						player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
					}
				} else {
					player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				}

				player.getCurrentSkill().start();
			}
			return true;

		case 34246:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(6324) });
					} else {
						player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
					}
				}

				player.getInterfaceState().openValueInput();
			}
			return true;

		case 34253: // third item
		case 34252:
		case 34251:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(6326) });
						player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
					} else {
						player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
					}
				} else {
					player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				}

				player.getCurrentSkill().start();
			}
			return true;

		case 34250:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(6326) });
					} else {
						player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
					}
				}

				player.getInterfaceState().openValueInput();
			}
			return true;

		case 35001: // fourth item
		case 35000:
		case 34255:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(6328) });
						player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
					} else {
						player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
					}
				} else {
					player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				}

				player.getCurrentSkill().start();
			}
			return true;

		case 34254:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(6328) });
					} else {
						player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
					}
				}

				player.getInterfaceState().openValueInput();
			}
			return true;

		case 35005: // fifth item
		case 35004:
		case 35003:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(6330) });
						player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
					} else {
						player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
					}
				} else {
					player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				}

				player.getCurrentSkill().start();
			}
			return true;

		case 35002:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(6330) });
					} else {
						player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
					}
				}

				player.getInterfaceState().openValueInput();
			}
			return true;

		/**
		 * 4 items interface buttons
		 */
		case 34205: // First Item
		case 34204:
		case 34203:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
				player.getCurrentSkill().start();
			} else if (player.getCurrentSkill() != null) {
				player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				player.getCurrentSkill().start();
			}
			return true;

		case 34202:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
			}

			if (player.getCurrentSkill() != null) {
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 34209: // Second Item
		case 34208:
		case 34207:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftLongBowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
				player.getCurrentSkill().start();
			} else if (player.getCurrentSkill() != null) {
				player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				player.getCurrentSkill().start();
			}
			return true;

		case 34206:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftLongBowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
			}

			if (player.getCurrentSkill() != null) {
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 34213: // Third Item
		case 34212:
		case 34211:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftCrossbowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
				player.getCurrentSkill().start();
			} else if (player.getCurrentSkill() != null) {
				player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				player.getCurrentSkill().start();
			}
			return true;

		case 34210:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftCrossbowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
			}

			if (player.getCurrentSkill() != null) {
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 34217: // Fourth item
		case 34216:
		case 34215:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftArrowShaftAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
				player.getCurrentSkill().start();
			} else if (player.getCurrentSkill() != null) {
				player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				player.getCurrentSkill().start();
			}
			return true;

		case 34214:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftArrowShaftAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
			}

			if (player.getCurrentSkill() != null) {
				player.getInterfaceState().openValueInput();
			}
			return true;

		/**
		 * 3 items interface
		 */
		case 34185: // First item
		case 34184:
		case 34183:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(((LeatherDialogueData) player.getCurrentSkill().getObjects()[0]).getVamb()) });
						player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
					} else {
						player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
					}

					player.getCurrentSkill().start();
				} else {
					player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
					player.getCurrentSkill().start();
				}
			}
			return true;

		case 34182:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
			}

			if (player.getCurrentSkill() != null) {
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 34189: // Second item
		case 34188:
		case 34187:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(((LeatherDialogueData) player.getCurrentSkill().getObjects()[0]).getChaps()) });
						player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
					} else {
						player.setCurrentSkill(new CraftLongBowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
					}

					player.getCurrentSkill().start();
				} else {
					player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
					player.getCurrentSkill().start();
				}
			}
			return true;
		case 34186:

			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftLongBowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
			}

			if (player.getCurrentSkill() != null) {
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 34193: // Third item
		case 34192:
		case 34191:
			if (player.getCurrentSkill() != null) {
				if (player.getCurrentSkill().getObjects() != null) {
					if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
						player.getCurrentSkill().setObjects(new Object[] { new Item(((LeatherDialogueData) player.getCurrentSkill().getObjects()[0]).getBody()) });
						player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
					} else {
						player.setCurrentSkill(new CraftCrossbowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
					}

					player.getCurrentSkill().start();
				} else {
					player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
					player.getCurrentSkill().start();
				}
			}
			return true;

		case 34190:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftCrossbowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
			}

			if (player.getCurrentSkill() != null) {
				player.getInterfaceState().openValueInput();
			}
			return true;

		/**
		 * 2 items interface
		 */

		case 34170: // First item
		case 34169:
		case 34168:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
				player.getCurrentSkill().start();
			} else if (player.getCurrentSkill() != null) {
				player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));

				if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
					player.getCurrentSkill().setObjects(new Object[] { new Item(CraftLeather.getProductByButton(button)) });
				}

				player.getCurrentSkill().start();
			}
			return true;

		case 34167:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftShortBowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
			}

			if (player.getCurrentSkill() != null) {
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 34174: // Second item
		case 34173:
		case 34172:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftLongBowAction(player, (int) player.getCurrentSkill().getObjects()[0], SkillHandler.getAmount(button)));
				player.getCurrentSkill().start();
			} else if (player.getCurrentSkill() != null) {
				player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				if (player.getCurrentSkill().getName().equals("CRAFTLEATHER")) {
					player.getCurrentSkill().setObjects(new Object[] { new Item(CraftLeather.getProductByButton(button)) });
				}
				player.getCurrentSkill().start();
			}
			return true;

		case 34171:
			if (player.getCurrentSkill() != null && player.getCurrentSkill().getObjects() != null) {
				player.setCurrentSkill(new CraftLongBowAction(player, (int) player.getCurrentSkill().getObjects()[0], 0));
			}

			if (player.getCurrentSkill() != null) {
				player.getInterfaceState().openValueInput();
			}
			return true;

		/**
		 * 1 item interface
		 */
		case 10239:
		case 10238:
		case 6211:
			if (player.getCurrentSkill() != null) {
				player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				player.getCurrentSkill().start();
			}
			return true;

		case 6212:
			if (player.getCurrentSkill() != null) {
				player.getInterfaceState().openValueInput();
			}
			return true;

		/**
		 * Smelting Interfaces.
		 */
		case 15147: // bronze (1)
		case 15146: // bronze (5)
		case 10247: // bronze (10)
			if (SmeltingAction.canSmelt(player, 2349)) {
				new SmeltingAction(player, 2349, SkillHandler.getAmount(button)).start();
			}
			return true;

		case 9110: // bronze (X)
			if (SmeltingAction.canSmelt(player, 2349)) {
				player.setCurrentSkill(new SmeltingAction(player, 2349, 0));
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 15151: // iron (1)
		case 15150: // iron (5)
		case 15149: // iron (10)
			if (SmeltingAction.canSmelt(player, 2351)) {
				new SmeltingAction(player, 2351, SkillHandler.getAmount(button)).start();
			}
			return true;

		case 15148:// Iron (X)
			if (SmeltingAction.canSmelt(player, 2351)) {
				player.setCurrentSkill(new SmeltingAction(player, 2351, 0));
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 15155: // Silver (1)
		case 15154: // Silver (5)
		case 15153: // Silver (10)
			if (SmeltingAction.canSmelt(player, 2355)) {
				new SmeltingAction(player, 2355, SkillHandler.getAmount(button)).start();
			}
			return true;

		case 15152:// Silver (X)
			if (SmeltingAction.canSmelt(player, 2355)) {
				player.setCurrentSkill(new SmeltingAction(player, 2355, 0));
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 15159: // Steel (1)
		case 15158: // Steel (5)
		case 15157: // Steel (10)
			if (SmeltingAction.canSmelt(player, 2353)) {
				new SmeltingAction(player, 2353, SkillHandler.getAmount(button)).start();
			}
			return true;

		case 15156:// Steel (X)
			if (SmeltingAction.canSmelt(player, 2353)) {
				player.setCurrentSkill(new SmeltingAction(player, 2353, 0));
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 15163: // Gold (1)
		case 15162: // Gold (5)
		case 15161: // Gold (10)
			if (SmeltingAction.canSmelt(player, 2357)) {
				new SmeltingAction(player, 2357, SkillHandler.getAmount(button)).start();
			}
			return true;

		case 15160:// Gold (X)
			if (SmeltingAction.canSmelt(player, 2357)) {
				player.setCurrentSkill(new SmeltingAction(player, 2357, 0));
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 29017: // MITHRIL (1)
		case 29016: // MITHRIL (5)
		case 24253: // MITHRIL (10)
			if (SmeltingAction.canSmelt(player, 2359)) {
				new SmeltingAction(player, 2359, SkillHandler.getAmount(button)).start();
			}
			return true;

		case 16062:// MITHRIL (X)
			if (SmeltingAction.canSmelt(player, 2359)) {
				player.setCurrentSkill(new SmeltingAction(player, 2359, 0));
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 29022: // ADAMANT (1)
		case 29020: // ADAMANT (5)
		case 29019: // ADAMANT (10)

			if (SmeltingAction.canSmelt(player, 2361)) {
				new SmeltingAction(player, 2361, SkillHandler.getAmount(button)).start();
			}
			return true;

		case 29018: // ADAMANT (x)
			if (SmeltingAction.canSmelt(player, 2361)) {
				player.setCurrentSkill(new SmeltingAction(player, 2361, 0));
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 29026: // RUNE (1)
		case 29025: // RUNE (5)
		case 29024: // RUNE (10)
			if (SmeltingAction.canSmelt(player, 2363)) {
				new SmeltingAction(player, 2363, SkillHandler.getAmount(button)).start();
			}
			return true;
		case 29023: // RUNE (x)
			if (SmeltingAction.canSmelt(player, 2363)) {
				player.setCurrentSkill(new SmeltingAction(player, 2363, 0));
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 44210: // Glass blowing interface buttons
		case 44209:
		case 44208:
		case 48108:
		case 48107:
		case 48106:
		case 48112:
		case 48111:
		case 48110:
		case 48116:
		case 48115:
		case 48114:
		case 48120:
		case 48119:
		case 48118:
		case 24059:
		case 24058:
		case 24057:
		case 48124:
		case 48123:
		case 48122:
			if (GlassBlowing.canCreate(player, button) && player.getCurrentSkill() != null) {
				player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				player.getCurrentSkill().setObjects(new Object[] { button });
				player.getCurrentSkill().start();
			}
			return true;

		case 44207:
		case 44211:
		case 48109:
		case 48113:
		case 48117:
		case 24056:
		case 48121:
			if (GlassBlowing.canCreate(player, button) && player.getCurrentSkill() != null) {

				player.getCurrentSkill().setObjects(new Object[] { button });
				player.getInterfaceState().openValueInput();
			}
			return true;

		case 57225:
		case 57217:
		case 57201:
		case 57226:
		case 57218:
		case 57202:
		case 57227:
		case 57219:
		case 57203:
		case 57228:
		case 57220:
		case 57204:
		case 57229:
		case 57221:
		case 57205:
		case 57230:
		case 57222:
		case 57206:
		case 57231:
		case 57223:
		case 57207:
		case 57232:
		case 57224:
		case 57208:
			if (TanningInterface.canTan(TanningInterface.getHideByButton(button))) {
				player.setCurrentSkill(new TanningInterface(player, TanningInterface.getHideByButton(button), SkillHandler.getAmount(button)));
				player.getCurrentSkill().start();
			}
			return true;

		case 57209:
		case 57210:
		case 57211:
		case 57212:
		case 57213:
		case 57214:
		case 57215:
		case 57216:
			if (TanningInterface.canTan(TanningInterface.getHideByButton(button))) {
				player.setCurrentSkill(new TanningInterface(player, TanningInterface.getHideByButton(button), 0));
				player.getInterfaceState().openValueInput();
			}
			return true;

		default:
			int citem = CraftLeather.getProductByButton(button);
			if (citem > 0 && player.getCurrentSkill() != null) {
				player.getCurrentSkill().setObjects(new Object[] { new Item(citem) });
				player.getCurrentSkill().setAmount(SkillHandler.getAmount(button));
				player.getCurrentSkill().start();
				return true;
			}

			return false;
		}
	}

}
