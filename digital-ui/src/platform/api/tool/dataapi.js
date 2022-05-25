import request from '@/platform/utils/request'

// 查询接口列表
export function listDataApi(query) {
  return request({
    url: '/tool/dataapi/list',
    method: 'get',
    params: query
  })
}


// 查询接口详细
export function getDataApi(tableName) {
  return request({
    url: '/tool/dataapi/' + tableName,
    method: 'get'
  })
}
