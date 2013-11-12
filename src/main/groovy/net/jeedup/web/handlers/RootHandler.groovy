package net.jeedup.web.handlers

import groovy.transform.CompileStatic
import net.jeedup.model.User
import net.jeedup.persistence.Db
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

    @Endpoint('')
    def html(Map data) {
        HTML([message: data?.message ?: 'Hello, World!', foo:[bar: 'BOOO']], 'test')
    }

    @Endpoint('test/zack')
    def zack(Map data) {
        // will render zack.html with no data
        HTML()
    }

    @Endpoint('admin')
    def admin(Map data) {
        HTML(null, 'admin/admin')
    }

    @Endpoint('db')
    def db(Map data) {
        Db<User> db = Db.db(User.class)
        List<User> users = db.getAll([1])
        JSON(users)
    }
}
