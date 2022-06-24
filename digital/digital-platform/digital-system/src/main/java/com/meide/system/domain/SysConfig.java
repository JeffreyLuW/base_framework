package com.meide.system.domain;

import com.meide.common.annotation.Excel;
import com.meide.common.annotation.Excel.ColumnType;
import com.meide.common.config.DigitalConfig;
import com.meide.common.constant.Constants;
import com.meide.common.core.domain.OldBaseEntity;
import com.meide.common.utils.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.File;

/**
 * 参数配置表 sys_config
 *
 * @author jiay
 */
public class SysConfig extends OldBaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 参数主键
     */
    @Excel(name = "参数主键", cellType = ColumnType.NUMERIC)
    private Long configId;

    /**
     * 参数名称
     */
    @Excel(name = "参数名称")
    private String configName;

    /**
     * 参数键名
     */
    @Excel(name = "参数键名")
    private String configKey;

    /**
     * 参数键值
     */
    @Excel(name = "参数键值")
    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    @Excel(name = "系统内置", readConverterExp = "Y=是,N=否")
    private String configType;

    /**
     * 当前值是否为图片（Y是 N否）
     */
    @Excel(name = "当前值是否为图片", readConverterExp = "Y=是,N=否")
    private String configIsImg;

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    @NotBlank(message = "参数名称不能为空")
    @Size(min = 0, max = 100, message = "参数名称不能超过100个字符")
    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @NotBlank(message = "参数键名长度不能为空")
    @Size(min = 0, max = 100, message = "参数键名长度不能超过100个字符")
    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    @NotBlank(message = "参数键值不能为空")
    @Size(min = 0, max = 500, message = "参数键值长度不能超过500个字符")
    public String getConfigValue() {
        return configValue;
    }

    public String getConfigIsImg() {
        return configIsImg;
    }

    public void setConfigIsImg(String configIsImg) {
        this.configIsImg = configIsImg;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("configId", getConfigId())
                .append("configName", getConfigName())
                .append("configKey", getConfigKey())
                .append("configValue", getConfigValue())
                .append("configType", getConfigType())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }

    /**
     * 返回图片配置是否有效
     *
     * @return
     */
    public boolean getValidateImage() {
        if ("N".equals(getConfigIsImg()) || StringUtils.isEmpty(getConfigValue())) {
            return false;
        }
        String filePath = getConfigValue();
        filePath = filePath.substring(Constants.RESOURCE_PREFIX.length());
        File file = new File(DigitalConfig.getAbsoluteProfilePath(), filePath);
        return file.exists();
    }
}
