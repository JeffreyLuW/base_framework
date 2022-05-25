package com.meide.register.api;

import com.github.pagehelper.PageHelper;
import com.meide.common.core.controller.BaseController;
import com.meide.common.core.domain.PageResult;
import com.meide.common.core.page.PageDomain;
import com.meide.common.core.page.TableSupport;
import com.meide.common.utils.sql.SqlUtil;
import com.meide.register.domain.DataApiQueryBean;
import com.meide.register.service.BusTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

/**
 * 动态注册统一crud接口
 *
 * @author jiay
 */
public class DataListApi extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(DataListApi.class);

    private String tableName;

    @Autowired
    private BusTableService busTableService;

    @PostMapping("/dataQueryApi/{tableName}")
    @ResponseBody
    public PageResult dataExecutor(@RequestBody DataApiQueryBean dataApiBean) {


            dataApiBean.setTableName(getTableName());
            if (dataApiBean.getPageNum() == null && dataApiBean.getPageSize() == null) {

                PageDomain pageDomain = TableSupport.buildPageRequest();
                pageDomain.setPageNum(1);
                pageDomain.setPageSize(10);

                if (dataApiBean.getOrderByColumn() != null) {
                    pageDomain.setOrderByColumn(dataApiBean.getOrderByColumn());

                    if (dataApiBean.getIsAsc() != null) {
                        if ("asc".equals(dataApiBean.getIsAsc())) {
                            pageDomain.setIsAsc("asc");
                        } else if ("desc".equals(dataApiBean.getIsAsc())) {
                            pageDomain.setIsAsc("desc");
                        } else {
                            pageDomain.setIsAsc("asc");
                        }
                    }
                }

                String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
                PageHelper.startPage(pageDomain.getPageNum(), pageDomain.getPageSize(), orderBy);
            }

            List<HashMap> list = busTableService.selectApi(dataApiBean);

            //return AjaxResult.success("查询成功", list);
            return page(list);

    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
