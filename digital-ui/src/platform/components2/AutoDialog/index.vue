<template>
  <el-dialog
    ref="dialog"
    class="overview-AutoDialog"
    :class="dialogClass"
    :visible="show"
    :width="width"
    :top="top"
    :append-to-body="true"
    :fullscreen="fullscreen"
    :modal="modal"
    :close-on-click-modal="closeOnClickModal"
    @open="onOpen"
    @opened="onOpened"
    :before-close="beforeClose"
    :custom-class="customClass"
  >
    <template slot="title">
      <span style="width: 400px; display: inline-block" class="single-line">
        <slot name="title">{{ title }}</slot>
      </span>
      <i
        v-if="fullscreen === true || fullscreen === false"
        class="el-icon-copy-document fullscreen-btn"
        @click="onClickFullScreen"
      ></i>
    </template>
    <!--     默认内容会添加到这里   -->
    <div ref="contentDiv" class="content-div" :class="contentClass" :style="contentStyle">
      <template v-if="smoothLoad">
        <transition name="el-fade-in">
          <div ref="contentDivLazyDiv" v-if="showInner" :key="index">
            <Jsx v-if="smoothLoad && contentLifeFn" :lifeFn="contentLifeFn" />
            <slot></slot>
          </div>
        </transition>
        <div
          v-if="!showInner"
          class="theme-text-color-regular layout-text-center"
          style="padding: 20px"
        >
          加载中...
        </div>
      </template>
      <slot v-else></slot>
      <Jsx v-if="!smoothLoad && contentLifeFn" :lifeFn="contentLifeFn" />
    </div>
    <div :class="footerClass" class="footer-btns" v-if="showFooter">
      <slot name="footer">
        <DataButton
          class="footer-btn"
          v-for="item in footerBtns"
          :key="item.key"
          :type="item.type"
          @click="clickClose(item.key)"
          :text="item.text"
          :loading="okBtnLoading && item.key === 'ok'"
        ></DataButton>
      </slot>
    </div>
  </el-dialog>
</template>

<script>
import Drag from "../../utils/dom_drag.js";

const defaultBtns = [
  { type: "default", text: "取消", key: "cancel" },
  { type: "primary", text: "确定", key: "ok" },
];
// 自适应对话框 组件 可以根据 sizeMode 显示不同模式
// 关闭监听 close 事件
export default {
  name: "overview-AutoDialog",
  props: {
    show: {}, //是否显示
    title: {
      default: "对话框", //标题 可以通过 template slot='title' 自定义
    },
    sizeMode: {
      default: "auto", // sizeMode:'auto|fullscreen|400px'    对话框尺寸，auto 自适应高度   fullscreen只是根据屏幕大小适应，但周边扔留空隙。
    },
    modal: {},
    showFooter: {
      type: Boolean, //是否显示底部按钮   可以通过 slot='footer'自定义按钮内容
      default: true,
    },
    footerBtns: {
      default: () => defaultBtns, //可以定制footer的按钮
    },
    footerHeight: {
      type: Number,
      default: 38, //footer的高度，如果重新调整了样式高度，需要修改，否则不需要改动
    },
    fullscreen: {
      type: Boolean, //传null，则不显示全屏按钮。 简单使用  fullscreen.sync='fullscreen'
      default: null,
    },
    width: {
      default: "1050px",
    },
    top: {
      type: String,
      default: "15vh",
    },
    closeOnClickModal: {
      type: Boolean,
      default: false, //点击遮罩自动关闭
    },
    draggable: {
      type: Boolean, //是否可以拖动
      default: true,
    },
    contentClasseObject: {
      type: Object,
      default: () => ({ lrPadding20: true }),
    },
    onCloseFn: {
      type: Function, // onCloseFn(btnKey,dialog,next)
    },
    onOpenFn: {
      type: Function, //
    },
    onOpenedFn: {
      type: Function, //
    },
    smoothLoad: {
      type: Boolean, //是否开启顺滑加载，用于大页面加载不流畅时使用
      default: false,
    },
    contentLifeFn: {
      type: Function,
    },
    dragInWindowContent: {
      type: Boolean, //默认在window内部拖动，不能超出窗口
      default: true,
    },
    customClass: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      firstOpen: true,
      showInner: false,
      index: 0,
      okBtnLoading: false,
    };
  },
  created() {},
  mounted() {
    let that = this
    let dialogHeader = this.$el.querySelector(".el-dialog__header");
    dialogHeader.addEventListener("dblclick", this.onClickFullScreen);
    Drag.init(
      dialogHeader,
      this.$el.querySelector(".el-dialog"),
      (handler, root, Drag) => {
        if (!this.dragInWindowContent) {
          return { canceled: this.fullscreen || !this.draggable };
        }
        let domDialog = this.$el.querySelector(".el-dialog");
        let rect = domDialog.getBoundingClientRect();
        let styleLeft = parseInt(domDialog.style.left || 0);
        let styleTop = parseInt(domDialog.style.top || 0);
        return {
          //canceled: !this.draggable // init: false,// minX: 0,// maxX: 100,// minY: 0,// maxY: 100,
          canceled: this.fullscreen || !this.draggable, // init: false,
          minX: styleLeft - rect.left,
          maxX: window.innerWidth - rect.width + styleLeft - rect.left,
          minY: styleTop - rect.top,
          maxY: window.innerHeight - rect.height + styleTop - rect.top,
        };
      },
      null, null, null, null, null, null, null, null,
      ()=>{
        // console.log(this.$bus.$emit);
        this.$bus.$emit('startVideoResize')  
      },
      ()=>{
        this.$bus.$emit('endVideoResize')  
      }
    );
  },
  watch: {
    fullscreen(v) {
      if (v) {
        let dragRoot = this.$el.querySelector(".el-dialog");
        dragRoot.style.left = "";
        dragRoot.style.top = "";
      }
    },
    show(v) {
      if (!v) {
        this.showInner = false;
      }
    },
  },
  computed: {
    contentClass() {
      return {
        "content-div-abs": this.sizeMode === "fullscreen" || this.fullscreen,
        ...this.contentClasseObject,
      };
    },
    contentStyle() {
      let style = {};
      if (this.sizeMode !== "auto" && this.sizeMode !== "fullscreen") {
        // let height = parseInt(this.sizeMode);
        // (this.showFooter ? height - this.footerHeight : height) + "px";
        style.height = this.sizeMode;
      }
      if (this.sizeMode === "auto") {
        //设置最大高度，超过屏幕自动滚动中心区域  80% 50 49px 48px
        if (window.innerHeight) {
          let maxHeight = 0;
          if (this.fullscreen) {
            maxHeight = Math.floor(window.innerHeight - 41);
          } else {
            maxHeight = Math.floor(window.innerHeight * 0.8 - 41 - 49);
          }
          if (this.showFooter) maxHeight -= this.footerHeight;
          style.maxHeight = maxHeight + "px";
        }
      }
      if (this.sizeMode === "fullscreen" || this.fullscreen) {
        if (this.showFooter) style.height = `calc(100% - ${this.footerHeight}px)`;
        else style.height = `100%`;
      }
      return style;
    },
    dialogClass() {
      return {
        "full-dialog": this.sizeMode === "fullscreen" || this.fullscreen,
        "draggable-dialog": this.draggable, // .draggable-dialog .el-dialog__header
      };
    },
    footerClass() {
      return {
        "footer-btns-abs": this.sizeMode === "fullscreen" || this.fullscreen,
      };
    },
  },
  methods: {
    beforeClose(done) {
      done();
      this.clickClose("close");
    },
    //btnKey:close cancel ok
    clickClose(btnKey) {
      if (this.onCloseFn) {
        if (this.onCloseFn.length === 3) {
          this.onCloseFn(btnKey, this, () => {
            this.emitClose(btnKey);
          });
        } else {
          this.emitClose(btnKey);
          this.onCloseFn(btnKey, this);
        }
      } else {
        this.emitClose(btnKey);
      }
    },
    //事件: close cancel ok
    emitClose(btnKey) {
      this.$emit("update:show", false);
      this.$emit("close", btnKey);
    },
    onClickFullScreen() {
      let fullscreen = this.fullscreen;
      this.$emit("update:fullscreen", !fullscreen);
      this.$emit("fullscreen", !fullscreen);
      if (this.$util && this.$util.rootVue) {
        this.$util.rootVue.$emit("auto-dialog-fullscreen", fullscreen);
      }
    },
    //对话框打开时的回调
    onOpen() {
      this.$emit("open", this.firstOpen);
      this.firstOpen = false;
      if (this.onOpenFn) {
        this.onOpenFn(this.$refs.contentDiv, this);
      }
    },
    onOpened() {
      this.index++;
      this.showInner = true;
      this.$emit("opened");
      if (this.onOpenedFn) {
        this.onOpenedFn(this.$refs.contentDiv, this);
      }
    },
  },
};
</script>

<style lang="scss">
.overview-AutoDialog {
  overflow: hidden;

  .el-dialog__header {
    /*color: white;*/
    /*background: #252b3a;*/
    padding: 18px 20px 10px 20px;
  }

  .el-dialog__headerbtn .el-dialog__close {
    /*color: white;*/
  }

  .el-dialog__headerbtn {
    top: 10px;
  }

  .el-dialog.is-fullscreen {
    max-height: 100%;
    margin-top: 0 !important;
    height: 100%;
    position: relative;

    .el-dialog__body {
      height: calc(100% - 46px) !important;
      position: relative;
    }
  }

  &.draggable-dialog .el-dialog__header {
    cursor: move;
  }

  .el-dialog__body {
    height: auto;
    padding: 0;
    font-size: 12px;
    overflow-y: auto !important;
  }

  .content-div {
    overflow-y: auto;
    box-sizing: border-box;
    position: relative;
    min-height: 260px;
  }

  .content-div-abs {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
  }

  .fullscreen-btn {
    position: absolute;
    right: 44px;
    top: 6px;
    padding: 6px;
    cursor: pointer;
    opacity: 0.4;

    &:hover {
      opacity: 0.6;
    }
  }

  .footer-btns-abs {
    position: absolute;
    right: 0;
    bottom: 0;
    height: 38px;
    width: 100%;
    box-sizing: border-box;
    text-align: center;
    padding-top: 10px;
  }

  .footer-btns {
    text-align: right;
    height: 38px;
    padding: 0px 20px 0px 20px;
  }

  .footer-btn {
    min-width: 76px;
  }

  .padding20 {
    padding: 20px;
  }

  .lrPadding20 {
    padding: 10px 20px 10px 20px;
  }

  .padding10 {
    padding: 10px;
  }

  .padding10_no_bottom {
    padding: 10px;
    padding-bottom: 0;
  }
}

.full-dialog {
  .el-dialog {
    height: calc(100% - 25vh);
    position: relative;

    .el-dialog__body {
      position: relative;
      height: calc(100% - 46px);
    }
  }
}
</style>
