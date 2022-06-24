package com.meide.web.controller.app;

import com.meide.common.annotation.Log;
import com.meide.common.core.controller.BaseController;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.core.domain.MapResult;
import com.meide.common.core.domain.entity.AppMenu;
import com.meide.common.core.domain.entity.AppUserMenu;
import com.meide.common.enums.BusinessType;
import com.meide.common.utils.SecurityUtils;
import com.meide.system.domain.vo.AppMenuVo;
import com.meide.system.service.IAppMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * APP菜单管理
 */
@RestController
@RequestMapping("app/menu")
@Api(tags = "APP菜单")
public class AppMenuController extends BaseController {

    @Autowired
    private IAppMenuService appMenuService;


    /**
     * 获取APP菜单列表
     */
    @GetMapping("/menuList")
    @ApiOperation("APP菜单权限")
    public AjaxResult<List<AppMenuVo>> menuList() {
        List<AppMenuVo> menus = appMenuService.getUserMenuList();
        return AjaxResult.success(menus);
    }

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermi('app:menu:list')")
    @GetMapping("/list")
    public AjaxResult<List<AppMenu>> list(AppMenu menu) {
        List<AppMenu> menus = appMenuService.selectMenuList(menu);
        return AjaxResult.success(menus);
    }

    /**
     * 获取菜单列表
     *
     * @return
     */
    @GetMapping(value = "/treeSelect")
    public MapResult postMenuTreeselect() {
        List<AppMenu> menus = appMenuService.selectMenuList(new AppMenu());
        MapResult ajax = MapResult.success();
        ajax.put("menus", appMenuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * 根据岗位id查询菜单id
     *
     * @param postId
     * @return
     */
    @GetMapping(value = "/treeSelect/{postId}")
    public MapResult postMenuTreeselect(@PathVariable("postId") Long postId) {
        List<AppMenu> menus = appMenuService.selectMenuList(new AppMenu());
        MapResult ajax = MapResult.success();
        ajax.put("menus", appMenuService.buildMenuTreeSelect(menus));
        ajax.put("checkedKeys", appMenuService.selectMenuListByPostId(postId));
        return ajax;
    }


    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('app:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult<Integer> add(@Validated @RequestBody AppMenu menu) {
        menu.setCreateBy(SecurityUtils.getUsername());
        return rows(appMenuService.insertMenu(menu));
    }

    /**
     * 新增用户常用菜单
     */
    @PostMapping("/favorite")
    @ApiOperation("新增用户常用菜单")
    public AjaxResult addFavoriteMenu(@Validated @RequestBody List<AppUserMenu> menuList) {
        return rows(appMenuService.insertAppUserMenu(menuList));
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('app:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult<Integer> edit(@Validated @RequestBody AppMenu menu) {
        menu.setUpdateBy(SecurityUtils.getUsername());
        return rows(appMenuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('app:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public AjaxResult<Integer> remove(@PathVariable("menuId") Long menuId) {
        if (appMenuService.hasChildByMenuId(menuId)) {
            return AjaxResult.errorWithMsg("存在子菜单,不允许删除");
        }
        return rows(appMenuService.deleteMenuById(menuId));
    }


}
