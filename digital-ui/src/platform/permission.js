import router from './router'
import store from './store'
import {Message} from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import {getToken} from '@/platform/utils/auth'

NProgress.configure({showSpinner: false})

const whiteList = ['/401', '/404', '/login', '/auth-redirect', '/bind', '/register',"/test/jiay","/system/configurationManagement/appversion/download"];

router.beforeEach((to, from, next) => {
  NProgress.start();
  if (getToken()) {
    /* has token*/
    if (to.path === '/login') {
      next({path: '/index'})
      pageDone();
    } else {
      if (store.getters.roles.length === 0) {
        // 判断当前用户是否已拉取完user_info信息
        store.dispatch('GetInfo').then(res => {
          // 拉取user_info
          const roles = res.roles;
          store.dispatch('GenerateMenus', {roles}).then(accessRoutes => {
            // 测试 默认静态页面
            // store.dispatch('permission/generateRoutes', { roles }).then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            // router.addRoutes(accessRoutes) // 动态添加可访问路由表
            // next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
            checkInMenu(to, from, next, () => {
              next(); // hack方法 确保addRoutes已完成
            });
          })
        })
          .catch(err => {
            store.dispatch('FedLogOut').then(() => {
              //Message.error(err);
              next({path: '/login'})
            })
          })
      } else {
        checkInMenu(to, from, next, () => {
          next()
        });
        // 没有动态改变权限的需求可直接next() 删除下方权限判断 ↓
        // if (hasPermission(store.getters.roles, to.meta.roles)) {
        //   next()
        // } else {
        //   next({ path: '/401', replace: true, query: { noGoBack: true }})
        // }
        // 可删 ↑
      }
    }
  } else {
    // 没有token
    if (whiteList.indexOf(to.path) !== -1) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${to.fullPath}`); // 否则全部重定向到登录页
      pageDone();
    }
  }
});

router.afterEach(() => {
  pageDone();
});

function checkInMenu(to, from, next, cb) {
  //shouldInMenuList
  let shouldInMenuList = to.matched.some((item) => item.meta && item.meta.shouldInMenuList);
  if (!shouldInMenuList) {
    cb();
  } else {
    let menuExists = !!store.state.permission.menusMap[to.fullPath];
    if (menuExists) {
      cb();
    } else {
      //当前页面是菜单页面，但用户不存在该菜单，用户则不能访问
      next('/404');
      pageDone();
    }
  }
}

function pageDone() {
  NProgress.done();
  let loaderDiv = document.getElementById("loader-wrapper");
  if (loaderDiv) {
    loaderDiv.style.opacity = '0';
    setTimeout(() => {
      loaderDiv.parentElement && loaderDiv.parentElement.removeChild(loaderDiv)
    }, 500);
  }
}
