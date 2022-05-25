<template>
  <div class="Babylon3d">
    <canvas ref="canvas" id="yingzizhaCanvans" touch-action="none"></canvas>

    <div v-if="rater.show" class="right-top-op">
      {{ rater.scene }}fps,{{ rater.theory }}fps
    </div>
  </div>
</template>

<script>
import BabylonHelper from "./BabylonHelper.js";

export default {
  name: "Babylon3d",
  data() {
    this.helper = BabylonHelper;
    return {
      rater: {
        show: false,
        scene: 0,
        theory: 0,
      },
    };
  },
  mounted() {
    this.loadScript();
  },
  methods: {
    loadScript() {
      let baseUrl = process.env.BASE_URL;
      this.$util.loadScripts([`${baseUrl}3d/babylonjs/babylon.js`], () => {
        this.$util.loadScripts(
          [
            `${baseUrl}3d/babylonjs/babylon.gui.min.js`,
            `${baseUrl}3d/babylonjs/babylonjs.loaders.min.js`,
            `${baseUrl}3d/babylonjs/pep.js`,
            `${baseUrl}3d/babylonjs/babylon.waterMaterial.js`,
            `${baseUrl}3d/babylonjs/babylonjs.materials.min.js`,
          ],
          () => {
            this.init();
          }
        );
      });
    },
    init() {
      this.canvas = this.$refs.canvas;
      this.engine = new BABYLON.Engine(this.canvas, true, {
        doNotHandleContextLost: true,
      });
      this.engine.runRenderLoop(this.renderLoop);
      // Watch for browser/canvas resize events
      let resizeFn = () => {
        this.engine.resize();
      };
      let keydownFn = (e) => {
        this.$emit("keydown", e);
      };
      window.addEventListener("resize", resizeFn);
      document.addEventListener("keydown", keydownFn);
      this.$on("hook:beforeDestroy", () => {
        window.removeEventListener("resize", resizeFn);
        document.removeEventListener("keydown", keydownFn);
        if (this.engine) {
          this.engine.stopRenderLoop(this.renderLoop);
          this.engine.dispose();
        }
        if (this.scene) {
          this.scene.dispose();
        }
      });

      this.$emit("init", this);
    },
    //设置scene
    setScene(scene) {
      this.scene = scene;
    },
    //创建一个帧率显示
    createRater(scene) {
      this.rater.show = true;
      this.helper.createRater(scene, (sceneRate, theoryRate) => {
        this.rater.scene = sceneRate;
        this.rater.theory = theoryRate;
      });
    },
    renderLoop() {
      //当前场景渲染
      if (this.scene) this.scene.render();
    },
  },
  beforeDestroy() {},
};
</script>

<style scoped>
.Babylon3d {
  min-width: 100px;
  min-height: 100px;
  position: relative;
  outline: none;
}

canvas {
  width: 100%;
  height: 100%;
  touch-action: none;
  outline: none;
}

.right-top-op {
  position: absolute;
  right: 0;
  top: 0;
  background: rgba(0, 0, 0, 0.5);
  color: white;
}
</style>
