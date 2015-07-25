package models.mert;

import jodd.bean.BeanCopy;
import models.common.ChinaPhone;
import models.constants.DeletedStatus;
import models.mert.enums.MerchantStatus;
import models.mert.enums.MerchantUserStatus;
import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.MinLength;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.data.validation.*;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;
import util.common.RandomNumberUtil;
import util.xsql.XsqlBuilder;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

/**
 * 商户操作用户.
 */
@Entity
@Table(name = "merchant_users")
public class MerchantUser extends Model {

    public static final String NOTSETPASSWORD = "!&NOTSETPASSWORD!";
    public static final  String LOGIN_ID             = "loginId"; //登陆ID
        public static final  String LOGIN_NAME           = "loginName"; //登陆密码
//    public static final  String LOST_USER_ID         = "lostUserId"; // 找回密码时候用到的Session
    public static final  String LOGIN_SESSION_USER   = "LoginUser_";
//    public static final  String PHONE_VALIDATE_TIMES = "Phone_Validate_Times";

    /**
     * 登录名.
     */
    @Required (message = "登录名称长度为4--10位")   //必填项目
    @MinLength(value = 4 , message = "不能低于4位有效数字")
    @MaxLength(value = 10 , message = "不能大于10位有效数字")
    @Column(name = "login_name", length = 20)
    public String loginName;

    /**
     * 显示名
     */
    @Column(name="show_name", length = 20)
    public String showName;

    /**
     * 手机号.
     */
    @Required(message = "请输入手机号")
    @CheckWith(value = ChinaPhone.class , message = "手机号码不正确")
    @Unique(message = "您输入的手机号已存在")
    @Column(name = "mobile", length = 20)
    public String mobile;

    /**
     * 加密后密码.
     */
    @Column(name = "encrypted_password", length = 50)
    public String encryptedPassword;


    /**
     * 加密盐.
     * 12位随机字符.
     */
    @Column(name = "password_salt", length = 20)
    public String passwordSalt;

    @Column(name = "created_at")
    public Date createdAt;

    @Column(name = "updated_at")
    public Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    public Merchant merchant;


    /**
     * 注册或登录时 暂时用的 password
     */
    @Required (message = "登录密码长度为6--12位")   //必填项目
    @MinLength(value = 6 , message = "不能低于6位有效数字")
    @MaxLength(value = 12 , message = "不能大于12位有效数字")
    public String tmpPassword;


    @Transient
    public String confirmPassword;

    @Transient
    public String oldPassword;
    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;
    /**
     * 员工状态.
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public MerchantUserStatus status;
    /**
     * 微信OpenId.
     * 通过OpenId，我们可以识别微信发起者对应的SupplierUser.
     */
    @Column(name = "weixin_open_id")
    public String weixinOpenId;

    /**
     * 更新员工信息
     * @param id
     * @param newObject
     */
    public static MerchantUser update(long id, MerchantUser newObject) {
        MerchantUser merchantUser = MerchantUser.findById(id);
        BeanCopy.beans(newObject, merchantUser).ignoreNulls(true).copy();
        return merchantUser.save();
    }

    public void updatePassword(String password) {
        this.passwordSalt = RandomNumberUtil.generateRandomChars(12);
        this.encryptedPassword = DigestUtils.md5Hex(password + this.passwordSalt);
    }



    public static MerchantUser findValidUser(Long userId) {
        return find("id = ? and merchant.status = ?", userId, MerchantStatus.OPEN).first();
    }
    /**
     * 使用密码登录.
     * @param userName
     * @param password
     * @return 是否登录成功.
     */
    public static Boolean login(String userName, String password) {
        if (StringUtils.isBlank(password)) {
            Logger.info("传入密码为空，登录失败");
            return false;
        }
        MerchantUser user = MerchantUser.find("loginName=? or mobile=? order by id", userName, userName).first();
        if (user == null) {
            Logger.info("找不到指定用户名(%s)对应的商户操作员.", userName);
            return false;
        }
        String hashPassword = DigestUtils.md5Hex(password + user.passwordSalt);
        if (!hashPassword.equals(user.encryptedPassword)) {
            Logger.info("密码不匹配，user.encryptedPassword=%s, hashPassword=%s", user.encryptedPassword, hashPassword);
            return false;
        }
        return true;
    }

    public static MerchantUser findByLoginNameAndPassword(String userName , String password) {
        if (StringUtils.isBlank(password)) {
            Logger.info("传入密码为空，登录失败");
            return null;
        }
        MerchantUser user = MerchantUser.find("loginName=? or mobile=? order by id", userName, userName).first();
        if (user == null) {
            Logger.info("找不到指定用户名(%s)对应的商户操作员.", userName);
            return null;
        }
        // TODO  暂时屏蔽掉密码
//        String hashPassword = DigestUtils.md5Hex(password + user.passwordSalt);
//        if (!hashPassword.equals(user.encryptedPassword)) {
//            Logger.info("密码不匹配，user.encryptedPassword=%s, hashPassword=%s", user.encryptedPassword, hashPassword);
//            return null;
//        }
        return user;
    }

    public static void delete(Long id){
        MerchantUser merchantUser=MerchantUser.findById(id);
        if(merchantUser != null){
            merchantUser.deleted=DeletedStatus.DELETED;
            merchantUser.save();
        }
    }

    /**
     * 分页查询.
     */
    public static JPAExtPaginator<MerchantUser> findByCondition(
            Map<String, Object> conditionMap, String orderByExpress,
            Integer pageNumber, Integer pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder("1=1 ")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.loginName like {searchName} ~/")
                .append("/~ and t.mobile = {mobile} ~/")
                .append("/~ and t.showName = {showName} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/")
                .append("/~ and t.updatedAt = {updatedAt} ~/")
                .append("/~ and t.merchant.id = {merchantId} ~/")
                .append("/~ and t.deleted = {deleted} ~/");
        XsqlBuilder.XsqlFilterResult result = new XsqlBuilder().generateHql(
                xsqlBuilder.toString(), conditionMap);
        Logger.info("films.xsql=" + result.getXsql());
        JPAExtPaginator<MerchantUser> merchantUserPage = new JPAExtPaginator<>("MerchantUser t", "t",
                MerchantUser.class, result.getXsql(), conditionMap)
                .orderBy(orderByExpress);
        merchantUserPage.setPageNumber(pageNumber);
        merchantUserPage.setPageSize(pageSize);
        merchantUserPage.setBoundaryControlsEnabled(false);
        return merchantUserPage;
    }
}
