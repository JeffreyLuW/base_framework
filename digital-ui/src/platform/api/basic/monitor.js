import request from '@/platform/utils/request'

// 查询监测点列表
export function listMonitors(query) {
  return request({
    url: '/basic/monitor/jcds',
    method: 'post',
    params: query
  })
}
//查询检测点
export function listMonitor(query) {
  return request({
    url: '/basic/monitor/queryJcd/{id}',
    method: 'post',
    params: query
  })
}
// 查询
export function listJcdmc(jcdJcdmc) {
  return request({
    url: '/basic/monitor/selectJcdList' + jcdJcdmc,
    method: 'post'
  })
}
// 新增
export function addMonitor(data) {

  return request({
    // headers:{'Content-Type' : 'application/json'},
    url: '/basic/monitor/addJcd',
    method: 'post',
    data: data
  })
}


export function updateMonitor(data) {
  return request({
    url: '/basic/monitor/modifyJcd',
    method: 'post',
    data: data
  })
}
// 删除
export function delMonitor(id) {
  return request({
    url: '/basic/monitor/modifyJcd' + id,
    method: 'post'
  })
}

