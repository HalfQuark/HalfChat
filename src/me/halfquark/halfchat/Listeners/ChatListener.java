package me.halfquark.halfchat.Listeners;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.halfquark.halfchat.HalfChat;
import me.halfquark.halfchat.Config.ConfigCommand;

import org.apache.commons.lang.StringUtils;

public class ChatListener implements Listener
{
    private List<ConfigCommand> possibleCommands;
    private List<ConfigCommand> directMessageCommands;
    private String defaultChannel;
    private File playerChannelsFile;
    private File playerDMsFile;
    private FileConfiguration playerChannels;
    private FileConfiguration playerDMs;

    @SuppressWarnings("unchecked")
    public ChatListener(HalfChat pluginInstance)
    {
        this.possibleCommands = (List<ConfigCommand>) pluginInstance.getConfig().getList("commands");
        this.directMessageCommands = (List<ConfigCommand>) pluginInstance.getConfig().getList("direct-message");
        this.defaultChannel = pluginInstance.getConfig().getString("default-channel");
        this.playerChannelsFile = pluginInstance.playerChannelsFile;
        this.playerDMsFile = pluginInstance.playerDMsFile;
        this.playerChannels = pluginInstance.playerChannels;
        this.playerDMs = pluginInstance.playerDMs;
    }

    @EventHandler
    
    public void OnPlayerJoin(PlayerJoinEvent event)
    {
    	playerSetChannel(event.getPlayer(),defaultChannel);
    }
    
    @EventHandler
    
    public void OnPlayerCommand(PlayerCommandPreprocessEvent event)
    {
    	String[] args = event.getMessage().split("\\s+");
    	
    	if(args[0].equalsIgnoreCase("/ch") && args.length == 1) {
    		listChannels(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
    	
    	if(args[0].equalsIgnoreCase("/ch")) {
    	//Change Channel
    		playerSetChannel(event.getPlayer(), Arrays.copyOfRange(args, 1, args.length));
    		event.setCancelled(true);
    		return;
    		
    	}else{
    	//Direct chat
    		for (ConfigCommand command : possibleCommands)
            {
    			for (String alias : command.aliases)
                {

                    if (args[0].equalsIgnoreCase("/" + alias))
                    {
                        if (command.permission == null || command.permission.isEmpty() || event.getPlayer().hasPermission(command.permission))
                        {
                            String joinedMessage = StringUtils.join(args, " ").replace(args[0], "").trim();

                            if (!joinedMessage.isEmpty())
                            {
                                String formattedMessage = formatMessage(event.getPlayer().getDisplayName(), joinedMessage, command.format);
                                SendMessage(formattedMessage, event.getPlayer().getLocation(), command.radius);
                            }
                        }
                        else
                        {
                            event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use that command!");
                        }

                        event.setCancelled(true);
                        return;
                    }
                }
            }
    		//Direct message
    		ConfigCommand command = directMessageCommands.get(0);
        	for (String alias : command.aliases)
        	{
        		if (args[0].equalsIgnoreCase("/" + alias))
        		{
        			if (!(command.permission == null || command.permission.isEmpty() || event.getPlayer().hasPermission(command.permission)))
                    {
        				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use that command!");
        				event.setCancelled(true);
        				return;
                    }
        			if (args.length <= 1)
        	    	{
        				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RED + "You need to specify a player!");
        				event.setCancelled(true);
        				return;
        	    	}
        			if(Bukkit.getServer().getPlayer(args[1]) == null) {
        				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RED + "This player is not online at the moment!");
        				event.setCancelled(true);
        				return;
            		}
        			String joinedMessage = StringUtils.join(args, " ").replace(args[0], "").replace(args[1], "").trim();
        			if (!joinedMessage.isEmpty())
                    {
                        String formattedMessage = formatMessage(event.getPlayer().getDisplayName(), Bukkit.getPlayer(args[1]).getDisplayName(), joinedMessage, command.format);
                        SendMessage(formattedMessage, event.getPlayer().getLocation(), command.radius, args[1]);
                        if(!args[1].equalsIgnoreCase(event.getPlayer().getName())) {
            				event.getPlayer().sendMessage(formattedMessage);
            			}
                        playerDMs.set(event.getPlayer().getName(), args[1]);
                        playerDMs.set(args[1], event.getPlayer().getName());
                		try {
                			playerDMs.save(playerDMsFile);
                		} catch (IOException ex) {
                			
                	    }
                    }
        			event.setCancelled(true);
        		}
        	}
        	//Reply to direct message
        	command = directMessageCommands.get(1);
        	for (String alias : command.aliases)
        	{
        		if (args[0].equalsIgnoreCase("/" + alias))
        		{
        			if (!(command.permission == null || command.permission.isEmpty() || event.getPlayer().hasPermission(command.permission)))
                    {
        				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use that command!");
        				event.setCancelled(true);
        				return;
                    }
        			String targetPlayerName = playerDMs.getString(event.getPlayer().getName());
        			if(targetPlayerName == null) {
        				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RED + "Noone to reply to!");
        				event.setCancelled(true);
        				return;
        			}
        			if(Bukkit.getServer().getPlayer(targetPlayerName) == null) {
        				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RED + "This player is not online at the moment!");
        				event.setCancelled(true);
        				return;
            		}
        			String joinedMessage = StringUtils.join(args, " ").replace(args[0], "").trim();
        			if (!joinedMessage.isEmpty())
                    {
                        String formattedMessage = formatMessage(event.getPlayer().getDisplayName(), Bukkit.getPlayer(targetPlayerName).getDisplayName(), joinedMessage, command.format);
                        SendMessage(formattedMessage, event.getPlayer().getLocation(), command.radius, targetPlayerName);
                        if(!targetPlayerName.equalsIgnoreCase(event.getPlayer().getName())) {
            				event.getPlayer().sendMessage(formattedMessage);
            			}
                    }
        			event.setCancelled(true);
        		}
        	}
    	}
    }

    @EventHandler
    public void OnPlayerChat(AsyncPlayerChatEvent event)
    {
    	//Normal Chat Message
    	String[] channelArgs = playerChannels.getString(event.getPlayer().getName()).split("\\s+");
    	if(channelArgs.length == 0) {
    		event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You need to join a channel first!");
    		return;
    	}
    	for (ConfigCommand command : possibleCommands)
        {
			for (String alias : command.aliases)
            {

                if (channelArgs[0].equalsIgnoreCase(alias))
                {
                	String formattedMessage = formatMessage(event.getPlayer().getDisplayName(), event.getMessage(), command.format);
                    SendMessage(formattedMessage, event.getPlayer().getLocation(), command.radius);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    	//Direct Message Chat
    	ConfigCommand command = directMessageCommands.get(0);
    	for (String alias : command.aliases)
    	{
    		if (channelArgs[0].equalsIgnoreCase(alias))
    		{
    			if (channelArgs.length <= 1)
    	    	{
    				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RED + "This direct message channel does not have a player specified!");
    				event.setCancelled(true);
    				return;
    	    	}
    			if(Bukkit.getServer().getPlayer(channelArgs[1]) == null) {
    				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RED + "This player is not online at the moment!");
    				event.setCancelled(true);
    				return;
        		}
    			String formattedMessage = formatMessage(event.getPlayer().getDisplayName(), Bukkit.getPlayer(channelArgs[1]).getDisplayName(), event.getMessage(), command.format);
    			SendMessage(formattedMessage, event.getPlayer().getLocation(), command.radius, channelArgs[1]);
    			if(!channelArgs[1].equalsIgnoreCase(event.getPlayer().getName())) {
    				event.getPlayer().sendMessage(formattedMessage);
    			}
    			event.setCancelled(true);
    			return;
    		}
    	}
    }

    private String formatMessage(String name, String message, String format)
    {
        String formattedMessage = format;

        formattedMessage = formattedMessage.replace("<name>", name);
        formattedMessage = formattedMessage.replace("<message>", message);
        formattedMessage = ChatColor.translateAlternateColorCodes('&', formattedMessage);

        return formattedMessage;
    }
    private String formatMessage(String name1, String name2, String message, String format)
    {
        String formattedMessage = format;

        formattedMessage = formattedMessage.replace("<name1>", name1);
        formattedMessage = formattedMessage.replace("<name2>", name2);
        formattedMessage = formattedMessage.replace("<message>", message);
        formattedMessage = ChatColor.translateAlternateColorCodes('&', formattedMessage);

        return formattedMessage;
    }

    private void SendMessage(String message, Location location, int radius)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (radius <= 0)
            {
                player.sendMessage(message);
            }
            else
            {
                double distance = location.distanceSquared(player.getLocation());
                if (distance <= Math.pow(radius, 2))
                {
                    player.sendMessage(message);
                }
            }
        }
    }
    
    private void SendMessage(String message, Location location, int radius, String targetPlayerName)
    {
    	Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (radius <= 0)
            {
            	targetPlayer.sendMessage(message);
            }
            else
            {
                double distance = location.distanceSquared(targetPlayer.getLocation());
                if (distance <= Math.pow(radius, 2))
                {
                	targetPlayer.sendMessage(message);
                }
            }
    }
    
    private boolean playerSetChannel(Player player, String ...args )
    {
    	//Normal Channels
    	for (ConfigCommand command : possibleCommands)
        {
    		String commandName = command.aliases.get(0);
			for (String alias : command.aliases)
            {
                if (args[0].equalsIgnoreCase(alias))
                {
                	if (command.permission == null || command.permission.isEmpty() || player.hasPermission(command.permission))
                    {
                		playerChannels.set(player.getName(), commandName);
                		try {
                			playerChannels.save(playerChannelsFile);
                		} catch (IOException ex) {
                	        
                	    }
                		player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.GOLD + "Talking to: " + ChatColor.WHITE + commandName);
                        return true;
                    }else{
                    	player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RED + "You don't have permission to use that command!");
                    	return false;
                    }
                }
            }
        }
    	//Direct Message Channels
		String commandName = directMessageCommands.get(0).aliases.get(0);
		for (String alias : directMessageCommands.get(0).aliases)
    	{
    		if (args[0].equalsIgnoreCase(alias))
    		{
    			if (args.length <= 1)
    	    	{
    				player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RED + "You need to specify a player!");
    	    		return false;
    	    	}
    			if(Bukkit.getServer().getPlayer(args[1]) == null) {
        			player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RED + "This player is not online at the moment!");
        			return false;
        		}
    			playerChannels.set(player.getName(), commandName + " " + args[1]);
        		try {
        			playerChannels.save(playerChannelsFile);
        		} catch (IOException ex) {
        			
        	    }
        		player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.GOLD + "Talking to: " + ChatColor.WHITE + args[1]);
        		return true;
    		}
    	}
    	listChannels(player);
    	return false;
    }
    
    private void listChannels(Player player)
    {
    	player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.GOLD + "Channels:");
		for (ConfigCommand command : possibleCommands)
        {
            player.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + command.aliases.get(0));
        }
		player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.GOLD + "Direct messages:");
		for (ConfigCommand command : directMessageCommands)
        {
            player.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + command.aliases.get(0));
        }
    }
}
