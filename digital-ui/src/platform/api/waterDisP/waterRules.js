import request from "@/platform/utils/request";

// 查询接口详细
export function LIST(query) {
  return request({
    url: "/planDispatch/irrWaterApplicationRules/list",
    method: "get",
    params: query
  });
}
// 
export function getDetail(id) {
  return request({
    url: `/planDispatch/irrWaterApplicationRules/${id}`,
    method: "get"
  });
}


// 获取用水年度的下拉框，key用水单位名称，value用水单位id
export function queryRulesDicts(query) {
  return request({
    url: "/planDispatch/irrWaterApplicationRules/queryRulesDicts",
    method: "get",
    params: query
  });
}

// 新增接口详细
export function ADD(data) {
  return request({
    url: `/planDispatch/irrWaterApplicationRules/`,
    method: "post",
    data: data
  });
}

// 删除
export function DEL(id) {
  return request({
    url: `/planDispatch/irrWaterApplicationRules/${id}`,
    method: "delete"
  });
}

// 修改
export function UPDATE(data) {
  return request({
    url: `/planDispatch/irrWaterApplicationRules/`,
    method: "PUT",
    data: data
  });
}
