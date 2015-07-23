package util.common;

/**
 * 类型转型接口.
 */
public interface Transformer<S, T> {
    T transform(S s);
}
