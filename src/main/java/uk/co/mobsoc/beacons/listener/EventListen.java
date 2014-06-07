package uk.co.mobsoc.beacons.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

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
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event){
		Event mode = Plugin.getCurrentEvent();
		if(mode != null){
		}
	}
	
	@EventHandler
	public void onBlockExp(BlockExpEvent event){
		Event mode = Plugin.getCurrentEvent();
		if(mode != null){
			// TODO cancel extra XP if raided
		}
	}
	
	@EventHandler
	public void onBlockFlow(BlockFromToEvent event){
		Event mode = Plugin.getCurrentEvent();
		if(mode != null){
		}
	}
	
	@EventHandler
	public void onEntityChangeBlockEvent(EntityChangeBlockEvent event){
		Event mode = Plugin.getCurrentEvent();
		if(mode != null){
		}
	}
}
