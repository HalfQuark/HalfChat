package me.halfquark.halfchat;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import me.halfquark.halfchat.Config.ConfigCommand;
import me.halfquark.halfchat.Listeners.ChatListener;

public class HalfChat extends JavaPlugin {
	
	public File playerChannelsFile = new File(getDataFolder(), "playerChannels.yml");
	public File playerDMsFile = new File(getDataFolder(), "playerDMs.yml");
	public FileConfiguration playerChannels = YamlConfiguration.loadConfiguration(playerChannelsFile);
	public FileConfiguration playerDMs = YamlConfiguration.loadConfiguration(playerDMsFile);
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		ConfigurationSerialization.registerClass(ConfigCommand.class);
		getServer().getPluginManager().registerEvents(new ChatListener(this), this);
		getConfig().options().copyDefaults(true);
	}
	
	@Override
	public void onDisable() {
		
	}
}
