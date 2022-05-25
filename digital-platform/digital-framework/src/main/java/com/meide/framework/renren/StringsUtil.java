package com.meide.framework.renren;

import com.alibaba.druid.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiay
 **/
public class StringsUtil {

  public final static String regex =
      "'|%|--|and|or|not|use|insert|delete|update|select|count|group|union" +
          "|create|drop|truncate|alter|grant|execute|exec|xp_cmdshell|call|declare|source|sql" ;

  /**
   * 把SQL关键字替换为空字符串
   */
  public static Boolean filter(String param) {
    if (param == null) {
      return false;
    }
    String newparam = param.replaceAll("(?i)" + regex, "");  //(?i)不区分大小写替换
    if (param.length() != newparam.length()) {
      return false;
    }
    return true;
  }

  public static Boolean isEmpty(String s) {
    if (s == null || StringUtils.isEmpty(s.trim().trim())) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 手机号验证
   */
  public static boolean checkMobileNumber(String mobileNumber) {
    boolean flag = false;
    try {
      if (mobileNumber.length() != 11) {
        return false;
      }
      Pattern regex = Pattern.compile(
          "^1[345678]\\d{9}$");
      //^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$
      Matcher matcher = regex.matcher(mobileNumber);
      flag = matcher.matches();
    } catch (Exception e) {
      flag = false;
    }
    return flag;
  }

  public static int lengthOfUTF8(String value) {
    int valueLength = 0;
    if (value == null) {
      return valueLength;
    }
    String chinese = "[\u0391-\uFFE5]" ;
    /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为3，否则为1 */
    for (int i = 0; i < value.length(); i++) {
      /* 获取一个字符 */
      String temp = value.substring(i, i + 1);
      /* 判断是否为中文字符 */
      if (temp.matches(chinese)) {
        /* 中文字符长度为3 */
        valueLength += 3;
      } else {
        /* 其他字符长度为1 */
        valueLength += 1;
      }
    }
    return valueLength;
  }

  /**
   * 判断是否是中文
   *
   * @param str
   * @return
   */
  public static boolean isChinese(String str) {

    String regEx = "[\\u4e00-\\u9fa5]+";

    Pattern p = Pattern.compile(regEx);

    Matcher m = p.matcher(str);

    if (m.find()) {

      return true;
    } else {

      return false;
    }

  }

  /**
   * 银行卡校验
   */
  public static boolean checkBankCard(String cardId) {
    char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
    if (bit == 'N') {
      return false;
    }
    return cardId.charAt(cardId.length() - 1) == bit;
  }

  /**
   * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
   */
  public static char getBankCardCheckCode(String nonCheckCodeCardId) {
    if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
        || !nonCheckCodeCardId.matches("\\d+")) {
      //如果传的不是数据返回N
      return 'N';
    }
    char[] chs = nonCheckCodeCardId.trim().toCharArray();
    int luhmSum = 0;
    for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
      int k = chs[i] - '0';
      if (j % 2 == 0) {
        k *= 2;
        k = k / 10 + k % 10;
      }
      luhmSum += k;
    }
    return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
  }


  // 查找方法
  public static int binarySearch(int[] srcArray, int des) {
    int low = 0;
    int high = srcArray.length;
    while (low < high) {

      if (des == srcArray[low]) {
        return low;
      }
      low++;
    }
    return -1;
  }
  //================================================验证身份证=======================================================================
  /**
   * 省、直辖市代码表：
   */
  private static String cityCode[] = {"11" , "12" , "13" , "14" , "15" , "21" ,
      "22" , "23" , "31" , "32" , "33" , "34" , "35" , "36" , "37" , "41" , "42" ,
      "43" , "44" , "45" , "46" , "50" , "51" , "52" , "53" , "54" , "61" , "62" ,
      "63" , "64" , "65" , "71" , "81" , "82" , "91"};

  /**
   * 每位加权因子
   */
  private static int power[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,
      8, 4, 2};

  /**
   * 验证所有的身份证的合法性
   *
   * @param idcard 身份证
   * @return 合法返回true，否则返回false
   */
  public static boolean isValidatedAllIdcard(String idcard) {
    if (idcard == null || "".equals(idcard)) {
      return false;
    }
    idcard = idcard.trim();
    if (idcard.length() == 15) {
      return validate15IDCard(idcard);
    }
    return validate18Idcard(idcard);
  }

  /**
   * 判断18位身份证的合法性
   */
  public static boolean validate18Idcard(String idcard) {
    if (idcard == null) {
      return false;
    }

    // 非18位为假
    if (idcard.length() != 18) {
      return false;
    }
    // 获取前17位
    String idcard17 = idcard.substring(0, 17);

    // 前17位全部为数字
    if (!isDigital(idcard17)) {
      return false;
    }

    String provinceid = idcard.substring(0, 2);
    // 校验省份
    if (!checkProvinceid(provinceid)) {
      return false;
    }

    // 校验出生日期
    String birthday = idcard.substring(6, 14);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    try {
      Date birthDate = sdf.parse(birthday);
      String tmpDate = sdf.format(birthDate);
      if (!tmpDate.equals(birthday)) {// 出生年月日不正确
        return false;
      }

    } catch (ParseException e1) {

      return false;
    }

    // 获取第18位
    String idcard18Code = idcard.substring(17, 18);

    char c[] = idcard17.toCharArray();

    int bit[] = converCharToInt(c);

    int sum17 = 0;

    sum17 = getPowerSum(bit);

    // 将和值与11取模得到余数进行校验码判断
    String checkCode = getCheckCodeBySum(sum17);
    if (null == checkCode) {
      return false;
    }
    // 将身份证的第18位与算出来的校码进行匹配，不相等就为假
    if (!idcard18Code.equalsIgnoreCase(checkCode)) {
      return false;
    }

    return true;
  }

  /**
   * 校验15位身份证
   */
  public static boolean validate15IDCard(String idcard) {
    if (idcard == null) {
      return false;
    }
    // 非15位为假
    if (idcard.length() != 15) {
      return false;
    }

    // 15全部为数字
    if (!isDigital(idcard)) {
      return false;
    }

    String provinceid = idcard.substring(0, 2);
    // 校验省份
    if (!checkProvinceid(provinceid)) {
      return false;
    }

    String birthday = idcard.substring(6, 12);

    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

    try {
      Date birthDate = sdf.parse(birthday);
      String tmpDate = sdf.format(birthDate);
      if (!tmpDate.equals(birthday)) {// 身份证日期错误
        return false;
      }

    } catch (ParseException e1) {

      return false;
    }

    return true;
  }

  /**
   * 将15位的身份证转成18位身份证
   */
  public static String convertIdcarBy15bit(String idcard) {
    if (idcard == null) {
      return null;
    }

    // 非15位身份证
    if (idcard.length() != 15) {
      return null;
    }

    // 15全部为数字
    if (!isDigital(idcard)) {
      return null;
    }

    String provinceid = idcard.substring(0, 2);
    // 校验省份
    if (!checkProvinceid(provinceid)) {
      return null;
    }

    String birthday = idcard.substring(6, 12);

    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

    Date birthdate = null;
    try {
      birthdate = sdf.parse(birthday);
      String tmpDate = sdf.format(birthdate);
      if (!tmpDate.equals(birthday)) {// 身份证日期错误
        return null;
      }

    } catch (ParseException e1) {
      return null;
    }

    Calendar cday = Calendar.getInstance();
    cday.setTime(birthdate);
    String year = String.valueOf(cday.get(Calendar.YEAR));

    String idcard17 = idcard.substring(0, 6) + year + idcard.substring(8);

    char c[] = idcard17.toCharArray();
    String checkCode = "" ;

    // 将字符数组转为整型数组
    int bit[] = converCharToInt(c);

    int sum17 = 0;
    sum17 = getPowerSum(bit);

    // 获取和值与11取模得到余数进行校验码
    checkCode = getCheckCodeBySum(sum17);

    // 获取不到校验位
    if (null == checkCode) {
      return null;
    }
    // 将前17位与第18位校验码拼接
    idcard17 += checkCode;
    return idcard17;
  }

  /**
   * 校验省份
   *
   * @return 合法返回TRUE，否则返回FALSE
   */
  private static boolean checkProvinceid(String provinceid) {
    for (String id : cityCode) {
      if (id.equals(provinceid)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 数字验证
   */
  private static boolean isDigital(String str) {
    return str.matches("^[0-9]*$");
  }

  /**
   * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
   */
  private static int getPowerSum(int[] bit) {

    int sum = 0;

    if (power.length != bit.length) {
      return sum;
    }

    for (int i = 0; i < bit.length; i++) {
      for (int j = 0; j < power.length; j++) {
        if (i == j) {
          sum = sum + bit[i] * power[j];
        }
      }
    }
    return sum;
  }

  /**
   * 将和值与11取模得到余数进行校验码判断
   *
   * @return 校验位
   */
  private static String getCheckCodeBySum(int sum17) {
    String checkCode = null;
    switch (sum17 % 11) {
      case 10:
        checkCode = "2" ;
        break;
      case 9:
        checkCode = "3" ;
        break;
      case 8:
        checkCode = "4" ;
        break;
      case 7:
        checkCode = "5" ;
        break;
      case 6:
        checkCode = "6" ;
        break;
      case 5:
        checkCode = "7" ;
        break;
      case 4:
        checkCode = "8" ;
        break;
      case 3:
        checkCode = "9" ;
        break;
      case 2:
        checkCode = "x" ;
        break;
      case 1:
        checkCode = "0" ;
        break;
      case 0:
        checkCode = "1" ;
        break;
    }
    return checkCode;
  }

  /**
   * 将字符数组转为整型数组
   */
  private static int[] converCharToInt(char[] c) throws NumberFormatException {
    int[] a = new int[c.length];
    int k = 0;
    for (char temp : c) {
      a[k++] = Integer.parseInt(String.valueOf(temp));
    }
    return a;
  }
  //================================================验证身份证=======================================================================
}
