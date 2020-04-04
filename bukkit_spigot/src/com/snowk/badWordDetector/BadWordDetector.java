package com.snowk.badWordDetector;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.snowk.badWordDetector.listener.ChatListener;


public class BadWordDetector extends JavaPlugin{
	
	public static BadWordDetector snowkPlugin;

    @Override
    public void onEnable() {
    	snowkPlugin = this;
    	getLogger().info("BadWordKiller[BWkiller] successfully enabled! - By:Bear");
    	getLogger().info("See more information at: https://github.com/i493052739");
    	registerListeners();
    	
        if (!new File("./plugins/BadWordKiller/config.yml").exists()) {
            this.saveDefaultConfig();
            this.getLogger().info("BadWordKiller[BWkiller] successfully created config file");
        }
    }
    
    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
    }

    @Override
    public void onDisable() {
    	getLogger().info("BadWordKiller[BWkiller] successfully disabled!");
    	
    }
    
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args) {
    	final Player p = (Player)commandSender;
    	if (label.equalsIgnoreCase("bwkiller") || label.equalsIgnoreCase("bwk")) {
            if (args.length == 0) {
            	p.sendMessage("¡ìe======= BadWordKiller By:Bear ========");
            	p.sendMessage("¡ìa/bwk reload  ¡ì3- reload the plugin");
            }
    		
    		if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (p.hasPermission("snowk.bwk.admin") || p.isOp()) {
                	this.reloadConfig();
                	Message.msg_Reject = BadWordDetector.snowkPlugin.getConfig().getString("Msg_Reject").replace("&", "¡ì");
                	Message.msg_Reload = BadWordDetector.snowkPlugin.getConfig().getString("Msg_Reload").replace("&", "¡ì");
                	Message.msg_NoPerm = BadWordDetector.snowkPlugin.getConfig().getString("Msg_NoPerm").replace("&", "¡ì");
                	p.sendMessage(Message.msg_Reload);
                } else {
                	p.sendMessage(Message.msg_NoPerm);
                }
                return true;
            }
    	}
    	return false;
    }
}
