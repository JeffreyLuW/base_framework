<template>
  <div class="root">
    <el-button
      type="primary"
      size="mini"
      class="btn"
      icon="el-icon-upload2"
      @click="upload.open = true"
      v-if="isShowImport"
      v-hasPermi="[importPermission]"
      >Excel导入</el-button
    >
    <el-button
      type="primary"
      size="mini"
      class="btn"
      icon="el-icon-download"
      @click="handleExport"
      v-if="isShowExport"
      v-hasPermi="[exportPermission]"
      >Excel导出</el-button
    >

    <AutoDialog :title="upload.title" :show.sync="upload.open" width="400px">
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :on-change="beforeChangeFile"
        :action="uploadUrl + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          将文件拖到此处，或
          <em>点击上传</em>
        </div>
        <div class="el-upload__tip" style="text-align: right" slot="tip">
          <el-link
            type="danger"
            style="font-size: 12px"
            @click="ImportTemplate"
            v-if="isShowImportTemplate"
            >点击下载模板</el-link
          >
        </div>
        <div class="el-upload__tip" style="color: red" slot="tip">
          提示：仅允许导入“xls”或“xlsx”格式文件！
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </AutoDialog>
  </div>
</template>
<script>
import Upload from "@/platform/views/waterDisP/__comom/upload";
import { getToken } from "@/platform/utils/auth";
export default {
  name: "ExcelUploadAndDowmload",
  props: {
    url: {
      type: String,
      default: "",
    },
    ids: {
      type: Array,
      default: () => {
        return [];
      },
    },
    type: {
      type: String,
      default: "table",
    },
    exportExls: {
      type: Function,
      default: () => () => {},
    },
    importTemplate: {
      type: Function,
      default: () => () => {},
    },
    isShowImport: {
      type: Boolean,
      default: true,
    },
    isShowExport: {
      type: Boolean,
      default: true,
    },
    isShowImportTemplate: {
      type: Boolean,
      default: true,
    },
    exportPermission: {
      type: String,
      default: "",
    },
    importPermission: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      // 导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "导入",
        // 是否禁用上传
        isUploading: false,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        // url: process.env.VUE_APP_BASE_API + "/irrEsDailypatrol/irrEsDailypatrol/import",
      },
    };
  },
  computed: {
    uploadUrl() {
      return process.env.VUE_APP_BASE_API + this.url;
    },
  },
  methods: {
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
    /** 下载模板操作 */
    ImportTemplate() {
      this.importTemplate().then((response) => {
        this.download(response.msg);
      });
    },

    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert(response.msg, "导入结果", {
        dangerouslyUseHTMLString: true,
      });
      this.getList();
    },

    beforeChangeFile(file, fileList) {
      if (!file.name.endsWith(".xls") && !file.name.endsWith(".xlsx")) {
        fileList.splice(0, fileList.length);
        this.msgError("只能上传xls和xlsx文件");
      }
    },

    /** 导出按钮操作 */
    handleExport() {
      this.$confirm(`是否确认导出所${this.ids.length > 0 ? "选" : "有"}数据项?`, "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          if (this.type === "table") {
            // if (this.ids.length === 0) {
            //   this.$message.warning("请选择需要导出的项目");
            //   return new Promise((resolve, reject) => {
            //     reject();
            //   });
            // } else {
            //   return this.exportExls(this.ids);
            // }

            return this.exportExls(this.ids);
          }
        })
        .then((response) => {
          this.download(response.msg);
        })
        .catch(function () {});
    },
  },
};
</script>
<style scoped>
.root {
  margin-left: 10px;
}
.el-button {
  padding: 7px 11px;
}
</style>
