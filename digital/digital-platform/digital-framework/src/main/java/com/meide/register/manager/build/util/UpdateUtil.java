package com.meide.register.manager.build.util;

import com.meide.register.domain.SysTable;
import com.meide.register.domain.SysTableColumn;

public class UpdateUtil {
    private SysTable sysTable;

    public UpdateUtil(SysTable sysTable) {

        this.sysTable = sysTable;

    }

    public String buildMapperStr() {


        String mapperSql =
                "    <update id=\"updateApi\" parameterType=\"Map\" > \n " +

                        "   update  " + sysTable.getRealTableName() + "   set " + sysTable.getPkColumn().getRealColumnName() + "=  #{" + sysTable.getPkColumn().getVoColumnName() + "}   \n";

        mapperSql += buildMapperColumn();
        mapperSql += "  where     " + sysTable.getPkColumn().getRealColumnName() + " = #{" + sysTable.getPkColumn().getVoColumnName() + "}     </update>    ";


        mapperSql += " <update id=\"updateMaxApi\" parameterType=\"Map\" > \n ";

        mapperSql += "   update  " + sysTable.getRealTableName() + "   set " + sysTable.getPkColumn().getRealColumnName() + "=  #{" + sysTable.getPkColumn().getVoColumnName() + "}   \n";


        mapperSql += buildMapperMaxColumn();


        mapperSql += "  where     " + sysTable.getPkColumn().getRealColumnName() + " = #{" + sysTable.getPkColumn().getVoColumnName() + "}     </update>    ";


        return mapperSql;
    }


    private String buildMapperColumn() {
        String mapperSql = "";
        for (SysTableColumn column : sysTable.getColumnList()) {

            if (column.getRealColumnName().equals(sysTable.getPkColumn().getRealColumnName())) {
                continue;
            }

            mapperSql += "  <if   test=\"" + column.getVoColumnName() + " != null \"   > \n  " +
                    "     , " + column.getRealColumnName() + " =  #{" + column.getVoColumnName() + "}  \n" +
                    "      </if> \n";
        }


        return mapperSql;
    }


    private String buildMapperMaxColumn() {
        String mapperSql = "";
        for (SysTableColumn column : sysTable.getColumnList()) {

            if (column.getRealColumnName().equals(sysTable.getPkColumn().getRealColumnName())) {
                continue;
            }
            //当不包含时候，直接全量更新    excludeColumn=",configName,configKey,configValue,"   前后加上英文逗号,方便mapper判断是否存在
            //1.当前段指定更新 2.前段未指定但是也没在excludeColumn中   这两种情况就更新该字段。第1种情况是正常更新页面传值，第2种情况更新为空
            mapperSql += "  <if   test=\"   "+ column.getVoColumnName() +  " != null  or ( excludeColumn!= null  and  excludeColumn.indexOf(',"+column.getVoColumnName()+",') ==-1 )  \"   > \n  ";
            mapperSql += "     , " + column.getRealColumnName() + " =  #{" + column.getVoColumnName() + "}  \n";
            mapperSql += "      </if> \n";
        }


        return mapperSql;
    }

}
