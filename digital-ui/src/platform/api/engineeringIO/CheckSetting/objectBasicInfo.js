import request from '@/platform/utils/request'


// 获取对象的基本信息
export function getObjBaseinfo (objectId){
  return request({
    url:`/inspection/object/baseinfo/${objectId}`,
    method:"get"
  })
}
// 添加基本信息
export function ObjBaseinfo (data){
  return request({
    url:`/inspection/object/baseinfo/`,
    method:"post",
    data:data
  })
}