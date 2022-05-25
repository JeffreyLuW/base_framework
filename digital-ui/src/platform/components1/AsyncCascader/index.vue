<template>
  <div>
    <el-cascader
      :props="props"
      v-model="myValue"
      :placeholder="regionName ? regionName : '请选择'"
      :clearable="true"
    ></el-cascader>
  </div>
</template>
<script>
import {
  getTreeSelect
} from "@/platform/api/common/AsyncCascader";
let id = 0;
export default {
  props: {
    value: {
      type: String,
      default: "",
    },
    regionName: {
      type: String,
      default: "",
    },
    emitPath: {
      type: Boolean,
      default: false,
    },
    level: {
      type: Number,
      default: 5,
    },
    checkStrictly: {
      type: Boolean,
      default: true,
    },
  },
  watch: {
    myValue(old) {
      this.$emit("changeValue", old);
    },
    value(old) {
      this.myValue = old;
    },
  },
  data() {
    return {
      myValue: "",
      Province: [],
      props: {
        emitPath: this.emitPath,
        checkStrictly: this.checkStrictly,
        expandTrigger: "click",
        lazy: true,
        lazyLoad: (node, resolve) => {
          const { level } = node;
          if (level == 0) {
            this.getTreeSelect().then((res) => {
              if (res.code !== 200) {
                return this.$message.error(res.msg);
              }
              const a = [];
              a.push({
                label: res.data[0].name,
                leaf: this.level == 1 ? true : false,
                value: res.data[0].id,
                ch: res.data[0].children,
              });
              resolve(a);
            });
          }

          if (level == 1) {
            const b = [];
            node.data.ch.forEach((item) => {
              b.push({
                label: item.name,
                leaf: this.level == 2 ? true : false,
                value: item.id,
                ch: item.children,
              });
            });
            resolve(b);
          }

          if (level == 2) {
            getTreeSelect({
              parentCode: node.data.value,
              level: level
            }).then((res) => {
              if (res.code !== 200) {
                return this.$message.error(res.msg);
              }
              const b = [];
              res.data[0].children.forEach((item) => {
                b.push({
                  label: item.name,
                  leaf: this.level == 3 ? true : false,
                  value: item.id,
                  });
              });
              resolve(b);
            });
          }
          if (level == 3) {
            getTreeSelect({
              parentCode: node.data.value,
              level: level
            }).then((res) => {
              if (res.code !== 200) {
                return this.$message.error(res.msg);
              }
              const b = [];
              res.data[0].children.forEach((item) => {
                b.push({
                  label: item.name,
                  leaf: this.level == 4 ? true : false,
                  value: item.id,
                });
              });
              resolve(b);
            });
          }
          if (level == 4) {
            getTreeSelect({
              parentCode: node.data.value,
              level: level
            }).then((res) => {
              if (res.code !== 200) {
                return this.$message.error(res.msg);
              }
              const b = [];
              res.data[0].children.forEach((item) => {
                b.push({
                  label: item.name,
                  leaf: this.level == 5 ? true : false,
                  value: item.id,
                });
              });

              resolve(b);
            });
          }
          if (level == 5) {
            resolve({
              leaf: true,
            });
          }
        },
      },
    };
  },
  methods: {
    getTreeSelect() {
      return getTreeSelect();
    },
  },
};
</script>
