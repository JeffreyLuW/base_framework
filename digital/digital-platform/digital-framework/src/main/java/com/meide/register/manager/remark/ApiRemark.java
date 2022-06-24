package com.meide.register.manager.remark;

import com.meide.register.domain.DataApiInfo;
import com.meide.register.domain.SysTable;
import com.meide.register.domain.SysTableColumn;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ApiRemark {

    private ArrayList<DataApiInfo> dataApiList;

    public ApiRemark(ArrayList<DataApiInfo> dataApiList) {
        this.dataApiList = dataApiList;
    }


    public void createrRemark() {
        paresSelectRemark();
        paresDeleteRemark();
        paresUpdateRemark();
        paresInsertRemark();
    }

    public void paresSelectRemark() {
        for (DataApiInfo info : dataApiList) {

            if (!"select".equals(info.getApiType())) {
                continue;
            }


            SysTable table = info.getTable();
            LinkedHashMap params = new LinkedHashMap();
            info.setParams(params);


            String param = "";
            for (int i = 0; i < table.getColumnList().size() && i < 4; i++) {
                SysTableColumn column = table.getColumnList().get(i);

                param += column.getVoColumnName() + " | ";


            }
            if (param.endsWith(" | ")) {
                param = param.substring(0, param.length() - 3);
            }
            LinkedHashMap remark = new LinkedHashMap();
            remark.put("selectType", "表示查询类型");
            remark.put("columnName",  info.getTable().getRealTableName()  + "表中允许查询的列");
            remark.put("columnValue", "本次查询条件具体数值");
            remark.put("pageNum", "当前记录起始索引");
            remark.put("pageSize", "每页显示记录数");
            remark.put("orderByColumn", "排序列");
            remark.put("isAsc", "排序的方向desc或者asc");


            params.put("参数说明", remark);

            LinkedHashMap selectType = new LinkedHashMap();
            selectType.put("equalSign", "where columnName= columnValue");
            selectType.put("bothLike", "where columnName like '%columnValue%'");
            selectType.put("leftLike", "where columnName like '%columnValue'");
            selectType.put("rightLike", "where columnName like 'columnValue%'");

            params.put("单选必填:selectType", selectType);
            params.put("单选必填:columnName", param);
            params.put("必填:columnValue", "查询具体数值");

            params.put("非必填:pageNum", "触发分页,与pageSize同时才生效");
            params.put("非必填:pageSize", "触发分页,与pageNum同时才生效");
            params.put("非必填(分页开启后生效)orderByColumn", param);
            params.put("非必填(分页开启后生效)isAsc", "desc | asc");


            LinkedHashMap demo1 = new LinkedHashMap();

            SysTableColumn  frist=table.getColumnList().get(0);


            params.put("示例1", demo1);
            demo1.put("selectType", "equalSign");
            demo1.put("columnName", frist .getVoColumnName());
            demo1.put("columnValue", "001");
            demo1.put("执行sql", "  select  * from    " +  info.getTable().getRealTableName()  + " where    " +
                    table.getColumnList().get(0).getRealColumnName()  + " = '001' ");


            LinkedHashMap demo2 = new LinkedHashMap();

            params.put("示例2", demo2);
            demo2.put("selectType", "bothLike");
            demo2.put("columnName", frist.getVoColumnName() );
            demo2.put("columnValue", "001");
            demo2.put("执行sql", "  select  * from    " +  info.getTable().getRealTableName()  + " where    " + frist.getRealColumnName()  + " like '%001%' ");


            LinkedHashMap demo3 = new LinkedHashMap();

            params.put("示例3", demo3);
            demo3.put("selectType", "leftLike");
            demo3.put("columnName", frist.getVoColumnName());
            demo3.put("columnValue", "001");
            demo3.put("执行sql", "  select  * from    " +  info.getTable().getRealTableName()  + " where    " + frist.getRealColumnName()  + " like '%001' ");


            LinkedHashMap demo4 = new LinkedHashMap();

            params.put("示例4", demo4);
            demo4.put("selectType", "rightLike");
            demo4.put("columnName", frist.getVoColumnName());
            demo4.put("columnValue", "001");
            demo4.put("执行sql", "  select  * from    " +  info.getTable().getRealTableName()  + " where    " +  frist.getRealColumnName()  + " like '001%' ");

        }

    }


    public void paresDeleteRemark() {

        for (DataApiInfo info : dataApiList) {

            if (!"delete".equals(info.getApiType())) {
                continue;
            }


            SysTable table = info.getTable();
            LinkedHashMap params = new LinkedHashMap();
            info.setParams(params);


            LinkedHashMap remark = new LinkedHashMap();
            remark.put("必填:columnValue", "主键" + table.getPkColumn().getVoColumnName() + "具体数值");

            params.put("参数说明", remark);

            LinkedHashMap demo1 = new LinkedHashMap();

            params.put("示例1", demo1);
            demo1.put("columnValue", "100001");
            demo1.put("执行sql", "  delete from    " +  info.getTable().getRealTableName()  + " where    " + table.getPkColumn().getRealColumnName() + " = '100001' ");


        }


    }


    public void paresUpdateRemark() {

        for (DataApiInfo info : dataApiList) {

            if (!"update".equals(info.getApiType())) {
                continue;
            }


            SysTable table = info.getTable();
            LinkedHashMap params = new LinkedHashMap();
            info.setParams(params);


            LinkedHashMap remark = new LinkedHashMap();


            LinkedHashMap updateType = new LinkedHashMap();
            updateType.put("min","部分更新：只更新本次请求中指定的字段，未指定的字段不更新");

            updateType.put("max","全量更新：除excludeColumn以外的字段，全部更新，请求json中未出现的字段，默认更新为空值");


            remark.put("必选:updateType" ,updateType);

            remark.put("excludeColumn" ,"updateType为max全量更新时必填,排除excludeColumn中的字段,不对其进行更新。需要排除多个字段,请用 英文逗号 隔开。");



            remark.put("必填:" + table.getPkColumn().getVoColumnName() , "主键" + table.getPkColumn().getVoColumnName()  + "具体数值");


            for (SysTableColumn column : table.getColumnList()) {

                if (column.getVoColumnName().equals(table.getPkColumn().getVoColumnName() )) {
                    continue;
                }

                remark.put("选填:" + column.getVoColumnName(), "" + column.getColumnComment() + "");
            }





            params.put("参数说明", remark);

            LinkedHashMap demo1 = new LinkedHashMap();

            params.put("示例1", demo1);

            demo1.put("updateType" , "min");

            demo1.put(table.getPkColumn().getVoColumnName() , "100001");

            int  i=0;

            for (SysTableColumn column : table.getColumnList()) {
                demo1.put(column.getVoColumnName(), column.getVoColumnName());
                if(i++>2){
                    break;
                }
            }


            String update = "  update    " +  info.getTable().getRealTableName()  +

                    "  set " + table.getPkColumn().getRealColumnName()  + "=  #{" + table.getPkColumn().getVoColumnName()  + "} ";
            i=0;
            for (SysTableColumn column : table.getColumnList()) {

                if(i++>3){
                    break;
                }

                if (column.getVoColumnName().equals(table.getPkColumn().getVoColumnName() )) {
                    continue;
                }

                update += "," + column.getRealColumnName() + " =  #{" + column.getVoColumnName() + "}  ";
            }


            update += " where    " + table.getPkColumn().getRealColumnName()  + " = '100001' ";


            demo1.put("执行sql", update);



            LinkedHashMap demo2 = new LinkedHashMap();
            params.put("示例2", demo2);
            demo2.put("updateType" , "max");



            String  excludeColumn="";
            for ( int  j=0; j< table.getColumnList().size()&&j<4  ;j++   ) {
                SysTableColumn column=table.getColumnList().get(j);
                if (column.getVoColumnName().equals(table.getPkColumn().getVoColumnName() )) {
                    continue;
                }
                excludeColumn +=  column.getVoColumnName()+ ","  ;
            }
            if(excludeColumn.endsWith(",")){
                excludeColumn=excludeColumn.substring(0,excludeColumn.length()-1);
            }

            demo2.put("excludeColumn" , excludeColumn);

            demo2.put(table.getPkColumn().getVoColumnName() , "200002");


            update = "  update    " +  info.getTable().getRealTableName()  +
                    "  set " + table.getPkColumn().getRealColumnName()  + "=  #{" + table.getPkColumn().getVoColumnName()  + "} ";

            for ( int  j=0; j< table.getColumnList().size()  ;j++   ) {
                if(j>3){
                    SysTableColumn column=table.getColumnList().get(j);
                    update += "," + column.getRealColumnName() + " =  #{" + column.getVoColumnName() + "}  ";
                }
            }
            update += " where    " + table.getPkColumn().getRealColumnName()  + " = '200002' ";
            demo2.put("执行sql", update);




        }


    }


    public void paresInsertRemark() {

        for (DataApiInfo info : dataApiList) {

            if (!"insert".equals(info.getApiType())) {
                continue;
            }


            SysTable table = info.getTable();
            LinkedHashMap params = new LinkedHashMap();
            info.setParams(params);

            LinkedHashMap remark = new LinkedHashMap();

            for (SysTableColumn column : table.getColumnList()) {

                String  isNullable="";
                if("NO".equals(column.getIsNullable())){
                    isNullable="必填";
                }else {
                    isNullable="选填";
                }

                remark.put(isNullable+ column.getVoColumnName(),  column.getColumnComment()  );
            }


            params.put("参数说明", remark);

            LinkedHashMap demo1 = new LinkedHashMap();

            params.put("示例", demo1);


            for (SysTableColumn column : table.getColumnList()) {
                demo1.put(column.getVoColumnName(), column.getVoColumnName());
            }


            String insert = "  insert into   " + info.getTable().getRealTableName() +
                    "  ( ";

            boolean isFrist = true;

            for (SysTableColumn column : table.getColumnList()) {
                if (isFrist) {
                    insert += column.getRealColumnName();
                    isFrist = false;
                } else {
                    insert += "," + column.getRealColumnName();
                }
            }

            insert += "  ) values ( ";
            isFrist = true;
            for (SysTableColumn column : table.getColumnList()) {

                if (isFrist) {
                    insert += "#{" + column.getVoColumnName() + "}  ";
                    isFrist = false;
                } else {
                    insert += ", #{" + column.getVoColumnName() + "}  ";
                }
            }

            insert += "  ) ";


            demo1.put("执行sql", insert);


        }


    }

}
