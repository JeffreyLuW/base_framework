package com.meide.dbengine.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 可以根据正则，挨个替换指定值。
 * 可以替换格式化 {name} 占位的字符串。
 * 不需要格式化的,前面加\ 如： \{name}
 */
public class StringReplacer {

    //用map中的值替换{name}样式的占位
    public static String replace(String s, Map<String, String> map) {
        return replace(s, (matchPattern, raw) -> {
            if (matchPattern) {
                return map.get(raw);
            }
            return raw;
        });
    }


    //自定义替换{name}样式的占位
    public static String replace(String s, Replacer replacer) {
        return replace(s, "\\{[\\s\\S]+?\\}", replacer, true);
    }

    /**
     * 自定义正则表达式，替换正则匹配的值
     *
     * @param s
     * @param reg
     * @param replacer
     * @param enableEndsFlag true时，如果前面有转义\ 则matchPattern=false，可以避免替换。
     * @return
     */
    public static String replace(String s, String reg, Replacer replacer, boolean enableEndsFlag) {
        if (s == null) return s;
        Pattern pattern = Pattern.compile(reg);
        Matcher m = pattern.matcher(s);
        StringBuilder sb = new StringBuilder();
        int last = 0;
        while (m.find()) {
            String before = s.substring(last, m.start());
            String current = m.group();
            last = m.end();
            if (replacer != null) {
                boolean endsFlag = false;
                if (enableEndsFlag) {
                    endsFlag = before.endsWith("\\");
                    if (endsFlag)
                        before = before.length() > 1 ? before.substring(0, before.length() - 1) : "";
                }
                before = replacer.replace(false, before);
                if (endsFlag) {
                    current = replacer.replace(false, current);
                } else {
                    current = replacer.replace(true, current.substring(1, current.length() - 1));
                }
            }
            sb.append(before);
            sb.append(current);
        }
        if (last < s.length()) {
            sb.append(s.substring(last));
        }
        return sb.toString();
    }


    public interface Replacer {
        String replace(boolean matchPattern, String raw);
    }

}
