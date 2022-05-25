import request from "@/platform/utils/request";

// 水量汇总表分页查询
export function LIST(query) {
  return request({
    url: "/irrWaterSummary/list",
    method: "get",
    params: query
  });
}
// 水量汇总
export function summary(query) {
  return request({
    url: "/irrWaterSummary/summary",
    method: "get",
    params: query
  });
}

// 新增
export function ADD(data) {
  return request({
    url: `/irrWaterSummary/add`,
    method: "post",
    data: data
  });
}

// 修改
export function UPDATE(data) {
  return request({
    url: `/irrWaterSummary/edit`,
    method: "PUT",
    data: data
  });
}

// 删除
export function DEL(query) {
  return request({
    url: "/irrWaterSummary/delete",
    method: "get",
    params: query
  });
}
