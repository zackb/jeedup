package net.jeedup.feed.parser

import groovy.transform.CompileStatic
import net.jeedup.feed.FeedItem
import net.jeedup.feed.IFeed

/**
 * Created by zack on 6/13/14.
 */
@CompileStatic
public interface IFeedParser {
    public List<FeedItem> parse(IFeed feed);
}