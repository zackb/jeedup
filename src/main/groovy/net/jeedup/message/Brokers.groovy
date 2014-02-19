package net.jeedup.message

import groovy.transform.CompileStatic
import net.jeedup.web.Config

/**
 * Created by zack on 2/18/14.
 */
@CompileStatic
class Brokers {

    private static Brokers instance = null

    private final Map<String, MessageBroker> brokers = [:]

    static {
        getInstance()
    }

    private Brokers() {
        config()
    }

    public static Brokers getInstance() {
        if (!instance) {
            synchronized (Brokers.class) {
                if (!instance) {
                    instance = new Brokers()
                }
            }
        }

        return instance
    }

    private void config() {
        Config.getQueues().each { String name, Map data ->
            RabbitMQBroker broker = new RabbitMQBroker()
            broker.name = name
            broker.host = data.host ?: '127.0.0.1'
            broker.port = (int)data.port ?: 5672
            broker.username = data.username
            broker.password = data.password
            broker.connect()
            brokers[name] = broker
        }
    }

    public MessageBroker named(String name) {
        return brokers[name]
    }
}
