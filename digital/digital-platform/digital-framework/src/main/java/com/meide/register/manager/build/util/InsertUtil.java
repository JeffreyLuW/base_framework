package com.meide.register.manager.build.util;

import com.meide.register.domain.SysTable;
import com.meide.register.domain.SysTableColumn;

public class InsertUtil {
    private SysTable sysTable;

    public InsertUtil(SysTable sysTable) {
        this.sysTable = sysTable;
    }

    public String buildMapperStr() {

        String  columnName="";
        String  columnValue="";


        boolean isFrist = true;
        for (SysTableColumn column : sysTable.getColumnList()) {

            columnName += "  <if   test=\"" + column.getVoColumnName() + " != null \"   > \n  " ;
            columnValue += "  <if   test=\"" + column.getVoColumnName() + " != null \"   > \n  " ;
            if (isFrist) {
                columnName += " " + column.getRealColumnName() + " ";
                columnValue +=   " #{" + column.getVoColumnName() + "}  ";
                isFrist = false;
            } else {
                columnName += " , " + column.getRealColumnName() + " ";
                columnValue += ",  #{" + column.getVoColumnName() + "}  ";
            }
            columnName += "      </if> \n";
            columnValue += "      </if> \n";
        }

        String mapperSql =
                "    <insert id=\"insertApi\" parameterType=\"Map\" > \n " +
                        "   insert  into  " + sysTable.getRealTableName() + " ( \n"+

                 columnName

               +") values ( "+
                   columnValue
           +  " ) </insert> ";
        return mapperSql;
    }


}
