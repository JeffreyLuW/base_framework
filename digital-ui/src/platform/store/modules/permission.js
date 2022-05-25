import { constantRoutes } from '@/platform/router'
import { getRouters } from '@/platform/api/menu'
import Layout from '@/platform/layout1/index'
import simple_util from '../../utils/simple_util.js';
import path from 'path';

const permission = {
  state: {
    routes: [],
    addRoutes: [],
    menus: [],//当前的菜单
    menusMap: {},//path-menuItem的匹配关系
    rootMenuMap: {},//保存每个路径对应的根|一级菜单
    menuItems: [],
    topbarRouters: [],
    sidebarRouters: []
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.addRoutes = routes
      state.routes = constantRoutes.concat(routes)
    },
    SET_TOPBAR_ROUTES: (state, routes) => {
      state.topbarRouters = routes
    },
    SET_SIDEBAR_ROUTERS: (state, routes) => {
      state.sidebarRouters = routes
    },
    SET_MENUS: (state, menus) => {
      state.menus = Object.freeze(menus);
      let menusMap = {};
      let menuItems = [];
      simple_util.eachTree(menus, 'children', (item, index, itemArray, parent, level) => {
        let basePath = parent ? parent._fullPath || parent.path : undefined;
        let routePath = item.path;
        let rsPath = null;
        if (isExternal(routePath)) {
          rsPath = routePath
        } else if (isExternal(basePath)) {
          rsPath = basePath
        } else {
          if (basePath) {
            rsPath = path.resolve(basePath, routePath)
          } else {
            rsPath = routePath;
          }
        }
        item._fullPath = rsPath;
        menusMap[rsPath] = item;
        menuItems.push(item);
      });
      let rootMenuMap = {};
      menus.forEach((rootMenu) => {
        simple_util.eachTree(rootMenu, 'children', (item, index, itemArray, parent, level) => {
          rootMenuMap[item._fullPath] = rootMenu;
        });
      });
      state.menusMap = Object.freeze(menusMap);
      state.rootMenuMap = Object.freeze(rootMenuMap);
      state.menuItems = Object.freeze(menuItems);
    },
    SET_SIDE_MENUS: (state, menu) => {
      state.menus = Object.freeze(menu);
    },
  },
  actions: {
    // 生成路由
    GenerateRoutes({ commit }) {
      return new Promise(resolve => {
        // 向后端请求路由数据
        getRouters().then(res => {
          const accessedRoutes = filterAsyncRouter(res.data)
          commit('SET_SIDEBAR_ROUTERS', constantRoutes.concat(accessedRoutes))
          commit('SET_TOPBAR_ROUTES', accessedRoutes)
          accessedRoutes.push({ path: '*', redirect: '/404', hidden: true })
          commit('SET_ROUTES', accessedRoutes)
          resolve(accessedRoutes)
        })
      })
    },
    // 生成菜单
    GenerateMenus({ commit }) {
      return new Promise(resolve => {
        // 向后端请求路由数据
        getRouters().then(res => {
          let menus = res.data;
          //添加首页菜单
          menus.unshift(
            /* {
              //path: '/OneMap', component: (resolve) => require(['@/platform/views/Mapdisplay/MapAction'], resolve), name: '一张图',
              path: '/OneMap', component: "index", name: '一张图',
              meta: { title: '一张图', icon: 'druid', noCache: true, affix: true }
            }, */
            {
              path: '/index', component: "index", name: '首页',
              meta: { title: '首页', icon: 'home', noCache: true, affix: true }
            }
          );
          commit('SET_MENUS', menus);
          commit('SET_ROUTES', menus);
          commit('SET_TOPBAR_ROUTES', menus);
          resolve(menus)
        })
      })
    },
  }
}

// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap) {
  return asyncRouterMap.filter(route => {
    if (route.component) {
      // Layout组件特殊处理
      if (route.component === 'Layout') {
        route.component = Layout
      } else {
        route.component = loadView(route.component)
      }
    }
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children)
    }
    return true
  })
}

export const loadView = (view) => { // 路由懒加载
  return (resolve) => require([`@/platform/views/${view}`], resolve)
}

function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

export default permission
