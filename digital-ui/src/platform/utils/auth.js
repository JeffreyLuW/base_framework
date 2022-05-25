import simple_util from './simple_util.js';

const TokenKey = 'Admin-Token'

export function getToken() {
  return simple_util.localStorage(TokenKey)
}

export function setToken(token) {
  return simple_util.localStorage(TokenKey, token)
}

export function removeToken() {
  return simple_util.localStorageRemove(TokenKey)
}
