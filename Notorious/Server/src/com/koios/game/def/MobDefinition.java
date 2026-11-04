package com.koios.game.def;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koios.util.JsonLoader;

/**
 * @author Erik Eide
 * @author Tringan
 */
public final class MobDefinition {

	public static MobDefinition get(final int id) {
		return definitions[com.koios.game.arpg.EncounterNpcs.template(com.koios.game.arpg.HubServices.template(id))];
	}

	/**
	 * Return the highest mob id
	 */
	public static int getHighestID() {
		return definitions.length;
	}

	public static void main(String[] args) throws IOException {
		parse().load();

		// List<String> lines = Files.readAllLines(Paths.get("data/definitions",
		// "mob_size_definitions.txt"));
		// for (String line : lines) {
		// StringTokenizer tokenizer = new StringTokenizer(line, ":");
		// while (tokenizer.hasMoreTokens()) {
		// int id = Integer.parseInt(tokenizer.nextToken());
		// byte size = Byte.parseByte(tokenizer.nextToken());
		// definitions[id].size = size;
		// }
		// }

		System.setOut(new PrintStream(new File("./data/mob_definition.json")));
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(MobDefinition.get()));
	}

	/**
	 * The mob definition
	 */
	private static MobDefinition[] definitions = new MobDefinition[16733];

	/**
	 * The mob actions
	 */
	private String[] actions = null;

	/**
	 * The mob aggressive flag
	 */
	private boolean aggressive = false;

	/**
	 * The mob attack animation
	 */
	private int attackAnimation = -1;

	/**
	 * The mob attack level
	 */
	private int attackLevel = -1;

	/**
	 * The mob combat level
	 */
	private short combatLevel = -1;

	/**
	 * The mob death animation
	 */
	private int deathAnimation = -1;

	/**
	 * The mob block animation
	 */
	private int blockAnimation = -1;

	/**
	 * The mob max hit
	 */
	private int maxHit = -1;

	/**
	 * The mob defence level
	 */
	private int defenceLevel = -1;

	/**
	 * The mob description
	 */
	private final String description = "none";

	/**
	 * The mob hitpoints
	 */
	private short hitpoints = 0;

	/**
	 * The mob id
	 */
	private final short id;

	/**
	 * The mob range animation
	 */
	private int magicAnimation = -1;

	/**
	 * The mob magic level
	 */
	private int magicLevel = -1;

	/**
	 * The mob members flag
	 */
	private boolean members = false;

	/**
	 * The mob name
	 */
	private String name = "none";

	/**
	 * The mob poisonous flag
	 */
	private boolean poisonous = false;

	/**
	 * The mob range animation
	 */
	private int rangeAnimation = -1;

	/**
	 * The mob ranged level
	 */
	private int rangedLevel = -1;

	/**
	 * The mob size
	 */
	private byte size = 1;

	/**
	 * The mob slayer key
	 */
	private String slayerKey = "none";

	/**
	 * The mob slayer requirement
	 */
	private int slayerRequirement = 1;

	/**
	 * The mob stand animation
	 */
	private int standAnimation = -1;

	/**
	 * The mob turn 180 animation
	 */
	private int turn180Animation = -1;

	/**
	 * The mob turn 90 animation
	 */
	private int[] turn90Animation = { -1, -1 };

	/**
	 * The mob walk animation
	 */
	private int walkAnimation = -1;

	private MobDefinition(final int id) {
		if (id < 0 || id > Short.MAX_VALUE) {
			throw new InternalError("illegal NPC definition ID: " + id);
		}

		this.id = (short) id;
	}

	public Optional<String> getAction(int index) {
		if (actions == null) {
			return Optional.empty();
		}

		if (index < 0 || index >= actions.length) {
			throw new IllegalArgumentException();
		}

		return Optional.ofNullable(actions[index]);
	}

	/**
	 * Parses the item definitions from the json file
	 *
	 * @return
	 */
	public static JsonLoader parse() {
		return new JsonLoader() {

			@Override
			public void load(JsonObject reader, Gson builder) {
				int id = reader.get("id").getAsInt();
				try {

					definitions[id] = new MobDefinition(id);
					String name = "none";
					try {
						name = reader.get("name").getAsString();
					} catch (Exception e) {

					}
					definitions[id].name = name;
					definitions[id].combatLevel = reader.get("combatLevel").getAsShort();
					definitions[id].hitpoints = reader.get("hitpoints").getAsShort();
					definitions[id].size = reader.get("size").getAsByte();
					definitions[id].walkAnimation = reader.get("walkAnimation").getAsInt();
					definitions[id].deathAnimation = reader.get("deathAnimation").getAsInt();
					definitions[id].blockAnimation = reader.get("blockAnimation").getAsInt();
					definitions[id].standAnimation = reader.get("standAnimation").getAsInt();
					definitions[id].turn180Animation = reader.get("turn180Animation").getAsInt();
					definitions[id].turn90Animation = builder.fromJson(reader.get("turn90Animation").getAsJsonArray(), int[].class);
					definitions[id].attackAnimation = reader.get("attackAnimation").getAsInt();
					definitions[id].rangeAnimation = reader.get("rangeAnimation").getAsInt();
					definitions[id].magicAnimation = reader.get("magicAnimation").getAsInt();

					String[] actions = new String[5];
					try {
						actions = builder.fromJson(reader.get("actions").getAsJsonArray(), String[].class);
					} catch (Exception e) {

					}
					definitions[id].actions = actions;

					definitions[id].maxHit = reader.get("maxHit").getAsInt();
					definitions[id].attackLevel = reader.get("attackLevel").getAsInt();
					definitions[id].defenceLevel = reader.get("defenceLevel").getAsInt();
					definitions[id].rangedLevel = reader.get("rangedLevel").getAsInt();
					definitions[id].magicLevel = reader.get("magicLevel").getAsInt();

					definitions[id].slayerRequirement = reader.get("slayerRequirement").getAsInt();
					definitions[id].slayerKey = reader.get("slayerKey").getAsString();
					definitions[id].poisonous = reader.get("poisonous").getAsBoolean();
					definitions[id].aggressive = reader.get("aggressive").getAsBoolean();
					definitions[id].members = reader.get("members").getAsBoolean();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("failed to load mob definition: " + id);
				}
			}

			@Override
			public String filePath() {
				return "./data/mob_definition.json";
			}

		};
	}

	/**
	 * Return mob attack animation
	 */
	public int getAttackAnimation() {
		return attackAnimation;
	}

	/**
	 * Return mob attack level
	 */
	public int getAttackLevel() {
		return attackLevel;
	}

	/**
	 * Return mob combat level
	 */
	public int getCombatLevel() {
		return combatLevel;
	}

	/**
	 * Return mob death animation
	 */
	public int getDeathAnimation() {
		return deathAnimation;
	}

	/**
	 * Return mob death animation
	 */
	public int getBlockAnimation() {
		return blockAnimation;
	}

	/**
	 * Return mob death animation
	 */
	public int getMaxHit() {
		return maxHit;
	}

	/**
	 * Return mob defence level
	 */
	public int getDefenceLevel() {
		return defenceLevel;
	}

	/**
	 * Return mob description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Return mob hitpoints
	 */
	public int getHitpoints() {
		return hitpoints;
	}

	/**
	 * Return mob id
	 */
	public int getID() {
		return id;
	}

	/**
	 * Return mob magic animation
	 */
	public int getMagicAnimation() {
		return magicAnimation;
	}

	/**
	 * Return mob magic level
	 */
	public int getMagicLevel() {
		return magicLevel;
	}

	/**
	 * Return mob name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return mob range animation
	 */
	public int getRangeAnimation() {
		return rangeAnimation;
	}

	/**
	 * Return mob ranged level
	 */
	public int getRangedLevel() {
		return rangedLevel;
	}

	/**
	 * Return mob size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Return mob slayer key
	 */
	public String getSlayerKey() {
		return slayerKey;
	}

	/**
	 * Return mob slayer requirement
	 */
	public int getSlayerRequirement() {
		return slayerRequirement;
	}

	/**
	 * Return mob standAnimation
	 */
	public int getStandAnimation() {
		return standAnimation;
	}

	/**
	 * Return mob turn180animation
	 */
	public int getTurn180Animation() {
		return turn180Animation;
	}

	/**
	 * Return mob turn90animation
	 */
	public int getTurn90Animation(final boolean antiClockwise) {
		return turn90Animation[antiClockwise ? 1 : 0];
	}

	/**
	 * Return mob walk animation
	 */
	public int getWalkAnimation() {
		return walkAnimation;
	}

	/**
	 * Return mob aggressive
	 */
	public boolean isAggressive() {
		return aggressive;
	}

	/**
	 * Return mob attackable
	 */
	public boolean isAttackable() {
		return hitpoints != 0;
	}

	/**
	 * Return mob members
	 */
	public boolean isMembers() {
		return members;
	}

	/**
	 * Return mob poisonous
	 */
	public boolean isPoisonous() {
		return poisonous;
	}

	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder("MobDefinition[id=");
		bldr.append(id).append(", name='").append(name).append("']");
		return bldr.toString();
	}

	public static MobDefinition[] get() {
		return definitions;
	}

}
