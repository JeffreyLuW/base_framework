package com.meide.register.manager.reg;

import com.meide.common.utils.spring.SpringUtils;
import com.meide.register.api.DataInsertApi;
import com.meide.register.domain.DataApiInfo;
import com.meide.register.domain.SysTable;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class InsertControllerRegister {

    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    private RequestMappingInfo.BuilderConfiguration requestMappingInfoConfiguration;
    private SysTable sysTable;
    private List<DataApiInfo> dataApiList;

    public InsertControllerRegister(RequestMappingHandlerMapping requestMappingHandlerMapping,
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

        String url = "/dataApi/" + sysTable.getVoTableName() + "/insert";


        //记录接口信息
        DataApiInfo info = new DataApiInfo();
        info.setTable(sysTable);

        info.setApiUrlReMark(sysTable.getVoTableName() + "表,insert");
        info.setApiUrl(url);
        info.setApiType("insert");
        dataApiList.add(info);


        Method dataDeleteApiMethod = DataInsertApi.class.getDeclaredMethod("dataExecutor", HashMap.class);
        RequestMapping dataDeletMapping = AnnotatedElementUtils.findMergedAnnotation(dataDeleteApiMethod, RequestMapping.class);
        DataInsertApi dataInsertApi = new DataInsertApi();
        dataInsertApi.setTableName(sysTable.getVoTableName());
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(url)
                .methods(dataDeletMapping.method())
                .params(dataDeletMapping.params())
                .headers(dataDeletMapping.headers())
                .consumes(dataDeletMapping.consumes())
                .produces(dataDeletMapping.produces())
                .mappingName(dataDeletMapping.name());
        builder.options(requestMappingInfoConfiguration);
        SpringUtils.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(dataInsertApi);
        this.requestMappingHandlerMapping.registerMapping(builder.build(), dataInsertApi, dataDeleteApiMethod);

    }

}
