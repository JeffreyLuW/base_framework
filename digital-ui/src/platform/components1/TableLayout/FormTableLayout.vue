<template>
  <el-form class="FormTableLayout" :class="{stretchItem}" ref="editForm" :model="form.model" :rules="form.rules">
    <TableLayout :col-width="colWidth" :table-layout="tableLayout" style="width: 100%;">
      <template v-for="s in slots" :slot="s">
        <slot :name="s"></slot>
      </template>
    </TableLayout>
  </el-form>
</template>

<script>
  //包裹了el-form的table布局
  export default {
    name: "FormTableLayout",
    props: {
      form: {},
      colWidth: {
        type: String,//"100,200,100,200"
      },
      stretchItem: {
        type: Boolean,//表单元素 宽度自动填充满cell
        default: true
      },
      tableLayout: {
        type: Array, // [[]] 二维数组 form_table_helper.js 快捷创建
      },
    },
    computed: {
      slots() {
        let slots = [];
        if (this.tableLayout) {
          this.tableLayout.forEach((arr) => {
            arr.forEach((item) => {
              if (item.slot) {
                slots.push(item.slot);
              }
            });
          });
        }
        return slots;
      }
    },
    methods: {
      validate(validator) {
        if (!this.$refs.editForm) {
          return;
        }
        this.$refs.editForm.validate((ok, error) => {
          if (!ok) return;
          validator({...this.form.model}, this);
        });
      },
    },
  }
</script>
<style lang="scss">

  .el-dialog .FormTableLayout {
    margin-bottom: 10px;
  }

  .FormTableLayout.stretchItem {
    .el-select, .el-cascader {
      display: block;
    }
  }

</style>
