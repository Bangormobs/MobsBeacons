package uk.co.mobsoc.beacons.timers;

import org.bukkit.Bukkit;

import uk.co.mobsoc.beacons.Plugin;
import uk.co.mobsoc.beacons.storage.BeaconData;
import uk.co.mobsoc.beacons.storage.MySQL;
import uk.co.mobsoc.beacons.storage.TeamData;

public class PointTimer implements Runnable {
	
	public PointTimer(Plugin plugin){
		// 20 ticks a second, 60 seconds a minute, 60 minutes an hour
		long oneHour = 20l * 60l * 60l;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 10, oneHour);
	}

	@Override
	public void run() {
		System.out.println("One hour! Lets add the scores!");
		for(TeamData td : MySQL.getAllTeams()){
			int points = MySQL.getBeaconsFromTeam(td).size() * 5;
			System.out.println("Team '"+td.getTeamName()+"' gains "+points+" points");
			td.addScore(points);
		}
	}

}
