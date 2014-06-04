package uk.co.mobsoc.beacons.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import uk.co.mobsoc.beacons.Plugin;
import uk.co.mobsoc.beacons.transientdata.Event;

public class EventListen implements Listener{
	Plugin plugin;
	public EventListen(Plugin plugin){
		this.plugin = plugin;
	}
	
	/*
	 * All Block-editing events MUST be recorded
	 */
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Event mode = Plugin.getCurrentEvent();
		if(mode != null){
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		Event mode = Plugin.getCurrentEvent();
		if(mode != null){
		}
	}
}
