package net.jeedup.nlp

import groovy.transform.CompileStatic

/**
 * Created by zack on 3/12/15.
 */
@CompileStatic
class Mallet {
    /*
    private static int ITERATIONS = 80;
    private static int numTopics = 1;
    private static int numWords = 8;
    private static final String[] contractions = ["a","able","about","above","according","accordingly","across","actually","after","afterwards","again","against","all","allow","allows","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","arent","around","as","aside","ask","asking","associated","at","available","away","awfully","b","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","believe","below","beside","besides","best","better","between","beyond","both","brief","but","by","c","came","can","cannot","cant","cant","cause","causes","certain","certainly","changes","clearly","co","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","couldnt","course","currently","d","definitely","described","despite","did","didnt","different","do","does","doesnt ","doing","done","dont","down","downwards","during","e","each","edu","eg","eight","either","else","elsewhere","enough","entirely","especially","et","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","f","far","few","fifth","first","five","followed","following","follows","for","former","formerly","forth","four","from","further","furthermore","g","get","gets","getting","given","gives","go","goes","going","gone","got","gotten","greetings","h","had","hadnt","happens","hardly","has","hasnt","have","havent","having","he","hed","hell","hello","help","hence","her","here","hereafter","hereby","herein","heres","hereupon","hers","herself","hes","hi","him","himself","his","hither","hopefully","how","howbeit","however","hows","i","id","ie","if","ignored","ill","im","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","inner","insofar","instead","into","inward","is","isnt","it","its","its","itself","ive","j","just","k","keep","keeps","kept","know","known","knows","l","last","lately","later","latter","latterly","least","less","lest","let","lets","like","liked","likely","little","look","looking","looks","ltd","m","mainly","many","may","maybe","me","mean","meanwhile","merely","might","more","moreover","most","mostly","much","must","mustnt","my","myself","n","name","namely","nd","near","nearly","necessary","need","needs","neither","never","nevertheless","new","next","nine","no","nobody","non","none","noone","nor","normally","not","nothing","novel","now","nowhere","o","obviously","of","off","often","oh","ok","okay","old","on","once","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","p","particular","particularly","per","perhaps","placed","please","plus","possible","presumably","probably","provides","q","que","quite","qv","r","rather","rd","re","really","reasonably","regarding","regardless","regards","relatively","respectively","right","s","said","same","saw","say","saying","says","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","shant","she","shed","shell","shes","should","shouldnt","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","t","take","taken","tell","tends","th","than","thank","thanks","thanx","that","thats","thats","the","their","theirs","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","theres","theres","thereupon","these","they","theyd","theyll","theyre","theyve","think","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","to","together","too","took","toward","towards","tried","tries","truly","try","trying","twice","two","u","un","under","unfortunately","unless","unlikely","until","unto","up","upon","us","use","used","useful","uses","using","usually","uucp","v","value","various","very","via","viz","vs","w","want","wants","was","wasnt","way","we","wed","welcome","well","well","went","were","were","werent","weve","what","whatever","whats","when","whence","whenever","whens","where","whereafter","whereas","whereby","wherein","wheres","whereupon","wherever","whether","which","while","whither","who","whoever","whole","whom","whos","whose","why","whys","will","willing","wish","with","within","without","wonder","wont","would","would","wouldnt","x","y","yes","yet","you","youd","youll","your","youre","yours","yourself","yourselves","youve","z","zero"] as String[]

    public static void main(String[] args) {
        String str = """
Echoing Chief Belmar’s comments, Attorney General Eric H. Holder Jr., called the shootings “heinous and cowardly attacks.”

“This was not someone trying to bring healing to Ferguson,” Mr. Holder said. “This was a damn punk, a punk, who was trying to sow discord.”

President Obama weighed in on Twitter, writing: “Violence against police is unacceptable. Our prayers are with the officers in MO. Path to justice is one all of us must travel together.”

Later in the morning, in an action that officials said was part of the investigation into the shooting, police SWAT units surrounded a house a few blocks from the shooting scene, and officers climbed onto the roof and broke through a vent there to gain access.

“People have been taken in for questioning,” said a police spokesman, Sgt. Brian Schellman. “No arrests at this point.”

Chief Belmar said people had a right to protest peacefully, but also said “there is an unfortunate association with that gathering” and the shooting.

“I feel confident that for whatever reason they were observers, whatever you want to call it, with the group of individuals that were down there protesting,” he said. “This is no reflection, again, on any of those guys, they can’t help it.”
"""
        println foo([str] as String[])
    }

    public static List<String> foo(String[] strings) {

        if(strings == null) {
            return null;
        }

        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        List<String> inputs = new ArrayList<String>();
        for(String s : strings){
            inputs.add(s.replaceAll("\\s+", " "));
        }

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        // pipeList.add( new StringList2FeatureSequence() );
        // pipeList.add( new CharSequenceArray2TokenSequence() );
        // pipeList.add( new TokenSequenceLowercase() );
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(false, false).addStopWords(contractions) );  // Remove stopwords from a standard English stoplist.
        pipeList.add( new TokenSequence2FeatureSequence() );

        final InstanceList instances = new InstanceList(new SerialPipes(pipeList));

        // Load data from input.strings
        for(String str : inputs) {
            Instance instance = new Instance(str, "target", "tweet", "twitter");
            instances.addThruPipe(instance);
        }

        // Create a model, alpha_t = 1.0, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        final ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(2);

        // Run the model for ITERATIONS iterations and stop
        model.setNumIterations(ITERATIONS);
        model.estimate();

        // OUTPUT
        List<String> output = new ArrayList<String>();

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();

        if(model.getData().size()==0){
            return output;
        }
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;

        // Estimate the topic distribution of the first instance,
        //  given the current Gibbs state.
        double[] topicDistribution = model.getTopicProbabilities(0);

        Object[][] topicSortedWords = model.getTopWords(numWords);

        // Show top numWords words in topic
        for(int word = 0; word < topicSortedWords[0].length; word++) {
            output.add(topicSortedWords[0][word].toString());
        }

        return output;
    }
    */
}
