import request from '@/platform/utils/request'

// 查询工程列表
export function listSy(query) {
  return request({
    url: '/basic/basicManagement/headwaters/sys/',
    method: 'post',
    params: query
  })
}
export function listSy2(query) {
  return request({
    url: '/basic/basicManagement/waterworks/selectGcList',
    method: 'post',
    params: query
  })
}
// 查询
export function getSy(syjcxxglId) {
  return request({
    url: '/basic/basicManagement/headwaters/querySy/' + syjcxxglId,
    method: 'post'
  })
}
// 新增
export function addSy(data) {

  return request({
    // headers:{'Content-Type' : 'application/json'},
    url: '/basic/basicManagement/headwaters/addSy/',
    method: 'post',
    data: data
  })
}


export function updateSy(data) {
  return request({
    url: '/basic/basicManagement/headwaters/modifySy/',
    method: 'post',
    data: data
  })
}
// 删除
export function delSy(syjcxxglId) {
  return request({
    url: '/basic/basicManagement/headwaters/deleteSy/' + syjcxxglId,
    method: 'post'
  })
}

