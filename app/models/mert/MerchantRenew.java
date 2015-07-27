package models.mert;

import jodd.bean.BeanCopy;
import models.constants.DeletedStatus;
import models.operate.OperateUser;
import org.apache.commons.lang.builder.ToStringBuilder;
import play.Logger;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;
import util.xsql.XsqlBuilder;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

/**
 * Created by upshan on 15/7/27.
 */

/**
 * 商家缴费记录
 */
@Entity
@Table(name = "merchant_renews")
public class MerchantRenew extends Model {

    /**
     * 商家信息
     */
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    public Merchant merchant;

    /**
     * 缴费时间
     */
    @Column(name = "update_at")
    public Date updateAt;


    /**
     * 本次缴费到期时间
     */
    @Column(name = "expire_at")
    public Date expireAt;


    /**
     * 操作人
     */
    @ManyToOne
    @JoinColumn(name = "operate_user_id")
    public OperateUser operateUser;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;



    /**
     * 分页查询.
     */
    public static JPAExtPaginator<MerchantRenew> findByCondition(
            Map<String, Object> conditionMap, String orderByExpress,
            Integer pageNumber, Integer pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder(" deleted = models.constants.DeletedStatus.UN_DELETED ")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.merchant.id = {merchantId} ~/");
        XsqlBuilder.XsqlFilterResult result = new XsqlBuilder().generateHql(
                xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<MerchantRenew> merchantPage = new JPAExtPaginator<>("MerchantRenew t", "t",
                MerchantRenew.class, result.getXsql(), conditionMap)
                .orderBy(orderByExpress);
        merchantPage.setPageNumber(pageNumber);
        merchantPage.setPageSize(pageSize);
        merchantPage.setBoundaryControlsEnabled(false);
        return merchantPage;
    }

    /**
     * 代码更新
     * @param id
     * @param newObject
     */
    public static void update(Long id, MerchantRenew newObject) {
        MerchantRenew merchantRenew=MerchantRenew.findById(id);
        BeanCopy.beans(newObject, merchantRenew).ignoreNulls(true).copy();
        merchantRenew.save();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("merchant", merchant.id)
                .append("updateAt", updateAt)
                .append("expireAt", expireAt)
                .toString();
    }

}
