package com.meide.common.utils;

import cn.hutool.core.util.StrUtil;

public class TreeUtil {

    /**
     * 拼接祖先节点集合
     *
     * @param parentAncestors 父级祖先节点集合
     * @param parentId        父级id
     * @return 本节点祖先结合
     */
    public static String joinWithCommas(String parentAncestors, Long parentId) {
        return StrUtil.join(",", parentAncestors, parentId);
    }
}
