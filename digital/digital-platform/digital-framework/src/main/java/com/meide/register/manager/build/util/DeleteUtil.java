package com.meide.register.manager.build.util;

import com.meide.register.domain.SysTable;

public class DeleteUtil {
    private SysTable sysTable;

    public DeleteUtil(SysTable sysTable) {

        this.sysTable = sysTable;

    }

    public String buildMapperStr() {

        String mapperSql =
                "   <delete id=\"deleteApi\" parameterType=\"Map\"    > \n " +
                        "    delete from  " + sysTable.getRealTableName()
                        + "  where  " + sysTable.getPkColumn().getRealColumnName()
                        + "   = #{id}   </delete>    \n ";

        return mapperSql;
    }


}
