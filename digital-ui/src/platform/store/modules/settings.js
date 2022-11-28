// import variables from '@/platform/assets/styles/element-variables.scss'
import defaultSettings from '@/platform/settings'
import simple_util from '../../utils/simple_util.js';
import theme_manager from '../../utils/theme_manager.js';

const {sideTheme, showSettings, topNav, tagsView, fixedHeader, sidebarLogo, dynamicTitle} = defaultSettings

let oldSettings = simple_util.localStorage("__store__settings") || {};
let theme = oldSettings.theme || window.__union_theme;
if (theme&&window.__theme_color) {
  //需要设置对应的主题
  theme_manager.applyTheme(theme);
}

const state = {
  title: defaultSettings.title,
  name: null,
  theme: theme,
  sideTheme: oldSettings.sideTheme || sideTheme,
  showSettings: showSettings,
  topNav: oldSettings.topNav === undefined ? topNav : oldSettings.topNav,
  tagsView: tagsView,
  fixedHeader: fixedHeader,
  sidebarLogo: sidebarLogo,
  serverUrl: null,//服务器url
  sysConfigs: {},//额外的一些全局参数配置
  dynamicTitle: oldSettings.dynamicTitle === undefined ? dynamicTitle : oldSettings.dynamicTitle,
  ...oldSettings
}

const mutations = {
  CHANGE_SETTING: (state, {key, value}) => {
    if (state.hasOwnProperty(key)) {
      state[key] = value;
      simple_util.localStorage("__store__settings", state);
    }
  },
  APPLY_SYS_CONFIG: (state, config) => {
    state.serverUrl = config.serverUrl;
    let sysConfigsMap = {};
    if (config.sysConfigs) {
      config.sysConfigs.forEach((c) => {
        //中横线的将被忽略
        if (c.configValue && c.configValue !== '-')
          sysConfigsMap[c.configKey] = c;
      });
    }
    //当前配置 "page.login.bg", "page.sys.name", "page.sys.logo", "page.sys.favicon", "pwd.security.rules"
    state.sysConfigs = Object.freeze(sysConfigsMap);
    let nameConfig = sysConfigsMap['page.sys.name'];
    if (nameConfig && nameConfig.configValue !== "|") {
      state.name = nameConfig.configValue;
    }
    //设置html-title:tab标签的标题。
    let titleConfig = sysConfigsMap['page.sys.title'];
    if (titleConfig && titleConfig.configValue) {
      state.title = titleConfig.configValue;
      document.querySelector("title").innerText = titleConfig.configValue;
    }
    //favicon的图片
    let faviconConfig = sysConfigsMap['page.sys.favicon'];
    if (faviconConfig) {
      let url = simple_util.fixFileUrl(faviconConfig.configValue, config.serverUrl);
      if (url) document.getElementById("favicon").href = url;
    }
    simple_util.localStorage("__store__settings", state);
  },
};

const actions = {
  changeSetting({commit}, data) {
    commit('CHANGE_SETTING', data)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

