<template>
  <div class="MapShell8C" :id="id">

  </div>
</template>

<script>
  import util from '../../platform/utils/util.js';

  // 使用步骤:
  // 1 copy SuperMap_iClient下的资源到public目录下
  // 2 copy MapShell.vue 并注册组件
  // 3 使用 MapShell 组件，监听 @init(mapId) 事件 其他api同超图的javascript API
  let shellId = 0;
  const superMapDir = util.baseUrl()+'SuperMap_iClient8C';
  export default {
    name: "MapShell8C",
    data() {
      return {
        id: 'MapShell8C_id_' + (++shellId),
      }
    },
    mounted() {
      this.prepareScripts();
    },
    methods: {
      prepareScripts() {
        let that = this;

        util.loadScripts([
          `${superMapDir}/theme/default/style.css`,
          `${superMapDir}/theme/default/google.css`,
          `${superMapDir}/js/SuperMap-8.1.1-17216.js`
        ], () => {
          //重写获取路径。会返回js所在路径。
          SuperMap._getScriptLocation = function () {
            let rootUrl = that.$route.path.replace(/\/\w+/g, '../') + superMapDir.substring(0) + '/js/';
            // console.log(rootUrl);
            return rootUrl;
          };
          //修改弹窗背景
          SuperMap.Popup.COLOR="transparent";
          /*//修改弹窗大小
          if (SuperMap.Browser.name === "msie") {
              SuperMap.Browser.name="Chrome";
          }*/
          util.loadScripts([
            `${superMapDir}/js/Lang/zh-CN.js`,
            `${superMapDir}/js/Pie.js`,
            `${superMapDir}/js/Circle.js`
          ], () => {

            this.$emit('init', this.id);
          });
        });
      },

    }
  }
</script>

<style lang="scss">
  .MapShell8C {
    height: 100%;
    min-height: 200px;
    box-sizing: border-box;
  }
</style>
