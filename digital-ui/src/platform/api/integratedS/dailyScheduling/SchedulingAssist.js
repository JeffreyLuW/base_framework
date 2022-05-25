import request from '@/platform/utils/request'

//
export function getTbleData(query) {
  return request({
    url: '/dispatchAnalyse/selectList',
    method: 'get',
    params: query
  })
} 