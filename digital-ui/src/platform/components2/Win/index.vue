<template>
  <div v-show="outerShow" ref="root" class="Win theme-bg-control theme-text-color-regular theme-border-color3"
       :class="{fullscreen,resize:canResize}"
       :style="frameStyle" @mousedown="onMouseDownTitle">
    <div v-if="showTitle" class="Win_title theme-border-color3 theme-bg-title" ref="title"
         @dblclick.self.capture="onClickFullScreen">
      <slot name="title">{{title}}</slot>
      <div class="right-btns">
        <i class="right-btn-item theme-bg-primary theme-text-color-darkop el-icon-copy-document fullscreen-btn"
           @click.stop.prevent="onClickFullScreen"></i>
        <i class="right-btn-item theme-bg-primary theme-text-color-darkop win__close el-icon el-icon-close"
           @click.stop.prevent="onClickClose"></i>
      </div>
    </div>
    <div ref="body" class="Win_body" v-if="innerShow">
      <slot></slot>
      <Jsx v-if="jsxSlot" :jsx="jsxSlot"></Jsx>
    </div>
    <span ref="resize" v-if="canResize&&!fullscreen" class="Win_resize" @mousedown="onResizeMouseDown"></span>
  </div>
</template>

<script>
  import Drag from "../../utils/dom_drag.js";
  import {moveTop, pushWin, removeWin, setWinStyleZIndex, AllWindows} from './win_helper.js';

  //类似窗口行为的div。内容为懒加载。
  //窗口被拖动后,如果想重置位置 可以调用 resetPos(true)
  //因为支持拖动和缩放,故有些值(如尺寸位置)只是决定初始值，不完全是响应式的，也就不会跟随属性变化双向绑定。
  //调试尺寸位置时，建议刷新页面或者使用v-if
  export default {
    name: "Win",
    props: {
      show: {
        type: Boolean,
        default: false //是否显示.如果希望内容是每次都新建 则自行在内容区域 添加 v-if="show"
      },
      modal: {
        type: Boolean,
        default: false,
      },
      title: {
        type: String,
      },
      showTitle: {
        type: Boolean,//是否显示标题栏
        default: true,
      },
      position: {
        type: String | Object,//Object:{left:20, top:40} String: top middle bottom left center right 水平+竖向6个单词的组合
        default: "center" //默认窗口在中间
      },
      offsetX: {
        type: Number,// 位置偏移
        default: 0
      },
      offsetY: {
        type: Number,// 位置偏移
        default: 0
      },
      draggable: {
        type: Boolean,
        default: true,//是否可拖动
      },
      minWidth: {
        type: Number,
        default: 200
      },
      minHeight: {
        type: Number,
        default: 100
      },
      width: {
        type: String,//初始宽 (因为窗口可以缩放，故窗口显示以后尺寸不随属性强制绑定)
        default: null,//宽高都不指定，则窗口尺寸为自适应。（reize下，如果需要body内容自适应，则应该给定尺寸）
      },
      height: {
        type: String,//初始高
        default: null
      },
      resize: {
        // 右下角允许拖拽缩放 建议给定width height
        // 注意内容区的宽高应该自适应。
        type: Boolean,
        default: false,
      },
      resetPositionOnClose: {
        type: Boolean,//关闭时重置位置。
        default: false
      },
      onEmptyBody: {
        type: Function//body内容为空时调用
      },
      jsxSlot: {
        type: Function,//以jsx函数渲染slot
      },
      inAnimClass: {
        type: String,//进入动画
        default: "winInAnim"
      },
      outAnimClass: {
        type: String,//关闭动画
        default: "winOutAnim"
      },
    },
    data() {
      return {
        fullscreen: false,
        hasDragged: false,
        outerShow: false,
        innerShow: false,//第一次show之后永久显示。懒加载效果。
      }
    },
    created() {
      this._width = this.width;
      this._height = this.height;
      pushWin(this);
    },
    mounted() {
      window.addEventListener("resize", this.onWindowResize);
      if (this.show) {
        this.outerShow = true;
        this.innerShow = true;
        this.showModal();
        this.animByAnimClass(this.$refs.root, this.inAnimClass);
        this.$nextTick(() => {
          this.init();
          moveTop(this);
        });
      }
    },
    watch: {
      show(v) {
        //要显示。
        if (v) {
          this.showModal();
          this.outerShow = v;
          let moveTopAndAnim = (hasResetPos) => {
            !hasResetPos && this.resetPos();
            moveTop(this);
            this.animByAnimClass(this.$refs.root, this.inAnimClass);
          };
          //首次显示
          if (!this.innerShow || this.resetPositionOnClose) {
            this.innerShow = true;
            this.$nextTick(() => {
              this.init();
              moveTopAndAnim(true);
            });
          } else {
            if (this.$refs.root) {
              moveTopAndAnim();
            } else {
              this.$nextTick(moveTopAndAnim);
            }
          }
        } else {
          this.hideModal();
          //隐藏
          if (this.$refs.root && this.outAnimClass) {//如果已经显示
            this.animByAnimClass(this.$refs.root, this.outAnimClass, () => {
              this.outerShow = this.show;
              this.checkResetPostion();
              this.emitClosed();
            });
          } else {
            this.outerShow = v;
            this.checkResetPostion();
            this.emitClosed();
          }
        }
      },
      position(n, o) {
        if (typeof n === "object" && typeof o === "object" && n.left == o.left && n.top == o.top) {
          return;
        }
        //变更了位置，并更新实际位置
        if (this.$refs.root && this.$refs.root._width) {
          this.resetPos(true);
        }
      },
      //宽高
      width() {
        this.resetResizeSizeOnShow();
      },
      height() {
        this.resetResizeSizeOnShow();
      },
    },
    computed: {
      canResize() {
        return this.resize && this.showTitle;
      },
      frameStyle() {
        let style = {
          minWidth: this.minWidth + "px",
          minHeight: this.minHeight + "px",
        };
        if (this._width) {
          style.width = this._width;
        }
        if (this._height) {
          style.height = this._height;
        }
        return style;
      },
    },
    methods: {
      init() {
        //初始化
        let root = this.$refs.root;
        if (!root || root._hasInit) return;
        // 有时需要动态添加body
        if (this.onEmptyBody && !this.$refs.body.children.length) {
          this.onEmptyBody(this.$refs.body, this);
        }
        //记录初始宽高
        if (!root._initSize) {
          root._initSize = true;
          root._initWidth = root.offsetWidth;
          root._initHeight = root.offsetHeight;
        }
        //额外记录宽高 resize时也会更新该值。必要时用该属性计算宽高
        root._width = root._initWidth;
        root._height = root._initHeight;

        //resize模式下，宽高，需要是确定值。
        let width = this.width;
        let height = this.height;
        if (this.resize) {
          if (!width) width = root._width + "px";
          if (!height) height = root._height + "px";
          root.style.width = width;
          root.style.height = height;
        }
        //如果有了高度，则body计算body的高度(可能无标题)
        if (height && this.$refs.body) {
          if (this.$refs.title) {
            this.$refs.body.style.height = "calc(100% - " + this.$refs.title.offsetHeight + "px)";
          } else {
            this.$refs.body.style.height = "100%";
          }
        }
        root._hasInit = true;
        setWinStyleZIndex(this);
        this.resetPos();
        //如果有标题的话
        if (this.$refs.title)
          Drag.init(this.$refs.title, this.$refs.root, (handler, root, Drag) => {
              this.$refs.root.hasDragged = true;
              return {
                canceled: this.fullscreen || !this.draggable, // init: false,
                autoInWindow: true
              };
            },
          );
      },
      //显示一个遮罩层
      showModal() {
        if (!this.modal) return;
        let div = document.getElementById("winMask");
        if (!div) {
          div = document.createElement("div");
          div.id = "winMask";
          div.classList.add("win-modal");
          div.style.zIndex = '' + (this.zIndex - 1);
          document.body.appendChild(div);
          this.animByAnimClass(div, "win-mask-fade-in");
        } else {
          div.style.zIndex = '' + (this.zIndex - 1);
          div.style.display = "block";
        }
      },
      hideModal() {
        let div = document.getElementById("winMask");
        if (!div || div.style.display === "none") return;
        let modalShouldShow = false;
        let currModalWin = null;
        for (let i = AllWindows.length - 1; i >= 0; i--) {
          let win = AllWindows[i];
          if (win.modal && win.outerShow) {
            modalShouldShow = true;
            currModalWin = win;
            break;
          }
        }
        if (modalShouldShow) {
          div.style.zIndex = '' + (currModalWin.zIndex - 1);
        } else {
          this.animByAnimClass(div, "win-mask-fade-out", () => {
            div.style.display = "none";
            div.style.zIndex = "-1";
          });
        }
      },
      checkResetPostion() {
        if (!this.outerShow && this.resetPositionOnClose) {
          let root = this.$refs.root;
          if (root) {
            root._hasInit = false;
            root.hasDragged = false;
          }
        }
      },
      emitClosed() {
        if (!this.outerShow) {
          this.$emit("closed");
        }
      },
      //动画显示
      animByAnimClass(dom, animName, extraCb) {
        let cb = () => {
          dom._removeLastAnimListener && dom._removeLastAnimListener();
        };
        cb();
        dom.addEventListener("animationend", cb);
        dom.addEventListener("webkitAnimationEnd", cb);
        dom.addEventListener("mozAnimationEnd", cb);
        dom._removeLastAnimListener = function () {
          dom.removeEventListener("animationend", cb);
          dom.removeEventListener("webkitAnimationEnd", cb);
          dom.removeEventListener("mozAnimationEnd", cb);
          dom._removeLastAnimListener = null;
          dom.classList.remove(animName);
          extraCb && extraCb();
        };
        dom.classList.add(animName);
      },
      onMouseDownTitle() {
        if (!this.modal)
          moveTop(this);
      },
      //窗口尺寸变化时调用
      onWindowResize() {
        this.resetPos();
      },
      //重新设置dom的位置。被拖动后记录最后位置，不再受position和offset影响。可手动调用。
      resetPos(force) {
        if (!this.$refs.root || (!force && this.$refs.root.hasDragged)) return;
        let root = this.$refs.root;
        let positon = this.position;
        let offX = this.offsetX;
        let offY = this.offsetY;
        if (typeof positon === "string") {
          if (positon === "center" || positon === "middle") positon = "middle-center";
          if (positon === "top") positon = "top-center";
          if (positon === "bottom") positon = "bottom-center";
          if (positon === "left") positon = "top-left";
          if (positon === "right") positon = "top-right";

          let top = positon.indexOf("top") > -1;
          let middle = positon.indexOf("middle") > -1;
          let bottom = positon.indexOf("bottom") > -1;
          let left = positon.indexOf("left") > -1;
          let right = positon.indexOf("right") > -1;
          let center = positon === "center" || positon.indexOf("center") > -1;

          //top middle bottom  left right center
          if (top) root.style.top = (offY + 0) + 'px';
          if (middle) root.style.top = (offY + window.innerHeight - root._height) / 2 + 'px';
          if (bottom) root.style.top = (offY + window.innerHeight - root._height) + 'px';

          if (left) root.style.left = (offX + 0) + 'px';
          if (center) root.style.left = (offX + window.innerWidth - root._width) / 2 + 'px';
          if (right) root.style.left = (offX + window.innerWidth - root._width) + 'px';
        } else {
          root.style.left = (offX + positon.left) + 'px';
          root.style.top = (offY + positon.top) + 'px';
        }
      },
      onClickFullScreen() {
        this.fullscreen = !this.fullscreen;
        if (this.$util && this.$util.rootVue) {
          this.$util.rootVue.$emit('auto-dialog-fullscreen', this.fullscreen);
        }
      },
      //显示状态下重新设置窗口大小
      resetResizeSizeOnShow() {
        if (!this.show) return;
        let root = this.$refs.root;
        root._width = this.width;
        root._height = this.height;
        root.style.width = root._width;
        root.style.height = root._height;
      },
      //点击缩放layer的尺寸时
      onResizeMouseDown(e) {
        let o = this.$refs.resize;
        o.lastMouseX = e.clientX;
        o.lastMouseY = e.clientY;
        //记录初始位置
        o.mStart = {
          width: this.$refs.root.clientWidth,
          height: this.$refs.root.clientHeight,
          ex: e.clientX, ey: e.clientY
        };
        document.onmousemove = this.onDragResize;
        document.onmouseup = this.onDragEndResize;
      },
      onDragResize(e) {
        let o = this.$refs.resize;
        let clientX = Math.min(e.clientX, window.innerWidth);
        let clientY = Math.min(e.clientY, window.innerHeight);

        let dx = clientX - o.mStart.ex;
        let dy = clientY - o.mStart.ey;
        let width = o.mStart.width + dx;
        let height = o.mStart.height + dy;
        //约束最小尺寸
        if (width < this.minWidth) {
          width = this.minWidth;
        }
        if (height < this.minHeight) {
          height = this.minHeight;
        }
        let root = this.$refs.root;
        root.style.width = width + 'px';
        root.style.height = height + 'px';
        root._resized = true;
        root._width = root.style.width;
        root._height = root.style.height;
        e.stopPropagation();
        e.preventDefault();
      },
      onDragEndResize(e) {
        document.onmousemove = null;
        document.onmouseup = null;
      },
      //没有特殊操作直接，发送事件
      onClickClose() {
        this.$emit("update:show", false);
        this.$emit("close");
      },
    },
    beforeDestroy() {
      window.removeEventListener("resize", this.onWindowResize);
      removeWin(this);
      //如果所有窗口都没有遮罩，应该隐藏遮罩
      this.hideModal();
      if (this.$el && this.$el.parentNode) {
        this.$el.parentNode.removeChild(this.$el);
      }
    }
  }

</script>

<style scoped>
  .Win {
    position: fixed;
    left: 0;
    top: 0;
    border-width: 1px;
    border-style: solid;
    box-shadow: 1px 1px 50px rgba(0, 0, 0, .3);
    min-width: 120px;
    border-radius: 2px;
  }

  .Win.resize {
    overflow: hidden;
  }

  .Win.fullscreen {
    left: 0 !important;
    right: 0 !important;
    top: 0 !important;
    bottom: 0 !important;
    width: auto !important;
    height: auto !important;
  }

  .Win_title {
    cursor: move;
    padding: 0 0px 0 10px;
    height: 38px;
    line-height: 38px;
    font-size: 14px;
    overflow: hidden;
    border-radius: 2px 2px 0 0;
    border-bottom-width: 1px;
    border-bottom-style: solid;
  }

  .Win_body {
    min-height: 42px;
    position: relative;
    overflow: auto;
    box-sizing: border-box;
    padding: 10px;
  }

  .Win_resize {
    position: absolute;
    right: 0;
    bottom: 0;
    width: 15px;
    height: 15px;
    cursor: se-resize;
    z-index: 10;
  }

  .right-btns {
    float: right;
    cursor: auto;
    letter-spacing: 6px;
    padding-right: 6px;
  }

  .right-btn-item {
    border-radius: 50%;
    font-size: 12px;
    padding: 4px;
    transform: scale(0.8);
    width: 20px;
    position: relative;
    top: -2px;
  }

  .right-btn-item:before {
    position: relative;
    top: 1px;
  }

  .fullscreen-btn, .win__close {
    cursor: pointer;
  }

  .fullscreen-btn:hover, .win__close:hover {
    opacity: 1;
  }

</style>
<style>
  .winInAnim {
    animation: winIn 0.25s;
    animation-fill-mode: backwards;
  }

  .winOutAnim {
    animation: winOut 0.25s;
    animation-fill-mode: both;
  }

  .win-modal {
    position: fixed;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    z-index: 100;
    background: rgba(0, 0, 0, 0.5);
  }

  .win-mask-fade-in {
    animation: winMaskIn 0.5s;
    animation-fill-mode: both;
  }

  .win-mask-fade-out {
    animation: winMaskOut 0.5s;
    animation-fill-mode: both;
  }

  @keyframes winMaskIn {
    0% {
      opacity: 0.5;
    }
    100% {
      opacity: 1;
    }
  }

  @keyframes winMaskOut {
    0% {
      opacity: 1;
    }
    100% {
      opacity: 0;
    }
  }

  @keyframes winIn {
    0% {
      opacity: 0;
      transform: scale(0.75);
    }
    70% {
      transform: scale(1.1);
    }
    100% {
      opacity: 1;
      transform: scale(1);
    }
  }

  @keyframes winOut {
    0% {
      opacity: 1;
      transform: scale(1);
    }
    30% {
      transform: scale(1.1);
    }
    100% {
      opacity: 0;
      transform: scale(0.75);
    }
  }
</style>
