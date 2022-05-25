import Router from "vue-router";

export default function (routes) {
  return new Router({
    mode: 'hash', // 去掉url中的#
    scrollBehavior: () => ({y: 0}),
    routes
  })
}
