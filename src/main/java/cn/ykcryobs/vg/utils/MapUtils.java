package cn.ykcryobs.vg.utils;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author llykff
 */
public class MapUtils {

    public static <K, V> void merge(Map<K, V> source, Map<K, V> target, BiFunction<V, V, V> mergeFunction) {
        source.forEach((k, v) -> target.merge(k, v, mergeFunction));
    }
}
