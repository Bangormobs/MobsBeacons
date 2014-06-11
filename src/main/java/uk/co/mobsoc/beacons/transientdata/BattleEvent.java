package uk.co.mobsoc.beacons.transientdata;

import uk.co.mobsoc.beacons.storage.MySQL;
import uk.co.mobsoc.beacons.storage.TeamData;

public class BattleEvent extends Event {

	private int team1=-1, team2=-1;

	@Override
	public TeamData getTeam1() {
		return MySQL.getTeam(team1);
	}

	@Override
	public TeamData getTeam2() {
		return MySQL.getTeam(team2);
	}

}
