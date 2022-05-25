import route_util from '../utils/route_util.js';
import Layout from "./layout";

let isDev = process.env.NODE_ENV === 'development';
//忽略的 不自动生成的路由。
let ignoredRoute = [
  "/404", "/redirect", "/system/user/profile/index", "/system/dict/data"
];
//顶级路由，不会包含首页布局、菜单导航。该组件就是整个页面。 /test/页面 默认都是顶级路由。
let topLevelRoute = (route) => {
  return (["/401", "/login", "/redirect","/OneMap","/system/configurationManagement/appversion/download"].indexOf(route.path) > -1 || route.path.startsWith("/test/"));
};
//内容区路由,位于菜单下|右方 但不在菜单中，不受菜单列表控制
let notMenuRoute = [
  "/system/user/profile/index", "/index",
];
let counter = 0;

//获取布局路由。
function getLayoutRoute(shouldInMenuList = true, children) {
  return {
    name: "auto_root_layout_" + (counter++), path: "",
    component: Layout,
    meta: {
      shouldInMenuList: shouldInMenuList
    },
    children: children || []
  };
}

function ignoreFn(id, name, path, parentPath) {
  if (ignoredRoute.indexOf(path) > -1 || path.indexOf("__") > -1 || name.startsWith("__")) return true;
  //非dev环境下忽略test下的页面。
  if (!isDev && path.startsWith("/test/")) return true;
  return false;
}

function postDo(routesArray, routesNameMap, routesMap) {
  // console.log(routesNameMap);
  let rsRoutes = [];
  let layoutRoute = getLayoutRoute(true);
  //顶级路由
  routesArray.forEach((route) => {
    if (topLevelRoute(route)) {
      rsRoutes.push(route);
    } else {
      if (notMenuRoute.indexOf(route.path) > -1) {
        rsRoutes.push(getLayoutRoute(false, [route]));
      } else {
        //普通菜单路由
        layoutRoute.children.push(route);
      }
    }
  });
  rsRoutes.push(layoutRoute);
  return rsRoutes;
}

//自动搜索路由。__开头的将会被忽略。
let routes1 = route_util.autoRoutes(require.context('../views/', true, /.vue$/, 'lazy'), {
  ignoreFn, postDo
});
let routes2 = route_util.autoRoutes(require.context('../../views/', true, /.vue$/, 'lazy'), {
  ignoreFn, postDo
});
let routes = routes1.concat(routes2);
export default routes;
