package com.meide.common.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meide.utils.SimpleUtil;
import com.meide.utils.UIDevUtil;
import com.meide.utils.ValRunnable;
import com.meide.utils.valid.Validator;
import com.meide.utils.valid.ValidatorResult;

/**
 * 保留基类
 */
public class BaseController {


    /**
     * 校验参数，并返回结果。
     *
     * @param validator
     * @param valRunnable
     * @return
     */
    public Object validateAndReturn(Validator validator, ValRunnable valRunnable) {
        if (null != validator) {
            ValidatorResult validatorResult = validator.thenSync();
            if (!validatorResult.isValid()) {
                return UIDevUtil.apiError(validatorResult.getMessage());
            }
        }
        return UIDevUtil.apiSuccess(null, valRunnable.run());
    }

    /**
     * 简单返回成功后的结果。
     *
     * @param data
     * @return
     */
    public Object success(Object data) {
        //统一输出格式。 {list,pageNum,pageSize}
        if (data instanceof Page) {
            data = SimpleUtil.pageList2NormalMap((Page) data);
        }
        return UIDevUtil.apiSuccess(null, data);
    }
}