package com.meide.system.mapper;

import com.meide.system.domain.SysNotice;
import com.meide.system.domain.SysNoticeDept;
import com.meide.system.domain.SysNoticeUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知公告表 数据层
 *
 * @author jiay
 */
public interface SysNoticeMapper
{
    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    public SysNotice selectNoticeById(Long noticeId);

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 查询用户的可见公告
     *
     * @param notice
     * @param userId
     * @param deptId
     * @return
     */
    List<SysNotice> selectUserNoticeList(@Param("notice") SysNotice notice, @Param("userId") Long userId, @Param("deptId") Long deptId);

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    public int insertNotice(SysNotice notice);

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    public int updateNotice(SysNotice notice);

    /**
     * 批量删除公告
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    public int deleteNoticeById(Long noticeId);

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    public int deleteNoticeByIds(Long[] noticeIds);

    /**
     * 批量添加通知部门
     *
     * @param noticeDeptList 通知部门
     * @return
     */
    int addNoticeDept(@Param("list") List<SysNoticeDept> noticeDeptList);

    /**
     * 批量添加通知人员
     *
     * @param noticeUserList
     * @return
     */
    int addNoticeUser(@Param("list") List<SysNoticeUser> noticeUserList);

    /**
     * 删除通知部门
     *
     * @param noticeId
     * @return
     */
    int deleteNoticeDeptByNoticeId(Long noticeId);

    /**
     * 删除通知人员
     *
     * @param noticeId
     * @return
     */
    int deleteNoticeUserByNoticeId(Long noticeId);

    /**
     * 批量删除通知部门
     *
     * @param noticeIds
     * @return
     */
    int deleteNoticeDept(Long[] noticeIds);

    /**
     * 批量删除通知人员
     *
     * @param noticeIds
     * @return
     */
    int deleteNoticeUser(Long[] noticeIds);

    /**
     * 查询通知的部门信息
     *
     * @param noticeId 通知id
     * @return
     */
    List<SysNoticeDept> selectNoticeDeptList(Long noticeId);

    /**
     * 查询通知的用户信息
     *
     * @param noticeId
     * @return
     */
    List<SysNoticeUser> selectNoticeUserList(Long noticeId);

}
