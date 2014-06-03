package uk.co.mobsoc.beacons.storage;

import java.util.UUID;

public class PlayerData {
	private UUID ident;
	private String name;
	private int id;
	private int team=-1;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTeamId() {
		return team;
	}
	public void setTeamId(int team) {
		this.team = team;
	}
	
}