import request from '@/platform/utils/request'

export function getCanalTree(query) {
  return request({
    url: '/wLMonitoring/canalTree',
    method: 'get',
    params: query
  })
}
//
export function getTbleData(query) {
  return request({
    url: '/waterHistory/list',
    method: 'get',
    params: query
  })
} 
export function getType(query) {
  return request({
    url: '/waterHistory/getType',
    method: 'get'
  })
} 