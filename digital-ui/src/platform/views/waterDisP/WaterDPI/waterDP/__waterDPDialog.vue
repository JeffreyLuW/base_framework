<template>
  <AutoDialog
    :show="show"
    class="waterDisPlanDialog"
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
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>用水单位
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="用水单位" prop="waterCompany">
              <el-select
                v-model="form.waterCompany"
                placeholder="请选择用水单位"
                style="width: 220px"
                @change="changeWaterCompany"
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
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            供水年度
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="供水年度" prop="yearId">
              <el-select
                v-model="waterYear"
                placeholder="请选择供水年度"
                style="width: 220px"
                :value-key="`idValue`"
                :disabled="!form.waterCompany"
                @change="changeYear"
              >
                <el-option
                  v-for="item in yearOption"
                  :key="item.idValue"
                  :label="item.yearKey"
                  :value="item"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            月份
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="月份" prop="monthId">
              <el-select
                v-model="waterMonth"
                placeholder="请选择月份"
                style="width: 220px"
                :disabled="!form.waterCompany"
                :value-key="`idValue`"
                @change="changeMonth"
              >
                <el-option
                  v-for="item in monthOption"
                  :key="item.idValue"
                  :label="item.yearKey"
                  :value="item"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            旬
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="旬" prop="tenDays">
              <el-select
                v-model="tenDays"
                placeholder="请选择旬"
                style="width: 220px"
                :value-key="`dictValue`"
                @change="changeTenDays"
              >
                <el-option
                  v-for="item in tenDaysOption"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>指标水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="指标水量" prop="indexWater">
              <el-input
                type="text"
                placeholder="请输入指标水量"
                v-model="form.indexWater"
              >
                <template slot="append">万(m³)</template>
              </el-input>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">状态</td>
          <td class="theme-table-form__input">
            <span>{{ status[form.status] || "草稿" }}</span>
            <!-- <el-form-item label="状态" prop="status">
                            <el-select
                                v-model="form.status"
                                placeholder="请选择状态"
                                style="width: 220px"
                            >
                                <el-option
                                    v-for="item in statusOption"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value"
                                >
                                </el-option>
                            </el-select>
                        </el-form-item> -->
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>灌溉面积
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="灌溉面积" prop="irrigationArea">
              <el-input
                type="text"
                placeholder="请输入灌溉面积"
                v-model="form.irrigationArea"
              >
                <template slot="append">万(亩)</template>
              </el-input>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>需水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="需水量" prop="needWater">
              <el-input
                type="text"
                placeholder="请输入需水量"
                v-model="form.needWater"
              >
                <template slot="append">万(m³)</template>
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>收益乡镇
          </td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="收益乡镇" prop="profitTown">
              <el-input
                type="text"
                placeholder="请输入收益乡镇"
                v-model="form.profitTown"
              >
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>用途
          </td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="用途" prop="purpose">
              <el-input
                type="text"
                placeholder="请输入用途"
                v-model="form.purpose"
              >
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr v-if="mode !== 'add'">
          <td class="theme-table-form__label">
            驳回原因
          </td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="驳回原因" prop="rejection">
              <el-input
                type="text"
                placeholder=""
                v-model="form.rejection"
                :disabled="mode !== 'audit'"
              >
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr></tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>旬计划列表
          </td>
          <td
            class="theme-table-form__input"
            colspan="3"
            style="overflow: auto"
          >
            <el-form-item prop="daysList">
              <el-button
                @click="addtableColumn()"
                v-if="mode != 'view' && _show_ && mode != 'audit'"
                >添加</el-button
              >
              <el-table
                :data="form.daysList"
                height="100%"
                border
                style="min-height: 200px"
              >
                <el-table-column
                  label="操作"
                  width="60"
                  v-if="mode != 'view' && mode != 'audit'"
                >
                  <template slot-scope="scope">
                    <span
                      class="el-tag el-tag--danger el-tag--mini"
                      style="cursor: pointer"
                      @click="delChange(scope.row, scope.$index)"
                      >删除</span
                    >
                  </template>
                </el-table-column>
                <el-table-column
                  v-for="(v, i) in columns"
                  :key="i"
                  :prop="v.field"
                  :label="v.title"
                  :width="v.type == 'month' ? '255px' : '130px'"
                  align="center"
                >
                  <template slot-scope="scope">
                    <el-form-item
                      :prop="`daysList[${scope.$index}].${v.field}`"
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
                      :prop="`daysList[${scope.$index}].${v.field}`"
                      v-else-if="v.type == 'month'"
                      :rules="{
                        required: true,
                        message: '不能为空',
                        trigger: ['blur', 'change']
                      }"
                    >
                      <el-date-picker
                        v-model="scope.row[v.field]"
                        type="daterange"
                        style="width: 230px"
                        range-separator="至"
                        :clearable="false"
                        value-format="yyyy-MM-dd"
                        :default-value="defTime"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        :picker-options="pickerOptions"
                        @change="changePickDay(scope.row)"
                      >
                      </el-date-picker>
                    </el-form-item>
                    <span v-else-if="v.type == 'readyonly'">
                      {{ scope.row[v.field] }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">申请文件</td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="参数键值" prop="applicationFile">
              <Upload
                v-model="fileList"
                :disabled="!(mode == 'audit')"
                :limit="1"
              ></Upload>
            </el-form-item>
          </td>
        </tr>
      </table>
    </el-form>
    <template slot="footer">
      <el-button
        type="primary"
        v-if="mode != 'audit'"
        @click="submitForm('form', 0)"
        >保存</el-button
      >
      <el-button
        type="primary"
        v-if="mode != 'audit'"
        @click="submitForm('form', 1)"
        >提交</el-button
      ><el-button
        type="primary"
        v-if="mode == 'audit' && model.status == '1'"
        @click="aduitForm('2')"
        >通过</el-button
      ><el-button
        type="primary"
        v-if="mode == 'audit' && model.status == '1'"
        @click="aduitForm('3')"
        >驳回</el-button
      >
      <el-button @click="onClickClose">取消</el-button>
    </template>
  </AutoDialog>
</template>
<script>
import ValidateUtil from "@/platform/utils/ValidateUtil.js";
import { queryYearPlanDictList } from "@/platform/api/waterDisP/annualDP";
import { queryCompentDicts } from "@/platform/api/waterR/waterInfo";
import { queryMonthPlanDictList } from "@/platform/api/waterDisP/moonDP";
import {
  uploadAddFile,
  getGroupFile,
  uploadEditFile
} from "@/platform/api/common/common.js";
import Upload from "@/platform/views/waterDisP/__comom/upload";
//guid 生成工具 这个不用看 示例而已 模拟后台返回的guid
let generateGuId = {
  _count: 1,
  get() {
    return +new Date() + "_" + this._count++;
  }
};
export default {
  components: {
    Upload
  },
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
  computed: {
    __showYearMonth__() {
      let year = "",
        str = "";
      if (this.form.waterMonth && this.form.year) {
        if (this.form.waterMonth < 7) {
          year = parseInt(this.form.year) + 1;
        } else {
          year = this.form.year;
        }
        str = `${year}年度${this.form.waterMonth}月份`;
      }
      return str;
    },
    // 当不选择 年度 月度 不允许填写数据
    _show_() {
      let t = false;
      if (this.form.tenDays && this.form.year && this.form.month) {
        t = true;
        this.dynamicDateOpt(this.form.year, this.form.month, this.form.tenDays);
      }
      return t;
    },
    // 旬默认时间
    defTime() {
      let year = "",
        str = [new Date(), new Date()];
      if (this.form.month && this.form.year && this.form.tenDays) {
        if (this.form.month < 7) {
          year = parseInt(this.form.year) + 1;
        } else {
          year = this.form.year;
        }
        let arr1 = ["", 1, 11, 21];
        let arr2 = [
          "",
          10,
          20,
          new Date(year, this.form.month - 1, 0).getDate()
        ];
        let type = this.form.tenDays;
        let d1 = arr1[type];
        let d2 = arr2[type];
        str = [
          new Date(year, this.form.month - 1, d1),
          new Date(year, this.form.month - 1, d2)
        ];
      }
      return str;
    }
  },
  data() {
    return {
      fullscreen: false,
      protitle: this.title,
      fileList: [],
      visible: false,
      waterYear: {
        idValue: "",
        yearKey: ""
      },
      waterMonth: {
        idValue: "",
        yearKey: ""
      },
      tenDays: {
        dictValue: "",
        dictLabel: ""
      },
      pickerOptions: {},

      form: {
        applicationFile: "",
        daysList: [],
        indexWater: 0,
        irrigationArea: 0,
        month: "",
        monthId: "",
        needWater: 0,
        profitTown: "",
        purpose: "",
        status: "",
        tenDays: "",
        tenDaysName: "",
        waterCompany: "",
        year: "",
        yearId: ""
      },
      columns: [
        { field: "time1", title: "农业开始结束日期", type: "month" },
        {
          field: "agricultureDays",
          title: "农业天数",
          type: "readyonly"
        },
        {
          field: "agricultureFlow",
          title: "农业流量(m³/s)",
          type: "number"
        },
        {
          field: "agricultureWater",
          title: "农业水量(万m³)",
          type: "readyonly"
        },
        { field: "time2", title: "非农业开始结束日期", type: "month" },
        {
          field: "noAgricultureDays",
          title: "非农业天数",
          type: "readyonly"
        },
        {
          field: "noAgricultureFlow",
          title: "非农业流量(m³/s)",
          type: "number"
        },
        {
          field: "noAgricultureWater",
          title: "非农业水量(万m³)",
          type: "readyonly"
        }
      ],
      rules: {
        waterCompany: ValidateUtil.required(),
        waterYear: ValidateUtil.required(),
        status: ValidateUtil.required(),
        purpose: ValidateUtil.requiredLength(0, 50),
        profitTown: ValidateUtil.requiredLength(0, 50),
        needWater: ValidateUtil.requiredPercent(0, 999999),
        indexWater: ValidateUtil.requiredPercent(0, 999999),
        irrigationArea: ValidateUtil.requiredPercent(0, 999999),
        abc: ValidateUtil.requiredPercent(0, 999999),
        yearId: ValidateUtil.required(),
        tenDays: ValidateUtil.required(),
        monthId: ValidateUtil.required(),
        daysList: ValidateUtil.requiredArray()
      },
      status: ["草稿", "待审批", "已审批", "已驳回"],
      statusOption: [
        {
          value: "0",
          label: "草稿"
        },
        {
          value: "1",
          label: "待审批"
        },
        {
          value: "2",
          label: "已审批"
        },
        {
          value: "3",
          label: "已驳回"
        }
      ],
      tenDaysOption: [],
      curMonthOptions: [],
      yearOption: [],
      useWaterOptions: [],
      monthOption: [],
      l: 86400
    };
  },
  created() {
    this.$nextTick(() => {
      this.curMonthOptions = this.monthOptions;
      this.getDicts("ten_days")
        .then(res => {
          this.tenDaysOption = res.data;
          if (this.model) {
            this.tenDays.dictValue = this.model.tenDays;
          }
        })
        .then(() => {
          queryCompentDicts()
            .then(res => {
              if (res.code !== 200) {
                return this.$message.error(res.msg);
              }
              this.useWaterOptions = res.data;
            })
            .then(() => {
              queryYearPlanDictList({
                companyId: this.model.waterCompanyId
              }).then(res => {
                if (res.code !== 200) {
                  return this.$message.error(res.msg);
                }
                this.yearOption = res.data;
                if (this.model) {
                  this.form = JSON.parse(JSON.stringify(this.model));
                  //    this.form =  this.model;

                  this.waterYear.idValue = this.model.yearId;
                  queryMonthPlanDictList({
                    waterCompanyId: this.model.waterCompanyId,
                    yearId: this.model.yearId
                  }).then(res => {
                    if (res.code !== 200) {
                      return this.$message.error(res.msg);
                    }
                    this.monthOption = res.data;
                    this.waterMonth.idValue = this.model.monthId;

                    // this.dynamicDateOpt(
                    //     this.form.year,
                    //     this.form.month,
                    //     this.form.tenDays
                    // );
                    this.initShow();
                    if (this.form.applicationFile) {
                      getGroupFile(this.form.applicationFile).then(res => {
                        console.log(res);
                        this.fileList = res.data;
                      });
                    }
                  });
                }
              });
            });
        });
    });
  },
  methods: {
    // 编辑回显 显示
    initShow() {
      console.log(this.form.daysList);
      let t = this.form.daysList;
      let arr = [];
      t.forEach(n => {
        this.$set(n, "time1", [n.agricultureStart, n.agricultureEnd]);
        this.$set(n, "time2", [n.noAgricultureStart, n.noAgricultureEnd]);
        arr.push(n);
      });
      console.log(arr);
      this.form.daysList = arr;
    },
    //新增水量
    addtableColumn() {
      let j = {
        agricultureDays: 0,
        agricultureEnd: "",
        agricultureFlow: 0,
        agricultureStart: "",
        agricultureWater: 0,
        id: "",
        noAgricultureDays: 0,
        noAgricultureEnd: "",
        noAgricultureFlow: 0,
        noAgricultureStart: "",
        noAgricultureWater: 0,
        tenDaysId: this.form.tenDays
      };
      this.form.daysList.push(j);
    },
    // 删除操作
    delChange(row, index) {
      this.form.daysList.splice(index, 1);
    },

    // 计算选着时间天数
    changePickDay(row) {
      if (row.time1) {
        row.agricultureDays =
          (new Date(row.time1[1]).getTime() -
            new Date(row.time1[0]).getTime()) /
            (1 * 24 * 60 * 60 * 1000) +
          1;
        row.agricultureStart = row.time1[0];
        row.agricultureEnd = row.time1[1];
        row.agricultureWater = (
          (row.agricultureDays * row.agricultureFlow * this.l) /
          10000
        ).toFixed(2);
      }
      if (row.time2) {
        row.noAgricultureDays =
          (new Date(row.time2[1]).getTime() -
            new Date(row.time2[0]).getTime()) /
            (1 * 24 * 60 * 60 * 1000) +
          1;
        row.noAgricultureStart = row.time2[0];
        row.noAgricultureEnd = row.time2[1];
        row.noAgricultureWater = (
          (row.noAgricultureDays * row.noAgricultureFlow * this.l) /
          10000
        ).toFixed(2);
      }
    },
    //输入流量 计算水量
    changeInput(row) {
      row.agricultureWater = (
        (row.agricultureDays * row.agricultureFlow * this.l) /
        10000
      ).toFixed(2);
      row.noAgricultureWater = (
        (row.noAgricultureDays * row.noAgricultureFlow * this.l) /
        10000
      ).toFixed(2);
    },
    changeWaterCompany(row) {
      this.form.yearId = "";
      this.form.year = "";
      this.waterYear = {
        idValue: "",
        yearKey: ""
      };
      this.form.monthId = "";
      this.form.month = "";
      this.waterMonth = {
        idValue: "",
        yearKey: ""
      };
      queryYearPlanDictList({
        companyId: row
      }).then(res => {
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.yearOption = res.data;
      });
      // this.selectMonthOption(row, this.form.yearId);
    },
    changeYear(row) {
      this.form.yearId = row.idValue;
      this.form.year = row.yearKey;
      this.selectMonthOption(this.form.waterCompany, row.idValue);
    },
    selectMonthOption(waterCompanyId, yearId) {
      this.form.monthId = "";
      this.form.month = "";
      this.waterMonth = {
        idValue: "",
        yearKey: ""
      };
      if (waterCompanyId && yearId) {
        queryMonthPlanDictList({
          waterCompanyId: waterCompanyId,
          yearId: yearId
        }).then(res => {
          if (res.code !== 200) {
            return this.$message.error(res.msg);
          }
          this.monthOption = res.data;
        });
      } else {
        this.monthOption = [];
      }
    },
    changeMonth(row) {
      this.form.monthId = row.idValue;
      this.form.month = row.yearKey;
    },
    // 选择上中下旬 修改指点日期
    changeTenDays(row) {
      this.form.tenDays = row.dictValue;
      this.form.tenDaysName = row.dictLabel;
      // 当改变 月份或者年份的时候 绑定的值应当取消重新选择 一遍上报正确
      this.form.daysList = [];
    },
    //动态改变上下旬日期选择
    dynamicDateOpt(year, month, type) {
      console.log("0---0");
      let y = year;
      let m = month;
      let arr1 = ["", 1, 11, 21];
      let arr2 = ["", 10, 20, new Date(y, m - 1, 0).getDate()];
      let d1 = arr1[type];
      let d2 = arr2[type];

      if (m < 7) {
        y = parseInt(y) + 1;
      }
      this.pickerOptions = {
        disabledDate(time) {
          let cur = new Date(y, m - 1, d1),
            up = new Date(y, m - 1, d2);
          return (
            time.getTime() < cur.getTime() || time.getTime() > up.getTime()
          ); //大于当前的禁止，小于7天前的禁止
        }
      };
    },
    onClickClose() {
      this.$emit("close", false);
    },

    submitForm(formName, status) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let form = JSON.parse(JSON.stringify(this.form));
          form.status = status;
          form.waterCompany = form.waterCompany;
          console.log(form);
          let ids = [];
          this.fileList.forEach(n => {
            ids.push(n.id);
          });
          if (ids.length === 0) {
            this.$emit("close", true, form);
            return;
          }
          if (form.applicationFile) {
            uploadEditFile({
              groupId: form.applicationFile,
              ids: ids
            }).then(res => {
              console.log(res);
              this.$emit("close", true, form);
            });
          } else {
            uploadAddFile(ids).then(res => {
              console.log(res);
              form.applicationFile = res.data;
              this.$emit("close", true, form);
            });
          }
          this.$emit("close", true, form);
        } else {
          return false;
        }
      });
    },
    aduitForm(status) {
      if (status == "3") {
        if (!this.form.rejection) {
          return this.$message.warning("请填写驳回原因");
        }
      }
      if (status == "2") {
        this.form.rejection = "";
      }
      let parms = {
        bak: "",
        planId: this.form.id,
        sign: "3",
        status: status
      };
      this.$emit("close", true, parms, JSON.parse(JSON.stringify(this.form)));
    },
    resetModel(model = {}) {
      this.form = this.$util.coverValue(this.form, model, true);
    }
  }
};
</script>
<style lang="scss">
.waterDisPlanDialog {
  .el-table .cell {
    overflow: inherit;
  }
}
</style>
