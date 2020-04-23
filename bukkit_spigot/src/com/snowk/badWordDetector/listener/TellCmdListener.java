package com.snowk.badWordDetector.listener;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.snowk.badWordDetector.BadWordDetector;
import com.snowk.badWordDetector.algorithm.SensitivewordFilter;
import com.snowk.badWordDetector.config.ConfigHandler;

public class TellCmdListener implements Listener {
	
	String REGEX_1 = "[a-zA-Z\\u4e00-\\u9fa5]"; // DFA Cycle 1 RegEx
	String REGEX_2 = "[\\u4e00-\\u9fa5]"; // DFA Cycle 2 RegEx
	String REGEX_3 = "[操干日]";  // 中性词 1 类 [排除单用/混用问题]
	String REGEX_4 = "[比币逼笔爹娘爸妈爷奶儿孙哥姐弟妹姑叔舅姨祖宗先辈人狗猪鸡鸭的吗a-zA-Z&\\&0-9]"; // 中性词 2类 [排除激烈/中性问题]
	
	@EventHandler
	public void onChat(PlayerCommandPreprocessEvent e) {
	
		if (!e.getMessage().toLowerCase().contains("/tell ")) {
			return; // 不是tell 直接结束
		}
		
		Player player = e.getPlayer();
		String toPlayer = e.getMessage().split(" ")[1];
		String inputMsg = ChatListener.ignoreCaseReplace(e.getMessage().replace(toPlayer, ""), "/tell ", "");; // 原始字符串，若无敏感词，则不可变动

		/**
		 * 初始化
		 * @Description: Initialization and Statistics
		 */
		
		boolean isSensitive = false; 
		SensitivewordFilter filter = new SensitivewordFilter();
		
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
    	        	buffString_1 = ChatListener.ignoreCaseReplace(buffString_1,x, ConfigHandler.maskSymbol);
    	        }
    	        for(String y: set) {
    	        	buffString_1 = ChatListener.ignoreCaseReplace(buffString_1,y, ConfigHandler.maskSymbol);
    			}
    		} 
        } else {
    		if (!set2.isEmpty()) {  // 敏感句处理：删除
    			player.sendMessage(ConfigHandler.msg_Reject);
    			e.setCancelled(true);
    			return;
    		}
    		
        }  
   
        /**
		 * postProcess DFA Cycle-2
		 * @Description:  全句单字检查： 激烈单字匹配（弥补DFA算法局限性）
		 */
        
    	if (!isSensitive) { 				// 非敏感句  用原始句子来检查
    		isSensitive = doCharCheck(isSensitive,e,inputMsg,toPlayer);
        } else { 							// 敏感句  用强制删除干扰符、二级敏感词之后的句子来检查
    	    String regExLvl_3 = REGEX_4;
    	    Pattern p3 = Pattern.compile(regExLvl_3);
    	    Matcher m3 = p3.matcher(buffString_1);
    	    buffString_1 = m3.replaceAll("").trim();
        	isSensitive = doCharCheck(isSensitive,e,buffString_1,toPlayer);
   		}

    	if (buffString_1.length()==0) { //在强制替换后避免发出空消息
    		buffString_1 = ConfigHandler.maskSymbol;
    	}	
    	

	}
	
	/**
	 * 激烈单字匹配（弥补DFA算法局限性）
	 * @Description: Check the single Chinese letter which is highly aggressive (fix DFA)
	 */
	private boolean doCharCheck(boolean isSensitive, PlayerCommandPreprocessEvent e, String pMessage, String toPlayer) {
		if (ConfigHandler.replaceEnable) {
   			for(String charBan: BadWordDetector.banCharList) {
   				if (pMessage.contains(charBan)) {
   					isSensitive = true;
   					pMessage = ChatListener.ignoreCaseReplace(pMessage,charBan, ConfigHandler.maskSymbol);	
   				}
   			}
   			e.setCancelled(true);
   			e.getPlayer().performCommand("tell " + toPlayer + " " + pMessage);
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
	
	
}
