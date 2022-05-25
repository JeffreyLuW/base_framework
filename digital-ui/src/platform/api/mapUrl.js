import request from "@/platform/utils/request";

export function getMapUrl(query) {
  return request({
    url: "/mapConfig/getMap",
    method: "get",
    params: query
  });
}
