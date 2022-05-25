<template>
  <table class="layout-table theme-table " :class="{layoutFixed:layoutFixed,'theme-table-hover':rowHover}">
    <colgroup v-if="cols&&cols.length">
      <col v-for="w,i in cols" :key="i" :width="w"></col>
    </colgroup>
    <tr v-for="rows,index in tableLayout" :key="index">
      <td v-for="cell in rows" :key="cell.index" :rowspan="cell.row" :colspan="cell.col" :class="cell.class"
          :style="cell.style">
        <template v-if="cell.slot">
          <slot :name="cell.slot"></slot>
        </template>
        <Jsx v-else-if="!cell.isVue" :c-tag="cell.tag" :c-data="cell.data"
             :c-children="cell.children"></Jsx>
        <!--如果有 data:{valueKey valueModel} 则手动设置v-model -->
        <component v-else-if="cell.data.valueKey" :is="cell.tag" v-bind="cell.data.props" v-on="cell.data.on"
                   :class="cell.data.class" :style="cell.data.style"
                   v-model="cell.data.valueModel[cell.data.valueKey]"
        ></component>
        <component v-else :is="cell.tag" v-bind="cell.data.props" v-on="cell.data.on"
                   :class="cell.data.class" :style="cell.data.style"
        ></component>
      </td>
    </tr>
  </table>
</template>

<script lang="jsx">
  // {row,col,tag:"div",style,class,data:dataObject,}
  // 数据对象dataObject：{class:Object,style:Object,attrs:{},props:{},domProps:{},on:{},nativeOn:{},directives:[],scopedSlots,slot,key,ref}

  export default {
    name: "TableLayout",
    components: {},
    props: {
      colWidth: {
        type: String,//"100,200,100,200"
      },
      layoutFixed: {
        type: Boolean,//设置 table-layout: fixed;
        default: true
      },
      tableLayout: {
        type: Array, //[[]] 二维数组 table_layout_helper.js ，  LayoutHelper.cell(...)
      },
      rowHover: {
        type: Boolean,//行 hover的时候变色
        default: true
      },
    },
    computed: {
      cols() {
        if (this.colWidth) {
          return this.colWidth.split(',');
        }
        return null;
      },
    },
    methods: {}
  }
</script>

<style scoped lang="scss">
  .layout-table {
    margin: auto;
    border-collapse: collapse;
    border-style: solid;
    border-width: 1px;
    font-size: 13px;

    td {
      padding: 8px;
    }

    &.layoutFixed {
      table-layout: fixed;
    }
  }
</style>
<style lang="scss">
  .layout-table {
    .el-form-item--small.el-form-item {
      margin-bottom: 0px !important;
    }
  }
</style>
