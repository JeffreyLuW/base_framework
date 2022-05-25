<template>
  <div class="app-container">
    <h2>地图url管理</h2>

    <div class="top">
      <el-button type="primary" size="mini" @click="handleAdd">新增</el-button>
    </div>

    <el-table border :data="typeList">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="地图名称" align="center" prop="mapName" />
      <el-table-column label="地图Code" align="center" prop="mapCode" />
      <el-table-column
        label="地图地址"
        align="center"
        prop="mapUrl"
        width="600"
      />
      <el-table-column
        width="100"
        label="操作"
        align="center"
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
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改参数配置对话框 -->
    <AutoDialog :title="title" :show.sync="open" width="800px">
      <el-form ref="form" :model="form" :rules="rules">
        <table class="theme-table-form">
          <colgroup>
            <col width="100" />
            <col />
            <col width="100" />
            <col />
          </colgroup>
          <tr>
            <td :class="themeTableLabelClass(rules.remark)">地图名称</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="地图名称" prop="mapName">
                <el-input
                  v-model="form.mapName"
                  type="textarea"
                  placeholder="请输入内容"
                ></el-input>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.remark)">地图Code</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="地图名称" prop="mapCode">
                <el-input
                  v-model="form.mapCode"
                  type="textarea"
                  placeholder="请输入内容"
                ></el-input>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.remark)">地图地址</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="地图地址" prop="mapUrl">
                <el-input
                  v-model="form.mapUrl"
                  type="textarea"
                  placeholder="请输入内容"
                ></el-input>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.remark)">经度</td>
            <td class="theme-table-form__input">
              <el-form-item label="经度" prop="longitude">
                <el-input
                  v-model="form.longitude"
                  type="textarea"
                  placeholder="请输入内容"
                ></el-input>
              </el-form-item>
            </td>

            <td :class="themeTableLabelClass(rules.remark)">纬度</td>
            <td class="theme-table-form__input">
              <el-form-item label="纬度" prop="latitude">
                <el-input
                  v-model="form.latitude"
                  type="textarea"
                  placeholder="请输入内容"
                ></el-input>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.remark)">备注信息</td>
            <td class="theme-table-form__input" colspan="3">
              <el-form-item label="备注信息" prop="remark">
                <el-input
                  v-model="form.remark"
                  type="textarea"
                  placeholder="请输入内容"
                ></el-input>
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
import {
  getList,
  getDetail,
  add,
  update,
} from "@/platform/api/system/mapUrl.js";
export default {
  data() {
    return {
      name: "",
      typeList: [],
      form: {},
      open: false,
      title: "",
      rules: {}
    };
  },
  methods: {
    handleQuery() {},
    resetQuery() {},

    getList() {
      getList().then(res => {
        this.typeList = res.rows;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      getDetail({ id: row.id }).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改";
      });
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加字典类型";
    },

    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            console.log(this.form);
            update(this.form).then(response => {
              if (response.code === 200) {
                this.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }
            });
          } else {
            add(this.form).then(response => {
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
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        mapName: undefined,
        mapUrl: undefined,
        remark: undefined,
        mapCode: undefined,
        longitude: undefined,
        latitude: undefined
      };
    }
  },
  created() {
    this.getList();
  }
};
</script>
<style scoped lang="scss">
.top {
  .el-input {
    width: 180px;
    margin-right: 20px;
  }
}
.el-form {
  // border: 1px solid red;
  .el-input {
    width: 500px;
    margin-right: 20px;
  }
}
.title {
  ::v-deep .el-form-item__label {
    color: royalblue;
  }
}
</style>
