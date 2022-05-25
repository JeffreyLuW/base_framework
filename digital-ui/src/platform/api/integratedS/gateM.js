import request from '@/platform/utils/request'

// 查询 radio==1
export function selectBySum(query) {
  return request({
    url: '/strobeMonitoring/selectBySum',
    method: 'get',
    params: query
  })
}
// 查询radio!=1
export function selectOldData(query) {
  return request({
    url: '/strobeMonitoring/selectOldData',
    method: 'get',
    params: query
  })
}
// 树节点点击处理
export function getByCanalName(query) {
  return request({
    url: '/strobeMonitoring/getByCanalName',
    method: 'get',
    params: query
  })
}