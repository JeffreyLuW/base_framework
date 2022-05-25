/**
 * 用于快速创建表格布局配置。
 * data:{class:Object,style:Object,attrs:{},props:{},domProps:{},on:{},nativeOn:{},directives:[],scopedSlots,slot,key,ref}
 */
export default {
  //复杂布局，可以使用html、slot、Jsx
  cell(tag, data = {}, row = 1, col = 1, mClass = 'table_layout_cell', style = undefined, isVue = true) {
    return {row, col, tag, style, data, 'class': mClass, isVue}
  },
  slot(name, row = 1, col = 1, mClass = 'table_layout_slot', style = undefined) {
    return {slot: name, row, col, style, 'class': mClass,}
  },
  span(innerText, row, col) {
    return this.cell('span', {domProps: {innerText}}, row, col, "table_layout_span", undefined, false);
  },
  html(innerHTML, row, col) {
    return this.cell('div', {domProps: {innerHTML}}, row, col, "table_layout_html", undefined, false);
  },
  label(text, required, row, col) {
    return this.cell("FormItemLabel", {props: {text, required,}}, row, col, "table_layout_label")
  }
};
