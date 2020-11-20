package me.halfquark.halfchat.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TabCompleterListener implements TabCompleter {
	
	@Override
    public List<String> onTabComplete(CommandSender sender, Command cde, String alias, String[] args) {
		if(args.length < 1) {
			List<String> playerNames = new ArrayList<String>();
			for(Player player : Bukkit.getOnlinePlayers()) {
				playerNames.add(player.getName());
			}
			return playerNames;
		}
		List<String> playerNames = new ArrayList<String>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
				playerNames.add(player.getName());
		}
		return playerNames;
    }
	
}
