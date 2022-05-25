<template>
  <div class="app-containter">
    <el-form :inline="true" :model="searchOpt" class="demo-form-inline">
      <el-form-item label="供水年度：">
        <el-date-picker
         @change="onSubmit"
          v-model="searchOpt.waterYear"
          align="right"
          type="year"
          value-format="yyyy"
          placeholder="选择供水年度"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="标题:">
        <el-input v-model="searchOpt.title" placeholder="请输入标题"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">查询</el-button>
        <el-button type="primary" @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <FormToolsRow :buttons="formTools" @click="onClickOps"></FormToolsRow>
    <el-table
      id="table"
      :stripe="true"
      :data="tableData"
      border
      @selection-change="dataListSelectionChangeHandle"
      style="width: 100%"
      height="100%"
    >
      <el-table-column
        type="index"
        width="50"
        label="序号"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="waterYear"
        label="供水年度"
        width="100"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="title"
        label="标题"
        min-width="120"
        align="left"
      ></el-table-column>
      <el-table-column
        prop="waterDiversionIndex"
        label="许可水量(万m³)"
        min-width="120"
        align="left"
      ></el-table-column>
      <el-table-column
        prop="agriculturalWater"
        label="农业用水(万m³)"
        width="120"
        align="right"
      ></el-table-column>
      <el-table-column
        prop="industrialWater"
        label="工业用水(万m³)"
        width="120"
        align="right"
      ></el-table-column>
      <el-table-column
        prop="ecologicalWater"
        label="生态用水(万m³)"
        width="120"
        align="right"
      ></el-table-column>
      <el-table-column
        prop="liveWater"
        label="生活用水(万m³)"
        width="120"
        align="right"
      ></el-table-column>
      <!-- <el-table-column
                prop="waterStartDate"
                label="调水开始日期"
                width="120"
                align="center"
            ></el-table-column>
            <el-table-column
                prop="waterEndDate"
                label="调水结束日期"
                width="120"
                align="center"
            ></el-table-column>
            <el-table-column
                prop="writeStartDate"
                label="供水开始日期"
                width="120"
                align="center"
            ></el-table-column>
            <el-table-column
                prop="writeEndDate"
                label="供水结束日期"
                width="120"
                align="center"
            ></el-table-column> -->
      <el-table-column label="操作" width="220" align="center">
        <template slot-scope="scope">
          <el-button type="primary" @click="handleEdit(scope.row)" size="small"
            >编辑</el-button
          >
          <el-button type="danger" size="small" @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      :current-page="page"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="pageSizeChangeHandle"
      @current-change="pageCurrentChangeHandle"
    ></el-pagination>
    <waterRulesDialog
      ref="addOrUpdate"
      v-if="waterRulesDialog.show"
      v-bind="waterRulesDialog"
      @close="waterRulesDialog.close"
    ></waterRulesDialog>
  </div>
</template>
<script>
import waterRulesDialog from "./__waterRulesDialog";
import { LIST, getDetail, ADD, UPDATE, DEL } from "@/platform/api/waterDisP/waterRules";
export default {
  data() {
    return {
      // 分页器属性
      page: 1,
      pageSize: 10,
      total: 2,
      props: { multiple: true },
      formTools: ["add"],
      waterRulesDialog: {
        show: false,
        title: "",
        mode: "",
        model: null,
        chartsSetOption: {},
        close: () => {
          this.waterRulesDialog.show = false;
        },
      },
      options: [
        {
          value: 1,
          label: "上旬",
        },
        {
          value: 2,
          label: "中旬",
        },
        {
          value: 3,
          label: "下旬",
        },
      ],
      searchOpt: {
        waterYear: "",
        title: "",
      },
      addOrUpdateVisible: false,
      tableData: [],
    };
  },
  components: {
    waterRulesDialog,
  },
  created() {
    this.$nextTick(() => {
      this.fetchData();
    });
  },
  methods: {
    fetchData() {
      let query = {
        pageNum: this.page,
        pageSize: this.pageSize,
        title: this.searchOpt.title,
        waterYear: this.searchOpt.waterYear,
      };
      LIST(query).then((res) => {
        console.log(res);
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.tableData = res.rows;
        this.total = res.total;
      });
    },
    onSubmit() {
      this.page = 1;
      this.fetchData();
    },
    onReset() {
      this.page = 1;
      this.searchOpt = {
        title: "",
        waterYear: "",
      };
      this.fetchData();
    },
    dataListSelectionChangeHandle() {},
    //分页器事件
    pageSizeChangeHandle(pageSize) {
      this.pageSize = pageSize;
      this.page = 1;
      this.fetchData();
    },
    pageCurrentChangeHandle(page) {
      this.page = page;
      this.fetchData();
    },
    getDataList() {},
    onClickOps(text) {
      if (text == "add") {
        this.waterRulesDialog = {
          show: true,
          title: "新增计划",
          mode: "",
          model: null,
          chartsSetOption: {},
          close: this.handleAddForm,
        };
      } else if (text == "refresh") {
        this.onReset();
      }
    },
    handleAddForm(save, model, parms) {
      if (!save) {
        this.waterRulesDialog.show = false;
        return;
      }
      ADD(model).then((res) => {
        this.waterRulesDialog.show = false;
        this.fetchData();
      });
    }, //修改
    handleEdit(row) {
      getDetail(row.id).then((res) => {
        this.waterRulesDialog = {
          show: true,
          title: "编辑",
          mode: "update",
          model: res.data,
          close: this.handleEditSave,
        };
      });
    },
    handleEditSave(save, model, parms) {
      if (!save) {
        this.waterRulesDialog.show = false;
        return;
      }
      UPDATE(model).then((res) => {
        this.waterRulesDialog.show = false;
        this.fetchData(false);
      });
    },
    handleDelete(row) {
      this.$confirm("确定要删除该记录吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          DEL(row.id).then((res) => {
            this.fetchData();
          });
        })
        .catch(() => {});
    },
  },
};
</script>
<style lang="scss" scoped>
.app-containter {
  padding: 10px;
  display: flex;
  flex-direction: column;
}
.el-pagination {
  margin-top: 5px;
  text-align: left;
}
</style>
