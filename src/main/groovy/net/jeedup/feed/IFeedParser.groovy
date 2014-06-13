package net.jeedup.feed

import groovy.transform.CompileStatic

/**
 * Created by zack on 6/13/14.
 */
@CompileStatic
public interface IFeedParser {
    public List<FeedItem> parse(IFeed feed);
}