import request from '@/platform/utils/request'

//获取全部河流
export function getAllRiver(query) {
  return request({
    url: '/headWaterMon/getRiverAll',
    method: 'get',
    params: query
  })
} 

//获取水清监测及闸位监测数据
export function getWaterGateData(query) {
  return request({
    url: '/headWaterMon/getWaterGateData',
    method: 'get',
    params: query
  })
} 

//获取水位流量历史数据
export function getHistoryData(query) {
  return request({
    url: '/headWaterMon/selectRiverSum',
    method: 'post',
    data: query
  })
} 

//获取全部机井
export function getAllWell(query) {
  return request({
    url: '/headWaterMon/getWell',
    method: 'get',
    params: query
  })
} 

//获取水源类型所有监控

export function getAllCamera(query) {
  return request({
    url: 'videoAlarm/getByType',
    method: 'get',
    params: query
  })
}