package uk.co.mobsoc.beacons;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.mobsoc.beacons.listener.BeaconListener;
import uk.co.mobsoc.beacons.listener.EventListen;
import uk.co.mobsoc.beacons.storage.MySQL;
import uk.co.mobsoc.beacons.timers.ClaimingWildTimer;
import uk.co.mobsoc.beacons.transientdata.Event;

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
}
