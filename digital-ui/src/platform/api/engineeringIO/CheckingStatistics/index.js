import request from "@/platform/utils/request";

// 漏检统计查询
export function missInspectList(query) {
  return request({
    url: `/inspectionStatisticsController/missInspectList`,
    method: "get",
    params: query
  });
}

// 人员统计查询
export function userList(query) {
  return request({
    url: `/inspectionStatisticsController/userList`,
    method: "get",
    params: query
  });
}
// 实时监控统计查询
export function realTimeList(query) {
  return request({
    url: `/inspectionStatisticsController/realTimeList`,
    method: "get",
    params: query
  });
}
// 线路统计查询
export function routeList(query) {
  return request({
    url: `/inspectionStatisticsController/routeList`,
    method: "get",
    params: query
  });
}

// 月历统计查询
export function monthList(query) {
  return request({
    url: `/inspectionStatisticsController/monthList`,
    method: "get",
    params: query
  });
}
// 查询所有监测站编号、名称、id等信息
export function selectAllMonitoring() {
  return request({
    url: `/monitoringStation/selectAll`,
    method: "get"
  });
}
// 监测站分析数据
export function monitoringStation(monitoringStationId,type,query) {
  return request({
    url: `/decisionAnalysis/monitoringStation/${monitoringStationId}/${type}`,
    method: "get",
    params: query
  });
}
