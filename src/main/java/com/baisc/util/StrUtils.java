package com.baisc.util;

/**
 * @author 橙子
 * @date 2020/1/12 11:20 下午
 * 字符串简单工具类
 */
public class StrUtils {

    /**
     * 第一个字符大写
     * @param toUpper
     * @return
     */
    public static String firstCharUpper(String toUpper){
        return toUpper.substring(0,1).toUpperCase().concat(toUpper.substring(1));
    }
}
