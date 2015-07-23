package util.common;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 对象属性差异比较.
 */
public class PropDiffItem {
    /**
     * 属性名.
     */
    public String prop;

    public String oldValue;

    public String newValue;

    public PropDiffItem(String prop, String oldValue, String newValue) {
        this.prop = prop;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public PropDiffItem(String prop, Long oldValue, Long newValue) {
        this.prop = prop;
        this.oldValue = String.valueOf(oldValue);
        this.newValue = String.valueOf(newValue);
    }

    public PropDiffItem(String prop, Boolean oldValue, Boolean newValue) {
        this.prop = prop;
        this.oldValue = String.valueOf(oldValue);
        this.newValue = String.valueOf(newValue);
    }

    public PropDiffItem(String prop, Enum<?> oldValue, Enum<?> newValue) {
        this.prop = prop;
        this.oldValue = String.valueOf(oldValue);
        this.newValue = String.valueOf(newValue);
    }


    public PropDiffItem(String prop, Integer oldValue, Integer newValue) {
        this.prop = prop;
        this.oldValue = String.valueOf(oldValue);
        this.newValue = String.valueOf(newValue);
    }


    public PropDiffItem(String prop, BigDecimal oldValue, BigDecimal newValue) {
        this.prop = prop;
        this.oldValue = String.valueOf(oldValue);
        this.newValue = String.valueOf(newValue);
    }

    public PropDiffItem(String prop, Date oldValue, Date newValue) {
        this.prop = prop;
        this.oldValue = DateUtil.dateToString(oldValue, "yyyy-MM-dd HH:mm:ss");
        this.newValue = DateUtil.dateToString(newValue, "yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("prop", prop).
                append("oldValue", oldValue).
                append("newValue", newValue).
                toString();
    }
}
