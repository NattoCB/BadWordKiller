package com.snowk.badWordDetector.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;


public abstract class CommandFramework implements CommandExecutor {
	
	private String label;
	public abstract void execute(CommandSender commandSender, String str, String[] strArr);
	
    public CommandFramework(String label) {
        this.label = label;
    }

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            execute(sender, label, args);
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
            return true;
        }
	}
	
    public static boolean register(JavaPlugin plugin, CommandFramework command) {
        PluginCommand pluginCommand = plugin.getCommand(command.label);
        if (pluginCommand == null) {
            return false;
        }
        pluginCommand.setExecutor(command);
        return true;
    }

}
