package com.meide.dbengine.api;

import com.meide.common.exception.CustomException;
import com.meide.dbengine.runninginfo.RunningInfo;
import com.meide.dbengine.utils.EngineUtil;
import com.meide.dbengine.utils.MapperHelper;
import com.meide.dbengine.utils.SwaggerUtil;
import io.swagger.models.Swagger;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 删除api，根据主键
 */
public class DeleteApi extends BaseApi {

    @Override
    public boolean support(String dbType) {
        return true;
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String buildSql() {
        String whereSql=pkCol.getName()+" in <foreach collection=\"__ids\" item=\"__id\" separator=\",\" open=\"(\" close=\")\">#{__id}</foreach>\n";
        //+ pkCol.getName() + " = #{" + pkCol.getVoName() + "}" +
        String sql = null;
        if (this.tableInfo.isLogicDelete()) {
            sql = "  <update id=\"delete\" parameterType=\"map\">\n" +
                    "    update " + this.tableInfo.getName() + "  set " + tableInfo.getLogicDeleteField() + " = " + tableInfo.getLogicDeleteValue() +
                    " where " +whereSql +
                    " </update>";
        } else {
            sql = " <delete id=\"delete\" parameterType=\"map\">\n" +
                    "delete from " + this.tableInfo.getName() + " where " +whereSql+
                    " </delete>";
        }
        return sql;
    }

    @Override
    public Object apiCall(RunningInfo runningInfo) throws Exception {
        Map<String, Object> params = runningInfo.getParams();
        String pkName = pkCol.getVoName();
        if (null == params || null == params.get(pkName)) {
            //params error
            throw new CustomException(pkName + " is required");
        }
        //改为批量删除 __ids
        List<String> pkVoIds= Arrays.asList(String.valueOf(params.get(pkName)).trim().split(",")).stream()
                .map(s->s.trim())
                .filter(s-> !StringUtils.isEmpty(s))
                .collect(Collectors.toList());
        if (pkVoIds.isEmpty()) {
            //params error
            throw new CustomException(pkName + " is required");
        }

        params.put("__ids",pkVoIds);
        MapperHelper mapperHelper = this.engineFacotry.getMapperHelper();
        Object rs = mapperHelper.exeStatementWithType(this.getMapperId(), runningInfo.getParams(), Map.class, Integer.class, false);
        return rs;
    }

    @Override
    public void registerToSwagger(Swagger swagger) {
        //添加一个分组
        String tag = "自动接口";
        SwaggerUtil.addGroup(swagger, tag, "");

        //添加模型类定义---参数
        String pkName = pkCol.getVoName();
        SwaggerUtil.addDefinitions(swagger, this.tableInfo.getName() + "_" + getName() + "_param", Arrays.asList(
                new SwaggerUtil.DefinitionProperty(pkName, pkCol.getSwaggerType(), "必填,可批量删,逗号隔开")
        ), false);

        //添加接口定义
        SwaggerUtil.addPath_postjson(swagger, tag, this.tableInfo.getName() + "|删除|" + EngineUtil.strVal(this.tableInfo.getComment()), this.getApiUrl(), this.tableInfo.getName() + "_" + getName() + "_param", null);
    }
}
