import request from '@/platform/utils/request'

//获取全部渠道
export function getCanalTree(query) {
  return request({
    url: '/pumpMon/getPumpCanalTree',
    method: 'get',
    params: query
  })
}  

//通过渠道id获取信息
export function getDataByCode(query) {
  return request({
    url: '/pumpMon/getPumpByCode',
    method: 'get',
    params: query
  })
}  
//通过渠道id获取表格信息
export function getTableDataByCode(query) {
  return request({
    url: '/pumpMon/getPumpDate',
    method: 'post',
    data: query
  })
}  