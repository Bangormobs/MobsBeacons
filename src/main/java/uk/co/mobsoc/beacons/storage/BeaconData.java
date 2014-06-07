package uk.co.mobsoc.beacons.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import uk.co.mobsoc.beacons.Plugin;
import uk.co.mobsoc.beacons.Util;

/**
 * Storage for all owned or previously owned beacons
 * @author triggerhapp
 *
 */
public class BeaconData {
	private int beaconx, beaconz; // Why does getBlockAt not accept long?
	private int beacony;
	private int teamId=-1;
	private int radius=7; // Random between 7 and 12
	// Should be 40 always for bases
	public BeaconData(ResultSet rs){
		try {
			beaconx = rs.getInt(1);
			beacony = rs.getInt(2);
			beaconz = rs.getInt(3);
			teamId = rs.getInt(4);
			radius = rs.getInt(5);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public BeaconData(){
		
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public Block getBeacon(){
		return Plugin.world.getBlockAt(beaconx, beacony, beaconz);
	}
	/**
	 * Is checking the type wise?
	 * @param block
	 */
	public void setBeacon(Block block){
		if(block == null){ return; }
		if(block.getType() == Material.BEACON){
			beaconx = block.getX();
			beacony = block.getY();
			beaconz = block.getZ();
		}
	}
	
	public boolean isInsideRadius(Block block){
		int tx = block.getX();
		int tz = block.getZ();
		Block beacon = getBeacon();
		int bx = beacon.getX();
		int bz = beacon.getZ();
		int rad = getRadius();
		if(tx < bx - rad || tx > bx + rad ||
		   tz < bz - rad || tz > bz + rad){
			// Outside of this beacon area
			return false;
		}else{
			// Inside this beacon area
			return true;
		}
	}
	
	public boolean isThreatNearby(int teamId){
		List<Entity> nearbyEntities = Util.getNearbyEntities(getBeacon().getLocation(),30); // TODO Abitrary number from docs. Check if it's usable in testing
		boolean threatNearby = false;
		for(Entity e : nearbyEntities){
			if(e instanceof Player){
				Player nearbyPlayer = (Player) e;
				PlayerData pd = MySQL.getPlayer(nearbyPlayer.getUniqueId());
				if(pd.getTeamId()!=-1 && pd.getTeamId() != teamId){
					// Different teams
					return true;
				}
			}
		}
		return false;
	}
	public int getX(){
		return beaconx;
	}
	public int getY(){
		return beacony;
	}
	public int getZ(){
		return beaconz;
	}
	public void setBase(Material m) {
		Block b = getBeacon();
		for(int x = -1; x < 2; x++){
			for(int z = -1 ; z < 2; z++){
				b.getRelative(x, -1, z).setType(m);
			}
		}
		
	}
}
