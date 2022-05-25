//shouldIteratorFn 默认返回true即可 不想进一步分解的返回false
let ToKvList = {
  parse(rs = [], targetObject, parentKey, shouldIteratorFn) {
    //这个object 一般是 {} | []
    if (targetObject instanceof Array) {
      targetObject.forEach((item, index) => {
        this._onGetVal(rs, item, index + '', parentKey, shouldIteratorFn);
      });
    } else {
      //object
      Object.keys(targetObject).forEach((key) => {
        let val = targetObject[key];
        this._onGetVal(rs, val, key, parentKey, shouldIteratorFn);
      });
    }
    return rs;
  },
  _onGetVal(rs, val, key, parentKey, shouldIteratorFn) {
    //val 如果是不需要遍历的类型，直接设定key val即可。比如 Number String Null Undefined
    let type = typeof val;
    let isNotIteralVal = val === null || val === undefined || 'number' === type || 'string' === type;
    if (!isNotIteralVal && (typeof window !== 'undefined' && typeof File !== 'undefined')) {
      isNotIteralVal = val instanceof File;
    }
    if (!isNotIteralVal && shouldIteratorFn) {
      isNotIteralVal = !shouldIteratorFn(val);
    }
    let currKey = key;
    if (parentKey) {
      currKey = parentKey + "[" + key + "]";
    }
    //不可迭代
    if (isNotIteralVal) {
      rs.push({
        key: currKey,
        val: val
      });
    } else {
      this.parse(rs, val, currKey, shouldIteratorFn);
    }
  }
};

//demo 可以将某个对象递归扁平化 转一维键值对列表  [{ key: 'root[name]', val: 'name' }]
// console.log(ToKvList.parse([], obj, 'root', (item) => {
//   if (item.constructor.name === "Test") {
//     return false;
//   }
//   return true;
// }));
export default ToKvList;
