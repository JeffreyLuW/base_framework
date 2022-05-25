<template>
  <el-form ref="editForm" :model="edit.model" :rules="edit.rules"
           :inline="false" label-width="80px">
    <DataFormItem
      v-for="(value,key) in edit.items"
      :key="key"
      v-bind="value"
      v-model="edit.model[key]"/>
  </el-form>
</template>

<script>
  import formhelper from '../../../utils/formhelper.js';

  //编辑表单form
  export default {
    name: "EditForm",
    props: {
      model: {},//修改时的model
    },
    data() {
      return {
        edit: formhelper.create([
          formhelper.itemCreator.input('角色编号', 'roleId', null, this.$rules.required()),
          formhelper.itemCreator.textarea('角色名称', 'roleName', null, this.$rules.required()),
          formhelper.itemCreator.input('权限字符', 'roleKey', null, this.$rules.required()),
          formhelper.itemCreator.input('显示顺序', 'roleSort', null, this.$rules.required()),
          formhelper.itemCreator.upload('上传附件', 'roleFiles', null, this.$rules.required(),null,{type_limit:4}),
          formhelper.itemCreator.select('操作', 'op', '1',null,"sys_oper_type")
        ], false),
      }
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
          validator(this.edit.model, this);
        });
      },
    },
  }
</script>
