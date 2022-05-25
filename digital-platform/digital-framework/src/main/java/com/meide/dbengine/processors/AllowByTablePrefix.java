package com.meide.dbengine.processors;

import com.meide.dbengine.tableinfo.TableInfo;
import com.meide.dbengine.tableinfo.TableInfos;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 根据设定的前缀，筛选可以通过的表。
 */
public class AllowByTablePrefix extends BaseProcessorChain<TableInfos> {
    List<String> ps = null;

    public AllowByTablePrefix(String[] prefixs) {
        ps = Arrays.asList(prefixs).stream().map(s -> s.toLowerCase()).collect(Collectors.toList());
    }

    @Override
    public Object doProcessor(TableInfos info) throws Exception {
        if (ps == null) {
            return super.doProcessor(info);
        }
        LinkedHashMap<String, TableInfo> newTables = new LinkedHashMap<>();
        for (TableInfo t : info.getTables().values()) {
            boolean matchPrefix = ps.stream().anyMatch(s -> t.getName().toLowerCase().startsWith(s));
            if (matchPrefix) {
                newTables.put(t.getName(), t);
            }
        }
        info.setTables(newTables);
        return super.doProcessor(info);
    }
}
