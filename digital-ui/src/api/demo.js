import request from '../platform/utils/request'

// demo api 定义
export function demo(data) {
  return request({
    url: '/login',
    method: 'post',
    data: data,
    ignoreToken:true
  })
}
