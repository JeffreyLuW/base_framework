//获取错误提示
export function getErrorMsg(ruleObj) {
  if (!ruleObj) {
    return "";
  }
  if (typeof ruleObj === "string") {
    ruleObj = parseRule(ruleObj);
  }
  let msgs = [];
  if (ruleObj.wordCase) {
    msgs.push("大小写字母");
  }
  if (ruleObj.num) {
    msgs.push("数字");
  }
  if (ruleObj.sign) {
    msgs.push("特殊符号");
  }
  if (ruleObj.lenMin && ruleObj.lenMax) {
    msgs.push(ruleObj.lenMin + "-" + ruleObj.lenMax + "个字符");
  } else if (ruleObj.lenMin) {
    msgs.push(ruleObj.lenMin + "个字符");
  }
  return "需要" + msgs.join(" ");
}

//解析规则
function parseRule(rule) {
  let rs = {
    wordCase: false,//同时含有大小写
    num: false,//需要有数字
    sign: false,//需要有特殊符号
    lenMin: 5,//最小长度
    lenMax: 0,//最大长度
    expire: 0 // 最长修改期限
  };
  if (!rule || rule.indexOf("=") === -1) {
    return rs;
  }
  rule.trim().split("&").forEach((kvStr) => {
    let kvArr = kvStr.split("=");
    let key = kvArr[0];
    let value = kvArr[1];
    if (!value) return;
    if (key === "type") {
      let typeMap = {};
      value.split("|").forEach((t) => {
        typeMap[t] = true;
      });
      rs.wordCase = !!typeMap['wordCase'];
      rs.num = !!typeMap['num'];
      rs.sign = !!typeMap['sign'];
    } else if (key === "len") {
      let lenArr = value.trim().split("-");
      if (lenArr.length >= 1) {
        rs.lenMin = parseInt(lenArr[0]);
      }
      if (lenArr.length >= 2) {
        rs.lenMax = parseInt(lenArr[1]);
      }
    } else if (key === "expire") {
      rs.expire = parseInt(value);
    }
  });

  return rs;
}

//获取表单密码的验证rule
export function autoCheckPwdRules(vueComponent) {
  return [
    {required: true, message: "密码不能为空", trigger: ["change", "blur"]},
    {
      validator: (rule, value, callback) => {
        if (value === null || value === undefined || value === "") {
          callback(new Error("密码不能为空"));
          return;
        }
        //获取系统配置。根据
        let pwdRuleConfig = vueComponent.$store.state.settings.sysConfigs['pwd.security.rules'];
        let pwdRule = pwdRuleConfig && pwdRuleConfig.configValue;
        if (!pwdRule) {
          callback();
          return;
        }
        let rs = checkPwd(pwdRule, value);
        if (rs.ok) {
          callback();
        } else {
          callback(rs.msg);
        }
      }
      , trigger: ["change", "blur"]
    }]
}

/**
 *  通过特定规则，校验密码是否符合
 * @param rule type=word|num|sign&len=6-18&expire=365
 * @param value
 * @return {{ok: boolean}|{msg: *, ok: boolean}|{msg: string, ok: boolean}}
 */
function checkPwd(rule, value) {
  if (value === null || value === undefined || value === "") {
    return {
      ok: false,
      msg: "密码不能为空"
    };
  }
  if (!rule) {
    return {ok: true};
  }
  value += '';
  let ruleObj = parseRule(rule);
  //验证是否有大小写
  if (ruleObj.wordCase) {
    let hasUpper = !!value.match(/[a-z]/);
    let hasLower = !!value.match(/[A-Z]/);
    let isWordCase = hasUpper && hasLower;
    if (!isWordCase) {
      return {ok: false, msg: getErrorMsg(ruleObj)};
    }
  }
  if (ruleObj.num) {
    if (!value.match(/[0-9]/)) {
      return {ok: false, msg: getErrorMsg(ruleObj)};
    }
  }
  if (ruleObj.sign) {
    //去掉字母 数字为空的话，表示没有特殊字符
    if (!value.replace(/[a-zA-Z0-9]/g, '')) {
      return {ok: false, msg: getErrorMsg(ruleObj)};
    }
  }
  if (ruleObj.lenMin && value.length < ruleObj.lenMin
    || ruleObj.lenMax && value.length > ruleObj.lenMax) {
    return {ok: false, msg: getErrorMsg(ruleObj)};
  }
  return {ok: true};
}

export default {
  checkPwd, // (rule,value):{ok:boolean,msg:string}
  autoCheckPwdRules,// (vueComponent):[{validator:...}]
  parseRule,
}

