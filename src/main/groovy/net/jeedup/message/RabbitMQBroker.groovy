package net.jeedup.message
import com.rabbitmq.client.*
import net.jeedup.coding.BSON
import rx.Observable
import rx.Subscription
import rx.functions.Func1
import rx.subscriptions.Subscriptions

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
/**
 * Created by zack on 2/18/14.
 */
public class RabbitMQBroker<T> implements MessageBroker<T> {

    private ConnectionFactory factory;
    private Connection mainConnection;
    //private GenericObjectPool channelInstances

    protected String exchangeName;
    protected String routingKey;

    private String name;
    public String host;
    public int port;
    public String username;
    public String password;

    protected ExecutorService executorService;

    @Override
    public void connect() {
        factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        factory.setHost(host);
        factory.setPort(port);
        exchangeName = getName();
        routingKey = getName();

        executorService = Executors.newFixedThreadPool(100)
        mainConnection = factory.newConnection(executorService)
    }

    @Override
    public void disconnect() {
        if (mainConnection != null) {
            try {
                mainConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mainConnection = null;

        if (executorService != null) {
            executorService.shutdown();
        }
        executorService = null;
    }

    @Override
    public void sendMessage(T t) {
        byte[] message = BSON.encode(t, true);
        Channel channel = newChannelInstance();
        try {
            channel.basicPublish(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    @Override
    public Observable<T> observe() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            void call(Subscriber<? super T> subscriber) {
                Channel channel = newChannelInstance()
                QueueingConsumer consumer = new QueueingConsumer(channel)
                channel.basicConsume(getName(), false, consumer)

                Thread.start {
                    while (true) {
                        //an exception thrown here will exit the loop
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery()
                        long tag = delivery.getEnvelope().getDeliveryTag()
                        try {
                            //T t = RabbitMQBroker.decode(delivery.getBody())
                            T t = (T)BSON.decodeObject(delivery.getBody(), String.class, true)
                            subscriber.onNext(t)
                            channel.basicAck(tag, false)
                        } catch (Exception e) {
                            channel.basicNack(tag, false, true)
                            subscriber.onError(e)
                        }
                    }
                }
            }
        })
    }
    */

    public Observable<T> observe() {
        return Observable.create(new Func1<rx.Observer, Subscription>() {
            public Subscription call(rx.Observer observer) {
                Channel channel = newChannelInstance();
                QueueingConsumer consumer = new QueueingConsumer(channel)
                channel.basicConsume(getName(), false, consumer)

                Thread.start {
                    while (true) {
                        //an exception thrown here will exit the loop
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery()
                        long tag = delivery.getEnvelope().getDeliveryTag()
                        try {
                            T t = BSON.decodeObject(delivery.getBody(), String.class, true)
                            observer.onNext(t)
                            channel.basicAck(tag, false)
                        } catch (Exception e) {
                            channel.basicNack(tag, false, true)
                            observer.onError(e)
                        }
                    }
                }
                return Subscriptions.empty();
            };
        } as Observable.OnSubscribe<T>);
    }

    private T decode(byte[] body) {
        return (T)BSON.decodeObject(body, String.class, true)
    }

    public String getName() {
        return name
    }

    public void setName(String n) {
        this.name = n
    }

    private Channel newChannelInstance() {
        Channel channel = mainConnection.createChannel()
        channel.exchangeDeclare(exchangeName, 'direct', true);
        channel.queueDeclare(getName(), true, false, false, null)
        channel.queueBind(getName(), exchangeName, routingKey)
        int prefetchCount = 1
        channel.basicQos(prefetchCount);
        return channel;
    }
}
