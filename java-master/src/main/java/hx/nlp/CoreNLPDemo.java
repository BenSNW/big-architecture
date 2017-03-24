package hx.nlp;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.tokensregex.MultiPatternMatcher;
import edu.stanford.nlp.ling.tokensregex.SequenceMatchResult;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.util.logging.Redwood;
import hx.nlp.parser.temporal.FromToPatternParser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Benchun on 3/12/17
 */
public class CoreNLPDemo {

    /** A logger for this class */
    private static Redwood.RedwoodChannels log = Redwood.channels(CoreNLPDemo.class);

    private static final String[] SAMPLES = new String[] { "工行昨天的股价", "工行最近几年的股价",
            "工商银行昨天的股价", "中国工商银行上周的股价", "工商银行3天前的股价", "工商银行3月5日到3月10日的股价走势",
            "工商银行3月份的股价走势", "工行第二季度的股价", "工商银行早上8点的股价", "工商银行到3月5日为止前一个月的股价",
            "工商银行的资金流入", "工商银行的股价和市盈率分别是多少", "资金流入前10位",
            "工商银行涨停了", "今天涨停的公司", "连续3天涨停的公司", "涨幅超过百分之三的公司",
            "603991市值三亿美元", "603991市值多少钱", "603991是哪家公司",
            "工商银行好不好", "工商银行呢，这个怎么样？", "有推荐的吗", "有什么推荐的", "有没有什么推荐的" };

    public CoreNLPDemo() {}

    public static void main(String[] args) {

//        dependencyParser(args);

//        openIEDemo();

//        openIESimpleApi();

//        chineseAnnotatorPipeline();

//        resolveSemanticGraph();

        String propsFile = "chinese-parser.properties";
        StanfordCoreNLP annotatorPipeline = new StanfordCoreNLP(propsFile);

        System.out.println(annotatorPipeline.getProperties().toString());

        Stream.of(SAMPLES).forEach(text -> {
            Annotation annotation = annotatorPipeline.process(text);
            annotation.set(CoreAnnotations.DocDateAnnotation.class, "2017-03-23");
//            annotation.keySet().forEach(System.out::println);

            CoreMap sentence = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0);

            SemanticGraph semanticGraph = sentence.get(EnhancedPlusPlusDependenciesAnnotation.class);
            System.out.println(semanticGraph.toString(SemanticGraph.OutputFormat.LIST));
            System.out.println(semanticGraph.toString(SemanticGraph.OutputFormat.XML));
            System.out.println(semanticGraph.toString(SemanticGraph.OutputFormat.READABLE));
            System.out.println(semanticGraph.toString(SemanticGraph.OutputFormat.RECURSIVE));
//            System.out.println(semanticGraph.toString(CoreLabel.OutputFormat.VALUE_MAP));
//            semanticGraph.prettyPrint();

            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            String cdmPattern = "(?$amount [{tag: /CD|DT/}]) (?$unit [{tag: M; word:/周|天/}]) (?$postLC [{tag: LC}])?";
            TokenSequencePattern pattern = TokenSequencePattern.compile(cdmPattern);
            TokenSequenceMatcher matcher = pattern.getMatcher(tokens);
            while (matcher.find()) {
                System.out.println(matcher.group());
                matcher.groupNodes().forEach(System.out::println);
                Stream.of(1,2,3).map(matcher::group).forEach(System.out::println);
                Stream.of("$amount", "$unit", "$postLC").map(matcher::group).forEach(System.out::println);
            }

//            Stream.of("[{tag:P}]? (?$date [{tag:NT}]{1,3}) [{tag:AD}]? (?$con [{tag:/P|CC/}]) (?$endDate [{tag:NT}]{1,3})",
//                    "[{tag:P}]? (?$date [{tag:NT}]{1,3}) [{tag:LC}]?",
//                    "[{tag:/NT|DT/}] [{tag:CD}] /个/? (?$unit [{tag:M; word:/天|日|周|星期|礼拜|旬|月份?|季度?|年/}]) [{tag:LC}]?",
//                    "(?$pre [{tag:/LC|DT|JJ/}])? (?$mod [{tag:/DT|CD|OD/}]) /个/? (?$unit [{tag:M; word:/天|日|周|星期|礼拜|旬|月份?|季度?|年/}]) (?$post [{tag:/LC|DT|JJ/}])?")
//                .map(p -> TokenSequencePattern.compile(p).matcher(tokens))
//                .filter(TokenSequenceMatcher::find)
//                .forEach(m -> {
//                    System.out.println(m.group());
//                    m.groupNodes().forEach(System.out::println);
//                });

            new FromToPatternParser().parse(tokens);

            List<TokenSequencePattern> patterns = Stream.of(
                    "[{tag:AD}]?　[{tag:P}]? (?$date [{tag:NT}]{1,3}) [{tag:AD}]? (?$con [{tag:/P|CC/}]) (?$endDate [{tag:NT}]{1,3})　[{tag:LC}]? ([{tag:DEG}]? []{0,2} [{tag:NN; word:/时./}] [{tag:LC}]?)?",
                    "[{tag:P}]? (?$date [{tag:NT}]{1,3}) [{tag:/LC|AD/}]?",
                    "[{tag:/NT|DT/}] [{tag:CD}] /个/? (?$unit [{tag:M; word:/天|日|周|星期|礼拜|旬|月份?|季度?|年/}]) [{tag:LC}]?",
                    "(?$pre [{tag:/LC|DT|JJ/}])? (?$mod [{tag:/DT|CD|OD/}]) /个/? (?$unit [{tag:M; word:/分钟|刻钟|小时|天|日|周|星期|礼拜|旬|月份?|季度?|年/}]) (?$post [{tag:/LC|JJ/])?")
                .map(TokenSequencePattern::compile).collect(Collectors.toList());
            MultiPatternMatcher multiPatternMatcher = TokenSequencePattern.getMultiPatternMatcher(patterns);
            List<SequenceMatchResult<CoreMap>> matches = multiPatternMatcher.findNonOverlapping(tokens);
            matches.forEach(matchResult -> {
                System.out.println(sentence + " -> " + matchResult.group());
                matchResult.groupNodes().forEach(label -> System.out.println(
                        label.get(CoreAnnotations.NamedEntityTagAnnotation.class) + "-"
                      + label.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class)));
            });

            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // Print out words, lemma, ne, and normalized ne
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                String normalized = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
                System.out.println("token: " + "word="+word + ", lemma="+lemma + ", pos=" + pos + ", ne=" + ne + ", normalized=" + normalized);
            }

            IndexedWord rootNode = semanticGraph.getFirstRoot();
            CoreLabel rootLabel = rootNode.backingLabel();
            showLabelAnnotations(rootLabel);

//            rootNode.keySet().forEach(System.out::println);
//            rootNode.get(CoreAnnotations.TokensAnnotation.class).forEach(token -> {
//                String word = token.getString(CoreAnnotations.TextAnnotation.class);
//                String pos = token.getString(CoreAnnotations.PartOfSpeechAnnotation.class);
//                String ner = token.getString(CoreAnnotations.NamedEntityTagAnnotation.class);
//                System.out.println(word + "\t " + pos + "\t " + ner);
//            });

            List<CoreMap> entities = sentence.get(CoreAnnotations.MentionsAnnotation.class);
            for (CoreMap entity: entities) {
                System.out.println(entity + ": " + entity.get(CoreAnnotations.NamedEntityTagAnnotation.class)
                    + "-" + entity.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class)
                    + "-" + entity.get(CoreAnnotations.NumericValueAnnotation.class));
//                entity.keySet().forEach((Class key) -> System.out.println(key + "-" + entity.get(key)));
                entity.get(CoreAnnotations.TokensAnnotation.class).forEach(token -> {
                    String word = token.getString(CoreAnnotations.TextAnnotation.class);
                    String pos = token.getString(CoreAnnotations.PartOfSpeechAnnotation.class);
                    String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                    String nner = token.getString(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
                    int index = token.get(CoreAnnotations.IndexAnnotation.class);
                    System.out.println(index + "\t" + word + "\t " + pos + "\t " + ner + "\t" + nner);
                });
            }

            semanticGraph.getOutEdgesSorted(rootNode).forEach(edge -> {    // 股价/NN -> 昨天/NT (nmod)
                String relation = edge.getRelation().toString();
                IndexedWord source = edge.getSource();
                IndexedWord target = edge.getTarget();
                System.out.printf("%s(%s, %s)%n", relation, word2String(source), word2String(target));
            });

            for (SemanticGraphEdge edge: semanticGraph.outgoingEdgeIterable(rootNode)) {
                GrammaticalRelation relation = edge.getRelation();
//                if ("nsubj".equals(relation.toString())) {
//                    IndexedWord subject = edge.getTarget();
//                    System.out.println(subject);
//                }
            }

            Tree dpTree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            System.out.println(dpTree);    // ROOT
            dpTree.pennPrint();

            Tree rootTree = dpTree.firstChild();
            resolveTreeHead(rootTree);

            switch (rootTree.nodeString()) {
                case ("IP"):
                    System.out.println("resolving sentence tree, children count: " + rootTree.children().length);
                    for (Tree child : rootTree.children()) {
                        System.out.println(child.nodeString());
                    }
                    break;
                case ("NP"):
                    resolveNP(rootTree, null);
                    break;
                default:
                    System.out.println(rootTree.nodeString() + " " + rootTree.getSpan());
            }

        });

    }

    static void showLabelAnnotations(CoreLabel label) {
        for (Class clazz : label.keySet()) {
            System.out.println(clazz.getName() + " " + label.get(clazz));
        }
    }

    static String word2String(IndexedWord word) {
//        System.out.println(word.backingLabel());    // 股价-5
        return word.index() + "-" + word.lemma()
                + "-" + word.get(CoreAnnotations.EntityClassAnnotation.class)
                + "-" + word.get(CoreAnnotations.EntityRuleAnnotation.class)
                + "-" + word.get(CoreAnnotations.EntityTypeAnnotation.class)
                + "-" + word.get(CoreAnnotations.NamedEntityTagAnnotation.class)
                + "-" + word.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
    }

    static void resolveNP(Tree npTree, Map<String, String> template) {
        System.out.println("resolving noun phrase tree, children count: " + npTree.children().length);

        resolveTreeHead(npTree);

        npTree.flatten().pennPrint();
        System.out.println(isExcactNP(npTree));
    }

    static CoreLabel resolveTreeHead(Tree tree) {
        if (tree.isLeaf())
            return (CoreLabel) tree.label();
        if (tree.isPreTerminal())
            return (CoreLabel) tree.firstChild().label();

        final CoreLabel coreLabel = (CoreLabel) tree.label();
//            coreLabel.keySet().stream().collect(Collectors.toMap(
//                    Function.identity(), clazz -> coreLabel.get(clazz)));
        CoreLabel headLabel = coreLabel.get(TreeCoreAnnotations.HeadWordLabelAnnotation.class);
        System.out.println(headLabel.ner() + "-" + headLabel.index() + "-" + headLabel.beginPosition());

//        CoreLabel headTag   = coreLabel.get(TreeCoreAnnotations.HeadTagLabelAnnotation.class);
//        System.out.println(headTag);

//        Stream.of(tree.children()).filter(child -> child.par)
        return headLabel;
    }

    static boolean isExcactNP(Tree npTree) {
        Set<String> nouns = new HashSet<>(Arrays.asList("NN", "NT"));
        return npTree.isLeaf() ||
            Stream.of(npTree.children()).map(Tree::nodeString).anyMatch(nouns::contains);
    }

//    static void resolveNP

    static void resolveSemanticGraph() {
        String propsFile = "chinese-parser.properties";
        StanfordCoreNLP annotatorPipeline = new StanfordCoreNLP(propsFile);
        Annotation annotation = annotatorPipeline.process("中国工商银行三天前的股价是多少");

        System.out.println(annotation.toShorterString());
        annotatorPipeline.prettyPrint(annotation, System.out);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        CoreMap sentence = sentences.get(0);

        SemanticGraph semanticGraph = sentence.get(EnhancedPlusPlusDependenciesAnnotation.class);
        int sentenceBegin = sentence.get(CoreAnnotations.TokenBeginAnnotation.class);
        // Iterate over all tokens and their dependencies
        for (int sourceTokenIndex = sentenceBegin;
             sourceTokenIndex < sentence.get(CoreAnnotations.TokenEndAnnotation.class);
             sourceTokenIndex++) {
           IndexedWord node = semanticGraph.getNodeByIndexSafe(sourceTokenIndex - sentenceBegin + 1);  // + 1 for ROOT
            if (node != null) {
                for (SemanticGraphEdge edge : semanticGraph.outgoingEdgeList(node)) {
                    String relation = edge.getRelation().toString();
                    int targetTokenIndex = sentenceBegin + edge.getTarget().index() - 1;
                    System.out.println(relation + "(" + node + "," + edge.getTarget() + ")");
                }
            }
        }


    }

    static IndexedWord getRoot(SemanticGraph graph) {
        if (graph.getRoots().size() != 1)
            throw new RuntimeException("abnormal root count in SemanticGraph: " + graph.getRoots().size());
        return graph.getFirstRoot();
    }

    static String getSubject(SemanticGraph graph) {
        IndexedWord root = getRoot(graph);
        return graph.getOutEdgesSorted(root).stream()
            .filter(edge -> "nsubj".equals(edge.getRelation().toString()))
            .findFirst().get().getTarget()
            .toString(CoreLabel.OutputFormat.VALUE);
    }

    static void resolveSyntacticTree(Tree root) {

    }

    static void chineseAnnotatorPipeline() {
        String propsFile = "chinese-parser.properties";
        final StanfordCoreNLP annotatorPipeline = new StanfordCoreNLP(propsFile);
        Stream.of("工商银行昨天的股价", "中国工商银行上周的股价", "阿里巴巴3月5日到3月10日的股价走势")
                .map(Annotation::new).forEach(annotation -> {
            annotatorPipeline.annotate(annotation);
            System.out.println(annotation.toShorterString());
            annotatorPipeline.prettyPrint(annotation, System.out);

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

    static void dependencyParser(String[] args) {
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
            SemanticGraph sg = sent.get(BasicDependenciesAnnotation.class);
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
