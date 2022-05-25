/**
 * 邮箱
 * @param {*} s
 */
export function isEmail (s) {
  return /^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(s)
}

/**
 * 手机号码
 * @param {*} s
 */
export function isMobile (s) {
  return /^1\d{10}$/.test(s)
}

/**
 * 电话号码
 * @param {*} s
 */
export function isPhone (s) {
  return /^([0-9]{3,4}-)?[0-9]{7,8}$/.test(s)
}

/**
 * URL地址
 * @param {*} s
 */
export function isURL (s) {
  return /^http[s]?:\/\/.*/.test(s)
}

export function isIP (s) {
  return /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/.test(s)
}

/**
 * 身份证号
 * @param {*} s
 */
export function isIdCrad (s) {
  return /^\d{6}(18|19|20)?\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|[xX])$/.test(s)
}

export function passwordsReg (s) {
  return /^[0-9 a-z A-Z]{6,8}$/.test(s)
}

//是否含有特殊字符 有则返回false 无则返回true
export function isSpecialKey(str) {
  var specialKey = "[`~!$^*%()=|{}':;'\\[\\].<>/?~！#￥……*（）——|{}【】‘；：”“'。，、？]‘'";
  for (var i = 0; i < str.length; i++) {
    if (specialKey.indexOf(str.substr(i, 1)) != -1) {
      return false;
    }
  }
  return true;
}

//是否中文
export function isChinese (s) {
  // return /^[\u4e00-\u9fa5]+$/.test(s)s
  for (var i = 0; i < s.length; i++) {
    if (/^[\u4e00-\u9fa5]+$/.test(s.substr(i, 1))== true) {
      return false;
    }
  }
  return true;
}
//是否空格
export function isSpace (s) {
  if(/^\s*(\S+)\s*$/.test(s) == true){
    return false;
  }
  return true;
}
