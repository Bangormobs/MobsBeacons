package uk.co.mobsoc.beacons.transientdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import uk.co.mobsoc.beacons.storage.MySQL;
import uk.co.mobsoc.beacons.storage.TeamData;

public class TeamVote {
	// TODO : Maybe replace with \\u codes?
	// And srsly? Java compiler reads unicode in comments? Strange
	public static String yes = "✓", no = "✘", unknown = "?";
	public static ArrayList<TeamVote> teamVotes = new ArrayList<TeamVote>();
	public static enum VoteFor {
		Change, SmallChance, Kick, Disband
	}
	public static enum VoteChoice {
		Yes, No
	}
	public static enum VoteChange {
		First, Changed, Same
	}
	
	public static TeamVote getTeamVote(int id){
		for(TeamVote tv : teamVotes){
			if(tv.getTeamId() == id){
				return tv;
			}
		}
		return null;
	}
	
	public TeamVote(){
		teamVotes.add(this);
	}
	
	public void endVote(){
		teamVotes.remove(this);
	}

	private int teamId = -1;
	private VoteFor votingFor = VoteFor.Change;
	private HashMap<UUID, VoteChoice> votes = new HashMap<UUID, VoteChoice>();
	private UUID kickPlayer = null;
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public VoteFor getVotingFor() {
		return votingFor;
	}
	public void setVotingFor(VoteFor votingFor) {
		this.votingFor = votingFor;
	}
	public UUID getKickPlayer() {
		return kickPlayer;
	}
	public void setKickPlayer(UUID kickPlayer) {
		this.kickPlayer = kickPlayer;
	}
	public VoteChange setPlayerVote(UUID player,  VoteChoice vc){
		VoteChoice lastChoice = null;
		if(votes.containsKey(player)){
			lastChoice = votes.get(player);
			if(lastChoice == vc){
				return VoteChange.Same; 
			}else{
				votes.put(player, vc);
				return VoteChange.Changed;
			}
		}
		votes.put(player, vc);
		return VoteChange.First;
	}

	public Collection<VoteChoice> getVotes() {
		return votes.values();
	}
	
	/**
	 * Returns the number of individuals who have voted the way given
	 * @param wantedChoice
	 * @return
	 */
	public int getVotes(VoteChoice wantedChoice){
		int yes = 0;
		for(VoteChoice choice : getVotes()){
			if(choice == wantedChoice){
				yes ++;
			}
		}
		return yes;
	}
	
	public String getReason(){
		if(votingFor == VoteFor.Kick){
			OfflinePlayer player = Bukkit.getOfflinePlayer(kickPlayer);
			if(player == null){ return "Voting to kick a player who doesn't exist"; }
			return "Voting to kick "+player.getName()+" from team";
		}else if(votingFor == VoteFor.Disband){
			return "Voting to disband team";
		}
		return "Nothing special";
	}
	public VoteChoice hasCompleted(){
		TeamData td = MySQL.getTeam(teamId);
		float midPoint = (td.getTeamSize()*1f)/2f; // Too long around languages which assume int in bad places.
		int midPointI = (int)(midPoint+.5f);
		if(getVotes(VoteChoice.Yes) >= midPointI){
			return VoteChoice.Yes;
		}else if(getVotes(VoteChoice.No) >= midPointI){
			return VoteChoice.No;
		}
		return null;
	}
}
