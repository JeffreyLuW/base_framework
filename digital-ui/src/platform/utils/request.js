import axios from 'axios'
import { Message, Notification } from 'element-ui'
import store from '@/platform/store'
import { getToken } from '@/platform/utils/auth'
import errorCode from '@/platform/utils/errorCode'
import router from '../router/index.js';




axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
    // 创建axios实例
const service = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
     baseURL: 'http://localhost:18003',
     //开发服务器
    // baseURL: 'http://10.20.3.12:18003',
    // 超时
    timeout: 15000
});
// request拦截器
service.interceptors.request.use(config => {

    //如果没有强制忽略token 自动设置token
    if (config.ignoreToken !== true) {
        const token = getToken();
        if (token) {
            // 让每个请求携带自定义token 请根据实际情况自行修改
            config.headers['Authorization'] = 'Bearer ' + token;
        }
    }
    return config
}, error => {
    console.log(error)
    Promise.reject(error)
})

let lastLoginHint = 0;

//提示登录
function hintLogin() {
    //5s内部重复提示
    if (Date.now() - lastLoginHint < 5 * 1000) {
        return;
    }
    Message({ message: "请登录后再操作", type: 'error' });
    lastLoginHint = Date.now();
}

/**
 * 需要登录。
 * @return {Promise<never>}
 */
function needLogin(config) {
    if (!config || !config.isLogOutApi) {
        hintLogin();
        store.dispatch('LogOut').then(() => {
            router.replace("/login");
        });
    }
    return Promise.reject(new Error('Login is required'));
}

// 响应拦截器
service.interceptors.response.use(res => {
        //如果返回原生响应。 尤其是退出，如果退出也返回401可能会导致死循环
        if (res.config.rawResp) {
            return res;
        }
        // 未设置状态码则默认成功状态
        const code = res.data.code || 200;
        // 获取错误信息
        const msg = errorCode[code] || res.data.msg || errorCode['default']
        if (code === 401) {
            return needLogin(res.config);
        } else if (code === 500) {
            Message({ message: msg, type: 'error' });
            return Promise.reject(new Error(msg))
        } else if (code !== 200) {
            Notification.error({
                title: msg
            });
            return Promise.reject('error')
        } else {
            return res.data || {}
        }
    },
    error => {
        let code = error.code || error.response && error.response.status;
        console.log(code, 'err', error);
        if (code === 401) {
            return needLogin(error.config);
        }
        let { message } = error;
        if (error.response && error.response && error.response.data && error.response.data.msg) {
            message = error.response.data.msg;
        } else if (message == "Network Error") {
            message = "后端接口连接异常";
        } else if (message.includes("timeout")) {
            message = "系统接口请求超时";
        } else if (message.includes("Request failed with status code")) {
            message = "系统接口" + message.substr(message.length - 3) + "异常";
        }
        Message({
            message: message,
            type: 'error',
            duration: 5 * 1000
        });
        return Promise.reject(error)
    }
);

export default service
