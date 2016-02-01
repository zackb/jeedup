package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import net.jeedup.finance.StockService
import net.jeedup.finance.model.Analyst
import net.jeedup.finance.model.Stock
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

    @Endpoint('home')
    def index(Map data) {
        HTML([:], 'admin/home')
    }

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
            case 'money':
                set = PhraseSet.moneySources()
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

    @Endpoint('n')
    def news(Map data) {
        List<Phrase> phrases = phraseSetStore.get((String)data.id ?: 'news')
        HTML([phrases:phrases], 'admin/news')
    }

    @Endpoint('admin/tool')
    def tool(Map data) {
        HTML([:], 'admin/tool')
    }

    @Endpoint('f/lookup')
    def lookup(Map data) {

        String id = data.id?.toString()?.trim()
        if (!id) {
            throw new Exception('Missing required id')
        }
        Stock stock = Stock.get(id)
        if (!stock) {
            stock = new Stock(id:id)
        }

        if (!stock.lastUpdated || stock.lastUpdated.time < System.currentTimeMillis() - 1000 * 60 * 15)
            StockService.instance.enrich(stock)

        String cls = 'up green', plus = '+'
        if (stock.change < 0) {
            cls = 'down red'
            plus = ''
        }

        List<Analyst> analysts = Analyst.forSymbol(stock.id)
        def ass = [strong:[],buy:[],hold:[],under:[],sell:[]]
        boolean agood = false
        analysts.each { Analyst a ->
            agood = true
            ass.strong << a.strongBuy
            ass.buy << a.buy
            ass.hold << a.hold
            ass.under << a.underperform
            ass.sell << a.sell
        }
        HTML([
                stock:stock,
                lwr:stock.id.toLowerCase(),
                cls:cls,
                plus:plus,
                analys:agood ? ass : null,
                mcap: String.format("%.2fM", stock.marketCapitalization / 1000000.0),
                eval: String.format("%.2fM", stock.enterpriseValue / 1000000.0)
        ], 'admin/search')
    }

    @Endpoint('f/suggest')
    def suggest(Map data) {
        String q = ((String)data.q)?.trim()
        //if (!q) return new Response().data(data).status(400)
        List<Stock> stocks = null
        if (q) {
            String like = q.replaceAll(' ', '%') + '%'
            stocks = Stock.db().executeQuery('id like ? or name like ? ', [like, like])
        } else {
            stocks = Stock.db().getAll()
        }

        List<Map<String, String>> symbs = new ArrayList<Map<String,String>>()
        for (Stock stock : stocks) {
            symbs.add([id: stock.id, name: stock.name])
        }

        JSON([data:symbs])
    }
}
