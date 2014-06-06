package uk.co.mobsoc.beacons.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerData {
	private UUID ident;
	private String name;
	private int team=-1;
	public PlayerData(ResultSet rs) {
		try {
			ident = MySQL.bytesToUuid(rs.getBytes(1));
			name = rs.getString(2);
			team = rs.getInt(3);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public PlayerData(){
	}
	public UUID getIdent() {
		return ident;
	}
	public void setIdent(UUID ident) {
		this.ident = ident;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTeamId() {
		return team;
	}
	public void setTeamId(int team) {
		this.team = team;
	}
	
}
