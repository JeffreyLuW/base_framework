<template>
  <div class="SystemLoginConfig">
    <el-form ref="form" :model="form.model" :rules="form.rules" label-width="120px">

      <DataFormItem v-bind="form.items.sysName" v-model="form.model.sysName"></DataFormItem>

      <DataFormItem v-bind="form.items.loginBgIsImg" v-model="form.model.loginBgIsImg"></DataFormItem>
      <DataFormItem v-if="form.model.loginBgIsImg" v-bind="form.items.loginBgUrl"
                    v-model="form.model.loginBgUrl"></DataFormItem>
      <DataFormItem v-else v-bind="form.items.loginBgValue"
                    v-model="form.model.loginBgValue"></DataFormItem>

      <!--Logo类型-->
      <DataFormItem v-bind="form.items.sysLogoIsImg" v-model="form.model.sysLogoIsImg"></DataFormItem>
      <DataFormItem v-if="form.model.sysLogoIsImg" v-bind="form.items.sysLogoUrl"
                    v-model="form.model.sysLogoUrl"></DataFormItem>
      <DataFormItem v-else v-bind="form.items.sysLogoValue"
                    v-model="form.model.sysLogoValue"></DataFormItem>

      <!--favicon-->
      <DataFormItem v-bind="form.items.sysFaviconIsImg" v-model="form.model.sysFaviconIsImg"></DataFormItem>
      <DataFormItem v-if="form.model.sysFaviconIsImg" v-bind="form.items.sysFaviconUrl"
                    v-model="form.model.sysFaviconUrl"></DataFormItem>
      <DataFormItem v-else v-bind="form.items.sysFaviconValue"
                    v-model="form.model.sysFaviconValue"></DataFormItem>
      <div class="save-btns">
        <DataButton type="warning" text="重置" @click="resetForm"></DataButton>
        <DataButton type="primary" text="保存" :loading="isSaving" @click="clickSave"></DataButton>
      </div>
    </el-form>
  </div>
</template>

<script>
  import {batchAddOrUpdate, listLike} from '@/platform/api/system/config.js';
  import formhelper from '../../../../../utils/formhelper.js';

  export default {
    name: "SystemLoginConfig",
    data() {
      return {
        configMap: {},
        form: formhelper.create([
          formhelper.itemCreator.input("系统标题", "sysName", null, this.$rules.required(), "系统标题,同时在登录页和浏览器标签显示"),

          formhelper.itemCreator.switch("背景类型", "loginBgIsImg", false, null, "上传图片"),
          formhelper.itemCreator.upload("背景图片", "loginBgUrl", null, this.$rules.required(), this.beforeAddFile, {type_limit: 1}),
          formhelper.itemCreator.input("登录背景", "loginBgValue", null, this.$rules.required(), "可以是背景色|远程图片地址,中横线表示忽略配置"),

          formhelper.itemCreator.switch("Logo类型", "sysLogoIsImg", false, null, "上传图片"),
          formhelper.itemCreator.upload("Logo图片", "sysLogoUrl", null, this.$rules.required(), this.beforeAddFile, {type_limit: 1}),
          formhelper.itemCreator.input("Logo地址", "sysLogoValue", null, this.$rules.required(), "远程图片地址,如 http://xxx.png,中横线表示忽略配置"),

          formhelper.itemCreator.switch("Favicon类型", "sysFaviconIsImg", false, null, "上传图片"),
          formhelper.itemCreator.upload("Favicon图片", "sysFaviconUrl", null, this.$rules.required(), this.beforeAddFile, {type_limit: 1}),
          formhelper.itemCreator.input("Favicon地址", "sysFaviconValue", null, this.$rules.required(), "远程图片地址,如 http://xxx.png,中横线表示忽略配置"),
        ], false),
        isSaving: false,
      };
    },
    created() {
      this.fetchConfig();
    },
    methods: {
      resetForm() {
        if (this._model) {
          this.form.model = this._model;
        }
        this.$refs.form.clearValidate();
      },
      fetchConfig() {
        listLike("page").then((rs) => {
          let map = {};
          rs.data.forEach((item) => {
            map[item.configKey] = item;
          });
          this.configMap = map;
          // configKey: "page.login.bg"
          // configIsImg: "Y"
          // configValue: "-"
          //"page.login.bg", "page.sys.name", "page.sys.logo", "page.sys.favicon"
          this.form.model.sysName = map['page.sys.name'].configValue;

          this.form.model.loginBgIsImg = map['page.login.bg'].configIsImg === 'Y';
          if (this.form.model.loginBgIsImg) {
            this.form.model.loginBgUrl = this.fixImgValue(map['page.login.bg'].configValue);
          } else {
            this.form.model.loginBgValue = map['page.login.bg'].configValue;
          }

          this.form.model.sysLogoIsImg = map['page.sys.logo'].configIsImg === 'Y';
          if (this.form.model.sysLogoIsImg) {
            this.form.model.sysLogoUrl = this.fixImgValue(map['page.sys.logo'].configValue);
          } else {
            this.form.model.sysLogoValue = map['page.sys.logo'].configValue;
          }

          this.form.model.sysFaviconIsImg = map['page.sys.favicon'].configIsImg === 'Y';
          if (this.form.model.sysFaviconIsImg) {
            this.form.model.sysFaviconUrl = this.fixImgValue(map['page.sys.favicon'].configValue);
          } else {
            this.form.model.sysFaviconValue = map['page.sys.favicon'].configValue;
          }

          this._model = {...this.form.model};
        });
      },
      fixImgValue(value) {
        if (!value || value === "-") return null;
        return value;
      },
      //判断是否为图片
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
      //点击保存
      clickSave() {
        this.$refs.form.validate((ok) => {
          if (!ok) return;
          this.updateConfigByModel();
          //提交
          this.doSave();
        });
      },
      //根据当前的model，修改对应的系统参数配置
      updateConfigByModel() {
        let map = this.configMap;
        map['page.sys.name'].configValue = this.form.model.sysName;

        if (this.form.model.loginBgIsImg) {
          map['page.login.bg'].configIsImg = 'Y';
          map['page.login.bg'].configValue = this.form.model.loginBgUrl;
        } else {
          map['page.login.bg'].configIsImg = 'N';
          map['page.login.bg'].configValue = this.form.model.loginBgValue;
        }

        if (this.form.model.sysLogoIsImg) {
          map['page.sys.logo'].configIsImg = 'Y';
          map['page.sys.logo'].configValue = this.form.model.sysLogoUrl;
        } else {
          map['page.sys.logo'].configIsImg = 'N';
          map['page.sys.logo'].configValue = this.form.model.sysLogoValue;
        }

        if (this.form.model.sysFaviconIsImg) {
          map['page.sys.favicon'].configIsImg = 'Y';
          map['page.sys.favicon'].configValue = this.form.model.sysFaviconUrl;
        } else {
          map['page.sys.favicon'].configIsImg = 'N';
          map['page.sys.favicon'].configValue = this.form.model.sysFaviconValue;
        }

      },
      //调用接口保存提交
      doSave() {
        let data = Object.values(this.configMap).map((item) => {
          return {
            configId: item.configId,
            configName: item.configName,
            configKey: item.configKey,
            configIsImg: item.configIsImg,
            configValue: item.configValue,
            configType: item.configType,
            remark: item.remark
          };
        });
        this.isSaving = true;
        batchAddOrUpdate(data).then(() => {
          this.msgSuccess("保存成功");
        }).finally(() => {
          this.isSaving = false;
        });
      },
    }
  }
</script>

<style scoped lang="scss">
  .el-form {
    width: 600px;
  }

  .save-btns {
    text-align: center;

    button {
      min-width: 80px;
    }
  }
</style>
