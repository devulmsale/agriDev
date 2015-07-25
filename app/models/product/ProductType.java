package models.product;

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
 * Created by upshan on 15/7/7.
 */
@Entity
@Table(name = "product_types")
public class ProductType extends Model {

    /**
     * 类别名称
     */
    @Required(message = "类别名称不能为空.")
    @MinLength(value = 2 , message = "类别名称不能低于2个字符")
    @MaxLength(value = 20 , message = "类别名称不能大于20个字符")
    @Column(name = "name")
    public String name;

    /**
     * 父类别关联
     */
    @JoinColumn(name = "parent_type_id")
    @ManyToOne
    public ProductType parentType;

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
    public static JPAExtPaginator<ProductType> findByCondition(Map<String, Object> conditionMap, String orderByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder(" t.deleted=models.constants.DeletedStatus.UN_DELETED ")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.name like {searchName} ~/")
                .append("/~ and t.parentType.id = {parentTypeId} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/");
        if(conditionMap.get("parentType") != null && conditionMap.get("parentType").equals("true")) {
            xsqlBuilder.append(" and t.parentType = null ");
            conditionMap.remove("parentType");
        }
        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<ProductType> resultPage = new JPAExtPaginator<ProductType>("ProductType t", "t", ProductType.class,
                result.getXsql(), conditionMap).orderBy(orderByExpress);
        Logger.info("order Select SQL :" + result.getXsql() + "---");
        resultPage.setPageNumber(pageNumber);
        resultPage.setPageSize(pageSize);
        resultPage.setBoundaryControlsEnabled(false);
        return resultPage;
    }

    /**
     * 查找第一类商品类别
     * @return
     */
    public static List<ProductType> findTopType() {
        return ProductType.find("deleted = ? and parentType = null", DeletedStatus.UN_DELETED).fetch();
    }


    /**
     * 根据父类查询
     * @return
     */
    public static List<ProductType> findByParentType(ProductType parentType) {
        return ProductType.find("deleted = ? and parentType = ?", DeletedStatus.UN_DELETED, parentType).fetch();
    }

    /**
     * 类别修改
     * @param id
     * @param newObject
     */
    public static void update(Long id , ProductType newObject) {
        ProductType oldproductType = ProductType.findById(id);
        BeanCopy.beans(newObject, oldproductType).ignoreNulls(true).copy();
        oldproductType.save();
    }

}
