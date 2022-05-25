package com.meide.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.meide.common.core.domain.model.LoginUser;
import com.meide.common.exception.CustomException;
import com.meide.common.utils.SecurityUtils;
import com.meide.system.domain.SysNotice;
import com.meide.system.domain.SysNoticeDept;
import com.meide.system.domain.SysNoticeUser;
import com.meide.system.mapper.SysNoticeMapper;
import com.meide.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 公告 服务层实现
 *
 * @author jiay
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService
{
    @Autowired
    private SysNoticeMapper noticeMapper;

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        SysNotice notice = noticeMapper.selectNoticeById(noticeId);
        List<SysNoticeDept> depts = noticeMapper.selectNoticeDeptList(noticeId);
        List<Long> deptIds = depts.stream().map(item -> item.getDeptId()).collect(Collectors.toList());
        notice.setDeptId(deptIds);
        List<SysNoticeUser> users = noticeMapper.selectNoticeUserList(noticeId);
        List<Long> userIds = users.stream().map(item -> item.getUserId()).collect(Collectors.toList());
        notice.setUserId(userIds);
        return notice;
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 查询用户所属的公告
     *
     * @param notice
     * @return
     */
    @Override
    public List<SysNotice> selectUserNoticeList(SysNotice notice) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Optional.ofNullable(loginUser).orElseThrow(() -> new CustomException("请先登录！"));
        List<SysNotice> list = noticeMapper.selectUserNoticeList(
                notice, loginUser.getUser().getUserId(), loginUser.getUser().getDeptId());
        return list;
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertNotice(SysNotice notice) {
        int num = noticeMapper.insertNotice(notice);
        saveNoticeDeptAndUser(notice);
        return num;
    }

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice) {
        int num = noticeMapper.updateNotice(notice);
        if (Optional.ofNullable(notice.getDeptId()).isPresent() ||
                Optional.ofNullable(notice.getUserId()).isPresent()) {
            noticeMapper.deleteNoticeDeptByNoticeId(notice.getNoticeId());
            noticeMapper.deleteNoticeUserByNoticeId(notice.getNoticeId());
            saveNoticeDeptAndUser(notice);
        }
        return num;
    }

    @Override
    public int publishNotice(SysNotice notice) {
        int num = noticeMapper.updateNotice(notice);
        return num;
    }

    /**
     * 保存通知部门和用户
     *
     * @param notice 通知实体类
     */
    private void saveNoticeDeptAndUser(SysNotice notice) {
        //如果通知部门为空
        if (Optional.ofNullable(notice.getDeptId()).isPresent() &&
                CollUtil.isNotEmpty(notice.getDeptId())) {

            List<SysNoticeDept> sysNoticeDepts = new ArrayList<>();
            sysNoticeDepts = notice.getDeptId().stream().map(item -> {
                SysNoticeDept sysNoticeDept = new SysNoticeDept();
                sysNoticeDept.setDeptId(item);
                sysNoticeDept.setNoticeId(notice.getNoticeId());
                return sysNoticeDept;
            }).collect(Collectors.toList());
            noticeMapper.addNoticeDept(sysNoticeDepts);
        }
        if (Optional.ofNullable(notice.getUserId()).isPresent() &&
                CollUtil.isNotEmpty(notice.getUserId())) {
            List<SysNoticeUser> sysNoticeUsers = new ArrayList<>();
            sysNoticeUsers = notice.getUserId().stream().map(item -> {
                SysNoticeUser sysNoticeUser = new SysNoticeUser();
                sysNoticeUser.setUserId(item);
                sysNoticeUser.setNoticeId(notice.getNoticeId());
                return sysNoticeUser;
            }).collect(Collectors.toList());
            noticeMapper.addNoticeUser(sysNoticeUsers);
        }
    }

    /**
     * 删除公告对象
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId) {
        //删除通知部门
        noticeMapper.deleteNoticeDeptByNoticeId(noticeId);
        //删除通知人员
        noticeMapper.deleteNoticeUserByNoticeId(noticeId);
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {
        noticeMapper.deleteNoticeDept(noticeIds);
        //删除通知人员
        noticeMapper.deleteNoticeUser(noticeIds);
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }

    /**
     * 阅读通知信息
     *
     * @param noticeId
     * @return
     */
    @Override
    public SysNotice readNotice(Long noticeId) {
        SysNotice notice = new SysNotice();
        notice.setNoticeId(noticeId);
        notice.setRead(true);
        this.updateNotice(notice);
        notice = selectNoticeById(noticeId);
        return notice;
    }
}
