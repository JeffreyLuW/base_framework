import request from '@/platform/utils/request'

//getTreeSelect
export function getTreeSelect(query) {
  return request({
    url: '/system/area/treeSelect',
    method: 'get',
    params: query
  })
}
