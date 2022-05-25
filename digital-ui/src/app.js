//项目自定义全局样式
import "./assets/styles/index.scss"

export default {
  before: function (Vue, router, store) {
    //项目自定义配置,比如插件 Vue.use(...)
  },
  after: function (app) {
    //app 为 vue根实例对象。
  }
}
