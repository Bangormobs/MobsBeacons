package uk.co.mobsoc.beacons.storage;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.block.Block;

/**
 * Utility class of static functions related to gathering or altering data in MySQL
 * @author triggerhapp
 *
 */
public class MySQL {
	public static void prepareMySQL(){
		// TODO : Anything
	}
	
	public PlayerData getPlayer(UUID id){
		// TODO : Anything
		return null;
	}
	
	public ArrayList<PlayerData> getPlayersFromTeam(TeamData td){
		// TODO : Anything
		return null;
	}
	
	public TeamData getTeam(int id){
		// TODO : Anything
		return null;
	}
	
	public ArrayList<TeamData> getAllTeams(){
		// TODO : Anything
		return null;
	}
	
	public ArrayList<BeaconData> getBeaconsFromTeam(TeamData td){
		// TODO : Anything
		return null;
	}
	
	public BeaconData getBeaconFromBlock(Block block){
		// TODO : Anything
		return null;
	}
}
