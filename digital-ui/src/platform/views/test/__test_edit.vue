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
  import formhelper from '../../utils/formhelper.js';

  //编辑表单form
  export default {
    name: "EditForm",
    props: {
      model: {},//修改时的model
    },
    data() {
      return {
        edit: formhelper.create([
          // model?formhelper.itemCreator.input('笔记编号', 'noteId', null, this.$rules.required()):null,
          formhelper.itemCreator.select('笔记状态', 'status', '0', null,"cxm_note_status"),
          formhelper.itemCreator.input('笔记标题', 'title', null, this.$rules.required()),
          formhelper.itemCreator.input('笔记内容', 'detail', null, this.$rules.required()),
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
