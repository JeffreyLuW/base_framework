package com.meide.register.manager.build;

import com.meide.register.domain.SysTable;
import com.meide.register.manager.build.util.DeleteUtil;
import com.meide.register.manager.build.util.InsertUtil;
import com.meide.register.manager.build.util.SelectUtil;
import com.meide.register.manager.build.util.UpdateUtil;

public class StringXmlMapperUtil {
    private SysTable sysTable;

    private String namespace;


    public StringXmlMapperUtil(SysTable sysTable, String namespace) {

        this.sysTable = sysTable;
        this.namespace = namespace;

    }


    public String build() {

        //<?xml version="1.0" encoding="UTF-8" ?>
        String xmlStr = buildMapperHeader();
        //组装select mapper语句  <select id="selectApi" parameterType="Map" resultType="Map">
        SelectUtil selectUtil=new SelectUtil(sysTable);
        xmlStr+=selectUtil.buildMapperStr();

        //只支持单主键删除
        if( sysTable.getPkColumn()!=null ){
            //组装delete mapper语句  <select id="deleteApi" parameterType="Map" resultType="Map">
            DeleteUtil deleteUtil=new DeleteUtil(sysTable);
            xmlStr+=deleteUtil.buildMapperStr();


            UpdateUtil updateUtil=new UpdateUtil(sysTable);
            xmlStr+=updateUtil.buildMapperStr();

        }

        InsertUtil insertUtil=new InsertUtil(sysTable);
        xmlStr+=insertUtil.buildMapperStr();



        //</mapper>
        xmlStr += buildMapperFooter();

        return xmlStr;
    }


    private String buildMapperHeader() {
        String mapperSql = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> " +
                "<!DOCTYPE mapper " +
                "PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" " +
                "\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\"> " +
                "<mapper namespace=\"" + namespace + "\"> " ;
        return mapperSql;
    }

    private String buildMapperFooter() {
        String mapperSql =    "</mapper> ";
        return mapperSql;
    }


}
