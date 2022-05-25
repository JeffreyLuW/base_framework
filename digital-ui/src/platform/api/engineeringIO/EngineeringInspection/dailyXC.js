import request from '@/platform/utils/request'

export function update(query) {
  return request({
    url: '/irrEsDailypatrol/irrEsDailypatrol',
    method: 'put',
    data: query
  })
}

//修改建筑物数据项
export function add(query) {
  return request({
    url: '/irrEsDailypatrol/irrEsDailypatrol/add',
    method: 'post',
    data: query
  })
}

export function getList(query) {
  return request({
    url: '/irrEsDailypatrol/irrEsDailypatrol/list',
    method: 'get',
    params: query
  })
}

export function getById(query) {
  return request({
    url: `/irrEsDailypatrol/irrEsDailypatrol/${query}`,
    method: 'get',
  })
}

export function delteData(query) {
  return request({
    url: `/irrEsDailypatrol/irrEsDailypatrol/${query}`,
    method: 'delete',
  })
}

export function getStation() {
  return request({
    url: `/irrEsDailypatrol/irrEsDailypatrol/getAll`,
    method: 'get',
  })
}

// 导出
export function exportExls(ids) {
  return request({
    url: `/irrEsDailypatrol/irrEsDailypatrol/export`,
    method: 'post',
    data: ids
  })
}
// 导出模板
export function importTemplate() {
  return request({
    url: `/irrEsDailypatrol/irrEsDailypatrol/templateDownload`,
    method: 'get',
  })
}
