package net.jeedup.text

import groovy.transform.CompileStatic

/**
 * Created by zack on 6/13/14.
 */
@CompileStatic
interface IPhraseParser {
    public List<Phrase> parsePhrases(PhraseSet phraseSet)
}
