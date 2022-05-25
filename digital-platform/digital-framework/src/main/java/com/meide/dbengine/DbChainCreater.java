package com.meide.dbengine;

import com.meide.dbengine.processors.AbstractChainRunner;
import com.meide.dbengine.processors.BaseProcessorChain;
import com.meide.dbengine.tableinfo.TableInfos;

/**
 * 用于创建数据库的基本表列信息
 * 通过 {@link DbChainCreater#addChain(BaseProcessorChain)} 添加额外的执行链。
 * 预处理参数、处理结果、拦截等。
 *
 * @date 2022-1-6
 */
public class DbChainCreater extends AbstractChainRunner<TableInfos> {

    @Override
    public TableInfos doInnerProcessor(TableInfos tableInfos) throws Exception {
        return tableInfos;
    }
}
