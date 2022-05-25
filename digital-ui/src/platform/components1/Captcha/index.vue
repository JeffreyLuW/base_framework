<template>
  <div class="Captcha">
    <el-input
      :value="value"
      @input="onInput"
      auto-complete="off"
      placeholder="验证码"
      style="width: 63%"
      @keyup.enter.native="onEnter"
    >
<!--      <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon"/>-->
    </el-input>
    <div class="login-code">
      <img :src="codeUrl" @click="getCode" class="login-code-img"/>
    </div>
  </div>
</template>

<script>
  import {getCodeImg} from "@/platform/api/login";

  export default {
    name: "Captcha",
    props: {
      value: {
        type: String,
        default: ''
      },
      initFn: {
        type: Function,
      }
    },
    data() {
      return {
        codeUrl: '',
        uuid: null,
      }
    },
    created() {
      if (this.initFn) {
        this.initFn(this);
      }
      this.getCode();
    },
    methods: {
      getCode() {
        getCodeImg().then(res => {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.uuid = res.uuid;
        });
      },
      onInput(v) {
        this.$emit("input", v);
      },
      onEnter() {
        this.$emit("enter");
      },
    }
  }
</script>

<style scoped lang="scss">
  .login-code {
    width: 33%;
    height: 34px;
    float: right;
    position: relative;

    img {
      cursor: pointer;
      vertical-align: middle;
    }
  }

  .login-code-img {
    height: 32px;
    width: 100%;
  }
</style>
