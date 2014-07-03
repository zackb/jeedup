package net.jeedup.social.twitter

import groovy.transform.CompileStatic
import net.jeedup.coding.JSON
import net.jeedup.net.http.HTTP
import net.jeedup.web.Config
import oauth.signpost.OAuthConsumer
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import rx.Observable
import rx.Subscriber
import rx.functions.Func1

/**
 * Created by zack on 7/3/14.
 */
@CompileStatic
class TwitterService {

    private static TwitterService instance = new TwitterService()

    private static final String API_STREAM_URL = "https://stream.twitter.com/1.1/statuses/filter.json?track="

    private static String consumerKey
    private static String consumerSecret
    private static String token
    private static String tokenSecret

    private TwitterService() {
        super()
        Map<String, String> config = Config.getTwitter()
        consumerKey = config.consumerKey
        consumerSecret = config.consumerSecret
        token = config.token
        tokenSecret = config.tokenSecret
    }

    public static TwitterService getInstance() {
        return instance
    }


    public Observable<Tweet> stream(List<String> terms) {
        try {
            OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
            consumer.setTokenWithSecret(token, tokenSecret)
            String track = terms.collect { URLEncoder.encode(it, 'UTF-8')}.join(',')
            String url = API_STREAM_URL + track
            String signedUrl = consumer.sign(url);

            return HTTP.streamLines(signedUrl)
                .map(new Func1<String, Tweet>() {
                    public Tweet call(String line) {
                        Map json = JSON.decode(line);
                        Tweet tweet = new Tweet()
                        tweet.text = json.text
                        return tweet
                    }
                })
        } catch (Exception e) {
            return Observable.error(e)
        }
    }
}
