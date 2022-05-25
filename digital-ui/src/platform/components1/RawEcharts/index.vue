<template>
  <div class="RawEcharts" :id="id"></div>
</template>

<script>
import echarts from "echarts";
  import simple_util from '../../utils/simple_util.js';

  let idCount = 0;
  //自定简单封装原生图表。避免部分api有出入、结果不一致。
  // this.$root.$emit('RawEcharts_resize') 如果外部调整了大小或者显示隐藏了，最好调用一下事件
  // 因为配置项比较多且复杂，推荐使用helper函数，封装实际常用图表配置。
  export default {
    name: "RawEcharts",
    props: {
      option: {
        type: Object,//参考官网的配置即可。
      },
      notMerge: {
        type: Boolean,
        default: false,
      },
      lazyUpdate: {
        type: Boolean,
        default: false
      },
      onGetChart: {
        type: Function,// 钩子回调函数
      },
    },

    data() {
      return {
        id: "raw_echarts_" + (++idCount),
        myChart: null,
        prepared: true
      };
    },
    created() {
      //注册一个全局的重绘 尺寸调整事件。如果外层有需要调整了尺寸或者显示隐藏导致不能正常重绘，则发送相关事件即可
      this.$root.$on('RawEcharts_resize', this.onResize);
      window.addEventListener('resize', this.onResize);
    },
    mounted() {
      simple_util.loadScript("js/echarts.min.js", () => {
        this.initEcharts();
      });
    },
    watch: {
      option: {
        deep: true,
        handler() {
          this.initEcharts();
        },
      },
    },
    beforeDestroy() {
      if (this.myChart) {
        this.myChart.dispose();
        this.myChart = null;
      }
      this.$root.$off('RawEcharts_resize', this.onResize);
      window.removeEventListener('resize', this.onResize);
    },
    methods: {
      //尺寸如果没有及时变化，可以手动调用。 也可以在显示隐藏的时候手动调用，避免不显示的问题。
      onResize() {
        setTimeout(() => {
          if (this.myChart)
            this.myChart.resize();
        });
      },
      showLoading() {
        if (this.myChart) {
          this.myChart.showLoading();
        }
      },
      hideLoading() {
        if (this.myChart) {
          this.myChart.hideLoading();
        }
      },
      initEcharts() {
        if (!echarts || !this.prepared) {
          return;
        }
        // 基于准备好的dom，初始化echarts实例
        if (!this.myChart) {
          this.myChart = echarts.init(document.getElementById(this.id));
          if (this.onGetChart) {
            this.onGetChart(this.myChart, this);
          }
        }
        let rightOption = this.option;
        if (!rightOption) {
          // echarts.showLoading();  echarts.hideLoading();
          return;
        }
        // 使用配置项和数据显示图表。
        this.myChart.setOption(rightOption, this.notMerge, this.lazyUpdate);
      },
    },
  }
</script>

<style lang="scss">
  .RawEcharts {
    min-height: 60px;
  }
</style>
