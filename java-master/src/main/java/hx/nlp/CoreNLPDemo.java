package hx.nlp;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.util.logging.Redwood;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Created by Benchun on 3/12/17
 */
public class CoreNLPDemo {

    /** A logger for this class */
    private static Redwood.RedwoodChannels log = Redwood.channels(CoreNLPDemo.class);

    public CoreNLPDemo() {}

    public static void main(String[] args) {

//        pependencyParser(args);

//        openIEDemo();

//        openIESimpleApi();

        chineseAnnoPipeline();

    }

    static void chineseAnnoPipeline() {
        String propsFile = "chinese-parser.properties";
        final StanfordCoreNLP chinesePipeline = new StanfordCoreNLP(propsFile);
        // "你转这篇博客很无知", "你转的这篇博客很无知",
        Stream.of("工行3天前的股价", "工商银行三天前的股价", "中国工商银行三天前的股价是多少",
                "工商银行3月5日到3月10日的股价走势").map(Annotation::new).forEach(annotation -> {
            chinesePipeline.annotate(annotation);
            System.out.println(annotation.toShorterString());
            chinesePipeline.prettyPrint(annotation, System.out);

            // 从注释中获取CoreMap List，并取第0个值
            List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
            CoreMap sentence = sentences.get(0);

            // 从CoreMap中取出CoreLabel List，逐一打印出来
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            System.out.println("字/词" + "\t " + "词性" + "\t " + "实体标记");
            System.out.println("-----------------------------");
            for (CoreLabel token : tokens) {
                String word = token.getString(CoreAnnotations.TextAnnotation.class);
                String pos = token.getString(CoreAnnotations.PartOfSpeechAnnotation.class);
                String ner = token.getString(CoreAnnotations.NamedEntityTagAnnotation.class);
                System.out.println(word + "\t " + pos + "\t " + ner);
            }

            // http://stanfordnlp.github.io/CoreNLP/entitymentions.html
            List<CoreMap> entityMentions = sentence.get(CoreAnnotations.MentionsAnnotation.class);
            entityMentions.stream().forEach(entity -> System.out.println(entity.toShorterString()));

            Tree dpTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            dpTree.forEach(tree -> {
//                tree.constituents().forEach(System.out::println);
                System.out.println(tree.toString());
//                System.out.println(tree.depth() + " " + tree.getSpan().toString());
//                tree.pennPrint(System.out, false);
            });
        });
    }

    static void pependencyParser(String[] args) {
        String text;
        if (args.length > 0) {  // if data from file
            text = IOUtils.slurpFileNoExceptions(args[0], "utf-8");
        } else {
            text = "I can almost always tell when movies use fake dinosaurs.";
        }
        Annotation ann = new Annotation(text);

        Properties props = PropertiesUtils.asProperties(
                "annotators", "tokenize,ssplit,pos,depparse",
                "depparse.model", DependencyParser.DEFAULT_MODEL
        );

        AnnotationPipeline pipeline = new StanfordCoreNLP(props);

        pipeline.annotate(ann);

        for (CoreMap sent : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
            SemanticGraph sg = sent.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
            log.info(IOUtils.eolChar + sg.toString(SemanticGraph.OutputFormat.LIST));
        }
    }

    static void openIEDemo() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Annotate an example document.
        Annotation doc = new Annotation("Obama was born in Hawaii. He is our president.");
        pipeline.annotate(doc);

        // Loop over sentences in the document
        for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the OpenIE triples for the sentence
            Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            // Print the triples
            for (RelationTriple triple : triples) {
                System.out.println(triple.confidence + "\t" +
                        triple.subjectLemmaGloss() + "\t" +
                        triple.relationLemmaGloss() + "\t" +
                        triple.objectLemmaGloss());
            }
        }
    }

    static void openIESimpleApi() {
        // Create a CoreNLP document
        Document doc = new Document("Obama was born in Hawaii. He is our president.");

        // Iterate over the sentences in the document
        for (Sentence sent : doc.sentences()) {
            // Iterate over the triples in the sentence
            for (RelationTriple triple : sent.openieTriples()) {
                // Print the triple
                System.out.println(triple.confidence + "\t" +
                        triple.subjectLemmaGloss() + "\t" +
                        triple.relationLemmaGloss() + "\t" +
                        triple.objectLemmaGloss());
            }
        }
    }


}
