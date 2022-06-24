package com.meide.register.api;

import com.meide.common.core.domain.AjaxResult;
import com.meide.register.service.BusTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * 动态注册统一crud接口
 *
 * @author jiay
 */
public class DataUpdateApi {
    private static final Logger log = LoggerFactory.getLogger(DataUpdateApi.class);
    private String tableName;
    //更新操作中,开启全部更新max时,updateIgnoredColumn存在的字段默认不进行更新，前段指定时才更新updateIgnoredColumn中的字段
    private String updateIgnoredColumn;

    @Autowired
    private BusTableService busTableService;

    @PostMapping("/dataApiUpdate/{tableName}")
    @ResponseBody
    public AjaxResult dataExecutor(@RequestBody HashMap params) {

        try {
            params.put("tableNmae", tableName);
            int deleteSize = 0;
            String updateType = params.get("updateType") + "";
            if ("min".equals(updateType)) {
                deleteSize = busTableService.updateApi(params);
            } else if ("max".equals(updateType)) {

                String excludeColumn = params.get("excludeColumn") == null ? "" : params.get("excludeColumn") + "";
                if (!excludeColumn.startsWith(",")) {
                    excludeColumn = "," + excludeColumn;
                }
                if (!excludeColumn.endsWith(",")) {
                    excludeColumn = excludeColumn + ",";
                }
                // excludeColumn=",configName,configKey,configValue,"   方便mapper判断是否存在
                params.put("excludeColumn",updateIgnoredColumn+","+ excludeColumn);
                deleteSize = busTableService.updateMaxApi(params);
            } else {
                return AjaxResult.errorWithMsg("请选择更新类型updateType :min 或者 max ");
            }

            return AjaxResult.success("count", deleteSize);

        } catch (Exception e) {
            log.info("update接口错误：{}", e.getMessage());
            return AjaxResult.errorWithMsg("update接口错误");
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getUpdateIgnoredColumn() {
        return updateIgnoredColumn;
    }

    public void setUpdateIgnoredColumn(String updateIgnoredColumn) {
        this.updateIgnoredColumn = updateIgnoredColumn;
    }
}
