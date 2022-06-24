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
public class DataInsertApi {
    private static final Logger log = LoggerFactory.getLogger(DataInsertApi.class);

    private String tableName;

    @Autowired
    private BusTableService busTableService;

    @PostMapping("/dataApiInsert/{tableName}")
    @ResponseBody
    public AjaxResult dataExecutor(@RequestBody HashMap params) {
        params.put("tableNmae", tableName);
        try {
            int insertSize = busTableService.insertApi(params);
            return AjaxResult.success("count", insertSize);
        } catch (Exception e) {
            log.info("insert接口错误：{}",e.getMessage());
            return AjaxResult.errorWithMsg("insert接口错误" );
        }

    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
