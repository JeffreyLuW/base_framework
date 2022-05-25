import request from '@/platform/utils/request'

//属性
// 列表
export function objectList (query){
  return request({
    url:`/inspection/object/list`,
    method:"get",
    params:query
  })
}
// 详情
export function objectDetail(id) {
  return request({
    url: `/inspection/object/${id}`,
    method: 'get'
  })
}
// 新增
export function objectAdd (data){
  return request({
    url:`/inspection/object/`,
    method:"post",
    data:data
  })
}
// 修改
export function objectUpdate(data){
  return request({
    url:`/inspection/object/`,
    method:"PUT",
    data:data
  })
}
// 删除
export function objectDel(ids) {
  return request({
    url: `/inspection/object/${ids}`,
    method: 'DELETE'
  })
}