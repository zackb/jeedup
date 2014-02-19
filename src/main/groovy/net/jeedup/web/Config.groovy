package net.jeedup.web

import groovy.transform.CompileStatic
import net.jeedup.coding.JSON

/**
 * User: zack
 * Date: 11/12/13
 */
@CompileStatic
class Config implements Map<String, Object> {

    private static Config instance
    private Map<String, Object> data = [:]

    private Config() {
        this('config.json')
    }

    private Config(String filename) {
        URL resource = ClassLoader.getSystemResource(filename)
        data = JSON.decode(resource.text)
    }

    public static final Config getInstance() {
        if (!instance) {
            synchronized (Config.class) {
                if (!instance) {
                    instance = new Config()
                }
            }
        }
        return instance
    }

    public static final Map<String, Map> getDataSources() {
        return (Map<String, Map>) getInstance().get('datasources')
    }

    public static final Map<String, Map> getQueues() {
        return (Map<String, Map>) getInstance().get('queues')
    }

    public static final String host() {
        return (String)getInstance().get('host')
    }

    public static final int port() {
        return (int)getInstance().get('port')
    }

    int size() {
        return data.size()
    }

    boolean isEmpty() {
        return data.isEmpty()
    }

    boolean containsKey(Object key) {
        return data.containsKey(key)
    }

    boolean containsValue(Object value) {
        return data.containsValue(value)
    }

    Object get(Object key) {
        return data.get(key)
    }

    Object put(String key, Object value) {
        return data.put(key, value)
    }

    Object remove(Object key) {
        return data.remove(key)
    }

    void putAll(Map<? extends String, ?> m) {
        data.putAll(m)
    }

    void clear() {
        data.clear()
    }

    Set<String> keySet() {
        return data.keySet()
    }

    Collection<Object> values() {
        return data.values()
    }

    Set<Map.Entry<String, Object>> entrySet() {
        return data.entrySet()
    }
}
