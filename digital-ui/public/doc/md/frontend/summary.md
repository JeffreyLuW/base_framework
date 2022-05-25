### 前端项目概述：

该项目改自若依前后分离版,传送门: https://doc.ruoyi.vip/ruoyi-vue/.

所以很多基础功能请详细阅读若依官网，此处只会做简单说明。

前端模块digital-ui，下面各种配置和文件多在此模块下。



##### 不足：

因为使用现成的开源项目修改，有有些功能也不适合大改、同时会存在部分冗余文件。

需求总是复杂多变的，所以只提供了一些最常用的基础功能。项目组可以根据自身情况，按需调整。



##### 前端主要改动:

1. src/views/test/__demo文件下放置了开发demo代码，这里主要是提供一种开发解决方案，而非要求一定如此。

2. 添加额外基础配置文件public/js/index.js。 可以自主切换两种布局。经典布局下，点击人物头像，可以切换主题色。

   ```javascript
   //定义默认主题色 经典布局下有效
   window.__union_theme="#2661a7";
   //layout2 经典布局  layout 流行模板布局|原若依默认布局版本
   window.__union_layout="layout2";//layout2 layout
   ```

   

3. 路由改为前端自动注册路由，菜单配置只需要匹配path属性即可。不再需要关心组件路径。

   菜单层级和实际路由页面不再有强对应关系，可以更灵活的组织嵌套。

   可以使用__前缀忽略文件夹或者vue文件。路由路径和文件路径相匹配。

4. 额外注册并添加了常用的表单组件，参考src/components/global/index.js

   其中下拉、单选框、复选框的options可以是数组、字典type、函数，使用更加方便
   

   ```javascript
   import Pagination from "../Pagination";
   import RightToolbar from "../RightToolbar"
   import Jsx from "../Jsx";
   import DataFormItem from "../DataFormItem";
   import DataButton from "../DataButton";
   import FormToolsRow from "../FormToolsRow";
   import AutoDialog from "../AutoDialog";
   import DataRadio from "../DataRadio";
   import DataCheckbox from "../DataCheckbox";
   import DataSelect from "../DataSelect";
   import RawEcharts from "../RawEcharts";
   import DataUpload from "../DataUpload";
   
   ```

5. 简化表单创建，避免属性的重复书写。

6. 简化上传逻辑，单个或者多个文件，直接用较长文本字段保存，不需要额外的附件表，上传和查询也不需要额外特殊操作。

7. 简化dialog调用，不需要额外模板声明、额外属性声明，直接调用组件即可。简单而且高内聚，不需要到处找调用代码。

   ```javascript
   this.$dialog.show({
     title: "对话框列表",
     component: PageListDemo,
   })
   ```

8. 原项目中的cookie的使用，基本删除，被localStorage替代。

9. 添加this.$utils对象，提供一些常用操作。

10. 添加this.$rules对象，提供常用的表单验证。

11. 添加render_ext工具，可以查看组件渲染情况，如果页面卡顿，可以分析是否过度render

12. 添加utils/app_starer.js,可以提前加载一些全局配置，比如服务器网址、登录背景、logo等(自己实现额外配置项，此处只是提供一个思路)

13. 接口代理地址改为 .env.development 文件中配置 VUE_APP_PROXY_URL

