<template>
  <div style="padding:0 5px 0 0;">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5" v-for="item in btnList" :key="item.name">
        <template v-if="item.slotName">
          <slot :name="item.slotName"></slot>
        </template>
        <DataButton v-else :type="item.type" :icon="item.icon" size="mini" :text="item.text"
                    @click="emitTypeEvent(item.name,$event)"/>
      </el-col>
      <slot></slot>
      <div class="top-right-btn" v-if="showRight">
        <template v-if="hasSearch">
          <DataButton v-if="beExpanded&&showSearch&&multiLineSearch" size="mini"
                      :icon="singleLineSearch?'el-icon-arrow-down':'el-icon-arrow-up'"
                      :text="singleLineSearch?'展开搜索':'折叠搜索'"
                      @click="toggleSingleLineSearch()"/>
          <DataButton size="mini" icon="el-icon-view" :text="showSearch?'隐藏搜索':'显示搜索'" @click="toggleSearch()"/>
        </template>
        <DataButton size="mini" icon="el-icon-refresh" text="刷新" @click="emitTypeEvent('refresh')"/>
      </div>
    </el-row>
  </div>
</template>

<script>
  //用于查询列表页面 表单下面的操作按钮 可以监听各种
  export default {
    name: "FormToolsRow",
    props: {
      buttons: {
        type: String | Array,// add,update|edit,delete|remove,export,import  其他type当做动态slot加入。
      },
      buttonsShow: {
        type: Object,//通过指定上面的button-type为false，来删除对应的按钮。比如传入权限配置{add:hasPermi(['system:role:export']),}
      },
      showRight: {
        type: Boolean,
        default: true
      },
      autoChangeFormStatus: {
        type: Boolean,//自动调整form状态
        default: true
      },
      beExpanded: {
        type: Boolean,//是否可以展开折叠
        default: true
      }
    },
    data() {
      return {
        multiLineSearch: false,
        showSearch: true,
        singleLineSearch: this.beExpanded,
        hasSearch: false,
      };
    },
    computed: {
      btnList() {
        if (!this.buttons || !this.buttons.length) {
          return [];
        }
        let buttonsShow = this.buttonsShow || {};
        let list = [];
        let buttons = this.buttons || [];
        if (typeof buttons === 'string') {
          buttons = buttons.split(',');
        }
        buttons.forEach((type) => {
          //不显示的隐藏 需要明确为false方可。
          if (buttonsShow[type] === false) return;
          let size = list.length;
          if (type === 'add') {
            list.push({type: "primary", name: type, icon: "el-icon-plus", text: "新增"});
          }
          if (type === 'update' || type === 'edit') {
            list.push({type: "success", name: type, icon: "el-icon-edit", text: "修改"});
          }
          if (type === 'delete' || type === 'remove') {
            list.push({type: "danger", name: type, icon: "el-icon-delete", text: "删除"});
          }
          if (type === 'export') {
            list.push({type: "warning", name: type, icon: "el-icon-download", text: "导出"});
          }
          if (type === 'import') {
            list.push({type: "info", name: type, icon: "el-icon-upload2", text: "导入"});
          }
          //未添加 创建slot
          if (list.length === size) {
            list.push({slotName: type});
          }
        });
        return list;
      },
    },
    created() {
      window.addEventListener("resize", this.computeSearchFormMultiLine);
      if (this.$util && this.$util.rootVue) {
        this.$util.rootVue.$on('auto-dialog-fullscreen', this.nextComputeSearchFormMultiLine);
      }
    },
    mounted() {
      this.computeSearchFormMultiLine();
    },
    methods: {
      nextComputeSearchFormMultiLine() {
        this.$nextTick(this.computeSearchFormMultiLine);
      },
      computeSearchFormMultiLine() {
        if (!this.showRight) return;
        if (this.$parent && this.$parent.$el) {
          let form = this.$parent.$el.querySelector(".el-form");
          this.hasSearch = !!form;
          if (form) {
            this.updateFormClass(form, "query-form", true);
            this.multiLineSearch = form.scrollHeight > 56;
            form.style.height = form.scrollHeight + 'px';
            this.updateFormClass(form, "singleLineSearch", this.singleLineSearch);
            this.updateFormClass(form, "hide", !this.showSearch);
          }
        }
      },
      //修改form的class，添加或者删除。
      updateFormClass(form, className, isAdd) {
        if (!form) {
          form = this.$parent && this.$parent.$el && this.$parent.$el.querySelector(".el-form");
        }
        if (form) {
          if (isAdd && !form.classList.contains(className)) {
            form.classList.add(className);
          }
          if (!isAdd && form.classList.contains(className)) {
            form.classList.remove(className);
          }
        }
      },
      emitTypeEvent(typeName, e) {
        this.$emit('click', typeName, e);
        if (typeName === "refresh") {
          this.$emit('refresh');
        }
      },
      //单行搜索
      toggleSingleLineSearch() {
        let nextVal = !this.singleLineSearch;
        if (this.autoChangeFormStatus) {
          this.singleLineSearch = nextVal;
          this.updateFormClass(null, "singleLineSearch", nextVal);
        } else {
          this.$emit('update:singleLineSearch', nextVal);
          this.$emit('click', 'singleLineSearch', nextVal);
        }
      },
      toggleSearch() {
        let nextVal = !this.showSearch;
        if (this.autoChangeFormStatus) {
          this.showSearch = nextVal;
          this.updateFormClass(null, "hide", !nextVal);
        } else {
          this.$emit('update:showSearch', nextVal);
          this.$emit('click', 'searchChange', nextVal);
        }
      },
    },
    beforeDestroy() {
      window.removeEventListener("resize", this.computeSearchFormMultiLine);
      if (this.$util && this.$util.rootVue) {
        this.$util.rootVue.$off('auto-dialog-fullscreen', this.nextComputeSearchFormMultiLine);
      }
    }
  }
</script>

<style scoped>

</style>
