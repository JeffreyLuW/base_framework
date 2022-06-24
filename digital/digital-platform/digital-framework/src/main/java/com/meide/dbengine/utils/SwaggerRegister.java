package com.meide.dbengine.utils;

import io.swagger.models.Swagger;

/**
 * 用于注册swagger接口
 */
public interface SwaggerRegister {
    /**
     * @param swagger
     */
    void registerToSwagger(Swagger swagger);
}
