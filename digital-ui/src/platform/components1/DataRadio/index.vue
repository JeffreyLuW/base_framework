<template>
  <el-radio-group :value="value"
                  :size="size"
                  :disabled="disabled"
                  :fill="fill"
                  :text-color="textColor"
                  @input="onInput"
                  @change="onChange"
  >
    <template v-if="button">
      <el-radio-button v-for="item in innerOptions" :label="item.value" :key="item.value"
                       :name="item.name"
                       :disabled="item.disabled"
                       :border="item.border"
                       :size="item.size"
      >{{item.label}}
      </el-radio-button>
    </template>
    <template v-else>
      <el-radio v-for="item in innerOptions" :label="item.value" :key="item.value"
                :name="item.name"
                :disabled="item.disabled"
                :border="item.border"
                :size="item.size"
      >{{item.label}}
      </el-radio>
    </template>
  </el-radio-group>
</template>

<script>
  //用于实现通用单选按钮
  export default {
    name: "DataRadio",
    props: {
      value: {},
      options: {
        type: String | Function | Array,// 字符串 Array Function [{value,label,size,border,disabled,name}]
      },
      button: {
        type: Boolean,//使用按钮样式
      },
      size: {},
      disabled: {},
      textColor: {},
      fill: {}
    },
    data() {
      return {
        innerOptions: []
      };
    },
    watch: {
      options: {
        immediate: true,
        handler(value) {
          if (!value) {
            this.innerOptions = [];
            return;
          }
          if (value instanceof Array) {
            this.innerOptions = value;
          } else if (typeof value === "string") {
            this.getDicts && this.getDicts(value).then((rs) => {
              this.innerOptions = rs.data || [];
              //可以触发一个事件 某些情况下外部需要监听。
              this.$emit("loadDict", this.innerOptions);
            }).catch(() => {
              this.innerOptions = [];
            })
          } else if (typeof value === "function") {
            value((options) => {
              this.innerOptions = options || [];
            });
          }
        }
      }
    },
    methods: {
      onInput(value) {
        this.$emit('input', value);
      },
      onChange(value) {
        this.$emit('change', value);
      },
    },
  }
</script>
