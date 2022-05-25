package com.meide.register.converter.imp;

import com.meide.register.converter.TableConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DefaultTableConverter implements TableConverter {

    @Override
    public String getVoTableName(String dbTableName) {

       return lineToHump(dbTableName) ;
    }

    @Override
    public String getPageColumn(String dbColumn) {

       // return lineToHump(dbColumn) ;
        return dbColumn;
    }


    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String dbTableName) {
        dbTableName = dbTableName.toLowerCase();
        Matcher matcher = linePattern.matcher(dbTableName);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
