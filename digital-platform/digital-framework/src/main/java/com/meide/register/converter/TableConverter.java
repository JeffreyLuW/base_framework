package com.meide.register.converter;

public interface TableConverter {

    /**
     *  获取前段页面表名展示名称
     *
     * @param dbTableName 数据库原始表名
     * @return
     */
    public    String getVoTableName(String dbTableName) ;

    /**
     *  获取前段页面列名展示名称
     *
     * @param dbColumn 数据库原始列名
     * @return
     */
    public    String getPageColumn(String dbColumn) ;


}
