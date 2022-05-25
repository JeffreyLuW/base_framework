import request from "@/platform/utils/request";

// 查询字典类型列表
export function getList(query) {
  return request({
    url: "/mapConfig/list",
    method: "get",
    params: query
  });
}

export function getDetail(query) {
  return request({
    url: "/mapConfig/info",
    method: "get",
    params: query
  });
}

export function add(query) {
  return request({
    url: "/mapConfig/add",
    method: "post",
    data: query
  });
}

export function update(query) {
  return request({
    url: "/mapConfig/update",
    method: "post",
    data: query
  });
}

