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
    url: '/waterRealMon/selectList',
    method: 'get',
    params: query
  })
} 
export function getTbleData2(query) {
  return request({
    url: "/dailyMonitor/segmentTimeList",
    method: "get",
    params: query
  });
} 
export function exportExl(data) {
  return request({
    url: '/waterRealMon/export',
    method: 'post',
    data:data
  })
} 

export function getTransferTree(data) {
  return request({
    url: '/waterRealMon/getDeptMonitorTree',
    method: 'get',
    params:data
  })
} 

export function saveCollection(data) {
  return request({
    url: '/waterRealMon/addCollect',
    method: 'post',
    data: data
  })
} 