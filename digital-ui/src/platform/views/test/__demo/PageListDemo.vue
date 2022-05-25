<template>
  <QueryPageList ref="page" :formTools="formTools" :queryOptions="queryOptions" :queryFn="queryFn"
                 @add="onAdd" @delete="onDelete" @update="onUpdate" @export="onExport">
    <template slot="table" slot-scope="{handleUpdate,handleDelete}">
      <el-table-column type="selection" width="55" align="center"/>
      <!--      <el-table-column type="index" width="55" align="center"/>-->
      <el-table-column label="角色编号" prop="roleId" width="120"/>
      <el-table-column label="角色名称" prop="roleName" :show-overflow-tooltip="true" width="150"/>
      <el-table-column label="权限字符" prop="roleKey" :show-overflow-tooltip="true" width="150"/>
      <el-table-column label="显示顺序" prop="roleSort" width="100"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <DataButton type="text" icon="el-icon-edit" text="修改" @click="handleUpdate(scope.row)"/>
          <DataButton type="text" icon="el-icon-delete" text="删除" @click="handleDelete([scope.row])"/>
        </template>
      </el-table-column>
    </template>
  </QueryPageList>
</template>

<script type="text/jsx">
  import formhelper from '../../../utils/formhelper.js';
  import QueryPageList from '../../__common/QueryPageList.vue';
  import EditForm from './EditFormDemo.vue';
  import {checkArray} from '../../../utils/permission.js';

  //演示自定义封装组件
  export default {
    name: "PageListDemo",
    components: {
      QueryPageList,
    },
    data() {
      return {
        //查询条件
        queryOptions: formhelper.create([
          formhelper.itemCreator.input('用户名', 'name', null),
          formhelper.itemCreator.select('性别', 'sex', null, null, "sys_user_sex"),
          formhelper.itemCreator.select('操作', 'op', '1', null, "sys_oper_type"),
          formhelper.itemCreator.dateRange('日期范围', 'dateRange', ["2020-08-05", "2020-08-14"]),
        ]),
        //工具栏 并判断权限 否则不需要写或者设置'add,delete,export'即可
        formTools: checkArray([
          {type: 'add', permi: ['*:*:*']},
          {type: 'delete', permi: ['*:*:*']},
          {type: 'export', permi: ['*:*:*']},
        ]).map((item) => item.type).join(','),
      };
    },
    created() {
      //add,delete,export
      console.log("formTools", this.formTools);
    },
    methods: {
      //调用查询接口
      queryFn(params, next) {
        console.log("query params:", params);
        setTimeout(() => {
          let data =
            Array(params.pageSize).fill(1).map((v, i) => {
              let id = parseInt(Math.random() * 100);
              return {id: i, roleId: id, roleName: id, roleKey: "code_" + i, roleSort: i};
            });
          next(data, 50);
        }, 500);
      },
      onExport() {
        console.log("点击导出!!");
      },
      //新增。非复杂表单，可以使用 DataEditForm 替换 EditForm
      onAdd() {
        this.$dialog.show({
          title: "新增",
          sizeMode: "300px",
          width: '400px',
          component: EditForm,
          onCloseFn: (type, dialog, next) => {
            if (type !== 'ok') {
              next();
              return;
            }
            //dialog.$component.validate(onValidate) 表单验证,执行新增操作
            //...
            //关闭对话框
            next();
            //然后刷新页面
            this.$refs.page.queryList();
          },
        });
      },
      //删除
      onDelete(rows) {
        console.log("on delete!", rows);
        this.$refs.page.queryList();
      },
      //点击更新
      onUpdate(row) {
        this.$dialog.show({
          title: "编辑",
          sizeMode: "300px",
          width: '400px',
          component: EditForm,
          propsData: {model: row},
          onCloseFn: (type, dialog, next) => {
            if (type !== 'ok') {
              next();
              return;
            }
            //执行修改操作 调用api。
            //...
            //关闭对话框
            next();
            //然后刷新页面
            this.$refs.page.queryList();
          },
        });
      },
    }
  }
</script>
