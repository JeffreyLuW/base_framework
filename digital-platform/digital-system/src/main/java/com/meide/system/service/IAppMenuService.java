package com.meide.system.service;

import com.meide.common.core.domain.TreeSelect;
import com.meide.common.core.domain.entity.AppMenu;
import com.meide.common.core.domain.entity.AppUserMenu;
import com.meide.system.domain.AppPostMenu;
import com.meide.system.domain.vo.AppMenuVo;

import java.util.List;

/**
 * APP菜单管理
 * @author jiay
 */
public interface IAppMenuService {

    /**
     * 查询app菜单
     *
     * @param appMenu
     * @return
     */
    List<AppMenu> selectMenuList(AppMenu appMenu);

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int insertMenu(AppMenu menu);

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int updateMenu(AppMenu menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int deleteMenuById(Long menuId);

    /**
     * 保存岗位菜单
     *
     * @param list
     * @return
     */
    int batchInsertPostMenu(List<AppPostMenu> list);

    /**
     * 删除岗位菜单
     *
     * @param postId
     * @return
     */
    int deletePostMenuByPostId(Long postId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    String checkMenuNameUnique(AppMenu menu);

    /**
     * 根据岗位id获取菜单
     *
     * @return
     */
    List<AppMenuVo> getUserMenuList();

    /**
     * 根据岗位获取菜单
     *
     * @param postId
     * @return
     */
    List<Integer> selectMenuListByPostId(Long postId);

    /**
     * 构建菜单树
     *
     * @param list
     * @return
     */
    List<TreeSelect> buildMenuTreeSelect(List<AppMenu> list);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean hasChildByMenuId(Long menuId);

    /**
     * 插入app用户菜单
     *
     * @param menuList
     * @return
     */
    int insertAppUserMenu(List<AppUserMenu> menuList);


}
