<template>
  <div id="app">
    <router-view />

    <audio
      class="audio"
      id="myaudio"
      ref="myaudio"
      src="../platform/assets/audio/warning.mp3"
      controls
      hidden="false"
      muted="true"
    ></audio>

    <AutoDialog
      title="预警信息"
      :show.sync="open"
      width="400px"
      customClass="warning-dialog"
    >
      <div class="warning-info">
        <div class="warning-info-item">
          <div class="title">站点名称：</div>
          <div class="content">{{ warningData.stationName }}</div>
        </div>
        <div class="warning-info-item">
          <div class="title">站点类型：</div>
          <div class="content">{{ warningData.stationType }}</div>
        </div>
        <div class="warning-info-item">
          <div class="title">报警类型</div>
          <div class="content">{{ warningData.alarmType }}</div>
        </div>
        <div class="warning-info-item">
          <div class="title">报警等级</div>
          <div class="content">{{ warningData.alarmGrade }}</div>
        </div>
        <div class="warning-info-item">
          <div class="title">指标阈值：</div>
          <div class="content">
            {{
              warningData.alarmMin +
                warningData.unit +
                "-" +
                warningData.alarmMax +
                warningData.unit
            }}
          </div>
        </div>
        <div class="warning-info-item">
          <div class="title">当前{{ warningData.alarmType }}</div>
          <div class="content">
            {{ warningData.alarmName + warningData.unit }}
          </div>
        </div>
        <div class="warning-info-item">
          <div class="title">报警时间：</div>
          <div class="content">{{ warningData.meatime }}</div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">关 闭</el-button>
      </div>
    </AutoDialog>
  </div>
</template>

<script>
export default {
  name: "App",
  data() {
    return {
      ws: null,
      open: false,
      warningData: {}
    };
  },

  methods: {
    initWaingConnect() {
      setTimeout(() => {
        if (sessionStorage.getItem("userId")) {
          if (!this.ws) {
            this.ws = new WebSocket(
              process.env.VUE_APP_BASE_SOKET_API +
                "/alarmWebSocket" +
                `/${sessionStorage.getItem("userId")}`
            );
            this.ws.onmessage = evt => {
              let data = JSON.parse(evt.data);

              if (data.type == "data") {
                let data2 = JSON.parse(data.data);
                this.warningData = data2;

                let audioPlay = document.getElementById("myaudio");
                audioPlay.play();

                //显示报警提示框
                this.$notify({
                  title: "报警信息",
                  message: `${data2.stationName}:${data2.alarmType}预警！`,
                  type: "warning",
                  duration: 0,
                  onClick: () => {
                    this.open = true;
                  }
                });
              }
            };
          }
        }
      }, 2000);
    },

    cancel() {
      this.open = false;
    }
  },

  beforeDestroy() {},

  mounted() {
    //连接报警scoket
    this.initWaingConnect();

    // 此操作是为了任意点击解锁静音，实现谷歌自动播放
    document.body.addEventListener(
      "mousedown",
      () => {
        this.$refs.myaudio.muted = false;
        document.body.removeEventListener("mousedown", () => {});
      },
      false
    );
  }
};
</script>

<style lang="scss">
.icon {
  width: 1em;
  height: 1em;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}
.Cmd3dCore-tag {
  position: absolute;
  background-color: red;
}
.el-notification {
  z-index: 3000 !important;
}
</style>
