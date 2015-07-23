package models.common;

import models.common.enums.AreaType;
import models.constants.DeletedStatus;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.List;

/**
 * 区域信息
 */
@Entity
@Table(name = "areas")
public class Area extends Model {

    private static final long serialVersionUID = 706109123113062L;

    public static final String DEFAULT_AREA_CITY = "002001";


    public String code;

    public String name;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    public Area parent;

    @Column(name = "display_order")
    public int displayOrder;

    /**
     * 是否是热门商圈
     */
    @Column(name = "popular_area")
    public Boolean popularArea = Boolean.FALSE;

    /**
     * 逻辑删除,0:未删除，1:已删除
     */
    @Enumerated(EnumType.ORDINAL)
    public DeletedStatus deleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "area_type")
    public AreaType areaType;




    public static List<Area> getAreasByType(AreaType type,Area parent) {
//        return Area.find
//                .where()
//                .eq("areaType", type)
//                .eq("parent",parent)
//                .eq("deleted", DeletedStatus.UN_DELETED)
//                .orderByAsc("displayOrder")
//                .findList();
        return Area.find("areaType = ? and parent = ? and deleted = ? order by displayOrder asc", type, parent, DeletedStatus.UN_DELETED).fetch();
    }

    public static Area getDefaultArea(){
//        return Area.find.where().eq("code", Area.DEFAULT_AREA_CITY).findUnique();
        return Area.find("code = ?", Area.DEFAULT_AREA_CITY).first();
    }
}
