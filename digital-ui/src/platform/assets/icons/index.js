import Vue from 'vue'
import SvgIcon from '@/platform/components2/SvgIcon'// svg component

import './iconfont'
import './myIconfont'

// register globally
Vue.component('svg-icon', SvgIcon);

const req = require.context('./svg', false, /\.svg$/);
const requireAll = requireContext => requireContext.keys().map(requireContext);
requireAll(req);
