package com.meide.web.core.reg;


import com.meide.register.domain.DataApiInfo;
import com.meide.register.domain.SysTable;
import com.meide.register.domain.SysTableColumn;
import com.meide.register.manager.DataApiManager;
import com.meide.dbengine.utils.SwaggerUtil;
import io.swagger.models.Swagger;

import java.util.ArrayList;
import java.util.Arrays;

public class DataApiSwagger {

    public static void regist(Swagger swagger) {

        //添加模型类定义
        SwaggerUtil.addDefinitions(swagger,  "deleteRequestModel", Arrays.asList(
                new SwaggerUtil.DefinitionProperty("id" ,"string",  "必填*该表主键值")
        ), true);


        //添加模型类定义
        SwaggerUtil.addDefinitions(swagger, "deleteResponseModel", Arrays.asList(
                new SwaggerUtil.DefinitionProperty("count", "integer", "删除条数")
        ), true);


        //添加模型类定义
        SwaggerUtil.addDefinitions(swagger, "insertResponseModel", Arrays.asList(
                new SwaggerUtil.DefinitionProperty("count", "integer", "插入条数")
        ), true);
        SwaggerUtil.addDefinitions(swagger, "updateResponseModel", Arrays.asList(
                new SwaggerUtil.DefinitionProperty("count", "integer", "更新条数")
        ), true);

        SwaggerUtil.addDefinitions(swagger, "FindOne", Arrays.asList(new SwaggerUtil.DefinitionProperty("id", "string","默认按主键查询。没有主键情况默认按第一列查询。" )), true);



        //添加一个分组
        SwaggerUtil.addGroup(swagger, "自动CRUD", "自动生成的CRUD接口");
        //分组下添加对应的接口。

        for (DataApiInfo apiInfo : DataApiManager.getDataApiList()) {


            String param = "";
            for (int i = 0; i < apiInfo.getTable().getColumnList().size() && i < 4; i++) {
                SysTableColumn column = apiInfo.getTable().getColumnList().get(i);
                param += column.getVoColumnName() + " | ";
            }
            if (param.endsWith(" | ")) {
                param = param.substring(0, param.length() - 3);
            }



            SysTable table = apiInfo.getTable();
            ArrayList listVo = new ArrayList();

            for (SysTableColumn column : table.getColumnList()) {
                String isNullable = "";
                if ("NO".equals(column.getIsNullable())) {
                    isNullable = "不可为空必填*";
                } else {
                    isNullable = "选填 ";
                }

                listVo.add(new SwaggerUtil.DefinitionProperty(column.getVoColumnName(), column.getJavaType()
                        , isNullable + " " + column.getColumnComment()));

            }
            //添加模型类定义
            SwaggerUtil.addDefinitions(swagger,apiInfo.getTable().getVoTableName()  , listVo, true);




            if ("list".equals(apiInfo.getApiType())) {
                ArrayList list = new ArrayList();
                list.add(new SwaggerUtil.DefinitionProperty("selectType", "string", "表示查询类型，默认为equalSign,目前支持：equalSign等于查询，bothLike左右模糊查询，leftLike左模糊查询" +
                        "，右模糊查询rightLike四种类型查询。其中" +
                        "equalSign 表示 where columnName= columnValue ，" +
                        "bothLike 表示 where columnName like '%columnValue%' ，" +
                        "leftLike 表示 where columnName like '%columnValue' ，" +
                        "rightLike 表示 where columnName like 'columnValue%'"));
                list.add(new SwaggerUtil.DefinitionProperty("columnName", "string","不传columnName情况下，默认根据主键查询。没有主键情况默认根据第一列查询。"
                        + apiInfo.getTable().getRealTableName() + "表中允许查询的列:" + param));
                list.add(new SwaggerUtil.DefinitionProperty("columnValue", "string", "本次查询的列具体数值。不传columnName columnValue情况下,默认查询全部数据前10条。"));


                list.add(new SwaggerUtil.DefinitionProperty("pageNum", "string", "默认查询第1页 当前记录起始索引"));
                list.add(new SwaggerUtil.DefinitionProperty("pageSize", "string", "默认查询前10条 每页显示记录数"));
                list.add(new SwaggerUtil.DefinitionProperty("orderByColumn", "string", "排序列"));
                list.add(new SwaggerUtil.DefinitionProperty("isAsc", "string", "排序的方向desc或者asc"));

                //添加模型类定义
                SwaggerUtil.addDefinitions(swagger, apiInfo.getTable().getVoTableName() + "QueryBean", list, true);

                list = new ArrayList();
                for (int i = 0; i < apiInfo.getTable().getColumnList().size() ; i++) {
                    SysTableColumn column = apiInfo.getTable().getColumnList().get(i);
                    list.add(new SwaggerUtil.DefinitionProperty(column.getVoColumnName(), column.getJavaType(), column.getColumnComment()));
                }
                SwaggerUtil.addDefinitions(swagger, apiInfo.getTable().getVoTableName() + "Bean", list, true);

                //分组下添加对应的接口。
                SwaggerUtil.addPath_postjson(swagger, "自动CRUD", apiInfo.getApiUrlReMark(), apiInfo.getApiUrl(), apiInfo.getTable().getVoTableName() + "QueryBean", "AjaxResultPage«"+ apiInfo.getTable().getVoTableName()+"Bean" +"»");

            } else if ("findOne".equals(apiInfo.getApiType())) {

                SwaggerUtil.addPath_postjson(swagger, "自动CRUD", apiInfo.getApiUrlReMark(), apiInfo.getApiUrl(),  "FindOne", "AjaxResult«"+apiInfo.getTable().getVoTableName()  +"»");

            } else if ("delete".equals(apiInfo.getApiType())) {

                if (apiInfo.getTable().getPkColumn() != null) {
                    //分组下添加对应的接口。
                    SwaggerUtil.addPath_postjson(swagger, "自动CRUD", apiInfo.getApiUrlReMark(), apiInfo.getApiUrl(),  "deleteRequestModel", "AjaxResult«deleteResponseModel»");
                }

            } else if ("update".equals(apiInfo.getApiType())) {

                if (apiInfo.getTable().getPkColumn() != null) {
                    ArrayList list = new ArrayList();
                    list.add(new SwaggerUtil.DefinitionProperty("updateType", "string", "min:部分更新：只更新本次请求中指定的字段，未指定的字段不更新。" +
                            "max 全量更新：除excludeColumn以外的字段，全部更新，请求json中未出现的字段，默认更新为空值"));

                    list.add(new SwaggerUtil.DefinitionProperty("excludeColumn", "string", "updateType为max全量更新时必填,排除excludeColumn中的字段," +
                            "不对其进行更新。需要排除多个字段,请用 英文逗号 隔开"));
                    list.add(new SwaggerUtil.DefinitionProperty(apiInfo.getTable().getPkColumn().getVoColumnName(), apiInfo.getTable().getPkColumn().getJavaType()
                            , "主键必填 * " + apiInfo.getTable().getPkColumn().getColumnComment()));


                    for (SysTableColumn column : apiInfo.getTable().getColumnList()) {
                        if (column.getVoColumnName().equals(apiInfo.getTable().getPkColumn().getVoColumnName())) {
                            continue;
                        }
                        list.add(new SwaggerUtil.DefinitionProperty(column.getVoColumnName(), column.getJavaType()
                                , column.getColumnComment()));
                    }

                    //添加模型类定义
                    SwaggerUtil.addDefinitions(swagger, apiInfo.getTable().getVoTableName() + "UpdateBean", list, true);
                    //分组下添加对应的接口。
                    SwaggerUtil.addPath_postjson(swagger, "自动CRUD", apiInfo.getApiUrlReMark(), apiInfo.getApiUrl(), apiInfo.getTable().getVoTableName() + "UpdateBean", "AjaxResult«updateResponseModel»");
                }
            } else if ("insert".equals(apiInfo.getApiType())) {


                //分组下添加对应的接口。
                SwaggerUtil.addPath_postjson(swagger, "自动CRUD", apiInfo.getApiUrlReMark(), apiInfo.getApiUrl(), apiInfo.getTable().getVoTableName() , "AjaxResult«insertResponseModel»");
            }


        }


    }

}
