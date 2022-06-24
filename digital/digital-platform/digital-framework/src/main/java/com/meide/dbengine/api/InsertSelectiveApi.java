package com.meide.dbengine.api;

import com.meide.common.exception.CustomException;
import com.meide.dbengine.runninginfo.RunningInfo;
import com.meide.dbengine.utils.*;
import io.swagger.models.Swagger;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 插入API-insertSelective 插入非空值
 */
public class InsertSelectiveApi extends BaseApi {

    @Override
    public boolean support(String dbType) {
        return true;
    }

    @Override
    public String getName() {
        return "insertSelective";
    }

    @Override
    public String buildSql() {
        String sql = "<insert id=\"insertSelective\" parameterType=\"map\">\n" +
                "    insert into {tableName}\n" +
                "    <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
                "     {cols} \n" +
                "    </trim>\n" +
                "    <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n" +
                "     {values} \n" +
                "    </trim>\n" +
                "  </insert>";
        HashMap<String, String> map = new HashMap<>();
        map.put("tableName", this.tableInfo.getName());
        map.put("cols", this.cols.stream().map(c -> "<if test=\""+c.getVoName()+" != null\">"+c.getName()+",</if>\n").collect(Collectors.joining("")));
        map.put("values", this.cols.stream().map(c -> {
            return " <if test=\""+c.getVoName()+" != null\"> #{"+c.getVoName() + JdbcTypeUtils.getTypeMapperDesc(c.getType()) + "}, </if>\n";
        }).collect(Collectors.joining("")));
        return StringReplacer.replace(sql, map);
    }

    @Override
    public Object apiCall(RunningInfo runningInfo) throws Exception {
        Map<String, Object> params = runningInfo.getParams();
        if (null == params || params.isEmpty()) {
            //params error
            throw new CustomException( "params is required");
        }
        MapperHelper mapperHelper = this.engineFacotry.getMapperHelper();
        Object rs = mapperHelper.exeStatementWithType(this.getMapperId(), runningInfo.getParams(), Map.class, Integer.class, false);
        return rs;
    }

    @Override
    public void registerToSwagger(Swagger swagger) {
        //添加一个分组
        String tag = "自动接口";
        SwaggerUtil.addGroup(swagger, tag,"");

        //添加模型类定义---参数
        SwaggerUtil.addDefinitions(swagger, this.tableInfo.getName() + "_"+getName()+"_param",
                this.cols.stream().map(c-> new SwaggerUtil.DefinitionProperty(c.getVoName(), c.getSwaggerType(), c.getComment())).collect(Collectors.toList())
                , false);

        //添加接口定义
        SwaggerUtil.addPath_postjson(swagger, tag, this.tableInfo.getName() + "|插入非空值|"+ EngineUtil.strVal(this.tableInfo.getComment()), this.getApiUrl(), this.tableInfo.getName() + "_"+getName()+"_param", null);
    }
}
