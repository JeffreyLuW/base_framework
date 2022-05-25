import {prepareConfig} from '../api/login';
import store from '../store/index';
//有时程序需要先获取一些配置，比如登录背景、文字、服务器地址等信息，然后才能正常渲染页面。
export default function (next) {
  let result = null;
  prepareConfig().then((rs) => {
    result = rs;
    if (rs.sysThemes && rs.sysThemes.length) {
      let preparedTheme = {};
      rs.sysThemes.forEach((r) => {
        r.vars = JSON.parse(r.vars);
        preparedTheme[r.key] = r;
      });
      rs.preparedTheme = preparedTheme;
    }
    store.commit("settings/APPLY_SYS_CONFIG", rs);
  }).finally(() => {
    next(result);
  })
}
