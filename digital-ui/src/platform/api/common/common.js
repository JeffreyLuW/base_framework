import request from '@/platform/utils/request'

//通用上传请求  file
//return {code:200,msg,fileName,url}
export function commonUploadFiles(file) {
  let data = new FormData();
  data.append("file", file);
  return request({
    url: '/common/upload',
    method: 'post',
    data: data,
    timeout: 120 * 1000  //较大文件适当调整超时时间
  })
}

//通用上传请求  file
//return {code:200,msg,fileName,url}
export function uploadFile(file) {
  let data = new FormData();
  data.append("file", file);
  return request({
    url: '/sysFilelink/upload',
    method: 'post',
    data: data,
    timeout: 120 * 1000  //较大文件适当调整超时时间
  })
}

export function uploadAddFile(ids) {
  return request({
    url: '/sysFilelink/add',
    method: 'PUT',
    data: {ids:ids}
  })
}
export function uploadEditFile(data) {
  return request({
    url: '/sysFilelink/group',
    method: 'PUT',
    data: data
  })
}
export function getGroupFile(groupId) {
  return request({
    url: `/sysFilelink/group/${groupId}`,
    method: 'get'
  })
}