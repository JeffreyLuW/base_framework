package com.meide.dbengine.api;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.meide.dbengine.runninginfo.RunningInfo;
import com.meide.dbengine.tableinfo.ColInfo;
import com.meide.dbengine.tableinfo.TableInfo;
import com.meide.dbengine.utils.EngineUtil;
import com.meide.dbengine.utils.MapperHelper;
import com.meide.dbengine.utils.StringReplacer;
import com.meide.dbengine.utils.SwaggerUtil;
import io.swagger.models.Swagger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 查询api，默认分页，前四列为查询条件 默认like eq字段需要用 __eq:'vo_name,user_name'指定。
 */
public class ListApi extends BaseApi {

    private List<ColInfo> whereCols = new ArrayList<>();

    @Override
    public boolean support(String dbType) {
        return true;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public void setTableInfo(TableInfo tableInfo) {
        super.setTableInfo(tableInfo);
        //分页条件
        for (ColInfo c : cols) {
            //主键或者逻辑删除字段，不作为查询条件。
            if (c.isPk() || (tableInfo.isLogicDelete() && c.getName().equals(tableInfo.getLogicDeleteField())))
                continue;
            whereCols.add(c);
            if (whereCols.size() >= 4) break;
        }
    }

    @Override
    public String buildSql() {
        String sql = "<select id=\"list\" parameterType=\"map\" resultType=\"map\"> select {cols} from {tableName} <where> {where} {logicDel} </where> </select>";
        HashMap<String, String> map = new HashMap<>();
        map.put("tableName", this.tableInfo.getName());
        map.put("cols", this.cols.stream().map(c -> {
            // name as name, name
            return c.getName().equals(c.getVoName()) ? c.getName() : (c.getName() + " as " + c.getVoName());
        }).collect(Collectors.joining(",")));

        //添加逻辑删除判断
        map.put("logicDel",
                tableInfo.isLogicDelete() ? " and " + tableInfo.getLogicDeleteField() + " = " + tableInfo.getLogicNoDeleteValue() + " " : ""
        );

        //mysql
        if ("mysql".equals(this.engineFacotry.getDbType())) {
            map.put("where", this.whereCols.stream().map(c -> {
                return " <if test='" + c.getVoName() + "!= null and " + c.getVoName() + " !=\"\" '>\n" +
                        "   <choose>\n" +
                        "                <when test=\"__" + c.getVoName() + "_like==true\">\n" +
                        "                   and  " + c.getName() + " like concat('%', #{" + c.getVoName() + "},'%')\n" +
                        "                </when>\n" +
                        "                <otherwise>\n" +
                        "                   and " + c.getName() + " = #{" + c.getVoName() + "}\n" +
                        "                </otherwise>\n" +
                        "            </choose>\n" +
                        " </if>";
            }).collect(Collectors.joining("\n")));
        }
        //oracle
        else if ("oracle".equals(this.engineFacotry.getDbType())) {
            map.put("where", this.whereCols.stream().map(c -> {
                return " <if test='" + c.getVoName() + "!= null and " + c.getVoName() + " !=\"\" '>\n" +
                        "   <choose>\n" +
                        "                <when test=\"__" + c.getVoName() + "_like==true\">\n" +
                        "                   and  " + c.getName() + " like '%'||#{" + c.getVoName() + "}||'%' \n" +
                        "                </when>\n" +
                        "                <otherwise>\n" +
                        "                   and " + c.getName() + " = #{" + c.getVoName() + "}\n" +
                        "                </otherwise>\n" +
                        "            </choose>\n" +
                        " </if>";
            }).collect(Collectors.joining("\n")));
        } else {
            //其他 sqlserver
            map.put("where", this.whereCols.stream().map(c -> {
                return " <if test='" + c.getVoName() + "!= null and " + c.getVoName() + " !=\"\" '>\n" +
                        "   <choose>\n" +
                        "                <when test=\"__" + c.getVoName() + "_like==true\">\n" +
                        "                  and   " + c.getName() + " like '%'+#{" + c.getVoName() + "}+'%' \n" +
                        "                </when>\n" +
                        "                <otherwise>\n" +
                        "                  and  " + c.getName() + " = #{" + c.getVoName() + "}\n" +
                        "                </otherwise>\n" +
                        "            </choose>\n" +
                        " </if>";
            }).collect(Collectors.joining("\n")));
        }
        return StringReplacer.replace(sql, map);
    }

    @Override
    public Object apiCall(RunningInfo runningInfo) throws Exception {
        Map<String, Object> params = runningInfo.getParams();
        boolean disablePage = params != null && params.get("__page") != null && params.get("__page").equals(false);
        if (!disablePage) {
            runningInfo.setPage(true);
            //分页
            int pageNum = 1;
            int pageSize = 10;
            if (null != params) {
                pageNum = EngineUtil.parseInt(params.get("pageNum"), pageNum);
                pageSize = EngineUtil.parseInt(params.get("pageSize"), pageSize);
            }
            if (pageNum < 1) pageNum = 1;
            if (pageSize < 1) pageSize = 10;
            PageHelper.startPage(pageNum, pageSize);
        }
        //__"+c.getVoName()+"_like==true
        //默认like eq字段需要用 __eq:'vo_name,user_name'指定。
        if (null != params) {
            Set<String> eqMap = params.get("__eq") == null ? new HashSet<>() : Arrays.asList(
                    EngineUtil.strVal(params.get("__eq")).split(",")
            ).stream().collect(Collectors.toSet());
            HashMap<String, Object> likeSettings = new HashMap<>();
            for (Map.Entry<String, Object> kv : params.entrySet()) {
                if (kv.getKey().startsWith("__") || null == kv.getValue()) {
                    continue;
                }
                //非字符串，默认相等。
                if (!(kv.getValue() instanceof String)) {
                    likeSettings.put("__" + kv.getKey() + "_like", false);
                } else {
                    //字符串默认like，除非指定了__eq
                    likeSettings.put("__" + kv.getKey() + "_like", !eqMap.contains(kv.getKey()));
                }
            }
            params.putAll(likeSettings);
        }

        MapperHelper mapperHelper = this.engineFacotry.getMapperHelper();
        Object rs = mapperHelper.exeStatementWithType(this.getMapperId(), runningInfo.getParams(), List.class, Map.class, false);
        if (runningInfo.isPage()) {
            if (rs instanceof Page) {
                runningInfo.setTotal(((Page) rs).getTotal());
            }
        }
        return this.engineFacotry.getDictConverter().convert(this, rs);
    }

    @Override
    public void registerToSwagger(Swagger swagger) {
        //添加一个分组
        String tag = "自动接口";
        SwaggerUtil.addGroup(swagger, tag, "");

        //添加模型类定义---参数
        List<SwaggerUtil.DefinitionProperty> properties = whereCols.stream().map(c -> new SwaggerUtil.DefinitionProperty(c.getVoName(), c.getSwaggerType(), c.getComment())).collect(Collectors.toList());
        properties.add(new SwaggerUtil.DefinitionProperty("pageNum", "integer", "页码,默认1").defaultVal("1"));
        properties.add(new SwaggerUtil.DefinitionProperty("pageSize", "integer", "页码,默认10").defaultVal("10"));

        SwaggerUtil.addDefinitions(swagger, this.tableInfo.getName() + "_" + getName() + "_param",
                properties
                , false);
        //添加结果模型类定义
        List<SwaggerUtil.DefinitionProperty> colsModel = new ArrayList<>();
        for (ColInfo c : this.cols) {
            colsModel.add(new SwaggerUtil.DefinitionProperty(c.getVoName(), c.getSwaggerType(), c.getComment()));
        }
        SwaggerUtil.addDefinitions(swagger, this.tableInfo.getName() + "_list_rs", colsModel, 2);

        //添加接口定义
        SwaggerUtil.addPath_postjson(swagger, tag, this.tableInfo.getName() + "|分页|" + EngineUtil.strVal(this.tableInfo.getComment()), this.getApiUrl(), this.tableInfo.getName() + "_" + getName() + "_param", "AjaxResultPage«" + this.tableInfo.getName() + "_list_rs" + "»");
    }
}
