package uk.co.mobsoc.beacons.listener;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldInitEvent;

import uk.co.mobsoc.beacons.BeaconPopulator;
import uk.co.mobsoc.beacons.Plugin;
import uk.co.mobsoc.beacons.Util;
import uk.co.mobsoc.beacons.storage.BeaconData;
import uk.co.mobsoc.beacons.storage.MySQL;
import uk.co.mobsoc.beacons.storage.PlayerData;
import uk.co.mobsoc.beacons.storage.TeamData;

public class BeaconListener implements Listener{
	private Plugin plugin;

	public BeaconListener(Plugin plugin){
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}
	
	// Land-Protection
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(event.isCancelled()){ return; }
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE){ return; } // TODO : Breaking a Beacon removes it from DB too
		if(Util.isBeacon(event.getBlock())){ event.setCancelled(true); return; }
		TeamData blockTeam = Util.getTeamFromBlock(event.getBlock());
		if(blockTeam == null){
			// "Wild" Land
			Material m = event.getBlock().getType();
			if(m == Material.LOG || m == Material.LOG_2 || m == Material.LEAVES || m == Material.LEAVES_2 || m == Material.LONG_GRASS || m == Material.GRASS || m == Material.DIRT || m == Material.STAINED_CLAY || m == Material.HARD_CLAY || m == Material.CLAY || m == Material.SAND || m == Material.GRAVEL){
				// Allowed to take from wild
			}else{
				event.setCancelled(true);
			}
			return;
		}
		PlayerData playerData = MySQL.getPlayer(event.getPlayer().getUniqueId());
		if(playerData == null){
			// No player data?
		}else{
			TeamData playerTeam = MySQL.getTeam(playerData.getTeamId());
			if(playerTeam.getTeamId() == blockTeam.getTeamId()){
				// Same team
			}else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.isCancelled()){ return; }
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE){ return; }
		if(Util.isBeacon(event.getBlock())){ event.setCancelled(true); return; }
		TeamData blockTeam = Util.getTeamFromBlock(event.getBlock());
		if(blockTeam == null){
			// "Wild" Land
			event.setCancelled(true);
			return;
		}
		PlayerData playerData = MySQL.getPlayer(event.getPlayer().getUniqueId());
		if(playerData == null){
			// No player data?
		}else{
			TeamData playerTeam = MySQL.getTeam(playerData.getTeamId());
			if(playerTeam.getTeamId() == blockTeam.getTeamId()){
				// Same team
				if(Util.isAboveBeacon(event.getBlock())){
					// Cannot place blocks above beacon
					event.setCancelled(true);
				}
				
			}else{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockInteract(PlayerInteractEvent event){
		if(event.isCancelled()){ return; }
		if(!event.hasBlock()){ return; }
		TeamData blockTeam = Util.getTeamFromBlock(event.getClickedBlock());
		if(blockTeam == null){
			// "Wild" Land
			return;
		}
		PlayerData playerData = MySQL.getPlayer(event.getPlayer().getUniqueId());
		if(playerData == null){
			// No player data?
		}else{
			TeamData playerTeam = MySQL.getTeam(playerData.getTeamId());
			if(playerTeam.getTeamId() == blockTeam.getTeamId()){
				// Same team
			}else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockPushed(BlockPistonExtendEvent event){
		// Disallow pushing blocks over boundraries
		if(event.isCancelled()){ return; }
		//if(event.getBlocks().size()==0){ return; }
		TeamData td = Util.getTeamFromBlock(event.getBlock());
		Block b = event.getBlock();
		for(int i = 0; i <= event.getLength()+1; i++){
			System.out.println(b.getType());
			TeamData td2 = Util.getTeamFromBlock(b);
			if(Util.isAboveBeacon(b)){ event.setCancelled(true); return; }
			if(Util.isBeacon(b)){ event.setCancelled(true); return; }
			// That's quite enough of that if.
			// Null being valid makes this a nice mess.
			if((td != td2) && ((td == null && td2 != null) || (td2 == null && td!=null) || (td2.getTeamId() != td.getTeamId()))){
				event.setCancelled(true);
				return;
			}
			b = b.getRelative(event.getDirection());
		}
	}
	
	@EventHandler
	public void onBlockPulled(BlockPistonRetractEvent event){
		if(event.isCancelled()){ return; }
		Block pulledBlock = event.getRetractLocation().getBlock();
		if(Util.isBeacon(pulledBlock)){ event.setCancelled(true); return; }
		if(Util.isAboveBeacon(event.getBlock())){ event.setCancelled(true); return; }
		TeamData td = Util.getTeamFromBlock(event.getBlock());
		TeamData td2 = Util.getTeamFromBlock(pulledBlock);
		if((td != td2) && ((td == null && td2 != null) || (td2 == null && td != null) || (td2.getTeamId() != td.getTeamId()))){
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBlockFlow(BlockFromToEvent event){
		if(event.isCancelled()){ return; }
		if(Util.isBeacon(event.getToBlock())){ event.setCancelled(true); return; }
		if(Util.isAboveBeacon(event.getToBlock())){
			// Cannot place blocks above beacon
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlocksExpolode(EntityExplodeEvent event){
		// TODO - test and see if the remove statement commented out is needed
		Iterator<Block> iter = event.blockList().iterator();
		while(iter.hasNext()){
			Block b = iter.next();
			if(Util.isBeacon(b)){
				iter.remove();
				//event.blockList().remove(b);
			}
		}
	}
	
	// Respawning after death
	
	@EventHandler
	public void onPrepareRespawn(PlayerRespawnEvent event){
		Location death = event.getPlayer().getLocation();
		Location l = Plugin.world.getSpawnLocation();
		double lowest = death.distanceSquared(l);
		PlayerData pd = MySQL.getPlayer(event.getPlayer().getUniqueId());
		TeamData td = MySQL.getTeam(pd.getTeamId());
		if(td!=null){
			ArrayList<BeaconData> bdList = MySQL.getBeaconsFromTeam(td);
			for(BeaconData bd : bdList){
				if(bd.isThreatNearby(pd.getTeamId())){ continue; }
				double newDist = death.distanceSquared(bd.getBeacon().getLocation());
				if(newDist < lowest){
					lowest = newDist;
					l = bd.getBeacon().getLocation();
				}
			}
		}
		l= l.add(0.5, 0.5, 0.5);
		event.setRespawnLocation(l);
	}

	// Spawn Beacons at random intervals
	
	@EventHandler
	public void onWorldInit(WorldInitEvent e){
		if(Plugin.world==null){ Plugin.world = e.getWorld(); System.out.println("World '"+Plugin.world.getName()+"' chosen as primary world"); }
		e.getWorld().getPopulators().add(new BeaconPopulator());
	}
	
	// Add beacons to DB
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event){
		Chunk chunk = event.getChunk();
		for(int x = 3; x<13; x++){
			for(int z = 3; z<13; z++){
				Block b= event.getWorld().getHighestBlockAt((chunk.getX()*16)+x, (chunk.getZ()*16)+z);
				if(b.getType()==Material.BEACON){
					BeaconData bd = MySQL.getBeaconFromBlock(b);
					if(bd==null){
						// Not a registered Beacon
						MySQL.createWildBeacon(b);
					}
				}
			}
		}
	}
}
