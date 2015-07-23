/*
 * All Rights Reserved.
 * Since 20012 - 2013
 */
package models.base;

import jodd.bean.BeanCopy;
import models.base.enums.Gender;
import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.order.User;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;
import util.xsql.XsqlBuilder;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

/**
 * @author Tang Liqun
 */
@Entity
@Table(name = "weixin_users")
public class WeixinUser extends Model {
    private static final long serialVersionUID = 289109934185631L;

    /**
     * 所属商户.
     */
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    public Merchant merchant;

    /**
     * 关联User.
     */
    @OneToOne
    @JoinColumn(name = "user_id")
    public User user;

    /**
     * 微信用户名，fromUserName.
     * TODO: 加上索引.
     */
    @Column(name = "weixin_open_id", length = 50)
    public String weixinOpenId;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "deleted")
    public DeletedStatus deleted = DeletedStatus.UN_DELETED;

    /**
     * 是否订阅.
     * 用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
     * 可考虑通过一个定时任务查询当前公众号的用户订阅状态.
     */
    @Column(name = "subcribed")
    public Boolean subcribed;

    /**
     * 用户的昵称
     */
    @Column(name = "nick_name", length = 50)
    public String nickName;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", length = 10)
    public Gender sex;

    /**
     * 用户语言
     */
    @Column(name = "user_lang", length = 20)
    public String language;

    @Column(name = "city", length = 20)
    public String city;

    @Column(name = "country", length = 20)
    public String country;

    /**
     * 用户头像路径.
     * <p/>
     * 保存微信文件到本地.
     */
    @Column(name = "head_img_url", length = 200)
    public String headImgUrl;

    /**
     * 统一文件上传ID.
     */
    @Column(name = "head_ufid", length = 200)
    public String headUfid;


    /**
     * 用户关注时间.
     * 为时间戳。如果用户曾多次关注，则取最后关注时间
     */
    @Column(name = "subscribed_at")
    public Date subscribedAt;

    /**
     * 按weixinUser值更新指定ID的WeixinUser.
     */
    public static void update(Long id, WeixinUser weixinUser) {
        WeixinUser oldWeixinUser = WeixinUser.findById(id);
        BeanCopy.beans(weixinUser, oldWeixinUser).ignoreNulls(true).copy();
        oldWeixinUser.save();
    }

    /**
     * 删除指定ID的WeixinMenu
     */
    public static void delete(Long id) {
        WeixinUser weixinUser = WeixinUser.findById(id);
        weixinUser.deleted = DeletedStatus.DELETED;
        weixinUser.save();
    }


    /**
     * 查询出商户指定OpenId的用户.
     */
    public static WeixinUser findOrCreateMerchantWxUser(Merchant merchant, String openId) {
        WeixinUser weixinUser = WeixinUser.find("merchant.id=? and weixinOpenId=?", merchant.id, openId).first();
        if (weixinUser == null) {
            User user = User.findByOpenId(openId);
            if(user == null) {
                user = new User();
                user.createdAt = new Date();
                user.fullName = "匿名用户";
                user.validated = true;
                user.save();
            }

            weixinUser = new WeixinUser();
            weixinUser.user = user;
            weixinUser.merchant = Merchant.findById(merchant.id);
            weixinUser.weixinOpenId = openId;
            weixinUser.nickName = "匿名用户";
            weixinUser.subcribed = Boolean.FALSE;
            weixinUser.save();

        }
        return weixinUser;
    }

    /**
     * 根据 user查找WeixinUser
     * @param user
     * @return
     */
    public static WeixinUser findByUser(User user){
        return WeixinUser.find("user = ?" , user).first();
    }

    /**
     * 根据OpenId查询用户
     */
    public static WeixinUser findByOpenId(String openId) {
        return WeixinUser.find("weixinOpenId = ?" , openId).first();
    }

    /**
     * 分页查询.
     */
    public static JPAExtPaginator<WeixinUser> findByCondition(Map<String, Object> conditionMap, String orderByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder("t.deleted=models.constants.DeletedStatus.UN_DELETED")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.weixinId = {weixinId} ~/");
        XsqlBuilder.XsqlFilterResult result = new XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);

        JPAExtPaginator<WeixinUser> weixinUserPage = new JPAExtPaginator<>("WeixinUser t", "t", WeixinUser.class,
                result.getXsql(), conditionMap).orderBy(orderByExpress);
        weixinUserPage.setPageNumber(pageNumber);
        weixinUserPage.setPageSize(pageSize);
        weixinUserPage.setBoundaryControlsEnabled(false);
        return weixinUserPage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", this.id)
                .append("weixinId", this.weixinOpenId)
                .append("nickName", this.nickName)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.id)
                .append(this.weixinOpenId)
                .append(this.nickName)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WeixinUser == false) return false;
        if (this == obj) return true;
        WeixinUser other = (WeixinUser) obj;
        return new EqualsBuilder()
                .append(this.id, other.id)
                .append(this.weixinOpenId, other.weixinOpenId)
                .append(this.nickName, other.nickName)
                .isEquals();
    }
}

