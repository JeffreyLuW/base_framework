<template>
  <div class="app-containter">
    <div class="__header__">
      <el-form :inline="true" :model="searchOpt" class="demo-form-inline">
        <!-- <el-form-item label="供水年度：">
                    <el-date-picker
                        v-model="searchOpt.value2"
                        align="right"
                        type="year"
                        placeholder="选择供水年度"
                    ></el-date-picker>
                </el-form-item>
                <el-form-item label="用水单位:">
                    <el-input
                        v-model="searchOpt.user"
                        placeholder="请输入用水单位"
                    ></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit">查询</el-button>
                    <el-button type="primary" @click="onReset">重置</el-button> 
                </el-form-item> -->
        <SelectYear @change-year="curYear"></SelectYear>
      </el-form>
    </div>
    <div class="__content__">
      <div class="__left__">
        <FormToolsRow
          :buttons="formTools"
          @click="onClickOps"
          v-hasPermi="['planDispatch:irrWaterUsePlanMonth:add']"
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
            prop="waterCompany"
            label="用水单位"
            min-width="120"
            align="left"
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
          <el-table-column
            prop="status"
            label="状态"
            width="100"
            align="center"
          ></el-table-column>
          <el-table-column label="操作" width="300">
            <template slot-scope="scope">
              <el-button
                type="primary"
                @click="handleEdit(scope.row)"
                v-if="scope.row.status != '已审批'"
                size="small"
                v-hasPermi="['planDispatch:irrWaterUsePlanMonth:edit']"
                >编辑</el-button
              >
              <el-button
                type="primary"
                @click="handleView(scope.row)"
                size="small"
                v-hasPermi="['planDispatch:irrWaterUsePlanMonth:info']"
                >查看</el-button
              >
              <el-button
                type="danger"
                size="small"
                @click="handleDelete(scope.row)"
                v-hasPermi="['planDispatch:irrWaterUsePlanMonth:remove']"
                >删除</el-button
              >

              <el-button
                type="primary"
                v-if="scope.row.status == '1' || scope.row.status == '待审批'"
                size="small"
                v-hasPermi="['water:plan:examine']"
                @click="handleAudit(scope.row)"
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
      </div>
      <div class="__right__">
        <div class="_r_header theme-border-color3">
          用水单位申请引水量(万m³)<br />
          （已审批通过纳入计算）
        </div>
        <el-table
          id="table"
          :stripe="true"
          :data="tableData1"
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
            label="用水单位"
            prop="company"
            min-width="120"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="applyWater"
            label="申请引水量"
            min-width="100"
            align="right"
          ></el-table-column>
          <el-table-column
            prop="indexWater"
            label="引水指标"
            min-width="100"
            align="right"
          ></el-table-column>
          <el-table-column
            prop="surplusWater"
            label="剩余指标量"
            min-width="100"
            align="right"
          ></el-table-column>
          <!-- <el-table-column min-width="100" align="center">
                        <template slot-scope="scope">
                            <p class="__p__">
                                <span class="__n__">申请量 ：</span>
                                <span>{{ scope.row.applyWater }}</span>
                            </p>
                            <p class="__p__">
                                <span class="__n__">指标量：</span>
                                <span>{{ scope.row.indexWater }}</span>
                            </p>
                            <p class="__p__">
                                <span class="__n__">剩余量：</span>
                                <span>{{ scope.row.surplusWater }}</span>
                            </p>
                        </template>
                    </el-table-column> -->
        </el-table>
      </div>
    </div>
    <moonDPDialog
      ref="addOrUpdate"
      v-if="moonDPDialog.show"
      v-bind="moonDPDialog"
      @close="moonDPDialog.close"
    ></moonDPDialog>
  </div>
</template>
<script>
import moonDPDialog from "./__moonDPDialog";
import SelectYear from "../../__comom/SelectYear";
import {
  LIST,
  getDetail,
  ADD,
  UPDATE,
  queryMonthPlanSummary,
  DEL
} from "@/platform/api/waterDisP/moonDP";
import { examine } from "@/platform/api/waterDisP/com";
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
      moonDPDialog: {
        show: false,
        title: "",
        mode: "",
        model: null,
        chartsSetOption: {},
        close: () => {
          this.moonDPDialog.show = false;
        }
      },
      currentYear: new Date().getFullYear(),
      options: [
        {
          value: 1,
          label: "上旬"
        },
        {
          value: 2,
          label: "中旬"
        },
        {
          value: 3,
          label: "下旬"
        }
      ],
      searchOpt: {
        year: new Date().getFullYear()
      },
      addOrUpdateVisible: false,
      tableData: [],
      tableData1: []
    };
  },
  components: {
    moonDPDialog,
    SelectYear
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
        year: this.searchOpt.year,
        waterYear: this.searchOpt.waterYear
      };
      LIST(query).then(res => {
        console.log(res);
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.tableData = res.rows;
        this.total = res.total;
      });
      queryMonthPlanSummary(this.searchOpt.year).then(res => {
        console.log(res);
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.tableData1 = res.data || [];
      });
    },
    curYear(val) {
      console.log(val);
      this.searchOpt.year = val;
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
        this.moonDPDialog = {
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
          "用水单位",
          "指标水量(万m³)",
          "需水量(万m³)",
          "状态"
        ];
        let filterVal = [
          "yearMonth",
          "waterCompany",
          "indexWater",
          "needWater",
          "status"
        ];
        ex(
          header,
          header,
          filterVal,
          this.tableData,
          `${this.currentYear}年月引水计划`
        );
      } else if (text == "refresh") {
        this.onReset();
      }
    },
    handleAddForm(save, model, parms) {
      if (!save) {
        this.moonDPDialog.show = false;
        return;
      }
      ADD(model).then(res => {
        this.moonDPDialog.show = false;
        this.fetchData();
      });
    }, //修改
    handleEdit(row) {
      getDetail(row.id).then(res => {
        console.log(res);
        this.moonDPDialog = {
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
        this.moonDPDialog.show = false;
        return;
      }
      UPDATE(model).then(res => {
        this.moonDPDialog.show = false;
        this.fetchData(false);
      });
    },
    //查看
    handleView(row) {
      getDetail(row.id).then(res => {
        console.log(res);
        this.moonDPDialog = {
          show: true,
          title: "查看",
          mode: "view",
          model: res.data,
          close: () => {
            this.moonDPDialog.show = false;
          }
        };
      });
    },
    handleAudit(row) {
      getDetail(row.id).then(res => {
        console.log(res);
        this.moonDPDialog = {
          show: true,
          title: "审核",
          mode: "audit",
          model: res.data,
          close: this.handleAuditSave
        };
      });
    },
    handleAuditSave(save, parms,model) {
      if (!save) {
        this.moonDPDialog.show = false;
        return;
      }
      examine(parms).then(res => {
        model.status = res.data;
        this.handleEditSave(true, model);
        this.moonDPDialog.show = false;
        // this.fetchData(false);
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
  height: 100%;
  .__content__ {
    padding-top: 10px;
    display: flex;
    height: 100%;
    flex-direction: column;
    .__left__ {
      width: 100%;
    }
    .__right__ {
      margin-top: 5px;
      width: 100%;
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
  display: flex;
  justify-content: space-between;
  .__n__ {
    text-align: right;
    display: inline-block;
    // width: 80px;
  }
}
</style>
