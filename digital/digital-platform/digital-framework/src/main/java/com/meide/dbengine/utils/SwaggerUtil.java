package com.meide.dbengine.utils;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.meide.common.utils.StringUtils;
import com.meide.common.utils.sign.Md5Utils;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.*;
import org.springframework.context.ApplicationContext;
import org.springframework.web.util.UriComponents;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.PropertySourcedRequestMappingHandlerMapping;
import springfox.documentation.swagger.common.HostNameProvider;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.web.Swagger2Controller;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * swagger的工具类。
 */
public class SwaggerUtil {

    private static List<SwaggerRegister> swaggerRegisterList = new ArrayList<>();

    /**
     * 添加swagger文档自定义注册接口。
     *
     * @param list
     */
    public static void addSwaggerRegisters(List<SwaggerRegister> list) {
        if (null != list) {
            swaggerRegisterList.addAll(list);
        }
    }

    public static void addSwaggerRegister(SwaggerRegister register) {
        if (null != register) {
            swaggerRegisterList.add(register);
        }
    }

    /**
     * 获取swagger的对象。
     *
     * @param applicationContext
     * @param swaggerGroup
     * @param servletRequest
     * @return
     * @throws Exception
     */
    public static Swagger getSwagger(ApplicationContext applicationContext, String swaggerGroup, HttpServletRequest servletRequest) throws Exception {
        Swagger swagger = null;
        Swagger2Controller c = getSwaggerController(applicationContext);
        String hostNameOverride = getFieldValue(c, "hostNameOverride");
        DocumentationCache documentationCache = getFieldValue(c, "documentationCache");
        ServiceModelToSwagger2Mapper mapper = getFieldValue(c, "mapper");
//        JsonSerializer jsonSerializer = getFieldValue(c, "jsonSerializer");

        String groupName = (String) Optional.fromNullable(swaggerGroup).or("default");
        Documentation documentation = documentationCache.documentationByGroup(groupName);
        if (documentation != null) {
            swagger = mapper.mapDocumentation(documentation);
            UriComponents uriComponents = HostNameProvider.componentsFrom(servletRequest, swagger.getBasePath());
            swagger.basePath(Strings.isNullOrEmpty(uriComponents.getPath()) ? "/" : uriComponents.getPath());
            if (Strings.isNullOrEmpty(swagger.getHost())) {
                swagger.host(hostName(hostNameOverride, uriComponents));
            }

        }

        for (SwaggerRegister s : swaggerRegisterList) {
            s.registerToSwagger(swagger);
        }

        return swagger;
    }

    //添加一个分组
    public static void addGroup(Swagger swagger, String name, String desc) {
        Tag oldTag = swagger.getTag(name);
        if (oldTag != null) {
            oldTag.description(desc);
            return;
        }
        Tag tag = new Tag();
        tag.description(desc);
        tag.name(name);
        swagger.addTag(tag);
    }

    /**
     * 添加一个简单的 post_json 路径定义。
     * 客户端请求时发送json。
     *
     * @param swagger
     * @param tag
     * @param url      api路径
     * @param paramRef 参数引用的模型类
     * @param respRef  响应结果引用的模型类。 负责返回标准数据|data为一个model
     */
    public static void addPath_postjson(Swagger swagger, String tag, String desc, String url, String paramRef, String respRef) {
        List<Parameter> parameters = null;
        if (!StringUtils.isEmpty(paramRef)) {
            parameters = Arrays.asList(new BodyParameter().schema(new RefModel(paramRef)));
        }
        addPath(swagger, tag, desc, url, "post", parameters, respRef);
    }

    /**
     * 添加一个请求的接口定义。
     *
     * @param swagger
     * @param tag        所在组
     * @param desc
     * @param url
     * @param parameters 参数 FormParameter QueryParameter BodyParameter
     * @param respRef    User = AjaxResult«User»  分页用 AjaxResultPage«User»
     */
    public static void addPath(Swagger swagger, String tag, String desc, String url, String method, List<Parameter> parameters, String respRef) {
        Path path = new Path();
        Operation operation = new Operation();
        operation.tag(tag);
        operation.summary(desc);
        operation.operationId(Md5Utils.hash(tag + "_" + url + "_" + method));

        operation.consumes("application/json");
        //参数 query 格式    FormParameter QueryParameter
        if (null != parameters)
            operation.setParameters(parameters);
        Response response = new Response();
        response.description("OK");
        //结果引用一个对象即可。
        if (StringUtils.isEmpty(respRef)) {
            response.setResponseSchema(
                    new RefModel("AjaxResult«object»")
            );
        } else {
            if (respRef.startsWith("Ajax")) {
                response.setResponseSchema(
                        new RefModel(respRef)
                );
            } else {
                response.setResponseSchema(
                        new RefModel("AjaxResult«" + respRef + "»")
                );
            }
        }
        operation.addResponse("200", response);
        path.set(method, operation);
        swagger.path(url, path);
    }

    //定义实体类型。则返回类型可以引用该类型。
    public static void addDefinitions(Swagger swagger, Model model) {
        if (swagger == null) {
            return;
        }
        Map<String, Model> map = swagger.getDefinitions();
        map.put(model.getTitle(), model);
    }

    /**
     * 以更简单的方式，添加自定义的模型类。  User
     *
     * @param title       比如 User
     * @param properties
     * @param wrapperType true时会额外生成对应的三个常用wrapper定义:  AjaxResultPage«User»   AjaxResult«User»
     */
    public static Model addDefinitions(Swagger swagger, String title, List<DefinitionProperty> properties, boolean wrapperType) {
        if (swagger == null || null == properties || properties.isEmpty()) {
            return null;
        }
        Map<String, Model> map = swagger.getDefinitions();
        if (wrapperType) {
            //返回分页的wrapper
            String out1Title = "AjaxResultPage«" + title + "»";
            Model modelWrapper = map.get(out1Title);
            if (null == modelWrapper) {
                modelWrapper = createModel(
                        out1Title,
                        Arrays.asList(
                                new DefinitionProperty("code", "int", "int32", "返回码"),
                                new DefinitionProperty("msg", "string", null, "返回提示"),
                                new DefinitionProperty("total", "int", "int32", "总条数"),
                                DefinitionProperty.ref("rows", "array", "列表集合", title)
                        )
                );
                map.put(modelWrapper.getTitle(), modelWrapper);
            }
            //返回单个对象的wrapper
            String outTitle = "AjaxResult«" + title + "»";
            Model wrapper = map.get(outTitle);
            if (null == wrapper) {
                wrapper = createModel(
                        outTitle,
                        Arrays.asList(
                                new DefinitionProperty("code", "int", "int32", "返回码"),
                                new DefinitionProperty("msg", "string", null, "返回提示"),
                                DefinitionProperty.ref("data", null, "返回值", title)
                        )
                );
                map.put(wrapper.getTitle(), wrapper);
            }
        }
        Model model = map.get(title);
        if (model == null) {
            model = createModel(title, properties);
            map.put(model.getTitle(), model);
        }
        return model;
    }

    //添加单一warpper
    public static Model addDefinitions(Swagger swagger, String title, List<DefinitionProperty> properties, int wrapperType) {
        if (swagger == null || null == properties || properties.isEmpty()) {
            return null;
        }
        Map<String, Model> map = swagger.getDefinitions();
        if (wrapperType == 1) {
            //返回单个对象的wrapper
            String outTitle = "AjaxResult«" + title + "»";
            Model wrapper = map.get(outTitle);
            if (null == wrapper) {
                wrapper = createModel(
                        outTitle,
                        Arrays.asList(
                                new DefinitionProperty("code", "int", "int32", "返回码"),
                                new DefinitionProperty("msg", "string", null, "返回提示"),
                                DefinitionProperty.ref("data", null, "返回值", title)
                        )
                );
                map.put(wrapper.getTitle(), wrapper);
            }
        }
        if (wrapperType == 2) {
            //返回分页的wrapper
            String out1Title = "AjaxResultPage«" + title + "»";
            Model modelWrapper = map.get(out1Title);
            if (null == modelWrapper) {
                modelWrapper = createModel(
                        out1Title,
                        Arrays.asList(
                                new DefinitionProperty("code", "int", "int32", "返回码"),
                                new DefinitionProperty("msg", "string", null, "返回提示"),
                                new DefinitionProperty("total", "int", "int32", "总条数"),
                                DefinitionProperty.ref("rows", "array", "列表集合", title)
                        )
                );
                map.put(modelWrapper.getTitle(), modelWrapper);
            }
        }
        Model model = map.get(title);
        if (model == null) {
            model = createModel(title, properties);
            map.put(model.getTitle(), model);
        }
        return model;
    }

    //以更简单的方式，添加自定义的模型类。
    //responses-》"$ref": "#/definitions/AjaxResult«title»","originalRef": "AjaxResult«用户实体»"
    public static ModelImpl createModel(String title, List<DefinitionProperty> properties) {
        if (null == properties || properties.isEmpty()) {
            return null;
        }
        ModelImpl model = new ModelImpl();
        model.setTitle(title);
        model.type("object");

        for (DefinitionProperty p : properties) {
            AbstractProperty tmp = null;
            //string,datetime,object,boolean, float,double,int,long
            //string,datetime:string|date-time,object,boolean, float,double,integer,long
            if (null != p && null != p.type) {
                switch (p.type) {
                    case "boolean":
                        tmp = new BooleanProperty();
                        break;
                    case "int":
                    case "integer":
                        tmp = new IntegerProperty();
                        break;
                    case "long":
                        tmp = new LongProperty();
                        break;
                    case "float":
                        tmp = new FloatProperty();
                        break;
                    case "double":
                        tmp = new DoubleProperty();
                        break;
                    case "string":
                        tmp = new StringProperty(p.format);
                        break;
                    case "datetime":
                        tmp = new DateTimeProperty();
                        break;
                    case "object":
                        tmp = new ObjectProperty();
                        break;
                }
            }
            //有ref
            if (!StringUtils.isEmpty(p.ref)) {
                RefProperty refProperty = new RefProperty();
                refProperty.description(p.description);
                refProperty.set$ref(p.ref);
                if (!StringUtils.isEmpty(p.type))
                    refProperty.setType(p.type);
                tmp = refProperty;
            }

            if (null != tmp) {
                if (p._default != null) {
                    tmp.setDefault(p._default);
                }
                tmp.description(p.description).setRequired(p.required);
                model.addProperty(p.name, tmp);
            }
        }
        return model;
    }

    private static String hostName(String hostNameOverride, UriComponents uriComponents) {
        if ("DEFAULT".equals(hostNameOverride)) {
            String host = uriComponents.getHost();
            int port = uriComponents.getPort();
            return port > -1 ? String.format("%s:%d", host, port) : host;
        } else {
            return hostNameOverride;
        }
    }

    private static <T extends Object> T getFieldValue(Swagger2Controller c, String fieldName) throws Exception {
        Field field = Swagger2Controller.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(c);
    }

    private static Swagger2Controller getSwaggerController(ApplicationContext applicationContext) throws Exception {
        PropertySourcedRequestMappingHandlerMapping mapping = (PropertySourcedRequestMappingHandlerMapping) applicationContext.getBean("swagger2ControllerMapping");
        Field field = PropertySourcedRequestMappingHandlerMapping.class.getDeclaredField("handler");
        field.setAccessible(true);
        Swagger2Controller c = (Swagger2Controller) field.get(mapping);
        return c;
    }

    /**
     * 简单的实体类定义。只支持几种基本类型。
     */
    public static class DefinitionProperty {
        String name;
        String type;//string,datetime:string|date-time,object,boolean, float,double,integer,long
        String format;
        String description;
        boolean required;
        String ref;

        String _default;//可以给基本类型执行默认值

        public DefinitionProperty() {
        }

        public DefinitionProperty(String name, String type, String description) {
            this.name = name;
            this.type = type;
            this.description = description;
        }

        public DefinitionProperty(String name, String type, String format, String description) {
            this.name = name;
            this.type = type;
            this.format = format;
            this.description = description;
        }


        //type 可以是 null 或者 array
        public static DefinitionProperty ref(String name, String type, String description, String ref) {
            DefinitionProperty rs = new DefinitionProperty(name, type, null, description);
            rs.ref = ref;
            return rs;
        }

        public DefinitionProperty defaultVal(String val) {
            this._default = val;
            return this;
        }
    }
}
