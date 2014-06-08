package uk.co.mobsoc.beacons.timers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import uk.co.mobsoc.beacons.Plugin;
import uk.co.mobsoc.beacons.Util;
import uk.co.mobsoc.beacons.storage.BeaconData;
import uk.co.mobsoc.beacons.storage.MySQL;
import uk.co.mobsoc.beacons.storage.PlayerData;
import uk.co.mobsoc.beacons.storage.TeamData;
import uk.co.mobsoc.beacons.transientdata.BeaconClaiming;

public class ClaimingWildTimer implements Runnable{
	public static ArrayList<BeaconClaiming> lastList = new ArrayList<BeaconClaiming>();
	public ClaimingWildTimer(Plugin plugin){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 10, 10);
	}

	@Override
	public void run() {
		@SuppressWarnings("unchecked")
		ArrayList<BeaconClaiming> inactiveList = (ArrayList<BeaconClaiming>) lastList.clone();
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.getGameMode() != GameMode.SURVIVAL){ continue; }
			Block b = p.getLocation().getBlock();
			BeaconData bd = Util.getBeaconFromBlock(b);
			if(bd==null){ continue; } // No nearby beacon
			if(bd.getTeamId()!=-1){ continue; } // Team Beacons should be dealt with another way - maybe here still but we'll see
			Block bBeacon = bd.getBeacon();
			PlayerData player = MySQL.getPlayer(p.getUniqueId());
			if(player.getTeamId() == -1){ continue; } // Team-less players cannot claim
			if(bBeacon.getLocation().distance(b.getLocation())<=3d){ // 3D? sounds epic! (Means 3 as a double, not 3 as an int)
				// So they are near a beacon
				boolean isAlreadyClaiming = false;
				for(BeaconClaiming bc : lastList){
					if(bc.isBeacon(bd)){
						isAlreadyClaiming = true;
						// This beacon is in a list of transient data on beacons being captured
						inactiveList.remove(bc);
						
						// Check for presence of threats.
						
						if(bd.isThreatNearby(player.getTeamId())){ continue; }
						
						bc.progress++;
						p.sendMessage("Claiming nearby beacon... "+bc.progress+"% complete");
						// 300 seconds to claim from wild? Maybe too much
						if(bc.progress >= 600){
							inactiveList.add(bc);
							bd.setTeamId(player.getTeamId());
							bd.setRadius(40);
							MySQL.updateBeacon(bd);
							TeamData td = MySQL.getTeam(player.getTeamId());
							bd.setBase(Material.IRON_BLOCK);
							Bukkit.broadcastMessage("'"+td.getTeamName()+"' has captured a Beacon from Wild");
						}
					}
				}	
				if(!isAlreadyClaiming){
					// Player is starting the capture of this beacon
					BeaconClaiming newClaiming = new BeaconClaiming();
					newClaiming.x = bBeacon.getX();
					newClaiming.y = bBeacon.getY();
					newClaiming.z = bBeacon.getZ();
					newClaiming.teamId = player.getTeamId();
					newClaiming.progress=0;
					lastList.add(newClaiming);
				}
			}

		}
		for(BeaconClaiming inactive : inactiveList){
			// Beacons no one is near any longer
			inactive.progress-=10;
			if(inactive.progress<=0){
				lastList.remove(inactive);
			}
		}
	}

}
