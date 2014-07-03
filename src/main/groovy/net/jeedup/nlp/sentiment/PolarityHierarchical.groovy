package net.jeedup.nlp.sentiment

import com.aliasi.classify.BaseClassifierEvaluator;
import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.JointClassifier;
import com.aliasi.classify.ConditionalClassification;
import com.aliasi.classify.DynamicLMClassifier
import com.aliasi.classify.LMClassifier
import com.aliasi.lm.NGramProcessLM
import com.aliasi.stats.MultivariateEstimator
import com.aliasi.util.AbstractExternalizable
import com.aliasi.util.BoundedPriorityQueue;
import com.aliasi.util.Files
import com.aliasi.util.ScoredObject


/**
 * Created by zack on 7/1/14.
 */
class PolarityHierarchical {

    static final String POLARITY_MODEL = "/Users/zack/Desktop/polarity/polarity.model";
    static final String SUBJECTIVITY_MODEL = "/Users/zack/Desktop/polarity/subjectivity.model";

    File mPolarityDir;
    String[] mCategories;
    LMClassifier<NGramProcessLM, MultivariateEstimator> mClassifier;
    //DynamicLMClassifier<NGramProcessLM> mClassifier;

    JointClassifier<CharSequence> mSubjectivityClassifier;

    public PolarityHierarchical(String dir) throws ClassNotFoundException, IOException {

        System.out.println("\nHIERARCHICAL POLARITY DEMO");
        mPolarityDir = new File(dir, "txt_sentoken");
        System.out.println("\nData Directory=" + mPolarityDir);
        mCategories = mPolarityDir.list();
        int nGram = 8;
        mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);
        File modelFile = new File(SUBJECTIVITY_MODEL);

        System.out.println("\nReading Compiled Model from file=" + modelFile);
        FileInputStream fileIn = new FileInputStream(modelFile);
        ObjectInputStream objIn = new ObjectInputStream(fileIn);

        @SuppressWarnings("unchecked")
        JointClassifier<CharSequence> subjectivityClassifier = (JointClassifier<CharSequence>) objIn.readObject();
        mSubjectivityClassifier = subjectivityClassifier;
        objIn.close();
    }

    private void run() throws ClassNotFoundException, IOException {
        train();
        evaluate();
    }

    private static boolean isTrainingFile(File file) {
        return file.getName().charAt(2) != '9';  // test on fold 9
    }

    public void train() throws IOException {
        File model = new File(POLARITY_MODEL);
        if (model.exists()) {
            mClassifier = (LMClassifier<NGramProcessLM, MultivariateEstimator>)AbstractExternalizable.readObject(model);
            /*
            FileInputStream fileIn = new FileInputStream(model);
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            mClassifier = (DynamicLMClassifier<NGramProcessLM>)objIn.readObject();
            */
            return;
        }
        int numTrainingCases = 0;
        int numTrainingChars = 0;
        System.out.println("\nTraining.");
        for (int i = 0; i < mCategories.length; ++i) {
            String category = mCategories[i];
            Classification classification = new Classification(category);
            File file = new File(mPolarityDir, category);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];
                if (isTrainingFile(trainFile)) {
                    ++numTrainingCases;
                    String review = Files.readFromFile(trainFile,"ISO-8859-1");
                    numTrainingChars += review.length();
                    Classified<CharSequence> classified = new Classified<CharSequence>(review, classification);
                    mClassifier.handle(classified);
                }
            }
        }

        println("  # Training Cases=" + numTrainingCases)
        println("  # Training Chars=" + numTrainingChars)
        // if you want to write the polarity model out for future use,
        AbstractExternalizable.compileTo(mClassifier, model);
        /*
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream(model));
            out.writeObject(mClassifier);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out?.close();
        }
        */
    }

    private void evaluate() throws IOException {
        boolean storeInstances = false;
        BaseClassifierEvaluator<CharSequence> evaluator = new BaseClassifierEvaluator<CharSequence>(null,mCategories,storeInstances);
        for (int i = 0; i < mCategories.length; ++i) {
            String category = mCategories[i];
            File file = new File(mPolarityDir,mCategories[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];
                if (!isTrainingFile(trainFile)) {
                    String review = Files.readFromFile(trainFile,"ISO-8859-1");
                    String subjReview = subjectiveSentences(review);
                    Classification classification = mClassifier.classify(subjReview);
                    evaluator.addClassification(category,classification,null);
                }
            }
        }
        System.out.println();
        System.out.println(evaluator.toString());
    }

    public Classification classify(String sentence) {
        String subjReview = subjectiveSentences(sentence);
        return mClassifier.classify(subjReview);
    }


    private String subjectiveSentences(String review) {

        String[] sentences = review.split("\n");
        BoundedPriorityQueue<ScoredObject<String>> pQueue = new BoundedPriorityQueue<ScoredObject<String>>(ScoredObject.comparator(), MAX_SENTS);

        for (int i = 0; i < sentences.length; ++i) {
            String sentence = sentences[i];
            ConditionalClassification subjClassification = (ConditionalClassification) mSubjectivityClassifier.classify(sentences[i]);
            double subjProb;

            if (subjClassification.category(0).equals("quote")) {
                subjProb = subjClassification.conditionalProbability(0);
            } else {
                subjProb = subjClassification.conditionalProbability(1);
            }
            pQueue.offer(new ScoredObject<String>(sentence,subjProb));
        }

        StringBuilder reviewBuf = new StringBuilder();
        Iterator<ScoredObject<String>> it = pQueue.iterator();
        for (int i = 0; it.hasNext(); ++i) {
            ScoredObject<String> so = it.next();
            if (so.score() < 0.5 && i >= MIN_SENTS) {
                break;
            }
            reviewBuf.append(so.getObject() + "\n");
        }
        String result = reviewBuf.toString().trim();
        return result;
    }

    static int MIN_SENTS = 5;
    static int MAX_SENTS = 25;

    public static void main(String[] args) {
        try {
            PolarityHierarchical classifier = new PolarityHierarchical('/Users/zack/Desktop/polarity');
            classifier.run();
            Classification c = classifier.classify("visually imaginative , thematically instructive and thoroughly delightful , it takes us on a roller-coaster ride from innocence to experience without even a hint of that typical kiddie-flick sentimentality");
            System.out.println(c);
            System.out.println(c.bestCategory());
        } catch (Throwable t) {
            System.out.println("Thrown: " + t);
            t.printStackTrace(System.out);
        }
    }
}
