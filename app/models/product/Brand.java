package models.product;

/**
 * Created by upshan on 15/7/24.
 */

import jodd.bean.BeanCopy;
import models.constants.DeletedStatus;
import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.MinLength;
import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 品牌
 */
@Entity
@Table(name = "brand")
public class Brand extends Model{

    /**
     * 品牌名称
     */
    @Required(message = "品牌名称不能为空.")
    @MinLength(value = 2 , message = "品牌名称不能低于2个字符")
    @MaxLength(value = 20 , message = "品牌名称不能大于20个字符")
    @Column(name = "name")
    public String name;


    /**
     * 二级品类. 比如  大米/一斤包装
     */
    @JoinColumn(name = "brand_id")
    @ManyToOne
    public Brand brand;

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


    public static void update(Long id , Brand newObject) {
        Brand oldBrand = Brand.findById(id);
        BeanCopy.beans(newObject, oldBrand).ignoreNulls(true).copy();
        oldBrand.save();
    }

    /**
     * 分页查询.
     */
    public static JPAExtPaginator<Brand> findByCondition(Map<String, Object> conditionMap, String orderByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder(" t.deleted=models.constants.DeletedStatus.UN_DELETED ")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.name = {name} ~/")
                .append("/~ and t.name like {searchName} ~/")
                .append("/~ and t.brand.id = {brandId} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/");

        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<Brand> resultPage = new JPAExtPaginator<Brand>("Brand t", "t", Brand.class,
                result.getXsql(), conditionMap).orderBy(orderByExpress);
        Logger.info("brand Select SQL :" + result.getXsql() + "---");
        resultPage.setPageNumber(pageNumber);
        resultPage.setPageSize(pageSize);
        resultPage.setBoundaryControlsEnabled(false);
        return resultPage;
    }

    /**
     * 查找第一类商品类别
     * @return
     */
    public static List<Brand> findTopBrand() {
        return Brand.find("deleted = ? and brand = null", DeletedStatus.UN_DELETED).fetch();
    }


    /**
     * 根据父类查询
     * @return
     */
    public static List<Brand> findByBrand(Brand brand) {
        return Brand.find("deleted = ? and brand = ?", DeletedStatus.UN_DELETED, brand).fetch();
    }

}
