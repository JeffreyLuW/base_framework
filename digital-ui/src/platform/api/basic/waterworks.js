import request from '@/platform/utils/request'

// 查询列表
export function listwaterworks(query) {
  return request({
    url: '/basic/basicManagement/waterworks/gcs/',
    method: 'post',
    params: query
  })
}
//搜索查询
export function listwaterworks2(query) {
  return request({
    url: '/basic/basicManagement/waterworks/selectGcList',
    method: 'post',
    params: query
  })
}
// 查询详细（ID）
export function getwaterworks(id) {
  return request({
    url: '//basic/basicManagement/waterworks/queryGc/' + id,
    method: 'post'
  })
}
// 新增
export function addwaterworks(data) {
  return request({
    // headers:{'Content-Type' : 'application/json'},
    url: '/basic/basicManagement/waterworks/addGc/',
    method: 'post',
    data: data
  })
}

// 修改
export function updatewaterworks(data) {
  return request({
    url: '/basic/basicManagement/waterworks/modifyGc',
    method: 'post',
    data: data
  })
}
// 删除
export function delwaterworks(id) {
  return request({
    url: '/basic/basicManagement/waterworks/deleteGc/' + id,
    method: 'post'
  })
}

