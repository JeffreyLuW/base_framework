### 开发上手：

#### 项目运行

1. clone项目代码 

2.  cd到digital-ui目录,安装依赖并运行

   ```npm
   yarn
   npm run dev 
   ```
   
#### 新页面-CRUD

1. 参考views/test/__demo文件夹的示例，创建自己的页面。

2. 路由配置(参考/router/auto_routes.js)
  
   如果是页面，一般无需配置。访问路径等同于views下的目录。
   
   如果是非菜单页面，目录或者文件名用双下划线开头即可。
   
   如果页面需要访问，但又不再菜单中，需要配置auto_routes.js中的 notMenuRoute 和 topLevelRoute

3. 菜单需要在 #/system/menu/index 菜单管理页面中配置。配置完成后刷新页面即可。

4. 接口调用参考src/api/...文件夹

5. 修改自己页面中的字段、验证、接口调用直至开发完成。

6. 如果是简单表单，可以查看 DataEditForm 或者扩展类似组件

```javascript
    this.$dialog.show({
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
      }
    })
```

#### 说明：

   ```
   上面的__demo示例只是提供一种解决方案。前端很难在较大的组件上提供通用模板。
   实际生产中可能会有更多不同的样板代码存在，需根据实际情况调整。
   ```