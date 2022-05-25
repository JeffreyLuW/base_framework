<template>
  <el-form-item ref="item" :label="label" :prop="prop" :labelWidth="labelWidth" v-bind="type_attrs.default">
    <component v-if="type" :is="type" v-bind="type_attrs.type" v-on="type_listeners.type" :value="value"
               @input="$emit('input',$event)"></component>
    <slot></slot>
  </el-form-item>
</template>

<script>
  import Jsx from '../Jsx/index.vue';
  import type_attrs_mixins from '../../mixin/type_attrs_mixins.js';
  //form-item去slot，改配置形式。杜绝表单元素的整体_render()
  //type_开头的属性，会匹配到内部的component元素上去。注意不会深度watch，如果需要，自行propValue={...}赋值。
  export default {
    name: "DataFormItem",
    inheritAttrs: false,
    mixins: [type_attrs_mixins],
    components: {Jsx},
    props: {
      prop: {},
      label: {},
      labelWidth: {},

      type: {},
      value: {},
      evt: {},
    },
    data() {
      return {
        type_attrs: {default: null, type: null},
        type_listeners: {default: null, type: null},
      };
    },
  }
</script>
