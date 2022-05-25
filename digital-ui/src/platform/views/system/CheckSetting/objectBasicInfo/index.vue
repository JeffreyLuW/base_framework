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
              placeholder="请输入巡检点类别"
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
          label-width="90px"
        >
          <el-form-item label="巡检点名称" prop="objectName">
            <el-input
              v-model="queryParams.objectName"
              placeholder="请输入巡检点名称"
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
        <FormToolsRow :buttons="formTools" @click="onClickOps">
          <el-button slot="addInfo" type="primary" size="mini" @click="addInfo"
            >添加基本信息</el-button
          >
        </FormToolsRow>

        <el-table
          v-loading="loading"
          :data="objList"
          border
          @selection-change="handleSelectionChange"
          height="calc(100% - 140px)"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column
            label="巡检点名称"
            align="center"
            prop="objectName"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="巡检点编号"
            align="center"
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
            align="center"
            prop="startingPointLatitude"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            label="起点经度"
            align="center"
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
                >添加基本信息
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
            <col min-width="260" />
            <col />
          </colgroup>
          <tr v-for="(item, index) in infoData" :key="index">
            <td :class="themeTableLabelClass(rules.objectCode)">
              {{ item.clazzAttribute.attributeName }}
            </td>
            <td class="theme-table-form__input">
              <el-form-item prop="baseinfoValue">
                <el-input
                  v-model="form[item.clazzAttribute.id].baseinfoValue"
                  v-if="item.clazzAttribute.type == 'text'"
                  placeholder="请输入文本"
                />
                <el-input-number
                  v-model="form[item.clazzAttribute.id].baseinfoValue"
                  :min="item.clazzAttribute.typeConfig.min"
                  :max="item.clazzAttribute.typeConfig.max"
                  v-if="item.clazzAttribute.type == 'number'"
                  placeholder="请输入数字"
                />
                <el-date-picker
                  v-model="form[item.clazzAttribute.id].baseinfoValue"
                  type="datetime"
                  v-if="item.clazzAttribute.type == 'date'"
                  :value-format="item.clazzAttribute.typeConfig.format"
                  :format="item.clazzAttribute.typeConfig.format"
                  placeholder="选择日期"
                >
                </el-date-picker>
                <el-select
                  v-model="form[item.clazzAttribute.id].baseinfoValue"
                  v-if="item.clazzAttribute.type == 'select'"
                  placeholder="请选择"
                  size="small"
                >
                  <el-option
                    v-for="d in item.clazzAttribute.typeConfig.options"
                    :key="d.text"
                    :label="d.text"
                    :value="d.text"
                  />
                </el-select>
                <el-checkbox-group
                  v-model="form[item.clazzAttribute.id].baseinfoValue"
                  v-if="item.clazzAttribute.type == 'checkbox'"
                >
                  <el-checkbox
                    v-for="d in item.clazzAttribute.typeConfig.options"
                    :label="d.text"
                    :key="d.text"
                    >{{ d.text }}</el-checkbox
                  >
                </el-checkbox-group>
                <Upload
                  v-if="item.clazzAttribute.type == 'image'"
                  v-model="form[item.clazzAttribute.id].fileList"
                  :limit="parseInt(item.clazzAttribute.typeConfig.maxNumber)"
                ></Upload>
                <Upload
                  v-if="item.clazzAttribute.type == 'video'"
                  v-model="form[item.clazzAttribute.id].fileList"
                  :fileSize="item.clazzAttribute.typeConfig.maxSize"
                  :limit="1"
                ></Upload>
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
import { getTree } from "@/platform/api/engineeringIO/CheckSetting/inspectionCate";
import {
  objectList,
  objectDel,
} from "@/platform/api/engineeringIO/CheckSetting/inspectionObj";
import {
  getObjBaseinfo,
  ObjBaseinfo,
} from "@/platform/api/engineeringIO/CheckSetting/objectBasicInfo";
import ValidateUtil from "@/platform/utils/ValidateUtil.js";
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
  name: "objectBasicInfo",
  data() {
    let permMap = this.$perm.checkArrayToPermMap([{ type: "addInfo" }]);
    return {
      // 遮罩层
      loading: false,
      formTools: Object.keys(permMap).join(","),
      // 选中数组
      ids: [],
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
        attributeCode: ValidateUtil.required(),
        attributeName: ValidateUtil.required(),
        attributeUnit: ValidateUtil.required(),
        baseinfo: ValidateUtil.required(),
        control: ValidateUtil.required(),
        map: ValidateUtil.required(),
        necessary: ValidateUtil.required(),
        picture: ValidateUtil.required(),
        video: ValidateUtil.required(),
        length: ValidateUtil.norequiredInt(),
      },
      infoData: [],
    };
  },
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
            clazzName: "巡检点类型",
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
      this.queryParams.clazzId = "";
      if (data.dir != "1" && data.id != "") {
        this.queryParams.clazzId = data.id;
        this.objectList();
      }
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 属性表单重置
    reset() {
      this.form = {};
      this.infoData = [];
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
    },
    onClickOps(type, e) {
      switch (type) {
        case "refresh":
          this.resetQuery();
          break;
        default:
          break;
      }
    },
    addInfo() {
      this.title = '编辑'
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
    },
    /** 修改按钮操作 */
    handleUpdate(row = {}) {
      this.title = '编辑'
      this.reset();
      const id = row.id || this.ids;
      getObjBaseinfo(id).then((res) => {
        if (res.data.length == 0) {
          this.msgInfo("暂无需要添加的基本信息");
          return;
        }
        this.infoData = res.data;
        this.infoData.forEach((n, i) => {
          this.infoData[i].clazzAttribute.typeConfig = JSON.parse(
            n.clazzAttribute.typeConfig
          );
          let obj = {
            type: n.clazzAttribute.type,
            baseinfoValue:
              n.clazzAttribute.type == "checkbox"
                ? n.baseinfoValue
                  ? n.baseinfoValue.split(",")
                  : []
                : n.baseinfoValue,
            clazzAttributeId: n.clazzAttribute.id,
            fileList: [],
          };
          this.$set(this.form, n.clazzAttribute.id, obj);
          if (n.clazzAttribute.type == "image" || n.clazzAttribute.type == "video") {
            if (n.baseinfoValue) {
              getGroupFile(n.baseinfoValue).then((res) => {
                let obj = {
                  type: n.clazzAttribute.type,
                  baseinfoValue:
                    n.clazzAttribute.type == "checkbox"
                      ? n.baseinfoValue
                        ? n.baseinfoValue.split(",")
                        : []
                      : n.baseinfoValue,
                  clazzAttributeId: n.clazzAttribute.id,
                  fileList: res.data,
                };
                this.$set(this.form, n.clazzAttribute.id, obj);
              });
            } else {
              let obj = {
                type: n.clazzAttribute.type,
                baseinfoValue:
                  n.clazzAttribute.type == "checkbox"
                    ? n.baseinfoValue
                      ? n.baseinfoValue.split(",")
                      : []
                    : n.baseinfoValue,
                clazzAttributeId: n.clazzAttribute.id,
                fileList: [],
              };
              this.$set(this.form, n.clazzAttribute.id, obj);
            }
          }
        });

        this.open = true;
      });
    },
    /** 提交按钮 */
    submitForm: function () {
      
      this.$refs["form"].validate((valid) => {
        if (valid) {
          let infoList = [];
          let index = 0;
          let arr = Object.keys(this.form);
          for (const k in this.form) {
            if (this.form.hasOwnProperty(k)) {
              const el = this.form[k];
              if (el.type == "checkbox") {
                index += 1;
                el.baseinfoValue = el.baseinfoValue.join(",");
                infoList.push(el);
              } else if (el.type == "image" || el.type == "video") {
                let ids = [];
                el.fileList.forEach((n) => {
                  ids.push(n.id);
                });
                if (el.baseinfoValue) {
                  uploadEditFile({
                    groupId: el.baseinfoValue,
                    ids: ids,
                  }).then((res) => {
                    index += 1;
                    infoList.push(el);
                    if (index == arr.length) {
                      ObjBaseinfo({
                        infoList: infoList,
                        objectId: this.infoData[0].objectId,
                      }).then((res) => {
                        if (res.code === 200) {
                          this.msgSuccess("添加成功");
                          this.open = false;
                          this.objectList();
                        }
                      });
                    }
                  });
                } else {
                  uploadAddFile(ids).then((res) => {
                    index += 1;
                    el.baseinfoValue = res.data;
                    infoList.push(el);
                    if (index == arr.length) {
                      ObjBaseinfo({
                        infoList: infoList,
                        objectId: this.infoData[0].objectId,
                      }).then((res) => {
                        if (res.code === 200) {
                          this.msgSuccess("添加成功");
                          this.open = false;
                          this.objectList();
                        }
                      });
                    }
                  });
                }
              } else {
                index += 1;
                infoList.push(el);
              }
            }
          }
          ObjBaseinfo({
              infoList: infoList,
              objectId: this.infoData[0].objectId,
          }).then((res) => {
              if (res.code === 200) {
                  this.msgSuccess("添加成功");
                  this.open = false;
                  this.objectList();
              }
          });
        }
      });
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
