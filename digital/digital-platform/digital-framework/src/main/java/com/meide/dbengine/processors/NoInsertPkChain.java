package com.meide.dbengine.processors;

import com.meide.dbengine.api.BaseApi;
import com.meide.dbengine.api.InsertApi;
import com.meide.dbengine.api.InsertSelectiveApi;
import com.meide.dbengine.tableinfo.TableInfos;

/**
 * 遍历api，插入类api，移除首列是主键的列。
 */
public class NoInsertPkChain extends BaseProcessorChain<TableInfos> {
    @Override
    public Object doProcessor(TableInfos info) throws Exception {
        for (BaseApi api : info.getApiList()) {
            if (api instanceof InsertApi || api instanceof InsertSelectiveApi) {
                //插入 首列如果是主键，移除掉。
                if (api.getCols() != null && api.getCols().get(0) != null && api.getCols().get(0).isPk()) {
                    api.getCols().remove(0);
                }
            }
        }
        return super.doProcessor(info);
    }
}
