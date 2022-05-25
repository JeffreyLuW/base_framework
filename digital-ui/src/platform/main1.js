import Vue from "vue";
//import "normalize.css/normalize.css"; // a modern alternative to CSS resets
import Element from "element-ui";
import "./assets/styles1/element-variables.scss";
import "./assets/styles1/index.scss"; // global css
import "./assets/styles1/ruoyi.scss"; // ruoyi css
import App from "./App";
import store from "./store";
import router from "./router";
import permission from "./directive/permission";
import * as PermChecker from "./utils/permission.js";
import "./assets/icons"; // icon
import "./permission"; // permission control
import { getDicts } from "./api/system/dict/data";
import { getConfigKey } from "./api/system/config";
import "./utils/image_viewer.js";
import app_starer from "./utils/app_starer.js";
import "./assets/font/MyIconfont";

import "./assets/font/font.scss";
import "./plugins"
import {
  addDateRange,
  download,
  downloadResource,
  handleTree,
  parseTime,
  resetForm,
  selectDictLabel,
  selectDictLabels
} from "./utils/ruoyi";
import globalComponent from "./components1/global/index";
import render_ext from "./utils/render_ext";
import simple_util from "./utils/simple_util";
import custom_rules from "./utils/custom_rules.js";
import common_mixin from "./mixin/common.js";
//import dialog_util from "./utils/dialog_util.js";
//import win_util from "./utils/win_util.js";
//import ThemeTemplate from "./utils/ThemeTemplate.js";
import AppExt from "../app.js";
//import formHelper from "./utils/formhelper.js";
import util from "./utils/util.js";

import IntroJs from "intro.js";
import "intro.js/introjs.css";
import VueIntro from "vue-introjs";
Vue.use(VueIntro);

// //火星3d
// import "mars3d/dist/mars3d.css";
// import * as mars3d from "mars3d";
// import "mars3d-space";
// import "mars3d-tdt";
// Vue.prototype.mars3d = mars3d;

// 引入videojs
import Video from "video.js";
import "video.js/dist/video-js.css";
Vue.prototype.$video = Video;
import hls from "videojs-contrib-hls";
import plugins from "./plugins";
Vue.use(hls); // hls流播放工具


Vue.use(util);
// 全局方法挂载
Vue.prototype.getDicts = getDicts;
Vue.prototype.getConfigKey = getConfigKey;
Vue.prototype.parseTime = parseTime;
Vue.prototype.resetForm = resetForm;
Vue.prototype.addDateRange = addDateRange;
Vue.prototype.selectDictLabel = selectDictLabel;
Vue.prototype.selectDictLabels = selectDictLabels;
Vue.prototype.download = download;
Vue.prototype.downloadResource = downloadResource;
Vue.prototype.handleTree = handleTree;
//常用工具
Vue.prototype.$util = simple_util;
//Vue.prototype.$dialog = dialog_util;
//Vue.prototype.$win = win_util;
//Vue.prototype.$formHelper = formHelper;
Vue.prototype.$perm = PermChecker;
Vue.prototype.$bus = new Vue();

Vue.prototype.msgSuccess = function(msg) {
  this.$message({ showClose: true, message: msg, type: "success" });
};

Vue.prototype.msgError = function(msg) {
  this.$message({ showClose: true, message: msg, type: "error" });
};

Vue.prototype.msgInfo = function(msg) {
  this.$message.info(msg);
};

//全局指令
// 防重复点击
Vue.directive("preventReClick", {
  inserted(el, binding) {
    el.addEventListener("click", () => {
      if (!el.disabled) {
        el.disabled = true;
        setTimeout(() => {
          el.disabled = false;
        }, binding.value || 3000);
      }
    });
  }
});

//全局组件注册
Vue.use(plugins);
Vue.use(globalComponent);
Vue.mixin(common_mixin);
Vue.use(permission);
Vue.use(custom_rules);
//监控组件渲染
Vue.use(render_ext, { log: false });

Vue.use(Element, {
  size: simple_util.localStorage("size") || "medium" // set element-ui default size medium small
});

Vue.config.productionTip = false;
AppExt.before(Vue, router, store);

app_starer(preparedConfig => {
  //不使用主题色设置主题时，默认使用主题模板
  // if (!window.__theme_color) {
  //   Vue.use(ThemeTemplate, {
  //     auto: true,
  //     defaultTheme: "light",
  //     preparedTheme: preparedConfig && preparedConfig.preparedTheme
  //   });
  // }
  let vue = (simple_util.rootVue = new Vue({
    el: "#app",
    router,
    store,
    render: h => h(App)
  }));
  //开发下暴露vue对象。方便开发调试。
  if (process.env.NODE_ENV === "development") {
    window.vue = vue;
  }
  AppExt.after(vue);
});
