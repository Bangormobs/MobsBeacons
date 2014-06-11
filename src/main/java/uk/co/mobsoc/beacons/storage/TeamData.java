package uk.co.mobsoc.beacons.storage;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import uk.co.mobsoc.beacons.listener.MessageListener;

public class TeamData {
	private int teamId = -1, score=0;
	private String teamName = "";
	public TeamData(ResultSet rs) {
		try {
			teamId = rs.getInt(1);
			teamName = rs.getString(2);
			score = rs.getInt(3);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public TeamData(){
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getTeamSize(){
		return MySQL.getPlayersFromTeam(this).size();
	}
	public void sendAll(String s) {
		for(PlayerData pd :MySQL.getPlayersFromTeam(this)){
			OfflinePlayer p = pd.getPlayer();
			if(p.isOnline()){
				Player player = p.getPlayer();
				player.sendMessage(s);
			}else{
				MessageListener.setMessage(p.getUniqueId(), s);
			}
		}
	}
	/**
	 * Disband a team - unclaim all land - remove all players to non-team
	 */
	public void disband() {
		for(PlayerData disbandedPlayer : MySQL.getPlayersFromTeam(this)){
			disbandedPlayer.setTeamId(-1);
			MySQL.updated(disbandedPlayer);
		}
		for(BeaconData disbandedBeacon : MySQL.getBeaconsFromTeam(this)){
			disbandedBeacon.setBase(Material.AIR);
			disbandedBeacon.setTeamId(-1);
			MySQL.updated(disbandedBeacon);
		}
		// TODO : Remove self from DB
	}
	/**
	 * Adds or removes points from team score - disbands if total points drops to or below 0
	 * @param i
	 */
	public void addScore(int i) {
		setScore(getScore()+i);
		if(getScore() <= 0){
			// Oh dear!
			disband();
			Bukkit.broadcastMessage("'"+getTeamName()+"' has lost too many points and disbanded!");
		}
		MySQL.updated(this);
	}
	
}
