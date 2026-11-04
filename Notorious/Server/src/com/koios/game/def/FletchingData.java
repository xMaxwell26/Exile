package com.koios.game.def;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koios.util.XStreamLibrary;

public class FletchingData {

	public static class LogData {

		private static Map<Integer, LogData> data;

		public static LogData getLog(int id) {
			return data.get(id);
		}

		@SuppressWarnings("unchecked")
		public static void load() throws FileNotFoundException {
			data = new HashMap<>();
			List<LogData> loaded = (ArrayList<LogData>) XStreamLibrary.load("./data/skills/LogData.xml");

			for (LogData log : loaded) {
				data.put(log.getLogId(), log);
			}
		}

		private int animId;
		private double[] exp;
		private int[] finalItem;
		private int interfaceId;
		private int[] itemInterface;
		private int[] levelReq;
		private int logId;
		private int[] textInterface;

		public int getAnimId() {
			return animId;
		}

		public double[] getExp() {
			return exp;
		}

		public int getInterface() {
			return interfaceId;
		}

		public int[] getItem() {
			return finalItem;
		}

		public int[] getItemInterface() {
			return itemInterface;
		}

		public int[] getLevelReq() {
			return levelReq;
		}

		public int getLogId() {
			return logId;
		}

		public int[] getTextInterface() {
			return textInterface;
		}
	}

	public static class StringData {

		private static Map<Integer, StringData> data;

		public static StringData getItem(int id) {
			return data.get(id);
		}

		@SuppressWarnings("unchecked")
		public static void load() throws FileNotFoundException {
			data = new HashMap<>();
			List<StringData> stringData = (ArrayList<StringData>) XStreamLibrary.load("./data/skills/StringData.xml");

			for (StringData string : stringData) {
				data.put(string.getUnfItem(), string);
			}
		}

		private int animId;
		private double exp;
		private int finalItem;
		private Object fletchType;
		private int levelReq;
		private int unfItem;

		public int getAnimId() {
			return animId;
		}

		public double getExp() {
			return exp;
		}

		public int getFinalItem() {
			return finalItem;
		}

		public Object getFlechType() {
			return fletchType;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getUnfItem() {
			return unfItem;
		}

	}

	public static void loadXml() {
		try {
			LogData.load();
			StringData.load();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

}
