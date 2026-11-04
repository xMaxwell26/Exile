package com.koios.game.content.combat.prayer;

import java.util.EnumSet;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.koios.game.content.KeepOnDeath;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Animation;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.Damage.HitIcons;
import com.koios.game.model.Damage.HitMasks;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;
import com.koios.util.StringUtil;

public enum CursedPrayer implements Prayer {
	PROTECT_ITEM(83109, 50, 0.3, NONE) {
		@Override
		public int getActivateAnimation() {
			return 12567;
		}

		@Override
		public int getActivateGraphic() {
			return 2213;
		}

		@Override
		public void onActivate(Player player) {
			// If we're currently viewing the kept on death interface, send our
			// (updated) kept items
			if (player.getInterfaceState().getCurrentInterface() == 17100) {
				KeepOnDeath.sendItems(player);
			}
		}
	},
	SAP_WARRIOR(83111, 50, 2.5, ATTACK),
	SAP_RANGER(83113, 52, 2.5, RANGE),
	SAP_MAGIC(83115, 54, 2.5, MAGE),
	SAP_SPIRIT(83117, 56, 2.5, SPIRIT),
	BERSERKER(83119, 59, 0.3, NONE) {
		@Override
		public int getActivateAnimation() {
			return 12589;
		}

		@Override
		public int getActivateGraphic() {
			return 2266;
		}

		@Override
		public void onActivate(Player player) {
			if (!player.berserkerActive) {
				player.berserkerActive = true;
				player.playAnimation(new Animation(12589));
				player.playGraphics(new Graphic(2266));
				player.sendMessage("You somehow feel your boosted stats will last longer.");
			} else {
				player.berserkerActive = false;
			}
		}
	},
	DEFLECT_SUMMONING(83121, 62, 2, OVERHEAD) {
		@Override
		public int getHeadIcon() {
			return 12;
		}
	},
	DEFLECT_MAGIC(83123, 65, 2, OVERHEAD) {
		@Override
		public boolean canActivate(Player player) {
			if (System.currentTimeMillis() - player.stopPrayerDelay < 5000) {
				player.sendMessage("You have been injured and can not use this Prayer!");
				return false;
			}

			return true;
		}

		@Override
		public void onActivate(Player player) {
			player.protMageDelay = System.currentTimeMillis();
		}

		@Override
		public int getHeadIcon() {
			return 10;
		}
	},
	DEFLECT_MISSILES(83125, 68, 2, OVERHEAD) {
		@Override
		public boolean canActivate(Player player) {
			if (System.currentTimeMillis() - player.stopPrayerDelay < 5000) {
				player.sendMessage("You have been injured and can not use this Prayer!");
				return false;
			}

			return true;
		}

		@Override
		public void onActivate(Player player) {
			player.protRangeDelay = System.currentTimeMillis();
		}

		@Override
		public int getHeadIcon() {
			return 11;
		}
	},
	DEFLECT_MELEE(83127, 71, 2, OVERHEAD) {
		@Override
		public boolean canActivate(Player player) {
			if (System.currentTimeMillis() - player.stopPrayerDelay < 5000) {
				player.sendMessage("You have been injured and can not use this Prayer!");
				return false;
			}

			return true;
		}

		@Override
		public void onActivate(Player player) {
			player.protMeleeDelay = System.currentTimeMillis();
		}

		@Override
		public int getHeadIcon() {
			return 9;
		}
	},
	LEECH_ATTACK(83129, 74, 1.6, ATTACK),
	LEECH_RANGED(83131, 76, 1.6, RANGE),
	LEECH_MAGIC(83133, 78, 1.6, MAGE),
	LEECH_DEFENCE(83135, 80, 1.6, DEFENCE),
	LEECH_STRENGTH(83137, 82, 1.6, STRENGTH),
	LEECH_ENERGY(83139, 84, 1.6, SPIRIT),
	LEECH_SPECIAL(83141, 86, 1.6, SPIRIT),
	WRATH(83143, 89, 0.5, OVERHEAD) {
		@Override
		public int getHeadIcon() {
			return 16;
		}

		@Override
		public void effect(Player player) {
			EntityCharacter _target = player.getCombat().getAggressor();
			if (_target == null || !_target.isPlayer()) {
				return;
			}

			Player target = _target.toPlayer();

			if (target.getClientIndex() == target.getClientIndex()) {
				return;
			}

			int pray = player.getSkill().getLevel(Skill.PRAYER) / 100;
			int wrathDamage = NumberUtil.inclusiveRandom((pray == 0 ? 1 : pray) * 26);
			player.playGraphics(new Graphic(2259));
			player.playAnimation(new Animation(12583));
			target.setDamage(new Damage(wrathDamage, HitMasks.REGULAR_HIT, HitIcons.DEFLECT), player);
			target.playGraphics(new Graphic(2260));
		}
	},
	SOULSPLIT(83145, 92, 3, OVERHEAD) {
		@Override
		public int getHeadIcon() {
			return 17;
		}
	},
	TURMOIL(83147, 95, 3, ATTACK | DEFENCE | MAGE | RANGE | STRENGTH | SPIRIT) {
		@Override
		public int getActivateAnimation() {
			return 12565;
		}

		@Override
		public int getActivateGraphic() {
			return 2226;
		}
	};

	public static final ImmutableSet<CursedPrayer> PRAYERS = Sets.immutableEnumSet(EnumSet.allOf(CursedPrayer.class));

	public static Optional<CursedPrayer> fromQuickButton(int id) {
		return PRAYERS.stream().filter(p -> p.getQuickPrayerButton() == id).findAny();
	}

	private final int buttonId;
	private final int levelRequired;
	private final double drain;
	private final int flags;
	private final String name;

	private CursedPrayer(int buttonId, int levelRequired, double drain, int flags) {
		this.buttonId = buttonId;
		this.levelRequired = levelRequired;
		this.flags = flags;
		this.drain = drain;
		this.name = StringUtil.capitalize(name().toLowerCase().replaceAll("_", " "));
	}

	@Override
	public int getId() {
		return ordinal();
	}

	@Override
	public int getGlow() {
		return 724 + getId();
	}

	@Override
	public int getButtonId() {
		return buttonId;
	}

	@Override
	public int getQuickPrayerButton() {
		return 0;
	}

	@Override
	public int getLevelRequired() {
		return levelRequired;
	}

	@Override
	public int getFlags() {
		return flags;
	}

	@Override
	public double getDrain() {
		return drain;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public PrayerBook getBook() {
		return PrayerBook.CURSED;
	}

}