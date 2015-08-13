package models.mert.hall;

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
import util.common.RandomNumberUtil;

import javax.persistence.*;
import javax.validation.constraints.Max;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商家的厅
 * Created by upshan on 15/8/11.
 */

@Entity
@Table(name = "merchant_halls")
public class MerchantHall extends Model {


    /**
     * 所属商家
     */
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    public Merchant merchant ;

    /**
     * 大厅名称
     */
    @Required(message = "大厅名称不能为空")
    @MaxLength(value = 50,message = "大厅名称不能超过50个字符")
    @Column(name = "name")
    public String name;



    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted = DeletedStatus.UN_DELETED;


    /**
     * 创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;


    /**
     * 更新
     * @param id
     * @param newObject
     */
    public static void update(Long id, MerchantHall newObject) {
        MerchantHall merchantHall=MerchantHall.findById(id);
        BeanCopy.beans(newObject, merchantHall).ignoreNulls(true).copy();
        merchantHall.save();
    }



    /**
     * 分页查询
     *
     */
    public static JPAExtPaginator<MerchantHall> findByCondition(Map<String, Object> conditionMap, String MemberCardByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder("t.deleted=models.constants.DeletedStatus.UN_DELETED")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.name = {name} ~/")
                .append("/~ and t.merchant.id = {merchantId} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/");

        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<MerchantHall> memberCardPage = new JPAExtPaginator<MerchantHall>("MerchantHall t", "t", MerchantHall.class,
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
        if(obj instanceof MerchantHall == false)
            return false;
        if(this == obj) return true;
        MerchantHall other = (MerchantHall)obj;
        return new EqualsBuilder()
                .append(this.id, other.id)
                .isEquals();
    }


}
