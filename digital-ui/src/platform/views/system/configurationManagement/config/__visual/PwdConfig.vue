<template>
  <div class="SystemPwdConfig">
    <el-form ref="form" :model="form.model" :rules="form.rules" label-width="120px">

      <DataFormItem v-for="(value,key) in form.items" :key="key" v-bind="value" v-model="form.model[key]">

      </DataFormItem>

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
  import PwdChecker from '../../../../../utils/pwd_checker';

  export default {
    name: "SystemPwdConfig",
    data() {
      return {
        configMap: {},
        form: formhelper.create([
          formhelper.itemCreator.switch("含大小写", "wordCase", false, this.$rules.required(), null),
          formhelper.itemCreator.switch("包含数字", "num", false, this.$rules.required(), null),
          formhelper.itemCreator.switch("特殊符号", "sign", false, this.$rules.required(), null),
          formhelper.itemCreator.input("最小长度", "lenMin", "6", this.$rules.num(true, 1),),
          formhelper.itemCreator.input("最大长度", "lenMax", "18", this.$rules.num(true, 1),),
          formhelper.itemCreator.input("定期修改(天)", "expire", "18", this.$rules.num(true), "仅大于0时可用,单位:天"),
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
        listLike("pwd").then((rs) => {
          let map = {};
          rs.data.forEach((item) => {
            map[item.configKey] = item;
          });
          this.configMap = map;
          let pwdRulesConfig = map['pwd.security.rules'];
          //configValue type=num&len=6-18&expire=30
          let ruleObject = PwdChecker.parseRule(pwdRulesConfig.configValue);
          this.form.model = ruleObject;
          this._model = {...this.form.model};
        });
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

        let pwdRulesConfig = map['pwd.security.rules'];
        //configValue type=num&len=6-18&expire=30
        let rsArray = [];
        let types = [];
        if (this.form.model.wordCase) {
          types.push("wordCase");
        }
        if (this.form.model.num) {
          types.push("num");
        }
        if (this.form.model.wordCase) {
          types.push("sign");
        }
        if (types.length > 0) {
          rsArray.push("type=" + types.join("|"))
        }
        rsArray.push(`len=${this.form.model.lenMin}-${this.form.model.lenMax}`);
        rsArray.push(`expire=${this.form.model.expire}`);
        pwdRulesConfig.configValue = rsArray.join("&");
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
