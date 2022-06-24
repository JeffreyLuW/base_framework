package com.meide.dbengine;

import com.meide.dbengine.processors.AbstractChainRunner;
import com.meide.dbengine.processors.BaseProcessorChain;
import com.meide.dbengine.runninginfo.RunningInfo;

/**
 * 用于执行数据库mapper-sql
 * 通过 {@link DbChainRunner#addChain(BaseProcessorChain)} 添加额外的执行链。
 * 预处理参数、处理结果、拦截等。
 *
 * @date 2022-1-6
 */
public class DbChainRunner extends AbstractChainRunner<RunningInfo> {

    @Override
    public Object doInnerProcessor(RunningInfo runningInfo) throws Exception {
        return runningInfo.getBaseApi().apiCall(runningInfo);
    }

}
