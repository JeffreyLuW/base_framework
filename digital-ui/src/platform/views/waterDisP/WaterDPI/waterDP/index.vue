<template>
  <div class="app-containter">
    <el-form
      :inline="true"
      :model="searchOpt"
      class="query-form"
      label-width="100px"
    >
      <el-form-item label="月度：">
        <el-date-picker
          @change="onSubmit"
          v-model="searchOpt.yearMonth"
          type="month"
          format="yyyy-MM"
          style="width: 240px"
          value-format="yyyy-M"
          placeholder="选择月度"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="旬：">
        <el-select
          v-model="searchOpt.tenDays"
          style="width: 240px"
          placeholder="请选择旬"
        >
          <el-option
            v-for="item in stOptions"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用水单位：">
        <el-select
          v-model="searchOpt.waterCompany"
          style="width: 240px"
          placeholder="请选择用水单位"
        >
          <el-option
            v-for="item in useWaterOptions"
            :key="item.value"
            :label="item.companyKey"
            :value="item.companyKey"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态：">
        <el-select
          v-model="searchOpt.status"
          style="width: 240px"
          placeholder="请选择状态"
        >
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.label"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <div class="query-form-btns">
        <el-button type="primary" @click="onSubmit">查询</el-button>
        <el-button type="primary" @click="onReset">重置</el-button>
      </div>
    </el-form>
    <FormToolsRow
      :buttons="formTools"
      @click="onClickOps"
      v-hasPermi="['planDispatch:irrWaterUsePlanTendays:add']"
    >
    </FormToolsRow>
    <el-table
      id="table"
      :stripe="true"
      :data="tableData"
      border
      style="width: 100%"
    >
      <el-table-column
        type="index"
        width="50"
        label="序号"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="yearMonth"
        label="供水月度"
        width="100"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="tenDays"
        label="旬"
        width="80"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="waterCompany"
        label="用水单位"
        min-width="120"
        align="center"
      ></el-table-column>

      <el-table-column
        prop="indexWater"
        label="指标水量(万m³)"
        min-width="120"
        align="right"
      ></el-table-column>
      <el-table-column
        prop="needWater"
        label="需水量(万m³)"
        min-width="120"
        align="right"
      ></el-table-column>
      <!--<el-table-column
                        prop="irrigationArea"
                        label="灌溉面积"
                        width="120"
                        align="center"
                    ></el-table-column>-->
      <el-table-column prop="status" label="状态" width="100" align="center">
      </el-table-column>
      <el-table-column label="操作" width="220" align="center">
        <template slot-scope="scope">
          <el-button
            type="primary"
            @click="handleEdit(scope.row)"
            size="small"
            v-hasPermi="['planDispatch:irrWaterUsePlanTendays:edit']"
            >编辑</el-button
          >
          <el-button
            type="danger"
            size="small"
            @click="handleDelete(scope.row)"
            v-hasPermi="['planDispatch:irrWaterUsePlanTendays:remove']"
            >删除</el-button
          >
          <el-button
            type="primary"
            v-if="scope.row.status == '1' || scope.row.status == '待审批'"
            size="small"
            @click="handleAudit(scope.row)"
            v-hasPermi="['water:plan:examine']"
            >审核</el-button
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
    <waterDPDialog
      ref="addOrUpdate"
      v-if="waterDPDialog.show"
      v-bind="waterDPDialog"
      @close="waterDPDialog.close"
    ></waterDPDialog>
  </div>
</template>
<script>
import waterDPDialog from "./__waterDPDialog";
import {
  LIST,
  getDetail,
  ADD,
  UPDATE,
  DEL
} from "@/platform/api/waterDisP/waterDP";
import { examine } from "@/platform/api/waterDisP/com";
import { queryCompentDicts } from "@/platform/api/waterR/waterInfo";
import { ex } from "@/platform/vendor/ExportExcel";
export default {
  data() {
    return {
      // 分页器属性
      page: 1,
      pageSize: 10,
      total: 2,
      props: { multiple: true },
      formTools: ["add", "export", "examine"],
      waterDPDialog: {
        show: false,
        title: "",
        mode: "",
        model: null,
        chartsSetOption: {},
        close: () => {
          this.waterDPDialog.show = false;
        }
      },
      statusOptions: [
        {
          value: 0,
          label: "草稿"
        },
        {
          value: 1,
          label: "待审批"
        },
        {
          value: 2,
          label: "已审批"
        },
        {
          value: 3,
          label: "已驳回"
        }
      ],
      status: ["草稿", "待审批", "已审批", "已驳回"],
      stOptions: [],
      useWaterOptions: [],
      searchOpt: {},
      addOrUpdateVisible: false,
      tableData: []
    };
  },
  components: {
    waterDPDialog
  },
  created() {
    this.$nextTick(() => {
      this.getDicts("ten_days").then(res => {
        this.stOptions = res.data;
      });
      queryCompentDicts().then(res => {
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.useWaterOptions = res.data;
      });
      this.fetchData();
    });
  },
  methods: {
    fetchData() {
      let query = {
        pageNum: this.page,
        pageSize: this.pageSize,
        year: this.searchOpt.yearMonth
          ? this.searchOpt.yearMonth.split("-")[0]
          : "",
        month: this.searchOpt.yearMonth
          ? this.searchOpt.yearMonth.split("-")[1]
          : "",
        tendays: this.searchOpt.tenDays,
        status: this.searchOpt.status,
        company: this.searchOpt.waterCompany
      };
      LIST(query).then(res => {
        console.log(res);
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.tableData = res.rows;
        this.total = res.total;
      });
    },
    curYear(val) {
      console.log(val);
      this.searchOpt.currentYear = val;
      this.fetchData();
    },
    onSubmit() {
      this.page = 1;
      this.fetchData();
    },
    onReset() {
      this.page = 1;
      this.searchOpt = {
        title: "",
        waterYear: ""
      };
      this.fetchData();
    },
    //分页器事件
    pageSizeChangeHandle(pageSize) {
      this.page = 1;
      this.pageSize = pageSize;
      this.fetchData();
    },
    pageCurrentChangeHandle(page) {
      this.page = page;
      this.fetchData();
    },
    getDataList() {},
    onClickOps() {},
    onClickOps(text) {
      if (text == "add") {
        this.waterDPDialog = {
          show: true,
          title: "新增计划",
          mode: "add",
          model: null,
          chartsSetOption: {},
          close: this.handleAddForm
        };
      } else if (text == "export") {
        let header = [
          "供水月度",
          "旬",
          "用水单位",
          "指标水量(万m³)",
          "需水量(万m³)",
          "状态"
        ];
        let filterVal = [
          "yearMonth",
          "tenDays",
          "waterCompany",
          "indexWater",
          "needWater",
          "status"
        ];
        ex(header, header, filterVal, this.tableData, `旬引水计划`);
      } else if (text == "refresh") {
        this.onReset();
      }
    },
    handleAddForm(save, model, parms) {
      if (!save) {
        this.waterDPDialog.show = false;
        return;
      }
      ADD(model).then(res => {
        this.waterDPDialog.show = false;
        this.fetchData();
      });
    }, //修改
    handleEdit(row) {
      getDetail(row.id).then(res => {
        console.log(res);
        res.data.month = row.yearMonth.split("-")[1];
        res.data.year = row.yearMonth.split("-")[0];
        this.waterDPDialog = {
          show: true,
          title: "编辑",
          mode: "update",
          model: res.data,
          close: this.handleEditSave
        };
      });
    },
    handleEditSave(save, model, parms) {
      if (!save) {
        this.waterDPDialog.show = false;
        return;
      }
      UPDATE(model).then(res => {
        this.waterDPDialog.show = false;
        this.fetchData(false);
      });
    },
    handleAudit(row) {
      getDetail(row.id).then(res => {
        console.log(res);
        res.data.month = row.yearMonth.split("-")[1];
        res.data.year = row.yearMonth.split("-")[0];
        this.waterDPDialog = {
          show: true,
          title: "编辑",
          mode: "audit",
          model: res.data,
          close: this.handleAuditSave
        };
      });
    },
    handleAuditSave(save, parms,model) {
      if (!save) {
        this.waterDPDialog.show = false;
        return;
      }
      examine(parms).then(res => {
        model.status = res.data
        this.handleEditSave(true,model)
        this.waterDPDialog.show = false;
      });
    },
    handleDelete(row) {
      this.$confirm("确定要删除该记录吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          DEL(row.id).then(res => {
            this.fetchData();
          });
        })
        .catch(() => {});
    }
  }
};
</script>
<style lang="scss" scoped>
.app-containter {
  padding: 10px;
  display: flex;
  flex-direction: column;
  .__content__ {
    display: flex;
    .__left__ {
      width: 80%;
    }
    .__right__ {
      margin-left: 1%;
      width: 19%;
      ._r_header {
        font-weight: 400;
        width: 100%;
        text-align: center;
        height: 59px;
        font-size: 14px;
        line-height: 27px;
        border-style: solid;
        border-width: 1px;
        border-bottom: none;
      }
    }
  }
}
.el-pagination {
  margin-top: 5px;
  text-align: left;
}
.__p__ {
  text-align: left;
  padding: 0 10px;
}
</style>
