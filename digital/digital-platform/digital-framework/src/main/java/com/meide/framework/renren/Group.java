package com.meide.framework.renren;

import javax.validation.GroupSequence;

/**
 * 功能描述 定义校验顺序，如果AddGroup组失败，则UpdateGroup组不会再校验
 *
 * @author jiay
 */
@GroupSequence({AddGroup.class, UpdateGroup.class})
public interface Group {

}
