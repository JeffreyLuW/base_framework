import request from '@/platform/utils/request'
//
export function PoliticalRegionList() {
  return request({
    url: '/IrrBase/PoliticalRegion/list',
    method: 'get'
  })
}
// selectTownById
export function selectTownById(query) {
  return request({
    url: '/monitoringStation/selectTownById',
    method: 'get',
    params: query
  })
}
// selectVillById
export function selectVillById(query) {
  return request({
    url: '/monitoringStation/selectVillById',
    method: 'get',
    params: query
  })
}