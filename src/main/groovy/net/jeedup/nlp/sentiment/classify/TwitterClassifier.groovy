package net.jeedup.nlp.sentiment.classify

import groovy.transform.CompileStatic
import net.jeedup.coding.CSV
import net.jeedup.coding.JSON
import net.jeedup.nlp.sentiment.ModelTrainer
import net.jeedup.social.twitter.Tweet
import net.jeedup.social.twitter.TwitterService
import net.jeedup.util.CollectionUtil
import net.jeedup.util.StringUtil

/**
 * Created by zack on 7/3/14.
 */
@CompileStatic
class TwitterClassifier extends Classifier {

    private static String dataDir

    private TwitterClassifier() {
        super()
    }

    public TwitterClassifier(String dataDir) {
        super()
        this.dataDir = dataDir
        initialize()
    }

    @Override
    public Constants.Type getType() {
        return Constants.Type.TWITTER
    }

    @Override
    public void train(ModelTrainer trainer) throws Exception {
        println 'Training'
        String json = dataDir + 'out.json'
        List<Data> data = JSON.decodeList(new File(json).text, Data.class)
        for (Data d : data) {
            if (d.text == null) {
                continue
            }
            String text = StringUtil.removeUrls(d.text)
            Constants.Category category
            switch (d.category) {
                case 'positive':
                    category = Constants.Category.POSITIVE
                    break
                case 'negative':
                    category = Constants.Category.NEGATIVE
                    break
                case 'neutral':
                    category = Constants.Category.NEUTRAL
                    break
                case 'irrelevant':
                    category = Constants.Category.IRRELEVANT
                    break
                default:
                    throw new Exception('Couldnt determine category: ' + d.category)

            }
            println 'Training: ' + text + ' ' + category.name
            trainer.train(text, category)
        }
    }

    /**
     * Imports twitter data for the sanders-twitter data. Only needs to be done once
     * Creates an out.json file which is later used for training
     *
     * http://www.sananalytics.com/lab/twitter-sentiment/
     */
    public static void parseData() {
        String data = new File(dataDir + 'corpus.csv').text

        List<Data> map = []

        data.eachLine { String line ->
            String[] parts = CSV.parse(line)
            String cat = parts[1]
            String id = parts[2]
            Data d = new Data()
            d.id = id
            d.category = cat
            map << d
        }

        List<List<Data>> partitions = CollectionUtil.partitionList(map, 100)

        TwitterService service = TwitterService.getInstance()
        for (List<Data> ds : partitions) {
            List<Tweet> tweets = service.lookupStatuses(ds*.id)
            for (Data d : ds) {
                for (Tweet tweet : tweets) {
                    if (tweet.id_str == d.id) {
                        d.text = tweet.text
                        break
                    }
                }
            }
        }

        new File(dataDir + 'out.json').write(JSON.encode(partitions.flatten()))
    }

    static class Data {
        String id
        String text
        String category
    }

    public static void main(String[] args) {
        /*
        String dir = '/Users/zack/Downloads/sanders-twitter-0.2/'
        TwitterClassifier classifier = new TwitterClassifier(dir)
        println classifier.classify('Tony Schwartz: To Solve Big Problems, Change Your Process http://bit.ly/1j1ewUH')
        */
    }
}
