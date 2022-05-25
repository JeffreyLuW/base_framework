<template>
  <AutoDialog
    :show="show"
    class="annualDPDialog"
    :title="protitle"
    sizeMode="auto"
    width="820px"
    fullscreen.sync="null"
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
                :disabled="mode == 'view'"
                placeholder="请选择用水单位"
                style="width: 220px"
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
            <el-form-item label="供水年度" prop="waterYear">
              <el-select
                v-model="waterYear"
                placeholder="请选择供水年度"
                style="width: 220px"
                :value-key="`idValue`"
                @change="changeYear"
                :disabled="mode == 'view'"
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
            <span class="theme-table-form__required"></span>指标水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="指标水量" prop="indexWater">
              <el-input
                type="text"
                placeholder="请输入指标水量"
                v-model="form.indexWater"
                :disabled="mode == 'view'"
              >
                <template slot="append">万(m³)</template>
              </el-input>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">状态</td>
          <td class="theme-table-form__input">
            <span>{{ status[form.status] }}</span>
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
                :disabled="mode == 'view'"
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
                :disabled="mode == 'view'"
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
                :disabled="mode == 'view'"
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
                :disabled="mode == 'view'"
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
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>各月数据
          </td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item prop="monthList">
              <el-button @click="addtableColumn()" v-if="mode != 'view'"
                >添加</el-button
              >
              <el-table
                :data="form.monthList"
                height="100%"
                border
                style="min-height: 180px"
              >
                <el-table-column label="操作" width="60" v-if="mode != 'view'">
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
                  v-for="(v, i) in tableColumn.columns"
                  :key="i"
                  :prop="v.field"
                  :label="v.title"
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
                        v-model="scope.row[v.field]"
                        :disabled="mode == 'view'"
                      ></el-input>
                    </el-form-item>
                    <el-form-item
                      :prop="`monthList[${scope.$index}].${v.field}`"
                      v-else-if="v.type == 'month'"
                      :rules="{
                        required: true,
                        message: '不能为空',
                        trigger: ['blur', 'change']
                      }"
                    >
                      <el-select
                        v-model="scope.row[v.field]"
                        placeholder="请选择月度"
                        @change="selectMonth"
                        :disabled="mode == 'view'"
                      >
                        <el-option
                          v-for="item in curMonthOptions"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        >
                        </el-option>
                      </el-select>
                    </el-form-item>
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
                :limit="1"
                :disabled="mode !== 'view'"
              ></Upload>
            </el-form-item>
          </td>
        </tr>
      </table>
    </el-form>
    <template slot="footer">
      <el-button
        type="primary"
        v-if="mode != 'audit' && mode != 'view'"
        @click="submitForm('form', 0)"
        >保存</el-button
      >
      <el-button
        type="primary"
        v-if="mode != 'audit' && mode != 'view'"
        @click="submitForm('form', 1)"
        >提交</el-button
      ><el-button
        type="primary"
        v-if="mode == 'audit' && model.status == '1' && mode != 'view'"
        @click="aduitForm('2')"
        >通过</el-button
      ><el-button
        type="primary"
        v-if="mode == 'audit' && model.status == '1' && mode != 'view'"
        @click="aduitForm('3')"
        >驳回</el-button
      >
      <el-button @click="onClickClose">取消</el-button>
    </template>
  </AutoDialog>
</template>
<script>
import ValidateUtil from "@/platform/utils/ValidateUtil.js";
import { queryRulesDicts } from "@/platform/api/waterDisP/waterRules";
import { queryCompentDicts } from "@/platform/api/waterR/waterInfo";
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
      form: {
        id: "",
        applicationFile: undefined,
        indexWater: 0,
        irrigationArea: 0,
        monthList: [
          // {
          //     agriculture: 0,
          //     month: "",
          //     noAgriculture: 0,
          //     total: 0,
          // },
        ],
        needWater: 0,
        profitTown: "",
        purpose: "",
        status: "0",
        waterCompany: "",
        waterYear: "",
        year: ""
      },
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
        year: ValidateUtil.required(),
        monthList: ValidateUtil.requiredArray()
      },
      tableColumn: {
        sel: null, //选中行
        columns: [
          { field: "month", title: "月度", type: "month" },
          {
            field: "agriculture",
            title: "农业(万m³)",
            type: "number"
          },
          {
            field: "noAgriculture",
            title: "非农业(万m³)",
            type: "number"
          },
          { field: "total", title: "总水量(万m³)", type: "number" }
        ],
        data: []
      },
      //存取临时月份 防止重复添加
      tempMonth: new Set(),
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
      status: ["草稿", "待审批", "已审批", "已驳回"],
      monthOptions: [
        {
          value: "7",
          label: "7月"
        },
        {
          value: "8",
          label: "8月"
        },
        {
          value: "9",
          label: "9月"
        },
        {
          value: "10",
          label: "10月"
        },
        {
          value: "11",
          label: "11月"
        },
        {
          value: "12",
          label: "12月"
        },
        {
          value: "1",
          label: "1月"
        },
        {
          value: "2",
          label: "2月"
        },
        {
          value: "3",
          label: "3月"
        },
        {
          value: "4",
          label: "4月"
        },
        {
          value: "5",
          label: "5月"
        },
        {
          value: "6",
          label: "6月"
        }
      ],
      curMonthOptions: [],
      yearOption: [],
      useWaterOptions: []
    };
  },
  created() {
    this.$nextTick(() => {
      this.curMonthOptions = this.monthOptions;
      queryCompentDicts()
        .then(res => {
          if (res.code !== 200) {
            return this.$message.error(res.msg);
          }
          this.useWaterOptions = res.data;
        })
        .then(() => {
          queryRulesDicts().then(res => {
            if (res.code !== 200) {
              return this.$message.error(res.msg);
            }
            this.yearOption = res.data;
            if (this.model) {
              this.form = this.model;
              let monthList = this.model.monthList;
              monthList.forEach(n => {
                this.selectMonth(n.month);
              });
              this.waterYear.idValue = this.model.waterYear;
              if (this.form.applicationFile) {
                getGroupFile(this.form.applicationFile).then(res => {
                  this.fileList = res.data;
                });
              }
            }
          });
        });
    });
  },
  methods: {
    changeYear(row) {
      this.form.waterYear = row.idValue;
      this.form.year = row.yearKey;
    },
    onClickClose() {
      this.$emit("close", false);
    },
    //新增水量
    addtableColumn() {
      let j = {
        month: "",
        agriculture: 0,
        noAgriculture: 0,
        total: 0
      };
      this.form.monthList.push(j);
    },
    // 删除操作
    delChange(row, index) {
      this.form.monthList.splice(index, 1);
      this.tempMonth.delete(row.month);
      this.overMonth();
    },
    // 选择月份
    selectMonth(m) {
      this.tempMonth.add(m);
      this.overMonth();
    },
    // 最终可选月份
    overMonth() {
      this.curMonthOptions = this.monthOptions.filter(n => {
        return !this.tempMonth.has(n.value);
      });
    },
    submitForm(formName, status) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let form = JSON.parse(JSON.stringify(this.form));
          form.status = status;
          let ids = [];
          this.fileList.forEach(n => {
            ids.push(n.id);
          });
          if (ids.length === 0) {
            if (form.applicationFile) {
              uploadEditFile({
                groupId: form.applicationFile,
                ids: ids
              }).then(res => {
                this.$emit("close", true, form);
                return;
              });
            }
          }
          if (form.applicationFile) {
            uploadEditFile({
              groupId: form.applicationFile,
              ids: ids
            }).then(res => {
              this.$emit("close", true, form);
            });
          } else {
            uploadAddFile(ids).then(res => {
              form.applicationFile = res.data;
              this.$emit("close", true, form);
            });
          }
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
        sign: "1",
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
.annualDPDialog {
  .el-table .cell {
    overflow: inherit;
  }
}
</style>
