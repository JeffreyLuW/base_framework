import request from '@/platform/utils/request'

// 查询列表
export function listVersion(query) {
  return request({
    url: '/appVersion/list',
    method: 'get',
    params: query
  })
}

// 查询详细
export function getVersionInfo(id) {
  return request({
    url: '/appVersion/info/' + id,
    method: 'get'
  })
}

// 查询最新版本
export function getNewVersion(query) {
  return request({
    url: '/appVersion/newVersion',
    method: 'get',
    params: query
  })
}


// 新增
export function addVersion(data) {
  return request({
    url: '/appVersion/add',
    method: 'post',
    data: data
  })
}
//更新
export function updateVersion(data) {
  return request({
    url: '/appVersion/update',
    method: 'put',
    data: data
  })
}


// 删除
export function delVersion(ids) {
  return request({
    url: '/appVersion/' + ids,
    method: 'delete'
  })
}

