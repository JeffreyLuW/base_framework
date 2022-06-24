package com.meide.dbengine.api;

import com.meide.common.exception.CustomException;
import com.meide.dbengine.runninginfo.RunningInfo;
import com.meide.dbengine.tableinfo.ColInfo;
import com.meide.dbengine.utils.*;
import io.swagger.models.Swagger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 更新API
 */
public class UpdateApi extends BaseApi {

    @Override
    public boolean support(String dbType) {
        return true;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String buildSql() {
        String sql = "<update id=\"update\" parameterType=\"map\">\n" +
                "    update {tableName} \n" +
                "    set {cols} \n" +
                "    where {where} \n" +
                "  </update>";
        HashMap<String, String> map = new HashMap<>();
        map.put("tableName", this.tableInfo.getName());
        map.put("where", pkCol.getName() + " = #{" + pkCol.getVoName() + "} ");
        map.put("cols", this.cols.stream().map(c -> {
            return c.getName() + " = #{" + c.getVoName() + JdbcTypeUtils.getTypeMapperDesc(c.getType()) + "}";
        }).collect(Collectors.joining(",\n")));
        return StringReplacer.replace(sql, map);
    }

    //每个接口都有默认需要处理的列。
    protected void copyCols() {
        //当前的列是个浅复制。可以随意增删，不影响其他接口列。
        this.cols = new ArrayList<>();
        for (ColInfo c : tableInfo.getCols()) {
            //主键列 不更新！
            if (isIgnoredCols(c) || c.isPk()) {
                continue;
            }
            this.cols.add(c);
        }
    }

    @Override
    public Object apiCall(RunningInfo runningInfo) throws Exception {
        Map<String, Object> params = runningInfo.getParams();
        if (null == params || params.size() < 2) {
            //params error
            throw new CustomException("params is required");
        }
        if (null == params.get(pkCol.getVoName()) || "".equals(params.get(pkCol.getVoName()))) {
            //更新需要主键
            throw new CustomException(pkCol.getVoName() + " is required");
        }
        MapperHelper mapperHelper = this.engineFacotry.getMapperHelper();
        Object rs = mapperHelper.exeStatementWithType(this.getMapperId(), runningInfo.getParams(), Map.class, Integer.class, false);
        return rs;
    }

    @Override
    public void registerToSwagger(Swagger swagger) {
        //添加一个分组
        String tag = "自动接口";
        SwaggerUtil.addGroup(swagger, tag, "");

        List<SwaggerUtil.DefinitionProperty> propertyList = new ArrayList<>();
        propertyList.add(new SwaggerUtil.DefinitionProperty(pkCol.getVoName(), pkCol.getSwaggerType(), pkCol.getComment()));
        propertyList.addAll(this.cols.stream().map(c ->
                new SwaggerUtil.DefinitionProperty(c.getVoName(), c.getSwaggerType(), c.getComment())).collect(Collectors.toList()));
        //添加模型类定义---参数
        SwaggerUtil.addDefinitions(swagger, this.tableInfo.getName() + "_" + getName() + "_param",
                propertyList, false);

        //添加接口定义
        SwaggerUtil.addPath_postjson(swagger, tag, this.tableInfo.getName() + "|修改|" + EngineUtil.strVal(this.tableInfo.getComment()), this.getApiUrl(), this.tableInfo.getName() + "_" + getName() + "_param", null);
    }
}
