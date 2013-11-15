package net.jeedup.web.handlers

import groovy.transform.CompileStatic

import net.jeedup.model.User
import net.jeedup.web.Config
import net.jeedup.web.Endpoint

import static net.jeedup.web.Response.JSON
import static net.jeedup.web.Response.TEXT
import static net.jeedup.web.Response.HTML

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
    def text(Map data) {
        TEXT(data)
    }

    @Endpoint('admin')
    def admin(Map data) {
        HTML(null, 'admin/admin')
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
