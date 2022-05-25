import request from '@/platform/utils/request'


// 根据对象或者监测点查询任务记录
export function queryObjectList(query) {
  return request({
    url: `/inspectionClassQueryController/queryObjectList`,
    method: 'get',
    params:query
  })
}
