package com.koios.network.in.impl;

import com.koios.GameService;
import com.koios.game.content.skill.agility.course.BarbarianCourse;
import com.koios.game.content.skill.agility.course.WildernessCourse;
import com.koios.game.content.skill.agility.obstacle.Obstacle;
import com.koios.game.content.skill.hunter.impl.DismantleTrap;
import com.koios.game.model.player.EntityInteraction;
import com.koios.game.model.player.Player;
import com.koios.network.GamePacket;
import com.koios.network.in.IncomingPacketDecoder;
import com.koios.task.EntityTask;

/**
 * Click Object
 */
public class PacketObjectOption implements IncomingPacketDecoder {

	private static final int FIRST_CLICK = 132;
	private static final int SECOND_CLICK = 252;
	private static final int THIRD_CLICK = 70;

	@Override
	public void handle(Player player, GamePacket packet) {
		player.clickObjectType = 0;
		player.objectX = 0;
		player.objectId = 0;
		player.objectY = 0;
		player.objectYOffset = 0;
		player.objectXOffset = 0;
		player.getFollowing().reset();

		if (player.getCurrentSkill() != null) {
			if (player.getCurrentSkill().canAbort()) {
				player.getCurrentSkill().setMustAbort(true);
			}
		}

		switch (packet.getOpcode()) {
		case FIRST_CLICK:
			player.objectX = packet.readLEShortA();
			player.objectId = packet.readShort();
			player.objectY = packet.readShortA();
			
            player.objectDistance = 1;
			player.setFacePosition(player.objectX, player.objectY);
            if(com.koios.game.arpg.MapRuns.object(player,player.objectId,player.objectX,player.objectY))return;

			if (player.isPacketReadDisabled()) {
				return;
			}

			if (Math.abs(player.getX() - player.objectX) > 25 || Math.abs(player.getY() - player.objectY) > 25) {
				player.getMovementHandler().reset();
				break;
			}

			switch (player.objectId) {
			case 37929:
			case 38811:
				if (player.getX() > player.objectX) {
					player.objectDistance = 4;
				}
				break;

			case 19187:
			case 19175:
			case 19195:
			case 19192:
			case 19189:
			case 19174:
			case 19179:
			case 19180:
			case 19183:
			case 19184:
			case 19185:
			case 19186:
			case 19654:
			case 19675:
			case 19659:
			case 19667:
			case 19661:
			case 19181:
			case 19182:
			case 19177:
			case 19178:
				if (GameService.getObjectHandler().get(player.objectX, player.objectY, player.getZ()) != null) {
					GameService.getTaskScheduler().schedule(new DismantleTrap(player, GameService.getObjectHandler().get(player.objectX, player.objectY, player.getZ())));
					return;
				}
				break;

			case 8929:
				player.objectDistance = 4;
				break;

			case 12163:
			case 4031:
			case 10595:
			case 10596:
				player.objectDistance = 2;
				break;

			case 3192:
				player.objectDistance = 4;
				break;

			case 1733:
				player.objectYOffset = 2;
				break;

			case 2966:
			case 3044:
			case 3294:
			case 3994:
			case 4304:
			case 6189:
			case 11009:
			case 11010:
			case 11666:
			case 12100:
			case 12809:
			case 14921:
			case 15976:
			case 18497:
			case 18525:
			case 18526:
				player.objectDistance = 3;
				break;

			case 245:
				player.objectYOffset = -1;
				player.objectDistance = 0;
				break;

			case 272:
				player.objectYOffset = 1;
				player.objectDistance = 0;
				break;

			case 273:
				player.objectYOffset = 1;
				player.objectDistance = 0;
				break;

			case 246:
				player.objectYOffset = 1;
				player.objectDistance = 0;
				break;

			case 4493:
			case 4494:
			case 4496:
			case 4495:
				player.objectDistance = 5;
				break;

			case 10229:
			case 6522:
				player.objectDistance = 2;
				break;

			case 8959:
				player.objectYOffset = 1;
				break;

			case 4417:
				if (player.objectX == 2425 && player.objectY == 3074) {
					player.objectYOffset = 2;
				}
				break;

			case 4420:
				if (player.getX() >= 2383 && player.getX() <= 2385) {
					player.objectYOffset = 1;
				} else {
					player.objectYOffset = -2;
				}
				break;

			case 6552:
			case 409:
			case 8749:
			case 61:
				player.objectDistance = 2;
				break;

			case 2879:
			case 2878:
				player.objectDistance = 3;
				break;

			case 2558:
				player.objectDistance = 0;

				if (player.getX() > player.objectX && player.objectX == 3044) {
					player.objectXOffset = 1;
				}

				if (player.getY() > player.objectY) {
					player.objectYOffset = 1;
				}

				if (player.getX() < player.objectX && player.objectX == 3038) {
					player.objectXOffset = -1;
				}
				break;

			case 9356:
				player.objectDistance = 2;
				break;

			case 5959:
			case 1815:
			case 5960:
			case 1816:
				player.objectDistance = 0;
				break;

			case 9293:
				player.objectDistance = 2;
				break;

			case 4418:
				if (player.objectX == 2374 && player.objectY == 3131) {
					player.objectYOffset = -2;
				} else if (player.objectX == 2369 && player.objectY == 3126) {
					player.objectXOffset = 2;
				} else if (player.objectX == 2380 && player.objectY == 3127) {
					player.objectYOffset = 2;
				}
				break;

			case 9706:
				player.objectDistance = 0;
				player.objectXOffset = 1;
				break;

			case 9707:
				player.objectDistance = 0;
				player.objectYOffset = -1;
				break;

			case 4419:
			case 6707: // verac
				player.objectYOffset = 3;
				break;

			case 6823:
				player.objectDistance = 2;
				player.objectYOffset = 1;
				break;

			case 6706: // torag
				player.objectXOffset = 2;
				break;

			case 6772:
				player.objectDistance = 2;
				player.objectYOffset = 1;
				break;

			case 6705: // karils
				player.objectYOffset = -1;
				break;

			case 6822:
				player.objectDistance = 2;
				player.objectYOffset = 1;
				break;

			case 6704: // guthan stairs
				player.objectYOffset = -1;
				break;

			case 6773:
				player.objectDistance = 2;
				player.objectXOffset = 1;
				player.objectYOffset = 1;
				break;

			case 6703: // dharok stairs
				player.objectXOffset = -1;
				break;

			case 6771:
				player.objectDistance = 2;
				player.objectXOffset = 1;
				player.objectYOffset = 1;
				break;

			case 6702: // ahrim stairs
				player.objectXOffset = -1;
				break;

			case 6821:
				player.objectDistance = 2;
				player.objectXOffset = 1;
				player.objectYOffset = 1;
				break;

			case 1276:
			case 1278:// trees
			case 1281: // oak
			case 1308: // willow
			case 1307: // maple
			case 1309: // yew
			case 1306: // magic
			case 6163:
				player.objectDistance = 3;
				player.objectYOffset = 1;
				player.objectXOffset = 1;
				break;

			case 2491:
				player.objectDistance = 5;
				break;

			case 23271:
				player.objectDistance = 2;
				player.objectYOffset = 1;
				break;

			default:
				
            player.objectDistance = 1;
				player.objectXOffset = 0;
				player.objectYOffset = 0;
				break;
			}

			int distance = 0;

			if (player.objectId == 2282) {

				if (player.distanceToPoint(2885, 5333) < 20) {
					if (player.getY() > player.objectY) {
						distance = 12;
					}
				} else {
					Obstacle barbarianObstacle = BarbarianCourse.getInstace().forData(player.objectId, player.getLocation());

					if (barbarianObstacle != null) {
						player.objectDistance = 5;
						BarbarianCourse.getInstace().interact(player, barbarianObstacle);
						return;
					}
				}
			} else if (player.objectId == 2283) {
				Obstacle wildernessObstacle = WildernessCourse.getInstace().forData(player.objectId, player.getLocation());

				if (wildernessObstacle != null) {
					player.objectDistance = 5;
					WildernessCourse.getInstace().interact(player, wildernessObstacle);
					return;
				}
			}

			if (distance == 0)
				if (player.getMinigame().inMinigame() && player.getMinigame().getCurrentMinigame().getMinigameName().equals("PROP_HUNT")) {
					distance = player.objectDistance = 7;
					player.sendMessage(player.objectX + ", " + player.objectXOffset + ", " + player.objectY + ", " + player.objectYOffset);
				} else {
					distance = EntityInteraction.getObjectDistanceRequired(player.objectId);
				}
			boolean goodDistance = player.goodDistance(player.objectX + player.objectXOffset, player.objectY + player.objectYOffset, player.getX(), player.getY(), distance);

			if (goodDistance) {
				EntityInteraction.firstClickObject(player, player.objectId, player.objectX, player.objectY);
			} else {
				player.clickObjectType = 1;

				player.schedule(new EntityTask<Player>(1, player, true) {

					@Override
					public void execute() {

						if (player.isDisconnected()) {
							stop();
							return;
						}

						if (player.clickObjectType != 1) {
							stop();
							return;
						}

						if (player.clickObjectType == 1 && player.goodDistance(player.objectX + player.objectXOffset, player.objectY + player.objectYOffset, player.getX(),
								player.getY(), player.objectDistance)) {
							EntityInteraction.firstClickObject(player, player.objectId, player.objectX, player.objectY);
							stop();
						}
					}

					@Override
					public void onStop() {
						player.clickObjectType = 0;
					}

				});
			}
			break;

		case SECOND_CLICK:
			player.objectId = packet.readLEShortA();
			player.objectY = packet.readLEShort();
			player.objectX = packet.readShortA();
			
            player.objectDistance = 1;
			player.setFacePosition(player.objectX, player.objectY);
            if(com.koios.game.arpg.MapRuns.object(player,player.objectId,player.objectX,player.objectY))return;

			if (player.isPacketReadDisabled()) {
				return;
			}

			switch (player.objectId) {
			case 6:
				// player.getDwarfMultiCannon().takeMultiCannon(
				// Server.objectHandler.get(player.objectX, player.objectY,
				// player.getZ()));
				break;

			case 5585:
			case 5584:
			case 313:
				player.objectDistance = 2;
				break;

			case 2560:
			case 2561:
			case 2562:
			case 2563:
			case 2564:
			case 2565:
			case 7053:
			case 14011:
			case 4277:
			case 4278:
				player.objectDistance = 2;
				player.objectXOffset = 1;
				player.objectYOffset = 1;
				break;

			case 9356:
				player.objectDistance = 2;
				break;

			case 6163:
			case 6165:
			case 6166:
			case 6164:
			case 6162:
				player.objectDistance = 2;
				break;

			default:
				
            player.objectDistance = 1;
				player.objectXOffset = 0;
				player.objectYOffset = 0;
				break;
			}

			if (player.goodDistance(player.objectX + player.objectXOffset, player.objectY + player.objectYOffset, player.getX(), player.getY(),
					EntityInteraction.getObjectDistanceRequired(player.objectId))) {
				EntityInteraction.secondClickObject(player, player.objectId, player.objectX, player.objectY);
			} else {
				player.clickObjectType = 2;

				player.schedule(new EntityTask<Player>(1, player, true) {

					@Override
					public void execute() {
						if (player.isDisconnected()) {
							stop();
							return;
						}

						if (player.clickObjectType != 2) {
							stop();
							return;
						}

						if (player.clickObjectType == 2 && player.goodDistance(player.objectX + player.objectXOffset, player.objectY + player.objectYOffset, player.getX(),
								player.getY(), player.objectDistance)) {
							EntityInteraction.secondClickObject(player, player.objectId, player.objectX, player.objectY);
							stop();
						}
					}

					@Override
					public void onStop() {
						player.clickObjectType = 0;
					}

				});
			}
			break;

		case THIRD_CLICK:
			player.objectX = packet.readLEShort();
			player.objectY = packet.readShort();
			player.objectId = packet.readLEShortA();
			player.setFacePosition(player.objectX, player.objectY);
            if(com.koios.game.arpg.MapRuns.object(player,player.objectId,player.objectX,player.objectY))return;

			if (player.isPacketReadDisabled()) {
				return;
			}

			switch (player.objectId) {
			default:
				
            player.objectDistance = 1;
				player.objectXOffset = 0;
				player.objectYOffset = 0;
				break;
			}

			if (player.goodDistance(player.objectX + player.objectXOffset, player.objectY + player.objectYOffset, player.getX(), player.getY(), player.objectDistance)) {
				EntityInteraction.thirdClickObject(player, player.objectId, player.objectX, player.objectY);
			} else {
				player.clickObjectType = 3;

				player.schedule(new EntityTask<Player>(1, player, true) {

					@Override
					public void execute() {
						if (player.isDisconnected()) {
							stop();
							return;
						}

						if (player.clickObjectType < 3) {
							stop();
							return;
						}

						if (player.clickObjectType == 3 && player.goodDistance(player.objectX + player.objectXOffset, player.objectY + player.objectYOffset, player.getX(),
								player.getY(), player.objectDistance)) {
							EntityInteraction.thirdClickObject(player, player.objectId, player.objectX, player.objectY);
							stop();
						}
					}

					@Override
					public void onStop() {
						player.clickObjectType = 0;
					}

				});
			}

			break;
		}
	}

}
