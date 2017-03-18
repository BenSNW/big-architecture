package hx.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.parser.lexparser.ChineseTreebankParserParams;
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphFactory;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;

import java.util.function.Function;

/**
 * A small demo that shows how to convert a tree to a SemanticGraph
 * and then run a SemgrexPattern on it
 *
 * @author John Bauer
 */
public class SemgrexDemo {

    static TreebankLangParserParams params = new ChineseTreebankParserParams();
    static GrammaticalStructureFactory gsf = params.treebankLanguagePack()
            .grammaticalStructureFactory(
                    params.treebankLanguagePack().punctuationWordRejectFilter(),
                    params.typedDependencyHeadFinder());

    static HeadFinder hf = params.headFinder(); //new ChineseHeadFinder();
    static TreebankLanguagePack tlp = params.treebankLanguagePack();
    static Function bcf = tlp.getBasicCategoryFunction();
    static TregexPatternCompiler tregexCompiler = new TregexPatternCompiler(hf, bcf);


    public static void main(String[] args) {
        String treeString = "(ROOT  (S (NP (PRP$ My) (NN dog)) (ADVP (RB also)) (VP (VBZ likes) (S (VP (VBG eating) (NP (NN sausage))))) (. .)))";
        // Typically the tree is constructed by parsing or reading a
        // treebank.  This is just for example purposes
        Tree tree = Tree.valueOf(treeString);

        // This creates English uncollapsed dependencies as a
        // SemanticGraph.  If you are creating many SemanticGraphs, you
        // should use a GrammaticalStructureFactory and use it to generate
        // the intermediate GrammaticalStructure instead
        SemanticGraph graph = SemanticGraphFactory.generateUncollapsedDependencies(tree);

        System.out.println(graph);

        SemgrexPattern semgrex = SemgrexPattern.compile("{}=A <<nsubj {}=B");
        SemgrexMatcher matcher = semgrex.matcher(graph);
        // This will produce two results on the given tree: "likes" is an
        // ancestor of both "dog" and "my" via the nsubj relation
        while (matcher.find()) {
            System.out.println(matcher.getNode("A") + " <<nsubj " + matcher.getNode("B"));
        }


        System.out.println("\n");


        // Alternatively, this could have been the Chinese params or any
        // other language supported.  As of 2014, only English and Chinese
        TreebankLangParserParams params = new ChineseTreebankParserParams();
        GrammaticalStructureFactory gsf = params.treebankLanguagePack()
                .grammaticalStructureFactory(
                        params.treebankLanguagePack().punctuationWordRejectFilter(),
                        params.typedDependencyHeadFinder());

        tree = Tree.valueOf("(ROOT (NP (NP (NN 工商) (NN 银行)) (DNP (NP (NT 昨天)) (DEG 的)) (NP (NN 股价))))");
        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
        graph = SemanticGraphFactory.generateEnhancedDependencies(gs);
        System.out.println(graph);

        semgrex = SemgrexPattern.compile("{tag:NN}=A </compound.*/ {idx:/\\d/}=B");
        matcher = semgrex.matcher(graph);
        while (matcher.find()) {
            System.out.println(matcher.getNode("A").toString(CoreLabel.OutputFormat.VALUE)
                    + matcher.getNode("B").toString(CoreLabel.OutputFormat.VALUE));

            matcher.getGraph();
        }

        System.out.println();


        IndexedWord root = graph.getFirstRoot();
        root.keySet().forEach(System.out::println);

        String rootPos = root.get(CoreAnnotations.PartOfSpeechAnnotation.class);


        TregexPattern tregex = TregexPattern.compile("DNP=A < (NP $ DEG)");
        TregexMatcher tregexMatcher = tregex.matcher(tree);
        tree.pennPrint();
        while (tregexMatcher.find()) {
            Tree subTree = tregexMatcher.getNode("A");
            System.out.println(subTree.toString());
            subTree.pennPrint();
            System.out.println(SentenceUtils.listToString(subTree.yield()));
//            System.out.println(SentenceUtils.listToOriginalTextString(subTree.yieldWords()));
        }


        resolveSemanticSVO();

        resolveSyntacticIP();

    }

    static void resolveSemanticSVO() {
        System.out.println("\nresolving semantic SVO structure...");
        String text = "(ROOT (IP (NP (NN 工商) (NN 银行)) (VP (VV 涨) (NP (NN 停了)))))";
        GrammaticalStructure gs = gsf.newGrammaticalStructure(Tree.valueOf(text));
        SemanticGraph graph = SemanticGraphFactory.generateUncollapsedDependencies(gs);

        String svoPattern = "{}=verb >/.*subj|agent/ {}=subject ?>/.*obj/ {}=object";
        SemgrexPattern semgrex = SemgrexPattern.compile(svoPattern);
        SemgrexMatcher matcher = semgrex.matcher(graph);

        IndexedWord subject = null, verb = null, object = null;

        while (matcher.find()) {
            subject = matcher.getNode("subject");
            verb    = matcher.getNode("verb");
            object  = matcher.getNode("object");
        }



        System.out.println(subject + " " + verb + " " + object);
    }

    static void resolveSyntacticIP() {
        System.out.println("\nresolving syntactic IP clause...");
        String ipClause = "(IP (NP (DNP (NP (NN 工商) (NN 银行)) (DEG 的)) (NP (NN 股价) (CC 和) (NN 市盈率))) (VP (ADVP (AD 分别)) (VP (VC 是) (QP (CD 多少)))))";
        Tree tree = Tree.valueOf(ipClause);
        TregexPattern tregex = TregexPattern.compile("IP < NP=subject < VP=verb ?< NP=object");
        TregexMatcher tregexMatcher = tregex.matcher(tree);
//        tree.pennPrint();

        while (tregexMatcher.find()) {
            Tree subject = tregexMatcher.getNode("subject");
            Tree verb = tregexMatcher.getNode("verb");
            Tree object = tregexMatcher.getNode("object");

            System.out.println(subject);
            System.out.println(verb);
            System.out.println(SentenceUtils.listToString(subject.yield()));
            System.out.println(SentenceUtils.listToString(verb.yield()));
//            System.out.println(SentenceUtils.listToString(object.yield()));
//            System.out.println(SentenceUtils.listToOriginalTextString(subject.yieldWords()));

            Tree subjHead = subject.headTerminal(hf, subject);
            System.out.println(subjHead);

            Tree subjTree = subjHead.parent(subject);
            System.out.println(subjHead.parent(subject));

        }

    }

    static void showTreeNodeAnnotations(Tree tree) {
//        tree.labels().forEach(System.out::println);
//        tree.labeledYield().forEach(System.out::println);
//        CoreLabel label = (CoreLabel) tree.label();
//        label.keySet().forEach((Class clazz) -> System.out.println(clazz.getName() + "-" + label.get(clazz)));
        System.out.println(tree.flatten());
    }


}