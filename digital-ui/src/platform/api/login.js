import request from '@/platform/utils/request'

// 登录方法
export function login(username, password, code, uuid) {
  const data = {
    username,
    password,
    code,
    uuid
  }
  return request({
    url: '/login',
    method: 'post',
    data: data,
    ignoreToken: true
  })
}

// 获取用户详细信息
export function getInfo() {
  return request({
    url: '/getInfo',
    method: 'get'
  })
}

// 退出方法
export function logout() {
  return request({
    url: '/logout',
    method: 'post',
    rawResp: true,
    isLogOutApi:true,//标记当前就是退出登录的API，不需要再次跳转登出操作
  })
}

// 获取验证码
export function getCodeImg() {
  return request({
    url: '/captchaImage',
    method: 'get',
    ignoreToken: true
  })
}

//程序进入之前，调用接口，获取服务器的一些基本信息和配置。
//与用户是否登录无关!通用配置。
export function prepareConfig() {
  return request({
    url: '/prepareConfig',
    method: 'get',
    ignoreToken: true
  })
}

//修改密码
export function resetPwd(data) {
  return request({
    url: '/resetPwd',
    method: 'put',
    data
  })
}

//获取二维码
export function getQrCode() {
  return request({
    url: '/appVersion/qrcode?key=app.qrcode.url',
    method: 'get',
  })
}

//获取二维码加密信息
export function getQrCodeInfo(data) {
  return request({
    url: '/sm2/encryptBcd',
    method: 'post',
    data:data
  })
}

