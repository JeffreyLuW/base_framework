import request from "@/platform/utils/request";
// 审核 

export function examine(data) {
    return request({
      url: `/irrWaterPlanExamine/examine`,
      method: "POST",
      data:data
    });
  }