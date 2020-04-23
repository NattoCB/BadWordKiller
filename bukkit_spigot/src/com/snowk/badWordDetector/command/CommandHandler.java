package com.snowk.badWordDetector.command;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.snowk.badWordDetector.BadWordDetector;
import com.snowk.badWordDetector.command.CommandFramework;
import com.snowk.badWordDetector.config.ConfigHandler;

public class CommandHandler extends CommandFramework {
    
	public CommandHandler(String label) {
        super(label);
    }
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player p = (Player) sender;
		
		if (label.equalsIgnoreCase("badwordkiller") || label.equalsIgnoreCase("bwk")) {
    		if (args.length == 0){
            	p.sendMessage("¡ìe======= BadWordKiller By:Bear ========");
            	p.sendMessage("¡ìa/bwk test  ¡ì3- show this page");
            	p.sendMessage("¡ìa/bwk help  ¡ì3- test your sentence [disable when calling twice]");
            	p.sendMessage("¡ìa/bwk reload  ¡ì3- reload the plugin");
    		}
    		if (args.length == 1) {
    			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
                	p.sendMessage("¡ìe======= BadWordKiller By:Bear ========");
                	p.sendMessage("¡ìa/bwk test  ¡ì3- show this page");
                	p.sendMessage("¡ìa/bwk help  ¡ì3- test your sentence [disable when calling twice]");
                	p.sendMessage("¡ìa/bwk reload  ¡ì3- reload the plugin");
        		}
        		if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
                    if (p.hasPermission("snowk.bwk.admin") || p.isOp()) {
                    	doReload();
                    	p.sendMessage(ConfigHandler.msg_Reload);
                    } else {
                    	p.sendMessage(ConfigHandler.msg_NoPerm);
                    }
                }
    		}
    	}
	}
	
	protected void doReload() {
        if (!new File("./plugins/BadWordKiller/config.yml").exists()) {
        	BadWordDetector.snowkPlugin.saveDefaultConfig();
        	BadWordDetector.snowkPlugin.getLogger().info("BadWordKiller[BWkiller] successfully created config file");
        }	
    	BadWordDetector.snowkPlugin.reloadConfig();
    	ConfigHandler.replaceEnable = BadWordDetector.snowkPlugin.getConfig().getBoolean("replace");
    	ConfigHandler.maskSymbol = BadWordDetector.snowkPlugin.getConfig().getString("mask");
    	ConfigHandler.msg_Reject = BadWordDetector.snowkPlugin.getConfig().getString("Msg_Reject").replace("&", "¡ì");
    	ConfigHandler.msg_Reload = BadWordDetector.snowkPlugin.getConfig().getString("Msg_Reload").replace("&", "¡ì");
    	ConfigHandler.msg_NoPerm = BadWordDetector.snowkPlugin.getConfig().getString("Msg_NoPerm").replace("&", "¡ì");
	}
}
