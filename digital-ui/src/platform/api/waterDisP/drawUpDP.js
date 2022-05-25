import request from "@/platform/utils/request";

// 编制年引水计划制定表详情查找
export function getYearDetail(year) {
  return request({
    url: `/irrWaterPlanMakeYear/${year}`,
    method: "get"
  });
}


// 编制年引水计划制定表新增
export function yearAdd(data) {
  return request({
    url: `/irrWaterPlanMakeYear/add`,
    method: "post",
    data: data
  });
}
// 编制年引水计划制定表修改
export function yearUpdate(data) {
  return request({
    url: `/irrWaterPlanMakeYear/`,
    method: "PUT",
    data: data
  });
}

// 编制月引水计划制定表详情查找
export function getMonthDetail(query) {
  return request({
    url: `/irrWaterPlanMakeMonth/get`,
    method: "get",
    params:query
  });
}


// 编制月引水计划制定表新增
export function monthAdd(data) {
  return request({
    url: `/irrWaterPlanMakeMonth/add`,
    method: "post",
    data: data
  });
}
// 编制月引水计划制定表修改
export function monthUpdate(data) {
  return request({
    url: `/irrWaterPlanMakeMonth/`,
    method: "PUT",
    data: data
  });
}

// 查询接口详细
export function LIST(query) {
  return request({
    url: "/irrWaterIntakeApply/list",
    method: "get",
    params: query
  });
}
//变更取水申请日期的时候同步变更的许可水量等信息
export function getNeedWaterByDate (date) {
  return request({
    url:`/irrWaterIntakeApply/getNeedWaterByDate/${date}`,
    method:'get'
  })
}


// 删除
export function DEL(id) {
  return request({
    url: `/irrWaterIntakeApply/${id}`,
    method: "delete"
  });
}

// 修改
export function UPDATE(data) {
  return request({
    url: `/irrWaterIntakeApply/`,
    method: "PUT",
    data: data
  });
}
