package com.meide.dbengine.api;

import com.meide.common.exception.CustomException;
import com.meide.dbengine.runninginfo.RunningInfo;
import com.meide.dbengine.tableinfo.ColInfo;
import com.meide.dbengine.utils.EngineUtil;
import com.meide.dbengine.utils.MapperHelper;
import com.meide.dbengine.utils.SwaggerUtil;
import io.swagger.models.Swagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 查询api，根据主键查询。
 */
public class SelectApi extends BaseApi {

    @Override
    public boolean support(String dbType) {
        return true;
    }

    @Override
    public String getName() {
        return "select";
    }

    @Override
    public String buildSql() {
        StringBuilder sb = new StringBuilder();
        for (ColInfo c : this.cols) {
            if (sb.length() > 0) sb.append(",");
            if (c.getName().equals(c.getVoName())) {
                sb.append(c.getName());
            } else {
                sb.append(c.getName() + " as " + c.getVoName());
            }
        }
        String sql = "<select id=\"" + getName() + "\" parameterType=\"map\" resultType=\"map\">" +
                "select " + sb.toString() + " from " + this.tableInfo.getName() + " where " + pkCol.getName() + " = #{" +pkCol.getVoName() + "}" +
                "</select>";
        return sql;
    }

    @Override
    public Object apiCall(RunningInfo runningInfo) throws Exception {
        Map<String, Object> params = runningInfo.getParams();
        String pkName =pkCol.getVoName();
        if (null == params || null == params.get(pkName)) {
            //params error
            throw new CustomException(pkName + " is required");
        }
        MapperHelper mapperHelper = this.engineFacotry.getMapperHelper();
        Object rs = mapperHelper.exeStatementWithType(this.getMapperId(), runningInfo.getParams(), Map.class, Map.class, false);
        return this.engineFacotry.getDictConverter().convert(this,rs);
    }

    @Override
    public void registerToSwagger(Swagger swagger) {
        //添加一个分组
        String tag = "自动接口";
        SwaggerUtil.addGroup(swagger, tag,"");

        //添加模型类定义---参数
        String pkName = pkCol.getVoName();
        SwaggerUtil.addDefinitions(swagger, this.tableInfo.getName() + "_"+getName()+"_param", Arrays.asList(
                new SwaggerUtil.DefinitionProperty(pkName, pkCol.getSwaggerType(), "必填*该表主键值")
        ), false);
        //添加结果模型类定义
        List<SwaggerUtil.DefinitionProperty> colsModel = new ArrayList<>();
        for (ColInfo c : this.cols) {
            colsModel.add(new SwaggerUtil.DefinitionProperty(c.getVoName(), c.getSwaggerType(), c.getComment()));
        }
        SwaggerUtil.addDefinitions(swagger, this.tableInfo.getName() + "_select_rs", colsModel, 1);

        //添加接口定义
        SwaggerUtil.addPath_postjson(swagger, tag,  this.tableInfo.getName() + "|查询|"+ EngineUtil.strVal(this.tableInfo.getComment()), this.getApiUrl(), this.tableInfo.getName() + "_"+getName()+"_param", "AjaxResult«" + this.tableInfo.getName() + "_select_rs" + "»");
    }
}
