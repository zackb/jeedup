package net.jeedup.feed.parser

import groovy.util.slurpersupport.GPathResult
import net.jeedup.feed.FeedItem
import net.jeedup.feed.IFeed
import net.jeedup.feed.RssFeed
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
        def slurper = new XmlSlurper(false, true).parseText(xml)

        if (slurper[0].namespaceURI?.equals('http://www.w3.org/2005/Atom')) {
            items = parseAtomFeed(slurper)
        } else {
            items = parseRssFeed(slurper)
        }
        return items
    }

    private static List<FeedItem> parseRssFeed(GPathResult slurper) {
        List<FeedItem> items = []
        slurper.channel.item.each { GPathResult i ->
            items << parseItem(i)
        }
        return items
    }

    private static FeedItem parseItem(GPathResult rss) {
        FeedItem item = new FeedItem()
        item.pubDate = DateTimeUtil.parseDate(rss.pubDate.text().trim())
        item.title = rss.title?.text()?.trim()
        item.description = StringUtil.removeHtml(rss.description?.text()?.trim())
        item.link = rss.link?.text()?.trim()
        item.guid = rss.guid?.text()?.trim()
        return item
    }

    private static List<FeedItem> parseAtomFeed(GPathResult slurper) {
        List<FeedItem> items = []
        slurper.entry.each { GPathResult i ->
            items << parseAtomItem(i)
        }
        return items
    }

    private static FeedItem parseAtomItem(GPathResult rss) {
        FeedItem item = new FeedItem()
        item.pubDate = DateTimeUtil.parseDate(rss.published.text().trim())
        item.title = rss.title?.text()?.trim()
        item.description = StringUtil.removeHtml(rss.content?.text()?.trim())
        item.link = rss.link?.@href?.text()?.trim()
        item.guid = rss.id?.text()?.trim()
        return item
    }
}
