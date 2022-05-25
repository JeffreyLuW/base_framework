import request from "@/platform/utils/request";

// 列表
export function objectList(query) {
  return request({
    url: `/videoInspection/list`,
    method: "get",
    params: query
  });
}
export function saveVideo(query) {
  return request({
    url: `/videoInspection/save`,
    method: "post",
    data: query
  });
}
export function getConnectVideo(query) {
  return request({
    url: `/videoInspection/getListByInsepectId`,
    method: "get",
    params: query
  });
}
//树结构
export function getTree(query) {
  return request({
    url: `/videoInspect/treeList`,
    method: "get",
    params: query
  });
}

// 新增Route
export function addLine(data) {
  return request({
    url: "/videoInspect/add",
    method: "post",
    data: data
  });
}
export function updateLine(data) {
  return request({
    url: "/videoInspect/update",
    method: "post",
    data: data
  });
}
export function deleteLine(query) {
  return request({
    url: "/videoInspect/delete",
    method: "get",
    params: query
  });
}
export function getLineDetail(query) {
  return request({
    url: "/videoInspect/info",
    method: "get",
    params: query
  });
}
