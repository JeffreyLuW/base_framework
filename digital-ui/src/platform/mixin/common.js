/**
 * 一些常用的全局mixins
 */
export default {
  methods: {
    confirm(msg, cb) {
      return this.$confirm(msg, "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(cb).catch(function () {
      });
    },
    //判断是否有权限，数组或者逗号隔开的字符串
    // value=[] | "system:config:list,system:config:edit"
    hasPermi(value) {
      let store = this.$store;
      if (value && typeof value === "string") {
        value = value.split(',');
      }
      let hasPermissions = false;
      const all_permission = "*:*:*";
      const permissions = store.getters && store.getters.permissions;
      if (value && value instanceof Array && value.length > 0) {
        const permissionFlag = value;
        hasPermissions = permissions.some(permission => {
          return all_permission === permission || permissionFlag.includes(permission)
        });
      }
      return hasPermissions;
    },
    //表格表单
    themeTableLabelClass(rules) {
      if (!rules || !rules.length) {
        return {'theme-table-form__label': true};
      }
      return {'theme-table-form__label': true, 'theme-table-form__required': rules[0].required};
    },
    //表格空值转 -
    emptyLineFormatter(row, column, cellValue, index) {
      if (cellValue === "" || cellValue === null || cellValue === undefined)
        return '-';
      return cellValue;
    },
  }
}
