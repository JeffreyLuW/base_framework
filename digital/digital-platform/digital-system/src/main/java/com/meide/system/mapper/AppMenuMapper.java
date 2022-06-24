package com.meide.system.mapper;

import com.meide.common.core.domain.entity.AppMenu;
import com.meide.common.core.domain.entity.AppUserMenu;
import com.meide.system.domain.vo.AppMenuVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * APP菜单数据访问接口
 * @author jiay
 */
@Repository
public interface AppMenuMapper {

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<AppMenu> selectMenuList(AppMenu menu);

    /**
     * 获取APP用户菜单
     *
     * @return
     */
    List<AppMenu> selectMenuListByUserId(Long userId);

    /**
     * 查询APP用户默认的我的菜单
     *
     * @param userId
     * @return
     */
    List<AppMenu> selectUserDefaultMenuListByUserId(Long userId);

    /**
     * 查询用户常用菜单
     *
     * @param userId
     * @return
     */
    List<AppMenu> selectUserFavoriteMenuList(Long userId);

    /**
     * 根据父级id查询菜单
     *
     * @param parentId
     * @return
     */
    List<AppMenuVo> selectMenuListByParentId(Long parentId);

    /**
     * 根据父级id和用户id查询菜单
     *
     * @param parentId
     * @return
     */
    List<AppMenuVo> selectUserMenuListByParentId(Long parentId);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    AppMenu selectMenuById(Long menuId);

    /**
     * 根据岗位id查询菜单列表
     *
     * @param postId
     * @return
     */
    List<Integer> selectMenuByPostId(Long postId);

    /**
     * 新增菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int insertMenu(AppMenu menu);

    /**
     * 批量插入APP用户菜单
     *
     * @param list
     * @return
     */
    int insertAppUserMenu(List<AppUserMenu> list);

    /**
     * 删除APP用户菜单
     *
     * @param userId
     * @return
     */
    int deleteAppUserMenu(Long userId);


    /**
     * 修改菜单信息
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
     * 校验菜单名称是否唯一
     *
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @return 结果
     */
    AppMenu checkMenuNameUnique(@Param("name") String menuName, @Param("parentId") Long parentId);

    /**
     * 查询菜单子节点数量
     *
     * @param menuId
     * @return
     */
    int hasChildByMenuId(Long menuId);


}
