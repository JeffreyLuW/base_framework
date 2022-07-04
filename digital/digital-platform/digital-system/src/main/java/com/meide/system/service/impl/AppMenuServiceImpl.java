package com.meide.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.meide.common.constant.UserConstants;
import com.meide.common.core.domain.TreeSelect;
import com.meide.common.core.domain.entity.AppMenu;
import com.meide.common.core.domain.entity.AppUserMenu;
import com.meide.common.exception.CustomException;
import com.meide.common.utils.SecurityUtils;
import com.meide.common.utils.StringUtils;
import com.meide.system.domain.AppPostMenu;
import com.meide.system.domain.vo.AppMenuVo;
import com.meide.system.mapper.AppMenuMapper;
import com.meide.system.mapper.AppPostMenuMapper;
import com.meide.system.service.IAppMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * APP菜单业务逻辑
 * @author jiay
 */
@Service
public class AppMenuServiceImpl implements IAppMenuService {

    @Autowired
    private AppMenuMapper appMenuMapper;

    @Autowired
    private AppPostMenuMapper postMenuMapper;

    /**
     * 查询app菜单
     *
     * @param appMenu
     * @return
     */
    @Override
    public List<AppMenu> selectMenuList(AppMenu appMenu) {
        return appMenuMapper.selectMenuList(appMenu);
    }

    /**
     * 插入菜单
     *
     * @param menu 菜单信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertMenu(AppMenu menu) {
        AppMenu appMenu = appMenuMapper.checkMenuNameUnique(menu.getName(), menu.getParentId());
        if (Optional.ofNullable(appMenu).isPresent()) {
            throw new CustomException(menu.getName() + "已存在");
        }
        menu.setCreateBy(SecurityUtils.getUsername());
        return appMenuMapper.insertMenu(menu);
    }

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateMenu(AppMenu menu) {

        AppMenu appMenu = appMenuMapper.checkMenuNameUnique(menu.getName(), menu.getParentId());
        if (null != appMenu && !appMenu.getItemId().equals(menu.getItemId())) {
            //不是同一个还重名
            throw new CustomException(menu.getName() + "已经被使用");
        }
        menu.setUpdateBy(SecurityUtils.getUsername());
        return appMenuMapper.updateMenu(menu);
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteMenuById(Long menuId) {
        AppPostMenu postMenu = new AppPostMenu();
        postMenu.setMenuId(menuId);
        int num = postMenuMapper.count(postMenu);
        if (0 < num) {
            throw new CustomException("该菜单正在使用，不可删除！");
        }
        return appMenuMapper.deleteMenuById(menuId);
    }

    /**
     * 批量插入岗位菜单
     *
     * @param list
     * @return
     */
    @Override
    public int batchInsertPostMenu(List<AppPostMenu> list) {
        return postMenuMapper.batchPostMenu(list);
    }

    /**
     * 删除岗位菜单
     *
     * @param postId
     * @return
     */
    @Override
    public int deletePostMenuByPostId(Long postId) {
        return postMenuMapper.deletePostMenuByPostId(postId);
    }

    /**
     * 检查菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return
     */
    @Override
    public String checkMenuNameUnique(AppMenu menu) {
        Long menuId = StringUtils.isNull(menu.getItemId()) ? -1L : menu.getItemId();
        AppMenu info = appMenuMapper.checkMenuNameUnique(menu.getName(), menu.getParentId());
        if (StringUtils.isNotNull(info) && info.getItemId().longValue() != menuId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * APP获取用户拥有的菜单
     *
     * @return
     */
    @Override
    public List<AppMenuVo> getUserMenuList() {
        List<AppMenuVo> list = new ArrayList<>();
        AppMenuVo appMenuVo = null;
        List<AppMenu> favoriteMenu = appMenuMapper.selectUserFavoriteMenuList(SecurityUtils.getLoginUser().getUser().getUserId());

        if (CollUtil.isEmpty(favoriteMenu)) {
            favoriteMenu = appMenuMapper.selectUserDefaultMenuListByUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        }
        if (CollUtil.isNotEmpty(favoriteMenu)) {
            appMenuVo = new AppMenuVo();
            appMenuVo.setItemId(0L)
                    .setName("favorite")
                    .setOrderNum(0)
                    .setChildren(favoriteMenu.stream().map(item -> BeanUtil.copyProperties(item, AppMenuVo.class)).collect(Collectors.toList()));
        }
        List<AppMenu> appMenuList = appMenuMapper.selectMenuListByUserId(SecurityUtils.getLoginUser().getUser().getUserId());
        if (CollUtil.isNotEmpty(appMenuList)) {
            list = appMenuList.stream().map(item -> {
                AppMenuVo vo = BeanUtil.copyProperties(item, AppMenuVo.class);
                vo.setChildren(appMenuMapper.selectUserMenuListByParentId(item.getItemId()));
                return vo;
            }).collect(Collectors.toList());
            list.add(appMenuVo);
        }
        return list;
    }

    @Override
    public List<Integer> selectMenuListByPostId(Long postId) {
        return appMenuMapper.selectMenuByPostId(postId);
    }

    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<AppMenu> list) {
        List<AppMenu> menuTrees = buildMenuTree(list);
        return menuTrees.stream().map(item -> new TreeSelect(item)).collect(Collectors.toList());
    }

    private List<AppMenu> buildMenuTree(List<AppMenu> menus) {
        List<AppMenu> returnList = new ArrayList();
        for (Iterator<AppMenu> iterator = menus.iterator(); iterator.hasNext(); ) {
            AppMenu t = (AppMenu) iterator.next();
            // 根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == 0) {
                recursionFn(menus, t);
                returnList.add(t);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<AppMenu> list, AppMenu t) {
        // 得到子节点列表
        List<AppMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (AppMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<AppMenu> it = childList.iterator();
                while (it.hasNext()) {
                    AppMenu n = it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<AppMenu> getChildList(List<AppMenu> list, AppMenu t) {
        List<AppMenu> tlist = new ArrayList();
        Iterator<AppMenu> it = list.iterator();
        while (it.hasNext()) {
            AppMenu n = it.next();
            if (n.getParentId().longValue() == t.getItemId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<AppMenu> list, AppMenu t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        int result = appMenuMapper.hasChildByMenuId(menuId);
        return result > 0 ? true : false;
    }

    /**
     * 插入app用户菜单
     *
     * @param menuList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertAppUserMenu(List<AppUserMenu> menuList) {
        int num = 0;
        if (CollUtil.isNotEmpty(menuList)) {
            appMenuMapper.deleteAppUserMenu(SecurityUtils.getLoginUser().getUser().getUserId());
            num = appMenuMapper.insertAppUserMenu(menuList);
        }
        return num;
    }
}
