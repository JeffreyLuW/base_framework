<template>
  <div class="HScroll" :style="scrollStyle">
    <div ref="content" class="HScroll_content" :style="contentStyle">
      <!--只能放置一个div元素。操作该元素的偏移量-->
      <div class="HScroll_content_inner" :style="innerStyle">
        <slot></slot>
      </div>
    </div>
    <template v-if="showArrow">
      <span v-show="currPage>1" class="HScroll_left" @click="changePage(-1)">《</span>
      <span v-show="currPage<totalPage" class="HScroll_right" @click="changePage(1)">》</span>
    </template>
  </div>
</template>

<script>

  //为多个内容元素提供一个略微友好的滚动。
  export default {
    name: "HScroll",
    props: {
      size: {
        default: 10 // cell的个数
      },
      step: {
        default: 100 //内部每个cell的大小
      },
      height: {
        default: '100px'
      },
      width: {
        default: "500px"
      },
      arrowOver: {
        type: Boolean,//箭头在内容区域上面。false时在两侧边缘。
        default: true,
      },
      dir: {
        default: "right"  //left right
      },
    },
    data() {
      return {
        showArrow: false,
        currPage: 1,
        pageSize: 0,
        totalPage: 0,
      }
    },
    created() {
      window.addEventListener("resize", this.computePage)
    },
    mounted() {
      this.computePage();
    },
    computed: {
      scrollStyle() {
        return {
          width: this.width,
          height: this.height
        }
      },
      innerStyle() {
        if (this.dir === "right" && this.totalPage <= 1 && this.$el) {
          return {
            width: this.size * this.step + 'px',
            left: 'auto',
            right: '0'
          };
        }
        return {
          width: this.size * this.step + 'px',
          left: (1 - this.currPage) * this.step * this.pageSize + "px"
        };
      },
      contentStyle() {
        if (this.arrowOver) {
          return {left: 0, right: 0}
        }
        return {};
      },
    },
    methods: {
      changePage(dp) {
        let newPage = this.currPage + dp;
        if (newPage < 1) {
          newPage = 1;
        }
        if (newPage > this.totalPage) {
          newPage = this.totalPage;
        }
        if (newPage === this.currPage) {
          return;
        }
        this.currPage = newPage;
      },
      computePage() {
        if (!this.$refs.content) return;
        this.currPage = 1;
        if (this.size <= 0) {
          this.showArrow = false;
          this.pageSize = 1;
          this.totalPage = 1;
          return;
        }
        let width = this.$refs.content.offsetWidth;
        this.showArrow = width < this.size * this.step;
        this.pageSize = Math.floor(width / this.step);
        let totalPage = this.size / this.pageSize;
        if (totalPage > Math.floor(totalPage)) {
          totalPage = Math.ceil(totalPage);
        }
        this.totalPage = totalPage;
      },
    },
    beforeDestroy() {
      window.removeEventListener("resize", this.computePage)
    }
  }
</script>

<style scoped>
  .HScroll {
    position: relative;
    overflow: hidden;
  }

  .HScroll_content {
    position: absolute;
    left: 20px;
    top: 0;
    right: 20px;
    bottom: 0;
    height: 100%;
    overflow: hidden;
  }

  .HScroll_content_inner {
    position: absolute;
    left: 0;
    top: 0;
    width: auto;
    height: 100%;
    overflow: hidden;
    transition: left ease 0.3s;
  }

  .HScroll_left, .HScroll_right {
    position: absolute;
    left: 0;
    top: 0;
    width: 20px;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    opacity: 0.75;
    transition: opacity ease 0.3s;
    color: white;
    background: rgba(0, 0, 0, 0.2);
    user-select: none;
  }

  .HScroll_left:hover, .HScroll_right:hover {
    opacity: 1;
  }

  .HScroll_right {
    left: auto;
    right: 0;
  }
</style>
