<template>
  <div class="login-right" :style="loginStyle">
    <div class="logobg">
      <img src="../assets/image/title.png" alt="" />
    </div>
    <el-form
      ref="loginForm"
      :model="loginForm"
      :rules="loginRules"
      class="login-form theme-bg-pannel"
    >
      <h3 class="title" v-once>{{ loginTitle }}</h3>
      <div class="login-content" v-show="isEWM === false">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            type="text"
            auto-complete="off"
            placeholder="账号"
          >
            <svg-icon
              slot="prefix"
              icon-class="user"
              class="el-input__icon input-icon"
            />
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            auto-complete="off"
            placeholder="密码"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon
              slot="prefix"
              icon-class="password"
              class="el-input__icon input-icon"
            />
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-input
            v-model="loginForm.code"
            auto-complete="off"
            placeholder="验证码"
            style="width: 63%"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon
              slot="prefix"
              icon-class="validCode"
              class="el-input__icon input-icon"
            />
          </el-input>
          <div class="login-code">
            <img :src="codeUrl" @click="getCode" class="login-code-img" />
          </div>
        </el-form-item>
        <el-checkbox
          v-model="loginForm.rememberMe"
          style="margin: 0px 0px 25px 0px"
          >记住密码</el-checkbox
        >
        <!-- <div class="text-align-right">
          <DataButton
            class="update-pwd-btn"
            type="text"
            text="修改密码"
            @click="clickUpdatePwd"
          ></DataButton>
        </div> -->
        <el-form-item style="width: 100%">
          <el-button
            :loading="loading"
            size="medium"
            type="primary"
            style="width: 100%;"
            @click.native.prevent="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登 录 中...</span>
          </el-button>
        </el-form-item>
      </div>
      <div
        class="ewm-content"
        v-show="isEWM && qrCodeType == 2"
        ref="logeinQrCodeUrl"
      >
        <vue-qr
          :logoSrc="config.logo"
          :text="config.value"
          class="qr-code-pic"
          :correctLevel="3"
          :margin="0"
        ></vue-qr>
        <div class="refresh">
          <span style="color:red;">{{ bottomText }}</span>
          <span
            class="sec"
            @click="setConfig"
            style="color:#2563bb;margin-left:10px"
            >刷新</span
          >
        </div>
      </div>
      <div class="ewm-content" v-if="isEWM && qrCodeType == 1">
        <img :src="qRCodeuRL" alt="" />
      </div>

    </el-form>

    <video muted autoplay loop class="bgvido" style="opacity: 1" v-if="show">
      <source type="video/mp4" src="../assets/logo/video-diqiu.mp4" />
    </video>
    <!--  底部  -->
    <div class="el-login-footer">
      <span>Copyright © 2020-2021 Meide All Rights Reserved.</span>
    </div>
  </div>
</template>

<script lang="jsx">
import { getCodeImg, resetPwd, getQrCode, getQrCodeInfo } from "../platform/api/login";
import { decrypt, encrypt } from "../platform/utils/jsencrypt";
import loginBackgroundImage from "../assets/image/login-background.jpg";
import formhelper from "../platform/utils/formhelper.js";
import PwdChecker from "../platform/utils/pwd_checker.js";
import Captcha from "../platform/components2/Captcha/index.vue";
import { getToken } from "@/platform/utils/auth";
import Util from "@/platform/utils/util";
import VueQr from "vue-qr";

export default {
  name: "Login",
  components: {
    VueQr,
  },
  data() {
    return {
      isEWM: false,
      codeUrl: "",
      cookiePassword: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: "",
        qRCodeuRL: "",
      },
      loginRules: {
        username: [
          { required: true, trigger: ["change", "blur"], message: "用户名不能为空" },
        ],
        password: [
          { required: true, trigger: ["change", "blur"], message: "密码不能为空" },
        ],
        code: [
          { required: true, trigger: ["change", "blur"], message: "验证码不能为空" },
        ],
      },
      loading: false,
      redirect: undefined,
      count: 1,
      show: false,
      qrCodeType: "",
      config: {
        value: "",
      },
      uuid: "",
      soket: "",
      soketPath: ``,
      bottomText:'请使用APP扫码登录'
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true,
    },
  },
  created() {
    this.soketPath = process.env.VUE_APP_BASE_SOKET_API+'/websocket/'
    this.uuid = Util.uuid().replace(/-/g, "");
    this.getCode();
    this.getCookie();
    this.goGrdoupRecor();
    //this.getQrCode();
  },
  mounted() {
    this.init();
  },
  beforeDestroy() {
    this.socket.close();
  },
  computed: {
    loginStyle() {
      let sysConfig = this.$store.state.settings.sysConfigs;
      let loginBgConfig = sysConfig["page.login.bg"];
      let style = {
        backgroundImage: `url(${loginBackgroundImage})`,
      };
      let configUrl = this.$util.getSysConfigParamUrl(loginBgConfig);
      if (configUrl) {
        /*if(!configUrl.indexOf("http") != -1 ){
            configUrl = process.env.VUE_APP_BASE_API + configUrl;
          }*/
        style.backgroundImage = `url(${configUrl})`;
      } else if (loginBgConfig && "N" === loginBgConfig.configIsImg) {
        //非图片 当颜色处理
        if (loginBgConfig.configValue.indexOf("gradient") === -1) {
          delete style.backgroundImage;
          style.backgroundColor = loginBgConfig.configValue;
        } else {
          //渐变背景
          style.backgroundImage = loginBgConfig.configValue;
        }
      }
      return style;
    },
    loginTitle() {
      let sysConfig = this.$store.state.settings.sysConfigs;
      let titleConfig = sysConfig["page.sys.name"];
      let title = titleConfig.configValue || "后台管理系统";
      return title;
    },
  },
  methods: {
    setConfig() {
      this.bottomText = '请使用APP扫码登录'
      let tmp = JSON.stringify({
        clientId: this.uuid,
        timestamp: new Date().getTime(),
      });
      getQrCodeInfo({ str: tmp }).then((res) => {
        this.config = {
          logo: require('../assets/logo/qrCodeLogo.png'),
          value: JSON.stringify({
            type: "login",
            info: res.data,
          }),
        };

      });
    },
    showEwm(type) {
      if (type == this.qrCodeType) {
        this.isEWM = !this.isEWM;
      } else {
        this.qrCodeType = type;
        this.isEWM = true;
      }
      if(type == 2){
        this.setConfig()
      }
    },
    getCode() {
      getCodeImg().then((res) => {
        this.codeUrl = "data:image/gif;base64," + res.img;
        this.loginForm.uuid = res.uuid;
      });
    },
    goGrdoupRecor() {
      const TIME_COUNT = 5;
      if (!this.timer) {
        this.timer = setInterval(() => {
          if (this.count > 0 && this.count <= TIME_COUNT) {
            this.count++;
          } else {
            this.show = true;
            clearInterval(this.timer);
            this.timer = null;
          }
        }, 1000);
      }
    },
    getCookie() {
      const username = this.$util.localStorage("username");
      const password = this.$util.localStorage("password");
      const rememberMe = this.$util.localStorage("rememberMe");
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            this.$util.localStorage("username", this.loginForm.username);
            this.$util.localStorage("password", encrypt(this.loginForm.password));
            this.$util.localStorage("rememberMe", this.loginForm.rememberMe);
          } else {
            this.$util.localStorageRemove("username");
            this.$util.localStorageRemove("password");
            this.$util.localStorageRemove("rememberMe");
          }
          this.$store
            .dispatch("Login", this.loginForm)
            .then(() => {
              this.$router.push({path: this.redirect || "/"});
            })
            .catch(() => {
              this.loading = false;
              this.getCode();
            });
        }
      });
    },
    //点击修改密码。
    clickUpdatePwd() {
      let captcha = null;
      let onCaptchaInit = (_captcha) => {
        captcha = _captcha;
      };
      this.$dialog.show({
        title: "修改密码",
        width: "450px",
        component: "DataEditForm",
        top: "calc(50vh - 150px)",
        propsData: {
          edit: formhelper.create(
            [
              formhelper.itemCreator.input(
                "用户名",
                "username",
                "",
                this.$rules.required()
              ),
              formhelper.itemCreator.password(
                "旧密码",
                "oldPassword",
                "",
                this.$rules.required("密码不能为空")
              ),
              formhelper.itemCreator.password(
                "新密码",
                "newPassword",
                "",
                PwdChecker.autoCheckPwdRules(this)
              ),
              formhelper.itemCreator.jsx(
                "验证码",
                "code",
                "",
                this.$rules.required(),
                (h, jsx) => {
                  return (
                    <Captcha
                      value={jsx.$attrs.value}
                      onInput={jsx.$listeners.input}
                      initFn={onCaptchaInit}
                    />
                  );
                }
              ),
            ],
            false
          ),
        },
        onAutoOk: (rs, dialog, next) => {
          if (!captcha) return;
          rs.uuid = captcha.uuid;
          dialog.okBtnLoading = true;
          resetPwd(rs)
            .then(() => {
              this.msgSuccess("修改成功");
              next();
            })
            .catch(() => {
              captcha.getCode();
              dialog.okBtnLoading = false;
            });
        },
      });
    },
    //获取二维码
    getQrCode() {
      getQrCode().then((res) => {
        this.qRCodeuRL = process.env.VUE_APP_BASE_API + res.data;
      });
    },

    init() {
      if (typeof WebSocket === "undefined") {
        alert("您的浏览器不支持socket");
      } else {
        // 实例化socket
        this.socket = new WebSocket(this.soketPath + this.uuid);
        // 监听socket连接
        this.socket.onopen = this.open;
        // 监听socket错误信息
        this.socket.onerror = this.error;
        // 监听socket消息
        this.socket.onmessage = this.getMessage;
        this.socket.onclose = this.close;
      }
    },
    open() {
      console.log("socket连接成功");
    },
    error() {
      console.log("连接错误");
    },
    getMessage(msg) {
      let a = JSON.parse(msg.data);
      if (a.type == "login") {
        if (a.code !== 200) return this.bottomText = a.msg;
        this.$store.commit("SET_TOKEN", a.data);
        this.socket.close();
        // if(a.data.bigscreen){
        //   window.location.href = `visu/index.html?token=${getToken()}`;
        // }else{
        //    this.$router.push({path: this.redirect || "/"});
        // }
        this.$router.push({path: this.redirect || "/"});

      }else if(a.type == 'loginMessage'){
        if (a.code !== 200) return this.$message.error(a.msg);
        this.bottomText = a.msg
        setTimeout(() => {
          this.bottomText = '登录超时'
        }, 30000);
      }
    },
    send() {
      this.socket.send(params);
    },
    close() {
      console.log("socket已经关闭");
    },
  },
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login-content {
  height: 265px;
}
.ewm-content {
  width: 100%;
  height: 265px;
  background-color: #fff;
  text-align: center;
  padding-bottom: 5px;
  img {
    width: 70%;
  }
  .refresh {
    position: relative;
    padding-top: 5px;
    cursor: pointer;
    .sec {
      position: absolute;
      right: 45px;
    }
  }
}

.phone-app {
  width: 100%;
  display: flex;
  justify-content: space-between;
  .phone {
    cursor: pointer;
    width: 145px;
    height: 35px;
    line-height: 35px;
    text-align: center;
    border: 1px solid #cfdcf1;
    border-radius: 5px;
    font-size: 14px;
    color: #2563bb;
    img {
      width: 22px;
      vertical-align: middle;
    }
  }
}
.login-center {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  /*background-image: url("../assets/image/login-background.jpg");*/
  background-size: cover;

  .title {
    margin: 0px auto 30px auto;
    text-align: center;
  }

  .login-form {
    border-radius: 6px;
    height: 375px !important;
    width: 400px;
    padding: 25px 25px 5px 25px;

    .el-input {
      height: 38px;

      input {
        height: 38px;
      }
    }

    .input-icon {
      height: 39px;
      width: 14px;
      margin-left: 2px;
    }
  }

  .login-tip {
    font-size: 13px;
    text-align: center;
    color: #bfbfbf;
  }

  .login-code {
    width: 33%;
    height: 38px;
    float: right;
    position: relative;

    img {
      cursor: pointer;
      vertical-align: middle;
    }
  }

  .el-login-footer {
    height: 40px;
    line-height: 40px;
    position: fixed;
    bottom: 0;
    width: 100%;
    text-align: center;
    color: #fff;
    font-family: Arial;
    font-size: 12px;
    letter-spacing: 1px;
  }

  .login-code-img {
    height: 38px;
    width: 100%;
  }

  .update-pwd-btn {
    position: relative;
    top: -8px;
  }
}

.login-right {
  overflow: hidden;
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
  /*background-image: url("../assets/image/login-background.jpg");*/
  background-size: cover;
  .title {
    margin: 0px auto 2.2rem auto;
    text-align: center;
  }

  .login-form {
    position: relative;
    margin-left: auto;
    padding-right: 30px;
    top: 0;
    right: 20px;
    z-index: 9999;
    bottom: 0;
    border-radius: 6px;
    width: 350px;
    padding: 2em 1em 1em 1em;
    background: #fff;
    .el-input {
      height: 38px;

      input {
        height: 38px;
      }
    }

    .input-icon {
      height: 39px;
      width: 14px;
      margin-left: 2px;
    }
  }

  .login-tip {
    font-size: 13px;
    text-align: center;
    color: #bfbfbf;
  }

  .login-code {
    width: 33%;
    height: 38px;
    float: right;
    position: relative;

    img {
      cursor: pointer;
      vertical-align: middle;
    }
  }

  .el-login-footer {
    height: 40px;
    line-height: 40px;
    position: fixed;
    bottom: 0;
    width: 100%;
    text-align: center;
    color: #fff;
    font-family: Arial;
    font-size: 12px;
    letter-spacing: 1px;
  }

  .login-code-img {
    height: 38px;
    width: 100%;
  }

  .update-pwd-btn {
    position: relative;
    top: -8px;
  }
}

.bgvido {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
  min-height: 100%;
  min-width: 1024px;
  width: 100%;
  height: auto;
}

.bgvidohe {
  position: absolute;
  top: 41%;
  left: 10%;
  z-index: 1;
  filter: alpha(opacity=90);
  min-height: 100%;
  min-width: 624px;
  width: 80%;
  height: auto;
}

.logobg {
  z-index: 9999;
  position: relative;
  padding: 0 3em;
  top: -34%;
  color: #fff;
  font-weight: 700;
  font-size: 1.5em;
  letter-spacing: 0.1em;
}
</style>
