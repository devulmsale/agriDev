package models.operate;

import models.constants.DeletedStatus;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 后台管理用户.
 */
@Entity
@Table(name="operate_users")
public class OperateUser extends Model {

    private static final long serialVersionUID = 21943311362L;
    public static final String CACHEKEY = "OpUser_";
    public static final  String LOGIN_ID             = "operator_user_loginId"; //登陆ID
    public static final  String LOGIN_NAME           = "operator_user_Name"; //登陆密码
    public static final  String LOST_USER_ID         = "operator_user_lostUserId"; // 找回密码时候用到的Session
    public static final  String LOGIN_SESSION_USER   = "operator_user_LoginUser_";


    /**
     * 关联商户
     */
    @JoinColumn(name = "operator_id", nullable = true)
    @ManyToOne
    public Operator operator;

    @Column(name = "login_name")
    public String loginName;

    public String mobile;

    @Column(name = "encrypted_password")
    public String encryptedPassword;

    @Column(name = "password_salt")
    public String passwordSalt;

    @Version
    @Column(name = "lock_version")
    public Integer lockVersion;

    @Column(name = "created_at")
    public Date createdAt;

    @Column(name = "updated_at")
    public Date updatedAt;

    @Column(name = "user_name")
    public String userName;

    public String email;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;
//
//    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
//    @JoinTable(name = "operate_users_roles",
//            inverseJoinColumns = @JoinColumn(name = "role_id"),
//            joinColumns = @JoinColumn(name = "user_id"))
    @Transient
    public List<OperateRole> roles;

//    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
//    @JoinTable(name = "operate_permissions_users",
//            inverseJoinColumns = @JoinColumn(name = "permission_id"),
//            joinColumns = @JoinColumn(name = "user_id"))
    @Transient
    public Set<OperatePermission> permissions;

    /**
     * 设置用户密码，更新掉对应的encryptedPassword和passwordSalt.
     * @param password 新密码
     * @return 当前用户
     */
    public OperateUser updatePassword(String password) {
        this.passwordSalt = RandomStringUtils.randomAlphanumeric(32);
        // 新密码
        this.encryptedPassword = DigestUtils.md5Hex(password + this.passwordSalt);
        return this;
    }

    /**
     * 根据公司 查看公司员工数量
     * @param operator
     * @return
     */
    public static Long countByOperator(Operator operator) {
        return OperateUser.count("operator = ?" , operator);
    }

    /**
     * 检查用户是否可以登录.
     * @param loginName 登录名
     * @param password 密码
     * @return 是否登录成功.
     */
    public static boolean login(String loginName, String password) {
        OperateUser operateUser = OperateUser.find("loginName = ?", loginName).first();
        String encryptedPassword = DigestUtils.md5Hex(password + operateUser.passwordSalt);
        return encryptedPassword.equals(operateUser.encryptedPassword);
    }


    /**
     * 检查用户是否可以登录.
     * @param loginName 登录名
     * @param password 密码
     * @return 是否登录成功.
     */
    public static OperateUser findByLoginNameAndPassword(String loginName, String password) {
        OperateUser operateUser = OperateUser.find("loginName = ?", loginName).first();
        if(operateUser != null ) {
            String encryptedPassword = DigestUtils.md5Hex(password + operateUser.passwordSalt);
            if (encryptedPassword.equals(operateUser.encryptedPassword)) {
                return operateUser;
            } else {
                return null;
            }
        }
        return null;
    }
}
