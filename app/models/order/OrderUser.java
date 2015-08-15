package models.order;

import jodd.bean.BeanCopy;
import models.common.ChinaPhone;
import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.mert.hall.HallTable;
import models.mert.hall.MerchantHall;
import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.MinLength;
import play.Logger;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by weixiao on 15/8/12.
 */
@Entity
@Table(name = "order_users")
public class OrderUser extends Model {

    /**
     * 订单用户名称
     */
    @Required(message = "姓名不能为空.")
    @MinLength(value = 2 , message = "姓名不能低于2个字符")
    @MaxLength(value = 20 , message = "姓名不能大于20个字符")
    @Column(name = "name")
    public String name;

    /**
     * 订单
     */

    @JoinColumn(name = "order_no")
    @ManyToOne
    public Order order;

    /**
     * 手机号
     */
    @Required(message = "手机号不能为空")
    @CheckWith(value = ChinaPhone.class , message = "手机号码不正确")
    @Column(name = "phone", length = 20)
    public String phone;

    /**
     * 时间
     */

    @Column(name = "time")
    public Date time;

    /**
     * 厅号
     */
    @ManyToOne
    @JoinColumn(name = "merchanthall_id")
    public MerchantHall merchantHall;
    /**
     * 桌号
     */
    @ManyToOne
    @JoinColumn(name = "halltable_id")
    public HallTable hallTable;
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
     * 备注
     */
    @Column(name = "remark", length = 1000)
    public String remark;

    /**
     * 分页查询.
     */
    public static JPAExtPaginator<OrderUser> findByCondition(Map<String, Object> conditionMap, String orderByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder(" t.deleted=models.constants.DeletedStatus.UN_DELETED ")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.name like {searchName} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/");

        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<OrderUser> resultPage = new JPAExtPaginator<OrderUser>("MerchantProductType t", "t", OrderUser.class,
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
    public static void update(Long id , OrderUser newObject) {
        OrderUser oldproductType = OrderUser.findById(id);
        BeanCopy.beans(newObject, oldproductType).ignoreNulls(true).copy();
        oldproductType.save();
    }

    /**
     * 根据商户查询类别
     */
    public static List<OrderUser> findMerchantProductType(Long merchantId){
        return OrderUser.find("deleted = ? and merchant.id = ?", DeletedStatus.UN_DELETED, merchantId).fetch();
    }

}
