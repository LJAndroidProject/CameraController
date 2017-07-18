package com.lj.cameracontroller.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ljs on 2017/6/28.
 * 字符串工具类
 */

public class StringUtils {

    /**
     * 判断字符串是否为空(null或空串)
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        } else if (str.trim().length() == 0) {
            return true;
        }
        return false;
    }


    /***
     * 判断字符串不为null 空字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty2(String str) {
        str = toNull(str);
        return isEmpty(str);
    }

    public static String toNull(String str) {
        if (str == null || "null".equals(str)) {
            return "";
        }
        return str;
    }


    /**
     * 验证手机格式 false代表不合格
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }

    /**
     * 判断字符串的字节长度是否在指定长度之类(注：如果是用utf-8使用中文，每个中文占3个字节，如果是用unicode则每个中文占2个字节)
     *
     * @param s
     * @param length
     * @return
     */
    public static boolean isOverLength(String s, int length) {
        if (isEmpty(s)) {
            return false;
        } else if (s.getBytes().length <= length) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断字符串长度是否在指定长度类(如果是纯中文则按照中文长度来判断，如果是纯英文则按照英文长度来判断，如果包含中英文则按英文长度来判断)
     *
     * @param s        要判断的字符串
     * @param clength  中文限制长度
     * @param eliength 英文限制长度
     * @return true 代表超过指定长度
     */
    public static boolean isOverLength(String s, int clength, int eliength) {
        char[] str = s.toCharArray();
        List<String> chars = new ArrayList<String>(); //中文集合
        List<String> english = new ArrayList<String>(); //英文集合
        int charsLength=0, EnglishLength=0;
        for (int i = 0; i < str.length; i++) {
            char c = str[i];
            if (isChinese(c)) {
                chars.add(String.valueOf(c));
            } else if(isEnglish(String.valueOf(c))){
                english.add(String.valueOf(c));
            }
        }
        charsLength = chars.size();
        EnglishLength = english.size();
        if (EnglishLength == 0 && charsLength > 0) { //纯中文
            if (charsLength > clength) {
                return true;
            } else {
                return false;
            }
        } else {// 中英文一起或者纯英文
            if ((charsLength + EnglishLength) > eliength) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 如果是中文则返回，如果是因为则转成大写返回
     *
     * @param str
     * @return
     */
    public static String ToUpperCase(String str) {
        String content = "";
        if (isEnglish(str)) {
            String string = str.replace(" ", "");//先去除字符串中所有空格
            content = string.trim().toUpperCase();
            return content;

        } else {
            return str;
        }
    }

    /***
     * 判断字符串是否是英文
     *
     * @param str
     * @return
     */
    public static boolean isEnglish(String str) {
        return str.matches("^[a-zA-Z]*");
    }

    /**
     * 判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumble(String str) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 根据Unicode编码完美的判断中文汉字和符号
     *
     * @param c
     * @return
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }
    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 判断是否含有表情
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        boolean isEmoji = false;
        for (int i = 0; i < len; i++) {
            char hs = source.charAt(i);
            if (0xd800 <= hs && hs <= 0xdbff) {
                if (source.length() > 1) {
                    char ls = source.charAt(i + 1);
                    int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                    if (0x1d000 <= uc && uc <= 0x1f77f) {
                        return true;
                    }
                }
            } else {
                // non surrogate
                if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
                    return true;
                } else if (0x2B05 <= hs && hs <= 0x2b07) {
                    return true;
                } else if (0x2934 <= hs && hs <= 0x2935) {
                    return true;
                } else if (0x3297 <= hs && hs <= 0x3299) {
                    return true;
                } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d
                        || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c
                        || hs == 0x2b1b || hs == 0x2b50 || hs == 0x231a) {
                    return true;
                }
                if (!isEmoji && source.length() > 1 && i < source.length() - 1) {
                    char ls = source.charAt(i + 1);
                    if (ls == 0x20e3) {
                        return true;
                    }
                }
            }
        }
        return isEmoji;
    }

    /**
     * 内容是否是符合中英文（包括字符）
     *
     * @return
     */
    public static boolean isMatch(String changeText) {
        boolean b = false;
        for (int i = 0; i < changeText.length(); i++) {
            String subStr = changeText.substring(i, i + 1);
            if (subStr.matches("[\u0000-\u024f]")
                    || subStr.matches("[\u3000-\u303F]")
                    || subStr.matches("[\u4e00-\u9fff]")
                    || subStr.matches("[\u20000-\u2A6D6]")
                    || subStr.matches("[\uFF00-\uFFEF]")) {
                if (i == changeText.length() - 1) {
                    b = true;
                }
            } else {
                break;
            }
        }
        return b;
    }


}
