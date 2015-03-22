package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import net.jeedup.finance.model.Stock
import net.jeedup.message.Brokers
import net.jeedup.net.http.Request
import net.jeedup.nlp.sentiment.classify.TwitterClassifier
import net.jeedup.persistence.DB
import net.jeedup.persistence.sql.SqlDB
import net.jeedup.social.twitter.Tweet
import net.jeedup.social.twitter.TwitterService
import net.jeedup.web.Config
import net.jeedup.web.Endpoint
import rx.Subscriber

import static net.jeedup.net.http.Response.*
/**
 * User: zack
 * Date: 11/9/13
 */
@CompileStatic
class RootHandler {

    @Endpoint('stream')
    def stream(Map data) {

        TwitterClassifier classifier = new TwitterClassifier('/Users/zack/Downloads/sanders-twitter-0.2/')
        classifier.retrain()

        TwitterService.getInstance().stream(['dessert'])
        .subscribe(new Subscriber<Tweet>() {
            @Override
            void onCompleted() {
                println 'COMPLETE'
            }

            @Override
            void onError(Throwable e) {
                println 'ERROR'
                e.printStackTrace()
            }

            @Override
            void onNext(Tweet tweet) {
                if (tweet)
                    println classifier.classify(tweet.text).name + ': ' + tweet.text
            }
        })
        HTML('OK')
    }

    @Endpoint('q')
    def q(Map data) {
        /*
        MessageBroker broker = Brokers.getInstance().named("mainQueue")
        broker.observe()
        .subscribe({ s ->
            System.out.println("Got: " + s)
            Thread.sleep(1000)
        }, { Throwable throwable ->
            throwable.printStackTrace()
        })
        */
        SqlDB<Stock> db = DB.sql(Stock.class);
        db.createTable()
        HTML("OK")
    }
    @Endpoint('send')
    def send(Map data) {
        def broker = Brokers.getInstance().named("mainQueue")
        broker.sendMessage(data.m)
        HTML("Done")
    }

    @Endpoint('json')
    def json(Map data) {
        JSON(Config.getInstance())
    }

    @Endpoint('text')
    def text(Request request) {
        TEXT(request.data)
    }

    @Endpoint('admin')
    def admin(Map data) {
        HTML(null, 'admin/admin')
    }
    @Endpoint('db')
    def db(Map data) {
        //SqlDB<User> db = DB.sql(User.class)
        /*
        User user = User.get(8)
        user.username = 'fooey ' + System.currentTimeMillis()
        user.save()
        JSON(User.get(8))
        */

        SqlDB<Stock> db = DB.sql(Stock.class)
        Stock stock = db.get('AAPL')
        JSON(stock)
    }
}
