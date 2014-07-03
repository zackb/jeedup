package net.jeedup.nlp.sentiment.classify

import net.jeedup.storage.FileStorageService

/**
 * Created by zack on 7/3/14.
 */
class Constants {
    enum Type {
        TWITTER('TWITTER_POLARITY'),
        LEE_PANG('LEE_PANG_POLARITY')

        String name

        private Type(String name) {
            this.name = name
        }

        File getModelFile() {
            return FileStorageService.getTemporaryFile(getClass().name + '_' + name + '.model')
        }
    }

    enum Category {
        POSITIVE('pos'),
        NEGATIVE('neg'),
        NEUTRAL('neut'),
        IRRELEVANT('irr')

        static List<Category> all = [POSITIVE, NEGATIVE, NEUTRAL, IRRELEVANT]
        String name

        private Category(String name) {
            this.name = name
        }

        public String toString() {
            return name
        }

        static Category named(String n) {
            return all.find { it.name == n }
        }
    }
}
