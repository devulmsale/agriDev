package models.operate;

import models.constants.DeletedStatus;
import org.apache.commons.lang.builder.ToStringBuilder;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * 运营商.
 */
@Entity
@Table(name = "operators")
public class Operator extends Model {

    private static final long serialVersionUID = 230901367311362L;

    @Column(unique = true)
    public String name;


    @Column(unique = true)
    public String code;

    @Column(name = "company_name")
    public String companyName;

    public String mobile;

    public String phone;

    @Column(name = "created_at")
    public Date createdAt;

    @Column(name = "updated_at")
    public Date updatedAt;

    public String email;

    public String remark;

    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;


    @Column(name = "created_by")
    public String createdBy;

    @Column(name = "updated_by")
    public String updatedBy;


    /**
     * 根据公司名称 查询公司
     * @param name
     * @return
     */
    public static Operator findByName(String name) {
        return Operator.find("name = ? and deleted = ?" , name , DeletedStatus.UN_DELETED).first();
    }


    public static void update(long id, Operator operator, String updatedBy) {
        Operator updatedOperator = Operator.findById(id);
        updatedOperator.name = operator.name;
        updatedOperator.code = operator.code;
        updatedOperator.mobile = operator.mobile;
        updatedOperator.email = operator.email;
        updatedOperator.phone = operator.phone;
        updatedOperator.updatedAt = new Date();
        updatedOperator.remark = operator.remark;
        updatedOperator.companyName = operator.companyName;
        updatedOperator.save();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("code", code)
                .toString();
    }

}
