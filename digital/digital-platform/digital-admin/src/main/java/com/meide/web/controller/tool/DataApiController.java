package com.meide.web.controller.tool;

import com.meide.common.constant.HttpStatus;
import com.meide.common.core.controller.BaseController;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.core.page.PageDomain;
import com.meide.common.core.domain.PageResult;
import com.meide.common.core.page.TableSupport;
import com.meide.register.domain.DataApiInfo;
import com.meide.register.manager.DataApiManager;
import com.meide.register.manager.remark.ApiRemark;
import com.github.pagehelper.PageInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数数据接口 信息操作处理
 *
 * @author jiay
 */
@RestController
@RequestMapping("/tool/dataapi")
public class DataApiController extends BaseController {

    /**
     * 获取数据接口列表
     */
    @PreAuthorize("@ss.hasPermi('tool:dataapi:list')")
    @GetMapping("/list")
    public PageResult list(DataApiInfo info) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        List<DataApiInfo> getlist = DataApiManager.getDataApiList();

        ArrayList<DataApiInfo> returnList =     new ArrayList<DataApiInfo>( );

        for (DataApiInfo dbInfo:getlist ) {
            if( !"".equals( info.getTableName())&& info.getTableName()!=null){
                if(info.getTableName().contains(dbInfo.getTable().getRealTableName())){
                    returnList.add(dbInfo);
                }
            }else {
                returnList.add(dbInfo);
            }
        }


        int start=0;
        int end=0;
        if(pageNum*pageSize >returnList.size()){
            end=returnList.size() ;
        }else{
            end=pageNum*pageSize;
        }
        start=pageNum*pageSize-pageSize;
        if(start<0){
            start=0;
        }

        ApiRemark remark=new ApiRemark(returnList);
        remark.createrRemark();



        PageResult rspData = new PageResult();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(returnList.subList(start ,end));
        rspData.setTotal(new PageInfo(returnList).getTotal());
        return rspData;

    }




    @PreAuthorize("@ss.hasPermi('tool:dataapi:query')")
    @GetMapping(value = "/{tableName}")
    public AjaxResult getInfo(@PathVariable String tableName)
    {
        List<DataApiInfo> getlist = DataApiManager.getDataApiList();
        for (DataApiInfo dbInfo:getlist ) {
              if(dbInfo.getTableName().equals(tableName)){
                  return  AjaxResult.success(dbInfo);
              }
        }
        return AjaxResult.errorWithMsg("查询接口'" + tableName+ "'失败，接口名已失效");

    }


}
