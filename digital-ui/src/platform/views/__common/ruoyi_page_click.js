//统一处理若依原页面的工具栏。
export default {
  methods: {
    onClickOps(type, e) {
      if (type === "edit") type = "update";
      if (type === "remove") type = "delete";
      if (type === "update" || type === "delete") {
        if (!this.ids || !this.ids.length) {
          this.msgInfo("请选择一行后再操作");
          return;
        }
      }
      if (type === "update") {
        if (this.ids.length !== 1) {
          this.msgInfo("只能选择一行");
          return;
        }
      }
      let typeMethod = {
        'add': this.handleAdd,
        'update': this.handleUpdate,
        'delete': this.handleDelete,
        'import': this.handleImport,
        'export': this.handleExport,
        'refresh': this.getList
      };
      if (!typeMethod[type]) return;
      typeMethod[type](e);
    },
  }
}
