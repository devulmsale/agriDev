package models.mert;

import cache.CacheCallBack;
import cache.CacheHelper;
import jodd.bean.BeanCopy;
import models.common.ChinaPhone;
import models.common.DateUtil;
import models.constants.DeletedStatus;
import models.mert.enums.MerchantStatus;
import models.operate.Oper;
import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.MinLength;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Index;
import play.Logger;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;
import util.xsql.XsqlBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商户.
 *
 * 一个商户对应一个公众号，公众号的配置由商户确定.
 *
 * <p/>
 * 一家商户可以有多个操作人员，但目标只做一个。
 * 商户开通功能使用MerchantFeature，商户自定义属性用MerchantProperty
 */
@Entity
@Table(name = "merchants")
public class Merchant extends Model {

    private static final long serialVersionUID = 5126870842118557757L;


    /**
     * 商户所属代理商
     */
    @ManyToOne
    @JoinColumn(name = "oper_id")
    public Oper oper ;


    /**
     * 佣金比例
     */
    @Required (message = "请输入佣金比例")
    @Column(name = "fee_rate")
    public Integer feeRate = 5;

    /**
     *结算周期 1:1天  2:2天
     */
    @Required (message = "请输入结算周期")
    @Column(name = "stlmt_period")
    public Integer stlmtPeriod;

    /**
     * 银行卡号
     */
    @Required (message = "请输入银行卡号")
    @Column(name = "bank_account_id")
    public String bankAccountId;

    /**
     * 开户人姓名
     */
    @Required (message = "请输入开户人姓名")
    @Column(name = "bank_account_name")
    public String bankAccountName;

    /**
     * 开户行名称
     */
    @Required (message = "请输入开户行名称")
    @Column(name = "bank_name")
    public String bankName;
    /**
     * 商户链接ID，用于外部网站链接，如微信回调URL。
     * 建议此linkId为12位随机数字
     */
    @Index(name = "link_id")
    @Column(name = "link_id", length = 12, unique = true)
    public String linkId;

    /**
     * 商户名称
     */
    @Required (message = "请输入商户名称")   //必填项目
    @MaxSize(value = 50 , message = "不能大于50位有效数字")
    @Column(name = "full_name", length = 50)
    public String fullName;

    /**
     * 名称缩写
     */
    @Required (message = "请输入商户简称")   //必填项目
    @MaxSize(value = 20 , message = "不能大于20位有效数字")
    @Column(name = "short_name", length = 20)
    public String shortName;

    /**
     * 商户联系电话
     */
    @Required(message = "请输入手机号")
    @CheckWith(value = ChinaPhone.class , message = "手机号码不正确")
    @Unique(message = "您输入的手机号已存在")
    @Column(name = "phone", length = 20)
    public String phone;

    /**
     * 联系人姓名.
     */
    @Column(name = "contact_name", length = 20)
    public String contactName;

    /**
     * 负责人手机号
     */
    @Column(name = "mobile", length = 20)
    public String mobile;

    /**
     * logo图片路径
     */
    @Column(name = "logo_path", length = 200)
    public String logoPath;

    /**
     * 商户备注.
     */
    @Column(name = "remark", length = 1000)
    public String remark;

    /**
     * 有效期，当前日期如果大于有效期，商户将不可用。交费后填写，如果不填写将不可用。
     */
    @Column(name = "expired_at")
    public Date expiredAt;

    /**
     * 该微信号是否认证 0未认证，1已认证
     */
    @Column(name = "is_auth")
    public Boolean isAuth;

    /**
     * 商户状态.
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public MerchantStatus status;

    /**
     * 地址
     */
    @Required (message = "请输入商户地址")   //必填项目
    @Column(name = "address")
    public String address; //地址

    /**
     * 联系人
     */
    @Required (message = "请输入联系人")   //必填项目
    @Column(name = "person")
    public String person;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;



    /**
     * 微信AppId
     */
    @Column(name = "weixin_app_id", length = 20)
    public String weixinAppId;

    /**
     * 微信AppSecret.
     */
    @Column(name = "weixin_app_secret", length = 50)
    public String weixinAppSecret;

    /**
     * 配置回调模式的Token.
     * <p/>
     * Token可由企业任意填写，用于生成签名.
     */
    @Column(name = "weixin_token", length = 50)
    public String weixinToken;

    /**
     * EncodingAESKey用于消息体的加密，是AES密钥的Base64编码.
     */
    @Column(name = "weixin_aes_key", length = 50)
    public String weixinAesKey;

    /**
     * 微信默认欢迎消息.
     */
    @Column(name = "weixin_default_message", length = 250, nullable = true)
    public String weixinDefaultMessage;

    @Column(name = "longitude", unique = false,
            nullable = true,
            insertable = true, updatable = true, length = 45)
    public String longitude = "0";  // 经度

    @Column(name = "latitude", unique = false,
            nullable = true,
            insertable = true, updatable = true, length = 45)
    public String latitude = "0";  // 纬度

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

    public static final String CACHEKEY         = "MERCHANT";
    public static final String CACHE_LINKID_KEY = "MERCHANT_LINKID";

    @Override
    public void _save() {
        super._save();
        CacheHelper.delete(CACHEKEY);
        CacheHelper.delete(CACHEKEY + this.id);
        CacheHelper.delete(CACHE_LINKID_KEY + this.linkId);
    }

    @Override
    public void _delete() {
        super._delete();
        CacheHelper.delete(CACHEKEY);
        CacheHelper.delete(CACHEKEY + this.id);
        CacheHelper.delete(CACHE_LINKID_KEY + this.linkId);
    }

    public static void update(Long id, Merchant newObject) {
        Merchant merchant=Merchant.findById(id);
        BeanCopy.beans(newObject, merchant).ignoreNulls(true).copy();
        merchant.save();
    }

    public static void delete(Long id){
        Merchant merchant=Merchant.findById(id);
        if (merchant != null){
            merchant.deleted=DeletedStatus.DELETED;
            merchant.save();
        }
    }



    /**
     * 分页查询.
     */
    public static JPAExtPaginator<Merchant> findByCondition(
            Map<String, Object> conditionMap, String orderByExpress,
            Integer pageNumber, Integer pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder(" deleted = models.constants.DeletedStatus.UN_DELETED ")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.fullName = {fullName} ~/")
                .append("/~ and t.shortName = {shortName} ~/")
                .append("/~ and t.fullName like {searchName} ~/")
                .append("/~ and t.phone = {phone} ~/")
                .append("/~ and t.contactName = {contactName} ~/")
                .append("/~ and t.mobile = {mobile} ~/")
                .append("/~ and t.deleted = {deleted} ~/")
                .append("/~ and t.status = {status} ~/");
        XsqlBuilder.XsqlFilterResult result = new XsqlBuilder().generateHql(
                xsqlBuilder.toString(), conditionMap);
        Logger.info("films.xsql=" + result.getXsql());
        JPAExtPaginator<Merchant> merchantPage = new JPAExtPaginator<>("Merchant t", "t",
                Merchant.class, result.getXsql(), conditionMap)
                .orderBy(orderByExpress);
        merchantPage.setPageNumber(pageNumber);
        merchantPage.setPageSize(pageSize);
        merchantPage.setBoundaryControlsEnabled(false);
        return merchantPage;
    }

    /**
     * 按linkId找到一个商户.
     *
     * @param linkId
     * @return
     */
    public static Merchant findByLinkId(final String linkId) {
        return CacheHelper.getCache(CacheHelper.getCacheKey(CACHE_LINKID_KEY + linkId, "FindByLinkId"), new CacheCallBack<Merchant>() {
            @Override
            public Merchant loadData() {
                return Merchant.find("linkId=? order by id", linkId).first();
            }
        });
    }

    /**
     * 查询当天过期的商户
     * @param searchDate
     * @return
     */
    public static List<Merchant> findByExpired(Date searchDate) {
        Date beginAt = DateUtil.getBeginOfDay(searchDate);
        Date endAt = DateUtil.getEndOfDay(searchDate);
        return Merchant.find("deleted = ? and expiredAt between ? and ?" , DeletedStatus.UN_DELETED , beginAt , endAt).fetch();
    }

    /**
     * 检查商户是否可用.
     * @return
     */
    public boolean isAvaliable() {
        /*
        return (this.status == MerchantStatus.OPEN
                && this.expiredAt != null && this.expiredAt.compareTo(new Date()) < 0)
                || this.status == MerchantStatus.TRIAL;
                */
        return this.status == MerchantStatus.OPEN;
    }
    public static Merchant findByName(String name) {
        return Merchant.find("fullName = ?",name).first();
    }


}
