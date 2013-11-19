package net.jeedup.web.handlers

import groovy.transform.CompileStatic

import net.jeedup.model.User
import net.jeedup.net.http.Request
import net.jeedup.web.Config
import net.jeedup.web.Endpoint

import static net.jeedup.net.http.Response.JSON
import static net.jeedup.net.http.Response.TEXT
import static net.jeedup.net.http.Response.HTML

/**
 * User: zack
 * Date: 11/9/13
 */
@CompileStatic
class RootHandler {

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
