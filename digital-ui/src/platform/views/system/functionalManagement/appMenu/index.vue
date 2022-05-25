<template>
  <div class="app-container">
    <el-form :model="queryParams" class="query-form" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="菜单名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入菜单名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="菜单状态" clearable size="small">
          <el-option
            v-for="dict in statusOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <div class="query-form-btns">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </div>
    </el-form>
    <FormToolsRow :buttons="formTools" @click="onClickOps"></FormToolsRow>
    <el-table
      v-loading="loading"
      :data="menuList"
      row-key="itemId"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column prop="name" label="菜单名称" :show-overflow-tooltip="true" width="160"></el-table-column>
      <el-table-column prop="icon" label="图标" align="center" width="100">
        <template slot-scope="scope">

          <img v-if="scope.row.menuType=='C'" :src="baseURL+scope.row.icon" width="40" height="40">
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" width="60"></el-table-column>
      <el-table-column prop="status" label="状态" :formatter="statusFormat" width="80"></el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini"
                     type="text"
                     icon="el-icon-edit"
                     @click="handleUpdate(scope.row)"
                     v-hasPermi="['app:menu:edit']"
          >修改
          </el-button>
          <el-button
            v-if="scope.row.menuType=='M'"
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleAdd(scope.row)"
            v-hasPermi="['app:menu:add']"
          >新增
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['app:menu:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改菜单对话框 -->
    <AutoDialog :title="title" :show.sync="open" width="600px">
      <el-form ref="form" :model="form" :rules="rules">
        <table class="theme-table-form">
          <colgroup>
            <col width="100"/>
            <col/>
            <col width="100"/>
            <col/>
          </colgroup>
          <tr>
            <td class="theme-table-form__label">上级菜单</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="上级菜单">
                <treeselect
                  v-model="form.parentId"
                  :options="menuOptions"
                  :normalizer="normalizer"
                  :show-count="true"
                  placeholder="选择上级菜单"
                />
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.menuType)">菜单类型</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="菜单类型" prop="menuType">
                <el-radio-group v-model="form.menuType">
                  <el-radio label="M">目录</el-radio>
                  <el-radio label="C">菜单</el-radio>
                </el-radio-group>
              </el-form-item>
            </td>
          </tr>
          <tr v-if="form.menuType != 'M'">
            <td class="theme-table-form__label">菜单图标</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="菜单图标">
                <DataUpload v-model="form.icon" :limit="1"
                            :beforeAdd="beforeAddFile"></DataUpload>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.name)">菜单名称</td>
            <td class="theme-table-form__input" key="name">
              <el-form-item label="菜单名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入菜单名称"/>
              </el-form-item>
            </td>
            <td :class="themeTableLabelClass(rules.orderNum)">显示排序</td>
            <td class="theme-table-form__input" key="orderNum">
              <el-form-item label="显示排序" prop="orderNum">
                <el-input-number v-model="form.orderNum" controls-position="right" :min="0"/>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td class="theme-table-form__label">菜单状态</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="菜单状态">
                <el-radio-group v-model="form.status">
                  <el-radio
                    v-for="dict in statusOptions"
                    :key="dict.dictValue"
                    :label="dict.dictValue"
                  >{{dict.dictLabel}}
                  </el-radio>
                </el-radio-group>
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
  import {listMenu, getMenu, delMenu, addMenu, updateMenu} from "@/platform/api/system/appMenu";
  import Treeselect from "@riophae/vue-treeselect";
  import "@riophae/vue-treeselect/dist/vue-treeselect.css";
  import IconSelect from "@/platform/components2/IconSelect";
  import ruoyi_page_click from '@/platform/views/__common/ruoyi_page_click.js';

  export default {
    name: "Menu",
    components: {Treeselect, IconSelect},
    mixins: [ruoyi_page_click],
    data() {
      let permMap = this.$perm.checkArrayToPermMap([
        {type: 'add', permi: ['app:menu:add']}
      ]);
      return {
        // 遮罩层
        loading: true,
        formTools: Object.keys(permMap).join(","),
        // 显示搜索条件
        showSearch: true,
        // 菜单表格树数据
        menuList: [],
        // 菜单树选项
        menuOptions: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 显示状态数据字典
        visibleOptions: [],
        // 菜单状态数据字典
        statusOptions: [],
        // 查询参数
        queryParams: {
          name: undefined,
          visible: undefined
        },
        // 表单参数
        form: {},
        baseURL: process.env.VUE_APP_BASE_API,
        // 表单校验
        rules: {
          name: [
            {required: true, message: "菜单名称不能为空", trigger: "blur"}
          ],
          orderNum: [
            {required: true, message: "菜单顺序不能为空", trigger: "blur"}
          ]
        }
      };
    },
    created() {
      this.getList();
      this.getDicts("sys_show_hide").then(response => {
        this.visibleOptions = response.data;
      });
      this.getDicts("sys_normal_disable").then(response => {
        this.statusOptions = response.data;
      });
    },
    methods: {
      // 选择图标
      selected(name) {
        this.form.icon = name;
      },
      /** 查询菜单列表 */
      getList() {
        this.loading = true;
        listMenu(this.queryParams).then(response => {
          this.menuList = this.handleTree(response.data, "itemId");
          this.loading = false;
        });
      },
      /** 转换菜单数据结构 */
      normalizer(node) {
        if (node.children && !node.children.length) {
          delete node.children;
        }
        return {
          id: node.itemId,
          label: node.name,
          children: node.children
        };
      },
      /** 查询菜单下拉树结构 */
      getTreeselect() {
        listMenu().then(response => {
          this.menuOptions = [];
          const menu = {itemId: 0, name: '主类目', children: []};
          menu.children = this.handleTree(response.data, "itemId");
          this.menuOptions.push(menu);
        });
      },
      // 显示状态字典翻译
      visibleFormat(row, column) {
        if (row.menuType == "F") {
          return "";
        }
        return this.selectDictLabel(this.visibleOptions, row.visible);
      },
      // 菜单状态字典翻译
      statusFormat(row, column) {
        if (row.menuType == "F") {
          return "";
        }
        return this.selectDictLabel(this.statusOptions, row.status);
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          itemId: undefined,
          parentId: 0,
          name: undefined,
          icon: undefined,
          menuType: "M",
          orderNum: undefined,
          isFrame: "1",
          visible: "0",
          status: "0"
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm("queryForm");
        this.handleQuery();
      },
      /** 新增按钮操作 */
      handleAdd(row) {
        this.reset();
        this.getTreeselect();
        if (row != null && row.itemId) {
          this.form.parentId = row.itemId;
        } else {
          this.form.parentId = 0;
        }
        this.open = true;
        this.title = "添加菜单";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        this.getTreeselect();
        this.form = row;
        this.open = true;
        this.title = "修改菜单";
      },
      /** 提交按钮 */
      submitForm: function () {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.itemId != undefined) {
              updateMenu(this.form).then(response => {
                if (response.code === 200) {
                  this.msgSuccess("修改成功");
                  this.open = false;
                  this.getList();
                }
              });
            } else {
              addMenu(this.form).then(response => {
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
        this.$confirm('是否确认删除名称为"' + row.name + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function () {
          return delMenu(row.itemId);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        }).catch(function () {
        });
      },
      beforeAddFile(file, fileList) {
        if (file.name.endsWith(".png")
          || file.name.endsWith(".jpg")
          || file.name.endsWith(".ico")
          || file.name.endsWith(".svg")
          || file.name.endsWith(".gif")) {
          return true;
        }
        this.msgError("只能上传常用图片格式");
        return false;
      },
    }
  };
</script>
