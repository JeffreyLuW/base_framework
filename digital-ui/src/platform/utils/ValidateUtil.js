const phoneReg = /(^0[0-9]{2,3}\-?[0-9]{3,8}$)|(^[0-9]{3,8}$)|(^1[0-9]{10}$)/;
const mobileReg = /^1[3456789]\d{9}$/; //手机规则
const numReg = /^\-?\d+\.?\d{0,2}$/g; // 2 位小数的reg
const numReg3 = /^\-?\d+\.?\d{0,3}$/g; // 3 位小数的reg
const numReg4 = /^\-?\d+\.?\d{0,4}$/g; // 4位小数的reg
const num = /^(0|\+?[1-9][0-9]*)$/; //非负整整
const sfzReg = /^[\dXx]{15,32}$/g;
const emailReg = /^\w+@\w+(\.\w)+\w+$/g;
const ipReg = /^((?:(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d))))$/g; //IP 正则
const zipReg = /^[1-9][0-9]{5}$/;
const patter = /((^[1-9]\d*)|^0)(\.\d{0,2}){0,1}$/; //非负整整或者至多保留两位小数


class ValidateUtil {

  required() {
    return [{
      required: true,
      message: '不能为空',
      trigger: ['blur', 'change']
    }, ]
  }

  //时间，不能超过当前时间
  dateNoFuture(required = true, futureMsg) {
    return [{
      validator: (rule, value, callback) => {
        let empty = (!value || value.length <= 0);
        if (!required && empty) {
          callback();
          return;
        }
        if (required && empty) {
          callback(new Error("不能为空"));
          return;
        }
        if (value instanceof Date) {
          if (Date.now() < value.getTime()) {
            callback(new Error("不能大于当前时间"));
            return;
          }
          callback();
          return;
        }
        //yyyy-MM-dd hh:mm:ss
        let checkMap = {
          4: 'yyyy',
          7: 'yyyy-MM',
          10: 'yyyy-MM-dd',
          13: 'yyyy-MM-dd hh',
          16: 'yyyy-MM-dd hh:mm',
          19: 'yyyy-MM-dd hh:mm:ss'
        };
        let rightFmt = checkMap[value.length];
        if (rightFmt) {
          let now = new Date().format(rightFmt);
          if (value > now) {
            callback(new Error(futureMsg ? futureMsg : ("不能大于" + now)));
            return;
          }
        }
        callback();
      },
      trigger: ['blur', 'change']
    }, ];
  }

  requiredArray(message = '不能为空') {
    return [{
        required: true,
        message,
        trigger: ['blur', 'change']
      },
      {
        validator: (rule, value, callback) => {
          if (!value || value.length <= 0) {
            callback(new Error(message));
          } else {
            callback();
          }
        },
        trigger: ['blur', 'change']
      },
    ]
  }

  //限制输入长度
  requiredLength(min = 0, max = 50) {
    return [{
        required: true,
        message: '不能为空',
        trigger: ['blur', 'change']
      },
      {
        validator: (rule, value, callback) => {
          value = this.fixStringVal(value);
          if (!value) {
            callback(new Error('不能为空'));
          } else if (min && value.length < min) {
            callback(new Error('不能小于' + min + '个字符'));
          } else if (max && value.length > max) {
            callback(new Error('不能大于' + max + '个字符'));
          } else {
            callback();
          }
        },
        trigger: 'blur'
      },
    ]
  }

  //限制输入长度
  noRequiredButLength(min = 0, max = 50) {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value) {
          callback();
        } else if (min && value.length < min) {
          callback(new Error('不能小于' + min + '个字符'));
        } else if (max && value.length > max) {
          callback(new Error('不能大于' + max + '个字符'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }, ]
  }

  //身份证
  requiredSfz() {
    return [{
        required: true,
        message: '不能为空',
        trigger: 'blur'
      },
      {
        validator: (rule, value, callback) => {
          value = this.fixStringVal(value);
          if (!value) {
            callback(new Error('不能为空'));
          } else if (!value.match(sfzReg)) {
            callback(new Error('格式错误'));
          } else {
            callback();
          }
        },
        trigger: 'blur'
      },
    ]
  }

  //身份证
  noRequiredSfz() {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value) {
          callback();
        } else if (!value.match(sfzReg)) {
          callback(new Error('格式错误'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }, ]
  }

  //非必填的邮箱
  noRequiredEmail() {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value) {
          callback();
        } else if (!value.match(emailReg)) {
          callback(new Error('邮箱格式错误'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }, ]
  }

  //必填IP
  requiredIP() {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value) {
          callback(new Error('不能为空'));
        } else if (!value.match(ipReg)) {
          callback(new Error('IP地址错误'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }, ]
  }

  //电话
  requiredPhone() {
    return [{
        required: true,
        message: '不能为空',
        trigger: 'blur'
      },
      {
        validator: (rule, value, callback) => {
          value = this.fixStringVal(value);
          if (!value) {
            callback(new Error('不能为空'));
          } else if (!value.match(phoneReg)) {
            callback(new Error('电话格式错误'));
          } else {
            callback();
          }
        },
        trigger: 'blur'
      },
    ]
  }


  noRequiredPhone() {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value) {
          callback();
        } else if (!value.match(phoneReg)) {
          callback(new Error('电话格式错误'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }, ]
  }

  requiredMobile() {
    return [{
        required: true,
        message: '不能为空',
        trigger: 'blur'
      },
      {
        validator: (rule, value, callback) => {
          value = this.fixStringVal(value);
          if (!value) {
            callback(new Error('不能为空'));
          } else if (!value.match(mobileReg)) {
            callback(new Error('手机号码错误'));
          } else {
            callback();
          }
        },
        trigger: 'blur'
      },
    ]
  }

  // add begin by zhangqianqian 2019-10-31 添加只验证手机号格式不验证空
  noRequiredMobile() {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value) {
          callback();
        } else if (!value.match(mobileReg)) {
          callback(new Error('手机号码错误'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }, ]
  }

  // add end by zhangqianqian 2019-10-31  添加只验证手机号格式不验证空
  // 大于等于0  不超过两位小数
  requiredDefaultNum(min = 0, max = 99999999999999, fixed = 2) {
    return [{
        required: true,
        message: '不能为空',
        trigger: 'blur'
      },
      {
        validator: (rule, value, callback) => {
          value = this.fixStringVal(value);
          if (!value) {
            callback(new Error('不能为空'));
          } else {
            if ((fixed === 2 && !value.match(numReg)) ||
              (fixed === 3 && !value.match(numReg3)) ||
              (fixed === 4 && !value.match(numReg4))) {
              callback(new Error(`请填写数字且最多${fixed}位小数`));
            } else {
              this.callbackMinMax(value, min, max, callback);
            }
          }
        },
        trigger: 'blur'
      }
    ]
  }

  // 大于等于0  不超过两位小数
  noRequiredDefaultNum(min = 0, max = 99999999999999, fixed = 2) {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value) {
          callback();
        } else {
          if ((fixed === 2 && !value.match(numReg)) ||
            (fixed === 3 && !value.match(numReg3)) ||
            (fixed === 4 && !value.match(numReg4))) {
            callback(new Error(`请填写数字且最多${fixed}位小数`));
          } else {
            this.callbackMinMax(value, min, max, callback);
          }
        }
      },
      trigger: 'blur'
    }]
  }

  //非必填经纬度  数字，可以有负 不多于14位小数
  noRequiredLonLat() {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value) {
          callback();
        } else if (!value.match(/^\-?\d+\.?\d{0,14}$/g)) {
          callback(new Error('经纬度格式错误'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }]
  }

  //默认非零整数
  requiredInt(min = 0, max = 2147483647) {
    return [{
        required: true,
        message: '不能为空',
        trigger: 'blur'
      },
      {
        validator: (rule, value, callback) => {
          value = this.fixStringVal(value);
          if (!value.match(/^\-?\d+$/g)) {
            callback(new Error('请填写整数'));
          } else {
            this.callbackMinMax(value, min, max, callback);
          }
        },
        trigger: 'blur'
      }
    ]
  }
  //验证非负整数
  norequiredInt(min = 0, max = 2147483647) {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value.match(/^\-?\d+$/g)) {
          callback(new Error('请填写整数'));
        } else {
          this.callbackMinMax(value, min, max, callback);
        }
      },
      trigger: 'blur'
    }]
  }

  nullOrUndef(v) {
    return v === null || v === undefined;
  }

  //检查最大最小值.不检查是否是整数 必须保证callback调用
  callbackMinMax(value, min, max, callback) {
    if (!this.nullOrUndef(min) || !this.nullOrUndef(max)) {
      let intVal = parseFloat(value);
      if (!this.nullOrUndef(min) && intVal < min) {
        callback(new Error('不能小于' + min));
        return;
      }
      if (!this.nullOrUndef(max) && intVal > max) {
        callback(new Error('不能大于' + max));
        return;
      }
    }
    callback();
  }

  //检查是否保留两位小数
  callbackNum(value, min, max, callback) {
    if (!value) {
      callback();
    } else if (!value.match(numReg)) {
      callback(new Error(`请填写数字且最多2位小数`));
    } else {
      this.callbackMinMax(value, min, max, callback);
    }
  }

  noRequiredInt(min = 0, max = 2147483647) {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value) {
          callback();
        } else if (!value.match(/^\-?\d+$/g)) {
          callback(new Error('请填写整数'));
        } else {
          this.callbackMinMax(value, min, max, callback);
        }
      },
      trigger: 'blur'
    }]
  }

  // 0-100
  requiredPercent(min = 0, max = 100) {
    return [{
        required: true,
        message: '不能为空',
        trigger: 'blur'
      },
      {
        validator: (rule, value, callback) => {
          value = this.fixStringVal(value);
          if (!value) {
            callback(new Error('不能为空'));
          } else if (!value.match(/^\d+\.?\d{0,2}$/g)) {
            callback(new Error('请填写数字且最多两位小数'));
          } else {
            this.callbackMinMax(value, min, max, callback);
          }
        },
        trigger: 'blur'
      }
    ]
  }

  noRequiredPercent(min = 0, max = 100) {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value) {
          callback();
        } else if (!value.match(/^\d+\.?\d{0,2}$/g)) {
          callback(new Error('请填写正数且最多两位小数'));
        } else {
          this.callbackMinMax(value, min, max, callback);
        }
      },
      trigger: 'blur'
    }]
  }

  /*常常用于检测输入框的值，其值需要时字符串*/
  fixStringVal(value) {
    if (value != null && value != undefined && typeof value !== 'string') {
      value += '';
    }
    return value;
  }

  //用于人工校验rule的预处理
  prepareRules(rulesObj) {
    if (rulesObj) {
      Object.keys(rulesObj).forEach((prop) => {
        rulesObj[prop + "_err"] = null;
      });
    }
    return rulesObj;
  }

  //人工校验 rulesObj的全部规则。
  manualValidAll(form, rulesObj, cb) {
    if (rulesObj) {
      let keys = Object.keys(rulesObj);
      let c = keys.length;
      let checkCount = 0;
      let errCount = 0;
      let checkOver = (valid) => {
        checkCount++;
        if (!valid) errCount++;
        if (checkCount >= c) {
          if (cb) cb(errCount === 0);
        }
      };
      keys.forEach((prop) => {
        this.manualValid(form, rulesObj, prop, checkOver)
      });
    } else {
      if (cb) cb(true);
    }
  }

  //验证的错误重置
  manualValidReset(rulesObj) {
    if (rulesObj) {
      Object.keys(rulesObj).filter((v) => v.endsWith("_err")).forEach((key) => {
        rulesObj[key] = null;
      });
    }
  }

  //人工校验  自动给rulesObj的同属性_err 添加错误提示。
  manualValid(form, rulesObj, prop, cb) {
    let value = form[prop];
    let rules = rulesObj[prop];
    rulesObj[prop + '_err'] = null;
    let hasCalled = false;
    if (rules) {
      let checkCount = 0;
      let checkOneOverFn = () => {
        checkCount++;
        //有异常 立刻提示
        if (rulesObj[prop + '_err'] && !hasCalled) {
          hasCalled = true;
          if (cb) cb(false, rulesObj[prop + '_err']);
          return;
        }
        //无异常，但已全检测完 提示
        if (checkCount >= rules.length) {
          if (cb) cb(true);
        }
      };
      for (let i = 0; i < rules.length; i++) {
        if (hasCalled) break;
        let item = rules[i];
        if (item.required) {
          if (!value) {
            rulesObj[prop + '_err'] = item.message || '不能为空';
          }
          checkOneOverFn();
        }
        // validator: (rule, value, callback)
        else if (item.validator) {
          item.validator(item, value, (err) => {
            if (err) {
              rulesObj[prop + '_err'] = err.message || '请检查输入项';
            }
            checkOneOverFn();
          });
        } else {
          checkOneOverFn();
        }
      }
    } else {
      if (cb) cb(true);
    }
  }


  // 邮政编码校验
  noRequiredZip() {
    return [{
      validator: (rule, value, callback) => {
        value = this.fixStringVal(value);
        if (!value) {
          callback();
        } else if (!value.match(zipReg)) {
          callback(new Error('邮编格式错误'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }, ]
  }


  //必须非负整数或至多保留两位小数
  isNumber() {
    return [{
        required: true,
        message: '不能为空',
        trigger: 'blur'
      },
      {
        validator: (rule, value, callback) => {
          if (!patter.test(value)) {
            return callback(new Error('必须非负整数或至多保留两位小数'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      },
    ]
  }
  //请输入非负整数
  isNum() {
    return [{
      validator: (rule, value, callback) => {
        if (!num.test(value)) {
          return callback(new Error('请输入非负整数'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }, ]
  }

}

export default new ValidateUtil();
