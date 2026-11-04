package com.koios.game.def;

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.IntStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koios.game.model.item.container.equip.Equipment.EquipmentType;

public final class ItemDefinition {

	private static ItemDefinition[] definitions;

	private static final String JSON_FILE = "./data/definitions/item_definitions.json";

	public static ItemDefinition forId(int id) {
		return id < 0 || id > definitions.length || definitions[id] == null ? definitions[0] : definitions[id];
	}

	public static Optional<ItemDefinition> getOptional(int id) {
		if (id < 0 || id >= definitions.length) {
			return Optional.empty();
		}

		return Optional.of(definitions[id]);
	}

	public static String getName(int id) {
		ItemDefinition definition = forId(id);

		if (definition == null) {
			return "null";
		}

		return definition.getName() == null ? "null" : definition.getName();
	}

	public static void load() {
		try (FileReader reader = new FileReader(JSON_FILE)) {
			JsonParser parser = new JsonParser();
			JsonArray root = parser.parse(reader).getAsJsonArray();
			definitions = new ItemDefinition[Math.max(root.size(),22500)];
			IntStream.range(0, definitions.length).forEach(id -> definitions[id] = new ItemDefinition(id));

			for (int i = 0; i < root.size(); i++) {
				JsonObject element = root.get(i).getAsJsonObject();
				JsonArray bonuses = element.get("bonuses").getAsJsonArray();
				ItemDefinition definition = definitions[element.get("id").getAsInt()];
				definition.name = element.get("name").getAsString();
				definition.untradeable = element.get("is_untradeable").getAsBoolean();
				definition.weapon = element.get("is_weapon").getAsBoolean();
				definition.noted = element.get("is_noted").getAsBoolean();
				definition.stackable = element.get("is_stackable").getAsBoolean();
				definition.isTwoHanded = element.get("is_two_handed").getAsBoolean();
				definition.lended = element.get("is_lended").getAsBoolean();
				definition.noteId = element.get("note_transform_id").getAsInt();
				definition.lendId = element.get("lend_transform_id").getAsInt();
				definition.weight = element.get("weight").getAsDouble();
				definition.value = element.get("value").getAsInt();
				definition.lowAlchemy = element.get("low_alchemy_value").getAsInt();
				definition.highAlchemy = element.get("high_alchemy_value").getAsInt();
				definition.equipmentType = EquipmentType.valueOf(element.get("equipment_type").getAsString());
				IntStream.range(0, 18).forEach(n -> definition.bonus[n] = bonuses.get(n).getAsDouble());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

  for(ItemDefinition d:definitions)if(d.name==null){d.name="Unknown item";d.equipmentType=definitions[561].equipmentType;d.noteId=-1;d.lendId=-1;}
  String[] currencyNames={"Orb of Alchemy","Chaos Orb","Exalted Orb","Orb of Transmutation","Vaal Orb","Scroll of Wisdom","Portal Scroll"};
  for(int n=0;n<7;n++){ItemDefinition d=definitions[22400+n];d.name=currencyNames[n];d.description="Use on equipment or a map to craft it.";d.stackable=true;d.untradeable=false;d.noteId=-1;d.lendId=-1;d.equipmentType=definitions[561].equipmentType;d.value=1;}
  int[] addedCurrencyIds={22407,22408,22409,22415,22416};String[] addedCurrencyNames={"Orb of Alteration","Orb of Augmentation","Regal Orb","Orb of Scouring","Orb of Annulment"};for(int n=0;n<addedCurrencyIds.length;n++){ItemDefinition d=definitions[addedCurrencyIds[n]];d.name=addedCurrencyNames[n];d.description="Use on equipment or a map to craft it.";d.stackable=true;d.untradeable=false;d.noteId=-1;d.lendId=-1;d.equipmentType=definitions[561].equipmentType;d.value=1;}
  custom(22413,8007,"Ashen Foundry Map");custom(22414,8007,"Thornwild Courtyard Map");
  for(com.koios.game.arpg.EquipmentBases.Base base:com.koios.game.arpg.EquipmentBases.all())custom(base.itemId,base.templateId,base.name);
  custom(22410,8007,"Forsaken Settlement Map");custom(22411,1623,"Uncut Skill Gem");custom(22412,1631,"Uncut Support Gem");
		System.out.println("Finished reading " + definitions.length + " item definitions!");
	}

 private static void custom(int id,int template,String name){ItemDefinition source=definitions[template],d=definitions[id];d.name=name;d.description="An artifact from the forsaken lands.";d.stackable=false;d.noted=false;d.lended=false;d.untradeable=false;d.noteId=-1;d.lendId=-1;d.weapon=source.weapon;d.equipmentType=source.equipmentType;d.isTwoHanded=source.isTwoHanded;d.weight=source.weight;d.value=source.value;d.lowAlchemy=source.lowAlchemy;d.highAlchemy=source.highAlchemy;System.arraycopy(source.bonus,0,d.bonus,0,d.bonus.length);}

	public static ItemDefinition[] toArray() {
		return definitions;
	}

	private final double[] bonus = new double[18];
	private String description;
	private boolean destroyable;
	private EquipmentType equipmentType;
	private int highAlchemy;
	private final int id;
	private boolean isTwoHanded;
	private boolean lended;
	private int lendId;
	private int lowAlchemy;
	private String name;
	private boolean noted;
	private int noteId;
	private boolean stackable;
	private boolean untradeable;
	private int value;
	private boolean weapon;
	private double weight;

	private ItemDefinition(int id) {
		this.id = id;
	}

	public double[] getBonus() {
		return bonus;
	}

	public String getDescription() {
		return description;
	}

	public EquipmentType getEquipType() {
		return equipmentType;
	}

	public int getHighAlchemy() {
		return highAlchemy;
	}

	public int getId() {
		return id;
	}

	public int getLendId() {
		return lendId;
	}

	public int getLowAlchemy() {
		return lowAlchemy;
	}

	public String getName() {
		return name;
	}

	public int getNoteId() {
		return noteId;
	}

	public int getValue() {
		return value;
	}

	public double getWeigth() {
		return weight;
	}

	public boolean isDestroyable() {
		return destroyable;
	}

	public boolean isLended() {
		return lended;
	}

	public boolean isNoted() {
		return noted;
	}

	public boolean isStackable() {
		return stackable;
	}

	public boolean isTwoHanded() {
		return isTwoHanded;
	}

	public boolean isUntradeable() {
		return untradeable;
	}

	public boolean isWeapon() {
		return weapon;
	}

}

