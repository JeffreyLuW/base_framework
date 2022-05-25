<template>
  <div class="app-container">
    <el-row :gutter="20" class="main-row">
      <!--时段类别-->
      <el-col :span="5" :xs="24" class="theme-pannel">
        <div class="theme-pannel__title">时段类别</div>
        <div class="theme-pannel__body">
          <div class="head-container">
            <el-input
              v-model="typeName"
              placeholder="请输入时段类型"
              clearable
              size="small"
              prefix-icon="el-icon-search"
              style="margin-bottom: 20px"
            />
          </div>
          <div class="head-container">
            <el-tree
              :data="timeTypeOptions"
              :props="defaultProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              ref="timeType"
              node-key="id"
              default-expand-all
              :highlight-current="true"
              @node-click="handleNodeClick"
            >
              <template slot="default" slot-scope="{ node, data }">
                <span class="custom-tree-node">
                  <span>
                    <i
                      v-if="data.id == '0'"
                      class="el-icon-folder icon-tree-btn"
                    ></i>
                    <span>{{ node.label }}</span>
                    <!-- 这里需要判断是否是文件夹，否则不能添加。 -->
                    <i
                      class="el-icon-plus icon-tree-btn"
                      v-if="data.id == '0'"
                      @click.stop.prevent="handleClickNodeAdd(data)"
                    ></i>
                    <!-- 这里需要判断是否是根节点，否则不能删除、修改。 -->
                    <i
                      class="el-icon-edit icon-tree-btn"
                      v-if="data.id != '0'"
                      @click.stop.prevent="handleClickNodeedit(data, node)"
                    ></i>
                    <i
                      class="el-icon-delete icon-tree-btn"
                      v-if="data.id != '0'"
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
        <el-form
          :model="queryParams"
          ref="queryForm"
          class="query-form"
          :inline="true"
          label-width="70px"
        >
          <el-form-item label="时间范围">
            <el-time-picker
              is-range
              v-model="dateRange"
              range-separator="至"
              value-format="HH:mm"
              format="HH:mm"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              placeholder="选择时间范围"
              style="width: 250px"
            >
            </el-time-picker>
          </el-form-item>
          <el-form-item label="是否跨天" prop="acrossToday">
            <el-select
              v-model="queryParams.acrossToday"
              placeholder="请选择是否跨天"
              clearable
              size="small"
              style="width: 200px"
            >
              <el-option
                v-for="dict in statusOptions"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
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
        <FormToolsRow :buttons="formTools" @click="onClickOps"></FormToolsRow>

        <el-table
          v-loading="loading"
          :data="timeInfoData"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="开始时间" align="center" prop="begenTime" />
          <el-table-column
            label="结束时间"
            align="center"
            prop="endTime"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="是否跨天"
            align="center"
            prop="acrossToday"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              {{ scope.row.acrossToday == 1 ? "是" : "否" }}
            </template>
          </el-table-column>
          <el-table-column
            label="顺序号"
            align="center"
            prop="orderNum"
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
          @pagination="timeInfoList"
        />
      </el-col>
    </el-row>

    <!-- 属性表单 -->
    <AutoDialog :title="title" :show.sync="open" width="600px">
      <el-form ref="form" :model="form" :rules="rules">
        <table class="theme-table-form">
          <colgroup>
            <col width="100" />
            <col />
            <col width="100" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(rules.timeRange)">时间范围</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="时间范围" prop="timeRange">
                <el-time-picker
                  is-range
                  v-model="timeRange"
                  range-separator="至"
                  value-format="HH:mm"
                  format="HH:mm"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  placeholder="选择时间范围"
                  @change="changeClick"
                >
                </el-time-picker>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.orderNum)">顺序号</td>
            <td class="theme-table-form__input">
              <el-form-item label="顺序号" prop="orderNum">
                <el-input-number
                  v-model="form.orderNum"
                  controls-position="right"
                  :min="0"
                />
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(rules.acrossToday)">是否跨天</td>
            <td class="theme-table-form__input">
              <el-form-item label="是否跨天" prop="acrossToday">
                <el-radio v-model="form.acrossToday" :label="'1'">是</el-radio>
                <el-radio v-model="form.acrossToday" :label="'0'">否</el-radio>
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

    <!-- 用户导入对话框 -->
    <AutoDialog
      :title="timeTypeDialog.title"
      :show.sync="timeTypeDialog.open"
      width="400px"
    >
      <el-form ref="timeTypeForm" :model="timeTypeForm" :rules="timeTypeRules">
        <table class="theme-table-form">
          <colgroup>
            <col width="100" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(timeTypeRules.typeName)">
              时段类型名称
            </td>
            <td class="theme-table-form__input">
              <el-form-item label="时段类型名称" prop="typeName">
                <el-input
                  v-model="timeTypeForm.typeName"
                  placeholder="请输入时段类型名称"
                />
              </el-form-item>
            </td>
          </tr>
        </table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitTimeTypeForm">确 定</el-button>
        <el-button @click="timeTypeDialog.open = false">取 消</el-button>
      </div>
    </AutoDialog>
  </div>
</template>

<script>
import {
  getTimeTypeDict,
  addTimeType,
  getTimeTypeDetail,
  updateTimeType,
  delTimeType,
  timeInfoList,
  timeInfoAdd,
  timeInfoUpdate,
  timeInfoDel,
  timeInfoDetail
} from "@/platform/api/engineeringIO/CheckSetting/timeDefine";
import ValidateUtil from "@/platform/utils/ValidateUtil.js";

export default {
  name: "timeDefine",
  data() {
    let permMap = this.$perm.checkArrayToPermMap([
      { type: "add", permi: ["inspection:time:info:add"] },
      { type: "update", permi: ["inspection:time:info:edit"] },
      { type: "delete", permi: ["inspection:time:info:remove"] }
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
      // 时段信息
      timeInfoData: null,
      // 弹出层标题
      title: "",
      // 类型树选项
      timeTypeOptions: undefined,
      // 是否显示弹出层
      open: false,
      // 时段类别名称
      typeName: undefined,
      // 默认密码
      initPassword: undefined,
      // 日期范围
      dateRange: "",
      statusOptions: [
        { label: "是", value: 1 },
        { label: "否", value: 0 }
      ],
      // 角色选项
      roleOptions: [],
      normalizer(node) {
        return {
          id: node.id,
          label: node.clazzName,
          children: node.childs
        };
      },
      // 表单参数
      form: {},
      // 时间范围
      timeRange: [],
      // timeType form
      timeTypeForm: {
        clazzCode: "",
        clazzName: "",
        orderNum: 0,
        parentId: 0
      },
      defaultProps: {
        children: "childs",
        label: "typeName"
      },
      // 对象类型
      timeTypeDialog: {
        // 是否显示弹出层（对象类型）
        open: false,
        // 弹出层标题（对象类型）
        title: ""
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        acrossToday: undefined,
        typeId: undefined
      },
      // 表单校验
      rules: {
        attributeCode: ValidateUtil.required(),
        attributeName: ValidateUtil.required(),
        attributeUnit: ValidateUtil.required(),
        baseinfo: ValidateUtil.required(),
        control: ValidateUtil.required(),
        acrossToday: ValidateUtil.required(),
        map: ValidateUtil.required(),
        necessary: ValidateUtil.required(),
        picture: ValidateUtil.required(),
        video: ValidateUtil.required(),
        length: ValidateUtil.norequiredInt()
      },
      // tree 表单验证
      timeTypeRules: {
        clazzCode: [
          {
            required: true,
            message: "类型编码不能为空",
            trigger: ["blur", "change"]
          }
        ],
        clazzName: [
          {
            required: true,
            message: "类型名称不能为空",
            trigger: ["blur", "change"]
          }
        ],
        dir: [
          {
            required: true,
            message: "对象类型不能为空",
            trigger: ["blur", "change"]
          }
        ]
      }
    };
  },
  watch: {
    // 根据名称筛选
    typeName(val) {
      this.$refs.timeType.filter(val);
    }
  },
  created() {
    this.timeInfoList();
    this.getTimeTypeselect();
  },
  methods: {
    /** 查询用户列表 */
    timeInfoList() {
      this.loading = true;
      timeInfoList(this.addDateRange(this.queryParams, this.dateRange)).then(
        response => {
          this.timeInfoData = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 查询类型树结构 */
    getTimeTypeselect() {
      getTimeTypeDict().then(response => {
        // 添加一个顶级目录
        let obj = [
          {
            childs: response.data,
            typeName: "时段类型",
            id: 0
          }
        ];
        this.timeTypeOptions = obj;
      });
    },
    // 筛选节点
    filterNode(value, data) {
      console.log(data);
      if (!value) return true;
      return data.typeName.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryParams.typeId = "";
      if (data.id != "0") {
        this.queryParams.typeId = data.id;
        this.timeInfoList();
      }
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 属性表单重置
    reset() {
      this.form = {
        acrossToday: "",
        begenTime: "",
        endTime: "",
        orderNum: 0,
        typeId: ""
      };
      this.timeRange = "";
      this.resetForm("form");
    },
    // 时间分离
    changeClick(v) {
      this.form.begenTime = v[0];
      this.form.endTime = v[1];
    },
    // 类型表单重置
    treeReset() {
      this.timeTypeForm = {
        typeName: ""
      };
      this.resetForm("timeTypeForm");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.timeInfoList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length != 1;
      this.multiple = !selection.length;
    },
    // tree 增删改查
    handleClickNodeAdd(row) {
      this.treeReset();
      getTimeTypeDetail(row.id).then(res => {
        console.log(res);
        this.timeTypeDialog.open = true;
        this.timeTypeDialog.type = "add";
        this.timeTypeForm.parentId = row.id;
        this.timeTypeForm.parentName = row.clazzName;
      });
    },
    handleClickNodeedit(row, node) {
      let parent = node.parent.data;
      this.treeReset();
      this.timeTypeDialog.open = true;
      this.timeTypeDialog.type = "update";
      this.timeTypeForm = JSON.parse(JSON.stringify(row));

      this.timeTypeForm.parentName = parent.clazzName;
    },
    handleClickNodeDel(row) {
      const _this = this;
      this.$confirm(
        '是否确认删除时段类型为"' + row.typeName + '"的数据项?',
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(function() {
          delTimeType(row.id).then(response => {
            if (response.code === 200) {
              _this.msgSuccess("删除成功");
              _this.timeTypeDialog.open = false;
              _this.getTimeTypeselect();
            }
          });
        })
        .catch(function() {});
    },
    submitTimeTypeForm() {
      this.$refs["timeTypeForm"].validate(valid => {
        if (valid) {
          if (this.timeTypeDialog.type == "update") {
            updateTimeType(this.timeTypeForm).then(response => {
              if (response.code === 200) {
                this.msgSuccess("修改成功");
                this.timeTypeDialog.open = false;
                this.getTimeTypeselect();
              }
            });
          } else {
            addTimeType(this.timeTypeForm).then(response => {
              if (response.code === 200) {
                this.msgSuccess("新增成功");
                this.timeTypeDialog.open = false;
                this.getTimeTypeselect();
              }
            });
          }
        }
      });
    },
    onClickOps(type, e) {
      switch (type) {
        case "add":
          // 判断是否已经选择类别
          if (!this.queryParams.typeId) {
            this.msgInfo("请选择一个类别后再操作");
            return false;
          } else {
            this.handleAdd();
          }
          break;
        case "update":
          if (!this.queryParams.typeId) {
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
          if (!this.queryParams.typeId) {
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
      this.form.typeId = this.queryParams.typeId;
      this.open = true;
    },
    /** 修改按钮操作 */
    handleUpdate(row = {}) {
      this.reset();
      const id = row.id || this.ids;
      timeInfoDetail(id).then(res => {
        console.log(res);
        this.form = res.data;
        this.timeRange = [this.form.begenTime, this.form.endTime];
        this.open = true;
      });
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            timeInfoUpdate(this.form).then(response => {
              if (response.code === 200) {
                this.msgSuccess("修改成功");
                this.open = false;
                this.timeInfoList();
              }
            });
          } else {
            timeInfoAdd(this.form).then(response => {
              if (response.code === 200) {
                this.msgSuccess("新增成功");
                this.open = false;
                this.timeInfoList();
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
          type: "warning"
        }
      )
        .then(() => {
          timeInfoDel(id).then(() => {
            this.timeInfoList();
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
