<template>
  <div class="app-container">
    <el-row :gutter="20" class="main-row">
      <!--对象类别-->
      <el-col :span="5" :xs="24" class="theme-pannel">
        <div class="theme-pannel__title">巡检点类别</div>
        <div class="theme-pannel__body">
          <div class="head-container">
            <el-input
              v-model="deptName"
              placeholder="请输入巡检点"
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
                    <i v-if="data.dir == '1'" class="el-icon-folder icon-tree-btn"></i>
                    <i
                      class="el-icon-folder icon-tree-btn"
                      v-else
                      style="visibility: hidden"
                    ></i>
                    <span>{{ node.label }}</span>
                  </span>
                </span>
              </template>
            </el-tree>
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
          label-width="80px"
        >
          <el-form-item label="巡检点名称" prop="objectName">
            <el-input
              v-model="queryParams.objectName"
              placeholder="请输入巡检点"
              clearable
              size="small"
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="巡检点编号" prop="objectCode">
            <el-input
              v-model="queryParams.objectCode"
              placeholder="请输入巡检点编号"
              clearable
              size="small"
              style="width: 240px"
              @keyup.enter.native="handleQuery"
            />
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
        <FormToolsRow
          :buttons="formTools"
          @click="onClickOps"
          :beExpanded="false"
        ></FormToolsRow>

        <el-table
          v-loading="loading"
          :data="objList"
          border
          @selection-change="handleSelectionChange"
          height="calc(100% - 150px)"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column
            label="巡检点名称"
            align="left"
            prop="objectName"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="巡检点编号"
            align="left"
            prop="objectCode"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="所在区域"
            align="center"
            prop="administrativeDivisionLabel"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="起点纬度"
            align="right"
            prop="startingPointLatitude"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="起点经度"
            align="right"
            prop="startingPointLongitude"
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
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                >修改
              </el-button>
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
          @pagination="objectList"
        />
      </el-col>
    </el-row>

    <!-- 属性表单 -->
    <AutoDialog :title="title" :show.sync="open" width="800px">
      <el-form ref="form" :model="form" :rules="rules">
        <table class="theme-table-form">
          <colgroup>
            <col width="140" />
            <col />
            <col width="140" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(rules.objectCode)">巡检点编号</td>
            <td class="theme-table-form__input">
              <el-form-item label="巡检点编号" prop="objectCode">
                <el-input v-model="form.objectCode" placeholder="请输入巡检点" />
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(rules.objectName)">巡检点名称</td>
            <td class="theme-table-form__input">
              <el-form-item label="巡检点名称" prop="objectName">
                <el-input v-model="form.objectName" placeholder="请输入巡检点名称" />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.cardCode)">卡编号</td>
            <td class="theme-table-form__input">
              <el-form-item label="卡编号" prop="cardCode">
                <el-input v-model="form.cardCode" placeholder="请输入卡编号" />
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(rules.cardId)">卡ID</td>
            <td class="theme-table-form__input">
              <el-form-item label="卡ID" prop="cardId">
                <el-input v-model="form.cardId" placeholder="请输入卡ID" />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.administrativeDivision)">行政区划</td>
            <td class="theme-table-form__input">
              <el-form-item label="行政区划" prop="administrativeDivision">
                <asyncCascader
                  @changeValue="handleFormChangeValue"
                  :regionName="regionName"
                  :value="'' + form.administrativeDivision"
                />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.startingPointLongitude)">起点经度</td>
            <td class="theme-table-form__input">
              <el-form-item label="起点经度" prop="startingPointLongitude">
                <el-input
                  v-model="form.startingPointLongitude"
                  placeholder="请输入起点经度"
                  @click.native="checkCoordinates(0)"
                />
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(rules.startingPointLatitude)">起点纬度</td>
            <td class="theme-table-form__input">
              <el-form-item label="起点纬度" prop="startingPointLatitude">
                <el-input
                  v-model="form.startingPointLatitude"
                  placeholder="请输入起点纬度"
                  @click.native="checkCoordinates(0)"
                />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.destinationLongitude)">迄点经度</td>
            <td class="theme-table-form__input">
              <el-form-item label="迄点经度" prop="destinationLongitude">
                <el-input
                  v-model="form.destinationLongitude"
                  placeholder="请输入迄点经度"
                  @click.native="checkCoordinates(1)"
                />
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(rules.destinationPointLatitude)">
              迄点纬度
            </td>
            <td class="theme-table-form__input">
              <el-form-item label="迄点纬度" prop="destinationPointLatitude">
                <el-input
                  v-model="form.destinationPointLatitude"
                  placeholder="请输入迄点纬度"
                  @click.native="checkCoordinates(1)"
                />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.clazzAttributeIds)">巡检类型属性</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="巡检类型属性" prop="clazzAttributeIds">
                <el-transfer
                  v-model="form.clazzAttributeIds"
                  :titles="['未选属性', '已选属性']"
                  :props="{
                    key: 'id',
                    label: 'attributeName',
                  }"
                  :data="transferData"
                ></el-transfer>
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

    <Position
      :positionVisible="positionVisible"
      @close="positionClose"
      @dblclick="dblclickHandle"
      :positionData="positionData"
      ref="position"
      @scriptReady="scriptReadyHandle"
    ></Position>
  </div>
</template>

<script>
import {
  getTree,
  clazzDict,
  getTreeDetail,
} from "@/platform/api/engineeringIO/CheckSetting/inspectionCate";
import {
  objectList,
  objectAdd,
  objectUpdate,
  objectDetail,
  objectDel,
  objectDict,
} from "@/platform/api/engineeringIO/CheckSetting/inspectionObj";
import Position from "@/platform/components2/Position/Position";
import AsyncCascader from "@/platform/components2/AsyncCascader";
import ValidateUtil from "@/platform/utils/ValidateUtil.js";

export default {
  name: "inspectionObj",
  data() {
    let permMap = this.$perm.checkArrayToPermMap([
      { type: "add", permi: ["inspection:object:add"] },
      { type: "update", permi: ["inspection:object:edit"] },
      { type: "delete", permi: ["inspection:object:remove"] },
    ]);
    return {
      // 遮罩层
      loading: false,
      formTools: Object.keys(permMap).join(","),
      // 选中数组
      ids: [],
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户表格数据
      objList: null,
      // 弹出层标题
      title: "",
      // 类型树选项
      treeOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 巡检属性
      transferData: [],
      // 部门名称
      deptName: undefined,
      // 表单参数
      form: {},
      defaultProps: {
        children: "childs",
        label: "clazzName",
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        objectName: undefined,
        objectCode: undefined,
        clazzId: undefined,
      },
      // 表单校验
      rules: {
        objectCode: ValidateUtil.required(),
        objectName: ValidateUtil.required(),
        administrativeDivision: ValidateUtil.required(),
        startingPointLongitude: ValidateUtil.required(),
        startingPointLatitude: ValidateUtil.required(),
        destinationLongitude: ValidateUtil.required(),
        necessary: ValidateUtil.required(),
        destinationPointLatitude: ValidateUtil.required(),
        video: ValidateUtil.required(),
        length: ValidateUtil.norequiredInt(),
      },
      // 定位
      positionVisible: false,
      positionData: [],
      // 点击坐标  0 始  1 迄
      coorType: "",
      regionName: "",
    };
  },
  components: { Position, AsyncCascader },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val);
    },
  },
  created() {
    this.objectList();
    this.getTreeselect();
  },
  methods: {
    handleFormChangeValue(value) {
      this.form.administrativeDivision = value;
    },
    checkCoordinates(type) {
      // 0 起  1 始
      this.coorType = type;
      this.positionVisible = true;
    },
    positionClose() {
      this.positionVisible = false;
    },
    //地图双击返回坐标
    dblclickHandle(clickCoordinates) {
      if (this.coorType === 0) {
        this.form.startingPointLongitude = clickCoordinates[0];
        this.form.startingPointLatitude = clickCoordinates[1];
      } else if (this.coorType === 1) {
        this.form.destinationLongitude = clickCoordinates[0];
        this.form.destinationPointLatitude = clickCoordinates[1];
      }
      this.positionVisible = false;
    },
    scriptReadyHandle() {},
    // 查巡检类型属性
    getClazzDict() {
      clazzDict(this.queryParams.clazzId).then((res) => {
        console.log(res);
        this.transferData = res.data;
      });
    },
    /** 查询用户列表 */
    objectList() {
      this.loading = true;
      objectList(this.queryParams).then((res) => {
        this.objList = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    /** 查询类型树结构 */
    getTreeselect() {
      getTree(0).then((res) => {
        // 添加一个顶级目录
        let obj = [
          {
            childs: res.data,
            clazzCode: "",
            clazzName: "巡检点",
            id: 0,
            dir: "1",
            orderNum: 0,
            parentId: "",
          },
        ];
        this.treeOptions = obj;
      });
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.clazzName.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      console.log(data);
      this.queryParams.clazzId = "";
      this.queryParams.clazzId = data.id;
      this.objectList();
      this.getClazzDict();
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 属性表单重置
    reset() {
      this.form = {
        administrativeDivision: "",
        cardCode: "",
        cardId: "",
        clazzAttributeIds: [],
        clazzId: 0,
        destinationLongitude: "",
        destinationPointLatitude: "",
        objectCode: "",
        objectName: "",
        startingPointLatitude: "",
        startingPointLongitude: "",
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.objectList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.regionName = selection.map((item) => item.administrativeDivisionLabel)[0];
    },
    onClickOps(type, e) {
      switch (type) {
        case "add":
          // 判断是否已经选择类别
          if (!this.queryParams.clazzId) {
            this.msgInfo("请选择一个类别后再操作");
            return false;
          } else {
            this.handleAdd();
          }
          break;
        case "update":
          // 判断是否已经选择类别
          if (!this.queryParams.clazzId) {
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
          if (!this.queryParams.clazzId) {
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.form.clazzId = this.queryParams.clazzId;
      this.open = true;
    },
    /** 修改按钮操作 */
    handleUpdate(row = {}) {
      this.reset();
      this.getTreeselect();
      const id = row.id || this.ids;
      if (row.administrativeDivisionLabel) {
        this.regionName = row.administrativeDivisionLabel;
      }
      objectDetail(id).then((res) => {
        this.form = res.data;
        this.open = true;
      });
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != undefined) {
            objectUpdate(this.form).then((res) => {
              if (res.code === 200) {
                this.msgSuccess("修改成功");
                this.open = false;
                this.objectList();
              }
            });
          } else {
            objectAdd(this.form).then((res) => {
              if (res.code === 200) {
                this.msgSuccess("新增成功");
                this.open = false;
                this.objectList();
              }
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row = {}) {
      const id = row.id || this.ids;
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
          objectDel(id).then(() => {
            this.objectList();
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
