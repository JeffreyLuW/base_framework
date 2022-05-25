import request from '@/platform/utils/request'

export function getList(query) {
  return request({
    url: `/irrWaterPriceInfo/list`,
    method: 'get',
    params:query
  })
}

// 删除
export function getDetail(id) {
  return request({
    url: `/irrWaterPriceInfo/${id}`,
    method: 'get'
  })
}
// 新增
export function add(data) {
  return request({
    url: '/irrWaterPriceInfo/',
    method: 'post',
    data: data
  })
}

// 修改
export function update(data) {
  return request({
    url: '/irrWaterPriceInfo/',
    method: 'PUT',
    data: data
  })
}
// 删除
export function del(id) {
  return request({
    url: `/irrWaterPriceInfo/${id}`,
    method: 'DELETE'
  })
}
