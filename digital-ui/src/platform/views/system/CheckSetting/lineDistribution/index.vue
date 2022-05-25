<template>
  <div class="app-container">
    <el-row :gutter="20" class="main-row">
      <!--部门-->
      <el-col :span="5" :xs="24" class="theme-pannel">
        <div class="theme-pannel__title">部门</div>
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
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="tree"
              node-key="id"
              default-expand-all
              :highlight-current="true"
              @node-click="handleNodeClick"
            >
            </el-tree>
          </div>
        </div>
      </el-col>
      <!--用户数据-->
      <el-col :span="19" :xs="24" class="main-row-right">
        <el-form
          :model="queryParams"
          lineDistribution
          ref="queryForm"
          class="query-form"
          :inline="true"
          label-width="40px"
        >
          <el-form-item label="路线" prop="userName">
            <el-select
              v-model="queryParams.routeId"
              placeholder="请选择"
              @change="handleQuery"
            >
              <el-option
                v-for="item in routeOptions"
                :key="item.id"
                :label="item.routeName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
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
        <FormToolsRow :buttons="formTools" @click="onClickOps"></FormToolsRow>

        <el-table
          v-loading="loading"
          :data="lineDptList"
          border
          @selection-change="handleSelectionChange"
          height="calc(100% - 140px)"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="部门" align="center" prop="deptName" />
          <el-table-column
            label="路线"
            align="center"
            prop="routeName"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="周期"
            align="center"
            prop="cycleName"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="时段"
            align="center"
            prop="timeInfo"
            :show-overflow-tooltip="true"
          />

          <el-table-column
            label="操作"
            align="center"
            width="180"
            class-name="small-padding fixed-width"
          >
            <template slot-scope="scope">
              <el-button
                v-if="scope.row.userId !== 1"
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                >删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="lineList"
        />
      </el-col>
    </el-row>

    <!-- 属性表单 -->
    <AutoDialog :title="title" :show.sync="open" width="400px">
      <el-form ref="form" :model="form" :rules="rules">
        <table class="theme-table-form">
          <colgroup>
            <col width="140" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(rules.deptId)">部门</td>
            <td class="theme-table-form__input">
              <el-form-item label="归属部门" prop="deptId">
                <!-- <treeselect
                                    v-model="form.deptId"
                                    :options="deptOptions"
                                    :disable-branch-nodes="false"
                                    :show-count="true"
                                    placeholder="请选择部门"
                                    :maxHeight="200"
                                /> -->
                <span>{{ currentDeptName }}</span>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.routeId)">路线</td>
            <td class="theme-table-form__input">
              <el-form-item label="路线" prop="routeId">
                <el-select
                  v-model="form.routeId"
                  placeholder="请选择"
                  @change="handleQuery"
                >
                  <el-option
                    v-for="item in routeOptions"
                    :key="item.id"
                    :label="item.routeName"
                    :value="item.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </td>
          </tr>
        </table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </AutoDialog>
  </div>
</template>

<script>
import {
  deptRouteList,
  deptRouteAdd,
  deptRouteDict,
  deptRouteDel,
} from "@/platform/api/engineeringIO/CheckSetting/lineDistribution";
import { routeList } from "@/platform/api/engineeringIO/CheckSetting/inspectionLine";
import { treeselect } from "@/platform/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import ValidateUtil from "@/platform/utils/ValidateUtil.js";

export default {
  name: "inspectionObj",
  data() {
    let permMap = this.$perm.checkArrayToPermMap([
      { type: "add", permi: ["inspection:dept:route:add"] },
      { type: "delete", permi: ["inspection:dept:route:remove"] },
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
      // 表格数据
      lineDptList: null,
      // 弹出层标题
      title: "",
      // 部门选项
      deptOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 部门名称
      deptName: undefined,
      currentDeptName: undefined,
      // 日期范围
      dateRange: [],
      // 路线字典
      routeOptions: [],
      // 性别状态字典
      sexOptions: [],
      // 属性选项
      attrOptions: [],
      // 表单参数
      form: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        status: undefined,
        deptId: undefined,
        routeId: undefined,
      },
      // 表单校验
      rules: {
        deptId: ValidateUtil.required(),
        routeId: ValidateUtil.required(),
      },
    };
  },
  components: { Treeselect },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val);
    },
  },
  created() {
    this.deptRouteDict();
    this.getTreeselect();
  },
  methods: {
    /** 查询列表 */
    lineList() {
      this.loading = true;
      deptRouteList(this.queryParams).then((res) => {
        this.lineDptList = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    deptRouteDict() {
      deptRouteDict().then((res) => {
        this.routeOptions = res.data;
      });
    },
    /** 查询类型树结构 */
    getTreeselect() {
      treeselect().then((res) => {
        this.deptOptions = res.data;
      });
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      console.log("123", data);
      this.queryParams.deptId = data.id;
      this.currentDeptName = data.label;
      this.lineList();
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 属性表单重置
    reset() {
      this.form = {
        deptId: undefined,
        routeId: undefined,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.lineList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.routeId);
    },
    onClickOps(type, e) {
      switch (type) {
        case "add":
          // 判断是否已经选择部门
          if (!this.queryParams.deptId) {
            this.msgInfo("请选择一个部门后再操作");
            return false;
          } else {
            this.handleAdd();
          }
          break;
        case "delete":
          if (!this.queryParams.deptId) {
            this.msgInfo("请选择一个部门后再操作");
            return false;
          }
          if (!this.ids || !this.ids.length) {
            this.msgInfo("请最少选择一行后再操作");
            return;
          }
          this.handleDelete();
          break;
        case "refresh":
          this.resetQuery();
          break;
        default:
          break;
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.form.deptId = this.queryParams.deptId;
      this.open = true;
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          deptRouteAdd(this.form).then((res) => {
            if (res.code === 200) {
              this.msgSuccess("新增成功");
              this.open = false;
              this.lineList();
            }
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row = {}) {
      const ids = row.routeId || this.ids;
      this.$confirm(
        `是否确认删除${this.ids.length > 1 ? "这些" : "这条"}数据项?`,
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          deptRouteDel(this.queryParams.deptId, ids).then(() => {
            console.log(3232);
            this.lineList();
            this.msgSuccess("删除成功");
          });
        })
        .catch(function () {});
    },
  },
};
</script>
<style scoped>
.icon-tree-btn {
  margin: 0 5px;
}
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
