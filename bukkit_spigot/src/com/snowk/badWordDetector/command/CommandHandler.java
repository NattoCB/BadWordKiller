package com.snowk.badWordDetector.command;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.snowk.badWordDetector.BadWordDetector;
import com.snowk.badWordDetector.command.CommandFramework;
import com.snowk.badWordDetector.config.ConfigHandler;
import com.snowk.badWordDetector.listener.ChatListener;

public class CommandHandler extends CommandFramework {
    
	public CommandHandler(String label) {
        super(label);
    }
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player p = (Player) sender;
		
		if (label.equalsIgnoreCase("badwordkiller") || label.equalsIgnoreCase("bwk") || label.equalsIgnoreCase("bwkiller")) {
			if (p.hasPermission("snowk.bwk.admin") || p.isOp()) {
				if (args.length == 0){
					getHelper(p);
	    		}
	    		if (args.length == 1) {
	    			if (args[0].equalsIgnoreCase("add")) {
	    				getHelper(p);
	        		} else if (args[0].equalsIgnoreCase("remove")) {
	    				getHelper(p);
	        		} else if (args[0].equalsIgnoreCase("help")) {
	    				getHelper(p);
	        		} else if (args[0].equalsIgnoreCase("reload")) {
	                    doReload();
	                    p.sendMessage(ConfigHandler.msg_Reload);
	                } else if (args[0].equalsIgnoreCase("debug")) {
	                	ChatListener.playerMatcher.add(p.getName());
	                	p.sendMessage(ConfigHandler.msg_test);
	                } else if (args[0].equalsIgnoreCase("mask")) {
	                	getHelper(p);
	                } else {
	                	getHelper(p);
	                }
	    		} 
	    		if (args.length >= 2) {
	    			String argConcat = "";
	    			
	    			if (args[0].equalsIgnoreCase("add")) {
	    				for (int i = 0; i < args.length; i++) {
	    					if (i > 1) {
	    						argConcat += args[i];
	    					}
	    				}
	    				if (args[1].equalsIgnoreCase("ban")) {
	    					List<String> changeList = BadWordDetector.snowkPlugin.getConfig().getStringList("add");
	    					changeList.add(argConcat);
	    					BadWordDetector.snowkPlugin.getConfig().set("add", changeList);
	    					BadWordDetector.snowkPlugin.saveConfig();
	    					doReload();
		        			p.sendMessage("§7[§3BWK§7] §e§l限制词： §3§l"+ argConcat +" §e在 config 添加成功");
		        			p.sendMessage("§7[§3BWK§7] §a请放心，检查时也会自动忽略空格");
	    				} else if (args[1].equalsIgnoreCase("unban")) {
	    					List<String> changeList = BadWordDetector.snowkPlugin.getConfig().getStringList("remove");
	    					changeList.add(argConcat);
	    					BadWordDetector.snowkPlugin.getConfig().set("remove", changeList);
	    					BadWordDetector.snowkPlugin.saveConfig();
	    					doReload();
	    					p.sendMessage("§7[§3BWK§7] §3§l忽略词：§6§l"+ argConcat +" §e在 config 添加成功");
		        			p.sendMessage("§7[§3BWK§7] §a请放心，检查时也会自动忽略空格");
	    				} else {
	    					getHelper(p);
	    				}
	        		} else if (args[0].equalsIgnoreCase("remove")) {
	    				for (int i = 0; i < args.length; i++) {
	    					if (i > 1) {
	    						argConcat += args[i];
	    					}
	    				}
	    				p.sendMessage(argConcat);
	    				if (args[1].equalsIgnoreCase("ban")) {
	    					List<String> changeList = BadWordDetector.snowkPlugin.getConfig().getStringList("add");
	    					if (changeList.contains(argConcat)) {
	    						changeList.remove(argConcat);
		    					BadWordDetector.snowkPlugin.getConfig().set("add", changeList);
		    					BadWordDetector.snowkPlugin.saveConfig();
		    					doReload();
			        			p.sendMessage("§7[§3BWK§7] §e§l限制词： §3§l"+ argConcat +" §e从 config 移除成功");
			        			p.sendMessage("§7[§3BWK§7] §a请放心，BWK已为您考虑空格");
	    					} else {
	    						p.sendMessage("§7[§3BWK§7] §e§l限制词：§c§l"+ argConcat +" §e未找到");
	    					}

	    				} else if (args[1].equalsIgnoreCase("unban")) {
	    					List<String> changeList = BadWordDetector.snowkPlugin.getConfig().getStringList("remove");
	    					if (changeList.contains(argConcat)) {
	    						changeList.remove(argConcat);
		    					BadWordDetector.snowkPlugin.getConfig().set("remove", changeList);
		    					BadWordDetector.snowkPlugin.saveConfig();
		    					doReload();
		    					p.sendMessage("§7[§3BWK§7] §3§l忽略词： §6§l"+ argConcat +" §e从 config 移除成功");
		    					p.sendMessage("§7[§3BWK§7] §a请放心，BWK已为您考虑空格");
	    					} else {
	    						p.sendMessage("§7[§3BWK§7] §e§l忽略词：§c§l"+ argConcat +" §e未找到");
	    					}
	    					
	    				}else {
	    					getHelper(p);
	    				}	
	        		} else if (args[0].equalsIgnoreCase("mask")) {
	                	for (int i = 0; i < args.length; i++) {
	    					if (i == 0) {
	    						argConcat +=  args[i];
	    					} else {
	    						argConcat +=  "" + args[i];
							}
	    				}
	                	BadWordDetector.snowkPlugin.getConfig().set("mask", argConcat);
	                	p.sendMessage(ConfigHandler.msg_maskChange);
	    			} else {
	                	getHelper(p);
	                }
	    		}
			} else {
				p.sendMessage(ConfigHandler.msg_NoPerm);
			}
    	}
	}
	
	protected void getHelper(Player p) {
    	p.sendMessage("§7§l[§3§m§l=======§6§l BadWordKiller §b§lBy:Bear §3§m§l========§7§l]");
    	p.sendMessage("§a/bwk add ban  §3- add a §3§lBAN §3word to §3§lCONFIG §3(add to the original thesaurus) - §e在config中添加一个限制词");
    	p.sendMessage("§a/bwk add unban  §3- add a §3§lBYPASS §3word to §3§lCONFIG §3(bypass the original thesaurus) - §e在config中添加一个忽略词");
    	p.sendMessage("§a/bwk remove ban  §3- delete the §3§lCUSTOM BAN §3word made by you - §e在config中删除一个限制词");
    	p.sendMessage("§a/bwk remove unban  §3- delete the §3§lCUSTOM BYPASS §3word made by you - §e在config中删除一个忽略词");
    	p.sendMessage("§a/bwk help  §3- show this page - §e查看本页面");
    	p.sendMessage("§a/bwk debug  §3- test your sentence [disable when say 'cancel'] - §e开启debug模式 [再次输入 §6§lcancel §e退出]");
    	p.sendMessage("§a/bwk reload  §3- reload the plugin - §e重载插件");
	}
	
	protected void doReload() {
        if (!new File("./plugins/BadWordKiller/config.yml").exists()) {
        	BadWordDetector.snowkPlugin.saveDefaultConfig();
        	BadWordDetector.snowkPlugin.getLogger().info("BadWordKiller[BWkiller] successfully created config file");
        }	
    	BadWordDetector.snowkPlugin.reloadConfig();
    	ConfigHandler.replaceEnable = BadWordDetector.snowkPlugin.getConfig().getBoolean("replace");
    	ConfigHandler.maskSymbol = BadWordDetector.snowkPlugin.getConfig().getString("mask");
    	ConfigHandler.msg_Reject = BadWordDetector.snowkPlugin.getConfig().getString("Msg_Reject").replace("&", "§");
    	ConfigHandler.msg_Reload = BadWordDetector.snowkPlugin.getConfig().getString("Msg_Reload").replace("&", "§");
    	ConfigHandler.msg_NoPerm = BadWordDetector.snowkPlugin.getConfig().getString("Msg_NoPerm").replace("&", "§");
    	ConfigHandler.msg_test = BadWordDetector.snowkPlugin.getConfig().getString("Msg_test").replace("&", "§");
    	ConfigHandler.msg_maskChange = BadWordDetector.snowkPlugin.getConfig().getString("Msg_maskChange").replace("&", "§");
	}
}
