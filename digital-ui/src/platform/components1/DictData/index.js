import Vue from 'vue'
import DataDict from '@/platform/utils/dict'
import { getDicts as getDicts } from '@/platform/api/system/dict/data'

function install() {
  Vue.use(DataDict, {
    metas: {
      '*': {
        labelField: 'dictLabel',
        valueField: 'dictValue',
        request(dictMeta) {
          return getDicts(dictMeta.type).then(res => res.data)
        },
      },
    },
  })
}

export default {
  install,
}