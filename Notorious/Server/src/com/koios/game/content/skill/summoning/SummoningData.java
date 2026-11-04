package com.koios.game.content.skill.summoning;

import java.util.HashMap;
import java.util.Map;

import com.koios.game.content.skill.Skill;
import com.koios.game.model.item.Item;
import com.koios.game.model.mob.impl.MobFamiliar;

public class SummoningData {

	public static String SUMMON_FAMILIAR = "Summon Familiar";
	public static String CALL_FAMILIAR = "Call Familiar";
	public static String DISSMISS_FAMILIAR = "Dismiss";
	public static String CAST_SCROLL = "Cast Scroll";
	public static String RENEW_FAMILIAR = "Renew Familiar";
	public static String RECHARGE = "Recharge";
	public static String DURATION_OVER = "Duration Over";
	public static String WITHDRAW_ALL = "Withdraw all";

	public static enum SummonPouch {

		SPIRIT_WOLF(12047, 1, 1, 6829, 12, 12425, 0, new String[] { "What are you doing?", "Danger!", "I smell something good! Hunting time!" },
				8298), SPIRIT_DREADFOWL(12043, 8, 1, 6825, 8, 12445, 0,
						new String[] { "Attack! Fight! Annihilate!", "Can it be fightin' time, please?", "I want to fight something." }, 7807), SPIRIT_SPIDER(12059, 8, 2, 6841, 30,
								12428, 0, new String[] { "When can I lay my eggs?", "Where are we going?", "Why are you not responding to me?" }, 8163), THORNY_SNAIL(12019, 13, 2,
										6806, 32, 12459, 3,
										new String[] { "All this running around the place is fun!", "Okay, I have to ask, what are those things you people totter about on?" },
										8141), GRANITE_CRAB(12009, 16, 2, 6796, 36, 12533, 0, new String[] { "Can I have some fish?", "Rock fish now, please?",
												"When can we go fishing? I want rock fish." },
										8108), SPIRIT_MOSQUITO(12778, 2, 17, 7331, 24, 12838, 0,
												new String[] { "You have lovely ankles.", "Have you ever tasted pirate blood?", "I'm soooo hungry!" },
												8037), DESERT_WYRM(12049, 18, 1, 6831, 38, 12460, 0, new String[] { "This is so unsafe...I should have a hard hat for this work...",
														"You can't touch me, I'm part of the union!", "If you have that pick, why make me dig?" }, 7794), SPIRIT_SCORPION(12055, 19,
																2, 6837, 34, 12432, 0, new String[] { "Hey, boss, how about we go to the bank?", "Say hello to my little friend!" },
																-1), SPIRIT_TZ_KIH(12808, 22, 3, 7361, 36, 12839, 0,
																		new String[] { "Hmph, silly JalYt.", "Have you heard of blood bat, JalYt?" },
																		8260), ALBINO_RAT(12067, 23, 3, 6847, 44, 12430, 0, new String[] {
																				"Hey boss, we going to do anything wicked today?",
																				" You know, boss, I don't think you're totally into this whole 'evil' thing.",
																				"Hey boss, can we go and loot something now?" },
																				7906), SPIRIT_KALPHITE(12063, 25, 3, 6994, 44, 12446, 6, new String[] {
																						"This activity is not optimal for us.", "We are growing infuriated. What is our goal?" },
																						8516), COMPOST_MOUND(112091, 28, 6, 6871, 48, 12440, 0, new String[] {
																								"Oi wus just a-wonderin'...", "*Errr...are ye gonna eat that?",
																								"Oi've gotta braand new comboine 'aarvester!" },
																								7773), GIANT_CHINCHOMPA(12800, 29, 1, 7353, 64, 12834, 0, new String[] {
																										"Half a pound of tuppenny rice, half a pound of treacle...",
																										"I seem to have found a paper bag.", "What's small, brown and blows up?" },
																										7754), VAMPIRE_BAT(12053, 31, 4, 6835, 66, 12447, 0, new String[] {
																												"You're vasting all that blood, can I have some?",
																												"Ven are you going to feed me?" }, 8279), HONEY_BADGER(12065, 32, 4,
																														6845, 50, 12433, 0,
																														new String[] { "*An outpouring of sanity-straining abuse*",
																																"*A lambasting of visibly illustrated obscenities.*" },
																														7929), BEAVER(12021, 33, 4, 6808, 54, 12429, 0,
																																new String[] {
																																		"Vot are you doing 'ere when we could be logging and building mighty dams, alors?",
																																		"Pardonnez-moi - you call yourself a lumberjack?" },
																																7721), VOID_RAVAGER(12818, 34, 4, 7370, 54, 12443,
																																		0,
																																		new String[] {
																																				"Let's go play hide an' seek!",
																																				"I'm coming to tickle you!" },
																																		8091), VOID_SHIFTER(12814, 34, 4, 7367, 188,
																																				12443, 0,
																																				new String[] {
																																						"Let's go play hide an' seek!",
																																						"I'm coming to tickle you!" },
																																				8075), VOID_SPINNER(12780, 34, 4,
																																						7333, 54, 12443, 0,
																																						new String[] {
																																								"Let's go play hide an' seek!",
																																								"I'm coming to tickle you!" },
																																						8174), VOID_TORCHER(12798,
																																								34, 4, 7351, 188,
																																								12443, 0,
																																								new String[] {
																																										"Let's go play hide an' seek!",
																																										"I'm coming to tickle you!" },
																																								8238), BRONZE_MINOTAUR(
																																										12073, 36,
																																										9, 6853, 60,
																																										12461, 0,
																																										new String[] {
																																												"All this walking about is making me angry.",
																																												"Can you tell me why we're not fighting yet?",
																																												"Hey no-horns!" },
																																										8029), BULL_ANT(
																																												12087,
																																												40,
																																												5,
																																												6867,
																																												60,
																																												12431,
																																												9,
																																												new String[] {
																																														"All right you worthless biped, fall in!",
																																														"What's the matter, Private? Not enjoying the run?" },
																																												7894), MACAW(
																																														12071,
																																														41,
																																														5,
																																														6851,
																																														62,
																																														12422,
																																														0,
																																														new String[] {
																																																"Awk! Gimme the rum! Gimme the rum!",
																																																"Awk! I'm a pirate! Awk! Yo, ho ho!" },
																																														8005), EVIL_TURNIP(
																																																12051,
																																																42,
																																																5,
																																																6833,
																																																60,
																																																12448,
																																																0,
																																																new String[] {
																																																		"My roots feel hurty. I thinking it be someone I eated.",
																																																		"Hur hur hur...",
																																																		"When we gonna fighting things, boss?" },
																																																8252), SPIRIT_COCKATRICE(
																																																		12095,
																																																		43,
																																																		5,
																																																		6875,
																																																		72,
																																																		12458,
																																																		0,
																																																		new String[] {
																																																				"Where's my brothers?",
																																																				"Where's my sisters." },
																																																		7765), SPIRIT_GUTHATRICE(
																																																				12097,
																																																				43,
																																																				5,
																																																				6877,
																																																				72,
																																																				12458,
																																																				0,
																																																				new String[] {
																																																						"Where's my brothers?",
																																																						"Where's my sisters." },
																																																				7765), SPIRIT_SARATRICE(
																																																						12099,
																																																						43,
																																																						5,
																																																						6879,
																																																						72,
																																																						12458,
																																																						0,
																																																						new String[] {
																																																								"Where's my brothers?",
																																																								"Where's my sisters." },
																																																						7765), SPIRIT_ZAMATRICE(
																																																								12101,
																																																								43,
																																																								5,
																																																								6881,
																																																								72,
																																																								12458,
																																																								0,
																																																								new String[] {
																																																										"Where's my brothers?",
																																																										"Where's my sisters." },
																																																								7765), SPIRIT_PENGATRICE(
																																																										12103,
																																																										43,
																																																										5,
																																																										6883,
																																																										72,
																																																										12458,
																																																										0,
																																																										new String[] {
																																																												"Where's my brothers?",
																																																												"Where's my sisters." },
																																																										7765), SPIRIT_CORAXATRICE(
																																																												12105,
																																																												43,
																																																												5,
																																																												6885,
																																																												72,
																																																												12458,
																																																												0,
																																																												new String[] {
																																																														"Where's my brothers?",
																																																														"Where's my sisters." },
																																																												7765), SPIRIT_VULATRICE(
																																																														12107,
																																																														43,
																																																														5,
																																																														6887,
																																																														72,
																																																														12458,
																																																														0,
																																																														new String[] {
																																																																"Where's my brothers?",
																																																																"Where's my sisters." },
																																																														7765), IRON_MINOTAUR(
																																																																12075,
																																																																46,
																																																																9,
																																																																6855,
																																																																74,
																																																																12462,
																																																																0,
																																																																new String[] {
																																																																		"All this walking about is making me angry.",
																																																																		"Can you tell me why we're not fighting yet?",
																																																																		"Hey no-horns!" },
																																																																8029), PYRELORD(
																																																																		12816,
																																																																		46,
																																																																		5,
																																																																		7377,
																																																																		64,
																																																																		12829,
																																																																		0,
																																																																		new String[] {
																																																																				"I used to be feared across five planes.",
																																																																				"I can teach you to smite your enemies with flames." },
																																																																		8081), MAGPIE(
																																																																				12041,
																																																																				47,
																																																																				5,
																																																																				6824,
																																																																				68,
																																																																				12426,
																																																																				0,
																																																																				new String[] {
																																																																						"Howway, let's gaan see what's happenin' in toon.",
																																																																						"Are we gaan oot soon? I'm up fer a good walk me." },
																																																																				8014), BLOATED_LEECH(
																																																																						12061,
																																																																						49,
																																																																						5,
																																																																						6843,
																																																																						68,
																																																																						12444,
																																																																						0,
																																																																						new String[] {
																																																																								"I'm afraid it's going to have to come off.",
																																																																								"Let's get a look at that brain of yours." },
																																																																						7711), SPIRIT_TERRORBIRD(
																																																																								12007,
																																																																								52,
																																																																								6,
																																																																								6794,
																																																																								72,
																																																																								12441,
																																																																								12,
																																																																								new String[] {
																																																																										"This is a fun little walk.",
																																																																										"Can we go to a bank now?",
																																																																										"I can keep this up for hours." },
																																																																								8231), ABYSSAL_PARASITE(
																																																																										12035,
																																																																										54,
																																																																										6,
																																																																										6818,
																																																																										60,
																																																																										12454,
																																																																										7,
																																																																										new String[] {
																																																																												"Ongk n'hd?",
																																																																												"Noslr'rh..." },
																																																																										7669), SPIRIT_JELLY(
																																																																												12027,
																																																																												55,
																																																																												6,
																																																																												6992,
																																																																												86,
																																																																												12453,
																																																																												0,
																																																																												new String[] {
																																																																														"Play play play play!",
																																																																														"Can we go over there now, pleasepleasepleasepleeeeeease?" },
																																																																												8058), STEEL_MINOTAUR(
																																																																														12077,
																																																																														56,
																																																																														9,
																																																																														6857,
																																																																														92,
																																																																														12463,
																																																																														0,
																																																																														new String[] {
																																																																																"All this walking about is making me angry.",
																																																																																"Can you tell me why we're not fighting yet?",
																																																																																"Hey no-horns!" },
																																																																														8029), IBIS(
																																																																																12531,
																																																																																56,
																																																																																6,
																																																																																6991,
																																																																																76,
																																																																																12424,
																																																																																0,
																																																																																new String[] {
																																																																																		"I'm the best fisherman ever!",
																																																																																		"Can I look after those sharks for you?" },
																																																																																8202), SPIRIT_GRAAHK(
																																																																																		12810,
																																																																																		57,
																																																																																		6,
																																																																																		7363,
																																																																																		98,
																																																																																		12835,
																																																																																		0,
																																																																																		new String[] {
																																																																																				"Hi!",
																																																																																				"Your spikes are looking particularly spiky today" },
																																																																																		7909), SPIRIT_KYATT(
																																																																																				12812,
																																																																																				57,
																																																																																				6,
																																																																																				7365,
																																																																																				98,
																																																																																				12836,
																																																																																				0,
																																																																																				new String[] {
																																																																																						"Hi!",
																																																																																						"Your spikes are looking particularly spiky today" },
																																																																																				7909), SPIRIT_LARUPIA(
																																																																																						12784,
																																																																																						57,
																																																																																						6,
																																																																																						7337,
																																																																																						98,
																																																																																						12840,
																																																																																						0,
																																																																																						new String[] {
																																																																																								"Hi!",
																																																																																								"Your spikes are looking particularly spiky today" },
																																																																																						7909), KARAMTHULHU_OVERLORD(
																																																																																								12023,
																																																																																								58,
																																																																																								6,
																																																																																								6809,
																																																																																								88,
																																																																																								12455,
																																																																																								0,
																																																																																								new String[] {
																																																																																										"SIIIIIILLLLLEEEEENCE!",
																																																																																										"Kneel before my awesome might!" },
																																																																																								7969), SMOKE_DEVIL(
																																																																																										12085,
																																																																																										61,
																																																																																										7,
																																																																																										6865,
																																																																																										96,
																																																																																										12468,
																																																																																										0,
																																																																																										new String[] {
																																																																																												"When are you going to be done with that?",
																																																																																												"Ah, this is the life!" },
																																																																																										7819), ABYSSAL_LURKER(
																																																																																												12037,
																																																																																												62,
																																																																																												7,
																																																																																												6820,
																																																																																												82,
																																																																																												12427,
																																																																																												7,
																																																																																												new String[] {
																																																																																														"Ongk n'hd?",
																																																																																														"Noslr'rh..." },
																																																																																												7683), SPIRIT_COBRA(
																																																																																														12015,
																																																																																														63,
																																																																																														7,
																																																																																														6802,
																																																																																														112,
																																																																																														12436,
																																																																																														0,
																																																																																														new String[] {
																																																																																																"When are you going to be done with that?",
																																																																																																"Ah, this is the life!" },
																																																																																														8157), STRANGER_PLANT(
																																																																																																12045,
																																																																																																64,
																																																																																																7,
																																																																																																6827,
																																																																																																98,
																																																																																																12467,
																																																																																																0,
																																																																																																new String[] {
																																																																																																		"I'M STRANGER PLANT!",
																																																																																																		"WILL WE HAVE TO BE HERE LONG?",
																																																																																																		"I THINK I'M WILTING!" },
																																																																																																8216), MITHRIL_MINOTAUR(
																																																																																																		12079,
																																																																																																		66,
																																																																																																		9,
																																																																																																		6859,
																																																																																																		16,
																																																																																																		12464,
																																																																																																		0,
																																																																																																		new String[] {
																																																																																																				"All this walking about is making me angry.",
																																																																																																				"Can you tell me why we're not fighting yet?",
																																																																																																				"Hey no-horns!" },
																																																																																																		8029), BARKER_TOAD(
																																																																																																				12123,
																																																																																																				66,
																																																																																																				7,
																																																																																																				6889,
																																																																																																				110,
																																																																																																				12452,
																																																																																																				0,
																																																																																																				new String[] {
																																																																																																						"Ladies and gentlemen, for my next trick, I shall swallow this fly!",
																																																																																																						"Roll up, roll up, roll up! See the greatest show on Exile!" },
																																																																																																				7702), WAR_TORTOISE(
																																																																																																						12031,
																																																																																																						67,
																																																																																																						7,
																																																																																																						6815,
																																																																																																						86,
																																																																																																						12439,
																																																																																																						18,
																																																																																																						new String[] {
																																																																																																								"Hssss! Hold up a minute, there.",
																																																																																																								"Hssss? What are we doing in this dump?" },
																																																																																																						8282), BUNYIP(
																																																																																																								12029,
																																																																																																								68,
																																																																																																								7,
																																																																																																								6813,
																																																																																																								88,
																																																																																																								12438,
																																																																																																								0,
																																																																																																								new String[] {
																																																																																																										"Where are we going and why is it not to the beach?",
																																																																																																										"Pass me another bunch of shrimps, mate!" },
																																																																																																								7736), FRUIT_BAT(
																																																																																																										12033,
																																																																																																										69,
																																																																																																										7,
																																																																																																										6817,
																																																																																																										90,
																																																																																																										12423,
																																																																																																										0,
																																																																																																										new String[] {
																																																																																																												"Squeeksqueekasqueek?",
																																																																																																												"Squeek?" },
																																																																																																										8321), RAVENOUS_LOCUST(
																																																																																																												12820,
																																																																																																												70,
																																																																																																												4,
																																																																																																												7372,
																																																																																																												44,
																																																																																																												12830,
																																																																																																												0,
																																																																																																												new String[] {
																																																																																																														"Bake pies, not war!",
																																																																																																														"*Chirp*" },
																																																																																																												7997), ARCTIC_BEAR(
																																																																																																														12057,
																																																																																																														71,
																																																																																																														8,
																																																																																																														6839,
																																																																																																														56,
																																																																																																														12451,
																																																																																																														0,
																																																																																																														new String[] {
																																																																																																																"Seems a bit cold master!",
																																																																																																																"I eat my children." },
																																																																																																														8522), PHEONIX(
																																																																																																																14623,
																																																																																																																72,
																																																																																																																8,
																																																																																																																8575,
																																																																																																																60,
																																																																																																																14622,
																																																																																																																0,
																																																																																																																new String[] {
																																																																																																																		"HOT!",
																																																																																																																		"FIRE! Oh wait, that's me. HAHA!" },
																																																																																																																-1), OBSIDIAN_GOLEM(
																																																																																																																		12792,
																																																																																																																		73,
																																																																																																																		8,
																																																																																																																		7345,
																																																																																																																		110,
																																																																																																																		12826,
																																																																																																																		0,
																																																																																																																		new String[] {
																																																																																																																				"Let us go forth and prove our strength, Master!",
																																																																																																																				"How many foes have you defeated, Master?" },
																																																																																																																		8049), GRANITE_LOBSTER(
																																																																																																																				12069,
																																																																																																																				74,
																																																																																																																				8,
																																																																																																																				6849,
																																																																																																																				94,
																																																																																																																				12449,
																																																																																																																				0,
																																																																																																																				new String[] {
																																																																																																																						"We shall heap the helmets of the fallen into a mountain!",
																																																																																																																						"Glory and plunder for all!" },
																																																																																																																				8122), PRAYING_MANTIS(
																																																																																																																						12011,
																																																																																																																						75,
																																																																																																																						8,
																																																																																																																						6798,
																																																																																																																						138,
																																																																																																																						12450,
																																																																																																																						0,
																																																																																																																						new String[] {
																																																																																																																								"*Cricket* Oh Wait!",
																																																																																																																								"I am your new master!" },
																																																																																																																						7894), ADAMANT_MINOTAUR(
																																																																																																																								12081,
																																																																																																																								76,
																																																																																																																								9,
																																																																																																																								6861,
																																																																																																																								132,
																																																																																																																								12465,
																																																																																																																								0,
																																																																																																																								new String[] {
																																																																																																																										"All this walking about is making me angry.",
																																																																																																																										"Can you tell me why we're not fighting yet?",
																																																																																																																										"Hey no-horns!" },
																																																																																																																								8029), FORGE_REGENT(
																																																																																																																										12782,
																																																																																																																										76,
																																																																																																																										9,
																																																																																																																										7335,
																																																																																																																										90,
																																																																																																																										12841,
																																																																																																																										0,
																																																																																																																										new String[] {
																																																																																																																												"I'm burning up." },
																																																																																																																										7870), TALON_BEAST(
																																																																																																																												12794,
																																																																																																																												77,
																																																																																																																												9,
																																																																																																																												7347,
																																																																																																																												98,
																																																																																																																												12831,
																																																																																																																												0,
																																																																																																																												new String[] {
																																																																																																																														"Is this all you apes do all day, then?",
																																																																																																																														"This place smells odd�" },
																																																																																																																												8045), GIANT_ENT(
																																																																																																																														12013,
																																																																																																																														78,
																																																																																																																														8,
																																																																																																																														6800,
																																																																																																																														98,
																																																																																																																														12457,
																																																																																																																														0,
																																																																																																																														new String[] {
																																																																																																																																"Groooooooooan.....",
																																																																																																																																"Groooooooooooooooooan....." },
																																																																																																																														7850), FIRE_TITAN(
																																																																																																																																12802,
																																																																																																																																79,
																																																																																																																																9,
																																																																																																																																7355,
																																																																																																																																124,
																																																																																																																																12824,
																																																																																																																																0,
																																																																																																																																new String[] {
																																																																																																																																		"Pick flax.",
																																																																																																																																		"WHERE ARE MY BROTHERS!" },
																																																																																																																																7829), MOSS_TITAN(
																																																																																																																																		12804,
																																																																																																																																		79,
																																																																																																																																		9,
																																																																																																																																		7357,
																																																																																																																																		128,
																																																																																																																																		12824,
																																																																																																																																		0,
																																																																																																																																		new String[] {
																																																																																																																																				"Pick flax.",
																																																																																																																																				"WHERE ARE MY BROTHERS!" },
																																																																																																																																		7951), ICE_TITAN(
																																																																																																																																				12806,
																																																																																																																																				79,
																																																																																																																																				9,
																																																																																																																																				7359,
																																																																																																																																				116,
																																																																																																																																				12824,
																																																																																																																																				0,
																																																																																																																																				new String[] {
																																																																																																																																						"Pick flax.",
																																																																																																																																						"WHERE ARE MY BROTHERS!" },
																																																																																																																																				8188), HYDRA(
																																																																																																																																						12025,
																																																																																																																																						80,
																																																																																																																																						8,
																																																																																																																																						6811,
																																																																																																																																						98,
																																																																																																																																						12442,
																																																																																																																																						0,
																																																																																																																																						new String[] {
																																																																																																																																								"Raaaspraaasp?",
																																																																																																																																								"Raaasp raaaaap raaaasp?" },
																																																																																																																																						7940), SPIRIT_DAGANNOTH(
																																																																																																																																								12017,
																																																																																																																																								83,
																																																																																																																																								9,
																																																																																																																																								6804,
																																																																																																																																								114,
																																																																																																																																								12456,
																																																																																																																																								0,
																																																																																																																																								new String[] {
																																																																																																																																										"Look at my spikes!",
																																																																																																																																										"Why aren't you looking at my spikes!" },
																																																																																																																																								7783), LAVA_TITAN(
																																																																																																																																										12788,
																																																																																																																																										83,
																																																																																																																																										9,
																																																																																																																																										7341,
																																																																																																																																										122,
																																																																																																																																										12837,
																																																																																																																																										0,
																																																																																																																																										new String[] {
																																																																																																																																												"I am the titan of Lava!",
																																																																																																																																												"Rocks are what I form!" },
																																																																																																																																										7987), SWAMP_TITAN(
																																																																																																																																												12776,
																																																																																																																																												85,
																																																																																																																																												9,
																																																																																																																																												7329,
																																																																																																																																												12,
																																																																																																																																												12832,
																																																																																																																																												0,
																																																																																																																																												new String[] {
																																																																																																																																														"I�m alone, all alone I say.",
																																																																																																																																														"Oh, Guthix! I�m so alone, I have no friends!" },
																																																																																																																																												8225), RUNE_MINOTAUR(
																																																																																																																																														12083,
																																																																																																																																														86,
																																																																																																																																														9,
																																																																																																																																														6863,
																																																																																																																																														302,
																																																																																																																																														12466,
																																																																																																																																														0,
																																																																																																																																														new String[] {
																																																																																																																																																"All this walking about is making me angry.",
																																																																																																																																																"Can you tell me why we're not fighting yet?",
																																																																																																																																																"Hey no-horns!" },
																																																																																																																																														8029), UNICORN_STALLION(
																																																																																																																																																12039,
																																																																																																																																																88,
																																																																																																																																																9,
																																																																																																																																																6822,
																																																																																																																																																108,
																																																																																																																																																12434,
																																																																																																																																																0,
																																																																																																																																																new String[] {
																																																																																																																																																		"You're hurt! Let me try to heal you.",
																																																																																																																																																		"I think I'm astrally projecting." },
																																																																																																																																																8266), GEYSER_TITAN(
																																																																																																																																																		12786,
																																																																																																																																																		89,
																																																																																																																																																		10,
																																																																																																																																																		7339,
																																																																																																																																																		138,
																																																																																																																																																		12833,
																																																																																																																																																		0,
																																																																																																																																																		new String[] {
																																																																																																																																																				"Did you know a snail can sleep up to three years?",
																																																																																																																																																				"Hey mate, how are you?" },
																																																																																																																																																		7881), WOLPERTINGER(
																																																																																																																																																				12089,
																																																																																																																																																				92,
																																																																																																																																																				10,
																																																																																																																																																				6869,
																																																																																																																																																				124,
																																																																																																																																																				12437,
																																																																																																																																																				0,
																																																																																																																																																				new String[] {
																																																																																																																																																						"*sniffle*",
																																																																																																																																																						"*squeak*" },
																																																																																																																																																				8309), ABYSSAL_TITAN(
																																																																																																																																																						12796,
																																																																																																																																																						93,
																																																																																																																																																						10,
																																																																																																																																																						7349,
																																																																																																																																																						64,
																																																																																																																																																						12827,
																																																																																																																																																						7,
																																																																																																																																																						new String[] {
																																																																																																																																																								"Ongk n'hd?",
																																																																																																																																																								"Noslr'rh..." },
																																																																																																																																																						7688), IRON_TITAN(
																																																																																																																																																								12822,
																																																																																																																																																								95,
																																																																																																																																																								10,
																																																																																																																																																								7375,
																																																																																																																																																								120,
																																																																																																																																																								12828,
																																																																																																																																																								0,
																																																																																																																																																								new String[] {
																																																																																																																																																										"My brother was always one better than me.",
																																																																																																																																																										"I cannot wait until I am honored as a steel titan!" },
																																																																																																																																																								7839), PACK_YAK(
																																																																																																																																																										12093,
																																																																																																																																																										96,
																																																																																																																																																										10,
																																																																																																																																																										6873,
																																																																																																																																																										115,
																																																																																																																																																										12435,
																																																																																																																																																										30,
																																																																																																																																																										new String[] {
																																																																																																																																																												"What do you need me to carry master?",
																																																																																																																																																												"Ugh. My pack is too heavy!" },
																																																																																																																																																										8058), STEEL_TITAN(
																																																																																																																																																												12790,
																																																																																																																																																												99,
																																																																																																																																																												10,
																																																																																																																																																												7343,
																																																																																																																																																												128,
																																																																																																																																																												12825,
																																																																																																																																																												0,
																																																																																																																																																												new String[] {
																																																																																																																																																														"Forward, master, to a battle that will waken the gods!",
																																																																																																																																																														"How do you wish to meet your end, master?" },
																																																																																																																																																												8188);

		private final int pouchId, levelReq, pointsReq, familiarId, duration, storageSlots, scrollId, spawn_anim;
		private final String[] interaction;

		static Map<Integer, SummonPouch> pouchData = new HashMap<Integer, SummonPouch>();

		static {
			for (SummonPouch s : values()) {
				pouchData.put(s.getPouchId(), s);
			}
		}

		private SummonPouch(int pouchId, int levelReq, int pointsReq, int familiarId, int duration, int scrollId, int storageSlots, String[] interaction, int spawn_anim) {
			this.pouchId = pouchId;
			this.levelReq = levelReq;
			this.pointsReq = pointsReq;
			this.familiarId = familiarId;
			this.duration = duration;
			this.scrollId = scrollId;
			this.storageSlots = storageSlots;
			this.interaction = interaction;
			this.spawn_anim = spawn_anim;
		}

		public static SummonPouch get(int pouchId) {
			return pouchData.get(pouchId);
		}

		public static SummonPouch getByFamiliar(int id) {
			for (SummonPouch s : SummonPouch.values()) {
				if (s != null && s.getFamiliarId() == id) {
					return s;
				}
			}
			return null;
		}

		public int getPouchId() {
			return pouchId;
		}

		public int getLevelRequired() {
			return levelReq;
		}

		public int getPointsReq() {
			return pointsReq;
		}

		public int getFamiliarId() {
			return familiarId;
		}

		public int getDuration() {
			return duration;
		}

		public int getScrollId() {
			return scrollId;
		}

		public int getStorageSlots() {
			return storageSlots;
		}

		public String[] getInteraction() {
			return interaction;
		}

		public int getSpawnAnim() {
			return spawn_anim;
		}

	}

	/**
	 * Contains pouch data through enumerations, Accessors are used to grab the data from external files.
	 *
	 * @author Pokemon Version 2.0
	 */
	public enum PouchData {

		SPIRIT_WOLF(205231, 12047, 12158, 7, 2859, 1, 5, 6829, 12), SPIRIT_DREADFOWL(205236, 12043, 12158, 8, 2138, 8, 9, 6824, 12), SPIRIT_SPIDER(205241, 12059, 12158, 8, 6291, 8,
				10, 6825, 12), THORNY_SNAIL(205246, 12019, 12158, 9, 3363, 13, 12, 6806, 12), GRANITE_CRAB(205251, 12009, 12158, 7, 440, 16, 22, 6796, 12), SPIRIT_MOSQUITO(206000,
						12778, 12158, 1, 6319, 17, 47, 7331,
						12), DESERT_WYRM(206005, 12049, 12159, 45, 1783, 18, 31, 6831, 12), SPIRIT_SCORPION(206010, 12055, 12160, 57, 3095, 19, 83, 6837, 12), SPIRIT_TZ_KIH(206015,
								12808, 12160, 64, 12168, 22, 97, 7361, 12), ALBINO_RAT(206020, 12067, 12163, 75, 2134, 23, 202, 6847, 12), SPIRIT_KALPHITE(206025, 12063, 12163, 51,
										3138, 25, 220, 6994, 12), COMPOST_MOUND(206030, 12091, 12159, 47, 6032, 28, 50, 6871, 12), GIANT_CHINCHOMPA(206035, 12800, 12163, 84, 9976,
												29, 255, 7353, 12), VAMPIRE_BAT(206040, 12053, 12160, 81, 3325, 31, 136, 6835, 12), HONEY_BADGER(206045, 12065, 12160, 84, 12156,
														32, 141, 6845, 12), BEAVER(206050, 12021, 12159, 72, 1519, 33, 58, 6808, 12), VOID_RAVAGER(206055, 12818, 12159, 74, 12164,
																34, 60, 7370, 12), VOID_SHIFTER(206060, 12814, 12163, 74, 12165, 34, 60, 7367, 12), VOID_SPINNER(206065, 12780,
																		12163, 74, 12166, 34, 60, 7333,
																		12), VOID_TORCHER(206070, 12798, 12163, 74, 12167, 34, 60, 7351, 12), BRONZE_MINOTAUR(206075, 12073, 12163,
																				102, 2349, 36, 317, 6853, 12), BULL_ANT(206080, 12087, 12158, 11, 6010, 40, 53, 6867, 12), MACAW(
																						206085, 12071, 12159, 78, 13572, 41, 72, 6850,
																						12), EVIL_TURNIP(206090, 12051, 12160, 104, 12153, 42, 185, 6833, 12), SPIRIT_COCKATRICE(
																								206095, 12095, 12159, 88, 12109, 43, (int) 75.2, 6875,
																								12), SPIRIT_GUTHATRICE(206100, 12097, 12159, 88, 12111, 43, (int) 75.2, 6877,
																										12), SPIRIT_SARATRICE(206105, 12099, 12159, 88, 12113, 43, (int) 75.2, 6879,
																												12), SPIRIT_ZAMATRICE(206110, 12101, 12159, 88, 12115, 43,
																														(int) 75.2, 6881, 12), SPIRIT_PENGATRICE(206115, 12103,
																																12159, 88, 12117, 43, (int) 75.2, 6883,
																																12), SPIRIT_CORAXATRICE(206120, 12105, 12159, 88,
																																		12119, 43, (int) 75.2, 6885,
																																		12), SPIRIT_VULATRICE(206125, 12107, 12159,
																																				88, 12121, 43, (int) 75.2, 6887,
																																				12), IRON_MINOTAUR(206130, 12075,
																																						12163, 125, 2351, 46, 405,
																																						6855, 12), PYRELORD(206135,
																																								12816, 12160, 111,
																																								590, 46, 202, 7377,
																																								12), MAGPIE(206140,
																																										12041,
																																										12159, 88,
																																										1635, 47,
																																										83, 6824,
																																										12), BLOATED_LEECH(
																																												206145,
																																												12061,
																																												12160,
																																												117,
																																												2132,
																																												49,
																																												215,
																																												6843,
																																												12), SPIRIT_TERRORBIRD(
																																														206150,
																																														12007,
																																														12158,
																																														12,
																																														9978,
																																														52,
																																														68,
																																														6794,
																																														12), ABYSSAL_PARASITE(
																																																206155,
																																																12035,
																																																12159,
																																																106,
																																																12161,
																																																54,
																																																95,
																																																6818,
																																																12), SPIRIT_JELLY(
																																																		206160,
																																																		12027,
																																																		12163,
																																																		151,
																																																		1937,
																																																		55,
																																																		484,
																																																		6992,
																																																		12), STEEL_MINOTAUR(
																																																				206165,
																																																				12077,
																																																				12163,
																																																				141,
																																																				2353,
																																																				56,
																																																				493,
																																																				6841,
																																																				12), IBIS(
																																																						206170,
																																																						12531,
																																																						12159,
																																																						109,
																																																						311,
																																																						56,
																																																						99,
																																																						6841,
																																																						12), SPIRIT_GRAAHK(
																																																								206175,
																																																								12810,
																																																								12163,
																																																								154,
																																																								10099,
																																																								57,
																																																								502,
																																																								6841,
																																																								12), SPIRIT_KYATT(
																																																										206180,
																																																										12812,
																																																										12163,
																																																										153,
																																																										10103,
																																																										57,
																																																										502,
																																																										6841,
																																																										12), SPIRIT_LARUPIA(
																																																												206185,
																																																												12784,
																																																												12163,
																																																												155,
																																																												10095,
																																																												57,
																																																												502,
																																																												6841,
																																																												12), KARAMTHULHU_OVERLORD(
																																																														206190,
																																																														12023,
																																																														12163,
																																																														144,
																																																														6667,
																																																														58,
																																																														510,
																																																														6841,
																																																														12), SMOKE_DEVIL(
																																																																206195,
																																																																12085,
																																																																12160,
																																																																141,
																																																																9736,
																																																																61,
																																																																268,
																																																																6841,
																																																																12), ABYSSAL_LURKER(
																																																																		206200,
																																																																		12037,
																																																																		12159,
																																																																		119,
																																																																		12161,
																																																																		62,
																																																																		110,
																																																																		6841,
																																																																		12), SPIRIT_COBRA(
																																																																				206205,
																																																																				12015,
																																																																				12160,
																																																																				116,
																																																																				6287,
																																																																				63,
																																																																				269,
																																																																				6841,
																																																																				12), STRANGER_PLANT(
																																																																						206210,
																																																																						12045,
																																																																						12160,
																																																																						128,
																																																																						8431,
																																																																						64,
																																																																						282,
																																																																						6841,
																																																																						12), MITHRIL_MINOTAUR(
																																																																								206215,
																																																																								12079,
																																																																								12163,
																																																																								152,
																																																																								2359,
																																																																								66,
																																																																								581,
																																																																								6841,
																																																																								12), BARKER_TOAD(
																																																																										206220,
																																																																										12123,
																																																																										12158,
																																																																										11,
																																																																										2150,
																																																																										66,
																																																																										87,
																																																																										6841,
																																																																										12), WAR_TORTOISE(
																																																																												206225,
																																																																												12031,
																																																																												12158,
																																																																												1,
																																																																												7939,
																																																																												67,
																																																																												59,
																																																																												6841,
																																																																												12), BUNYIP(
																																																																														206230,
																																																																														12029,
																																																																														12159,
																																																																														110,
																																																																														383,
																																																																														68,
																																																																														120,
																																																																														6841,
																																																																														12), FRUIT_BAT(
																																																																																206235,
																																																																																12033,
																																																																																12159,
																																																																																130,
																																																																																1963,
																																																																																69,
																																																																																121,
																																																																																6841,
																																																																																12), RAVENOUS_LOCUST(
																																																																																		206240,
																																																																																		12820,
																																																																																		12160,
																																																																																		79,
																																																																																		1933,
																																																																																		70,
																																																																																		132,
																																																																																		6841,
																																																																																		12), ARCTIC_BEAR(
																																																																																				206245,
																																																																																				12057,
																																																																																				12158,
																																																																																				14,
																																																																																				10117,
																																																																																				71,
																																																																																				93,
																																																																																				6841,
																																																																																				12), PHEONIX(
																																																																																						206250,
																																																																																						14623,
																																																																																						12160,
																																																																																						165,
																																																																																						14616,
																																																																																						72,
																																																																																						301,
																																																																																						6841,
																																																																																						12), OBSIDIAN_GOLEM(
																																																																																								206255,
																																																																																								12792,
																																																																																								12163,
																																																																																								195,
																																																																																								12168,
																																																																																								73,
																																																																																								642,
																																																																																								6841,
																																																																																								12), GRANITE_LOBSTER(
																																																																																										207004,
																																																																																										12069,
																																																																																										12160,
																																																																																										166,
																																																																																										6979,
																																																																																										74,
																																																																																										326,
																																																																																										6841,
																																																																																										12), PRAYING_MANTIS(
																																																																																												207009,
																																																																																												12011,
																																																																																												12160,
																																																																																												168,
																																																																																												2460,
																																																																																												75,
																																																																																												330,
																																																																																												6841,
																																																																																												12), ADAMANT_MINOTAUR(
																																																																																														207014,
																																																																																														12081,
																																																																																														12163,
																																																																																														144,
																																																																																														2361,
																																																																																														76,
																																																																																														669,
																																																																																														6841,
																																																																																														12), FORGE_REGENT(
																																																																																																207019,
																																																																																																12782,
																																																																																																12159,
																																																																																																141,
																																																																																																10020,
																																																																																																76,
																																																																																																134,
																																																																																																6841,
																																																																																																12), TALON_BEAST(
																																																																																																		207024,
																																																																																																		12794,
																																																																																																		12160,
																																																																																																		174,
																																																																																																		12162,
																																																																																																		77,
																																																																																																		1015,
																																																																																																		6841,
																																																																																																		12), GIANT_ENT(
																																																																																																				207029,
																																																																																																				12013,
																																																																																																				12159,
																																																																																																				124,
																																																																																																				5933,
																																																																																																				78,
																																																																																																				137,
																																																																																																				6841,
																																																																																																				12), FIRE_TITAN(
																																																																																																						207034,
																																																																																																						12802,
																																																																																																						12163,
																																																																																																						198,
																																																																																																						1442,
																																																																																																						79,
																																																																																																						695,
																																																																																																						6841,
																																																																																																						12), MOSS_TITAN(
																																																																																																								207039,
																																																																																																								12804,
																																																																																																								12163,
																																																																																																								202,
																																																																																																								1440,
																																																																																																								79,
																																																																																																								695,
																																																																																																								6841,
																																																																																																								12), ICE_TITAN(
																																																																																																										207044,
																																																																																																										12806,
																																																																																																										12163,
																																																																																																										198,
																																																																																																										1444,
																																																																																																										79,
																																																																																																										695,
																																																																																																										6841,
																																																																																																										12), HYDRA(
																																																																																																												207049,
																																																																																																												12025,
																																																																																																												12159,
																																																																																																												128,
																																																																																																												571,
																																																																																																												80,
																																																																																																												141,
																																																																																																												6841,
																																																																																																												12), SPIRIT_DAGGANOTH(
																																																																																																														207054,
																																																																																																														12017,
																																																																																																														12160,
																																																																																																														1,
																																																																																																														6155,
																																																																																																														83,
																																																																																																														365,
																																																																																																														6841,
																																																																																																														12), LAVA_TITAN(
																																																																																																																207059,
																																																																																																																12788,
																																																																																																																12163,
																																																																																																																219,
																																																																																																																12168,
																																																																																																																83,
																																																																																																																730,
																																																																																																																6841,
																																																																																																																12), SWAMP_TITAN(
																																																																																																																		207064,
																																																																																																																		12776,
																																																																																																																		12160,
																																																																																																																		150,
																																																																																																																		10149,
																																																																																																																		85,
																																																																																																																		374,
																																																																																																																		6841,
																																																																																																																		12), RUNE_MINOTAUR(
																																																																																																																				207069,
																																																																																																																				12083,
																																																																																																																				12163,
																																																																																																																				1,
																																																																																																																				2363,
																																																																																																																				86,
																																																																																																																				757,
																																																																																																																				6841,
																																																																																																																				12), UNICORN_STALLION(
																																																																																																																						207074,
																																																																																																																						12039,
																																																																																																																						12159,
																																																																																																																						140,
																																																																																																																						237,
																																																																																																																						88,
																																																																																																																						154,
																																																																																																																						6841,
																																																																																																																						12), GEYSER_TITAN(
																																																																																																																								207079,
																																																																																																																								12786,
																																																																																																																								12163,
																																																																																																																								222,
																																																																																																																								1444,
																																																																																																																								89,
																																																																																																																								783,
																																																																																																																								6841,
																																																																																																																								12), WOLPERTINGER(
																																																																																																																										207084,
																																																																																																																										12089,
																																																																																																																										12160,
																																																																																																																										203,
																																																																																																																										3226,
																																																																																																																										92,
																																																																																																																										405,
																																																																																																																										6841,
																																																																																																																										12), ABYSSAL_TITAN(
																																																																																																																												207089,
																																																																																																																												12796,
																																																																																																																												12159,
																																																																																																																												113,
																																																																																																																												12161,
																																																																																																																												93,
																																																																																																																												163,
																																																																																																																												6841,
																																																																																																																												12), IRON_TITAN(
																																																																																																																														207094,
																																																																																																																														12822,
																																																																																																																														12160,
																																																																																																																														198,
																																																																																																																														1115,
																																																																																																																														95,
																																																																																																																														418,
																																																																																																																														6841,
																																																																																																																														12), PACK_YAK(
																																																																																																																																207099,
																																																																																																																																12093,
																																																																																																																																12160,
																																																																																																																																211,
																																																																																																																																10818,
																																																																																																																																96,
																																																																																																																																422,
																																																																																																																																6841,
																																																																																																																																12), STEEL_TITAN(
																																																																																																																																		207104,
																																																																																																																																		12790,
																																																																																																																																		12163,
																																																																																																																																		178,
																																																																																																																																		1119,
																																																																																																																																		99,
																																																																																																																																		435,
																																																																																																																																		6841,
																																																																																																																																		12);

		// gold charm = 12158
		// green charm = 12159
		// crimson charm = 12160
		// Obsidian charm = 12168
		// Blue Charm = 12163
		// Green charm = 12159
		// Abyssal charm = 12161

		/**
		 * Variables
		 */

		private final int buttonId;
		private final int pouchId;
		private final int charmId;
		private final int shardsRequired;
		private final int secondIngredientId;
		private final int levelRequired;
		private final int experience;

		static Map<Integer, PouchData> pouchs = new HashMap<Integer, PouchData>();

		static {
			for (PouchData p : PouchData.values()) {
				pouchs.put(p.getPouchId(), p);
			}
		}

		/**
		 * Sets the enumeration data format
		 *
		 * @param buttonId returns the value of the actionbutton
		 * @param pouchId returns the value of the pouchId
		 * @param charmId returns the value of the charmId
		 * @param sacrificeId returns the value of the sacrificeId
		 * @param levelRequired returns the value of the levelRequired
		 * @param experience returns the value of the experience given
		 */

		private PouchData(final int buttonId, final int pouchId, final int charmId, final int shardsRequired, final int secondIngredientId, final int levelRequired,
				final int experience, int familiarId, int duration) {
			this.buttonId = buttonId;
			this.pouchId = pouchId;
			this.charmId = charmId;
			this.shardsRequired = shardsRequired;
			this.secondIngredientId = secondIngredientId;
			this.levelRequired = levelRequired;
			this.experience = experience;
		}

		/**
		 * Loops through all the pouch enumeration
		 *
		 * @param id the item id
		 * @return the pouchId
		 */

		public static PouchData get(int id) {
			return pouchs.get(id);
		}

		/**
		 * Loops through all the pouch enumeration
		 *
		 * @param id the button id
		 * @return the pouchId
		 */

		public static PouchData getByButton(int id) {
			for (PouchData p : PouchData.values()) {
				if (p.getButtonId() == id || p.getButtonId() + 1 == id || p.getButtonId() + 2 == id || p.getButtonId() + 3 == id || p.getButtonId() + 4 == id) {
					return p;
				}
			}
			return null;
		}

		/**
		 * Accessor for the action button id pouch creation interface
		 *
		 * @param buttonId parses the value of the buttonId
		 * @return buttonId
		 */

		public int getButtonId() {
			return buttonId;
		}

		/**
		 * Accessor for the Pouch Id it gives the player
		 *
		 * @return pouchId
		 */

		public int getPouchId() {
			return pouchId;
		}

		/**
		 * Accessor for the charm id required
		 *
		 * @return charmId
		 */

		public int getCharmId() {
			return charmId;
		}

		/**
		 * Accessor for the shards required to create the pouch
		 *
		 * @return shardsRequired
		 */

		public int getShardsRequired() {
			return shardsRequired;
		}

		/**
		 * Accessor for the second Ingredient required to create the pouch
		 *
		 * @return sacrificeId
		 */
		public int getSecondIngredientId() {
			return secondIngredientId;
		}

		/**
		 * Accessor for the level required to create the pouch
		 *
		 * @return levelRequired
		 */

		public int getLevelRequired() {
			return levelRequired;
		}

		/**
		 * Experience it gives the player for creating the pouch
		 *
		 * @return experience
		 */

		public int getExp() {
			return experience;
		}
	}

	public static final Item[] FRUITFALL_ITEMS = { new Item(2120, 1), new Item(1963, 1), new Item(2108, 1), new Item(2102, 1), new Item(5972, 1) };

	/**
	 * Handle Scrolls data for summoning.
	 *
	 * @author Gabriel : Wolfs Darker
	 */
	public enum ScrollsData {

		HOWL(201255, 0.1, 12047, 12425, 1, 0.1, 3, 15), DREADFOWL_STRIKE(202004, 0.1, 12043, 12445, 4, 0.1, 3, 15), EGG_SPAWN(202009, 0.2, 12059, 12428, 10, 0.2, 6,
				3), SLIME_SPRAY(202014, 0.2, 12019, 12459, 13, 0.2, 3, 15), STONY_SHELL(202019, 0.2, 12009, 12533, 16, 0.2, 12, 15), PESTER(202024, 0.5, 12778, 12838, 17, 0.5, 3,
						15), ELECTRIC_LASH(202029, 0.4, 12049, 12460, 18, 0.4, 6, 15), BRONZE_BULL_RUSH(202099, 3.6, 12073, 12461, 36, 3.6, 6, 15), IRON_BULL_RUSH(202154, 4.6,
								12075, 12462, 46, 4.6, 6, 15), TIRELESS_RUN(202174, 0.8, 12007, 12441, 52, 0.8, 8, 15), STEEL_BULL_RUSH(202189, 5.6, 12077, 12463, 56, 5.6, 6,
										15), MITHRIL_BULL_RUSH(202239, 6.6, 12079, 12464, 66, 6.6, 6, 15), SWALLOW_WHOLE(202254, 1.4, 12029, 12438, 68, 1.4, 3, 10), FRUITFALL(
												203003, 1.4, 12033, 12423, 69, 1.4, 6,
												3), ARCTIC_BLAST(203013, 1.1, 12057, 12451, 71, 1.1, 6, 15), INFERNO(203038, 1.5, 12782, 12841, 76, 1.5, 6, 15), ADAMANT_BULL_RUSH(
														203043, 7.6, 12081, 12465, 76, 7.6, 6,
														15), TITANS_CONSTITUTION(203058, 7.9, 12802, 12824, 79, 7.9, 20, 15), TITANS_CONSTITUTION_2(203063, 7.9, 12804, 12824, 79,
																7.9, 20, 15), TITANS_CONSTITUTION_3(203068, 7.9, 12806, 12824, 79, 7.9, 20, 15), SWAMP_PLAGUE(203088, 4.1, 12776,
																		12832, 86, 4.2, 6, 15), RUNE_BULL_RUSH(203093, 8.6, 12083, 12466, 86, 8.6, 6, 15), HEALING_AURA(203098, 1.8,
																				12039, 12434, 88, 1.8, 20, 15), BOIL(203103, 8.9, 12786, 12833, 89, 8.9, 6, 15), MAGIC_FOCUS(203108,
																						4.6, 12089, 12437, 92, 4.6, 20, 15), ESSENCE_SHIPMENT(203113, 1.9, 12796, 12827, 93, 1.9, 6,
																								10), IRON_WITHIN(203118, 4.7, 12822, 12828, 95, 4.8, 12, 15), WINTER_STORAGE(203123,
																										4.8, 12093, 12435, 96, 4.8, 12,
																										15), STEEL_OF_LEGENDS(203128, 4.9, 12790, 12825, 99, 5, 18, 15);

		private final int scrollId, levelReq, specRequired, pouchReq, buttonId;
		private final double castExp, createExp;
		private final int specialDelay;

		static Map<Integer, ScrollsData> scrolls = new HashMap<Integer, ScrollsData>();

		static {
			for (ScrollsData s : ScrollsData.values()) {
				scrolls.put(s.getScrollId(), s);
			}
		}

		private ScrollsData(int buttonId, double createExp, int pouchReq, int scrollId, int leveReq, double castExp, int specRequired, int specialDelay) {
			this.buttonId = buttonId;
			this.scrollId = scrollId;
			this.createExp = createExp;
			this.pouchReq = pouchReq;
			levelReq = leveReq;
			this.castExp = castExp;
			this.specRequired = specRequired;
			this.specialDelay = specialDelay;
		}

		public static ScrollsData get(int scrollId) {
			return scrolls.get(scrollId);
		}

		public static ScrollsData getByButtton(int id) {
			for (ScrollsData s : ScrollsData.values()) {
				if (s.getButtonId() == id || s.getButtonId() + 1 == id || s.getButtonId() + 2 == id || s.getButtonId() + 3 == id || s.getButtonId() + 4 == id) {
					return s;
				}
			}
			return null;
		}

		public int getButtonId() {
			return buttonId;
		}

		public int getScrollId() {
			return scrollId;
		}

		public double getCreateExp() {
			return createExp;
		}

		public int getPouchReq() {
			return pouchReq;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public double getCastExperience() {
			return castExp;
		}

		public int getSpecRequired() {
			return specRequired;
		}

		public int getSpecialDelay() {
			return specialDelay;
		}
	}

	public enum FamiliarAbilitys {
		GRANITE_CRAB(6796, Skill.FISHING, 1, 0), VOID_SPINNNER(7333, Skill.HITPOINTS, 1, 25), PYRELORD(7377, -1, -1, 0), IBIS(6991, Skill.FISHING, 3, 0), BUNYIP(6813,
				Skill.HITPOINTS, 1, 25), GRANITE_LOBSTER(6849, Skill.FISHING, 4, 0);

		private final int familiarId, skill_to_boost, boost_amount, loop_duration;

		private FamiliarAbilitys(int familiarId, int skill_to_boost, int boost_amount, int loop_duration) {
			this.familiarId = familiarId;
			this.skill_to_boost = skill_to_boost;
			this.boost_amount = boost_amount;
			this.loop_duration = loop_duration;
		}

		public int getFamiliarId() {
			return familiarId;
		}

		public int getSkillToBoost() {
			return skill_to_boost;
		}

		public int getBoostAmount() {
			return boost_amount;
		}

		public int getLoopDuration() {
			return loop_duration;
		}

		public static FamiliarAbilitys get(int npc_id) {
			for (FamiliarAbilitys f : FamiliarAbilitys.values()) {
				if (f.getFamiliarId() == npc_id) {
					return f;
				}
			}
			return null;
		}
	}

	public static boolean castOnInventory(MobFamiliar f) {
		switch (f.getId()) {
		case 6813:
		case 6873:
			return true;
		default:
			return false;
		}
	}

}
