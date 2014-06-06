package uk.co.mobsoc.beacons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import uk.co.mobsoc.beacons.storage.BeaconData;
import uk.co.mobsoc.beacons.storage.MySQL;
import uk.co.mobsoc.beacons.storage.TeamData;

public class Util {
	public static boolean isBeacon(Block b){
		Material type = b.getType();
		if(type == Material.BEACON){ return true; }
		if(type == Material.IRON_BLOCK || type == Material.GOLD_BLOCK || 
		   type == Material.DIAMOND_BLOCK || type == Material.EMERALD_BLOCK ||
		   type == Material.COAL_BLOCK || type == Material.LAPIS_BLOCK ||
		   type == Material.REDSTONE_BLOCK || type == Material.QUARTZ_BLOCK){
			// Might be Beacon base
			for(int x = -1 ; x < 2 ; x ++){
				for(int z = -1 ; z < 2 ; z++){
					if(b.getRelative(x, 1, z).getType() == Material.BEACON){
						return true;
					}
				}
			}
		}
		return false;
	}
	public static BeaconData getBeaconFromBlock(Block block){
		ArrayList<BeaconData> bdList = MySQL.getAllBeacons();
		for(BeaconData bd : bdList){
			if(bd.isInsideRadius(block)){
				return bd;
			}
		}
		return null;	
	}
	public static TeamData getTeamFromBlock(Block block){
		ArrayList<TeamData> tdList = MySQL.getAllTeams();
		for(TeamData td : tdList){
			ArrayList<BeaconData> bdList = MySQL.getBeaconsFromTeam(td);
			for(BeaconData bd : bdList){
				if(bd.isInsideRadius(block)){
					return td;
				}
			}
		}
		return null;
	}
	public static boolean isAboveBeacon(Block b){
		BeaconData bd = getBeaconFromBlock(b);
		if(bd == null){ return false; } // Not stored beacon-data. Let's hope
		return bd.getBeacon().getX() == b.getX() &&
				   bd.getBeacon().getZ() == b.getZ() &&
				   bd.getBeacon().getY() < b.getY();
	}
	public static List<Entity> getNearbyEntities(Location where, int range) {
		List<Entity> found = new ArrayList<Entity>();
		for (Entity entity : where.getWorld().getEntities()) {
			if (isInBorder(where, entity.getLocation(), range)) {
				found.add(entity);
			}
		}
		return found;
	}
	public static boolean isInBorder(Location center, Location notCenter, int range) {
		int x = center.getBlockX(), z = center.getBlockZ(), y = center.getBlockY();
		int x1 = notCenter.getBlockX(), z1 = notCenter.getBlockZ(), y1 = center.getBlockY();	 
		if (x1 >= (x + range) || z1 >= (z + range) || x1 <= (x - range) || z1 <= (z - range) || y1 >= (y+range) || y1 <= (y-range)) {
			return false;
		}
		return true;
	}
}
