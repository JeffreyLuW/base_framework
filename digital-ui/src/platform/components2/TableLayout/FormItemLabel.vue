<template>
  <div class="FormItemLabel" :class="align">
    <span class="FormItemLabel_required" v-if="innerRequired">*</span>
    {{text}}
  </div>
</template>

<script>
  export default {
    name: "FormItemLabel",
    props: {
      rules: {},//表单验证规则 自动判断是否有required属性
      required: {
        default: false,//手动指定required
      },
      align: {
        type: String,//left right,center
        default: 'right'
      },
      text: {},
    },
    computed: {
      innerRequired() {
        if (this.rules && this.rules instanceof Array) {
          return this.rules[0].required;
        }
        return this.required;
      }
    },
  }
</script>

<style scoped>
  .FormItemLabel_required {
    color: #f56c6c;
    position: relative;
    top: 2px;
  }

  .FormItemLabel.left {
    text-align: left;
  }

  .FormItemLabel.right {
    text-align: right;
  }

  .FormItemLabel.center {
    text-align: center;
  }

</style>
