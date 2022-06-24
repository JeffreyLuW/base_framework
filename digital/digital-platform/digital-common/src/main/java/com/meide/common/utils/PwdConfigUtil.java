package com.meide.common.utils;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Data
public class PwdConfigUtil {
    //密码必须拥有的字符类型
    private List<String> type;
    //密码最小长度
    private Integer minLen;
    //密码最大长度
    private Integer maxLen;
    //强制改密码时间，单位：日
    private Integer expire;

    public static void main(String[] args) {
        PwdConfigUtil init = init("type=wordCase|num|sign&len=6-18&expire=365");
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.print("请输入密码：");
            String next = sc.next();
            if ("exit".equals(next)) {
                break;
            }
            String matcher = init.matcher(next);
            if (StrUtil.isBlank(matcher)) {
                System.out.println("密码合规");
            } else {
                System.out.println(matcher);
            }
        }
    }


    public static PwdConfigUtil init(String configStr) {
        PwdConfigUtil pwdConfigUtil = new PwdConfigUtil();
        if (StrUtil.isBlank(configStr)) {
            return pwdConfigUtil;
        }
        String[] split = configStr.split("&");
        Map<String, String> map = Arrays.stream(split).collect(Collectors.toMap(str -> str.split("=")[0], str -> str.split("=")[1]));
        String type = map.get("type");
        if (StrUtil.isNotBlank(type)) {
            pwdConfigUtil.setType(Arrays.asList(type.split("\\|")));
        }
        String len = map.get("len");
        if (StrUtil.isNotBlank(len)) {
            String[] lens = len.split("-");
            pwdConfigUtil.setMinLen(Integer.parseInt(lens[0]));
            pwdConfigUtil.setMaxLen(Integer.parseInt(lens[1]));
        }
        String expire = map.get("expire");
        if (StrUtil.isNotBlank(expire)) {
            pwdConfigUtil.setExpire(Integer.parseInt(expire));
        }
        return pwdConfigUtil;
    }

    public String matcher(String password) {
        StrBuilder strBuilder = StrBuilder.create();
        //判断长度
        if (minLen != null && maxLen != null && (minLen > password.length() || maxLen < password.length())) {
            strBuilder.append(StrUtil.format("密码长度需要在{}-{}范围内；", minLen, maxLen));
        }
        if (type != null && type.contains("wordCase") && !password.matches(".*[A-Z]{1,}.*")) {
            strBuilder.append("密码需要包含大写字母；");
        }
        if (type != null && type.contains("wordCase") && !password.matches(".*[a-z]{1,}.*")) {
            strBuilder.append("密码需要包含小写字母；");
        }
        if (type != null && type.contains("num") && !password.matches(".*[0-9]{1,}.*")) {
            strBuilder.append("密码需要包含数字；");
        }
        if (type != null && type.contains("sign") && !password.matches(".*[^a-zA-Z0-9]{1,}.*")) {
            strBuilder.append("密码需要包含特殊字符；");
        }
        return strBuilder.isEmpty() ? null : strBuilder.toString().substring(0, strBuilder.length() - 1);
    }
}
