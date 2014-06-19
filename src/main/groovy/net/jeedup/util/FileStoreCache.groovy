package net.jeedup.util

import groovy.transform.CompileStatic
import net.jeedup.coding.JSON
import net.jeedup.storage.FileStorageService

/**
 * Created by zack on 6/18/14.
 */
@CompileStatic
class FileStoreCache<T> {

    private final long expire
    private final Closure loader

    private FileStoreCache() {}

    public FileStoreCache(Closure loader, long expire) {
        super()
        this.loader = loader
        this.expire = expire
    }

    private static void put(String key, T value) {
        File file = FileStorageService.getTemporaryFile(key)
        if (file.exists()) {
            file.delete()
            file = FileStorageService.getTemporaryFile(key)
        }
        file.write(JSON.encode(value))
    }

    public T get(String key) {
        File file = FileStorageService.getTemporaryFile(key)
        T data
        if (!file.exists() || file.lastModified() < System.currentTimeMillis() - expire) {
            data = (T)loader.call(key)
            put(key, data)
        } else {
            data = JSON.decodeObject(file.text, T)
        }
        return data
    }
}
