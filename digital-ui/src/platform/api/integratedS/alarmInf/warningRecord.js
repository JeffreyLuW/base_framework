import request from '@/platform/utils/request'

export function getCanalTree(query) {
    return request({
        url: '/wLMonitoring/canalTree',
        method: 'get',
        params: query
    })
}
//
export function getTbleData(query) {
    return request({
        url: '/alarmMon/selectList',
        method: 'get',
        params: query
    })
}