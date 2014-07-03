package net.jeedup.nlp.sentiment

import net.jeedup.util.RxUtil

/**
 * Created by zack on 7/3/14.
 */
class TwitterClassifier extends Classifier {

    @Override
    Constants.Type getType() {
        return Constants.Type.TWITTER
    }

    @Override
    void train(ModelTrainer trainer) throws Exception {

    }

    public static void main(String[] args) {
        String dataFile = ''
        RxUtil.stream()
    }
}
