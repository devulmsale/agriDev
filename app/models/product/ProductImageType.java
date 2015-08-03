package models.product;

/**
 * Created by upshan on 15/7/24.
 */

import jodd.bean.BeanCopy;
import models.constants.DeletedStatus;
import models.product.enums.ImageType;
import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.MinLength;
import play.Logger;
import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;

import javax.persistence.*;
import javax.validation.constraints.Max;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 品牌
 */
@Entity
@Table(name = "product_image_types")
public class ProductImageType extends Model{

    /**
     * 图片类型
     */
    @Required (message = "图片类型不能为空")
    @Enumerated(EnumType.STRING)
    public ImageType imageType;

    /**
     * 图片宽度
     */
    @Required (message = "图片宽度不能为空")
    @Min(value = 50 , message = "图片宽度不能低于50")
    @Max(value = 1024 , message = "图片宽度不能大于1024")
    @Column(name = "width")
    public Integer width;


    /**
     * 图片高度
     */
    @Required(message = "图片高度不能为空")
    @Min(value = 50 , message = "图片高度不能低于50")
    @Max(value = 1024 , message = "图片高度不能大于1024")
    @Column(name = "height")
    public Integer height;

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


    /**
     * 图片类型修改
     * @param id
     * @param newObject
     */
    public static void update(Long id , ProductImageType newObject) {
        ProductImageType oldBrand = ProductImageType.findById(id);
        BeanCopy.beans(newObject, oldBrand).ignoreNulls(true).copy();
        oldBrand.save();
    }

    /**
     * 分页查询.
     */
    public static JPAExtPaginator<ProductImageType> findByCondition(Map<String, Object> conditionMap, String orderByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder(" t.deleted=models.constants.DeletedStatus.UN_DELETED ")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.imageType = {imageType} ~/")
                .append("/~ and t.width = {width} ~/")
                .append("/~ and t.height = {height} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/");


        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<ProductImageType> resultPage = new JPAExtPaginator<ProductImageType>("ProductImageType t", "t", ProductImageType.class,
                result.getXsql(), conditionMap).orderBy(orderByExpress);
        Logger.info("ProductImageType Select SQL :" + result.getXsql() + "---");
        resultPage.setPageNumber(pageNumber);
        resultPage.setPageSize(pageSize);
        resultPage.setBoundaryControlsEnabled(false);
        return resultPage;
    }

    /**
     * 查找第一类商品类别
     * @return
     */
    public static List<ProductImageType> findTopBrand() {
        return ProductImageType.find("deleted = ? and brand = null", DeletedStatus.UN_DELETED).fetch();
    }


    /**
     * 根据父类查询
     * @return
     */
    public static List<ProductImageType> findByBrand(ProductImageType brand) {
        return ProductImageType.find("deleted = ? and brand = ?", DeletedStatus.UN_DELETED, brand).fetch();
    }


    public static ProductImageType findByImageType(ImageType imageType) {
        return ProductImageType.find("imageType = ? " , imageType).first();
    }

    public static Integer findWidthByImageType(ImageType imageType) {
        ProductImageType productImageType =  findByImageType(imageType);
        return productImageType != null ? productImageType.width : null;
    }

    public static Integer findHeightByImageType(ImageType imageType) {
        ProductImageType productImageType =  findByImageType(imageType);
        return productImageType != null ? productImageType.height : null;
    }

    public static Long findIdByImageType(ImageType imageType) {
        ProductImageType productImageType =  findByImageType(imageType);
        return productImageType != null ? productImageType.id : null;
    }

}
