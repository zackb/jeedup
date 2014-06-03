package net.jeedup.finance

import net.jeedup.coding.JSON
import net.jeedup.model.finance.Industry
import net.jeedup.model.finance.Sector
import net.jeedup.model.finance.Stock
import net.jeedup.net.http.HTTP


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

    public static void retrieveAllSectorsAndIndustries() {
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

    public static void main(String[] args) {
    }
}
