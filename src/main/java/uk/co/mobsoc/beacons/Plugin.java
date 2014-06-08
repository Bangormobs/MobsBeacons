package uk.co.mobsoc.beacons;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.mobsoc.beacons.listener.BeaconListener;
import uk.co.mobsoc.beacons.listener.EventListen;
import uk.co.mobsoc.beacons.storage.MySQL;
import uk.co.mobsoc.beacons.storage.PlayerData;
import uk.co.mobsoc.beacons.storage.TeamData;
import uk.co.mobsoc.beacons.timers.ClaimingWildTimer;
import uk.co.mobsoc.beacons.transientdata.Event;
import uk.co.mobsoc.beacons.transientdata.InviteData;
import uk.co.mobsoc.beacons.transientdata.PrelimTeam;

public class Plugin extends JavaPlugin {
	public static World world;
	private String userName, passWord, dataBase, IP;
	@Override
	public void onEnable(){
		readConfig();
		MySQL.prepareMySQL(IP, userName, passWord , dataBase);
		new EventListen(this);
		new BeaconListener(this);
		new ClaimingWildTimer(this);
	}
	
	@Override
	public void onDisable(){
		
	}

	private static Event event = null;
	public static Event getCurrentEvent() {
		return event;
	}
	public static void setCurrentEvent(Event event){
		if(Plugin.event != null){
			Plugin.event = event;
		}
	}
	
	private void readConfig() {
		saveDefaultConfig();
		userName = getConfig().getString("sql-username");
		passWord = getConfig().getString("sql-password");
		dataBase = getConfig().getString("sql-database");
		IP = getConfig().getString("sql-ip");

	}
	
	private ChatColor b=ChatColor.BLUE,g=ChatColor.GREEN,r=ChatColor.RESET;
	private String options = b+"["+g+"team"+b+"|"+g+"event"+b+"]";
	private String teamOptions = b+"["+g+"create"+b+"|"+g+"invite"+b+"|"+g+"join"+b+"|"+g+"leave"+b+"]";
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	Player player = null;
    	if(sender instanceof Player){
    		player = (Player) sender;
    	}
     	if(args.length==0){
    		sender.sendMessage("/beacons "+options);
    		return true;
    	}else{
    		if(args[0].equalsIgnoreCase("team")){
    			if(args.length==1){
    				sender.sendMessage("/beacons team "+teamOptions);
    				return true;
    			}else{
    				if(args[1].equalsIgnoreCase("create")){
    					if(args.length!=3){ sender.sendMessage("/beacons team create TeamName"); return true; }
    					if(args[2].length()>10){ sender.sendMessage("Team name too long."); return true; }
    					if(player == null){ sender.sendMessage("Only players can create teams"); return true; }
    					
    					PlayerData pd = MySQL.getPlayer(player.getUniqueId());
    					TeamData td = MySQL.getTeam(pd.getTeamId());
    					if(td != null){ sender.sendMessage("You are already in a team"); return true; }

    					
    					// No complete team
    					PrelimTeam pre = PrelimTeam.getPrelimTeam(player);
    					pre.setName(args[2]);
    					sender.sendMessage("You are now forming a team, use "+g+"/beacon team invite PlayerName"+r+" to invite players");
    					sender.sendMessage("When you have another 3 members the team will be able to claim beacons");
    					Bukkit.broadcastMessage(player.getName()+" is forming a team! Why not ask for an invite?");
    					return true;
    				}else if(args[1].equalsIgnoreCase("invite")){
    					if(args.length!=3){ sender.sendMessage("/beacons team invite PlayerName"); return true; }
    					if(player == null){ sender.sendMessage("Only players can invite to a team"); return true; }
    					Player p = Bukkit.getPlayer(args[2]);
    					PlayerData pd = MySQL.getPlayer(player.getUniqueId());
    					TeamData td = MySQL.getTeam(pd.getTeamId());
    					PrelimTeam pre = null;
    					if(td == null){
    						// Try Prelim
    						pre = PrelimTeam.getPrelimTeam(player);
    						if(pre == null){
    							sender.sendMessage("You are not in a team!");
    							return true;
    						}
    					}
    					InviteData id = new InviteData(p, td, pre);
    					sender.sendMessage("Invite sent to "+args[2]);
    					p.sendMessage("You have been invited into a team by "+player.getName());
    					p.sendMessage("To join do "+g+"/beacon team join "+id.getIndex());
    					return true;
    				}else if(args[1].equalsIgnoreCase("join")){
    					if(args.length!=3){ sender.sendMessage("/beacons team join inviteID"); return true; }
    					if(player==null){ sender.sendMessage("Only players can join a team"); return true; }
    					// Why no Integer.isInteger(String s) ?
    					if(!args[2].matches("^\\d+$")){ sender.sendMessage("The invite ID must be a number"); return true; }
    					PlayerData pd = MySQL.getPlayer(player.getUniqueId());
    					if(pd.getTeamId() != -1){ sender.sendMessage("You are already in a team!"); return true; }
    					if(PrelimTeam.isInAnyTeam(player)){ sender.sendMessage("You are already in a team!"); return true; }
    					InviteData id = InviteData.getInvite(Integer.parseInt(args[2]));
    					if(id!=null){
    						if(!id.getInvited().equals(player.getUniqueId())){
    							sender.sendMessage("That invite wasn't yours. Nice try though.");
    							return true;
    						}
    						if(id.getAge()>10){
    							sender.sendMessage("That invite has run out of time. Ask for a new one!");
    							return true;
    						}
    						int ident = id.getTeamId();
    						if(ident!=-1){
    							// It's a real Team
    							TeamData td = MySQL.getTeam(ident);
    							if(td==null){
    								// No, it isn't
    								sender.sendMessage("Cannot join a team that disbanded");
    								return true;
    							}
    							pd.setTeamId(ident);
    							MySQL.updated(pd);
    							sender.sendMessage("You have joined '"+td.getTeamName()+"'");
    							return true;
    						}
    						ident = id.getPreTeamId();
    						if(ident!=-1){
    							PrelimTeam pre = PrelimTeam.getPrelimTeam(ident);
    							if(pre==null){
    								sender.sendMessage("Cannot join a team that disbanded");
    								return true;
    							}
    							pre.addPlayer(player);
    							sender.sendMessage("You have joined '"+pre.getName()+"'");
    							// Lets see!
    							if(pre.getSize()>=4){
    								TeamData td = new TeamData();
    								td.setTeamName(pre.getName());
    								td.setScore(1000);
    								int teamID = MySQL.insertTeam(td);
    								MySQL.updated(td);
    								for(UUID uuid : pre.getPlayers()){
    									PlayerData thisPD = MySQL.getPlayer(uuid);
    									thisPD.setTeamId(teamID);
    									MySQL.updated(thisPD);
    								}
    								Bukkit.broadcastMessage("New team '"+td.getTeamName()+"' has been formed!");
    								return true;
    							}
    							return true;
    						}
    					}else{
    						sender.sendMessage("That invite has run out of time. Ask for a new one!");
    						return true;
    					}
    				}else if(args[1].equalsIgnoreCase("leave")){
    					if(player==null){ sender.sendMessage("You're a special kind of clever aren't you?"); return true; }
    					PlayerData pd = MySQL.getPlayer(player.getUniqueId());
    					TeamData td = MySQL.getTeam(pd.getTeamId());
    					PrelimTeam pre = null;
    					if(td == null){
    						// Try Prelim
    						pre = PrelimTeam.getPrelimTeam(player);
    						if(pre == null){
    							sender.sendMessage("You are not in a team!");
    							return true;
    						}else{
    							// Leave Prelim
    							pre.removePlayer(player);
    							sender.sendMessage("Left team");
    							return true;
    						}
    					}else{
    						// Leave Team
    						ArrayList<PlayerData> players = MySQL.getPlayersFromTeam(td);
    						if(players.size()<=4){
    							sender.sendMessage("You cannot leave a team with too few players. Vote to disband instead");
    							return true;
    						}
    						pd.setTeamId(-1);
    						MySQL.updated(pd);
    					}
    					return true;
    				}
    			}
    		}
    	}
     	return false;
    }
}
