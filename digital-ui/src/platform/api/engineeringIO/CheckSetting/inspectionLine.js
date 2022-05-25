import request from '@/platform/utils/request'

export function getRoute(id) {
  return request({
    url: `/inspection/route/tree/${id}`,
    method: 'get'
  })
}

// 详情
export function getRouteDetail(id) {
  return request({
    url: `/inspection/route/${id}`,
    method: 'get'
  })
}
// 新增Route
export function addRoute(data) {
  return request({
    url: '/inspection/route/',
    method: 'post',
    data: data
  })
}

// 修改Route
export function updateRoute(data) {
  return request({
    url: '/inspection/route/',
    method: 'PUT',
    data: data
  })
}
// 删除Route
export function delRoute(id) {
  return request({
    url: `/inspection/route/${id}`,
    method: 'DELETE'
  })
}


//属性
// 列表
export function routeList (query){
  return request({
    url:`/inspection/route_object/list`,
    method:"get",
    params:query
  })
}// 列表
export function getRouteObjDetail (id){
  return request({
    url:`/inspection/route_object/${id}`,
    method:"get"
  })
}
// 新增
export function routeObjAdd (data){
  return request({
    url:`/inspection/route_object/`,
    method:"post",
    data:data
  })
}
// 修改
export function routeObjUpdate(data){
  return request({
    url:`/inspection/route_object/`,
    method:"PUT",
    data:data
  })
}
// 删除
export function routeObjDel(routeId,objectIds) {
  return request({
    url: `/inspection/route_object/${routeId}/${objectIds}`,
    method: 'DELETE'
  })
}