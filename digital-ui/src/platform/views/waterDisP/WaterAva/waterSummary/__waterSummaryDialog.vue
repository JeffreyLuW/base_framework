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
            <span class="theme-table-form__required"></span>用水单位
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="用水单位" prop="companyId">
              <el-select
                v-model="form.companyId"
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
          <td class="theme-table-form__label">用水量</td>
          <td class="theme-table-form__input">
            <el-form-item label="用水量" prop="water">
              <el-input class="input" placeholder="请输入用水量" v-model="form.water"
                ><template slot="append">万(m³)</template></el-input
              >
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">
            <span class="theme-table-form__required"></span>用水类型
          </td>
          <td class="theme-table-form__input">
            <el-form-item label="用水类型" prop="waterType">
              <el-input
                class="input"
                placeholder="请输入用水类型"
                v-model.number="form.waterType"
              ></el-input>
            </el-form-item>
          </td>
        </tr>
        <tr>
          <td class="theme-table-form__label">附件</td>
          <td class="theme-table-form__input" colspan="3">
            <el-form-item label="参数键值" prop="file">
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
import { queryCompentDicts } from "@/platform/api/waterR/waterInfo";
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
      fileList: [],
      visible: false,
      useWaterOptions: [],
      form: {
        applyId: "",
        companyId: "",
        file: "",
        waterType: "",
        water: 0,
      },
      rules: {
        companyId: ValidateUtil.required(),
        water: ValidateUtil.requiredPercent(0, 9999),
        waterType: ValidateUtil.required(),
      },
    };
  },
  created() {
    this.$nextTick(() => {
      queryCompentDicts().then((res) => {
        if (res.code !== 200) {
          return this.$message.error(res.msg);
        }
        this.useWaterOptions = res.data;
        if (this.mode == "add") {
          this.form.applyId = this.model.id;
        } else if (this.mode == "update") {
          this.form = this.model;
          if (this.form.file) {
            getGroupFile(this.form.file).then((res) => {
              console.log(res);
              this.fileList = res.data;
            });
          }
        }
      });
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
          let ids = [];
          this.fileList.forEach((n) => {
            ids.push(n.id);
          });
          if (ids.length === 0) {
            this.$emit("close", true, this.form);
            return;
          }
          if (this.form.file) {
            uploadEditFile({
              groupId: this.form.file,
              ids: ids,
            }).then((res) => {
              console.log(res);
              this.$emit("close", true, this.form);
            });
          } else {
            uploadAddFile(ids).then((res) => {
              console.log(res);
              this.form.file = res.data;
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
<style lang="scss" scoped>
.waterDisPlanDialog {
}
</style>
