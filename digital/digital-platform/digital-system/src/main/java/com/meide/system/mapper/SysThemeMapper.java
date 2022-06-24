package com.meide.system.mapper;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.meide.system.domain.SysTheme;
import com.meide.system.domain.param.sysTheme.SysThemePageParam;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

/**
 * 主题(SysTheme)表数据库访问层
 *
 * @author jiay
 */
@Repository
public interface SysThemeMapper extends Mapper<SysTheme> {
    default List<SysTheme> selectByParam(SysThemePageParam param) {
        Example example = new Example(SysTheme.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(param.getStatus())) {
            criteria.andEqualTo("status", param.getStatus());
        }
        return this.selectByExample(example);
    }

    default int deleteByIds(String[] ids) {
        Example example = new Example(SysTheme.class);
        example.createCriteria().andIn("key", Arrays.asList(ids));
        return this.deleteByExample(example);
    }

    default List<SysTheme> selectAllEffective() {
        Example example = new Example(SysTheme.class);
        example.createCriteria().andEqualTo("status", 0);
        return this.selectByExample(example);
    }
}