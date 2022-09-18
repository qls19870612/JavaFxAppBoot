package com.ejjiu.common.utils;

import java.util.Arrays;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/01/14 14:42
 */
public class CodeStructUtils {
    public static char sLeft = '(';
    public static char sRight = ')';
    public static char mLeft = '[';
    public static char mRight = ']';
    public static char bLeft = '{';
    public static char bRight = '}';
 
    /**
     * 检查:后面没有跟()的情况
     * @param s
     * @return
     */
    public static String addBracketIfParamNotHave(String s) {
        StringBuilder stringBuilder = null;
        int length = s.length();
        int i = -1;
        int addCount = 0;
        boolean bracket = false;
        while (i < length-1 ) {
            i++;
            char c = s.charAt(i);
            if (bracket) {
                if (c == '(') {
                    bracket = false;
                } else if (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
                } else {
                    if (stringBuilder == null) {
                        stringBuilder = new StringBuilder(s);
                    }
                    stringBuilder.insert(i + addCount * 2, "()");
                    addCount++;
                    bracket = false;
                }
            } else if (c == ':') {
                bracket = true;
            }
        }
        if (stringBuilder != null) {
            return stringBuilder.toString();
        }
        return s;
    }
    
    public static String findOneArea(String s, int beginIndex, char left, char right) {
        int count = 0;
        int start = 0;
        
        int length = s.length();
        for (int i = beginIndex; i < length; i++) {
            char c = s.charAt(i);
            
            if (c == left) {
                if (count == 0) {
                    start = i;
                }
                count++;
            } else if (c == right) {
                count--;
                if (count == 0) {
                    return s.substring(start, i + 1);
                }
            }
            
        }
        return "";
    }
    
    public static String findOneAreaDesc(String s, int beginIndex, char left, char right) {
        int count = 0;
        int start = 0;
        
        for (int i = beginIndex; i >= 0; i--) {
            char c = s.charAt(i);
            
            if (c == left) {
                if (count == 0) {
                    start = i;
                }
                count++;
            } else if (c == right) {
                count--;
                if (count == 0) {
                    return s.substring(i, start + 1);
                }
            }
            
        }
        return "";
    }
    
    /**
     * 找到字符串中最外层的代码块（找到一个后，直接返回，不再再找第二个块)
     * @param s
     * @param left
     * @param right
     * @return
     */
    public static String findOneArea(String s, char left, char right) {
        return findOneArea(s, 0, left, right);
    }
    
    /**
     * 找到代码块,分把代码块替换成特殊字符，方便分割字符串
     * @param s
     * @param left 如'('或'{' 需要和 right 参数配合使用
     * @param right
     * @param replaceArea 把代码块替换成指定的特殊符号
     * @return 第0个元素，替换后的字符串，后面的元素，为代码块内的内容
     */
    public static String[] findAreaAndReplace(String s, char left, char right, String replaceArea) {
        
        String[] ret = new String[20];//就认为你有最多有20个参数
        int retSize = 1;
        int count = 0;
        int start = 0;
        StringBuilder stringBuilder = new StringBuilder();
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            
            if (c == left) {
                if (count == 0) {
                    start = i + 1;
                }
                count++;
            } else if (c == right) {
                count--;
                if (count == 0) {
                    if (retSize >= ret.length) {
                        ret = Arrays.copyOf(ret,retSize*2);
                    }
                    ret[retSize] = s.substring(start, i);
                    retSize++;
                    start = i + 1;
                    stringBuilder.append(replaceArea);
                }
            } else {
                if (count == 0) {
                    stringBuilder.append(c);
                }
            }
        }
        ret[0] = stringBuilder.toString();
        return Arrays.copyOf(ret, retSize);
    }
    
    
    public static String removeComment(String s) {
        if (s.contains("//")) {
            s = StringUtils.removeComment(s);
        }
        if (s.contains("/**")) {
            s = StringUtils.removeComment(s);
        }
        return s;
    }
}
