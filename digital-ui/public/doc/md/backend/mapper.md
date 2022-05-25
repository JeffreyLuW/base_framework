# mapper简单使用方法

> 官方文档 https://github.com/abel533/Mapper/wiki

通用 Mapper 可以很简单的让你获取基础的单表数据库操作方法，也很方便扩展通用方法。使用通用 Mapper 可以极大的提高工作效率。

下面是基本使用的方法，如有其它需求或问题，建议阅读官方文档

## 1. 包扫描

1. 在启动类或配置类添加包扫描注解`@tk.mybatis.spring.annotation.MapperScan("com.meide.**.mapper")`
2. 如果不添加包扫描，也可以在每个mapper接口类名上添加`@org.apache.ibatis.annotations.Mapper`注解

## 2. 标注实体类

需要在实体类主键上标注`@javax.persistence.Id`

如果实例类有数据库中不存在的字段，给这些字段标注`@javax.persistence.Transient`，在生成sql的时候忽略掉它们


## 3. 继承通用crud接口

mapper接口继承`tk.mybatis.mapper.common.Mapper<T>`接口，可以不提供xml格式SQL文件

T为与数据库表对应的实体类

## 4. 使用

在service层注入mapper接口，就可以直接使用基本的单表的crud功能。

如果需要复杂数据库操作，使用mybatis常规操作，在mapper接口添加接口方法，

在`resources/mapper/模块名/`中添加xml文件；

- 需要注意的是，新增方法注意不要和已存在通用方法重名。