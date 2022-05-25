<template>
  <div class="app-container">
    <el-form :model="queryParams" class="query-form" ref="queryForm" :inline="true" v-show="showSearch"
             label-width="68px">
      <el-form-item label="字典名称" prop="dictType">
        <el-select v-model="queryParams.dictType" size="small" clearable>
          <el-option
            v-for="item in typeOptions"
            :key="item.dictId"
            :label="item.dictName"
            :value="item.dictType"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="字典标签" prop="dictLabel">
        <el-input
          v-model="queryParams.dictLabel"
          placeholder="请输入字典标签"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="数据状态" clearable size="small">
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
    <el-table border v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="字典编码" align="center" prop="dictCode"/>
      <el-table-column label="字典标签" align="center" prop="dictLabel"/>
      <el-table-column label="字典键值" align="center" prop="dictValue"/>
      <el-table-column label="字典排序" align="center" prop="dictSort"/>
      <el-table-column label="状态" align="center" prop="status" :formatter="statusFormat"/>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="128" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:dict:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:dict:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改参数配置对话框 -->
    <AutoDialog :title="title" :show.sync="open" width="500px">
      <el-form ref="form" :model="form" :rules="rules">
        <table class="theme-table-form">
          <colgroup>
            <col width="100"/>
            <col/>
          </colgroup>
          <tr>
            <td class="theme-table-form__label">字典类型</td>
            <td class="theme-table-form__input">
              <el-form-item label="字典类型">
                <el-input v-model="form.dictType" :disabled="true"/>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.dictLabel)">数据标签</td>
            <td class="theme-table-form__input">
              <el-form-item label="数据标签" prop="dictLabel">
                <el-input v-model="form.dictLabel" placeholder="请输入数据标签"/>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.dictValue)">数据键值</td>
            <td class="theme-table-form__input">
              <el-form-item label="数据键值" prop="dictValue">
                <el-input v-model="form.dictValue" placeholder="请输入数据键值"/>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.dictSort)">显示排序</td>
            <td class="theme-table-form__input">
              <el-form-item label="显示排序" prop="dictSort">
                <el-input-number v-model="form.dictSort" controls-position="right" :min="0"/>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.status)">状态</td>
            <td class="theme-table-form__input">
              <el-form-item label="状态" prop="status">
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
          <tr>
            <td :class="themeTableLabelClass(rules.remark)">备注</td>
            <td class="theme-table-form__input">
              <el-form-item label="备注" prop="remark">
                <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
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
  import {listData, getData, delData, addData, updateData, exportData} from "@/platform/api/system/dict/data";
  import {listType, getType} from "@/platform/api/system/dict/type";
  import ruoyi_page_click from '@/platform/views/__common/ruoyi_page_click.js';

  export default {
    name: "Data",
    mixins: [ruoyi_page_click],
    props: {
      dictId: {}
    },
    data() {
      let permMap = this.$perm.checkArrayToPermMap([
        {type: 'add', permi: ['system:dict:add']},
        {type: 'edit', permi: ['system:dict:edit']},
        {type: 'remove', permi: ['system:dict:remove']},
        {type: 'export', permi: ['system:dict:export']}
      ]);
      return {
        // 遮罩层
        loading: true,
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
        // 字典表格数据
        dataList: [],
        // 默认字典类型
        defaultDictType: "",
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 状态数据字典
        statusOptions: [],
        // 类型数据字典
        typeOptions: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          dictName: undefined,
          dictType: undefined,
          status: undefined
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          dictLabel: [
            {required: true, message: "数据标签不能为空", trigger: "blur"}
          ],
          dictValue: [
            {required: true, message: "数据键值不能为空", trigger: "blur"}
          ],
          dictSort: [
            {required: true, message: "数据顺序不能为空", trigger: "blur"}
          ]
        }
      };
    },
    created() {
      const dictId = this.dictId || this.$route.params && this.$route.params.dictId;
      this.getType(dictId);
      this.getTypeList();
      this.getDicts("sys_normal_disable").then(response => {
        this.statusOptions = response.data;
      });
    },
    methods: {
      /** 查询字典类型详细 */
      getType(dictId) {
        getType(dictId).then(response => {
          this.queryParams.dictType = response.data.dictType;
          this.defaultDictType = response.data.dictType;
          this.getList();
        });
      },
      /** 查询字典类型列表 */
      getTypeList() {
        listType().then(response => {
          this.typeOptions = response.rows;
        });
      },
      /** 查询字典数据列表 */
      getList() {
        this.loading = true;
        listData(this.queryParams).then(response => {
          this.dataList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      // 数据状态字典翻译
      statusFormat(row, column) {
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
          dictCode: undefined,
          dictLabel: undefined,
          dictValue: undefined,
          dictSort: 0,
          status: "0",
          remark: undefined
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm("queryForm");
        this.queryParams.dictType = this.defaultDictType;
        this.handleQuery();
      },
      /** 新增按钮操作 */
      handleAdd() {
        if (!this.queryParams.dictType) {
          this.$message("选择字典名称|分类");
          return;
        }
        this.reset();
        this.open = true;
        this.title = "添加字典数据";
        this.form.dictType = this.queryParams.dictType;
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.dictCode)
        this.single = selection.length != 1
        this.multiple = !selection.length
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const dictCode = row.dictCode || this.ids
        getData(dictCode).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改字典数据";
        });
      },
      /** 提交按钮 */
      submitForm: function () {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.dictCode != undefined) {
              updateData(this.form).then(response => {
                if (response.code === 200) {
                  this.msgSuccess("修改成功");
                  this.open = false;
                  this.getList();
                }
              });
            } else {
              addData(this.form).then(response => {
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
        const dictCodes = row.dictCode || this.ids;
        this.$confirm('是否确认删除字典编码为"' + dictCodes + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function () {
          return delData(dictCodes);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        }).catch(function () {
        });
      },
      /** 导出按钮操作 */
      handleExport() {
        const queryParams = this.queryParams;
        this.$confirm('是否确认导出所有数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function () {
          return exportData(queryParams);
        }).then(response => {
          this.download(response.msg);
        }).catch(function () {
        });
      }
    }
  };
</script>
