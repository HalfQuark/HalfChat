package me.halfquark.halfchat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.halfquark.halfchat.Config.ChatCommand;
import me.halfquark.halfchat.Listeners.ChatListener;
import me.halfquark.halfchat.Listeners.PMListener;
import me.halfquark.halfchat.Listeners.TabCompleterListener;

public class HalfChat extends JavaPlugin {
	
	public File playerChannelsFile = new File(getDataFolder(), "playerChannels.yml");
	public File playerDMsFile = new File(getDataFolder(), "playerDMs.yml");
	public File playerColorsFile = new File(getDataFolder(), "playerColors.yml");
	public File playerMutesFile = new File(getDataFolder(), "playerMutes.yml");
	public File partyFile = new File(getDataFolder(), "party.yml");
	public File prefixFile = new File(getDataFolder(), "prefix.yml");
	public FileConfiguration playerChannels = YamlConfiguration.loadConfiguration(playerChannelsFile);
	public FileConfiguration playerDMs = YamlConfiguration.loadConfiguration(playerDMsFile);
	public FileConfiguration playerColors = YamlConfiguration.loadConfiguration(playerColorsFile);
	public FileConfiguration playerMutes = YamlConfiguration.loadConfiguration(playerMutesFile);
	public FileConfiguration party = YamlConfiguration.loadConfiguration(partyFile);
	public FileConfiguration prefix = YamlConfiguration.loadConfiguration(prefixFile);
	public Boolean fislandsHook;
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		ConfigurationSerialization.registerClass(ChatCommand.class);
		getServer().getPluginManager().registerEvents(new ChatListener(this), this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PMListener(this));
        getConfig().options().copyDefaults(true);
        this.getCommand("tell").setTabCompleter(new TabCompleterListener());
        this.getCommand("msg").setTabCompleter(new TabCompleterListener());
        this.getCommand("mute").setTabCompleter(new TabCompleterListener());
        this.getCommand("unmute").setTabCompleter(new TabCompleterListener());
        this.getCommand("prefix").setTabCompleter(new TabCompleterListener());
        @SuppressWarnings("unchecked")
		List<String> MuteList = (List<String>) playerMutes.getList("Mutes");
		playerMutes.set("Mutes", MuteList);
		try {
			playerMutes.save(playerMutesFile);
		} catch (IOException ex) {
			
	    }
		fislandsHook = (getServer().getPluginManager().getPlugin("FIslands") != null);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public void crossMessage(Player player, String message, String target) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Message");
		out.writeUTF(target);
		out.writeUTF(message);
		player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
		//this.getLogger().log(Level.INFO, "Cross Message Successfully Sent!");
	}
	
}
