package com.koios.game.content;

import com.koios.game.content.achievement.Achievement;
import com.koios.game.content.achievement.AchievementManager;
import com.koios.game.model.Animation;
import com.koios.game.model.Graphic;
import com.koios.game.model.item.container.equip.Equipment;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendRemoveInterfacePacket;

/**
 * Holds the data for a skillcape animation
 * 
 * @author Timothy Howard <Arithium>
 */
public class SkillcapeAnimation {

	private static final int[][] SKILLCAPE_ANIMATION = { { 9747, 4959 }, { 9748, 4959 }, { 9750, 4981 }, { 9751, 4981 }, { 9753, 4961 }, { 9754, 4961 }, { 9756, 4973 },
			{ 9757, 4973 }, { 9759, 4979 }, { 9760, 4979 }, { 9762, 4939 }, { 9763, 4939 }, { 9765, 4947 }, { 9766, 4947 }, { 9768, 4971 }, { 9769, 4971 }, { 9771, 4977 },
			{ 9772, 4977 }, { 9774, 4969 }, { 9775, 4969 }, { 9777, 4965 }, { 9778, 4965 }, { 9780, 4949 }, { 9781, 4949 }, { 9783, 4937 }, { 9784, 4937 }, { 9786, 4967 },
			{ 9787, 4967 }, { 9789, 4953 }, { 9790, 4953 }, { 9792, 4941 }, { 9793, 4941 }, { 9795, 4943 }, { 9796, 4943 }, { 9798, 4951 }, { 9799, 4951 }, { 9801, 4955 },
			{ 9802, 4955 }, { 9804, 4975 }, { 9805, 4975 }, { 9807, 4957 }, { 9808, 4957 }, { 9810, 4963 }, { 9811, 4963 }, { 9948, 5158 }, { 9949, 5158 }, { 10662, 4945 } };
	private static final int[][] SKILLCAPE_GRAPHICS = { { 9747, 823 }, { 9748, 823 }, { 9750, 828 }, { 9751, 828 }, { 9753, 824 }, { 9754, 824 }, { 9756, 832 }, { 9757, 832 },
			{ 9759, 829 }, { 9760, 829 }, { 9762, 813 }, { 9763, 813 }, { 9765, 817 }, { 9766, 817 }, { 9768, 833 }, { 9769, 833 }, { 9771, 830 }, { 9772, 830 }, { 9774, 835 },
			{ 9775, 835 }, { 9777, 826 }, { 9778, 826 }, { 9780, 818 }, { 9781, 818 }, { 9783, 812 }, { 9784, 812 }, { 9786, 827 }, { 9787, 827 }, { 9789, 820 }, { 9790, 820 },
			{ 9792, 814 }, { 9793, 814 }, { 9795, 815 }, { 9796, 815 }, { 9798, 819 }, { 9799, 819 }, { 9801, 821 }, { 9802, 821 }, { 9804, 831 }, { 9805, 831 }, { 9807, 822 },
			{ 9808, 822 }, { 9810, 825 }, { 9811, 825 }, { 9948, 907 }, { 9949, 907 }, { 10662, 816 } };

	public static int skillcapeEmote(int id) {
		for (int[] node : SKILLCAPE_ANIMATION) {
			if (node[0] == id) {
				return node[1];
			}
		}

		return -1;
	}

	public static int skillcapeGraphics(int id) {
		for (int[] node : SKILLCAPE_GRAPHICS) {
			if (node[0] == id) {
				return node[1];
			}
		}

		return -1;
	}

	/**
	 * Performs a skillcape animation
	 * 
	 * @param player The {@link Player} performing a skillcape animation
	 */
	public static void perform(Player player) {
		if (player.getEquipment().get(Equipment.SLOT_CAPE) != null && Equipment.isCapeEquipped(player.getEquipment().get(Equipment.SLOT_CAPE).getId())) {
			player.getSession().send(new SendRemoveInterfacePacket());
			player.getMovementHandler().reset();
			player.playGraphics(new Graphic(SkillcapeAnimation.skillcapeGraphics(player.getEquipment().get(Equipment.SLOT_CAPE).getId())));
			player.playAnimation(new Animation(SkillcapeAnimation.skillcapeEmote(player.getEquipment().get(Equipment.SLOT_CAPE).getId())));
			AchievementManager.performed(player, Achievement.SKILLCAPE_EMOTE);
		} else {
			player.sendMessage("You must be wearing a skillcape to do this emote.");
		}
	}

}
