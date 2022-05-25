<template>
  <div class="">
    <div class="__header__">
      <SelectYear @change-year="changeYear"></SelectYear>
      <!-- <el-button
                type="primary"
                v-if="!showAdd && !showEdit"
                style="margin-left: 10px"
                @click="handleAdd"
                >新增</el-button
            >
            <el-button
                type="primary"
                v-if="!showEdit && showAdd"
                style="margin-left: 10px"
                @click="handleEdit"
                >编辑</el-button
            > -->
      <el-button
        type="primary"
        style="margin-left: 10px"
        @click="handleEdit"
        v-hasPermi="['month:make:add']"
        >生成引水计划</el-button
      >
      <el-button type="primary" @click="handlePrint">打印取水申请表</el-button>
    </div>
    <div class="__content__" style="height: 100%">
      <div class="__left__">
        <div
          v-for="i in showMonth"
          :key="i"
          @click="changeMonth(i)"
          :class="[
            '__item',
            'theme-border-color3',
            currentMonth == i
              ? 'theme-bg-dark-light theme-text-color-custom-dark'
              : 'theme-text-color-primary-light',
          ]"
        >
          {{ i }}月
        </div>
      </div>
      <div class="__right__">
        <el-table
          id="table"
          :stripe="true"
          :data="tableData"
          :span-method="objectSpanMethod"
          border
          style="width: 100%"
          height="calc(100% - 20px)"
        >
          <el-table-column
            prop="reach"
            label="河段"
            min-width="100"
            show-overflow-tooltip
            align="center"
          ></el-table-column>
          <el-table-column label="涵闸类型" width="120" align="center">
            <template slot-scope="scope">
              {{
                scope.row.gateType == "1"
                  ? "邢家渡引黄闸"
                  : scope.row.gateType == "2"
                  ? "其中生活"
                  : "农业"
              }}
            </template>
          </el-table-column>
          <el-table-column label="上旬" width="120" align="center">
            <el-table-column label="引水时段" width="160" align="center">
              <template slot-scope="scope"
                ><span>{{
                  `${scope.row.topTimeStart}至${scope.row.topTimeEnd}`
                }}</span></template
              ></el-table-column
            >
            <el-table-column
              prop="topFlow"
              label="流量(m³/s)"
              width="120"
              align="center"
            ></el-table-column>
            <el-table-column
              prop="topPilotage"
              label="引水量(万m³)"
              width="120"
              align="center"
            ></el-table-column>
          </el-table-column>
          <el-table-column label="中旬" width="120" align="center">
            <el-table-column label="引水时段" width="160" align="center">
              <template slot-scope="scope"
                ><span>{{
                  `${scope.row.midTimeStart}至${scope.row.midTimeEnd}`
                }}</span></template
              ></el-table-column
            >
            <el-table-column
              prop="midFlow"
              label="流量(m³/s)"
              width="120"
              align="center"
            ></el-table-column>
            <el-table-column
              prop="midPilotage"
              label="引水量(万m³)"
              width="120"
              align="center"
            ></el-table-column>
          </el-table-column>
          <el-table-column label="下旬" width="120" align="center">
            <el-table-column label="引水时段" width="160" align="center">
              <template slot-scope="scope"
                ><span>{{
                  `${scope.row.botTimeStart}至${scope.row.botTimeEnd}`
                }}</span></template
              ></el-table-column
            >
            <el-table-column
              prop="botFlow"
              label="流量(m³/s)"
              width="120"
              align="center"
            ></el-table-column>
            <el-table-column
              prop="botPilotage"
              label="引水量(万m³)"
              width="120"
              align="center"
            ></el-table-column>
          </el-table-column>
          <el-table-column
            prop="total"
            label="合计(万m³)"
            width="120"
            align="center"
          ></el-table-column>
          <el-table-column
            prop="bak"
            label="备注"
            width="120"
            align="center"
          ></el-table-column>
        </el-table>
      </div>
    </div>
    <monthDPDialog
      ref="addOrUpdate"
      v-if="monthDPDialog.show"
      v-bind="monthDPDialog"
      @close="monthDPDialog.close"
    ></monthDPDialog>

    <div class="print_content">
      <div id="print">
        <div class="title">年 月引水计划</div>
        <div class="qz">分管领导签字（单位公章）</div>
        <div class="dw">流量：立方米/秒 引水量：万立方米</div>
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
            <td rowspan="2">河段</td>
            <td colspan="2" rowspan="2">涵闸名称</td>
            <td colspan="4">上旬</td>
            <td colspan="4">中旬</td>
            <td colspan="4">下旬</td>
            <td rowspan="2">全月合计引水量</td>
            <td rowspan="2" colspan="2">备注</td>
          </tr>
          <tr>
            <td colspan="2">引水时段</td>
            <td>流量</td>
            <td>引水量</td>
            <td colspan="2">引水时段</td>
            <td>流量</td>
            <td>引水量</td>
            <td colspan="2">引水时段</td>
            <td>流量</td>
            <td>引水量</td>
          </tr>
          <tr v-for="(v, i) in printJSON.tendaysList" :key="i">
            <td rowspan="3" v-if="i == 0">{{ printJSON.reach }}</td>
            <td colspan="2">{{ v.gateType }}</td>
            <td colspan="2">{{ v.topTimeStart }}至{{ v.topTimeEnd }}</td>
            <td>{{ v.topFlow }}</td>
            <td>{{ v.topPilotage }}</td>
            <td colspan="2">{{ v.midTimeStart }}至{{ v.midTimeEnd }}</td>
            <td>{{ v.midFlow }}</td>
            <td>{{ v.midPilotage }}</td>
            <td colspan="2">{{ v.botTimeStart }}至{{ v.botTimeEnd }}</td>
            <td>{{ v.botFlow }}</td>
            <td>{{ v.botPilotage }}</td>
            <td>{{ v.total }}</td>
            <td colspan="2">{{ v.bak }}</td>
          </tr>
        </table>
        <div class="date">
          <span>年</span>
          <span>月</span>
          <span>日</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SelectYear from "../../../__comom/SelectYear";
import { getMonthDetail, monthAdd, monthUpdate } from "@/platform/api/waterDisP/drawUpDP";
import monthDPDialog from "./__monthDPDialog";
import print from "print-js";
export default {
  data() {
    return {
      // 分页器属性
      year: new Date().getFullYear(),
      tableData: [],
      monthDPDialog: {
        show: false,
        title: "",
        mode: "",
        model: null,
        chartsSetOption: {},
        close: () => {
          this.monthDPDialog.show = false;
        },
      },
      currData: {},
      currentMonth: new Date().getMonth() + 1,
      printJSON: {},
      showMonth:[7,8,9,10,11,12,1,2,3,4,5,6]
    };
  },
  components: {
    SelectYear,
    monthDPDialog,
  },
  created() {
    this.$nextTick(() => {
      this.fetchData();
    });
  },
  computed: {
    tendaysList() {
      return this.currData.tendaysList || [];
    },
    showAdd() {
      return this.currData.id;
    },
    showEdit() {
      return JSON.stringify(this.currData) == "{}";
    },
  },
  methods: {
    fetchData() {
      this.tableData = [];
      this.currData = {};
      getMonthDetail({ year: this.year, month: this.currentMonth }).then((res) => {
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
      // let tendaysList = this.currData.tendaysList || [];
      for (let i = 0; i < this.tendaysList.length; i++) {
        const el = this.tendaysList[i];
        let obj = {};
        for (const k in currData) {
          if (currData.hasOwnProperty(k)) {
            const e = currData[k];
            if (k == "tendaysList") {
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
      // let tendaysList = this.currData.tendaysList || [];
      const l = this.tendaysList.length;
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
    changeMonth(v) {
      this.currentMonth = v;
      this.fetchData();
    },
    handleAdd(text) {
      getMonthDetail({ year: this.year, month: this.currentMonth }).then((res) => {
        console.log(res);
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        let currData = res.data || {};
        currData.year = this.year;
        currData.month = this.currentMonth;
        this.monthDPDialog = {
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
        this.monthDPDialog.show = false;
        return;
      }
      monthAdd(model).then((res) => {
        this.monthDPDialog.show = false;
        this.fetchData();
      });
    },
    //点击保存表单时判断month是否存在决定调用add或者update
    handleFormSave(save, model, parms) {
      if (!save) {
        this.monthDPDialog.show = false;
        return;
      }
      //   model.year = this.year;
      console.log(model);
      if (model.monthId) {
        this.handleEditSave(save, model, parms);
      } else {
        this.handleAddForm(save, model, parms);
      }
    },
    //修改
    handleEdit(row, type) {
      getMonthDetail({ year: this.year, month: this.currentMonth }).then((res) => {
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        if (!res.data) {
          return this.$message.warning("暂无月引水计划");
        }
        console.log(res);
        let currData = res.data || {};
        currData.year = this.year;
        currData.month = this.currentMonth;
        this.monthDPDialog = {
          show: true,
          title: `编辑${this.year}年度`,
          mode: "update",
          model: currData,
          close: this.handleFormSave,
        };
      });
    },
    handleEditSave(save, model, parms) {
      if (!save) {
        this.monthDPDialog.show = false;
        return;
      }
      monthUpdate(model).then((res) => {
        this.monthDPDialog.show = false;
        this.fetchData(false);
      });
    },
    handlePrint() {
      getMonthDetail({ year: this.year, month: this.currentMonth })
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
              style: "@media print{@page {size:landscape}}",
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
  display: flex;
  padding: 10px 0 0 0;
  width: 100%;
  height: 100%;
  .__left__ {
    width: 140px;
    height: 445px;
    overflow: scroll;

    .__item {
      margin: 5px;
      border-width: 1px;
      border-style: solid;
      cursor: pointer;
      height: 31px;
      line-height: 31px;
      text-align: center;
      font-weight: bold;
      font-size: 16px;
    }
  }
  .__right__ {
    width: calc(100% - 140px);
    position: relative;
  }
}
.print_content {
  position: absolute;
  top: 99999;
  z-index: -9999;
  #print {
    width: 1000px;
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
      padding-left: 500px;
    }
    .qz {
      display: inline-block;
      font-size: 18px;
      text-align: left;
      line-height: 60px;
      margin-left: 60px;
    }
    .date {
      margin: 10px 0 0 900px;
      span {
        margin-right: 20px;
      }
    }
    table {
      font-size: 16px;
      td {
        height: 70px;
        text-align: center;
        border-color: #000000;
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
