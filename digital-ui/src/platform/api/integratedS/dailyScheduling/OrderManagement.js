import request from '@/platform/utils/request'

//
export function getTbleData(query) {
  return request({
    url: '/irrDispatch/list',
    method: 'get',
    params: query
  })
} 

export function add(query) {
  return request({
    url: '/irrDispatch/add',
    method: 'post',
    data: query
  })
} 

export function update(query) {
  return request({
    url: '/irrDispatch/update',
    method: 'put',
    data: query
  })
} 

export function deleteOrder(query) {
  return request({
    url: `/irrDispatch/${query}`,
    method: 'delete',
  })
} 

export function getDataById(query) {
  return request({
    url: `/irrDispatch/${query}`,
    method: 'get',
  })
} 