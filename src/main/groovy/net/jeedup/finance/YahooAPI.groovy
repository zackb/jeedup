package net.jeedup.finance

import net.jeedup.finance.model.Industry
import net.jeedup.finance.model.Sector
import net.jeedup.finance.model.Stock
import net.jeedup.net.http.HTTP
import net.jeedup.util.ThreadedJob
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


//http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json
class YahooAPI {

    private static final String BASE_URL = "http://query.yahooapis.com/v1/public/yql?q="

    public static List<Map<String, String>> fetchData(Collection<String> symbols) {
        String sym = URLEncoder.encode('"' + symbols.join('","') + '"', 'UTF-8')
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(${sym})%0A%09%09&format=json&env=http%3A%2F%2Fdatatables.org%2Falltables.env"
        Map<String, Object> json = HTTP.getJSON(url)
        println json
        // they query the CSV api under the hood
    }

    public static void retrieveAllSecurities() {
        ThreadedJob<Industry> job = new ThreadedJob<>(20, { Industry industry ->
            retrieveIndustrySecurities(industry)
        })

        Industry.db().executeQuery('select * from Industry').each { Industry industry ->
            job.add(industry)
        }
        job.waitFor()
    }

    public static retrieveIndustrySecurities(Industry industry) {
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.industry%20where%20id%3D%22${industry.yahooId}%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback="
        Map<String, Object> data = HTTP.getJSON(url)
        if (!data.query) {
            println 'Bad data ' + data + ' ' + industry.title
            return
        }
        data.query.results.industry.company.each {
            String name = it.name.replaceAll('\n', ' ')
            String symbol = it.symbol
            Stock stock = Stock.get(symbol)
            if (!stock) {
                println 'New stock: ' + name
                stock = new Stock()
                stock.id = symbol
                stock.name = name
                stock.active = 1
            }
            stock.industryId = industry.id
            stock.sectorId = industry.sectorId
            stock.save()
        }
    }

    public static void retrieveAllSectorsAndIndustries() {

        def logic = { Element table ->
            Sector sector
            table.select('tr').each { Element e ->
                Elements elements = e.select('td')
                if (elements.size() == 1) {
                    String sectorName = elements[0].text()
                    if (sectorName) {
                        sector = Sector.db().findBy('title', sectorName)
                        if (!sector) {
                            println "New sector: " + sectorName
                            sector = new Sector()
                            sector.title = sectorName
                            sector.save()
                        }
                    }
                } else if (elements.size() == 2) {
                    Element td = elements.get(1)
                    Element a = td.select('a').get(0)
                    Long yahooId = a.attr('href').find('([0-9]+).html').replaceAll('.html', '').toLong()
                    Industry i = Industry.db().findBy('yahooId', yahooId)
                    if (!i) {
                        println 'New Industry ' + a.text()
                        i = new Industry()
                        i.yahooId = yahooId.toLong()
                    }
                    i.title = a.text()
                    i.sectorId = sector.id
                    i.save()
                }
            }
        }

        String url = 'http://biz.yahoo.com/ic/ind_index.html'
        String data = HTTP.get(url)
        Document doc = Jsoup.parse(data)
        Elements tables = doc.select('table[cellpadding=2]')
        logic.call(tables[2])
        logic.call(tables[3])
    }

    public static void retrieveAllSectorsAndIndustriesYQL() {
        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.sectors&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
        Map<String, Object> json = HTTP.getJSON(url)
        json['query']['results']['sector'].each { Map sector ->
            if (!sector.name) {
                return
            }
            Sector s = Sector.db().findBy('title', sector.name)
            if (!s) {
                println "New sector: " + sector.name
                s = new Sector()
                s.title = sector.name
                s.save()
            }
            sector.industry.each { industry ->
                try {
                    Industry i = Industry.db().findBy('yahooId', industry.id)
                    if (!i) {
                        i = new Industry()
                        i.yahooId = industry.id.toLong()
                    }
                    i.title = industry.name
                    i.sectorId = s.id
                    i.save()
                } catch (Exception e) {
                    System.out.println("Failed parsing sector or industry: " + industry)
                    e.printStackTrace()
                }
            }
        }
    }

    public static void retrieveAllSecuritiesYQL() {

        String url = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.industry%20where%20id%20in%20(select%20industry.id%20from%20yahoo.finance.sectors)&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&format=json"

        //Map<String, Object> json = JSON.decode(new File('/Users/zack/Desktop/Code/jeedup/src/main/groovy/net/jeedup/finance/all.json').text)
        Map<String, Object> json = HTTP.getJSON(url)

        json['query']['results']['industry'].each { Map industry ->
            Industry i = Industry.db().findBy('yahooId', industry.id)
            if (i == null) {
                throw new Exception('No such industry: ' + industry)
            }
            industry.company.each { company ->
                if (!(company instanceof Map)) {
                    // TODO, non list, just obj
                    return
                }
                Stock stock = Stock.get(company.symbol)
                if (!stock) {
                    println 'No such stock: ' + company
                    stock = new Stock()
                    stock.id = company.symbol
                    stock.name = company.name
                    stock.active = 1
                }
                stock.industryId = i.id
                stock.sectorId = i.sectorId
                stock.save()
            }
        }
    }


    public static void main(String[] args) {
        println StockService.getInstance().retrieveAndUpdateStockData(true, false)
    }
}
