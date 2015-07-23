package models.product;

import models.common.enums.GoodsStatus;
import models.constants.DeletedStatus;
import models.order.Goods;
import models.order.Supplier;
import play.Logger;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by upshan on 15/7/7.
 */
@Entity
@Table(name = "products")
public class Product extends Model {

    /**
     * 类别名称
     */
    @Column(name = "name")
    public String name;

    /**
     * 所属类别
     */
    @JoinColumn(name = "parent_type_id")
    @ManyToOne
    public ProductType parentType;

    /**
     * 市场价格
     */
    @Column(name = "male_price")
    public BigDecimal salePrice;


    /**
     *  微信价格
     */
    @Column(name = "weixin_price")
    public BigDecimal weixinPrice;

    /**
     * 成本价格
     */
    @Column(name = "original_price")
    public BigDecimal originalPrice;

    /**
     * 产品介绍
     */
    @Lob
    @Column(name = "content")
    public String content;

    /**
     * 列表图片标识
     */
    @Column(name = "list_ufid")
    public String listUFID;

    /**
     * 列表图片地址
     */
    @Column(name = "list_image")
    public String listImage;

    /**
     * 展示图片
     */
    @Column(name = "show_image")
    public String showImage;

    /**
     * 二维码图片
     */
    @Column(name = "qr_image")
    public String qrImage;


    /**
     * 创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;


    public static List<Product> findByType(Long typeId) {
        return Product.find("parentType.id = ? and deleted = ?", typeId, DeletedStatus.UN_DELETED).fetch();
    }

    public static List<Product> findByTopType(Long typeId) {
        return Product.find("parentType.parentType.id = ? and deleted = ?", typeId, DeletedStatus.UN_DELETED).fetch();
    }


    /**
     * 分页查询.
     */
    public static JPAExtPaginator<Product> findByCondition(Map<String, Object> conditionMap, String orderByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder(" t.deleted=models.constants.DeletedStatus.UN_DELETED ")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.name = {name} ~/")
                .append("/~ and t.parentType.id = {parentTypeId} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/")
                .append("/~ and t.updatedAt = {updatedAt} ~/");
        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<Product> resultPage = new JPAExtPaginator<Product>("Product t", "t", Product.class,
                result.getXsql(), conditionMap).orderBy(orderByExpress);
        Logger.info("product Select SQL :" + result.getXsql() + "---");
        resultPage.setPageNumber(pageNumber);
        resultPage.setPageSize(pageSize);
        resultPage.setBoundaryControlsEnabled(false);
        return resultPage;
    }

    /**
     * 构建 Goods
     * @return
     */
    public Goods findOrCreateGoods() {
        Goods goods = Goods.find("serial = ?", "PRODUCT_" + this.id).first();
        if (goods == null) {
            goods = new Goods();
            goods.createdAt = new Date();
            goods.name = this.name;
            goods.deleted = DeletedStatus.UN_DELETED;
            goods.facePrice = this.weixinPrice;
            goods.originalPrice = this.originalPrice;
            goods.salePrice = this.salePrice;
            goods.status = GoodsStatus.OPEN;
            goods.serial = "PRODUCT_" + this.id;
            goods.supplier = Supplier.defaultSuppler();
            goods.save();
        }
        return goods;
    }

}
