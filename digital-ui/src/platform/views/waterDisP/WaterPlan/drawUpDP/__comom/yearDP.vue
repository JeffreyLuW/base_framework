<template>
  <div class="" style="height: 100%">
    <div class="__header__">
      <SelectYear @change-year="changeYear"></SelectYear>
      <!-- <el-button
                type="primary"
                v-if="showAdd"
                style="margin-left: 10px"
                @click="handleAdd"
                >新增</el-button
            >
            <el-button
                type="primary"
                v-if="showEdit"
                style="margin-left: 10px"
                @click="handleEdit"
                >编辑</el-button
            > -->
      <el-button
        type="primary"
        style="margin-left: 10px"
        @click="handleEdit"
        v-hasPermi="['year:make:add']"
        >生成引水计划</el-button
      >
      <el-button type="primary" @click="handlePrint">打印取水申请表</el-button>
    </div>
    <div class="__content__" style="height: 100%">
      <el-table
        id="table"
        :stripe="true"
        :data="tableData"
        :span-method="objectSpanMethod"
        border
        style="width: 100%"
        height="calc(100% - 50px)"
      >
        <el-table-column
          prop="reach"
          label="河段"
          show-overflow-tooltip
          min-width="100"
          align="center"
        ></el-table-column>
        <el-table-column label="涵闸类型" width="120" align="center">
          <template slot-scope="scope">
            {{
              scope.row.gateType == "1"
                ? "邢家渡引黄闸"
                : scope.row.gateType == "2"
                ? "非农业"
                : "农业"
            }}
          </template>
        </el-table-column>
        <el-table-column
          prop="july"
          label="7月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="august"
          label="8月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="september"
          label="9月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="october"
          label="10月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="november"
          label="11月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="december"
          label="12月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="january"
          label="1月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="february"
          label="2月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="march"
          label="3月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="april"
          label="4月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="may"
          label="5月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="june"
          label="6月(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="total"
          label="合计(万m³)"
          width="100"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="sevenTen"
          label="7-10月(万m³)"
          width="120"
          align="right"
        ></el-table-column>
        <el-table-column
          prop="elevenSix"
          label="11-6月(万m³)"
          width="120"
          align="right"
        ></el-table-column>
        <el-table-column prop="bak" label="备注" min-width="100"></el-table-column>
      </el-table>
    </div>
    <yearDPDialog
      ref="addOrUpdate"
      v-if="yearDPDialog.show"
      v-bind="yearDPDialog"
      @close="yearDPDialog.close"
    ></yearDPDialog>
    <div class="print_content">
      <div id="print">
        <div class="title">{{ printJSON.formTitle }}</div>
        <div class="qz">分管领导签字（单位公章）</div>
        <div class="dw">单位：万立方米</div>
        <table class="theme-table-form">
          <colgroup>
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
            <col />
          </colgroup>
          <tr>
            <td>河段</td>
            <td>用水单位</td>
            <td>引黄水源名称</td>
            <td>7月</td>
            <td>8月</td>
            <td>9月</td>
            <td>10月</td>
            <td>11月</td>
            <td>12月</td>
            <td>1月</td>
            <td>2月</td>
            <td>3月</td>
            <td>4月</td>
            <td>5月</td>
            <td>6月</td>
            <td>合计</td>
            <td>7-10月</td>
            <td>11-6月</td>
          </tr>
          <tr v-for="(v, i) in printJSON.monthList" :key="i">
            <td rowspan="3" v-if="i == 0">{{ printJSON.reach }}</td>
            <td rowspan="3" v-if="i == 0">{{ printJSON.waterUnit }}</td>
            <td>{{ v.gateType }}</td>
            <td>{{ v.july }}</td>
            <td>{{ v.august }}</td>
            <td>{{ v.september }}</td>
            <td>{{ v.october }}</td>
            <td>{{ v.november }}</td>
            <td>{{ v.december }}</td>
            <td>{{ v.january }}</td>
            <td>{{ v.february }}</td>
            <td>{{ v.march }}</td>
            <td>{{ v.april }}</td>
            <td>{{ v.may }}</td>
            <td>{{ v.june }}</td>
            <td>{{ v.total }}</td>
            <td>{{ v.sevenTen }}</td>
            <td>{{ v.elevenSix }}</td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import SelectYear from "../../../__comom/SelectYear";
import { getYearDetail, yearAdd, yearUpdate } from "@/platform/api/waterDisP/drawUpDP";
import yearDPDialog from "./__yearDPDialog";
import print from "print-js";
export default {
  data() {
    return {
      // 分页器属性
      year: new Date().getFullYear(),
      tableData: [],
      yearDPDialog: {
        show: false,
        title: "",
        mode: "",
        model: null,
        chartsSetOption: {},
        close: () => {
          this.yearDPDialog.show = false;
        },
      },
      currData: {},
      printJSON: {},
    };
  },
  components: {
    SelectYear,
    yearDPDialog,
  },
  created() {
    this.$nextTick(() => {
      this.fetchData();
    });
  },
  computed: {
    monthList() {
      console.log(this.currData);
      return this.currData.monthList || [];
    },
    showAdd() {
      if (JSON.stringify(this.currData) == "{}") {
        return true;
      } else {
        return false;
      }
    },
    showEdit() {
      if (JSON.stringify(this.currData) == "{}") {
        return false;
      } else {
        return true;
      }
    },
  },
  methods: {
    fetchData() {
      this.tableData = [];
      this.currData = {};
      getYearDetail(this.year).then((res) => {
        console.log(res);
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.currData = res.data || {};
        this.initTable();
      });
    },
    //把数据 组合成多条可重复数据 合并单元格
    initTable() {
      let currData = this.currData;

      // let monthList = this.currData.monthList || [];
      for (let i = 0; i < this.monthList.length; i++) {
        const el = this.monthList[i];
        let obj = {};
        for (const k in currData) {
          if (currData.hasOwnProperty(k)) {
            const e = currData[k];
            if (k == "monthList") {
              for (const key in e[i]) {
                if (e[i].hasOwnProperty(key)) {
                  const element = e[i][key];
                  obj[key] = element;
                }
              }
            } else {
              obj[k] = e;
            }
          }
        }
        this.tableData.push(obj);
      }
      console.log(this.tableData);
    },
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      // let monthList = this.currData.monthList || [];
      const l = this.monthList.length;
      if (columnIndex === 0) {
        if (rowIndex % l === 0) {
          return {
            rowspan: l,
            colspan: 1,
          };
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          };
        }
      }
    },
    changeYear(val) {
      this.year = val;
      this.fetchData();
    },
    handleAdd(text) {
      getYearDetail(this.year).then((res) => {
        console.log(res);
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        let currData = res.data || {};
        this.yearDPDialog = {
          show: true,
          title: `新增${this.year}年度`,
          mode: "add",
          model: currData,
          close: this.handleAddForm,
        };
      });
    },
    handleAddForm(save, model, parms) {
      if (!save) {
        this.yearDPDialog.show = false;
        return;
      }
      yearAdd(model).then((res) => {
        this.$message.success("提交成功");
        this.yearDPDialog.show = false;
        this.fetchData();
      });
    },

    //点击保存表单时判断year是否存在决定调用add或者update
    handleFormSave(save, model, parms) {
      if (!save) {
        this.yearDPDialog.show = false;
        return;
      }
      model.year = this.year;
      console.log(model);
      if (model.yearId) {
        this.handleEditSave(save, model, parms);
      } else {
        this.handleAddForm(save, model, parms);
      }
    },
    //修改

    handleEdit(row, type) {

      getYearDetail(this.year).then((res) => {
        console.log(res);
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        if (!res.data) {
          return this.$message.warning("暂无年引水计划");
        }
        let currData = res.data || {};
        this.yearDPDialog = {
          show: true,
          title: `${this.year}年度`,
          mode: "update",
          model: currData,
          close: this.handleFormSave,
        };
      });
    },
    handleEditSave(save, model, parms) {
      if (!save) {
        this.yearDPDialog.show = false;
        return;
      }
      yearUpdate(model).then((res) => {
        this.$message.success("提交成功");
        this.yearDPDialog.show = false;
        this.fetchData(false);
      });
    },

    handlePrint() {
      getYearDetail(this.year)
        .then((res) => {
          this.printJSON = res.data || {};
        })
        .then(() => {
          this.$nextTick(() => {
            printJS({
              printable: "print",
              type: "html",
              targetStyles: ["*"],
              ignoreElements: ["no-print", "bc", "gb"],
            });
          });
        });
    },
  },
};
</script>

<style lang="scss" scoped>
.__header__ {
  display: flex;
}
.__content__ {
  padding: 10px 0;
}
.print_content {
  position: absolute;
  top: 99999;
  z-index: -9999;
  #print {
    width: 1500px;
    margin-top: 20px;
    .title {
      text-align: center;
      font-weight: bold;
      font-size: 38px;
      line-height: 50px;
    }
    .dw {
      display: inline-block;
      font-size: 18px;
      padding-left: 300px;
    }
    .qz {
      display: inline-block;
      font-size: 18px;
      text-align: left;
      line-height: 60px;
      margin-left: 60px;
    }
    table {
      font-size: 16px;
      td {
        height: 70px;
        border-color: #000000;
        text-align: center;
      }
      ._center {
        td {
          text-align: center;
        }
      }
      .content {
        height: 200px;
        position: relative;
        ._t {
          position: absolute;
          left: 10px;
          top: 10px;
        }
        ._c {
          text-indent: 2em;
          position: absolute;
          bottom: 10px;
          width: 100%;
          height: 140px;
        }
        ._z {
          position: absolute;
          right: 130px;
          bottom: 45px;
        }
        ._time {
          position: absolute;
          right: 120px;
          bottom: 10px;
        }
      }
    }
  }
}
</style>
