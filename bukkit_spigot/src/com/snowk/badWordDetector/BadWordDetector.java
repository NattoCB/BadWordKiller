package com.snowk.badWordDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.snowk.badWordDetector.listener.ChatListener;
import com.snowk.badWordDetector.listener.TellCmdListener;
//import com.snowk.badWordDetector.metrics.Metrics;
import com.snowk.badWordDetector.command.CommandFramework;
import com.snowk.badWordDetector.command.CommandHandler;
import com.snowk.badWordDetector.command.ConstructTabCompleter;

public class BadWordDetector extends JavaPlugin {
	
	public static BadWordDetector snowkPlugin;
	public static List<String> banCharList = new ArrayList<String>();
	public static String VERSION = "";
	
    @Override
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
    	CommandFramework.register(this, new CommandHandler("bwkiller"));
    	getCommand("badwordkiller").setTabCompleter(new ConstructTabCompleter());
    	getCommand("bwk").setTabCompleter(new ConstructTabCompleter());
    	getCommand("bwkiller").setTabCompleter(new ConstructTabCompleter());
    	
    	// listeners
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new TellCmdListener(), this);

        // bStats  TODO
//    	int pluginId = 6684;
//    	Metrics metrics = new Metrics(this,pluginId);
//    	metrics.addCustomChart(new Metrics.SingleLineChart("players", new Callable<Integer>() {
//    		@Override
//    		public Integer call() throws Exception {
//    			// (This is useless as there is already a player chart by default.)
//    			return Bukkit.getOnlinePlayers().size();
//          }
//    	}));
        
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
