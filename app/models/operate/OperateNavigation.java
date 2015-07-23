package models.operate;


import play.db.jpa.JPA;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.*;

//@Entity
//@Table(name = "operate_navigations")
public class OperateNavigation extends Model {
    
    private static final long serialVersionUID = 219113062L;

    @Column(name="nav_name")
    public String name;

    @Column(name="nav_text")
    public String text;

    public String description;

    public String action;

    public String url;

    public String labels;

    public boolean actived;

    /**
     * 用于生成Dev环境的topMenu及navigation时使用的baseUrl，如http://localhost:8080
     */
    @Column(name="dev_base_url")
    public String devBaseUrl;

    /**
     * 用于生成Prod环境的topMenu及navigation时使用的baseUrl，如http://admin.uhuila.net
     */
    @Column(name="prod_base_url")
    public String prodBaseUrl;

    @OrderColumn(name="display_order")
    public Integer displayOrder;

    @Column(name="application_name")
    public String applicationName;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "parent_id", nullable = true)
    public OperateNavigation parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, targetEntity = OperateNavigation.class)
    @OrderBy("displayOrder")
    public List<OperateNavigation> children;


    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "operate_navs_perms",
        inverseJoinColumns = @JoinColumn(name= "permission_id"),
        joinColumns = @JoinColumn(name = "navigation_id"))
    public Set<OperatePermission> permissions;


    public boolean hasLink() {
        return url != null || action != null;
    }

    /**
     * 加载版本，使用应用程序加载时间，在处理完成后，删除不是当前loadVersion的记录，以完成同步.
     */
    @Column(name = "load_version")
    public long loadVersion;

    @Column(name = "lock_version")
    public int lockVersion;

    @Column(name = "created_at")
    public Date createdAt;

    @Column(name = "updated_at")
    public Date updatedAt;



    /**
     * 删除不是当前loadVersion的记录，这样可以保证只有一个版本的rbac.xml的数据在数据库中.
     * @param applicationName
     * @param loadVersion
     */
    public static void deleteUndefinedNavigation(String applicationName, long loadVersion) {
        String hql = "select s from OperateNavigation s where s.applicationName= :applicationName " +
                "and s.loadVersion <> :loadVersion  order by parent DESC, id DESC";

        TypedQuery<OperateNavigation> query = JPA.em().createQuery(hql, OperateNavigation.class);
        query.setParameter("applicationName", applicationName);
        query.setParameter("loadVersion", loadVersion);
        List<OperateNavigation> list = query.getResultList();
        for (OperateNavigation nav : list) {
            nav.delete();
        }
    }

    /**
     * 得到所有子系统拼接在一起的顶级菜单.
     * @return
     */
    public static List<OperateNavigation> getTopNavigations() {
        String hql = "select n from OperateNavigation n where n.parent is null order by displayOrder";
        TypedQuery<OperateNavigation> query = JPA.em().createQuery(hql, OperateNavigation.class);
        return query.getResultList();
    }

    /**
     * 给定一个菜单名，查出所有上级菜单的序列。
     * 如: main => child => subchild
     * @param currentMenuName
     * @return
     */
    public static List<OperateNavigation> getNavigationParentStack(String applicationName, String currentMenuName) {

        if (currentMenuName == null) {
            // throw new IllegalAccessError("必须在Controller中定义" + ActiveNavigation.class.getName() + " Annotation。");
            return null;
        }

        Stack<OperateNavigation> stack = new Stack<>();

//        OperateNavigation nav = OperateNavigation.find.where().eq("applicationName", applicationName)
//                .eq("name", currentMenuName).findUnique();

        OperateNavigation nav = OperateNavigation.find("applicationName = ? and name = ?", applicationName, currentMenuName).first();

        if (nav == null) {
            return Collections.emptyList();
        }

        while(nav != null) {
            stack.push(nav);
            nav = nav.parent;
        }

        List<OperateNavigation> parentStackList = new ArrayList<>();
        while (!stack.isEmpty()) {
            parentStackList.add(stack.pop());
        }

        return parentStackList;
    }

    public static List<OperateNavigation> getSecondLevelNavigations(String applicationName, String navName) {
        List<OperateNavigation> parentStack = getNavigationParentStack(applicationName, navName);

        if (parentStack == null || parentStack.size() < 1) {
            return Collections.emptyList();
        }

        OperateNavigation topMenu = parentStack.get(0);

        return topMenu.children;
    }

    public static OperateNavigation findByName(String key) {
        return OperateNavigation.find("name", key).first();
    }
}
