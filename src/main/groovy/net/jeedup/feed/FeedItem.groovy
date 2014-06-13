package net.jeedup.feed

import groovy.transform.CompileStatic

/**
 * Created by zack on 6/13/14.
 */
@CompileStatic
class FeedItem {
    public String title
    public String description
    public String link
    public String guid
    public Date pubDate

    public String toString() {
        return """Title: ${title}
Description: ${description}
Link: ${link}
GUID: ${guid}
Date: ${pubDate}"""
    }
}
