<template>
  <div class="QueryPageList">
    <el-form v-if="hasSearch" ref="queryForm" class="query-form" :model="query.model" :rules="query.rules"
             v-bind="formOptions">
      <DataFormItem v-for="(value,key) in query.items" :key="key"
                    v-bind="value"
                    @keyup.enter.native="handleQuery"
                    @type_change="handleQuery"
                    v-model="query.model[key]"/>
      <div class="query-form-btns">
        <DataButton :class="{queryBtnNoQuery:!hasQueryItem}" icon="el-icon-search" type="primary"
                    @click="handleQuery"
                    text="搜索"/>
        <DataButton icon="el-icon-refresh" @click="resetQuery" text="重置"/>
      </div>
    </el-form>
    <FormToolsRow :buttons="formTools" @click="onClickOps"></FormToolsRow>
    <!--表格-->
    <el-table border v-loading="loading" :data="tableData" @selection-change="handleSelectionChange"
              v-bind="tableOptions">
      <!--表列-->
      <slot name="table" :handleUpdate="handleUpdate" :handleDelete="handleDelete"></slot>
    </el-table>
    <!--分页-->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="query.model.pageNum"
      :limit.sync="query.model.pageSize"
      @pagination="getList"
    />
    <!--其他-->
    <slot></slot>
  </div>
</template>

<script type="text/jsx">
  import formhelper from '../../utils/formhelper.js';
  import paging_list_mixin from '../../mixin/paging_list_mixin.js';

  let defaultQueryOptions = formhelper.create([]);

  //简单封装的查询列表
  export default {
    name: "QueryPageList",
    mixins: [paging_list_mixin],
    props: {
      queryOptions: {
        type: Object //定义查询条件 formhelper.create([]);
      },
      formTools: {
        type: String,
        default: 'add,delete,export'
      },
      queryFn: {
        type: Function //查询函数
      },
      formOptions: {
        default: () => ({inline: true, labelWidth: "80px"})
      },//el-form的配置 确定样式后，应减少配置传递
      tableOptions: {
        default: () => ({})
      },//el-table的配置  确定样式后，应减少配置传递
    },
    data() {
      return {
        query: this.queryOptions || defaultQueryOptions,
      };
    },
    created() {
      this.queryList();
    },
    computed: {
      hasQueryItem() {
        return this.query && this.query.items && !!Object.keys(this.query.items).length;
      },
      hasSearch() {
        if (!this.query || this.query.empty) {
          return false;
        }
        return true;
      },
    },
    methods: {
      queryList() {
        this.getList();
      },
      //实际的查询数据方法。外部调用 getList()
      doGetList(params) {
        if (!this.queryFn) {
          console.warn("no queryFn method ?");
          return;
        }
        this.loading = true;
        this.queryFn(params, (data, total, err) => {
          this.loading = false;
          if (!err) {
            this.tableData = data;
            this.total = total;
          }
        });
      },
      //工具按钮
      doClickOps(type, value) {
        // if (type === "export")  this.download('system/role/export', {...this.query.model}, `export.xlsx`)
        this.$emit("tools", type, value);
      },
      doExport() {
        this.$emit("export");
      },
      //新增。
      doAdd() {
        this.$emit("add");
      },
      //删除
      doDelete(rows) {
        this.$emit("delete", rows);
      },
      doUpdate(row) {
        this.$emit("update", row);
      },
    },
  }
</script>
<style scoped>
  .queryBtnNoQuery {
    margin-left: 5px;
  }
</style>
