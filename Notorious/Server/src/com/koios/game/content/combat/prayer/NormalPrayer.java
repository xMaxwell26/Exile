package com.koios.game.content.combat.prayer;

import java.util.EnumSet;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.koios.GameService;
import com.koios.game.content.KeepOnDeath;
import com.koios.game.content.dialogue.impl.util.SimpleDialogues;
import com.koios.game.content.skill.Skill;
import com.koios.game.model.Damage;
import com.koios.game.model.EntityCharacter;
import com.koios.game.model.Graphic;
import com.koios.game.model.player.Player;
import com.koios.util.NumberUtil;
import com.koios.util.StringUtil;

public enum NormalPrayer implements Prayer {

	THICK_SKIN(83, 21233, 1, 0.5, DEFENCE),
	BURST_OF_STRENGTH(84, 21234, 4, 0.5, STRENGTH),
	CLARITY_OF_THOUGHT(85, 21235, 7, 0.5, ATTACK),
	SHARP_EYE(700, 77100, 8, 0.5, RANGE),
	MYSTIC_WILL(701, 77102, 9, 0.5, MAGE),
	ROCK_SKIN(86, 21236, 10, 1, DEFENCE),
	SUPERHUMAN_STRENGTH(87, 21237, 13, 1, STRENGTH),
	IMPROVED_REFLEXES(88, 21238, 16, 1, ATTACK),
	RAPID_RESTORE(89, 21239, 19, 0.15, HEALING),
	RAPID_HEAL(90, 21240, 22, 0.3, HEALING),
	PROTECT_ITEM(91, 21241, 25, 0.3, NONE) {

		@Override
		public void onActivate(Player player) {
			// If we're currently viewing the kept on death interface, send our
			// (updated) kept items
			if (player.getInterfaceState().getCurrentInterface() == 17100) {
				KeepOnDeath.sendItems(player);
			}
		}

	},
	HAWK_EYE(702, 77104, 26, 1, RANGE),
	MYSTIC_LORE(703, 77106, 27, 1, MAGE),
	STEEL_SKIN(92, 21242, 28, 2, DEFENCE),
	ULTIMATE_STRENGTH(93, 21243, 31, 2, STRENGTH),
	INCREDIBLE_REFLEXES(94, 21244, 34, 2, ATTACK),

	PROTECT_FROM_MAGIC(95, 21245, 37, 2, OVERHEAD) {

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
			return 2;
		}

	},
	PROTECT_FROM_MISSILES(96, 21246, 40, 2, OVERHEAD) {

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
			return 1;
		}

	},
	PROTECT_FROM_MELEE(97, 21247, 43, 2, OVERHEAD) {

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
			return 0;
		}

	},
	EAGLE_EYE(704, 77109, 44, 2, RANGE),
	MYSTIC_MIGHT(705, 77111, 45, 2, MAGE),
	RETRIBUTION(98, 2171, 46, 0.5, OVERHEAD) {

		@Override
		public void effect(Player player) {
			int max = (int) (player.getSkill().getLevel(Skill.PRAYER) * 0.25), damage = NumberUtil.random(max) + 1;

			player.playGraphics(Graphic.create(437));

			if (player.inWild()) {
				if (player.inMulti()) {
					for (Player target : GameService.getWorld().getPlayers()) {
						if (!target.inMulti() || target.distanceToPoint(player.getX(), player.getY()) < 2) {
							continue;
						}

						target.setDamage(new Damage(damage), player);
					}
				} else {
					EntityCharacter victim = player.getCombat().getAggressor();
					if (victim != null) {
						victim.setDamage(new Damage(damage), player);
					}
				}
			}
		}

		@Override
		public int getHeadIcon() {
			return 3;
		}

	},
	REDEMPTION(99, 2172, 49, 1, HEALING | OVERHEAD) {

		@Override
		public int getHeadIcon() {
			return 5;
		}

	},
	SMITE(100, 2173, 52, 2, OVERHEAD) {

		@Override
		public int getHeadIcon() {
			return 4;
		}

	},
	CHIVALRY(706, 77113, 60, 2, ATTACK | STRENGTH | DEFENCE) {

		@Override
		public boolean canActivate(Player player) {
			if (player.getSkill().getLevelForExperience(Skill.DEFENCE) < 60) {
				SimpleDialogues.sendStatement(player, "You need a @blu@Defence level of 60  to use " + getName() + ".");
				return false;
			}

			return true;
		}

	},
	PIETY(707, 77115, 70, 4, ATTACK | STRENGTH | DEFENCE) {

		@Override
		public boolean canActivate(Player player) {
			if (player.getSkill().getLevelForExperience(Skill.DEFENCE) < 70) {
				SimpleDialogues.sendStatement(player, "You need a @blu@Defence level of 70 to use " + getName() + ".");
				return false;
			}

			return true;
		}
	};

	public static final ImmutableSet<NormalPrayer> PRAYERS = Sets.immutableEnumSet(EnumSet.allOf(NormalPrayer.class));

	public static Optional<NormalPrayer> fromQuickButton(int id) {
		return PRAYERS.stream().filter(p -> p.getQuickPrayerButton() == id).findAny();
	}

	private final int glow;
	private final int buttonId;
	private final int levelRequired;
	private final int flags;
	private final double drain;
	private final String name;

	private NormalPrayer(int glow, int buttonId, int levelRequired, double drain, int flags) {
		this.glow = glow;
		this.buttonId = buttonId;
		this.levelRequired = levelRequired;
		this.flags = flags;
		this.drain = drain;
		this.name = StringUtil.capitalize(name().toLowerCase().replaceAll("_", " "));
	}

	@Override
	public int getQuickPrayerButton() {
		return 67050 + getId();
	}

	@Override
	public int getLevelRequired() {
		return levelRequired;
	}

	@Override
	public int getId() {
		return ordinal();
	}

	@Override
	public int getGlow() {
		return glow;
	}

	@Override
	public int getButtonId() {
		return buttonId;
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
	public String toString() {
		return name;
	}

	@Override
	public PrayerBook getBook() {
		return PrayerBook.NORMAL;
	}

}