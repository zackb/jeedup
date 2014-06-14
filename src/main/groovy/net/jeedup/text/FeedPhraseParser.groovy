package net.jeedup.text

import groovy.transform.CompileStatic
import net.jeedup.feed.FeedItem
import net.jeedup.feed.IFeed
import net.jeedup.feed.RssFeed
import net.jeedup.util.ThreadedJob

/**
 * Created by zack on 6/13/14.
 */
@CompileStatic
abstract class FeedPhraseParser implements IPhraseParser {

    @Override
    public List<Phrase> parsePhrases(PhraseSet phraseSet) {

        List<Phrase> phrases = []

        ThreadedJob<String> job = new ThreadedJob<String>(10, { String url ->
            RssFeed feed = new RssFeed()
            feed.url = url
            phrases.addAll(parseFeed(feed))
        });

        for (String url : phraseSet.urls) {
            job.add(url)
        }

        job.waitFor()

        return phrases
    }

    protected abstract List<Phrase> parseFeed(IFeed feed)

    protected static Phrase parseFeedItem(FeedItem item) {
        Phrase phrase = new Phrase()
        phrase.text = item.title
        phrase.description = item.description
        phrase.url = item.link
        return phrase
    }
}
