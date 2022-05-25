import request from '@/platform/utils/request'

/**
 * 首页折线图数据
 */
export function lineChartData() {
  return request({
    url: "/homepage/monthRequireWaterAndDeliverWaterInfo",
    method: "get"
  });
}

/**
 * 首页指标数据
 */
export function indexData() {
  return request({
    url: '/homepage/yearWaterStatisticsInfo',
    method: 'get',
  })
}

/**
 * 首页雷达图数据
 */
export function radarChart() {
  return request({
    url: '/homepage/deptIndexNumInfo',
    method: 'get',
  })
}

/**
 * 首页饼图数据
 */
export function pieChartData() {
  return request({
    url: '/homepage/yearWaterClassifiedStatisticsInfo',
    method: 'get',
  })
}

/**
 * 首页柱状图数据
 */
export function barChartData() {
  return request({
    url: `/homepage/companyMonthWaterInfo`,
    method: 'get'
  })
}
