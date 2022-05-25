### 组件说明：

组件基于Element-ui和若依，故首选传送门: 

1  https://element.eleme.cn/#/zh-CN/component/installation

2  https://doc.ruoyi.vip/ruoyi-vue/.

3  添加了一些常用组件，参考 components/global/index.js

#### 一些DataXXX开头的组件

1. 简单封装的组件，无难点，直接查看源文件即可。

比如下拉、单|多选都内建支持字典(注意value值类型必须匹配,数字1和字符串1并不相同)

DataFormItem减少el-form-item多层嵌套、更加容易的被js定义封装

上传组件和后端无缝集成，
 
JSX 允许模板和jsx语法并存，极大扩展了js封装组件的动态能力 

RawEcharts 减少echart直接引入和耗费额外编译时间，并和官网API保持高度一致。

2. 部分组件列举:
 
 DataButton  DataCheckbox  DataEditForm DataFormItem  DataRadio DataSelect DataUpload  Jsx

#### 对话框和窗口:

简介: 支持拖动、限制最大高度、支持js快速调用、支持表单快速回调

1. 支持模板调用，属性参考 AutoDialog Win 组件。

2. 支持js调用(推荐) 

   ```javascript
    this.$dialog.show({
      bind:this,
      title: "测试动态表单",
      width: "400px",
      component: "DataEditForm",
      propsData: {
        edit: formhelper.create([
          formhelper.itemCreator.input('角色编号', 'roleId', null, this.$rules.required()),
          formhelper.itemCreator.textarea('角色名称', 'roleName', null, this.$rules.required()),
          formhelper.itemCreator.upload('上传附件', 'roleFiles', null, this.$rules.required(), null, {type_limit: 4}),
          formhelper.itemCreator.select('操作', 'op', '1', null, "sys_oper_type")
        ], false),
      },
      onAutoOk: (rs, dialog, next) => {
        console.log(rs, dialog, next);
      }
    })
   ```

```javascript

    this.$win.show({
      bind: this,
      title: "测试动态表单",
      width: "548px",
      height: '478px',
      resize: true,
      modal: false,
      component: "DataEditForm",
      buttons: [{type: 'primary', text: '就这样~喵', key: 'ok'}],
      propsData: {
        edit: formhelper.create([
          formhelper.itemCreator.input('角色编号', 'roleId', null, this.$rules.required()),
          formhelper.itemCreator.textarea('角色名称', 'roleName', null, this.$rules.required()),
          formhelper.itemCreator.upload('上传附件', 'roleFiles', null, this.$rules.required(), null, {type_limit: 4}),
          formhelper.itemCreator.select('操作', 'op', '1', null, "sys_oper_type")
        ], false),
      },
      onAutoOk: (rs, dialog, next) => {
        console.log(rs, dialog, next);
      }
    });
```
   

3. 常用属性说明:
```javascript
// title:string 标题
// sizeMode:string sizeMode:'auto|fullscreen|400px' 高度模式，auto 自适应高度  fullscreen只是根据屏幕高度限制最大高度
// top|width 调整顶部位置、 宽度
// fullscreen:boolean 为true|false时会显示全屏按钮，否则不显示
// onCloseFn(btnKey,dialog,next) 对话框关闭时的回调。
// 其他尝试用属性参考文件 AutoDialog/index.vue
// ------ js调用时的额外属性
// component: VueComponent|String 弹窗的内部组件，为字符串时需要是vue注册的全局组件
// propsData: Object  component的属性配置
// onInit(componentInstance) 内部component初始化时调用，
// bind:VueComponent 声明周期绑定到某个vue示例上，可以确保页面销毁时对应的对话框也正常关闭。
// onAutoOk: (rs, dialog, next) 自动关闭对话框，且在点击ok按钮时给予回调。如果内部组件有validate()函数，也会执行。可以减少表单对话框样板代码
```

#### 快速表单:

简介: 快速创建表单、减少重复代码、足够的布局灵活性
1. 引入 utils/formhelper.js 

2. 创建data:
```javascript
{
    edit: formhelper.create([
          formhelper.itemCreator.input('角色编号', 'roleId', null, this.$rules.required()),
          formhelper.itemCreator.textarea('角色名称', 'roleName', null, this.$rules.required()),
          formhelper.itemCreator.upload('上传附件', 'roleFiles', null, this.$rules.required(), null, {type_limit: 4}),
          formhelper.itemCreator.select('操作', 'op', '1', null, "sys_oper_type")
        ], false)
}
```

3. 模板代码：
```html
  <el-form ref="editForm" :model="edit.form" :rules="edit.rules"
           :inline="false" label-width="80px">
    <DataFormItem
      v-for="(value,key) in edit.items"
      :key="key"
      v-bind="value"
      v-model="edit.form[key]"/>
  </el-form>
```

4. 扩展。根据需要，随时扩展formhelper.js  参考js文件里的 itemCreator 即可。


#### 表格布局 TableLayout FormTableLayout.vue:

简介: 快速快速创建一般复杂度的表格展示、表格表单。
1. 引入helper工具 TableLayout/table_layout_helper.js  TableLayout/form_table_helper.js

2. TableLayout:
```html
<TableLayout col-width="80," :table-layout="layout" style="width: 600px;"/>
```
```javascript
{
layout: [
         [TableHelper.span("开始1:"), TableHelper.span("还好"), TableHelper.html("test")],
         [TableHelper.span("开始2:"), TableHelper.span("还好", 1, 2),],
         [TableHelper.span("开始3:"), TableHelper.span("还好", 1, 2)],
         [TableHelper.span("开始4:", 1, 3)],
       ]
}
```

3. FormTableLayout：
```javascript
 let tableHelper = new FormTableHelper();
 tableHelper.createForm([
   tableHelper.itemCreator.input("输入", "name", "", null, "请输入用户名"),
   tableHelper.itemCreator.select("测试1", "op", "1", this.$rules.required(), "sys_oper_type"),
   tableHelper.itemCreator.textarea("测试2", "op1", "1", this.$rules.required()),
   tableHelper.itemCreator.select("测试3", "op2", "1", this.$rules.required(), "sys_oper_type"),
 ]);
 tableHelper.createLayout([
   [tableHelper.span("总结", 4, 1), tableHelper.label("name",), tableHelper.item("name", 1, 2),],
   [tableHelper.labelText("就是个测试", false, 2, 1), tableHelper.label("op",), tableHelper.item("op",),],
   [tableHelper.label("op1",), tableHelper.item("op1",),],
   [tableHelper.label("op2",), tableHelper.item("op2", 1, 2),],
 ]);
 this.$dialog.show({
   bind: this,
   title: "测试动态表单",
   width: "600px",
   component: "FormTableLayout",
   fullscreen: false,
   propsData: {
     colWidth: "15,30,30,80",
     form: tableHelper.form,
     tableLayout: tableHelper.layout,
   },
   onAutoOk: (rs, dialog, next) => {
     console.log(rs);
   }
 })
```
