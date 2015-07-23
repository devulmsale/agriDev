package models.mert;

import models.constants.DeletedStatus;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;
import util.xsql.XsqlBuilder;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

/**
 * 商家礼品设置 。 抽奖时使用
 * Created with IntelliJ IDEA.
 * User: upshan
 * Date: 14-4-13
 * Time: 上午7:40
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "giftseat")
public class MerchantGift extends Model {

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    public Merchant merchant ;//抽奖商家

    @Column(name = "product")
    public String   product; // 抽奖商品名称

    @Column(name = "proportion")
    public Long     proportion ; // 中奖利比有多大

    @Column(name = "imgUrl")
    public String   imgUrl; // 图片路径

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted = DeletedStatus.UN_DELETED;

    /**
     * 商品短描述，纯文本.
     */
    @Column(name = "description")
    public String description;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 修改时间
     */
    @Column(name = "updated_at")
    public Date updatedAt;

    /**
     * 有效期，当前日期如果大于有效期，抽奖商品讲不在被使用
     */
    @Column(name = "expired_at")
    public Date expiredAt;



    /**
     * 分页查询.
     */
    public static JPAExtPaginator<MerchantGift> findByCondition(Map<String, Object> conditionMap, String orderByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder("t.deleted=models.constants.DeletedStatus.UN_DELETED")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.merchant.id = {merchantId} ~/")
                .append("/~ and t.product = {product} ~/")
                .append("/~ and t.createdAt >= {beginCreated} ~/")
                .append("/~ and t.createdAt <= {endCreated} ~/")
                .append("/~ and t.updatedAt >= {beginUpdated} ~/")
                .append("/~ and t.updatedAt <= {endUpdated} ~/")
                .append("/~ and t.expiredAt <= {expiredAt} ~/");
        XsqlBuilder.XsqlFilterResult result = new XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);

        JPAExtPaginator<MerchantGift> giftPage = new JPAExtPaginator<>("MerchantGift t", "t", MerchantGift.class,
                result.getXsql(), conditionMap).orderBy(orderByExpress);
        giftPage.setPageNumber(pageNumber);
        giftPage.setPageSize(pageSize);
        giftPage.setBoundaryControlsEnabled(false);
        return giftPage;
    }
}
