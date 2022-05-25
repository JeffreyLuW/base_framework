<template>
  <div class="DataFooter" :class="{fixed}">
    <DataButton class="footer-btn" v-for="item in innerButtons" :key="item.key" :type="item.type"
                @click="click(item.key,item)"
                :text="item.text" :loading="okBtnLoading&&item.key==='ok'"></DataButton>
  </div>
</template>

<script>
  const defaultBtns = [
    {type: 'default', text: '取消', key: 'cancel'},
    {type: 'primary', text: '确定', key: 'ok'},
  ];
  const okBtns = [
    {type: 'primary', text: '确定', key: 'ok'},
  ];
  export default {
    name: "DataFooter",
    props: {
      buttons: {
        default: () => defaultBtns,//可以定制footer的按钮
      },
      fixed: {}
    },
    data() {
      return {
        okBtnLoading: false
      }
    },
    computed: {
      innerButtons() {
        let buttons = this.buttons;
        if (buttons === "confirm") {
          buttons = defaultBtns;
        } else if (buttons === "ok") {
          buttons = okBtns;
        }
        return buttons;
      }
    },
    methods: {
      click(key, item) {
        this.$emit('click', key, item);
      }
    },
  }
</script>

<style scoped>
  .DataFooter {
    text-align: right;
    padding: 10px 20px 16px 20px;
  }

  .DataFooter.fixed {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
  }

  .footer-btn {
    min-width: 80px;
  }
</style>
