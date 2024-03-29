package net.jeedup.text

import groovy.transform.CompileStatic
import net.jeedup.feed.FeedItem
import net.jeedup.feed.IFeed
import net.jeedup.feed.RssFeed
import net.jeedup.feed.parser.RssFeedParser

/**
 * Created by zack on 6/13/14.
 */
@CompileStatic
class RssFeedPhraseParser extends FeedPhraseParser {

    @Override
    protected List<Phrase> parseFeed(IFeed feed) {
        //return new RssFeedParser().parse(feed).collect { parseFeedItem(it) }
        List<Phrase> phrases = []
        new RssFeedParser().parse(feed).each { FeedItem item ->
            phrases << parseFeedItem(item)
        }
        if (!phrases) {
            System.err.println('No entries for: ' + ((RssFeed)feed).url)
        }
        return phrases
    }
}
