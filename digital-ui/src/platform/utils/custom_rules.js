const defaultTrigger = ["change", "blur"];
const requiredObject = {
  required: true,
  message: '不能为空',
  trigger: defaultTrigger
};
const requiredArray = [requiredObject];
const requiredArrayOnChange = [{
  required: true,
  message: '不能为空',
  trigger: 'change'
}];


function isEmpty(value) {
  return value === null || value === undefined || value === "";
}

const rules = {
  isEmpty: isEmpty,
  requiredOnChange(msg) {
    if (!msg) {
      return requiredArrayOnChange;
    }
    return [{required: true, message: msg, trigger: 'change'}];
  },
  required(msg, trigger) {
    if (!msg && !trigger) {
      return requiredArray;
    }
    return [{required: true, message: msg || "不能为空", trigger: trigger || defaultTrigger}];
  },
  phone(required, msg, trigger) {
    return this.reg(required, /^1(3|4|5|6|7|8|9)\d{9}$/, msg || "手机号码错误", trigger);
  },
  email(required, msg, trigger) {
    return this.reg(required, /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/, msg || "邮箱错误", trigger);
  },
  //限制长度 0 为不限制
  len(required, min, max, msg, trigger) {
    if (!msg) {
      if (min > 0 && max > 0) {
        msg = `${min}-${max}个字符`;
      } else if (min > 0) {
        msg = `至少${min}个字符`;
      } else if (max > 0) {
        msg = `最多${max}个字符`;
      }
    }
    return [{
      required: !!required,
      validator: (rule, value, callback) => {
        if (isEmpty(value)) {
          if (required) {
            callback(new Error("不能为空"));
          } else {
            callback();
          }
          return;
        }
        value += '';
        if (min > 0 || max > 0) {
          if (value.length < min && value.length > max) {
            callback(new Error(msg));
          } else if (min > 0 && value.length < min) {
            callback(new Error(msg));
          } else if (max > 0 && value.length > max) {
            callback(new Error(msg));
          } else {
            callback();
          }
        } else {
          callback();
        }
      },
      trigger: trigger || defaultTrigger
    }];
  },
  //默认是整数
  num(required, min, max, msg, trigger, scale) {
    return this.custom(required, (rule, value, callback) => {
      if (!required && isEmpty(value)) {
        callback();
        return;
      }
      //整数
      if (!value.match(/^\d+.?\d*$/g)) {
        callback(new Error(msg || "只能输入数字"));
        return;
      }
      let numVal = parseFloat(value);
      //应该是整数
      if (!scale) {
        if (numVal != Math.floor(numVal)) {
          callback(new Error(msg || "只能输入整数"));
          return;
        }
      } else {
        //判断精度
        let scaleStr = value.split('.')[1];
        if (scaleStr && scaleStr.length > scale) {
          callback(new Error(msg || "最多保留" + scale + "位小数"));
          return;
        }
      }
      //如果不限制大小
      let hasMin = !isEmpty(min);
      let hasMax = !isEmpty(max);

      if (!hasMin && !hasMax) {
        callback();
        return;
      }
      if (hasMin && hasMax) {
        if (numVal <= max && numVal >= min) {
          callback();
        } else {
          callback(new Error(msg || `数字应在${min}-${max}之间`));
        }
        return;
      }
      if (hasMin && !hasMax) {
        if (numVal >= min) {
          callback();
        } else {
          callback(new Error(msg || `数字应不能小于${min}`));
        }
        return;
      }
      if (!hasMin && hasMax) {
        if (numVal <= max) {
          callback();
        } else {
          callback(new Error(msg || `数字不能大于${max}`));
        }
        return;
      }
    }, trigger)
  },
  url(required, msg, trigger) {
    return this.reg(required, /http(s)?:\/\/(\w+\.)[\s\S]+/g, msg, trigger);
  },
  reg(required, reg, msg, trigger) {
    msg = msg || "格式错误";
    return [{
      required: !!required,
      validator: (rule, value, callback) => {
        if (isEmpty(value)) {
          if (required) {
            callback(new Error("不能为空"));
          } else {
            callback();
          }
          return;
        }
        value += '';
        if (!value.match(reg)) {
          callback(new Error(msg));
        } else {
          callback();
        }
      },
      trigger: trigger || defaultTrigger
    }];
  },
  //自定义 (rule, value, callback) =>{callback()}
  custom(required, validator, trigger, autoString = true) {
    return [{
      required: !!required,
      validator: (rule, value, callback) => {
        if (isEmpty(value)) {
          if (required) {
            callback(new Error("不能为空"));
          } else {
            validator(rule, value, callback);
          }
          return;
        }
        if (autoString && !(value instanceof Array)) {
          value += '';//这里统一处理成了字符串，注意下拉可能值有所不同。
        }
        validator(rule, value, callback);
      },
      trigger: trigger || defaultTrigger
    }];
  },
};

export default {
  rules: rules,
  install: function (Vue, options) {
    Vue.prototype.$rules = rules;
  },
  // required required('填写用户名')   
  getRuleByString(s) {
    if (!s) {
      return null;
    }
    if (!s.match("\\(")) {
      s += "()";
    }
    s = "rules." + s;
    return eval(s);
  }

}
