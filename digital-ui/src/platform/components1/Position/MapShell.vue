<template>
  <div class="MapShell"  :id="id" style="height: 100%;width: 100%;background-color:#fff;position: relative;"></div>
</template>

<script>

// 使用步骤:
// 1 copy SuperMap_iClient下的资源到public目录下
// 2 copy MapShell.vue 并注册组件
// 3 使用 MapShell 组件，监听 @init(mapId) 事件 其他api同超图的javascript API
let shellId = 0;
export default {
  name: "MapShell",
  data() {
    return {
      id: "MapShell_id_" + ++shellId,
      loadedScripts : {
          key: {
              state: -1,
              cbs: [],
              script: null
          }
      }
    };
  },
  props:{
    fromSy:{
      type: Boolean
    },
  },
  mounted() {
    this.prepareScripts();
  },
  methods: {
     //保存使用当前类加载的script标签
    
    //加载多个标签
    loadScripts(srcArr, onload, type) {
        if (!(srcArr instanceof Array))
            srcArr = [srcArr];
        let completeCount = 0;
        let onComplete = () => {
            completeCount++;
            if (completeCount >= srcArr.length && onload)
                onload();
        };
        srcArr.forEach((src) => {
            this.loadScript(src, onComplete, type);
        });
    },
    //加载单个标签
     loadScript(src, onload, type) {
        let info = this.loadedScripts[src];
        if (info) {
            if (info.state === 1) {
                if (onload) onload.bind(info.script)();
                return;
            } else {
                info.cbs.push(onload);
                return;
            }
        }
        if (!type) {
            if (src.endsWith('.js')) {
                type = 'script';
            } else if (src.endsWith('.css')) {
                type = 'link';
            }
        }
        info = {
            state: -1,
            cbs: [onload]
        };
        this.loadedScripts[src] = info;
        let script = document.createElement(type);
        info.script = script;
        script.onload = function () {
            let that = this;
            let args = arguments;
            info.state = 1;
            info.cbs.forEach(function (cb) {
                if (cb) cb.apply(that, args);
            });
            info.cbs = [];
        };
        script.charset = "utf-8";
        if (type === 'script')
            script.setAttribute("src", src);
        if (type === 'link') {
            script.setAttribute("rel", 'stylesheet');
            script.setAttribute("href", src);
        }
        document.querySelector("head").appendChild(script);
    },
    prepareScripts() {
      let ctx=this;
      ctx.loadScripts(
        ["/SuperMap_iClient/libs/css/ol.css", "/SuperMap_iClient/libs/js/ol.js"],
        () => {
          ctx.loadScripts(
            [
              "/SuperMap_iClient/libs/css/iclient9-openlayers.min.css",
              "/SuperMap_iClient/libs/css/ol-debug.css",
              "/SuperMap_iClient/libs/css/ol-layerswitcher.css",
              
              "/SuperMap_iClient/libs/js/canvg.min.js",
              "/SuperMap_iClient/libs/js/deck.gl.min.js",
              "/SuperMap_iClient/libs/js/echarts.min.js",
              "/SuperMap_iClient/libs/js/geostats.js",
              "/SuperMap_iClient/libs/js/jsonsql.js",
              "/SuperMap_iClient/libs/js/ol-debug.js",
              "/SuperMap_iClient/libs/js/ol-layerswitcher.js",
              "/SuperMap_iClient/libs/js/olms.js",
              "/SuperMap_iClient/libs/js/proj4.js",

              "/SuperMap_iClient/libs/js/mapv.min.js",
              "/SuperMap_iClient/libs/js/iclient9-openlayers.min.js",
              "/SuperMap_iClient/libs/js/turf.min.js",
              "/SuperMap_iClient/libs/js/OSMBuildings-OL3.js",
              "/SuperMap_iClient/libs/js/animatedclusterlayer.js",
              "/SuperMap_iClient/libs/dat.gui.js",
              "/SuperMap_iClient/libs/widgets.js",

            ],
            () => {
              this.$emit("init", this.id);
            }
          );
        }
      );
    }
  }
};
</script>

