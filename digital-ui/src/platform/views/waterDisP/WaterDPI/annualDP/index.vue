<template>
  <div class="app-containter">
    <el-form
      :inline="true"
      :model="searchOpt"
      class="query-form"
      label-width="100px"
    >
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
      <el-form-item label="用水单位：">
        <el-select
          v-model="searchOpt.waterCompany"
          style="width: 240px"
          placeholder="请选择"
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
      <div class="query-form-btns">
        <el-button type="primary" @click="onSubmit">查询</el-button>
        <el-button type="primary" @click="onReset">重置</el-button>
      </div>
    </el-form>
    <div class="__content__">
      <div class="__left__">
        <FormToolsRow
          :buttons="formTools"
          @click="onClickOps"
          v-hasPermi="['planDispatch:irrWaterUsePlanYear:add']"
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
            prop="waterCompany"
            label="用水单位"
            align="left"
          ></el-table-column>
          <el-table-column
            prop="waterYear"
            label="供水年度"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="indexWater"
            label="指标水量(万m³)"
            align="right"
          ></el-table-column>
          <el-table-column
            prop="needWater"
            label="需水量(万m³)"
            align="right"
          ></el-table-column>
          <!--<el-table-column
                        prop="irrigationArea"
                        label="灌溉面积"
                        width="120"
                        align="center"
                    ></el-table-column>
                    <el-table-column
                        prop="profitTown"
                        label="收益乡镇"
                        min-width="120"
                        align="center"
                    ></el-table-column>
                    <el-table-column
                        prop="purpose"
                        label="用途"
                        min-width="120"
                        align="left"
                    ></el-table-column>-->
          <el-table-column prop="status" label="状态" width="80" align="center">
          </el-table-column>
          <el-table-column label="操作" width="300" align="center">
            <template slot-scope="scope">
              <el-button
                type="primary"
                @click="handleEdit(scope.row)"
                v-if="scope.row.status != '已审批'"
                size="small"
                v-hasPermi="['planDispatch:irrWaterUsePlanYear:edit']"
                >编辑</el-button
              >
              <!-- <el-button
                type="primary"
                @click="handleEdit(scope.row,false)"
                v-if="scope.row.status == '已审批'"
                size="small"
                v-hasPermi="['planDispatch:irrWaterUsePlanYear:edit']"
                >查看</el-button
              > -->
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
                v-hasPermi="['planDispatch:irrWaterUsePlanYear:remove']"
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
      </div>
      <div class="__right__">
        <div class="_r_header theme-border-color3">
          年度总申请引水量<span style="font-size: 11px">(万m³)</span>
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
            prop="year"
            width="100"
            label="年度"
            align="center"
          ></el-table-column>

          <el-table-column
            prop="applyWater"
            label="申请引水量"
            min-width="100"
            align="right"
          >
          </el-table-column>

          <el-table-column
            prop="indexWater"
            label="引水指标"
            min-width="100"
            align="right"
          >
          </el-table-column>

          <el-table-column
            prop="surplusWater"
            label="剩余指标量"
            min-width="100"
            align="right"
          >
          </el-table-column>
          <!-- <el-table-column prop="b" min-width="100" align="center">
                        <template slot-scope="scope">
                            <p class="__p__">
                                <span class="__n__">申请引水量 ：</span>
                                <span> {{ scope.row.applyWater }}</span>
                            </p>
                            <p class="__p__">
                                <span class="__n__">引水指标： </span>
                                <span>{{ scope.row.indexWater }}</span>
                            </p>
                            <p class="__p__">
                                <span class="__n__">剩余指标量：</span>
                                <span>{{ scope.row.surplusWater }}</span>
                            </p>
                        </template>
                    </el-table-column> -->
        </el-table>
      </div>
    </div>
    <annualDPDialog
      ref="addOrUpdate"
      v-if="annualDPDialog.show"
      v-bind="annualDPDialog"
      @close="annualDPDialog.close"
    ></annualDPDialog>
  </div>
</template>
<script>
import annualDPDialog from "./__annualDPDialog";
import {
  LIST,
  getDetail,
  ADD,
  UPDATE,
  queryYearPlanSummary,
  DEL
} from "@/platform/api/waterDisP/annualDP";
import { queryCompentDicts } from "@/platform/api/waterR/waterInfo";
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
      annualDPDialog: {
        show: false,
        title: "",
        mode: "",
        model: null,
        chartsSetOption: {},
        close: () => {
          this.annualDPDialog.show = false;
        }
      },
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
        waterCompany: "",
        waterYear: ""
      },
      addOrUpdateVisible: false,
      tableData: [],
      tableData1: [],
      useWaterOptions: []
    };
  },
  components: {
    annualDPDialog
  },
  created() {
    this.$nextTick(() => {
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
        waterCompany: this.searchOpt.waterCompany,
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
      queryYearPlanSummary().then(res => {
        console.log(res);
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.tableData1 = res.data;
      });
    },
    onSubmit() {
      this.page = 1;
      this.fetchData();
    },
    onReset() {
      this.searchOpt = {
        waterCompany: "",
        waterYear: ""
      };
      this.page = 1;
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
    onClickOps(text) {
      if (text == "add") {
        this.annualDPDialog = {
          show: true,
          title: "新增",
          mode: "add",
          model: null,
          chartsSetOption: {},
          close: this.handleAddForm
        };
      } else if (text == "export") {
        let header = [
          "用水单位",
          "供水年度",
          "指标水量(万m³)",
          "需水量(万m³)",
          "状态"
        ];
        let filterVal = [
          "waterCompany",
          "waterYear",
          "indexWater",
          "needWater",
          "status"
        ];
        ex(header, header, filterVal, this.tableData, "年引水计划");
      } else if (text == "refresh") {
        this.onReset();
      }
    },

    handleAddForm(save, model, parms) {
      if (!save) {
        this.annualDPDialog.show = false;
        return;
      }
      ADD(model).then(res => {
        this.annualDPDialog.show = false;
        this.fetchData();
      });
    }, //修改
    handleEdit(row, isCheck = true) {
      getDetail(row.id).then(res => {
        res.data.year = row.waterYear;
        this.annualDPDialog = {
          show: true,
          title: "编辑",
          mode: "update",
          model: res.data,
          close: this.handleEditSave,
          showFooter: isCheck
        };
      });
    },
    //查看
    handleView(row) {
      getDetail(row.id).then(res => {
        console.log(res);
        this.annualDPDialog = {
          show: true,
          title: "查看",
          mode: "view",
          model: res.data,
          close: () => {
            this.annualDPDialog.show = false;
          }
        };
      });
    },
    handleEditSave(save, model, parms) {
      if (!save) {
        this.annualDPDialog.show = false;
        return;
      }
      UPDATE(model).then(res => {
        this.annualDPDialog.show = false;
        this.fetchData(false);
      });
    },
    handleP(row) {
      this.annualDPDialog = {
        show: true,
        title: "编辑",
        mode: "view",
        model: row,
        close: () => {
          this.annualDPDialog.show = false;
        }
      };
    },
    handleAudit(row) {
      getDetail(row.id).then(res => {
        this.annualDPDialog = {
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
        this.annualDPDialog.show = false;
        return;
      }
      examine(parms).then(res => {
        model.status = res.data
        this.handleEditSave(true,model)
        this.annualDPDialog.show = false;
      })

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
    flex-direction: column;
    .__left__ {
      width: 100%;
    }
    .__right__ {
      margin-top: 5px;
      width: 100%;
      ._r_header {
        width: 100%;
        text-align: center;
        height: 35px;
        font-size: 16px;
        line-height: 35px;
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
    width: 80px;
  }
}
</style>
