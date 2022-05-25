import util from './simple_util.js';
import Vue from 'vue';
import router from '../router/index.js';
import AutoDialog from '../components2/AutoDialog/index.vue';

let dialogList = [];
router.beforeEach((to, from, next) => {
  dialogList.forEach((dialog) => {
    dialog.$destroy();
  });
  dialogList = [];
  next();
});

//路由切换时，关闭对话框
function autoCloseOrRouteChange(dialog) {
  dialogList.push(dialog);
  dialog.$on("hook:beforeDestroy", () => {
    util.arrRemove(dialogList, dialog);
  });
}

//处理
function handleAutoOkClose(onAutoOk, btnKey, dialog, innerNext) {
  if (!onAutoOk) {
    return false;
  }
  if (btnKey !== "ok") {
    innerNext();
  } else {
    let onValidate = (rs) => {
      //没有提供next函数，则自动关闭
      if (onAutoOk.length < 3) {
        onAutoOk(rs, dialog);
        innerNext();
      } else {
        //提供next函数，手动关闭。
        onAutoOk(rs, dialog, innerNext);
      }
    };
    //如果内部组件提供了validate函数，则主动调用。
    if (dialog.$component.validate && typeof dialog.$component.validate === "function") {
      dialog.$component.validate(onValidate);
    } else {
      onValidate(undefined);
    }
  }
  return true;
}

/**
 * 手动弹窗。除 component  propsData onInit:(component, close) 外，其他属性参考 AutoDialog 组件
 * 如果想自己处理关闭事件，可以添加箭头函数 onCloseFn: (type, dialog, next)=>{};通过dialog.$component 可以获取内部component的实例。
 * 注意，如果希望对话框在当前组件销毁时关闭，需要额外配置 bind:this .
 * 默认情况下，路由切换时，会关闭该方法产生的所有对话框。
 * @param dialogOptions  {component,propsData,onInit(componentInstance), ...AutoDialog.options}
 * @param currVueComponent
 * @return {*}
 */
function show(dialogOptions) {

  let dialogComponent = dialogOptions.component;
  let dialogPropsData = dialogOptions.propsData;
  let dialogOnInit = dialogOptions.onInit;
  let bindTarget = dialogOptions.bind;//当前对话框生命周期绑定某个vue实例。
  //自动处理点击OK的操作。 如果内部组件有validate方法，则会调用validate的回调。 onAutoOk(rs,dialog,next) .next不提供时，将自动关闭，否则手动调用next关闭
  //一般不与 onCloseFn 同时使用。
  let onAutoOk = dialogOptions.onAutoOk;
  dialogOptions.component = undefined;
  dialogOptions.propsData = undefined;
  dialogOptions.onInit = undefined;
  dialogOptions.bind = undefined;

  return util.createVueAndMount(AutoDialog, dialogOptions, (component, close) => {
    autoCloseOrRouteChange(component);
    if (bindTarget) {
      bindTarget.$on("hook:beforeDestroy", close);
    }
    //处理全屏事件。
    component.$on("fullscreen", (fullscreen) => {
      component.fullscreen = fullscreen;
    });
    let _onCloseFn = component.onCloseFn;
    component.onCloseFn = (btnKey, dialog, next) => {
      if (bindTarget) {
        bindTarget.$off("hook:beforeDestroy", close);
      }
      let innerNext = () => {
        component.show = false;
        next();
        close();
      };
      //自动处理ok函数
      if (handleAutoOkClose(onAutoOk, btnKey, dialog, innerNext)) {
        return;
      }
      if (!_onCloseFn || _onCloseFn.length < 3) {
        _onCloseFn && _onCloseFn(btnKey, dialog);
        innerNext();
      } else {
        _onCloseFn(btnKey, dialog, innerNext);
      }
    };
    //监听内容的声明周期。实例化内容组件
    let _contentLifeFn = component.contentLifeFn;
    component.contentLifeFn = (lifeName, jsx) => {
      _contentLifeFn && _contentLifeFn(lifeName, jsx);
      if (lifeName === "mounted") {
        if (typeof dialogComponent === "string") {
          dialogComponent = Vue.component(dialogComponent);
        }
        if(!dialogComponent){
          console.warn("no dialog component found!!");
          return;
        }
        component.$component = util.createVueAndMount(dialogComponent, dialogPropsData, dialogOnInit, component.$refs.contentDiv);
      } else if (lifeName === 'beforeDestroy') {
        component.$component && component.$component.$destroy();
      }
    };
    requestAnimationFrame(() => {
      component.show = true;
    });
  });
}

export default {show}
