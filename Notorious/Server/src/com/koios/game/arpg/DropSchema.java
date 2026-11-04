package com.koios.game.arpg;

import com.google.gson.*;
import java.util.*;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

/**
 * Strict author-facing schema gate for drops.json.
 *
 * Balance files are deliberately easy to edit, which makes readable failures
 * important. Gson normally accepts quoted numbers and ignores misspelled keys;
 * this class rejects those mistakes with the exact JSON path before rewards can
 * be generated. Underscore-prefixed string fields remain available as comments.
 */
final class DropSchema {
 private static final String[] ROOT={"version","normalTable","eliteTable","bossTable","npcOverrides","pools","tables"};
 private static final String[] TABLE={"id","experiencePerTier","rolls"};
 private static final String[] POOL={"itemId","weight"};
 private static final String[] ROLL={"label","type","pool","baseId","itemId","chanceNumerator","chanceDenominator","abundantChanceDenominator","minQuantity","maxQuantity","abundantQuantityMultiplier","excludeIntroduction","minimumMapTier","equipmentLevelPerTier","normalWeight","magicWeight","rareWeight","identifiedChancePercent","gemLevelBase","gemLevelTierDivisor","supportChancePercent"};

 static JsonObject read(String json) {
  final JsonElement parsed;
  try (JsonReader reader = new JsonReader(new StringReader(json))) {
   // JsonParser temporarily enables Gson's lenient mode and loses duplicate
   // keys. Read tokens ourselves so author mistakes cannot silently win.
   reader.setLenient(false);
   parsed = value(reader, "drops.json", 0);
   if (reader.peek() != JsonToken.END_DOCUMENT) fail("drops.json", "unexpected trailing content");
  }
  catch(IOException failure){throw new IllegalArgumentException("drops.json: malformed JSON: "+failure.getMessage(),failure);}
  catch(RuntimeException failure){throw new IllegalArgumentException("drops.json: malformed JSON: "+failure.getMessage(),failure);}
  JsonObject root=object(parsed,"drops.json"); keys(root,"drops.json",ROOT);
  integer(required(root,"version","drops.json"),"drops.json.version");
  for(String key:new String[]{"normalTable","eliteTable","bossTable"})string(required(root,key,"drops.json"),"drops.json."+key);
  JsonObject pools=object(required(root,"pools","drops.json"),"drops.json.pools"); comments(pools,"drops.json.pools");
  for(Map.Entry<String,JsonElement> pool:pools.entrySet()){
   String at="drops.json.pools."+pool.getKey(); JsonArray entries=array(pool.getValue(),at);
   for(int i=0;i<entries.size();i++){
    String row=at+"["+i+"]"; JsonObject entry=object(entries.get(i),row); keys(entry,row,POOL);
    for(String key:POOL)integer(required(entry,key,row),row+"."+key);
   }
  }
  JsonArray tables=array(required(root,"tables","drops.json"),"drops.json.tables");
  for(int i=0;i<tables.size();i++){
   String at="drops.json.tables["+i+"]"; JsonObject table=object(tables.get(i),at); keys(table,at,TABLE);
   string(required(table,"id",at),at+".id"); integer(required(table,"experiencePerTier",at),at+".experiencePerTier");
   JsonArray rolls=array(required(table,"rolls",at),at+".rolls");
   for(int j=0;j<rolls.size();j++){
    String row=at+".rolls["+j+"]"; JsonObject roll=object(rolls.get(j),row); keys(roll,row,ROLL);
    string(required(roll,"label",row),row+".label"); string(required(roll,"type",row),row+".type");
    for(Map.Entry<String,JsonElement> field:roll.entrySet()){
     String key=field.getKey(),where=row+"."+key;
     if(Arrays.asList("label","type","pool","baseId").contains(key))string(field.getValue(),where);
     else if(key.equals("excludeIntroduction"))bool(field.getValue(),where);
     else integer(field.getValue(),where);
    }
    String kind=roll.get("type").getAsString();
    if(kind.equals("ITEM"))integer(required(roll,"itemId",row),row+".itemId");
    if(kind.equals("POOL"))string(required(roll,"pool",row),row+".pool");
    if(kind.equals("BASE"))string(required(roll,"baseId",row),row+".baseId");
   }
  }
  if(root.has("npcOverrides")){
   JsonObject overrides=object(root.get("npcOverrides"),"drops.json.npcOverrides"); comments(overrides,"drops.json.npcOverrides");
   for(Map.Entry<String,JsonElement> override:overrides.entrySet()){
    String at="drops.json.npcOverrides."+override.getKey();
    if(!override.getKey().matches("0|[1-9][0-9]{0,4}"))fail(at,"expected a canonical NPC id (for example 15001, without leading zeros)");
    string(override.getValue(),at);
   }
  }
  return root;
 }
 /** Preserve JSON types and reject duplicate fields before a tree can overwrite them. */
 private static JsonElement value(JsonReader reader, String at, int depth) throws IOException {
  if (depth > 32) { fail(at, "nesting exceeds 32 levels"); }
  switch (reader.peek()) {
   case BEGIN_OBJECT:
    reader.beginObject();
    JsonObject object = new JsonObject();
    while (reader.hasNext()) {
     String key = reader.nextName();
     if (object.has(key)) fail(at + "." + key, "duplicate field");
     object.add(key, value(reader, at + "." + key, depth + 1));
    }
    reader.endObject();
    return object;
   case BEGIN_ARRAY:
    reader.beginArray();
    JsonArray array = new JsonArray();
    while (reader.hasNext()) array.add(value(reader, at + "[" + array.size() + "]", depth + 1));
    reader.endArray();
    return array;
   case STRING: return new JsonPrimitive(reader.nextString());
   case NUMBER: return new JsonPrimitive(new BigDecimal(reader.nextString()));
   case BOOLEAN: return new JsonPrimitive(reader.nextBoolean());
   case NULL: reader.nextNull(); return JsonNull.INSTANCE;
   default: fail(at, "expected a JSON value"); return null;
  }
 }
 private static void keys(JsonObject object,String at,String[] allowed){comments(object,at);for(Map.Entry<String,JsonElement> field:object.entrySet()){String key=field.getKey();if(!Arrays.asList(allowed).contains(key))fail(at+"."+key,"unknown field; check spelling (allowed: "+Arrays.toString(allowed)+")");}}
 /** Underscore-prefixed string notes are documentation and never affect rewards. */
 private static void comments(JsonObject object,String at){List<String> remove=new ArrayList<>();for(Map.Entry<String,JsonElement> field:object.entrySet())if(field.getKey().startsWith("_")){string(field.getValue(),at+"."+field.getKey());remove.add(field.getKey());}for(String key:remove)object.remove(key);}
 private static JsonElement required(JsonObject object,String key,String at){if(!object.has(key)||object.get(key).isJsonNull())fail(at+"."+key,"missing required field");return object.get(key);}
 private static JsonObject object(JsonElement value,String at){if(value==null||!value.isJsonObject())fail(at,"expected an object");return value.getAsJsonObject();}
 private static JsonArray array(JsonElement value,String at){if(value==null||!value.isJsonArray())fail(at,"expected an array");return value.getAsJsonArray();}
 private static void string(JsonElement value,String at){if(value==null||!value.isJsonPrimitive()||!value.getAsJsonPrimitive().isString())fail(at,"expected a string");}
 private static void bool(JsonElement value,String at){if(value==null||!value.isJsonPrimitive()||!value.getAsJsonPrimitive().isBoolean())fail(at,"expected true or false, without quotes");}
 private static void integer(JsonElement value,String at){if(value==null||!value.isJsonPrimitive()||!value.getAsJsonPrimitive().isNumber())fail(at,"expected an integer number, without quotes");try{value.getAsBigDecimal().intValueExact();}catch(ArithmeticException|NumberFormatException failure){fail(at,"expected an exact 32-bit integer (no fractional or overflowing values)");}}
 private static void fail(String at,String reason){throw new IllegalArgumentException(at+": "+reason);}
 private DropSchema(){}
}
