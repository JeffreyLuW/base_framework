import request from '@/platform/utils/request'

// 获取未添加的控制对象（水闸）列表信息
export function getWatergateListInfo(query) {
  return request({
    url: '/strobeControl/getWatergateListInfo',
    method: 'get',
    params: query
  })
}
// 查询控制对象分页列表
export function queryControlObjectList(query) {
  return request({
    url: '/strobeControl/queryControlObjectList',
    method: 'get',
    params: query
  })
}
// 增加控制对象
export function addControlObject(data) {
  return request({
    url: '/strobeControl/addControlObject',
    method: 'post',
    data: data
  })
}
// 删除控制对象
export function batchDelControlObject(data) {
  return request({
    url: '/strobeControl/batchDelControlObject',
    method: 'DELETE',
    data: data
  })
}
// 通过水闸编码获取闸门列表
export function queryStrobeList(query) {
  return request({
    url: '/strobeControl/queryStrobeList',
    method: 'get',
    params: query
  })
}
// 通过id获取水闸基本信息
export function getWaterGateInfoById(query) {
  return request({
    url: '/strobeControl/getWaterGateInfoById',
    method: 'get',
    params: query
  })
}