/**
 * 创建默认的表单组件定义
 * @param label
 * @param propName
 * @param propValue
 * @param rules
 * @param options
 * @param freeze 默认freeze冻结属性变化。如果需要动态修改组件属性，可以改为false
 * @return {{rules: *, propValue: *, propName: *, propObject: any}}
 */
function createDefaultDefine(label, propName, propValue, rules, options, freeze = false) {
  let propObject = {
    label: label,
    prop: propName,
    ...options
  };
  if (options && options.__init) {
    freeze = false;
  }
  return {
    propName: propName,
    propValue: propValue,
    rules: rules,
    propObject: freeze ? Object.freeze(propObject) : propObject
  };
}

export default {
  /**
   * 可以扩展各种不同属性的快速创建
   * 只需要返回对应的固定结构即可
   * rules可以使用 custom_rules.js 快速创建
   * 创建的属性集合 propObject ，可以传递 __init(key, value, rs) 二次自定义配置。
   */
  itemCreator: {
    //占位使用 需要在指定位置 指定 type type_attrs 各种属性和事件。
    holder: function (label, propName, propValue, rules) {
      return createDefaultDefine(label, propName, propValue, rules);
    },

    textarea: function (label, propName, propValue, rules, placeholder, rows, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "el-input",
        type_type: "textarea",
        type_rows: rows || 4,
        type_clearable: true,
        type_placeholder: placeholder || '请输入' + label,
        ...elseOpts
      });
    },
    password: function (label, propName, propValue, rules, placeholder, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "el-input",
        type_showPassword: true,
        type_clearable: true,
        type_placeholder: placeholder || '请输入' + label,
        ...elseOpts
      });
    },
    switch: function (label, propName, propValue, rules, activeText, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "el-switch",
        'type_active-text': activeText,
        ...elseOpts
      });
    },
    input: function (label, propName, propValue, rules, placeholder, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "el-input",
        type_clearable: true,
        type_placeholder: placeholder || '请输入' + label,
        ...elseOpts
      });
    },
    //beforeAdd(file,fileList):boolean 可以拦截和提示错误
    upload: function (label, propName, propValue, rules, beforeAdd, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "DataUpload",
        type_beforeAdd: beforeAdd,
        ...elseOpts
      });
    },
    radio: function (label, propName, propValue, rules, options, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "DataRadio",
        type_options: options || [],
        ...elseOpts
      }, false);
    },
    checkbox: function (label, propName, propValue, rules, options, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "DataCheckbox",
        type_options: options || [],
        ...elseOpts
      }, false);
    },
    number: function (label, propName, propValue, rules, min, max, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "el-input-number",
        type_min: min,
        type_max: max,
        ...elseOpts
      });
    },
    select: function (label, propName, propValue, rules, options, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "DataSelect",
        type_clearable: true,
        type_options: options || [],
        ...elseOpts
      });
    },
    cascader: function (label, propName, propValue, rules, options, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "el-cascader",
        type_clearable: true,
        type_options: options || [],
        ...elseOpts
      }, false);
    },
    jsx: function (label, propName, propValue, rules, jsxFn) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "Jsx",
        type_jsx: jsxFn
      });
    },
    dateRange: function (label, propName, propValue, rules, elseOpts) {
      return createDefaultDefine(label, propName, propValue || [], rules, {
        type: "el-date-picker",
        type_valueFormat: "yyyy-MM-dd",
        type_clearable: true,
        type_format: "yyyy-MM-dd",
        type_type: "daterange",
        type_rangeSeparator: "-",
        type_startPlaceholder: "开始日期",
        type_endPlaceholder: "结束日期",
        ...elseOpts
      });
    },
    //type: date|week|month|year
    date: function (label, propName, propValue, rules, type, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "el-date-picker",
        type_valueFormat: "yyyy-MM-dd",
        type_format: "yyyy-MM-dd",
        type_clearable: true,
        type_type: type || "date",
        type_placeholder: "请选择",
        ...elseOpts
      });
    },
    //时间下拉 间隔一定时间
    timeSelect: function (label, propName, propValue, rules, start, end, step, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "el-time-select",
        type_valueFormat: "HH:mm:ss",
        type_format: "HH:mm:ss",
        type_clearable: true,
        type_pickerOptions: {
          start: start || '00:00',
          step: step || "00:15",
          end: end || '23:59'
        },
        type_placeholder: "请选择",
        ...elseOpts
      });
    },
    time: function (label, propName, propValue, rules, selectableRange, elseOpts) {
      return createDefaultDefine(label, propName, propValue, rules, {
        type: "el-time-picker",
        type_valueFormat: "HH:mm:ss",
        type_format: "HH:mm:ss",
        type_placeholder: "请选择",
        type_clearable: true,
        type_pickerOption: {
          selectableRange: selectableRange
        },
        ...elseOpts
      });
    },
  },
  /**
   * 创建表单对象 .注意itemCreator中可以传递 __init(key, value, rs) 二次配置数据。
   * @param defList  itemCreator.xxx()创建返回。 过滤空对象。
   * @return {{model, rules, items}}
   */
  create(defList = [], page = true) {
    let model = page ? {
      pageNum: 1,
      pageSize: 10,
    } : {};
    let rules = {};
    let items = {};
    let inits = {};//key,initFn
    defList.forEach((item) => {
      if (!item) return;
      let propName = item.propName;
      let propValue = item.propValue;
      let propObject = item.propObject;

      //收集初始化函数
      if (propObject && propObject.__init) {
        inits[propName] = propObject.__init;
        delete propObject.__init;
      }

      model[propName] = propValue;
      rules[propName] = item.rules;
      items[propName] = propObject;
    });
    let rs = {
      model, rules, items,
      empty:!defList||!defList.length,//标记是否有搜索条件
      props(propName) {
        return items[propName];
      }
    };

    //遍历 items 的属性配置，如果有初始化函数__init(key, value, rs)，则应该调用
    Object.keys(inits).forEach((key) => {
      inits[key](key, items[key], rs);
    });

    return rs;
  }
}
