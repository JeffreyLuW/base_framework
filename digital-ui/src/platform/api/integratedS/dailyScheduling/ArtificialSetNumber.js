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
    url: '/artificialExamine/selectList',
    method: 'get',
    params: query
  })
} 
//
export function updateData(query) {
  return request({
    url: '/artificialExamine/update',
    method: 'put',
    data: query
  })
} 