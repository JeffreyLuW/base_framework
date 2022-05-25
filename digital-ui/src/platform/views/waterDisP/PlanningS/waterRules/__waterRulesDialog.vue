<template>
  <AutoDialog
    :show="show"
    class="waterDisPlanDialog"
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
            <span class="theme-table-form__required"></span>
            供水年度
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="供水年度" prop="waterYear">
              <el-date-picker
                v-model="form.waterYear"
                align="right"
                type="year"
                format="yyyy"
                value-format="yyyy"
                placeholder="选择供水年度"
              ></el-date-picker>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>标题
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="标题" prop="title">
              <el-input
                class="input"
                placeholder="请输入标题"
                v-model="form.title"
              ></el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>许可水量
          </td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="许可水量" prop="waterDiversionIndex">
              <el-input
                class="input"
                placeholder="请输入许可水量"
                v-model="form.waterDiversionIndex"
                ><template slot="append">万(m³)</template></el-input
              >
            </el-form-item>
          </td>
         
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>工业许可水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="工业许可水量" prop="industrialWater">
              <el-input
                class="input"
                placeholder="请输入工业许可水量"
                v-model="form.industrialWater"
                ><template slot="append">万(m³)</template></el-input
              >
            </el-form-item>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>生态许可水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="生态许可水量" prop="ecologicalWater">
              <el-input
                class="input"
                placeholder="请输入生态许可水量"
                v-model="form.ecologicalWater"
                ><template slot="append">万(m³)</template></el-input
              >
            </el-form-item>
          </td>
        </tr>
        <tr>
             <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>农业许可水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="农业许可水量" prop="agriculturalWater">
              <el-input
                class="input"
                placeholder="请输入农业许可水量"
                v-model="form.agriculturalWater"
                ><template slot="append">万(m³)</template></el-input
              >
            </el-form-item>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>生活许可水量
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="生活许可水量" prop="liveWater">
              <el-input
                class="input"
                placeholder="请输入生活许可水量"
                v-model="form.liveWater"
                ><template slot="append">万(m³)</template></el-input
              >
            </el-form-item>
          </td>
        </tr>
             <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            供水开始时间
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="供水开始时间" prop="waterStartDate">
              <el-date-picker
                v-model="form.waterStartDate"
                align="right"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="选择供水开始时间"
              ></el-date-picker>
            </el-form-item>
          </td>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            供水结束时间
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="供水结束时间" prop="waterEndDate">
              <el-date-picker
                v-model="form.waterEndDate"
                align="right"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="选择供水结束时间"
              ></el-date-picker>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            填写时间
          </td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="填报填写时间" prop="writeDate">
              <el-date-picker
                style="width:100%"
                v-model="form.writeDate"
                align="right"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="选择填报填写时间"
              ></el-date-picker>
            </el-form-item>
          </td>
          <!-- <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>
            结束时间
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="填报结束时间" prop="waterEndDate">
              <el-date-picker
                v-model="form.waterEndDate"
                align="right"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="选择填报结束时间"
              ></el-date-picker>
            </el-form-item>
          </td> -->
        </tr>
   
        <!-- <tr>
          <td class="theme-table-form__label">内容</td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="内容">
              <el-input
                type="textarea"
                :autosize="{ minRows: 2, maxRows: 4 }"
                placeholder="请输入内容"
                v-model="form.content"
              >
              </el-input>
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
        content: "",
        id: "",
        title: "",
        waterDiversionIndex: 0,
        agriculturalWater: 0,
        industrialWater: 0,
        ecologicalWater: 0,
        liveWater: 0,
        waterEndDate: "",
        waterStartDate: "",
        waterYear: "",
        writeEndDate: "",
        writeStartDate: "",
      },
      rules: {
        title: ValidateUtil.required(),
        waterYear: ValidateUtil.required(),
        waterDiversionIndex: ValidateUtil.requiredPercent(0, 9999999),
        agriculturalWater: ValidateUtil.requiredPercent(0, 9999999),
        ecologicalWater: ValidateUtil.requiredPercent(0, 9999999),
        industrialWater: ValidateUtil.requiredPercent(0, 9999999),
        liveWater: ValidateUtil.requiredPercent(0, 9999999),
        waterEndDate: ValidateUtil.required(),
        waterStartDate: ValidateUtil.required(),
        writeEndDate: ValidateUtil.required(),
        writeStartDate: ValidateUtil.required(),
        writeDate: ValidateUtil.required(),
        writeStartDate: ValidateUtil.required(),
        writeStartDate: ValidateUtil.required(),
        content: ValidateUtil.required(),
      },
    };
  },
  created() {
    this.$nextTick(() => {
      console.log(this.model);
      if (this.model) {
        this.form = this.model;
        this.form.waterYear = this.form.waterYear.toString();
      }
    });
  },
  methods: {
    onClickClose() {
      this.$emit("close", false);
    },
    init() {
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
      });
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
