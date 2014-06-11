package uk.co.mobsoc.beacons.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import uk.co.mobsoc.beacons.Plugin;
import uk.co.mobsoc.beacons.storage.MySQL;
import uk.co.mobsoc.beacons.storage.PlayerData;
import uk.co.mobsoc.beacons.storage.TeamData;

public class PointListener implements Listener {
	public PointListener(Plugin plugin){
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		Player p = event.getEntity();
		PlayerData pd = MySQL.getPlayer(p.getUniqueId());
		TeamData td = MySQL.getTeam(pd.getTeamId());
		EntityDamageEvent lastEvent = event.getEntity().getLastDamageCause();
		if(lastEvent instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent justAFleshWound = (EntityDamageByEntityEvent) lastEvent;
			if(justAFleshWound.getDamager() instanceof Player){

				td.addScore(-10); // TODO Magic number from Docs
				return;
			}
		}
		td.addScore(-1); // TODO Magic number from Docs
	}
}
