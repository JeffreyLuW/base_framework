import request from "@/platform/utils/request";

// 查询接口详细
export function LIST(query) {
  return request({
    url: "/irrWaterUserPlanMonth/list",
    method: "get",
    params: query
  });
}
// 
export function getDetail(id) {
  return request({
    url: `/irrWaterUserPlanMonth/${id}`,
    method: "get"
  });
}

// 统计
export function queryMonthPlanSummary(year) {
  return request({
    url: `/irrWaterUserPlanMonth/queryMonthPlanSummary`,
    method: "get",
    params:{
      year:year
    }
  });
}

// 年对应的月度计划下拉框
export function queryMonthPlanDictList(query) {
  return request({
    url: `/irrWaterUserPlanMonth/queryMonthPlanDictList/queryMonthPlanDictList`,
    method: "get",
    params:query
  });
}

// 已存在的月的年引水计划下拉框
export function queryYearPlanDictList() {
  return request({
    url: `/irrWaterUserPlanMonth/queryYearPlanDictList`,
    method: "get"
  });
}
// 新增接口详细
export function ADD(data) {
  return request({
    url: `/irrWaterUserPlanMonth/add`,
    method: "post",
    data: data
  });
}

// 删除
export function DEL(id) {
  return request({
    url: `/irrWaterUserPlanMonth/${id}`,
    method: "delete"
  });
}

// 修改
export function UPDATE(data) {
  return request({
    url: `/irrWaterUserPlanMonth/`,
    method: "PUT",
    data: data
  });
}
