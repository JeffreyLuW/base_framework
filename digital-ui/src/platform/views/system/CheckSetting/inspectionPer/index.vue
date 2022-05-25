<template>
  <div class="app-container">
    <el-row :gutter="20" class="main-row">
      <!--部门数据-->
      <el-col :span="5" :xs="24" class="theme-pannel">
        <div class="theme-pannel__title">部门选择</div>
        <div class="theme-pannel__body">
          <div class="head-container">
            <el-input
              v-model="deptName"
              placeholder="请输入部门名称"
              clearable
              size="small"
              prefix-icon="el-icon-search"
              style="margin-bottom: 20px"
            />
          </div>
          <div class="head-container">
            <el-tree
              :data="deptOptions"
              :props="defaultProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="tree"
              default-expand-all
              @node-click="handleNodeClick"
            />
          </div>
        </div>
      </el-col>
      <!--用户数据-->
      <el-col :span="19" :xs="24" class="main-row-right">
        <el-form
          :model="queryParams"
          ref="queryForm"
          class="query-form"
          :inline="true"
          label-width="70px"
        >
          <el-form-item label="用户账号" prop="userName">
            <el-input
              v-model="queryParams.userName"
              placeholder="请输入用户账号"
              clearable
              size="small"
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="手机号码" prop="phonenumber">
            <el-input
              v-model="queryParams.phonenumber"
              placeholder="请输入手机号码"
              clearable
              size="small"
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select
              v-model="queryParams.status"
              placeholder="用户状态"
              clearable
              size="small"
              style="width: 240px"
            >
              <el-option
                v-for="dict in statusOptions"
                :key="dict.dictValue"
                :label="dict.dictLabel"
                :value="dict.dictValue"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="dateRange"
              size="small"
              style="width: 240px"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <!--          <el-form-item>-->
          <!--            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>-->
          <!--            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
          <!--          </el-form-item>-->
          <div class="query-form-btns">
            <el-button
              type="primary"
              icon="el-icon-search"
              size="mini"
              @click="handleQuery"
              >搜索</el-button
            >
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
              >重置</el-button
            >
          </div>
        </el-form>
        <!-- <FormToolsRow :buttons="formTools" @click="onClickOps"></FormToolsRow> -->

        <el-table
          v-loading="loading"
          :data="userList"
          border
          @selection-change="handleSelectionChange"
          height="calc(100% - 160px)"
        >
          <el-table-column type="selection" width="50" align="center" />
          <!-- <el-table-column label="用户编号" align="center" prop="userId" /> -->
          <el-table-column
            label="用户账号"
            align="center"
            prop="userName"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="用户姓名"
            align="center"
            prop="nickName"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="部门"
            align="center"
            prop="deptName"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="手机号码"
            align="center"
            prop="phonenumber"
            width="120"
          />
          <el-table-column label="创建时间" align="center" prop="createTime" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {
  addUser,
  changeUserStatus,
  delUser,
  exportUser,
  getUser,
  importTemplate,
  inspectorlist,
  resetUserPwd,
  updateUser,
} from "@/platform/api/system/user";
import { getToken } from "@/platform/utils/auth";
import { treeselect } from "@/platform/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import PwdChecker from "../../../../utils/pwd_checker.js";
import formhelper from "../../../../utils/formhelper.js";
import ruoyi_page_click from "@/platform/views/__common/ruoyi_page_click.js";

export default {
  name: "User",
  components: { Treeselect },
  mixins: [ruoyi_page_click],
  data() {
    let permMap = this.$perm.checkArrayToPermMap([
      { type: "add", permi: ["system:user:add"] },
      { type: "update", permi: ["system:user:edit"] },
      { type: "delete", permi: ["system:user:edit"] },
      { type: "import", permi: ["system:user:import"] },
      { type: "export", permi: ["system:user:export"] },
    ]);
    return {
      // 遮罩层
      loading: false,
      formTools: Object.keys(permMap).join(","),
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户表格数据
      userList: null,
      // 弹出层标题
      title: "",
      // 部门树选项
      deptOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 部门名称
      deptName: undefined,
      // 默认密码
      initPassword: undefined,
      // 日期范围
      dateRange: [],
      // 状态数据字典
      statusOptions: [],
      // 性别状态字典
      sexOptions: [],
      // 岗位选项
      postOptions: [],
      // 角色选项
      roleOptions: [],
      // 表单参数
      form: {},
      defaultProps: {
        children: "children",
        label: "label",
      },
      // 用户导入参数
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/system/user/importData",
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        status: "0",
        deptId: undefined,
      },
      // 表单校验
      rules: {
        userName: this.$rules.len(true, 5, 30, "用户账号5-30个字符"),
        //   [
        //   {required: true, message: "用户账号不能为空", trigger: "blur"},
        // ],
        nickName: [{ required: true, message: "用户姓名不能为空", trigger: "blur" }],
        deptId: [{ required: true, message: "归属部门不能为空", trigger: "blur" }],
        password: PwdChecker.autoCheckPwdRules(this),
        email: [
          { required: true, message: "邮箱地址不能为空", trigger: "blur" },
          {
            type: "email",
            message: "'请输入正确的邮箱地址",
            trigger: ["blur", "change"],
          },
        ],
        phonenumber: [
          { required: true, message: "手机号码不能为空", trigger: "blur" },
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur",
          },
        ],
      },
    };
  },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val);
    },
  },
  created() {
    //   this.getList();
    this.getDicts("sys_normal_disable").then((response) => {
      this.statusOptions = response.data;
    });
    this.getTreeselect();
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      inspectorlist(
        this.queryParams.deptId,
        this.addDateRange(this.queryParams, this.dateRange)
      ).then((response) => {
        this.userList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询部门下拉树结构 */
    getTreeselect() {
      treeselect().then((response) => {
        this.deptOptions = response.data;
        this.queryParams.deptId = this.deptOptions[0].id;
        this.getList();
      });
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;

      return data.label.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.deptId = data.id;
      this.getList();
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        userId: undefined,
        deptId: undefined,
        userName: undefined,
        nickName: undefined,
        password: undefined,
        phonenumber: undefined,
        email: undefined,
        sex: undefined,
        status: "0",
        remark: undefined,
        postIds: [],
        roleIds: [],
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.page = 1;
      // 判断是否已经选择类别
      if (!this.queryParams.deptId) {
        this.msgInfo("请选择一个部门后再操作");
        return false;
      } else {
        this.getList();
      }
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.userId);
      this.single = selection.length != 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.getTreeselect();
      getUser().then((response) => {
        this.postOptions = response.posts;
        this.roleOptions = response.roles;
        this.open = true;
        this.title = "添加用户";
        this.form.password = this.initPassword;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      const userId = row.userId || this.ids;
      getUser(userId).then((response) => {
        this.form = response.data;
        this.postOptions = response.posts;
        this.roleOptions = response.roles;
        this.form.postIds = response.postIds;
        this.form.roleIds = response.roleIds;
        this.open = true;
        this.title = "修改用户";
        this.form.password = "";
      });
    },
    checkPwdByRules(value) {
      if (value === null || value === undefined || value === "") {
        return "密码不能为空";
      }
      //获取系统配置。根据
      let pwdRuleConfig = this.$store.state.settings.sysConfigs["pwd.security.rules"];
      let pwdRule = pwdRuleConfig && pwdRuleConfig.configValue;
      if (!pwdRule) {
        return;
      }
      let rs = PwdChecker.checkPwd(pwdRule, value);
      return rs.ok ? null : rs.msg;
    },
    /** 重置密码按钮操作 */
    handleResetPwd(row) {
      this.$dialog.show({
        title: "重置密码",
        width: "400px",
        component: "DataEditForm",
        propsData: {
          edit: formhelper.create(
            [formhelper.itemCreator.input("输入密码", "pwd", null, this.rules.password)],
            false
          ),
        },
        onCloseFn: (type, dialog, next) => {
          if (type !== "ok") {
            next();
            return;
          }
          dialog.$component.validate((model) => {
            dialog.okBtnLoading = true;
            resetUserPwd(row.userId, model.pwd)
              .then((response) => {
                if (response.code === 200) {
                  this.msgSuccess("修改成功");
                  next();
                }
              })
              .finally(() => {
                dialog.okBtnLoading = false;
              });
          });
        },
      });
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.userId != undefined) {
            updateUser(this.form).then((response) => {
              if (response.code === 200) {
                this.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }
            });
          } else {
            addUser(this.form).then((response) => {
              if (response.code === 200) {
                this.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const userIds = row.userId || this.ids;
      this.$confirm(
        `是否确认删除${this.ids.length > 1 ? "这些" : "这条"}数据项?`,
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(function () {
          return delUser(userIds);
        })
        .then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
        .catch(function () {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm("是否确认导出所有用户数据项?", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(function () {
          return exportUser(queryParams);
        })
        .then((response) => {
          this.download(response.msg);
        })
        .catch(function () {});
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "用户导入";
      this.upload.open = true;
    },
    /** 下载模板操作 */
    importTemplate() {
      importTemplate().then((response) => {
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
      this.$alert(response.msg, "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
  },
};
</script>
<style scoped>
.main-row,
.main-row-right {
  height: 100%;
}

.main-row-right {
  overflow: auto;
}

.theme-pannel {
  padding: 0 !important;
  height: 100%;
}

.theme-pannel__body {
  padding: 10px;
  box-sizing: border-box;
  height: calc(100% - 30px);
  overflow: auto;
}

.app-container {
  padding-left: 20px;
}
</style>
