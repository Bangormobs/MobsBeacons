package uk.co.mobsoc.beacons.transientdata;

import org.bukkit.block.Block;

import uk.co.mobsoc.beacons.storage.BeaconData;

public class BeaconClaiming {
	/**
	 * The team attempting to claim this beacon
	 */
	public int teamId = -1;
	
	public int x,y,z;
	
	public int progress=0;
	
	public boolean isBeacon(BeaconData bd){
		Block b = bd.getBeacon();
		return b.getX() == x && b.getY() == y && b.getZ() == z;
	}

}
