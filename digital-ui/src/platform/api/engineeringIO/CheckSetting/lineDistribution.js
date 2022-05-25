import request from '@/platform/utils/request'
// 列表
export function deptRouteList(query) {
  return request({
    url: `/inspection/dept_route/list`,
    method: "get",
    params: query
  });
}

export function deptRouteDict() {
  return request({
    url: `/inspection/route/dict`,
    method: "get"
  });
}
// 新增
export function deptRouteAdd(data) {
  return request({
    url: `/inspection/dept_route/`,
    method: "post",
    data: data
  });
}
// 修改
export function deptRouteDel(deptId,routeIds) {
  return request({
    url: `/inspection/dept_route/${deptId}/${routeIds}`,
    method: "DELETE"
  });
}
