<script lang="jsx">
  //用于模板中嵌入jsx.
  //也可以使用cTag和原始数据对象来创建。
  export default {
    // functional: true,
    inheritAttrs: false,
    props: {
      jsx: {
        type: Function,
        default: (h, jsx) => {
        }
      },
      lifeFn: {
        type: Function
      },
      cTag: {
        type: String,//createElement函数动态创建 配合 cData 使用
      },
      cData: {
        type: Object,//数据对象。
      },
      cChildren: {
        type: String | Array,
      },
    },
    created() {
      this.lifeFn && this.lifeFn('created', this);
    },
    mounted() {
      this.lifeFn && this.lifeFn('mounted', this);
    },

    beforeDestroy() {
      this.lifeFn && this.lifeFn('beforeDestroy', this);
    },
    render(h) {
      if (this.cTag) {
        return h(this.cTag, {...this.cData}, this.cChildren);
      }
      //可以使用 value={jsx.$attrs.value} onInput={jsx.$listeners.input} 传递v-model
      return this.jsx ? this.jsx(h, this) : null;
    }
  };
</script>
