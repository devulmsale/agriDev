/*
 * All Rights Reserved.
 * Since 20012 - 2013
 */
package models.base;

import models.constants.DeletedStatus;
import models.mert.Merchant;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import play.db.jpa.Model;
import play.modules.paginate.JPAExtPaginator;
import util.common.DateConvertUtils;
import util.xsql.XsqlBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Tang Liqun
 */
@Entity
@Table(name = "weixin_menus")
public class WeixinMenu extends Model {
    private static final long serialVersionUID = 289109934185631L;

    //date formats
    public static final String FORMAT_CREATED_AT = "yyyy-MM-dd";
    public static final String FORMAT_UPDATED_AT = "yyyy-MM-dd";


    @ManyToOne
    @JoinColumn(name = "merchant_id")
    public Merchant merchant;

    /**
     * name.
     */
    @Column(name = "name")
    public String name;

    /**
     * key.
     */
    @Column(name = "menu_key")
    public String key;

    /**
     * 对应acctionType=url时的url.
     */
    @Column(name = "url")
    public String url;


    /**
     * 用于响应用户输入的消息key。
     * 样例：【影讯】【YQ】，查询是以大写字母去like方式找到
     * <p/>
     * 但这东西不应该是在菜单这吧？用于触发菜单吗？
     */
    @Deprecated
    @Column(name = "match_keys", length = 250, nullable = true)
    public String matchKeys;

    /**
     * createdAt.
     */
    @Column(name = "created_at")
    public Date createdAt;

    /**
     * 显示顺序，大的在前面。
     */
    @Column(name = "display_order")
    public Integer displayOrder;

    /**
     * 对应微信动作.
     * 可选值为： click/url ...
     */
    @Column(name = "action_type", length = 200)
    public String actionType;

    /**
     * updatedAt.
     */
    @Column(name = "updated_at")
    public Date updatedAt;

    /**
     * 删除标志.
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;

    @Transient
    public String getCreatedAtString() {
        return DateConvertUtils.format(createdAt, FORMAT_CREATED_AT);
    }

    public void setCreatedAtString(String value) {
        this.createdAt = DateConvertUtils.parse(value, FORMAT_CREATED_AT, Date.class);
    }

    @Transient
    public String getUpdatedAtString() {
        return DateConvertUtils.format(updatedAt, FORMAT_UPDATED_AT);
    }

    public void setUpdatedAtString(String value) {
        this.updatedAt = DateConvertUtils.parse(value, FORMAT_UPDATED_AT, Date.class);
    }


    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "parent_id", nullable = true)
    })
    public WeixinMenu parent;

    /**
     * 删除指定ID的WeixinMenu
     */
    public static void delete(Long id) {
        WeixinMenu weixinMenu = WeixinMenu.findById(id);
        weixinMenu.deleted = DeletedStatus.DELETED;
        weixinMenu.save();
    }

    /**
     * 按weixinMenus值更新指定ID的WeixinMenu.
     */
    public static void update(Long id, WeixinMenu weixinMenu) {
        WeixinMenu oldWeixinMenu = WeixinMenu.findById(id);
        oldWeixinMenu.name = weixinMenu.name;
        oldWeixinMenu.key = weixinMenu.key;
        oldWeixinMenu.url = weixinMenu.url;
        oldWeixinMenu.displayOrder = weixinMenu.displayOrder;
        oldWeixinMenu.actionType = weixinMenu.actionType;
        oldWeixinMenu.updatedAt = new Date();
        oldWeixinMenu.save();
    }

    /**
     * 分页查询.
     */
    public static JPAExtPaginator<WeixinMenu> findByCondition(Map<String, Object> conditionMap, String orderByExpress, int pageNumber, int pageSize) {
        StringBuilder xsqlBuilder = new StringBuilder("t.deleted=models.constants.DeletedStatus.UN_DELETED")
                .append("/~ and t.id = {id} ~/")
                .append("/~ and t.name like {name} ~/")
                .append("/~ and t.key like {key} ~/")
                .append("/~ and t.parent.id = {parentId} ~/")
                .append("/~ and t.operator.id = {operatorId} ~/")
                .append("/~ and t.createdAt = {createdAt} ~/")
                .append("/~ and t.updatedAt = {updatedAt} ~/");
        if (conditionMap.get("parentId") == null) {
            xsqlBuilder.append(" and t.parent is null");
        }
        XsqlBuilder.XsqlFilterResult result = new XsqlBuilder().generateHql(xsqlBuilder.toString(), conditionMap);
        JPAExtPaginator<WeixinMenu> weixinMenuPage = new JPAExtPaginator<>("WeixinMenu t", "t", WeixinMenu.class,
                result.getXsql(), conditionMap).orderBy(orderByExpress);
        weixinMenuPage.setPageNumber(pageNumber);
        weixinMenuPage.setPageSize(pageSize);
        weixinMenuPage.setBoundaryControlsEnabled(false);
        return weixinMenuPage;
    }


    public static List<WeixinMenu> findSubMenu(WeixinMenu menu) {
        return WeixinMenu.find("parent=? and deleted=? order by displayOrder desc",
                menu, DeletedStatus.UN_DELETED).fetch(5);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", this.id)
                .append("name", this.name)
                .append("key", this.key)
                .append("parent", this.parent)
                .append("createdAt", this.createdAt)
                .append("updatedAt", this.updatedAt)
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
        if (obj instanceof WeixinMenu == false) return false;
        if (this == obj) return true;
        WeixinMenu other = (WeixinMenu) obj;
        return new EqualsBuilder()
                .append(this.id, other.id)
                .isEquals();
    }

    public List<WeixinMenu> undeletedChildren() {
        return WeixinMenu.find("parent = ? and  ( deleted = ? or deleted is null)",
                this, DeletedStatus.UN_DELETED).fetch();
    }

    public static List<WeixinMenu> findTopMenu(Merchant merchant) {

        return WeixinMenu.find("merchant=? and deleted=? and parent is null order by displayOrder desc",
                merchant, DeletedStatus.UN_DELETED).fetch(3);
    }
}

