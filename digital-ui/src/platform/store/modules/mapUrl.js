import { getMapUrl } from "@/platform/api/mapUrl";

const mapUrl = {
  state: {
    mapUrl: {}
  },

  mutations: {
    SET__MAP_URL: (state, data) => {
      state.mapUrl = data;
      localStorage.setItem("mapUrl", JSON.stringify(data));
    }
  },

  actions: {
    // 登录
    GET_MAP_URL(context) {
      return new Promise((resolve, reject) => {
        getMapUrl()
          .then(res => {
            context.commit("SET__MAP_URL", res.data);
            resolve();
          })
          .catch(error => {
            reject(error);
          });
      });
    }
  }
};

export default mapUrl;
