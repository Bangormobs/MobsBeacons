package uk.co.mobsoc.beacons.storage;

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
	
}
