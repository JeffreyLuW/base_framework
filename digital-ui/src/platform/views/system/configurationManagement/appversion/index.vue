<template>
  <div class="app-container">
    <el-form :model="queryParams" class="query-form" ref="queryForm" :inline="true" v-show="showSearch"
             label-width="68px">
      <el-form-item label="APP标识" prop="appKey">
        <el-input
          v-model="queryParams.appKey"
          placeholder="请输入APP标识"
          clearable
          size="small"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="版本号" prop="versionCode">
        <el-input
          v-model="queryParams.versionCode"
          placeholder="请输入版本号"
          clearable
          size="small"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <div class="query-form-btns">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </div>
    </el-form>

    <FormToolsRow :buttons="formTools" @click="onClickOps">

    </FormToolsRow>

    <el-table border v-loading="loading" :data="versionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="APP标识" align="center" prop="appKey"/>
      <el-table-column label="版本号" align="center" prop="versionCode"/>
      <el-table-column label="版本名称" align="center" prop="versionName" :show-overflow-tooltip="true"/>
      <el-table-column label="强制更新" align="center" prop="updateStatus" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ scope.row.updateStatus==0?'否':'是' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="下载地址" align="center" prop="downloadUrl" :show-overflow-tooltip="true"/>
      <el-table-column label="更新内容" align="center" prop="modifyContent" :show-overflow-tooltip="true"/>
      <el-table-column label="创建人" align="center" prop="createBy" />
      <el-table-column label="上传时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:config:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:appVersion:remove']"
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
            <td :class="themeTableLabelClass(rules.appKey)">应用标识</td>
            <td class="theme-table-form__input">
              <el-form-item label="应用标识" prop="appKey">
                <el-input v-model="form.appKey" placeholder="请输入应用标识"/>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.versionName)">版本名称</td>
            <td class="theme-table-form__input">
              <el-form-item label="版本名称" prop="versionName">
                <el-input v-model="form.versionName" placeholder="请输入版本名称"/>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.versionCode)">版本号</td>
            <td class="theme-table-form__input">
              <el-form-item label="版本号" prop="versionCode">
                <el-input v-model="form.versionCode" placeholder="请输入版本号" type="number"/>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.updateStatus)">强制更新</td>
            <td class="theme-table-form__input">
              <el-form-item label="是否强制更新" prop="updateStatus">
                <el-radio
                  v-model="form.updateStatus"
                  label="0"
                >否</el-radio
                >
                <el-radio
                  v-model="form.updateStatus"
                  label="1"
                >是</el-radio
                >
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.apk)">apk安装包</td>
            <td class="theme-table-form__input">
              <el-form-item label="apk安装包" prop="updateStatus">
                <el-upload
                  class="upload-demo"
                  :action="fileUploadUrl"
                  :limit="1"
                  accept=".apk"
                  :multiple=false
                  :before-remove="beforeRemove"
                  :before-upload="beforeUpload"
                  :on-remove="handleRemove"
                  :on-exceed="handleOnExceed"
                  :on-progress="handleProgress"
                  :file-list="fileList">
                  <el-button v-show="uploadShow" size="small" type="primary">点击上传APk</el-button>
                </el-upload>
                <el-button v-show="downShow" size="small" type="primary" @click="downloadClick">点击下载</el-button>
              </el-form-item>
            </td>
          </tr>
          <tr>
            <td :class="themeTableLabelClass(rules.modifyContent)">备注</td>
            <td class="theme-table-form__input">
              <el-form-item label="更新内容" prop="modifyContent">
                <el-input v-model="form.modifyContent" type="textarea" placeholder="请输入更新内容"/>
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
    listVersion,
    getVersionInfo,
    delVersion,
    addVersion,
    updateVersion

  } from "@/platform/api/system/appversion";
  import ruoyi_page_click from '@/platform/views/__common/ruoyi_page_click.js';
  import {uploadFile} from '@/platform/api/common/common.js';

  export default {
    name: "AppVersion",
    mixins: [ruoyi_page_click],
    data() {
      let permMap = this.$perm.checkArrayToPermMap([
        {type: 'add', permi: ['system:appVersion:add']},
        {type: 'remove', permi: ['system:appVersion:remove']},
        {type: 'edit', permi: ['system:appVersion:edit']},
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
        // 参数表格数据
        versionList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          versionCode: undefined,
          appKey: undefined,
        },
        // 表单参数
        form: {
          updateStatus: 0
        },
        // 表单校验
        rules: {
          appKey: [
            {required: true, message: "APP标识不能为空", trigger: "blur"}
          ],
          versionCode: [
            {required: true, message: "版本号不能为空", trigger: "blur"}
          ],
          versionName: [
            {required: true, message: "版本名称不能为空", trigger: "blur"}
          ],
          modifyContent: [
            {required: true, message: "更新内容不能为空", trigger: "blur"}
          ],
          apk: [
            {required: true, message: "安装包不能为空", trigger: "blur"}
          ],
          updateStatus: [
            {required: true, message: "", trigger: "blur"}
          ]
        },
        fileUploadUrl:'',           //  文件上传接口
        fileList:[],
        uploadShow:true,           //  上传
        downShow:false,             //  下载
        baseUrl:process.env.VUE_APP_BASE_API,   //  baseUrl
      };
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询参数列表 */
      getList() {
        this.loading = true;
        listVersion(this.queryParams).then(response => {
            this.versionList = response.rows;
            this.total = response.total;
            this.loading = false;
          }
        );
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          versionoCode: undefined,
          versionName: undefined,
          appKey: undefined,
          updateStatus: '0',
          modifyContent: undefined,
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
        this.handleQuery();
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "上传APP版本";
        this.fileList = []
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.versionId)
        this.single = selection.length != 1
        this.multiple = !selection.length
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const versionId = row.versionId || this.ids
        getVersionInfo(versionId).then(response => {
          this.form = response.data;
          if(this.form.updateStatus==0){
            this.form.updateStatus = '0'
          }else{
            this.form.updateStatus = '1'
          }
          let fileObject = new Object();
          let file = this.form.downloadUrl.split("/")
          fileObject.name = file[file.length-1];
          fileObject.url = this.baseUrl+this.form.downloadUrl;
          fileObject.uid = 1;
          fileObject.size = this.form.apkSize;
          if(this.fileList.length==0){
            this.fileList.push(fileObject);
          }

          this.open = true;
          this.title = "修改版本信息";
        });
      },
      /** 提交按钮 */
      submitForm: function () {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if(this.fileList.length!=1){
              this.msgError("请上传apk安装包")
              return;
            }else{
              this.form.downloadUrl = this.fileList[0].uid
              this.form.apkSize = this.fileList[0].size
            }
            if (this.form.versionId != undefined) {
              updateVersion(this.form).then(response => {
                if (response.code === 200) {
                  this.msgSuccess("修改成功");
                  this.open = false;
                  this.getList();
                }
              });
            } else {
              addVersion(this.form).then(response => {
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
        const versionIds = row.versionId || this.ids;
        this.$confirm('是否确认删除参数编号为"' + versionIds + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function () {
          return delVersion(versionIds);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        }).catch(function () {
        });
      },
      beforeUpload(file){
        if(this.fileList.length>0){
          this.msgError("只能上传一个安装包");
          return false
        }
        if(!file.name.endsWith(".apk")){
          this.msgError("请上传apk文件");
          return false
        }
        let fileObject = {
          name:'',
          url:'',
          uid:0,
        };
        uploadFile(file).then(res => {
          console.log("上传后：",res);
          if (res.code == 200) {
            fileObject.name = res.fileName;
            fileObject.url = res.url;
            fileObject.uid = res.id;
            fileObject.size = file.size;
            this.fileList.push(fileObject);
          } else {
            this.$message.error(res.msg);
          }
        });
      },
      handleOnExceed(files, fileList){
        this.$message.warning('只能选择1个文件!');
      },
      handleProgress(event,file, fileList){
        console.log(event)
        console.log(file)
        console.log(fileList)
      },
      //  文件移除之前
      beforeRemove(file, fileList) {
        return this.$confirm(`确定移除 ${ file.name }？`);
      },

      //  文件移除
      handleRemove(file, fileList) {
        for (var i = 0;i < this.fileList.length;i++) {
          if (file.uid == this.fileList[i].uid ) {
            this.fileList.splice(i,1);
          }
        }
      },
      //  文件下载
      downloadClick() {
        console.log("文件：",this.fileList);
        for (var i = 0;i < this.fileList.length;i++) {
          var url = this.baseUrl + this.fileList[i].url;
          window.open(url, '_blank')
        }

      },
    }
  };
</script>
