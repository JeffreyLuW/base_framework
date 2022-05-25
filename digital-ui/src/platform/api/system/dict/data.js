import request from '@/platform/utils/request'

// 查询字典数据列表
export function listData(query) {
  return request({
    url: '/system/dict/data/list',
    method: 'get',
    params: query
  })
}

// 查询字典数据详细
export function getData(dictCode) {
  return request({
    url: '/system/dict/data/' + dictCode,
    method: 'get'
  })
}

// 根据字典类型查询字典数据信息
let cachedDictType = {};
export function getDicts(dictType,noCache) {
  if (!noCache&&cachedDictType[dictType])
    return Promise.resolve(cachedDictType[dictType]);
  return request({
    url: '/system/dict/data/type/' + dictType,
    method: 'get'
  }).then((rs) => {
    if (rs && rs.data) {
      rs.data.forEach((item) => {
        item.label = item.dictLabel;
        item.value = item.dictValue;
      });
      cachedDictType[dictType] = rs;
    }
    return rs;
  })
}

// 新增字典数据
export function addData(data) {
  return request({
    url: '/system/dict/data',
    method: 'post',
    data: data
  })
}

// 修改字典数据
export function updateData(data) {
  return request({
    url: '/system/dict/data',
    method: 'put',
    data: data
  })
}

// 删除字典数据
export function delData(dictCode) {
  return request({
    url: '/system/dict/data/' + dictCode,
    method: 'delete'
  })
}

// 导出字典数据
export function exportData(query) {
  return request({
    url: '/system/dict/data/export',
    method: 'get',
    params: query
  })
}
