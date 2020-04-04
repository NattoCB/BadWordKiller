package com.snowk.badWordDetector.listener;

import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.snowk.badWordDetector.Message;
import com.snowk.badWordDetector.util.SensitivewordFilter;


public class ChatListener implements Listener {
		
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		SensitivewordFilter filter = new SensitivewordFilter();
		Set<String> set = filter.getSensitiveWord(e.getMessage(), 1);
		if (!set.isEmpty()) {
			e.getPlayer().sendMessage(Message.msg_Reject);
			e.setCancelled(true);
			return;
		}
	}
}
