//说明: 路由传值建议只传简单值，可以最大化的方便copy地址在不同用户不同浏览器打开而不出bug

//routesMap 根据 path 来cache   routesNameMap根据name来cache
function getComponentPath(id) {
  let index1 = id.indexOf('/');
  let index2 = id.lastIndexOf('.');
  let rs = id.substring(index1, index2);
  return rs;
}

function getComponentParentPath(id) {
  let index1 = id.indexOf('/');
  let index2 = id.lastIndexOf('/');
  if (index1 === index2) return '';
  return id.substring(index1, index2);
}

// path之间 只能去重，路径展示更友好些。
function autoSmartPath(path) {
  let arr = path.split('/');
  //如果只有一级 不处理
  if (arr.length <= 1) {
    return path;
  }
  let newPathArr = [];
  for (let i = arr.length - 1; i > 0; i--) {
    let curr = arr[i];
    let pPath = arr[i - 1];
    if (curr.startsWith(pPath)) {
      curr = curr.substring(pPath.length);
    }
    if (curr)
      newPathArr.unshift('/' + curr);
  }
  return newPathArr.join('');
}

// 递归查找上级路由。
function findParentRoute(routesMap, parentPath) {
  if (!parentPath) return null;
  //处理层级，挂载上级route
  let parentRoute = null;
  let spliterIndex = parentPath.lastIndexOf("/");
  while (parentPath && parentPath.length > 1) {
    parentRoute = routesMap[parentPath];
    if (parentRoute) break;
    spliterIndex = parentPath.lastIndexOf("/");
    if (spliterIndex > 0)
      parentPath = parentPath.substring(0, spliterIndex);
    else break;
  }
  return parentRoute;
}

function requiredVal(componentConfig) {
  return componentConfig.default || componentConfig;
}

/**
 *  自动搜索指定目录下的文件生成异步路由。文件名就是路由name,根据目录组织路由嵌套(可以有多级嵌套)。
 *  如果Home.vue有子路由，请建Home文件夹，添加HomeSub.vue页面。
 * @param requireContext
 * @param options {ignoreFn(id, name, path, parentPath),postDo(routesArray,routesNameMap,routesMap)} 允许自由忽略或者修改生成的routes配置对象。
 * @returns RouteConfig[]
 */
//let requireContext = require.context('./views/', true, /.vue$/, 'lazy');
function autoRoutes(requireContext, options = {}) {
  let {ignoreFn, postDo, asyncComponentFn, smartPath = false} = options;
  let routesMap = {};
  let routesNameMap = {};
  requireContext.keys().sort().forEach((id) => {
    let name = id.replace(/[\s\S]+?\/(\w+).vue/, '$1');
    let path = getComponentPath(id);
    let parentPath = getComponentParentPath(id);
    //如果忽略指定的文件，则跳过
    if (ignoreFn && ignoreFn(id, name, path, parentPath)) {
      return;
    }
    let asyncComponent = asyncComponentFn ?
      asyncComponentFn(() => requiredVal(requireContext(id)))
      : () => requiredVal(requireContext(/* webpackChunkName: "auto-page" */id));
    let rsPath = smartPath ? autoSmartPath(path) : path;
    name = rsPath.replace(/\//g, "_").substring(1);
    let routeInfo = {
      parentPath,
      file: id,
      route: {
        path: rsPath,
        name: name,
        component: asyncComponent,
      }
    };
    routesMap[path] = routeInfo;
    if (routesNameMap[name]) {
      console.warn('发现重名路由', name, id);
    }
    routesNameMap[name] = routeInfo;
    //处理层级，挂载上级route
    let parentRoute = findParentRoute(routesMap, parentPath);// routesMap[parentPath];
    if (parentRoute) {
      //mark has parent
      routeInfo.hasParent = true;
      if (!parentRoute.route.children) parentRoute.route.children = [];
      // auto add to parent children
      parentRoute.route.children.push(routeInfo.route);
    }
  });
  let routesArray = Object.values(routesMap).filter((routeInfo) => {
    return !routeInfo.hasParent;
  }).map((routeInfo) => {
    return routeInfo.route
  });
  if (postDo) {
    let newArr = postDo(routesArray, routesNameMap, routesMap);
    if (newArr) routesArray = newArr;
  }
  return routesArray;
}

/*
//示例： 传值建议只传简单值。
  let routes = routeUtils.autoRoutes(require.context('./views/', true, /.vue$/, 'lazy'), {
    ignoreFn: (id, name, path, parentPath) => {
       if (name.startsWith("__")) return true;
        return false;
    },
    postDo: (routesArray, routesNameMap, routesMap) => {
        //Home作为默认首页 /
        routesNameMap.Home.route.alias = ['/'];
    }
});
 */
export default {
  autoRoutes,
}
