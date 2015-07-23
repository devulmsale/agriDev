package util.gson;

/**
 * 对象转换接口.
 * @author 唐力群
 */
public interface ModelConvertor<M, V> {

    /**
     * 把M对象(Model) 转换为V对象 (值对象)，以安全的进行网络传输、对象持久化等操作。
     * @param model 不安全的Model对象
     * @return 值对象
     */
    public V fromObject(M model);

}
