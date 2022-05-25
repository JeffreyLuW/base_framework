import request from '@/platform/utils/request'

// selectVillById
export function getWeatherInfo(query) {
  return request({
    url: 'http://124.128.15.175:28006/WaterRegime/getNowWeatherInfo',
    method: 'get',
    params: query
  })
}
export function getRainShow(query) {
  return request({
    url: '/rainMon/getRainShow',
    method: 'get',
    params: query
  })
}

export function getAveRainData(query) {
  return request({
    url: '/rainMon/getAveRainData',
    method: 'get',
    params: query
  })
}

export function getHistoryData(query) {
  return request({
    url: '/rainMon/getRainDate',
    method: 'put',
    data: query
  })
}

export function selectBySum(query) {
  return request({
    url: '/rainMonitoring/selectBySum',
    method: 'get',
    params: query
  })
}

export function selectOldData(query) {
  return request({
    url: '/rainMonitoring/selectOldData',
    method: 'get',
    params: query
  })
}
