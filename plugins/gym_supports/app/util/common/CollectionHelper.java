package util.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Collection处理辅助类.
 */
public class CollectionHelper {

    /**
     * 转换List
     * @param sources 源List
     * @param transformer 目标List
     * @param <T> 源类型
     * @param <S> 目标类型
     * @return 转换后的List
     */
    public static <T, S> List<T> convertList(List<S> sources, Transformer<S, T> transformer) {
        if (sources == null) {
            return null;
        }

        List<T> targets = new ArrayList<>();
        for (S source : sources) {
            targets.add(transformer.transform(source));
        }

        return targets;
    }

    public static <T, S> Set<T> convertSet(Set<S> sources, Transformer<S, T> transformer) {
        if (sources == null) {
            return null;
        }

        Set<T> targets = new HashSet<>();
        for (S source : sources) {
            targets.add(transformer.transform(source));
        }

        return targets;
    }
}
