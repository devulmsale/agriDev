package util.gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成Gson值
 */
public class ConvertHelper {

    /**
     * 转换列表对象.
     * @param models
     * @param convertor
     * @param <M>
     * @param <V>
     * @return
     */
    public static <M, V> List<V> convertList(List<M> models, ModelConvertor<M, V> convertor) {
        List<V> vList = new ArrayList<>();
        if (models != null && models.size() > 0) {
            for (M m : models) {
                vList.add(convertor.fromObject(m));
            }
        }
        return vList;
    }

}
