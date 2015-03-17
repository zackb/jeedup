package net.jeedup.nlp.speech

/**
 * Sphinx
 */
class SpeechToText {

    /*
    Configuration configuration

    public SpeechToText() {
        init()
    }

    private void init() {
        configuration = new Configuration()
        configuration.setAcousticModelPath('/Users/zack/Downloads/cmusphinx-5prealpha-en-us-2.0')
        //configuration.setAcousticModelPath('resource:/edu/cmu/sphinx/models/en-us/en-us')

        configuration.setDictionaryPath('resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict')

        //configuration.setLanguageModelPath('resource:/edu/cmu/sphinx/models/en-us/en-us.lm.dmp')
        configuration.setLanguageModelPath('/Users/zack/Downloads/cmusphinx-5.0-en-us.lm')
    }

    public void liveRecognize() {
        LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
        recognizer.startRecognition(true);
        SpeechResult result
        while ((result = recognizer.getResult()) != null) {
            println result.getHypothesis()
            println result.words*.word
        }
        recognizer.stopRecognition();
    }

    public void recognize(String filename) {
        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration)
        recognizer.startRecognition(new FileInputStream(filename))
        SpeechResult result
        while ((result = recognizer.getResult()) != null) {

            System.out.format("Hypothesis: %s\n", result.getHypothesis())

            println('List of recognized words and their times:')

            for (WordResult r : result.getWords()) {
                println(r)
            }
        }
        recognizer.stopRecognition()
    }

    public static void main(String[] args) {
        //SpeechToText s = new SpeechToText()
        //s.recognize('/Users/zack/Desktop/atp103.mp3')
        //s.recognize('/Users/zack/Desktop/test.wav')
        //println HTTP.getJSON("http://localhost:8080/vitals/system")//("$SCHEME://$HOST:$PORT/vitals/system")
    }
    private static final String SCHEME = 'http'
    private static final String HOST = 'localhost'
    private static final int PORT = 8080
    */
}
