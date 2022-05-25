<template>
  <AutoDialog
    :show="show"
    class="waterDisPlanDialog"
    :title="protitle"
    sizeMode="auto"
    width="900px"
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
          <td class="theme-table-form__label">申请单位</td>
          <td class="theme-table-form__input">
            <span>{{ form.applyCompanyName }}</span>
          </td>
          <td class="theme-table-form__label">取水口</td>
          <td class="theme-table-form__input">
            <span>{{ form.waterIntake }}</span>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">许可水量</td>
          <td class="theme-table-form__input">
            <span>{{ form.allowWater }}</span>
          </td>
          <td class="theme-table-form__label">年计划批复水量</td>
          <td class="theme-table-form__input">
            <span
              >{{ form.yearReplyWater }}{{ form.yearReplyWater ? "万(m³)" : "" }}</span
            >
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>城市生活用水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="城市生活" prop="cityLife">
              <el-input type="text" placeholder="请输入城市生活" v-model="form.cityLife">
                <template slot="append">万(m³)</template>
              </el-input>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>农业灌溉用水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="农业灌溉用水量" prop="agriculture">
              <el-input
                type="text"
                placeholder="请输入农业灌溉用水量"
                v-model="form.agriculture"
              >
                <template slot="append">万(m³)</template>
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>工业用水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="工业用水量" prop="industry">
              <el-input type="text" placeholder="请输入工业用水量" v-model="form.industry"
                ><template slot="append">万(m³)</template>
              </el-input>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>生态环境用水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="生态环境用水量" prop="ecology">
              <el-input
                type="text"
                placeholder="请输入生态环境用水量"
                v-model="form.ecology"
                ><template slot="append">万(m³)</template>
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>合计
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="合计" prop="total">
              <el-input type="text" placeholder="请输入合计" v-model="form.total"
                ><template slot="append">万(m³)</template>
              </el-input>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>取水时段
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="取水时段" prop="intakeTime">
              <el-date-picker
                v-model="form.intakeTime"
                type="daterange"
                style="width: 255px"
                range-separator="至"
                value-format="yyyy-MM-dd"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="changePickDay"
              >
              </el-date-picker>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>取水流量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="取水流量" prop="intakeFlow">
              <el-input type="text" placeholder="请输入取水流量" v-model="form.intakeFlow"
                ><template slot="append">m³/s</template>
              </el-input>
            </el-form-item>
          </td>

          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>填写日期
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="合计" prop="writeDate">
              <el-date-picker
                v-model="form.writeDate"
                type="date"
                style="width: 255px"
                value-format="yyyy-MM-dd"
              >
              </el-date-picker>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>申请单位意见
          </td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="申请单位意见" prop="applyOpinion">
              <el-input
                type="text"
                placeholder="请输入申请单位意见"
                v-model="form.applyOpinion"
              >
              </el-input>
            </el-form-item>
          </td>
        </tr>
        <!-- <tr>
                    <td class="theme-table-form__label">
                        <span class="theme-table-form__required"></span>月度用水
                    </td>
                    <td class="theme-table-form__input" colspan="3">
                        <el-button @click="addtableColumn()">添加</el-button>
                        <el-table :data="tableColumn.data" height="100%" border>
                            <el-table-column label="操作" width="97">
                                <template slot-scope="scope">
                                    <span
                                        class="el-tag el-tag--info el-tag--mini"
                                        style="cursor: pointer"
                                        @click="
                                            editChange(
                                                scope.row,
                                                scope.$index,
                                                true
                                            )
                                        "
                                        >{{
                                            scope.row.isSet ? "保存" : "修改"
                                        }}</span
                                    >
                                    <span
                                        v-if="!scope.row.isSet"
                                        class="el-tag el-tag--danger el-tag--mini"
                                        style="cursor: pointer"
                                        @click="
                                            delChange(scope.row, scope.$index)
                                        "
                                        >删除</span
                                    >
                                    <span
                                        v-else
                                        class="el-tag el-tag--mini"
                                        style="cursor: pointer"
                                        @click="
                                            editChange(
                                                scope.row,
                                                scope.$index,
                                                false
                                            )
                                        "
                                        >取消</span
                                    >
                                </template>
                            </el-table-column>
                            <el-table-column
                                v-for="(v, i) in tableColumn.columns"
                                :key="i"
                                :prop="v.field"
                                :label="v.title"
                                :width="v.type === 'month' ? 220 : 120"
                                :align="v.type === 'month' ? 'center' : 'left'"
                            >
                                <template slot-scope="scope">
                                    <span v-if="scope.row.isSet">
                                        <el-input
                                            size="mini"
                                            v-if="v.type == 'text'"
                                            :placeholder="`${v.title}`"
                                            v-model="tableColumn.sel[v.field]"
                                            @input="changeLL"
                                        ></el-input>
                                        <el-input
                                            size="mini"
                                            v-else-if="v.type == 'number'"
                                            :placeholder="`请输入${v.title}`"
                                            type="number"
                                            min="0"
                                            v-model="tableColumn.sel[v.field]"
                                        ></el-input>
                                        <el-input
                                            size="mini"
                                            v-else-if="v.type == 'readonly'"
                                            :placeholder="`${v.title}`"
                                            v-model="tableColumn.sel[v.field]"
                                            :disabled="true"
                                        ></el-input>
                                        <el-select
                                            v-model="tableColumn.sel[v.field]"
                                            placeholder="请选择月度"
                                            v-else-if="v.type == 'month'"
                                        >
                                            <el-option
                                                v-for="item in curMonthOptions"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value"
                                            >
                                            </el-option>
                                        </el-select>
                                    </span>
                                    <span v-else>{{
                                        idToName(scope.row[v.field], v.field)
                                    }}</span>
                                </template>
                            </el-table-column>
                        </el-table>
                    </td>
                </tr> -->
        <!-- <tr>
                    <td class="theme-table-form__label">
                        申请文件
                    </td>
                    <td class="theme-table-form__input" colspan="3">
                        <el-form-item label="参数键值" prop="configValue">
                            <DataUpload
                                v-model="form.configValue"
                                :limit="1"
                            ></DataUpload>
                        </el-form-item>
                    </td>
                </tr> -->
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
      visible: false,
      form: {
        agriculture: 0,
        allowWater: 0,
        applyCompany: "",
        applyCompanyName: "",
        applyOpinion: "",
        cityLife: 0,
        intakeTime: [],
        ecology: 0,
        industry: 0,
        intakeEnd: "",
        intakeFlow: 0,
        intakeStart: "",
        total: 0,
        waterIntake: "",
        writeDate: "",
        yearReplyWater: 0,
      },
      early: {},
      rules: {
        cityLife: ValidateUtil.requiredPercent(0, 9999),
        agriculture: ValidateUtil.requiredPercent(0, 9999),
        industry: ValidateUtil.requiredPercent(0, 9999),
        ecology: ValidateUtil.requiredPercent(0, 9999),
        intakeFlow: ValidateUtil.requiredPercent(0, 9999),
        total: ValidateUtil.requiredPercent(0, 9999),
        intakeTime: ValidateUtil.requiredArray(),
        writeDate: ValidateUtil.required(),
        applyOpinion: ValidateUtil.requiredLength(0, 255),
      },
      monthOptions: [
        {
          value: 7,
          label: "7月",
        },
        {
          value: 8,
          label: "8月",
        },
        {
          value: 9,
          label: "9月",
        },
        {
          value: 10,
          label: "10月",
        },
        {
          value: 11,
          label: "11月",
        },
        {
          value: 12,
          label: "12月",
        },
        {
          value: 1,
          label: "1月",
        },
        {
          value: 2,
          label: "2月",
        },
        {
          value: 3,
          label: "3月",
        },
        {
          value: 4,
          label: "4月",
        },
        {
          value: 5,
          label: "5月",
        },
        {
          value: 6,
          label: "6月",
        },
      ],
      curMonthOptions: [],
      DPOptions: [],
      // 表格Obj
      tableColumn: {
        sel: null, //选中行
        columns: [
          { field: "month", title: "月度", type: "month" },
          { field: "day", title: "农业", type: "text" },
          { field: "ll", title: "非农业", type: "text" },
          { field: "sl", title: "总水量", type: "readonly" },
        ],
        data: [],
      },
      //存取临时月份 防止重复添加
      tempMonth: new Set(),
      l: 86400,
    };
  },
  created() {
    this.$nextTick(() => {
      this.curMonthOptions = this.monthOptions;
      if (this.mode == "add") {
        this.form.applyCompanyName = this.model.applyCompanyName;
        this.form.applyCompany = this.model.applyCompany;
        this.form.waterIntake = this.model.waterIntake;
        this.form.allowWater = this.model.allowWater;
        this.form.yearReplyWater = this.model.yearReplyWater;
        this.form.intakeTime = [];
      } else if (this.mode == "update") {
        this.form = this.model;

        this.$set(this.form, "intakeTime", [
          this.model.intakeStart,
          this.model.intakeEnd,
        ]);
        // this.form.intakeTime = [
        //     this.model.intakeStart,
        //     this.model.intakeEnd,
        // ];
      }
    });
  },
  watch: {
    "tableColumn.sel.day"(v) {
      this.tableColumn.sel.sl = this.l * this.tableColumn.sel.ll * v;
    },
  },
  methods: {
    changePickDay() {
      console.log(1);
      this.form.intakeStart = this.form.intakeTime[0];
      this.form.intakeEnd = this.form.intakeTime[1];
    },
    onClickClose() {
      this.$emit("close", false);
    },
    init() {
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
      });
    },
    changeLL(v) {
      this.tableColumn.sel.sl = this.l * this.tableColumn.sel.day * v;
    },
    //读取表格数据
    readtableColumn() {
      //根据实际情况，自己改下啊
      this.tableColumn.data.map((i) => {
        i.guid = generateGuId.get();
        i.isSet = false; //给后台返回数据添加`isSet`标识
        return i;
      });
    },
    //新增水量
    addtableColumn() {
      for (let i of this.tableColumn.data) {
        if (i.isSet) return this.$message.warning("请先保存当前编辑项");
      }
      let j = {
        guid: 0,
        month: "",
        day: "0",
        ll: "0",
        sl: "0",
        isSet: true,
      };
      this.tableColumn.data.push(j);
      this.tableColumn.sel = JSON.parse(JSON.stringify(j));
    }, //修改
    editChange(row, index, cg) {
      let _this = this;
      //点击修改 判断是否已经保存所有操作
      for (let i of this.tableColumn.data) {
        if (i.isSet && i.guid != row.guid) {
          _this.$message.warning("请先保存当前编辑项");
          return false;
        }
      }
      //是否是取消操作
      if (!cg) {
        if (!_this.tableColumn.sel.guid) _this.tableColumn.data.splice(index, 1);
        return (row.isSet = !row.isSet);
      }

      //提交数据
      if (row.isSet) {
        row = this.saveChange(row);
        // _this.$message({
        //     type: 'success',
        //     message: "保存成功!"
        // });
        //然后这边重新读取表格数据
        _this.readtableColumn();
        row.isSet = false;
      } else {
        _this.tableColumn.sel = JSON.parse(JSON.stringify(row));
        row.isSet = true;
      }
    },
    // 删除操作
    delChange(row, index) {
      this.tableColumn.data.splice(index, 1);
      this.tempMonth.delete(row.month);
    },
    //保存一条数据
    saveChange(row) {
      let curRow = row;
      //保存数据
      let data = JSON.parse(JSON.stringify(this.tableColumn.sel));
      this.saveMonth(this.tableColumn.sel.month);
      for (let k in data) curRow[k] = data[k];
      return curRow;
    },
    // 保存当前存在的月份
    saveMonth(m) {
      this.tempMonth.add(m);
      this.curMonthOptions = this.monthOptions.filter((n) => {
        return !this.tempMonth.has(n.value);
      });
    },
    idToName(id, type) {
      let result = "";
      if (type == "month") {
        result = `${id}月`;
      } else {
        result = id;
      }
      return result;
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
<style lang="scss" scoped>
.waterDisPlanDialog {
}
</style>
