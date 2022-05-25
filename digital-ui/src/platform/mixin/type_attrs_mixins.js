//用于代理 $attrs 和 $listeners 减少每次组件render的频率。
// 定义 type_attrs ={type,button,_type,_button...} 可以将attrs根据这里的前缀分组，从而可以给内部组件直接绑定对应分类属性。  <button v-bind="type_attrs.button"/>
// 可以方便定义嵌套属性。
const Empty = {};
export default {
  data() {
    return {
      my_attrs: this.$attrs || Empty,
      my_listeners: this.$listeners || Empty,
      type_attrs: null,//用于设置需要监听的分类属性前缀 {type:null,default:null,_type:null}
      type_listeners: null,//从而可以通过 前缀 给内部不同组件属性赋值。
    };
  },
  created() {
    if (this.type_attrs || this.type_listeners) {
      this.forceResetAttrs();
    }
  },
  watch: {
    $attrs(n, o) {
      if (this.simple_key_eq(n, o)) {
        return;
      }
      this.my_attrs = n || Empty;
      this.fetch_type_keys(this.my_attrs, this.type_attrs);
    },
    $listeners(n, o) {
      if (this.simple_key_eq(n, o)) {
        return;
      }
      this.my_listeners = n || Empty;
      this.fetch_type_keys(this.my_listeners, this.type_listeners);
    },
  },
  methods: {
    //刷新属性。因为上面为了性能，对属性和事件仅进行了浅层次对比。
    //如果属性发生改变，但对象仍是同一个时，会导致不能正常刷新，可手动刷新。
    forceResetAttrs() {
      this.my_attrs = this.$attrs || Empty;
      this.fetch_type_keys(this.my_attrs, this.type_attrs);

      this.my_listeners = this.$listeners || Empty;
      this.fetch_type_keys(this.my_listeners, this.type_listeners);
    },
    //只简单判断对象的key是否相等
    simple_key_eq(n, o) {
      //n valued
      let isSame = true;
      let keysNew = null;
      if (!n) {
        isSame = !o || o && Object.keys(o).length == 0 ? true : false;
      } else {
        if (o) {
          //all valued
          keysNew = Object.keys(n);
          let keysOld = Object.keys(o);
          if (keysNew.length !== keysOld.length) {
            isSame = false;
          } else {
            let key = null;
            for (let i = 0; i < keysNew.length; i++) {
              key = keysNew[i];
              if (n[key] !== o[key]) {
                isSame = false;
                break;
              }
            }
          }
        }

      }
      return isSame;
    },
    //n==attrs|listeners ,type_attrs={default:{},type:{}}
    //返回{type:,default:,_type}
    fetch_type_keys(n, type_attrs) {
      // type_attrs={default,type,old};
      if (type_attrs) {
        let typeKeys = [];
        for (let k in type_attrs) {
          if (!k.startsWith("_") && k !== 'default') typeKeys.push(k);
        }
        if (n) {
          typeKeys.forEach((key) => {
            type_attrs[key] = {};
            type_attrs['_' + key] = {};
          });
          //else
          type_attrs.default = {};

          Object.keys(n).forEach((key) => {
            let val = n[key];
            let k = null;
            let k_prefix = null;
            let matched = false;
            for (let i = 0; i < typeKeys.length; i++) {
              k = typeKeys[i];
              k_prefix = k + "_";
              if (key.startsWith(k_prefix)) {
                let tmpKey = key.substring(k_prefix.length);
                type_attrs[k][tmpKey] = val;
                type_attrs['_' + k][key] = val;
                matched = true;
                break;
              }
            }
            if (!matched) {
              type_attrs.default[key] = val;
            }
          });
        } else {
          typeKeys.forEach((key) => {
            type_attrs[key] = Empty;
            type_attrs['_' + key] = Empty;
          });
          type_attrs.default = Empty;
        }
      }
      return type_attrs;
    }
  }
}
