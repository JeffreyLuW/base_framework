<template>
  <el-button
    :size="size"
    :type="type"
    :plain="plain"
    :round="round"
    :circle="circle"
    :loading="loading"
    :disabled="disabled"
    :autofocus="autofocus"
    :native-type="nativeType"
    :icon="icon"
    @click="onClick">{{text}}
    <slot></slot>
  </el-button>
</template>

<script>
  export default {
    name: "DataButton",
    props: {
      size: {
        type: String,
        default: 'mini'
      },
      type: {},
      icon: {},
      plain: {},
      round: {},
      circle: {},
      loading: {},
      disabled: {},
      autofocus: {},
      nativeType: {},
      text: {},
    },
    methods: {
      onClick(e) {
        this.$emit('click', e);
      }
    },
  }
</script>

<style scoped>

</style>
