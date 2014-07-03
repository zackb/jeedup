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

    private static final String API_URL = 'https://api.twitter.com/1.1/'
    private static final String API_STREAM_URL = 'https://stream.twitter.com/1.1/statuses/filter.json?track='
    private static final String API_LOOKUP_URL = API_URL + 'statuses/lookup.json?id='

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
            String track = terms.collect { URLEncoder.encode(it, 'UTF-8')}.join(',')
            String url = sign(API_STREAM_URL + track)

            return HTTP.streamLines(url)
                .map(new Func1<String, Tweet>() {
                    public Tweet call(String line) {
                        Tweet tweet = JSON.decodeObject(line, Tweet.class)
                        return tweet
                    }
                })
        } catch (Exception e) {
            return Observable.error(e)
        }
    }

    public List<Tweet> lookupStatuses(List<String> statusIds) {
        String url = API_LOOKUP_URL + statusIds.join(',') + '&include_entities=false&trim_user=true'
        url = sign(url)
        String data = HTTP.get(url)
        List<Tweet> result = JSON.decodeList(data, Tweet.class)
        return result
    }

    private String sign(String url) {
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        consumer.setTokenWithSecret(token, tokenSecret)
        return consumer.sign(url)
    }
}
