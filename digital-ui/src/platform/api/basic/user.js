import request from '@/platform/utils/request'

// 查询列表
export function listUser(query) {
  return request({
    url: '/basic/basicManagement/user/yshs/',
    method: 'post',
    params: query
  })
}
//搜索查询
export function listUser2(query) {
  return request({
    url: '/basic/basicManagement/user/selectYshList',
    method: 'post',
    params: query
  })
}
// 查询详细（ID）
export function getUser(id) {
  return request({
    url: '/basic/basicManagement/user/queryYsh/' + id,
    method: 'post'
  })
}
// 新增
export function addUser(data) {
  return request({
    // headers:{'Content-Type' : 'application/json'},
    url: '/basic/basicManagement/user/addYsh/',
    method: 'post',
    data: data
  })
}

// 修改
export function updateUser(data) {
  return request({
    url: '/basic/basicManagement/user/modifyYsh/',
    method: 'post',
    data: data
  })
}
// 删除
export function delUser(id) {
  return request({
    url: '/basic/basicManagement/user/deleteYsh/' + id,
    method: 'post'
  })
}

