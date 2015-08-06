package models.order;

import org.apache.commons.codec.digest.DigestUtils;
import play.Logger;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员管理.
 */
@Entity
@Table(name="users")
public class User extends Model {

    private static final long   serialVersionUID = 21943311362L;
    public static final  String CACHEKEY         = "OpUser_";
    public static final  String LOGIN_ID             = "user_loginId"; //登陆ID
    public static final  String LOGIN_NAME           = "user_Name"; //登陆密码
    public static final  String LOST_USER_ID         = "user_lostUserId"; // 找回密码时候用到的Session
    public static final  String LOGIN_SESSION_USER   = "user_LoginUser_";

    @Column(name = "login_name")
    public String  loginName;

    public String email;

    public String phone;

    @Column(name = "full_name")
    public String fullName;

    @Column(name = "password_salt")
    public String passwordSalt;

    @Column(name = "password_hash")
    public String passwordHash;

    @Column(name = "encrypted_password")
    public String encryptedPassword;

    public Boolean validated;

    @Column(name = "created_at")
    public Date createdAt;

    @Column(name = "updated_at")
    public Date updatedAt;

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
     * 地址
     */
    @Column(name = "address", length = 200)
    public String address;

    /**
     * 性别
     */
    @Column(name = "sex", length = 200)
    public String sex;


    @Column(name = "wx_open_id", length = 200)
    public String wxOpenId;

    public User() {
        super();
    }

    public User(String phone , String address , String name , String wxOpenId , String sex) {
        this.createdAt = new Date();
        this.loginName = phone;
        this.phone = phone;
        this.address = address;
        this.fullName = name;
        this.wxOpenId = wxOpenId;
        this.sex = sex;
        this.validated = true;
    }


    public static User findByPhone(String phone) {
        return User.find("phone = ?", phone).first();
    }


    public static User findByOpenId(String openId) {
        return User.find("wxOpenId = ?", openId).first();
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", loginName='").append(loginName).append('\'');
        sb.append(", fullName='").append(fullName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static Map<String, String> formSelectMap() {
        Logger.info("===formSelectMap");
        Map<String, String> selectMap = new HashMap<>();
        List<User> userList = User.findAll();
        Logger.info("===user list size:" + userList.size());
        for (User user : userList) {
            selectMap.put(user.id.toString(), user.loginName+"("+user.phone+")");
        }
        return selectMap;
    }

    /**
     * 检查用户是否可以登录.
     * @param loginName 登录名
     * @param password 密码
     * @return 是否登录成功.
     */
    public static User findByLoginNameAndPassword(String loginName, String password) {
        User user = User.find("loginName = ?", loginName).first();
        if(user != null ) {
            String encryptedPassword = DigestUtils.md5Hex(password + user.passwordSalt);
            if (encryptedPassword.equals(user.encryptedPassword)) {
                return user;
            } else {
                return null;
            }
        }
        return null;
    }
}
