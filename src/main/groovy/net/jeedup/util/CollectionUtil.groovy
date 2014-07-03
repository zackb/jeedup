package net.jeedup.util

import groovy.transform.CompileStatic

/**
 * Created by zack on 7/3/14.
 */
@CompileStatic
class CollectionUtil {
    public static <T> List<List<T>> partitionList(List<T> list, int l) {
        if (!list)  {
            return []
        }
        List<List<T>> parts = new ArrayList<List<T>>()
        int n = list.size()
        for (int i = 0; i < n; i += l) {
            parts.add(new ArrayList<T>(
                list.subList(i, Math.min(n, i + l)))
            )
        }
        return parts
    }
}
