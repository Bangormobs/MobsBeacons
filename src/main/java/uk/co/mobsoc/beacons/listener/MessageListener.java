package uk.co.mobsoc.beacons.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import uk.co.mobsoc.beacons.Plugin;

public class MessageListener implements Listener {
	private static HashMap<UUID, String> latestMessage= new HashMap<UUID, String>();
	private Plugin plugin;

	public MessageListener(Plugin plugin){
		this.plugin=plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}
	
	public static void setMessage(UUID player, String message){
		latestMessage.put(player, message);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		Player p = event.getPlayer();
		if(latestMessage.containsKey(p.getUniqueId())){
			String message = latestMessage.get(p);
			p.sendMessage(message);
			latestMessage.remove(p.getUniqueId());
		}
	}
}
