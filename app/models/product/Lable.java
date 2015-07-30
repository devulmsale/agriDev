package models.product;

import models.constants.DeletedStatus;
import models.operate.OperateUser;
import net.sf.oval.constraint.MinLength;
import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by upshan on 15/7/28.
 */
@Entity
@Table(name = "lables")
public class Lable extends Model {

    /**
     * 标签名称
     */
    @Required(message="标签名称不能为空")
    @MinSize(value = 2 , message = "不能少于2个字符")
    @MaxSize(value = 4 , message = "不能多于4个字符")
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


    /**
     * 分页查询.
     */
    public static JPAExtPaginator<Lable> findByCondition(Map<String, Object> conditionMap, String orderByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder(" t.deleted=models.constants.DeletedStatus.UN_DELETED ")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.name like {searchName} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/");

        util.xsql.XsqlBuilder.XsqlFilterResult result = new util.xsql.XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<Lable> resultPage = new JPAExtPaginator<Lable>("Lable t", "t", Lable.class,
                result.getXsql(), conditionMap).orderBy(orderByExpress);
        Logger.info("Lable Select SQL :" + result.getXsql() + "---");
        resultPage.setPageNumber(pageNumber);
        resultPage.setPageSize(pageSize);
        resultPage.setBoundaryControlsEnabled(false);
        return resultPage;
    }

    public static List<Lable> fingLable(){
        return Lable.find("deleted = ?",DeletedStatus.UN_DELETED).fetch();
    }
}
