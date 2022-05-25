import request from '@/platform/utils/request'


// 查询主题列表
export function list(params) {
  return request({
    url: '/system/themes',
    method: 'get',
    params: params
  })
}

// 查询主题 单条 详情
export function detail(params) {
  return request({
    url: '/system/theme/'+params,
    method: 'get'
  })
}

// 新增主题
export function add(params) {
  return request({
    url: '/system/theme',
    method: 'post',
    data: params
  })
}

// 修改主题
export function update(params) {
  return request({
    url: '/system/theme',
    method: 'put',
    data: params
  })
}


// 删除主题 params=1,2
export function del(params) {
  return request({
    url: '/system/theme/'+params,
    method: 'delete',
  })
}
