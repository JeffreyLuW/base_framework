import util from './simple_util.js';
import Vue from 'vue';
import router from '../router/index.js';
// import AutoDialog from '../components/AutoDialog/index.vue';
import Win from '../components2/Win/index.vue';
import DataFooter from '../components2/DataFooter/index.vue';


let winList = [];
router.beforeEach((to, from, next) => {
  winList.forEach((winComponent) => {
    winComponent.$destroy();
  });
  winList = [];
  next();
});

//路由切换时，关闭对话框
function autoCloseOrRouteChange(winComponent) {
  winList.push(winComponent);
  winComponent.$on("hook:beforeDestroy", () => {
    util.arrRemove(winList, winComponent);
  });
}

//处理
function handleAutoOkClose(onAutoOk, btnKey, winComponent, innerNext) {
  if (!onAutoOk || btnKey !== "ok") {
    innerNext();
    return false;
  }
  let onValidate = (rs) => {
    //没有提供next函数，则自动关闭
    if (onAutoOk.length < 3) {
      onAutoOk(rs, winComponent);
      innerNext();
    } else {
      //提供next函数，手动关闭。
      onAutoOk(rs, winComponent, innerNext);
    }
  };
  //如果内部组件提供了validate函数，则主动调用。
  if (winComponent.$component.validate && typeof winComponent.$component.validate === "function") {
    winComponent.$component.validate(onValidate);
  } else {
    onValidate(undefined);
  }
  return true;
}

/**
 * 手动弹窗。除 component  propsData onInit:(component, close) 外，其他属性参考 Win 组件
 * 如果想自己处理关闭事件，可以添加箭头函数 onAutoOk: (rs, winComponent, next)=>{};通过winComponent.$component 可以获取内部component的实例。
 * 注意，如果希望窗口在当前组件销毁时关闭，需要额外配置 bind:this .
 * 默认情况下，路由切换时，会关闭该方法产生的所有窗口
 * @param winOptions  {component,propsData,onInit(componentInstance), ...Win.options}
 * @param currVueComponent
 * @return {*}
 */
function show(winOptions) {
  let onAutoOk = winOptions.onAutoOk;
  let winComponent = winOptions.component;
  let winPropsData = winOptions.propsData;
  let winOnInit = winOptions.onInit;
  let bindTarget = winOptions.bind;//当前对话框生命周期绑定某个vue实例。
  let footerButtons = winOptions.buttons;// confirm  ok |  [{type: 'primary', text: '确定', key: 'ok'}]; 用于渲染 DataFooter
  //自动处理点击OK的操作。 如果内部组件有validate方法，则会调用validate的回调。 onAutoOk(rs,winComponent,next) .next不提供时，将自动关闭，否则手动调用next关闭
  //一般不与 onCloseFn 同时使用。
  winOptions.component = undefined;
  winOptions.propsData = undefined;
  winOptions.onInit = undefined;
  winOptions.bind = undefined;
  winOptions.buttons = undefined;
  winOptions.onAutoOk = undefined;

  return util.createVueAndMount(Win, winOptions, (component, close) => {
    autoCloseOrRouteChange(component);
    if (bindTarget) {
      bindTarget.$on("hook:beforeDestroy", close);
    }
    if (winComponent) {
      let _onEmptyBody = component.onEmptyBody;
      component.onEmptyBody = (body, win) => {
        _onEmptyBody && _onEmptyBody(body, win);
        if (typeof winComponent === "string") {
          winComponent = Vue.component(winComponent);
        }
        if (!winComponent) {
          console.warn("no win component found!!");
          return;
        }
        component.$component = util.createVueAndMount(winComponent, winPropsData, winOnInit, body);

        if (footerButtons) {
          //指定高度时，footer固定在底部。其他内容，应该限制最大高度。
          if (winOptions.height && component.$component.$el) {
            component.$component.$el.style.maxHeight = "calc(100% - 44px)";
            component.$component.$el.style.overflowY = "auto";
          }
          component.$footer = util.createVueAndMount(DataFooter,
            {
              buttons: footerButtons, fixed: !!winOptions.height
            }, null, body);
          component.$footer.$on("click", (key, item) => {
            //自动检验通用表单验证?
            handleAutoOkClose(onAutoOk, key, component, () => {
              component.show = false;
            });
          });
        }
        component.$once("hook:beforeDestroy", () => {
          component.$component && component.$component.$destroy();
          component.$footer && component.$footer.$destroy();
        });
      };
    }
    component.$on("close", () => {
      component.show = false;
    });
    //动画关闭后 直接销毁。
    component.$on("closed", () => {
      close();
    });
    requestAnimationFrame(() => {
      component.show = true;
    });
  });
}

export default {show}
