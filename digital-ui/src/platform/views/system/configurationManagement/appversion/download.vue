<template>
  <div>
    <!-- <img src="~@/assets/appversion/logo_text.png" width="180" height="30" id="text"/> -->
    <el-button @click="download" round class="btn" size="medium" type="warning">下载</el-button>
  </div>

</template>

<script>
import {getNewVersion} from "@/platform/api/system/appversion";
  export default {
    data() {
      return {
        type : "android",
        tips : false,
        baseUrl: process.env.VUE_APP_BASE_API,   //  baseUrl
      };
    },

    created() {
      let ua = navigator.userAgent.toLowerCase();

      if (/iphone|ipad|ipod/.test(ua)) {
        this.type = "ios";

      } else if (/android/.test(ua)) {
        this.type = "android";

      }

      if(this.isWeixinBrowser()){
        this.$message({
          type: 'info',
          message: '请在手机浏览器中打开',
          duration:0

        });
      }

    },

    methods: {
      download() {
        const that = this;
        if(that.type == 'android') {
          const query = {appkey:"com.meide"}
          getNewVersion(query).then(res=>{
            if(res.code==200){
              window.location.href = this.baseUrl+res.data.downloadUrl;
            }
          })


        } else {
          window.location.href = 'IOS下载地址';

        }

      },
      isWeixinBrowser() {
        let ua = navigator.userAgent.toLowerCase();
        return (/micromessenger/.test(ua)) ? true : false;

      }

    }

  };

</script>
<style>
  html,body{
    width:100%;
    height:100%;
  }
  body {
    background: url('~@/assets/appversion/bg_welcome.png');
    background-repeat: no-repeat;
    background-size: 100% 100%;
    -webkit-background-size: 100% 100%;
    background-attachment: fixed;
    -webkit-background-attachment: fixed;
  }
  #text{
    margin: 50px auto;
    display: flex;
    justify-content: center;
  }
  .btn{
    position: absolute;
    bottom: 10%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 300px;
  }
</style>
