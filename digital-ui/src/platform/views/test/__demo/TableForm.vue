<template>
  <!--演示表格-->
  <el-form style="padding-bottom: 10px;" :model="edit.model" :rules="edit.rules">
    <table class="theme-table-form">
      <colgroup>
        <col width="100"></col>
        <col></col>
        <col width="100"></col>
      </colgroup>
      <tr>
        <!--必填时添加theme-table-form__required 即可-->
        <td class="theme-table-form__label theme-table-form__required">角色名</td>
        <td class="theme-table-form__input">
          <DataFormItem v-bind="edit.items.roleName" v-model="edit.model.roleName"></DataFormItem>
        </td>
        <td class="theme-table-form__label theme-table-form__required">角色Key</td>
        <td class="theme-table-form__input">
          <DataFormItem v-bind="edit.items.roleKey" v-model="edit.model.roleKey"></DataFormItem>
        </td>
      </tr>
      <tr>
        <td class="theme-table-form__label">用户名</td>
        <td class="theme-table-form__input" colspan="3">
          <DataFormItem type="el-input" type_placeholder="输入用户名"></DataFormItem>
        </td>
      </tr>
      <tr>
        <td class="theme-table-form__label">操作</td>
        <td class="theme-table-form__input">
          <DataFormItem v-bind="edit.items.op" v-model="edit.model.op"></DataFormItem>
        </td>
        <td class="theme-table-form__label theme-table-form__required">编号</td>
        <td class="theme-table-form__input">
          <DataFormItem v-bind="edit.items.roleId" v-model="edit.model.roleId"></DataFormItem>
        </td>
      </tr>
    </table>
  </el-form>
</template>

<script>
  //演示一个普通的table布局。
  //el-form中嵌套一个table布局。
  export default {
    name: "TableForm",
    data() {
      return {
        edit: this.$formHelper.create([
          this.$formHelper.itemCreator.input('角色编号', 'roleId', null, this.$rules.required()),
          this.$formHelper.itemCreator.input('角色名称', 'roleName', null, this.$rules.required()),
          this.$formHelper.itemCreator.input('权限字符', 'roleKey', null, this.$rules.required()),
          this.$formHelper.itemCreator.select('操作', 'op', '1', null, "sys_oper_type")
        ], false),
      }
    },
  }
</script>

<style scoped>

</style>
