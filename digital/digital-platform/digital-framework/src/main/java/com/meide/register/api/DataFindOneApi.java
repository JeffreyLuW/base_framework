package com.meide.register.api;

import com.meide.common.core.controller.BaseController;
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
public class DataFindOneApi extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(DataFindOneApi.class);
    private String tableName;

    @Autowired
    private BusTableService busTableService;

    @PostMapping("/dataQueryApi/{tableName}")
    @ResponseBody
    public AjaxResult dataExecutor(@RequestBody HashMap params) {
        params.put("tableNmae", tableName);
        try {
             HashMap map = busTableService.selectOneApi(params);
            return AjaxResult.success(  map);
        } catch (Exception e) {
            log.info("findOne接口错误：{}", e.getMessage());
            return AjaxResult.errorWithMsg("findOne接口错误");
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
