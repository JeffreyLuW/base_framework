import Vue from 'vue';

/**
 * 简单常用工具
 */
class SimpleUtil {
  rootVue = null;
  //保存使用当前类加载的script|link标签
  loadedScripts = {
    'key': {
      state: -1,
      cbs: [],
      script: null
    }
  };

  saveKeyPrefix = '__util__xqh__'; //本地存储的key前缀。

  localStorage(key, val) {
    return this.autoSave(localStorage, key, val);
  }

  localStorageRemove(key) {
    localStorage.removeItem(this.saveKeyPrefix + key);
  }

  sessionStorage(key, val) {
    return this.autoSave(sessionStorage, key, val);
  }

  sessionStorageRemove(key) {
    sessionStorage.removeItem(this.saveKeyPrefix + key);
  }

  autoSave(target, key, val) {
    if (val === undefined) {
      //get val
      let savedVal = target.getItem(this.saveKeyPrefix + key);
      if (savedVal) {
        try {
          savedVal = JSON.parse(savedVal).val;
        } catch (e) {
          savedVal = undefined;
        }
        return savedVal;
      } else {
        return undefined;
      }
    } else {
      target.setItem(this.saveKeyPrefix + key, JSON.stringify({val}));
      return val;
    }
  }

  //根据 属性名称获取分组
  groupByKey(arr, keyName) {
    let rs = [];
    let map = {};
    if (!arr || !keyName) return rs;
    arr.forEach((item) => {
      if (!item) return;
      let key = item[keyName];
      let group = map[key];
      if (!group) {
        group = {
          ...item,
          children: []
        };
        map[key] = group;
        rs.push(group);
      }
      group.children.push(item);
    });
    return {list: rs, map: map};
  }

  //当前是否是开发环境
  isDev() {
    return process.env.NODE_ENV === 'development';
  }

  //服务器部署的相对路径 /开头和结尾 =/static/  | /
  baseUrl() {
    return process.env.BASE_URL;
  }

  isLowerIE() {
    let type = this.browserType();
    return type.startsWith("IE") && parseFloat(type.split(' ')[1]) < 10;
  }

  //判断浏览器类型 IE 10.0 待扩展  Edge 18.17763
  browserType() {
    if (navigator.userAgent.indexOf('Edge') > -1) {
      let arr = navigator.userAgent.split(' ');
      let last = arr[arr.length - 1];
      return last.replace('/', ' '); //Edge 18.17763
    }
    // navigator.appVersion<'5' 版本低于IE9
    if (navigator.appName === 'Microsoft Internet Explorer' ||
      navigator.userAgent.indexOf('.NET') > -1) {
      if (navigator.userAgent.indexOf('rv:11.0') > -1) {
        return 'IE 11.0';
      }
      let matchMsie = navigator.userAgent.match(/MSIE ([\d+\.]+);/i);
      if (matchMsie) {
        return 'IE ' + matchMsie[1];
      }
      return 'IE -1';
    }
    let userAgent = navigator.userAgent;
    let userAgentarr = userAgent.split(' ');
    if (userAgentarr[userAgentarr.length - 2].startsWith('Chrome/')) {
      return 'Chrome ' + userAgentarr[userAgentarr.length - 2].split('/')[1];
    }
    return 'unknown 0';
  }


  //加载多个标签
  loadScripts(srcArr, onload, type) {
    if (!(srcArr instanceof Array))
      srcArr = [srcArr];
    let completeCount = 0;
    let onComplete = () => {
      completeCount++;
      if (completeCount >= srcArr.length && onload)
        onload();
    };
    srcArr.forEach((src) => {
      this.loadScript(src, onComplete, type);
    });
  }

  //加载script标签。
  loadScript(src, onload, type) {
    let info = this.loadedScripts[src];
    if (info) {
      if (info.state === 1) {
        if (onload) onload.bind(info.script)();
        return;
      } else {
        info.cbs.push(onload);
        return;
      }
    }
    if (!type) {
      if (src.endsWith('.js')) {
        type = 'script';
      } else if (src.endsWith('.css')) {
        type = 'link';
      }
    }
    if (!type) type = 'script';
    info = {
      state: -1,
      cbs: [onload]
    };
    this.loadedScripts[src] = info;
    let script = document.createElement(type);
    info.script = script;
    let cb = function () {
      let that = this;
      let args = arguments;
      info.state = 1;
      info.cbs.forEach(function (cb) {
        if (cb) cb.apply(that, args);
      });
      info.cbs = [];
    };
    script.addEventListener('error', cb, false);
    script.onload = cb;
    script.charset = "utf-8";
    if (!src.startsWith("http")) {
      src = this.baseUrl() + src;
    }
    if (type === 'script')
      script.setAttribute("src", src);
    if (type === 'link') {
      script.setAttribute("rel", 'stylesheet');
      script.setAttribute("href", src);
    }
    document.querySelector("head").appendChild(script);
  }

  //一个简单的动画计算 this.anim().start(from,to,duration,cb)
  anim() {
    function AnimHelper() {
      let self = this;
      this.stop = function () {
        if (self.timerId)
          clearInterval(self.timerId);
        self.timerId = null;
        self.isAnim = false;
      };
      this.start = function (from, to, duration, cb) {
        self.isAnim = true;
        self.dx = to - from;
        self.step = self.dx * 15 / duration;
        self.current = from;
        self.timerId = setInterval(() => {
          self.current += self.step;
          let shouldStop = false;

          if (from > to && self.current <= to) {
            shouldStop = true;
          } else if (from < to && self.current >= to) {
            shouldStop = true;
          }
          if (shouldStop) {
            self.stop();
            self.current = to;
          }
          if (cb) cb(self.current);
        }, 15);
      }
    }

    return new AnimHelper();
  }

  // 自动注册指定目录下的组件。
  //Vue,require.context('./components',true,/[0-9a-zA-Z_]+\.vue$/)
  regiterComponent(Vue, requireComponent) {
    requireComponent.keys().forEach((key) => {
      const componentConfig = requireComponent(key);
      let arr = key.split('/');
      let last = arr[arr.length - 1];
      let fileName = last.replace(/^(.*)\.\w+$/g, '$1'); //文件名就是组件名。
      Vue.component(
        fileName,
        componentConfig.default || componentConfig
      )
    });
  }

  //获取地址栏中的参数
  getWindowSearchParams() {
    // name=a&b=c
    var params = {};
    var search = decodeURIComponent(window.location.href);
    var paramSpliter = search.indexOf("?");
    if (paramSpliter > 0) {
      search = search.substring(paramSpliter + 1);
    } else {
      return params;
    }
    search.split("&").forEach(function (kvStr) {
      //kvStr:  name=a
      var kvArr = kvStr.split('=');
      var key = null;
      var value = null;
      if (kvArr.length > 0) {
        key = kvArr[0];
      }
      if (kvArr.length > 1) {
        value = kvArr[1];
      }
      params[key] = value;
    });

    return params;
  }

  // 将一个json对象，保存到FormData中去，
  // obj是要转换的json对象，可以额外提供key值作为根部的Key。 如果obj = Array，往往也需要提供一个key。
  toForm(obj, key = '') {
    let iteratorFn = (key, value, cb, parentKey = '') => {
      let onGetKv = (k, v, isArray, index) => {
        if (v === null || v === undefined) return;
        if (typeof v === 'boolean' || typeof v === 'number' || typeof v === 'string' || v instanceof Number || v instanceof String || v instanceof File) {
          let mapKey = isArray ? key + '[' + index + ']' : (parentKey ? parentKey + '.' + k + '' : k);
          cb(mapKey, v);
        } else {
          //复杂对象
          let mapKey = isArray ? key + '[' + index + ']' : (parentKey ? parentKey + '.' + k + '' : k);
          iteratorFn(mapKey, v, cb, mapKey);
        }
      };
      if (value instanceof Array) {
        value.forEach((v, i) => {
          onGetKv(key, v, true, i);
        });
        return;
      }
      Object.keys(value).forEach((key) => {
        let v = value[key];
        onGetKv(key, v);
      })
    };
    let rs = new FormData();
    iteratorFn(key, obj, (k, v) => {
      rs.append(k, v);
    });
    return rs;
  }

  //遍历tree结构数组  f:(item,index,itemArray,parent,level)
  eachTree(menu, childrenProp = 'children', f, parentItem, level = 0, _info) {
    //倒叙
    if (!menu) {
      return;
    }
    if (!(menu instanceof Array)) {
      menu = [menu];
    }
    if (!_info) _info = {}; //用于有效终止遍历，提高效率
    //倒叙遍历
    for (let i = menu.length - 1; i >= 0; i--) {
      if (_info.goOn === false) break;
      let item = menu[i];
      if (f) {
        let goOn = f(item, i, menu, parentItem, level);
        if (goOn === false) {
          _info.goOn = false;
          break;
        }
      }
      if (item[childrenProp]) {
        this.eachTree(item[childrenProp], childrenProp, f, item, level + 1, _info);
      }
    }
  }

  //根据val，获取父父级一串的选择索引，一般多级联会用 map=map|Array
  treePath(val, map, keyProp = 'id', parentProp = 'pid', includeCurrent = true) {
    return this.treeValArr(val, map, keyProp, parentProp, includeCurrent);
  }

  // map=map|Array
  arrayPath(val, map, keyProp = 'id', parentProp = 'pid', includeCurrent = true) {
    return this.treeValArr(val, map, keyProp, parentProp, includeCurrent);
  }

  //根据val，获取父父级一串的选择索引，一般多级联会用 map=map|Array
  treeValArr(val, map, keyProp = 'id', parentProp = 'pid', includeCurrent = true) {
    if (!map) return [];
    //如果不是map，自动转换为map
    if (map instanceof Array) {
      let tmpMap = {};
      this.eachTree(map, 'children', (item, index, itemArray, parent, level) => {
        tmpMap[item[keyProp]] = item;
        if (parent)
          item[parentProp] = parent[keyProp];
      });
      map = tmpMap;
    }
    let c = map[val];
    let treeValArr = [];
    if (c) {
      do {
        let tmp = c[keyProp];
        if (tmp) {
          treeValArr.unshift(tmp);
          c = map[c[parentProp]];
        } else {
          c = null;
        }
        if (!c) break;
      } while (true);
    }
    if (!includeCurrent) treeValArr.pop();
    return treeValArr;
  }

  //数组转map，多用于简化判断
  arr2Map(arr, key = 'id') {
    let rs = {};
    if (arr) {
      arr.forEach((obj) => {
        if (typeof obj === 'string') {
          rs[obj] = true;
          return;
        }
        if (key) {
          let keyVal = obj[key];
          rs[keyVal] = obj;
        }
      });
    }
    return rs;
  }

  //数组进行分组
  groupArr(arr, groupSize, singRowIndexArray = []) {
    let rs = [];
    if (!arr) return rs;
    let tmpArr = null;
    let singleRowMap = {};
    singRowIndexArray.forEach((v, i) => {
      singleRowMap[v] = true;
    });
    arr.forEach((v, i) => {
      if (singleRowMap[i]) {
        tmpArr = [v];
        rs.push(tmpArr);
        tmpArr = null;
        return;
      }
      if (!tmpArr) {
        tmpArr = [];
        rs.push(tmpArr);
      }
      if (tmpArr.length < groupSize) {
        tmpArr.push(v);
      }
      //当前组大于分组数量 该组结束
      if (tmpArr.length >= groupSize) {
        tmpArr = null;
      }
    });
    return rs;
  }

  /**
   * 创建一个vue组件
   * @param vueOptionOrConstructor
   * @param opt
   * @returns {*}
   */
  createVueComponent(vueOptionOrConstructor, opt) {
    let vueComponent = null;
    if (typeof vueOptionOrConstructor === 'function') {
      vueComponent = new vueOptionOrConstructor(opt);
    } else {
      if (vueOptionOrConstructor.$options) {
        vueComponent = vueOptionOrConstructor;
      } else { //options
        let Constructor = Vue.extend(vueOptionOrConstructor);
        vueComponent = new Constructor(opt);
      }
    }
    return vueComponent;
  }

  /**
   * 创建并挂载到指定位置。 一般用于对话框，可以避免页面其他组件的重新render diff等。效率最大化。
   * @param vueOptionOrConstructor
   * @param propsData
   * @param initFn  fn(component,close)
   * @param target
   * @return component {*}
   */
  createVueAndMount(vueOptionOrConstructor, propsData, initFn, target) {
    let component = this.createVueComponent(vueOptionOrConstructor, {propsData});
    let close = () => {
      if (component)
        component.$destroy();
      component = null;
    };
    if (initFn) {
      initFn(component, close);
    }
    component.$mount();
    if (!target) target = document.body;
    if (typeof target === 'string') {
      target = document.querySelector(target);
    }
    if (target instanceof Node) {
      target.appendChild(component.$el);
    } else if (target.$el) {
      //可能是父VueComponent
      target.$el.appendChild(component.$el);
      target.$once('hook:beforeDestroy', close);
    }
    return component;
  }

  //设置属性，attrObj={key:val}
  domSetAttr(target, attrObj) {
    target = this.fixDom(target);
    Object.keys(attrObj).forEach((key) => {
      let val = attrObj[key];
      target.setAttribute(key, val);
    });
  }

  fixDom(target) {
    if (typeof target === 'string') {
      target = document.querySelector(target);
    }
    return target;
  }

  //设置样式
  setStyle(target, styles) {
    target = this.fixDom(target);
    Object.keys(styles).forEach((key) => {
      let val = styles[key] + '';
      target.style[key] = val;
    });
  }

  //动态更新css
  cssUpdate(id, content) {
    let oldS = document.getElementById(id);
    if (oldS) {
      oldS.parentNode.removeChild(oldS);
    }
    this.createDom(`<style id="${id}" type="text/css"> ${content} </style>`, "head");
  }

  // 通过字符串创建dom元素，并能加入到指定的target中
  // target: String||DOM
  createDom(html, target) {
    let rs = null;
    let div = document.createElement('div');
    div.innerHTML = html;
    rs = [];
    for (let i = 0; i < div.children.length; i++) {
      rs.push(div.children[i]);
    }
    if (target) {
      if ((typeof target) === 'string') {
        for (let i = 0; i < rs.length; i++) {
          document.querySelector(target).appendChild(rs[i]);
        }
      } else {
        for (let i = 0; i < rs.length; i++) {
          target.appendChild(rs[i]);
        }
      }
    }
    return rs.length > 1 ? rs : rs[0];
  }

  //一维数组变tree形结构 {children} 不会改变原arr的内容,withHelpKey会生成_keys 和 _level hasChildren 属性，用于判断tree的节点
  flap2tree(arr, key = 'id', parentKey = 'pid', rootFilter, withHelpKey) {
    if (!arr) return [];
    let rs = [];
    let mapObj = {};
    let copyArr = [];
    arr.forEach((item) => {
      let newItem = {...item};
      copyArr.push(newItem);
      mapObj[item[key]] = newItem;
    });
    copyArr.forEach((item) => {
      //有children但为空时，删除
      if (item.children && !item.children.length) {
        item.children = undefined;
      }
      let pid = item[parentKey];
      let parent = null;
      if (pid) {
        parent = mapObj[pid];
        if (parent) {
          if (!parent.children) {
            parent.children = [];
          }
          if (withHelpKey)
            parent.hasChildren = true;
          parent.children.push(item);
        }
      }
      if (!parent) {
        rs.push(item);
      }
      mapObj[item[key]] = item;
    });
    //如果root需要重新指定过滤，比如有时候，只能是level==1或者parentCode==='0'的才是根级别
    if (rootFilter) {
      rs = rs.filter(rootFilter);
    }
    if (withHelpKey) {
      let eachWithParent = (arr, parent) => {
        arr.forEach((item) => {
          if (!parent) {
            item._level = 1;
            item._keys = [item[key]];
          } else {
            item._level = parent._level + 1;
            item._keys = [...parent._keys, item[key]];
          }
          if (item.children)
            eachWithParent(item.children, item);
        });
      };
      eachWithParent(rs, null);
    }
    return rs;
  }

  //将一个树形对象，变成一个扁平化的对象-kv格式。{id:{id,...,children,$parent},id2:{}}
  tree2flap(tree, keyName = 'id', childrenName = 'children') {
    let rs = {};
    let arr = null;
    if (tree.constructor !== Array) {
      arr = [tree];
    } else {
      arr = tree;
    }
    let flapFn = (arr, parent) => {
      arr.forEach((item) => {
        let copyItem = {...item};
        if (parent) {
          copyItem.$parent = parent[keyName];
        }
        rs[item[keyName]] = copyItem;
        if (copyItem[childrenName] && (copyItem[childrenName].constructor === Array)) {
          flapFn(copyItem[childrenName], copyItem);
        }
      });
    };
    flapFn(arr);
    return rs;
  }

  //模拟生成一个uuid，重复几率低，但>0
  uuid() {
    let d = new Date().getTime();
    if (window.performance && typeof window.performance.now === "function") {
      d += performance.now(); //use high-precision timer if available
    }
    let uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      var r = (d + Math.random() * 16) % 16 | 0;
      d = Math.floor(d / 16);
      return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
    return uuid;
  }

  //多个值相加，需要准确，保留指定位数。 返回的是计算的字符串
  sumStringArray(arr, fixed = 2) {
    let rs = 0;
    if (!arr || arr.length <= 0) {
      return rs;
    }
    let a = 10000;
    arr.forEach((item) => {
      let currVal = 0;
      try {
        if (!item) {
          currVal = 0;
        } else if (typeof item === 'number') {
          currVal = item * a;
        } else if (typeof item === 'string' && item.match(/^\d+\.?\d*$/g)) {
          currVal = parseInt(parseFloat(item) * a);
        } else {
          console.log('wrong value:', item);
        }
      } catch (e) {
        currVal = 0;
      }
      rs += currVal;
    });
    rs /= a;
    return rs.toFixed(fixed);
  }

  //  查询dom对应子元素有多少
  //  最后进行dom分析，列举哪些下面dom数量最多
  findDomChildren(target, rs = []) {
    if (!target) {
      return rs;
    }
    if (!target.children) {
      return rs;
    }
    for (let i = 0; i < target.children.length; i++) {
      if (!target.children[i]) continue;
      rs.push(target.children[i]);
      this.findDomChildren(target.children[i], rs);
    }
    return rs;
  }

  //给指定的dom全部上色 用于观察页面组成
  colorDomChildren(target) {
    let doms = this.findDomChildren(target, []);
    doms.forEach((dom, i) => {
      dom.style.backgroundColor = this.genColor(i * 10);
    })
    return doms.length;
  }

  //简单生成125种颜色  由白-黑
  genColor(index) {
    index = index % 125; //限制在125种颜色中
    let colorArr = new Number(index).toString('5').padStart(3, '0').split('');
    let colors = [];
    colorArr.forEach((i) => {
      i = parseInt(i);
      colors.push(new Number(255 - i * 50).toString('16').padStart(2, '0'));
    });
    return '#' + colors.join('');
  }

  //获取dom的数量情况
  findDomCountInfo(target = document.body) {
    if (typeof target === 'string') {
      target = document.querySelector(target);
    }
    let rs = [];
    let allChildren = this.findDomChildren(target);
    rs.push({
      target: target,
      childrenCount: allChildren.length,
      children: allChildren
    });
    allChildren.forEach((item) => {
      if (!item) return;
      let itemChildren = this.findDomChildren(item);
      rs.push({
        target: item,
        childrenCount: itemChildren.length,
        children: itemChildren
      });
    });
    rs.sort((a, b) => {
      return b.childrenCount - a.childrenCount;
    });
    return rs;
  }


  //数组排序
  sort(arr, prop, reverse) {
    if (!arr) return [];
    return arr.sort((a, b) => {
      if (a === b) return 0;
      if (prop) {
        a = a[prop];
        b = b[prop];
      }
      if (reverse) {
        return a > b ? -1 : 1;
      }
      return a > b ? 1 : -1;
    })
  }


  //根据model的key，从entity取值覆盖model对应值。
  // includeNullValue 是否采用entity的 null undefined的值
  coverValue(model, entity, includeNullValue) {
    let rs = {};
    if (model && entity) {
      Object.keys(model).forEach((key) => {
        rs[key] = model[key];
        let nv = entity[key];
        if ((nv === null || nv === undefined) && !includeNullValue) {
          return;
        }
        if (nv === undefined) nv = null;
        rs[key] = nv;
      });
    }
    return rs;
  }

  //判断值是否是 null  undefined  ''
  nullUndefEmptyVal(val) {
    return val === null || val === undefined || val === ''
  }

  //限制字符串长度
  limitStrLen(s, maxLen) {
    if (!s || maxLen < 3) return s;
    if (s.length < maxLen) return s;
    return s.substring(0, maxLen - 3) + '...';
    // return  s.substring(0, maxLen ) ;
  }


  //转罗马数字
  romanNum(num) {
    if (num === null || num === undefined) return '';
    let newArr = [];
    let newStr;
    //先把数字转化为相应的罗马字母
    while (num > 0) {
      if (num - 1000 >= 0) {
        newArr.push('M');
        num -= 1000;
      } else if (num - 500 >= 0) {
        newArr.push('D');
        num -= 500;
      } else if (num - 100 >= 0) {
        newArr.push('C');
        num -= 100;
      } else if (num - 50 >= 0) {
        newArr.push('L');
        num -= 50;
      } else if (num - 10 >= 0) {
        newArr.push('X');
        num -= 10;
      } else if (num - 5 >= 0) {
        newArr.push('V');
        num -= 5;
      } else if (num - 1 >= 0) {
        newArr.push('I');
        num -= 1;
      }
    }
    newStr = newArr.join('');
    //将4和9的情况进行替换
    newStr = newStr.replace(/VI{4}|LX{4}|DC{4}|I{4}|X{4}|C{4}/g, function (match) {
      switch (match) {
        case 'VIIII':
          return "IX";
        case 'LXXXX':
          return "XC";
        case 'DCCCC':
          return "CM";
        case 'IIII':
          return "IV";
        case 'XXXX':
          return "XL";
        case 'CCCC':
          return "CD";
      }
    });
    return newStr ? newStr : num;
  }

  //参数转数组
  arguments2Array(arg) {
    let arr = [];
    if (!arg) return arr;
    for (let i = 0; i < arg.length; i++) {
      arr.push(arg[i]);
    }
    return arr;
  }

  //根据组件名，向上获取parent
  getParentByName(vue, name) {
    if (vue.$options && vue.$options.name === name) {
      return vue;
    }
    return this.getParentByName(vue.$parent, name);
  }

  // 16进制的颜色自动计算 #3484c2 变深或者变浅
  // ratio取值 0 - 2  ratio越小颜色越深。
  // 一般可取0.8 1.2作为按钮的值变化
  colorCompute(color, ratio) {
    let vals = [];
    if (color.startsWith("#"))
      color = color.substring(1);
    for (let i = 0; i < color.length; i += 2) {
      let val = parseInt(color.substring(i, i + 2), 16);
      vals.push(val);
    }
    let rs = vals.map((v, i) => {
      let val = 0;
      if (ratio > 1) { // 差值+
        val = v + (255 - v) * (ratio - 1);
      } else {
        val = v * ratio;
      }
      val = Math.round(val);
      if (val < 0) val = 0;
      if (val > 255) val = 255;
      return val.toString(16).padStart(2, '0');
    });
    return "#" + rs.join("");
  }

  //自动给Menu添加有规律的唯一索引。index属性 1-2-1-1
  autoIndexMenu(menu = [], parentIndex) {
    menu.forEach((menuItem, index) => {
      if (!parentIndex) {
        menuItem.index = '' + (index + 1);
      } else {
        menuItem.index = parentIndex + '-' + (index + 1);
      }
      if (menuItem.children) {
        this.autoIndexMenu(menuItem.children, menuItem.index)
      }
    });
    return menu;
  }

  //查找tree的第一个叶子节点
  treeFirstLeaf(arr, childProp = "children") {
    if (!arr) {
      return null;
    }
    if (!(arr instanceof Array)) {
      arr = [arr];
    }
    let leaf = arr[0];
    while (leaf && true) {
      let children = leaf[childProp];
      if (!children || children.length <= 0) {
        break;
      }
      leaf = children[0];
    }
    return leaf;
  }

  //连接url路径 参数为多个字符串
  joinUrls() {
    let rs = '';
    Object.values(arguments).forEach((value) => {
      if (!value) return;
      if (!value.startsWith('/')) {
        value = '/' + value;
      }
      if (value.length > 1 && value.endsWith('/')) {
        value = value.substring(0, value.length - 1);
      }
      if (rs.endsWith('/')) {
        rs += value.substring(1);
      } else {
        rs += value;
      }
    });
    return rs;
  }

  //根据名称查找最近的父级对象
  nearParent(component, parentName) {
    if (!component) return null;
    if (!parentName) return component.$parent;
    let target = null;
    let parent = component.$parent;
    while (parent) {
      if (parent.$options.name === parentName) {
        target = parent;
        break;
      } else {
        parent = parent.$parent;
      }
    }
    return target;
  }

  //值转字符串。常用于字典表类型转换
  strVal(val, defaultVal) {
    return val === null || val === undefined ? (defaultVal || val) : val + '';
  }

  //循环直到条件成立.组件销毁时能正常取消定时。场景:刷新时$refs获取和正常router切换有延迟
  runUntil(conditionFn, callback, vueComponent, duration = 50) {
    let timerId = null;
    let innerRun = () => {
      timerId = null;
      if (conditionFn()) {
        callback();
      } else {
        timerId = setTimeout(innerRun, duration);
      }
    };
    vueComponent.$once('hook:beforeDestroy', () => {
      if (timerId) {
        timerId = null;
        window.clearTimeout(timerId);
      }
    });
    innerRun();
  }

  parseJson(s) {
    let rs = null;
    if (typeof s === "string") {
      try {
        rs = JSON.parse(s);
      } catch (e) {

      }
    } else if (typeof s === "object") {
      rs = s;
    }
    return rs;
  }

  //array中选择最后一个选项
  last(arr) {
    if (!arr || !arr.length) return undefined;
    return arr[arr.length - 1];
  }

  //数组中移除某个元素；ps不是索引
  arrRemove(arr, ele) {
    if (!arr) return arr;
    let index = arr.indexOf(ele);
    if (index > -1) {
      arr.splice(index, 1);
    }
    return arr;
  }

  //  /system/config/index页面配置的参数，如果有图片地址，则提取图片地址。
  getSysConfigParamUrl(config, baseUrl) {
    if (!config) {
      return null;
    }
    let rs = null;
    //非图片
    if ("N" === config.configIsImg) {
      if (config.configValue.indexOf("://") !== -1) {
        //可能是一个http地址
        rs = this.fixFileUrl(config.configValue, baseUrl);
      }
    } else if (config.validateImage) {
      rs = this.fixFileUrl(config.configValue, baseUrl);
    }
    if (rs) rs = rs.trim();
    return rs;
  }

  //自动拼接服务器文件路径
  fixFileUrl(url, baseUrl) {
    if (!baseUrl && this.rootVue) {
      baseUrl = this.rootVue.$store.state.settings.serverUrl;
    }
    if (!url) {
      return baseUrl;
    }
    if (url.indexOf("://") === -1) {
      if (baseUrl.endsWith("/")) {
        baseUrl = baseUrl.substring(0, baseUrl.length - 1);
      }
      if (!url.startsWith("/")) {
        url = "/" + url;
      }
      return baseUrl + url;
    }
    return url;
  }

}

export default new SimpleUtil();
