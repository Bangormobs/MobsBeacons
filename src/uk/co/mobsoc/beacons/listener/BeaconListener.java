package uk.co.mobsoc.beacons.listener;

import java.util.ArrayList;
import java.util.Iterator;

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
import org.bukkit.event.world.WorldInitEvent;

import uk.co.mobsoc.beacons.BeaconPopulator;
import uk.co.mobsoc.beacons.Plugin;
import uk.co.mobsoc.beacons.storage.BeaconData;
import uk.co.mobsoc.beacons.storage.MySQL;
import uk.co.mobsoc.beacons.storage.PlayerData;
import uk.co.mobsoc.beacons.storage.TeamData;

public class BeaconListener implements Listener{
	private Plugin plugin;

	public BeaconListener(Plugin plugin){
		this.plugin = plugin;
	}
	
	// Land-Protection
	public boolean isBeacon(Block b){
		Material type = b.getType();
		if(type == Material.BEACON){ return true; }
		if(type == Material.IRON_BLOCK || type == Material.GOLD_BLOCK || 
		   type == Material.DIAMOND_BLOCK || type == Material.EMERALD_BLOCK ||
		   type == Material.COAL_BLOCK || type == Material.LAPIS_BLOCK ||
		   type == Material.REDSTONE_BLOCK || type == Material.QUARTZ_BLOCK){
			// Might be Beacon base
			for(int x = -1 ; x < 2 ; x ++){
				for(int z = -1 ; z < 2 ; z++){
					if(b.getRelative(x, 1, z).getType() == Material.BEACON){
						return true;
					}
				}
			}
		}
		return false;
	}
	public BeaconData getBeaconFromBlock(Block block){
		ArrayList<BeaconData> bdList = MySQL.getAllBeacons();
		for(BeaconData bd : bdList){
			if(bd.isInsideRadius(block)){
				return bd;
			}
		}
		return null;	
	}
	public TeamData getTeamFromBlock(Block block){
		ArrayList<TeamData> tdList = MySQL.getAllTeams();
		for(TeamData td : tdList){
			ArrayList<BeaconData> bdList = MySQL.getBeaconsFromTeam(td);
			for(BeaconData bd : bdList){
				if(bd.isInsideRadius(block)){
					return td;
				}
			}
		}
		return null;
	}
	public boolean isAboveBeacon(Block b){
		BeaconData bd = getBeaconFromBlock(b);
		return bd.getBeacon().getX() == b.getX() &&
				   bd.getBeacon().getZ() == b.getZ() &&
				   bd.getBeacon().getY() < b.getY();
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(event.isCancelled()){ return; }
		if(isBeacon(event.getBlock())){ event.setCancelled(true); return; }
		TeamData blockTeam = getTeamFromBlock(event.getBlock());
		if(blockTeam == null){
			// "Wild" Land
			Material m = event.getBlock().getType();
			if(m == Material.LOG || m == Material.LOG_2 || m == Material.LEAVES || m == Material.LEAVES_2 || m == Material.LONG_GRASS || m == Material.GRASS || m == Material.DIRT){
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
		if(isBeacon(event.getBlock())){ event.setCancelled(true); return; }
		TeamData blockTeam = getTeamFromBlock(event.getBlock());
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
				if(isAboveBeacon(event.getBlock())){
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
		TeamData blockTeam = getTeamFromBlock(event.getClickedBlock());
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
		if(event.getBlocks().size()==0){ return; }
		TeamData td = getTeamFromBlock(event.getBlocks().get(0));
		for(Block b : event.getBlocks()){
			TeamData td2 = getTeamFromBlock(b);
			if(isAboveBeacon(b)){ event.setCancelled(true); return; }
			if(isBeacon(b)){ event.setCancelled(true); return; }
			if(td2.getTeamId() != td.getTeamId()){
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onBlockPulled(BlockPistonRetractEvent event){
		if(event.isCancelled()){ return; }
		Block pulledBlock = event.getRetractLocation().getBlock();
		if(isBeacon(pulledBlock)){ event.setCancelled(true); return; }
		TeamData td = getTeamFromBlock(event.getBlock());
		if(getTeamFromBlock(pulledBlock).getTeamId() != td.getTeamId()){
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBlockFlow(BlockFromToEvent event){
		if(event.isCancelled()){ return; }
		if(isBeacon(event.getToBlock())){ event.setCancelled(true); return; }
		if(isAboveBeacon(event.getToBlock())){
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
			if(isBeacon(b)){
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
		event.setRespawnLocation(l);
		PlayerData pd = MySQL.getPlayer(event.getPlayer().getUniqueId());
		TeamData td = MySQL.getTeam(pd.getTeamId());
		if(td!=null){
			ArrayList<BeaconData> bdList = MySQL.getBeaconsFromTeam(td);
			for(BeaconData bd : bdList){
				// TODO : Check proximity of threat Teams or Mobs. Disallow a base that is surrounded from spawning players
				double newDist = death.distanceSquared(bd.getBeacon().getLocation());
				if(newDist < lowest){
					lowest = newDist;
					l = bd.getBeacon().getLocation();
				}
			}
		}
		event.setRespawnLocation(l);
	}

	// Spawn Beacons at random intervals
	
	@EventHandler
	public void onWorldInit(WorldInitEvent e){
		e.getWorld().getPopulators().add(new BeaconPopulator());
	}
}
