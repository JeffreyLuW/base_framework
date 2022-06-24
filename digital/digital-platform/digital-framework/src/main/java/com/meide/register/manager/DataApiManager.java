package com.meide.register.manager;

import com.meide.common.utils.spring.SpringUtils;
import com.meide.register.config.properties.DataApiProperties;
import com.meide.register.converter.TableConverter;
import com.meide.register.domain.DataApiInfo;
import com.meide.register.domain.SysTable;
import com.meide.register.domain.SysTableColumn;
import com.meide.register.manager.build.StringXmlMapperUtil;
import com.meide.register.manager.reg.*;
import com.meide.register.service.IDataApiService;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态注册统一crud接口
 *
 * @author jiay
 */
public class DataApiManager {


    //数据接口配置文件
    @Autowired
    private DataApiProperties dataApiProperties;

    private static ArrayList<DataApiInfo> dataApiList = new ArrayList<>();

    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private SqlSessionFactory sqlSessionFactory;

    private Configuration configuration;

    private List<SysTable> sysTableList;

    private RequestMappingInfo.BuilderConfiguration requestMappingInfoConfiguration;

    private IDataApiService dataApiService;


    private TableConverter tableConverter;

    public DataApiManager(IDataApiService dataApiService, TableConverter tableConverter, DataApiProperties dataApiProperties) {
        try {
            this.tableConverter = tableConverter;
            this.dataApiService = dataApiService;
            this.requestMappingHandlerMapping = SpringUtils.getBean(RequestMappingHandlerMapping.class);
            this.sqlSessionFactory = SpringUtils.getBean(SqlSessionFactory.class);
            this.configuration = sqlSessionFactory.getConfiguration();
            //  AND table_name NOT LIKE 'qrtz_%' AND table_name NOT LIKE 'gen_%'

            String ignoredTableSetting = dataApiProperties.getIgnoredTableList();

            String ignoredTableList[] = ignoredTableSetting.split(",");

            String ignorSql = "";
            for (String tableName : ignoredTableList) {
                if(tableName.contains("%")){
                    ignorSql += " AND table_name NOT LIKE '"+tableName+"'";
                }else {
                    ignorSql += " AND table_name <> '"+tableName+"'";
                }

            }

            this.sysTableList = dataApiService.selectTableList(ignorSql);
            //获取每个表的所有列
            getTableColumn();

            Field field = RequestMappingHandlerMapping.class.getDeclaredField("config");
            field.setAccessible(true);
            this.requestMappingInfoConfiguration = (RequestMappingInfo.BuilderConfiguration) field.get(requestMappingHandlerMapping);


            for (SysTable sys : sysTableList) {


                //动态注册list控制类
                SelectControllerRegister selectControllerRegister = new SelectControllerRegister(requestMappingHandlerMapping,
                        requestMappingInfoConfiguration, sys, dataApiList);
                selectControllerRegister.regist();

                //动态注册page控制类
                FindOneControllerRegister pageControllerRegister = new FindOneControllerRegister(requestMappingHandlerMapping,
                        requestMappingInfoConfiguration, sys, dataApiList);
                pageControllerRegister.regist();

                //动态注册delete控制类
                DeleteControllerRegister deleteControllerRegister = new DeleteControllerRegister(requestMappingHandlerMapping,
                        requestMappingInfoConfiguration, sys, dataApiList);
                deleteControllerRegister.regist();

                //动态注册update控制类
                UpdateControllerRegister updateControllerRegister = new UpdateControllerRegister(requestMappingHandlerMapping,
                        requestMappingInfoConfiguration, sys, dataApiList);
                updateControllerRegister.regist(dataApiProperties.getUpdateIgnoredColumn());

                //动态注册insert控制类
                InsertControllerRegister insertControllerRegister = new InsertControllerRegister(requestMappingHandlerMapping,
                        requestMappingInfoConfiguration, sys, dataApiList);
                insertControllerRegister.regist();

            }


            //动态注入mapper
            buildMybatisMapper();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getTableColumn() {
        for (SysTable table : sysTableList) {
            table.setTableConverter(tableConverter);
            List<SysTableColumn> columnList = dataApiService.selectColumnListByTableName(table.getRealTableName());

            int pkCount = 0;

            for (SysTableColumn column : columnList) {
                column.setTableConverter(tableConverter);

                if ("1".equals(column.getIsPk())) {
                    pkCount++;
                    table.setPkColumn(column);
                }
            }
            if (pkCount != 1) {//只支持单主键情况
                table.setPkColumn(null);
                table.setFirstColum(columnList.get(0));//因为没有主键，那么第一列就是第一列
            } else {
                table.setFirstColum(table.getPkColumn());//设置为主键为第一列
            }


            table.setColumnList(columnList);
        }
    }


    private void buildMybatisMapper() throws UnsupportedEncodingException {


        for (SysTable sysTable : sysTableList) {

            String namespace = "com.meide.register.mapper." + sysTable.getVoTableName() + "Mapper";
            StringXmlMapperUtil mapperUtil = new StringXmlMapperUtil(sysTable, namespace);
            String mapperXmlStr = mapperUtil.build();
            ByteArrayInputStream bis = new ByteArrayInputStream(mapperXmlStr.getBytes("utf-8"));
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(bis, configuration, namespace, configuration.getSqlFragments());
            xmlMapperBuilder.parse();

        }


    }


    public static ArrayList<DataApiInfo> getDataApiList() {

        return dataApiList;
    }
}
