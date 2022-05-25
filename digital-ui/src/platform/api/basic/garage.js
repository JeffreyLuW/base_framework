import request from '@/platform/utils/request'

// 查询列表
export function listgarage(query) {
  return request({
    url: '/basic/basicManagement/garage/jyzs/',
    method: 'post',
    params: query
  })
}
export function listgarage2(query) {
  return request({
    url: '/basic/basicManagement/garage/selectJyzList',
    method: 'post',
    params: query
  })
}
// 查询详细（ID）
export function getgarage(id) {
  return request({
    url: '/basic/basicManagement/garage/queryJyz/' + id,
    method: 'post'
  })
}
// 新增
export function addgarage(data) {
  return request({
    // headers:{'Content-Type' : 'application/json'},
    url: '/basic/basicManagement/garage/addJyz/',
    method: 'post',
    data: data
  })
}

// 修改
export function updategarage(data) {
  return request({
    url: '/basic/basicManagement/garage/modifyJyz/',
    method: 'post',
    data: data
  })
}
// 删除
export function delgarage(id) {
  return request({
    url: '/basic/basicManagement/garage/deleteJyz/' + id,
    method: 'post'
  })
}

