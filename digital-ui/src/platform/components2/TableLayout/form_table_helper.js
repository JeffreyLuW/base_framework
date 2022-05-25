import LayoutHelper from "./table_layout_helper.js";
import formhelper from '../../utils/formhelper.js';

//使用示例。 配合 FormTableLayout 组件
// let tableHelper = new FormTableHelper();
// tableHelper.createForm([
//   tableHelper.itemCreator.input("输入", "name", "", null, "请输入用户名"),
//   tableHelper.itemCreator.select("测试1", "op", "1", this.$rules.required(), "sys_oper_type"),
//   tableHelper.itemCreator.textarea("测试2", "op1", "1", this.$rules.required()),
//   tableHelper.itemCreator.select("测试3", "op2", "1", this.$rules.required(), "sys_oper_type"),
// ]);
// tableHelper.createLayout([
//   [tableHelper.span("总结", 4, 1), tableHelper.label("name",), tableHelper.item("name", 1, 2),],
//   [tableHelper.labelText("就是个测试", false, 2, 1), tableHelper.label("op",), tableHelper.item("op",),],
//   [tableHelper.label("op1",), tableHelper.item("op1",),],
//   [tableHelper.label("op2",), tableHelper.item("op2", 1, 2),],
// ]);
// form layout可以用于表格表单布局 FormTableLayout
// form:tableHelper.form,layout:tableHelper.layout
/**
 * 用于快速创建表格布局配置。
 *
 * data:{class:Object,style:Object,attrs:{},props:{},domProps:{},on:{},nativeOn:{},directives:[],scopedSlots,slot,key,ref}
 */
export default class FormTableHelper {

  constructor() {
    this.formhelper = formhelper;
    this.itemCreator = formhelper.itemCreator;

    this.form = null;
    this.layout = [];
  }

  //第一步
  createForm(defList, page = false) {
    this.form = formhelper.create(defList, page);
  }


  //第一步和第二步中间
  resetModel(entity) {
    let model = this.form.model;
    if (model && entity) {
      Object.keys(model).forEach((key) => {
        model[key] = entity[key];
      });
    }
  }

  //第二步
  createLayout(list) {
    this.layout = list || [];
  }

  label(key, row, col) {
    let label = this.form.items[key].label;
    let rules = this.form.rules[key];
    return this.labelText(label, rules && rules[0] && rules[0].required, row, col);
  }

  item(key, row, col) {
    let model = this.form.model;
    let oldItem = this.form.items[key];
    oldItem.label = null;
    return this.cell("DataFormItem", {
      valueKey: key,
      valueModel: model,
      props: oldItem, attrs: oldItem,
    }, row, col);
  }

  jsx(jsxFn, row, col) {
    return LayoutHelper.cell('Jsx', {props: {jsx: jsxFn}}, row, col);
  }

  slot(name, row = 1, col = 1, mClass, style) {
    return LayoutHelper.slot(name, row, col, mClass, style)
  }

  cell(tag, data, row, col, mClass, style) {
    return LayoutHelper.cell(tag, data, row, col, mClass, style);
  }

  span(innerText, row, col) {
    return LayoutHelper.span(innerText, row, col);
  }

  html(innerHTML, row, col) {
    return LayoutHelper.html(innerHTML, row, col);
  }

  labelText(text, required, row, col) {
    return LayoutHelper.label(text, required, row, col);
  }

};
