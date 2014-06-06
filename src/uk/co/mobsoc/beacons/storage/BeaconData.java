package uk.co.mobsoc.beacons.storage;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Material;
import org.bukkit.block.Block;

import uk.co.mobsoc.beacons.Plugin;

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
	
}
