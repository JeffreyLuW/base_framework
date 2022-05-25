import request from '@/platform/utils/request'

export function getCanalTree(query) {
  return request({
    url: '/manualInput/getSysDeptInfo',
    method: 'get',
    params: query
  })
}
//
export function getTbleData(query) {
  return request({
    url: '/manualInput/getDisplayInfo',
    method: 'post',
    data: query
  })
}
//根据条件查询接口
export function getApi(query) {
  return request({
    url: '/manualInput/getApiUrl',
    method: 'get',
    params: query
  })
}
//根据类型搜索
export function searcByType(query) {
  return request({
    url: '/manualInput/getMonitorInfo',
    method: 'get',
    params: query
  })
}
//数据回显
export function getReshowData(query) {
  return request({
    url: '/manualInput/getMonCarInfo',
    method: 'get',
    params: query
  })
}
