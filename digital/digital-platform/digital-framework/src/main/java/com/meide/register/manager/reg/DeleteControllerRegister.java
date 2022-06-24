package com.meide.register.manager.reg;

import com.meide.common.utils.spring.SpringUtils;
import com.meide.register.api.DataDeleteApi;
import com.meide.register.domain.DataApiInfo;
import com.meide.register.domain.SysTable;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class DeleteControllerRegister {

    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    private RequestMappingInfo.BuilderConfiguration requestMappingInfoConfiguration;
    private SysTable sysTable;
    private List<DataApiInfo> dataApiList;

    public DeleteControllerRegister(RequestMappingHandlerMapping requestMappingHandlerMapping,
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


        if (sysTable.getPkColumn()==null) {
            //只支持根据单主键进行删除
            return;
        }


        String url = "/dataApi/" + sysTable.getVoTableName() + "/delete";

        //记录接口信息
        DataApiInfo info = new DataApiInfo();
        info.setTable(sysTable);

        info.setApiUrlReMark(sysTable.getVoTableName() + "表,delete");
        info.setApiUrl(url);
        info.setApiType("delete");
        dataApiList.add(info);


        Method dataDeleteApiMethod = DataDeleteApi.class.getDeclaredMethod("dataExecutor", HashMap.class);
        RequestMapping dataDeletMapping = AnnotatedElementUtils.findMergedAnnotation(dataDeleteApiMethod, RequestMapping.class);
        DataDeleteApi dataDeleteApi = new DataDeleteApi();
        dataDeleteApi.setTableName(sysTable.getVoTableName());
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(url)
                .methods(dataDeletMapping.method())
                .params(dataDeletMapping.params())
                .headers(dataDeletMapping.headers())
                .consumes(dataDeletMapping.consumes())
                .produces(dataDeletMapping.produces())
                .mappingName(dataDeletMapping.name());
        builder.options(requestMappingInfoConfiguration);
        SpringUtils.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(dataDeleteApi);
        this.requestMappingHandlerMapping.registerMapping(builder.build(), dataDeleteApi, dataDeleteApiMethod);

    }

}
