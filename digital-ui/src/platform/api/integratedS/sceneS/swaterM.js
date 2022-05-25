import request from '@/platform/utils/request'

//获取全部河流
export function getCanalTree(query) {
  return request({
    url: '/wLMonitoring/canalTree',
    method: 'get',
    params: query
  })
}

//通过渠道id获取信息
export function getDataByCode(query) {
  return request({
    url: '/waterDelMon/getCanalData',
    method: 'get',
    params: query
  })
}


//通过渠道id获取即时水情和平均水位信息
export function getMoreDataByCode(query) {
  return request({
    url: '/waterDelMon/getMonDataByCode',
    method: 'get',
    params: query
  })
}

//获取工程监测表格数据
export function getTableData(query) {
  return request({
    url: '/waterDelMon/getSumDataByCode',
    method: 'post',
    data: query
  })
}

//获取工程监测图表数据
export function getchart(query) {
  return request({
    url: '/waterDelMon/getSumDataChart',
    method: 'post',
    data: query
  })
}
