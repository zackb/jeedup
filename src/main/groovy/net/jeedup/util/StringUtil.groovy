package net.jeedup.util

import groovy.transform.CompileStatic

import java.util.regex.Pattern

/**
 * Created by zack on 6/13/14.
 */
@CompileStatic
class StringUtil {

    public static String removeHtml(String text) {
        return text?.replaceAll('<.*?>', ' ')?.trim()
    }

    public static String removeIgnoraleWordsFromNews(String text) {
        text = ' ' + text + ' '
        List<String> ignores = []
        ignores.addAll(IGNORABLE_WORDS)
        ignores.addAll(IGNORABLE_WORDS_NEWS)
        for(String ignore : ignores) {
            if (!ignore) {
                continue
            }
            text = Pattern.compile(ignore, Pattern.CASE_INSENSITIVE).matcher(text).replaceAll(' ')
        }

        return text.trim()
    }

    public static final List<String> IGNORABLE_WORDS = Arrays.asList(
            " — ",
            "'s ",
            " at ",
            " to ",
            " is ",
            " on ",
            " as ",
            " says ",
            " of ",
            " a ",
            " an ",
            " and ",
            " by ",
            " has ",
            " the ",
            " that ",
            " it ",
            " in ",
            " with ",
            " for ",
            " from ",
            "\\:",
            ",",
            "’",
            " s ",
            "\"",
            "'",
            "\\&.*?;",
            "\\?",
            "\\&",
            "\\)",
            "\\(",
            " \\- ",
            "\r",
            "\n",
            ";",
            " "
    )

    public static final List<String> IGNORABLE_WORDS_NEWS = Arrays.asList(
        '\\(afp\\)',
        '\\(ap\\)',
        'the associated press',
        'reuters',
        'abc news',
        'foxnews',
        ' in',
        ' with',
        ' for',
        ' from',
        'usa today',
        'los angeles times',
        'washington post',
        'wall street journal',
        'new york times',
        'new York daily news',
        ' - times online'
    )
}
