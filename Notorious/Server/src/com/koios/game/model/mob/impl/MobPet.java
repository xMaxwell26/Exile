package com.koios.game.model.mob.impl;

import com.koios.game.content.PetManager;
import com.koios.game.content.PetManager.PetDefinition;
import com.koios.game.model.Location;
import com.koios.game.model.mob.Mob;
import com.koios.game.model.player.Player;
import com.koios.network.out.impl.SendClientScriptPacket;
import com.koios.network.out.impl.SendOpenSideTabPacket;
import com.koios.network.out.impl.SendSidebarInterfacePacket;
import com.koios.util.NumberUtil;

public class MobPet extends Mob {

	private PetDefinition petDefinition = null;
	private int hunger = 0;
	private int sizeStage = 0;
	private int tickTime = 0;
	private boolean warned = false;

	public MobPet(int id, Location location, PetDefinition definition) {
		super(id, location);
		petDefinition = definition;
		tickTime = 1000;
	}

	@Override
	public boolean isFamiliar() {
		return true;
	}

	@Override
	public void update() {
		if (tickTime == 0) {
			if (summoner == null) {
				remove();
				return;
			}

			Player player = summoner.toPlayer();
			hunger += NumberUtil.random(4);

			if (hunger >= 100) {
				if (warned) {
					player.sendMessage("Your pet has ran away from you...");
					remove();
					player.getSummoning().reset();
					player.getSession().send(new SendSidebarInterfacePacket(16, -1));
					player.getSession().send(new SendOpenSideTabPacket(4));
					player.getSession().send(new SendClientScriptPacket(1, "false-null-false"));
				} else {
					player.sendMessage("Your pet is hungry, feed your pet otherwise it will run away.");
					warned = true;
				}
			} else {
				if (petDefinition.getNextStage() > 0) {
					sizeStage += NumberUtil.inclusiveRandom((100 - hunger) / 20);

					if (sizeStage >= 100) {
						sizeStage = 0;
						PetManager.growPet(summoner.toPlayer());
					}
				}

				player.getSummoning().sendTab(false);
			}

			tickTime = 1000;
		}

		if (tickTime > 0) {
			tickTime--;
		}

		super.update();
	}

	public int getHunger() {
		return hunger;
	}

	public int getStage() {
		return sizeStage;
	}

	public PetDefinition getPetDef() {
		return petDefinition;
	}

	public void update(int stage, int hunger) {
		this.hunger = hunger;
		sizeStage = stage;
	}

	public void grow(PetDefinition definition) {
		requestTransform(definition.getNextStage());
		setId(definition.getNextStage());
		petDefinition = definition;
	}

}