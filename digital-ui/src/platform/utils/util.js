import FileSaver from 'file-saver';

/**
 * 常用工具类。注入到Vue实列的$util对象。
 */
class Utils {
    //保存使用当前类加载的script标签
    loadedScripts = {
        'key': {
            state: -1,
            cbs: [],
            script: null
        }
    };
    rootVue = null;//vue根实列
    saveKeyPrefix = '__util__';//本地存储的key前缀。
    Vue = null;

    install(Vue, options) {
        this.Vue = Vue;
        this.init(Vue, options);
        Vue.filter('json', function (value) {
            return JSON.stringify(value);
        });
        Vue.prototype.$util = this;
        window.util = this;
    }


    //保存或者取出
    localStorage(key, val) {
        return this.autoSave(localStorage, key, val);
    }

    //保存或者取出
    sessionStorage(key, val) {
        return this.autoSave(sessionStorage, key, val);
    }

    autoSave(target, key, val) {
        if (val === undefined) {
            //get val
            let savedVal = target.getItem(this.saveKeyPrefix + key);
            if (savedVal) {
                return JSON.parse(savedVal).val;
            } else {
                return undefined;
            }
        } else {
            target.setItem(this.saveKeyPrefix + key, JSON.stringify({val}));
            return val;
        }
    }

    //数组转map，多用于简化判断
    arr2Map(arr, key) {
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

    combineStore(modules) {
        let rs = {};
        if (modules) {
            modules.forEach((m) => {
                Object.keys(m).forEach((key) => {
                    rs[key] = {...rs[key], ...m[key]}
                });
            });
        }
        return rs;
    }

    combineObjects(modules) {
        let rs = {};
        if (modules) {
            modules.forEach((m) => {
                rs = {...rs, ...m};
            });
        }
        return rs;
    }

    init(Vue, options) {
        if (this.isDev()) {
            window.j = function (rs) {
                console.log(JSON.stringify(rs));
            };
            window.jj = function (rs) {
                console.log(JSON.stringify(rs, null, '\t'));
            };
        } else {
            //屏蔽console.log
            console._log = console.log;
            console.log = () => {
            };
        }

        Array.prototype.last = function () {
            return this.length > 0 ? this[this.length - 1] : null;
        };
        //日期clone
        Date.prototype.clone = function () {
            //year: number, month: number, date?: number, hours?: number, minutes?: number, seconds?: number, ms?: number
            return new Date(this.getFullYear(), this.getMonth(), this.getDate(), this.getHours(), this.getMinutes(), this.getSeconds(), this.getMilliseconds()
            );
        };
        //日期格式化
        Date.prototype.format = function (fmt = "yyyy-MM-dd hh:mm:ss") {
            let o = {
                "M+": this.getMonth() + 1, //月份
                "d+": this.getDate(), //日
                "h+": this.getHours(), //小时
                "H+": this.getHours(), //小时
                "m+": this.getMinutes(), //分
                "s+": this.getSeconds(), //秒
                "q+": Math.floor((this.getMonth() + 3) / 3), //季度
                S: this.getMilliseconds() //毫秒
            };
            if (/(y+)/.test(fmt))
                fmt = fmt.replace(
                    RegExp.$1,
                    (this.getFullYear() + "").substr(4 - RegExp.$1.length)
                );
            for (let k in o)
                if (new RegExp("(" + k + ")").test(fmt))
                    fmt = fmt.replace(
                        RegExp.$1,
                        RegExp.$1.length == 1
                            ? o[k]
                            : ("00" + o[k]).substr(("" + o[k]).length)
                    );
            return fmt;
        };

        //pollify trunc
        if (!Math.trunc) {
            Math.trunc = function (v) {
                v = +v;
                if (!isFinite(v)) return v;
                return (v - v % 1) || (v < 0 ? -0 : v === 0 ? v : 0);
            };
        }
    }


    //服务器部署的相对路径 /开头和结尾 =/static/  | /
    baseUrl() {
        return process.env.BASE_URL;
    }

    //当前是否是开发环境
    isDev() {
        return process.env.NODE_ENV === 'development';
    }

    //获取当前部分env变量
    getEnvVars() {
        return process.env;
    }

    //根据名称查找vue实例
    vm(name) {
        let rs = this.vms(name);
        return rs.length > 0 ? rs[0] : null
    }

    //根据名称查找vue实例
    vms(name) {
        if (!this.rootVue) {
            console.warn('please call util.rootVue=new Vue(..) first');
            return null;
        }
        let rs = [];
        let find = (targetVue, name) => {
            if (!targetVue) return;
            if (targetVue.$options) {
                if (targetVue.$options.name === name) {
                    rs.push(targetVue);
                }
            }
            if (targetVue.$children) {
                targetVue.$children.forEach((v) => {
                    find(v, name);
                });
            }
        };
        find(this.rootVue, name);
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
            } else {//options
                let Constructor = this.Vue.extend(vueOptionOrConstructor);
                vueComponent = new Constructor(opt);
            }
        }
        return vueComponent;
    }

    isLowerIE() {
        let type = this.browserType();
        return type.startsWith("IE") && parseFloat(type.split(' ')[1]) < 10;
    }

    //判断浏览器类型 IE 10.0 待扩展
    browserType() {
        if (navigator.userAgent.indexOf('Edge') > -1) {
            let arr = navigator.userAgent.split(' ');
            let last = arr[arr.length - 1];
            return last.replace('/', ' ');//Edge 18.17763
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

    //设置属性，attrObj={key:val}
    setAttribute(target, attrObj) {
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
        info = {
            state: -1,
            cbs: [onload]
        };
        this.loadedScripts[src] = info;
        let script = document.createElement(type);
        info.script = script;
        script.onload = function () {
            let that = this;
            let args = arguments;
            info.state = 1;
            info.cbs.forEach(function (cb) {
                if (cb) cb.apply(that, args);
            });
            info.cbs = [];
        };
        script.charset = "utf-8";
        if (type === 'script')
            script.setAttribute("src", src);
        if (type === 'link') {
            script.setAttribute("rel", 'stylesheet');
            script.setAttribute("href", src);
        }
        document.querySelector("head").appendChild(script);
    }

    //判断两个Array是否相等。会判断内部元素是否是同一个。
    isSameArry(a, b) {
        if (!a && !b) {
            return true;
        }
        if (!a && b || a && !b) {
            return false;
        }
        if (a.length !== b.length) {
            return false;
        }
        let same = true;
        for (let i = 0; i < a.length; i++) {
            if (a[i] !== b[i]) {
                same = false;
                break;
            }
        }
        return same;
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
            let fileName = last.replace(/^(.*)\.\w+$/g, '$1');//文件名就是组件名。
            Vue.component(
                fileName,
                componentConfig.default || componentConfig
            )
        });
    }

    //一维数组变tree形结构 {children} 不会改变原arr的内容,withHelpKey会生成_keys 和 _level 属性，用于判断tree的节点
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
            let pid = item[parentKey];
            let parent = null;
            if (pid) {
                parent = mapObj[pid];
                if (parent) {
                    if (!parent.children) {
                        parent.children = [];
                    }
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

    // 返回数组第一个元素
    arrayFirstElement(array, keyProp) {
        if (!array || array.length <= 0) {
            return null;
        }
        let rs = array[0];
        if (rs && keyProp) {
            return rs[keyProp];
        }
        return rs;
    }

    //模拟生成一个uuid，重复几率低 ，但>0
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

    // 返回数组叶子级别的第一个元素，数组[]，包含各个级别的值。如果给了keyProp，则自动取对应属性的集合
    arrayFirstLeafElements(array, childrenProp = 'children', keyProp) {
        let rs = [];
        if (!array || array.length <= 0) {
            return [];
        }
        let curr = array[0];
        if (curr) {
            rs.push(curr);
            do {
                let children = curr[childrenProp];
                if (children && children.length > 0) {
                    curr = children[0];
                    if (curr)
                        rs.push(curr);
                    else
                        break;
                } else {
                    break;
                }
            } while (true);
        }
        if (keyProp) {
            return rs.map((item) => {
                return item[keyProp];
            })
        }
        return rs;
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

    //用于删除对象中的空值  clone=true，则会复制原对象。
    deleteEmptyAttribute(obj, clone) {
        if (!obj || obj instanceof FormData) return obj;
        if (clone) {
            obj = this.deepClone(obj);
        }
        Object.keys(obj).forEach(key => {
            let val = obj[key];
            if (val === null || val === undefined || val === '') {
                delete obj[key];
                return;
            }
            if (typeof val === 'object') {
                if (val instanceof Array) {
                    val.forEach((v) => {
                        this.deleteEmptyAttribute(v);
                    })
                } else {
                    this.deleteEmptyAttribute(val);
                }
            }
        });
        return obj;
    }

    joinLabel(arr, spliter = '') {
        let rs = '';
        if (arr) {
            arr.forEach((v) => {
                if (v !== null && v != undefined) {
                    if (rs.length > 0) rs += spliter;
                    rs += v;
                }
            })
        }
        return rs;
    }

    //用于常见的文件上传，fileProps会自动提取raw和guid，提出后遗留的文件guid会拼接到 key+suffix的[]属性中.
    toFormAutoFile(model, fileProps = [], leavedFileSuffix = 'idArr',) {
        fileProps.forEach((key) => {
            let val = model[key];
            model[key] = null;
            let files = model[key] = [];//原文件
            let leavedFiles = [];
            if (leavedFileSuffix) {
                model[key + leavedFileSuffix] = leavedFiles;
            }
            if (val) {
                val.forEach((row) => {
                    if (row.raw) {
                        files.push(row.raw);
                        // console.log('files', files, model[key])
                    } else if (row.guid) {
                        leavedFiles.push(row.guid)
                    }
                });
            }
        });
        return this.toForm(model);
    }

    // 将一个json对象，保存到FormData中去，
    // obj是要转换的json对象，可以额外提供key值作为根部的Key。 如果obj = Array，往往也需要提供一个key。
    toForm(obj, key = '') {
        let iteratorFn = (key, value, cb, parentKey = '') => {
            let onGetKv = (k, v, isArray, index) => {
                if (v === null || v === undefined) return;
                if (typeof v === 'number' || typeof v === 'string' || v instanceof Number || v instanceof String || v instanceof File) {
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

    // obj转FormData onExtraFile(formData,key, file);
    // 非input=file上传的文件，可能是服务端带回来的文件。
    obj2FormData(form, filesProp, ignoreProp, onExtraFile) {
        //this.form
        let formData = new FormData();
        let filesPropMap = this.arr2Map(filesProp);
        let ignorePropMap = this.arr2Map(ignoreProp);
        Object.keys(form).forEach((key) => {
            if (ignorePropMap[key]) return;
            if (filesPropMap[key]) {
                let files = form[key];
                if (!(files instanceof Array)) {
                    files = [files];
                }
                files.forEach((file) => {
                    //{name:"icon_设置.png",percentage:0,raw:File,size:2046,status:'ready',uid}
                    if (file.raw) {
                        formData.append(key, file.raw);
                    } else {
                        //非input=file上传的文件，可能是服务端带回来的文件。
                        if (onExtraFile)
                            onExtraFile(formData, key, file);
                    }

                });
            } else {
                let val = form[key];
                let isEmpty = val === null || val === undefined || val === '';
                if (!isEmpty)
                    formData.append(key, val);
            }
        });
        return formData;
    }

    //formData 转map对象查看
    formData2Obj(formData) {
        let formDataObj = {};
        if (!formData)
            return formDataObj;
        let keys = formData.keys();
        let n = null;
        do {
            n = keys.next();
            if (!n || n.done) break;
            let fKey = n.value;
            formDataObj[fKey] = formData.getAll(fKey);
        } while (true);
        return formDataObj;
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
        index = index % 125;//限制在125种颜色中
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

    //遍历tree结构数组  f:(item,index,itemArray,parent,level)
    eachTree(menu, childrenProp = 'children', f, parentItem, level = 0) {
        //倒叙
        if (!menu) {
            return;
        }
        if (!(menu instanceof Array)) {
            menu = [menu];
        }
        //倒叙遍历
        for (let i = menu.length - 1; i >= 0; i--) {
            let item = menu[i];
            if (f) {
                f(item, i, menu, parentItem, level);
            }
            if (item[childrenProp]) {
                this.eachTree(item[childrenProp], childrenProp, f, item, level + 1);
            }
        }
    }

    //深度复制一个对象。部分特殊对象不会复制。 可能是{} | [{}]
    deepClone(object) {
        if (!object || typeof object !== 'object' || object instanceof FormData || object instanceof File) {
            return object;
        }
        let rs = null;
        if (object instanceof Array) {
            rs = [];
            object.forEach((item) => {
                rs.push(this.deepClone(item))
            })
        } else {
            rs = {};
            Object.keys(object).forEach((key) => {
                let val = object[key];
                rs[key] = this.deepClone(val);
            })
        }
        return rs;
    }

    //根据model的key，从entity取值，includeNullValue=false，则 entity 值为null undefined时，不会赋值到model，可以让model保留默认值。
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

    //根据val，获取父父级一串的选择索引，一般多级联会用
    treeValArr(val, map, keyProp, parentProp) {
        let c = map[val];
        let treeValArr = [];
        if (c) {
            do {
                treeValArr.unshift(c[keyProp]);
                c = map[c[parentProp]];
                if (!c) break;
            } while (true);
        }
        return treeValArr;
    }
    //根据val，获取行政区划名称
    treeNameArr(val, map, keyProp, parentProp) {
        let c = map[val];
        let treeValArr = [];
        let name = "";
        if (c) {
            do {
                name = c.name+name;
                treeValArr.unshift(c[keyProp]);
                c = map[c[parentProp]];
                if (!c) break;
            } while (true);
        }
        return name;
    }
    //使用定义的api的返回值下载保存 最好不要用get请求，复杂参数有问题。
    apiDownload(_promise, fileName) {
        _promise.then((res) => {
            if (!fileName && res.$response && res.$response.headers) {
                let contentDisposition = res.$response.headers['content-disposition'];
                if (contentDisposition) {
                    contentDisposition = decodeURI(contentDisposition);
                    if (contentDisposition.indexOf('filename') !== -1) {
                        fileName = (contentDisposition.split(';')[1]).split('=')[1];
                    }
                }
            }
            console.log('apiDownload', fileName);
            FileSaver.saveAs(res.data, fileName);
        });
    }

    //携带当前的服务器地址 相对地址 参数 来下载文件
    downloadWidthDev(remoteServer, relUrl, paramsName, params) {
        let baseUrl = this.isDev() ? remoteServer : '';
        let url = baseUrl + relUrl;
        if (paramsName && params) {
            url += '?' + paramsName + '=' + encodeURI(JSON.stringify(this.deleteEmptyAttribute(params)));
        }
        this.download(url, null, false);
    }

    //下载  autoName是自动截取url中的名称，如果需要使用后台的名称，则为false
    download(url, filename, autoName = true) {
        if (!filename && autoName) {
            let newUrl = url;
            let paramsIndex = newUrl.indexOf('?');
            if (paramsIndex !== -1) {
                newUrl = newUrl.substring(0, paramsIndex);
            }
            filename = newUrl.substring(url.lastIndexOf('/') + 1);
        }
        let baseUrl = this.baseUrl();
        if (!url.startsWith("http") && !url.startsWith(baseUrl)) {
            if (url.startsWith('/')) {
                url = baseUrl + url.substring(1);
            } else {
                url = baseUrl + url;
            }
        }
        FileSaver.saveAs(url, filename);
    }

    //读取某个文件，转url
    readFileAsUrl(file, cb) {
        //转码base64
        let reader = new FileReader();
        // let that = this
        reader.readAsDataURL(file);
        reader.onload = e => {
            cb(e.target.result);
        };
    }

    //获取表单验证时的错误提示信息 :children  [{prop,label}] | {prop:{opt:{placeholder}}}
    getFormValideMsg(children, err, limitLen = 50) {
        // err {propKey:[{message,field}]}
        if (!err) {
            return '';
        }
        let childMap = {};
        if (children) {
            if (children instanceof Array) {
                children.forEach((item) => {
                    childMap[item.prop] = item.label;
                })
            } else if (typeof children === 'object') {
                Object.keys(children).forEach((key) => {
                    let label = children[key].opt && children[key].opt.placeholder;
                    if (label) {
                        childMap[key] = label;
                    }
                });
            }
        }
        let msgArr = [];

        Object.keys(err).forEach((key) => {
            let errFieldArr = err[key];
            let label = childMap[key];
            if (errFieldArr && label) {
                let tmpMsgArr = [];
                errFieldArr.forEach(({message, field}) => {
                    tmpMsgArr.push(message);
                });
                msgArr.push(label + ',' + tmpMsgArr.join(' '));
            }
        });
        let rs = msgArr.join(';');
        if (limitLen && rs.length > limitLen) {
            rs = rs.substring(0, limitLen - 3) + '...';
        }
        return rs;
    }

    //回车自动搜索 component中需要refProp对应的 SearchTable
    autoEnterSearch(component, refProp) {
        return {
            keyup: (e) => {
                if (e.keyCode === 13 && component && component.$refs[refProp])
                    component.$refs[refProp].manualSearch();
            }
        };
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

    //显示时间时长  1分10秒
    timeDurationDesc(talklong) {
        if (talklong === null || talklong === undefined) return '';
        if (typeof talklong === 'string') {
            talklong = parseFloat(talklong);
        }
        talklong = Math.floor(talklong);

        let h = Math.floor(talklong / 3600);
        let min = Math.floor((talklong - 3600 * h) / 60);
        let second = (talklong - h * 3600 - min * 60) % 60;

        if (h > 0) {
            return `${h}时${min}分${second}秒`;
        }
        if (min > 0) {
            return `${min}分${second}秒`;
        }
        return `${second}秒`;
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

}

export default new Utils();
