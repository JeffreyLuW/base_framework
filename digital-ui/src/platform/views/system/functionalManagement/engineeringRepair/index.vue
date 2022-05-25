<template>
  <div class="app-container">
    <el-form :model="queryParams" class="query-form" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="报修类型" prop="type">
        <el-select
          v-model="queryParams.type"
          placeholder="请选择类型"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        >
          <el-option v-for="item in typeArr"
          :label="item.dictLabel"
          :key="item.dictCode"
          :value="item.dictCode"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="填报人" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入填报人"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <div class="query-form-btns">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </div>
    </el-form>
    <el-table border v-loading="loading" :data="infoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="填报人" align="center" prop="createBy"/>
      <el-table-column label="类型" align="center" prop="type"/>
      <el-table-column label="内容" align="center" prop="content"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            v-if="scope.row.file!=null"
            @click="handleDetail(scope.row)"
          >查看附件
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
    <AutoDialog :title="title" :show.sync="open" width="700px">
      <el-form ref="form">
        <el-form-item label="" prop="file">
          <Upload v-model="fileList" :limit="1" :disabled="false"></Upload>
        </el-form-item>
      </el-form>
    </AutoDialog>
  </div>
</template>

<script>
  import {list} from "@/platform/api/system/engineeringRepair";
  import ruoyi_page_click from '@/platform/views/__common/ruoyi_page_click.js';
  import { getGroupFile} from "@/platform/api/common/common.js";
  import Upload from "@/platform/views/waterDisP/__comom/upload";
  export default {
    name: "EngineeringRepair",
    components: {Upload},
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
        infoList: [],
        total:0,
        // 菜单树选项
        typeArr: [],
        fileList:[],
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
          type: undefined,
          name: undefined
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
      this.getDicts("repair_type").then(response => {
        this.typeArr = response.data;
      });
    },
    methods: {

      /** 查询菜单列表 */
      getList() {
        this.loading = true;
        list(this.queryParams).then(response => {
          this.infoList = response.rows
          this.total = response.total
          this.loading = false;
        });
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.postId)
        this.single = selection.length != 1
        this.multiple = !selection.length
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
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
      handleDetail(row){
        if(null!=row.file){
          getGroupFile(row.file).then((res) => {
            console.log(res);
            this.fileList = res.data;
            this.open = true
            this.title = '附件'
          });
        }
      }
    }
  };
</script>
