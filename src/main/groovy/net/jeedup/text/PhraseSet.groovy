package net.jeedup.text

import groovy.transform.CompileStatic

/**
 * Created by zack on 6/13/14.
 */
@CompileStatic
class PhraseSet {

    public String title
    public List<String> urls

    public static PhraseSet sportsSources() {
        PhraseSet result = new PhraseSet()
        result.title = 'Sports'
        result.urls = [
                'http://sports.espn.go.com/espn/rss/news',
                'http://feeds.foxsports.com/feedout/syndicatedContent?categoryId=0',
                'http://www.nbcsports.com/rss/section/all/feed',
                'http://www.cbssports.com/partners/feeds/rss/home_news',
                'http://feeds.bbci.co.uk/sport/0/rss.xml?edition=uk',
                'http://sports.yahoo.com/top/rss.xml', // http://sports.yahoo.com/top/rss
                'http://rssfeeds.usatoday.com/UsatodaycomSports-TopStories', // http://content.usatoday.com/marketing/rss/index.aspx
                'http://www.skysports.com/rss/0,20514,12040,00.xml', // http://www.skysports.com/rss_home/0,20366,,00.html
                'http://www.sportingnews.com/rss',
        ]
        return result
    }

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
            "http://feeds.reuters.com/reuters/topNews", // http://www.reuters.com/tools/rss
            "http://hosted.ap.org/lineups/TOPHEADS-rss_2.0.xml?SITE=TXHAR&SECTION=HOME",
            "http://online.wsj.com/xml/rss/3_7085.xml",
            "http://feeds.feedburner.com/euronews/en/world?format=xml",
            //"http://www.euronews.com/rss/euronews_en.xml",
            "http://russiatoday.com/Top_News.rss",
            "http://english.aljazeera.net/Services/Rss/?PostingId=2007731105943979989",
            "http://rss.feedsportal.com/c/32158/f/414206/index.rss",
            "http://news.sky.com/sky-news/rss/world-news/rss.xml",
            "http://rssfeeds.usatoday.com/usatoday-NewsTopStories",
            // ??? 'http://content.usatoday.com/marketing/rss/rsstrans.aspx?feedId=news1',
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
            // "http://www.vox.com/rss/index.xml" // Atom. not news
            //"http://feeds.huffingtonpost.com/huffingtonpost/LatestNews"
        ]
        return result
    }

    public static PhraseSet moneySources() {
        PhraseSet result = new PhraseSet()
        result.title = 'Money'
        result.urls = [
                "http://www.dailyfinance.com/rss.xml",
                "http://www.fool.com/feeds/index.aspx?id=foolwatch&format=rss2",
                "http://feeds.reuters.com/news/wealth",
                //"http://feeds.reuters.com/reuters/businessNews",
                "http://feeds.feedburner.com/reuters/blogs/data-dive",
                "http://feeds.reuters.com/reuters/USenergyNews",
                "http://feeds.reuters.com/reuters/hotStocksNews",
                "http://feeds.reuters.com/news/economy",
                "http://feeds.reuters.com/news/usmarkets",
                "http://rss.cnn.com/rss/money_latest.rss",
                "http://rss.cnn.com/rss/money_markets.rss",
                "http://rss.cnn.com/cnnmoneymorningbuzz",
                "http://www.ft.com/rss/companies",
                "http://www.ft.com/rss/companies/us",
                "http://www.ft.com/rss/markets",
                "http://www.ft.com/rss/personal-finance/top-tips",
                "http://finance.yahoo.com/rss/popularstories",
                "http://www.marketwatch.com/rss/topstories",
                "http://www.marketwatch.com/rss/realtimeheadlines",
                "http://www.marketwatch.com/rss/StockstoWatch",
                "http://www.marketwatch.com/rss/commentary",
                "http://www.marketwatch.com/rss/marketpulse",
                "http://feeds.marketwatch.com/marketwatch/bulletins",
                "http://www.forbes.com/business/index.xml",
                "http://www.forbes.com/markets/index.xml",
                "http://www.cnbc.com/id/100003114/device/rss/rss.html",
                "http://feeds.thestreet.com/TheRealStory",
        ]
        return result
    }
}
