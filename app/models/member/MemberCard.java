package models.member;


import jodd.bean.BeanCopy;
import models.base.enums.MemberCardType;
import models.common.DateUtil;
import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.order.User;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "member_cards")

public class MemberCard extends Model {

    private static final long serialVersionUID = 219113062L;


    @Required
    @Column(name = "card_number")
    public String cardNumber; // 卡号

    @Column(name = "card_password")
    public String cardPassword; //支付密码

    /**
     * 持卡人信息
     */
    @JoinColumn(name = "user_id", nullable = true)
    @ManyToOne
    public User user; //持卡人

    /**
     * 所属商家
     */
    @JoinColumn(name = "merchant_id", nullable = true)
    @ManyToOne
    public Merchant merchant;

    /**
     * 会员卡类型
     */
    @Enumerated(EnumType.STRING)
    public MemberCardType type;

    /**
     * 开始生效期  如果不限制为空
     */
    @Column(name = "begin_at")
    public Date beginAt;

    /**
     * 结束生效期 如果不限制为空
     */
    @Column(name = "end_at")
    public Date endAt;

    /**
     * 是否本卡只有自己可用
     */
    @Column(name = "only_self")
    public Boolean onlySelf;

    /**
     * 是否已经开始使用
     */
    @Column(name = "is_used")
    public Boolean isUsed;

    /**
     * 余额
     */
    @Column(name = "amount")
    public BigDecimal amount;

    /**
     * 商品余额
     */
    @Column(name = "shop_amount")
    public BigDecimal shopAmount;

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
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;


    /**
     * 分页查询
     *
     */
    public static JPAExtPaginator<MemberCard> findByCondition(Map<String, Object> conditionMap, String MemberCardByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder("t.deleted=models.constants.DeletedStatus.UN_DELETED")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.cardNumber = {cardNumber} ~/")
                .append("/~ and t.cardPassword = {cardPassword} ~/")
                .append("/~ and t.user.id = {userId} ~/")
                .append("/~ and t.venue.id = {venueId} ~/")
                .append("/~ and t.beginAt = {beginAt} ~/")
                .append("/~ and t.endAt = {endAt} ~/")
                .append("/~ and t.onlySelf = {onlySelf} ~/")
                .append("/~ and t.amount = {amount} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/")
                .append("/~ and t.updatedAt = {updatedAt} ~/");

        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<MemberCard> memberCardPage = new JPAExtPaginator<MemberCard>("MemberCard t", "t", MemberCard.class,
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
                .append("cardNumber", this.cardNumber)
                .append("cardPassword", this.cardPassword)
                .append("user", this.user)
                .append("merchant", this.merchant)
                .append("beginAt", this.beginAt)
                .append("endAt", this.endAt)
                .append("onlySelf", this.onlySelf)
                .append("amount", this.amount)
                .append("createdAt", this.createdAt)
                .append("updatedAt", this.updatedAt)
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
        if(obj instanceof MemberCard == false)
            return false;
        if(this == obj) return true;
        MemberCard other = (MemberCard)obj;
        return new EqualsBuilder()
                .append(this.id, other.id)
                .isEquals();
    }


    public static Long countByTypeAndDate(MemberCardType type , Date searchDate) {
       return  MemberCard.count("type = ? and ( createdAt between ? and ? ) and deleted = ?", type, DateUtil.getBeginOfDay(searchDate), DateUtil.getEndOfDay(searchDate), DeletedStatus.UN_DELETED);
    }

    public static Long countByType(Date searchDate) {
        return  MemberCard.count("createdAt between ? and ? and deleted = ?", DateUtil.getBeginOfDay(searchDate), DateUtil.getEndOfDay(searchDate), DeletedStatus.UN_DELETED);
    }

    /**
     * 是否可用
     */
    public Boolean enabled;

    public static MemberCard findByCardNumber(String cardNumber) {
        return MemberCard.find("cardNumber = ? ", cardNumber).first();
    }

    public static MemberCard findByCardNumberAndEnabled(String cardNumber) {
        return MemberCard.find("cardNumber = ? and enabled = ?", cardNumber, true).first();
    }

    public static MemberCard findByCardNumberAndDeleted(String cardNumber) {
        return MemberCard.find("cardNumber = ? and deleted = ?", cardNumber, DeletedStatus.UN_DELETED).first();
    }

    public static MemberCard findByCardNumberAndDeletedAndEnabled(String cardNumber) {
        return MemberCard.find("cardNumber = ? and deleted = ? and enabled = ?", cardNumber, DeletedStatus.UN_DELETED, true).first();
    }

    /**
     * 根据 用户和会馆查询会员卡
     * @param userId
     * @param venueId
     * @return
     */
    public static MemberCard findByUserAndVenue(Long userId , Long venueId ,  Date searchDate) {
        Logger.info("user.id :" + userId + "venueId :" + venueId + "searchDate :" + DateUtil.dateToString(searchDate, "yyyy-MM-dd HH:mm:ss"));
//        List<MemberCard> memberCardList =  find.where().join("user").join("venue")
//                .eq("user.id", userId)
//                .eq("venue.id", venueId)
//                .eq("deleted" , DeletedStatus.UN_DELETED)
//                .eq("enabled" , true)
//                .lt("beginAt", searchDate)
//                .gt("endAt", searchDate)
//                .findList();
//        Logger.info("获取到的会员卡数量 :" + memberCardList.size());
//        return getFirst(memberCardList);
        return MemberCard.find("user.id = ? and venue.id = ? and deleted = ? and enabled = ? and beginAt <= ? and endAt >= ?",
                userId, venueId, DeletedStatus.UN_DELETED, true, searchDate, searchDate).first();
    }

    /**
     * 根据 用户和会馆查询会员卡
     * @param userId
     * @param venueId
     * @return
     */
    public static MemberCard findByUserAndVenue(Long userId , Long venueId) {
//        List<MemberCard> memberCardList =  find.where().join("user").join("venue")
//                .eq("user.id", userId)
//                .eq("venue.id", venueId)
//                .eq("deleted", DeletedStatus.UN_DELETED)
//                .eq("enabled" , true)
//                .findList();
//        return getFirst(memberCardList);
        return MemberCard.find("user.id = ? and venue.id = ? and deleted = ? and enabled = ?",
                userId, venueId, DeletedStatus.UN_DELETED, true).first();

    }

//    private static MemberCard getFirst(List<MemberCard> memberCards) {
//        if (memberCards !=null && memberCards.size() >0 ) {
//            return memberCards.get(0);
//        }
//        return null;
//    }

    public MemberCard() {
        super();
    }

    /**
     * //TODO  没有过滤 有效日期
     * 查找指定场馆 指定人的会员卡
     *
     * @param userPhone  用户联系电话
     * @param venuePhone 场馆联系电话
     * @return
     */
    public static MemberCard findByUserAndVenue(String userPhone, String venuePhone) {
//        List<MemberCard> memberCardList = MemberCard.find
//                .where()
//                .join("user")
//                .join("venue")
//                .eq("deleted" , DeletedStatus.UN_DELETED)
//                .eq("deleted", DeletedStatus.UN_DELETED)
//                .eq("user.phone", userPhone)
//                .eq("venue.contactTel", venuePhone)
//                .findList();
//        return getFirst(memberCardList);
        return MemberCard.find("deleted = ? and user.phone = ? and venue.contactTel",
                DeletedStatus.UN_DELETED, userPhone, venuePhone).first();
    }

    /**
     * 查找指定场馆 指定人的会员卡
     * @return
     */
    public static MemberCard findByUserAndVenue(User user, Merchant merchant) {
        return MemberCard.find("deleted = ? and user = ? and merchant = ? and type = ?", DeletedStatus.UN_DELETED, user, merchant, MemberCardType.RECHARGE).first();
    }

    public static MemberCard byId(Long id) {
        return MemberCard.findById(id);
    }

    /**
     * 检查该会员卡是否可以支付
     *
     * @param MemberCard
     * @param type
     * @return
     */
    public boolean checkCanPay(MemberCard MemberCard, String type) {

        return true;//TODO
    }

    public boolean payMemberCard(MemberCard MemberCard, String type) {
        //TODO

        if ("byTimes".equals(type)) {
            this.save();
        }
        if ("byAmount".equals(type)) {
            this.amount.subtract(MemberCard.amount);
            this.save();
        }
        return true;
    }

    public boolean recharge(BigDecimal amount, String createdBy) {
        RechargeRecord.create(amount, createdBy, this);
        if(amount != null) {
            this.amount = this.amount.add(amount);
        }
        Logger.info("会员卡充值：cardNo:" + this.cardNumber + " , amount: " + this.amount);
        this.save();
        return true;
    }

    public boolean recharge(BigDecimal amount, BigDecimal giveAmount , BigDecimal shopAmount , String createdBy) {
        RechargeRecord.create(amount, giveAmount , shopAmount, createdBy, this);
        this.save();
        return true;
    }

    public static Map<String , String> checkByCardNumber(String cardNumber) {
        Map<String , String> resultMap =  new HashedMap();
        MemberCard card = findByCardNumberAndDeletedAndEnabled(cardNumber);
        if(card == null) {
            card = MemberCard.findByCardNumberAndEnabled(cardNumber);
            if(card == null) {
                card = MemberCard.findByCardNumberAndDeleted(cardNumber);
                if(card == null) {
                    card = MemberCard.findByCardNumber(cardNumber);
                    if(card == null) {
                        resultMap.put("success" , "error");
                        resultMap.put("msg", "该卡不存在，请联系前台开卡!");
                    } else {
                        resultMap.put("success" , "error");
                        resultMap.put("msg", "该卡已被删除或被冻结，请联系管理员!");
                    }
                } else {
                    resultMap.put("success" , "error");
                    resultMap.put("msg", "该卡已被冻结，请联系前台进行解冻!");
                }
            } else {
                resultMap.put("success" , "error");
                resultMap.put("msg", "该卡已被删除或被冻结，请联系管理员!");
            }
        } else {
            resultMap.put("success" , "ok");
            resultMap.put("msg", "会员卡正常");
        }



//
//        MemberCard card = MemberCard.findByCardNumber(cardNumber);
//        if (card == null) {
//            resultMap.put("success" , "error");
//            resultMap.put("msg", "该卡不存在，请联系前台开卡!");
//        } else {
//            card = MemberCard.findByCardNumberAndEnabled(cardNumber);
//            if (card == null) {
//                resultMap.put("success" , "error");
//                resultMap.put("msg", "该卡已被冻结，请联系前台进行解冻!");
//            } else {
//                card = MemberCard.findByCardNumberAndDeleted(cardNumber);
//                if (card == null) {
//                    resultMap.put("success" , "error");
//                    resultMap.put("msg", "该卡已被删除，请联系系统开发人员!");
//                } else {
//                    card = MemberCard.findByCardNumberAndDeletedAndEnabled(cardNumber);
//                    if(card == null) {
//                        resultMap.put("success" , "error");
//                        resultMap.put("msg", "该卡已被删除或被冻结，请联系管理员!");
//                    }
//                    resultMap.put("success" , "ok");
//                }
//            }
//            resultMap.put("success" , "ok");
//            resultMap.put("msg", "会员卡正常");
//        }
        return resultMap;
    }

    /**
     * 冻结
     * @return
     */
    public boolean freeze(){
        Logger.info(" 会员卡冻结：cardNod" + this.cardNumber);
        this.enabled  = false;
        this.save();
        return true;
    }

    /**
     * 解除冻结
     * @return
     */
    public boolean unfreeze(){
        this.enabled  = true;
        this.save();
        return true;
    }

    public static boolean bind(MemberCard obj){
        MemberCard oldObj = MemberCard.byId(obj.id);
        oldObj.user = obj.user;
        oldObj.merchant = obj.merchant;
        oldObj.save();

        return true;
    }

    /**
     * 会员卡延期
     *
     * @param date
     * @return
     */
    public boolean yanqiTo(Date date) {
        if(this.beginAt == null) {
            this.beginAt = this.createdAt;
        }
        this.endAt = date;
        this.save();
        DelayRecord.create(date, "管理员", this);
        return true;
    }

    /**
     * 判断是否一个场馆内唯一的用户会员卡
     * @param user
     * @param merchant
     * @return
     */
    public static boolean isUnique(User user, Merchant merchant) {
        long count = 0;
        if(user!= null && merchant != null) {
            count = MemberCard.count("user = ? and venue = ? and deleted = ?",
                    user, merchant, DeletedStatus.UN_DELETED);
        }
        return count > 0;
    }

//    /**
//     * 更新对象.
//     */
//    public static MemberCard update(Long id, MemberCard newObject) {
//        MemberCard memberCard = MemberCard.findById(id);
//        if (memberCard == null) {
//            return null;
//        }
//        memberCard.cardNumber = newObject.cardNumber;
//        memberCard.cardPassword = newObject.cardPassword;
//        memberCard.user = newObject.user;
//        memberCard.merchant = newObject.merchant;
//        memberCard.beginAt = newObject.beginAt;
//        memberCard.endAt = newObject.endAt;
//        memberCard.onlySelf = newObject.onlySelf;
//        memberCard.amount = newObject.amount;
//        memberCard.createdAt = newObject.createdAt;
//        memberCard.updatedAt = newObject.updatedAt;
//        memberCard.save();
//        return memberCard;
//    }

    /**
     * 按Company值更新指定ID的company.
     */
    public static void update(Long id, MemberCard newObject) {
        MemberCard oldMerchant = MemberCard.findById(id);
        BeanCopy.beans(newObject, oldMerchant).ignoreNulls(true).copy();
        oldMerchant.save();
    }
}
