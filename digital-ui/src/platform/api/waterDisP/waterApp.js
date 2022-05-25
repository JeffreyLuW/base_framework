import request from "@/platform/utils/request";

// 查询接口详细
export function LIST(query) {
    return request({
        url: "/irrWaterIntakeApply/list",
        method: "get",
        params: query
    });
}
// 获取详细
export function getDetail(id, year, month) {
    return request({
        url: '/irrWaterIntakeApply/info/',
        method: "get",
        params: {
            id: id,
            year: year,
            month: month
        }
    });
}
//变更取水申请日期的时候同步变更的许可水量等信息
export function getNeedWaterByDate(date) {
    return request({
        url: `/irrWaterIntakeApply/getNeedWaterByDate/${date}`,
        method: 'get'
    })
}

// 新增
export function ADD(data) {
    return request({
        url: `/irrWaterIntakeApply/add`,
        method: "post",
        data: data
    });
}

// 删除
export function DEL(id) {
    return request({
        url: `/irrWaterIntakeApply/${id}`,
        method: "delete"
    });
}

// 修改
export function UPDATE(data) {
    return request({
        url: `/irrWaterIntakeApply/`,
        method: "PUT",
        data: data
    });
}

//保存文件关联
export function saveFileKey(data) {
    return request({
        url: `/irrWaterIntakeApply/saveFileKey`,
        method: "get",
        params: data
    });
}