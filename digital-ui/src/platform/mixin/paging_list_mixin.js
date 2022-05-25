import formhelper from '../utils/formhelper.js';

/**
 * 分页列表页面的通用js逻辑
 *  1 列表查询 需要提供  doGetList(params) 函数， 设置对应的 tableData 和 total
 *  2 新增修改，是自己添加按钮监听，从EditForm中获取数据调用API。
 *  2 删除需要提供  doDelete(rows) 函数
 *  3 工具按钮 已有add update delete 操作(处理对话框 询问等操作); 额外的按钮操作,比如导出，需要添加 doClickOps(type)
 * */

export default {
  data() {
    return {
      formhelper: formhelper,//用于创建查询条件 query 对象。
      query: formhelper.create([]),
      loading: false,//table显示loading效果
      tableData: [],//table的数据
      total: 0,//全部table的条数
      selectionList: null,//用户选中的行 : @selection-change="handleSelectionChange" <el-table-column type="selection"
    };
  },
  methods: {
    //获取列表 提供 doGetList(params) 函数， 设置对应的 tableData 和 total
    getList() {
      if ('doGetList' in this) {
        this['doGetList'](this.query.model);
      } else {
        console.warn("no doGetList funciton found!")
      }
    },
    handleQuery() {
      this.query.model.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.query.model.pageNum = 1;
      this.resetForm("queryForm");
      this.getList();
    },
    //额外的按钮操作。需要添加 onClickOps(type)
    onClickOps(type, value) {
      //统一的操作监听
      if ('doClickOps' in this) {
        this['doClickOps'](type, value);
      }
      if ("refresh" === type) {
        this.getList();
        return;
      }
      if (type === "export") {
        // this.download('system/role/export', {...this.query.model}, `export.xlsx`);
        if ('doExport' in this) {
          this['doExport']();
        } else {
          console.warn("no doExport funciton found!")
        }
      }
      if (type === 'add') {
        //添加操作 弹对话框
        if ('doAdd' in this) {
          this['doAdd']();
        } else {
          console.warn("no doAdd funciton found!")
        }
        return;
      }
      if (type === "update") {
        if (!this.selectionList || !this.selectionList.length) {
          this.msgInfo("请选择一行后再操作");
          return;
        }
        if (this.selectionList.length !== 1) {
          this.msgInfo("只能选择一行");
          return;
        }
        this.handleUpdate(this.selectionList[0]);
        return;
      }
      if (type === "delete") {
        this.handleDelete(this.selectionList);
        return;
      }
    },
    //选中的行
    handleSelectionChange(list) {
      this.selectionList = list;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      //添加操作 弹对话框
      if ('doUpdate' in this) {
        this['doUpdate'](row);
      } else {
        console.warn("no doUpdate funciton found!")
      }
    },
    /** 删除按钮操作 */
    handleDelete(rows) {
      if (!rows || !rows.length) {
        this.msgInfo("请选择一行后再操作");
        return;
      }
      this.confirm(rows.length === 1 ? "确定删除当前行?" : "确定删除这" + rows.length + "行?", () => {
        //执行删除操作
        if ('doDelete' in this) {
          this['doDelete'](rows);
        } else {
          console.warn("no doDelete funciton found!")
        }
      });
    },
  },
}
