import request from "@/platform/utils/request";

// 查询接口详细
export function LIST(query) {
  return request({
    url: "/irrWaterUsePlanYear/list",
    method: "get",
    params: query
  });
}
// 
export function getDetail(id) {
  return request({
    url: `/irrWaterUsePlanYear/${id}`,
    method: "get"
  });
}

//统计
export function queryYearPlanSummary() {
  return request({
    url: "/irrWaterUsePlanYear/queryYearPlanSummary",
    method: "get"
  });
}


// 查询接口详细
export function queryYearPlanDictList(query) {
  return request({
    url: "/irrWaterUsePlanYear/queryYearPlanDictList",
    method: "get",
    params: query
  });
}

// 新增接口详细
export function ADD(data) {
  return request({
    url: `/irrWaterUsePlanYear/add`,
    method: "post",
    data: data
  });
}

// 删除
export function DEL(id) {
  return request({
    url: `/irrWaterUsePlanYear/${id}`,
    method: "delete"
  });
}

// 修改
export function UPDATE(data) {
  return request({
    url: `/irrWaterUsePlanYear/`,
    method: "PUT",
    data: data
  });
}
