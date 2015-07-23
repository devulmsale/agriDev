package models.mert;

import models.mert.enums.MerchantStatus;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.db.jpa.Model;
import util.common.RandomNumberUtil;

import javax.persistence.*;
import java.util.Date;

/**
 * 商户操作用户.
 */
@Entity
@Table(name = "merchant_users")
public class MerchantUser extends Model {

    public static final String NOTSETPASSWORD = "!&NOTSETPASSWORD!";
    public static final  String LOGIN_ID             = "loginId"; //登陆ID
    //    public static final  String LOGIN_NAME           = "loginName"; //登陆密码
//    public static final  String LOST_USER_ID         = "lostUserId"; // 找回密码时候用到的Session
    public static final  String LOGIN_SESSION_USER   = "LoginUser_";
//    public static final  String PHONE_VALIDATE_TIMES = "Phone_Validate_Times";

    /**
     * 登录名.
     */
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

    @Transient
    public String confirmPassword;

    @Transient
    public String oldPassword;

    /**
     * 微信OpenId.
     * 通过OpenId，我们可以识别微信发起者对应的SupplierUser.
     */
    @Column(name = "weixin_open_id")
    public String weixinOpenId;

    /**
     * 更新操作员信息
     *
     * @param id           ID
     * @param merchantUser 用户信息
     */
    public static void update(long id, MerchantUser merchantUser) {
        MerchantUser updatedUser = MerchantUser.findById(id);

        if (StringUtils.isNotEmpty(merchantUser.encryptedPassword) &&
                !"******".equals(merchantUser.encryptedPassword) &&
                !merchantUser.encryptedPassword.equals(NOTSETPASSWORD)) {
            //随机码
            updatedUser.updatePassword(merchantUser.encryptedPassword);
        }
        updatedUser.loginName = merchantUser.loginName;
        updatedUser.showName = merchantUser.showName;
        updatedUser.mobile = merchantUser.mobile;
        updatedUser.updatedAt = new Date();
        updatedUser.save();
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
}
