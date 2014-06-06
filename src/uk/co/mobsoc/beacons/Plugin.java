package uk.co.mobsoc.beacons;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.mobsoc.beacons.listener.BeaconListener;
import uk.co.mobsoc.beacons.listener.EventListen;
import uk.co.mobsoc.beacons.transientdata.Event;

public class Plugin extends JavaPlugin {
	public static World world;
	@Override
	public void onEnable(){
		new EventListen(this);
		new BeaconListener(this);
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
}
