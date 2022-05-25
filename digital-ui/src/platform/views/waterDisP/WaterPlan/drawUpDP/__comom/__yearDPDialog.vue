<template>
  <AutoDialog
    :show="show"
    class="yearDPDialog"
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
            <span>{{ form.applyDate }}</span>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>各月段需水量(万m³)
          </td>
          <td
            class="theme-table-form__input"
            colspan="3"
            style="overflow: auto"
          >
            <el-form-item prop="monthList">
              <el-table :data="form.monthList" height="180px" border>
                <el-table-column
                  v-for="(v, i) in columns"
                  :key="i"
                  :prop="v.field"
                  :label="v.title"
                  :width="
                    v.field == 'gateTypeName' || v.field == 'gateType'
                      ? '130px'
                      : '130px'
                  "
                  align="center"
                >
                  <template slot-scope="scope">
                    <el-form-item
                      :prop="`monthList[${scope.$index}].${v.field}`"
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
                      :prop="`monthList[${scope.$index}].${v.field}`"
                      :rules="{
                        required: true,
                        message: '不能为空',
                        trigger: ['blur', 'change']
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
                    <span v-else-if="v.type == 'readyonly'">
                      {{ scope.row[v.field] }}
                    </span>
                    <span v-else-if="v.type == 'type'">
                      {{
                        scope.row.gateType == "1"
                          ? "邢家渡引黄闸"
                          : scope.row.gateType == "2"
                          ? "非农业"
                          : "农业"
                      }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">批复文件</td>
          <td class="theme-table-form__input">
            <el-form-item label="参数键值" prop="applicationFile">
              <DataUpload
                v-model="form.applicationFile"
                :limit="1"
              ></DataUpload>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">年计划批复水量</td>
          <td class="theme-table-form__input">
            <el-form-item label="年计划批复水量" prop="replyWaterYear">
              <el-input
                style="width:80%"
                v-model="form.replyWaterYear"
                size="mini"
                :placeholder="`请输入年计划批复水量`"
                type="number"
                min="0"
              ></el-input
              >万m³
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">表格标题</td>
          <td class="theme-table-form__input" colspan="3">
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
  }
};
export default {
  props: {
    show: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: ""
    },
    mode: {
      type: String,
      default: "add"
    },
    model: {
      type: Object
    }
  },

  data() {
    return {
      fullscreen: false,
      protitle: this.title,
      pickerOptions: {},
      tendaysDTO: {},
      adjustList: [],
      defTime: [],
      form: {
        applyDate: "",
        deptCode: "",
        file: "",
        monthList: [
          {
            april: 0,
            august: 0,
            bak: "",
            december: 0,
            elevenSix: 0,
            february: 0,
            gateType: "",
            january: 0,
            july: 0,
            june: 0,
            march: 0,
            may: 0,
            november: 0,
            october: 0,
            september: 0,
            sevenTen: 0,
            total: 0
          }
        ],
        replyWater: 0,
        yearId: "",
        replyWaterYear: "",
        formTitle: ""
      },
      columns: [
        // {
        //     field: "gateTypeName",
        //     title: "涵闸名称",
        //     type: "text",
        // },
        {
          field: "gateType",
          title: "涵闸类型",
          type: "type"
        },
        {
          field: "july",
          title: "7月",
          type: "number"
        },
        {
          field: "august",
          title: "8月",
          type: "number"
        },
        {
          field: "september",
          title: "9月",
          type: "number"
        },
        {
          field: "october",
          title: "10月",
          type: "number"
        },
        {
          field: "november",
          title: "11月",
          type: "number"
        },
        {
          field: "december",
          title: "12月",
          type: "number"
        },
        {
          field: "january",
          title: "1月",
          type: "number"
        },
        {
          field: "february",
          title: "2月",
          type: "number"
        },
        {
          field: "march",
          title: "3月",
          type: "number"
        },
        {
          field: "april",
          title: "4月",
          type: "number"
        },
        {
          field: "may",
          title: "5月",
          type: "number"
        },
        {
          field: "june",
          title: "6月",
          type: "number"
        },
        {
          field: "total",
          title: "合计",
          type: "readyonly"
        },
        {
          field: "sevenTen",
          title: "7-10月",
          type: "readyonly"
        },
        {
          field: "elevenSix",
          title: "11-6月",
          type: "readyonly"
        }
      ],
      rules: {
        formerWater: ValidateUtil.requiredPercent(0, 999999),
        needWater: ValidateUtil.requiredPercent(0, 999999),
        abc: ValidateUtil.requiredPercent(0, 9999999),
        replyWaterYear: ValidateUtil.requiredPercent(0, 9999999),
        formTitle: ValidateUtil.required()
        // applicationFile: ValidateUtil.required(),
      },
      typeName: ["", "邢家渡引黄闸", "其中生活", "农业"],
      l: 86400
    };
  },
  created() {
    this.$nextTick(() => {
      if (this.model) {
        if (this.model) {
          this.form = this.model;
        }
      }
    });
  },
  methods: {
    //输入流量 计算水量
    changeInput(row) {
      // 计算 7-10月  january february march april may june july august september october november december
      row.sevenTen = (
        parseFloat(row.july) +
        parseFloat(row.august) +
        parseFloat(row.september) +
        parseFloat(row.october)
      ).toFixed(2);
      // 计算 11-6月
      row.elevenSix = (
        parseFloat(row.november) +
        parseFloat(row.december) +
        parseFloat(row.january) +
        parseFloat(row.february) +
        parseFloat(row.march) +
        parseFloat(row.april) +
        parseFloat(row.may) +
        parseFloat(row.june)
      ).toFixed(2);
      // 总和
      row.total = (
        parseFloat(row.sevenTen) + parseFloat(row.elevenSix)
      ).toFixed(2);
    },
    onClickClose() {
      this.$emit("close", false);
    },

    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.$emit("close", true, this.form);
        } else {
          return false;
        }
      });
    },
    resetModel(model = {}) {
      this.form = this.$util.coverValue(this.form, model, true);
    }
  }
};
</script>
<style lang="scss">
.yearDPDialog {
  .el-table .cell {
    overflow: inherit;
  }
}
</style>
