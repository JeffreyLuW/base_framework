import request from '@/platform/utils/request'

// 查询菜单列表
export function list(query) {
  return request({
    url: '/publicExperts/suggestions/list',
    method: 'get',
    params: query
  })
}
