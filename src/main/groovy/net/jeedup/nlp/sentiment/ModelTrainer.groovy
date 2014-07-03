package net.jeedup.nlp.sentiment

import com.aliasi.classify.Classification
import com.aliasi.classify.Classified
import com.aliasi.classify.DynamicLMClassifier
import com.aliasi.lm.NGramProcessLM
import com.aliasi.util.AbstractExternalizable
import net.jeedup.nlp.sentiment.classify.Constants

/**
 * Created by zack on 7/3/14.
 */
class ModelTrainer {


    private final Constants.Type type

    private final DynamicLMClassifier<NGramProcessLM> classifier

    private static final int ngram = 8

    private ModelTrainer() {
        super()
    }

    public ModelTrainer(Constants.Type type) {
        this.type = type
        String[] categories = Constants.Category.all*.name
        classifier = DynamicLMClassifier.createNGramProcess(categories, ngram)
    }

    public void train(String text, Constants.Category category) throws Exception {
        Classification classification = new Classification(category.name)
        Classified<CharSequence> classified = new Classified<CharSequence>(text, classification)
        classifier.handle(classified)
    }

    public void storeModel() throws IOException {
        AbstractExternalizable.compileTo(classifier, type.modelFile)
    }

    public void deleteModel() throws IOException {
        File file = type.modelFile
        if (file.exists()) {
            file.delete()
        }
    }
}
