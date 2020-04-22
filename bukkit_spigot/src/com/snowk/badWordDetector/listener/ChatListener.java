package com.snowk.badWordDetector.listener;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.snowk.badWordDetector.BadWordDetector;
import com.snowk.badWordDetector.Message;
import com.snowk.badWordDetector.util.SensitivewordFilter;

public class ChatListener implements Listener {
		
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		
		String intputMsg = e.getMessage().toLowerCase();

		// 正则过滤
		String regEx="[a-zA-Z\\u4e00-\\u9fa5]"; 
		Pattern p = Pattern.compile(regEx); 
		Matcher m  = p.matcher(intputMsg);
		StringBuffer finalStr = new StringBuffer();
		while(m.find()){
			finalStr.append(m.group());
		}
		
		// 多字检查 - DFA
		SensitivewordFilter filter = new SensitivewordFilter();
		String finalfinal = finalStr.toString();
		Set<String> set = filter.getSensitiveWord(finalfinal, 1);
		
        if (Message.replaceEnable) {
    		if (!set.isEmpty()) {
//    	        e.getPlayer().sendMessage("敏感词的数量：" + filter.sensitiveWordMap.size());
//    	        e.getPlayer().sendMessage("语句中包含敏感词的个数为： " + set.size() + "。包含： " + set);
    	        for(String x: set) {
    	        	finalfinal = finalfinal.replace(x, Message.maskSymbol);
    			}
    	        // 非空则为敏感语句，强制替换二级敏感词：爸妈等
    	        String regExLvl_2 = "[爹娘爸妈爷奶儿孙姑叔舅姨祖宗先辈人&\\&0-9]";
    	        Pattern p2 = Pattern.compile(regExLvl_2);
    	        Matcher m2 = p2.matcher(finalfinal);
    	        finalfinal = m2.replaceAll("").trim();
    		}
        } else {
    		if (!set.isEmpty()) {
    			e.getPlayer().sendMessage(Message.msg_Reject);
    			e.setCancelled(true);
    		}
        }
        
		// 单字检查
        if (set.isEmpty()) {
    		if (Message.replaceEnable) {
    			for(String charBan: BadWordDetector.banCharList) {
    				intputMsg = intputMsg.replace(charBan, Message.maskSymbol);
    			}
    			// 发送语句
    			e.setMessage(intputMsg);
    		} else {
    	        for(String charBan: BadWordDetector.banCharList) {
    	        	if (intputMsg.contains(charBan)) {
    	    			e.getPlayer().sendMessage(Message.msg_Reject);
    	    			e.setCancelled(true);
    	        	}
    	        }
    		}
        } else {
    		if (Message.replaceEnable) {
    			for(String charBan: BadWordDetector.banCharList) {
    				finalfinal = finalfinal.replace(charBan, Message.maskSymbol);
    			}
    			// 发送语句
    			e.setMessage(finalfinal);
    		} else {
    	        for(String charBan: BadWordDetector.banCharList) {
    	        	if (finalfinal.contains(charBan)) {
    	    			e.getPlayer().sendMessage(Message.msg_Reject);
    	    			e.setCancelled(true);
    	        	}
    	        }
    		}
		}

		return;
	}
}
