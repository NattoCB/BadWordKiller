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
		doDFA(e, "[a-zA-Z\\u4e00-\\u9fa5]", false); // 正则忽略所有数字和符号，排除草←@ #拟123吗
		doDFA(e, "[\\u4e00-\\u9fa5]", true); // 正则忽略所有英文，针对中文再做一次检查，排除  草as你as吗
		return;
	}
	
	private void doDFA(AsyncPlayerChatEvent e, String regEx, boolean doCharCheck) {

		String intputMsg = e.getMessage().toLowerCase();
		
		// 正则过滤 初步干扰
		Pattern p = Pattern.compile(regEx); 
		Matcher m  = p.matcher(intputMsg);
		StringBuffer finalStr = new StringBuffer();
		while(m.find()){
			finalStr.append(m.group());
		}
		
		// 先判断中性词在不同场合的运用，若单独一个中性词则代表攻击词
		if (finalStr.toString().length()==1) {
			String regExLvl_0 = "[操干日]";
	        Pattern p0 = Pattern.compile(regExLvl_0);
	        Matcher m0 = p0.matcher(intputMsg);
	        intputMsg = m0.replaceAll(Message.maskSymbol).trim();
		}
		
		// 全句多字检查 - DFA
		String finalfinal = finalStr.toString();
		SensitivewordFilter filter = new SensitivewordFilter();
		Set<String> set = filter.getSensitiveWord(finalfinal, 1);
		
        if (Message.replaceEnable) {
    		if (!set.isEmpty()) {
//    	        e.getPlayer().sendMessage("敏感词的数量：" + filter.sensitiveWordMap.size());
//    	        e.getPlayer().sendMessage("语句中包含敏感词的个数为： " + set.size() + "。包含： " + set);
    	        for(String x: set) {
    	        	finalfinal = finalfinal.replace(x, Message.maskSymbol);
    			}
    	        // 非空则确定必为敏感句，强制替换二级敏感词：爸妈等
    	        String regExLvl_2 = "[爹娘爸妈爷奶儿孙姑叔舅姨祖宗先辈人&\\&0-9]";
    	        Pattern p2 = Pattern.compile(regExLvl_2);
    	        Matcher m2 = p2.matcher(finalfinal);
    	        finalfinal = m2.replaceAll("").trim();
    	        if (!doCharCheck) {
           			// 发送语句
           			e.setMessage(finalfinal);
    	        }
    		}
        } else {
    		if (!set.isEmpty()) {  // 直接删除
    			e.getPlayer().sendMessage(Message.msg_Reject);
    			e.setCancelled(true);
    		}
        }
        
		// 全句单字检查
        if (doCharCheck) {
        	if (set.isEmpty()) { 				// DFA判断为非敏感句  用未被正则处理的原始句子来检查
           		if (Message.replaceEnable) {
           			for(String charBan: BadWordDetector.banCharList) {
           				intputMsg = intputMsg.replace(charBan, Message.maskSymbol);
           			}
           			// 发送语句
           			e.setMessage(intputMsg);
           		} else {  // 直接删除
           	        for(String charBan: BadWordDetector.banCharList) {
           	        	if (intputMsg.contains(charBan)) {
           	    			e.getPlayer().sendMessage(Message.msg_Reject);
           	    			e.setCancelled(true);
           	        	}
           	        }
           		}
            } else { 							// DFA判断为敏感句  用强制删除干扰符之后的句子来检查
           		if (Message.replaceEnable) {
           			for(String charBan: BadWordDetector.banCharList) {
           				finalfinal = finalfinal.replace(charBan, Message.maskSymbol);
           			}
           			// 发送语句
           			e.setMessage(finalfinal);
           		} else {  // 直接删除
           	        for(String charBan: BadWordDetector.banCharList) {
           	        	if (finalfinal.contains(charBan)) {
           	    			e.getPlayer().sendMessage(Message.msg_Reject);
           	    			e.setCancelled(true);
           	        	}
           	        }
           		}
       		}
        }
	}
}
