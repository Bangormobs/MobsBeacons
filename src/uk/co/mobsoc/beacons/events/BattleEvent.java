package uk.co.mobsoc.beacons.events;

import uk.co.mobsoc.beacons.Plugin;
import uk.co.mobsoc.beacons.storage.MySQL;
import uk.co.mobsoc.beacons.storage.TeamData;

public class BattleEvent implements Event {
	private int warmuptimer = 0;
	private int maintimer = 0;
	private int cooldowntimer = 0;
	private int team1=-1, team2=-1;
	@Override
	public boolean isActive() {
		return cooldowntimer < Plugin.world.getMaxHeight();
	}

	@Override
	public TeamData getTeam1() {
		return MySQL.getTeam(team1);
	}

	@Override
	public TeamData getTeam2() {
		return MySQL.getTeam(team2);
	}

}
