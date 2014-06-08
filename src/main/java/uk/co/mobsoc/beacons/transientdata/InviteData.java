package uk.co.mobsoc.beacons.transientdata;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

import uk.co.mobsoc.beacons.storage.TeamData;

/**
 * Transient data containing all recent invites
 * @author triggerhapp
 *
 */
public class InviteData {
	private static ArrayList<InviteData> invites = new ArrayList<InviteData>();
	private static int INDEX = 1;
	
	private int index = -1;
	private UUID invited;
	private int teamId = -1;
	private int preTeamId = -1;
	private int age = 0;
	/**
	 * Save an invite for a player into a Team or PrelimTeam. One of the team types must be null and the other a valid team or pre-team
	 * @param player
	 * @param td
	 * @param pd
	 */
	public InviteData(Player player, TeamData td, PrelimTeam pd){
		invited = player.getUniqueId();
		if(td != null){
			teamId = td.getTeamId();
		}
		if(pd != null){
			preTeamId = pd.getIndex();
		}
		setIndex(INDEX); INDEX ++;
		invites.add(this);
	}

	public UUID getInvited() {
		return invited;
	}
	public void setInvited(UUID invited) {
		this.invited = invited;
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public int getPreTeamId() {
		return preTeamId;
	}
	public void setPreTeamId(int preTeamId) {
		this.preTeamId = preTeamId;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public static InviteData getInvite(int index) {
		for(InviteData id : invites){
			if(id.getIndex() == index){ return id; }
		}
		return null;
	}
}
