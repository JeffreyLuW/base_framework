import request from '@/platform/utils/request'

// 获取列表
export function getTimeTypeDict(){
    return request({
        url:`/inspection/time_type/dict`,
        method:"get"
    })
}
// 详情TimeType
export function getTimeTypeDetail(id) {
  return request({
    url: `/inspection/time_type/${id}`,
    method: 'get'
  })
}
// 新增TimeType
export function addTimeType(data) {
  return request({
    url: '/inspection/time_type/',
    method: 'post',
    data: data
  })
}

// 修改TimeType
export function updateTimeType(data) {
  return request({
    url: '/inspection/time_type/',
    method: 'PUT',
    data: data
  })
}
// 删除TimeType
export function delTimeType(id) {
  return request({
    url: `/inspection/time_type/${id}`,
    method: 'DELETE'
  })
}


//属性
// 列表
export function timeInfoList (query){
  return request({
    url:`/inspection/time_info/list`,
    method:"get",
    params:query
  })
}
// 新增
export function timeInfoAdd (data){
  return request({
    url:`/inspection/time_info/`,
    method:"post",
    data:data
  })
}
// 修改
export function timeInfoUpdate(data){
  return request({
    url:`/inspection/time_info/`,
    method:"PUT",
    data:data
  })
}
// 删除
export function timeInfoDel(ids) {
  return request({
    url: `/inspection/time_info/${ids}`,
    method: 'DELETE'
  })
}
// 详情

export function timeInfoDetail(id) {
  return request({
    url: `/inspection/time_info/${id}`,
    method: 'GET'
  })
}