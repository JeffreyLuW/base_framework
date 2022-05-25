<template>
  <div class="SystemVisualConfig">
    <el-tabs v-model="activeName" type="border-card">
      <el-tab-pane v-if="hasAuthLogin" label="登录界面" name="login">
        <LoginConfig></LoginConfig>
      </el-tab-pane>
      <el-tab-pane v-if="hasAuthPwd" label="密码规则" name="pwd">
        <PwdConfig></PwdConfig>
      </el-tab-pane>
      <!--      <el-tab-pane label="主题管理" name="theme">-->
      <!--        <ThemeList></ThemeList>-->
      <!--      </el-tab-pane>-->
    </el-tabs>
  </div>
</template>

<script>
  import LoginConfig from './__visual/LoginConfig.vue';
  import PwdConfig from './__visual/PwdConfig.vue';
  // import ThemeList from './__visual/ThemeList.vue';


  export default {
    name: "SystemVisualConfig",
    components: {
      LoginConfig, PwdConfig
    },
    data() {
      let hasAuthLogin = this.hasPermi('system:visual-config:login');
      let hasAuthPwd = this.hasPermi('system:visual-config:pwd');
      return {
        hasAuthLogin,
        hasAuthPwd,
        activeName: hasAuthLogin ? "login" : "pwd"
      };
    },
  }
</script>

<style scoped>
  .el-tabs {
    margin: 10px;
  }
</style>
