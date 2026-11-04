package com.koios.game.content.skill;

public class SkillHandler {

	public static boolean[] isSkilling = new boolean[25];

	public static long lastSkillingAction;

	public static int getAmount(int button) {
		int[] amount5 = { 34204, 34208, 34212, 34216, 34184, 34188, 34192, 34169, 34173, 33186, 33189, 33192, 33195, 33198, 33201, 33204, 57217, 57218, 57219, 57220, 57221, 57222,
				57223, 57224, 2798, 3986, 3990, 3994, 3998, 4002, 44209, 48107, 48111, 48115, 48119, 24058, 48123, 12403, 12407, 12411, 14809, 10238 };
		int[] amount10 = { 34203, 34207, 34211, 34215, 34183, 34187, 34191, 34168, 34172, 33185, 33188, 33191, 33194, 33197, 33200, 33203, 2807, 3989, 3993, 3997, 4001, 44208,
				48106, 48110, 48114, 48118, 24057, 48122, 12402, 12406, 12410 };
		int[] amount28 = { 1747, 1748, 14793, 6211, 57201, 57202, 57203, 57204, 57205, 57206, 57207, 57208 };

		for (int buttonId : amount5) {
			if (buttonId == button) {
				return 5;
			}
		}

		for (int buttonId : amount10) {
			if (buttonId == button) {
				return 10;
			}
		}

		for (int buttonId : amount28) {
			if (buttonId == button) {
				return 28;
			}
		}

		return 1;
	}

}