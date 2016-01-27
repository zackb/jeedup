package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import net.jeedup.coding.JSON
import net.jeedup.util.StringUtil
import net.jeedup.web.Endpoint
import static net.jeedup.net.http.Response.*

import net.jeedup.text.Phrase
import net.jeedup.text.PhraseSet
import net.jeedup.text.RssFeedPhraseParser
import net.jeedup.text.cluster.CosineDistancePhraseCluserAlgorithm
import net.jeedup.util.FileStoreCache

/**
 * Created by zack on 7/3/14.
 */
@CompileStatic
class AdminHandler {

    @Endpoint('admin/repl')
    def repl(Map data) {
        String output = ''
        String code = data.code
        GroovyShell shell = new GroovyShell(ClassLoader.getSystemClassLoader())
        Script scpt = shell.parse(code)
        Binding binding = new Binding()
        binding.setVariable ('render', { args ->
            output += args
        })
        scpt.setBinding(binding)


        try {
            String result = '' + scpt.run()
            return JSON(['result':result])
        } catch (Exception e) {
            e.printStackTrace();
            return JSON(['message': e.message]).status(500)
        }
    }

    FileStoreCache<List<Phrase>> phraseSetStore = new FileStoreCache<List<Phrase>>({ String key ->
        PhraseSet set
        switch (key) {
            case 'news':
                set = PhraseSet.newsSources()
                break
            case 'sports':
                set = PhraseSet.sportsSources()
                break
            default:
                throw new Exception('No such news category')
        }
        List<Phrase> phrases = new RssFeedPhraseParser().parsePhrases(set)
        List<Phrase> cleaned = []
        Set<String> seen = []
        phrases.each { Phrase phrase ->
            String url = phrase.url.trim()
            if (!seen.contains(url)) {
                cleaned << phrase
                seen.add(url)
            }
        }
        List<Phrase> cluster = new CosineDistancePhraseCluserAlgorithm().cluserPhrases(cleaned)
        cluster = cluster.sort { Phrase phrase -> -phrase.relatedPhrases.size() }

        return cluster

    }, 10 * 60 * 1000)

    @Endpoint('admin/news')
    def news(Map data) {
        List<Phrase> phrases = phraseSetStore.get((String)data.id)
        HTML([phrases:phrases], 'admin/news')
    }
}
