package net.jeedup.text

import groovy.transform.CompileStatic

/**
 * Created by zack on 6/13/14.
 */
@CompileStatic
class PhraseSet {

    public String title
    public List<String> urls

    public static PhraseSet newsSources() {
        PhraseSet result = new PhraseSet()
        result.title = 'News'
        //https://github.com/MLSahitya/DataStorage/blob/master/app/assets/SourceLinks/TopStories
        result.urls = [
            "http://www.npr.org/rss/rss.php?id=1001",
            //"http://pheedo.msnbc.msn.com/id/3032091/device/rss",
            "http://www.msnbc.com/feeds/latest",
            "http://rss.cnn.com/rss/cnn_topstories.rss",
            "http://newsrss.bbc.co.uk/rss/newsonline_world_edition/front_page/rss.xml",
            "http://www.nytimes.com/services/xml/rss/nyt/GlobalHome.xml",
            "http://feeds.reuters.com/reuters/topNews",
            "http://hosted.ap.org/lineups/TOPHEADS-rss_2.0.xml?SITE=TXHAR&SECTION=HOME",
            "http://online.wsj.com/xml/rss/3_7085.xml",
            "http://feeds.feedburner.com/euronews/en/world?format=xml",
            //"http://www.euronews.com/rss/euronews_en.xml",
            "http://russiatoday.com/Top_News.rss",
            "http://english.aljazeera.net/Services/Rss/?PostingId=2007731105943979989",
            "http://rss.feedsportal.com/c/32158/f/414206/index.rss",
            "http://news.sky.com/sky-news/rss/world-news/rss.xml",
            "http://rssfeeds.usatoday.com/usatoday-NewsTopStories",
            "http://rss.news.yahoo.com/rss/topstories",
            "http://news.google.com/news?pz=1&ned=us&hl=en&output=rss",
            //"http://feeds.washingtonpost.com/wp-dyn/rss/print/index_xml",
            "http://feeds.latimes.com/latimes/news?format=xml",
            "http://feeds.newsweek.com/newsweek/TopNews",
            "http://www.stuff.co.nz/rss/world",
            "http://www.nypost.com/rss/news.xml",
            "http://feeds.abcnews.com/abcnews/topstories",
            "http://feeds.feedburner.com/foxnews/latest",
            "http://feeds.feedburner.com/DrudgeReportFeed",
            "http://www.usnews.com/rss/news",
            "http://rss.upi.com/news/news.rss",
            "http://news.sky.com/feeds/rss/home.xml", // http://news.sky.com/info/rss
            "http://feeds.nbcnews.com/feeds/topstories",
            //"http://feeds.huffingtonpost.com/huffingtonpost/LatestNews"
        ]
        return result
    }
}
