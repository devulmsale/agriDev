package util.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import util.gson.annotation.NotSerialize;

/**
 * 基于NotSerialize标记进行排队Gson序列化的策略.
 */
public class SimpleExclusionStrategy implements ExclusionStrategy {

    private final Class<?> typeToSkip;

    public SimpleExclusionStrategy() {
        this.typeToSkip = null;
    }

    public SimpleExclusionStrategy(Class<?> typeToSkip) {
        this.typeToSkip = typeToSkip;
    }

    public boolean shouldSkipClass(Class<?> clazz) {
        return (clazz == typeToSkip);
    }

    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(NotSerialize.class) != null;
    }

}
