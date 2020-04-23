package com.snowk.badWordDetector.algorithm;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.snowk.badWordDetector.BadWordDetector;
import com.snowk.badWordDetector.util.StringLengthComparator;

/**
 * 引用自CSDN：https://www.cnblogs.com/shihaiming/p/7048379.html 
*/

/**
 * @Description: 初始化敏感词库，将敏感词加入到HashMap中，构建DFA算法模型
 * @Author : chenming
 * @Date ： 2014年4月20日 下午2:27:06
 * @version 1.0
 */
public class SensitiveWordInit {
    private String ENCODING = "GBK";    //字符编码
    @SuppressWarnings("rawtypes")
    public HashMap sensitiveWordMap;
    
    public SensitiveWordInit(){
        super();
    }
    
    /**
     * @author chenming 
     * @date 2014年4月20日 下午2:28:32
     * @version 1.0
     */
    @SuppressWarnings("rawtypes")
    public Map initKeyWord(){
        try {
            //读取敏感词库
            Set<String> keyWordSet = readSensitiveWordFile();
            //将敏感词库加入到HashMap中
            addSensitiveWordToHashMap(keyWordSet);
            //spring获取application，然后application.setAttribute("sensitiveWordMap",sensitiveWordMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sensitiveWordMap;
    }

    /**
              * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：<br>
     * 
              * 中 = {
     *      isEnd = 0
              *      国 = {<br>
     *           isEnd = 1
              *           人 = {isEnd = 0
              *                民 = {isEnd = 1}
     *                }
              *           男  = {
     *                  isEnd = 0
              *                   人 = {
     *                        isEnd = 1
     *          }
     *        }
     *      }
     *    }
     *    
              *  五 = {
     *      isEnd = 0
              *      星 = {
     *          isEnd = 0
              *          红 = {
     *              isEnd = 0
              *              旗 = {
     *                   isEnd = 1
     *        }
     *       }
     *      }
     *     }
     * @author chenming 
     * @date 2014年4月20日 下午3:04:20
     * @param keyWordSet  敏感词库
     * @version 1.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void addSensitiveWordToHashMap(Set<String> keyWordSet) {
        sensitiveWordMap = new HashMap(keyWordSet.size());     //初始化敏感词容器，减少扩容操作
        String key = null;  
        Map nowMap = null;
        Map<String, String> newWorMap = null;
        //迭代keyWordSet
        Iterator<String> iterator = keyWordSet.iterator();
        while(iterator.hasNext()){
            key = iterator.next();    //关键字
            nowMap = sensitiveWordMap;
            for(int i = 0 ; i < key.length() ; i++){
                char keyChar = key.charAt(i);       //转换成char型
                Object wordMap = nowMap.get(keyChar);       //获取
                
                if(wordMap != null){        //如果存在该key，直接赋值
                    nowMap = (Map) wordMap;
                }
                else{     //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                    newWorMap = new HashMap<String,String>();
                    newWorMap.put("isEnd", "0");     //不是最后一个
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }
                
                if(i == key.length() - 1){
                    nowMap.put("isEnd", "1");    //最后一个
                }
            }
        }
    }
 

    /**
     * 读取敏感词库中的内容，将内容添加到set集合中
     * @author chenming 
     * @date 2014年4月20日 下午2:31:18
     * @return
     * @version 1.0
     * @throws Exception 
     */
    @SuppressWarnings("resource")
    private Set<String> readSensitiveWordFile() throws Exception{
        Set<String> set = new TreeSet<String>(new StringLengthComparator());  
        
        InputStreamReader read = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("resource/SensitiveWord.txt"),ENCODING);
        try {
            set = new TreeSet<String>(new StringLengthComparator());  
            BufferedReader bufferedReader = new BufferedReader(read);
            String txt = null;
            while((txt = bufferedReader.readLine()) != null){    //读取文件，将文件内容放入到set中
            	// 解码
            	final Base64.Decoder decoder = Base64.getMimeDecoder();
            	String decodeTxt = new String(decoder.decode(txt), "GBK");
            	
            	// 从文件读取的词汇录入
            	if (decodeTxt.length()==1) { // 先判断是否为单字（DFA无法处理单字）
            		BadWordDetector.banCharList.add(decodeTxt.toLowerCase());
            	} else if (!(BadWordDetector.snowkPlugin.getConfig().getStringList("remove").contains(decodeTxt))) { // 则若解码后的非单关键字不在忽略列表中，则添加至hashMap 
            		set.add(decodeTxt.toLowerCase());
            	}
            	// 从config读取的词汇录入
            	List<String> configAddList = BadWordDetector.snowkPlugin.getConfig().getStringList("add");
            	for (String i : configAddList) {
            		if (i.length()==1) { //DFA 算法仅支持长度大于1的匹配，若长度只有1，则进入单字检查功能
            			BadWordDetector.banCharList.add(i.toLowerCase());
            		} else {
            			set.add(i.toLowerCase());
            		}
            	}
            }

        } catch (Exception e) {
            throw e;
        }finally{
            read.close();     //关闭文件流
        }
        return set;
    }
}