package com.meide.common.utils;

public class SysDictDetail {
    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典值
     */
    private String value;

    /**
     * 字典组编码(与sys_dict表的code一致,不论是否是tree，同一组的编码应相同)
     */
    private String groupCode;

    public SysDictDetail() {
    }

    public SysDictDetail(String groupCode, String name, String value) {
        this.name = name;
        this.value = value;
        this.groupCode = groupCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
