package com.meide.register.manager.build.util;

import com.meide.register.domain.SysTable;
import com.meide.register.domain.SysTableColumn;

public class SelectUtil {
    private  SysTable  sysTable;

    public SelectUtil(SysTable sysTable) {

        this.sysTable = sysTable;

    }

    public String buildMapperStr() {

        String mapperSql =
                "    <select id=\"selectApi\" parameterType=\"Map\" resultType=\"Map\"> \n " +
                        "       SELECT  *   FROM   "+sysTable.getRealTableName()+"  where  <choose>   \n";

        mapperSql += buildMapperColumn();
        mapperSql += buildMapperEndStr();


          mapperSql +=
                "    <select id=\"selectOneApi\" parameterType=\"Map\" resultType=\"Map\"> \n " +
                        "       SELECT  *   FROM   "+sysTable.getRealTableName()+"  where  "+sysTable.getFirstColum().getRealColumnName()+" = #{columnValue}  </select>   \n";


        mapperSql +=
                "    <select id=\"selectAllApi\" parameterType=\"Map\" resultType=\"Map\"> \n " +
                        "       SELECT  *   FROM   "+sysTable.getRealTableName()+"   </select>   \n";


        return mapperSql;
    }



    private String buildMapperColumn() {
        String mapperSql = "";

        if(sysTable.getTableName().equals("sys_dict_data")){
            System.out.println();
        }

            for (int i = 0; i < sysTable.getColumnList().size() && i < 4; i++) {
                SysTableColumn column = sysTable.getColumnList().get(i);

                String utilStr = "  columnName != null and columnName != ''   and  columnName == '" + column.getVoColumnName() + "'   " +
                        "and  selectType  != null  and selectType != ''       ";

                mapperSql += "  <when   test=\" " + utilStr + "   and  selectType ==  'equalSign'    \"   > \n  " +
                        "                " + column.getRealColumnName() + " =  #{columnValue}  \n" +
                        "         </when> \n";

                mapperSql += "  <when   test=\" " + utilStr + "   and  selectType ==  'bothLike'    \"   > \n  " +
                        "                " + column.getRealColumnName() + "  like concat('%', #{columnValue}, '%')    \n" +
                        "         </when> \n";


                mapperSql += "  <when   test=\" " + utilStr + "   and  selectType ==  'leftLike'    \"   > \n  " +
                        "                " + column.getRealColumnName() + "  like concat('%', #{columnValue}, '')    \n" +
                        "         </when> \n";


                mapperSql += "  <when   test=\" " + utilStr + "   and  selectType ==  'rightLike'    \"   > \n  " +
                        "                " + column.getRealColumnName() + "  like concat('', #{columnValue}, '%')    \n" +
                        "         </when> \n";


            }


        return mapperSql;
    }


    private String buildMapperEndStr() {

        String mapperSql = "  <otherwise> " +
                "                 1 <![CDATA[   <>    ]]>  1 " +
                "            </otherwise> " +
                "        </choose> " +
                "    </select>  ";

        return mapperSql;
    }


}
