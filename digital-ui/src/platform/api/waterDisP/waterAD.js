import request from "@/platform/utils/request";

// 查询接口详细
export function LIST(query) {
  return request({
    url: "/irrWaterTendaysAdjust/list",
    method: "get",
    params: query
  });
}
// 
export function getDetail(id) {
  return request({
    url: `/irrWaterTendaysAdjust/${id}`,
    method: "get"
  });
}

// 新增接口详细
export function ADD(data) {
  return request({
    url: `/irrWaterTendaysAdjust/add`,
    method: "post",
    data: data
  });
}

// 删除
export function DEL(id) {
  return request({
    url: `/irrWaterTendaysAdjust/${id}`,
    method: "delete"
  });
}

// 修改
export function UPDATE(data) {
  return request({
    url: `/irrWaterTendaysAdjust/`,
    method: "PUT",
    data: data
  });
}
