package me.halfquark.halfchat.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("ConfigCommand")
public class ConfigCommand implements ConfigurationSerializable
{
    public ArrayList<String> aliases;
    public String permission;
    public String format;
    public int radius;

    public ConfigCommand(ArrayList<String> aliases, String permission, String format, int radius)
    {
        this.aliases = aliases;
        this.permission = permission;
        this.format = format;
        this.radius = radius;
    }

    @SuppressWarnings("unchecked")
    public ConfigCommand(Map<String, Object> serializedConfigCommand)
    {
    	this.aliases = (ArrayList<String>) serializedConfigCommand.get("aliases");
    	this.permission = (String) serializedConfigCommand.get("permission");
    	this.format = (String) serializedConfigCommand.get("format");
    	this.radius = (int) serializedConfigCommand.get("radius");
    }

    @Override
    public Map<String, Object> serialize()
    {
        Map<String, Object> result = new HashMap<>();
        result.put("aliases", aliases);
        result.put("permission", permission);
        result.put("format", format);
        result.put("radius", radius);
        return result;
    }
}
