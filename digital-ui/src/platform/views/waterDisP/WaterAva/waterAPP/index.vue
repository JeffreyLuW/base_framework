<template>
  <div class="app-containter">
    <el-tabs v-model="activeName" @tab-click="handleClick" type="border-card">
      <el-tab-pane label="取水申请表" name="appForm">
        <appForm></appForm>
      </el-tab-pane>
      <!-- <el-tab-pane label="引水指令" name="ins">
        <INS></INS>
      </el-tab-pane> -->
    </el-tabs>
  </div>
</template>

<script>
import appForm from "./__comom/appForm";
import INS from "./__comom/ins";
export default {
  data() {
    return {
      activeName: "appForm",
    };
  },
  components: {
    appForm,
    INS,
  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event);
    },
  },
};
</script>
<style lang="scss" scoped>
    .el-tabs{
        height: 100%;
    }
</style>
