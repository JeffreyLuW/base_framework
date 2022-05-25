import request from "@/platform/utils/request";

// 查询接口详细
export function getList(query) {
  return request({
    url: "/irrWaterSystemInfo/list",
    method: "get",
    params: query
  });
}
// 
export function getDetail(id) {
  return request({
    url: `/irrWaterSystemInfo/${id}`,
    method: "get"
  });
}


// 获取用水年度的下拉框，key用水单位名称，value用水单位id
export function queryCompentDicts(query) {
  return request({
    url: "/irrWaterSystemInfo/queryCompentDicts",
    method: "get",
    params: query
  });
}

// 新增接口详细
export function add(data) {
  return request({
    url: `/irrWaterSystemInfo/`,
    method: "post",
    data: data
  });
} 

// 删除
export function del(id) {
  return request({
    url: `/irrWaterSystemInfo/${id}`,
    method: "delete"
  });
}

// 修改
export function update(data) {
  return request({
    url: `/irrWaterSystemInfo/`,
    method: "PUT",
    data: data
  });
}
