import { getInfo, login, logout } from "@/platform/api/login";
import { getToken, removeToken, setToken } from "@/platform/utils/auth";

const user = {
  state: {
    token: getToken(),
    name: "",
    avatar: "",
    roles: [],
    permissions: [],
    bigscreen: false
  },

  mutations: {
    SET_TOKEN: (state, data) => {
      //避免测试塞入非法字符报错
      state.token = encodeURIComponent(data.token);
      state.bigscreen = data.bigscreen;
      setToken(data.token);
    },
    SET_NAME: (state, name) => {
      state.name = name;
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar;
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles;
    },
    SET_PERMISSIONS: (state, permissions) => {
      state.permissions = permissions;
    }
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      const username = userInfo.username.trim();
      const password = userInfo.password;
      const code = userInfo.code;
      const uuid = userInfo.uuid;
      return new Promise((resolve, reject) => {
        login(username, password, code, uuid)
          .then(res => {
            commit("SET_TOKEN", {
              token: res.token,
              bigscreen: res.bigscreen
            });
            resolve();
          })
          .catch(error => {
            reject(error);
          });
      });
    },

    // 获取用户信息
    GetInfo({ commit, state }) {
      return new Promise((resolve, reject) => {
        getInfo(state.token)
          .then(res => {
            const user = res.user;
            sessionStorage.setItem("userId", user.userId);
            const avatar =
              user.avatar == ""
                ? require("@/platform/assets/image/profile.jpg")
                : process.env.VUE_APP_BASE_API + user.avatar;
            if (res.roles && res.roles.length > 0) {
              // 验证返回的roles是否是一个非空数组

              commit("SET_ROLES", res.roles);
              commit("SET_PERMISSIONS", res.permissions);
            } else {
              commit("SET_ROLES", ["ROLE_DEFAULT"]);
            }
            commit("SET_NAME", user.userName);
            commit("SET_AVATAR", avatar);
            resolve(res);
          })
          .catch(error => {
            reject(error);
          });
      });
    },

    // 退出系统
    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        let onOver = () => {
          commit("SET_TOKEN", "");
          commit("SET_ROLES", []);
          commit("SET_PERMISSIONS", []);
          removeToken();
          resolve();
        };
        //// token失效 | 401，都可以认为已经成功退出。
        logout(state.token)
          .then(onOver)
          .catch(onOver);
      });
    },

    // 前端 登出
    FedLogOut({ commit }) {
      return new Promise(resolve => {
        commit("SET_TOKEN", "");
        removeToken();
        resolve();
      });
    }
  }
};

export default user;
