<template>
  <el-form ref="editForm" :model="edit.model" :rules="edit.rules" :inline="false" style="padding-bottom: 10px;">
    <table class="theme-table-form">
      <colgroup>
        <col width="100"></col>
        <col></col>
      </colgroup>
      <tr v-for="(value,key) in edit.items"
          :key="key">
        <td :class="themeTableLabelClass(edit.rules[key])">
          {{edit.items[key].label}}
        </td>
        <td class="theme-table-form__input">
          <DataFormItem v-bind="value" v-model="edit.model[key]">
            <template v-if="key==='vars'">
              <!--主题变量-->
              <DataButton type="primary" text="预览和编辑" @click="editThemeVars"></DataButton>
              <span
                style="display:inline-block;vertical-align: top;text-overflow:ellipsis; white-space:nowrap;width: 100px;overflow: hidden">{{edit.model.vars||'--请编辑--'}}</span>
            </template>
          </DataFormItem>
        </td>
      </tr>
    </table>
  </el-form>
</template>

<script>
  import ThemeVarsEdit from './ThemeVarsEdit.vue';
  //编辑表单form
  export default {
    name: "EditForm",
    props: {
      model: {},//修改时的model
    },
    data() {
      let formhelper = this.$formHelper;
      return {
        edit: formhelper.create([
          formhelper.itemCreator.input('主题编号', 'key', null, this.$rules.len(true, 1, 50), undefined, {type_disabled: !!this.model}),
          formhelper.itemCreator.input('主题名称', 'name', null, this.$rules.len(true, 1, 50)),
          formhelper.itemCreator.holder('主题变量', 'vars', null, this.$rules.len(true, 1, 500)),
          formhelper.itemCreator.select('主题状态', 'status', '0', this.$rules.required(), "sys_normal_disable"),
          formhelper.itemCreator.input('备注说明', 'remark', null, this.$rules.len(false, 0, 100)),
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
      editThemeVars() {
        this.$dialog.show({
          bind: this,
          title: "编辑和预览",
          width: "1300px",
          component: ThemeVarsEdit,
          fullscreen: false,
          showFooter: true,
          dragInWindowContent: false,
          propsData: {
            vars: this.edit.model.vars
          },
          onAutoOk: (rs, dialog, next) => {
            this.$refs.editForm.clearValidate("vars");
            this.edit.model.vars = dialog.$component.themeVars;
            next();
          }
        })
      },
      validate(validator) {
        if (!this.$refs.editForm) {
          return;
        }
        this.$refs.editForm.validate((ok, error) => {
          if (!ok) return;
          let rs = {...this.edit.model};
          if (typeof rs.vars === "object") {
            rs.vars = JSON.stringify(rs.vars);
          }
          validator(rs, this);
        });
      },
    },
  }
</script>
