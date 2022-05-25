<template>
  <el-form ref="editForm" :model="edit.model" :rules="edit.rules"
           :inline="false" :label-width="labelWidth">
    <DataFormItem
      v-for="(value,key) in edit.items"
      :key="key"
      v-bind="value"
      v-model="edit.model[key]"/>
  </el-form>
</template>

<script>
  import formhelper from '../../utils/formhelper.js';

  const defaultEditConfig = formhelper.create([], false);
  //一个通用的编辑表单form
  //后期可以支持更加复杂的布局。
  export default {
    name: "DataEditForm",
    props: {
      model: {},//修改时的model
      edit: {
        type: Object,
        default: defaultEditConfig
      },
      labelWidth: {
        default: "80px"
      }
    },
    data() {
      return {}
    },
    created() {
      //修改的话，需要替换部分属性值为外部传递值
      if (this.model) {
        this.edit.model = this.$util.coverValue(this.edit.model, this.model, true)
      }
    },
    methods: {
      validate(validator) {
        if (!this.$refs.editForm) {
          return;
        }
        this.$refs.editForm.validate((ok, error) => {
          if (!ok) return;
          validator({...this.edit.model}, this);
        });
      },
    },
  }
</script>
