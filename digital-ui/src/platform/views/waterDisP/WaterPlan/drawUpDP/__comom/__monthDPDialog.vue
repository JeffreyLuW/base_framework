<template>
  <AutoDialog
    :show="show"
    class="monthDPDialog"
    :title="protitle"
    sizeMode="auto"
    width="1024px"
    :fullscreen.sync="fullscreen"
    :closeOnClickModal="false"
    @close="onClickClose"
  >
    <el-form :model="form" ref="form" :rules="rules">
      <table class="theme-table-form">
        <colgroup>
          <col width="160" />
          <col />
          <col width="160" />
          <col />
        </colgroup>
        <tr>
          <td class="theme-table-form__label">河段</td>
          <td class="theme-table-form__input">
            <span>{{ form.reach }}</span>
          </td>
          <td class="theme-table-form__label">填报年月</td>
          <td class="theme-table-form__input">
            <span>{{ form.year + "-" + form.month }}</span>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>各旬段需水量
          </td>
          <td class="theme-table-form__input" colspan="3" style="overflow: auto">
            <el-form-item prop="tendaysList">
              <el-table :data="form.tendaysList" height="180px" border>
                <el-table-column
                  v-for="(v, i) in columns"
                  :key="i"
                  :prop="v.field"
                  :label="v.title"
                  :width="
                    v.type == 'date1' || v.type == 'date2' || v.type == 'date3'
                      ? '260px'
                      : '130px'
                  "
                  align="center"
                >
                  <template slot-scope="scope">
                    <el-form-item
                      :prop="`tendaysList[${scope.$index}].${v.field}`"
                      :rules="rules.abc"
                      v-if="v.type == 'number'"
                    >
                      <el-input
                        size="mini"
                        :placeholder="`请输入${v.title}`"
                        type="number"
                        min="0"
                        v-model="scope.row[v.field]"
                        @input="changeInput(scope.row)"
                      ></el-input>
                    </el-form-item>
                    <el-form-item
                      :prop="`tendaysList[${scope.$index}].${v.field}`"
                      :rules="{
                        required: true,
                        message: '不能为空',
                        trigger: ['blur', 'change'],
                      }"
                      v-else-if="v.type == 'text'"
                    >
                      <el-input
                        size="mini"
                        :placeholder="`请输入${v.title}`"
                        type="text"
                        v-model="scope.row[v.field]"
                      ></el-input>
                    </el-form-item>
                    <el-form-item
                      :prop="`tendaysList[${scope.$index}].${v.field}`"
                      v-else-if="v.type == 'notRequireText'"
                    >
                      <el-input
                        size="mini"
                        :placeholder="`请输入${v.title}`"
                        type="text"
                        v-model="scope.row[v.field]"
                      ></el-input>
                    </el-form-item>

                    <el-form-item
                      :prop="`tendaysList[${scope.$index}].${v.field}`"
                      v-else-if="
                        v.type == 'date1' || v.type == 'date2' || v.type == 'date3'
                      "
                      :rules="{
                        required: true,
                        message: '不能为空',
                        trigger: ['blur', 'change'],
                      }"
                    >
                      <el-date-picker
                        v-model="scope.row[v.field]"
                        type="daterange"
                        style="width: 230px"
                        range-separator="至"
                        :clearable="false"
                        value-format="yyyy-MM-dd"
                        :default-value="
                          v.type == 'date1'
                            ? defTime1
                            : v.type == 'date2'
                            ? defTime2
                            : defTime3
                        "
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        :picker-options="
                          v.type == 'date1'
                            ? pickerOptions1
                            : v.type == 'date2'
                            ? pickerOptions2
                            : pickerOptions3
                        "
                        @change="changePickDay(scope.row)"
                      >
                      </el-date-picker>
                    </el-form-item>
                    <span v-else-if="v.type == 'readyonly'">
                      {{ scope.row[v.field] }}
                    </span>
                    <span v-else-if="v.type == 'type'">
                      {{ typeName[scope.row[v.field]] }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">申请文件</td>
          <td class="theme-table-form__input" >
            <el-form-item label="参数键值" prop="applicationFile">
              <DataUpload v-model="form.applicationFile" :limit="1"></DataUpload>
            </el-form-item>
          </td>
               <td class="theme-table-form__label">表格标题</td>
          <td class="theme-table-form__input">
            <el-form-item label="表格标题" prop="formTitle">
              <el-input
              v-model="form.formTitle"
                size="mini"
                :placeholder="`请输入表格标题`"
              ></el-input>
            </el-form-item>
          </td>
        </tr>
      </table>
    </el-form>
    <template slot="footer">
      <el-button type="primary" @click="submitForm('form')">保存</el-button>
      <el-button @click="onClickClose">取消</el-button>
    </template>
  </AutoDialog>
</template>
<script>
import ValidateUtil from "@/platform/utils/ValidateUtil.js";
import { queryYearPlanDictList } from "@/platform/api/waterDisP/annualDP";
import { queryMonthPlanDictList } from "@/platform/api/waterDisP/moonDP";
//guid 生成工具 这个不用看 示例而已 模拟后台返回的guid
let generateGuId = {
  _count: 1,
  get() {
    return +new Date() + "_" + this._count++;
  },
};
export default {
  props: {
    show: {
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
      default: "",
    },
    mode: {
      type: String,
      default: "add",
    },
    model: {
      type: Object,
    },
  },
  data() {
    return {
      fullscreen: false,
      protitle: this.title,
      pickerOptions1: {},
      pickerOptions2: {},
      pickerOptions3: {},
      form: {
        applyDate: "",
        deptCode: "",
        file: "",
        id: "",
        reach: "",
        replyWater: 0,
        tendaysList: [],
        yearId: "",
      },
      columns: [
        {
          field: "gateType",
          title: "涵闸类型",
          type: "type",
        },
        {
          field: "time1",
          title: "上旬引水时段",
          type: "date1",
        },
        {
          field: "topPilotage",
          title: "上旬引水量(万m³)",
          type: "number",
        },
        {
          field: "topFlow",
          title: "上旬流量(m³/s)",
          type: "number",
        },
        {
          field: "time2",
          title: "中旬引水时段",
          type: "date2",
        },
        {
          field: "midPilotage",
          title: "中旬引水量(万m³)",
          type: "number",
        },
        {
          field: "midFlow",
          title: "中旬流量(m³/s)",
          type: "number",
        },
        {
          field: "time3",
          title: "下旬引水时段",
          type: "date3",
        },
        {
          field: "botPilotage",
          title: "下旬引水量(万m³)",
          type: "number",
        },
        {
          field: "botFlow",
          title: "下旬流量(m³/s)",
          type: "number",
        },
        {
          field: "total",
          title: "合计(万m³)",
          type: "readyonly",
        },
        {
          field: "bak",
          title: "备注",
          type: "notRequireText",
        },
      ],
      rules: {
        formerWater: ValidateUtil.requiredPercent(0, 9999),
        needWater: ValidateUtil.requiredPercent(0, 9999),
        abc: ValidateUtil.requiredPercent(0, 9999),
        tenDaysList: ValidateUtil.requiredArray(),
        // applicationFile: ValidateUtil.required(),
      },
      typeName: ["", "邢家渡引黄闸", "其中生活", "农业"],
      l: 86400,
    };
  },
  computed: {
    defTime1() {
      let year = "",
        str = [new Date(), new Date()];
      if (this.form.month && this.form.year) {
        if (this.form.month < 7) {
          year = parseInt(this.form.year) + 1;
        } else {
          year = this.form.year;
        }
        let arr1 = ["", 1, 11, 21];
        let arr2 = ["", 10, 20, new Date(year, this.form.month - 1, 0).getDate()];

        let d1 = arr1[1];
        let d2 = arr2[1];
        str = [
          new Date(year, this.form.month - 1, d1),
          new Date(year, this.form.month - 1, d2),
        ];
      }
      return str;
    },
    defTime2() {
      let year = "",
        str = [new Date(), new Date()];
      if (this.form.month && this.form.year) {
        if (this.form.month < 7) {
          year = parseInt(this.form.year) + 1;
        } else {
          year = this.form.year;
        }
        let arr1 = ["", 1, 11, 21];
        let arr2 = ["", 10, 20, new Date(year, this.form.month - 1, 0).getDate()];
        let d1 = arr1[2];
        let d2 = arr2[2];
        str = [
          new Date(year, this.form.month - 1, d1),
          new Date(year, this.form.month - 1, d2),
        ];
      }
      return str;
    },
    defTime3() {
      let year = "",
        str = [new Date(), new Date()];
      if (this.form.month && this.form.year) {
        if (this.form.month < 7) {
          year = parseInt(this.form.year) + 1;
        } else {
          year = this.form.year;
        }
        let arr1 = ["", 1, 11, 21];
        let arr2 = ["", 10, 20, new Date(year, this.form.month - 1, 0).getDate()];
        let d1 = arr1[3];
        let d2 = arr2[3];
        str = [
          new Date(year, this.form.month - 1, d1),
          new Date(year, this.form.month - 1, d2),
        ];
      }
      return str;
    },
  },
  created() {
    this.$nextTick(() => {
      if (this.model) {
        // 新增的话 默认添加进入一条
        let obj = {
          bak: "",
          botFlow: 0,
          botPilotage: 0,
          botTimeEnd: "",
          botTimeStart: "",
          gateType: "1",
          midFlow: 0,
          midPilotage: 0,
          midTimeEnd: "",
          midTimeStart: "",
          monthId: "",
          topFlow: 0,
          topPilotage: 0,
          topTimeEnd: "",
          topTimeStart: "",
          total: 0,
        };
        if (this.mode == "add") {
          this.model.tendaysList.unshift(obj);
        }
        this.form = this.model;
        this.initShow();
      }
    });
  },
  methods: {
    // 编辑回显 显示
    initShow() {
      let t = this.form.tendaysList;
      let arr = [];
      if (t) {
        t.forEach((n) => {
          this.$set(n, "time1", [n.topTimeStart, n.topTimeEnd]);
          this.$set(n, "time2", [n.topTimeStart, n.topTimeEnd]);
          this.$set(n, "time3", [n.topTimeStart, n.topTimeEnd]);
          arr.push(n);
        });
      }

      this.form.tendaysList = arr;
      this.initPickerOpt();
    },
    initPickerOpt() {
      let y = this.form.year;
      let m = this.form.month;

      if (m < 7) {
        y = parseInt(y) + 1;
      }
      this.pickerOptions1 = {
        disabledDate(time) {
          let cur = new Date(y, m - 1, 1),
            up = new Date(y, m - 1, 10);
          return time.getTime() < cur.getTime() || time.getTime() > up.getTime(); //大于当前的禁止，小于7天前的禁止
        },
      };

      this.pickerOptions2 = {
        disabledDate(time) {
          let cur = new Date(y, m - 1, 11),
            up = new Date(y, m - 1, 20);
          return time.getTime() < cur.getTime() || time.getTime() > up.getTime(); //大于当前的禁止，小于7天前的禁止
        },
      };

      this.pickerOptions3 = {
        disabledDate(time) {
          let cur = new Date(y, m - 1, 21),
            up = new Date(y, m - 1, new Date(y, m - 1, 0).getDate());
          return time.getTime() < cur.getTime() || time.getTime() > up.getTime(); //大于当前的禁止，小于7天前的禁止
        },
      };
    },
    changePickDay(row) {
      console.log(row);
      if (row.time1) {
        row.topTimeStart = row.time1[0];
        row.topTimeEnd = row.time1[1];
      }
      if (row.time2) {
        row.midTimeStart = row.time2[0];
        row.midTimeEnd = row.time2[1];
      }
      if (row.time3) {
        row.botTimeStart = row.time3[0];
        row.botTimeEnd = row.time3[1];
      }
    },
    //输入流量 计算水量
    changeInput(row) {
      // 计算 上中下 总和
      row.total = (
        parseFloat(row.topFlow) +
        parseFloat(row.topPilotage) +
        parseFloat(row.midFlow) +
        parseFloat(row.midPilotage) +
        parseFloat(row.botFlow) +
        parseFloat(row.botPilotage)
      ).toFixed(2);
    },
    onClickClose() {
      this.$emit("close", false);
    },

    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$emit("close", true, this.form);
        } else {
          return false;
        }
      });
    },
    resetModel(model = {}) {
      this.form = this.$util.coverValue(this.form, model, true);
    },
  },
};
</script>
<style lang="scss">
.monthDPDialog {
  .el-table .cell {
    overflow: inherit;
  }
}
</style>
