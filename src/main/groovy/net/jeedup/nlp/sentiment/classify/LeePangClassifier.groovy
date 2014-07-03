package net.jeedup.nlp.sentiment.classify

import com.aliasi.util.Files
import groovy.transform.CompileStatic
import net.jeedup.nlp.sentiment.classify.Constants.Type
import net.jeedup.nlp.sentiment.ModelTrainer

/**
 * Created by zack on 7/3/14.
 */
@CompileStatic
class LeePangClassifier extends Classifier {

    public static String dataDir = ''

    private LeePangClassifier() {
        super()
    }

    public LeePangClassifier(String dataDir) {
        super()
        this.dataDir = dataDir
        initialize()
    }

    @Override
    public Type getType() {
        return Type.LEE_PANG
    }

    @Override
    public void train(ModelTrainer trainer) throws Exception {
        println("\nTraining.")
        for (Constants.Category category : Constants.Category.all) {
            File file = new File(dataDir, category.name)
            File[] trainFiles = file.listFiles()
            for (File trainFile : trainFiles) {
                if (isTrainingFile(trainFile)) {
                    String review = Files.readFromFile(trainFile, 'ISO-8859-1');
                    trainer.train(review, category)
                }
            }
        }
    }

    private static boolean isTrainingFile(File file) {
        return file.name.charAt(2) != '9'
    }

    public static void main(String[] args) {
        String TRAIN_DATA = '/Users/zack/Desktop/polarity/txt_sentoken'

        LeePangClassifier classifier = new LeePangClassifier(TRAIN_DATA)

        println classifier.classify('big happy happy happy. I liked everything so much. That thing is a thing')
    }
}
