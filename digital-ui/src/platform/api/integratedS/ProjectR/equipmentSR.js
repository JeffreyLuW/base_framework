import request from '@/platform/utils/request'

export function missInspectList(query) {
  return request({
    url: '/equipmentState/list',
    method: 'post',
    data: query
  })
}
