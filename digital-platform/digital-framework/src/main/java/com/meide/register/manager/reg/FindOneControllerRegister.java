package com.meide.register.manager.reg;

import com.meide.common.utils.spring.SpringUtils;
import com.meide.register.api.DataFindOneApi;
import com.meide.register.domain.DataApiInfo;
import com.meide.register.domain.SysTable;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class FindOneControllerRegister {

    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    private RequestMappingInfo.BuilderConfiguration requestMappingInfoConfiguration;
    private SysTable sysTable;
    private List<DataApiInfo> dataApiList;


    public FindOneControllerRegister(RequestMappingHandlerMapping requestMappingHandlerMapping,
                                  RequestMappingInfo.BuilderConfiguration requestMappingInfoConfiguration, SysTable sysTable
            , List<DataApiInfo> dataApiList) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.requestMappingInfoConfiguration = requestMappingInfoConfiguration;
        this.sysTable = sysTable;
        this.dataApiList = dataApiList;
    }

    /*
    动态注册，根据某列进行精确删除controller
    */
    public void regist() throws NoSuchMethodException {


        String url = "/dataApi/" + sysTable.getVoTableName() + "/findOne";


        //记录接口信息
        DataApiInfo info = new DataApiInfo();
        info.setTable(sysTable);

        info.setApiUrlReMark(sysTable.getVoTableName() + "表,findOne");
        info.setApiUrl(url);
        info.setApiType("findOne");
        dataApiList.add(info);


        //查询统一方法
        Method dataQueryApiMethod = DataFindOneApi.class.getDeclaredMethod("dataExecutor", HashMap.class);
        RequestMapping dataQueryMapping = AnnotatedElementUtils.findMergedAnnotation(dataQueryApiMethod, RequestMapping.class);
        DataFindOneApi dataListApi = new DataFindOneApi();
        dataListApi.setTableName(sysTable.getVoTableName());
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(url)
                .methods(dataQueryMapping.method())
                .params(dataQueryMapping.params())
                .headers(dataQueryMapping.headers())
                .consumes(dataQueryMapping.consumes())
                .produces(dataQueryMapping.produces())
                .mappingName(dataQueryMapping.name());
        builder.options(requestMappingInfoConfiguration);
        SpringUtils.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(dataListApi);
        this.requestMappingHandlerMapping.registerMapping(builder.build(), dataListApi, dataQueryApiMethod);


    }


}
