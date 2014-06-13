package net.jeedup.util

import groovy.transform.CompileStatic

/**
 * Created by zack on 6/13/14.
 */
@CompileStatic
class StringUtil {

    public static String removeHtml(String text) {
        return text?.replaceAll('<.*?>', ' ')?.trim()
    }
}
