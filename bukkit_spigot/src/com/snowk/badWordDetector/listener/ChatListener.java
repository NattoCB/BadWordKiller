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

		String inputMsg = e.getMessage();
		
		// init and stats
		boolean isSensitive = false; 
		boolean printLog = true;
		SensitivewordFilter filter = new SensitivewordFilter();
		String logger_1 = "§7[§c!§7] §d§lBadWordKiller 开始检测 ";
		String logger_2 = "§7[§a!§7] §3敏感词库总数：" + filter.sensitiveWordMap.size();
		
		//=============================================================================================================
		// 正则过滤 初步干扰   -  忽略所有数字、符号，查找：    草fu！ck拟123123#@￥吗 【不更改inputMsg，只做替换】 目的：找出 fuck 草拟吗
		String regEx0 = "[a-zA-Z\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx0); 
		Matcher m  = p.matcher(inputMsg);
		StringBuffer finalStr = new StringBuffer();
		while(m.find()){
			finalStr.append(m.group());
		}
		// 用于处理的字符串 【仅在检测到敏感词后使用】
		String buffString_1 = finalStr.toString(); 
		
		// 先判断中性词在不同场合的运用，若单独一个中性词则代表攻击词 【仅在忽略干扰后存在的单个字符】 排除：操     但不排除：操场
		if (buffString_1.length()==1) {
			String regExLvl_0 = "[操干日]";
	        Pattern p0 = Pattern.compile(regExLvl_0);
	        Matcher m0 = p0.matcher(inputMsg);
	        buffString_1 = m0.replaceAll(Message.maskSymbol).trim();
		}
		String logger_3 = "§7[§e!§7] §6正在处理字符串（第一波正则后）：" + buffString_1;
		
		// 全句多字检查 - DFA Cyc-1
		Set<String> set = filter.getSensitiveWord(buffString_1.toLowerCase(), 1);
        if (Message.replaceEnable) {
    		if (!set.isEmpty()) { // 敏感句处理：替换
    			isSensitive = true;
    		}
        } else {
    		if (!set.isEmpty()) {  // 敏感句处理：删除
    			e.getPlayer().sendMessage(Message.msg_Reject);
    			e.setCancelled(true);
    			return;
    		}
        }
		String logger_4 = "§7[§c!§7] §a§l第一轮敏感词： §3" + set;
        String logger_5 = "§7[§e!§7] §6正在处理字符串（DFA-1后）中文单筛：" + buffString_1;
        
        if (!isSensitive) { // 【DFA-1轮结束，若非敏感，恢复原句】
        	buffString_1 = inputMsg;
        } else { // 【敏感句强制替换二级敏感词：爸妈等】
	        String regExLvl_3 = "[比币逼笔爹娘爸妈爷奶儿孙姑叔舅姨祖宗先辈人狗猪鸡鸭的a-zA-Z&\\&0-9]";
	        Pattern p3 = Pattern.compile(regExLvl_3);
	        Matcher m3 = p3.matcher(buffString_1);
	        buffString_1 = m3.replaceAll("").trim();
        }
        //==========================================================================================================
		// 正则过滤 第二层干扰   -  忽略所有英文、数字、符号，查找：    草←asd@ #拟1-2sadefvse3吗 【不更改inputMsg，只做替换】 目的：找出  草拟吗
		String regEx1 = "[\\u4e00-\\u9fa5]";
		Pattern p1 = Pattern.compile(regEx1); 
		Matcher m1  = p1.matcher(inputMsg);
		StringBuffer finalStr2 = new StringBuffer();
		while(m1.find()){
			finalStr2.append(m1.group());
		}
		// 用于处理的字符串 【仅在检测到敏感词后使用】
		buffString_1 = finalStr2.toString(); 
		String logger_6 = "§7[§e!§7] §6正在处理字符串（第二波正则后）：" + buffString_1;
		
		// 全句多字检查 - DFA Cyc-2
		SensitivewordFilter filter2 = new SensitivewordFilter();
		Set<String> set2 = filter2.getSensitiveWord(buffString_1.toLowerCase(), 1);
		
        if (Message.replaceEnable) {
    		if (!set2.isEmpty()) { // 敏感句处理：替换
    	        isSensitive = true;
    			// 两波词一起替换，但先替换全中，再替换中英混合
    	        for(String x: set2) {
    	        	buffString_1 = ignoreCaseReplace(buffString_1,x, Message.maskSymbol);
    	        }
    	        for(String y: set) {
    	        	buffString_1 = ignoreCaseReplace(buffString_1,y, Message.maskSymbol);
    			}
    		} 
        } else {
    		if (!set2.isEmpty()) {  // 敏感句处理：删除
    			e.getPlayer().sendMessage(Message.msg_Reject);
    			e.setCancelled(true);
    			return;
    		}
    		
        }  
        String logger_7 = "§7[§c!§7] §a§l第二轮敏感词： §3" + set2;
        String logger_8 = "§7[§e!§7] §6正在处理字符串（DFA-2）中英混筛：" + buffString_1;
        
        //===========================================================================================
		// 最后： 全句单字检查
    	if (!isSensitive) { 				// 非敏感句  用原始句子来检查
    		isSensitive = doCharCheck(isSensitive,e,inputMsg);
        } else { 							// 敏感句  用强制删除干扰符、二级敏感词之后的句子来检查
    	    String regExLvl_3 = "[逼比币笔爹娘爸妈爷奶儿孙姑叔舅姨祖宗先辈人a-zA-Z&\\&0-9]";
    	    Pattern p3 = Pattern.compile(regExLvl_3);
    	    Matcher m3 = p3.matcher(buffString_1);
    	    buffString_1 = m3.replaceAll("").trim();
        	isSensitive = doCharCheck(isSensitive,e,buffString_1);
   		}
    	String logger_9 = "§7[§e!§7] §6正在处理字符串（DFA-补）单字单筛：" + buffString_1;
    	
    	if (buffString_1.length()==0) { //在强制替换后避免发出空消息
    		buffString_1 = Message.maskSymbol;
    	}
    	
    	String logger_10 = "§7[§4!§7] §c§l最终敏感句判断结果：§a§l" + isSensitive;
    	
    	// output for debugging
    	if (printLog) {
    		e.getPlayer().sendMessage(logger_1);
    		e.getPlayer().sendMessage(logger_2);
    		e.getPlayer().sendMessage(logger_3);
    		e.getPlayer().sendMessage(logger_4);
    		e.getPlayer().sendMessage(logger_5);
    		e.getPlayer().sendMessage(logger_6);
    		e.getPlayer().sendMessage(logger_7);
    		e.getPlayer().sendMessage(logger_8);
    		e.getPlayer().sendMessage(logger_9);
    		e.getPlayer().sendMessage(logger_10);
    		e.setCancelled(true);
    	}
	}
	
	private boolean doCharCheck(boolean isSensitive, AsyncPlayerChatEvent e, String pMessage) {
		if (Message.replaceEnable) {
   			for(String charBan: BadWordDetector.banCharList) {
   				if (pMessage.contains(charBan)) {
   					isSensitive = true;
   					pMessage = ignoreCaseReplace(pMessage,charBan, Message.maskSymbol);	
   				}
   				
   			}
   			e.setMessage(pMessage);
   		} else {
   	        for(String charBan: BadWordDetector.banCharList) {
   	        	if (pMessage.contains(charBan)) {
   	        		isSensitive = true;
   	        		e.setCancelled(true);
   	        	}
   	        }
   	        e.getPlayer().sendMessage(Message.msg_Reject);
   		}
   		return isSensitive;
	}
	
	public static String ignoreCaseReplace(String source, String oldstring,String newstring){
		Pattern p = Pattern.compile(oldstring, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(source);
		String ret = m.replaceAll(newstring);
		return ret;
	}
}
