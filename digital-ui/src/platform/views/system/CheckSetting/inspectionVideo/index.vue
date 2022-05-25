<template>
  <div class="app-container">
    <el-row :gutter="20" class="main-row">
      <!--对象类别-->
      <el-col :span="5" :xs="24" class="theme-pannel">
        <div class="theme-pannel__title">线路类别</div>
        <div class="theme-pannel__body">
          <div class="head-container">
            <el-input
              v-model="deptName"
              placeholder="请输入线路类型"
              clearable
              size="small"
              prefix-icon="el-icon-search"
              style="margin-bottom: 20px"
            />
          </div>
          <div class="head-container">
            <el-tree
              :data="treeOptions"
              :props="defaultProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="tree"
              node-key="id"
              default-expand-all
              :highlight-current="true"
              @node-click="handleNodeClick"
            >
              <template slot="default" slot-scope="{ node, data }">
                <span class="custom-tree-node">
                  <span>
                    <!-- <i
                      v-if="data.dir == '1'"
                      class="el-icon-folder icon-tree-btn"
                    ></i>
                    <i
                      class="el-icon-folder icon-tree-btn"
                      v-else
                      style="visibility: hidden"
                    ></i> -->
                    <span>{{ node.label }}</span>
                    <!-- 这里需要判断是否是文件夹，否则不能添加。 -->
                    <i
                      class="el-icon-plus icon-tree-btn"
                      v-if="node.level == 3"
                      @click.stop.prevent="handleClickNodeAdd(data)"
                    ></i>
                    <!-- 这里需要判断是否是根节点，否则不能删除、修改。 -->
                    <i
                      class="el-icon-edit icon-tree-btn"
                      v-if="node.level > 3"
                      @click.stop.prevent="handleClickNodeedit(data, node)"
                    ></i>
                    <i
                      class="el-icon-delete icon-tree-btn"
                      v-if="node.level > 3"
                      @click.stop.prevent="handleClickNodeDel(data)"
                    ></i>
                  </span>
                </span>
              </template>
            </el-tree>
          </div>
        </div>
      </el-col>
      <!--用户数据-->
      <el-col :span="19" :xs="24" class="main-row-right">
        <FormToolsRow :buttons="formTools" @click="onClickOps">
          <el-button
            slot="addroute"
            type="primary"
            size="mini"
            @click="addRoutes"
            >添加视频点</el-button
          >
        </FormToolsRow>

        <el-table
          v-loading="loading"
          :data="routeObjList"
          border
          @selection-change="handleSelectionChange"
          height="calc(100% - 140px)"
        >
          <el-table-column type="index" width="50" align="center" />
          <el-table-column
            label="视频点名称"
            align="center"
            :show-overflow-tooltip="true"
            prop="videoMonitoringName"
          />
          <el-table-column
            label="视频点编号"
            align="center"
            prop="videoAlarmIndex"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="所属部门"
            align="center"
            prop="deptName"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="所在区域"
            align="center"
            prop="installArea"
            :show-overflow-tooltip="true"
          />
        </el-table>

        <!-- <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="routeList"
        /> -->
      </el-col>
    </el-row>

    <!-- 属性表单 -->
    <AutoDialog :title="title" :show.sync="open" width="1500px">
      <el-form ref="form" :model="form" :rules="rules">
        <table class="theme-table-form">
          <colgroup>
            <col width="100" />
            <col />
          </colgroup>
          <!-- <tr>
            <td :class="themeTableLabelClass(rules.orderNum)">顺序号</td>
            <td class="theme-table-form__input">
              <el-form-item label="排序字段" prop="orderNum">
                <el-input-number
                  v-model="form.orderNum"
                  controls-position="right"
                  :min="0"
                />
              </el-form-item>
            </td>
          </tr> -->
          <tr v-if="mode == 'add'">
            <td :class="themeTableLabelClass(rules.objectId)">巡检点</td>
            <td class="theme-table-form__input">
              <el-form-item label="巡检点">
                <span>视频点名称:</span>
                <el-input style="width:200px" v-model="pointName" />
                <el-table
                  v-loading="loading"
                  :data="insObjList"
                  border
                  ref="multipleTable"
                  @select="insSelected"
                  @select-all="selectedAll"
                >
                  <el-table-column type="selection" width="50" align="center" />
                  <el-table-column
                    label="视频点名称"
                    align="center"
                    prop="videoMonitoringName"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    label="视频点编号"
                    align="center"
                    prop="videoAlarmIndex"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    label="所属部门"
                    align="center"
                    prop="deptName"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    label="所在区域"
                    align="center"
                    prop="installArea"
                    :show-overflow-tooltip="true"
                  />
                </el-table>
                <!-- <pagination
                  style="margin-bottom: 25px"
                  v-show="objTotal > 0"
                  :total="objTotal"
                  :page.sync="objParams.pageNum"
                  :limit.sync="objParams.pageSize"
                  @pagination="getInsObjList"
                /> -->
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

    <!-- 对话框 -->
    <AutoDialog
      :title="treeDialog.title"
      :show.sync="treeDialog.open"
      width="600px"
    >
      <el-form ref="treeForm" :model="treeForm" :rules="treeRules">
        <table class="theme-table-form">
          <colgroup>
            <col width="100" />
            <col />
            <col width="100" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass()">路线名称</td>
            <td class="theme-table-form__input">
              <el-form-item label="路线名称" prop="label">
                <el-input
                  v-model="treeForm.label"
                  placeholder="请输入路线名称"
                />
              </el-form-item>
            </td>

            <td :class="themeTableLabelClass(treeRules.orderNum)">排序字段</td>
            <td class="theme-table-form__input">
              <el-form-item label="排序字段" prop="orderNum">
                <el-input-number
                  v-model="treeForm.orderNum"
                  controls-position="right"
                  :min="0"
                />
              </el-form-item>
            </td>
          </tr>
        </table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitTreeForm">确 定</el-button>
        <el-button @click="treeDialog.open = false">取 消</el-button>
      </div>
    </AutoDialog>
  </div>
</template>

<script>
import {
  getRoute,
  addRoute,
  getRouteDetail,
  updateRoute,
  delRoute,
  routeList,
  routeObjAdd,
  routeObjUpdate,
  getRouteObjDetail,
  routeObjDel
} from "@/platform/api/engineeringIO/CheckSetting/inspectionLine";
import {
  objectList,
  saveVideo,
  getConnectVideo,
  getTree,
  addLine,
  deleteLine,
  getLineDetail,
  updateLine
} from "@/platform/api/engineeringIO/CheckSetting/inspectionVideo";
import { getCycleDict } from "@/platform/api/engineeringIO/CheckSetting/pollingPeriod";
import ValidateUtil from "@/platform/utils/ValidateUtil.js";
import { treeselect } from "@/platform/api/system/dept";

export default {
  name: "inspectionLine",
  data() {
    let permMap = this.$perm.checkArrayToPermMap([
      { type: "addroute", permi: ["inspection:route:add"] }
      // { type: "edit", permi: ["inspection:route:edit"] },
      // { type: "delete", permi: ["inspection:route:remove"] }
    ]);
    return {
      pointName: "",
      // 遮罩层
      loading: false,
      formTools: Object.keys(permMap).join(","),
      // 选中数组
      ids: [],
      contactedIds: [],
      oldSelection: [],
      row: [],
      idsObj: [],
      // 总条数
      total: 0,
      // 巡检表格数据
      routeObjList: null,
      // 查找巡查点
      insObjList: null,
      insObjList2: null,
      // 弹出层标题
      title: "",
      // 类型树选项
      treeOptions: undefined,
      // 是否显示弹出层
      open: false,
      mode: "add",
      // 部门名称
      deptName: undefined,
      // 日期范围
      dateRange: [],
      // 巡检周期
      cycleOptions: [],
      // 角色选项
      roleOptions: [],
      normalizer(node) {
        return {
          id: node.id,
          label: node.routeName,
          children: node.childs
        };
      },
      // 表单参数
      form: {},
      // tree form
      treeForm: {
        deptId: "",
        label: "",
        orderNum: 0
      },
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 线路类型
      treeDialog: {
        // 是否显示弹出层（线路类型）
        open: false,
        // 弹出层标题（线路类型）
        title: ""
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        objectName: undefined,
        routeId: undefined
      },
      objParams: { pageNum: 1, pageSize: 10 },
      objTotal: 0,
      // 表单校验
      rules: {
        orderNum: ValidateUtil.required()
      },
      // tree 表单验证
      treeRules: {
        routeCode: [
          {
            required: true,
            message: "类型编码不能为空",
            trigger: ["blur", "change"]
          }
        ],
        label: [
          {
            required: true,
            message: "类型名称不能为空",
            trigger: ["blur", "change"]
          }
        ],
        dir: [
          {
            required: true,
            message: "线路类型不能为空",
            trigger: ["blur", "change"]
          }
        ],
        cycleId: ValidateUtil.required()
      }
    };
  },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val);
    },
    pointName(val) {
      let a = this.fuzzyQuery(this.insObjList, val);
      this.insObjList = a;
      this.$nextTick(() => {
        this.contactedIds.forEach(id => {
          this.insObjList.find(item => {
            if (item.id == id) {
              this.$refs.multipleTable.toggleRowSelection(item, true);
            }
          });
        });
      });
      if (val == "") {
        this.insObjList = this.insObjList2;
      }
    }
  },
  created() {
    this.getCycleDict();
    this.getTreeselect();
  },
  methods: {
    fuzzyQuery(list, keyWord) {
      var arr = [];
      for (var i = 0; i < list.length; i++) {
        if (list[i].videoMonitoringName.indexOf(keyWord) >= 0) {
          arr.push(list[i]);
        }
      }
      return arr;
    },

    //树形数组添加层级标识
    arrayTreeSetLevel(array, levelName = "level", childrenName = "children") {
      if (!Array.isArray(array)) return [];
      const recursive = (array, level = 0) => {
        level++;
        return array.map(v => {
          v[levelName] = level;
          const child = v[childrenName];
          if (child && child.length) recursive(child, level);
          return v;
        });
      };
      return recursive(array);
    },

    /** 查询用户列表 */
    routeList() {
      this.loading = true;
      routeList(this.queryParams).then(res => {
        this.routeObjList = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    /** 查询巡检周期 */
    getCycleDict() {
      getCycleDict().then(res => {
        this.cycleOptions = res.data;
      });
    },
    /** 查询类型树结构 */
    getTreeselect() {
      getTree().then(res => {
        let a = this.arrayTreeSetLevel(res.data);
        console.log(a);
        this.treeOptions = a;
      });
    },

    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.routeName.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      console.log(data);
      this.queryParams.routeId = data.id;
      this.queryParams.level = data.level;
      getConnectVideo({ insepectId: this.queryParams.routeId }).then(res => {
        this.routeObjList = res.data;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 属性表单重置
    reset() {
      this.form = {
        objectId: undefined,
        orderNum: 0,
        routeId: undefined
      };
      this.mode = "";
      this.resetForm("form");
    },
    // 类型表单重置
    treeReset() {
      this.treeForm = {
        deptId: "",
        label: "",
        orderNum: 0
      };
      this.resetForm("treeForm");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.routeList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.objectId);
      this.row = selection;
    },
    insSelected(selection, row) {
      let a = this.contactedIds.indexOf(row.id);
      if (a == -1) {
        this.contactedIds.push(row.id);
      } else {
        this.contactedIds.splice(a, 1);
      }
    },
    //全选
    selectedAll(selection) {
      if (selection.length == 0) {
        this.oldSelection.forEach(item => {
          let b = this.contactedIds.indexOf(item.id);
          this.contactedIds.splice(b, 1);
        });
      }
      selection.forEach(item => {
        let a = this.contactedIds.indexOf(item.id);
        if (a == -1) {
          this.contactedIds.push(item.id);
        }
      });
      this.oldSelection = selection;
    },
    // tree 增删改查
    handleClickNodeAdd(row) {
      this.treeReset();
      this.treeDialog.open = true;
      this.treeDialog.type = "add";
      this.treeForm.deptId = row.id;
    },
    handleClickNodeedit(row, node) {
      this.treeReset();
      getLineDetail({ id: row.id }).then(res => {
        this.treeDialog.open = true;
        this.treeDialog.type = "update";
        this.treeForm = res.data;
      });
    },
    handleClickNodeDel(row) {
      this.$confirm(`是否确认删除该路线`, "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteLine({ id: row.id }).then(res => {
          if (res.code === 200) {
            this.msgSuccess("删除成功");
            this.treeDialog.open = false;
            this.getTreeselect();
          }
        });
      });
    },
    submitTreeForm() {
      this.$refs["treeForm"].validate(valid => {
        if (valid) {
          if (this.treeDialog.type == "update") {
            updateLine(this.treeForm).then(res => {
              if (res.code === 200) {
                this.msgSuccess("修改成功");
                this.treeDialog.open = false;
                this.getTreeselect();
              }
            });
          } else {
            addLine(this.treeForm).then(res => {
              if (res.code === 200) {
                this.msgSuccess("新增成功");
                this.treeDialog.open = false;
                this.getTreeselect();
              }
            });
          }
        }
      });
    },

    onClickOps(type, e) {
      console.log(type);
      switch (type) {
        case "edit":
          // 判断是否已经选择类别
          if (!this.queryParams.routeId) {
            this.msgInfo("请选择一个类别后再操作");
            return false;
          }
          if (!this.ids || !this.ids.length) {
            this.msgInfo("请选择一行后再操作");
            return;
          }
          if (this.ids.length !== 1) {
            this.msgInfo("只能选择一行");
            return;
          }
          this.handleUpdate();
          break;
        case "delete":
          if (!this.queryParams.routeId) {
            this.msgInfo("请选择一个类别后再操作");
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

    addRoutes(type) {
      // 判断是否已经选择类别
      if (!this.queryParams.routeId || this.queryParams.level !== 4) {
        this.msgInfo("请选择一条路线后再操作");
        return false;
      } else {
        this.handleAdd();
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.mode = "add";
      this.getInsObjList();
    },
    // 巡查点列表
    getInsObjList() {
      objectList().then(res => {
        this.insObjList = res.data;
        this.insObjList2 = res.data;
        // this.objTotal = res.total;
        this.getConnectVideo();
      });
    },
    //获取巡检路线关联的视频点
    getConnectVideo() {
      getConnectVideo({ insepectId: this.queryParams.routeId }).then(res => {
        //这里如果用res.data直接循环赋值的话会无法选中，必须在数据源中找
        let ids = [];
        res.data.forEach(row => {
          ids.push(row.id);
        });
        this.contactedIds = ids;
        this.$nextTick(() => {
          ids.forEach(id => {
            this.insObjList.find(item => {
              if (item.id == id) {
                this.$refs.multipleTable.toggleRowSelection(item, true);
              }
            });
          });
        });
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row = {}) {
      this.reset();
      const id = row.objectId || this.ids;
      this.form = this.row.length > 0 ? this.row[0] : row;
      this.mode = "edit";
      this.open = true;
    },
    /** 提交按钮 */
    submitForm: function() {
      saveVideo({
        inspectId: this.queryParams.routeId,
        videoId: this.contactedIds
      }).then(res => {
        this.$message.success("关联成功");
        this.pointName = "";
        this.open = false;
        getConnectVideo({ insepectId: this.queryParams.routeId }).then(res => {
          this.routeObjList = res.data;
        });
      });
    },
    /** 删除按钮操作 */
    handleDelete(row = {}) {
      const ids = row.objectId || this.ids;
      this.$confirm(
        `是否确认删除${this.ids.length > 1 ? "这些" : "这条"}数据项?`,
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          routeObjDel(this.queryParams.routeId, ids).then(() => {
            this.routeList();
            this.msgSuccess("删除成功");
          });
        })
        .catch(function() {});
    }
  }
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
