<template>
  <div v-loading="loading" :style="'height:'+ height" style="overflow-y: hidden;">
    <iframe :src="src" frameborder="no" style="width: 100%;height: 100%" scrolling="auto"/>
  </div>
</template>
<script>
  export default {
    name: "Swagger",
    data() {
      let dev = process.env.NODE_ENV === "development";//process.env.VUE_APP_BASE_API
      let proxUrl = process.env.VUE_APP_PROXY_URL || '';
      return {
        src: dev ? proxUrl + "/swagger/doc.html" : "/swagger/doc.html",//后端修改地址时，注意修改这里。
        height: document.documentElement.clientHeight - 60 + "px;",
        loading: true
      };
    },
    mounted: function () {
      setTimeout(() => {
        this.loading = false;
      }, 230);
      const that = this;
      window.onresize = function temp() {
        that.height = document.documentElement.clientHeight - 94.5 + "px;";
      };
    }
  };
</script>
