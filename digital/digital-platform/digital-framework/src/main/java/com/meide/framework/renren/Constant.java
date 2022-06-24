package com.meide.framework.renren;

import java.util.Arrays;
import java.util.List;

/**
 * 功能描述 系统常量
 *
 * @author jiay
 */
public interface Constant {
    /**
     * 成功
     */
    int SUCCESS = 1;
    /**
     * 失败
     */
    int FAIL = 0;
    /**
     * OK
     */
    String OK = "OK";
    /**
     * 用户标识
     */
    String USER_KEY = "userId";
    /**
     * 菜单根节点标识
     */
    Long MENU_ROOT = 0L;
    /**
     * 部门根节点标识
     */
    Long DEPT_ROOT = 0L;
    /**
     * 数据字典根节点标识
     */
    Long DICT_ROOT = 0L;
    /**
     * 升序
     */
    String ASC = "asc";
    /**
     * 降序
     */
    String DESC = "desc";
    /**
     * 创建时间字段名
     */
    String CREATE_DATE = "create_date";

    /**
     * 创建时间字段名
     */
    String ID = "id";

    /**
     * 数据权限过滤
     */
    String SQL_FILTER = "sqlFilter";

    /**
     * 当前页码
     */
    String PAGE = "page";
    /**
     * 每页显示记录数
     */
    String LIMIT = "limit";
    /**
     * 每页显示记录数
     */
    String PAGE_SIZE = "pageSize";
    /**
     * 排序字段
     */
    String ORDER_FIELD = "orderField";
    /**
     * 排序方式
     */
    String ORDER = "order";
    /**
     * token header
     */
    String TOKEN_HEADER = "token";

    /**
     * 云存储配置KEY
     */
    String CLOUD_STORAGE_CONFIG_KEY = "CLOUD_STORAGE_CONFIG_KEY";
    /**
     * 短信配置KEY
     */
    String SMS_CONFIG_KEY = "SMS_CONFIG_KEY";
    /**
     * 邮件配置KEY
     */
    String MAIL_CONFIG_KEY = "MAIL_CONFIG_KEY";
    /**
     * 用户ID
     */
    String USERID = "userId";

    /**
     * 定时任务状态
     */
    enum ScheduleStatus {
        /**
         * 暂停
         */
        PAUSE(0),
        /**
         * 正常
         */
        NORMAL(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3),
        /**
         * FASTDFS
         */
        FASTDFS(4),
        /**
         * 本地
         */
        LOCAL(5);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 短信服务商
     */
    enum SmsService {
        /**
         * 阿里云
         */
        ALIYUN(1),
        /**
         * 腾讯云
         */
        QCLOUD(2);

        private int value;

        SmsService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 物联网设备类型编号，010智能家居
     */
    String IOT_DEVICE_TYPE_CODE = "010";

    /**
     * 超级管理员保留的菜单id
     */
    List<Long> SUPER_ADMIN_MENU_ID_LIST = Arrays.asList(
            /**
             * 权限管理
             */
            1067246875800000002L,
            /**
             * 系统设置
             */
            1067246875800000035L,
            /**
             *日志管理
             */
            1067246875800000046L,
            /**
             * 消息管理
             */
            1067246875800000024L
    );
    /**
     * 超级管理员去除的子菜单的id
     */
    List<Long> SUPER_ADMIN_MENU_ID_NOT_LIST = Arrays.asList(
            /**
             * 参数设置
             */
            1067246875800000040L,
            /**
             * 字典管理
             */
            1067246875800000041L,
            /**
             * 文件上传
             */
            1067246875800000047L,
            /**
             * 调试二维码
             */
            1115847812371943425L,
            /**
             * 邮件模板
             */
            1067246875800000022L,
            /**
             * 邮件发送记录
             */
            1067246875800000023L
    );

    /**
     * 消息和推送通道Channel获取接口
     * 的formatId
     * 的枚举
     */
    enum MESSAGE_FORMAT_CONVENTIO {
        /**
         * 纯文本消息
         */
        TEXT_MSG(1),
        /**
         * WiFi状态消息
         */
        WIFI_STATUS_MSG(2),
        /**
         * 空调状态消息
         */
        AIR_CONDITION_STATUS_MSG(3),
        /**
         * 图片文本消息
         */
        PRICTURE_TEXT_MSG(4),
        /**
         * 自生成消息
         */
        SLFGEN_MSG(5),
        /**
         * 命令响应消息
         */
        CMD_RESPONSE_MSG(6),
        /**
         * 状态告警
         */
        STATYS_ALARM_MSG(7),
        /**
         * 人机交互
         */
        HUMAN_COMPUTER_INTERACTION_MSG(8),
        /**
         * 第三方设备告警
         */
        THIRD_PARTY_RQUIPMENT_WARNING_MSG(9);


        private Integer formatId;

        MESSAGE_FORMAT_CONVENTIO(Integer formatId) {
            this.formatId = formatId;
        }

        public Integer getFormatId() {
            return formatId;
        }
    }

    //商品
    enum SkuData {
        /**
         * 属性值ID
         */
        ATTR_VALUE_ID("attrValueID"),
        /**
         * 属性值名称
         */
        ATTR_VALUE_NAME("attrValueName"),
        /**
         * 属性ID
         */
        ATTR_ID("attrID"),
        /**
         * 属性名称
         */
        ATTR_NAME("attrName");

        private String value;

        SkuData(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 物联网平台成员角色类型
     */
    enum IoTRoleFlag {
        /**
         * 管理员
         */
        MANAGER(1),
        /**
         * 普通成员
         */
        MEMBER(2);
        private int code;

        IoTRoleFlag(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 已经删除
     */
    int DELETE = 1;
    /**
     * 未删除
     */
    int NOT_DELETE = 0;

    //APNS签名加密串
    String SIGN_ENCRYPTION = "4032af8d61035123906e58e067140cc5";

    //文件上传时，网络路径与服务器地址路径特殊分隔。
    String FILE_SPLIT_STR = "@IRR@";
}
