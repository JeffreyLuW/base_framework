//可以代理_render方法，添加自己的拦截方法onRender, 也可用于性能分析，追踪render调用。
export default {
  install(Vue, options = {}) {
    let rawRender = Vue.prototype._render;
    let renderCounter = 0;
    let lastStart = 0;
    let lastEnd = 0;
    let lastTimer = 0;

    let config = {
      log: options.log || false,
      logFn: options.logFn || function (vueComponent) {
        renderCounter++;

        let cTime = Date.now();
        //起始阶段
        if (lastStart <= 0) {
          lastStart = lastEnd = cTime;
        } else {
          //不断记录最后的时间
          lastEnd = cTime;
        }
        if (lastTimer) {
          window.clearTimeout(lastTimer);
          lastTimer = 0;
        }
        //n ms后检测结束，只要指定时间内继续被更新，则会取消，不作为结束。
        lastTimer = setTimeout(() => {
          console.log("本次render-useTime(ms)", lastEnd - lastStart);
          lastStart = lastEnd = 0;
        }, 10);
        console.log('_render:', '' + vueComponent._uid, vueComponent.$options && vueComponent.$options.name, "counter:", renderCounter)
      }
    };
    if (!config.log) {
      return;
    }
    Vue.prototype._render = function () {
      let that = this;
      if (config.log)
        config.logFn(this);
      let vnode = null;
      if (this.onRender) {
        vnode = this.onRender(function () {
          return rawRender.apply(that, arguments);
        });
      } else {
        vnode = rawRender.apply(this, arguments);
      }
      return vnode;
    };

    if (process.env.NODE_ENV === "development") {
      window.onVueRender = config;
    } else {
      //生产下自动关闭log
      config.log = false;
    }
  }
}
