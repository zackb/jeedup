package net.jeedup.web

import groovy.transform.CompileStatic

/**
 * User: zack
 * Date: 11/10/13
 */
@CompileStatic
class Main {

    static void main(final String[] args) {
        Jeedup jeedup = new Jeedup()
        jeedup.start()
    }

}
