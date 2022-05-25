<template>
  <QueryPageList ref="page" :queryOptions="queryOptions" :queryFn="queryFn"
                 @add="onAdd" @delete="onDelete" @update="onUpdate" @export="onExport">
    <template slot="table" slot-scope="{handleUpdate,handleDelete}">
      <el-table-column type="selection" width="55" align="center"/>
      <!--      <el-table-column type="index" width="55" align="center"/>-->
      <el-table-column label="笔记编号" prop="noteId" width="120"/>
      <el-table-column label="笔记标题" prop="title" :show-overflow-tooltip="true" width="300"/>
      <el-table-column label="笔记状态" prop="status" :show-overflow-tooltip="true" width="150"/>
      <el-table-column label="创建人" prop="createBy" width="100"/>
      <el-table-column label="创建时间" prop="createTime" width="100"/>
      <el-table-column label="更新人" prop="updateBy" width="100"/>
      <el-table-column label="更新时间" prop="updateTime" width="100"/>
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
  import formhelper from '../../utils/formhelper.js';
  import QueryPageList from '../__common/QueryPageList.vue';
  import EditForm from './__test_edit';
  import request from '@/platform/utils/request';

  // 获取列表
  function getList(param) {
    let url = '/note/list?pageSize=' + param.pageSize + '&pageNum=' + param.pageNum;
    if (param.status != null) {
      url += '&status=' + param.status;
    }
    if (param.title != null) {
      url += '&title=' + param.title;
    }
    return request({
      url: url,
      method: 'get'
    })
  }

  function add(param) {
    let url = '/note/add'
    return request({
      url: url,
      method: 'post',
      data: param
    })
  }

  function update(param) {
    let url = '/note'
    return request({
      url: url,
      method: 'put',
      data: param
    })
  }

  function remove(ids) {
    return request({
      url: '/note',
      method: 'delete',
      data: ids
    })
  }

  function exportExcel(param) {
    let url = '/note/export?pageSize=' + param.pageSize + '&pageNum=' + param.pageNum;
    if (param.status != null) {
      url += '&status=' + param.status;
    }
    if (param.title != null) {
      url += '&title=' + param.title;
    }
    return request({
      url: url,
      method: 'get'
    })
  }

  //演示自定义封装组件
  export default {
    name: "PageListDemo.vue",
    components: {
      QueryPageList,
    },
    data() {
      return {
        //查询条件
        queryOptions: formhelper.create([
          formhelper.itemCreator.input('标题', 'title', null),
          formhelper.itemCreator.select('状态', 'status', null, null, "cxm_note_status"),
          formhelper.itemCreator.dateRange('日期范围', 'dateRange', ["2020-08-05", "2020-08-14"]),
        ])
      };
    },
    methods: {
      //调用查询接口
      queryFn: function (params, next) {
        console.log("query params:", params);
        let result = getList(params);
        result.then(response =>
          next(response.rows, response.total)
        )
      },
      onExport: function () {
        let form = this.queryOptions.form;
        let result = exportExcel(form);
        result.then(
          response => {
            this.download(response.msg);
          }
        )
        //todo 根据文件名下载
        //然后刷新页面
        this.$refs.page.queryList();
      },
      //新增。
      onAdd: function () {
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
            let form = dialog.$component.edit.form
            let param = {
              status: form.status,
              title: form.title,
              detail: form.detail
            }
            console.log('param1 = ' + param);
            add(param);
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
        let ids = []
        for (let i = 0; i < rows.length; i++) {
          ids[i] = rows[i].noteId;
        }
        remove(ids);
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
            let form = dialog.$component.edit.form
            let param = {
              noteId: row.noteId,
              status: form.status,
              title: form.title,
              detail: form.detail
            }
            update(param);
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
