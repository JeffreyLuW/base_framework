<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" class="query-form" :inline="true" v-show="showSearch"
             label-width="68px">
      <el-form-item label="表名称" prop="tableName">
        <el-input
          v-model="queryParams.tableName"
          placeholder="请输入表名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="表描述" prop="tableComment">
        <el-input
          v-model="queryParams.tableComment"
          placeholder="请输入表描述"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
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
      <div class="query-form-btns">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </div>
    </el-form>
    <FormToolsRow :buttons="formTools" @click="onClickOps">
      <el-button
        slot="genTable"
        type="primary"
        icon="el-icon-download"
        size="mini"
        @click="handleGenTable"
      >生成
      </el-button>
      <el-button
        slot="importTable"
        type="info"
        icon="el-icon-upload"
        size="mini"
        @click="openImportTable"
      >导入
      </el-button>
      <el-button
        slot="editTable"
        type="success"
        icon="el-icon-edit"
        size="mini"
        :disabled="single"
        @click="handleEditTable"
      >修改
      </el-button>
    </FormToolsRow>

    <el-table border v-loading="loading" :data="tableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="序号" type="index" width="50" align="center">
        <template slot-scope="scope">
          <span>{{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="表名称"
        align="center"
        prop="tableName"
        :show-overflow-tooltip="true"
        width="130"
      />
      <el-table-column
        label="表描述"
        align="center"
        prop="tableComment"
        :show-overflow-tooltip="true"
        width="130"
      />
      <el-table-column
        label="实体"
        align="center"
        prop="className"
        :show-overflow-tooltip="true"
        width="130"
      />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160"/>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="160"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            icon="el-icon-view"
            @click="handlePreview(scope.row)"
            v-hasPermi="['tool:gen:preview']"
          >预览
          </el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-edit"
            @click="handleEditTable(scope.row)"
            v-hasPermi="['tool:gen:edit']"
          >编辑
          </el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['tool:gen:remove']"
          >删除
          </el-button>
          <el-button
            type="text"
            size="small"
            icon="el-icon-download"
            @click="handleGenTable(scope.row)"
            v-hasPermi="['tool:gen:code']"
          >生成代码
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
    <!-- 预览界面 -->
    <AutoDialog :title="preview.title" :show.sync="preview.open" width="80%" size-mode="fullscreen"
                :show-footer="false">
      <el-tabs v-model="preview.activeName">
        <el-tab-pane
          v-for="(value, key) in preview.data"
          :label="key.substring(key.lastIndexOf('/')+1,key.indexOf('.vm'))"
          :name="key.substring(key.lastIndexOf('/')+1,key.indexOf('.vm'))"
          :key="key"
        >
          <pre>{{ value }}</pre>
        </el-tab-pane>
      </el-tabs>
    </AutoDialog>
    <import-table ref="import" @ok="handleQuery"/>
  </div>
</template>

<script>
  import {listTable, previewTable, delTable, genCode} from "@/platform/api/tool/gen";
  import importTable from "./importTable";
  import {downLoadZip} from "@/platform/utils/zipdownload";
  import EditTable from './editTable.vue';
  import ruoyi_page_click from '@/platform/views/__common/ruoyi_page_click.js';

  export default {
    name: "Gen",
    components: {importTable},
    mixins: [ruoyi_page_click],
    data() {
      let permMap = this.$perm.checkArrayToPermMap([
        {type: 'genTable', permi: ['tool:gen:code']},
        {type: 'importTable', permi: ['tool:gen:import']},
        {type: 'editTable', permi: ['tool:gen:edit']},
        {type: 'delete', permi: ['tool:gen:remove']}
      ]);
      return {
        // 遮罩层
        loading: true,
        formTools: Object.keys(permMap).join(","),
        // 唯一标识符
        uniqueId: "",
        // 选中数组
        ids: [],
        // 选中表数组
        tableNames: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        // 表数据
        tableList: [],
        // 日期范围
        dateRange: "",
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          tableName: undefined,
          tableComment: undefined
        },
        // 预览参数
        preview: {
          open: false,
          title: "代码预览",
          data: {},
          activeName: "domain.java"
        }
      };
    },
    created() {
      this.getList();
    },
    activated() {
      const time = this.$route.query.t;
      if (time != null && time != this.uniqueId) {
        this.uniqueId = time;
        this.resetQuery();
      }
    },
    methods: {
      /** 查询表集合 */
      getList() {
        this.loading = true;
        listTable(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
            this.tableList = response.rows;
            this.total = response.total;
            this.loading = false;
          }
        );
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 生成代码操作 */
      handleGenTable(row) {
        const tableNames = row.tableName || this.tableNames;
        if (tableNames == "") {
          this.msgError("请选择要生成的数据");
          return;
        }
        if (row.genType === "1") {
          genCode(row.tableName).then(response => {
            this.msgSuccess("成功生成到自定义路径：" + row.genPath);
          });
        } else {
          downLoadZip("/tool/gen/batchGenCode?tables=" + tableNames, "ruoyi");
        }
      },
      /** 打开导入表弹窗 */
      openImportTable() {
        this.$refs.import.show();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.dateRange = [];
        this.resetForm("queryForm");
        this.handleQuery();
      },
      /** 预览按钮 */
      handlePreview(row) {
        previewTable(row.tableId).then(response => {
          this.preview.data = response.data;
          this.preview.open = true;
        });
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.tableId);
        this.tableNames = selection.map(item => item.tableName);
        this.single = selection.length != 1;
        this.multiple = !selection.length;
      },
      /** 修改按钮操作 */
      handleEditTable(row) {
        const tableId = row.tableId || this.ids[0];
        // this.$router.push("/gen/edit/" + tableId);
        this.$dialog.show({
          title: "编辑",
          sizeMode: "fullscreen",//fullscreen
          component: EditTable,
          fullscreen: false,
          showFooter: false,
          propsData: {
            tableId
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const tableIds = row.tableId || this.ids;
        this.$confirm('是否确认删除表编号为"' + tableIds + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function () {
          return delTable(tableIds);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        }).catch(function () {
        });
      }
    }
  };
</script>
