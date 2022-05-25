import request from '@/platform/utils/request'

// 缴费记录查询
export function getList(query) {
  return request({
    url: `/irrWaterPayRecord/list`,
    method: 'get',
    params:query
  })
}
// 水量详情查询
export function getWaterList(query) {
  return request({
    url: `/irrWaterPayRecord/waterList`,
    method: 'get',
    params:query
  })
}

// 缴费记录信息表详情查找(新增)
export function getInfoDetail(query) {
  return request({
    url: `/irrWaterPayRecord/info`,
    method: 'get',
    params:query
  })
}
// 已缴费记录
export function getDetail(query) {
  return request({
    url: `/irrWaterPayRecord/get`,
    method: 'get',
    params:query
  })
}
// 新增
export function add(data) {
  return request({
    url: '/irrWaterPayRecord/',
    method: 'post',
    data: data
  })
}

// 删除
export function del(id) {
  return request({
    url: `/irrWaterPayRecord/${id}`,
    method: 'DELETE'
  })
}
