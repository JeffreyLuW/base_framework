<template>
  <div class="app-containter">
    <el-tabs v-model="activeName" @tab-click="handleClick" type="border-card">
      <el-tab-pane label="年引水计划" name="year">
        <yearDP v-if="activeName == 'year'"></yearDP>
      </el-tab-pane>
      <el-tab-pane label="月引水计划" name="month">
        <monthDP v-if="activeName == 'month'"></monthDP
      ></el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import yearDP from "./__comom/yearDP";
import monthDP from "./__comom/monthDP";
export default {
  data() {
    return {
      activeName: "year",
    };
  },

  components: {
    yearDP,
    monthDP,
  },
  
  methods: {
    handleClick(tab, event) {
      console.log(tab, event);
    },
  },
};
</script>

<style lang="scss" scoped>
.app-containter {
  .el-tabs {
    height: 100%;
    /deep/.el-tabs__content {
      height: 95%;
      .el-tab-pane {
        height: calc(100% - 30px);
      }
    }
  }
}
</style>
