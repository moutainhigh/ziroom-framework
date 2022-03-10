package com.ziroom.framework.example.infrastructure.common;

/**
 * <p>系统枚举，用于标示不同系统，比如库存、信用等</p>
 * <p>
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author taiyn
 * @version 1.0
 * @date Created in 2019年11月27日 11:14
 * @since 1.0
 */
public enum SystemEnum {

    /**
     * ehr系统
     */
    EHR,
    /**
     * CRM系统
     */
    CRM,
    /**
     * CMS系统
     */
    CMS,
    /**
     * 收房系统
     */
    HIRE,
    /**
     * 合同管理系统
     */
    CONTRACT_SYSTEM,
    /**
     * 客户库
     */
    CUSTOMER,
    /**
     * 生活费用系统
     */
    RENT_LIVE,
    /**
     * 生活费用系统,不自动校验
     */
    RENT_LIVE_UN_CHECK,
    /**
     * 自如白条
     */
    ZWHITE,
    /**
     * 转租平台
     */
    SUBLET,
    /**
     * 自如寓
     */
    ZYU,
    /**
     * 自如支付
     */
    ZPAY,
    /**
     * 支付中心
     */
    ZPAY_CENTER,
    /**
     * 退款中心
     */
    REFUND_CENTER,
    /**
     * 卡券系统
     */
    COUPON,
    /**
     * 配置档案库
     */
    ARCHIVES,
    /**
     * 资产系统
     */
    AMI_UN_AUTO_CHECK,
    /**
     * 消息平台
     */
    MESSAGE,
    /**
     * 财务系统
     */
    ZFRECEIPT,
    /**
     * 智能锁平台
     */
    SAAS,
    /**
     * 价格系统
     */
    PRICE,
    /**
     * 老支付平台
     */
    JPAYMENT_UN_CHECK,
    /**
     * 支付平台
     */
    ZF,
    /**
     * 结算中台
     */
    EFTCENTRE,
    /**
     * 库存系统
     */
    INVENTORY,
    /**
     * 配置系统
     */
    ZRPDW,
    /**
     * 活动系统
     */
    ACTIVITY,
    /**
     * 财务数据主线系统
     */
    ZF_DATALINE,
    /**
     * 网站接口
     */
    WWW,
    /**
     * solr 接口
     */
    SOLR,
    /**
     * 楼盘系统
     */
    ZRBD,
    /**
     * 认证客户信息系统
     */
    PASSPORT,
    /**
     * 认证客户信息系统对内
     */
    PASSPORT_INSIDE,
    /**
     * 信用系统
     */
    CREDIT,
    /**
     * 楼盘字典
     */
    HOUSEBOOK,
    /**
     * 房源展示系统
     */

    HELIOS,
    /**
     * 品控系统
     */

    QMS,
    /**
     * 帕拉丁系统
     */
    PALADIN,
    /**
     * 库存系统新
     */
    INV_NEW,
    /**
     * 智能锁系统
     */
    LINK_LOCK;
}