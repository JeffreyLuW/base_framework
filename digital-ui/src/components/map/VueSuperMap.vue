<template>
  <div class="VueSuperMap">
    <MapShell8C @init="initMap"></MapShell8C>
    <div class="tool-bar" v-if="showToolBar">
      <div
        v-for="(item, index) in tools"
        :key="index"
        :class="[item.className, 'tool-bar-item']"
        @click="item.click"
        :title="item.title"
      ></div>
    </div>
    <slot></slot>
  </div>
</template>

<script>
import Vue from "vue";
import MapShell8C from "./MapShell8C";
import aa from "../../platform/views/__IrrMap/3dMap/flyUrl/yzzUrl";
//超图的使用示例。也可以直接用作正式组件 所在div及父div应该明确指定高度，否则可能会因撑不开不显示
//map.updateSize(); 尺寸变化时使用?
// 测量后 监听 @measure({value,unit}) 事件npm
// feature 点击后 监听 @clickFeature(feature)
// 不同feature 鼠标滑动的状态：feature.style  feature.style_normal  feature.style_active
export default {
  name: "VueSuperMap",
  components: { MapShell8C },
  props: {
    serviceLayerUrls: {
      //提供自己发布的服务图层。 会叠加到基础图层之上。
      type: Array,
      default: () => {
        return [
          {
            name: "37000",
            url: " "
          }
        ];
      }
    },
    measureServiceUrl: {
      type: String //测量服务url，默认和 serviceLayerUrls的第一个相同
    },
    showToolBar: {
      type: Boolean,
      default: false
    },
    defaultCenter: {
      type: Array, // [118.76, 36.3891]
      default: () => [116.227, 35.539]
    },
    defaultZoom: {
      type: Number,
      default: 0 //默认缩放级别
    },
    resolutions: {
      //天地图比例尺
      type: Array,
      default: () => [
        0.02197265625,
        0.010986328125,
        0.0054931640625,
        0.00274658203125,
        0.001373291015625,
        0.0006866455078125,
        0.00034332275390625,
        0.000171661376953125,
        0.0000858306884765625,
        0.00004291534423828125,
        0.000021457672119140632,
        0.000010728836059570316,
        0.000005364418029785155
      ]
    },
    dxresolutions: {
      //天地图地形图
      type: Array,
      default: () => [
        0.02197265625,
        0.010986328125,
        0.0054931640625,
        0.00274658203125,
        0.001373291015625,
        0.0006866455078125,
        0.00034332275390625,
        0.000171661376953125,
        0.0000858306884765625,
        0.00004291534423828125,
        0.000021457672119140632,
        0.000010728836059570316,
        0.000005364418029785155
      ]
    },
    resolutionsindex: {
      //天地图相应参数
      type: Number,
      default: 6
    }
  },
  data() {
    return {
      drownnum: 0,
      map: null, //地图引用
      layers: {}, //保存全部的图层  vectorLayer 矢量图层   labelLayer 文本标签图层
      drawControls: null, //绘制控件  {line:SuperMap.Control.DrawFeature,polygon}
      //selectFeatureControls: null,//特征选择 标签等的选择事件？
      serviceLayers: [],
      popupInfo: null, //点击弹窗
      overpopupInfo: null, //鼠标移入弹窗
      tools: {
        //几个工具类，以及对应的逻辑。 明天添加对应的图标
        FullExtent: {
          className: "super-map-tool-bar-FullExtent",
          title: "复位",
          click: () => {
            this.setCenter();
          }
        },
        MeasureLine: {
          className: "super-map-tool-bar-MeasureLine",
          title: "距离测量",
          click: () => {
            this.clearFeaturesT();
            this.drawControls["line"].activate();
          }
        },
        MeasureArea: {
          className: "super-map-tool-bar-MeasureArea",
          title: "面积测量",
          click: () => {
            this.clearFeaturesT();
            this.drawControls["polygon"].activate();
          }
        },
        Satellite: {
          className: "super-map-tool-bar-Satellite",
          title: "卫图",
          click: () => {
            this.CheckYxt();
          }
        },
        Plat: {
          className: "super-map-tool-bar-Plat",
          title: "地图",
          click: () => {
            this.CheckSlt();
          }
        },
        clearAllQT: {
          className: "super-map-tool-bar-clearAllQT",
          title: "清除",
          click: () => {
            this.clearFeaturesT();
            // this.clearFeatures();
          }
        }
      }
    };
  },
  beforeDestroy() {
    if (this.map) {
      /*let obj = document.getElementById('map')
        if (obj.parentNode) {GQ
          obj.parentNode.removeChild(obj)
        }*/
      this.map.destroy();
      this.map = null;
    }
  },
  methods: {
    initMap(mapId) {
      let map = new SuperMap.Map(mapId, {
        controls: [
          //new SuperMap.Control.ScaleLine(),
          //  new SuperMap.Control.LayerSwitcher(),
          new SuperMap.Control.Navigation({
            dragPanOptions: {
              enableKinetic: true
            }
          })
        ]
      });

      map.addControl(
        new SuperMap.Control.MousePosition({
          numDigits: 3
        })
      );
      //地图所有图层都被当做叠加图层来使用
      map.allOverlays = true;
      this.map = map;
      //发送事件
      this.$emit("initMap", map, mapId);
      this.addLayer();
    },

    addLayer() {
      let map = this.map;
      let layers = this.layers;
      let url1 =
        "http://t0.tianditu.com/img_c/wmts?tk=3c301ea21d2b0b57e638866c061e54fd"; //天地图世界影像图底图
      let url1_1 =
        "http://www.sdmap.gov.cn/tileservice/SDRasterPubMap?tk=3c301ea21d2b0b57e638866c061e54fd"; //天地图山东影像图底图
      let url3 =
        "http://t0.tianditu.com/vec_c/wmts?tk=3c301ea21d2b0b57e638866c061e54fd"; //天地图世界矢量图层
      let url3_1 =
        "http://www.sdmap.gov.cn/tileservice/SDPubMap?tk=3c301ea21d2b0b57e638866c061e54fd"; //天地图山东矢量图层

      let urldx = 'http://t0.tianditu.gov.cn/ter_c/wmts?tk=3c301ea21d2b0b57e638866c061e54fd'; //天地图地形服务
      let urldxbz = 'http://t0.tianditu.com/cta_c/wmts?tk=3c301ea21d2b0b57e638866c061e54fd'; //天地图地形标注服务
      let urlyxbz = 'http://t0.tianditu.gov.cn/cva_c/wmts?tk=3c301ea21d2b0b57e638866c061e54fd'; //天地图影像标注全国（没有路网）
      let url2 = 'http://www.sdmap.gov.cn/tileservice/sdrasterpubmapdj?tk=3c301ea21d2b0b57e638866c061e54fd'; //世界标注矢量图层

      //let urlyxbz="http://www.sdmap.gov.cn/tileservice/sdrasterpubmapdj?tk=3c301ea21d2b0b57e638866c061e54fd";//天地图影像标注山东
      //let urlyxbz="http://t0.tianditu.gov.cn/cia_c/wmts?tk=3c301ea21d2b0b57e638866c061e54fd";//天地图影像标注全国

      //新建图层天地图影像图底图
      this.createWMTSLayers(
        {
          layer1_1: { url: url1_1, name: "img" },
          layerDT: { url: url3, name: "vec" },
          layerDT_1: { url: url3_1, name: "vec" },
          layer1: { url: url1, name: "img" },
          layer1_2: { url: urlyxbz, name: "cva" },
          layerdx: { url: urldx, name: "ter" },
          layerdxbz: { url: urldxbz, name: "cta" }
        },
        this.layers
      ); // layer1 layer1_1 layerDT layerDT_1
      //服务图层?
      if (this.serviceLayerUrls) {
        this.serviceLayerUrls.forEach((item, index) => {
          let { name, url } = item; //370000
          let layer = new SuperMap.Layer.TiledDynamicRESTLayer(
            name,
            url,
            { transparent: true, cacheEnabled: true },
            {
              resolutions: this.resolutions,
              dpi: 99999999999939
            }
          );
          //layer.visibility=false;
          this.layers[name] = layer;
          this.serviceLayers.push(layer);
        });
      }

      //marker图层
      //layers.markerLayer = new SuperMap.Layer.Markers("图标");
      //创建点线面图层
      layers.polygonLayer = new SuperMap.Layer.Vector("面图层");
      layers.pointLayer = new SuperMap.Layer.Vector("线");
      layers.lineLayer = new SuperMap.Layer.Vector("线图层");

      //位图图层 可以放置路径相关图层
      layers.vectorLayer = new SuperMap.Layer.Vector("查询图层");
      layers.xzqhlayer = new SuperMap.Layer.Vector("xzqh"); //行政区划图层
      //临时存储的图层 业务专题使用
      layers.qdLayer = new SuperMap.Layer.Vector("qdLayer");
      let strategy = new SuperMap.Strategy.GeoText();
      //设置标签的样式
      strategy.style = {
        fontColor: "#9932CC",
        fontWeight: "bolder",
        fontSize: "14px",
        //   fill: true,
        // fillColor: "#3c6dc9",
        fillOpacity: 1,
        stroke: false,
        labelYOffset: -20,
        strokeColor: "#8B7B8B"
      };
      //可以放置文本标签之类的内容
      layers.labelLayer = new SuperMap.Layer.Vector("标签图层", {
        strategies: [strategy]
      });
      //this.addFeatureSelectControls();
      //添加线面绘制控件 可用于线面测量   control-->layer-->vector feature
      let drawControls = {
        line: new SuperMap.Control.Measure(SuperMap.Handler.Path, {
          persist: true,
          immediate: true
        }),
        //面积量算
        polygon: new SuperMap.Control.Measure(SuperMap.Handler.Polygon, {
          persist: true
        })
      };
      this.drawControls = drawControls;
      for (let key in drawControls) {
        //drawControls[key].events.on({"featureadded": this.drawCompleted});//绘制完成的回调？
        drawControls[key].events.on({
          measure: this.handleMeasure,
          measurepartial: this.handleMeasurements
        });
        map.addControl(drawControls[key]);
      }
      //为图层初始化完毕添加layerInitialized事件
      let addLayersFn = () => {
        //加载顺序从左到右==》对应图层下到上  可以调整顺序
        map.addLayers([
          layers.layerDT,
          layers.layerDT_1,
          layers.layer1,
          layers.layer1_1,
          layers.layer1_2,
          layers.layerdx,
          layers.layerdxbz,
          ...this.serviceLayers, //服务图层
          layers.xzqhlayer,
          layers.polygonLayer,
          layers.lineLayer,
          layers.pointLayer,

          //layers.markerLayer,
          layers.vectorLayer,
          layers.labelLayer,
          layers.qdLayer
        ]);
        layers.layer1.setVisibility(false); //影像
        layers.layer1_1.setVisibility(false); //影像
        layers.layer1_2.setVisibility(false); //影像
        layers.layerdx.setVisibility(false); //地形图
        layers.layerdxbz.setVisibility(false); //地形图
        this.setCenter(null, null);
        //激活选择控件
        // this.selectFeatureControls.activate();
        //加载缩放控件
        //this.addPanZoomBar();
        //设置地图缩放级别的数量
        if (this.defaultZoom) {
          map.zoomTo(this.defaultZoom);
        }
        //地图操作事件

        map.events.on({
          zoomend: () => {
            this.$emit("zoomend", map.getZoom());
          },
          moveend: () => {
            this.$emit("moveend", map.getZoom());
          },
          mousemove: e => {
            this.$emit("mousemove", e);
          },
          click: e => {
            this.$emit("mapclick", e);
          }
        });
        this.$emit("initData", map);
      };
      if (this.serviceLayerUrls && this.serviceLayerUrls.length > 0) {
        let count = this.serviceLayerUrls.length;
        this.serviceLayers.forEach(layer => {
          layer.events.on({
            layerInitialized: () => {
              count--;
              if (count <= 0) {
                addLayersFn();
              }
            }
          });
        });
      } else {
        addLayersFn();
      }
    },

    createVueComponent(vueOptionOrConstructor, opt) {
      let vueComponent = null;

      if (typeof vueOptionOrConstructor === "function") {
        vueComponent = new vueOptionOrConstructor(opt);
      } else {
        if (vueOptionOrConstructor.$options) {
          vueComponent = vueOptionOrConstructor;
        } else {
          //options

          let Constructor = Vue.extend(vueOptionOrConstructor);
          vueComponent = new Constructor(opt);
        }
      }
      return vueComponent;
    },
    //移除popup
    mapRemovePopup(clicklx, popupInfo) {
      let map = this.map;
      if (!popupInfo) {
        if (clicklx === "click") {
          popupInfo = this.popupInfo;
          this.popupInfo = null;
        } else {
          popupInfo = this.overpopupInfo;
          this.overpopupInfo = null;
        }
      }
      if (popupInfo) {
        map.removePopup(popupInfo);
        if (popupInfo._vue) {
          popupInfo._vue.$destroy();
          popupInfo._vue = null;
        }
      }
    },
    //点击feature后，显示气泡popup信息 .lx=vue时，可以传递vue组件
    showFeatureInfo(paramobj, isRemoveLastPopup = true) {
      //清除上一个弹窗。
      if (isRemoveLastPopup) {
        this.mapRemovePopup(paramobj.clicklx);
      }

      let map = this.map;
      let popupInfo = this.popupInfo;
      let vueComponent = null;
      let contentHTML = paramobj.html;
      let jb = paramobj.jb || 3;
      let zoom = map.getZoom();
      if (zoom > jb) {
        jb = zoom;
      }
      let lonlat = new SuperMap.LonLat(paramobj.x, paramobj.y);
      if (paramobj.clicklx === "click") {
        //避免弹窗被右侧窗口遮盖
        //this.map.setCenter(new SuperMap.LonLat(paramobj.x+0.15, paramobj.y), jb);
      }
      if (paramobj.lx === "url") {
        contentHTML =
          '<iframe src="' +
          contentHTML +
          '" frameborder="0" style="width:100%;height:100%;" scrolling="no"  allowtransparency="true"/>';
      }
      if (paramobj.lx === "vue") {
        vueComponent = this.createVueComponent(contentHTML, paramobj.vueOpt);
        popupInfo = new SuperMap.Popup(
          "chicken",
          lonlat,
          new SuperMap.Size(paramobj.width, paramobj.height),
          vueComponent,
          true, //closeBox
          null
        );
      }
      if (paramobj.lx === "html") {
        popupInfo = new SuperMap.Popup.FramedCloud(
          "duck",
          lonlat,
          new SuperMap.Size(paramobj.width, paramobj.height),
          contentHTML,
          null, // anchor
          false, //closeBox
          () => {
            // this.mapRemovePopup(clicklx);
          }, //closeBoxCallback
          true //isShowShadow
        );
      }
      // popupInfo = new SuperMap.Popup.FramedCloud(
      //   'chicken',
      //   lonlat,
      //   new SuperMap.Size(paramobj.width, paramobj.height),
      //   contentHTML,
      //   null,// anchor
      //   false,//closeBox
      //   () => {
      //     this.mapRemovePopup(clicklx)
      //   },//closeBoxCallback
      //   true//isShowShadow
      // )

      let draw = popupInfo.draw;
      //monkey draw方法，扩展内容div
      popupInfo.draw = function() {
        let rs = draw.apply(this, arguments);
        if (vueComponent) {
          let conentParentDiv = rs.querySelector(".smPopupContent");
          conentParentDiv.innerHTML = "";
          vueComponent.$mount();
          conentParentDiv.appendChild(vueComponent.$el);
          popupInfo._vue = vueComponent;
        }
        return rs;
      };

      if (paramobj.width && paramobj.height) {
        popupInfo.autoSize = false;
      }

      /* popupInfo.fixedRelativePosition=true;
         popupInfo.panMapIfOutOfView=true;*/
      popupInfo.keepInMap = true;
      map.addPopup(popupInfo);
      if (paramobj.clicklx === "click") {
        this.popupInfo = popupInfo;
      } else {
        this.overpopupInfo = popupInfo;
      }
    },
    //新建图层天地图影像图底图
    createWMTSLayers(urlObj, targetLayers) {
      //新建图层 天地图全部缩放级别
      var matrixIds = [];
      Object.keys(urlObj).forEach((key, index) => {
        let resolutions = this.resolutions;
        let item = urlObj[key];
        //地形图的级别比矢量和影像的小
        if (item.name === "ter" || item.name === "cta") {
          resolutions = this.dxresolutions;
        }
        for (var i = 0; i < resolutions.length; ++i) {
          matrixIds[i] = { identifier: i + this.resolutionsindex };
        }
        targetLayers[key] = new SuperMap.Layer.WMTS({
          name: item.name,
          url: item.url,
          matrixSet: "c",
          layer: item.name,
          style: "default",
          format: "tiles",
          resolutions: resolutions,
          matrixIds: matrixIds,
          opacity: 1,
          requestEncoding: "KVP"
        });
      });
      return targetLayers;
    },
    //复杂缩放控件类
    addPanZoomBar() {
      //初始化
      let panzoombar = new SuperMap.Control.PanZoomBar();
      // 是否固定缩放级别为[0,16]之间的整数，默认为false
      panzoombar.forceFixedZoomLevel = true;
      //是否显示滑动条，默认值为false
      //panzoombar.showSlider = true;
      /*点击箭头移动地图时，所移动的距离占总距离（上下移动的总距离为高度，左右移动的总距离为宽度）
        的百分比，默认为null。 例如：如果slideRatio 设为0.5, 则垂直上移地图半个地图高度.*/
      //panzoombar.slideRatio = 0.5;
      //设置缩放条滑块的高度，默认为11
      panzoombar.zoomStopHeight = 5;
      //设置缩放条滑块的宽度，默认为13
      panzoombar.zoomStopWidth = 9;
      //panzoombar.showCompass = false;
      this.map.addControl(panzoombar);
      //获取罗盘中心的按钮
      let doms = panzoombar.getDoms();
      doms.zoommaxextent.style.display = "none"; //罗盘中心的按钮隐藏
    },
    //设置中心点
    setCenter(x, y) {
      if (x && y) {
        this.map.setCenter(new SuperMap.LonLat(x, y), this.map.getZoom());
      } else {
        this.map.setCenter(
          new SuperMap.LonLat(this.defaultCenter[0], this.defaultCenter[1]),
          this.defaultZoom
        ); //设置地图中心点
      }
    },
    //获取测量服务url
    findMeasureServiceUrl() {
      let url = this.measureServiceUrl;
      if (!url && this.serviceLayerUrls && this.serviceLayerUrls[0]) {
        url = this.serviceLayerUrls[0].url; // ?服务类url，也是默认的
      }
      return url;
    },
    //绘制完成后的回调
    drawCompleted(drawGeometryArgs) {
      //停止画线画面控制
      this.deactivateControl();
      //获得图层几何对象
      let geometry = drawGeometryArgs.feature.geometry;
      let measureParam = new SuperMap.REST.MeasureParameters(
        geometry
      ); /* MeasureParameters：量算参数类。 客户端要量算的地物间的距离或某个区域的面积*/
      let myMeasuerService = new SuperMap.REST.MeasureService(
        this.findMeasureServiceUrl()
      ); //量算服务类，该类负责将量算参数传递到服务端，并获取服务端返回的量算结果
      myMeasuerService.events.on({ processCompleted: this.measureCompleted });

      //对MeasureService类型进行判断和赋值，当判断出是LineString时设置MeasureMode.DISTANCE，否则是MeasureMode.AREA
      if (geometry.CLASS_NAME.indexOf("LineString") > -1) {
        myMeasuerService.measureMode = SuperMap.REST.MeasureMode.DISTANCE;
      } else {
        myMeasuerService.measureMode = SuperMap.REST.MeasureMode.AREA;
      }
      myMeasuerService.processAsync(measureParam); //processAsync负责将客户端的量算参数传递到服务端。
    },
    //绘制控件 deactivate
    deactivateControl() {
      this.drawControls["line"].deactivate();
      this.drawControls["polygon"].deactivate();
    },
    //移除线面图层要素  即自己绘制的线面
    clearFeaturesT() {
      this.layers.lineLayer.removeAllFeatures();
      this.layers.polygonLayer.removeAllFeatures();
      this.layers.pointLayer.removeAllFeatures();
      this.deactivateControl();
      let vectorLayerX = this.getLayer("删除");
      if (vectorLayerX) {
        vectorLayerX.removeAllFeatures();
      }
      let deleteControl = this.map.getControlsBy("type", "delete");
      if (deleteControl && deleteControl[0]) deleteControl[0].deactivate();
    },
    //测量结束调用事件
    measureCompleted(measureEventArgs) {
      let distance = measureEventArgs.result.distance,
        area = measureEventArgs.result.area,
        unit = measureEventArgs.result.unit;
      if (distance != -1) {
        this.$emit("measure", { value: distance, unit: "米" });
      } else if (area != -1) {
        this.$emit("measure", { value: area, unit: "平方米" });
      }
    },
    clearFeatures() {
      //先清除上次的显示结果
      this.layers.vectorLayer.removeAllFeatures();
      this.layers.vectorLayer.refresh();
      this.layers.pointLayer.removeAllFeatures();
      this.layers.pointLayer.refresh();
    },
    //获取超图资源包路径
    getSuperMapSourceLocation() {
      let rootUrl =
        this.$route.path.replace(/\/\w+/g, "../") + superMapDir + "/";
      return rootUrl;
    },
    //隐藏图层上指定attribute中type类型的内容 layername=null|undefined 则清除全部
    //需要提前给feature添加对应的 feature.attributes.type| data.type
    hidenFeatureLayer(layername) {
      let { vectorLayer, labelLayer, qdLayer } = this.layers;
      let opLayers = [vectorLayer, labelLayer, qdLayer];
      opLayers.forEach(layer => {
        if (!layername) {
          layer.removeAllFeatures();
          layer.refresh();
        } else {
          let fs = layer.getFeaturesByAttribute("type", layername);
          if (fs.length > 0) {
            layer.removeFeatures(fs);
          }
        }
      });
    },
    //返回查询的事件回调
    createQueryListener(objParm, cbFn) {
      return {
        processCompleted: queryEventArgs => {
          // 添加图形，文字，可以用： layer.addFeatures([new SuperMap.Feature.Vector(geometry:SuperMap.Geometry.Point,attributes,style)])
          let features = this.queryProcessCompleted(queryEventArgs, feature => {
            if (objParm.type) {
              feature.attributes.type = objParm.type;
              feature.data.type = objParm.type;
              if (objParm.styleSynormal) {
                feature.style = objParm.styleSynormal;
                feature.style_normal = objParm.styleSynormal;
              }
              if (objParm.styleSyactive) {
                feature.style_active = objParm.styleSyactive;
              }
            }
          });
          //cbFn
          if (cbFn) {
            cbFn(true, features, this, queryEventArgs);
          } else {
            this.layers.vectorLayer.addFeatures(features);
          }
        },
        processFailed: e => {
          if (cbFn) {
            cbFn(false, null, this, e); // e.error.errorMsg
          }
        }
      };
    },
    //查询数据集
    //  filterOpt:{name: "gis_gwx@GYGIS_DBST",fields: ["gwxID", "GCMC", "CZMC", "MS"],attributeFilter: "lx = 1"}
    //  sqlOpt:{datasetNames: ["GYGIS_DBST:gis_gwx"]}
    queryDatasets(objParm, cbFn) {
      // var urldata = "http://60.208.113.67:8090/iserver/services/data-gis4/rest/data";
      this.hidenFeatureLayer(objParm.type);
      let getFeatureParam, getFeatureBySQLService, getFeatureBySQLParams;
      getFeatureParam = new SuperMap.REST.FilterParameter(objParm.filterOpt);
      getFeatureBySQLParams = new SuperMap.REST.GetFeaturesBySQLParameters({
        queryParameter: getFeatureParam,
        ...objParm.sqlOpt,
        toIndex: 100000000
      });
      getFeatureBySQLService = new SuperMap.REST.GetFeaturesBySQLService(
        objParm.url,
        {
          eventListeners: this.createQueryListener(objParm, cbFn)
        }
      );
      getFeatureBySQLService.processAsync(getFeatureBySQLParams);
    },
    //根据地图范围查询数据集
    queryDatasetsBybound(objParm, cbFn) {
      this.hidenFeatureLayer(objParm.type);
      let getFeaturesByGeometryService, GetFeaturesByBoundsParameters;
      GetFeaturesByBoundsParameters = new SuperMap.REST.GetFeaturesByBoundsParameters(
        objParm.filterOpt
      );
      getFeaturesByGeometryService = new SuperMap.REST.GetFeaturesByBoundsService(
        objParm.url,
        {
          eventListeners: this.createQueryListener(objParm, cbFn)
        }
      );
      getFeaturesByGeometryService.processAsync(GetFeaturesByBoundsParameters);
    },
    //根据地图范围查询地图服务
    queryMapBybound(objParm, cbFn) {
      this.hidenFeatureLayer(objParm.type);
      let queryService, queryByBoundsParams;
      let queryParam = new SuperMap.REST.FilterParameter(objParm.sqlOpt);
      queryByBoundsParams = new SuperMap.REST.QueryByBoundsParameters({
        queryParams: [queryParam],
        ...objParm.filterOpt
      });
      queryService = new SuperMap.REST.QueryByBoundsService(objParm.url, {
        eventListeners: this.createQueryListener(objParm, cbFn)
      });
      queryService.processAsync(queryByBoundsParams);
    },
    //查询地图数据 filterOpt:{ name: "gis_gj@GYGIS_DBST",fields: ["SmID", "SmX", "SmY", "Gjlx", "gjmc"],attributeFilter: ""}
    //查询的地图服务url，过滤条件 回调函数cbFn(success,features,VueSuperMap,rawArg)
    //featureType 对应 feature.attributes.type 如果设置了该值，可以针对性的删除features
    queryMapLayer(objParm, cbFn) {
      // let mapurl = "http://60.208.113.67:8090/iserver/services/map-gis4/rest/maps/370829";
      this.hidenFeatureLayer(objParm.type);
      let queryParam, queryBySQLParams, queryBySQLService;
      queryParam = new SuperMap.REST.FilterParameter(objParm.filterOpt);
      queryBySQLParams = new SuperMap.REST.QueryBySQLParameters({
        queryParams: [queryParam]
      });
      queryBySQLService = new SuperMap.REST.QueryBySQLService(objParm.url, {
        eventListeners: this.createQueryListener(objParm, cbFn)
      });
      queryBySQLService.processAsync(queryBySQLParams);
    },
    //查询结束后的回调。  回调结果中，features位置不一定一样，需要自己debugger
    queryProcessCompleted(queryEventArgs, onGetFeature) {
      let features = []; //arg.result.features:[]     arg.result.recordsets:[].features
      let i,
        j,
        feature,
        result = queryEventArgs.result;
      if (result && result.features) {
        for (i = 0; i < result.features.length; i++) {
          feature = result.features[i];
          features.push(feature);
          if (onGetFeature) {
            onGetFeature(feature);
          }
        }
      }
      if (result && result.recordsets) {
        for (i = 0; i < result.recordsets.length; i++) {
          if (result.recordsets[i].features) {
            for (j = 0; j < result.recordsets[i].features.length; j++) {
              feature = result.recordsets[i].features[j];
              features.push(feature);
              if (onGetFeature) {
                onGetFeature(feature);
              }
            }
          }
        }
      }
      return features;
    },
    CheckYxt() {
      this.layers.layerDT.setVisibility(false); //地图
      this.layers.layerDT_1.setVisibility(false); //地图
      this.layers.layer1.setVisibility(true); //卫图
      this.layers.layer1_1.setVisibility(true); //卫图
      this.layers.layer1_2.setVisibility(true); //卫图
      this.layers.layerdx.setVisibility(false); //地形
      this.layers.layerdxbz.setVisibility(false); //地形标注
      this.map.setBaseLayer(this.layers.layer1);
      this.map.setBaseLayer(this.layers.layer1_1);
    },
    CheckSlt() {
      this.layers.layerDT.setVisibility(true); //地图
      this.layers.layer1.setVisibility(false); //卫图
      this.layers.layerDT_1.setVisibility(true); //地图
      this.layers.layer1_1.setVisibility(false); //卫图
      this.layers.layer1_2.setVisibility(false); //卫图
      this.layers.layerdx.setVisibility(false); //地形
      this.layers.layerdxbz.setVisibility(false); //地形标注
      this.map.setBaseLayer(this.layers.layerDT);
      this.map.setBaseLayer(this.layers.layerDT_1);
    },
    CheckDxt() {
      this.layers.layerDT.setVisibility(false); //地图
      this.layers.layer1.setVisibility(false); //卫图
      this.layers.layerDT_1.setVisibility(false); //地图
      this.layers.layer1_1.setVisibility(false); //卫图
      this.layers.layer1_2.setVisibility(false); //卫图
      this.layers.layerdx.setVisibility(true); //地形
      this.layers.layerdxbz.setVisibility(true); //地形标注
      this.map.setBaseLayer(this.layers.layerdx);
      this.map.setBaseLayer(this.layers.layerdxbz);
    },
    getLayer(layername) {
      let layers = this.map.getLayersByName(layername);
      if (layers.length > 0) {
        return layers[0];
      }
      return null;
    },
    //定义 handleMeasurements 函数，触发 measurepartial 事件会调用此函数
    //事件参数 event 包含了测量要素 geometry 信息
    handleMeasurements(event) {
      let vectorLayerTmp = this.getLayer("临时");
      if (!vectorLayerTmp) {
        vectorLayerTmp = new SuperMap.Layer.Vector("临时");
        this.map.addLayer(vectorLayerTmp);
      }
      let unit = this.getunit(event);
      //获取传入参数 event 的 geometry 信息
      let geometry = event.geometry;
      //获取传入参数 event 的 type 信息（click指示的是点击事件，move指示的是移动事件）
      let type = event.type;
      if (type == "click") {
        //当==2时为起点
        if (geometry.components.length == 2) {
          let pointStart = new SuperMap.Geometry.Point(
            geometry.components[geometry.components.length - 1].x,
            geometry.components[0].y
          );
          let f = new SuperMap.Feature.Vector();
          f.geometry = pointStart;
          f.style = {
            label: "起点",
            fontColor: "red",
            strokeColor: "red",
            strokeOpacity: 1,
            fillColor: "#ffffff",
            labelXOffset: 20,
            labelYOffset: 20,
            pointRadius: 4
          };
          f.attributes.data = this.drownnum;
          this.layers.pointLayer.addFeatures(f);
        } else {
          //单机节点
          let pointStart = new SuperMap.Geometry.Point(
            geometry.components[geometry.components.length - 1].x,
            geometry.components[geometry.components.length - 1].y
          );
          let f = new SuperMap.Feature.Vector();
          f.geometry = pointStart;
          f.style = {
            label: +event.measure.toFixed(1) + unit,
            fontColor: "red",
            strokeColor: "red",
            strokeOpacity: 1,
            fillColor: "#ffffff",
            labelXOffset: 20,
            labelYOffset: 20,
            pointRadius: 4
          };
          f.attributes.data = this.drownnum;
          this.layers.pointLayer.addFeatures(f);
        }
      } else {
        //临时显示点信息
        vectorLayerTmp.removeAllFeatures();
        let pointStart = new SuperMap.Geometry.Point(
          geometry.components[geometry.components.length - 1].x,
          geometry.components[geometry.components.length - 1].y
        );
        let f = new SuperMap.Feature.Vector();
        f.geometry = pointStart;
        f.style = {
          label: +event.measure.toFixed(1) + unit,
          fontColor: "red",
          strokeColor: "red",
          strokeOpacity: 1,
          fillColor: "#ffffff",
          labelXOffset: 20,
          labelYOffset: 20,
          pointRadius: 4
        };
        f.attributes.data = this.drownnum;
        vectorLayerTmp.addFeatures(f);
      }
    },
    //定义 handleMeasurements 函数，触发 measure 事件会调用此函数
    //事件参数 event 包含了测量要素 geometry 信息
    handleMeasure(event) {
      let unit = this.getunit(event);
      //获取传入参数 event 的 geometry 信息
      let geometry = event.geometry;
      //order=1时表示距离测量，order=2时表示面积测量
      let order = event.order;
      let vectorLayerTmp = this.getLayer("临时");
      if (vectorLayerTmp) {
        vectorLayerTmp.removeAllFeatures();
      }
      let qu, pointStart, fillOpacity, drowlayer;
      let f = new SuperMap.Feature.Vector();
      this.deactivateControl();
      if (order == 1) {
        //获取当前坐标点
        pointStart = new SuperMap.Geometry.Point(
          geometry.components[geometry.components.length - 1].x,
          geometry.components[geometry.components.length - 1].y
        );
        f.geometry = pointStart;
        f.style = {
          label: "总长：" + event.measure.toFixed(1) + unit,
          fontColor: "red",
          strokeColor: "red",
          strokeOpacity: 1,
          fillColor: "#ffffff",
          labelXOffset: 20,
          labelYOffset: 20,
          pointRadius: 4
        };
        f.attributes.data = this.drownnum;
        //线颜色的透明度
        fillOpacity = 1;
        drowlayer = this.layers.lineLayer;
        //保存之前绘制的节点用于显示其他样式
        let points = [];
        for (let i = 0; i < event.geometry.components.length; i++) {
          points.push(
            new SuperMap.Geometry.Point(
              geometry.components[i].x,
              geometry.components[i].y
            )
          );
        }
        qu = new SuperMap.Geometry.LineString(points);
      } else {
        //获取绘制面的中心点
        let center = geometry.getCentroid();
        pointStart = new SuperMap.Geometry.Point(center.x, center.y);
        f.geometry = pointStart;
        f.style = {
          label: "总面积：" + event.measure.toFixed(1) + unit,
          fontColor: "red",
          strokeColor: "red",
          strokeOpacity: 1,
          fillColor: "#ffffff",
          labelXOffset: 20,
          labelYOffset: 20
        };
        f.attributes.data = this.drownnum;
        qu = geometry;
        //面颜色的透明度
        fillOpacity = 0.3;
        drowlayer = this.layers.polygonLayer;
      }
      //保留线样式
      let ff = new SuperMap.Feature.Vector();
      ff.geometry = qu;
      ff.style = {
        strokeColor: "#FC854D",
        strokeWidth: 2.5,
        //pointerEvents: "visiblePainted",
        fillColor: "#FC854D",
        fillOpacity: fillOpacity
      };
      ff.attributes.data = this.drownnum;
      drowlayer.addFeatures(ff);
      this.layers.pointLayer.addFeatures(f);
      //克隆坐标点 用于显示关闭按钮
      let pointCopy = pointStart.clone();
      //删除按钮
      let vectorLayerX = this.getLayer("删除");
      if (!vectorLayerX) {
        vectorLayerX = new SuperMap.Layer.Vector("删除");
        this.map.addLayer(vectorLayerX);
      }
      let fc = new SuperMap.Feature.Vector();
      fc.geometry = pointCopy;
      fc.style = {
        //externalGraphic: require('src/assets/img/onemap/delete.png'),
        strokeColor: "red",
        graphicWidth: 16,
        graphicHeight: 16,
        strokeOpacity: 1,
        fillColor: "#ffffff",
        graphicXOffset: 10,
        pointRadius: 4
      };
      fc.attributes.data = this.drownnum;
      vectorLayerX.addFeatures(fc);
      //添加删除事件
      let deleteControl = new SuperMap.Control.SelectFeature(vectorLayerX, {
        onSelect: this.onFeatureSelect
      });
      //激活删除事件
      this.map.addControl(deleteControl);
      deleteControl.activate();
      deleteControl.type = "delete";
      vectorLayerX.addFeatures(fc);
    },
    //点击删除量算按钮
    onFeatureSelect(feature) {
      let data = feature.attributes.data;
      let vectorLayerX = this.getLayer("删除");
      if (vectorLayerX) {
        vectorLayerX.removeFeatures(
          vectorLayerX.getFeaturesByAttribute("data", data)
        );
      }
      let deleteControl = this.map.getControlsBy("type", "delete");
      this.layers.pointLayer.removeFeatures(
        this.layers.pointLayer.getFeaturesByAttribute("data", data)
      );
      this.layers.lineLayer.removeFeatures(
        this.layers.lineLayer.getFeaturesByAttribute("data", data)
      );
      this.layers.polygonLayer.removeFeatures(
        this.layers.polygonLayer.getFeaturesByAttribute("data", data)
      );
      deleteControl[0].deactivate();
    },
    //获取单位
    getunit(event) {
      //单位转换
      let unitString = "";
      if (event.order == 1) {
        if (event.units == "m") {
          unitString = "米";
        } else if (event.units == "km") {
          unitString = "公里";
        } else {
          unitString = "";
        }
      } else {
        if (event.units == "m") {
          unitString = "平方米";
        } else if (event.units == "km") {
          unitString = "平方公里";
        } else {
          unitString = "";
        }
      }
      return unitString;
    }
  }
};
</script>

<style lang="scss" scoped>
/deep/#chicken_close {
  right: 65px !important;
  top: -83px !important;
  border-radius: 50%;
}
.VueSuperMap {
  position: relative;
  overflow: hidden;
  height: 100%;

  .tool-bar {
    position: absolute;
    right: 0px;
    top: 0px;
    z-index: 1249;
    height: 26px;
    background: rgba(0, 0, 0, 0.5);

    .tool-bar-item {
      width: 26px;
      height: 26px;
      float: left;
      cursor: pointer;
      opacity: 0.8;
      box-sizing: border-box;
      border: solid 1px rgba(255, 255, 255, 0.5);

      &:hover {
        opacity: 1;
      }
    }

    &:after {
      display: block;
      clear: both;
    }
  }
}
</style>
