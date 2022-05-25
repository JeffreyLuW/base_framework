package com.meide.system.mapper;

import com.meide.system.domain.AppPostMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * APP岗位菜单数据访问接口
 * @author jiay
 */
@Repository
public interface AppPostMenuMapper {
    /**
     * 通过岗位ID删除岗位和菜单关联
     *
     * @param postId 角色ID
     * @return 结果
     */
    int deletePostMenuByPostId(Long postId);

    /**
     * 批量新增岗位菜单信息
     *
     * @param postMenuList 岗位菜单列表
     * @return 结果
     */
    int batchPostMenu(List<AppPostMenu> postMenuList);

    /**
     * 计算数量
     *
     * @param postMenu
     * @return
     */
    int count(AppPostMenu postMenu);
}
