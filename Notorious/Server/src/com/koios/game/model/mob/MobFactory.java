package com.koios.game.model.mob;

import java.util.Objects;

import com.koios.game.def.MobDefinition;
import com.koios.game.model.Location;
import com.koios.game.model.mob.impl.MobAbyssalDemon;
import com.koios.game.model.mob.impl.MobBanker;
import com.koios.game.model.mob.impl.MobBork;
import com.koios.game.model.mob.impl.MobCaveHorror;
import com.koios.game.model.mob.impl.MobDarkBeast;
import com.koios.game.model.mob.impl.bosses.MobChaosElemental;
import com.koios.game.model.mob.impl.bosses.MobCorporealBeast;
import com.koios.game.model.mob.impl.bosses.MobCursebearer;
import com.koios.game.model.mob.impl.bosses.MobDagganothPrime;
import com.koios.game.model.mob.impl.bosses.MobDagganothRex;
import com.koios.game.model.mob.impl.bosses.MobDagganothSupreme;
import com.koios.game.model.mob.impl.bosses.MobKingBlackDragon;
import com.koios.game.model.mob.impl.bosses.MobNomad;
import com.koios.game.model.mob.impl.bosses.MobPummeller;
import com.koios.game.model.mob.impl.bosses.MobTormentedDemon;
import com.koios.game.model.mob.impl.bosses.godwars.MobArmadylBoss;
import com.koios.game.model.mob.impl.bosses.godwars.MobArmadylMinion;
import com.koios.game.model.mob.impl.bosses.godwars.MobBandosBoss;
import com.koios.game.model.mob.impl.bosses.godwars.MobBandosMinion;
import com.koios.game.model.mob.impl.bosses.godwars.MobSaradominBoss;
import com.koios.game.model.mob.impl.bosses.godwars.MobSaradominMinion;
import com.koios.game.model.mob.impl.bosses.godwars.MobZamorakBoss;
import com.koios.game.model.mob.impl.bosses.godwars.MobZamorakMinion;
import com.koios.game.model.mob.impl.creature.MobChaosDwarf;

public final class MobFactory {

	protected static Mob create(int index, int id, Location location) {
		MobDefinition definition = MobDefinition.get(id);

		if (Objects.nonNull(definition) && Objects.nonNull(definition.getName())) {
			if (definition.getName().contains("Banker")) {
				return new MobBanker(index, id, location);
			}
		}

		switch (id) {
		case 50: // King black dragon
			return new MobKingBlackDragon(index, location);

		case 119: // Chaos dwarf
			return new MobChaosDwarf(index, id, location);

		case 1615: // Abyssal demon
			return new MobAbyssalDemon(index, location);

		case 2783: // Dark beast
			return new MobDarkBeast(index, location);

		case 2881: // Dagannoth Supreme
			return new MobDagganothSupreme(index, location);

		case 2882: // Dagannoth Prime
			return new MobDagganothPrime(index, location);

		case 2883: // Dagannoth Rex
			return new MobDagganothRex(index, location);

		case 3200: // Chaos Elemental
			return new MobChaosElemental(index, location);

		case 4353: // Cave horror
		case 4354: // Cave horror
		case 4355: // Cave horror
		case 4356: // Cave horror
		case 4357: // Cave horror
			return new MobCaveHorror(index, id, location);

		case 7133: // Bork
			return new MobBork(index, location);

		case 8133: // Corporeal Beast
			return new MobCorporealBeast(index, location);

		case 8349: // Tormented demon
		case 8350: // Tormented demon
		case 8351: // Tormented demon
			return new MobTormentedDemon(index, id, location);

		case 10141:
			return new MobPummeller(index, location);

		case 8528: // Nomad
			return new MobNomad(index, location);

		case 6222: // Kree'arra
			return new MobArmadylBoss(index, location);

		case 6225: // Flockleader Geerin
		case 6227: // Flight Kilisa
		case 6223: // Wingman Skree
			return new MobArmadylMinion(index, id, location);

		case 6260: // General Graardor
			return new MobBandosBoss(index, location);

		case 6261: // Sergeant Strongstack
		case 6263: // Sergeant Steelwill
		case 6265: // Sergeant Grimspike
			return new MobBandosMinion(index, id, location);

		case 6247: // Commander Zilyana
			return new MobSaradominBoss(index, location);

		case 6248: // Starlight
		case 6250: // Growler
		case 6252: // Bree
			return new MobSaradominMinion(index, id, location);

		case 6203: // K'ril Tsutsaroth
			return new MobZamorakBoss(index, location);

		case 6204: // Tstanon Karlak
		case 6206: // Zakl'n Gritch
		case 6208: // Balfrug Kreeyath
			return new MobZamorakMinion(index, id, location);

		case 10127: // Unholy cursebearer
			return new MobCursebearer(index, location);

		}

		return new Mob(index, id, location);
	}

}
