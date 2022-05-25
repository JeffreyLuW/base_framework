import request from '@/platform/utils/request'

//查询对象，监测点树
export function queryClassTree() {
  return request({
    url: `/inspectionClassQueryController/queryClassTree`,
    method: 'get'
  })
}

// 根据对象或者监测点查询任务记录
export function queryMissionList(query) {
  return request({
    url: `/inspectionClassQueryController/queryMissionList`,
    method: 'get',
    params:query
  })
}
