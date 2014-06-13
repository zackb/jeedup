package net.jeedup.text.cluster

import com.aliasi.cluster.CompleteLinkClusterer
import com.aliasi.cluster.Dendrogram
import com.aliasi.cluster.HierarchicalClusterer
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import com.aliasi.tokenizer.Tokenizer
import com.aliasi.tokenizer.TokenizerFactory
import com.aliasi.util.Counter
import com.aliasi.util.Distance
import com.aliasi.util.ObjectToCounterMap
import net.jeedup.text.Phrase
import net.jeedup.util.StringUtil

/**
 * Created by zack on 6/13/14.
 */
class CosineDistancePhraseCluserAlgorithm implements IPhraseClusterAlgoritm {

    static final TokenizerFactory TOKENIZER_FACTORY = tokenizerFactory()

    static TokenizerFactory tokenizerFactory() {
        TokenizerFactory factory = IndoEuropeanTokenizerFactory.INSTANCE
        // factory  = new LowerCaseTokenizerFactory(factory)
        // factory = new EnglishStopTokenizerFactory(factory)
        // factory = new PorterStemmerTokenizerFactory(factory)
        return factory
    }

    static final Distance<Document> COSINE_DISTANCE = new Distance<Document>() {
        public double distance(Document doc1, Document doc2) {
            double oneMinusCosine = 1.0 - doc1.cosine(doc2)
            if (oneMinusCosine > 1.0)
                return 1.0
            else if (oneMinusCosine < 0.0)
                return 0.0
            else
                return oneMinusCosine
        }
    }

    @Override
    public List<Phrase> cluserPhrases(List<Phrase> phrases) {

        Set<Document> docSet = new HashSet<Document>()

        for (Phrase phrase : phrases) {
            Document doc = new Document(phrase)
            docSet.add(doc)
        }

        if (!docSet) {
            return []
        }

        HierarchicalClusterer<Document> clusterer = new CompleteLinkClusterer<Document>(COSINE_DISTANCE)
        Dendrogram<Document> dendrogram
        try {
            dendrogram = clusterer.hierarchicalCluster(docSet)
        } catch(IllegalArgumentException iae) {
            System.err.println('Failed cluster: ' + iae.message)
            return []
        }

        Set<Set<Document>> partition = dendrogram.partitionDistance(0.5)

        List<Phrase> result = new ArrayList<Phrase>()
        for (Set<Document> set : partition) {
            Phrase main = null
            for (Document doc : set) {
                if (main == null) {
                    main = doc.phrase
                    result.add(main)
                } else {
                    main.relatedPhrases << doc.phrase
                }
            }
        }

        return result
    }

    private static class Document {

        final ObjectToCounterMap<String> tokenCounter = new ObjectToCounterMap<String>()
        final double mLength
        final Phrase phrase

        private Document(Phrase _phrase) {
            this.phrase = _phrase

            // TODO: really do want to use description as well
            String string = phrase.text// + ' ' + phrase.description
            string = StringUtil.removeIgnoraleWordsFromNews(string)
            Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(string.toCharArray(), 0, string.size())
            String token
            while ((token = tokenizer.nextToken()) != null)
                tokenCounter.increment(token.toLowerCase())

            mLength = length(tokenCounter)
        }

        double cosine(Document doc) {
            return product(doc) / (mLength * doc.mLength)
        }

        double product(Document thatDoc) {
            double sum = 0.0
            for (String token : tokenCounter.keySet()) {
                int count = thatDoc.tokenCounter.getCount(token)
                if (count == 0) {
                    continue
                }
                sum += Math.sqrt(count * tokenCounter.getCount(token))
            }
            return sum
        }

        static double length(ObjectToCounterMap<String> otc) {
            double sum = 0.0
            for (Counter counter : otc.values()) {
                double count = counter.doubleValue()
                sum += count
            }
            return Math.sqrt(sum)
        }
    }
}
