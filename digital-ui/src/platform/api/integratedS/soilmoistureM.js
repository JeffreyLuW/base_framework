import request from '@/platform/utils/request'

// selectVillById

export function getRainShow(query) {
  return request({
    url: '/SoilMon/getRainShow',
    method: 'get',
    params: query
  })
}

export function getAveRainData(query) {
  return request({
    url: '/SoilMon/getSoilMonData',
    method: 'get',
    params: query
  })
}

export function getHistoryData(query) {
  return request({
    url: '/SoilMon/getSoilData',
    method: 'put',
    data: query
  })
}

export function getFirstImageInfo(query) {
  return request({
    url: 'http://124.128.15.175:28006/img/getFirstImageInfo',
    method: 'get',
    params: query
  })
}

export function selectBySum(query) {
  return request({
    url: '/soilMonitoring/selectBySum',
    method: 'get',
    params: query
  })
}

export function selectOldData(query) {
  return request({
    url: '/soilMonitoring/selectOldData',
    method: 'get',
    params: query
  })
}
