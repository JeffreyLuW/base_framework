package com.meide.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.meide.common.core.domain.TreeSelect;
import com.meide.common.core.domain.entity.SysArea;
import com.meide.common.core.domain.param.SysAreaParam;
import com.meide.common.utils.StringUtils;
import com.meide.system.mapper.SysAreaMapper;
import com.meide.system.service.ISysAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author jiay
 */
@Service("areaService")
public class ISysAreaServiceImpl implements ISysAreaService {

    @Autowired
    private SysAreaMapper sysAreaMapper;

    @Override
    public List<SysArea> selectAreaList(SysAreaParam area) {
        return sysAreaMapper.selectAreaList(area);
    }

    @Override
    public List<TreeSelect> buildAreaTreeSelect(List<SysArea> areas) {
        List<SysArea> deptTrees = buildAreaTree(areas);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据省市区县镇村的id,查询其中文名
     *
     * @param id
     */
    @Override
    public String getNameById(String id) {
        SysArea area = sysAreaMapper.selectOneById(id, getLevel(id));
        return Optional.ofNullable(area).map(SysArea::getName).orElse("");
    }

    /**
     * 根据id结构,判断级别
     */
    private int getLevel(String id) {
        if (StrUtil.isNotBlank(id)) {
            if (id.length() == 12) {
                if (id.endsWith("000")) {
                    //镇
                    return 4;
                } else {
                    //村
                    return 5;
                }
            } else if (id.length() == 6) {
                if (id.endsWith("0000")) {
                    //省
                    return 1;
                } else if (id.endsWith("00")) {
                    //市
                    return 2;
                } else {
                    //县
                    return 3;
                }
            }
        }
        return 0;
    }

    private List<SysArea> buildAreaTree(List<SysArea> areas) {
        List<SysArea> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<>();
        for (SysArea area : areas) {
            tempList.add(area.getCode());
        }
        for (Iterator<SysArea> iterator = areas.iterator(); iterator.hasNext(); ) {
            SysArea area = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(area.getParentCode())) {
                recursionFn(areas, area);
                returnList.add(area);
            }
        }
        if (returnList.isEmpty()) {
            returnList = areas;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysArea> list, SysArea t) {
        // 得到子节点列表
        List<SysArea> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysArea tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<SysArea> it = childList.iterator();
                while (it.hasNext()) {
                    SysArea n = it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysArea> getChildList(List<SysArea> list, SysArea t) {
        List<SysArea> tlist = new ArrayList<>();
        Iterator<SysArea> it = list.iterator();
        while (it.hasNext()) {
            SysArea n = it.next();
            if (StringUtils.isNotNull(n.getParentCode()) && n.getParentCode().longValue() == t.getCode().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysArea> list, SysArea t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }
}
