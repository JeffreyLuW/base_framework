import request from '@/platform/utils/request'

// 巡检周期字典
export function getCycleDict(){
    return request({
        url:`/inspection/cycle/dict`,
        method:"get"
    })
}
// 巡检周期类型字典
export function getCycleTypeDict(){
    return request({
        url:`/inspection/cycle/type/dict`,
        method:"get"
    })
}
// 详情Cycle
export function getCycleDetail(id) {
  return request({
    url: `/inspection/cycle/${id}`,
    method: 'get'
  })
}
// 新增Cycle
export function addCycle(data) {
  return request({
    url: '/inspection/cycle/',
    method: 'post',
    data: data
  })
}

// 修改Cycle
export function updateCycle(data) {
  return request({
    url: '/inspection/cycle/',
    method: 'PUT',
    data: data
  })
}
// 删除Cycle
export function delCycle(id) {
  return request({
    url: `/inspection/cycle/${id}`,
    method: 'DELETE'
  })
}