package com.snowk.badWordDetector.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ConstructTabCompleter implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("badwordkiller") || cmd.getName().equalsIgnoreCase("bwk") || cmd.getName().equalsIgnoreCase("bwkiller")) {
			Player p = (Player) sender;
			if (p.hasPermission("snowk.bwk.admin") || p.isOp()) {
				List<String> tabList = new ArrayList<String>();
				if (args.length == 1) {
					if (args[0].startsWith("a")) {
						tabList.add("add");
					} else if (args[0].startsWith("rem")) {
						tabList.add("remove");
					} else if (args[0].startsWith("d")) {
						tabList.add("debug");
					} else if (args[0].startsWith("h")) {
						tabList.add("help");
					} else if (args[0].startsWith("rel")) {
						tabList.add("reload");
					} else if (args[0].startsWith("m")) {
						tabList.add("mask");
					} else if (args[0].startsWith("r")) {
						tabList.add("reload");
						tabList.add("remove");
					} else {
						tabList.add("add");
						tabList.add("remove");
						tabList.add("debug");
						tabList.add("help");
						tabList.add("reload");
						tabList.add("mask");
					}
				}
				if (args.length == 2) {
					List<String> tabListSub_1 = new ArrayList<String>();
					if (args[0].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) {
						if (args[1].startsWith("b")) {
							tabListSub_1.add("ban");
						} else if (args[1].startsWith("u")) {
							tabListSub_1.add("unban");
						} else {
							tabListSub_1.add("ban");
							tabListSub_1.add("unban");
						}
					}
					return tabListSub_1;
				}
				return tabList;
			}
		}
		return null;
	}

}
