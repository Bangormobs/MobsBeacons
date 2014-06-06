package uk.co.mobsoc.beacons.storage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeamData {
	private int teamId = -1;
	private String teamName = "";
	public TeamData(ResultSet rs) {
		try {
			teamId = rs.getInt(1);
			teamName = rs.getString(2);

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
	
}
