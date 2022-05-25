import request from '@/platform/utils/request'

// 查询 radio==1
export function selectBySum(query) {
    return request({
        url: '/pumpMonitoring/selectRealData',
        method: 'get',
        params: query
    })
}
// 查询radio!=1
export function selectOldData(query) {
    return request({
        url: '/pumpMonitoring/selectOldData',
        method: 'get',
        params: query
    })
}
// 树节点点击处理
export function getByCanalName(query) {
    return request({
        url: '/pumpMonitoring/getByCanalName',
        method: 'get',
        params: query
    })
}