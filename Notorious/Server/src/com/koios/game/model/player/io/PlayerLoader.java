package com.koios.game.model.player.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Pattern;

import com.koios.game.content.combat.CombatVariables.AttackStyle;
import com.koios.game.content.skill.slayer.Master;
import com.koios.game.content.skill.slayer.assignments.Assignment;
import com.koios.game.content.titles.Title;
import com.koios.game.model.Location;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.BankItem;
import com.koios.game.model.player.Player;
import com.koios.game.model.player.Spellbook;
import com.koios.game.model.player.Privileges.DonatorPrivilege;
import com.koios.game.model.player.Privileges.RegularPrivilege;
import com.koios.util.StringUtil;

/**
 * An implementation of the player de-serialization tool that is stricter than the previous version.
 * <p>
 * Warnings will be provided when an initialised account has fields that cannot be parsed in the current category. This will, in the long run, benefit developers in that they will
 * know during development if account data was accidently removed.
 */
public final class PlayerLoader {

	private static interface Parser {

		public void parse(Player player, String key, String value);

	}

	public static PlayerLoadResponse load(Player player, String password) {
		Objects.requireNonNull(player, "player");

		try {
			// fetch the character file for the player in question
			Path characterFile = StoreUtils.getCharacterFile(player.getName());
			
			if (!Files.exists(characterFile)) {
				player.setPasswordPlain(password);
				return PlayerLoadResponse.NEW_ACCOUNT;
			}

			// pre-compile the regex pattern to split keys and values
			Pattern keyValuePattern = Pattern.compile(" = ");

			// read all the lines from the character file
			List<String> lines = Files.readAllLines(characterFile);

			// the current character file parser
			Parser parser = null;

			// the current section being parsed
			String activeSection = null;

			for (String line : lines) {

				if (line.isEmpty()) {
					activeSection = null;
					continue;
				}

				if (line.charAt(0) == '[' && line.length() > 2) {
					activeSection = line.substring(1, line.length() - 1);

					if ("EOF".equals(activeSection)) {
						return PlayerLoadResponse.SUCCESS;
					} else {
						switch (activeSection) {
						case "CREDENTIALS":
							// special case; handled without a parser.
							break;

						case "ACCOUNT":
							parser = PlayerLoader::parseAccount;
							break;

						case "POSITION":
							parser = PlayerLoader::parsePosition;
							break;

						case "ESSENTIALS":
							parser = PlayerLoader::parseEssentials;
							break;

						case "ASSIGNMENT":
							parser = PlayerLoader::parseAssignment;
							break;

						case "MUSIC":
							parser = PlayerLoader::parseMusic;
							break;

						case "TITLE-SETTINGS":
							parser = PlayerLoader::parseTitleSettings;
							break;

						case "TREASURE-TRAILS":
							parser = PlayerLoader::parseTreasureTrails;
							break;

						case "MINIGAMES":
							parser = PlayerLoader::parseMinigames;
							break;

						case "BANK-PIN":
							parser = PlayerLoader::parseBankPin;
							break;

						case "FAMILIAR-DATA":
							parser = PlayerLoader::parseFamiliarData;
							break;

						case "POINT-STATISTICS":
							parser = PlayerLoader::parsePointStatistics;
							break;

						case "ACHIEVEMENTS":
							parser = PlayerLoader::parseAchievements;
							break;

						case "APPEARANCE":
							parser = PlayerLoader::parseAppearance;
							break;

						case "BANK":
							parser = PlayerLoader::parseBank;
							break;

						case "EQUIPMENT":
							parser = PlayerLoader::parseEquipment;
							break;

						case "INVENTORY":
							parser = PlayerLoader::parseInventory;
							break;

						case "FAMILIAR-INVENTORY":
							parser = PlayerLoader::parseFamiliarInventory;
							break;

						case "STATS":
							parser = PlayerLoader::parseStats;
							break;

						case "FRIEND-LIST":
							parser = PlayerLoader::parseFriendList;
							break;

						case "IGNORE-LIST":
							parser = PlayerLoader::parseIgnoreList;
							break;

						default:
							System.err.println("Unrecognized character variable section: " + activeSection);
							System.err.println("in file: " + characterFile.toAbsolutePath());
							return PlayerLoadResponse.UNEXPECTED_ERROR;
						}
					}
				} else if (line.contains("=")) {
					String[] split = keyValuePattern.split(line, 2);
					String key = split[0];
					String value = split.length > 1 ? split[1] : "";

					if ("CREDENTIALS".equals(activeSection)) {
						if ("password-plain".equals(key)) {
							player.setPasswordPlain(value);

							if (!value.equals(password)) {
								return PlayerLoadResponse.INVALID_CREDENTIALS;
							}
						} else {
							// unrecognized variable
							return PlayerLoadResponse.UNEXPECTED_ERROR;
						}
					} else if (parser == null) {
						System.err.println("Reached key-value set before having an loot parser: ['" + key + "', '" + value + "']");
						System.err.println("in file: " + characterFile.toAbsolutePath());
						return PlayerLoadResponse.UNEXPECTED_ERROR;
					} else {
						try {
							parser.parse(player, key, value);
						} catch (NoSuchElementException ex) {
							System.err.println(player.getName() + " -> account store had unknown varset: '" + activeSection + "', '" + key + "', '" + value + "'");
						} catch (Throwable t) {
							t.printStackTrace();
							System.err.println("Parser threw an exception: ['" + activeSection + "', '" + key + "', '" + value + "']");
							System.err.println("in file: " + characterFile.toAbsolutePath());
							return PlayerLoadResponse.UNEXPECTED_ERROR;
						}
					}
				} else {
					System.err.println("Malformed character line: " + line);
					System.err.println("in file: " + characterFile.toAbsolutePath());
					return PlayerLoadResponse.UNEXPECTED_ERROR;
				}
			}
		} catch (IOException ex) {
			return PlayerLoadResponse.UNEXPECTED_ERROR;
		}

		// if this is reached we must not have found the EOF tag.
		return PlayerLoadResponse.UNEXPECTED_ERROR;
	}

	private static void parseAccount(Player player, String key, String value) {
		switch (key) {
		case "user_id":
			player.userId = Integer.parseInt(value);
			break;

		case "usergroup":
			player.setPrivilege(RegularPrivilege.valueOf(value));
			break;

		case "join-date":
			player.setJoinDate(value);
			break;

		case "play-duration":
			player.cachedPlaytime = Long.valueOf(value);
			break;

		case "donation-status":
			player.setDonatorPrivilege(DonatorPrivilege.valueOf(value));
			break;

		case "last-connection-ip":
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parsePosition(Player player, String key, String value) {
		switch (key) {
		case "position-x":
			player.getLocation().setAs(new Location(Integer.valueOf(value), player.getLocation().getY(), player.getLocation().getZ()));
			break;

		case "position-y":
			player.getLocation().setAs(new Location(player.getLocation().getX(), Integer.valueOf(value), player.getLocation().getZ()));
			break;

		case "position-z":
			player.getLocation().setAs(new Location(player.getLocation().getX(), player.getLocation().getY(), Integer.valueOf(value)));
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseEssentials(Player player, String key, String value) {
		switch (key) {
		case "essential-arpg-passives":
            player.getArpgPassives().load(value,player.getArpgProgress().points());break;
        case "essential-appearance-cleanup":player.setAttribute("appearance-cleanup",Boolean.parseBoolean(value));break;
        case "essential-currency-art-migrated":player.setAttribute("currency-art-migrated",Boolean.parseBoolean(value));break;
        case "essential-starter-claimed":
            player.setAttribute("starter-claimed",Boolean.parseBoolean(value));break;
        case "essential-tutorial-stage":player.setAttribute("tutorial-stage",Math.max(0,Math.min(11,Integer.parseInt(value))));break;
        case "essential-tutorial-practice":player.setAttribute("tutorial-practice",Boolean.parseBoolean(value));break;
        case "essential-tutorial-casts":player.setAttribute("tutorial-casts",Integer.parseInt(value)&3);break;
        case "essential-intro-complete":
            player.setAttribute("intro-complete",Boolean.parseBoolean(value));break;
        case "essential-arpg-rewards":
            player.getArpgRewards().load(value);break;
        case "essential-arpg-xp":
            player.getArpgProgress().restore(Long.parseLong(value));break;
        case "essential-arpg-life":
            player.getArpgVitals().restore(Double.parseDouble(value),player.getArpgVitals().shield());break;
        case "essential-arpg-es":
            player.getArpgVitals().restore(player.getArpgVitals().life(),Double.parseDouble(value));break;
        case "essential-arpg-support-owned":player.getArpgSkills().loadOwned(value);break;
        case "essential-arpg-support-slam":player.getArpgSkills().setSupport(3,Integer.parseInt(value));break;
        case "essential-arpg-support-execute":player.getArpgSkills().setSupport(4,Integer.parseInt(value));break;
        case "essential-arpg-hotbar":player.getArpgSkills().loadHotbar(value);break;
        case "essential-arpg-gems":
            player.getArpgSkills().loadLevels(value);break;
        case "essential-arpg-support-q":
            player.getArpgSkills().setSupport(0,Integer.parseInt(value));break;
        case "essential-arpg-support-e":player.getArpgSkills().setSupport(2,Integer.parseInt(value));break;
        case "essential-arpg-support-w":
            player.getArpgSkills().setSupport(1,Integer.parseInt(value));break;
        case "essential-spellbook":
			player.setSpellbook(Spellbook.valueOf(value));
			break;

		case "essential-god-alignment":
			player.altarPrayed = Integer.valueOf(value);
			break;

		case "essential-fight-mode":
			player.getCombat().setAttackStyle(AttackStyle.getAttackType(Integer.valueOf(value)));
			break;

		case "essential-split-chat":
			player.isChatSplit = Boolean.valueOf(value);
			break;

		case "essential-profanity-filter":
			player.profanityFilter = Boolean.valueOf(value);
			break;

		case "essential-exp-counter":
			player.totalExperience = Integer.valueOf(value);
			break;

		case "essential-skull-timer":
			player.skullTimer = Integer.valueOf(value);
			break;

		case "essential-special-energy":
			player.specialEnergy = Integer.valueOf(value);
			break;

		case "essential-special-energy-cooldown":
			player.playgroundAltarCooldown = Integer.valueOf(value);
			break;

		case "essential-crystal-bow-usages":
			player.crystalBowArrowCount = Integer.valueOf(value);
			break;

		case "essential-run-energy":
			player.getRunEnergy().setEnergy(Double.valueOf(value));
			break;

		case "essential-run-status":
			player.getRunEnergy().setRunning(Boolean.parseBoolean(value));
			break;

		case "essential-poison-damage":
			break;

		case "essential-auto-retaliate":
			player.isAutoRetaliateEnabled = Integer.valueOf(value);
			break;

		case "essential-brightness-level":
			player.brightnessLevel = Integer.valueOf(value);
			break;

		case "essential-yelltag":
			player.getYT().setTag(value);
			break;

		case "essential-yellcolor":
			player.getYT().setColour(value);
			break;

		case "essential-option-globalchat":
			player.disabledGlobalChat = Boolean.valueOf(value);
			break;

		case "essential-option-newsfeed":
			player.setNewsfeedEnabled(Boolean.valueOf(value));
			break;

		case "essential-option-xplock":
			player.enabledXPLock = Boolean.valueOf(value);
			break;

		case "essential-last-clanchat":
			player.friendschatName = value;
			break;

		case "essential-dfs-count":
			player.dfsCount = Integer.valueOf(value);
			break;
		case "essential-ss-chest":
			player.setAttribute("stronghold_chest", Integer.valueOf(value));
			break;

		case "essential-music-volume":
			player.setAttribute("music_volume", Integer.valueOf(value));
			break;

		case "essential-sound-volume":
			player.setAttribute("sound_volume", Integer.valueOf(value));
			break;
			
		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseTitleSettings(Player player, String key, String value) {
		switch (key) {
		case "title-current": {
			String[] split = value.split("\t");
			String name = split[0];
			int color = Integer.decode(split[1]);
			boolean prefix = Boolean.valueOf(split[2]);
			player.setTitle(Title.of(name, color, prefix));
			break;
		}

		case "title-unlocked":
			String[] split = value.split("\t");
			String name = split[0];
			int color = Integer.decode(split[1]);
			boolean prefix = Boolean.valueOf(split[2]);
			player.unlockedTitles.add(Title.of(name, color, prefix));
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseTreasureTrails(Player player, String key, String value) {
		switch (key) {
		case "EASY":
			player.setEasyCluesCompleted(Integer.parseInt(value));
			break;

		case "MEDIUM":
			player.setMediumCluesCompleted(Integer.parseInt(value));
			break;

		case "HARD":
			player.setHardCluesCompleted(Integer.parseInt(value));
			break;

		case "ELITE":
			player.setEliteCluesCompleted(Integer.parseInt(value));
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseAssignment(Player player, String key, String value) {
		switch (key) {
		case "slayer-assignment-key":
			if (!"null".equals(value)) {
				player.getSlayer().setAssignment(Assignment.of(Integer.parseInt(value)));
			}
			break;

		case "slayer-assignment-master":
			player.getSlayer().setMaster(Master.forId(Integer.valueOf(value)));
			break;

		case "slayer-assignment-count":
			player.getSlayer().setKillsLeft(Integer.valueOf(value));
			break;

		case "count_crystal_chest":
		case "kc_abyssal_demon":
		case "kc_bork":
		case "kc_cave_horror":
		case "kc_chaos_elemental":
		case "kc_corporeal_beast":
		case "kc_cursebearer":
		case "kc_dark_beast":
		case "kc_dagannoth_prime":
		case "kc_dagannoth_rex":
		case "kc_dagannoth_supreme":
		case "kc_king_black_dragon":
		case "kc_nomad":
		case "kc_tormented_demon":
		case "kc_ballak_pummeller":
		case "kc_gwd_boss_armadyl":
		case "kc_gwd_boss_bandos":
		case "kc_gwd_boss_saradomin":
		case "kc_gwd_boss_zamorak":
		case "kc_gwd_minion_armadyl":
		case "kc_gwd_minion_bandos":
		case "kc_gwd_minion_saradomin":
		case "kc_gwd_minion_zamorak":
			player.setAttribute(key, Integer.valueOf(value));
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseMusic(Player player, String key, String value) {
		int index = Integer.parseInt(key);
		player.unlocked[index] = Boolean.parseBoolean(value);
		if (player.unlocked[index]) {
			player.musicAmountUnlocked++;
		}
	}

	private static void parseMinigames(Player player, String key, String value) {
		switch (key) {
		case "barrows-kill-count":
			player.setBarrowsKillCount(Integer.valueOf(value));
			break;

		case "barrows-chosen-coffin":
			player.setHiddenBrother(Integer.valueOf(value));
			break;

		case "barrows-killed-0":
			player.getKilledBrothers()[0] = Boolean.valueOf(value);
			break;

		case "barrows-killed-1":
			player.getKilledBrothers()[1] = Boolean.valueOf(value);
			break;

		case "barrows-killed-2":
			player.getKilledBrothers()[2] = Boolean.valueOf(value);
			break;

		case "barrows-killed-3":
			player.getKilledBrothers()[3] = Boolean.valueOf(value);
			break;

		case "barrows-killed-4":
			player.getKilledBrothers()[4] = Boolean.valueOf(value);
			break;

		case "barrows-killed-5":
			player.getKilledBrothers()[5] = Boolean.valueOf(value);
			break;

		case "effect":
			player.getEF().deserialize(player, value);
			break;

		case "bh-target":
			player.getBountyHunter().addTargetToList(value);
			break;

		case "bh-hunter":
			player.getBountyHunter().getHunterInfo()[1] = Integer.valueOf(value);
			break;

		case "bh-rogue":
			player.getBountyHunter().getRogueInfo()[1] = Integer.valueOf(value);
			break;

		case "bh-targets-lost":
			player.getBountyHunter().setTargetsLost(Integer.valueOf(value));
			break;

		case "bh-bounties":
			player.setBounties(Integer.valueOf(value));
			break;

		case "challenge-has-succeeded":
			player.wonChallenge = Boolean.valueOf(value);
			break;

		case "fight-kiln-inside":
			player.getMinigame().setInKiln(Boolean.valueOf(value));
			break;

		case "fight-kiln-fee":
			player.getMinigame().setKilnFee(Boolean.valueOf(value));
			break;

		case "tzhaar-current-wave":
			player.getMinigame().setTzhaarWave(Integer.valueOf(value));
			break;

		case "challenge-current-wave":
			player.getMinigame().setChallengeWave(Integer.valueOf(value));
			break;

		case "god-wars-kill-count":
			break;

		case "godwars-kc-0":
			player.getGodwardsKc()[0] = Integer.valueOf(value);
			break;

		case "godwars-kc-1":
			player.getGodwardsKc()[1] = Integer.valueOf(value);
			break;

		case "godwars-kc-2":
			player.getGodwardsKc()[2] = Integer.valueOf(value);
			break;

		case "godwars-kc-3":
			player.getGodwardsKc()[3] = Integer.valueOf(value);
			break;
			
		case "rfd-status":
			player.rfdGloveCount = Integer.valueOf(value);
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseFamiliarData(Player player, String key, String value) {
		switch (key) {
		case "familiar-item-id":
			player.getSummoning().setFamiliarItem(Integer.valueOf(value));
			break;

		case "familiar-special-energy":
			player.getSummoning().setSpecialPoints(Integer.valueOf(value));
			break;

		case "familiar-time":
			player.getSummoning().setRemainingTicks(Integer.valueOf(value));
			break;

		case "familiar-inventory-size":
			break;

		case "pet-size":
			player.temp_stage = Integer.valueOf(value);
			break;

		case "pet-hunger":
			player.temp_hunger = Integer.valueOf(value);
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parsePointStatistics(Player player, String key, String value) {
		switch (key) {
		case "points-achievement":
			player.achievementPoints = Integer.valueOf(value);
			break;

		case "points-donation-current":
			player.donatorPoints = Integer.valueOf(value);
			break;

		case "points-donation-total":
			player.totalDonationAmount = Double.valueOf(value);
			break;

		case "points-fight-pits":
			player.setFightPitPoints(Integer.valueOf(value));
			break;

		case "points-pest-control":
			player.pcPoints = Integer.valueOf(value);
			break;

		case "points-slayer-reward":
			player.slayerPoints = Integer.valueOf(value);
			break;

		case "points-pvp":
			player.pvpPoints = Integer.valueOf(value);
			break;

		case "pvp-kills":
			player.killCount = Integer.valueOf(value);
			break;

		case "pvp-deaths":
			player.deathCount = Integer.valueOf(value);
			break;

		case "duels_won":
			player.getDuel().getDuelData()[0] = Integer.valueOf(value);
			break;

		case "duels_lost":
			player.getDuel().getDuelData()[1] = Integer.valueOf(value);
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseBankPin(Player player, String key, String value) {
		switch (key) {
		case "bank-PIN":
			player.getBankPinHandler().getSettings().setPin(value);
			break;

		case "bank-recovery-delay":
			player.getBankPinHandler().getSettings().setRecoverDelay(Integer.valueOf(value));
			break;

		case "bank-attempts":
			player.getBankPinHandler().getSettings().setAttempts(Integer.valueOf(value), Integer.valueOf(value));
			break;

		case "bank-disable-time":
			player.getBankPinHandler().getSettings().setDisableTime(Integer.valueOf(value));
			break;

		case "bank-activate-date":
			if (!"null".equals(value)) {
				//player.getBankPinHandler().getSettings().setActiveDate(new Date(Long.valueOf(value)));
			}
			break;

		case "bank-remove-date":
			if (!"null".equals(value)) {
				//player.getBankPinHandler().getSettings().setRemoveDate(new Date(Long.valueOf(value)));
			}
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseAchievements(Player player, String key, String value) {
		switch (key) {
		case "achievement":
			String[] split = value.split("\t", 2);
			int id = Integer.valueOf(split[0]);
			int status = Integer.valueOf(split[1]);
			player.achievements[id] = status;
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseAppearance(Player player, String key, String value) {
		switch (key) {
		case "appearance":
			String[] split = value.split("\t", 2);
			int id = Integer.valueOf(split[0]);
			int val = Integer.valueOf(split[1]);
			player.getAppearance().setLook(id, val);
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseBank(Player player, String key, String value) {
		switch (key) {
		case "bank-item":
			String[] split = value.split("\t");
			int slot = Integer.valueOf(split[0]);
			int id = Integer.valueOf(split[1]);
			int amount = Integer.valueOf(split[2]);
			int tab = Integer.valueOf(split[3]);
			player.getBank().set(slot, com.koios.game.arpg.ItemRollCodec.restore(new BankItem(tab, id, amount),split,4));
			player.getBankField().getTabAmounts()[tab]++;

			if (tab < player.getBankField().getLoadTab() || tab - player.getBankField().getLoadTab() > 1) {
				player.getBankField().setLoadError(true);
			}

			player.getBankField().setLoadTab(tab);
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseEquipment(Player player, String key, String value) {
		switch (key) {
		case "equipment-item":
			String[] split = value.split("\t");
			int slot = Integer.valueOf(split[0]);
			int id = Integer.valueOf(split[1]);
			int amount = Integer.valueOf(split[2]);
			player.getEquipment().set(slot, com.koios.game.arpg.ItemRollCodec.restore(new Item(id, amount),split,3));
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseInventory(Player player, String key, String value) {
		switch (key) {
		case "inventory-item":
			String[] split = value.split("\t");
			int slot = Integer.valueOf(split[0]);
			int id = Integer.valueOf(split[1]);
			int amount = Integer.valueOf(split[2]);
			player.getInventory().set(slot, com.koios.game.arpg.ItemRollCodec.restore(new Item(id, amount),split,3));
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseFamiliarInventory(Player player, String key, String value) {
		switch (key) {
		case "familiar-inventory-item":
		case "familiar-item":
			String[] split = value.split("\t");
			int slot = Integer.valueOf(split[0]);
			int id = Integer.valueOf(split[1]);
			int amount = Integer.valueOf(split[2]);
			player.temporaryFamiliarInventory.set(slot, com.koios.game.arpg.ItemRollCodec.restore(new Item(id, amount),split,3));
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseStats(Player player, String key, String value) {
		switch (key) {
		case "stat":
			String[] split = value.split("\t");
			int id = Integer.valueOf(split[0]);
			int level = Integer.valueOf(split[1]);
			int experience = Integer.valueOf(split[2]);
			int goal = Integer.valueOf(split[3]);
			int goalType = Integer.valueOf(split[4]);
			int goalInit = Integer.valueOf(split[5]);
			player.getSkill().setSkill(id, level, experience);
			player.getSkill().setGoalData(id, goal > 1 ? goal : -1, goalType, goalInit);
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseFriendList(Player player, String key, String value) {
		switch (key) {
		case "friend":
			putInFirstAvailableSlot(player.friends, StringUtil.usernameToHash(value));
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void parseIgnoreList(Player player, String key, String value) {
		switch (key) {
		case "ignore":
			putInFirstAvailableSlot(player.ignores, StringUtil.usernameToHash(value));
			break;

		default:
			throw new NoSuchElementException("'" + key + "' = '" + value + "'");
		}
	}

	private static void putInFirstAvailableSlot(long[] a, long value) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] == 0L) {
				a[i] = value;
				break;
			}
		}
	}

	private PlayerLoader() {
		throw new InstantiationError();
	}

}
