package net.jeedup.storage

import groovy.transform.CompileStatic

/**
 * Created by zack on 6/18/14.
 */
@CompileStatic
class FileStorageService {

    private static final String TMP_DIR = '/tmp'

    public static File getTemporaryFile(String filename) {
        File file = new File(TMP_DIR + '/' + filename)
        return file
    }
}
