package models.order;

import models.common.enums.PaymentStatus;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;


/**
 * 支付渠道定义.
 */
@Entity
@Table(name = "payment_configs")
public class PaymentConfig extends Model {


    /**
     * 支付编码
     */
    @Column(name = "code")
    public String code;

    /**
     * 发票抬头
     */
    @Column(name = "invoice_title")
    public String invoiceTitle;

    /**
     * 支付方法工厂类
     */
    @Column(name = "factory_clazz")
    public String factoryClazz;

    /**
     * B2C支付表单的目标URL
     */
    @Column(name = "post_url")
    public String postUrl;

    /**
     * 支付请求服务URL，可为空
     */
    @Column(name = "service_url")
    public String serviceUrl;

    /**
     * 支付方分配的账户ID
     */
    @Column(name = "merchant_acct_id")
    public String merchantAcctId;

    /**
     * 支付方分配的协议邮箱
     */
    @Column(name = "merchant_email")
    public String merchantEmail;

    /**
     * 支付方 分配的协议密码
     */
    @Column(name = "merchant_password")
    public String merchantPassword;


    /**
     * 私钥路径
     */
    @Column(name = "private_key")
    public String private_key;

    /**
     *  公钥路径
     */
    @Column(name = "public_key")
    public String publicKey;

    /**
     * 公钥密码
     */
    @Column(name = "key_password")
    public String keyPassword;

    /**
     * 支付后台接收URL
     */
    @Column(name = "notify_url")
    public String notifyUrl;

    /**
     * 支付页面接收URL
     */
    @Column(name = "return_url")
    public String return_url;

    /**
     * 当前状态
     */
    @Enumerated(EnumType.ORDINAL)
    public PaymentStatus status;

    /**
     * 订单创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    public Date updatedAt;



    /**
     * 更新对象.
     */
    public static PaymentConfig update(Long id, PaymentConfig newObject) {
        PaymentConfig conf = PaymentConfig.findById(id);
        if (conf == null) {
            return null;
        }
        conf.code = newObject.code;
        conf.save();
        return conf;
    }

    public static Boolean delete(Long id) {

        PaymentConfig characteristic = PaymentConfig.findById(id);
        if (characteristic == null) {
            return Boolean.FALSE;
        }
        characteristic.status = PaymentStatus.CLOSE;
        characteristic.save();
        return Boolean.TRUE;
    }


}
