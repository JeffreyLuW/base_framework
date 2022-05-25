<template>
  <div class="app-containter">
    <el-form :inline="true" :model="searchOpt" class="query-form" label-width="100px">
      <el-form-item label="月度：">
        <el-date-picker
          v-model="searchOpt.yearMonth"
          type="month"
          format="yyyy-MM"
          style="width: 220px"
          value-format="yyyy-M"
          placeholder="选择月度"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="旬：">
        <el-select
          v-model="searchOpt.tenDays"
          style="width: 220px"
          placeholder="请选择旬"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用水单位：">
        <el-select
          v-model="searchOpt.waterCompany"
          style="width: 220px"
          placeholder="请选择用水单位"
        >
          <el-option
            v-for="item in useWaterOptions"
            :key="item.value"
            :label="item.companyKey"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <div class="query-form-btns">
        <el-button type="primary" @click="onSubmit">查询</el-button>
        <el-button type="primary" @click="onReset">重置</el-button>
      </div>
    </el-form>
    <FormToolsRow :buttons="formTools" @click="onClickOps"> </FormToolsRow>
    <el-table id="table" :stripe="true" :data="tableData" border style="width: 100%">
      <el-table-column
        type="index"
        width="50"
        label="序号"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="yearMonth"
        label="供水月份"
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
        align="left"
      ></el-table-column>
      <el-table-column
        prop="formerWater"
        label="调整前(万m³)"
        min-width="120"
        align="right"
      ></el-table-column>
      <el-table-column
        prop="needWater"
        label="调整后(万m³)"
        min-width="120"
        align="right"
      ></el-table-column>
      <el-table-column label="操作" width="220" align="center">
        <template slot-scope="scope">
          <el-button
            type="primary"
            @click="handleEdit(scope.row, 'add')"
            size="small"
            v-hasPermi="['water:adjust:add']"
            >新增</el-button
          >
          <!-- <el-button
            type="primary"
            @click="handleEdit(scope.row, 'update')"
            size="small"
            v-hasPermi="['water:adjust:edit']"
            >调整</el-button
          > -->
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
    <waterADDialog
      ref="addOrUpdate"
      v-if="waterADDialog.show"
      v-bind="waterADDialog"
      @close="waterADDialog.close"
    ></waterADDialog>
  </div>
</template>
<script>
import waterADDialog from "./__waterADDialog";
import { LIST, getDetail, ADD, UPDATE, DEL } from "@/platform/api/waterDisP/waterAD";
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
      formTools: ["export", "examine"],
      waterADDialog: {
        show: false,
        title: "",
        mode: "",
        model: null,
        chartsSetOption: {},
        close: () => {
          this.waterADDialog.show = false;
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
      status: ["草稿", "待审批", "已审批", "已驳回"],
      useWaterOptions: [],
      searchOpt: {},
      addOrUpdateVisible: false,
      tableData: [],
    };
  },
  components: {
    waterADDialog,
  },
  created() {
    this.$nextTick(() => {
      queryCompentDicts().then((res) => {
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
        year: this.searchOpt.yearMonth ? this.searchOpt.yearMonth.split("-")[0] : "",
        month: this.searchOpt.yearMonth ? this.searchOpt.yearMonth.split("-")[1] : "",
        tenDays: this.searchOpt.tenDays,
        waterCompany: this.searchOpt.waterCompany,
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
        waterYear: "",
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
        this.waterADDialog = {
          show: true,
          title: "新增计划",
          mode: "",
          model: null,
          chartsSetOption: {},
          close: this.handleAddForm,
        };
      } else if (text == "export") {
        let header = ["供水月份", "旬", "用水单位", "指标水量(万m³)", "需水量(万m³)"];
        let filterVal = [
          "yearMonth",
          "tenDays",
          "waterCompany",
          "formerWater",
          "needWater",
        ];
        ex(header, header, filterVal, this.tableData, "水量调整");
      } else if (text == "refresh") {
        this.onReset();
      }
    },
    handleAddForm(save, model, parms) {
      if (!save) {
        this.waterADDialog.show = false;
        return;
      }
      ADD(model).then((res) => {
        this.waterADDialog.show = false;
        this.fetchData();
      });
    }, //修改
    handleEdit(row, type) {
      getDetail(row.id).then((res) => {
        console.log(res);
        this.waterADDialog = {
          show: true,
          title: type == "add" ? "新增" : "编辑",
          mode: type,
          model: res.data,
          close: this.handleEditSave,
        };
        // this.waterADDialog.model.id = row.id
        // console.log(this.waterADDialog);
      });
    },
    handleEditSave(save, model, parms) {
      console.log(model);
      if (!save) {
        this.waterADDialog.show = false;
        return;
      }
      if (this.waterADDialog.mode == "add") {
        ADD(model).then((res) => {
          this.waterADDialog.show = false;
          this.fetchData(false);
        });
      } else {
        UPDATE(model).then((res) => {
          this.waterADDialog.show = false;
          this.fetchData(false);
        });
      }
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
