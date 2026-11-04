package com.koios.game.model.player.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import com.koios.game.content.effects.Effect;
import com.koios.game.content.skill.Skill;
import com.koios.game.content.skill.farming.Farming;
import com.koios.game.content.titles.Title;
import com.koios.game.model.item.Item;
import com.koios.game.model.item.container.bank.BankItem;
import com.koios.game.model.player.Player;
import com.koios.util.StringUtil;

public final class PlayerSaver {

	public static void save(Player player) {
		Objects.requireNonNull(player, "player");

		com.koios.game.arpg.Tutorial.releaseLegacyRestrictions(player);

		if (!player.allowSaveRequests || !player.saveCharacter) {
			return;
		}

		Farming.save(player);

		try {
			// fetch the character file for the player in question
			Path characterFile = StoreUtils.getCharacterFile(player.getName());

			try (BufferedWriter bw = Files.newBufferedWriter(characterFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
				// append account credential category
				bw.write("[CREDENTIALS]");
				bw.newLine();
				writeValue(bw, "password-plain", player.getPasswordPlain());

				// append account variables
				writeHeader(bw, "ACCOUNT");
				writeValue(bw, "user_id", player.userId);
				writeValue(bw, "usergroup", player.getPrivilege().name());
				writeValue(bw, "join-date", player.getJoinDate());
				writeValue(bw, "play-duration", player.getCurrentPlaytime());
				writeValue(bw, "donation-status", player.getDonatorPrivilege().name());
				writeValue(bw, "last-connection-ip", player.getSession().getHostAddress());

				// append position CATEGORY
				writeHeader(bw, "POSITION");
				writeValue(bw, "position-x", player.getLocation().getX());
				writeValue(bw, "position-y", player.getLocation().getY());
				writeValue(bw, "position-z", player.getLocation().getZ());

				// append essential variables; such as active spellbook and
				// teleblock duration
				writeHeader(bw, "ESSENTIALS");
				writeValue(bw,"essential-appearance-cleanup",player.getAttribute("appearance-cleanup",false));
                writeValue(bw,"essential-currency-art-migrated",player.getAttribute("currency-art-migrated",false));
                writeValue(bw,"essential-starter-claimed",player.getAttribute("starter-claimed",false));
                writeValue(bw,"essential-tutorial-stage",player.getAttribute("tutorial-stage",0));
                writeValue(bw,"essential-tutorial-practice",player.getAttribute("tutorial-practice",false));
                writeValue(bw,"essential-tutorial-casts",player.getAttribute("tutorial-casts",0));
                writeValue(bw,"essential-intro-complete",player.getAttribute("intro-complete",false));
                writeValue(bw,"essential-arpg-rewards",player.getArpgRewards().save());
                writeValue(bw,"essential-arpg-xp",player.getArpgProgress().xp());
                writeValue(bw,"essential-arpg-passives",player.getArpgPassives().save());
                writeValue(bw,"essential-arpg-life",player.getArpgVitals().life());
                writeValue(bw,"essential-arpg-es",player.getArpgVitals().shield());
                writeValue(bw,"essential-arpg-support-owned",player.getArpgSkills().saveOwned());
                writeValue(bw,"essential-arpg-support-slam",player.getArpgSkills().getSupport(3));
                writeValue(bw,"essential-arpg-support-execute",player.getArpgSkills().getSupport(4));
                writeValue(bw,"essential-arpg-hotbar",player.getArpgSkills().saveHotbar());
                writeValue(bw,"essential-arpg-gems",player.getArpgSkills().saveLevels());
                writeValue(bw,"essential-arpg-support-q",player.getArpgSkills().getSupport(0));
                writeValue(bw,"essential-arpg-support-w",player.getArpgSkills().getSupport(1));
                writeValue(bw,"essential-arpg-support-e",player.getArpgSkills().getSupport(2));
                writeValue(bw, "essential-spellbook", player.getSpellbook().name());
				writeValue(bw, "essential-god-alignment", player.altarPrayed);
				writeValue(bw, "essential-fight-mode", player.getCombat().getAttackStyle().ordinal());
				writeValue(bw, "essential-split-chat", player.isChatSplit);
				writeValue(bw, "essential-profanity-filter", player.profanityFilter);
				writeValue(bw, "essential-exp-counter", player.totalExperience);
				writeValue(bw, "essential-skull-timer", player.skullTimer);
				writeValue(bw, "essential-special-energy", player.specialEnergy);
				writeValue(bw, "essential-special-energy-cooldown", player.playgroundAltarCooldown);
				writeValue(bw, "essential-crystal-bow-usages", player.crystalBowArrowCount);
				writeValue(bw, "essential-run-energy", player.getRunEnergy().getEnergy());
				writeValue(bw, "essential-run-status", player.getRunEnergy().isRunning());
				writeValue(bw, "essential-auto-retaliate", player.isAutoRetaliateEnabled);
				writeValue(bw, "essential-brightness-level", player.brightnessLevel);
				writeValue(bw, "essential-yelltag", player.getYT().getTag());
				writeValue(bw, "essential-yellcolor", player.getYT().getColour());
				writeValue(bw, "essential-option-globalchat", player.disabledGlobalChat);
				writeValue(bw, "essential-option-newsfeed", player.isNewsfeedEnabled());
				writeValue(bw, "essential-option-xplock", player.enabledXPLock);
				writeValue(bw, "essential-dfs-count", player.dfsCount);
				writeValue(bw, "essential-ss-chest", player.getAttribute("stronghold_chest", 0));
				writeValue(bw, "essential-music-volume", player.getAttribute("music_volume", 2));
				writeValue(bw, "essential-sound-volume", player.getAttribute("sound_volume", 2));

				if (player.friendschatName != null) {
					writeValue(bw, "essential-last-clanchat", player.friendschatName);
				}

				// append title settings
				writeHeader(bw, "TITLE-SETTINGS");

				if (player.getTitle() != null) {
					Title title = player.getTitle();

					bw.write("title-current = ");
					bw.write(title.getName());
					bw.write("\t#");
					bw.write(Integer.toHexString(title.getColor()));
					bw.write('\t');
					bw.write(String.valueOf(title.isPrefix()));
					bw.newLine();
				}

				for (Title title : player.unlockedTitles) {
					bw.write("title-unlocked = ");
					bw.write(title.getName());
					bw.write("\t#");
					bw.write(Integer.toHexString(title.getColor()));
					bw.write('\t');
					bw.write(String.valueOf(title.isPrefix()));
					bw.newLine();
				}

				// append slayer-specific variables
				writeHeader(bw, "ASSIGNMENT");
				writeValue(bw, "slayer-assignment-key", player.getSlayer().getAssignment() == null ? "null" : player.getSlayer().getAssignment().getMobId());
				writeValue(bw, "slayer-assignment-master", player.getSlayer().getMaster() == null ? 0 : player.getSlayer().getMaster().getId());
				writeValue(bw, "slayer-assignment-count", player.getSlayer().getKillsLeft());
				writeValue(bw, "count_crystal_chest", player.getVariable("count_crystal_chest", 0));
				writeValue(bw, "kc_abyssal_demon", player.getVariable("kc_abyssal_demon", 0));
				writeValue(bw, "kc_bork", player.getVariable("kc_bork", 0));
				writeValue(bw, "kc_cave_horror", player.getVariable("kc_cave_horror", 0));
				writeValue(bw, "kc_chaos_elemental", player.getVariable("kc_chaos_elemental", 0));
				writeValue(bw, "kc_corporeal_beast", player.getVariable("kc_corporeal_beast", 0));
				writeValue(bw, "kc_cursebearer", player.getVariable("kc_cursebearer", 0));
				writeValue(bw, "kc_dark_beast", player.getVariable("kc_dark_beast", 0));
				writeValue(bw, "kc_dagannoth_prime", player.getVariable("kc_dagannoth_prime", 0));
				writeValue(bw, "kc_dagannoth_rex", player.getVariable("kc_dagannoth_rex", 0));
				writeValue(bw, "kc_dagannoth_supreme", player.getVariable("kc_dagannoth_supreme", 0));
				writeValue(bw, "kc_king_black_dragon", player.getVariable("kc_king_black_dragon", 0));
				writeValue(bw, "kc_nomad", player.getVariable("kc_nomad", 0));
				writeValue(bw, "kc_tormented_demon", player.getVariable("kc_tormented_demon", 0));
				writeValue(bw, "kc_ballak_pummeller", player.getVariable("kc_ballak_pummeller", 0));
				writeValue(bw, "kc_gwd_boss_armadyl", player.getVariable("kc_gwd_boss_armadyl", 0));
				writeValue(bw, "kc_gwd_boss_bandos", player.getVariable("kc_gwd_boss_bandos", 0));
				writeValue(bw, "kc_gwd_boss_saradomin", player.getVariable("kc_gwd_boss_saradomin", 0));
				writeValue(bw, "kc_gwd_boss_zamorak", player.getVariable("kc_gwd_boss_zamorak", 0));
				writeValue(bw, "kc_gwd_minion_armadyl", player.getVariable("kc_gwd_minion_armadyl", 0));
				writeValue(bw, "kc_gwd_minion_bandos", player.getVariable("kc_gwd_minion_bandos", 0));
				writeValue(bw, "kc_gwd_minion_saradomin", player.getVariable("kc_gwd_minion_saradomin", 0));
				writeValue(bw, "kc_gwd_minion_zamorak", player.getVariable("kc_gwd_minion_zamorak", 0));

				writeHeader(bw, "MUSIC");
				for (int i = 0; i < player.unlocked.length; i++) {
					writeValue(bw, Integer.toString(i), player.unlocked[i]);
				}

				// append treasure trails
				writeHeader(bw, "TREASURE-TRAILS");
				writeValue(bw, "EASY", player.getEasyCluesCompleted());
				writeValue(bw, "MEDIUM", player.getMediumCluesCompleted());
				writeValue(bw, "HARD", player.getHardCluesCompleted());
				writeValue(bw, "ELITE", player.getEliteCluesCompleted());

				// append minigame variables
				writeHeader(bw, "MINIGAMES");
				writeValue(bw, "barrows-kill-count", player.getBarrowsKillCount());
				writeValue(bw, "barrows-chosen-coffin", player.getHiddenBrother());
				writeValue(bw, "barrows-killed-0", player.getKilledBrothers()[0]);
				writeValue(bw, "barrows-killed-1", player.getKilledBrothers()[1]);
				writeValue(bw, "barrows-killed-2", player.getKilledBrothers()[2]);
				writeValue(bw, "barrows-killed-3", player.getKilledBrothers()[3]);
				writeValue(bw, "barrows-killed-4", player.getKilledBrothers()[4]);
				writeValue(bw, "barrows-killed-5", player.getKilledBrothers()[5]);

				for (Effect e : player.getEF().getEffects()) {
					if (e == null || e.getName().length() <= 0) {
						continue;
					}

					writeValue(bw, "effect", e.toString());
				}

				for (String targ : player.getBountyHunter().getLastTargets()) {
					if (targ == null || targ.length() <= 0) {
						continue;
					}

					writeValue(bw, "bh-target", targ);
				}

				writeValue(bw, "bh-hunter", player.getBountyHunter().getHunterInfo()[1]);
				writeValue(bw, "bh-rogue", player.getBountyHunter().getRogueInfo()[1]);
				writeValue(bw, "bh-targets-lost", player.getBountyHunter().getTargetsLost());
				writeValue(bw, "bh-bounties", player.getBounties());

				writeValue(bw, "challenge-has-succeeded", player.wonChallenge);
				writeValue(bw, "fight-kiln-inside", player.getMinigame().inKiln());
				writeValue(bw, "fight-kiln-fee", player.getMinigame().isKilnFeePaid());
				writeValue(bw, "tzhaar-current-wave", player.getMinigame().getTzhaarWave());
				writeValue(bw, "challenge-current-wave", player.getMinigame().getChallengeWave());
				writeValue(bw, "godwars-kc-0", player.getGodwardsKc()[0]);
				writeValue(bw, "godwars-kc-1", player.getGodwardsKc()[1]);
				writeValue(bw, "godwars-kc-2", player.getGodwardsKc()[2]);
				writeValue(bw, "godwars-kc-3", player.getGodwardsKc()[3]);
				
				writeValue(bw, "rfd-status", player.rfdGloveCount);

				// append point variables
				writeHeader(bw, "POINT-STATISTICS");
				writeValue(bw, "points-achievement", player.achievementPoints);
				writeValue(bw, "points-donation-current", player.donatorPoints);
				writeValue(bw, "points-donation-total", player.totalDonationAmount);
				writeValue(bw, "points-fight-pits", player.getFightPitPoints());
				writeValue(bw, "points-pest-control", player.pcPoints);
				writeValue(bw, "points-slayer-reward", player.slayerPoints);
				writeValue(bw, "points-pvp", player.pvpPoints);
				writeValue(bw, "pvp-kills", player.killCount);
				writeValue(bw, "pvp-deaths", player.deathCount);
				writeValue(bw, "duels_won", player.getDuel().getDuelData()[0]);
				writeValue(bw, "duels_lost", player.getDuel().getDuelData()[1]);

				// append bank PIN category
				writeHeader(bw, "BANK-PIN");
				writeValue(bw, "bank-PIN", player.getBankPinHandler().getSettings().getPIN());
				writeValue(bw, "bank-recovery-delay", player.getBankPinHandler().getSettings().getRecoverDelay());
				writeValue(bw, "bank-attempts", player.getBankPinHandler().getSettings().getAttempts());
				writeValue(bw, "bank-disable-time", player.getBankPinHandler().getSettings().getDisableTime());
				writeValue(bw, "bank-activate-date", player.getBankPinHandler().getSettings().getActiveDate());
				writeValue(bw, "bank-remove-date", player.getBankPinHandler().getSettings().getRemoveDate());

				writeHeader(bw, "FAMILIAR-DATA");
				writeValue(bw, "familiar-item-id", player.getSummoning().getFamiliarItem());
				writeValue(bw, "familiar-special-energy", player.getSummoning().getSpecialPoints());
				writeValue(bw, "familiar-time", player.getSummoning().getRemainingTicks());
				writeValue(bw, "pet-size", player.getSummoning().getPet() == null ? player.temp_stage : player.getSummoning().getPet().getStage());
				writeValue(bw, "pet-hunger", player.getSummoning().getPet() == null ? player.temp_hunger : player.getSummoning().getPet().getHunger());

				// append achievement states
				writeHeader(bw, "ACHIEVEMENTS");

				for (int i = 0; i < player.achievements.length; i++) {
					if (player.achievements[i] != 0) {
						bw.write("achievement = ");
						bw.write(String.valueOf(i));
						bw.write('\t');
						bw.write(String.valueOf(player.achievements[i]));
						bw.newLine();
					}
				}

				// append appearance
				writeHeader(bw, "APPEARANCE");

				for (int i = 0; i < player.getAppearance().getLook().length; i++) {
					bw.write("appearance = ");
					bw.write(String.valueOf(i));
					bw.write('\t');
					bw.write(String.valueOf(player.getAppearance().getLook()[i]));
					bw.newLine();
				}

				// append bank contents
				writeHeader(bw, "BANK");

				int bankSize = player.getBank().size();

				for (int index = 0; index < bankSize; index++) {
					Item item = player.getBank().get(index);

					if (item == null) {
						// System.out
						// .printf("Bank item null for player '%s' at bank item index %s. Bank size:
						// %s; Tab sizes: [%s, %s, %s, %s, %s, %s, %s, %s]",
						// player.getName(), index, bankSize,
						// player.getBankField().getTabAmounts()[0], player
						// .getBankField().getTabAmounts()[1],
						// player.getBankField().getTabAmounts()[2],
						// player.getBankField().getTabAmounts()[3],
						// player.getBankField().getTabAmounts()[4],
						// player.getBankField().getTabAmounts()[5],
						// player.getBankField().getTabAmounts()[6],
						// player.getBankField().getTabAmounts()[7]);
						// System.out.println();
						continue;
					} else if (!(item instanceof BankItem)) {
						// System.out.println("Bank item index " + index + " for player '" +
						// player.getName()
						// + "' with item class " + item.getClass().getSimpleName() + " with
						// contents "
						// + item.toString());
						continue;
					}

					bw.write("bank-item");
					bw.write(" = ");
					bw.write(String.valueOf(index));
					bw.write('\t');
					bw.write(String.valueOf(item.getId()));
					bw.write('\t');
					bw.write(String.valueOf(item.getCount()));
					bw.write('\t');
					bw.write(String.valueOf(((BankItem) item).getTabIndex()));
                    bw.write("\t" + com.koios.game.arpg.ItemRollCodec.encode(item));
					bw.write("\t# ");
					bw.write(item.getDefinition() == null ? "missing item definition" : item.getDefinition().getName());
					bw.newLine();
				}

				// append equipment contents
				writeHeader(bw, "EQUIPMENT");
				writeContainer(bw, "equipment-item", player.getEquipment().toArray());

				// append inventory contents
				writeHeader(bw, "INVENTORY");
				writeContainer(bw, "inventory-item", player.getInventory().toArray());

				// append familiar inventory

				if (player.getSummoning().getFamiliar() != null && player.getSummoning().getFamiliarInventory() != null) {
					writeHeader(bw, "FAMILIAR-INVENTORY");
					writeContainer(bw, "familiar-item", player.getSummoning().getFamiliarInventory().toArray());
				}

				// append stats
				writeHeader(bw, "STATS");

				for (int i = 0; i < Skill.SKILL_COUNT; i++) {
					bw.write("stat = ");
					bw.write(String.valueOf(i));
					bw.write('\t');
					bw.write(String.valueOf(player.getSkill().getLevel(i)));
					bw.write('\t');
					bw.write(String.valueOf(player.getSkill().getExperience(i)));
					bw.write('\t');
					bw.write(String.valueOf(player.getSkill().getGoalData(i)[0]));
					bw.write('\t');
					bw.write(String.valueOf(player.getSkill().getGoalData(i)[1]));
					bw.write('\t');
					bw.write(String.valueOf(player.getSkill().getGoalData(i)[2]));
					bw.write("\t# ");
					bw.write(Skill.SKILL_NAME[i]);
					bw.newLine();
				}

				// append friend list
				writeHeader(bw, "FRIEND-LIST");

				for (long nameHash : player.friends) {
					if (nameHash != 0L) {
						bw.write("friend = ");
						bw.write(StringUtil.hashToUsername(nameHash));
						bw.newLine();
					}
				}

				// append ignore list
				writeHeader(bw, "IGNORE-LIST");

				for (long nameHash : player.ignores) {
					if (nameHash != 0L) {
						bw.write("ignore = ");
						bw.write(StringUtil.hashToUsername(nameHash));
						bw.newLine();
					}
				}

				// append EOF indication
				bw.newLine();
				bw.write("[EOF]");
			}
		} catch (IOException ex) {
			throw new UncheckedIOException("Unable to save: " + player.getName(), ex);
		}
	}

	private static void writeContainer(BufferedWriter bw, String tag, Item[] items) throws IOException {
		if (items == null) {
			return;
		}

		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && items[i].getId() >= 0) {
				bw.write(tag);
				bw.write(" = ");
				bw.write(String.valueOf(i));
				bw.write('\t');
				bw.write(String.valueOf(items[i].getId()));
				bw.write('\t');
				bw.write(String.valueOf(items[i].getCount()));
                bw.write("\t" + com.koios.game.arpg.ItemRollCodec.encode(items[i]));
				bw.write("\t# ");
				bw.write(items[i].getDefinition() == null ? "missing item definition" : items[i].getDefinition().getName());
				bw.newLine();
			}
		}
	}

	private static void writeHeader(BufferedWriter bw, String header) throws IOException {
		bw.newLine();
		bw.write('[');
		bw.write(header);
		bw.write(']');
		bw.newLine();
	}

	private static void writeValue(BufferedWriter bw, String key, Object value) throws IOException {
		bw.write(key);
		bw.write(" = ");
		bw.write(String.valueOf(value));
		bw.newLine();
	}

	private PlayerSaver() {
		throw new InstantiationError();
	}

}
