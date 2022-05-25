import simple_util from '../../utils/simple_util.js';

const state = {
  sidebar: {
    opened: simple_util.localStorage('sidebarStatus') || true,
    withoutAnimation: false,
    hide: false
  },
  device: 'desktop',
  size: simple_util.localStorage('size') || 'medium'
}

const mutations = {
  TOGGLE_SIDEBAR: state => {
    state.sidebar.opened = !state.sidebar.opened
    state.sidebar.withoutAnimation = false
    if (state.sidebar.opened) {
      simple_util.localStorage('sidebarStatus', true)
    } else {
      simple_util.localStorage('sidebarStatus', false)
    }
  },
  CLOSE_SIDEBAR: (state, withoutAnimation) => {
    simple_util.localStorage('sidebarStatus', false)
    state.sidebar.opened = false
    state.sidebar.withoutAnimation = withoutAnimation
  },
  TOGGLE_DEVICE: (state, device) => {
    state.device = device
  },
  SET_SIZE: (state, size) => {
    state.size = size
    simple_util.localStorage('size', size)
  },
  SET_SIDEBAR_HIDE: (state, status) => {
    state.sidebar.hide = status
  }
}

const actions = {
  toggleSideBar({ commit }) {
    commit('TOGGLE_SIDEBAR')
  },
  closeSideBar({ commit }, { withoutAnimation }) {
    commit('CLOSE_SIDEBAR', withoutAnimation)
  },
  toggleDevice({ commit }, device) {
    commit('TOGGLE_DEVICE', device)
  },
  setSize({ commit }, size) {
    commit('SET_SIZE', size)
  },
  toggleSideBarHide({ commit }, status) {
    commit('SET_SIDEBAR_HIDE', status)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
