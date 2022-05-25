package com.meide.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.meide.common.constant.UserConstants;
import com.meide.common.exception.CustomException;
import com.meide.common.utils.StringUtils;
import com.meide.system.domain.AppPostMenu;
import com.meide.system.domain.SysPost;
import com.meide.system.mapper.AppPostMenuMapper;
import com.meide.system.mapper.SysPostMapper;
import com.meide.system.mapper.SysUserPostMapper;
import com.meide.system.service.ISysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 岗位信息 服务层处理
 *
 * @author jiay
 */
@Service
public class SysPostServiceImpl implements ISysPostService {
    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private AppPostMenuMapper postMenuMapper;

    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post) {
        return postMapper.selectPostList(post);
    }

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll() {
        return postMapper.selectPostAll();
    }

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(Long postId) {
        return postMapper.selectPostById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Integer> selectPostListByUserId(Long userId) {
        return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostNameUnique(SysPost post) {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostCodeUnique(SysPost post) {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostCodeUnique(post.getPostCode());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int countUserPostById(Long postId) {
        return userPostMapper.countUserPostById(postId);
    }

    /**
     * 删除岗位信息
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePostById(Long postId) {
        if (countUserPostById(postId) > 0) {
            SysPost sysPost = selectPostById(postId);
            throw new CustomException(StrUtil.format("{}存在用户，不能删除", sysPost.getPostName()));
        }
        return postMapper.deletePostById(postId);
    }

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePostByIds(Long[] postIds) {
        int i = 0;
        for (Long postId : postIds) {
            i += deletePostById(postId);
        }
        return i;
    }

    /**
     * 新增保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPost post) {
        postMapper.insertPost(post);
        return insertPostMenu(post);
    }

    public int insertPostMenu(SysPost post) {
        int rows = 1;
        // 新增用户与角色管理
        List<AppPostMenu> list = new ArrayList();
        for (Long menuId : post.getMenuIds()) {
            AppPostMenu pm = new AppPostMenu();
            pm.setPostId(post.getPostId());
            pm.setMenuId(menuId);
            list.add(pm);
        }
        if (list.size() > 0) {
            rows = postMenuMapper.batchPostMenu(list);
        }
        return rows;
    }

    /**
     * 修改保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPost post) {
        postMapper.updatePost(post);
        postMenuMapper.deletePostMenuByPostId(post.getPostId());
        return insertPostMenu(post);
    }
}
