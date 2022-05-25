import request from '@/platform/utils/request'

// 查询 radio==1
export function selectBySum(query) {
  return request({
    url: '/fluxMonitoring/selectBySum',
    method: 'get',
    params: query
  })
}
// 查询radio!=1
export function selectOldData(query) {
  return request({
    url: '/fluxMonitoring/selectOldData',
    method: 'get',
    params: query
  })
}
// 树节点点击处理
export function getByCanalName(query) {
  return request({
    url: '/fluxMonitoring/getByCanalName',
    method: 'get',
    params: query
  })
}