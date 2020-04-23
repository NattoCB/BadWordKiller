package com.snowk.badWordDetector.util;

import java.util.Comparator;

public class StringLengthComparator implements Comparator<String> {

	/**
	 * set±È½ÏÆ÷
	 * @Description: Compare String lengths conversely
	 */
    @Override
    public int compare(String str1, String str2) {
        int num = str2.length() - str1.length();
        if(num == 0)
            return str1.compareTo(str2);
        return num;
    }
    
}
