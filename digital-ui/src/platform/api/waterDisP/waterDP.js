import request from "@/platform/utils/request";

// 查询接口详细
export function LIST(query) {
  return request({
    url: "/irrWaterUsePlanTendays/list",
    method: "get",
    params: query
  });
}
// 
export function getDetail(id) {
  return request({
    url: `/irrWaterUsePlanTendays/${id}`,
    method: "get"
  });
}

// 新增接口详细
export function ADD(data) {
  return request({
    url: `/irrWaterUsePlanTendays/add`,
    method: "post",
    data: data
  });
}

// 删除
export function DEL(id) {
  return request({
    url: `/irrWaterUsePlanTendays/${id}`,
    method: "delete"
  });
}

// 修改
export function UPDATE(data) {
  return request({
    url: `/irrWaterUsePlanTendays/`,
    method: "PUT",
    data: data
  });
}
