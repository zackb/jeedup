package net.jeedup.feed

import junit.framework.Test
import junit.framework.TestCase
import junit.framework.TestSuite
import net.jeedup.text.Phrase
import net.jeedup.text.PhraseSet
import net.jeedup.text.RssFeedPhraseParser
import net.jeedup.text.cluster.CosineDistancePhraseCluserAlgorithm

/**
 * Created by zack on 6/13/14.
 */
class RssFeedParserTest extends TestCase {
    public RssFeedParserTest(String testName) {
        super(testName)
    }

    public static Test suite() {
        return new TestSuite(RssFeedParserTest.class)
    }

    public void testSimpleParse() {
        RssFeed feed = new RssFeed()
        feed.url = 'http://news.google.com/news?pz=1&ned=us&hl=en&output=rss'
        List<FeedItem> items = new RssFeedParser().parse(feed)
        items.each { FeedItem item ->
            assert item
            assert item.title
            assert item.description
            assert item.pubDate
            assert item.guid
            assert item.link
        }
    }

    public void testRssFeedPhraseParser() {
        PhraseSet set = PhraseSet.newsSources()
        List<Phrase> phrases = new RssFeedPhraseParser().parsePhrases(set)
        List<Phrase> cluster = new CosineDistancePhraseCluserAlgorithm().cluserPhrases(phrases)
        cluster = cluster.sort { -it.relatedPhrases.size() }
        println cluster
    }
}
