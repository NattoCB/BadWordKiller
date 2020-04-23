package com.snowk.badWordDetector.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TellCmdListener implements Listener {
	@EventHandler
	public void onChat(PlayerCommandPreprocessEvent e) {
	e.getPlayer().sendMessage("ÄãÊäÈëÁË"+e.getMessage());
	
	}
}
