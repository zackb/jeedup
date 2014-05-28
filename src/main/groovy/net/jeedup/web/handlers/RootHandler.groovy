package net.jeedup.web.handlers
import groovy.transform.CompileStatic
import net.jeedup.finance.Stock
import net.jeedup.message.Brokers
import net.jeedup.message.MessageBroker
import net.jeedup.model.User
import net.jeedup.net.http.Request
import net.jeedup.persistence.DB
import net.jeedup.persistence.sql.SqlDB
import net.jeedup.web.Config
import net.jeedup.web.Endpoint

import static net.jeedup.net.http.Response.*
/**
 * User: zack
 * Date: 11/9/13
 */
@CompileStatic
class RootHandler {

    /*
   import net.jeedup.finance.*
import net.jeedup.persistence.*


DB<Stock> db = DB.sql(Stock.class)
new File("/Users/zack/Downloads/quandl-stock-code-list.csv").splitEachLine(",") { fields ->
String s = fields[0]
String name = fields[1]
String a = fields[4]
Stock stock = new Stock()
stock.id = s
stock.name = name
stock.active = a.startsWith('Not') ? 0 : 1
db.save(stock)
}

     */

    @Endpoint('q')
    def q(Map data) {
        /*
        MessageBroker broker = Brokers.getInstance().named("mainQueue")
        broker.observe()
        .subscribe({ s ->
            System.out.println("Got: " + s)
            Thread.sleep(1000)
        }, { Throwable throwable ->
            throwable.printStackTrace()
        })
        */
        SqlDB<Stock> db = DB.sql(Stock.class);
        db.createTable()
        HTML("OK")
    }
    @Endpoint('send')
    def send(Map data) {
        def broker = Brokers.getInstance().named("mainQueue")
        broker.sendMessage(data.m)
        HTML("Done")
    }

    @Endpoint('json')
    def json(Map data) {
        JSON(Config.getInstance())
    }

    @Endpoint('text')
    def text(Request request) {
        TEXT(request.data)
    }

    @Endpoint('admin')
    def admin(Map data) {
        HTML(null, 'admin/admin')
    }

    @Endpoint('admin/repl')
    def repl(Map data) {
        String output = ''
        String code = data.code
        GroovyShell shell = new GroovyShell(ClassLoader.getSystemClassLoader())
        Script scpt = shell.parse(code);
        Binding binding = new Binding();
        binding.setVariable ("render", { args ->
            output += args
        });
        scpt.setBinding(binding);


        try {
            String result = '' + scpt.run()
            return JSON(['result':result])
        } catch (Exception e) {
            e.printStackTrace();
            return JSON(['message': e.message]).status(500)
        }
    }

    @Endpoint('db')
    def db(Map data) {
        //SqlDB<User> db = DB.sql(User.class)
        User user = User.get(8)
        user.username = 'fooey ' + System.currentTimeMillis()
        user.save()
        JSON(User.get(8))
    }
}
