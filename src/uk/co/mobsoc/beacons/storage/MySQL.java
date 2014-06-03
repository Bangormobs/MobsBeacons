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
	
	public static PlayerData getPlayer(UUID id){
		// TODO : Anything
		return null;
	}
	
	public static ArrayList<PlayerData> getPlayersFromTeam(TeamData td){
		// TODO : Anything
		return null;
	}
	
	public static TeamData getTeam(int id){
		// TODO : Anything
		return null;
	}
	
	public static ArrayList<TeamData> getAllTeams(){
		// TODO : Anything
		return null;
	}
	
	public static ArrayList<BeaconData> getBeaconsFromTeam(TeamData td){
		// TODO : Anything
		return null;
	}
	
	public static BeaconData getBeaconFromBlock(Block block){
		// TODO : Anything
		return null;
	}
}
