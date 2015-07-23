package models.order;

import models.common.enums.SupplierStatus;
import models.constants.DeletedStatus;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * 场馆图片
 */
@Entity
@Table(name = "suppliers")
public class Supplier extends Model {

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
     * 备注信息
     */
    @Column(name = "remark")
    public String remark;

    /**
     * 上游状态
     */
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    public SupplierStatus status;

    /**
     * 删除状态
     */
    @Column(name = "deleted")
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;


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


    /**
     * 自营供应商
     *
     * @return
     */
    public static Supplier defaultSuppler() {
//        Supplier supplier = Supplier.find.where().eq("shortName", "owner").findUnique();
        Supplier supplier = Supplier.find("shortName = ?", "owner").first();
        if (supplier == null) {
            supplier = new Supplier();
            supplier.beginAt = new Date();
            supplier.createdAt = new Date();
            supplier.shortName = "owner";
            supplier.shortName = "自营";
            supplier.deleted = DeletedStatus.UN_DELETED;
            supplier.invoiceTitle = "同城健身网";
            supplier.status = SupplierStatus.NORMAL;
            supplier.save();
        }
        return supplier;
    }

}
