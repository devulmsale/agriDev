package models.order;

import models.common.enums.GoodsStatus;
import models.constants.DeletedStatus;
import play.db.jpa.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 商品.
 */
@Entity
@Table(name = "goods")
public class Goods extends Model {

    /**
     * 商品名称
     */
    @Column(name = "name")
    public String name;

    /**
     * 商品编号
     */
    @Column(name = "serial")
    public String serial;

    /**
     * 商品标题
     */
    @Column(name = "title")
    public String title;

    /**
     * 商品关键字
     */
    @Column(name = "keywords")
    public String keywords;

    /**
     * 商品简介
     */
    @Column(name = "summary")
    public String summary;

    /**
     * 商品详情
     */
    @Column(name = "details")
    public String details;

    /**
     * 主图片
     */
    @Column(name = "main_image_ufid")
    public String mainImageUfid;// 主图片

    /**
     *  供应商
     */
    @JoinColumn(name = "supplier_id", nullable = true)
    @ManyToOne
    public Supplier supplier;

    /**
     * 供应商商品编号
     */
    @Column(name = "supplier_goods_id")
    public String supplierGoodsId;

    /**
     * 开始销售时间
     */
    @Column(name = "begin_onsale_at")
    public Date beginOnsaleAt;

    /**
     * 结束销售时间
     */
    @Column(name = "end_onsale_at")
    public Date endOnsaleAt;

    /**
     * 销售单价(面值)
     */
    @Column(name = "face_price")
    public BigDecimal facePrice;

    /**
     * 成本价
     */
    @Column(name = "original_price")
    public BigDecimal originalPrice;

    /**
     * 销售单价
     */
    @Column(name = "sale_price")
    public BigDecimal salePrice;

    /**
     * 状态，包括：NEW/OPEN/CLOSE
     */
    @Enumerated(EnumType.ORDINAL)
    public GoodsStatus status;

    /**
     * 排列顺序
     */
    @Column(name = "display_order")
    public Integer displayOrder;



    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;


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
    public static Goods update(Long id, Goods newObject) {
        Goods goods = Goods.findById(id);
        if (goods == null) {
            return null;
        }
        goods.name = newObject.name;
        goods.save();
        return goods;
    }

    public static Boolean delete(Long id) {

        Goods goods = Goods.findById(id);
        if (goods == null) {
            return Boolean.FALSE;
        }
        goods.deleted = DeletedStatus.DELETED;
        goods.save();
        return Boolean.TRUE;
    }




}
