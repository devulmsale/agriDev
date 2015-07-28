package models.product;

import models.constants.DeletedStatus;
import models.operate.OperateUser;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by upshan on 15/7/28.
 */
@Entity
@Table(name = "lables")
public class Lable extends Model {

    /**
     * 标签名称
     */
    @Column(name = "name")
    public String name;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;

    /**
     * 添加人员
     */
    @JoinColumn(name = "operate_user_id")
    @ManyToOne
    public OperateUser operateUser;

    /**
     * 添加时间
     */
    @Column(name = "created_at")
    public Date createdAt;
}
