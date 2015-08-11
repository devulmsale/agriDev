package models.mert;

import jodd.bean.BeanCopy;
import models.constants.DeletedStatus;
import models.product.Product;
import models.product.TypeBrand;
import models.product.TypeLable;
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
 * Created by upshan on 15/7/7.
 */
@Entity
@Table(name = "merchant_product_types")
public class MerchantProductType extends Model {

    /**
     * 类别名称
     */
    @Required(message = "类别名称不能为空.")
    @MinLength(value = 2 , message = "类别名称不能低于2个字符")
    @MaxLength(value = 20 , message = "类别名称不能大于20个字符")
    @Column(name = "name")
    public String name;

    /**
     * 商户信息
     */
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    public Merchant merchant;

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
     * 分页查询.
     */
    public static JPAExtPaginator<MerchantProductType> findByCondition(Map<String, Object> conditionMap, String orderByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder(" t.deleted=models.constants.DeletedStatus.UN_DELETED ")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.name like {searchName} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/");

        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<MerchantProductType> resultPage = new JPAExtPaginator<MerchantProductType>("MerchantProductType t", "t", MerchantProductType.class,
                result.getXsql(), conditionMap).orderBy(orderByExpress);
        Logger.info("order Select SQL :" + result.getXsql() + "---");
        resultPage.setPageNumber(pageNumber);
        resultPage.setPageSize(pageSize);
        resultPage.setBoundaryControlsEnabled(false);
        return resultPage;
    }

    /**
     * 类别修改
     * @param id
     * @param newObject
     */
    public static void update(Long id , MerchantProductType newObject) {
        MerchantProductType oldproductType = MerchantProductType.findById(id);
        BeanCopy.beans(newObject, oldproductType).ignoreNulls(true).copy();
        oldproductType.save();
    }

    /**
     * 根据商户查询类别
     */
    public static List<MerchantProductType> findMerchantProductType(Long merchantId){
        return MerchantProductType.find("deleted = ? and merchant.id = ?" , DeletedStatus.UN_DELETED , merchantId).fetch();
    }

}
