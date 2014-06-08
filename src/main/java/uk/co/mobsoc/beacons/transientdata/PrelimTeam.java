package uk.co.mobsoc.beacons.transientdata;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Transient data on teams below the player limit threshold. Teams only "form" truely after there are enough players 
 * @author triggerhapp
 *
 */
public class PrelimTeam {
	public static ArrayList<PrelimTeam> prelims = new ArrayList<PrelimTeam>();
	public static int INDEX = 0;
	
	private ArrayList<UUID> playersInTeam = new ArrayList<UUID>();
	private int index=-1;
	private String name = "";
	
	public PrelimTeam(Player player) {
		setIndex(INDEX); INDEX ++;
		addPlayer(player);
	}
	
	/**
	 * Adds player to potential team, before it is formed. Returns true if the team is large enough to become a real team
	 * @param player
	 * @return
	 */
	public boolean addPlayer(Player player) {
		playersInTeam.add(player.getUniqueId());
		return playersInTeam.size()>=4;
	}

	/**
	 * Gets the preliminary team the player belongs to. If none, create one with only them in and return it
	 * @param player
	 * @return
	 */
	public static PrelimTeam getPrelimTeam(Player player){
		for(PrelimTeam pre : prelims){
			if(pre.isInTeam(player)){ return pre; }
		}
		return new PrelimTeam(player);
	}
	
	public static boolean isInAnyTeam(Player player){
		for(PrelimTeam pre : prelims){
			if(pre.isInTeam(player.getUniqueId())){
				return true;
			}
		}
		return false;
	}
	
	public boolean isInTeam(Player player){
		return isInTeam(player.getUniqueId());
	}

	private boolean isInTeam(UUID uniqueId) {
		return playersInTeam.contains(uniqueId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public static PrelimTeam getPrelimTeam(int ident) {
		for(PrelimTeam pre : prelims){
			if(pre.getIndex() == ident){
				return pre;
			}
		}
		return null;
	}

	public void removePlayer(Player player) {
		playersInTeam.remove(player.getUniqueId());
	}
	
	

}
