package com.snowk.badWordDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.snowk.badWordDetector.listener.ChatListener;
import com.snowk.badWordDetector.command.CommandFramework;
import com.snowk.badWordDetector.command.CommandHandler;

public class BadWordDetector extends JavaPlugin {
	
	public static BadWordDetector snowkPlugin;
	public static List<String> banCharList = new ArrayList<String>();
	
    @Override
    @SuppressWarnings("resource")
    public void onEnable() {
    	
    	// this
    	snowkPlugin = this;
    	
    	// configurations
        if (!new File("./plugins/BadWordKiller/config.yml").exists()) {
            this.saveDefaultConfig();
            this.getLogger().info("BadWordKiller[BWkiller] successfully created config file");
        }	
        
        // commands
        CommandFramework.register(this, new CommandHandler("badwordkiller"));
    	CommandFramework.register(this, new CommandHandler("bwk"));
    	
    	// listeners
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

        // loggers
    	getLogger().info("===================================================================");
    	getLogger().info("  ____            ___        __            _ _  ___ _ _           ");
    	getLogger().info(" | __ )  __ _  __| \\ \\      / /__  _ __ __| | |/ (_) | | ___ _ __ ");
    	getLogger().info(" |  _ \\ / _` |/ _` |\\ \\ /\\ / / _ \\| '__/ _` | ' /| | | |/ _ \\ '__|");
    	getLogger().info(" | |_) | (_| | (_| | \\ V  V / (_) | | | (_| | . \\| | | |  __/ |   ");
    	getLogger().info(" |____/ \\__,_|\\__,_|  \\_/\\_/ \\___/|_|  \\__,_|_|\\_\\_|_|_|\\___|_|   ");
    	getLogger().info("      BadWordKiller[BWkiller] successfully enabled! -By:Bear");
    	getLogger().info("      See more information at: https://github.com/i493052739");
    	getLogger().info("===================================================================");

    }
    
    @Override
    public void onDisable() {
    	getLogger().info("BadWordKiller[BWkiller] successfully disabled!");
    }
    
}
