<template>
  <QueryPageList ref="page" :formTools="formTools" :queryOptions="queryOptions" :queryFn="queryFn"
                 :showSearch="false" @add="onAdd" @delete="onDelete" @update="onUpdate">
    <template slot="table" slot-scope="{handleUpdate,handleDelete}">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="主题编号" prop="key"/>
      <el-table-column label="主题名称" prop="name"/>
      <el-table-column label="主题变量" prop="vars" :show-overflow-tooltip="true"/>
      <el-table-column label="状态" prop="statusLabel"/>
      <el-table-column label="备注" prop="remark" :show-overflow-tooltip="true" :formatter="emptyLineFormatter"/>
      <el-table-column v-if="permDesc.update||permDesc.delete" label="操作" align="center"
                       class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <DataButton v-if="permDesc.update" type="text" icon="el-icon-edit" text="修改"
                      @click="handleUpdate(scope.row)"/>
          <DataButton v-if="permDesc.delete" type="text" icon="el-icon-delete" text="删除"
                      @click="handleDelete([scope.row])"/>
        </template>
      </el-table-column>
    </template>
  </QueryPageList>
</template>

<script type="text/jsx">
  import QueryPageList from '../../../../__common/QueryPageList.vue';
  import EditForm from './ThemeForm.vue';
  import {checkArrayToPermMap} from '../../../../../utils/permission.js';
  import * as ThemeApi from '../../../../../api/system/theme.js';
  //主题管理
  export default {
    name: "SystemThemeList",
    components: {
      QueryPageList,
    },
    data() {
      let permDesc = checkArrayToPermMap([
        {type: 'add', permi: ['system:theme:add']},
        {type: 'update', permi: ['system:theme:edit']},
        {type: 'delete', permi: ['system:theme:remove']},
      ]);
      return {
        //查询条件
        queryOptions: this.$formHelper.create([]),
        permDesc: permDesc,
        //工具栏 并判断权限 否则不需要写或者设置'add,delete,export'即可
        formTools: Object.keys(permDesc).join(','),
      };
    },
    created() {
      //add,delete,export
    },
    methods: {
      //调用查询接口
      queryFn(params, next) {
        ThemeApi.list(params).then((rs) => {
          next(rs.rows, rs.total);
        }).catch((err) => {
          next(null, null, true);
        });
      },
      //新增。非复杂表单，可以使用 DataEditForm 替换 EditForm
      onAdd() {
        this.$dialog.show({
          title: "新增",
          sizeMode: "auto",
          width: '400px',
          component: EditForm,
          onAutoOk: (rs, dialog, next) => {
            dialog.okBtnLoading = true;
            ThemeApi.add(rs).then(() => {
              next();
              this.$refs.page.queryList();
            }).finally(() => {
              dialog.okBtnLoading = false;
            });
          }
        });
      },
      //删除
      onDelete(rows) {
        let keys = rows.map((r) => r.key);
        ThemeApi.del(keys).then(() => {
          this.$refs.page.handleQuery();
        });
      },
      //点击更新
      onUpdate(row) {
        this.$dialog.show({
          title: "编辑",
          sizeMode: "auto",
          width: '400px',
          component: EditForm,
          propsData: {model: row},
          onAutoOk: (rs, dialog, next) => {
            // console.log(rs);
            dialog.okBtnLoading = true;
            ThemeApi.update(rs).then(() => {
              next();
              this.$refs.page.queryList();
            }).finally(() => {
              dialog.okBtnLoading = false;
            });
          }
        });
      },
    }
  }
</script>
