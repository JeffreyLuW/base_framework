<template>
  <div v-if="show" class="dashboard-editor-container theme-bg-pannel">
    <h6>首页</h6>
    <!-- <panel-group @handleSetLineChartData="handleSetLineChartData"/>

    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <line-chart :chart-data="lineChartData"/>
    </el-row>

    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <raddar-chart/>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <pie-chart/>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <bar-chart/>
        </div>
      </el-col>
    </el-row> -->

<div style="position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0 ">
        <iframe
          src='http://36.250.94.248:6082/main.html?user=9dfced1cc0b49f54&pwd=ffffe084a55098e514ddd30f2213274f'
          frameborder="no"
          style="width: 100%; height: 100%"
          scrolling="auto"
        ></iframe>
      </div>
  </div>
</template>

<script>
  //import PanelGroup from '../platform/views/dashboard/PanelGroup'
  //import LineChart from '../platform/views/dashboard/LineChart'
  //import RaddarChart from '../platform/views/dashboard/RaddarChart'
  //import PieChart from '../platform/views/dashboard/PieChart'
  //import BarChart from '../platform/views/dashboard/BarChart'

  const height = document.documentElement.clientHeight - 94.5 + "px";
  const style = { position: 'absolute',
    left: 0,
    right: 0,
    top: 0,
    bottom: 0 };

  const lineChartData = {
    newVisitis: {
      expectedData: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
      actualData: [0, 0, 1300.81, 0, 0, 1300, 0, 2400, 7879, 1300, 2500, 3260]
    },
    messages: {
      expectedData: [200, 192, 120, 144, 160, 130, 140],
      actualData: [180, 160, 151, 106, 145, 150, 130]
    },
    purchases: {
      expectedData: [80, 100, 121, 104, 105, 90, 100],
      actualData: [120, 90, 100, 138, 142, 130, 130]
    },
    shoppings: {
      expectedData: [130, 140, 141, 142, 145, 150, 160],
      actualData: [120, 82, 91, 154, 162, 140, 130]
    }
  }

  export default {
    name: 'Index',
    components: {
      //PanelGroup,
      //LineChart,
      //RaddarChart,
      //PieChart,
      //BarChart
    },
    data() {
      return {
        lineChartData: lineChartData.newVisitis,
        show: false
      }
    },
    mounted() {
      setTimeout(() => {
        this.show = true
      }, 300);
    },
    methods: {
      handleSetLineChartData(type) {
        this.lineChartData = lineChartData[type]
      }
    }
  }
</script>

<style lang="scss" scoped>
  .dashboard-editor-container {
    padding: 32px;
    /*background-color: rgb(240, 242, 245);*/
    position: relative;

    .chart-wrapper {
      /*background: #fff;*/
      padding: 16px 16px 0;
      margin-bottom: 32px;
    }
  }

  @media (max-width: 1024px) {
    .chart-wrapper {
      padding: 8px;
    }
  }
</style>
