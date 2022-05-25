<template>
  <el-color-picker
    v-model="theme"
    :predefine="['#2661a7','#304156','#409EFF','#212121','#11a983', '#13c2c2', '#6959CD', '#f5222d', ]"
    class="theme-picker"
    popper-class="theme-picker-dropdown"
  />
</template>

<script>
  // const version = require('element-ui/package.json').version // element-ui version from node_modules
  // const ORIGINAL_THEME = '#409EFF' // default color
  import theme_manager from '../../utils/theme_manager.js';

  export default {
    data() {
      return {
        chalk: '', // content of theme-chalk css
        theme: ''
      }
    },
    computed: {
      defaultTheme() {
        return this.$store.state.settings.theme
      }
    },
    watch: {
      defaultTheme: {
        handler: function (val, oldVal) {
          this.theme = val
        },
        immediate: true
      },
      async theme(val) {
        await theme_manager.applyTheme(val);
        this.$emit('change', val)
      }
    },

    methods: {}
  }
</script>

<style>
  .theme-message,
  .theme-picker-dropdown {
    z-index: 99999 !important;
  }

  .theme-picker .el-color-picker__trigger {
    height: 26px !important;
    width: 26px !important;
    padding: 2px;
  }

  .theme-picker-dropdown .el-color-dropdown__link-btn {
    display: none;
  }
</style>
