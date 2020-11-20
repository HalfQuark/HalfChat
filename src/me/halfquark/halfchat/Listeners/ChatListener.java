package me.halfquark.halfchat.Listeners;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.halfquark.halfchat.HalfChat;
import me.halfquark.halfchat.Config.ChatCommand;

import org.apache.commons.lang.StringUtils;

public class ChatListener implements Listener
{
	private HalfChat plugin;
    private List<ChatCommand> possibleCommands;
    private List<ChatCommand> directMessageCommands;
    private ChatCommand partyCommand;
    private String defaultChannel;
    private File playerChannelsFile;
    private File playerDMsFile;
    private File playerColorsFile;
    private File playerMutesFile;
    private File partyFile;
    private File prefixFile;
    private FileConfiguration playerChannels;
    private FileConfiguration playerDMs;
    private FileConfiguration playerColors;
    private FileConfiguration playerMutes;
    private FileConfiguration party;
    private FileConfiguration prefix;
    private List<String> colors;
    //private String defaultColor;

    @SuppressWarnings("unchecked")
    public ChatListener(HalfChat pluginInstance)
    {
    	this.plugin = pluginInstance;
        this.possibleCommands = (List<ChatCommand>) pluginInstance.getConfig().getList("commands");
        this.directMessageCommands = (List<ChatCommand>) pluginInstance.getConfig().getList("direct-message");
        this.partyCommand = (ChatCommand) plugin.getConfig().getList("party-chat").get(0);
        this.defaultChannel = pluginInstance.getConfig().getString("default-channel");
        this.playerChannelsFile = pluginInstance.playerChannelsFile;
        this.playerDMsFile = pluginInstance.playerDMsFile;
        this.playerColorsFile = pluginInstance.playerColorsFile;
        this.playerMutesFile = pluginInstance.playerMutesFile;
        this.partyFile = pluginInstance.partyFile;
        this.prefixFile = pluginInstance.prefixFile;
        this.playerChannels = pluginInstance.playerChannels;
        this.playerDMs = pluginInstance.playerDMs;
        this.playerColors = pluginInstance.playerColors;
        this.playerMutes = pluginInstance.playerMutes;
        this.party = pluginInstance.party;
        this.prefix = pluginInstance.prefix;
        this.colors = (List<String>) pluginInstance.getConfig().getList("colors");
        //this.defaultColor = pluginInstance.getConfig().getString("default-color");
    }

    @EventHandler
    
    public void OnPlayerJoin(PlayerJoinEvent event)
    {
    	playerSetChannel(event.getPlayer(),defaultChannel);
    	if(prefix.getString(event.getPlayer().getName()) == null) {
    		prefix.set(event.getPlayer().getName(), plugin.getConfig().get("default-prefix"));
    		try {
    			prefix.save(prefixFile);
    		} catch (IOException ex) {
    			
    	    }
    	}
    }
    
    @EventHandler
    
    public void OnPlayerCommand(PlayerCommandPreprocessEvent event)
    {
    	String[] args = event.getMessage().split("\\s+");
    	
    	/*if(args[0].equalsIgnoreCase("/nick") && args.length == 1) {
    		nickHelp(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
    	
    	if(args[0].equalsIgnoreCase("/nick")) {
    		if(args[1].equalsIgnoreCase("set")) {
    			if(args.length == 3) {
    				if(!event.getPlayer().hasPermission("halfchat.nickSet")) {
    					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use that command!");
    					event.setCancelled(true);
        	    		return;
    				}
    				String formattedNick = ChatColor.translateAlternateColorCodes('&', args[2]);
    				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "Your nick is now: " + formattedNick);
    				
    				event.getPlayer().setDisplayName(formattedNick);
    				event.setCancelled(true);
    	    		return;
    			}
    			if(args.length == 4) {
    				if(!event.getPlayer().hasPermission("halfchat.nickSetAll")) {
    					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use that command!");
    					event.setCancelled(true);
        	    		return;
    				}
    				if(Bukkit.getServer().getPlayer(args[2]) == null) {
    					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "This player is not online at the moment!");
    					event.setCancelled(true);
        	    		return;
    				}
    				String formattedNick = ChatColor.translateAlternateColorCodes('&', args[3]);
    				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "Player's nick is now: " + formattedNick);
    				
    				Bukkit.getServer().getPlayer(args[2]).setDisplayName(formattedNick);
    				event.setCancelled(true);
    	    		return;
    			}
    		}
    	}*/
    	
    	if(args[0].equalsIgnoreCase("/prefix")) {
    		if(!event.getPlayer().hasPermission("halfchat.prefix")) {
    			event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use that command!");
				event.setCancelled(true);
				return;
    		}
    		if(args.length != 3) {
    			event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "Please specify a player and a prefix!");
    			event.setCancelled(true);
    			return;
    		}
    		prefix.set(args[1], args[2]);
    		try {
    			prefix.save(prefixFile);
    		} catch (IOException ex) {
    			
    	    }
    		event.setCancelled(true);
			return;
    	}
    	
    	if(args[0].equalsIgnoreCase("/mute")) {
    		if(!event.getPlayer().hasPermission("halfchat.mute")) {
    			event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use that command!");
    			event.setCancelled(true);
    			return;
    		}
    		if(args.length < 4) {
    			event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "Please specify a player, a duration and a reason");
    			event.setCancelled(true);
    			return;
    		}
    		if(toMillis(args[2]) == -1) {
    			event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "Wrong time format: 1w1d1h1m1s");
    			event.setCancelled(true);
    			return;
    		}
    		playerMutes.set(args[1], System.currentTimeMillis() + toMillis(args[2]));
			/*List<String> MuteList = (List<String>) playerMutes.getList("Mutes");
    		if(MuteList == null) {
    			MuteList = Arrays.asList(args[1]);
    		} else {
    			MuteList.add(args[1]);
    		}
    		playerMutes.set("Mutes", MuteList);*/
    		String message = ChatColor.YELLOW + "[C/2]" + ChatColor.RESET + Bukkit.getPlayer(args[1]).getDisplayName() + ChatColor.RED +
    				" has been muted by " + ChatColor.RESET + event.getPlayer().getDisplayName() + ChatColor.RED +
    				" for: " + ChatColor.RESET + args[2] + ChatColor.RED + " for reason: " + ChatColor.RESET + StringUtils.join(args, " ").replace(args[0], "").replace(args[1], "").replace(args[2], "").trim();
    		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    		Bukkit.getServer().getConsoleSender().sendMessage("[" + format.format(System.currentTimeMillis()) + "] " + message);
    		plugin.crossMessage(event.getPlayer(), message, "ALL");
    		try {
    			playerMutes.save(playerMutesFile);
    		} catch (IOException ex) {
    			
    	    }
    		event.setCancelled(true);
    		return;
    	}
    	
    	if(args[0].equalsIgnoreCase("/unmute")) {
    		if(!event.getPlayer().hasPermission("halfchat.unmute")) {
    			event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use that command!");
    			event.setCancelled(true);
    			return;
    		}
    		if(args.length < 2) {
    			event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "Please specify a player");
    			event.setCancelled(true);
    			return;
    		}
    		/*@SuppressWarnings("unchecked")
			List<String> MuteList = (List<String>) playerMutes.getList("Mutes");
    		MuteList.remove(args[1]);*/
    		playerMutes.set(args[1], 0);
    		try {
    			playerMutes.save(playerMutesFile);
    		} catch (IOException ex) {
    			
    	    }
    		event.setCancelled(true);
    		return;
    	}
    	
    	for(String alias : partyCommand.aliases) {
    		if(args[0].equalsIgnoreCase("/" + alias)) {
    			if(args.length == 1) {
    				event.setCancelled(true);
    	    		return;
    			}
    			String partyString;
    			String[] partyMembers;
    			if(party.getString(event.getPlayer().getName()) == null) {
    				party.set(event.getPlayer().getName(), "");
    				try {
    	    			party.save(partyFile);
    	    		} catch (IOException ex) {
    	    			
    	    	    }
    			}
    			switch(args[1]) {
    			case "create":
    				if(!party.getString(event.getPlayer().getName()).isEmpty()) {
    					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You have to leave your party first!");
    					break;
    				}
    				party.set(event.getPlayer().getName(), event.getPlayer().getName());
    	    		try {
    	    			party.save(partyFile);
    	    		} catch (IOException ex) {
    	    			
    	    	    }
    				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.DARK_GREEN + "Party created successfully! Invite more players with /" + partyCommand.aliases.get(0) + " add");
    				break;
    			case "add":
    				if(party.getString(event.getPlayer().getName()).isEmpty()) {
    					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You have to be on a party!");
    					break;
    				}
    				if(args.length < 3) {
    					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "Please specify a player!");
    					break;
    				}
    				if(Bukkit.getServer().getPlayer(args[2]) == null) {
    					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "This player is not online at the moment!");
    					break;
    				}
    				if(party.getString(Bukkit.getServer().getPlayer(args[2]).getName()) != null) {
    					if(!party.getString(args[2]).isEmpty()) {
	    					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "This player is already on a party!");
	    					break;
    					}
    				}
    				partyString = party.getString(event.getPlayer().getName());
    				if(partyString.contains(Bukkit.getServer().getPlayer(args[2]).getName())) {
    					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "This player is already on the party!");
    					break;
    				}
    				partyString += "," + Bukkit.getServer().getPlayer(args[2]).getName();
    				partyMembers = partyString.split(",");
    				for(String playerName : partyMembers) {
    					party.set(playerName, partyString);
    					if(Bukkit.getServer().getPlayer(playerName) == null)
    						continue;
    					Bukkit.getServer().getPlayer(playerName).sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RESET + Bukkit.getServer().getPlayer(args[2]).getDisplayName() + ChatColor.DARK_GREEN + " has joined the party");
    				}
    				party.set(Bukkit.getServer().getPlayer(args[2]).getName(), partyString);
    				try {
    	    			party.save(partyFile);
    	    		} catch (IOException ex) {
    	    			
    	    	    }
    				break;
    			case "leave":
    				if(party.getString(event.getPlayer().getName()).isEmpty()) {
    					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You have to be on a party!");
    					break;
    				}
    				partyString = party.getString(event.getPlayer().getName());
    				partyMembers = partyString.split(",");
    				List<String> partyList = new LinkedList<String>(Arrays.asList(partyMembers));
    				partyList.remove(event.getPlayer().getName());
    				if(partyList.isEmpty())
    					partyString = "";
    				else
    					partyString = StringUtils.join(partyList.toArray(), ",");
    				for(String playerName : partyMembers) {
    					party.set(playerName, partyString);
    					if(Bukkit.getServer().getPlayer(playerName) == null)
    						continue;
    					Bukkit.getServer().getPlayer(playerName).sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RESET + event.getPlayer().getDisplayName() + ChatColor.DARK_GREEN + " has left the party");
    				}
    				party.set(event.getPlayer().getName(), "");
    				try {
    	    			party.save(partyFile);
    	    		} catch (IOException ex) {
    	    			
    	    	    }
    				break;
    			case "list":
    				if(party.getString(event.getPlayer().getName()).isEmpty()) {
    					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You have to be on a party!");
    					break;
    				}
    				partyString = party.getString(event.getPlayer().getName());
    				partyMembers = partyString.split(",");
    				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.DARK_GREEN + "Online party members:");
    				for(String playerName : partyMembers) {
    					if(Bukkit.getServer().getPlayer(playerName) == null)
    						continue;
    					event.getPlayer().sendMessage("    " + Bukkit.getServer().getPlayer(playerName).getDisplayName());
    				}
    				break;
    			}
    			event.setCancelled(true);
	    		return;
    		}
    	}
    	
    	if(args[0].equalsIgnoreCase("/cc") && args.length == 1) {
    		listColors(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
    	
    	if(args[0].equalsIgnoreCase("/cc") && args.length == 2) {
    		if(!event.getPlayer().hasPermission("halfchat.setColor")) {
    			event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use that command!");
    			event.setCancelled(true);
    			return;
    		}
    		String[] chatColors = args[1].split(",");
    		String playerColor = "";
    		for(String chatColor : chatColors) {
    			chatColor = getColorName(chatColor);
				if(event.getPlayer().hasPermission("halfchat.setColor".concat(chatColor))) {
					playerColor = playerColor.concat(getColorCode(chatColor));
				} else {
					event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use the color: " + ChatColor.RESET + chatColor);
					event.setCancelled(true);
					return;
				}
    		}
    		playerSetColor(event.getPlayer(), playerColor);
    		event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.GOLD + "Your chat color has been changed");
    		event.setCancelled(true);
			return;
    	}
    	
    	if(args[0].equalsIgnoreCase("/cc") && args.length == 3) {
    		if(!event.getPlayer().hasPermission("halfchat.setPlayerColor")) {
    			event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use that command!");
    			event.setCancelled(true);
    			return;
    		}
    		if(Bukkit.getPlayer(args[1]) == null) {
    			event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "This player is not online at the moment!");
    			event.setCancelled(true);
    			return;
    		}
    		String[] chatColors = args[2].split(",");
    		String playerColor = "";
    		for(String chatColor : chatColors) {
				if(event.getPlayer().hasPermission("halfchat.setColor".concat(chatColor))) {
					playerColor = playerColor.concat(getColorCode(chatColor));
				} 
    		}
    		playerSetColor(Bukkit.getPlayer(args[1]), playerColor);
    		event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.GOLD + "Chat color has been changed for: " + ChatColor.RESET + Bukkit.getPlayer(args[1]).getDisplayName());
    		event.setCancelled(true);
			return;
    	}
    	
    	if(args[0].equalsIgnoreCase("/ch") && args.length == 1) {
    		listChannels(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
    	
    	if(args[0].equalsIgnoreCase("/ch")) {
    	//Change Channel
    		if(args.length > 2) {
    			if(Bukkit.getServer().getPlayer(args[2]) == null) {
    				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "This player is not online at the moment!");
    	    		event.setCancelled(true);
    				return;
    			}
    			args[2] = Bukkit.getServer().getPlayer(args[2]).getName();
    		}
    		playerSetChannel(event.getPlayer(), Arrays.copyOfRange(args, 1, args.length));
    		event.setCancelled(true);
    		return;
    		
    	}else{
    	//Direct chat
    		for (ChatCommand command : possibleCommands)
            {
    			for (String alias : command.aliases)
                {

                    if (args[0].equalsIgnoreCase("/" + alias))
                    {
                        if (command.permission == null || command.permission.isEmpty() || event.getPlayer().hasPermission(command.permission))
                        {
                            String joinedMessage = StringUtils.join(args, " ").replaceFirst(args[0], "").trim();

                            if (!joinedMessage.isEmpty())
                            {
                                String formattedMessage = formatMessage(prefix.get(event.getPlayer().getName()) + event.getPlayer().getDisplayName(), joinedMessage, command.format, event.getPlayer());
                                SendMessage(event.getPlayer(), formattedMessage, event.getPlayer().getLocation(), command.radius, command.permission);
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
    		ChatCommand command = directMessageCommands.get(0);
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
        			String joinedMessage = StringUtils.join(args, " ").replaceFirst(args[0], "").replaceFirst(args[1], "").trim();
        			args[1] = Bukkit.getServer().getPlayer(args[1]).getName();
        			if (!joinedMessage.isEmpty())
                    {
                        String formattedMessage = formatMessage(prefix.get(event.getPlayer().getName()) + event.getPlayer().getDisplayName(), Bukkit.getPlayer(args[1]).getDisplayName(), joinedMessage, command.format, event.getPlayer());
                        SendMessage(event.getPlayer(), formattedMessage, event.getPlayer().getLocation(), command.radius, command.permission, args[1]);
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
                        String formattedMessage = formatMessage(prefix.get(event.getPlayer().getName()) + event.getPlayer().getDisplayName(), Bukkit.getPlayer(targetPlayerName).getDisplayName(), joinedMessage, command.format, event.getPlayer());
                        SendMessage(event.getPlayer(), formattedMessage, event.getPlayer().getLocation(), command.radius, command.permission, targetPlayerName);
                        if(!targetPlayerName.equalsIgnoreCase(event.getPlayer().getName())) {
            				event.getPlayer().sendMessage(formattedMessage);
            			}
                        playerDMs.set(targetPlayerName, event.getPlayer().getName());
                		try {
                			playerDMs.save(playerDMsFile);
                		} catch (IOException ex) {
                			
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
    	for (ChatCommand command : possibleCommands)
        {
			for (String alias : command.aliases)
            {

                if (channelArgs[0].equalsIgnoreCase(alias))
                {
                	String formattedMessage = formatMessage(prefix.get(event.getPlayer().getName()) + event.getPlayer().getDisplayName(), event.getMessage(), command.format, event.getPlayer());
                    SendMessage(event.getPlayer(), formattedMessage, event.getPlayer().getLocation(), command.radius, command.permission);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    	//Direct Message Chat
    	ChatCommand command = directMessageCommands.get(0);
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
    			String formattedMessage = formatMessage(prefix.get(event.getPlayer().getName()) + event.getPlayer().getDisplayName(), Bukkit.getPlayer(channelArgs[1]).getDisplayName(), event.getMessage(), command.format, event.getPlayer());
    			SendMessage(event.getPlayer(), formattedMessage, event.getPlayer().getLocation(), command.radius, command.permission, channelArgs[1]);
    			if(!channelArgs[1].equalsIgnoreCase(event.getPlayer().getName())) {
    				event.getPlayer().sendMessage(formattedMessage);
    			}
    			playerDMs.set(channelArgs[1], event.getPlayer().getName());
        		try {
        			playerDMs.save(playerDMsFile);
        		} catch (IOException ex) {
        			
        	    }
    			event.setCancelled(true);
    			return;
    		}
    	}
    	command = partyCommand;
    	for (String alias : command.aliases)
    	{
    		if (channelArgs[0].equalsIgnoreCase(alias))
    		{
    			if (!(command.permission == null || command.permission.isEmpty() || event.getPlayer().hasPermission(command.permission)))
                {
    				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2]" + ChatColor.RED + "You don't have permission to use that command!");
    				event.setCancelled(true);
    				return;
                }
    			if(party.getString(event.getPlayer().getName()).isEmpty()) {
    				event.getPlayer().sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RED + "You haven't joined a party!");
    				event.setCancelled(true);
    				return;
    			}
    			String[] targetList = party.getString(event.getPlayer().getName()).split(",");
    			for(String playerName : targetList) {
        			if(Bukkit.getServer().getPlayer(playerName) == null) {
        				continue;
            		}
                    String formattedMessage = formatMessage(prefix.get(event.getPlayer().getName()) + event.getPlayer().getDisplayName(), event.getMessage(), command.format, event.getPlayer());
                    SendMessage(event.getPlayer(), formattedMessage, event.getPlayer().getLocation(), command.radius, command.permission, playerName);
    			}
    			event.setCancelled(true);
    			return;
    		}
    	}
    }

    private String formatMessage(String name, String message, String format, Player sender)
    {
        return formatMessage(name, "", message, format, sender);
    }
    private String formatMessage(String name1, String name2, String message, String format, Player sender)
    {
        String formattedMessage = format;

        if(!name2.equals("") && !(name2 == null)) {
        	formattedMessage = formattedMessage.replaceFirst("<name1>", name1);
        	formattedMessage = formattedMessage.replaceFirst("<name2>", name2);
        } else {
        	formattedMessage = formattedMessage.replaceFirst("<name>", name1);
        }
        if(playerColors.getString(sender.getName()) != null)
        	formattedMessage = formattedMessage.replaceFirst("<message>", playerColors.getString(sender.getName()) + "<message>");
        formattedMessage = ChatColor.translateAlternateColorCodes('&', formattedMessage);
        formattedMessage = formattedMessage.replaceFirst("<message>", message);
        if(sender.hasPermission("halfchat.useColor")) {
        	for(int i = 0; i + 1 < colors.size(); i+=2) {
        		formattedMessage = formattedMessage.replaceAll((String)"%"+colors.get(i), (String)colors.get(i + 1));
        	}
        	formattedMessage = ChatColor.translateAlternateColorCodes('&', formattedMessage);
        }
        
        return formattedMessage;
    }

    
    //Am fixing cross server chat kek xD VENTOX COME LMAO ye ima now fix the tab thing
    
    private void SendMessage(Player sender, String message, Location location, int radius, String permissions)
    {
        SendMessage(sender, message, location, radius, permissions, "");
    }
    
    private void SendMessage(Player sender, String message, Location location, int radius, String permissions, String targetPlayerName)
    {
    	if(message.toLowerCase().contains("owo") || message.toLowerCase().contains("uwu")) {
    		message = message.replace("r", "w");
    		message = message.replace("l", "w");
    		message = message.replace("i", "iw");
    		message = message.replace("ns", "wns");
    		message = message.replace("ps", "wps");
    		message = message.replace("ms", "wms");
    		message = message.replace("vs", "wvs");
    		message = message.replace("R", "W");
    		message = message.replace("L", "W");
    		message = message.replace("I", "IW");
    		message = message.replace("NS", "WNS");
    		message = message.replace("PS", "WPS");
    		message = message.replace("MS", "WMS");
    		message = message.replace("VS", "WVS");
    	}
    	long muteTime = playerMutes.getLong(sender.getName());
    	/*if(MuteList != null)
	    	if(MuteList.contains(sender.getName()))
	    		return;*/
    	if(System.currentTimeMillis() < muteTime)
    		return;
    	SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    	Bukkit.getServer().getConsoleSender().sendMessage("[" + format.format(System.currentTimeMillis()) + "] " + message);
    	if(radius < -1) {
			plugin.crossMessage(sender, message, "ALL");
		}
    	for(Player player: Bukkit.getOnlinePlayers()) {
    		if(player.getName().equals(targetPlayerName) || targetPlayerName.equals("") || targetPlayerName == null) {
    			if(!permissions.equals("") && permissions != null)
    				if(!player.hasPermission(permissions))
    					continue;
    			boolean ping = false;
    			if(message.contains("@"+ player.getName()) || message.contains("@" + player.getDisplayName()) || message.contains("@" + player.getPlayerListName()) || message.contains("@Everyone")) {
    				ping = true;
    			}
    			if(radius < -1) {
	    			if(ping) {
	    				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
	    			}
	    			continue;
    			}
		        if (radius < 0)
		        {
		        	
		        	if(ping) {
		        		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
		        		player.sendMessage(ChatColor.DARK_RED + "[!] " + ChatColor.RESET  + message);
		        	} else {
		        		player.sendMessage(message);
		        	}
		        }
		        else
		        {
		        	try {
			            double distance = location.distanceSquared(player.getLocation());
			            if (distance <= Math.pow(radius, 2))
			            {
			            	if(ping) {
			            		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
				        		player.sendMessage(ChatColor.DARK_RED + "[!] " + ChatColor.RESET  + message);
				        	} else {
				        		player.sendMessage(message);
				        	}
			            }
		        	} catch (NullPointerException | IllegalArgumentException e) {
		                continue;
		            }
		        }
    		}
    	}
    }
    
    private boolean playerSetChannel(Player player, String ...args )
    {
    	//Normal Channels
    	for (ChatCommand command : possibleCommands)
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
		for (String alias : partyCommand.aliases)
    	{
    		if (args[0].equalsIgnoreCase(alias))
    		{
    			String partyMembers = party.getString(player.getName());
    			if(partyMembers == null || partyMembers == "") {
        			player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.RED + "You need to join a party!");
        			return false;
        		}
    			playerChannels.set(player.getName(), partyCommand.aliases.get(0));
        		try {
        			playerChannels.save(playerChannelsFile);
        		} catch (IOException ex) {
        			
        	    }
        		player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.GOLD + "Talking to: " + ChatColor.WHITE + partyCommand.aliases.get(0));
        		return true;
    		}
    	}
    	listChannels(player);
    	return false;
    }
    
    private void listChannels(Player player)
    {
    	player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.GOLD + "Channels:");
		for (ChatCommand command : possibleCommands)
        {
			if(player.hasPermission(command.permission) || command.permission.equals("") || command.permission == null)
				player.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + command.aliases.get(0));
        }
		player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.GOLD + "Direct messages:");
		for (ChatCommand command : directMessageCommands)
        {
            player.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + command.aliases.get(0));
        }
		player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.GOLD + "Party chat:");
		player.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GOLD + partyCommand.aliases.get(0));
    }
    
    private void listColors(Player player)
    {
    	player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.GOLD + "Colors:");
    	for(int i = 0; i + 1 < colors.size(); i += 2) {
    		if(player.hasPermission("halfchat.setColor" + colors.get(i))) {
	    		String color = colors.get(i + 1) + colors.get(i);
	    		color = ChatColor.translateAlternateColorCodes('&', color);
	    		color = color + "(" + colors.get(i + 1) + ")";
	    		player.sendMessage("   " + color);
    		}
    	}
    }
    
    /*private void nickHelp(Player player)
    {
    	player.sendMessage(ChatColor.YELLOW + "[C/2] " + ChatColor.GOLD + "Nick:");
    	player.sendMessage("   " + ChatColor.GOLD + "set");
    }*/
    private void playerSetColor(Player player, String color)
    {
    	playerColors.set(player.getName(), color);
		try {
			playerColors.save(playerColorsFile);
		} catch (IOException ex) {
			
	    }
    }
    
    private String getColorCode(String color)
    {
    	for(int i = 0; i + 1 < colors.size(); i+=2) {
    		if(colors.get(i).equalsIgnoreCase(color) || colors.get(i + 1).equals(color))
    			return colors.get(i + 1);
    	}
    	return "";
    }
    
    private String getColorName(String color)
    {
    	for(int i = 0; i + 1 < colors.size(); i+=2) {
    		if(colors.get(i).equalsIgnoreCase(color) || colors.get(i + 1).equals(color))
    			return colors.get(i);
    	}
    	return "";
    }
    
    private long toMillis(String time) {
    	time = time.replace("w", "w ");
    	time = time.replace("d", "d ");
    	time = time.replace("h", "h ");
    	time = time.replace("m", "m ");
    	time = time.replace("s", "s ");
    	String[] args = time.split(" ");
    	long result = 0;
    	for(String arg : args) {
    		long mult = 1;
    		switch(arg.charAt(arg.length() - 1)) {
    		case 'w':
    			mult *= 7;
    		case 'd':
    			mult *= 24;
    		case 'h':
    			mult *= 60;
    		case 'm':
    			mult *= 60;
    		case 's':
    			mult *= 1000;
    			break;
    		default:
    			return -1;
    		}
    		arg = arg.substring(0, arg.length() - 1);
    		try {
                long argInt = (long)Integer.parseInt(arg);
                result += argInt * mult;
            } catch (NumberFormatException e) {
            	return -1;
            }
    	}
    	return result;
    }
    
}
