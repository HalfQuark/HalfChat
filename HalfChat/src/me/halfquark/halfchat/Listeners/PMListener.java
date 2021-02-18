package me.halfquark.halfchat.Listeners;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import me.halfquark.halfchat.HalfChat;

public class PMListener implements PluginMessageListener {
	
	private HalfChat plugin;
	
	public PMListener(HalfChat plugin) {
		this.plugin = plugin;
	}
	
	@Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		this.plugin.getLogger().log(Level.INFO, "Cross Message Successfully Received!");
		this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
	        if (!channel.equals("HCMessage")) return;
	        String stringMessage = new String(message);
	        for(Player serverPlayer: Bukkit.getOnlinePlayers()) {
	        	serverPlayer.sendMessage(stringMessage);
	        }

        }, 5L);
	}
	
}
