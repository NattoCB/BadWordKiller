package com.snowk.badWordDetector.listener;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.snowk.badWordDetector.BadWordDetector;
import com.snowk.badWordDetector.algorithm.SensitivewordFilter;
import com.snowk.badWordDetector.config.ConfigHandler;

public class ChatListener implements Listener {
	
	String REGEX_1 = "[a-zA-Z\\u4e00-\\u9fa5]"; // DFA Cycle 1 RegEx
	String REGEX_2 = "[\\u4e00-\\u9fa5]"; // DFA Cycle 2 RegEx
	String REGEX_3 = "[操干日]";  // 中性词 1 类 [排除单用/混用问题]
	String REGEX_4 = "[比币逼笔爹娘爸妈爷奶儿孙哥姐弟妹姑叔舅姨祖宗先辈人狗猪鸡鸭的吗a-zA-Z&\\&0-9]"; // 中性词 2类 [排除激烈/中性问题]
	String CANCEL_DEBUG = "cancel"; // cancel debug function
	
	/** playerMatcher (ArrayList)
	 *  k: Player Name (String)
	 *  function: save PlayerName when they use /bwk test command,
	 *  		  then for matching them with the next chat.
	 */
	public static ArrayList<String> playerMatcher = new ArrayList<String>();
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		
		Player player = e.getPlayer();
		String inputMsg = e.getMessage(); // 原始字符串，若无敏感词，则不可变动

		/**
		 * 初始化
		 * @Description: Initialization and Statistics
		 */
		
		boolean isSensitive = false; 
		SensitivewordFilter filter = new SensitivewordFilter();
		String logger_1 = "§7[§c!§7] §d§lBadWordKiller 开始检测 ";
		String logger_2 = "§7[§a!§7] §3敏感词库总数：" + filter.sensitiveWordMap.size();
		
		/**
		 * 正则过滤 初步干扰
		 * @Description: 忽略所有数字、符号，查找：    草fu！ck拟123123#@￥吗 【不更改inputMsg，只做替换】 目的：找出 fuck
		 */
		
		String regEx0 = REGEX_1;
		Pattern p = Pattern.compile(regEx0); 
		Matcher m  = p.matcher(inputMsg);
		StringBuffer finalStr = new StringBuffer();
		while(m.find()){
			finalStr.append(m.group());
		}
		String buffString_1 = finalStr.toString(); 	// 用于处理的字符串 【仅在检测到敏感词后使用】
		
		/**
		 * 中性词过滤 
		 * @Description: 判断中性词在不同场合的运用，若单独一个中性词则代表攻击词 【仅在忽略干扰后存在的单个字符】 排除：操     但不排除：操场
		 */
		
		if (buffString_1.length()==1) {
			String regExLvl_0 = REGEX_3;
	        Pattern p0 = Pattern.compile(regExLvl_0);
	        Matcher m0 = p0.matcher(inputMsg);
	        buffString_1 = m0.replaceAll(ConfigHandler.maskSymbol).trim();
		}
		String logger_3 = "§7[§e!§7] §6正在处理字符串（第一波正则后）：" + buffString_1;

		/**
		 * DFA Cycle-1
		 * @Description: 全句多字检查   第一批 【主要针对英文】
		 */
		
		Set<String> set = filter.getSensitiveWord(buffString_1.toLowerCase(), 1);
        if (ConfigHandler.replaceEnable) {
    		if (!set.isEmpty()) { // 敏感句处理：替换
    			isSensitive = true;
    		}
        } else {
    		if (!set.isEmpty()) {  // 敏感句处理：删除
    			player.sendMessage(ConfigHandler.msg_Reject);
    			e.setCancelled(true);
    			return;
    		}
        }
		String logger_4 = "§7[§c!§7] §a§l第一轮敏感词： §3" + set;
        String logger_5 = "§7[§e!§7] §6正在处理字符串（DFA-1）中文单筛：" + buffString_1;
		
        /**
		 * postProcess DFA Cycle-1 
		 * @Description: 若非敏感，恢复原句；若是敏感，强制替换二级敏感词：爸妈等。
		 */
        
        if (!isSensitive) { 
        	buffString_1 = inputMsg;
        } else {
	        String regExLvl_3 = REGEX_4;
	        Pattern p3 = Pattern.compile(regExLvl_3);
	        Matcher m3 = p3.matcher(buffString_1);
	        buffString_1 = m3.replaceAll("").trim();
        }
        
		/**
		 * 正则过滤 第二层干扰
		 * @Description: 忽略所有英文、数字、符号，查找：    草←asd@ #拟1-2sadefvse3吗 【不更改inputMsg，只做替换】 目的：找出  草拟吗
		 */
		
		String regEx1 = REGEX_2;
		Pattern p1 = Pattern.compile(regEx1); 
		Matcher m1  = p1.matcher(inputMsg);
		StringBuffer finalStr2 = new StringBuffer();
		while(m1.find()){
			finalStr2.append(m1.group());
		}
		buffString_1 = finalStr2.toString();  // 用于处理的字符串 【仅在检测到敏感词后使用】
		String logger_6 = "§7[§e!§7] §6正在处理字符串（第二波正则后）：" + buffString_1;
		
		/**
		 * DFA Cycle-2
		 * @Description: 全句多字检查   第二批 【主要针对  中文、以及中英文混淆敏感词】
		 */
		
		SensitivewordFilter filter2 = new SensitivewordFilter();
		Set<String> set2 = filter2.getSensitiveWord(buffString_1.toLowerCase(), 1);
		
        if (ConfigHandler.replaceEnable) {
    		if (!set2.isEmpty()) { // 敏感句处理：替换
    	        isSensitive = true;
    			// 先替换全中文，再替换中英混合，避免因小失大
    	        for(String x: set2) {
    	        	buffString_1 = ignoreCaseReplace(buffString_1,x, ConfigHandler.maskSymbol);
    	        }
    	        for(String y: set) {
    	        	buffString_1 = ignoreCaseReplace(buffString_1,y, ConfigHandler.maskSymbol);
    			}
    		} 
        } else {
    		if (!set2.isEmpty()) {  // 敏感句处理：删除
    			player.sendMessage(ConfigHandler.msg_Reject);
    			e.setCancelled(true);
    			return;
    		}
    		
        }  
        String logger_7 = "§7[§c!§7] §a§l第二轮敏感词： §3" + set2;
        String logger_8 = "§7[§e!§7] §6正在处理字符串（DFA-2）中英混筛：" + buffString_1;
		
        /**
		 * postProcess DFA Cycle-2
		 * @Description:  全句单字检查： 激烈单字匹配（弥补DFA算法局限性）
		 */
        
    	if (!isSensitive) { 				// 非敏感句  用原始句子来检查
    		isSensitive = doCharCheck(isSensitive,e,inputMsg);
        } else { 							// 敏感句  用强制删除干扰符、二级敏感词之后的句子来检查
    	    String regExLvl_3 = REGEX_4;
    	    Pattern p3 = Pattern.compile(regExLvl_3);
    	    Matcher m3 = p3.matcher(buffString_1);
    	    buffString_1 = m3.replaceAll("").trim();
        	isSensitive = doCharCheck(isSensitive,e,buffString_1);
   		}
    	String logger_9 = "§7[§e!§7] §6正在处理字符串（DFA-补）单字单筛：" + buffString_1;
    	
    	if (buffString_1.length()==0) { //在强制替换后避免发出空消息
    		buffString_1 = ConfigHandler.maskSymbol;
    	}
    	
    	String logger_10 = "§7[§4!§7] §c§l最终敏感句判断结果：§a§l" + isSensitive;
    	String logger_11 = "§7[§4!§7] §c§l最终敏感句判断结果：§c§l" + isSensitive;
    	
        /**
		 * Log Print for Debugging
		 * @Description:  Log Print for Debugging
		 */
    	
    	if (playerMatcher.contains(player.getName())) {
    		if (e.getMessage().equalsIgnoreCase(CANCEL_DEBUG)) {
            	playerMatcher.remove(player.getName());
            	e.setCancelled(true);
            	player.sendMessage("§7[§c!§7] §d§lBadWordKiller debug功能已关闭！");
            	return;
            } 
    		player.sendMessage(logger_1);
    		player.sendMessage(logger_2);
    		player.sendMessage(logger_3);
    		player.sendMessage(logger_4);
    		player.sendMessage(logger_5);
    		player.sendMessage(logger_6);
    		player.sendMessage(logger_7);
    		player.sendMessage(logger_8);
    		player.sendMessage(logger_9);
    		if (isSensitive) {
        		player.sendMessage(logger_10);
    		} else {
        		player.sendMessage(logger_11);
    		}
    		player.sendMessage("§7[§c!§7] §cBadWordKiller §adebug报告完毕， §e输入 §ccancel §e退出检测");
    		player.sendMessage("");
    		e.setCancelled(true);
    	}
	}
	
	/**
	 * 激烈单字匹配（弥补DFA算法局限性）
	 * @Description: Check the single Chinese letter which is highly aggressive (fix DFA)
	 */
	private boolean doCharCheck(boolean isSensitive, AsyncPlayerChatEvent e, String pMessage) {
		if (ConfigHandler.replaceEnable) {
   			for(String charBan: BadWordDetector.banCharList) {
   				if (pMessage.contains(charBan)) {
   					isSensitive = true;
   					pMessage = ignoreCaseReplace(pMessage,charBan, ConfigHandler.maskSymbol);	
   				}
   				
   			}
   			e.setMessage(pMessage);
   		} else {
   	        for(String charBan: BadWordDetector.banCharList) {
   	        	if (pMessage.contains(charBan)) {
   	        		isSensitive = true;
   	        		e.getPlayer().sendMessage(ConfigHandler.msg_Reject);
   	        		e.setCancelled(true);
   	        		break;
   	        	}
   	        }
   		}
   		return isSensitive;
	}
	
	/**
	 * 忽略大小写进行文本替换
	 * @Description: Replace string without case sensitive
	 */
	public static String ignoreCaseReplace(String source, String oldstring,String newstring){
		Pattern p = Pattern.compile(oldstring, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(source);
		String ret = m.replaceAll(newstring);
		return ret;
	}
}
