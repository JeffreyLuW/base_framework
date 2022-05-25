<template>
  <div class="AutoMatchDiv">
    <slot></slot>
  </div>
</template>

<script>
  //内部的div都以绝对布局显示。垂直填充自适应的一种尝试。
  //可以指定计算某些div，然后让某些div: .AutoMatchDiv__fill 填充剩余尺寸。
  export default {
    name: "AutoMatchDiv",
    created() {
      window.addEventListener("resize", this.resetSize);
    },
    mounted() {
      this.resetSize();
    },
    methods: {
      //compute size
      resetSize() {
        let el = this.$el;
        if (!el || !el.children.length) return;
        let sumHeight = el.offsetHeight;
        let fillDivs = [];
        let elseHeight = 0;
        let lastDiv = null;
        for (let i = 0; i < el.children.length; i++) {
          let c = el.children[i];
          if (c.classList.contains("AutoMatchDiv__ignore")) {
            continue;
          } else if (c.classList.contains("AutoMatchDiv__fill")) {
            fillDivs.push(c);
          } else {
            elseHeight += c.offsetHeight;
          }
          lastDiv = c;
        }
        if (!fillDivs.length) {
          lastDiv.classList.add("AutoMatchDiv__fill");
          fillDivs.push(lastDiv);
          elseHeight -= lastDiv.offsetHeight;
        }
        let fillDivHeight = (sumHeight - elseHeight) / fillDivs.length;
        fillDivs.forEach((c) => {
          // c.style.height = `calc(${100 / fillDivs.length}% - ${elseHeight / fillDivs.length}px)`;
          c.style.height = fillDivHeight + 'px';
        });
        let lastHeight = 0;
        for (let i = 0; i < el.children.length; i++) {
          let c = el.children[i];
          if (c.classList.contains("AutoMatchDiv__ignore")) {
            continue;
          }
          c.style.top = lastHeight + "px";
          lastHeight += c.offsetHeight;
        }
      }
    },
    beforeDestroy() {
      window.removeEventListener("resize", this.resetSize);
    }
  }
</script>

<style lang="scss">
  .AutoMatchDiv {
    position: relative;

    & > :not(.AutoMatchDiv__ignore) {
      position: absolute !important;
      left: 0;
      width: 100%;
    }

  }
</style>
