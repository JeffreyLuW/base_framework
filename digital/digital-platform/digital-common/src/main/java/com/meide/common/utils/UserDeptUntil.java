package com.meide.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.meide.common.core.domain.entity.SysDept;
import com.meide.common.core.domain.entity.SysUser;
import com.meide.common.core.domain.model.LoginUser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiay
 */
@Component
public class UserDeptUntil {

    @Resource
    private UserDeptUntilMapper userDeptUntilMapper;

    private static UserDeptUntil deptUntil;

    @PostConstruct
    public void init() {
        deptUntil = this;
    }

    /**
     * 以字符串的形式，返回当前用户所在部门以及子部门的部门code，以逗号分隔
     * @return
     */
    public static String getUserDept(){

        LoginUser loginUser = SecurityUtils.getLoginUser();

        if (ObjectUtil.isEmpty(loginUser)) {
            return null;
        }
        SysUser user = loginUser.getUser();

        if (ObjectUtil.isEmpty(user)) {
            return null;
        }
        SysDept dept = user.getDept();

        List<String> deptCodes = new ArrayList<>();
        //获取部门上级id,部门id
        Long parentId = dept.getParentId();
        Long deptId = dept.getDeptId();
        switch (parentId.intValue()){
            case 0:
                //查询所有部门编码
                deptCodes = deptUntil.userDeptUntilMapper.selectAllDept();
                break;
            case 1:
                switch (deptId.intValue()){
                    case 244:
                        //查询所有部门编码
                        deptCodes = deptUntil.userDeptUntilMapper.selectAllDept();
                        break;
                    case 245:
                        //查询管理站下面的单位
                        deptCodes = deptUntil.userDeptUntilMapper.selectManDept(deptId);
                        break;
                    case 242:
                        //查询所有部门编码
                        deptCodes = deptUntil.userDeptUntilMapper.selectAllDept();
                        break;
                    default:
                        break;
                }
                break;
            default:
                //查询所属单位
                deptCodes = deptUntil.userDeptUntilMapper.selectDept(deptId);
                break;
        }

        StringBuilder codes = new StringBuilder();
        codes.append("'");

        for (String code : deptCodes) {
            codes.append(code).append("','");
        }

        String deptCode = codes.substring(0, codes.length() - 2);

        return deptCode;
    }

    /**
     * 返回当前登陆用户的部门编码
     * @return
     */
    public static String getUserDeptCode() {

        LoginUser loginUser = SecurityUtils.getLoginUser();

        if (ObjectUtil.isEmpty(loginUser)) {
            return null;
        }

        SysUser user = loginUser.getUser();

        if (ObjectUtil.isEmpty(user)) {
            return null;
        }

        SysDept dept = user.getDept();

        if (ObjectUtil.isEmpty(dept)) {
            return null;
        }

        return dept.getCode();

    }

    //根据部门id查找部门编码以及子级部门编码
    public static String getUser(Long[] ids){

        List<String> deptCodes = new ArrayList<>();
        for (Long id : ids) {
            //获取上级id
            Long parentId = deptUntil.userDeptUntilMapper.getParentId(id);
            switch (parentId.intValue()){
                case 0:
                    //查询所有部门编码
                    List<String> all = deptUntil.userDeptUntilMapper.selectAllDept();
                    deptCodes.addAll(all);
                    break;
                case 1:
                    switch (id.intValue()){
                        case 244:
                            //查询所有科室下面的单位
                            List<String> one = deptUntil.userDeptUntilMapper.selectAllDept();
                            deptCodes.addAll(one);
                            break;
                        case 245:
                            //查询管理站下面的单位
                            List<String> two = deptUntil.userDeptUntilMapper.selectManDept(id);
                            deptCodes.addAll(two);
                            break;
                        case 242:
                            //查询所有用水单位下面的单位
                            List<String> three = deptUntil.userDeptUntilMapper.selectAllDept();
                            deptCodes.addAll(three);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    //查询所属单位
                    deptCodes = deptUntil.userDeptUntilMapper.selectDept(id);
                    break;
            }
        }
        List<String> collect = deptCodes.stream().distinct().collect(Collectors.toList());
        StringBuilder codes = new StringBuilder();
        codes.append("'");

        for (String code : collect) {
            codes.append(code).append("','");
        }

        String deptCode = codes.substring(0, codes.length() - 2);

        return deptCode;
    }
}
