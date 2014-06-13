package net.jeedup.text

import groovy.transform.CompileStatic

/**
 * Created by zack on 6/13/14.
 */
@CompileStatic
class Phrase {
    public String text
    public String description
    public String url
    public Set<Phrase> relatedPhrases = []

    public String toString() {
        return "Text: ${text} Url: ${url} Related: ${relatedPhrases}"
    }
}
