import request from '@/platform/utils/request'
export function update(query) {
  return request({
    url: '/irrEsXs/irrEsXs',
    method: 'put',
    data: query
  })
}

export function add(query) {
  return request({
    url: '/irrEsXs/irrEsXs/add',
    method: 'post',
    data: query
  })
}

export function getList(query) {
  return request({
    url: '/irrEsXs/irrEsXs/list',
    method: 'get',
    params: query
  })
}

export function getById(query) {
  return request({
    url: `/irrEsXs/irrEsXs/${query}`,
    method: 'get',
  })
}

export function delteData(query) {
  return request({
    url: `/irrEsXs/irrEsXs/${query}`,
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
    url: `/irrEsXs/irrEsXs/export`,
    method: 'post',
    data: ids
  })
}

// 导出模板
export function importTemplate() {
  return request({
    url: `/irrEsXs/irrEsXs/templateDownload`,
    method: 'get',
  })
}
