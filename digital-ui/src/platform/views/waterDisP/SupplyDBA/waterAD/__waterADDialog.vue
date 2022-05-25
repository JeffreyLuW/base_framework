<template>
  <AutoDialog
    :show="show"
    class="waterADDialog"
    :title="protitle"
    sizeMode="auto"
    width="1024px"
    :fullscreen.sync="fullscreen"
    :closeOnClickModal="false"
    @close="onClickClose"
  >
    <el-form :model="form" ref="form" :rules="rules">
      <el-divider content-position="left">旬计划的基本信息</el-divider>
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
            <span>{{ tendaysDTO.waterCompany }}</span>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            供水年度
          </td>
          <td class="theme-table-form__input">
            <span>{{ tendaysDTO.year }}</span>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            月份
          </td>
          <td class="theme-table-form__input">
            <span>{{ tendaysDTO.month }}</span>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            旬
          </td>
          <td class="theme-table-form__input">
            <span>{{ tendaysDTO.tenDaysName }}</span>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>指标水量
          </td>
          <td class="theme-table-form__input">
            <span>{{ tendaysDTO.indexWater }}</span
            >(万m³)
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            状态
          </td>
          <td class="theme-table-form__input">
            <span>{{ statusO[tendaysDTO.status] }}</span>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>灌溉面积
          </td>
          <td class="theme-table-form__input" prop="irrigationArea">
            <span>{{ tendaysDTO.irrigationArea }}</span
            >(万亩)
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>需水量
          </td>
          <td class="theme-table-form__input">
            <span>{{ tendaysDTO.needWater }}</span
            >(万m³)
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>收益乡镇
          </td>
          <td class="theme-table-form__input" colspan="3">
            <span>{{ tendaysDTO.profitTown }}</span>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>用途
          </td>
          <td class="theme-table-form__input" colspan="3">
            <span>{{ tendaysDTO.purpose }}</span>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>旬计划列表
          </td>
          <td class="theme-table-form__input" colspan="3" style="overflow: auto">
            <el-table
              :data="tendaysDTO.daysList"
              height="100%"
              border
              style="min-height: 0px"
            >
              <el-table-column
                v-for="(v, i) in columns"
                :key="i"
                :prop="v.field"
                :label="v.title"
                :width="v.type == 'month1' || v.type == 'month2' ? '255px' : '130px'"
                align="center"
              >
                <template slot-scope="scope">
                  <span>
                    {{
                      v.type == "month1"
                        ? `${scope.row.agricultureStart}至${scope.row.agricultureEnd}`
                        : v.type == "month2"
                        ? `${scope.row.noAgricultureStart}至${scope.row.noAgricultureEnd}`
                        : scope.row[v.field]
                    }}
                  </span>
                </template>
              </el-table-column>
            </el-table>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">申请文件</td>
          <td class="theme-table-form__input" colspan="3">
            <Upload v-model="tendaysDTOFile" :disabled="false" :limit="1"></Upload>
          </td>
        </tr>
      </table>
      <div v-if="adjustList.length > 0">


        <el-divider content-position="left">水量调整历史信息</el-divider>


        <table
          class="theme-table-form"
          style="margin: 5px 0"
          v-for="(item, i) in adjustList"
          :key="i"
        >
          <colgroup>
            <col width="160" />
            <col />
            <col width="160" />
            <col />
          </colgroup>
          <tr>
            <td class="theme-table-form__label">
              <span class="theme-table-form__required"></span>原先需水量
            </td>
            <td class="theme-table-form__input">
              <span>{{ item.formerWater }}</span>
            </td>
            <td class="theme-table-form__label">
              <span class="theme-table-form__required"></span>调整后水量
            </td>
            <td class="theme-table-form__input">
              <span>{{ item.needWater }}</span>
            </td>
          </tr>
          <tr>
            <td class="theme-table-form__label">
              各时间段需水量
            </td>
            <td class="theme-table-form__input" colspan="3" style="overflow: auto">
              <el-table
                :data="item.waterList"
                height="100%"
                border
                style="min-height: 0px"
              >
                <el-table-column
                  v-for="(v, i) in columns"
                  :key="i"
                  :prop="v.field"
                  :label="v.title"
                  :width="v.type == 'month1' || v.type == 'month2' ? '255px' : '130px'"
                  align="center"
                >
                  <template slot-scope="scope">
                    <span>
                      {{
                        v.type == "month1"
                          ? `${scope.row.agricultureStart}至${scope.row.agricultureEnd}`
                          : v.type == "month2"
                          ? `${scope.row.noAgricultureStart}至${scope.row.noAgricultureEnd}`
                          : scope.row[v.field]
                      }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </td>
          </tr>
          <tr>
            <td class="theme-table-form__label">
              申请文件
            </td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="参数键值" prop="applicationFile">
                <Upload v-model="item.fileList" :editable="true" :limit="1"></Upload>
              </el-form-item>
            </td>
          </tr>
        </table>
      </div>



      <el-divider content-position="left">水量调整的信息</el-divider>
      
      <table class="theme-table-form">
        <colgroup>
          <col width="160" />
          <col />
          <col width="160" />
          <col />
        </colgroup>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>原先需水量
          </td>
          <td class="theme-table-form__input">
            <!-- <el-form-item label="原先需水量" prop="formerWater">
              <el-input
                type="text"
                placeholder="请输入原先需水量"
                v-model="form.formerWater"
              >
                <template slot="append">万(m³)</template>
              </el-input>
            </el-form-item> -->
            {{adjustList.length>0?adjustList[adjustList.length-1].needWater:tendaysDTO.needWater}}
            <!-- {{tendaysDTO.needWater}} -->
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>调整后水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="调整后水量" prop="needWater">
              <el-input
                type="text"
                placeholder="请输入调整后水量"
                v-model="form.needWater"
              >
                <template slot="append">万(m³)</template>
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            旬计划列表
          </td>
          <td class="theme-table-form__input" colspan="3" style="overflow: auto">
            <el-form-item prop="waterList">
              <el-button @click="addtableColumn()">添加</el-button>
              <el-table
                :data="form.waterList"
                height="100%"
                border
                style="min-height: 0px"
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
                  v-for="(v, i) in columns"
                  :key="i"
                  :prop="v.field"
                  :label="v.title"
                  :width="v.type == 'month1' || v.type == 'month2' ? '255px' : '130px'"
                  align="center"
                >
                  <template slot-scope="scope">
                    <el-form-item
                      :prop="`waterList[${scope.$index}].${v.field}`"
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
                      :prop="`waterList[${scope.$index}].${v.field}`"
                      v-else-if="v.type == 'month1' || v.type == 'month2'"
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
              <Upload v-model="fileList" :limit="1"></Upload>
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
import {
  uploadAddFile,
  getGroupFile,
  uploadEditFile,
} from "@/platform/api/common/common.js";
import Upload from "@/platform/views/waterDisP/__comom/upload";
export default {
  components: {
    Upload,
  },
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
      pickerOptions: {},
      fileList: [],
      tendaysDTO: {},
      tendaysDTOFile: [],
      adjustList: [],
      defTime: [],
      form: {
        applicationFile: "",
        formerWater: '',
        needWater: '',
        tendaysId: "",
        waterList: [],
      },
      columns: [
        { field: "time1", title: "农业开始结束日期", type: "month1" },
        {
          field: "agricultureDays",
          title: "农业天数",
          type: "readyonly",
        },
        {
          field: "agricultureFlow",
          title: "农业流量(m³/s)",
          type: "number",
        },
        {
          field: "agricultureWater",
          title: "农业水量(万m³)",
          type: "readyonly",
        },
        { field: "time2", title: "非农业开始结束日期", type: "month2" },
        {
          field: "noAgricultureDays",
          title: "非农业天数",
          type: "readyonly",
        },
        {
          field: "noAgricultureFlow",
          title: "非农业流量(m³/s)",
          type: "number",
        },
        {
          field: "noAgricultureWater",
          title: "非农业水量(万m³)",
          type: "readyonly",
        },
      ],
      rules: {
        formerWater: ValidateUtil.requiredPercent(0, 9999),
        needWater: ValidateUtil.requiredPercent(0, 9999),
        abc: ValidateUtil.requiredPercent(0, 9999),
        // applicationFile: ValidateUtil.required(),
      },
      statusO: ["草稿", "待审批", "已审批", "已驳回"],
      l: 86400,
    };
  },
  created() {
    this.$nextTick(() => {
      if (this.model) {
        this.tendaysDTO = this.model.tendaysDTO;
        // 编辑 进入
        console.log(this.mode);
        if (this.mode == "add") {
          if (this.model.adjustList.length > 0) {
            this.adjustList = this.model.adjustList;
            this.getHistoryFile(this.adjustList);
          }
        } else if (this.mode == "update") {
          let adjustList = this.model.adjustList;
          if (adjustList.length > 0) {
            this.adjustList = adjustList.slice(0, adjustList.length - 1);
            console.log(this.model.adjustList);
            console.log(adjustList);
            this.getHistoryFile(this.adjustList);
            this.form = this.model.adjustList[adjustList.length - 1];
            if (this.form.applicationFile) {
              getGroupFile(this.form.applicationFile).then((res) => {
                this.fileList = res.data;
              });
            }
          }
        }
        
        this.form.tendaysId = this.tendaysDTO.id;
        let tenDays =
          this.tendaysDTO.tenDaysName == "上旬"
            ? 1
            : this.tendaysDTO.tenDaysName == "中旬"
            ? 2
            : 3;
        this.dynamicDateOpt(this.tendaysDTO.year, this.tendaysDTO.month, tenDays);
        this.initShow();
        if (this.tendaysDTO.applicationFile) {
          getGroupFile(this.tendaysDTO.applicationFile).then((res) => {
            this.tendaysDTOFile = res.data;
          });
        }
      }
    });
  },
  methods: {
    // 历史信息文件获取
    getHistoryFile(list) {
      console.log(list);
      let arr = list || [];
      console.log(arr);
      arr.forEach((n) => {
        getGroupFile(n.applicationFile).then((res) => {
          this.$set(n, "fileList", res.data);
        });
      });
    },
    // 编辑回显 显示
    initShow() {
      console.log(this.form.waterList);
      let t = this.form.waterList;
      let arr = [];
      t.forEach((n) => {
        n.time1 = [n.agricultureStart, n.agricultureEnd];
        n.time2 = [n.noAgricultureStart, n.noAgricultureEnd];
        arr.push(n);
      });
      console.log(arr);
      this.form.waterList = arr;
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
        tenDaysId: this.form.tenDays,
      };
      this.form.waterList.push(j);
    },
    // 删除操作
    delChange(row, index) {
      this.form.waterList.splice(index, 1);
    },

    // 计算选着时间天数
    changePickDay(row) {
      if (row.time1) {
        row.agricultureDays =
          (new Date(row.time1[1]).getTime() - new Date(row.time1[0]).getTime()) /
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
          (new Date(row.time2[1]).getTime() - new Date(row.time2[0]).getTime()) /
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
    //动态改变上下旬日期选择
    dynamicDateOpt(year, month, type) {
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
          return time.getTime() < cur.getTime() || time.getTime() > up.getTime(); //大于当前的禁止，小于7天前的禁止
        },
      };
      this.defTime = [new Date(y, m - 1, d1), new Date(y, m - 1, d2)];
    },
    onClickClose() {
      this.$emit("close", false);
    },

    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.form.formerWater = this.adjustList.length>0?this.adjustList[this.adjustList.length-1].needWater:this.tendaysDTO.needWater
          console.log(this.form);
          if(this.model.id){
            this.form.id = this.model.id
          }
          let ids = [];
          this.fileList.forEach((n) => {
            ids.push(n.id);
          });
          if (ids.length === 0) {
            this.$emit("close", true, this.form);
            return;
          }
          if (this.form.applicationFile) {
            uploadEditFile({
              groupId: this.form.applicationFile,
              ids: ids,
            }).then((res) => {
              this.$emit("close", true, this.form);
            });
          } else {
            uploadAddFile(ids).then((res) => {
              this.form.applicationFile = res.data;
              this.$emit("close", true, this.form);
            });
          }
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
.waterADDialog {
  .el-table .cell {
    overflow: inherit;
  }
}
</style>
