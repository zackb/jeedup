package net.jeedup.nlp.sentiment.classify

import com.aliasi.classify.Classification
import com.aliasi.classify.LMClassifier
import com.aliasi.lm.NGramProcessLM
import com.aliasi.stats.MultivariateEstimator
import com.aliasi.util.AbstractExternalizable
import net.jeedup.nlp.sentiment.classify.Constants.Type
import net.jeedup.nlp.sentiment.ModelTrainer

/**
 * Created by zack on 7/3/14.
 */
abstract class Classifier {

    protected LMClassifier<NGramProcessLM, MultivariateEstimator> classifier

    protected void initialize() throws Exception {
        if (!type) {
            throw new Exception('Must specify a Type')
        }

        File model = type.modelFile
        if (model.exists()) {
            classifier = (LMClassifier<NGramProcessLM, MultivariateEstimator>) AbstractExternalizable.readObject(model)
        } else {
            retrain()
        }
    }

    public Constants.Category classify(String text) {
        Classification classification = classifier.classify(text)
        return Constants.Category.named(classification.bestCategory())
    }

    public void retrain() throws Exception {
        ModelTrainer trainer = new ModelTrainer(getType())
        trainer.deleteModel()
        train(trainer)
        trainer.storeModel()
        initialize()
    }

    public abstract Type getType()

    public abstract void train(ModelTrainer trainer) throws Exception
}
