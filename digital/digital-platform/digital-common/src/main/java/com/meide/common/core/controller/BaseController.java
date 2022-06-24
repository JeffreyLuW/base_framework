package com.meide.common.core.controller;

import com.meide.common.constant.HttpStatus;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.core.domain.PageResult;
import com.meide.common.core.page.PageDomain;
import com.meide.common.core.page.TableSupport;
import com.meide.common.utils.DateUtils;
import com.meide.common.utils.StringUtils;
import com.meide.common.utils.sql.SqlUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 *
 * @author jiay
 */
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(BaseController.class);

    private Page page;

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            page = PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> PageResult<T> page(List<T> list) {
        PageResult rspData = new PageResult();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        if (null != page) {
            rspData.setTotal(page.getTotal());
        } else {
            rspData.setTotal(new PageInfo(list).getTotal());
        }
        return rspData;
    }
    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> PageResult<T> page(List<T> list,Object data)
    {
        PageResult rspData = new PageResult();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        rspData.setData(data);
        return rspData;
    }

    /**
     * 响应操作行数
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult<Integer> rows(int rows)
    {
        return rows > 0 ? AjaxResult.success(rows) : AjaxResult.error();
    }

    /**
     * 操作成功
     */
    @SuppressWarnings({ "rawtypes"})
    protected AjaxResult success(){
        return AjaxResult.success();
    }



    /**
     * 页面跳转
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }
}
