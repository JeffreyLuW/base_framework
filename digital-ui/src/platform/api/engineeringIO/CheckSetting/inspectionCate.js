import request from '@/platform/utils/request'

export function getTree(id) {
  return request({
    url: `/inspection/clazz/tree/${id}`,
    method: 'get'
  })
}

// 删除tree
export function getTreeDetail(id) {
  return request({
    url: `/inspection/clazz/${id}`,
    method: 'get'
  })
}
// 新增tree
export function addTree(data) {
  return request({
    url: '/inspection/clazz/',
    method: 'post',
    data: data
  })
}

// 修改tree
export function updateTree(data) {
  return request({
    url: '/inspection/clazz/',
    method: 'PUT',
    data: data
  })
}
// 删除tree
export function delTree(id) {
  return request({
    url: `/inspection/clazz/${id}`,
    method: 'DELETE'
  })
}


//属性
// 列表
export function clazzList (query){
  return request({
    url:`/inspection/clazz/attribute/list`,
    method:"get",
    params:query
  })
}
// 详情
export function clazzDetail (id){
  return request({
    url:`/inspection/clazz/attribute/${id}`,
    method:"get"
  })
}
// 新增
export function clazzAdd (data){
  return request({
    url:`/inspection/clazz/attribute/`,
    method:"post",
    data:data
  })
}
// 修改
export function clazzUpdate(data){
  return request({
    url:`/inspection/clazz/attribute/`,
    method:"PUT",
    data:data
  })
}
// 删除
export function clazzDel(ids) {
  return request({
    url: `/inspection/clazz/attribute/${ids}`,
    method: 'DELETE'
  })
}
// 巡检类别属性下拉选

export function clazzDict(clazzId) {
  return request({
    url: `/inspection/clazz/attribute/dict/${clazzId}`,
    method: 'GET'
  })
}