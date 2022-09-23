package com.ejjiu.common.utils;

import com.ejjiu.entries.Area;

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
        while (i < length - 1) {
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
    
    /**
     * 找出匹配块位置
     * @param s
     * @param beginIndex
     * @param left
     * @param right
     * @return Area
     */
    public static Area findOneAreaIndex(String s, int beginIndex, char left, char right) {
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
                    //                    return s.substring(start, i + 1);
                    return new Area(start, i + 1);
                }
            }
            
        }
        return null;
    }
    
    public enum MatchType {
        SINGLE_COMMENT("//", "\n"),//单行注释 //
        MULTI_COMMENT("/*", "*/"),//多行注释 /* */
        STRING_DOUBLE("\"", "\""),//字符串 ""
        STRING_SINGLE("'", "'"),//字符串 ''
        STRING_EXPRESS("`", "`"),//字符串 ``
        BRACKET_BIG("{", "}"),//大括号
        BRACKET_MIDDLE("[", "]"),//中括号
        BRACKET_SMALL("(", ")"),//小括号
        ;
        private final String start;
        private final String end;
        
        MatchType(String start, String end) {
            
            this.start = start;
            this.end = end;
        }
    }
    
    public enum CodeType {
        NORMAL(null, null,false),//普通代码
        SINGLE_COMMENT("//", "\n",false),//单行注释 //
        MULTI_COMMENT("/*", "*/",false),//多行注释 /* */
        STRING_DOUBLE("\"", "\"",true),//字符串 ""
        STRING_SINGLE("'", "'",true),//字符串 ''
        STRING_EXPRESS("`", "`",true),//字符串 ``
        ;
        
        private final String start;
        private final String end;
        private final boolean isString;//字符串类型的要处理转义，比如:   "\"sss\""
    
        CodeType(String start, String end,boolean isString) {
            
            this.start = start;
            this.end = end;
            this.isString = isString;
        }
    }
    private static int getPrevXieGangCount(String fileContent, int i) {
        int count = 0;
        for (int j = i - 1; j >= 0; j--) {
            char c = fileContent.charAt(j);
            if (c == '\\') {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
    
    public static Area findCodeOneAreaIndex(String s, int beginIndex, MatchType matchType) {
        return findCodeOneAreaIndex(s,beginIndex,matchType.start,matchType.end);
    }
    
//    private static final Logger logger = LoggerFactory.getLogger(CodeStructUtils.class);
    public static Area findCodeOneAreaIndex(String s, int beginIndex, String left, String right) {
        int count = 0;
        int start = 0;
        CodeType codeType = CodeType.NORMAL;//当前代码类型
        int length = s.length();
        int noNormalTypeStart = 0;
        for (int i = beginIndex; i < length; i++) {
            
            if (codeType == CodeType.NORMAL) {
                codeType = findCodeTypeOfStart(s, i);
                if (codeType == CodeType.NORMAL) {
                   if (count > 0 && isMatchOf(s,i,right,false)) {
                        count--;
                        if (count == 0) {
                            return new Area(start, i + 1);
                        }
                        i += right.length() - 1;
                    }else if (isMatchOf(s,i,left,false)) {
                       if (count == 0) {
                           start = i;
                       }
                       count++;
                       i += left.length() - 1;
                   }
                } else {
                    noNormalTypeStart = i;
                    i += codeType.start.length() - 1;
                }
                
            } else {
                if (isMatchOf(s, i, codeType.end,codeType.isString)) {
                    i += codeType.end.length() - 1;
                    codeType = CodeType.NORMAL;
//                    logger.debug("findCodeOneAreaIndex s.substring:{}", s.substring(noNormalTypeStart,i + 1));
                }
                
            }
        }
        if (count > 0) {
            throw new RuntimeException("左边和右边不匹配");
        }
        return null;
    }
    
    private static boolean isMatchOf(String s, int startIndex, String sub,boolean handleZhuanYi) {
        
        int endIndex = startIndex + sub.length();
        if (endIndex > s.length()) {
            return false;
        }
        if (handleZhuanYi) {
            int prevXieGangCount = getPrevXieGangCount(s, startIndex);
            if (prevXieGangCount %2 != 0) {
                return false;
            }
        }
        for (int i = startIndex; i < endIndex; i++) {
            if (s.charAt(i) != sub.charAt(i - startIndex)) {
                return false;
            }
        }
        return true;
    }
    
    private static CodeType findCodeTypeOfStart(String s, int start) {
        int startOrdinal = 1;
        CodeType[] values = CodeType.values();
        int endOrdinal = values.length;
        
        for (int j = startOrdinal; j < endOrdinal; j++) {
            if (isMatchOf(s, start, values[j].start,false)) {
                return values[j];
            }
        }
        return CodeType.NORMAL;
    }
    
    private static boolean isSingleCommentEnd(char c) {
        return c == '\n';
    }
    
    private static boolean isSingleCommentStart(String fileContent, int i) {
        if (fileContent.length() <= i + 1) {
            return false;
        }
        return fileContent.charAt(i) == '/' && fileContent.charAt(i + 1) == '/';
    }
    
    private static boolean isMultiCommentStart(String fileContent, int i) {
        if (fileContent.length() <= i + 1) {
            return false;
        }
        return fileContent.charAt(i) == '/' && fileContent.charAt(i + 1) == '*';
    }
    
    private static boolean isMultiCommentEnd(String fileContent, int i) {
        if (fileContent.length() <= 1) {
            return false;
        }
        return fileContent.charAt(i) == '/' && fileContent.charAt(i - 1) == '*';
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
                        ret = Arrays.copyOf(ret, retSize * 2);
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
