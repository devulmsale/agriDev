package models.product;

import jodd.bean.BeanCopy;
import models.constants.DeletedStatus;
import models.mert.Merchant;
import net.sf.oval.constraint.MaxLength;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/13.
 */
@Entity
@Table(name = "set_meals")
public class SetMeal extends Model {
    /**
     * 套餐名称
     */
    @Required(message = "名称不能为空")
    @MaxLength(value = 100,message = "名称不能超过100个字符")
    @Column(name = "name")
    public String name;
    /*
    购买须知
     */

    @Column(name = "content")
    public String content;

    /**
     * 所属商家
     */
    @JoinColumn(name = "merchant_id")
    @ManyToOne
    public Merchant merchant;

    /**
     * 现价
     *
     */
    @Required(message = "现价不能为空")
    @Column(name = "present_price")
    public BigDecimal presentPrice;


    /**
     * 原价
     *
     */
    @Column(name = "original_price")
    public BigDecimal originalPrice;


    /**
     * 套餐使用时间
     */
    @Required(message = "时间不能为空")
    @Column(name = "begin_date")
    public Date beginDate;

    /**
     * 套餐结束时间
     */
    @Required(message = "时间不能为空")
    @Column(name = "end_date")
    public Date endDate;



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
     * 更新
     * @param id
     * @param newObject
     */
    public static void update(Long id, SetMeal newObject) {
        SetMeal setMeal=SetMeal.findById(id);
        BeanCopy.beans(newObject, setMeal).ignoreNulls(true).copy();
        setMeal.save();
    }


    /**
     * 根据商户查找套餐
     */
    public static List<SetMeal> findByMerchant(Merchant merchant){
        return SetMeal.find("deleted = ? and merchant.id = ?",DeletedStatus.UN_DELETED,merchant.id).fetch();
    }

    /**
     * 分页查询
     *
     */
    public static JPAExtPaginator<SetMeal> findByCondition(Map<String, Object> conditionMap, String MemberCardByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder("t.deleted=models.constants.DeletedStatus.UN_DELETED")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.name = {name} ~/")
                .append("/~ and t.merchant.id = {merchantId} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/");

        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<SetMeal> memberCardPage = new JPAExtPaginator<SetMeal>("SetMeal t", "t", SetMeal.class,
                result.getXsql(), conditionMap).orderBy(MemberCardByExpress);
        memberCardPage.setPageNumber(pageNumber);
        memberCardPage.setPageSize(pageSize);
        memberCardPage.setBoundaryControlsEnabled(false);
        return memberCardPage;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", this.id)
                .append("merchant", this.merchant)
                .append("name", this.name)
                .append("createdAt", this.createdAt)
                .append("deleted", this.deleted)
                .toString();
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.id)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SetMeal == false)
            return false;
        if(this == obj) return true;
        SetMeal other = (SetMeal)obj;
        return new EqualsBuilder()
                .append(this.id, other.id)
                .isEquals();
    }


}
