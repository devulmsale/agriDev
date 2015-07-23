package models.order;

import models.common.enums.SupplierStatus;
import org.apache.commons.lang.time.DateUtils;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * 分销商
 */
@Entity
@Table(name = "resalers")
public class Resaler extends Model {

    private static final long serialVersionUID = 8451051653150386274L;

    /**
     * 简称
     */
    @Column(name = "short_name")
    public String shortName;

    /**
     * 全称
     */
    @Column(name = "show_name")
    public String showName;

    /**
     * 发票抬头
     */
    @Column(name = "invoice_title")
    public String invoiceTitle;


    /**
     * 接口代码
     */
    @Column(name = "app_code")
    public String appCode;

    /**
     * 接口安全码
     */
    @Column(name = "app_secret")
    public String appSecret;

    /**
     * 接口限制IP数量
     */
    @Column(name = "limit_ips")
    public String limitIps;


    /**
     * 备注信息
     */
    @Column(name = "remark")
    public String remark;

    /**
     * 是否可欠款
     */
    @Column(name = "creditable")
    public Boolean creditable;


    /**
     * 上游状态
     */
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    public SupplierStatus status;


    /**
     * 服务开通时间
     */
    @Column(name = "begin_at")
    public Date beginAt;

    /**
     * 服务关闭时间
     */
    @Column(name = "end_at")
    public Date endAt;

    /**
     * 上游创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 上游更新时间
     */
    @Column(name = "updated_at")
    public Date updatedAt;


    public static Resaler weixin() {
//        Resaler resaler = Resaler.find.where().eq("appCode", "weixin").findUnique();
        Resaler resaler = Resaler.find("appCode = ?", "weixin").first();
        if (resaler == null) {
            resaler = new Resaler();
            resaler.appCode = "weixin";
            resaler.appSecret = "84ad5105op38";
            resaler.shortName = "微信";
            resaler.shortName = "微信平台";
            resaler.beginAt = DateUtils.addDays(new Date(), -1); //1天前
            resaler.createdAt = new Date();
            resaler.creditable = Boolean.TRUE;
            resaler.status = SupplierStatus.NORMAL;
            resaler.save();
        }
        return resaler;
    }


}
