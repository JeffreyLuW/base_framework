import request from '@/platform/utils/request'

// 查询 radio==1
export function selectBySum(query) {
  return request({
    url: '/wLMonitoring/selectBySum',
    method: 'get',
    params: query
  })
}
// 查询radio!=1
export function selectOldData(query) {
  return request({
    url: '/wLMonitoring/selectOldData',
    method: 'get',
    params: query
  })
}
// 树节点点击处理
export function getByCanalName(query) {
  return request({
    url: '/wLMonitoring/getByCanalName',
    method: 'get',
    params: query
  })
}
//获取左侧树形结构数据
export function canalTree(query) {
  return request({
    url: '/wLMonitoring/canalTree',
    method: 'get',
    params: query
  })
}