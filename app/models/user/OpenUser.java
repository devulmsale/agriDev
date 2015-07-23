package models.user;

import models.constants.DeletedStatus;
import models.mert.Merchant;
import models.order.User;
import models.user.enums.SourceSite;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户的OpenId信息.
 */
@Entity
@Table(name = "open_users")
public class OpenUser extends Model{

    private static final long serialVersionUID = 198334973362L;

    /**
     * 对应的用户.
     */
    @JoinColumn(name = "user_id", nullable = true)
    @ManyToOne
    public User user;

    /**
     * 来源网站.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "source_site")
    public SourceSite sourceSite;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    public Merchant merchant;

    /**
     * OpenId.
     */
    @Column(name = "open_id")
    public String openId;

    /**
     * 来源网站的refreshToken.
     */
    @Column(name = "refresh_token")
    public String refreshToken;

    /**
     * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知.
     */
    @Column(name = "sex")
    public Integer sex;

    /**
     * 用户呢称.
     */
    @Column(name = "nickname")
    public String nickname;

    /**
     * 用户个人资料填写的省份.
     */
    @Column(name = "province")
    public String province;

    /**
     * 普通用户个人资料填写的城市.
     */
    @Column(name = "city")
    public String city;

    /**
     * 国家，如中国为CN.
     */
    @Column(name = "country")
    public String country;

    /**
     * 用户头像地址.
     */
    @Column(name = "head_image_url")
    public String headImageUrl;

    /**
     * 用户扩展信息，json 数组.
     */
    @Column(name = "addition_info")
    public String additionInfo;

    /**
     * 创建时间.
     */
    @Column(name = "created_at")
    public Date createdAt;

    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;

    /**
     * 分页查询
     *
     */
    public static JPAExtPaginator<OpenUser> findByCondition(Map<String, Object> conditionMap, String OpenUserByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder("t.deleted=models.constants.DeletedStatus.UN_DELETED")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.user.id = {userId} ~/")
                .append("/~ and t.merchant.id = {merchantId} ~/")
                .append("/~ and t.sourceSite = {sourceSite} ~/")
                .append("/~ and t.openId = {openId} ~/")
                .append("/~ and t.refreshToken = {refreshToken} ~/")
                .append("/~ and t.sex = {sex} ~/")
                .append("/~ and t.nickname = {nickname} ~/")
                .append("/~ and t.province = {province} ~/")
                .append("/~ and t.city = {city} ~/")
                .append("/~ and t.country = {country} ~/")
                .append("/~ and t.headImageUrl.id = {headImageUrl} ~/")
                .append("/~ and t.additionInfo = {additionInfo} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/");

        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<OpenUser> openUserPage = new JPAExtPaginator<OpenUser>("OpenUser t", "t", OpenUser.class,
                result.getXsql(), conditionMap).orderBy(OpenUserByExpress);
        openUserPage.setPageNumber(pageNumber);
        openUserPage.setPageSize(pageSize);
        openUserPage.setBoundaryControlsEnabled(false);
        return openUserPage;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", this.id)
                .append("user", this.user)
                .append("merchant", this.merchant)
                .append("sourceSite", this.sourceSite)
                .append("openId", this.openId)
                .append("refreshToken", this.refreshToken)
                .append("sex", this.sex)
                .append("nickname", this.nickname)
                .append("province", this.province)
                .append("city", this.city)
                .append("country", this.country)
                .append("headImageUrl", this.headImageUrl)
                .append("additionInfo", this.additionInfo)
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
        if(obj instanceof OpenUser == false)
            return false;
        if(this == obj) return true;
        OpenUser other = (OpenUser)obj;
        return new EqualsBuilder()
                .append(this.id, other.id)
                .isEquals();
    }




    /**
     * 按openId查找OpenUser.
     */
    public static OpenUser findByOpenId(SourceSite sourceSite, String openId) {
//        return find.where().eq("sourceSite", sourceSite).eq("openId", openId).orderByAsc("id").findUnique();
        return find("sourceSite = ? and openId = ?", sourceSite , openId).first();
    }

    /**
     * 创建指定站点和openId对应的OpenUser，并建立User对象.
     */
    public static OpenUser createByOpenId(SourceSite sourceSite, String openId, String refreshToken) {
        User user = new User();
        user.createdAt = new Date();
        user.loginName = sourceSite.toString() + openId;
        user.validated = Boolean.TRUE;
        user.save();
        OpenUser openUser = new OpenUser();
        openUser.openId = openId;
        openUser.sourceSite = sourceSite;
        openUser.createdAt = new Date();
        openUser.refreshToken = refreshToken;
        openUser.user = user;
        openUser.save();
        return openUser;
    }

    public static List<OpenUser> orderByCreateAt() {

//        return OpenUser.find.where().orderByDesc("createdAt").findList();
        return OpenUser.find("order by createdAt desc").fetch();
    }

    /**
     * 更新refreshToken.
     */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        this.save();
    }

    /**
     * 按user找到对应的OpenUser.
     */
    public static OpenUser findByUser(User user) {
//        return find.where().eq("user", user).findUnique();
        return find("user = ?", user).first();
    }



}
