package net.jeedup.feed

import groovy.util.slurpersupport.GPathResult
import net.jeedup.net.http.HTTP
import net.jeedup.util.DateTimeUtil
import net.jeedup.util.StringUtil

/**
 * Created by zack on 6/13/14.
 */
class RssFeedParser implements IFeedParser {
    @Override
    public List<FeedItem> parse(IFeed feed) {
        List<FeedItem> items = []
        RssFeed rssFeed = (RssFeed)feed
        String xml = HTTP.get(rssFeed.url)
        def slurper = new XmlSlurper(false, false).parseText(xml)
        slurper.channel.item.each { GPathResult i ->
            items << parseItem(i)
        }
        return items
    }

    private static FeedItem parseItem(GPathResult rss) {
        FeedItem item = new FeedItem()
        item.pubDate = DateTimeUtil.parseDate(rss.pubDate.toString().trim())
        item.title = rss.title?.toString()?.trim()
        item.description = StringUtil.removeHtml(rss.description?.toString()?.trim())
        item.link = rss.link?.toString()?.trim()
        item.guid = rss.guid?.toString()?.trim()
        return item
    }
}
