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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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


        parseSemanticSVO();

        parseSyntacticIP();

    }

    static void parseSemanticSVO() {
        System.out.println("\nparsing semantic SVO structure...");
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

        graph.edgeListSorted();
//        graph.outg
        graph.getAllNodesByWordPattern("");
        graph.vertexSet();
        System.out.println(subject + " " + verb + " " + object);
    }

    static void parseSyntacticIP() {
        System.out.println("\nparsing syntactic IP clause...");
        String ipClause = "(IP (NP (DNP (NP (NN 工商) (NN 银行)) (DEG 的)) (NP (NN 股价) (CC 和) (NN 市盈率))) (VP (ADVP (AD 分别)) (VP (VC 是) (QP (CD 多少)))))";
        Tree tree = Tree.valueOf(ipClause);
        TregexPattern tregex = TregexPattern.compile("IP < NP=subject < VP=verb ?< NP=object");
        TregexMatcher tregexMatcher = tregex.matcher(tree);
//        tree.pennPrint();

        tregex.prettyPrint();

        while (tregexMatcher.findAt(tree)) {

            tregexMatcher.getMatch().pennPrint();

            Tree subject = tregexMatcher.getNode("subject");
            Tree verb = tregexMatcher.getNode("verb");
            Tree object = tregexMatcher.getNode("object");

            System.out.println(subject);
            System.out.println(verb);
            System.out.println(SentenceUtils.listToString(subject.yield()));
            System.out.println(SentenceUtils.listToString(verb.yield()));
//            System.out.println(SentenceUtils.listToString(object.yield()));
//            System.out.println(SentenceUtils.listToOriginalTextString(subject.yieldWords()));

            parseSyntacticNP(subject);

            Tree subjHead = subject.headTerminal(hf, subject);
            System.out.println(subjHead);   // 市盈率

            Tree subjHeadParent = subjHead.parent(subject); // (NN 市盈率)
            System.out.println(subjHead.parent(subject));

            System.out.println(subjHeadParent);

        }

    }

    static void parseSyntacticNP(Tree npTree) {
        System.out.println("\nparsing syntactic NP tree...");
        TregexPattern pattern = TregexPattern.compile("NP ?<, NP=nmod1 < DNP=nmod2 < NP=nHead");
        TregexMatcher matcher = pattern.matcher(npTree);

        System.out.println(treeSpaningString(npTree));
        treeSpaningTokens(npTree).forEach(label -> System.out.println(
                label.index() + "-" + label.value()  + "-" + label.tag()));
        treeSpaningLabels(npTree).forEach(label -> System.out.println(
                label.index() + "-" + label.value()  + "-" + label.tag()));
        System.out.println(treeStartIndex(npTree) + "-" + treeEndIndex(npTree));

        if (matcher.matchesAt(npTree)) {
            Tree nmod1 = matcher.getNode("nmod1");
            Tree nmod2 = matcher.getNode("nmod2");
            Tree nHead = matcher.getNode("nHead");
            System.out.println(nmod1);
            System.out.println(nmod2);
            System.out.println(nHead);
        }
    }

    static boolean isEntityTree(Tree tree) {
        return true;
    }

    static boolean isCompositeNode(Tree tree) {
        return tree.size() > tree.depth();
    }

    static boolean isDateTimeTree(Tree tree) {
        return true;
    }

    static int treeStartIndex(Tree tree) {
        while (!tree.isLeaf())
            tree = tree.firstChild();
        CoreLabel label = (CoreLabel) tree.label();
        return label.get(CoreAnnotations.IndexAnnotation.class);
    }

    static int treeEndIndex(Tree tree) {
        while (!tree.isLeaf())
            tree = tree.lastChild();
        CoreLabel label = (CoreLabel) tree.label();
        return label.get(CoreAnnotations.IndexAnnotation.class);
    }

    static int treeIndex(Tree tree) {
        if (!tree.isLeaf())
            throw new UnsupportedOperationException("only leaf node has index annotation");
        CoreLabel label = (CoreLabel) tree.label();
        return label.get(CoreAnnotations.IndexAnnotation.class);
    }

    static List<CoreLabel> treeSpaningTokens(Tree tree) {
        if (tree.isLeaf())
            return Arrays.asList((CoreLabel) tree.label());
        if (tree.isPreTerminal())
            return Arrays.asList((CoreLabel) tree.firstChild().label());
        List<CoreLabel> leaves = new ArrayList<>();
        for (Tree child: tree.children())
            leaves.addAll(treeSpaningTokens(child));
        return leaves;
    }

    static List<CoreLabel> treeSpaningLabels(Tree tree) {
        List<CoreLabel> labels = new ArrayList<>();
        addTreeLabels(tree, labels);
        return labels;
    }

    static private void addTreeLabels(Tree tree, List<CoreLabel> labels) {
        if (tree.isLeaf()) {
            labels.add((CoreLabel) tree.label());
            return;
        }
        if (tree.isPreTerminal()) {
            labels.add((CoreLabel) tree.firstChild().label());
            return;
        }
        for (Tree child: tree.children())
            addTreeLabels(child, labels);
    }

    static String treeSpaningString(Tree tree) {
        if (tree.isLeaf())
            return tree.nodeString();
        if (tree.isPreTerminal())
            return tree.firstChild().nodeString();
        StringBuilder sb = new StringBuilder();
        for (Tree child: tree.children())
            sb.append(treeSpaningString(child));
        return sb.toString();
    }

}