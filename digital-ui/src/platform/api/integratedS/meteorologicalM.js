import request from '@/platform/utils/request'

// 查询 radio==1
export function selectBySum(query) {
  return request({
    url: '/weatherMonitoring/selectBySum',
    method: 'get',
    params: query
  })
}
// 查询radio!=1
export function selectOldData(query) {
  return request({
    url: '/weatherMonitoring/selectOldData',
    method: 'get',
    params: query
  })
}
// 树节点点击处理
export function getByRegionId(query) {
  return request({
    url: '/weatherMonitoring/getByRegionId',
    method: 'get',
    params: query
  })
}