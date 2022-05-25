import request from '@/platform/utils/request'

export function queryMissionInfoList(query) {
  return request({
    url: `/inspectionClassQueryController/queryMissionInfoList`,
    method: 'get',
    params:query
  })
}

// 详情
export function queryMissionObjectInfo(missionId) {
  return request({
    url: `/inspectionClassQueryController/queryMissionObjectInfo/${missionId}`,
    method: 'get'
  })
}

// 详情
export function delMissionId(missionId) {
  return request({
    url: `/inspection/mission/${missionId}`,
    method: 'DELETE'
  })
}
