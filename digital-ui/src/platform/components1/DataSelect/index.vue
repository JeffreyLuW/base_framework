<template>
  <el-select ref="elSelect" :value="value"
             :placeholder="placeholder"
             :disabled="disabled"
             :clearable="clearable"
             :multiple="multiple"
             :filterable="filterable"
             :remote="remote"
             :loading="loading"
             :remote-method="remoteMethod"
             :size="size"
             :collapse-tags="collapseTags"
             :multiple-limit="multipleLimit"
             :name="name"
             :autocomplete="autocomplete"
             :allow-create="allowCreate"
             :loading-text="loadingText"
             :no-match-text="noMatchText"
             :no-data-text="noDataText"
             :popper-class="popperClass"
             :reserve-keyword="reserveKeyword"
             :default-first-option="defaultFirstOption"
             :popper-append-to-body="popperAppendToBody"
             :automatic-dropdown="automaticDropdown"
             @input="onInput"
             @change="onChange"
             @visible-change="onVisibleChange"
             @remove-tag="onRemoveTag"
             @clear="onClear"
             @blur="onBlur"
             @focus="onFocus"
  >
    <el-option v-for="item in innerOptions"
               :value="item.value"
               :key="item.value"
               :label="item.label"
               :disabled="item.disabled"
    >
    </el-option>
  </el-select>
</template>

<script>
  //用于实现通用下拉
  export default {
    name: "DataSelect",
    props: {
      value: {},//单选是基本类型，多选是数组
      options: {
        type: String | Function | Array,//String|Function:loadFn(next([{label,value}]))|Array:[{value,label,size,border,disabled,name}]
      },
      placeholder: {},
      disabled: {},
      clearable: {},
      multiple: {},
      filterable: {},
      remote: {},
      loading: {},
      remoteMethod: {},
      size: {},
      collapseTags: {},
      multipleLimit: {},
      name: {},
      autocomplete: {},
      allowCreate: {},
      loadingText: {},
      noMatchText: {},
      noDataText: {},
      popperClass: {},
      reserveKeyword: {},
      defaultFirstOption: {},
      popperAppendToBody: {},
      automaticDropdown: {}
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
      onVisibleChange(value) {
        this.$emit('visible-change', value);
      },
      onRemoveTag(value) {
        this.$emit('remove-tag', value);
      },
      onClear(value) {
        this.$emit('clear', value);
      },
      onBlur(value) {
        this.$emit('blur', value);
      },
      onFocus(value) {
        this.$emit('focus', value);
      },
      focus() {
        if (this.$refs.elSelect) {
          this.$refs.elSelect.focus();
        }
      },
      blur() {
        if (this.$refs.elSelect) {
          this.$refs.elSelect.blur();
        }
      }
    },
  }
</script>
